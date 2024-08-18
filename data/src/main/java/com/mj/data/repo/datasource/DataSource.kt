package com.mj.data.repo.datasource

import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import kotlinx.coroutines.flow.Flow


interface DataSource {
    suspend fun getShoppingData(query: String, page: Int, pageSize: Int = 20): ShoppingVo
    fun shoppingFlow(): Flow<List<ShoppingEntity>>
    suspend fun insertShopping(shoppingEntity: ShoppingEntity)
    suspend fun deleteShopping(productId: String): Int
}