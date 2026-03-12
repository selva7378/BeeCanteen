package com.example.beecanteen.presentation.ui.screen.voting

import android.R.attr.name
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.beecanteen.presentation.designsystem.GreetingHeader
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.format

@Composable
fun VotingScreen(
    viewModel: VotingViewModel = hiltViewModel(),
    name: String,
    modifier: Modifier = Modifier
) {
    GreetingHeader(
        name = name,
        date = remember { viewModel.todayFormatted() },
        modifier = modifier
    )
}

