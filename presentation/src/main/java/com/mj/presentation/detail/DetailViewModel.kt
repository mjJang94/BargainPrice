package com.mj.presentation.detail

import androidx.lifecycle.viewModelScope
import com.mj.core.base.BaseViewModel
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val combinedDetailUseCases: CombinedDetailUseCases
): BaseViewModel<DetailContract.Event, DetailContract.State, DetailContract.Effect>() {

    override fun setInitialState() = DetailContract.State(
        productId = ""
    )

    override fun handleEvents(event: DetailContract.Event) {
        when (event) {
            DetailContract.Event.Back -> Timber.d("back clicked")
        }
    }


    private val _productId = MutableSharedFlow<String>(replay = 1)
    fun configure(productId: String) {
        viewModelScope.launch {
            Timber.d("productId = $productId")
            _productId.emit(productId)
        }
    }
}