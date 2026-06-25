package com.yongjincomapny.y2k.feature.library

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.yongjincomapny.y2k.core.player.Y2KAlbum
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.core.player.toAlbums
import com.yongjincomapny.y2k.designsystem.component.AlbumArt
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.component.Y2KChip
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

private val gradientColors = listOf(
    Color(0xFFF5E6D3) to Color(0xFFD4A574),
    Color(0xFFD3E6F5) to Color(0xFF74A5D4),
    Color(0xFFE6D3F5) to Color(0xFFA574D4),
    Color(0xFFF5D3D3) to Color(0xFFD47474),
    Color(0xFFC8E0C8) to Color(0xFF78B878),
    Color(0xFFE0D4B8) to Color(0xFFB8A070),
    Color(0xFFD4B8E0) to Color(0xFFA078B8),
    Color(0xFFB8D4E0) to Color(0xFF7098B8),
)

private data class ArtistInfo(
    val name: String,
    val initials: String,
    val albumCount: Int,
    val trackCount: Int,
)

@Composable
fun LibraryScreen(
    tracks: List<Y2KTrack>,
    onAlbumClick: (album: String) -> Unit,
    onTrackClick: (index: Int) -> Unit,
    onArtistClick: (artist: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Albums", "Playlists", "Artists")
    val recentTracks = remember(tracks) { tracks.takeLast(4).reversed() }
    val albums = remember(tracks) { tracks.toAlbums() }
    val hiResCount = remember(tracks) { tracks.count { it.isHiRes } }
    val artists = remember(tracks) {
        tracks.groupBy { it.artist }.map { (artist, trackList) ->
            val albumCount = trackList.map { it.album }.distinct().size
            val initials = artist.split(" ")
                .take(2)
                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                .joinToString("")
                .ifEmpty { artist.take(2).uppercase() }
            ArtistInfo(artist, initials, albumCount, trackList.size)
        }.sortedByDescending { it.trackCount }
    }

    LazyColumn(modifier = modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
        // Title
        item {
            Row(
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("✦", color = Y2KTheme.colors.accent, fontSize = 20.sp)
                Spacer(Modifier.width(8.dp))
                Text("Library", style = Y2KTheme.textStyles.displayMedium)
            }
        }

        // Tabs
        item {
            Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tabs.forEachIndexed { index, tab ->
                    Y2KChip(
                        text = tab,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        // Stats — only on "All" tab
        if (selectedTab == 0) {
            item {
                Row(modifier = Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    listOf(
                        "${tracks.size}" to "Tracks",
                        "${albums.size}" to "Albums",
                        "${albums.size}" to "Playlists",
                    ).forEach { (num, label) ->
                        Column(
                            modifier = Modifier.weight(1f).clip(RoundedCornerShape(8.dp))
                                .background(Y2KTheme.colors.surface)
                                .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(num, style = Y2KTheme.textStyles.displaySmall)
                            Text(
                                label.uppercase(), fontFamily = MonoFontFamily, fontSize = 10.sp,
                                color = Y2KTheme.colors.fgMuted, letterSpacing = 0.06.sp,
                            )
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }
        }

        // === All tab: Quick Actions + Recently Added ===
        if (selectedTab == 0) {
            item {
                SectionHeader("바로가기")
                Spacer(Modifier.height(8.dp))
            }
            val quickActions = listOf(
                Triple(Icons.Default.Favorite, "좋아요 표시한 트랙", "$hiResCount tracks"),
                Triple(Icons.Default.Download, "다운로드한 트랙", "${tracks.size} tracks"),
                Triple(Icons.Default.AccessTime, "최근 재생", "${recentTracks.size} tracks"),
            )
            items(quickActions.size) { i ->
                val (icon, name, count) = quickActions[i]
                QuickActionItem(icon, name, count, showDivider = i < quickActions.size - 1, onClick = { })
            }
        }

        // === All tab: Recently Added ===
        if (selectedTab == 0 && recentTracks.isNotEmpty()) {
            item {
                Spacer(Modifier.height(24.dp))
                SectionHeader("최근 추가")
                Spacer(Modifier.height(8.dp))
            }
            itemsIndexed(recentTracks, key = { _, track -> "recent_${track.id}" }) { index, track ->
                val originalIndex = tracks.indexOf(track)
                val dateLabel = when (index) {
                    0 -> "오늘"
                    1 -> "어제"
                    else -> "${index}일 전"
                }
                RecentTrackRow(
                    track = track,
                    dateLabel = dateLabel,
                    colorIndex = index,
                    showDivider = index < recentTracks.size - 1,
                    onClick = { onTrackClick(originalIndex) },
                )
            }
        }

        // === Albums tab ===
        if (selectedTab == 1) {
            if (albums.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("내 앨범")
                    Spacer(Modifier.height(12.dp))
                    LibraryAlbumGrid(albums, onAlbumClick)
                }
            }
        }

        // === Playlists tab ===
        if (selectedTab == 2) {
            if (albums.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("내 플레이리스트")
                    Spacer(Modifier.height(12.dp))
                }
                items(albums.size) { i ->
                    val album = albums[i]
                    val albumTracks = tracks.filter { it.album == album.name }
                    val totalDuration = albumTracks.sumOf { it.duration }
                    PlaylistCard(
                        name = album.name,
                        trackCount = album.trackCount,
                        duration = formatDuration(totalDuration),
                        colorIndex = i,
                        onClick = { onAlbumClick(album.name) },
                    )
                }
            }
        }

        // === Artists tab ===
        if (selectedTab == 3) {
            if (artists.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("내 아티스트")
                    Spacer(Modifier.height(12.dp))
                }
                items(artists.size) { i ->
                    ArtistRow(
                        artist = artists[i],
                        colorIndex = i,
                        showDivider = i < artists.size - 1,
                        onClick = { onArtistClick(artists[i].name) },
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionItem(
    icon: ImageVector,
    name: String,
    count: String,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp))
                    .background(Y2KTheme.colors.surface)
                    .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) }
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = Y2KTheme.textStyles.titleMedium)
                Text(count, fontFamily = MonoFontFamily, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
                modifier = Modifier.size(18.dp), tint = Y2KTheme.colors.fgMuted,
            )
        }
        if (showDivider) {
            HorizontalDivider(color = Y2KTheme.colors.border, thickness = 1.dp)
        }
    }
}

@Composable
private fun PlaylistCard(
    name: String,
    trackCount: Int,
    duration: String,
    colorIndex: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Y2KTheme.colors.surface)
            .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 2x2 mosaic
        Box(
            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(4.dp)),
        ) {
            Column {
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 0..1) {
                        val colors = gradientColors[(colorIndex * 4 + j) % gradientColors.size]
                        Box(
                            modifier = Modifier.weight(1f).fillMaxSize()
                                .background(Brush.linearGradient(listOf(colors.first, colors.second))),
                            contentAlignment = Alignment.Center,
                        ) { Text("✦", fontSize = 12.sp) }
                    }
                }
                Row(modifier = Modifier.weight(1f)) {
                    for (j in 2..3) {
                        val colors = gradientColors[(colorIndex * 4 + j) % gradientColors.size]
                        Box(
                            modifier = Modifier.weight(1f).fillMaxSize()
                                .background(Brush.linearGradient(listOf(colors.first, colors.second))),
                            contentAlignment = Alignment.Center,
                        ) { Text("✦", fontSize = 12.sp) }
                    }
                }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(name, style = Y2KTheme.textStyles.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                "$trackCount tracks · $duration",
                fontFamily = MonoFontFamily, fontSize = 12.sp,
                color = Y2KTheme.colors.fgMuted,
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
            modifier = Modifier.size(18.dp), tint = Y2KTheme.colors.fgMuted,
        )
    }
}

@Composable
private fun ArtistRow(
    artist: ArtistInfo,
    colorIndex: Int,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val colors = gradientColors[colorIndex % gradientColors.size]
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape)
                    .border(1.5.dp, Y2KTheme.colors.border, CircleShape)
                    .background(Brush.linearGradient(listOf(colors.first, colors.second))),
                contentAlignment = Alignment.Center,
            ) {
                Text(artist.initials, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(artist.name, style = Y2KTheme.textStyles.titleMedium)
                val albumLabel = if (artist.albumCount == 1) "1 album" else "${artist.albumCount} albums"
                Text(
                    "$albumLabel · ${artist.trackCount} tracks",
                    fontFamily = MonoFontFamily, fontSize = 12.sp,
                    color = Y2KTheme.colors.fgMuted,
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null,
                modifier = Modifier.size(18.dp), tint = Y2KTheme.colors.fgMuted,
            )
        }
        if (showDivider) {
            HorizontalDivider(color = Y2KTheme.colors.border, thickness = 1.dp)
        }
    }
}

@Composable
private fun RecentTrackRow(
    track: Y2KTrack,
    dateLabel: String,
    colorIndex: Int,
    showDivider: Boolean,
    onClick: () -> Unit,
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val colors = gradientColors[colorIndex % gradientColors.size]
            AlbumArt(
                artworkUri = track.artworkUri,
                size = 48.dp,
                fallbackGradient = colors,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(track.title, style = Y2KTheme.textStyles.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${track.artist} · ${track.album}", fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
            }
            Text(dateLabel, fontFamily = MonoFontFamily, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
        }
        if (showDivider) {
            HorizontalDivider(color = Y2KTheme.colors.border, thickness = 1.dp)
        }
    }
}

@Composable
private fun LibraryAlbumGrid(albums: List<Y2KAlbum>, onAlbumClick: (album: String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        for (row in albums.chunked(2)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                for (album in row) {
                    val colors = gradientColors[albums.indexOf(album) % gradientColors.size]
                    Column(modifier = Modifier.weight(1f).clickable { onAlbumClick(album.name) }) {
                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                            AlbumArt(
                                artworkUri = album.artworkUri,
                                modifier = Modifier.fillMaxSize(),
                                cornerRadius = 8.dp,
                                fallbackColor = colors.second,
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(album.name, fontSize = 13.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(album.artist, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
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

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return if (hours > 0) "${hours}h ${"%02d".format(minutes)}m"
    else "${minutes}m"
}
