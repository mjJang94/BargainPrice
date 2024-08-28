package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase

class SetRecentQueriesUseCase(
    private val repository: DataRepository
) : ActionUseCase<Set<String>>() {

    override suspend fun execute(param: Set<String>) =
        repository.setRecentQueries(param)
}