package com.mtt.jaapmala.util

import com.mtt.jaapmala.data.local.entity.GoalEntity
import com.mtt.jaapmala.data.local.entity.GoalWithJaapName
import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto
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
        currentCount=count,
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

