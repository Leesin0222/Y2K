package com.yongjincomapny.y2k.core.player

interface MusicScanner {
    suspend fun scanLocalMusic(): List<Y2KTrack>
}
