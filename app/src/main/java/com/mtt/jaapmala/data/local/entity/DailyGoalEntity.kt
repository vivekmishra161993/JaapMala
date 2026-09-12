package com.mtt.jaapmala.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_goals",
    foreignKeys = [
        ForeignKey(
            entity = JaapEntity::class,
            parentColumns = ["id"],
            childColumns = ["jaapId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["jaapId"]),
        Index(value = ["isActive"])
    ]
)
data class DailyGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val jaapId: Int,

    val targetMalas: Int,

    val startDate: String, // yyyy-MM-dd

    val endDate: String? = null, // yyyy-MM-dd

    val isActive: Boolean = true
)