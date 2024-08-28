package com.mj.app.bargainprice.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mj.app.bargainprice.ui.Navigation.Args.FAVORITE_PRODUCT_ID
import com.mj.app.bargainprice.ui.Navigation.Routes.DETAIL
import com.mj.app.bargainprice.ui.Navigation.Routes.HOME
import com.mj.presentation.detail.navigation.DetailScreenDestination
import com.mj.presentation.home.navigation.HomeScreenDestination

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = HOME,
    ) {
        composable(route = HOME) {
            HomeScreenDestination(navController = navController)
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
                    navController = navController,
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
        const val HOME = "HOME"
        const val DETAIL = "DETAIL"
    }
}