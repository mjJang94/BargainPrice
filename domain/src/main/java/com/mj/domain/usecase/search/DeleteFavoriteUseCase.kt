package com.mj.domain.usecase.search

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase

class DeleteFavoriteUseCase(
    private val repository: DataRepository
) : ActionUseCase<String>() {
    
    override suspend fun execute(param: String) {
        repository.deleteShoppingItem(param)
    }
}