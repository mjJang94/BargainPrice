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
import com.mj.domain.model.RecordPrice
import com.mj.domain.model.Shopping
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataRepositoryImpl(
    private val dataSource: DataSource
) : DataRepository {
    override suspend fun getShoppingList(query: String): Flow<PagingData<Shopping>> {
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                ShoppingPagingSource(dataSource = dataSource, query = query)
            }
        ).flow
    }

    override fun shoppingFlow(): Flow<List<Shopping>> =
        dataSource.shoppingFlow().map { it.translate() }

    override suspend fun getAllShoppingItems(): List<Shopping> =
        dataSource.getAllShoppingItems().translate()

    override fun getShoppingFlowById(productId: String): Flow<Shopping> =
        dataSource.getShoppingFlowById(productId).map { it.translate() }

    override suspend fun insertShoppingItem(data: Shopping) =
        dataSource.insertShoppingItem(data.translate())

    override suspend fun deleteShoppingItem(productId: String): Int =
        dataSource.deleteShoppingItem(productId)

    override fun recordPriceFlow(productId: String, startTime: Long, endTime: Long): Flow<List<RecordPrice>> =
        dataSource.recordPriceFlow(productId, startTime, endTime).map { it.translate() }

    override suspend fun insertRecordPriceItem(recordPrice: RecordPrice) =
        dataSource.insertRecordPriceItem(recordPrice.translate())

    override suspend fun deleteRecordPriceItem(productId: String) =
        dataSource.deleteRecordPriceItem(productId)

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