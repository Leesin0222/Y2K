package com.yongjincomapny.y2k.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.ai.SmartPlaylist
import com.yongjincomapny.y2k.core.player.Y2KAlbum
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.core.player.toAlbums
import com.yongjincomapny.y2k.designsystem.component.AlbumArt
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.component.Y2KButton
import com.yongjincomapny.y2k.designsystem.component.Y2KButtonStyle
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

private val albumColors = listOf(
    Color(0xFFD4A574),
    Color(0xFF74A5D4),
    Color(0xFFA574D4),
    Color(0xFFD47474),
    Color(0xFF78B878),
)

private val trackGradients = listOf(
    Color(0xFFE8D5B7) to Color(0xFFC9A87C),
    Color(0xFFD4B8E0) to Color(0xFFA078B8),
    Color(0xFFB8D4E0) to Color(0xFF7098B8),
    Color(0xFFE0D4B8) to Color(0xFFB8A070),
    Color(0xFFC8E0C8) to Color(0xFF78B878),
)

@Composable
fun HomeScreen(
    tracks: List<Y2KTrack>,
    smartPlaylists: List<SmartPlaylist> = emptyList(),
    analysisProgress: Pair<Int, Int>? = null,
    onTrackClick: (index: Int) -> Unit,
    onAlbumClick: (album: String) -> Unit,
    onShufflePlay: () -> Unit,
    onSmartPlaylistClick: (SmartPlaylist) -> Unit = {},
    onAiDjClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val albums = remember(tracks) { tracks.toAlbums() }
    val playlists = remember(tracks, albums) { buildPlaylists(tracks, albums) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 4.dp, top = 12.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("✦", color = Y2KTheme.colors.accent, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text("Y2K", style = Y2KTheme.textStyles.displayMedium)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { }) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications", modifier = Modifier.size(22.dp))
                }
            }
        }
        item { AiDjFeaturedCard(trackCount = tracks.size, onAiDjClick = onAiDjClick, onShufflePlay = onShufflePlay) }
        if (analysisProgress != null) {
            item { AnalysisProgressBanner(analysisProgress) }
        }
        item {
            SectionHeader("트랙")
            Spacer(Modifier.height(8.dp))
            RecommendationRow(tracks.take(5), onTrackClick)
        }
        if (albums.isNotEmpty()) {
            item {
                Spacer(Modifier.height(24.dp))
                SectionHeader("앨범")
                Spacer(Modifier.height(12.dp))
                AlbumGrid(albums, onAlbumClick)
            }
        }
        if (smartPlaylists.isNotEmpty()) {
            item {
                Spacer(Modifier.height(24.dp))
                SectionHeader("AI 추천")
                Spacer(Modifier.height(12.dp))
                SmartPlaylistRow(smartPlaylists, onSmartPlaylistClick)
            }
        }
        if (playlists.isNotEmpty()) {
            item {
                Spacer(Modifier.height(32.dp))
                SectionHeader("플레이리스트")
                Spacer(Modifier.height(12.dp))
                PlaylistSection(playlists, onAlbumClick)
            }
        }
    }
}

@Composable
private fun AiDjFeaturedCard(trackCount: Int, onAiDjClick: () -> Unit, onShufflePlay: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = 4.dp, y = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Y2KTheme.colors.accent),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Y2KTheme.colors.fg)
                .clickable(onClick = onAiDjClick)
                .padding(20.dp),
        ) {
            Column {
                Text(
                    text = "✦ AI DJ",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 0.08.sp,
                    color = Y2KTheme.colors.accent,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ask\nYour DJ",
                    style = Y2KTheme.textStyles.headlineMedium,
                    color = Y2KTheme.colors.bg,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "자연어로 원하는 음악을 요청하세요. $trackCount Tracks",
                    style = Y2KTheme.textStyles.bodySmall,
                    color = Y2KTheme.colors.bg.copy(alpha = 0.7f),
                )
                Spacer(Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Y2KButton(
                        text = "AI DJ",
                        onClick = onAiDjClick,
                        style = Y2KButtonStyle.Primary,
                    )
                    Y2KButton(
                        text = "Shuffle",
                        onClick = onShufflePlay,
                        icon = Icons.Default.PlayArrow,
                        style = Y2KButtonStyle.Secondary,
                    )
                }
            }
            Text(
                text = "✦ ✦ ✦",
                modifier = Modifier.align(Alignment.TopEnd),
                fontSize = 24.sp,
                color = Y2KTheme.colors.accent.copy(alpha = 0.15f),
            )
        }
    }
}

@Composable
private fun RecommendationRow(tracks: List<Y2KTrack>, onTrackClick: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(tracks.size) { i ->
            val track = tracks[i]
            val gradient = trackGradients[i % trackGradients.size]
            Column(
                modifier = Modifier.width(148.dp).clickable { onTrackClick(i) },
            ) {
                Box(
                    modifier = Modifier.size(148.dp),
                ) {
                    AlbumArt(
                        artworkUri = track.artworkUri,
                        size = 148.dp,
                        cornerRadius = 8.dp,
                        fallbackGradient = gradient,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(track.title, style = Y2KTheme.textStyles.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(track.artist, style = Y2KTheme.textStyles.labelSmall, color = Y2KTheme.colors.fgMuted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
private fun AlbumGrid(albums: List<Y2KAlbum>, onAlbumClick: (album: String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (row in albums.chunked(2)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for ((index, album) in row.withIndex()) {
                    val color = albumColors[(albums.indexOf(album)) % albumColors.size]
                    Column(modifier = Modifier.weight(1f).clickable { onAlbumClick(album.name) }) {
                        Box(
                            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
                        ) {
                            AlbumArt(
                                artworkUri = album.artworkUri,
                                modifier = Modifier.fillMaxSize(),
                                cornerRadius = 8.dp,
                                fallbackColor = color,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(album.name, style = Y2KTheme.textStyles.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(album.artist, style = Y2KTheme.textStyles.labelSmall, color = Y2KTheme.colors.fgMuted)
                    }
                }
                if (row.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

private data class PlaylistInfo(
    val name: String,
    val trackCount: Int,
    val totalDurationMs: Long,
    val mosaicColors: List<Color>,
    val firstAlbum: String,
)

private val mosaicPalette = listOf(
    Color(0xFFE8D5B7), Color(0xFFD4B8E0), Color(0xFFB8D4E0), Color(0xFFE0D4B8),
    Color(0xFFC8E0C8), Color(0xFFF5D3D3), Color(0xFFD3E6F5), Color(0xFFE6D3F5),
    Color(0xFFF5E6D3), Color(0xFFD4A574), Color(0xFF74A5D4), Color(0xFFA574D4),
)

private fun buildPlaylists(tracks: List<Y2KTrack>, albums: List<Y2KAlbum>): List<PlaylistInfo> {
    if (albums.size < 2) return emptyList()
    val chunked = albums.chunked(2.coerceAtLeast(albums.size / 3))
    return chunked.take(3).mapIndexed { index, group ->
        val groupTracks = tracks.filter { t -> group.any { it.name == t.album } }
        val colorOffset = index * 4
        PlaylistInfo(
            name = group.joinToString(" & ") { it.name },
            trackCount = groupTracks.size,
            totalDurationMs = groupTracks.sumOf { it.duration },
            mosaicColors = (0 until 4).map { mosaicPalette[(colorOffset + it) % mosaicPalette.size] },
            firstAlbum = group.first().name,
        )
    }
}

@Composable
private fun PlaylistSection(playlists: List<PlaylistInfo>, onAlbumClick: (album: String) -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        playlists.forEach { playlist ->
            val totalSec = playlist.totalDurationMs / 1000
            val hours = totalSec / 3600
            val mins = (totalSec % 3600) / 60
            val meta = if (hours > 0) "${playlist.trackCount} tracks · ${hours}h ${mins}m"
            else "${playlist.trackCount} tracks · ${mins}m"

            Row(
                modifier = Modifier.fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
                    .background(Y2KTheme.colors.surface)
                    .clickable { onAlbumClick(playlist.firstAlbum) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier.size(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Y2KTheme.colors.border),
                ) {
                    Column {
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().background(playlist.mosaicColors[0]),
                                contentAlignment = Alignment.Center,
                            ) { Text("♫", fontSize = 14.sp) }
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().background(playlist.mosaicColors[1]),
                                contentAlignment = Alignment.Center,
                            ) { Text("♫", fontSize = 14.sp) }
                        }
                        Row(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().background(playlist.mosaicColors[2]),
                                contentAlignment = Alignment.Center,
                            ) { Text("♫", fontSize = 14.sp) }
                            Box(
                                modifier = Modifier.weight(1f).fillMaxSize().background(playlist.mosaicColors[3]),
                                contentAlignment = Alignment.Center,
                            ) { Text("♫", fontSize = 14.sp) }
                        }
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(playlist.name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(meta, fontFamily = MonoFontFamily, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
                }
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Y2KTheme.colors.fgMuted,
                )
            }
        }
    }
}

@Composable
private fun AnalysisProgressBanner(progress: Pair<Int, Int>) {
    val (current, total) = progress
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Y2KTheme.colors.surface)
            .border(1.5.dp, Y2KTheme.colors.accent.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text("✦", color = Y2KTheme.colors.accent, fontSize = 16.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "AI 분석 중",
                style = Y2KTheme.textStyles.titleMedium,
                fontSize = 13.sp,
            )
            Text(
                "$current / $total 트랙",
                fontFamily = MonoFontFamily,
                fontSize = 11.sp,
                color = Y2KTheme.colors.fgMuted,
            )
        }
        Text(
            "${(current * 100 / total.coerceAtLeast(1))}%",
            fontFamily = MonoFontFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Y2KTheme.colors.accent,
        )
    }
}

@Composable
private fun SmartPlaylistRow(
    playlists: List<SmartPlaylist>,
    onClick: (SmartPlaylist) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(playlists.size) { i ->
            val playlist = playlists[i]
            val gradient = trackGradients[i % trackGradients.size]
            Column(
                modifier = Modifier
                    .width(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
                    .background(Y2KTheme.colors.surface)
                    .clickable { onClick(playlist) }
                    .padding(12.dp),
            ) {
                Box(
                    modifier = Modifier.size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(gradient.first, gradient.second))),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        if (playlist.type == com.yongjincomapny.y2k.core.ai.PlaylistType.GENRE) "♫" else "✦",
                        fontSize = 16.sp,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    playlist.name,
                    style = Y2KTheme.textStyles.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${playlist.tracks.size} tracks",
                    fontFamily = MonoFontFamily,
                    fontSize = 10.sp,
                    color = Y2KTheme.colors.fgMuted,
                )
            }
        }
    }
}
