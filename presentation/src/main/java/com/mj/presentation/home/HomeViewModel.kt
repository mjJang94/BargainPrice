package com.mj.presentation.home

import androidx.lifecycle.viewModelScope
import com.mj.core.common.compose.removeHtmlTag
import com.mj.core.ktx.calculatePercentageDifferenceOrNull
import com.mj.core.ktx.toLongSafety
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.home.CombinedHomeUseCases
import com.mj.presentation.base.BaseViewModel
import com.mj.presentation.home.HomeContract.Effect
import com.mj.presentation.home.HomeContract.Event
import com.mj.presentation.home.HomeContract.State
import com.mj.presentation.home.HomeViewModel.PriceState.Decrease
import com.mj.presentation.home.HomeViewModel.PriceState.Increase
import com.mj.presentation.home.HomeViewModel.PriceState.None
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val combinedHomeUseCases: CombinedHomeUseCases
) : BaseViewModel<Event, State, Effect>() {

    companion object {
        private const val MAX_RECENT_QUERY_COUNT = 10
    }

    override fun setInitialState() = State(
        priceAlarmActivated = MutableStateFlow(false),
        favoriteFavoriteItems = MutableStateFlow(emptyList()),
        refreshTime = MutableStateFlow(0L)
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.AlarmActive -> setAlarmActive(event.active)
            is Event.DeleteFavorite -> deleteFavoriteItem(event.id)
            is Event.ItemClick -> setEffect { Effect.Navigation.ToDetail(event.id) }
            is Event.SearchClick -> setEffect { Effect.Navigation.ToSearch }
        }
    }

    private val _favoriteShoppingInfo: MutableStateFlow<List<FavoriteItem>> = MutableStateFlow(emptyList())

    fun getFavoriteShoppingItems() {
        viewModelScope.launch {
            combinedHomeUseCases.getFavoriteShoppingData()
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect {
                    _favoriteShoppingInfo.emit(it.formalize())
                    setState { copy(favoriteFavoriteItems = _favoriteShoppingInfo) }
                }
        }
    }

    private fun deleteFavoriteItem(productId: String) {
        viewModelScope.launch {
            Timber.d("productId = $productId")
            combinedHomeUseCases.deleteFavoriteShoppingData(
                dispatcher = Dispatchers.IO,
                param = productId
            )
        }
    }

    fun getAlarmActivation() {
        viewModelScope.launch {
            combinedHomeUseCases.getAlarmActive()
                .flowOn(Dispatchers.IO)
                .distinctUntilChanged()
                .collect {
                    setState { copy(priceAlarmActivated = MutableStateFlow(it)) }
                }
        }
    }

    private fun setAlarmActive(active: Boolean) {
        viewModelScope.launch {
            combinedHomeUseCases.setAlarmActive(
                dispatcher = Dispatchers.IO,
                param = active
            )
        }
    }

    fun getRefreshTime() {
        viewModelScope.launch {
            combinedHomeUseCases.getRefreshTime()
                .flowOn(Dispatchers.IO)
                .collect {
                    setState { copy(refreshTime = MutableStateFlow(it)) }
                }
        }
    }

    data class FavoriteItem(
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

    private fun List<Shopping>.formalize(): List<FavoriteItem> = this.map {
        FavoriteItem(
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
    sealed interface PriceState {
        data class Increase(val difference: String?) : PriceState
        data class Decrease(val difference: String?) : PriceState
        data object None : PriceState
    }
}