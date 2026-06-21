package com.yongjincomapny.y2k.core.player

import kotlinx.coroutines.flow.StateFlow

interface Y2KPlayer {
    val playbackState: StateFlow<PlaybackState>
    val audioSessionId: Int

    fun play()
    fun pause()
    fun togglePlayPause()
    fun next()
    fun previous()
    fun seekTo(positionMs: Long)
    fun setQueue(tracks: List<Y2KTrack>, startIndex: Int = 0)
    fun addToQueue(track: Y2KTrack)
    fun toggleShuffle()
    fun cycleRepeatMode()
    fun clearError()
}
