package com.example.beecanteen.presentation.ui.screen.admin

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.repository.admin.AdminResult


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPollDetailScreen(
    categoryId: String,
    onNavigateBack: () -> Unit,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val pollsResult by viewModel.polls.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState() // 1. Observe the refreshing state

    // Find the specific poll from the loaded state
    val poll = (pollsResult as? AdminResult.Success)?.data?.find { it.category.id == categoryId }
    Log.i("AdminPollDetailScreen", "poll: ${poll?.category?.title}")

    if (poll == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Custom Header Row for Back Navigation and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = poll.category.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // 2. Wrap the LazyColumn in a PullToRefreshBox
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.fetchPolls() }, // 3. Trigger the same fetch function
            modifier = Modifier.fillMaxSize()
        ) {
            // Voting List Details
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // Padding to avoid overlap with bottom nav
            ) {
                // Iterate over each option in the poll
                poll.options.forEach { option ->
                    // Filter the allVotes list to find votes for this specific option
                    val votesForOption = poll.allVotes.filter { it.optionId == option.id }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "${option.name} (${votesForOption.size} votes)",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    }

                    if (votesForOption.isEmpty()) {
                        item {
                            Text(
                                text = "No votes yet",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(votesForOption) { vote ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                modifier = Modifier.padding(vertical = 4.dp) // Added slight vertical padding between names
                            ) {
                                Text(
                                    text = " ${vote.userName}",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}