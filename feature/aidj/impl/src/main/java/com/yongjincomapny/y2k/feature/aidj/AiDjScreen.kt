package com.yongjincomapny.y2k.feature.aidj

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yongjincomapny.y2k.core.ai.ModelDownloadState
import com.yongjincomapny.y2k.core.player.Y2KTrack
import com.yongjincomapny.y2k.designsystem.component.Y2KButton
import com.yongjincomapny.y2k.designsystem.component.Y2KButtonStyle
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme

@Composable
fun AiDjScreen(
    viewModel: AiDjViewModel,
    tracks: List<Y2KTrack>,
    onBack: () -> Unit,
    onPlayTracks: (List<Y2KTrack>) -> Unit,
    onTrackClick: (Y2KTrack) -> Unit,
    onSaveAsPlaylist: (name: String, trackIds: List<String>) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val downloadState by viewModel.downloadState.collectAsState()
    val isAvailable by viewModel.isAvailable.collectAsState()

    LaunchedEffect(tracks) {
        viewModel.setTracks(tracks)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 4.dp, end = 16.dp, top = 12.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("✦", color = Y2KTheme.colors.accent, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text("AI DJ", style = Y2KTheme.textStyles.displayMedium)
            Spacer(Modifier.weight(1f))
            ModelStatusBadge(downloadState)
        }

        // Model download banner
        if (downloadState !is ModelDownloadState.Ready) {
            ModelDownloadBanner(
                state = downloadState,
                isAvailable = isAvailable,
                onDownload = { viewModel.downloadModel() },
                onDelete = { viewModel.deleteModel() },
            )
        }

        // Chat messages
        val listState = rememberLazyListState()
        LaunchedEffect(messages.size) {
            if (messages.isNotEmpty()) {
                listState.animateScrollToItem(messages.size - 1)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (messages.isEmpty()) {
                item { WelcomeMessage(onSuggestionClick = { viewModel.sendMessage(it) }) }
            }
            items(messages) { message ->
                ChatBubble(
                    message = message,
                    onPlayAll = { onPlayTracks(message.tracks) },
                    onTrackClick = onTrackClick,
                    onSaveAsPlaylist = { onSaveAsPlaylist(it, message.tracks.map { t -> t.id }) },
                )
            }
            if (isLoading) {
                item { TypingIndicator() }
            }
        }

        // Input bar
        ChatInputBar(
            enabled = !isLoading,
            onSend = { viewModel.sendMessage(it) },
        )
    }
}

@Composable
private fun ModelStatusBadge(state: ModelDownloadState) {
    val (text, color) = when (state) {
        is ModelDownloadState.Ready -> "ON" to Y2KTheme.colors.accent
        is ModelDownloadState.Downloading -> "..." to Y2KTheme.colors.fgMuted
        else -> "OFF" to Y2KTheme.colors.fgMuted
    }
    Text(
        text = text,
        fontFamily = MonoFontFamily,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Composable
private fun ModelDownloadBanner(
    state: ModelDownloadState,
    isAvailable: Boolean,
    onDownload: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Y2KTheme.colors.surface)
            .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        when (state) {
            is ModelDownloadState.NotDownloaded -> {
                Text("AI DJ 모델", style = Y2KTheme.textStyles.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    if (isAvailable) "Gemma 3 모델을 다운로드하면 자연어로 음악을 요청할 수 있어요 (~1.3GB)"
                    else "이 기기에서는 AI DJ를 사용할 수 없어요 (6GB+ RAM 필요). 키워드 검색은 사용 가능합니다.",
                    style = Y2KTheme.textStyles.bodySmall,
                    color = Y2KTheme.colors.fgMuted,
                )
                if (isAvailable) {
                    Spacer(Modifier.height(12.dp))
                    Y2KButton(
                        text = "모델 다운로드",
                        onClick = onDownload,
                        icon = Icons.Default.Download,
                        style = Y2KButtonStyle.Primary,
                    )
                }
            }
            is ModelDownloadState.Downloading -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(
                        progress = { state.progress },
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = Y2KTheme.colors.accent,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("다운로드 중...", style = Y2KTheme.textStyles.titleMedium)
                        Text(
                            "${(state.progress * 100).toInt()}%",
                            fontFamily = MonoFontFamily,
                            fontSize = 11.sp,
                            color = Y2KTheme.colors.fgMuted,
                        )
                    }
                }
            }
            is ModelDownloadState.Error -> {
                Text("다운로드 실패", style = Y2KTheme.textStyles.titleMedium)
                Text(state.message, style = Y2KTheme.textStyles.bodySmall, color = Y2KTheme.colors.fgMuted)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Y2KButton(text = "재시도", onClick = onDownload, style = Y2KButtonStyle.Primary)
                    Y2KButton(text = "삭제", onClick = onDelete, icon = Icons.Default.Delete, style = Y2KButtonStyle.Secondary)
                }
            }
            is ModelDownloadState.Ready -> {}
        }
    }
}

@Composable
private fun WelcomeMessage(onSuggestionClick: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("✦", fontSize = 40.sp, color = Y2KTheme.colors.accent)
        Spacer(Modifier.height(16.dp))
        Text("AI DJ에게 물어보세요", style = Y2KTheme.textStyles.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "자연어로 원하는 음악을 요청하면\nAI가 맞춤 플레이리스트를 만들어드려요",
            style = Y2KTheme.textStyles.bodySmall,
            color = Y2KTheme.colors.fgMuted,
            lineHeight = 20.sp,
        )
        Spacer(Modifier.height(24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SuggestionChip("비 오는 날 감성적인 곡 틀어줘", onSuggestionClick)
            SuggestionChip("운동할 때 듣기 좋은 신나는 음악", onSuggestionClick)
            SuggestionChip("잠들기 전에 들을 잔잔한 노래", onSuggestionClick)
        }
    }
}

@Composable
private fun SuggestionChip(text: String, onClick: (String) -> Unit) {
    Text(
        text = "\"$text\"",
        style = Y2KTheme.textStyles.bodySmall,
        color = Y2KTheme.colors.fgMuted,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
            .clickable { onClick(text) }
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@Composable
private fun ChatBubble(
    message: ChatMessage,
    onPlayAll: () -> Unit,
    onTrackClick: (Y2KTrack) -> Unit,
    onSaveAsPlaylist: (name: String) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
    ) {
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = if (message.isUser) 12.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 12.dp,
                    )
                )
                .background(
                    if (message.isUser) Y2KTheme.colors.fg
                    else Y2KTheme.colors.surface,
                )
                .border(
                    1.dp,
                    if (message.isUser) Y2KTheme.colors.fg else Y2KTheme.colors.border,
                    RoundedCornerShape(
                        topStart = 12.dp, topEnd = 12.dp,
                        bottomStart = if (message.isUser) 12.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 12.dp,
                    ),
                )
                .padding(12.dp),
        ) {
            Text(
                text = message.text,
                style = Y2KTheme.textStyles.bodySmall,
                color = if (message.isUser) Y2KTheme.colors.bg else Y2KTheme.colors.fg,
            )
        }

        if (message.tracks.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Y2KTheme.colors.surface)
                    .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "${message.tracks.size}곡",
                        fontFamily = MonoFontFamily,
                        fontSize = 11.sp,
                        color = Y2KTheme.colors.fgMuted,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Y2KButton(
                            text = "Save",
                            onClick = { onSaveAsPlaylist("AI DJ Mix") },
                            icon = Icons.Default.Save,
                            style = Y2KButtonStyle.Secondary,
                        )
                        Y2KButton(
                            text = "Play All",
                            onClick = onPlayAll,
                            icon = Icons.Default.PlayArrow,
                            style = Y2KButtonStyle.Primary,
                        )
                    }
                }
                message.tracks.take(5).forEach { track ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onTrackClick(track) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                track.title,
                                style = Y2KTheme.textStyles.bodySmall,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                track.artist,
                                style = Y2KTheme.textStyles.labelSmall,
                                color = Y2KTheme.colors.fgMuted,
                            )
                        }
                    }
                }
                if (message.tracks.size > 5) {
                    Text(
                        "+${message.tracks.size - 5}곡 더",
                        fontFamily = MonoFontFamily,
                        fontSize = 10.sp,
                        color = Y2KTheme.colors.fgMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 4.dp))
            .background(Y2KTheme.colors.surface)
            .border(1.dp, Y2KTheme.colors.border, RoundedCornerShape(12.dp, 12.dp, 12.dp, 4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Y2KTheme.colors.fgMuted),
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    enabled: Boolean,
    onSend: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Y2KTheme.colors.bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.weight(1f),
            placeholder = { Text("무슨 음악 틀어줄까요?", style = Y2KTheme.textStyles.bodySmall) },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Y2KTheme.colors.border,
                focusedBorderColor = Y2KTheme.colors.accent,
            ),
            singleLine = true,
            enabled = enabled,
        )
        IconButton(
            onClick = {
                if (text.isNotBlank()) {
                    onSend(text)
                    text = ""
                }
            },
            enabled = enabled && text.isNotBlank(),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = if (text.isNotBlank()) Y2KTheme.colors.accent else Y2KTheme.colors.fgMuted,
            )
        }
    }
}
