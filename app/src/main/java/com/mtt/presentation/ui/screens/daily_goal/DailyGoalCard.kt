package com.mtt.presentation.ui.screens.daily_goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.domain.model.DailyGoalDayStatus

@Composable
fun DailyGoalCard(
    goal: DailyGoalItemUiModel,
    onViewHistory: () -> Unit,
    onStop: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "${goal.targetMalas} malas daily",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${goal.completedMalas} / ${goal.targetMalas} malas today",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { goal.progress },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = when (goal.status) {
                    DailyGoalDayStatus.COMPLETED ->
                        "Today's goal completed"

                    DailyGoalDayStatus.PARTIAL ->
                        "${goal.remainingMalas} malas remaining"

                    DailyGoalDayStatus.PENDING ->
                        "Start today's practice"

                    DailyGoalDayStatus.MISSED ->
                        "Goal missed"
                },
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))



            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OutlinedButton(
                    onClick = onViewHistory,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("View History")
                }

                TextButton(
                    onClick = onStop
                ) {
                    Text("Stop")
                }
            }
        }
    }
}

