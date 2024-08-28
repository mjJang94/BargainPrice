package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase

class SetAlarmActiveUseCase(
    private val repository: DataRepository
) : ActionUseCase<Boolean>() {

    override suspend fun execute(param: Boolean) =
        repository.setAlarmActive(param)
}