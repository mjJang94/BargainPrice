package com.mj.presentation.detail

import androidx.lifecycle.viewModelScope
import com.mj.core.base.BaseViewModel
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import com.mj.presentation.detail.DetailContract.Effect
import com.mj.presentation.detail.DetailContract.Event
import com.mj.presentation.detail.DetailContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val combinedDetailUseCases: CombinedDetailUseCases
) : BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State(
        shoppingInfo = MutableStateFlow(null)
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Back -> setEffect { Effect.Navigation.Back }
            is Event.MallClick -> setEffect { Effect.Navigation.OpenLink(event.link) }
        }
    }

    fun configure(productId: String) {
        viewModelScope.launch {
            getShoppingItem(productId)
        }
    }

    private val _shoppingInfo = MutableStateFlow<Shopping?>(null)
    private suspend fun getShoppingItem(productId: String) {
        combinedDetailUseCases.getFavoriteShoppingItemUseCase(param = productId)
            .flowOn(Dispatchers.IO)
            .collect {
                _shoppingInfo.emit(it)
                setState { copy(shoppingInfo = _shoppingInfo) }
            }
    }
}