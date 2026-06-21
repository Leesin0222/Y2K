package com.yongjincomapny.y2k.core.player.di

import android.content.Context
import com.yongjincomapny.y2k.core.player.EqualizerManager
import com.yongjincomapny.y2k.core.player.EqualizerManagerImpl
import com.yongjincomapny.y2k.core.player.MusicScanner
import com.yongjincomapny.y2k.core.player.MusicScannerImpl
import com.yongjincomapny.y2k.core.player.Y2KPlayer
import com.yongjincomapny.y2k.core.player.Y2KPlayerImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlayerModule {

    @Binds
    @Singleton
    abstract fun bindMusicScanner(impl: MusicScannerImpl): MusicScanner

    @Binds
    @Singleton
    abstract fun bindEqualizerManager(impl: EqualizerManagerImpl): EqualizerManager

    companion object {
        @Provides
        @Singleton
        fun provideY2KPlayer(@ApplicationContext context: Context): Y2KPlayer {
            return Y2KPlayerImpl(context)
        }
    }
}
