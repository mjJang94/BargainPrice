package com.mj.domain.di

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import com.mj.domain.usecase.detail.GetFavoriteShoppingItemUseCase
import com.mj.domain.usecase.detail.GetRecordPriceUseCase
import com.mj.domain.usecase.detail.InsertRecordPriceUseCase
import com.mj.domain.usecase.home.CombinedShoppingUseCases
import com.mj.domain.usecase.home.DeleteFavoriteShoppingUseCase
import com.mj.domain.usecase.home.DeleteRecordPriceUseCase
import com.mj.domain.usecase.home.GetAlarmActiveUseCase
import com.mj.domain.usecase.home.GetFavoriteShoppingListUseCase
import com.mj.domain.usecase.home.GetRecentQueriesUseCase
import com.mj.domain.usecase.home.GetRefreshTimeUseCase
import com.mj.domain.usecase.home.GetShoppingDataUseCase
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
        getFavoriteShoppingData = GetFavoriteShoppingListUseCase(repository),
        insertFavoriteShoppingData = InsertFavoriteShoppingUseCase(repository),
        deleteFavoriteShoppingData = DeleteFavoriteShoppingUseCase(repository),
        getAlarmActiveUseCase = GetAlarmActiveUseCase(repository),
        setAlarmActiveUseCase = SetAlarmActiveUseCase(repository),
        getRecentQueriesUseCase = GetRecentQueriesUseCase(repository),
        setRecentQueriesUseCase = SetRecentQueriesUseCase(repository),
        getRefreshTimeUseCase = GetRefreshTimeUseCase(repository),
        deleteRecordPriceUseCase = DeleteRecordPriceUseCase(repository),
    )

    @Provides
    @Singleton
    fun provideDetailUseCase(
        repository: DataRepository
    ) = CombinedDetailUseCases(
        getRecordPriceUseCase = GetRecordPriceUseCase(repository),
        getFavoriteShoppingItemUseCase = GetFavoriteShoppingItemUseCase(repository),
        inertRecordPriceUseCase = InsertRecordPriceUseCase(repository),
    )
}