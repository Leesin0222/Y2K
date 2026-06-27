package com.yongjincomapny.y2k.core.player

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LyricsExtractor @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun extract(uri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            val filePath = getFilePath(uri) ?: return@withContext null
            val audioFile = File(filePath)
            val lrcFile = File(audioFile.parentFile, audioFile.nameWithoutExtension + ".lrc")
            if (lrcFile.exists()) {
                parseLrc(lrcFile.readText())
            } else {
                // Try .txt fallback
                val txtFile = File(audioFile.parentFile, audioFile.nameWithoutExtension + ".txt")
                if (txtFile.exists()) txtFile.readText().takeIf { it.isNotBlank() }
                else null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun getFilePath(uri: Uri): String? {
        var cursor: Cursor? = null
        return try {
            cursor = context.contentResolver.query(
                uri, arrayOf(MediaStore.Audio.Media.DATA), null, null, null,
            )
            if (cursor != null && cursor.moveToFirst()) {
                cursor.getString(0)
            } else null
        } catch (_: Exception) {
            null
        } finally {
            cursor?.close()
        }
    }

    private fun parseLrc(content: String): String {
        // Strip LRC timestamps like [00:12.34] and return plain text
        return content.lines()
            .filter { it.isNotBlank() }
            .map { line ->
                line.replace(Regex("\\[\\d{2}:\\d{2}[.:]\\d{2,3}]"), "").trim()
            }
            .filter { it.isNotEmpty() && !it.startsWith("[") }
            .joinToString("\n")
    }
}
