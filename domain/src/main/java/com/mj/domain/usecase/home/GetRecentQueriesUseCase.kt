package com.mj.domain.usecase.home

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

class GetRecentQueriesUseCase(
    private val repository: DataRepository
) : FlowUseCase<Set<String>>()  {


    override fun execute(): Flow<Set<String>> {
        Timber.d("execute()")
        return repository.getRecentSearchQueriesFlow()
    }
}