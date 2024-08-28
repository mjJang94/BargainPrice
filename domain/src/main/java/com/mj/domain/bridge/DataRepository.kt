package com.mj.domain.bridge

import androidx.paging.PagingData
import com.mj.domain.model.RecordPrice
import com.mj.domain.model.Shopping
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getShoppingList(query: String): Flow<PagingData<Shopping>>

    fun shoppingFlow(): Flow<List<Shopping>>
    fun getShoppingFlowById(productId: String): Flow<Shopping>
    suspend fun getAllShoppingItems(): List<Shopping>
    suspend fun insertShoppingItem(data: Shopping)
    suspend fun deleteShoppingItem(productId: String): Int

    fun recordPriceFlow(productId: String, startTime: Long, endTime: Long): Flow<List<RecordPrice>>
    suspend fun insertRecordPriceItem(recordPrice: RecordPrice)

    fun getAlarmActive(): Flow<Boolean>
    suspend fun setAlarmActive(active: Boolean)

    fun getRecentSearchQueriesFlow(): Flow<Set<String>>
    suspend fun setRecentQueries(queries: Set<String>)

    fun getRefreshTimeFlow(): Flow<Long>
}