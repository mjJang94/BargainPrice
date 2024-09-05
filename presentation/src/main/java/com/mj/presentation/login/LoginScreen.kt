package com.mj.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.viewinterop.AndroidView
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.presentation.R
import com.mj.presentation.login.LoginContract.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    state: State,
    effectFlow: Flow<Effect>?,
    onEventSent: (event: Event) -> Unit,
    onNavigationRequested: (effect: Effect) -> Unit,
) {

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            when(effect) {
                is Effect.Login -> onNavigationRequested(effect)
            }
        }?.collect()
    }

    Column(modifier = modifier) {
        LoginContent(
            onLoginClick = { onEventSent(Event.Login)}
        )
    }
}

@Composable
fun LoginContent(
    onLoginClick: () -> Unit,
){
    Column {
        Image(
            modifier = Modifier.clickable(onClick = onLoginClick),
            painter = painterResource(id = R.drawable.naver_white_login),
            contentDescription = ""
        )
    }
}