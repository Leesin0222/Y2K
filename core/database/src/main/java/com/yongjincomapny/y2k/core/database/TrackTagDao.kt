package com.yongjincomapny.y2k.core.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TrackTagDao {
    @Query("SELECT * FROM track_tags WHERE trackId = :trackId")
    suspend fun getByTrackId(trackId: String): TrackTagEntity?

    @Query("SELECT * FROM track_tags WHERE trackId IN (:trackIds)")
    suspend fun getByTrackIds(trackIds: List<String>): List<TrackTagEntity>

    @Upsert
    suspend fun upsert(entity: TrackTagEntity)

    @Query("SELECT trackId FROM track_tags")
    suspend fun getAnalyzedTrackIds(): List<String>
}
