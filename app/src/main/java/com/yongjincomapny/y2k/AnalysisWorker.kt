package com.yongjincomapny.y2k

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.yongjincomapny.y2k.core.ai.AudioAnalyzer
import com.yongjincomapny.y2k.core.player.MusicScanner
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class AnalysisWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val musicScanner: MusicScanner,
    private val audioAnalyzer: AudioAnalyzer,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val tracks = musicScanner.scanLocalMusic()
            if (tracks.isEmpty()) return Result.success()

            audioAnalyzer.analyzeBatch(tracks) { current, total ->
                setProgress(
                    Data.Builder()
                        .putInt(KEY_CURRENT, current)
                        .putInt(KEY_TOTAL, total)
                        .build()
                )
            }

            Result.success()
        } catch (e: OutOfMemoryError) {
            // OOM 시 재시도하지 않음
            Result.failure()
        } catch (e: Exception) {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    companion object {
        const val KEY_CURRENT = "current"
        const val KEY_TOTAL = "total"
        const val WORK_NAME = "ai_analysis"
        private const val MAX_RETRIES = 2
    }
}
