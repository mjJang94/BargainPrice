package com.mj.presentation.search

import androidx.paging.PagingData
import com.mj.presentation.base.ViewEvent
import com.mj.presentation.base.ViewSideEffect
import com.mj.presentation.base.ViewState
import com.mj.presentation.search.SearchViewModel.ShoppingItem
import kotlinx.coroutines.flow.StateFlow

class SearchContract {

    sealed class Event : ViewEvent {
        data class QueryChange(val query: String) : Event()
        data object Retry : Event()
        data object SearchClick : Event()
        data class DeleteFavorite(val id: String) : Event()
        data class AddFavorite(val item: ShoppingItem) : Event()
        data class RecentQueryClick(val query: String) : Event()
        data class DeleteQuery(val query: String) : Event()
        data class ItemClick(val id: String) : Event()
    }

    data class State(
        val query: StateFlow<String>,
        val recentQueries: StateFlow<List<String>>,
        val shoppingItems: StateFlow<PagingData<ShoppingItem>>,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            data object ToMain : Navigation()
        }
    }
}