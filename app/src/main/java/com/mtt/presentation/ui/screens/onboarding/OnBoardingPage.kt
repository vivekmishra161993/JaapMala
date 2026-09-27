package com.mtt.presentation.ui.screens.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mtt.jaapmala.R

data class OnboardingPage(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    @DrawableRes val imageRes: Int
)

val onboardingPages = listOf(
    OnboardingPage(
        titleRes = R.string.welcome_to_jaap_mala,
        descriptionRes = R.string.track_your_daily_jaaps_easily,
        imageRes = R.drawable.ic_launcher_round
    ),
    OnboardingPage(
        titleRes = R.string.mala_counting,
        descriptionRes = R.string.increment_your_count_in_multiples_of_your_chosen_mala_size,
        imageRes = R.drawable.ic_mala_beads
    ),
    OnboardingPage(
        titleRes = R.string.backup_restore,
        descriptionRes = R.string.back_up_and_restore_your_jaaps_securely_to_your_device,
        imageRes = R.drawable.ic_backup
    )
)
