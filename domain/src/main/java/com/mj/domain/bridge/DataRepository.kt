package com.mj.domain.bridge

import androidx.paging.PagingData
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getShoppingList(query: String): Flow<PagingData<ShoppingData>>

    fun shoppingFlow(): Flow<List<ShoppingData>>
    suspend fun getAllShoppingItems(): List<ShoppingData>
    suspend fun insertShoppingItem(data: ShoppingData)
    suspend fun deleteShoppingItem(productId: String): Int

    fun getAlarmActive(): Flow<Boolean>
    suspend fun setAlarmActive(active: Boolean)

    fun getRecentSearchQueriesFlow(): Flow<Set<String>>
    suspend fun setRecentQueries(queries: Set<String>)

    fun getRefreshTimeFlow(): Flow<Long>
}