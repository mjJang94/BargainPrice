package com.mj.domain.di

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.home.GetShoppingDataUseCase
import com.mj.domain.usecase.home.CombinedShoppingUseCases
import com.mj.domain.usecase.home.DeleteFavoriteShoppingUseCase
import com.mj.domain.usecase.home.GetAlarmActiveUseCase
import com.mj.domain.usecase.home.GetFavoriteShoppingUseCase
import com.mj.domain.usecase.home.GetRecentQueriesUseCase
import com.mj.domain.usecase.home.InsertFavoriteShoppingUseCase
import com.mj.domain.usecase.home.SetAlarmActiveUseCase
import com.mj.domain.usecase.home.SetRecentQueriesUseCase
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
        getAlarmActiveUseCase = GetAlarmActiveUseCase(repository),
        setAlarmActiveUseCase = SetAlarmActiveUseCase(repository),
        getRecentQueriesUseCase = GetRecentQueriesUseCase(repository),
        setRecentQueriesUseCase = SetRecentQueriesUseCase(repository),
    )
}