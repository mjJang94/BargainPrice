package com.mj.presentation.search.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mj.core.theme.white
import com.mj.presentation.search.SearchContract
import com.mj.presentation.search.SearchScreen
import com.mj.presentation.search.SearchViewModel

@Composable
fun SearchScreenDestination(
    viewModel: SearchViewModel = hiltViewModel(),
    onGoToHome: () -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.getRecentQueries()
    }

    SearchScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { effect ->
            when (effect) {
                SearchContract.Effect.Navigation.ToMain -> onGoToHome()
            }
        }
    )
}