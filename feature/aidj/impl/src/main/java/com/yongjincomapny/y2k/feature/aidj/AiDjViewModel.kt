package com.yongjincomapny.y2k.feature.aidj

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yongjincomapny.y2k.core.ai.AudioTags
import com.yongjincomapny.y2k.core.ai.LlmEngine
import com.yongjincomapny.y2k.core.ai.LlmResponse
import com.yongjincomapny.y2k.core.ai.ModelDownloadState
import com.yongjincomapny.y2k.core.player.Y2KTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val tracks: List<Y2KTrack> = emptyList(),
)

@HiltViewModel
class AiDjViewModel @Inject constructor(
    private val llmEngine: LlmEngine,
) : ViewModel() {

    val downloadState: StateFlow<ModelDownloadState> = llmEngine.downloadState
    val isAvailable: StateFlow<Boolean> = llmEngine.isAvailable

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allTracks: List<Y2KTrack> = emptyList()

    fun setTracks(tracks: List<Y2KTrack>) {
        allTracks = tracks
    }

    fun downloadModel() {
        viewModelScope.launch {
            llmEngine.downloadModel()
        }
    }

    fun deleteModel() {
        viewModelScope.launch {
            llmEngine.deleteModel()
        }
    }

    fun sendMessage(input: String) {
        if (input.isBlank() || _isLoading.value) return

        val userMessage = ChatMessage(text = input, isUser = true)
        _messages.value = _messages.value + userMessage
        _isLoading.value = true

        viewModelScope.launch {
            val trackTags = allTracks
                .filter { it.aiAnalyzed }
                .map { track ->
                    AudioTags(
                        trackId = track.id,
                        genres = track.genres.map { com.yongjincomapny.y2k.core.ai.TagScore(it, 1f) },
                        moods = track.moods.map { com.yongjincomapny.y2k.core.ai.TagScore(it, 1f) },
                    )
                }

            val response = llmEngine.query(input, trackTags)
            val matchedTracks = findMatchingTracks(response)

            val reply = ChatMessage(
                text = response.explanation.ifEmpty {
                    buildReplyText(response, matchedTracks.size)
                },
                isUser = false,
                tracks = matchedTracks,
            )
            _messages.value = _messages.value + reply
            _isLoading.value = false
        }
    }

    private fun findMatchingTracks(response: LlmResponse): List<Y2KTrack> {
        return allTracks.filter { track ->
            val genreMatch = response.targetGenres.isEmpty() ||
                track.genres.any { it in response.targetGenres }
            val moodMatch = response.targetMoods.isEmpty() ||
                track.moods.any { it in response.targetMoods }
            genreMatch && moodMatch && track.aiAnalyzed
        }.take(20)
    }

    private fun buildReplyText(response: LlmResponse, trackCount: Int): String {
        val tags = (response.targetGenres + response.targetMoods).joinToString(", ")
        return if (trackCount > 0) {
            "${trackCount}곡을 찾았어요 ($tags)"
        } else {
            "조건에 맞는 곡을 찾지 못했어요. 다른 요청을 해보세요."
        }
    }
}
