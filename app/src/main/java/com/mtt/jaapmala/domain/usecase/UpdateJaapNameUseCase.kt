package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.repository.JaapRepository
import javax.inject.Inject

class UpdateJaapNameUseCase @Inject constructor(
    private val repository: JaapRepository
) {
    suspend operator fun invoke(jaapId: Int, newName: String) {
        repository.updateJaapName(jaapId, newName)
    }
}