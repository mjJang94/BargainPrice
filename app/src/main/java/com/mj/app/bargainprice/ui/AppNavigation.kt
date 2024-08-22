package com.mj.app.bargainprice.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mj.app.bargainprice.ui.Navigation.Args.SHOPPING_DATA
import com.mj.app.bargainprice.ui.Navigation.Routes.DETAIL
import com.mj.app.bargainprice.ui.Navigation.Routes.HOME
import com.mj.home.navigation.HomeScreenDestination

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
            route = "$DETAIL/{$SHOPPING_DATA}",
            arguments = listOf(navArgument(name = SHOPPING_DATA) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString(SHOPPING_DATA)
            if (url == null) {
                navController.popBackStack()
            } else {
//                DetailScreenDestination(
//                    url = URLDecoder.decode(url, StandardCharsets.UTF_8.toString()),
//                    navController = navController,
//                )
            }
        }
    }
}

object Navigation {
    object Args {
        const val SHOPPING_DATA = "shopping_data"
    }

    object Routes {
        const val HOME = "home"
        const val DETAIL = "detail"
    }
}