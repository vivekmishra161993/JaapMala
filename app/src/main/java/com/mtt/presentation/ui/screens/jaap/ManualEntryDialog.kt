package com.mtt.presentation.ui.screens.jaap

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.mtt.jaapmala.R

@Composable
fun ManualJaapEntryDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var countText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_count)) },
        text = {
            OutlinedTextField(
                value = countText,
                onValueChange = { newValue ->
                    // Allow only digits
                    if (newValue.all { it.isDigit() }) {
                        countText = newValue
                    }
                },
                label = { Text(stringResource(R.string.enter_count)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = {
                val count = countText.toIntOrNull()
                if (count != null && count > 0) {
                    onSubmit(count)
                }
            }) {
                Text(stringResource(R.string.submit))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
