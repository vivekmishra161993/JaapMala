package com.mtt.jaapmala.data.local.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mtt.jaapmala.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var db: JaapDatabase? = null

    fun getDatabase(): JaapDatabase {
        if (db == null) {
            db = createDatabase()
        }
        return db!!
    }

    private fun createDatabase(): JaapDatabase {
        return Room.databaseBuilder(
            context, JaapDatabase::class.java, Constants.DB_NAME
        ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
            .addMigrations(
                MIGRATION_1_2,
                MIGRATION_2_3,
                MIGRATION_3_4,
                MIGRATION_4_5,
                MIGRATION_GOAL_REMOVE_JAAP_NAME_5_6,
                MIGRATION_6_7
            )
            .build()
    }

    fun closeDatabase() {
        db?.close()
        db = null
    }
}
