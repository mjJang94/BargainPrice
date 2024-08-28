package com.mj.domain.usecase.detail

import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.RecordPrice
import com.mj.domain.usecase.base.ConsumerFlowUseCase
import kotlinx.coroutines.flow.Flow

class GetRecordPriceUseCase(
    private val repository: DataRepository
) : ConsumerFlowUseCase<Triple<String, Long, Long>, List<RecordPrice>>() {

    override fun execute(param: Triple<String, Long, Long>): Flow<List<RecordPrice>> {
        val (productId, startTime, endTime) = param
        return repository.recordPriceFlow(productId, startTime, endTime)
    }
}