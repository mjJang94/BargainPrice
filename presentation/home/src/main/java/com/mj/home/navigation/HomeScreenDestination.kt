package com.mj.home.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mj.core.theme.white
import com.mj.home.HomeContract
import com.mj.home.HomeScreen
import com.mj.home.HomeViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun HomeScreenDestination(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
) {
    HomeScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { navigationEffect ->
            if (navigationEffect is HomeContract.Effect.Navigation.ToDetail) {
                navController.navigateToDetail(navigationEffect.url)
            }
        },
    )
}

fun NavHostController.navigateToDetail(url: String) {
    URLEncoder.encode(url, StandardCharsets.UTF_8.toString()).also { encodeUrl ->
        navigate(route = "detail/$encodeUrl")
    }
}