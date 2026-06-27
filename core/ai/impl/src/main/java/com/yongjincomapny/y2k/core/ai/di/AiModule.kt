package com.yongjincomapny.y2k.core.ai.di

import com.yongjincomapny.y2k.core.ai.AudioAnalyzer
import com.yongjincomapny.y2k.core.ai.GemmaEngine
import com.yongjincomapny.y2k.core.ai.LlmEngine
import com.yongjincomapny.y2k.core.ai.YamNetAnalyzer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {

    @Binds
    @Singleton
    abstract fun bindAudioAnalyzer(impl: YamNetAnalyzer): AudioAnalyzer

    @Binds
    @Singleton
    abstract fun bindLlmEngine(impl: GemmaEngine): LlmEngine
}
