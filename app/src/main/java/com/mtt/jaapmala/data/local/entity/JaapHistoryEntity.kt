package com.mtt.jaapmala.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "jaap_history",
    primaryKeys = ["jaapId", "date"],
    foreignKeys = [
        ForeignKey(
            entity = JaapEntity::class,
            parentColumns = ["id"],
            childColumns = ["jaapId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class JaapHistoryEntity(
    val jaapId: Int,
    val date: String, // Format: yyyy-MM-dd
    val count: Int,
    val malaCount: Int
)
