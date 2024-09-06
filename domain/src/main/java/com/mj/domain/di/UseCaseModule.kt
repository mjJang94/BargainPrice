package com.mj.domain.di

import com.mj.domain.bridge.DataRepository
import com.mj.domain.usecase.detail.CombinedDetailUseCases
import com.mj.domain.usecase.detail.GetFavoriteShoppingItemUseCase
import com.mj.domain.usecase.detail.GetRecordPriceUseCase
import com.mj.domain.usecase.detail.InsertRecordPriceUseCase
import com.mj.domain.usecase.home.CombinedHomeUseCases
import com.mj.domain.usecase.home.DeleteFavoriteShoppingUseCase
import com.mj.domain.usecase.home.GetAlarmActiveUseCase
import com.mj.domain.usecase.home.GetFavoriteShoppingListUseCase
import com.mj.domain.usecase.search.GetRecentQueriesUseCase
import com.mj.domain.usecase.home.GetRefreshTimeUseCase
import com.mj.domain.usecase.search.GetShoppingDataUseCase
import com.mj.domain.usecase.search.InsertFavoriteUseCase
import com.mj.domain.usecase.home.SetAlarmActiveUseCase
import com.mj.domain.usecase.search.SetRecentQueriesUseCase
import com.mj.domain.usecase.login.CombinedLoginUseCases
import com.mj.domain.usecase.login.GetSkipLoginUseCase
import com.mj.domain.usecase.search.CombinedSearchUseCases
import com.mj.domain.usecase.search.DeleteFavoriteUseCase
import com.mj.domain.usecase.search.GetFavoriteListUseCase
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
    fun provideLoginUseCase(
        repository: DataRepository
    ) = CombinedLoginUseCases (
        getSkipLoginUseCase = GetSkipLoginUseCase(repository)
    )

    @Provides
    @Singleton
    fun provideHomeUseCase(
        repository: DataRepository
    ) = CombinedHomeUseCases(
        getFavoriteShoppingData = GetFavoriteShoppingListUseCase(repository),
        deleteFavoriteShoppingData = DeleteFavoriteShoppingUseCase(repository),
        getAlarmActive = GetAlarmActiveUseCase(repository),
        setAlarmActive = SetAlarmActiveUseCase(repository),
        getRefreshTime = GetRefreshTimeUseCase(repository),
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

    @Provides
    @Singleton
    fun provideSearchUseCase(
        repository: DataRepository
    ) = CombinedSearchUseCases(
        getShoppingData = GetShoppingDataUseCase(repository),
        getFavoriteData = GetFavoriteListUseCase(repository),
        getRecentQueries = GetRecentQueriesUseCase(repository),
        setRecentQueries = SetRecentQueriesUseCase(repository),
        deleteFavoriteData = DeleteFavoriteUseCase(repository),
        insertFavoriteData = InsertFavoriteUseCase(repository),
    )
}