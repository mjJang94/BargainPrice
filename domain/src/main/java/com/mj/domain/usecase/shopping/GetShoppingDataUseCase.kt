package com.mj.domain.usecase.shopping

import androidx.paging.PagingData
import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.ShoppingData
import com.mj.domain.usecase.base.ConsumerUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class GetShoppingDataUseCase (
    private val repository: DataRepository
) : ConsumerUseCase<String, Flow<PagingData<ShoppingData>>>() {

    override suspend fun execute(param: String): Flow<PagingData<ShoppingData>> {
        Timber.d("execute($param)")
        return repository.getShoppingList(param)
    }
}