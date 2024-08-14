package com.mj.domain.usecase.base

import kotlinx.coroutines.flow.Flow
import timber.log.Timber

abstract class FlowUseCase<out R> {
    operator fun invoke(): Flow<R> {
        val name = this::class.java.simpleName
        Timber.d(name, "invoke()")
        return execute()
    }

    protected abstract fun execute(): Flow<R>

}
