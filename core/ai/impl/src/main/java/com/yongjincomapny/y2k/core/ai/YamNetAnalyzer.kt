package com.yongjincomapny.y2k.core.ai

import android.content.Context
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import com.yongjincomapny.y2k.core.database.TrackTagDao
import com.yongjincomapny.y2k.core.database.TrackTagEntity
import com.yongjincomapny.y2k.core.player.Y2KTrack
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.tensorflow.lite.Interpreter
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

private const val MODEL_FILE = "yamnet.tflite"
private const val SAMPLE_RATE = 16000
private const val WINDOW_SAMPLES = 15600 // 0.975초 윈도우 (YAMNet Lite 입력 크기)
private const val MAX_EXTRACT_SEC = 30    // 최대 30초만 추출
private const val MODEL_VERSION = "yamnet_v1"
private const val NUM_CLASSES = 521
private const val MIN_CONFIDENCE = 0.3f

@Singleton
class YamNetAnalyzer @Inject constructor(
    @ApplicationContext private val context: Context,
    private val trackTagDao: TrackTagDao,
) : AudioAnalyzer {

    @Volatile
    private var interpreter: Interpreter? = null
    private var classNames: List<String> = emptyList()

    private fun ensureModel(): Boolean {
        if (interpreter != null) return true
        return try {
            val assetFiles = context.assets.list("") ?: emptyArray()
            if (MODEL_FILE !in assetFiles) return false

            val modelBuffer = context.assets.open(MODEL_FILE).use { input ->
                val bytes = input.readBytes()
                ByteBuffer.allocateDirect(bytes.size).apply {
                    order(ByteOrder.nativeOrder())
                    put(bytes)
                    rewind()
                }
            }
            interpreter = Interpreter(modelBuffer)

            // 클래스 이름 로드
            classNames = try {
                context.assets.open("yamnet_class_map.csv").bufferedReader().useLines { lines ->
                    lines.drop(1).map { line ->
                        val parts = line.split(",")
                        if (parts.size >= 3) parts[2].trim('"', ' ') else ""
                    }.filter { it.isNotEmpty() }.toList()
                }
            } catch (e: Exception) {
                // CSV 없으면 빈 리스트로 폴백
                emptyList()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun analyze(track: Y2KTrack): AudioTags? = withContext(Dispatchers.Default) {
        // 캐시 확인
        val cached = trackTagDao.getByTrackId(track.id)
        if (cached != null) return@withContext cached.toAudioTags()

        if (!ensureModel()) return@withContext null

        val audioData = extractAudio(track) ?: return@withContext null
        val result = runSlidingWindowInference(audioData) ?: return@withContext null

        val tags = mapToTags(track.id, result)

        // 캐시 저장
        trackTagDao.upsert(tags.toEntity())

        tags
    }

    override suspend fun analyzeBatch(
        tracks: List<Y2KTrack>,
        onProgress: suspend (current: Int, total: Int) -> Unit,
    ): List<AudioTags> = withContext(Dispatchers.Default) {
        val analyzedIds = trackTagDao.getAnalyzedTrackIds().toSet()
        val toAnalyze = tracks.filter { it.id !in analyzedIds }
        val cached = trackTagDao.getByTrackIds(tracks.filter { it.id in analyzedIds }.map { it.id })
            .map { it.toAudioTags() }

        if (!ensureModel()) return@withContext cached

        val newTags = mutableListOf<AudioTags>()
        try {
            toAnalyze.forEachIndexed { index, track ->
                onProgress(index + 1, toAnalyze.size)
                val tags = analyze(track)
                if (tags != null) newTags.add(tags)
            }
        } finally {
            releaseModel()
        }

        cached + newTags
    }

    private fun releaseModel() {
        interpreter?.close()
        interpreter = null
    }

    private suspend fun extractAudio(track: Y2KTrack): FloatArray? = withContext(Dispatchers.IO) {
        try {
            val extractor = MediaExtractor()
            extractor.setDataSource(context, track.uri, null)

            // 오디오 트랙 찾기
            var audioTrackIndex = -1
            for (i in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(i)
                val mime = format.getString(MediaFormat.KEY_MIME) ?: continue
                if (mime.startsWith("audio/")) {
                    audioTrackIndex = i
                    break
                }
            }
            if (audioTrackIndex == -1) {
                extractor.release()
                return@withContext null
            }

            extractor.selectTrack(audioTrackIndex)
            val format = extractor.getTrackFormat(audioTrackIndex)
            val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
            val mime = format.getString(MediaFormat.KEY_MIME) ?: run {
                extractor.release()
                return@withContext null
            }

            val codec = MediaCodec.createDecoderByType(mime)
            codec.configure(format, null, null, 0)
            codec.start()

            val pcmSamples = mutableListOf<Float>()
            val maxSamples = sampleRate * MAX_EXTRACT_SEC
            val bufferInfo = MediaCodec.BufferInfo()
            var isEos = false

            while (!isEos && pcmSamples.size < maxSamples) {
                val inputIndex = codec.dequeueInputBuffer(10_000)
                if (inputIndex >= 0) {
                    val inputBuffer = codec.getInputBuffer(inputIndex) ?: continue
                    val sampleSize = extractor.readSampleData(inputBuffer, 0)
                    if (sampleSize < 0) {
                        codec.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                        isEos = true
                    } else {
                        codec.queueInputBuffer(inputIndex, 0, sampleSize, extractor.sampleTime, 0)
                        extractor.advance()
                    }
                }

                val outputIndex = codec.dequeueOutputBuffer(bufferInfo, 10_000)
                if (outputIndex >= 0) {
                    val outputBuffer = codec.getOutputBuffer(outputIndex)
                    if (outputBuffer != null) {
                        outputBuffer.order(ByteOrder.LITTLE_ENDIAN)
                        val shortBuffer = outputBuffer.asShortBuffer()
                        while (shortBuffer.hasRemaining() && pcmSamples.size < maxSamples) {
                            pcmSamples.add(shortBuffer.get().toFloat() / Short.MAX_VALUE)
                        }
                    }
                    codec.releaseOutputBuffer(outputIndex, false)
                }
            }

            codec.stop()
            codec.release()
            extractor.release()

            if (pcmSamples.isEmpty()) return@withContext null

            // YAMNet은 16kHz 모노 입력을 기대 — 리샘플링
            if (sampleRate != SAMPLE_RATE) {
                resample(pcmSamples.toFloatArray(), sampleRate, SAMPLE_RATE)
            } else {
                pcmSamples.toFloatArray()
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun resample(input: FloatArray, fromRate: Int, toRate: Int): FloatArray {
        val ratio = fromRate.toDouble() / toRate
        val outputLength = (input.size / ratio).toInt()
        return FloatArray(outputLength) { i ->
            val srcIndex = (i * ratio).toInt().coerceIn(0, input.size - 1)
            input[srcIndex]
        }
    }

    /**
     * 0.975초(15600 samples) 윈도우를 슬라이딩하면서 여러 번 추론 후 평균 점수를 반환한다.
     */
    private fun runSlidingWindowInference(audioData: FloatArray): FloatArray? {
        val interpreter = interpreter ?: return null
        if (audioData.size < WINDOW_SAMPLES) return null

        val accumulatedScores = FloatArray(NUM_CLASSES)
        val windowCount = (audioData.size - WINDOW_SAMPLES) / (WINDOW_SAMPLES / 2) + 1 // 50% overlap
        var validWindows = 0

        for (w in 0 until windowCount.coerceAtMost(30)) { // 최대 30 윈도우
            val offset = w * (WINDOW_SAMPLES / 2)
            if (offset + WINDOW_SAMPLES > audioData.size) break

            val inputBuffer = ByteBuffer.allocateDirect(WINDOW_SAMPLES * 4).apply {
                order(ByteOrder.nativeOrder())
                for (i in 0 until WINDOW_SAMPLES) {
                    putFloat(audioData[offset + i])
                }
                rewind()
            }

            val output = Array(1) { FloatArray(NUM_CLASSES) }
            try {
                interpreter.run(inputBuffer, output)
                for (i in 0 until NUM_CLASSES) {
                    accumulatedScores[i] += output[0][i]
                }
                validWindows++
            } catch (e: Exception) {
                continue
            }
        }

        if (validWindows == 0) return null

        // 평균
        for (i in accumulatedScores.indices) {
            accumulatedScores[i] /= validWindows
        }
        return accumulatedScores
    }

    private fun mapToTags(trackId: String, scores: FloatArray): AudioTags {
        val genres = mutableListOf<TagScore>()
        val moods = mutableListOf<TagScore>()

        scores.forEachIndexed { index, confidence ->
            if (confidence < MIN_CONFIDENCE) return@forEachIndexed
            if (index >= classNames.size) return@forEachIndexed

            val className = classNames[index]
            AudioSetMapper.mapToGenres(className)?.let { genre ->
                genres.add(TagScore(genre, confidence))
            }
            AudioSetMapper.mapToMood(className)?.let { mood ->
                moods.add(TagScore(mood, confidence))
            }
        }

        return AudioTags(
            trackId = trackId,
            genres = genres.sortedByDescending { it.confidence }.take(3),
            moods = moods.sortedByDescending { it.confidence }.take(3),
        )
    }
}

private fun TrackTagEntity.toAudioTags(): AudioTags {
    fun parseTagScores(json: String): List<TagScore> {
        return try {
            val array = JSONArray(json)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                TagScore(obj.getString("tag"), obj.getDouble("confidence").toFloat())
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    return AudioTags(
        trackId = trackId,
        genres = parseTagScores(genres),
        moods = parseTagScores(moods),
    )
}

private fun AudioTags.toEntity(): TrackTagEntity {
    fun toJson(tags: List<TagScore>): String {
        val array = JSONArray()
        tags.forEach { tag ->
            val obj = org.json.JSONObject()
            obj.put("tag", tag.tag)
            obj.put("confidence", tag.confidence.toDouble())
            array.put(obj)
        }
        return array.toString()
    }
    return TrackTagEntity(
        trackId = trackId,
        genres = toJson(genres),
        moods = toJson(moods),
        analyzedAt = System.currentTimeMillis(),
        modelVersion = MODEL_VERSION,
    )
}
