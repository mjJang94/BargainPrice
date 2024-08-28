package com.mj.domain.usecase.home

import androidx.paging.PagingData
import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.ConsumerUseCase
import kotlinx.coroutines.flow.Flow

class GetShoppingDataUseCase(
    private val repository: DataRepository
) : ConsumerUseCase<String, Flow<PagingData<Shopping>>>() {

    override suspend fun execute(param: String): Flow<PagingData<Shopping>> =
        repository.getShoppingList(param)
}