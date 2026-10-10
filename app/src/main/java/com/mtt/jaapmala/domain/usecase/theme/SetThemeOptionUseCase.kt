package com.mtt.jaapmala.domain.usecase.theme

import com.mtt.jaapmala.domain.model.ThemeOption
import com.mtt.jaapmala.domain.repository.SettingsRepository
import javax.inject.Inject

class SetThemeOptionUseCase @Inject constructor(
    private val repo: SettingsRepository
) {
    suspend operator fun invoke(option: ThemeOption) {
        repo.setThemeOption(option)
    }
}
