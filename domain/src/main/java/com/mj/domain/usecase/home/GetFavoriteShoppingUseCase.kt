package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetFavoriteShoppingUseCase(
    private val repository: DataRepository
) : FlowUseCase<List<ShoppingData>>() {

    override fun execute(): Flow<List<ShoppingData>>{
        Timber.d("execute()")
        return repository.shoppingFlow()
    }
}