package com.mj.presentation.home

import com.mj.presentation.base.ViewEvent
import com.mj.presentation.base.ViewSideEffect
import com.mj.presentation.base.ViewState
import com.mj.presentation.home.HomeViewModel.FavoriteItem
import kotlinx.coroutines.flow.StateFlow

class HomeContract {

    sealed class Event : ViewEvent {
        data class AlarmActive(val active: Boolean) : Event()
        data class DeleteFavorite(val id: String) : Event()
        data class ItemClick(val id: String) : Event()
        data object SearchClick : Event()
    }

    data class State(
        val priceAlarmActivated: StateFlow<Boolean>,
        val favoriteFavoriteItems: StateFlow<List<FavoriteItem>>,
        val refreshTime: StateFlow<Long>,
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        sealed class Navigation : Effect() {
            data class ToDetail(val productId: String) : Navigation()
            data object ToSearch : Navigation()
        }
    }
}
