package com.mj.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mj.core.base.BaseViewModel
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.shopping.CombinedShoppingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val combinedShoppingUseCases: CombinedShoppingUseCases
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    override fun setInitialState() = HomeContract.State(
        query = MutableStateFlow(""),
        shoppingInfo = MutableStateFlow(PagingData.empty()),
        isLoading = false,
        isError = false,
    )

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.QueryChange -> queryChange(event.query)
            is HomeContract.Event.SearchClick -> getShoppingItems()
            is HomeContract.Event.Retry -> getShoppingItems()
            is HomeContract.Event.DataLoaded -> setState { copy(isLoading = false) }
            is HomeContract.Event.ItemClick -> Timber.d("item click = ${event.item}")
        }
    }

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private fun queryChange(query: String) {
        _query.value = query
        setState { copy(query = _query) }
    }

    private val _shoppingInfo: MutableStateFlow<PagingData<ShoppingData>> = MutableStateFlow(PagingData.empty())

    private fun getShoppingItems() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }
            val query = _query.value
            combinedShoppingUseCases.getShoppingData(dispatcher = Dispatchers.IO, param = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                //https://medium.com/@vagabond95/paging-3-%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%81%ED%83%9C-%EB%B3%80%EA%B2%BD-15400cedf423
                //.combine(_shoppingInfo) {}
//                .catch { e -> Timber.e("error = $e") }
                .collect {
                    _shoppingInfo.emit(it)
                    setState { copy(shoppingInfo = _shoppingInfo) }
                }
        }
    }
}