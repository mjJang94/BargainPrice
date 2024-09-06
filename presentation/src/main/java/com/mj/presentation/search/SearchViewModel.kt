package com.mj.presentation.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.mj.core.common.compose.removeHtmlTag
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.search.CombinedSearchUseCases
import com.mj.presentation.base.BaseViewModel
import com.mj.presentation.home.HomeViewModel.FavoriteItem
import com.mj.presentation.search.SearchContract.Effect
import com.mj.presentation.search.SearchContract.Event
import com.mj.presentation.search.SearchContract.State
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
class SearchViewModel @Inject constructor(
    private val combinedSearchUseCases: CombinedSearchUseCases
) : BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State(
        query = MutableStateFlow(""),
        recentQueries = MutableStateFlow(emptyList()),
        shoppingItems = MutableStateFlow(PagingData.empty()),
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.QueryChange -> queryChange(event.query)
            is Event.DeleteQuery -> deleteQuery(event.query)
            is Event.ItemClick -> {
                Timber.d("open link")
            }

            is Event.RecentQueryClick -> {
                queryChange(event.query)
                getShoppingItems()
            }

            is Event.SearchClick,
            is Event.Retry -> getShoppingItems()

            is Event.AddFavorite -> addFavoriteItem(event.item)
            is Event.DeleteFavorite -> deleteFavoriteItem(event.id)
        }
    }

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private fun queryChange(query: String) {
        _query.value = query
        setState { copy(query = _query) }
    }

    private val _favoriteShoppingInfo = combinedSearchUseCases.getFavoriteData()

    private fun getShoppingItems() {
        viewModelScope.launch {
            val query = _query.value
            setRecentQueries(query)

            combinedSearchUseCases.getShoppingData(dispatcher = Dispatchers.IO, param = query)
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

    private fun addFavoriteItem(item: ShoppingItem) {
        viewModelScope.launch {
            combinedSearchUseCases.insertFavoriteData(
                dispatcher = Dispatchers.IO,
                param = item.translate()
            )
        }
    }

    private fun deleteFavoriteItem(productId: String) {
        viewModelScope.launch {
            Timber.d("productId = $productId")
            combinedSearchUseCases.deleteFavoriteData(
                dispatcher = Dispatchers.IO,
                param = productId
            )
        }
    }

    private fun setRecentQueries(query: String) {
        viewModelScope.launch {
            val lastQueries = (combinedSearchUseCases.getRecentQueries().firstOrNull() ?: emptySet())
            val mutableQueries = lastQueries.toMutableSet().apply {
                add(query)
            }
            combinedSearchUseCases.setRecentQueries(
                dispatcher = Dispatchers.IO,
                param = mutableQueries
            )
        }
    }

    fun getRecentQueries() {
        viewModelScope.launch {
            combinedSearchUseCases.getRecentQueries()
                .flowOn(Dispatchers.IO)
                .collect {
                    setState { copy(recentQueries = MutableStateFlow(it.toList())) }
                }
        }
    }

    private fun deleteQuery(query: String) {
        viewModelScope.launch {
            combinedSearchUseCases.getRecentQueries().firstOrNull()?.let {
                val tempQueries = it.toMutableSet().apply { remove(query) }
                combinedSearchUseCases.setRecentQueries(
                    dispatcher = Dispatchers.IO,
                    param = tempQueries,
                )
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
    )

    private fun PagingData<Shopping>.formalize(favorite: List<Shopping>): PagingData<ShoppingItem> = this.map {
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