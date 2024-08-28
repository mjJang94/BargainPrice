package com.mj.domain.usecase.base

import kotlinx.coroutines.flow.Flow
import timber.log.Timber

abstract class ConsumerFlowUseCase<in P, out R> {
    operator fun invoke(param: P): Flow<R> {
        val name = this::class.java.simpleName
        Timber.d(name, "invoke()")
        return execute(param)
    }

    protected abstract fun execute(param: P): Flow<R>

}
