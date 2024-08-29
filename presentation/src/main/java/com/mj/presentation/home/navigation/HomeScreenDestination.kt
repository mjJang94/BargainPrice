package com.mj.presentation.home.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mj.core.theme.white
import com.mj.presentation.home.HomeContract
import com.mj.presentation.home.HomeScreen
import com.mj.presentation.home.HomeViewModel

@Composable
fun HomeScreenDestination(
    viewModel: HomeViewModel = hiltViewModel(),
    onGoToDetail: (String) -> Unit,
) {
    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { effect ->
            when(effect) {
                is HomeContract.Effect.Navigation.ToDetail -> onGoToDetail(effect.productId)
            }
        },
    )
}