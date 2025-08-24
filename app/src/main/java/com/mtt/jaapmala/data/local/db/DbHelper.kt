package com.mtt.jaapmala.data.local.db

import android.content.Context
import android.net.Uri
import android.widget.Toast
import java.io.File
import java.io.InputStream
import java.io.OutputStream

interface FileHelper {
    fun getDatabaseFile(context: Context, dbName: String): File
    fun openOutputStream(context: Context, uri: Uri, mode: String = "w"): OutputStream?
    fun openInputStream(context: Context, uri: Uri): InputStream?
    fun fileExists(file: File): Boolean
}


interface BackupPreferences {
    fun getBackupUri(context: Context): Uri?
    fun saveBackupUri(context: Context, uri: Uri)
}

interface Notifier {
    fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT)
    fun logError(tag: String, message: String, throwable: Throwable? = null)
}

interface AppRestarter {
    fun restartApp(context: Context)
}