package com.mj.presentation.login

import com.mj.core.base.ViewEvent
import com.mj.core.base.ViewSideEffect
import com.mj.core.base.ViewState

class LoginContract {

    sealed class Event: ViewEvent {
        data object Login: Event()
    }

    data class State(
        val tt: String
    ): ViewState

    sealed class Effect: ViewSideEffect {
        data object Login: Effect()
    }
}