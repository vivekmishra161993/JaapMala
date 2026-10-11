package com.mtt.presentation.ui.screens.practice_insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.domain.model.ai.PracticeInsights
import com.mtt.jaapmala.domain.model.ai.PracticeTrend
import java.util.Locale

@Composable
fun PracticeInsightsContent(
    insights: PracticeInsights
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            InsightsPeriodCard(
                periodDays = insights.periodDays
            )
        }

        item {
            InsightsSummaryCard(
                insights = insights
            )
        }

        item {
            InsightsStreakCard(
                currentStreak = insights.currentStreak,
                longestStreak = insights.longestStreak
            )
        }

        item {
            InsightsTrendCard(
                trend = insights.trend
            )
        }
    }
}
@Composable
private fun InsightsPeriodCard(
    periodDays: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Last $periodDays days",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "Your practice overview for this period",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun InsightsSummaryCard(
    insights: PracticeInsights
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(
                text = "Practice Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InsightMetric(
                    value = insights.totalMalas.toString(),
                    label = "Total malas"
                )

                InsightMetric(
                    value = String.format(
                        Locale.getDefault(),
                        "%.1f",
                        insights.averageMalasPerDay
                    ),
                    label = "Average / day"
                )

                InsightMetric(
                    value = insights.activeDays.toString(),
                    label = "Active days"
                )
            }
        }
    }
}
@Composable
private fun InsightMetric(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun InsightsStreakCard(
    currentStreak: Int,
    longestStreak: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            InsightMetric(
                value = currentStreak.toString(),
                label = "Current streak"
            )

            InsightMetric(
                value = longestStreak.toString(),
                label = "Longest streak"
            )
        }
    }
}
@Composable
private fun InsightsTrendCard(
    trend: PracticeTrend
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = "Practice Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = when (trend) {
                    PracticeTrend.IMPROVING ->
                        "Your practice has been increasing."

                    PracticeTrend.STABLE ->
                        "Your practice has remained fairly consistent."

                    PracticeTrend.DECLINING ->
                        "Your practice has decreased recently."
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}