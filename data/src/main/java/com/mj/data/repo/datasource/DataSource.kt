package com.mj.data.repo.datasource

import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import kotlinx.coroutines.flow.Flow


interface DataSource {
    suspend fun getShoppingData(query: String, start: Int): ShoppingVo
    suspend fun refreshFavoriteList(query: String, start: Int, pageSize: Int): ShoppingVo

    fun shoppingFlow(): Flow<List<ShoppingEntity>>
    fun getShoppingFlowById(productId: String): Flow<ShoppingEntity>
    suspend fun getAllShoppingItems(): List<ShoppingEntity>
    suspend fun insertShoppingItem(shoppingEntity: ShoppingEntity)
    suspend fun deleteShoppingItem(productId: String): Int

    fun recordPriceFlow(productId: String, startTime: Long, endTime: Long): Flow<List<RecordPriceEntity>>
    suspend fun insertRecordPriceItem(recordPriceEntity: RecordPriceEntity)
    suspend fun deleteRecordPriceItem(productId: String)

    fun getAlarmActiveFlow(): Flow<Boolean>
    suspend fun setAlarmActive(active: Boolean)

    fun getRecentSearchQueriesFlow(): Flow<Set<String>>
    suspend fun setRecentQueries(queries: Set<String>)

    fun getRefreshTimeFlow(): Flow<Long>
    suspend fun setRefreshTime(time: Long)

    fun getSkipLoginFlow(): Flow<Boolean>
}