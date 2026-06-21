package com.yongjincomapny.y2k.designsystem.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

private val defaultGradient = Color(0xFFE8D5B7) to Color(0xFFC9A87C)

@Composable
fun AlbumArt(
    artworkUri: Uri?,
    modifier: Modifier = Modifier,
    size: Dp = 0.dp,
    cornerRadius: Dp = 4.dp,
    fallbackGradient: Pair<Color, Color>? = null,
    fallbackColor: Color? = null,
) {
    val sizeModifier = if (size > 0.dp) Modifier.size(size) else Modifier
    val shape = RoundedCornerShape(cornerRadius)

    if (artworkUri != null) {
        AsyncImage(
            model = artworkUri,
            contentDescription = "Album art",
            modifier = modifier
                .then(sizeModifier)
                .clip(shape)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, shape),
            contentScale = ContentScale.Crop,
        )
    } else {
        val gradient = fallbackGradient ?: (fallbackColor?.let { it.copy(alpha = 0.4f) to it }) ?: defaultGradient
        Box(
            modifier = modifier
                .then(sizeModifier)
                .clip(shape)
                .border(1.5.dp, MaterialTheme.colorScheme.outline, shape)
                .background(Brush.linearGradient(listOf(gradient.first, gradient.second))),
            contentAlignment = Alignment.Center,
        ) {
            val fontSize = if (size > 0.dp) (size.value * 0.4f).sp else 24.sp
            Text("♫", fontSize = fontSize)
        }
    }
}
