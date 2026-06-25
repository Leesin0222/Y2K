package com.yongjincomapny.y2k.designsystem.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme

@Composable
fun MiniPlayer(
    title: String,
    artist: String,
    isPlaying: Boolean,
    progress: Float,
    onTap: () -> Unit,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    artworkUri: Uri? = null,
) {
    val colors = Y2KTheme.colors
    val textStyles = Y2KTheme.textStyles

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface),
    ) {
        Column {
            // Progress bar
            Box(modifier = Modifier.fillMaxWidth().height(2.dp).background(colors.border)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(colors.accent),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onTap)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AlbumArt(
                    artworkUri = artworkUri,
                    size = 40.dp,
                )
                Column(modifier = Modifier.weight(1f)) {
                    BasicText(
                        text = title,
                        style = textStyles.bodySmall.copy(fontWeight = FontWeight.Medium),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    BasicText(
                        text = artist,
                        style = textStyles.labelSmall.copy(color = colors.fgMuted),
                    )
                }
                Y2KIconButton(
                    icon = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    onClick = onPlayPause,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    size = 36.dp,
                    iconSize = 18.dp,
                    style = Y2KButtonStyle.Primary,
                )
                Y2KIconButton(
                    icon = Icons.Default.SkipNext,
                    onClick = onNext,
                    contentDescription = "Next",
                    size = 36.dp,
                    iconSize = 22.dp,
                    style = Y2KButtonStyle.Secondary,
                )
            }
        }
    }
}
