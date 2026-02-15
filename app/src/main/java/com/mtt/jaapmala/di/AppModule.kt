package com.mtt.jaapmala.di

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.room.Room
import com.mtt.jaapmala.data.SoundManager
import com.mtt.jaapmala.data.local.dao.GoalDao
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.db.AppRestarter
import com.mtt.jaapmala.data.local.db.BackupPreferences
import com.mtt.jaapmala.data.local.db.BackupPrefs
import com.mtt.jaapmala.data.local.db.DatabaseManager
import com.mtt.jaapmala.data.local.db.DatabaseProvider
import com.mtt.jaapmala.data.local.db.FileHelper
import com.mtt.jaapmala.data.local.db.JaapDatabase
import com.mtt.jaapmala.data.local.db.Notifier
import com.mtt.jaapmala.data.repository.ChangeLogRepoImpl
import com.mtt.jaapmala.data.repository.GoalRepositoryImpl
import com.mtt.jaapmala.data.repository.JaapHistoryRepositoryImpl
import com.mtt.jaapmala.data.repository.JaapRepositoryImpl
import com.mtt.jaapmala.data.repository.SettingsRepositoryImpl
import com.mtt.jaapmala.domain.repository.ChangelogRepository
import com.mtt.jaapmala.domain.repository.GoalRepository
import com.mtt.jaapmala.domain.repository.JaapHistoryRepository
import com.mtt.jaapmala.domain.repository.JaapRepository
import com.mtt.jaapmala.domain.repository.SettingsRepository
import com.mtt.jaapmala.domain.usecase.AddGoalUseCase
import com.mtt.jaapmala.domain.usecase.DeleteGoalUseCase
import com.mtt.jaapmala.domain.usecase.GetDailyReminderEnabledUseCase
import com.mtt.jaapmala.domain.usecase.GetGoalsUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFeedbackUseCase
import com.mtt.jaapmala.domain.usecase.GetHapticFrequencyUseCase
import com.mtt.jaapmala.domain.usecase.GetJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.GetMeditationSoundUseCase
import com.mtt.jaapmala.domain.usecase.GetReminderTimeUseCase
import com.mtt.jaapmala.domain.usecase.GetThemeOptionUseCase
import com.mtt.jaapmala.domain.usecase.SaveJaapHistoryUseCase
import com.mtt.jaapmala.domain.usecase.SetDailyReminderEnabledUseCase
import com.mtt.jaapmala.domain.usecase.SetHapticFeedbackUseCase
import com.mtt.jaapmala.domain.usecase.SetHapticFrequencyUseCase
import com.mtt.jaapmala.domain.usecase.SetMeditationSoundUseCase
import com.mtt.jaapmala.domain.usecase.SetReminderTimeUseCase
import com.mtt.jaapmala.domain.usecase.SetThemeOptionUseCase
import com.mtt.jaapmala.domain.usecase.UpdateGoalProgressUseCase
import com.mtt.jaapmala.util.JaapSoundManager
import com.mtt.jaapmala.util.ReminderScheduler
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
    fun providesDatabase(@ApplicationContext context: Context): JaapDatabase {
        return Room.databaseBuilder(context, JaapDatabase::class.java, "JaapMala").build()
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
    fun provideJaapHistoryDao(provider: DatabaseProvider): JaapHistoryDao {
        return provider.getDatabase().jaapHistoryDao()
    }

    @Provides
    fun provideGoalDao(provider: DatabaseProvider): GoalDao {
        return provider.getDatabase().goalDao()
    }

    @Provides
    fun provideRepository(dao: JaapDao): JaapRepository {
        return JaapRepositoryImpl(dao)
    }

    @Provides
    fun provideGoalRepository(dao: GoalDao): GoalRepository {
        return GoalRepositoryImpl(dao)
    }

    @Provides
    fun provideDatabaseManager(
        @ApplicationContext context: Context,
        helper: FileHelper,
        provider: DatabaseProvider,
        notifier: Notifier,
        appRestarter: AppRestarter
    ): DatabaseManager {
        return DatabaseManager(
            context, provider,
            helper, notifier, appRestarter
        )
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
    fun provideBackupPreferences(): BackupPreferences =
        object : BackupPreferences {
            override fun getBackupUri(context: Context): Uri? = BackupPrefs.getBackupUri(context)
            override fun saveBackupUri(context: Context, uri: Uri) =
                BackupPrefs.saveBackupUri(context, uri)
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

    @Provides
    @Singleton
    fun provideJaapHistoryRepository(
        dao: JaapHistoryDao
    ): JaapHistoryRepository {
        return JaapHistoryRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideSaveJaapHistoryUseCase(
        repository: JaapHistoryRepository
    ): SaveJaapHistoryUseCase = SaveJaapHistoryUseCase(repository)

    @Provides
    @Singleton
    fun provideGetJaapHistoryUseCase(
        repository: JaapHistoryRepository
    ): GetJaapHistoryUseCase = GetJaapHistoryUseCase(repository)

    @Provides
    @Singleton
    fun provideSettingsRepository(
        @ApplicationContext context: Context
    ): SettingsRepository {
        return SettingsRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideChangeLogRepository(@ApplicationContext context: Context): ChangelogRepository {
        return ChangeLogRepoImpl(context)
    }

    @Provides
    @Singleton
    fun provideReminderScheduler(
        @ApplicationContext context: Context
    ): ReminderScheduler = ReminderScheduler(context)

    @Provides
    @Singleton
    fun provideSoundManager(
        @ApplicationContext context: Context
    ): SoundManager {
        return SoundManager(context)
    }

    @Provides
    @Singleton
    fun provideGetGoalsUseCase(repo: GoalRepository): GetGoalsUseCase =
        GetGoalsUseCase(repo)

    @Provides
    @Singleton
    fun provideAddGoalUseCase(repo: GoalRepository): AddGoalUseCase =
        AddGoalUseCase(repo)

    @Provides
    @Singleton
    fun provideDeleteGoalUseCase(repo: GoalRepository): DeleteGoalUseCase =
        DeleteGoalUseCase(repo)

    @Provides
    @Singleton
    fun provideUpdateGoalProgressUseCase(repo: GoalRepository): UpdateGoalProgressUseCase =
        UpdateGoalProgressUseCase(repo)

    // Theme
    @Provides
    fun provideGetThemeOptionUseCase(
        repo: SettingsRepository
    ) = GetThemeOptionUseCase(repo)

    @Provides
    fun provideSetThemeOptionUseCase(
        repo: SettingsRepository
    ) = SetThemeOptionUseCase(repo)

    // Meditation Sound
    @Provides
    fun provideGetMeditationSoundUseCase(
        repo: SettingsRepository
    ) = GetMeditationSoundUseCase(repo)

    @Provides
    fun provideSetMeditationSoundUseCase(
        repo: SettingsRepository
    ) = SetMeditationSoundUseCase(repo)

    // Haptic Feedback
    @Provides
    fun provideGetHapticFeedbackUseCase(
        repo: SettingsRepository
    ) = GetHapticFeedbackUseCase(repo)

    @Provides
    fun provideSetHapticFeedbackUseCase(
        repo: SettingsRepository
    ) = SetHapticFeedbackUseCase(repo)

    // Haptic Frequency
    @Provides
    fun provideGetHapticFrequencyUseCase(
        repo: SettingsRepository
    ) = GetHapticFrequencyUseCase(repo)

    @Provides
    fun provideSetHapticFrequencyUseCase(
        repo: SettingsRepository
    ) = SetHapticFrequencyUseCase(repo)

    // Daily Reminder
    @Provides
    fun provideGetDailyReminderEnabledUseCase(
        repo: SettingsRepository
    ) = GetDailyReminderEnabledUseCase(repo)

    @Provides
    fun provideSetDailyReminderEnabledUseCase(
        repo: SettingsRepository
    ) = SetDailyReminderEnabledUseCase(repo)

    // Reminder Time
    @Provides
    fun provideGetReminderTimeUseCase(
        repo: SettingsRepository
    ) = GetReminderTimeUseCase(repo)

    @Provides
    fun provideSetReminderTimeUseCase(
        repo: SettingsRepository
    ) = SetReminderTimeUseCase(repo)

    @Provides
    @Singleton
    fun provideJaapSoundManager(
        @ApplicationContext context: Context
    ): JaapSoundManager {
        return JaapSoundManager(context)
    }
}