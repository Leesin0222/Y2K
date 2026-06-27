package com.yongjincomapny.y2k.core.database.di

import android.content.Context
import androidx.room.Room
import com.yongjincomapny.y2k.core.database.FavoriteDao
import com.yongjincomapny.y2k.core.database.PlayHistoryDao
import com.yongjincomapny.y2k.core.database.PlaylistDao
import com.yongjincomapny.y2k.core.database.TrackTagDao
import com.yongjincomapny.y2k.core.database.Y2KDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Y2KDatabase {
        return Room.databaseBuilder(
            context,
            Y2KDatabase::class.java,
            "y2k_database",
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideTrackTagDao(database: Y2KDatabase): TrackTagDao {
        return database.trackTagDao()
    }

    @Provides
    @Singleton
    fun providePlaylistDao(database: Y2KDatabase): PlaylistDao {
        return database.playlistDao()
    }

    @Provides
    @Singleton
    fun provideFavoriteDao(database: Y2KDatabase): FavoriteDao {
        return database.favoriteDao()
    }

    @Provides
    @Singleton
    fun providePlayHistoryDao(database: Y2KDatabase): PlayHistoryDao {
        return database.playHistoryDao()
    }
}
