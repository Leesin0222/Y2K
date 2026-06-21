package com.yongjincomapny.y2k.feature.artistdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.designsystem.component.AlbumArt
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.component.Y2KButton
import com.yongjincomapny.y2k.designsystem.component.Y2KButtonStyle
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    artistName: String,
    allTracks: List<Y2KTrack>,
    onBack: () -> Unit,
    onTrackClick: (index: Int) -> Unit,
    onAlbumClick: (album: String) -> Unit,
    onShufflePlay: () -> Unit,
    onPlayAll: () -> Unit,
    onArtistClick: (artist: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val artistTracks = allTracks.filter { it.artist == artistName }
    val albums = artistTracks.map { it.album }.distinct().filter { it.isNotBlank() }
    val initials = artistName.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .ifEmpty { artistName.take(2).uppercase() }

    val otherArtists = allTracks.map { it.artist }.distinct()
        .filter { it != artistName }
        .take(6)

    val colorIndex = allTracks.map { it.artist }.distinct().indexOf(artistName).coerceAtLeast(0)
    val heroColors = gradientColors[colorIndex % gradientColors.size]

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        TopAppBar(
            title = {
                Text(
                    "ARTIST",
                    fontFamily = MonoFontFamily,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.06.sp,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            windowInsets = WindowInsets(0),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            // Hero
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Avatar with hard shadow
                    Box {
                        Box(
                            modifier = Modifier.size(120.dp)
                                .offset(x = 4.dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.onSurface),
                        )
                        Box(
                            modifier = Modifier.size(120.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                .background(Brush.linearGradient(listOf(heroColors.first, heroColors.second))),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                initials,
                                style = MaterialTheme.typography.displaySmall,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.ExtraBold,
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(artistName, style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${albums.size} albums · ${artistTracks.size} tracks",
                        fontFamily = MonoFontFamily,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Y2KButton(
                            text = "Shuffle Play",
                            onClick = onShufflePlay,
                            icon = Icons.Default.Shuffle,
                            style = Y2KButtonStyle.Primary,
                            modifier = Modifier.weight(1f),
                        )
                        Y2KButton(
                            text = "Play All",
                            onClick = onPlayAll,
                            icon = Icons.Default.PlayArrow,
                            style = Y2KButtonStyle.Outlined,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }

            // Popular Tracks
            if (artistTracks.isNotEmpty()) {
                item {
                    SectionHeader("인기 트랙")
                    Spacer(Modifier.height(12.dp))
                }
                val topTracks = artistTracks.take(5)
                items(topTracks.size) { i ->
                    val track = topTracks[i]
                    val originalIndex = allTracks.indexOf(track)
                    val trackColors = gradientColors[i % gradientColors.size]
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onTrackClick(originalIndex) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                "${i + 1}",
                                fontFamily = MonoFontFamily,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.width(20.dp),
                            )
                            AlbumArt(
                                artworkUri = track.artworkUri,
                                size = 44.dp,
                                fallbackGradient = trackColors,
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    track.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    track.album,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Text(
                                formatDuration(track.duration),
                                fontFamily = MonoFontFamily,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (i < topTracks.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                        }
                    }
                }
            }

            // Albums
            if (albums.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("앨범")
                    Spacer(Modifier.height(12.dp))
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        for (row in albums.chunked(2)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                for (album in row) {
                                    val albumTracks = artistTracks.filter { it.album == album }
                                    val albumColorIdx = albums.indexOf(album)
                                    val colors = gradientColors[albumColorIdx % gradientColors.size]
                                    Column(
                                        modifier = Modifier.weight(1f)
                                            .clickable { onAlbumClick(album) },
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
                                            AlbumArt(
                                                artworkUri = albumTracks.firstOrNull()?.artworkUri,
                                                modifier = Modifier.fillMaxSize(),
                                                cornerRadius = 8.dp,
                                                fallbackColor = colors.second,
                                            )
                                        }
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            album,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                        Text(
                                            "${albumTracks.size} tracks",
                                            fontFamily = MonoFontFamily,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
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
            }

            // Similar Artists
            if (otherArtists.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    SectionHeader("비슷한 아티스트")
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        otherArtists.forEachIndexed { idx, artist ->
                            val colors = gradientColors[(idx + 1) % gradientColors.size]
                            val artistInitials = artist.split(" ")
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                                .joinToString("")
                                .ifEmpty { artist.take(2).uppercase() }
                            Column(
                                modifier = Modifier.width(80.dp)
                                    .clickable { onArtistClick(artist) },
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Box(
                                    modifier = Modifier.size(64.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                        .background(Brush.linearGradient(listOf(colors.first, colors.second))),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        artistInitials,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    artist,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
