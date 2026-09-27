package com.mtt.jaapmala.domain.model

import androidx.annotation.StringRes
import com.mtt.jaapmala.R

enum class ThemeOption(
    @StringRes val displayNameRes: Int
) {
    SYSTEM(R.string.system_default),
    LIGHT(R.string.light),
    DARK(R.string.dark),
    MIDNIGHT_BLUE(R.string.midnight_blue),
    FOREST_EMERALD(R.string.forest_emerald),
    LOTUS_PINK(R.string.lotus_pink)
}
