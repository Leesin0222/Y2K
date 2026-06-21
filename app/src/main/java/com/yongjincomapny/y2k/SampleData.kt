package com.yongjincomapny.y2k

import android.net.Uri
import com.yongjincomapny.y2k.core.player.Y2KTrack

val sampleTracks = listOf(
    Y2KTrack(
        id = "1", title = "One More Time", artist = "Daft Punk",
        album = "Discovery", duration = 320_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
    ),
    Y2KTrack(
        id = "2", title = "Digital Love", artist = "Daft Punk",
        album = "Discovery", duration = 298_000L, isHiRes = true,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3"),
    ),
    Y2KTrack(
        id = "3", title = "Harder, Better, Faster, Stronger", artist = "Daft Punk",
        album = "Discovery", duration = 225_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3"),
    ),
    Y2KTrack(
        id = "4", title = "Aerodynamic", artist = "Daft Punk",
        album = "Discovery", duration = 212_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3"),
    ),
    Y2KTrack(
        id = "5", title = "Something About Us", artist = "Daft Punk",
        album = "Discovery", duration = 231_000L, isHiRes = true,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3"),
    ),
    Y2KTrack(
        id = "6", title = "Voyager", artist = "Daft Punk",
        album = "Discovery", duration = 227_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3"),
    ),
    Y2KTrack(
        id = "7", title = "Crescendolls", artist = "Daft Punk",
        album = "Discovery", duration = 211_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3"),
    ),
    Y2KTrack(
        id = "8", title = "Nightvision", artist = "Daft Punk",
        album = "Discovery", duration = 104_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3"),
    ),
    Y2KTrack(
        id = "9", title = "Superheroes", artist = "Daft Punk",
        album = "Discovery", duration = 237_000L, isHiRes = false,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3"),
    ),
    Y2KTrack(
        id = "10", title = "High Life", artist = "Daft Punk",
        album = "Discovery", duration = 202_000L, isHiRes = true,
        uri = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3"),
    ),
)
