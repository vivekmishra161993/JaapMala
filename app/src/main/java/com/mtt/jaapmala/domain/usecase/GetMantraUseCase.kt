package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMantraUseCase @Inject constructor(
    private val repository: JaapRepository
) {
    suspend operator fun invoke(id: Int): Flow<JaapEntity> {
        return repository.getMantra(id)
    }
}