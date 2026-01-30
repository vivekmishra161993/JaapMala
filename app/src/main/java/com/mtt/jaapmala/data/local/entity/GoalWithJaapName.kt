package com.mtt.jaapmala.data.local.entity

import androidx.room.Embedded

data class GoalWithJaapName(
    @Embedded val goal: GoalEntity,
    val jaapName: String
)
