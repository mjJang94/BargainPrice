package com.mj.presentation.login.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mj.core.theme.white
import com.mj.presentation.login.LoginContract
import com.mj.presentation.login.LoginScreen
import com.mj.presentation.login.LoginViewModel

@Composable
fun LoginScreenDestination(
    viewModel: LoginViewModel = hiltViewModel(),
    onAuthenticate: () -> Unit,
    onSkip: () -> Unit,
) {

    LaunchedEffect(Unit) {
        viewModel.checkRequireLogin()
    }

    LoginScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(white),
        state = viewModel.viewState.value,
        effectFlow = viewModel.effect,
        onEventSent = { event -> viewModel.setEvent(event) },
        onNavigationRequested = { effect ->
            when (effect) {
                is LoginContract.Effect.Login -> onAuthenticate()
                is LoginContract.Effect.Skip -> onSkip()
            }
        }
    )
}