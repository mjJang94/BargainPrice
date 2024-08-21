package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase
import timber.log.Timber

class DeleteFavoriteShoppingUseCase(
    private val repository: DataRepository
) : ActionUseCase<String>() {
    
    override suspend fun execute(param: String) {
        Timber.d("execute($param)")
        repository.deleteShoppingItem(param)
    }
}