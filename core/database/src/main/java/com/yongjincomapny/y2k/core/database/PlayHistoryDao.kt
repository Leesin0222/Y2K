package com.yongjincomapny.y2k.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayHistoryDao {

    @Query(
        """
        SELECT trackId FROM play_history
        GROUP BY trackId
        ORDER BY MAX(playedAt) DESC
        LIMIT :limit
        """
    )
    fun getRecentTrackIds(limit: Int = 30): Flow<List<String>>

    @Insert
    suspend fun insert(entity: PlayHistoryEntity)

    @Query("DELETE FROM play_history WHERE id NOT IN (SELECT id FROM play_history ORDER BY playedAt DESC LIMIT 200)")
    suspend fun trimOld()
}
