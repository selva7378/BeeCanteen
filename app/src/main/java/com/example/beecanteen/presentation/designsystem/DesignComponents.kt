package com.example.beecanteen.presentation.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.beecanteen.domain.beverages
import com.example.beecanteen.domain.categories
import com.example.beecanteen.domain.model.user.Beverage
import com.example.beecanteen.domain.model.user.Category
import com.example.beecanteen.presentation.ui.theme.BeeCanteenTheme


@Composable
fun RoleCard(
    icon: ImageVector,
    role: String,
    description: String,
    actionLabel: String,
    onActionClick: () -> Unit,
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = role
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center
                )

            Spacer(Modifier.height(12.dp))

            CanteenButton(actionLabel, onClick = onActionClick)
        }
    }
}

@Composable
fun VoteCard(
    category: Category,
    beverages: List<Beverage>,
    selectedVote: String?,
    onVote: (String) -> Unit,
    onCancelVote: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = category.name,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            beverages.forEach { beverage ->

                BeverageVoteRow(
                    beverage = beverage,
                    isSelected = beverage.id == selectedVote,
                    voteDisabled = selectedVote != null,
                    onVoteClick = { onVote(beverage.id) }
                )

                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = selectedVote != null,
                onClick = onCancelVote
            ) {
                Text("Cancel Vote")
            }
        }
    }
}

@Composable
fun BeverageVoteRow(
    beverage: Beverage,
    isSelected: Boolean,
    voteDisabled: Boolean,
    onVoteClick: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = beverage.name,
            modifier = Modifier.weight(1f)
        )

        VoteCountBadge(beverage.voteCount)

        Spacer(Modifier.width(8.dp))

        Button(
            enabled = !voteDisabled,
            onClick = onVoteClick
        ) {
            Text("Vote")
        }
    }
}


@Preview(showBackground =  true)
@Composable
fun RoleCardPreview() {
    BeeCanteenTheme {
        RoleCard(
            icon = Icons.Default.AdminPanelSettings,
            role = "Admin Portal",
            description = "Manage Menu Items, view Voting results, and configure settings",
            actionLabel = "Login As Admin",
            onActionClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun VoteCardPreview() {
    BeeCanteenTheme {
        var selectedVote by remember { mutableStateOf<String?>(null) }
        VoteCard(
            category = categories[0],
            beverages = beverages,
            selectedVote = selectedVote,
            onVote = { beverageId ->

                if (selectedVote != null) {
//                    downVote(selectedVote!!)
                }

//                upVote(beverageId)

                selectedVote = beverageId
            },
            onCancelVote = {

                selectedVote?.let {
//                    downVote(it)
                }

                selectedVote = null
            }
        )
    }
}
