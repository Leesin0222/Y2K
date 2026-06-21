package com.yongjincomapny.y2k.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.yongjincomapny.y2k.designsystem.component.SectionHeader
import com.yongjincomapny.y2k.designsystem.component.Y2KChip
import com.yongjincomapny.y2k.designsystem.theme.MonoFontFamily

private val trendingColors = listOf(
    Color(0xFFE8D5B7) to Color(0xFFC9A87C),
    Color(0xFFD4B8E0) to Color(0xFFA078B8),
    Color(0xFFB8D4E0) to Color(0xFF7098B8),
    Color(0xFFF5D3D3) to Color(0xFFD47474),
    Color(0xFFC8E0C8) to Color(0xFF78B878),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    tracks: List<Y2KTrack>,
    onTrackClick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableIntStateOf(0) }
    val genres = listOf("All", "Pop", "R&B", "Hip-Hop", "Electronic", "Rock", "Dance", "K-Pop")

    val displayTracks = if (query.isBlank()) {
        tracks.take(5)
    } else {
        tracks.filter {
            it.title.contains(query, ignoreCase = true) || it.artist.contains(query, ignoreCase = true)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("✦", color = MaterialTheme.colorScheme.primary, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text("Search", style = MaterialTheme.typography.displayMedium)
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("트랙, 아티스트, 앨범 검색...") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader("장르")
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                genres.forEachIndexed { index, genre ->
                    Y2KChip(
                        text = genre,
                        selected = selectedGenre == index,
                        onClick = { selectedGenre = index },
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                SectionHeader(if (query.isBlank()) "트렌딩" else "검색 결과")
                Spacer(Modifier.height(12.dp))
            }

            itemsIndexed(displayTracks, key = { _, track -> track.id }) { index, track ->
                val originalIndex = tracks.indexOf(track)
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onTrackClick(originalIndex) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = "%02d".format(index + 1),
                            fontFamily = MonoFontFamily, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(28.dp),
                        )
                        Box(
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp))
                                .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp))
                                .background(Brush.linearGradient(listOf(
                                    trendingColors[index % trendingColors.size].first,
                                    trendingColors[index % trendingColors.size].second,
                                ))),
                            contentAlignment = Alignment.Center,
                        ) { Text("♫", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (track.isHiRes) {
                            Text("HI-RES", fontFamily = MonoFontFamily, fontSize = 9.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(formatDuration(track.duration), fontFamily = MonoFontFamily, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 1.dp)
                }
            }

            if (query.isBlank()) {
                item {
                    Spacer(Modifier.height(24.dp))
                    SectionHeader("카테고리")
                    Spacer(Modifier.height(12.dp))
                }

                val categories = listOf(
                    "Y2K Hits" to "248 tracks", "Club Anthems" to "186 tracks",
                    "Slow Jams" to "132 tracks", "New Releases" to "64 tracks",
                )

                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        for (row in categories.chunked(2)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                for ((name, count) in row) {
                                    Box(
                                        modifier = Modifier.weight(1f).height(88.dp).clip(RoundedCornerShape(8.dp))
                                            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { }.padding(12.dp),
                                        contentAlignment = Alignment.BottomStart,
                                    ) {
                                        Text("✦", modifier = Modifier.align(Alignment.TopEnd), fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f))
                                        Column {
                                            Text(name, style = MaterialTheme.typography.headlineSmall)
                                            Text(count, fontFamily = MonoFontFamily, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(12.dp))
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
