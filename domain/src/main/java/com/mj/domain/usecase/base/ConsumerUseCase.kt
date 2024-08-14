package com.mj.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class ConsumerUseCase<in P, out R> {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher, param: P): R {
        val name = this::class.java.simpleName
        return withContext(dispatcher) {
            Timber.d(name, "invoke($param)")
            execute(param)
        }
    }

    protected abstract suspend fun execute(param: P): R
}