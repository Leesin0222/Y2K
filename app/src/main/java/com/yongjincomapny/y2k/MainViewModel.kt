package com.yongjincomapny.y2k

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.yongjincomapny.y2k.core.ai.SmartPlaylist
import com.yongjincomapny.y2k.core.ai.SmartPlaylistGenerator
import com.yongjincomapny.y2k.core.database.FavoriteDao
import com.yongjincomapny.y2k.core.database.FavoriteEntity
import com.yongjincomapny.y2k.core.database.PlayHistoryDao
import com.yongjincomapny.y2k.core.database.PlayHistoryEntity
import com.yongjincomapny.y2k.core.database.PlaylistDao
import com.yongjincomapny.y2k.core.database.PlaylistEntity
import com.yongjincomapny.y2k.core.database.TrackTagDao
import com.yongjincomapny.y2k.core.player.LyricsExtractor
import com.yongjincomapny.y2k.core.player.MusicScanner
import com.yongjincomapny.y2k.core.player.UserPlaylist
import com.yongjincomapny.y2k.core.player.Y2KTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val application: Application,
    private val musicScanner: MusicScanner,
    private val playlistGenerator: SmartPlaylistGenerator,
    private val trackTagDao: TrackTagDao,
    private val playlistDao: PlaylistDao,
    private val favoriteDao: FavoriteDao,
    private val playHistoryDao: PlayHistoryDao,
    private val lyricsExtractor: LyricsExtractor,
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Y2KTrack>>(emptyList())
    val tracks: StateFlow<List<Y2KTrack>> = _tracks.asStateFlow()

    private val _smartPlaylists = MutableStateFlow<List<SmartPlaylist>>(emptyList())
    val smartPlaylists: StateFlow<List<SmartPlaylist>> = _smartPlaylists.asStateFlow()

    private val _analysisProgress = MutableStateFlow<Pair<Int, Int>?>(null)
    val analysisProgress: StateFlow<Pair<Int, Int>?> = _analysisProgress.asStateFlow()

    private val _analysisComplete = MutableStateFlow(false)
    val analysisComplete: StateFlow<Boolean> = _analysisComplete.asStateFlow()

    fun onAnalysisCompleteShown() {
        _analysisComplete.value = false
    }

    private val _userPlaylists = MutableStateFlow<List<UserPlaylist>>(emptyList())
    val userPlaylists: StateFlow<List<UserPlaylist>> = _userPlaylists.asStateFlow()

    init {
        viewModelScope.launch {
            playlistDao.getAll().collect { entities ->
                _userPlaylists.value = entities.map { entity ->
                    val count = playlistDao.getTrackCount(entity.id)
                    UserPlaylist(entity.id, entity.name, count, entity.createdAt)
                }
            }
        }
    }

    fun createPlaylist(name: String, trackIds: List<String> = emptyList()) {
        viewModelScope.launch {
            val id = playlistDao.insertPlaylist(PlaylistEntity(name = name))
            if (trackIds.isNotEmpty()) {
                playlistDao.setTracks(id, trackIds)
            }
        }
    }

    fun renamePlaylist(id: Long, name: String) {
        viewModelScope.launch { playlistDao.rename(id, name) }
    }

    fun deletePlaylist(id: Long) {
        viewModelScope.launch { playlistDao.deletePlaylist(id) }
    }

    suspend fun getPlaylistTrackIds(playlistId: Long): List<String> {
        return playlistDao.getPlaylistTracks(playlistId).map { it.trackId }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: String) {
        viewModelScope.launch { playlistDao.addTrack(playlistId, trackId) }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: String) {
        viewModelScope.launch { playlistDao.removeTrack(playlistId, trackId) }
    }

    // Favorites
    private val _favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favoriteIds: StateFlow<Set<String>> = _favoriteIds.asStateFlow()

    init {
        viewModelScope.launch {
            favoriteDao.getAllIds().collect { ids ->
                _favoriteIds.value = ids.toSet()
            }
        }
    }

    fun toggleFavorite(trackId: String) {
        viewModelScope.launch {
            if (trackId in _favoriteIds.value) {
                favoriteDao.remove(trackId)
            } else {
                favoriteDao.add(FavoriteEntity(trackId))
            }
        }
    }

    // Play History
    private val _recentTrackIds = MutableStateFlow<List<String>>(emptyList())
    val recentTrackIds: StateFlow<List<String>> = _recentTrackIds.asStateFlow()

    init {
        viewModelScope.launch {
            playHistoryDao.getRecentTrackIds().collect { ids ->
                _recentTrackIds.value = ids
            }
        }
    }

    fun recordPlay(trackId: String) {
        viewModelScope.launch {
            playHistoryDao.insert(PlayHistoryEntity(trackId = trackId))
            playHistoryDao.trimOld()
        }
    }

    // Lyrics
    private val _currentLyrics = MutableStateFlow<String?>(null)
    val currentLyrics: StateFlow<String?> = _currentLyrics.asStateFlow()

    private var lastLyricsTrackId: String? = null

    fun loadLyrics(track: Y2KTrack) {
        if (track.id == lastLyricsTrackId) return
        lastLyricsTrackId = track.id
        _currentLyrics.value = null
        viewModelScope.launch {
            _currentLyrics.value = lyricsExtractor.extract(track.uri)
        }
    }

    private val _permissionGranted = MutableStateFlow(false)
    val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

    fun onPermissionResult(granted: Boolean) {
        _permissionGranted.value = granted
        if (granted) {
            scanMusic()
        } else {
            _tracks.value = sampleTracks
        }
    }

    fun loadWithoutPermission() {
        _tracks.value = sampleTracks
    }

    private fun scanMusic() {
        viewModelScope.launch {
            val scanned = musicScanner.scanLocalMusic()
            _tracks.value = scanned.ifEmpty { sampleTracks }

            if (scanned.isNotEmpty()) {
                startAnalysisWorker()
            }
        }
    }

    private fun startAnalysisWorker() {
        val workManager = WorkManager.getInstance(application)
        val request = OneTimeWorkRequestBuilder<AnalysisWorker>().build()

        workManager.enqueueUniqueWork(
            AnalysisWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            request,
        )

        viewModelScope.launch {
            workManager.getWorkInfoByIdFlow(request.id).collect { workInfo ->
                if (workInfo == null) return@collect

                when (workInfo.state) {
                    WorkInfo.State.RUNNING -> {
                        val current = workInfo.progress.getInt(AnalysisWorker.KEY_CURRENT, 0)
                        val total = workInfo.progress.getInt(AnalysisWorker.KEY_TOTAL, 0)
                        if (total > 0) {
                            _analysisProgress.value = current to total
                        }
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        _analysisProgress.value = null
                        refreshTracksWithTags()
                        _analysisComplete.value = true
                    }
                    WorkInfo.State.FAILED, WorkInfo.State.CANCELLED -> {
                        _analysisProgress.value = null
                    }
                    else -> {}
                }
            }
        }
    }

    private suspend fun refreshTracksWithTags() {
        val currentTracks = _tracks.value
        val allTags = trackTagDao.getByTrackIds(currentTracks.map { it.id })
        val tagMap = allTags.associateBy { it.trackId }

        val updatedTracks = currentTracks.map { track ->
            val entity = tagMap[track.id]
            if (entity != null) {
                track.copy(
                    genres = parseTagNames(entity.genres),
                    moods = parseTagNames(entity.moods),
                    aiAnalyzed = true,
                )
            } else {
                track
            }
        }
        _tracks.value = updatedTracks
        _smartPlaylists.value = playlistGenerator.generateAutoPlaylists(updatedTracks)
    }

    private fun parseTagNames(json: String): List<String> {
        return try {
            val array = org.json.JSONArray(json)
            (0 until array.length()).map { array.getJSONObject(it).getString("tag") }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
