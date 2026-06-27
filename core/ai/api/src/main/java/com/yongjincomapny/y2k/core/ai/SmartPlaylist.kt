package com.yongjincomapny.y2k.core.ai

import com.yongjincomapny.y2k.core.player.Y2KTrack

data class SmartPlaylist(
    val name: String,
    val tracks: List<Y2KTrack>,
    val type: PlaylistType,
)

enum class PlaylistType { GENRE, MOOD }
