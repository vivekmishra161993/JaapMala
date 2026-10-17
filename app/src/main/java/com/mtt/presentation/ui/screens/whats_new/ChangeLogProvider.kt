package com.mtt.presentation.ui.screens.whats_new

object ChangeLogProvider {

    fun getChangesFor(versionCode: Int): List<String> {
        return when (versionCode) {
            14 -> listOf( "Fixed issue related to jaap history not getting saved" )
            13 -> listOf( "New: Daily Goals to build a consistent Jaap practice",
                "Track your daily goal progress and history",
                "New Practice Insights to reflect on your progress",
                "UI improvements and bug fixes" )
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
