package com.mj.presentation.home

import androidx.paging.PagingData
import com.mj.core.base.ViewEvent
import com.mj.core.base.ViewSideEffect
import com.mj.core.base.ViewState
import com.mj.presentation.home.HomeViewModel.*
import kotlinx.coroutines.flow.StateFlow

class HomeContract {

    sealed class Event : ViewEvent {
        data class QueryChange(val query: String) : Event()
        data object Retry : Event()
        data object SearchClick : Event()
        data class RecentQueryClick(val query: String) : Event()
        data class AlarmActive(val active: Boolean) : Event()
        data class AddFavorite(val item: ShoppingItem) : Event()
        data class DeleteFavorite(val id: String) : Event()
        data class ItemClick(val id: String) : Event()
    }

    data class State(
        val changedQuery: StateFlow<String>,
        val priceAlarmActivated: StateFlow<Boolean>,
        val recentQueries: StateFlow<List<String>>,
        val shoppingItems: StateFlow<PagingData<ShoppingItem>>,
        val favoriteShoppingItems: StateFlow<List<ShoppingItem>>,
        val refreshTime: StateFlow<Long>,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object EmptyQuery : Effect()

        sealed class Navigation : Effect() {
            data class ToDetail(val id: String) : Navigation()
        }
    }
}
