package com.mj.presentation.detail.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.mj.presentation.detail.DetailScreen
import com.mj.presentation.detail.DetailViewModel

@Composable
fun DetailScreenDestination (
    productId: String,
    viewModel: DetailViewModel = hiltViewModel(),
    navController: NavHostController,
) {

    viewModel.configure(productId)

    DetailScreen()
}