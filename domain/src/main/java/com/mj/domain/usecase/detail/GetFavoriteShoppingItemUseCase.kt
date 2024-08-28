package com.mj.domain.usecase.detail

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.ConsumerFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetFavoriteShoppingItemUseCase(
    private val repository: DataRepository
) : ConsumerFlowUseCase<String, Shopping>() {
    override fun execute(param: String): Flow<Shopping> =
        repository.getShoppingFlowById(param)
}