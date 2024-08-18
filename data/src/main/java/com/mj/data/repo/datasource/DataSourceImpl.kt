package com.mj.data.repo.datasource

import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.api.NaverApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DataSourceImpl @Inject constructor(
    private val naverApi: NaverApi,
    private val shoppingDao: ShoppingDao,
) : DataSource {
    override suspend fun getShoppingData(query: String, page: Int, pageSize: Int) =
        naverApi.getShoppingData(q = query, start = page)

    override fun shoppingFlow(): Flow<List<ShoppingEntity>> =
        shoppingDao.flow()


    override suspend fun insertShopping(shoppingEntity: ShoppingEntity) =
        shoppingDao.insert(shoppingEntity)

    override suspend fun deleteShopping(productId: String): Int =
        shoppingDao.deleteById(productId)
}