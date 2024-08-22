package com.mj.data.di

import android.content.Context
import androidx.room.Room
import com.mj.data.repo.DataRepositoryImpl
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.datasource.DataSourceImpl
import com.mj.data.repo.local.AppDatabase
import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.pref.DataStoreManager
import com.mj.data.repo.remote.api.NaverApi
import com.mj.data.repo.remote.api.RemoteApiService
import com.mj.domain.bridge.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    /**
     * Local
     */
    @Provides
    @Singleton
    fun provideLocalDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "local-database",
        ).build()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStoreManager =
        DataStoreManager(context)

    @Provides
    @Singleton
    fun provideShoppingDao(appDatabase: AppDatabase):ShoppingDao = appDatabase.shoppingDao()

    @Provides
    @Singleton
    fun provideDataSource(
        naverApi: NaverApi,
        shoppingDao: ShoppingDao,
        store: DataStoreManager,
    ): DataSource = DataSourceImpl(
        naverApi = naverApi,
        shoppingDao = shoppingDao,
        store = store,
    )

    @Provides
    @Singleton
    fun provideRepository(
        dataSource: DataSource
    ): DataRepository = DataRepositoryImpl(dataSource)

}