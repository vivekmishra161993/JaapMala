package com.mtt.presentation.ui.screens.add_goal


import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mtt.jaapmala.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    paddingValues: PaddingValues,
    navController: NavController, // For navigating back
    viewModel: AddGoalViewModel = hiltViewModel<AddGoalViewModel>()
) {

    var isExpanded by remember { mutableStateOf(false) }
    val mantras by viewModel.mantra.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.endDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis >= System.currentTimeMillis() - 86400000
            }
        }
    )
    var showDatePickerDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // Add more space between fields
    ) {
        // Dropdown for selecting Jaapa
        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                readOnly = true,
                value = uiState.selectedJaapName.ifEmpty { "Select a Jaap" },
                onValueChange = {},
                label = { Text("For which Jaap?") },
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 18.sp),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) }
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false }
            ) {
                mantras.forEach { mantra ->
                    DropdownMenuItem(
                        text = { Text(mantra.name) },
                        onClick = {
                            viewModel.onJaapSelected(mantra.id, mantra.name)
                            isExpanded = false
                        }
                    )
                }
            }
        }
        uiState.jaapError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        // Text field for Goal Name
        OutlinedTextField(
            value = uiState.goalName,
            onValueChange = viewModel::onGoalNameChange,
            label = { Text("Goal Name") },
            singleLine = true,
            isError = uiState.nameError != null,
            supportingText = {
                uiState.nameError?.let { Text(it) }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = 18.sp)
        )

        // Text field for Target Malas
        OutlinedTextField(
            value = uiState.targetMalas,
            onValueChange = viewModel::onTargetMalasChange,
            label = { Text("Target Malas") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = 18.sp),
            isError = uiState.targetMalasError != null,
            supportingText = {
                uiState.targetMalasError?.let { Text(it) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            OutlinedTextField(
                value = DateUtils.formatMillisToDate(uiState.endDate),
                onValueChange = {},
                label = { Text("Target End Date") },
                readOnly = true,
                enabled = true,
                isError = uiState.dateError != null,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pick date"
                    )
                },
                supportingText = {
                    uiState.dateError?.let {
                        Text(it, color = MaterialTheme.colorScheme.error)
                    }
                }
            )

            // ✅ THIS captures clicks reliably
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null // no ripple on top
                    ) {
                        showDatePickerDialog = true
                    }
            )
        }


        ElevatedButton(
            enabled = uiState.isFormValid,
            onClick = {
                viewModel.submitGoal {
                    navController.popBackStack()
                }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                "Submit",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        if (showDatePickerDialog) {
            DatePickerDialog(
                onDismissRequest = { showDatePickerDialog = false },
                confirmButton = {
                    Button(onClick = {
                        viewModel.onDateSelected(endDatePickerState.selectedDateMillis)
                        showDatePickerDialog = false
                    }) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDatePickerDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
            {
                DatePicker(state = endDatePickerState)
            }
        }

    }

}


