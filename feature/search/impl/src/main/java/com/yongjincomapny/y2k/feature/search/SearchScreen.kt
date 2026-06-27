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
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
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
    var selectedFilter by remember { mutableIntStateOf(0) }
    val filters = listOf("전체", "제목", "아티스트", "앨범")
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var selectedMood by remember { mutableStateOf<String?>(null) }

    val availableGenres = remember(tracks) {
        tracks.flatMap { it.genres }.distinct().sorted()
    }
    val availableMoods = remember(tracks) {
        tracks.flatMap { it.moods }.distinct().sorted()
    }

    val displayTracks = remember(query, selectedFilter, selectedGenre, selectedMood, tracks) {
        var result = tracks

        // 장르/무드 필터
        if (selectedGenre != null) {
            result = result.filter { selectedGenre in it.genres }
        }
        if (selectedMood != null) {
            result = result.filter { selectedMood in it.moods }
        }

        // 텍스트 검색
        if (query.isBlank()) {
            result.take(10)
        } else {
            val matchedTags = resolveNaturalLanguageTags(query)
            result.filter { track ->
                val textMatch = when (selectedFilter) {
                    1 -> track.title.contains(query, ignoreCase = true)
                    2 -> track.artist.contains(query, ignoreCase = true)
                    3 -> track.album.contains(query, ignoreCase = true)
                    else -> track.title.contains(query, ignoreCase = true)
                        || track.artist.contains(query, ignoreCase = true)
                        || track.album.contains(query, ignoreCase = true)
                }
                val tagMatch = if (matchedTags.first.isNotEmpty() || matchedTags.second.isNotEmpty()) {
                    (matchedTags.first.isEmpty() || track.genres.any { it in matchedTags.first })
                        && (matchedTags.second.isEmpty() || track.moods.any { it in matchedTags.second })
                } else false
                textMatch || tagMatch
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("✦", color = Y2KTheme.colors.accent, fontSize = 20.sp)
            Spacer(Modifier.width(8.dp))
            Text("Search", style = Y2KTheme.textStyles.displayMedium)
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            placeholder = { Text("트랙, 아티스트, 앨범 또는 \"신나는 음악\"") },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Y2KTheme.colors.border,
                focusedBorderColor = Y2KTheme.colors.accent,
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionHeader("검색 필터")
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                filters.forEachIndexed { index, filter ->
                    Y2KChip(
                        text = filter,
                        selected = selectedFilter == index,
                        onClick = { selectedFilter = index },
                    )
                }
            }
        }

        if (availableGenres.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("장르")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    availableGenres.forEach { genre ->
                        Y2KChip(
                            text = genre,
                            selected = selectedGenre == genre,
                            onClick = { selectedGenre = if (selectedGenre == genre) null else genre },
                        )
                    }
                }
            }
        }

        if (availableMoods.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SectionHeader("무드")
                Spacer(Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    availableMoods.forEach { mood ->
                        Y2KChip(
                            text = mood,
                            selected = selectedMood == mood,
                            onClick = { selectedMood = if (selectedMood == mood) null else mood },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
        ) {
            item {
                val sectionTitle = when {
                    query.isNotBlank() -> "검색 결과"
                    selectedGenre != null || selectedMood != null -> "필터 결과"
                    else -> "트렌딩"
                }
                SectionHeader(sectionTitle)
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
                            color = Y2KTheme.colors.fgMuted, modifier = Modifier.width(28.dp),
                        )
                        Box(
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp))
                                .border(1.5.dp, Y2KTheme.colors.border, RoundedCornerShape(4.dp))
                                .background(Brush.linearGradient(listOf(
                                    trendingColors[index % trendingColors.size].first,
                                    trendingColors[index % trendingColors.size].second,
                                ))),
                            contentAlignment = Alignment.Center,
                        ) { Text("♫", fontSize = 20.sp) }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, style = Y2KTheme.textStyles.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(track.artist, style = Y2KTheme.textStyles.labelSmall, color = Y2KTheme.colors.fgMuted)
                        }
                        if (track.isHiRes) {
                            Text("HI-RES", fontFamily = MonoFontFamily, fontSize = 9.sp, color = Y2KTheme.colors.accent)
                        }
                        Text(formatDuration(track.duration), fontFamily = MonoFontFamily, fontSize = 11.sp, color = Y2KTheme.colors.fgMuted)
                    }
                    HorizontalDivider(color = Y2KTheme.colors.border, thickness = 1.dp)
                }
            }

        }
    }
}

private fun resolveNaturalLanguageTags(query: String): Pair<List<String>, List<String>> {
    val lower = query.lowercase()
    val genres = mutableListOf<String>()
    val moods = mutableListOf<String>()

    val genreKeywords = mapOf(
        "팝" to "Pop", "록" to "Rock", "락" to "Rock",
        "힙합" to "Hip-Hop", "랩" to "Hip-Hop",
        "재즈" to "Jazz", "클래식" to "Classical",
        "전자" to "Electronic", "일렉" to "Electronic",
        "알앤비" to "R&B", "r&b" to "R&B",
        "발라드" to "Vocal", "컨트리" to "Country",
        "메탈" to "Metal", "블루스" to "Blues",
        "소울" to "Soul", "펑크" to "Punk",
        "레게" to "Reggae", "포크" to "Folk",
    )
    val moodKeywords = mapOf(
        "슬픈" to "Sad", "슬프" to "Sad", "우울" to "Sad", "감성" to "Sad",
        "비 오는" to "Sad", "비오는" to "Sad",
        "신나" to "Energetic", "힘찬" to "Energetic", "운동" to "Energetic", "파티" to "Energetic",
        "행복" to "Happy", "기분 좋" to "Happy",
        "잔잔" to "Tender", "잠" to "Tender", "편안" to "Tender", "휴식" to "Tender",
        "화난" to "Aggressive", "격한" to "Aggressive",
        "무서" to "Dark", "어두" to "Dark",
    )

    genreKeywords.forEach { (keyword, genre) ->
        if (lower.contains(keyword)) genres.add(genre)
    }
    moodKeywords.forEach { (keyword, mood) ->
        if (lower.contains(keyword)) moods.add(mood)
    }
    return genres.distinct() to moods.distinct()
}

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    return "%d:%02d".format(totalSeconds / 60, totalSeconds % 60)
}
