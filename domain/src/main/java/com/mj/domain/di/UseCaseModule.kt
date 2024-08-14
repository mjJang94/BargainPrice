package com.mj.domain.di

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.shopping.GetShoppingDataUseCase
import com.mj.domain.usecase.shopping.CombinedShoppingUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideShoppingUseCase(
        repository: DataRepository
    ) = CombinedShoppingUseCases(
        getShoppingData = GetShoppingDataUseCase(repository)
    )
}