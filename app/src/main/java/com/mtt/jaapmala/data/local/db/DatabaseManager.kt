package com.mtt.jaapmala.data.local.db
import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import com.mtt.jaapmala.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
 class DatabaseManager @Inject constructor(
    @ApplicationContext private val context: Context, // Still needed for some operations
    private val databaseProvider: DatabaseProvider,
    private val fileHelper: FileHelper, // Injected
    private val notifier: Notifier, // Injected
    private val appRestarter: AppRestarter // Injected
) {
     private var backupLauncher: ManagedActivityResultLauncher<String, Uri?>? = null
    private var restoreLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>? = null
    // Call this from your Activity/Fragment where you register the launchers
    fun setupActivityLaunchers(
        backupLauncher: ManagedActivityResultLauncher<String, Uri?>,
        restoreLauncher: ManagedActivityResultLauncher<Array<String>, Uri?>
    ) {
        this.backupLauncher = backupLauncher
        this.restoreLauncher = restoreLauncher
    }
    fun backupDatabase() {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy_HH-mm-ss")
            val fileName = "jaap_backup_${LocalDateTime.now().format(formatter)}.db"
            backupLauncher?.launch(fileName) // Use the injected launcher
    }

    fun restoreDatabase() {
        restoreLauncher?.launch(arrayOf("application/octet-stream")) // Use the injected launcher
    }

    fun handleBackup(uri: Uri?) {
        uri?.let {
            val dbFile = fileHelper.getDatabaseFile(context, Constants.DB_NAME)

            if (!fileHelper.fileExists(dbFile)) {
                notifier.logError("Backup", "Database file does not exist!")
                notifier.showToast(context, "Database not found")
                return
            }

            try {
                fileHelper.openOutputStream(context, it, "rwt")?.use { output ->
                    dbFile.inputStream().use { input ->
                        input.copyTo(output)
                    }
                }

                notifier.showToast(context, "Backup successful")
            } catch (e: Exception) {
                notifier.showToast(context, "Backup failed")
                notifier.logError("Backup", "Backup failed", e)
            }
        }
    }

    // Now returns a Boolean indicating success/failure
    fun handleRestore(uri: Uri?): Boolean {
        if (uri == null) {
            return false
        }

        return try {
            databaseProvider.closeDatabase()
            val dbFile = fileHelper.getDatabaseFile(context, Constants.DB_NAME)

            fileHelper.openInputStream(context, uri)?.use { input ->
                dbFile.outputStream().use { output -> // This is a Kotlin extension on File
                    input.copyTo(output)
                }
            }
            notifier.showToast(context, "Restore successful")
            appRestarter.restartApp(context) // Delegate app restart
            true // Indicate success
        } catch (e: Exception) {
            notifier.showToast(context, "Restore failed")
            notifier.logError("Restore", "Restore failed", e)
            false // Indicate failure
        }
    }
}
