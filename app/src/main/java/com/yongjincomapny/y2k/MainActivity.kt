package com.yongjincomapny.y2k

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.yongjincomapny.y2k.core.navigation.TopLevelRoute
import com.yongjincomapny.y2k.core.player.EqualizerManager
import com.yongjincomapny.y2k.core.player.Y2KPlayer
import com.yongjincomapny.y2k.designsystem.component.BottomNavItem
import com.yongjincomapny.y2k.designsystem.component.MiniPlayer
import com.yongjincomapny.y2k.designsystem.component.Y2KBottomNavBar
import com.yongjincomapny.y2k.designsystem.theme.Y2KTheme
import com.yongjincomapny.y2k.feature.equalizer.EqualizerScreen
import com.yongjincomapny.y2k.feature.equalizer.api.EqualizerRoute
import com.yongjincomapny.y2k.feature.home.HomeScreen
import com.yongjincomapny.y2k.feature.home.api.HomeRoute
import com.yongjincomapny.y2k.feature.library.LibraryScreen
import com.yongjincomapny.y2k.feature.library.api.LibraryRoute
import com.yongjincomapny.y2k.feature.nowplaying.NowPlayingScreen
import com.yongjincomapny.y2k.feature.nowplaying.api.NowPlayingRoute
import com.yongjincomapny.y2k.feature.search.SearchScreen
import com.yongjincomapny.y2k.feature.search.api.SearchRoute
import com.yongjincomapny.y2k.feature.artistdetail.ArtistDetailScreen
import com.yongjincomapny.y2k.feature.artistdetail.api.ArtistDetailRoute
import com.yongjincomapny.y2k.feature.tracklist.TrackListScreen
import com.yongjincomapny.y2k.feature.tracklist.api.TrackListRoute
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var player: Y2KPlayer
    @Inject lateinit var equalizerManager: EqualizerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Y2KTheme {
                Y2KApp(player, equalizerManager)
            }
        }
    }
}

private val topLevelDestinations = listOf(
    BottomNavItem("home", "Home", Icons.Default.Home) to HomeRoute,
    BottomNavItem("search", "Search", Icons.Default.Search) to SearchRoute,
    BottomNavItem("library", "Library", Icons.Default.LibraryMusic) to LibraryRoute,
    BottomNavItem("equalizer", "EQ", Icons.Default.Equalizer) to EqualizerRoute,
)

private fun audioPermission(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_AUDIO
    else
        Manifest.permission.READ_EXTERNAL_STORAGE

@Composable
fun Y2KApp(player: Y2KPlayer, equalizerManager: EqualizerManager) {
    val viewModel: MainViewModel = viewModel()
    val tracks by viewModel.tracks.collectAsState()
    val playbackState by player.playbackState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.onPermissionResult(granted)
    }

    LaunchedEffect(Unit) {
        val permission = audioPermission()
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onPermissionResult(true)
        } else {
            permissionLauncher.launch(permission)
        }
    }

    // Equalizer 초기화: 재생 시작 시 audio session ID 연결
    val audioSessionId = player.audioSessionId
    LaunchedEffect(audioSessionId) {
        if (audioSessionId != 0) {
            equalizerManager.initialize(audioSessionId)
        }
    }
    val eqState by equalizerManager.state.collectAsState()

    LaunchedEffect(playbackState.error) {
        playbackState.error?.let { error ->
            snackbarHostState.showSnackbar(error.message)
            player.clearError()
        }
    }

    val backStack: SnapshotStateList<Any> = remember { listOf<Any>(HomeRoute).toMutableStateList() }

    val currentKey = backStack.lastOrNull()
    val showBottomBar = currentKey is TopLevelRoute
    val hasTrack = playbackState.currentTrack != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it },
                exit = slideOutVertically { it },
            ) {
                Column {
                    if (hasTrack) {
                        MiniPlayer(
                            title = playbackState.currentTrack?.title ?: "",
                            artist = playbackState.currentTrack?.artist ?: "",
                            isPlaying = playbackState.isPlaying,
                            progress = playbackState.progress,
                            onTap = { backStack.add(NowPlayingRoute) },
                            onPlayPause = { player.togglePlayPause() },
                            onNext = { player.next() },
                            artworkUri = playbackState.currentTrack?.artworkUri,
                        )
                    }
                    Y2KBottomNavBar(
                        items = topLevelDestinations.map { it.first },
                        currentRoute = topLevelDestinations
                            .firstOrNull { it.second == currentKey }?.first?.route,
                        onNavigate = { item ->
                            val target = topLevelDestinations
                                .first { it.first.route == item.route }.second
                            val firstTopLevel = backStack.indexOfFirst { it is TopLevelRoute }
                            if (firstTopLevel >= 0) {
                                while (backStack.size > firstTopLevel + 1) {
                                    backStack.removeLastOrNull()
                                }
                                backStack[firstTopLevel] = target
                            } else {
                                backStack.add(target)
                            }
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = entryProvider {
                    entry<HomeRoute> {
                        HomeScreen(
                            tracks = tracks,
                            onTrackClick = { index ->
                                player.setQueue(tracks, index)
                                backStack.add(NowPlayingRoute)
                            },
                            onAlbumClick = { album ->
                                backStack.add(TrackListRoute(album))
                            },
                            onShufflePlay = {
                                player.setQueue(tracks)
                                player.toggleShuffle()
                                backStack.add(NowPlayingRoute)
                            },
                        )
                    }
                    entry<SearchRoute> {
                        SearchScreen(
                            tracks = tracks,
                            onTrackClick = { index ->
                                player.setQueue(tracks, index)
                                backStack.add(NowPlayingRoute)
                            },
                        )
                    }
                    entry<LibraryRoute> {
                        LibraryScreen(
                            tracks = tracks,
                            onAlbumClick = { album ->
                                backStack.add(TrackListRoute(album))
                            },
                            onTrackClick = { index ->
                                player.setQueue(tracks, index)
                                backStack.add(NowPlayingRoute)
                            },
                            onArtistClick = { artist ->
                                backStack.add(ArtistDetailRoute(artist))
                            },
                        )
                    }
                    entry<EqualizerRoute> {
                        EqualizerScreen(
                            eqState = eqState,
                            onEnabledChange = { equalizerManager.setEnabled(it) },
                            onBandLevelChange = { band, level -> equalizerManager.setBandLevel(band, level) },
                            onPresetSelect = { equalizerManager.selectPreset(it) },
                            onBassBoostEnabledChange = { equalizerManager.setBassBoostEnabled(it) },
                            onBassBoostStrengthChange = { equalizerManager.setBassBoostStrength(it) },
                            onLoudnessEnabledChange = { equalizerManager.setLoudnessEnabled(it) },
                            onSurroundEnabledChange = { equalizerManager.setSurroundEnabled(it) },
                            onGaplessEnabledChange = { equalizerManager.setGaplessEnabled(it) },
                        )
                    }
                    entry<NowPlayingRoute> {
                        NowPlayingScreen(
                            playbackState = playbackState,
                            onBack = { backStack.removeLastOrNull() },
                            onQueueClick = {
                                val album = playbackState.currentTrack?.album ?: return@NowPlayingScreen
                                backStack.add(TrackListRoute(album))
                            },
                            onPlayPause = { player.togglePlayPause() },
                            onNext = { player.next() },
                            onPrevious = { player.previous() },
                            onSeek = { player.seekTo(it) },
                            onToggleShuffle = { player.toggleShuffle() },
                            onCycleRepeatMode = { player.cycleRepeatMode() },
                        )
                    }
                    entry<ArtistDetailRoute> { route ->
                        val artistTracks = remember(tracks, route.artist) {
                            tracks.filter { it.artist == route.artist }
                        }
                        ArtistDetailScreen(
                            artistName = route.artist,
                            allTracks = tracks,
                            onBack = { backStack.removeLastOrNull() },
                            onTrackClick = { index ->
                                player.setQueue(tracks, index)
                                backStack.add(NowPlayingRoute)
                            },
                            onAlbumClick = { album ->
                                backStack.add(TrackListRoute(album))
                            },
                            onShufflePlay = {
                                player.setQueue(artistTracks)
                                player.toggleShuffle()
                                backStack.add(NowPlayingRoute)
                            },
                            onPlayAll = {
                                player.setQueue(artistTracks)
                                backStack.add(NowPlayingRoute)
                            },
                            onArtistClick = { artist ->
                                backStack.add(ArtistDetailRoute(artist))
                            },
                        )
                    }
                    entry<TrackListRoute> { route ->
                        val albumTracks = remember(tracks, route.album) {
                            tracks.filter { it.album == route.album }
                        }
                        TrackListScreen(
                            tracks = albumTracks,
                            playbackState = playbackState,
                            onBack = { backStack.removeLastOrNull() },
                            onTrackClick = { index ->
                                player.setQueue(albumTracks, index)
                                backStack.add(NowPlayingRoute)
                            },
                            onPlayAll = {
                                player.setQueue(albumTracks)
                                backStack.add(NowPlayingRoute)
                            },
                            onShuffle = {
                                player.setQueue(albumTracks)
                                player.toggleShuffle()
                                backStack.add(NowPlayingRoute)
                            },
                        )
                    }
                },
            )
        }
    }
}
