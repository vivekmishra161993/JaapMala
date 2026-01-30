package com.mtt.presentation.ui.screens.goals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.data.local.entity.GoalStatus
import com.mtt.jaapmala.util.DateUtils
import com.mtt.presentation.ui.screens.home.DeleteConfirmationDialog

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalCard(
    goal: GoalUiModel,
    onDeleteGoal: () -> Unit,
) {
    val cardColor = when (goal.status) {
        GoalStatus.ACTIVE -> MaterialTheme.colorScheme.surfaceVariant
        GoalStatus.SUCCEEDED -> MaterialTheme.colorScheme.tertiaryContainer
        GoalStatus.FAILED -> MaterialTheme.colorScheme.errorContainer
    }

    val progressColor = when (goal.status) {
        GoalStatus.ACTIVE -> MaterialTheme.colorScheme.primary
        GoalStatus.SUCCEEDED -> MaterialTheme.colorScheme.tertiary
        GoalStatus.FAILED -> MaterialTheme.colorScheme.error
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            title = "Delete Goal",
            message = "Are you sure you want to delete '${goal.name}'?",
            onConfirm = {
                onDeleteGoal()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = { showDeleteDialog = true }
            )
    ) {
        val animatedProgress by animateFloatAsState(
            targetValue = goal.progress,
            animationSpec = tween(durationMillis = 1000, delayMillis = 200),
            label = "progressAnimation"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // --- Top Row: Title + Badge ---
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
                        text = goal.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    goal.jaapName?.let {
                        Text(
                            text = "For: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                StatusBadge(status = goal.status)
            }

            // --- Progress Section ---
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surface,
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                )

                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${goal.current} / ${goal.target}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = progressColor,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "${(goal.progress * 100).toInt()}% Complete",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    )
                }
            }

            // --- Bottom Row ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "End Date: ${DateUtils.formatMillisToDate(goal.endDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

/**
 * Responsive Status Badge (Single Line Always)
 */
@Composable
private fun StatusBadge(status: GoalStatus) {

    val (text, color, icon) = when (status) {
        GoalStatus.ACTIVE -> Triple("Active", MaterialTheme.colorScheme.primary, Icons.Default.DonutLarge)
        GoalStatus.SUCCEEDED -> Triple("Succeeded", Color(0xFF388E3C), Icons.Default.CheckCircle)
        GoalStatus.FAILED -> Triple("Failed", Color(0xFFD32F2F), Icons.Default.Warning)
    }

    val fontScale = LocalDensity.current.fontScale
    val horizontalPadding = (8 * fontScale).dp.coerceAtMost(12.dp)
    val verticalPadding = (4 * fontScale).dp.coerceAtMost(6.dp)
    val iconSize = (16 * fontScale).dp.coerceIn(14.dp, 20.dp)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .widthIn(max = 120.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding)
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

@Preview
@Composable
fun Preview() {
    GoalCard(
        GoalUiModel(
            name = "Very Long Goal Name That Should Wrap Into Multiple Lines Without Breaking Layout",
            current = 10,
            target = 100,
            jaapName = "Long Jaap Name That Should Also Wrap Into Multiple Lines Smoothly",
            endDate = 0,
            status = GoalStatus.ACTIVE
        ),
        {}
    )
}
