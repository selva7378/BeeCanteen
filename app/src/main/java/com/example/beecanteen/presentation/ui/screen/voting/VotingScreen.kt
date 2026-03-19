package com.example.beecanteen.presentation.ui.screen.voting

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.repository.authentication.Result
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotingScreen(
    name: String,
    viewModel: VotingViewModel = hiltViewModel()
) {
    val pollsState by viewModel.pollsState.collectAsState()
    val customAlarms by viewModel.customAlarms.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState() // ✨ Observe refreshing state

    val context = LocalContext.current
    val activity = context as? Activity

    var showAlarmDialog by remember { mutableStateOf(false) }
    var showRationaleDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showAlarmDialog = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize()) {

            Text(
                text = "Welcome, $name! Today is ${viewModel.todayFormatted()}",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.titleMedium
            )

            // ✨ The PullToRefresh wrapper
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.fetchPolls() },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val result = pollsState) {
                    null, Result.Loading -> {
                        if (!isRefreshing) {
                            // Wrapped in LazyColumn so pull-to-refresh works even when loading
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                item {
                                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }
                        }
                    }

                    is Result.Success -> {
                        VotingScreenContent(
                            polls = result.data,
                            onOptionSelected = { categoryId, optionId ->
                                viewModel.castVote(categoryId, optionId)
                            },
                            onRevokeClicked = { categoryId ->
                                viewModel.revokeVote(categoryId)
                            }
                        )
                    }

                    is Result.Error -> {
                        // Wrapped in LazyColumn so user can swipe down to try again if it fails
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            item {
                                Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "Error loading polls: ${result.message}",
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB
        // FAB
        FloatingActionButton(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    val isGranted = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED

                    if (isGranted) {
                        showAlarmDialog = true
                    } else {
                        val shouldShowRationale = activity?.let {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.POST_NOTIFICATIONS
                            )
                        } == true

                        if (shouldShowRationale) {
                            // User denied previously, show our friendly explanation
                            showRationaleDialog = true
                        } else {
                            // ✨ FIRST TIME ASKING! Launch the system prompt.
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }

                } else {
                    // Android 12 or below
                    showAlarmDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Schedule daily reminder notification"
            )
        }
    }

    // Rationale Dialog -> Goes to Settings
    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Notification Permission Required") },
            text = {
                Text("To enable reminders, please allow notification permission in settings.")
            },
            confirmButton = {
                Button(onClick = {
                    showRationaleDialog = false
                    openAppSettings(context)
                }) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRationaleDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAlarmDialog) {
        AlarmSettingsDialog(
            alarms = customAlarms,
            onDismiss = { showAlarmDialog = false },
            onAddAlarm = { hour, minute ->
                viewModel.addCustomAlarm(hour, minute)
            },
            onDeleteAlarm = { time ->
                viewModel.removeCustomAlarm(time)
            }
        )
    }
}

/* ---------------- SETTINGS NAVIGATION ---------------- */

fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    ).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}

/* ---------------- UI ---------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingsDialog(
    alarms: List<String>,
    onDismiss: () -> Unit,
    onAddAlarm: (Int, Int) -> Unit,
    onDeleteAlarm: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Daily Reminders") },
        text = {
            Column {
                if (alarms.isEmpty()) {
                    Text("No reminders set.")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                        items(alarms) { time ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(time)
                                IconButton(onClick = { onDeleteAlarm(time) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { showTimePicker = true }) {
                Text("Add Reminder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState()

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                Button(onClick = {
                    onAddAlarm(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VotingScreenContent(
    polls: List<CategoryPoll>,
    onOptionSelected: (String, String) -> Unit,
    onRevokeClicked: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(polls) { poll ->
            VotingPollCard(
                poll = poll,
                selectedOptionId = poll.currentVotedOptionId,
                onOptionSelected = { onOptionSelected(poll.category.id, it) },
                onRevokeClicked = { onRevokeClicked(poll.category.id) }
            )
        }
    }
}

@Composable
fun VotingPollCard(
    poll: CategoryPoll,
    selectedOptionId: String?,
    onOptionSelected: (String) -> Unit,
    onRevokeClicked: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(poll.category.title, fontWeight = FontWeight.Bold)

                if (selectedOptionId != null) {
                    TextButton(onClick = onRevokeClicked) {
                        Text("Revoke")
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            poll.options.forEach { option ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(option.id) }
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = option.id == selectedOptionId,
                        onClick = { onOptionSelected(option.id) }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(option.name)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text("${poll.category.totalVotes} votes")
        }
    }
}


fun formatMillisToTime(millis: Long): String {
    val totalMinutes = millis / (1000 * 60)
    val hours = (totalMinutes / 60).toInt()
    val minutes = (totalMinutes % 60).toInt()

    val amPm = if (hours >= 12) "PM" else "AM"
    val displayHour = if (hours == 0) 12 else if (hours > 12) hours - 12 else hours

    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minutes, amPm)
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}