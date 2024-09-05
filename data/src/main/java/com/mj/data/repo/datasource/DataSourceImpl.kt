package com.mj.data.repo.datasource

import com.mj.data.repo.local.dao.RecordPriceDao
import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.local.pref.DataStoreManager
import com.mj.data.repo.remote.api.NaverApi
import com.mj.data.repo.remote.data.ShoppingVo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSourceImpl @Inject constructor(
    private val naverApi: NaverApi,
    private val shoppingDao: ShoppingDao,
    private val recordPriceDao: RecordPriceDao,
    private val store: DataStoreManager
) : DataSource {
    override suspend fun getShoppingData(query: String, start: Int): ShoppingVo =
        naverApi.getShoppingData(q = query, start = start)

    override suspend fun refreshFavoriteList(query: String, start: Int, pageSize: Int) =
        naverApi.getShoppingData(q = query, start = start, display = pageSize)

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

    override suspend fun deleteRecordPriceItem(productId: String) =
        recordPriceDao.deleteById(productId)

    override fun getAlarmActiveFlow(): Flow<Boolean> =
        store.priceCheckAlarmActiveFlow

    override suspend fun setAlarmActive(active: Boolean) =
        store.storePriceCheckAlarmActivation(active)

    override fun getRecentSearchQueriesFlow(): Flow<Set<String>> =
        store.recentSearchQueriesFlow

    override suspend fun setRecentQueries(queries: Set<String>) =
        store.storeRecentSearchQueries(queries)

    override fun getRefreshTime(): Flow<Long> =
        store.recentRefreshTimeFlow

    override suspend fun setRefreshTime(time: Long) =
        store.storeRecentRefreshTime(time)
}