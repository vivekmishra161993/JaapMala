package com.mtt.jaapmala.domain.usecase

import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeOptionUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    operator fun invoke(): Flow<ThemeOption> = repo.themeOption
}
