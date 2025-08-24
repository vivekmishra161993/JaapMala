package com.mtt.jaapmala.di

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.room.Room
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.db.AppRestarter
import com.mtt.jaapmala.data.local.db.BackupPreferences
import com.mtt.jaapmala.data.local.db.BackupPrefs
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.data.local.db.DatabaseProvider
import com.mtt.jaapmala.data.local.db.FileHelper
import com.mtt.jaapmala.data.local.db.JaapDatabase
import com.mtt.jaapmala.data.local.db.Notifier
import com.mtt.jaapmala.data.repository.JaapRepositoryImpl
import com.mtt.jaapmala.domain.repository.JaapRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context):JaapDatabase{
        return Room.databaseBuilder(context,JaapDatabase::class.java,"JaapMala").build()
    }
    @Provides
    @Singleton
    fun provideDatabaseProvider(@ApplicationContext context: Context): DatabaseProvider {
        return DatabaseProvider(context)
    }
    @Provides
    fun provideJaapDao(provider: DatabaseProvider): JaapDao {
        return provider.getDatabase().jaapDao()
    }
    @Provides
    fun provideRepository(dao: JaapDao):JaapRepository{
        return JaapRepositoryImpl(dao)
    }
    @Provides
    fun provideDatabaseManager(@ApplicationContext context: Context,
                              helper: FileHelper,
                              provider: DatabaseProvider,
                              backupPreferences: BackupPreferences,
                              notifier: Notifier,
                              appRestarter: AppRestarter): DatabaseManager {
        return  DatabaseManager(context,provider,
            helper,backupPreferences,notifier,appRestarter)
    }
    @Provides
    @Singleton
    fun provideFileHelper(): FileHelper = object : FileHelper {
        override fun getDatabaseFile(context: Context, dbName: String): File =
            context.getDatabasePath(dbName)

        override fun openOutputStream(context: Context, uri: Uri, mode: String): OutputStream? =
            context.contentResolver.openOutputStream(uri, mode)

        override fun openInputStream(context: Context, uri: Uri): InputStream? =
            context.contentResolver.openInputStream(uri)

        override fun fileExists(file: File): Boolean = file.exists()
    }
    @Provides
    @Singleton
    fun provideBackupPreferences(@ApplicationContext context: Context): BackupPreferences =
        object : BackupPreferences {
            override fun getBackupUri(context: Context): Uri? = BackupPrefs.getBackupUri(context)
            override fun saveBackupUri(context: Context, uri: Uri) = BackupPrefs.saveBackupUri(context, uri)
        }
    @Provides
    @Singleton
    fun provideNotifier(): Notifier = object : Notifier {
        override fun showToast(context: Context, message: String, duration: Int) {
            Toast.makeText(context, message, duration).show()
        }
        override fun logError(tag: String, message: String, throwable: Throwable?) {
            android.util.Log.e(tag, message, throwable)
        }
    }
    @Provides
    @Singleton
    fun provideAppRestarter(): AppRestarter = object : AppRestarter {
        override fun restartApp(context: Context) {
            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            Runtime.getRuntime().exit(0)
        }
    }

}