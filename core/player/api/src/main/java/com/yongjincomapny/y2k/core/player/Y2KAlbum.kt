package com.yongjincomapny.y2k.core.player

import android.net.Uri

data class Y2KAlbum(
    val name: String,
    val artist: String,
    val trackCount: Int,
    val artworkUri: Uri? = null,
)

fun List<Y2KTrack>.toAlbums(): List<Y2KAlbum> =
    filter { it.album.isNotBlank() }
        .groupBy { it.album }
        .map { (album, tracks) ->
            Y2KAlbum(
                name = album,
                artist = tracks.first().artist,
                trackCount = tracks.size,
                artworkUri = tracks.firstNotNullOfOrNull { it.artworkUri },
            )
        }
