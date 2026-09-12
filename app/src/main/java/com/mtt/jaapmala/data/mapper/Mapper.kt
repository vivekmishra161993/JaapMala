package com.mtt.jaapmala.data.mapper

import com.mtt.jaapmala.data.local.entity.DailyGoalEntity
import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.GoalWithJaapName
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.local.entity.JaapHistoryEntity
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.domain.model.DailyGoal
import com.mtt.jaapmala.domain.model.JaapHistory
import com.mtt.presentation.ui.screens.goals.GoalUiModel

// 🗃️ Entity -> DTO
fun JaapEntity.toMantraDto(): MantraDto {
    return MantraDto(
        name = name,
        date = date,
        todayCount = todayCount,
        malaCount = todayMalaCount,
        lifetimeCount = lifetimeCount,
        lifetimeMalaCount = lifetimeMalaCount,
        id = id,
        currentCount = count,
        malaSize = malaSize
    )
}

// 📦 DTO -> Entity
fun MantraDto.toJaapEntity(): JaapEntity {
    return JaapEntity(
        id = id,
        name = name,
        date = date,
        todayCount = todayCount,
        todayMalaCount = malaCount,
        lifetimeCount = lifetimeCount,
        lifetimeMalaCount = lifetimeMalaCount,
        count = currentCount,
        malaSize = malaSize
    )
}

fun GoalWithJaapName.toUiModel(): GoalUiModel {
    return GoalUiModel(
        id = goal.id,
        name = goal.name,
        current = goal.currentMalas,
        target = goal.targetMalas,
        endDate = goal.endDate,
        status = goal.status,
        jaapId = goal.jaapId,
        jaapName = jaapName
    )
}

fun GoalUiModel.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        jaapId = jaapId,
        name = name,
        targetMalas = target,
        currentMalas = current,
        endDate = endDate,
        status = status
    )
}

fun DailyGoalEntity.toDomain(): DailyGoal {
    return DailyGoal(
        id = id,
        jaapId = jaapId,
        targetMalas = targetMalas,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive
    )
}

fun DailyGoal.toEntity(): DailyGoalEntity {
    return DailyGoalEntity(
        id = id,
        jaapId = jaapId,
        targetMalas = targetMalas,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive
    )
}

fun JaapHistory.toEntity(): JaapHistoryEntity {
    return JaapHistoryEntity(
        jaapId = jaapId,
        date = date,
        count = count,
        malaCount = malaCount
    )
}

fun JaapHistoryEntity.toDomain(): JaapHistory {
    return JaapHistory(
        jaapId = jaapId,
        date = date,
        count = count,
        malaCount = malaCount
    )
}


