package com.example.beecanteen.presentation.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.repository.admin.AdminResult
import com.example.beecanteen.presentation.ui.theme.BeeCanteenTheme
import java.util.Locale

@Composable
fun AdminScreen(
    onClickFloat: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val polls by viewModel.polls.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchPolls()
    }

    val pollsList = when (val result = polls) {
        is AdminResult.Success -> {
            Log.i("AdminScreen", "Success: ${result.data}")
            result.data
        }
        else -> {
            Log.i("AdminScreen", "Not a Success result: $polls")
            emptyList()
        }
    }

    AdminScreenContent(
        polls = pollsList,
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.fetchPolls() },
        onDeleteClick = { categoryId -> viewModel.deleteCategory(categoryId) },
        onClickFloat = onClickFloat
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreenContent(
    polls: List<CategoryPoll>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onDeleteClick: (String) -> Unit,
    onClickFloat: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(polls) { poll ->
                    CategoryPollCard(
                        poll = poll,
                        onDeleteClick = { onDeleteClick(poll.category.id) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = onClickFloat,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Category")
        }
    }
}

@Composable
fun CategoryPollCard(
    poll: CategoryPoll,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Category Name + Delete Icon (The Bin!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = poll.category.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Poll",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start and End Time Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${formatMillisToTime(poll.category.startTime)} - ${formatMillisToTime(poll.category.endTime)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Options List with Radio Button Icons
            poll.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option.name, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

// Helper to convert Long milliseconds to "10:00 AM" format
fun formatMillisToTime(millis: Long): String {
    val totalMinutes = millis / (1000 * 60)
    val hours = (totalMinutes / 60).toInt()
    val minutes = (totalMinutes % 60).toInt()

    val amPm = if (hours >= 12) "PM" else "AM"
    val displayHour = if (hours == 0) 12 else if (hours > 12) hours - 12 else hours

    return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minutes, amPm)
}

@Preview(showBackground = true)
@Composable
fun AdminScreenPreview() {
    BeeCanteenTheme {
        AdminScreenContent(
            polls = emptyList(),
            isRefreshing = false,
            onRefresh = {},
            onDeleteClick = {},
            onClickFloat = {}
        )
    }
}