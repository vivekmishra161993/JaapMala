package com.mtt.presentation.ui.screens.onboarding

import com.mtt.jaapmala.R

data class OnboardingPage(
    val title: String,
    val description: String,
    val imageRes: Int // You can use painterResource(id)
)

val onboardingPages = listOf(
    OnboardingPage("Welcome to Jaap Mala", "Track your daily jaaps easily.", R.drawable.ic_launcher_round),
    OnboardingPage("Mala Counting", "Increment your count in multiples of your chosen mala size.", R.drawable.ic_mala_beads),
    OnboardingPage("Backup & Restore", "Back up and restore your Jaaps securely to your device.", R.drawable.ic_backup)
)
