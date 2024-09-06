package com.mj.domain.usecase.search

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.ActionUseCase

class InsertFavoriteUseCase(
    private val repository: DataRepository
) : ActionUseCase<Shopping>() {

    override suspend fun execute(param: Shopping) =
        repository.insertShoppingItem(param)
}