package com.yongjincomapny.y2k

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yongjincomapny.y2k.core.player.MusicScanner
import com.yongjincomapny.y2k.core.player.Y2KTrack
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicScanner: MusicScanner,
) : ViewModel() {

    private val _tracks = MutableStateFlow<List<Y2KTrack>>(emptyList())
    val tracks: StateFlow<List<Y2KTrack>> = _tracks.asStateFlow()

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
        }
    }
}
