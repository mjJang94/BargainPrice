package com.mj.data.di

import com.mj.data.repo.DataRepositoryImpl
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.datasource.DataSourceImpl
import com.mj.data.repo.remote.api.RemoteApiService
import com.mj.data.repo.remote.api.NaverApi
import com.mj.domain.bridge.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Remote
     */
    @Provides
    @Singleton
    fun provideNaverApiService(): NaverApi = RemoteApiService().create(NaverApi::class.java)


    @Provides
    @Singleton
    fun provideDataSource(
        naverApi: NaverApi
    ): DataSource = DataSourceImpl(naverApi)

    @Provides
    @Singleton
    fun provideRepository(
        dataSource: DataSource
    ): DataRepository = DataRepositoryImpl(dataSource)

}