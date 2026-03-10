package com.example.beecanteen.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.beecanteen.presentation.ui.theme.BeeCanteenTheme

// ui/components/MenuVoteCard.kt
@Composable
fun MenuVoteCard(
    emoji: String,
    title: String,
    timeSlot: String,
    category: String,
    voteCount: Int,
    content: @Composable ColumnScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 24.sp)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleLarge)
                    Text(
                        text = timeSlot,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
                Spacer(Modifier.weight(1f))
                CategoryChip(label = category)
            }

            Spacer(Modifier.height(16.dp))
            content()
            Spacer(Modifier.height(12.dp))

            // Vote count badge
            VoteCountBadge(count = voteCount)
        }
    }
}

@Preview
@Composable
fun MenuVoteCardPreview() {
    BeeCanteenTheme  {
        MenuVoteCard(
            emoji = "🍔",
            title = "Cheeseburger",
            timeSlot = "12:0",
            category = "tea and coffe",
            voteCount = 5,
            content = {
                Text(text = "Delicious cheeseburger with all the fixings.")
            }
        )
    }
}