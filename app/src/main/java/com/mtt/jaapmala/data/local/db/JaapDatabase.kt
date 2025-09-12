package com.mtt.jaapmala.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity

@Database(entities = [JaapEntity::class, JaapHistoryEntity::class], version = 2, exportSchema = false)
abstract class JaapDatabase: RoomDatabase() {
    abstract fun jaapDao():JaapDao
    abstract fun jaapHistoryDao(): JaapHistoryDao
}
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS jaap_history (
                jaapId INTEGER NOT NULL,
                date TEXT NOT NULL,
                count INTEGER NOT NULL,
                malaCount INTEGER NOT NULL,
                PRIMARY KEY(jaapId, date),
                FOREIGN KEY(jaapId) REFERENCES jaaps(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
    }
}
