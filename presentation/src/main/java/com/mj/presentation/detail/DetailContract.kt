package com.mj.presentation.detail

import com.mj.core.base.ViewEvent
import com.mj.core.base.ViewSideEffect
import com.mj.core.base.ViewState
import com.mj.domain.model.Shopping
import kotlinx.coroutines.flow.StateFlow

class DetailContract {

    sealed class Event : ViewEvent {
        data object Back : Event()
        data class MallClick(val link: String): Event()
    }

    data class State(
        val shoppingInfo: StateFlow<Shopping?>,
        val recordPrices: StateFlow<List<Long>>,
        val recordTimes: StateFlow<List<Long>>,
    ) : ViewState

    sealed class Effect: ViewSideEffect {
        sealed class Navigation: Effect() {
            data object Back: Navigation()
            data class OpenLink(val link: String): Navigation()
        }
    }
}