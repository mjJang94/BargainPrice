package com.mj.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mj.data.mapper.translate
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.remote.paging.MAX_PAGE_SIZE
import com.mj.data.repo.remote.paging.PREFETCH_DISTANCE
import com.mj.data.repo.remote.paging.ShoppingPagingSource
import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepositoryImpl(
    private val dataSource: DataSource
) : DataRepository {
    override suspend fun getShoppingList(query: String): Flow<PagingData<ShoppingData>> {
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                ShoppingPagingSource(dataSource = dataSource, query = query)
            }
        ).flow
    }

    override fun shoppingFlow(): Flow<List<ShoppingData>> =
        dataSource.shoppingFlow().map { it.translate() }

    override suspend fun getAllShoppingItems(): List<ShoppingData> =
        dataSource.getAllShoppingItems().translate()

    override suspend fun insertShoppingItem(data: ShoppingData) =
        dataSource.insertShoppingItem(data.translate())

    override suspend fun deleteShoppingItem(productId: String): Int =
        dataSource.deleteShoppingItem(productId)

    override fun getAlarmActive(): Flow<Boolean> =
        dataSource.getAlarmActiveFlow()

    override suspend fun setAlarmActive(active: Boolean) =
        dataSource.setAlarmActive(active)

    override fun getRecentSearchQueriesFlow(): Flow<Set<String>> =
        dataSource.getRecentSearchQueriesFlow()

    override suspend fun setRecentQueries(queries: Set<String>) =
        dataSource.setRecentQueries(queries)

    override fun getRefreshTimeFlow(): Flow<Long> =
        dataSource.getRefreshTime()
}