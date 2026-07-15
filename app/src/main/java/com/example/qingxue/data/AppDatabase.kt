package com.example.qingxue.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        StudyTaskEntity::class,
        FocusSessionEntity::class,
        CountdownEventEntity::class,
        DailyQuoteEntity::class,
        AiAnalysisEntity::class
    ],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "qingxue.db"
                ).addMigrations(
                    MIGRATION_1_2,
                    MIGRATION_2_3,
                    MIGRATION_3_4,
                    MIGRATION_4_5,
                    MIGRATION_5_6,
                    MIGRATION_6_7,
                    MIGRATION_7_8,
                    MIGRATION_8_9,
                    MIGRATION_9_10
                )
                    .build()
                    .also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `countdown_events` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `targetDate` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `daily_quotes` (
                        `date` TEXT NOT NULL,
                        `text` TEXT NOT NULL,
                        `source` TEXT NOT NULL,
                        `fetchedAt` INTEGER NOT NULL,
                        `isOnline` INTEGER NOT NULL,
                        `networkAttempted` INTEGER NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `countdown_events` ADD COLUMN `isPinned` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `study_tasks` ADD COLUMN `isHabit` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `study_tasks` ADD COLUMN `lastCompletedDate` TEXT"
                )
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `study_tasks` ADD COLUMN `studyType` TEXT NOT NULL DEFAULT 'GENERAL'"
                )
                database.execSQL(
                    "ALTER TABLE `study_tasks` ADD COLUMN `isCore` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `plannedMinutes` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `actualSeconds` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `pauseCount` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `pausedSeconds` INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `endReason` TEXT NOT NULL DEFAULT 'COMPLETED'"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `outcome` TEXT NOT NULL DEFAULT 'UNREVIEWED'"
                )
                database.execSQL(
                    "UPDATE `focus_sessions` SET `plannedMinutes` = `durationMinutes`, " +
                        "`actualSeconds` = `durationMinutes` * 60"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `focusBlockMinutes` " +
                        "INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `breakMinutes` " +
                        "INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `plannedCycles` " +
                        "INTEGER NOT NULL DEFAULT 1"
                )
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `completedCycles` " +
                        "INTEGER NOT NULL DEFAULT 0"
                )
                database.execSQL(
                    "UPDATE `focus_sessions` SET " +
                        "`focusBlockMinutes` = `plannedMinutes`, " +
                        "`completedCycles` = CASE WHEN `endReason` = 'COMPLETED' THEN 1 ELSE 0 END"
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `study_tasks` ADD COLUMN `description` " +
                        "TEXT NOT NULL DEFAULT ''"
                )
                database.execSQL(
                    "ALTER TABLE `countdown_events` ADD COLUMN `description` " +
                        "TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ai_analyses` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `date` TEXT NOT NULL,
                        `periodStart` TEXT NOT NULL DEFAULT '',
                        `periodEnd` TEXT NOT NULL DEFAULT '',
                        `overallComment` TEXT NOT NULL,
                        `dimensionAnalysis` TEXT NOT NULL,
                        `advice` TEXT NOT NULL,
                        `createdAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE `focus_sessions` ADD COLUMN `reflection` TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE study_tasks ADD COLUMN habitId INTEGER")
                database.execSQL("ALTER TABLE focus_sessions ADD COLUMN habitId INTEGER")
                database.execSQL(
                    "UPDATE focus_sessions SET habitId = taskId, taskId = NULL " +
                        "WHERE taskId IN (SELECT id FROM study_tasks WHERE isHabit = 1)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_study_tasks_habitId " +
                        "ON study_tasks (habitId)"
                )
                database.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_focus_sessions_habitId " +
                        "ON focus_sessions (habitId)"
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE study_tasks ADD COLUMN isArchived INTEGER NOT NULL DEFAULT 0"
                )
            }
        }
    }
}
