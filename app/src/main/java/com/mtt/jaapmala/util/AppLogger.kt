package com.mtt.jaapmala.util

import android.content.Context
import android.util.Log
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {

    private const val FILE_NAME = "app_debug.log"

    fun log(context: Context, tag: String, message: String) {
        try {
            val timestamp = SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS",
                Locale.getDefault()
            ).format(Date())

            val line = "$timestamp $tag: $message\n"

            File(context.filesDir, FILE_NAME).appendText(line)
        } catch (e: Exception) {
            Log.e("AppLogger", "Failed to write log", e)
        }

        // Keep Logcat as well
        Log.d(tag, message)
    }

    fun readLogs(context: Context): String {
        val file = File(context.filesDir, FILE_NAME)

        return if (file.exists()) {
            file.readText()
        } else {
            ""
        }
    }

    fun clearLogs(context: Context) {
        File(context.filesDir, FILE_NAME).delete()
    }
}