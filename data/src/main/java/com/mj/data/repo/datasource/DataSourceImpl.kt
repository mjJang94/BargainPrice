package com.mj.data.repo.datasource

import com.mj.data.repo.local.dao.RecordPriceDao
import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.local.pref.DataStoreManager
import com.mj.data.repo.remote.api.NaverApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSourceImpl @Inject constructor(
    private val naverApi: NaverApi,
    private val shoppingDao: ShoppingDao,
    private val recordPriceDao: RecordPriceDao,
    private val store: DataStoreManager
) : DataSource {
    override suspend fun getShoppingData(query: String, page: Int, pageSize: Int) =
        naverApi.getShoppingData(q = query, start = page)

    override suspend fun refreshFavoriteList(query: String, page: Int, pageSize: Int) =
        naverApi.getShoppingData(q = query, start = page, display = pageSize)

    override fun shoppingFlow(): Flow<List<ShoppingEntity>> =
        shoppingDao.flow()

    override fun getShoppingFlowById(productId: String): Flow<ShoppingEntity> =
        shoppingDao.getShoppingByIdFlow(productId)

    override suspend fun getAllShoppingItems(): List<ShoppingEntity> =
        shoppingDao.getAll()

    override suspend fun insertShoppingItem(shoppingEntity: ShoppingEntity) =
        shoppingDao.insert(shoppingEntity)

    override suspend fun deleteShoppingItem(productId: String): Int =
        shoppingDao.deleteById(productId)

    override fun recordPriceFlow(productId: String, startTime: Long, endTime: Long): Flow<List<RecordPriceEntity>> =
        recordPriceDao.flow(productId, startTime, endTime)

    override suspend fun insertRecordPriceItem(recordPriceEntity: RecordPriceEntity) =
        recordPriceDao.insert(recordPriceEntity)

    override fun getAlarmActiveFlow(): Flow<Boolean> =
        store.priceCheckAlarmActiveFlow

    override suspend fun setAlarmActive(active: Boolean) =
        store.storePriceCheckAlarmActivation(active)

    override fun getRecentSearchQueriesFlow(): Flow<Set<String>> =
        store.recentSearchQueries

    override suspend fun setRecentQueries(queries: Set<String>) =
        store.storeRecentSearchQueries(queries)

    override fun getRefreshTime(): Flow<Long> =
        store.recentRefreshTime

    override suspend fun setRefreshTime(time: Long) =
        store.storeRecentRefreshTime(time)
}