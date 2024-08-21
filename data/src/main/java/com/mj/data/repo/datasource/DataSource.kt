package com.mj.data.repo.datasource

import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import kotlinx.coroutines.flow.Flow


interface DataSource {
    suspend fun getShoppingData(query: String, page: Int, pageSize: Int = 20): ShoppingVo
    suspend fun refreshFavoriteList(query: String, page: Int, pageSize: Int): ShoppingVo

    fun shoppingFlow(): Flow<List<ShoppingEntity>>
    suspend fun getAllShoppingItems(): List<ShoppingEntity>
    suspend fun insertShoppingItem(shoppingEntity: ShoppingEntity)
    suspend fun deleteShoppingItem(productId: String): Int

    fun getAlarmActiveFlow(): Flow<Boolean>
    suspend fun setAlarmActive(active: Boolean)

    fun getRecentSearchQueriesFlow(): Flow<Set<String>>
    suspend fun setRecentQueries(queries: Set<String>)
}