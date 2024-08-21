package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.ActionUseCase
import timber.log.Timber

class SetRecentQueriesUseCase(
    private val repository: DataRepository
): ActionUseCase<Set<String>>() {

    override suspend fun execute(param: Set<String>) {
        Timber.d("execute($param)")
        repository.setRecentQueries(param)
    }
}