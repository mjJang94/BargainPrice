package com.mj.domain.usecase.search

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.base.FlowUseCase
import kotlinx.coroutines.flow.Flow

class GetRecentQueriesUseCase(
    private val repository: DataRepository
) : FlowUseCase<Set<String>>() {
    
    override fun execute(): Flow<Set<String>> =
        repository.getRecentSearchQueriesFlow()
}