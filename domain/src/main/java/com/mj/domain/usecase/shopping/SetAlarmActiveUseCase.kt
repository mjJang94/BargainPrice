package com.mj.domain.usecase.shopping

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase
import timber.log.Timber

class SetAlarmActiveUseCase(
    private val repository: DataRepository
): ActionUseCase<Boolean>() {

    override suspend fun execute(param: Boolean) {
        Timber.d("execute($param)")
        repository.setAlarmActive(param)
    }
}