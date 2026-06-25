package com.yongjincomapny.y2k.feature.nowplaying

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.player.PlaybackState
import com.yongjincomapny.y2k.core.player.RepeatMode
import com.yongjincomapny.y2k.designsystem.component.AlbumArt
import com.yongjincomapny.y2k.designsystem.component.Y2KButtonStyle
import com.yongjincomapny.y2k.designsystem.component.Y2KIconButton as Y2KIconBtn
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreen(
    playbackState: PlaybackState,
    onBack: () -> Unit,
    onQueueClick: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSeek: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onCycleRepeatMode: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = playbackState.currentTrack
    val title = track?.title ?: "No Track"
    val artist = track?.artist ?: "-"
    val album = track?.album ?: ""
    val isHiRes = track?.isHiRes ?: false

    var isSeeking by remember { mutableStateOf(false) }
    var seekValue by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier.fillMaxSize().background(Y2KTheme.colors.bg),
    ) {
        TopAppBar(
            title = {
                Text("NOW PLAYING", style = Y2KTheme.textStyles.titleLarge,
                    modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            },
            navigationIcon = {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
            },
            actions = {
                IconButton(onClick = { }) { Icon(Icons.Default.MoreVert, contentDescription = "Menu") }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Y2KTheme.colors.bg),
            windowInsets = WindowInsets(0),
        )

        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center,
        ) {
            // Album art with hard shadow
            Box {
                // Hard shadow
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .offset(x = 4.dp, y = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Y2KTheme.colors.fg),
                )
                Box(
                    modifier = Modifier
                        .border(2.dp, Y2KTheme.colors.fg, RoundedCornerShape(16.dp)),
                ) {
                    AlbumArt(
                        artworkUri = track?.artworkUri,
                        size = 280.dp,
                        cornerRadius = 16.dp,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            MarqueeText(
                text = title,
                style = Y2KTheme.textStyles.displayMedium,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            MarqueeText(
                text = artist,
                style = Y2KTheme.textStyles.bodyLarge,
                color = Y2KTheme.colors.fgMuted,
            )
            if (album.isNotEmpty()) {
                Text(album, fontFamily = MonoFontFamily, fontSize = 12.sp, color = Y2KTheme.colors.accent, maxLines = 1)
            }
            Spacer(Modifier.height(8.dp))
            if (isHiRes) {
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        .border(1.5.dp, Y2KTheme.colors.accent, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                ) {
                    Text("HI-RES", fontFamily = MonoFontFamily, fontSize = 10.sp, fontWeight = FontWeight.Medium,
                        color = Y2KTheme.colors.accent, letterSpacing = 0.04.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            val displayProgress = if (isSeeking) seekValue else playbackState.progress

            Slider(
                value = displayProgress,
                onValueChange = {
                    isSeeking = true
                    seekValue = it
                },
                onValueChangeFinished = {
                    onSeek((seekValue * playbackState.duration).toLong())
                    isSeeking = false
                },
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Y2KTheme.colors.accent,
                    activeTrackColor = Y2KTheme.colors.accent,
                    inactiveTrackColor = Y2KTheme.colors.border,
                ),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatTime(playbackState.position), fontFamily = MonoFontFamily, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
                Text("-${formatTime(playbackState.duration - playbackState.position)}", fontFamily = MonoFontFamily, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
            }

            Spacer(Modifier.height(20.dp))

            // Controls — prev / play / next with hard shadow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onPrevious, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(24.dp))
                // Main play button with hard shadow accent
                Y2KIconBtn(
                    icon = if (playbackState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    onClick = onPlayPause,
                    contentDescription = "Play/Pause",
                    size = 64.dp,
                    iconSize = 32.dp,
                    style = Y2KButtonStyle.Primary,
                )
                Spacer(Modifier.width(24.dp))
                IconButton(onClick = onNext, modifier = Modifier.size(48.dp)) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(28.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action row — like / shuffle / repeat / queue
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { }) {
                    Icon(
                        Icons.Default.FavoriteBorder, contentDescription = "Like",
                        tint = Y2KTheme.colors.fgMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        Icons.Default.Shuffle, contentDescription = "Shuffle",
                        tint = if (playbackState.shuffleEnabled) Y2KTheme.colors.accent else Y2KTheme.colors.fgMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = onCycleRepeatMode) {
                    Icon(
                        when (playbackState.repeatMode) {
                            RepeatMode.ONE -> Icons.Default.RepeatOne
                            else -> Icons.Default.Repeat
                        },
                        contentDescription = "Repeat",
                        tint = if (playbackState.repeatMode != RepeatMode.OFF) Y2KTheme.colors.accent else Y2KTheme.colors.fgMuted,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(Modifier.width(16.dp))
                IconButton(onClick = onQueueClick) { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = "Queue", modifier = Modifier.size(22.dp)) }
            }

        }
    }
}

@Composable
private fun MarqueeText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Unspecified,
    color: Color = Color.Unspecified,
) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    var containerWidth by remember { mutableFloatStateOf(0f) }
    val textWidth = remember(text, style) {
        textMeasurer.measure(text, style, maxLines = 1).size.width.toFloat()
    }
    val isOverflowing = containerWidth > 0f && textWidth > containerWidth

    Text(
        text = text,
        style = style,
        textAlign = if (isOverflowing) TextAlign.Unspecified else textAlign,
        color = color,
        maxLines = 1,
        modifier = modifier
            .onSizeChanged { containerWidth = it.width.toFloat() }
            .then(
                if (isOverflowing) {
                    Modifier
                        .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                        .drawWithContent {
                            drawContent()
                            val fadePx = with(density) { 24.dp.toPx() }
                            drawRect(
                                brush = Brush.horizontalGradient(
                                    0f to Color.Transparent,
                                    fadePx / size.width to Color.Black,
                                    1f - fadePx / size.width to Color.Black,
                                    1f to Color.Transparent,
                                ),
                                blendMode = BlendMode.DstIn,
                            )
                        }
                        .basicMarquee(iterations = Int.MAX_VALUE)
                } else {
                    Modifier
                }
            ),
    )
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
