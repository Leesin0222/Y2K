package com.yongjincomapny.y2k.core.player

import kotlinx.coroutines.flow.StateFlow

data class EqualizerState(
    val enabled: Boolean = false,
    val bands: List<BandInfo> = emptyList(),
    val presets: List<String> = emptyList(),
    val currentPreset: Int = -1,
    val minLevel: Int = -1500,
    val maxLevel: Int = 1500,
    val bassBoostEnabled: Boolean = false,
    val bassBoostStrength: Int = 0,
    val loudnessEnabled: Boolean = false,
    val surroundEnabled: Boolean = false,
    val gaplessEnabled: Boolean = true,
)

data class BandInfo(
    val index: Int,
    val centerFrequency: Int,
    val level: Int,
)

interface EqualizerManager {
    val state: StateFlow<EqualizerState>

    fun initialize(audioSessionId: Int)
    fun setEnabled(enabled: Boolean)
    fun setBandLevel(bandIndex: Int, level: Int)
    fun selectPreset(presetIndex: Int)
    fun setBassBoostEnabled(enabled: Boolean)
    fun setBassBoostStrength(strength: Int)
    fun setLoudnessEnabled(enabled: Boolean)
    fun setSurroundEnabled(enabled: Boolean)
    fun setGaplessEnabled(enabled: Boolean)
    fun release()
}
