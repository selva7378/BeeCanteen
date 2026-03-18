package com.example.beecanteen.presentation.ui.screen.voting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.domain.model.CategoryPoll
import com.example.beecanteen.domain.model.admin.CategoryDto
import com.example.beecanteen.domain.model.admin.OptionDto
import com.example.beecanteen.presentation.ui.theme.BeeCanteenTheme
import java.util.Locale
import com.example.beecanteen.domain.repository.authentication.Result

@Composable
fun VotingScreen(
    name: String,
    viewModel: VotingViewModel = hiltViewModel()
) {
    val pollsState by viewModel.pollsState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Welcome, $name! Today is ${viewModel.todayFormatted()}",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )

        when (val result = pollsState) {
            null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            else -> {
                if (result is Result.Success) {
                    VotingScreenContent(
                        polls = result.data ?: emptyList(),
                        onOptionSelected = { categoryId, optionId ->
                            viewModel.castVote(categoryId, optionId)
                        },
                        onRevokeClicked = { categoryId ->
                            viewModel.revokeVote(categoryId)
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error loading polls: Ceck your internet",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VotingScreenContent(
    polls: List<CategoryPoll>,
    onOptionSelected: (String, String) -> Unit, // categoryId, optionId
    onRevokeClicked: (String) -> Unit // categoryId
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(polls) { poll ->
            VotingPollCard(
                poll = poll,
                // Passing the Firestore-derived state directly
                selectedOptionId = poll.currentVotedOptionId,
                onOptionSelected = { selectedOptionId ->
                    onOptionSelected(poll.category.id, selectedOptionId)
                },
                onRevokeClicked = {
                    onRevokeClicked(poll.category.id)
                }
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
            // Top Row: Category Name + Revoke Button
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

                if (selectedOptionId != null) {
                    TextButton(onClick = onRevokeClicked) {
                        Text(
                            text = "Revoke",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Start and End Time Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
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

            // Options List using RadioButtons
            poll.options.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onOptionSelected(option.id) }
                        .padding(vertical = 4.dp, horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option.id == selectedOptionId),
                        onClick = { onOptionSelected(option.id) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option.name, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-time Total Votes Footer
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ThumbUp,
                    contentDescription = "Total Votes",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${poll.category.totalVotes} votes",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline
                )
            }
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

@Preview(showBackground = true)
@Composable
fun VotingScreenPreview() {
    val dummyPolls = listOf(
        CategoryPoll(
            category = CategoryDto(id = "1", title = "Daily Beverages", startTime = 48600000L, endTime = 54600000L, totalVotes = 10),
            options = listOf(OptionDto(id = "opt1", name = "Tea"), OptionDto(id = "opt2", name = "Coffee")),
            currentVotedOptionId = "opt2"
        ),
        CategoryPoll(
            category = CategoryDto(id = "2", title = "Lunch Options", startTime = 36000000L, endTime = 41400000L, totalVotes = 45),
            options = listOf(OptionDto(id = "opt3", name = "Pizza"), OptionDto(id = "opt4", name = "Burger")),
            currentVotedOptionId = null
        )
    )

    BeeCanteenTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Welcome, User! Today is January 1",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                VotingScreenContent(
                    polls = dummyPolls,
                    onOptionSelected = { _, _ -> },
                    onRevokeClicked = {}
                )
            }
        }
    }
}