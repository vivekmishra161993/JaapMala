package com.mtt.presentation.ui.screens.daily_goal

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.R
import com.mtt.jaapmala.domain.model.DailyGoalDayStatus

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DailyGoalCard(
    goal: DailyGoalItemUiModel,
    onViewHistory: () -> Unit,
    onStop: () -> Unit
) {
    val progressColor = when (goal.status) {
        DailyGoalDayStatus.PENDING,
        DailyGoalDayStatus.PARTIAL ->
            MaterialTheme.colorScheme.primary

        DailyGoalDayStatus.COMPLETED ->
            MaterialTheme.colorScheme.tertiary

        DailyGoalDayStatus.MISSED ->
            MaterialTheme.colorScheme.error
    }

    val animatedProgress by animateFloatAsState(
        targetValue = goal.progress,
        animationSpec = tween(
            durationMillis = 700,
            easing = FastOutSlowInEasing
        ),
        label = "dailyGoalProgressAnimation"
    )

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 8.dp,
                vertical = 6.dp
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = "For : "+goal.jaapName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                }

                DailyGoalStatusBadge(
                    status = goal.status
                )
            }

            // Main progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        text = goal.completedMalas.toString(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = progressColor
                    )

                    Text(
                        text = stringResource(R.string.malas_completed),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box(
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(58.dp),
                        color = progressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 6.dp
                    )

                    Text(
                        text = "${(goal.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Progress bar
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(50)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            R.string.goal_progress,
                            goal.completedMalas,
                            goal.targetMalas
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (goal.status == DailyGoalDayStatus.PARTIAL) {
                        Text(
                            text = stringResource(
                                R.string.malas_remaining,
                                goal.remainingMalas
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Status message
            Text(
                text = when (goal.status) {
                    DailyGoalDayStatus.COMPLETED ->
                        stringResource(R.string.today_s_goal_completed)

                    DailyGoalDayStatus.PARTIAL ->
                        stringResource(
                            R.string.malas_remaining,
                            goal.remainingMalas
                        )

                    DailyGoalDayStatus.PENDING ->
                        stringResource(R.string.start_today_s_practice)

                    DailyGoalDayStatus.MISSED ->
                        stringResource(R.string.today_s_goal_was_missed)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onViewHistory
                ) {
                    Text(
                        text = stringResource(R.string.view_history),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                TextButton(
                    onClick = onStop
                ) {
                    Text(
                        text = stringResource(R.string.stop),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
@Composable
private fun DailyGoalStatusBadge(
    status: DailyGoalDayStatus
) {
    val (text, color, icon) = when (status) {

        DailyGoalDayStatus.PENDING ->
            Triple(
                stringResource(R.string.pending),
                MaterialTheme.colorScheme.primary,
                Icons.Default.Schedule
            )

        DailyGoalDayStatus.PARTIAL ->
            Triple(
                stringResource(R.string.partial),
                MaterialTheme.colorScheme.primary,
                Icons.Default.DonutLarge
            )

        DailyGoalDayStatus.COMPLETED ->
            Triple(
                stringResource(R.string.completed),
                MaterialTheme.colorScheme.tertiary,
                Icons.Default.CheckCircle
            )

        DailyGoalDayStatus.MISSED ->
            Triple(
                stringResource(R.string.missed),
                MaterialTheme.colorScheme.error,
                Icons.Default.Warning
            )
    }

    Row(
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.08f),
                shape = RoundedCornerShape(50)
            )
            .padding(
                horizontal = 9.dp,
                vertical = 5.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

