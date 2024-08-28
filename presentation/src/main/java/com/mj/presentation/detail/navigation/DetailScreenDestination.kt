package com.mj.presentation.detail.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mj.core.theme.white
import com.mj.presentation.detail.DetailContract
import com.mj.presentation.detail.DetailScreen
import com.mj.presentation.detail.DetailViewModel

@Composable
fun DetailScreenDestination(
    productId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    navController: NavHostController,
) {

    viewModel.configure(productId)

    DetailScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSend = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is DetailContract.Effect.Navigation.ToMain) {
                navController.popBackStack()
            }
        }
    )
}