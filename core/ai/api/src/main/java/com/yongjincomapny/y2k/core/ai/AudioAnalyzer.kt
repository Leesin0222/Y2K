package com.yongjincomapny.y2k.core.ai

import com.yongjincomapny.y2k.core.player.Y2KTrack

interface AudioAnalyzer {
    suspend fun analyze(track: Y2KTrack): AudioTags?
    suspend fun analyzeBatch(
        tracks: List<Y2KTrack>,
        onProgress: suspend (current: Int, total: Int) -> Unit = { _, _ -> },
    ): List<AudioTags>
}

data class AudioTags(
    val trackId: String,
    val genres: List<TagScore>,
    val moods: List<TagScore>,
)

data class TagScore(
    val tag: String,
    val confidence: Float,
)
