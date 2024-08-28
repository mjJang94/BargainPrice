package com.mj.domain.usecase.detail

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.RecordPrice
import com.mj.domain.usecase.base.ActionUseCase

class InsertRecordPriceUseCase(
    private val repository: DataRepository
) : ActionUseCase<RecordPrice>() {

    override suspend fun execute(param: RecordPrice) =
        repository.insertRecordPriceItem(param)
}