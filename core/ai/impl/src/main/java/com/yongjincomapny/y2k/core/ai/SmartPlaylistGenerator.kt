package com.yongjincomapny.y2k.core.ai

import com.yongjincomapny.y2k.core.player.Y2KTrack
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartPlaylistGenerator @Inject constructor() {

    fun generateAutoPlaylists(tracks: List<Y2KTrack>): List<SmartPlaylist> {
        val analyzed = tracks.filter { it.aiAnalyzed }
        if (analyzed.isEmpty()) return emptyList()

        val genrePlaylists = analyzed
            .flatMap { track -> track.genres.map { genre -> genre to track } }
            .groupBy({ it.first }, { it.second })
            .filter { it.value.size >= 3 }
            .map { (genre, genreTracks) ->
                SmartPlaylist(
                    name = genre,
                    tracks = genreTracks.distinct(),
                    type = PlaylistType.GENRE,
                )
            }

        val moodPlaylists = analyzed
            .flatMap { track -> track.moods.map { mood -> mood to track } }
            .groupBy({ it.first }, { it.second })
            .filter { it.value.size >= 3 }
            .map { (mood, moodTracks) ->
                SmartPlaylist(
                    name = mood,
                    tracks = moodTracks.distinct(),
                    type = PlaylistType.MOOD,
                )
            }

        return (genrePlaylists + moodPlaylists).sortedByDescending { it.tracks.size }
    }

    fun filterByGenre(genre: String, tracks: List<Y2KTrack>): List<Y2KTrack> {
        return tracks.filter { genre in it.genres }
    }

    fun filterByMood(mood: String, tracks: List<Y2KTrack>): List<Y2KTrack> {
        return tracks.filter { mood in it.moods }
    }
}
