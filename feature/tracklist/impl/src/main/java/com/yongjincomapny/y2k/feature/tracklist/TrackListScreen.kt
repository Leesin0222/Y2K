package com.yongjincomapny.y2k.feature.tracklist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
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
import com.yongjincomapny.y2k.core.player.PlaybackState
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.designsystem.component.Y2KButton
import com.yongjincomapny.y2k.designsystem.component.Y2KButtonStyle
import com.yongjincomapny.y2k.designsystem.component.Y2KIconButton
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackListScreen(
    tracks: List<Y2KTrack>,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onTrackClick: (index: Int) -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTrackId = playbackState.currentTrack?.id
    val totalDuration = tracks.sumOf { it.duration }

    Column(modifier = modifier.fillMaxSize().background(Y2KTheme.colors.bg)) {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Y2KTheme.colors.bg),
            windowInsets = WindowInsets(0),
        )

        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Album art with hard shadow
            Box {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Y2KTheme.colors.fg),
                )
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(2.dp, Y2KTheme.colors.fg, RoundedCornerShape(8.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFFE8D5B7), Color(0xFFC9A87C)))),
                    contentAlignment = Alignment.Center,
                ) { Text("✦", fontSize = 48.sp) }
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                val album = tracks.firstOrNull()?.album ?: "Album"
                val artist = tracks.firstOrNull()?.artist ?: "Artist"
                Text(album, style = Y2KTheme.textStyles.displaySmall)
                Spacer(Modifier.height(4.dp))
                Text(artist, fontSize = 14.sp, color = Y2KTheme.colors.fgMuted)
                Text(
                    "${tracks.size} tracks · ${formatDuration(totalDuration)}",
                    fontFamily = MonoFontFamily, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Y2KButton(
                        text = "Play",
                        onClick = onPlayAll,
                        icon = Icons.Default.PlayArrow,
                        style = Y2KButtonStyle.Primary,
                    )
                    Y2KIconButton(
                        icon = Icons.Default.Shuffle,
                        onClick = onShuffle,
                        contentDescription = "Shuffle",
                        size = 40.dp,
                        iconSize = 18.dp,
                        style = Y2KButtonStyle.Secondary,
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
            itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
                val isPlaying = track.id == currentTrackId
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onTrackClick(index) }
                            .then(if (isPlaying) Modifier.background(Y2KTheme.colors.accentDim) else Modifier)
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            if (isPlaying) "▶" else "%02d".format(index + 1),
                            fontFamily = MonoFontFamily, fontSize = 13.sp,
                            color = if (isPlaying) Y2KTheme.colors.accent else Y2KTheme.colors.fgMuted,
                            fontWeight = if (isPlaying) FontWeight.Medium else FontWeight.Normal,
                            modifier = Modifier.width(24.dp),
                        )
                        Box(
                            modifier = Modifier.size(44.dp).clip(RoundedCornerShape(4.dp))
                                .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(4.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFFE8D5B7), Color(0xFFC9A87C)))),
                            contentAlignment = Alignment.Center,
                        ) { Text("♫", fontSize = 18.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = Y2KTheme.textStyles.titleMedium,
                                color = if (isPlaying) Y2KTheme.colors.accent else Y2KTheme.colors.fg,
                                maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
                        }
                        if (track.isHiRes) {
                            Box(
                                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                                    .border(1.dp, Y2KTheme.colors.accent, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 5.dp, vertical = 2.dp),
                            ) {
                                Text("HI-RES", fontFamily = MonoFontFamily, fontSize = 9.sp, fontWeight = FontWeight.Medium,
                                    color = Y2KTheme.colors.accent, letterSpacing = 0.04.sp)
                            }
                        }
                        Text(formatTrackDuration(track.duration), fontFamily = MonoFontFamily, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
                        IconButton(onClick = { }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(18.dp), tint = Y2KTheme.colors.fgMuted)
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = Y2KTheme.colors.border,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}

private fun formatTrackDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%dh %02dm %02ds".format(hours, minutes, seconds)
    else "%dm %02ds".format(minutes, seconds)
}
