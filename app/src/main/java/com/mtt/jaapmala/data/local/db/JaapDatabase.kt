package com.mtt.jaapmala.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mtt.jaapmala.data.local.dao.DailyGoalDao
import com.mtt.jaapmala.data.local.dao.DailyGoalTargetDao
import com.mtt.jaapmala.data.local.dao.GoalDao
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.dao.JaapHistoryDao
import com.mtt.jaapmala.data.local.entity.DailyGoalEntity
import com.mtt.jaapmala.data.local.entity.DailyGoalTargetEntity
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity

@Database(
    entities = [
        JaapEntity::class,
        JaapHistoryEntity::class,
        GoalEntity::class,
        DailyGoalEntity::class,
        DailyGoalTargetEntity::class
    ],
    version = 8,
    exportSchema = true
)
abstract class JaapDatabase : RoomDatabase() {
    abstract fun jaapDao(): JaapDao
    abstract fun jaapHistoryDao(): JaapHistoryDao
    abstract fun goalDao(): GoalDao
    abstract fun dailyGoalDao(): DailyGoalDao
    abstract fun dailyGoalTargetDao(): DailyGoalTargetDao
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
        db.execSQL(
            """
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
        """.trimIndent()
        )

        // 2. Copy old data, converting Int → Long for lifetime fields and setting default 0 for new columns
        db.execSQL(
            """
            INSERT INTO jaaps_new (
                id, name, date, count, todayCount, todayMalaCount,
                lifetimeCount, lifetimeMalaCount, sessionCount, sessionMalaCount, malaSize
            )
            SELECT 
                id, name, date, 0 AS count, todayCount, todayMalaCount,
                lifetimeCount AS lifetimeCount, lifetimeMalaCount AS lifetimeMalaCount,
                0 AS sessionCount, 0 AS sessionMalaCount, malaSize
            FROM jaaps
        """.trimIndent()
        )

        // 3. Drop old table
        db.execSQL("DROP TABLE jaaps")

        // 4. Rename new table
        db.execSQL("ALTER TABLE jaaps_new RENAME TO jaaps")
    }
}
val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                jaapName TEXT NOT NULL,
                name TEXT NOT NULL,
                targetMalas INTEGER NOT NULL,
                currentMalas INTEGER NOT NULL DEFAULT 0,
                startDate INTEGER,
                endDate INTEGER,
                status TEXT NOT NULL DEFAULT 'ACTIVE',
                FOREIGN KEY(jaapId) REFERENCES jaaps(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_goals_jaapId ON goals(jaapId)"
        )
    }
}
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {

        // 1. Create correct table
        db.execSQL(
            """
            CREATE TABLE goals_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                name TEXT NOT NULL,
                targetMalas INTEGER NOT NULL,
                currentMalas INTEGER NOT NULL,
                startDate INTEGER,
                endDate INTEGER,
                jaapName TEXT,
                status TEXT NOT NULL,
                FOREIGN KEY(jaapId) REFERENCES jaaps(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        // 2. Copy existing data
        db.execSQL(
            """
            INSERT INTO goals_new (
                id, jaapId, name, targetMalas, currentMalas,
                startDate, endDate, jaapName, status
            )
            SELECT
                id, jaapId, name, targetMalas, currentMalas,
                startDate, endDate, jaapName, status
            FROM goals
            """.trimIndent()
        )

        // 3. Replace table
        db.execSQL("DROP TABLE goals")
        db.execSQL("ALTER TABLE goals_new RENAME TO goals")

        // 4. Recreate index
        db.execSQL(
            "CREATE INDEX IF NOT EXISTS index_goals_jaapId ON goals(jaapId)"
        )
    }
}
val MIGRATION_GOAL_REMOVE_JAAP_NAME_5_6 = object : Migration(
    startVersion = 5,
    endVersion = 6
) {
    override fun migrate(database: SupportSQLiteDatabase) {

        // 1️⃣ Create new table
        database.execSQL(
            """
            CREATE TABLE goals_new (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                name TEXT NOT NULL,
                targetMalas INTEGER NOT NULL,
                currentMalas INTEGER NOT NULL,
                startDate INTEGER,
                endDate INTEGER,
                status TEXT NOT NULL,
                FOREIGN KEY(jaapId) REFERENCES jaaps(id) ON DELETE CASCADE
            )
        """
        )

        // 2️⃣ Copy data (ignore jaapName)
        database.execSQL(
            """
            INSERT INTO goals_new (
                id, jaapId, name, targetMalas, currentMalas, startDate, endDate, status
            )
            SELECT 
                id, jaapId, name, targetMalas, currentMalas, startDate, endDate, status
            FROM goals
        """
        )

        // 3️⃣ Drop old table
        database.execSQL("DROP TABLE goals")

        // 4️⃣ Rename
        database.execSQL("ALTER TABLE goals_new RENAME TO goals")

        // 5️⃣ Recreate index
        database.execSQL("CREATE INDEX index_goals_jaapId ON goals(jaapId)")
    }
}
val MIGRATION_6_7 = object : Migration(6, 7) {

    override fun migrate(db: SupportSQLiteDatabase) {

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS daily_goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                targetMalas INTEGER NOT NULL,
                startDate TEXT NOT NULL,
                endDate TEXT,
                isActive INTEGER NOT NULL,
                FOREIGN KEY(jaapId)
                    REFERENCES jaaps(id)
                    ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_daily_goals_jaapId
            ON daily_goals(jaapId)
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE INDEX IF NOT EXISTS index_daily_goals_isActive
            ON daily_goals(isActive)
            """.trimIndent()
        )
    }
}
val MIGRATION_7_8 = object : Migration(7, 8) {

    override fun migrate(db: SupportSQLiteDatabase) {

        // Drop indexes from the old daily_goals table first.
        db.execSQL("""
            DROP INDEX IF EXISTS index_daily_goals_jaapId
        """.trimIndent())

        db.execSQL("""
            DROP INDEX IF EXISTS index_daily_goals_isActive
        """.trimIndent())

        // Rename old table
        db.execSQL("""
            ALTER TABLE daily_goals
            RENAME TO daily_goals_old
        """.trimIndent())

        // Create new daily_goals table
        db.execSQL("""
            CREATE TABLE daily_goals (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                jaapId INTEGER NOT NULL,
                isActive INTEGER NOT NULL,
                FOREIGN KEY(jaapId)
                    REFERENCES jaaps(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        // Recreate required indexes
        db.execSQL("""
            CREATE INDEX index_daily_goals_jaapId
            ON daily_goals(jaapId)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX index_daily_goals_isActive
            ON daily_goals(isActive)
        """.trimIndent())

        // Copy existing goals
        db.execSQL("""
            INSERT INTO daily_goals (
                id,
                jaapId,
                isActive
            )
            SELECT
                id,
                jaapId,
                isActive
            FROM daily_goals_old
        """.trimIndent())

        // Create target history table
        db.execSQL("""
            CREATE TABLE daily_goal_targets (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                dailyGoalId INTEGER NOT NULL,
                targetMalas INTEGER NOT NULL,
                effectiveFrom TEXT NOT NULL,
                effectiveTo TEXT,
                FOREIGN KEY(dailyGoalId)
                    REFERENCES daily_goals(id)
                    ON DELETE CASCADE
            )
        """.trimIndent())

        // Target indexes
        db.execSQL("""
            CREATE INDEX index_daily_goal_targets_dailyGoalId
            ON daily_goal_targets(dailyGoalId)
        """.trimIndent())

        db.execSQL("""
            CREATE INDEX index_daily_goal_targets_dailyGoalId_effectiveFrom
            ON daily_goal_targets(
                dailyGoalId,
                effectiveFrom
            )
        """.trimIndent())

        // Migrate existing target information
        db.execSQL("""
            INSERT INTO daily_goal_targets (
                dailyGoalId,
                targetMalas,
                effectiveFrom,
                effectiveTo
            )
            SELECT
                id,
                targetMalas,
                startDate,
                endDate
            FROM daily_goals_old
        """.trimIndent())

        // Remove old table
        db.execSQL("""
            DROP TABLE daily_goals_old
        """.trimIndent())
    }
}



