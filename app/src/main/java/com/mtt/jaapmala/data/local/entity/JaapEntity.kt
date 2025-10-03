package com.mtt.jaapmala.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jaaps")
data class JaapEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String, // For daily reset logic
    val count: Int = 0, // Tracks progress towards next 108
    val todayCount: Int = 0,
    val todayMalaCount: Int = 0,
    val lifetimeCount: Long = 0L,
    val lifetimeMalaCount: Long = 0L,
    val sessionCount: Int = 0,
    val sessionMalaCount: Int = 0,
    val malaSize: Int = 108
)

