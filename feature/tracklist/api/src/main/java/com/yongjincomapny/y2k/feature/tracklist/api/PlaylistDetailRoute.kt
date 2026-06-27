package com.yongjincomapny.y2k.feature.tracklist.api

import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDetailRoute(val playlistId: Long, val playlistName: String)
