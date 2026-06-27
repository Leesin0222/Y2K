package com.yongjincomapny.y2k.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_tags")
data class TrackTagEntity(
    @PrimaryKey val trackId: String,
    val genres: String,
    val moods: String,
    val analyzedAt: Long,
    val modelVersion: String,
)
