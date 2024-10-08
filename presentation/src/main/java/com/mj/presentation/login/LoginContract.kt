package com.mj.presentation.login

import com.mj.presentation.base.ViewEvent
import com.mj.presentation.base.ViewSideEffect
import com.mj.presentation.base.ViewState
import kotlinx.coroutines.flow.StateFlow

class LoginContract {

    sealed class Event: ViewEvent {
        data object Login: Event()
        data object Skip: Event()
    }

    data class State(
        val showLogin: StateFlow<Boolean>
    ): ViewState

    sealed class Effect: ViewSideEffect {
        data object Login: Effect()
        data object Skip: Effect()
    }
}