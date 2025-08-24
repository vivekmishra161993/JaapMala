package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import javax.inject.Inject

class DeleteJaapUseCase @Inject constructor(
    private val repository: JaapRepository
) {
    suspend operator fun invoke(jaap: JaapEntity) {
        repository.deleteJaap(jaap)
    }
}