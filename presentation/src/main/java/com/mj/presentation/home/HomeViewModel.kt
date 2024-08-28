package com.mj.presentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mj.core.base.BaseViewModel
import com.mj.core.common.compose.removeHtmlTag
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.home.CombinedShoppingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val combinedShoppingUseCases: CombinedShoppingUseCases
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    companion object {
        private const val MAX_RECENT_QUERY_COUNT = 10
    }

    init {
        getFavoriteShoppingItems()
        getRecentQueries()
        getAlarmActivation()
        getRefreshTime()
    }

    override fun setInitialState() = HomeContract.State(
        changedQuery = MutableStateFlow(""),
        priceAlarmActivated = MutableStateFlow(false),
        shoppingItems = MutableStateFlow(PagingData.empty()),
        recentQueries = MutableStateFlow(emptyList()),
        favoriteShoppingItems = MutableStateFlow(emptyList()),
        refreshTime = MutableStateFlow(0L)
    )

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.QueryChange -> queryChange(event.query)
            is HomeContract.Event.SearchClick -> getShoppingItems()
            is HomeContract.Event.RecentQueryClick -> queryChange(event.query)
            is HomeContract.Event.Retry -> getShoppingItems()
            is HomeContract.Event.AlarmActive -> setAlarmActive(event.active)
            is HomeContract.Event.AddFavorite -> addFavoriteItem(event.item)
            is HomeContract.Event.DeleteFavorite -> deleteFavoriteItem(event.id)
            is HomeContract.Event.ItemClick -> setEffect { HomeContract.Effect.Navigation.ToDetail(event.id) }
        }
    }

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private fun queryChange(query: String) {
        _query.value = query
        setState { copy(changedQuery = _query) }
    }

    private val _shoppingInfo: MutableStateFlow<PagingData<ShoppingItem>> = MutableStateFlow(PagingData.empty())
    private val _favoriteShoppingInfo: MutableStateFlow<List<ShoppingItem>> = MutableStateFlow(emptyList())
    private val _alarmActive: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _recentQueries: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    private val _recentRefreshTime: MutableStateFlow<Long> = MutableStateFlow(0L)

    private fun getShoppingItems() {
        viewModelScope.launch {
            val query = _query.value
            setRecentQueries(query)

            combinedShoppingUseCases.getShoppingData(dispatcher = Dispatchers.IO, param = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                .combine(_favoriteShoppingInfo) { remote, favorite ->
                    remote.formalize(favorite)
                }
                .collect {
                    _shoppingInfo.emit(it)
                    setState { copy(shoppingItems = _shoppingInfo) }
                }
        }
    }

    private fun setRecentQueries(query: String) {
        viewModelScope.launch {
            val lastQueries = (combinedShoppingUseCases.getRecentQueriesUseCase().firstOrNull() ?: emptySet())
            val mutableQueries = lastQueries.toMutableSet().apply {
                when (MAX_RECENT_QUERY_COUNT > size) {
                    true -> add(query)
                    else -> remove(last())
                }
            }
            combinedShoppingUseCases.setRecentQueriesUseCase(
                dispatcher = Dispatchers.IO,
                param = mutableQueries
            )
        }
    }

    private fun getFavoriteShoppingItems() {
        viewModelScope.launch {
            combinedShoppingUseCases.getFavoriteShoppingData()
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect {
                    _favoriteShoppingInfo.emit(it.formalize())
                    setState { copy(favoriteShoppingItems = _favoriteShoppingInfo) }
                }
        }
    }

    private fun addFavoriteItem(item: ShoppingItem) {
        viewModelScope.launch {
            combinedShoppingUseCases.insertFavoriteShoppingData(
                dispatcher = Dispatchers.IO,
                param = item.translate()
            )
        }
    }

    private fun deleteFavoriteItem(productId: String) {
        viewModelScope.launch {
            combinedShoppingUseCases.deleteFavoriteShoppingData(
                dispatcher = Dispatchers.IO,
                param = productId
            )
        }
    }

    private fun getAlarmActivation() {
        viewModelScope.launch {
            combinedShoppingUseCases.getAlarmActiveUseCase()
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect {
                    _alarmActive.emit(it)
                    setState { copy(priceAlarmActivated = _alarmActive) }
                }
        }
    }

    private fun setAlarmActive(active: Boolean) {
        viewModelScope.launch {
            combinedShoppingUseCases.setAlarmActiveUseCase(
                dispatcher = Dispatchers.IO,
                param = active
            )
        }
    }

    private fun getRecentQueries() {
        viewModelScope.launch {
            combinedShoppingUseCases.getRecentQueriesUseCase()
                .flowOn(Dispatchers.IO)
                .collect {
                    _recentQueries.emit(it.toList())
                    setState { copy(recentQueries = _recentQueries) }
                }
        }
    }

    private fun getRefreshTime() {
        viewModelScope.launch {
            combinedShoppingUseCases.getRefreshTimeUseCase()
                .flowOn(Dispatchers.IO)
                .collect {
                    _recentRefreshTime.emit(it)
                    setState { copy(refreshTime = _recentRefreshTime) }
                }
        }
    }

    data class ShoppingItem(
        val title: String,
        val link: String,
        val image: String,
        val lowestPrice: String,
        val highestPrice: String,
        val prevLowestPrice: String,
        val prevHighestPrice: String,
        val mallName: String,
        val productId: String,
        val productType: String,
        val maker: String,
        val brand: String,
        val category1: String,
        val category2: String,
        val category3: String,
        val category4: String,
        val isFavorite: Boolean,
        val isRefreshFail: Boolean,
    )

    private fun PagingData<Shopping>.formalize(favorite: List<ShoppingItem>): PagingData<ShoppingItem> = this.map {
        ShoppingItem(
            title = it.title.removeHtmlTag(),
            link = it.link,
            image = it.image,
            lowestPrice = it.lowestPrice,
            highestPrice = it.highestPrice,
            prevLowestPrice = it.prevLowestPrice,
            prevHighestPrice = it.prevHighestPrice,
            mallName = it.mallName,
            productId = it.productId,
            productType = it.productType,
            maker = it.maker,
            brand = it.brand,
            category1 = it.category1,
            category2 = it.category2,
            category3 = it.category3,
            category4 = it.category4,
            isFavorite = favorite.any { favorite -> it.productId == favorite.productId },
            isRefreshFail = false,
        )
    }

    private fun List<Shopping>.formalize(): List<ShoppingItem> = this.map {
        ShoppingItem(
            title = it.title.removeHtmlTag(),
            link = it.link,
            image = it.image,
            lowestPrice = it.lowestPrice,
            highestPrice = it.highestPrice,
            prevLowestPrice = it.prevLowestPrice,
            prevHighestPrice = it.prevHighestPrice,
            mallName = it.mallName,
            productId = it.productId,
            productType = it.productType,
            maker = it.maker,
            brand = it.brand,
            category1 = it.category1,
            category2 = it.category2,
            category3 = it.category3,
            category4 = it.category4,
            isFavorite = true,
            isRefreshFail = it.isRefreshFail,
        )
    }

    private fun ShoppingItem.translate(): Shopping = Shopping(
        title = title.removeHtmlTag(),
        link = link,
        image = image,
        lowestPrice = lowestPrice,
        highestPrice = highestPrice,
        prevLowestPrice = prevLowestPrice,
        prevHighestPrice = prevHighestPrice,
        mallName = mallName,
        productId = productId,
        productType = productType,
        maker = maker,
        brand = brand,
        category1 = category1,
        category2 = category2,
        category3 = category3,
        category4 = category4,
        isRefreshFail = false,
    )
}