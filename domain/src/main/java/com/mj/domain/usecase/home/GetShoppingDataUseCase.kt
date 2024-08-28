package com.mj.domain.usecase.home

import androidx.paging.PagingData
import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.Shopping
import com.mj.domain.usecase.base.ConsumerUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetShoppingDataUseCase (
    private val repository: DataRepository
) : ConsumerUseCase<String, Flow<PagingData<Shopping>>>() {

    override suspend fun execute(param: String): Flow<PagingData<Shopping>> {
        Timber.d("execute($param)")
        return repository.getShoppingList(param)
    }
}