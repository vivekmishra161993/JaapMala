package com.mtt.presentation.ui.screens.daily_goal

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DonutLarge
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
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
    val cardColor = when (goal.status) {
        DailyGoalDayStatus.PENDING,
        DailyGoalDayStatus.PARTIAL ->
            MaterialTheme.colorScheme.surfaceVariant

        DailyGoalDayStatus.COMPLETED ->
            MaterialTheme.colorScheme.tertiaryContainer

        DailyGoalDayStatus.MISSED ->
            MaterialTheme.colorScheme.errorContainer
    }

    val progressColor = when (goal.status) {
        DailyGoalDayStatus.PENDING,
        DailyGoalDayStatus.PARTIAL ->
            MaterialTheme.colorScheme.primary

        DailyGoalDayStatus.COMPLETED ->
            MaterialTheme.colorScheme.tertiary

        DailyGoalDayStatus.MISSED ->
            MaterialTheme.colorScheme.error
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            )
    ) {

        val animatedProgress by animateFloatAsState(
            targetValue = goal.progress,
            animationSpec = tween(
                durationMillis = 1000,
                delayMillis = 200
            ),
            label = "dailyGoalProgressAnimation"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = goal.jaapName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.daily_goal),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.7f
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }

                DailyGoalStatusBadge(
                    status = goal.status
                )
            }

            // Progress
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surface,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )

                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = stringResource(R.string.malas, goal.completedMalas, goal.targetMalas),
                        style = MaterialTheme.typography.bodyLarge,
                        color = progressColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = stringResource(R.string.complete, (goal.progress * 100).toInt()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.7f
                        )
                    )
                }
            }

            // Remaining
            Text(
                text = when (goal.status) {
                    DailyGoalDayStatus.COMPLETED ->
                        stringResource(R.string.today_s_goal_completed)

                    DailyGoalDayStatus.PARTIAL ->
                        stringResource(R.string.malas_remaining, goal.remainingMalas)

                    DailyGoalDayStatus.PENDING ->
                        stringResource(R.string.start_today_s_practice)

                    DailyGoalDayStatus.MISSED ->
                        stringResource(R.string.today_s_goal_was_missed)
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.7f
                )
            )

            // Bottom Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {

                TextButton(
                    onClick = onViewHistory
                ) {
                    Text(stringResource(R.string.view_history))
                }

                TextButton(
                    onClick = onStop
                ) {
                    Text(stringResource(R.string.stop))
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

    val fontScale = LocalDensity.current.fontScale
    val horizontalPadding =
        (8 * fontScale).dp.coerceAtMost(12.dp)

    val verticalPadding =
        (4 * fontScale).dp.coerceAtMost(6.dp)

    val iconSize =
        (16 * fontScale).dp.coerceIn(14.dp, 20.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .widthIn(max = 120.dp)
            .background(
                color.copy(alpha = 0.1f),
                RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            )
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(iconSize)
        )

        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            softWrap = false
        )
    }
}

