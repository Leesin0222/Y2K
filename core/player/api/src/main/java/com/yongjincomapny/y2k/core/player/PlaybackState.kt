package com.yongjincomapny.y2k.core.player

data class PlaybackState(
    val currentTrack: Y2KTrack? = null,
    val isPlaying: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
    val queue: List<Y2KTrack> = emptyList(),
    val currentIndex: Int = -1,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val error: PlaybackError? = null,
) {
    val progress: Float
        get() = if (duration > 0) position.toFloat() / duration else 0f
}

data class PlaybackError(
    val message: String,
    val code: Int = 0,
)

enum class RepeatMode { OFF, ONE, ALL }
