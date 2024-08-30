package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase

class DeleteRecordPriceUseCase(
    private val repository: DataRepository
) : ActionUseCase<String>() {
    
    override suspend fun execute(param: String) {
        repository.deleteRecordPriceItem(param)
    }
}