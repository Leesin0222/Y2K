package com.yongjincomapny.y2k.core.ai

import kotlinx.coroutines.flow.StateFlow

interface LlmEngine {
    val isAvailable: StateFlow<Boolean>
    val downloadState: StateFlow<ModelDownloadState>

    suspend fun downloadModel(onProgress: suspend (Float) -> Unit = {}): Result<Unit>
    suspend fun deleteModel(): Result<Unit>
    suspend fun query(input: String, trackTags: List<AudioTags>): LlmResponse
}

data class LlmResponse(
    val targetGenres: List<String>,
    val targetMoods: List<String>,
    val explanation: String,
)

sealed interface ModelDownloadState {
    data object NotDownloaded : ModelDownloadState
    data class Downloading(val progress: Float) : ModelDownloadState
    data object Ready : ModelDownloadState
    data class Error(val message: String) : ModelDownloadState
}
