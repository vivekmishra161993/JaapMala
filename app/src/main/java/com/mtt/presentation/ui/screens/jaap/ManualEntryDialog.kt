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
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun ManualJaapEntryDialog(
    onDismiss: () -> Unit,
    onSubmit: (Int) -> Unit
) {
    var countText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Count") },
        text = {
            OutlinedTextField(
                value = countText,
                onValueChange = { newValue ->
                    // Allow only digits
                    if (newValue.all { it.isDigit() }) {
                        countText = newValue
                    }
                },
                label = { Text("Enter count") },
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
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
