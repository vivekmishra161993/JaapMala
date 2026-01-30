package com.mtt.presentation.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mtt.jaapmala.data.model.MantraDto
import com.mtt.jaapmala.util.formatIndianNumber

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MantraListItem(
    mantraDto: MantraDto,
    onMantraClick: () -> Unit,
    onDeleteMantra: () -> Unit,
    onEditMantra: (String) -> Unit,
    modifier: Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteConfirmationDialog(
            message = "Are you sure you want to delete '${mantraDto.name}'?",
            onConfirm = {
                onDeleteMantra()
                showDeleteDialog = false
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    if (showEditDialog) {
        EditMantraDialog(
            currentName = mantraDto.name,
            onDismiss = { showEditDialog = false },
            onSubmit = { newName ->
                onEditMantra(newName)
                showEditDialog = false
            }
        )
    }

    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = onMantraClick,
                onLongClick = { showDeleteDialog = true }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {

            // ---------- Title Row ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = mantraDto.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { showEditDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Mantra",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------- Today Stats ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatsColumn(
                    title = "Today",
                    value = formatIndianNumber(mantraDto.todayCount),
                    modifier = Modifier.weight(1f)
                )

                StatsColumn(
                    title = "${mantraDto.malaSize}×",
                    value = formatIndianNumber(mantraDto.malaCount),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ---------- Total Stats (Short Labels) ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatsColumn(
                    title = "Total",
                    value = formatIndianNumber(mantraDto.lifetimeCount),
                    modifier = Modifier.weight(1f)
                )

                StatsColumn(
                    title = "Total ${mantraDto.malaSize}×",
                    value = formatIndianNumber(mantraDto.lifetimeMalaCount),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StatsColumn(
    title: String,
    value: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 4.dp),
            maxLines = 1,
            softWrap = false,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}





@Composable
fun DeleteConfirmationDialog(
    title: String = "Delete Jaap",
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMantraListItem() {
    val sampleMantra = MantraDto(
        id = 1,
        name = "Gayatri Mantra",
        todayCount = 54,
        malaCount = 2,
        lifetimeCount = 1008,
        lifetimeMalaCount = 9,
        date = "20/04/2025",
        currentCount = 0
    )


}


