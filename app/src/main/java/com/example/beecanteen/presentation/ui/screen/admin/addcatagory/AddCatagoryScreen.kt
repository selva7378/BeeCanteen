package com.example.beecanteen.presentation.ui.screen.admin.addcatagory


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.model.admin.CategoryDto
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCategoryScreen(
    viewModel: AddCatagoryViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var categoryName by rememberSaveable { mutableStateOf("") }
    var optionText by rememberSaveable { mutableStateOf("") }
    var options by rememberSaveable { mutableStateOf(listOf<String>()) }

    var startTimeMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var endTimeMillis by rememberSaveable { mutableStateOf<Long?>(null) }
    var startTimeDisplay by rememberSaveable { mutableStateOf("") }
    var endTimeDisplay by rememberSaveable { mutableStateOf("") }

    // State for controlling our TimePicker Dialog
    var showTimePicker by remember { mutableStateOf(false) }
    var isPickingStartTime by remember { mutableStateOf(true) }

    // Material 3 TimePicker State (is24Hour = false gives us the AM/PM dial!)
    val timePickerState = rememberTimePickerState(
        initialHour = 12,
        initialMinute = 0,
        is24Hour = false
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = categoryName,
            onValueChange = { categoryName = it },
            label = { Text("Category Name (e.g., Lunch)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time Selection Row using Clock Icons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    isPickingStartTime = true
                    showTimePicker = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = "Start Time")
                Spacer(modifier = Modifier.width(8.dp))
                Text(startTimeDisplay.ifEmpty { "Start Time" })
            }

            Spacer(modifier = Modifier.width(16.dp))

            OutlinedButton(
                onClick = {
                    isPickingStartTime = false
                    showTimePicker = true
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = "End Time")
                Spacer(modifier = Modifier.width(8.dp))
                Text(endTimeDisplay.ifEmpty { "End Time" })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Options Input
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = optionText,
                onValueChange = { optionText = it },
                label = { Text("Add Voting Option") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (optionText.isNotBlank() && !options.contains(optionText)) {
                        options = options + optionText
                        optionText = ""
                    }
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // List of added options
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(options) { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(option)
                    IconButton(onClick = { options = options - option }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Option")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Save Button
        Button(
            onClick = {
                if (startTimeMillis != null && endTimeMillis != null) {
                    val newCategory = CategoryDto(
                        title = categoryName,
                        startTime = startTimeMillis!!,
                        endTime = endTimeMillis!!
                    )
                    viewModel.createCategory(newCategory,options)
                    onNavigateBack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = categoryName.isNotBlank() && options.isNotEmpty() &&
                    startTimeMillis != null && endTimeMillis != null
        ) {
            Text("Save Category Poll")
        }
    }

    // --- The Clock Time Picker Dialog ---
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Calculate Milliseconds from the selected hour and minute
                    val hour = timePickerState.hour
                    val minute = timePickerState.minute
                    val millis = (hour * 60 * 60 * 1000L) + (minute * 60 * 1000L)

                    // Format for the UI Display
                    val amPm = if (hour >= 12) "PM" else "AM"
                    val displayHour = if (hour == 0) 12 else if (hour > 12) hour - 12 else hour
                    val displayMinute = String.format(Locale.getDefault(), "%02d", minute)
                    val displayStr = "$displayHour:$displayMinute $amPm"

                    if (isPickingStartTime) {
                        startTimeMillis = millis
                        startTimeDisplay = displayStr
                    } else {
                        endTimeMillis = millis
                        endTimeDisplay = displayStr
                    }

                    showTimePicker = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                // This is the actual Material 3 Clock UI!
                TimePicker(state = timePickerState)
            }
        )
    }
}