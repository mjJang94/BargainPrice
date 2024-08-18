package com.mj.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import com.mj.core.base.BaseViewModel
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.shopping.CombinedShoppingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
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
    )

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.QueryChange -> queryChange(event.query)
            is HomeContract.Event.SearchClick -> getShoppingItems()
            is HomeContract.Event.Retry -> getShoppingItems()
            is HomeContract.Event.AddFavorite -> addFavoriteItem(event.item)
            is HomeContract.Event.ItemClick -> Timber.d("item click = ${event.item}")
        }
    }

    private val _query: MutableStateFlow<String> = MutableStateFlow("")
    private fun queryChange(query: String) {
        _query.value = query
        setState { copy(query = _query) }
    }

    private val _shoppingInfo: MutableStateFlow<PagingData<ShoppingItem>> = MutableStateFlow(PagingData.empty())

    private fun getShoppingItems() {
        viewModelScope.launch {
            val query = _query.value
            combinedShoppingUseCases.getShoppingData(dispatcher = Dispatchers.IO, param = query)
                .distinctUntilChanged()
                .cachedIn(viewModelScope)
                //https://medium.com/@vagabond95/paging-3-%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%81%ED%83%9C-%EB%B3%80%EA%B2%BD-15400cedf423
                .combine(combinedShoppingUseCases.getFavoriteShoppingData()) { remote, favorite ->
                    remote.formalize(favorite)
                }
                .collect {
                    _shoppingInfo.emit(it)
                    setState { copy(shoppingInfo = _shoppingInfo) }
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

    data class ShoppingItem(
        val title: String,
        val link: String,
        val image: String,
        val lowestPrice: String,
        val highestPrice: String,
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

    private fun PagingData<ShoppingData>.formalize(favorite: List<ShoppingData>): PagingData<ShoppingItem> = this.map {
        ShoppingItem(
            title = it.title,
            link = it.link,
            image = it.image,
            lowestPrice = it.lowestPrice,
            highestPrice = it.highestPrice,
            mallName = it.mallName,
            productId = it.productId,
            productType = it.productType,
            maker = it.maker,
            brand = it.brand,
            category1 = it.category1,
            category2 = it.category2,
            category3 = it.category3,
            category4 = it.category4,
            isFavorite = favorite.any { favorite -> it.productId == favorite.productId }
        )
    }

    private fun ShoppingItem.translate(): ShoppingData = ShoppingData(
        title = title,
        link = link,
        image = image,
        lowestPrice = lowestPrice,
        highestPrice = highestPrice,
        mallName = mallName,
        productId = productId,
        productType = productType,
        maker = maker,
        brand = brand,
        category1 = category1,
        category2 = category2,
        category3 = category3,
        category4 = category4,
    )
}