package com.mtt.jaapmala.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mtt.jaapmala.data.local.dao.GoalDao
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity

@Database(entities = [JaapEntity::class, JaapHistoryEntity::class, GoalEntity::class], version = 4, exportSchema = false)
abstract class JaapDatabase: RoomDatabase() {
    abstract fun jaapDao():JaapDao
    abstract fun jaapHistoryDao(): JaapHistoryDao
    abstract fun goalDao() : GoalDao
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
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // 1. Create new table with full schema
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS jaaps_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                name TEXT NOT NULL,
                date TEXT NOT NULL,
                count INTEGER NOT NULL DEFAULT 0,
                todayCount INTEGER NOT NULL DEFAULT 0,
                todayMalaCount INTEGER NOT NULL DEFAULT 0,
                lifetimeCount INTEGER NOT NULL DEFAULT 0,
                lifetimeMalaCount INTEGER NOT NULL DEFAULT 0,
                sessionCount INTEGER NOT NULL DEFAULT 0,
                sessionMalaCount INTEGER NOT NULL DEFAULT 0,
                malaSize INTEGER NOT NULL DEFAULT 108
            )
        """.trimIndent())

        // 2. Copy old data, converting Int → Long for lifetime fields and setting default 0 for new columns
        db.execSQL("""
            INSERT INTO jaaps_new (
                id, name, date, count, todayCount, todayMalaCount,
                lifetimeCount, lifetimeMalaCount, sessionCount, sessionMalaCount, malaSize
            )
            SELECT 
                id, name, date, 0 AS count, todayCount, todayMalaCount,
                lifetimeCount AS lifetimeCount, lifetimeMalaCount AS lifetimeMalaCount,
                0 AS sessionCount, 0 AS sessionMalaCount, malaSize
            FROM jaaps
        """.trimIndent())

        // 3. Drop old table
        db.execSQL("DROP TABLE jaaps")

        // 4. Rename new table
        db.execSQL("ALTER TABLE jaaps_new RENAME TO jaaps")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                name TEXT NOT NULL,
                targetMalas INTEGER NOT NULL,
                currentMalas INTEGER NOT NULL DEFAULT 0,
                startDate INTEGER,
                endDate INTEGER,
                FOREIGN KEY(jaapId) REFERENCES jaaps(id) ON DELETE CASCADE
                );
            """.trimIndent()
        )
        database.execSQL("CREATE INDEX IF NOT EXISTS index_goals_jaapId ON goals(jaapId)")

    }
}

