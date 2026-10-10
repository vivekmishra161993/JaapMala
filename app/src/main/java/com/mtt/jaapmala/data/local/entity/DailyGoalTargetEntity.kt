package com.mtt.jaapmala.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "daily_goal_targets",
    foreignKeys = [
        ForeignKey(
            entity = DailyGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["dailyGoalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["dailyGoalId"]),
        Index(value = ["dailyGoalId","effectiveFrom"])
    ]
)

data class DailyGoalTargetEntity(
    @PrimaryKey(autoGenerate = true)
    val id:Int =0,
    val dailyGoalId:Int,
    val targetMalas:Int,
    val effectiveFrom: String,
    val effectiveTo: String? = null
)