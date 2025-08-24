package com.mtt.jaapmala.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mtt.jaapmala.data.local.dao.JaapDao
import com.mtt.jaapmala.data.local.entity.JaapEntity

@Database(entities = [JaapEntity::class], version = 1, exportSchema = false)
abstract class JaapDatabase: RoomDatabase() {
    abstract fun jaapDao():JaapDao
}