package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetFavoriteShoppingUseCase(
    private val repository: DataRepository
) : FlowUseCase<List<Shopping>>() {

    override fun execute(): Flow<List<Shopping>>{
        Timber.d("execute()")
        return repository.shoppingFlow()
    }
}