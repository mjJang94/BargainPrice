package com.mj.domain.bridge

import androidx.paging.PagingData
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.flow.Flow

interface DataRepository {
    suspend fun getShoppingList(query: String): Flow<PagingData<ShoppingData>>
    fun shoppingFlow(): Flow<List<ShoppingData>>
    suspend fun insertShopping(data: ShoppingData)
    suspend fun deleteShopping(productId: String): Int
}