package com.yongjincomapny.y2k.core.ai

import android.content.Context
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val MODEL_DIR = "gemma"
private const val MODEL_FILENAME = "gemma3-2b-it-gpu-int4.bin"
private const val MODEL_URL = "https://storage.googleapis.com/mediapipe-models/llm_inference/gemma3-2b-it-gpu-int4/float16/latest/gemma3-2b-it-gpu-int4.bin"
private const val MAX_TOKENS = 512
private const val MIN_RAM_MB = 6000L

@Singleton
class GemmaEngine @Inject constructor(
    @ApplicationContext private val context: Context,
) : LlmEngine {

    private var inference: LlmInference? = null

    private val _isAvailable = MutableStateFlow(false)
    override val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()

    private val _downloadState = MutableStateFlow<ModelDownloadState>(ModelDownloadState.NotDownloaded)
    override val downloadState: StateFlow<ModelDownloadState> = _downloadState.asStateFlow()

    init {
        val modelFile = modelFile()
        if (modelFile.exists()) {
            _downloadState.value = ModelDownloadState.Ready
            _isAvailable.value = hasEnoughRam()
        }
    }

    override suspend fun downloadModel(onProgress: suspend (Float) -> Unit): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                if (!hasEnoughRam()) {
                    return@withContext Result.failure(
                        IllegalStateException("6GB 이상의 RAM이 필요합니다")
                    )
                }

                val modelFile = modelFile()
                if (modelFile.exists()) {
                    _downloadState.value = ModelDownloadState.Ready
                    _isAvailable.value = true
                    return@withContext Result.success(Unit)
                }

                _downloadState.value = ModelDownloadState.Downloading(0f)

                modelFile.parentFile?.mkdirs()

                val url = java.net.URL(MODEL_URL)
                val connection = url.openConnection()
                connection.connect()
                val totalBytes = connection.contentLengthLong

                connection.getInputStream().buffered().use { input ->
                    modelFile.outputStream().buffered().use { output ->
                        val buffer = ByteArray(8192)
                        var downloaded = 0L
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            downloaded += read
                            val progress = if (totalBytes > 0) downloaded.toFloat() / totalBytes else 0f
                            _downloadState.value = ModelDownloadState.Downloading(progress)
                            onProgress(progress)
                        }
                    }
                }

                _downloadState.value = ModelDownloadState.Ready
                _isAvailable.value = true
                Result.success(Unit)
            } catch (e: Exception) {
                modelFile().delete()
                _downloadState.value = ModelDownloadState.Error(e.message ?: "다운로드 실패")
                Result.failure(e)
            }
        }

    override suspend fun deleteModel(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            releaseModel()
            val dir = File(context.filesDir, MODEL_DIR)
            dir.deleteRecursively()
            _downloadState.value = ModelDownloadState.NotDownloaded
            _isAvailable.value = false
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun query(input: String, trackTags: List<AudioTags>): LlmResponse =
        withContext(Dispatchers.Default) {
            val llm = ensureModel() ?: return@withContext fallbackResponse(input, trackTags)

            val availableGenres = trackTags.flatMap { it.genres }.map { it.tag }.distinct()
            val availableMoods = trackTags.flatMap { it.moods }.map { it.tag }.distinct()

            val prompt = buildPrompt(input, availableGenres, availableMoods)

            try {
                val response = llm.generateResponse(prompt)
                parseResponse(response)
            } catch (e: OutOfMemoryError) {
                releaseModel()
                fallbackResponse(input, trackTags)
            } catch (e: Exception) {
                fallbackResponse(input, trackTags)
            }
        }

    private fun buildPrompt(
        input: String,
        availableGenres: List<String>,
        availableMoods: List<String>,
    ): String = buildString {
        append("<start_of_turn>user\n")
        append("You are a music DJ assistant. Given the user's request and available music tags, ")
        append("return ONLY a JSON object with targetGenres, targetMoods, and explanation (in Korean).\n")
        append("Available genres: ${availableGenres.joinToString(", ")}\n")
        append("Available moods: ${availableMoods.joinToString(", ")}\n")
        append("User request: $input\n")
        append("Respond with JSON only, no markdown.\n")
        append("<end_of_turn>\n")
        append("<start_of_turn>model\n")
    }

    private fun parseResponse(response: String): LlmResponse {
        return try {
            // JSON 부분 추출
            val jsonStr = response.trim()
                .removePrefix("```json").removePrefix("```")
                .removeSuffix("```").trim()
            val json = JSONObject(jsonStr)
            LlmResponse(
                targetGenres = json.optJSONArray("targetGenres")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList(),
                targetMoods = json.optJSONArray("targetMoods")?.let { arr ->
                    (0 until arr.length()).map { arr.getString(it) }
                } ?: emptyList(),
                explanation = json.optString("explanation", ""),
            )
        } catch (e: Exception) {
            LlmResponse(emptyList(), emptyList(), response.take(200))
        }
    }

    private fun fallbackResponse(input: String, trackTags: List<AudioTags>): LlmResponse {
        // Gemma 없이 키워드 매칭으로 폴백
        val lower = input.lowercase()
        val moods = mutableListOf<String>()
        val genres = mutableListOf<String>()

        val moodKeywords = mapOf(
            "슬픈" to "Sad", "슬프" to "Sad", "우울" to "Sad", "감성" to "Sad",
            "비 오는" to "Sad", "비오는" to "Sad",
            "신나" to "Energetic", "힘찬" to "Energetic", "운동" to "Energetic", "파티" to "Energetic",
            "행복" to "Happy", "기분 좋" to "Happy", "좋은" to "Happy",
            "잔잔" to "Tender", "잠" to "Tender", "편안" to "Tender", "휴식" to "Tender",
            "화난" to "Aggressive", "격한" to "Aggressive",
            "무서" to "Dark", "어두" to "Dark",
        )
        val genreKeywords = mapOf(
            "팝" to "Pop", "록" to "Rock", "락" to "Rock",
            "힙합" to "Hip-Hop", "랩" to "Hip-Hop",
            "재즈" to "Jazz", "클래식" to "Classical",
            "전자" to "Electronic", "일렉" to "Electronic",
            "알앤비" to "R&B", "r&b" to "R&B",
            "발라드" to "Vocal",
        )

        moodKeywords.forEach { (keyword, mood) ->
            if (lower.contains(keyword)) moods.add(mood)
        }
        genreKeywords.forEach { (keyword, genre) ->
            if (lower.contains(keyword)) genres.add(genre)
        }

        if (moods.isEmpty() && genres.isEmpty()) {
            moods.add("Happy")
        }

        return LlmResponse(
            targetGenres = genres.distinct(),
            targetMoods = moods.distinct(),
            explanation = "키워드 기반으로 곡을 선택했어요",
        )
    }

    private fun ensureModel(): LlmInference? {
        if (inference != null) return inference
        val modelFile = modelFile()
        if (!modelFile.exists()) return null
        return try {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelFile.absolutePath)
                .setMaxTokens(MAX_TOKENS)
                .build()
            LlmInference.createFromOptions(context, options).also { inference = it }
        } catch (e: Exception) {
            null
        }
    }

    private fun releaseModel() {
        inference?.close()
        inference = null
    }

    private fun modelFile(): File =
        File(context.filesDir, "$MODEL_DIR/$MODEL_FILENAME")

    private fun hasEnoughRam(): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return memInfo.totalMem / (1024 * 1024) >= MIN_RAM_MB
    }
}
