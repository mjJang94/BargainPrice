package com.mj.domain.usecase.login

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetSkipLoginUseCase(
    private val repository: DataRepository
): FlowUseCase<Boolean>() {

    override fun execute(): Flow<Boolean> =
        repository.getSkipLoginFlow()
}