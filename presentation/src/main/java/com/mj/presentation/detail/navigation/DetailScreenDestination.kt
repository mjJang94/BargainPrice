package com.mj.presentation.detail.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mj.core.theme.white
import com.mj.presentation.detail.DetailContract
import com.mj.presentation.detail.DetailScreen
import com.mj.presentation.detail.DetailViewModel

@Composable
fun DetailScreenDestination(
    viewModel: DetailViewModel = hiltViewModel(),
    productId: String,
    onOpenLink: (String) -> Unit,
    onBack: () -> Unit,
) {

    LaunchedEffect(productId) {
        viewModel.configure(productId)
    }

    //https://issuetracker.google.com/issues/225987040?pli=1
    DetailScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSend = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { effect ->
            when (effect) {
                is DetailContract.Effect.Navigation.Back -> onBack()
                is DetailContract.Effect.Navigation.OpenLink -> onOpenLink(effect.link)
            }
        }
    )

}