package com.mj.presentation.detail

import androidx.lifecycle.viewModelScope
import com.mj.core.base.BaseViewModel
import com.mj.core.ktx.Calendar
import com.mj.core.ktx.lastWeek
import com.mj.domain.model.RecordPrice
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import com.mj.presentation.detail.DetailContract.Effect
import com.mj.presentation.detail.DetailContract.Event
import com.mj.presentation.detail.DetailContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val combinedDetailUseCases: CombinedDetailUseCases
) : BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State(
        shoppingInfo = MutableStateFlow(null),
        recordPrices = MutableStateFlow(emptyList()),
        recordTimes = MutableStateFlow(emptyList()),
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Back -> setEffect { Effect.Navigation.Back }
            is Event.MallClick -> setEffect { Effect.Navigation.OpenLink(event.link) }
        }
    }

    fun configure(productId: String) {
        viewModelScope.launch {
            val shoppingItem = async { getShoppingItem(productId) }.await()
            val (prices, times) = async {
                val recordItems = getRecordPrices(productId)
                recordItems.map { it.lowestPrice.toLong() } to recordItems.map { it.timeStamp }
            }.await()
            setState { copy(shoppingInfo = MutableStateFlow(shoppingItem), recordPrices = MutableStateFlow(prices), recordTimes = MutableStateFlow(times)) }
        }
    }

    private suspend fun getShoppingItem(productId: String) =
        combinedDetailUseCases.getFavoriteShoppingItemUseCase(param = productId)
            .flowOn(Dispatchers.IO)
            .firstOrNull()

    private suspend fun getRecordPrices(productId: String): List<RecordPrice> {
        val startTime = Calendar(System.currentTimeMillis()).lastWeek().timeInMillis
        val endTime = Calendar(System.currentTimeMillis()).timeInMillis
        return combinedDetailUseCases.getRecordPriceUseCase(param = Triple(productId, startTime, endTime))
            .flowOn(Dispatchers.IO)
            .firstOrNull()
            .orEmpty()
    }
}