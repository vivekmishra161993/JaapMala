package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import javax.inject.Inject

class EnsureTodayUseCase @Inject constructor(private val repository: JaapRepository) {
    suspend operator fun invoke(entity: JaapEntity): JaapEntity {
       return repository.ensureToday(entity)
    }
}