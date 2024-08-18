package com.mj.domain.di

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.shopping.GetShoppingDataUseCase
import com.mj.domain.usecase.shopping.CombinedShoppingUseCases
import com.mj.domain.usecase.shopping.DeleteFavoriteShoppingUseCase
import com.mj.domain.usecase.shopping.GetFavoriteShoppingUseCase
import com.mj.domain.usecase.shopping.InsertFavoriteShoppingUseCase
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
        getShoppingData = GetShoppingDataUseCase(repository),
        getFavoriteShoppingData = GetFavoriteShoppingUseCase(repository),
        insertFavoriteShoppingData = InsertFavoriteShoppingUseCase(repository),
        deleteFavoriteShoppingData = DeleteFavoriteShoppingUseCase(repository),
    )
}