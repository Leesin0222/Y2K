package com.yongjincomapny.y2k.feature.equalizer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.player.EqualizerState
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.component.Y2KChip
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EqualizerScreen(
    eqState: EqualizerState,
    onEnabledChange: (Boolean) -> Unit,
    onBandLevelChange: (bandIndex: Int, level: Int) -> Unit,
    onPresetSelect: (Int) -> Unit,
    onBassBoostEnabledChange: (Boolean) -> Unit,
    onBassBoostStrengthChange: (Int) -> Unit,
    onLoudnessEnabledChange: (Boolean) -> Unit,
    onSurroundEnabledChange: (Boolean) -> Unit,
    onGaplessEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("✦", color = Y2KTheme.colors.accent, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text("Equalizer", style = Y2KTheme.textStyles.displayMedium)
        }

        // EQ 활성화 토글
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Equalizer", style = Y2KTheme.textStyles.titleMedium)
            Switch(
                checked = eqState.enabled,
                onCheckedChange = onEnabledChange,
                colors = SwitchDefaults.colors(checkedTrackColor = Y2KTheme.colors.accent),
            )
        }

        // 스펙트럼 시각화
        Column(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(16.dp))
                .background(Y2KTheme.colors.surface).padding(20.dp),
        ) {
            SectionHeader("오디오 스펙트럼")
            Spacer(Modifier.height(16.dp))

            if (eqState.bands.isNotEmpty()) {
                val range = (eqState.maxLevel - eqState.minLevel).coerceAtLeast(1)
                Row(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom,
                ) {
                    eqState.bands.forEach { band ->
                        val normalized = (band.level - eqState.minLevel).toFloat() / range
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier.width(16.dp).height((100 * normalized.coerceIn(0.05f, 1f)).dp)
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(
                                        if (eqState.enabled) Y2KTheme.colors.accent
                                        else Y2KTheme.colors.border
                                    ),
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    eqState.bands.forEach { band ->
                        val label = if (band.centerFrequency >= 1000) "${band.centerFrequency / 1000}K" else "${band.centerFrequency}"
                        Text(label, fontFamily = MonoFontFamily, fontSize = 9.sp, color = Y2KTheme.colors.fgMuted)
                    }
                }
            } else {
                Text("이퀄라이저를 사용할 수 없습니다", fontSize = 13.sp, color = Y2KTheme.colors.fgMuted)
            }
        }

        Spacer(Modifier.height(24.dp))

        // 프리셋
        if (eqState.presets.isNotEmpty()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("프리셋")
                Spacer(Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    eqState.presets.forEachIndexed { index, preset ->
                        Y2KChip(
                            text = preset,
                            selected = eqState.currentPreset == index,
                            onClick = { onPresetSelect(index) },
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 밴드 수동 조절
        if (eqState.bands.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(16.dp))
                    .background(Y2KTheme.colors.surface).padding(20.dp),
            ) {
                SectionHeader("수동 조절")
                Spacer(Modifier.height(16.dp))
                eqState.bands.forEach { band ->
                    val label = if (band.centerFrequency >= 1000) "${band.centerFrequency / 1000}K" else "${band.centerFrequency}"
                    val range = eqState.maxLevel.toFloat() - eqState.minLevel.toFloat()
                    val normalized = if (range > 0) (band.level - eqState.minLevel) / range else 0.5f
                    val db = band.level / 100

                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(label, fontFamily = MonoFontFamily, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted, modifier = Modifier.width(40.dp))
                        Slider(
                            value = normalized,
                            onValueChange = { value ->
                                val level = (eqState.minLevel + value * range).toInt()
                                onBandLevelChange(band.index, level)
                            },
                            modifier = Modifier.weight(1f),
                            enabled = eqState.enabled,
                            colors = SliderDefaults.colors(
                                thumbColor = Y2KTheme.colors.accent,
                                activeTrackColor = Y2KTheme.colors.accent,
                                inactiveTrackColor = Y2KTheme.colors.border,
                            ),
                        )
                        Text(
                            "${if (db >= 0) "+" else ""}${db}dB",
                            fontFamily = MonoFontFamily, fontSize = 12.sp,
                            color = Y2KTheme.colors.accent, modifier = Modifier.width(44.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        // 오디오 설정
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader("오디오 설정")
            Spacer(Modifier.height(12.dp))

            AudioSettingItem(
                name = "Bass Enhancer",
                desc = "저음역 부스트 효과",
                checked = eqState.bassBoostEnabled,
                onCheckedChange = onBassBoostEnabledChange,
            )
            if (eqState.bassBoostEnabled) {
                Slider(
                    value = eqState.bassBoostStrength / 1000f,
                    onValueChange = { onBassBoostStrengthChange((it * 1000).toInt()) },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Y2KTheme.colors.accent,
                        activeTrackColor = Y2KTheme.colors.accent,
                        inactiveTrackColor = Y2KTheme.colors.border,
                    ),
                )
            }
            AudioSettingItem(
                name = "3D Surround",
                desc = "공간감 있는 서라운드 효과",
                checked = eqState.surroundEnabled,
                onCheckedChange = onSurroundEnabledChange,
            )
            AudioSettingItem(
                name = "Loudness",
                desc = "볼륨 정규화",
                checked = eqState.loudnessEnabled,
                onCheckedChange = onLoudnessEnabledChange,
            )
            AudioSettingItem(
                name = "Gapless Playback",
                desc = "트랙 간 끊김 없는 재생",
                checked = eqState.gaplessEnabled,
                onCheckedChange = onGaplessEnabledChange,
            )
        }
    }
}

@Composable
private fun AudioSettingItem(
    name: String,
    desc: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = Y2KTheme.textStyles.titleMedium)
            Text(desc, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = Y2KTheme.colors.accent),
        )
    }
}
