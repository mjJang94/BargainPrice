package com.mj.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mj.core.base.BaseViewModel
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.shopping.CombinedShoppingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val combinedShoppingUseCases: CombinedShoppingUseCases
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun setInitialState() = HomeContract.State(
        shoppingInfo = MutableStateFlow(PagingData.empty()),
        isLoading = false,
        isError = false,
    )

    override fun handleEvents(event: HomeContract.Event) {
        Timber.d("event = $event")
        when (event) {
            is HomeContract.Event.SearchClick -> getShoppingItems(event.query)
            is HomeContract.Event.Retry -> getShoppingItems(event.query)
            is HomeContract.Event.ItemClick -> Timber.d("item click = ${event.item}")
        }
    }

    private val _shoppingInfo: MutableStateFlow<PagingData<ShoppingData>> = MutableStateFlow(PagingData.empty())

    private fun getShoppingItems(query: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            combinedShoppingUseCases.getShoppingData(dispatcher = Dispatchers.IO, param = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                //https://medium.com/@vagabond95/paging-3-%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%81%ED%83%9C-%EB%B3%80%EA%B2%BD-15400cedf423
                //.combine(_shoppingInfo) {}
                .collect {
                    _shoppingInfo.emit(it)
                    setState { copy(shoppingInfo = _shoppingInfo, isLoading = false) }
                    setEffect { HomeContract.Effect.DataLoaded }
                }
        }
    }
}