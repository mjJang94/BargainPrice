package com.mj.domain.usecase.search

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetFavoriteListUseCase(
    private val repository: DataRepository
) : FlowUseCase<List<Shopping>>() {

    override fun execute(): Flow<List<Shopping>> =
        repository.shoppingFlow()
}