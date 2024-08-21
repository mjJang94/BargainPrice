package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetAlarmActiveUseCase(
    private val repository: DataRepository
) : FlowUseCase<Boolean>()  {

    override fun execute(): Flow<Boolean> {
        Timber.d("execute()")
        return repository.getAlarmActive()
    }
}