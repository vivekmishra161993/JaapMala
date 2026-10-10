package com.mtt.jaapmala.domain.usecase.jaap

import com.mtt.jaapmala.data.local.entity.JaapEntity
import com.mtt.jaapmala.domain.repository.JaapRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMantrasUseCase @Inject constructor(private val repo: JaapRepository) {
    operator fun invoke(): Flow<List<JaapEntity>> { return repo.getAllMantras()}
}