package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetRefreshTimeUseCase(
    private val repository: DataRepository
) : FlowUseCase<Long>() {

    override fun execute(): Flow<Long> =
        repository.getRefreshTimeFlow()
}