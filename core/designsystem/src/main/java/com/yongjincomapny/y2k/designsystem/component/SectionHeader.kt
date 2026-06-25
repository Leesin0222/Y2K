package com.yongjincomapny.y2k.designsystem.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    val colors = Y2KTheme.colors
    val starStyle = TextStyle(
        fontSize = 10.sp,
        color = colors.fgMuted.copy(alpha = 0.25f),
    )
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicText(text = "✦", style = starStyle)
        Spacer(Modifier.width(6.dp))
        BasicText(
            text = title.uppercase(),
            style = Y2KTheme.textStyles.labelMedium.copy(fontWeight = FontWeight.Bold),
        )
        Spacer(Modifier.width(6.dp))
        BasicText(text = "✦", style = starStyle)
    }
}
