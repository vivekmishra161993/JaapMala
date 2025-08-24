package com.mtt.jaapmala.util

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.data.model.MantraDto

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
