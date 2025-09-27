

import android.content.Context
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import com.mtt.jaapmala.data.local.db.AppRestarter
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.data.local.db.DatabaseProvider
import com.mtt.jaapmala.data.local.db.FileHelper
import com.mtt.jaapmala.data.local.db.Notifier
import com.mtt.jaapmala.util.Constants
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class DatabaseManagerTest {

    private lateinit var context: Context
    private lateinit var fileHelper: FileHelper
    private lateinit var notifier: Notifier
    private lateinit var databaseManager: DatabaseManager
    private lateinit var databaseProvider: DatabaseProvider
    private lateinit var appRestarter: AppRestarter

    @Before
    fun setUp() {
        context = mockk(relaxed = true)
        fileHelper = mockk(relaxed = true)
        notifier = mockk(relaxed = true)
        databaseProvider = mockk(relaxed = true)
        appRestarter = mockk(relaxed = true)
        databaseManager = DatabaseManager(context,databaseProvider, fileHelper, notifier,appRestarter)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // -------------------------------------------------
    // setupActivityLaunchers, backupDatabase, restoreDatabase
    // -------------------------------------------------

    @Test
    fun backupDatabase_shouldLaunchWithGeneratedFileName() {
        val backupLauncher = mockk<ManagedActivityResultLauncher<String, Uri?>>(relaxed = true)
        val restoreLauncher = mockk<ManagedActivityResultLauncher<Array<String>, Uri?>>(relaxed = true)

        databaseManager.setupActivityLaunchers(backupLauncher, restoreLauncher)
        databaseManager.backupDatabase()

        verify { backupLauncher.launch(match { it.startsWith("jaap_backup_") && it.endsWith(".db") }) }
    }

    @Test
    fun restoreDatabase_shouldLaunchWithCorrectMimeType() {
        val backupLauncher = mockk<ManagedActivityResultLauncher<String, Uri?>>(relaxed = true)
        val restoreLauncher = mockk<ManagedActivityResultLauncher<Array<String>, Uri?>>(relaxed = true)

        databaseManager.setupActivityLaunchers(backupLauncher, restoreLauncher)
        databaseManager.restoreDatabase()

        verify { restoreLauncher.launch(arrayOf("application/octet-stream")) }
    }

    // -------------------------------------------------
    // handleBackup
    // -------------------------------------------------

    @Test
    fun handleBackup_shouldNotProceedWhenFileDoesNotExist() {
        val uri = mockk<Uri>()
        val dbFile = File("nonexistent.db")

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.fileExists(dbFile) } returns false

        databaseManager.handleBackup(uri)

        verify { notifier.logError("Backup", "Database file does not exist!") }
    }

    @Test
    fun handleBackup_shouldSucceedWhenFileIsCopied() {
        val uri = mockk<Uri>()
        val dbFile = File.createTempFile("test", ".db").apply { writeText("fake data") }

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.fileExists(dbFile) } returns true
        every { fileHelper.openOutputStream(context, uri, any()) } returns ByteArrayOutputStream()

        databaseManager.handleBackup(uri)

        verify { notifier.showToast(context, "Backup successful") }
    }

    @Test
    fun handleBackup_shouldHandleException() {
        val uri = mockk<Uri>()
        val dbFile = File.createTempFile("test", ".db").apply { writeText("fake") }

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.fileExists(dbFile) } returns true
        every { fileHelper.openOutputStream(context, uri, any()) } throws IOException("Disk full")

        databaseManager.handleBackup(uri)

        verify { notifier.showToast(context, "Backup failed") }
        verify { notifier.logError("Backup", "Backup failed", any()) }
    }

    @Test
    fun handleBackup_withNullUri_shouldDoNothing() {
        databaseManager.handleBackup(null)
        // should not crash or call anything
        confirmVerified(fileHelper, notifier)
    }

    // -------------------------------------------------
    // handleRestore
    // -------------------------------------------------

    @Test
    fun handleRestore_shouldReturnFalseWhenUriIsNull() {
        val result = databaseManager.handleRestore(null)
        Assert.assertFalse(result)
    }

    @Test
    fun handleRestore_shouldReturnTrueWhenInputStreamIsNull() {
        val uri = mockk<Uri>()
        val dbFile = File.createTempFile("test", ".db")

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.openInputStream(context, uri) } returns null

        val result = databaseManager.handleRestore(uri)

        Assert.assertTrue(result)
        verify { notifier.showToast(context, "Restore successful") }
    }


    @Test
    fun handleRestore_shouldSucceedWhenFileIsCopied() {
        val uri = mockk<Uri>()
        val dbFile = File.createTempFile("test", ".db")

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.openInputStream(context, uri) } returns ByteArrayInputStream("fake data".toByteArray())

        val result = databaseManager.handleRestore(uri)

        Assert.assertTrue(result)
        verify { notifier.showToast(context, "Restore successful") }
    }

    @Test
    fun handleRestore_shouldHandleException() {
        val uri = mockk<Uri>()
        val dbFile = File.createTempFile("test", ".db")

        every { fileHelper.getDatabaseFile(context, Constants.DB_NAME) } returns dbFile
        every { fileHelper.openInputStream(context, uri) } throws IOException("Corrupted file")

        val result = databaseManager.handleRestore(uri)

        Assert.assertFalse(result)
        verify { notifier.showToast(context, "Restore failed") }
        verify { notifier.logError("Restore", "Restore failed", any()) }
    }
}
