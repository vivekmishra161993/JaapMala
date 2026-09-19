package com.mtt.presentation.ui.screens.goals

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GoalsTypeSelector(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {

        SegmentedButton(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 0,
                count = 2
            )
        ) {
            Text("Lifetime")
        }

        SegmentedButton(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            shape = SegmentedButtonDefaults.itemShape(
                index = 1,
                count = 2
            )
        ) {
            Text("Daily")
        }
    }
}