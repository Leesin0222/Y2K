package com.yongjincomapny.y2k.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Y2K 하드섀도우 스타일 버튼 — 네오브루탈리즘 (4dp offset shadow)
 */
@Composable
fun Y2KButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    style: Y2KButtonStyle = Y2KButtonStyle.Primary,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowOffset = if (isPressed) 0.dp else 4.dp
    val topOffset = if (isPressed) 4.dp else 0.dp

    val isOutlined = style == Y2KButtonStyle.Outlined
    val bgColor = when (style) {
        Y2KButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Outlined -> Color.Transparent
    }
    val fgColor = when (style) {
        Y2KButtonStyle.Primary -> Color.White
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.background
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.background
        Y2KButtonStyle.Outlined -> MaterialTheme.colorScheme.onSurface
    }
    val shadowColor = when (style) {
        Y2KButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Outlined -> Color.Transparent
    }

    Box(modifier = modifier, propagateMinConstraints = true) {
        // Hard shadow (offset rectangle) — skip for Outlined
        if (!isOutlined) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .clip(CircleShape)
                    .background(shadowColor),
            )
        }
        // Button body
        Row(
            modifier = Modifier
                .offset(x = 0.dp, y = if (isOutlined) 0.dp else topOffset)
                .clip(CircleShape)
                .then(
                    if (isOutlined) Modifier.border(1.5.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                    else Modifier
                )
                .background(bgColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick,
                )
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (icon != null) {
                Icon(
                    icon, contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = fgColor,
                )
                Box(Modifier.size(6.dp))
            }
            Text(
                text = text,
                color = fgColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

/**
 * Y2K 하드섀도우 아이콘 버튼
 */
@Composable
fun Y2KIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp,
    style: Y2KButtonStyle = Y2KButtonStyle.Secondary,
    tint: Color? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowDp = if (isPressed) 0.dp else 3.dp
    val topOffset = if (isPressed) 3.dp else 0.dp

    val bgColor = when (style) {
        Y2KButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Outlined -> Color.Transparent
    }
    val fgColor = when (style) {
        Y2KButtonStyle.Primary -> Color.White
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.background
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.background
        Y2KButtonStyle.Outlined -> MaterialTheme.colorScheme.onSurface
    }
    val shadowColor = when (style) {
        Y2KButtonStyle.Primary -> MaterialTheme.colorScheme.primary
        Y2KButtonStyle.Secondary -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Dark -> MaterialTheme.colorScheme.onSurface
        Y2KButtonStyle.Outlined -> Color.Transparent
    }

    Box(modifier = modifier.size(size + 3.dp, size + 3.dp)) {
        Box(
            modifier = Modifier
                .size(size)
                .offset(x = shadowDp, y = shadowDp)
                .clip(CircleShape)
                .background(shadowColor),
        )
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = topOffset)
                .clip(CircleShape)
                .background(bgColor)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                icon, contentDescription = contentDescription,
                modifier = Modifier.size(iconSize),
                tint = tint ?: fgColor,
            )
        }
    }
}

/**
 * Y2K 하드보더 칩 — border: 1.5dp solid, active시 반전
 */
@Composable
fun Y2KChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.onSurface else Color.Transparent
    val textColor = if (selected) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.onSurface
    val borderColor = MaterialTheme.colorScheme.onSurface

    Box(
        modifier = modifier
            .clip(CircleShape)
            .border(1.5.dp, borderColor, CircleShape)
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

enum class Y2KButtonStyle { Primary, Secondary, Dark, Outlined }
