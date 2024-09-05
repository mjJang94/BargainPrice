package com.mj.app.bargainprice.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mj.app.bargainprice.ui.Navigation.Args.FAVORITE_PRODUCT_ID
import com.mj.app.bargainprice.ui.Navigation.Routes.DETAIL
import com.mj.app.bargainprice.ui.Navigation.Routes.HOME
import com.mj.app.bargainprice.ui.Navigation.Routes.LOGIN
import com.mj.app.bargainprice.ui.event.HoistingEventController
import com.mj.presentation.detail.navigation.DetailScreenDestination
import com.mj.presentation.home.navigation.HomeScreenDestination
import com.mj.presentation.login.navigation.LoginScreenDestination
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun AppNavigation(
    proceedFlow: Flow<Boolean>,
    navController: NavHostController,
    hoistingEventController: HoistingEventController,
) {

    LaunchedEffect(Unit) {
        proceedFlow.collect { result ->
            if (result) {
                navController.navigate(route = HOME) {
                    popUpTo(LOGIN) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = LOGIN,
    ) {

        composable(route = LOGIN) {
            LoginScreenDestination(
                onAuthenticate = hoistingEventController.authenticate,
            )
        }

        composable(route = HOME) {
            HomeScreenDestination(
                onGoToDetail = { productId -> navController.navigate(route = "$DETAIL/${productId}") }
            )
        }

        composable(
            route = "$DETAIL/{$FAVORITE_PRODUCT_ID}",
            arguments = listOf(navArgument(name = FAVORITE_PRODUCT_ID) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString(FAVORITE_PRODUCT_ID)
            if (productId == null) {
                navController.popBackStack()
            } else {
                DetailScreenDestination(
                    productId = productId,
                    onOpenLink = hoistingEventController.openLink,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

object Navigation {
    object Args {
        const val FAVORITE_PRODUCT_ID = "FAVORITE_PRODUCT_ID"
    }

    object Routes {
        const val LOGIN = "LOGIN"
        const val HOME = "HOME"
        const val DETAIL = "DETAIL"
    }
}