package com.mj.home

import androidx.paging.PagingData
import com.mj.core.base.ViewEvent
import com.mj.core.base.ViewSideEffect
import com.mj.core.base.ViewState
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.flow.StateFlow

class HomeContract {

    sealed class Event : ViewEvent {
        data class QueryChange(val query: String) : Event()
        data object Retry : Event()
        data object SearchClick : Event()
        data object DataLoaded : Event()
        data class ItemClick(val item: ShoppingData) : Event()
    }

    data class State(
        val query: StateFlow<String>,
        val shoppingInfo: StateFlow<PagingData<ShoppingData>>,
        val isLoading: Boolean,
        val isError: Boolean,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object EmptyQuery : Effect()

        sealed class Navigation : Effect() {
            data class ToDetail(val url: String) : Navigation()
        }
    }
}