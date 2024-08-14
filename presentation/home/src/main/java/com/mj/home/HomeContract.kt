package com.mj.home

import androidx.paging.PagingData
import com.mj.core.base.ViewEvent
import com.mj.core.base.ViewSideEffect
import com.mj.core.base.ViewState
import com.mj.domain.model.ShoppingData
import com.mj.home.model.HomeInfo
import kotlinx.coroutines.flow.StateFlow

class HomeContract {

    sealed class Event: ViewEvent {
        data class Retry(val query: String): Event()
        data class SearchClick(val query: String): Event()
        data class ItemClick(val item: ShoppingData): Event()
    }

    data class State(
        val shoppingInfo: StateFlow<PagingData<ShoppingData>>,
        val isLoading: Boolean,
        val isError: Boolean,
    ): ViewState

    sealed class Effect: ViewSideEffect {
        data object DataLoaded: Effect()
        data object EmptyQuery: Effect()

        sealed class Navigation: Effect() {
            data class ToDetail(val url: String): Navigation()
        }
    }
}