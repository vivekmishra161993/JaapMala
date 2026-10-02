package com.mtt.presentation.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.mtt.jaapmala.R

@Composable
fun EditMantraDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    val trimmedName = name.trim()
    val isValid = trimmedName.isNotEmpty() && trimmedName != currentName.trim()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
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
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 20.dp
                    )
            ) {

                // -----------------------------------------------------
                // Header
                // -----------------------------------------------------

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = stringResource(R.string.edit_mantra),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // -----------------------------------------------------
                // Mantra Name
                // -----------------------------------------------------

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text(
                            text = stringResource(
                                R.string.enter_mantra_name
                            )
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = null
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    textStyle = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(20.dp))

                // -----------------------------------------------------
                // Actions
                // -----------------------------------------------------

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onDismiss
                    ) {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (isValid) {
                                onSubmit(trimmedName)
                            }
                        },
                        enabled = isValid,
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
