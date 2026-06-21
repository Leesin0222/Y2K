package com.yongjincomapny.y2k.core.player

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.LoudnessEnhancer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EqualizerManagerImpl @Inject constructor() : EqualizerManager {

    private val _state = MutableStateFlow(EqualizerState())
    override val state: StateFlow<EqualizerState> = _state.asStateFlow()

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var loudnessEnhancer: LoudnessEnhancer? = null

    override fun initialize(audioSessionId: Int) {
        if (audioSessionId == 0) return
        release()

        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _state.value.enabled
            }
            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = _state.value.bassBoostEnabled
            }
            loudnessEnhancer = LoudnessEnhancer(audioSessionId).apply {
                enabled = _state.value.loudnessEnabled
            }
            syncState()
        } catch (_: Exception) {
            // AudioEffect not available on this device
        }
    }

    private fun syncState() {
        val eq = equalizer ?: return
        val bands = (0 until eq.numberOfBands).map { i ->
            BandInfo(
                index = i,
                centerFrequency = eq.getCenterFreq(i.toShort()) / 1000,
                level = eq.getBandLevel(i.toShort()).toInt(),
            )
        }
        val presets = (0 until eq.numberOfPresets).map { i ->
            eq.getPresetName(i.toShort())
        }

        _state.update {
            it.copy(
                bands = bands,
                presets = presets,
                minLevel = eq.bandLevelRange[0].toInt(),
                maxLevel = eq.bandLevelRange[1].toInt(),
            )
        }
    }

    override fun setEnabled(enabled: Boolean) {
        equalizer?.enabled = enabled
        _state.update { it.copy(enabled = enabled) }
    }

    override fun setBandLevel(bandIndex: Int, level: Int) {
        equalizer?.setBandLevel(bandIndex.toShort(), level.toShort())
        _state.update {
            it.copy(
                currentPreset = -1,
                bands = it.bands.map { band ->
                    if (band.index == bandIndex) band.copy(level = level) else band
                },
            )
        }
    }

    override fun selectPreset(presetIndex: Int) {
        equalizer?.usePreset(presetIndex.toShort())
        _state.update { it.copy(currentPreset = presetIndex) }
        syncState()
    }

    override fun setBassBoostEnabled(enabled: Boolean) {
        bassBoost?.enabled = enabled
        _state.update { it.copy(bassBoostEnabled = enabled) }
    }

    override fun setBassBoostStrength(strength: Int) {
        try {
            bassBoost?.setStrength(strength.toShort())
        } catch (_: Exception) { }
        _state.update { it.copy(bassBoostStrength = strength) }
    }

    override fun setLoudnessEnabled(enabled: Boolean) {
        loudnessEnhancer?.enabled = enabled
        _state.update { it.copy(loudnessEnabled = enabled) }
    }

    override fun setSurroundEnabled(enabled: Boolean) {
        _state.update { it.copy(surroundEnabled = enabled) }
    }

    override fun setGaplessEnabled(enabled: Boolean) {
        _state.update { it.copy(gaplessEnabled = enabled) }
    }

    override fun release() {
        equalizer?.release()
        bassBoost?.release()
        loudnessEnhancer?.release()
        equalizer = null
        bassBoost = null
        loudnessEnhancer = null
    }
}
