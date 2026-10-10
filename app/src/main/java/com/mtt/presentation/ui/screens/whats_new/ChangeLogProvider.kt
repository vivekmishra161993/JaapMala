package com.mtt.presentation.ui.screens.whats_new

object ChangeLogProvider {

    fun getChangesFor(versionCode: Int): List<String> {
        return when (versionCode) {
            12-> listOf("Bug fixes and performance improvements")
            11-> listOf(
                "Configurable Sound Option while counting",
                "Minor bug fixes and performance improvements"
            )
            9 -> listOf(
                "Better UI support for large font sizes",
                "Improved goal and jaap card readability",
                "Minor bug fixes and performance improvements"
            )
            8 -> listOf(
                "New: Goals tracking to stay consistent with your Jaap practice",
                "Set a target end date for each goal",
                "Improved Jaap counting accuracy and stability",
                "Configurable haptic feedback while counting"
            )

            7 -> listOf(
                "New spiritual themes",
                "Mala size selection per Jaap",
                "Bug fixes & performance improvements"
            )

            else -> emptyList()
        }
    }
}
