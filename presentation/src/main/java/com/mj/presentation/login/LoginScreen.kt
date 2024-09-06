package com.mj.presentation.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mj.core.base.SIDE_EFFECTS_KEY
import com.mj.core.common.compose.Progress
import com.mj.core.theme.BargainPriceTheme
import com.mj.core.theme.Typography
import com.mj.core.theme.gray
import com.mj.core.theme.green_500
import com.mj.core.theme.white
import com.mj.presentation.R
import com.mj.presentation.login.LoginContract.Effect
import com.mj.presentation.login.LoginContract.Event
import com.mj.presentation.login.LoginContract.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

    val showLogin by state.showLogin.collectAsStateWithLifecycle()

    LaunchedEffect(SIDE_EFFECTS_KEY) {
        effectFlow?.onEach { effect ->
            onNavigationRequested(effect)
        }?.collect()
    }

    Column(modifier = modifier) {
        if (showLogin) {
            LoginContent(
                onLoginClick = { onEventSent(Event.Login) },
                onSkipClick = { onEventSent(Event.Skip) },
            )
        } else {
            Progress()
        }
    }
}

@Composable
private fun LoginContent(
    onLoginClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LoginTitle()

        Spacer(modifier = Modifier.height(40.dp))

        ActionButtons(
            onLoginClick = onLoginClick,
            onSkipClick = onSkipClick,
        )
    }
}

@Composable
private fun LoginTitle() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .border(1.dp, gray, RoundedCornerShape(18.dp))
                    .padding(10.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.baseline_shopping_basket_24),
                    contentDescription = ""
                )
            }
            Text(
                modifier = Modifier.padding(start = 5.dp),
                text = "Bargain",
                style = Typography.titleLarge,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "간편하게 로그인 하세요",
            style = Typography.titleMedium
        )
    }
}

@Composable
private fun ActionButtons(
    onLoginClick: () -> Unit,
    onSkipClick: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
        Image(
            modifier = Modifier
                .width(175.dp)
                .wrapContentHeight()
                .clickable(onClick = onLoginClick),
            painter = painterResource(id = R.drawable.naver_white_login),
            contentDescription = ""
        )

        Text(
            modifier = Modifier
                .width(175.dp)
                .wrapContentHeight()
                .border(1.dp, green_500, RoundedCornerShape(3.dp))
                .clickable(onClick = onSkipClick)
                .padding(10.dp),
            text = "비회원 이용하기",
            textAlign = TextAlign.Center,
            color = green_500,
        )
    }
}

@Composable
@Preview
private fun LoginScreenPreview() {
    BargainPriceTheme {
        LoginScreen(
            modifier = Modifier
                .fillMaxSize()
                .background(white),
            state = State(
                showLogin = MutableStateFlow(false),
            ),
            effectFlow = null,
            onEventSent = { },
            onNavigationRequested = {}
        )
    }
}