package com.mtt.presentation.ui.screens.add_daily_goals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.mtt.jaapmala.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDailyGoalScreen(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: AddDailyGoalViewModel = hiltViewModel()
) {

    var isExpanded by remember { mutableStateOf(false) }

    val mantras by viewModel.mantra.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // -------------------------
        // Jaap selection
        // -------------------------

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
                    Text(stringResource(R.string.for_which_jaap))
                },
                shape = RoundedCornerShape(12.dp),
                textStyle = TextStyle(fontSize = 18.sp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = isExpanded
                    )
                }
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
                            Text(mantra.name)
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

        // Jaap validation error
        uiState.jaapError?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        // -------------------------
        // Daily target
        // -------------------------

        OutlinedTextField(
            value = uiState.targetMalas,
            onValueChange = viewModel::onTargetMalasChange,
            label = {
                Text(stringResource(R.string.daily_target_malas))
            },
            placeholder = {
                Text(stringResource(R.string.e_g_10))
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            textStyle = TextStyle(fontSize = 18.sp),
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

        // -------------------------
        // Information
        // -------------------------

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    text = stringResource(R.string.daily_goal),
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = stringResource(R.string.this_target_will_start_today_and_continue_every_day_until_you_change_or_stop_it),
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = stringResource(R.string.change_later),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // -------------------------
        // Create button
        // -------------------------

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
                text = stringResource(R.string.create_daily_goal),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // -------------------------
        // General error
        // -------------------------

        uiState.error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}