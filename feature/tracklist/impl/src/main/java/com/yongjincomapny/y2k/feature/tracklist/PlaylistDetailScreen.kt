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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme

private val playlistGradients = listOf(
    Color(0xFFD4B8E0) to Color(0xFFA078B8),
    Color(0xFFB8D4E0) to Color(0xFF7098B8),
    Color(0xFFC8E0C8) to Color(0xFF78B878),
    Color(0xFFE0D4B8) to Color(0xFFB8A070),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistName: String,
    tracks: List<Y2KTrack>,
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onTrackClick: (index: Int) -> Unit,
    onPlayAll: () -> Unit,
    onShuffle: () -> Unit,
    onRemoveTrack: (trackId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentTrackId = playbackState.currentTrack?.id

    Column(modifier = modifier.fillMaxSize().background(Y2KTheme.colors.bg)) {
        TopAppBar(
            title = { },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Y2KTheme.colors.bg),
            windowInsets = WindowInsets(0),
        )

        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp))
                    .border(2.dp, Y2KTheme.colors.fg, RoundedCornerShape(8.dp))
                    .background(Brush.linearGradient(listOf(playlistGradients[0].first, playlistGradients[0].second))),
                contentAlignment = Alignment.Center,
            ) { Text("♫", fontSize = 36.sp) }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
                Text(playlistName, style = Y2KTheme.textStyles.displaySmall)
                Spacer(Modifier.height(4.dp))
                Text(
                    "${tracks.size} tracks",
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

        if (tracks.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("♫", fontSize = 40.sp, color = Y2KTheme.colors.fgMuted)
                    Spacer(Modifier.height(8.dp))
                    Text("트랙이 없습니다", style = Y2KTheme.textStyles.bodySmall, color = Y2KTheme.colors.fgMuted)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp)) {
                itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
                    val isPlaying = track.id == currentTrackId
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                                .clickable { onTrackClick(index) }
                                .then(if (isPlaying) Modifier.background(Y2KTheme.colors.accentDim) else Modifier)
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(
                                if (isPlaying) "▶" else "%02d".format(index + 1),
                                fontFamily = MonoFontFamily, fontSize = 13.sp,
                                color = if (isPlaying) Y2KTheme.colors.accent else Y2KTheme.colors.fgMuted,
                                fontWeight = if (isPlaying) FontWeight.Medium else FontWeight.Normal,
                                modifier = Modifier.width(24.dp),
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    track.title, style = Y2KTheme.textStyles.titleMedium,
                                    color = if (isPlaying) Y2KTheme.colors.accent else Y2KTheme.colors.fg,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                )
                                Text(track.artist, fontSize = 12.sp, color = Y2KTheme.colors.fgMuted)
                            }
                            IconButton(onClick = { onRemoveTrack(track.id) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp), tint = Y2KTheme.colors.fgMuted)
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
}
