package com.mtt.presentation.ui.screens.add_goal


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mtt.jaapmala.R
import com.mtt.jaapmala.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: AddGoalViewModel = hiltViewModel()
) {
    var isExpanded by remember { mutableStateOf(false) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val mantras by viewModel.mantra.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val endDatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = uiState.endDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(
                utcTimeMillis: Long
            ): Boolean {
                return utcTimeMillis >=
                        System.currentTimeMillis() - 86400000
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = 20.dp,
                vertical = 16.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------------------------------------------------------
        // Header
        // ---------------------------------------------------------

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary
                            .copy(alpha = 0.08f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = stringResource(R.string.set_a_long_term_goal_for_your_jaap_practice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ---------------------------------------------------------
        // Mantra Selection
        // ---------------------------------------------------------

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isExpanded,
            onExpandedChange = {
                isExpanded = !isExpanded
            }
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(
                        MenuAnchorType.PrimaryNotEditable,
                        true
                    ),
                readOnly = true,
                value = uiState.selectedJaapName.ifEmpty {
                    stringResource(R.string.select_a_jaap)
                },
                onValueChange = {},
                label = {
                    Text(
                        stringResource(R.string.for_which_jaap)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.SelfImprovement,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isExpanded
                    )
                },
                isError = uiState.jaapError != null,
                supportingText = {
                    uiState.jaapError?.let {
                        Text(it)
                    }
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {
                    isExpanded = false
                }
            ) {
                mantras.forEach { mantra ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = mantra.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.SelfImprovement,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            viewModel.onJaapSelected(
                                mantra.id,
                                mantra.name
                            )
                            isExpanded = false
                        }
                    )
                }
            }
        }

        // ---------------------------------------------------------
        // Goal Name
        // ---------------------------------------------------------

        OutlinedTextField(
            value = uiState.goalName,
            onValueChange = viewModel::onGoalNameChange,
            label = {
                Text(
                    stringResource(R.string.goal_name)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Flag,
                    contentDescription = null
                )
            },
            singleLine = true,
            isError = uiState.nameError != null,
            supportingText = {
                uiState.nameError?.let {
                    Text(it)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        // ---------------------------------------------------------
        // Target Malas
        // ---------------------------------------------------------

        OutlinedTextField(
            value = uiState.targetMalas,
            onValueChange = viewModel::onTargetMalasChange,
            label = {
                Text(
                    stringResource(R.string.target_malas)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Repeat,
                    contentDescription = null
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            isError = uiState.targetMalasError != null,
            supportingText = {
                uiState.targetMalasError?.let {
                    Text(it)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            )
        )

        // ---------------------------------------------------------
        // End Date
        // ---------------------------------------------------------

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = DateUtils.formatMillisToDate(
                    uiState.endDate
                ),
                onValueChange = {},
                label = {
                    Text(
                        stringResource(R.string.target_end_date)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null
                    )
                },
                readOnly = true,
                enabled = true,
                isError = uiState.dateError != null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                supportingText = {
                    uiState.dateError?.let {
                        Text(it)
                    }
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        interactionSource = remember {
                            MutableInteractionSource()
                        },
                        indication = null
                    ) {
                        showDatePickerDialog = true
                    }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // ---------------------------------------------------------
        // Submit
        // ---------------------------------------------------------

        Button(
            enabled = uiState.isFormValid,
            onClick = {
                viewModel.submitGoal {
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            contentPadding = PaddingValues(
                horizontal = 24.dp
            )
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // -------------------------------------------------------------
    // Date Picker
    // -------------------------------------------------------------

    if (showDatePickerDialog) {
        DatePickerDialog(
            onDismissRequest = {
                showDatePickerDialog = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDateSelected(
                            endDatePickerState.selectedDateMillis
                        )
                        showDatePickerDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.confirm),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePickerDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.cancel)
                    )
                }
            }
        ) {
            DatePicker(
                state = endDatePickerState
            )
        }
    }
}

