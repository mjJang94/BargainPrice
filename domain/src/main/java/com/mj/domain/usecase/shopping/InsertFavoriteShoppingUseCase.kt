package com.mj.domain.usecase.shopping

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.base.ActionUseCase
import timber.log.Timber

class InsertFavoriteShoppingUseCase(
    private val repository: DataRepository
) : ActionUseCase<ShoppingData>() {

    override suspend fun execute(param: ShoppingData) {
        Timber.d("execute($param)")
        repository.insertShopping(param)
    }
}