package com.yongjincomapny.y2k.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.designsystem.theme.BodyFontFamily
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
)

@Composable
fun Y2KBottomNavBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (BottomNavItem) -> Unit,
) {
    val colors = Y2KTheme.colors
    val borderColor = colors.border

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx(),
                )
            }
            .background(colors.surface)
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route
            val tint = if (selected) colors.accent else colors.fgMuted

            Box(
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onNavigate(item) },
                    )
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Box(contentAlignment = Alignment.TopCenter) {
                        if (selected) {
                            BasicText(
                                text = "✦",
                                style = TextStyle(fontSize = 6.sp, color = colors.accent),
                                modifier = Modifier.offset(y = (-2).dp),
                            )
                        }
                        Image(
                            painter = rememberVectorPainter(item.icon),
                            contentDescription = item.label,
                            modifier = Modifier.size(22.dp),
                            colorFilter = ColorFilter.tint(tint),
                        )
                    }
                    BasicText(
                        text = item.label,
                        style = TextStyle(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = BodyFontFamily,
                            color = tint,
                        ),
                    )
                }
            }
        }
    }
}
