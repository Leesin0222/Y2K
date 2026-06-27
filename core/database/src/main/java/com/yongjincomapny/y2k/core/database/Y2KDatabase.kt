package com.yongjincomapny.y2k.core.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [TrackTagEntity::class, PlaylistEntity::class, PlaylistTrackEntity::class, FavoriteEntity::class, PlayHistoryEntity::class],
    version = 4,
    exportSchema = false,
)
abstract class Y2KDatabase : RoomDatabase() {
    abstract fun trackTagDao(): TrackTagDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun playHistoryDao(): PlayHistoryDao
}
