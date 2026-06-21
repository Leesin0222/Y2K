package com.yongjincomapny.y2k.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

private val artistColors = listOf(
    Color(0xFFE8D5B7) to Color(0xFFC9A87C),
    Color(0xFFD4B8E0) to Color(0xFFA078B8),
    Color(0xFFB8D4E0) to Color(0xFF7098B8),
    Color(0xFFC8E0C8) to Color(0xFF78B878),
    Color(0xFFF5D3D3) to Color(0xFFD47474),
)

private data class RandomProfile(
    val name: String,
    val handle: String,
    val initials: String,
    val year: Int,
)

private val profilePool = listOf(
    RandomProfile("Alex", "@alex_y2k", "AL", 2023),
    RandomProfile("Luna", "@luna_beats", "LU", 2024),
    RandomProfile("Kai", "@kai_music", "KA", 2022),
    RandomProfile("Miso", "@miso_wav", "MI", 2024),
    RandomProfile("Haru", "@haru_sound", "HR", 2023),
    RandomProfile("Nova", "@nova_hifi", "NV", 2024),
    RandomProfile("Zen", "@zen_vinyl", "ZN", 2022),
    RandomProfile("Riku", "@riku_bass", "RK", 2023),
)

@Composable
fun ProfileScreen(
    tracks: List<Y2KTrack>,
    appVersion: String,
    modifier: Modifier = Modifier,
) {
    val profile = remember { profilePool.random() }
    val artists = remember(tracks) {
        tracks.groupBy { it.artist }
            .map { (artist, trackList) -> artist to trackList.size }
            .sortedByDescending { it.second }
            .take(5)
    }
    val totalDuration = remember(tracks) { tracks.sumOf { it.duration } }
    val totalHours = totalDuration / 3_600_000
    val artistCount = remember(tracks) { tracks.map { it.artist }.distinct().size }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 16.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("✦", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text("Profile", style = MaterialTheme.typography.displayMedium)
        }

        // Profile card
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box {
                Box(
                    modifier = Modifier.size(72.dp).offset(x = 4.dp, y = 4.dp)
                        .clip(CircleShape).background(MaterialTheme.colorScheme.onSurface),
                )
                Box(
                    modifier = Modifier.size(72.dp).clip(CircleShape)
                        .background(MaterialTheme.colorScheme.onSurface)
                        .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(profile.initials, style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.background)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(profile.name, style = MaterialTheme.typography.displaySmall)
                Text(profile.handle, fontFamily = MonoFontFamily, fontSize = 13.sp, color = MaterialTheme.colorScheme.primary)
                Text("Since ${profile.year}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Box(
                modifier = Modifier.clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    .clickable { }
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) { Text("Edit", fontSize = 12.sp, fontWeight = FontWeight.Medium) }
        }

        // Subscription badge
        Box(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth()
                .clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.onSurface).padding(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("✦", fontSize = 28.sp, color = MaterialTheme.colorScheme.primary)
                Column {
                    Text("Y2K Premium", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.background)
                    Text("HI-RES 무제한 · 오프라인 재생", fontSize = 12.sp, color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f))
                }
            }
            Text("✦ ✦ ✦", modifier = Modifier.align(Alignment.TopEnd), fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        }

        Spacer(Modifier.height(24.dp))

        // Listening stats
        Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("${tracks.size}" to "트랙", "${totalHours}h" to "청취 시간", "$artistCount" to "아티스트").forEach { (num, label) ->
                Column(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(num, style = MaterialTheme.typography.displaySmall, fontSize = 18.sp)
                    Text(label, fontFamily = MonoFontFamily, fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 0.06.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Top artists
        if (artists.isNotEmpty()) {
            SectionHeader("많이 들은 아티스트")
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                artists.forEachIndexed { index, (name, count) ->
                    val colors = artistColors[index % artistColors.size]
                    Column(modifier = Modifier.width(80.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(64.dp).clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                .background(Brush.linearGradient(listOf(colors.first, colors.second))),
                            contentAlignment = Alignment.Center,
                        ) { Text("♫", fontSize = 24.sp) }
                        Spacer(Modifier.height(8.dp))
                        Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("$count tracks", fontFamily = MonoFontFamily, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }

        // Settings
        SectionHeader("설정")
        Spacer(Modifier.height(12.dp))

        val settingsGroups = listOf(
            listOf(
                Triple(Icons.Default.Mic, "오디오 품질", "HI-RES · FLAC 우선"),
                Triple(Icons.Default.Download, "다운로드", "${tracks.size}곡"),
                Triple(Icons.Default.Settings, "일반 설정", "언어, 알림, 캐시"),
            ),
            listOf(
                Triple(Icons.Default.Notifications, "알림", "새 릴리즈, 추천 알림"),
                Triple(Icons.Default.Security, "개인정보 보호", "계정 보안, 데이터 관리"),
            ),
            listOf(
                Triple(Icons.AutoMirrored.Filled.HelpOutline, "도움말 & 피드백", "FAQ, 버그 리포트"),
                Triple(Icons.Default.Info, "앱 정보", "Y2K v$appVersion"),
            ),
        )

        settingsGroups.forEach { group ->
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp).fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp)),
            ) {
                group.forEach { (icon, name, desc) -> SettingsItem(icon, name, desc) }
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SettingsItem(icon: ImageVector, name: String, desc: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = MaterialTheme.typography.titleMedium)
            Text(desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
