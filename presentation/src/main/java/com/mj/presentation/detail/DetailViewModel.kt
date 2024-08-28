package com.mj.presentation.detail

import androidx.lifecycle.viewModelScope
import com.mj.core.base.BaseViewModel
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val combinedDetailUseCases: CombinedDetailUseCases
) : BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    override fun setInitialState() = DetailContract.State(
        shoppingInfo = MutableStateFlow(null)
    )

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            DetailContract.Event.Back -> Timber.d("back clicked")
        }
    }

    fun configure(productId: String) {
        viewModelScope.launch {
            Timber.d("productId = $productId")
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