package com.mj.domain.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber

abstract class ProviderUseCase<out R> {
    suspend operator fun invoke(dispatcher: CoroutineDispatcher): R {
        val name = this::class.java.simpleName
        return withContext(dispatcher) {
            Timber.d(name, "invoke()")
            execute()
        }
    }

    protected abstract suspend fun execute(): R
}