package com.mj.presentation.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mj.core.base.BaseViewModel
import com.mj.core.common.compose.removeHtmlTag
import com.mj.core.ktx.calculatePercentageDifferenceOrNull
import com.mj.core.ktx.toLongSafety
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.home.CombinedShoppingUseCases
import com.mj.presentation.home.HomeContract.Effect
import com.mj.presentation.home.HomeContract.Event
import com.mj.presentation.home.HomeContract.State
import com.mj.presentation.home.HomeViewModel.PriceState.Decrease
import com.mj.presentation.home.HomeViewModel.PriceState.Increase
import com.mj.presentation.home.HomeViewModel.PriceState.None
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
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
) : BaseViewModel<Event, State, Effect>() {

    companion object {
        private const val MAX_RECENT_QUERY_COUNT = 10
    }

    override fun setInitialState() = State(
        changedQuery = MutableStateFlow(""),
        priceAlarmActivated = MutableStateFlow(false),
        shoppingItems = MutableStateFlow(PagingData.empty()),
        recentQueries = MutableStateFlow(emptyList()),
        favoriteShoppingItems = MutableStateFlow(emptyList()),
        refreshTime = MutableStateFlow(0L)
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.QueryChange -> queryChange(event.query)
            is Event.SearchClick -> getShoppingItems()
            is Event.Retry -> getShoppingItems()
            is Event.AlarmActive -> setAlarmActive(event.active)
            is Event.DeleteFavorite -> deleteFavoriteItem(event.id)
            is Event.DeleteQuery -> deleteQuery(event.query)
            is Event.ItemClick -> setEffect { Effect.Navigation.ToDetail(event.id) }
            is Event.AddFavorite -> addFavoriteItem(event.item)
            is Event.RecentQueryClick -> {
                queryChange(event.query)
                getShoppingItems()
            }
        }
    }

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private fun queryChange(query: String) {
        _query.value = query
        setState { copy(changedQuery = _query) }
    }

    private val _favoriteShoppingInfo: MutableStateFlow<List<ShoppingItem>> = MutableStateFlow(emptyList())

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
                    setState { copy(shoppingItems = MutableStateFlow(it)) }
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

    fun getFavoriteShoppingItems() {
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
            Timber.d("productId = $productId")
            combinedShoppingUseCases.deleteFavoriteShoppingData(
                dispatcher = Dispatchers.IO,
                param = productId
            )
            combinedShoppingUseCases.deleteRecordPriceUseCase(
                dispatcher = Dispatchers.IO,
                param = productId
            )
        }
    }

    private fun deleteQuery(query: String) {
        viewModelScope.launch {
            combinedShoppingUseCases.getRecentQueriesUseCase().firstOrNull()?.let {
                val tempQueries = it.toMutableSet().apply { remove(query) }
                combinedShoppingUseCases.setRecentQueriesUseCase(
                    dispatcher = Dispatchers.IO,
                    param = tempQueries,
                )
            }
        }
    }

    fun getAlarmActivation() {
        viewModelScope.launch {
            combinedShoppingUseCases.getAlarmActiveUseCase()
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect {
                    setState { copy(priceAlarmActivated = MutableStateFlow(it)) }
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

    fun getRecentQueries() {
        viewModelScope.launch {
            combinedShoppingUseCases.getRecentQueriesUseCase()
                .flowOn(Dispatchers.IO)
                .collect {
                    setState { copy(recentQueries = MutableStateFlow(it.toList())) }
                }
        }
    }

    fun getRefreshTime() {
        viewModelScope.launch {
            combinedShoppingUseCases.getRefreshTimeUseCase()
                .flowOn(Dispatchers.IO)
                .collect {
                    setState { copy(refreshTime = MutableStateFlow(it)) }
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
        val priceState: PriceState?,
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
            priceState = null
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
            priceState = when {
                (it.lowestPrice.toLongSafety() - it.prevLowestPrice.toLongSafety()) > 0L -> Increase(
                    difference = calculatePercentageDifferenceOrNull(it.lowestPrice, it.prevLowestPrice)
                )

                (it.lowestPrice.toLongSafety() - it.prevLowestPrice.toLongSafety()) < 0L -> Decrease(
                    difference = calculatePercentageDifferenceOrNull(it.lowestPrice, it.prevLowestPrice)
                )

                else -> None
            }
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

    sealed interface PriceState {
        data class Increase(val difference: String?) : PriceState
        data class Decrease(val difference: String?) : PriceState
        data object None : PriceState
    }
}