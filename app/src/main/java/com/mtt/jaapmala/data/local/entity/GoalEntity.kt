package com.mtt.jaapmala.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = JaapEntity::class,
            parentColumns = ["id"],
            childColumns = ["jaapId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("jaapId")]
)
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val jaapId: Int,
    val name: String,
    val targetMalas: Int,
    val currentMalas: Int = 0,
    val startDate: Long? = null,
    val endDate: Long? = null
)

