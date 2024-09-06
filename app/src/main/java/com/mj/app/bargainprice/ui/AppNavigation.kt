package com.mj.app.bargainprice.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.mj.app.bargainprice.ui.Navigation.Routes.SEARCH
import com.mj.app.bargainprice.ui.event.HoistingEventController
import com.mj.presentation.detail.navigation.DetailScreenDestination
import com.mj.presentation.home.navigation.HomeScreenDestination
import com.mj.presentation.login.navigation.LoginScreenDestination
import com.mj.presentation.search.navigation.SearchScreenDestination
import kotlinx.coroutines.flow.Flow

@Composable
fun AppNavigation(
    proceedFlow: Flow<Boolean>,
    navController: NavHostController,
    hoistingEventController: HoistingEventController,
) {

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = LOGIN,
    ) {

        composable(
            route = LOGIN,
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)) },
            ) {
            LoginScreenDestination(
                proceedFlow = proceedFlow,
                onAuthenticate = hoistingEventController.authenticate,
                onSkipRequest = hoistingEventController.skipLogin,
                onProceed = {
                    navController.navigate(route = HOME) {
                        popUpTo(LOGIN) { inclusive = true }
                    }
                },
            )
        }

        composable(route = HOME) {
            HomeScreenDestination(
                onGoToDetail = { productId -> navController.navigate(route = "$DETAIL/${productId}") },
                onGoToSearch = { navController.navigate(route = SEARCH) }
            )
        }

        composable(
            route = SEARCH,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, animationSpec = tween(500)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(500)) },
        ) {
            SearchScreenDestination(
                onGoToHome = {
                    navController.navigate(route = HOME) {
                        popUpTo(SEARCH) { inclusive = true }
                    }
                }
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
        const val SEARCH = "SEARCH"
    }
}