package com.mj.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.remote.paging.MAX_PAGE_SIZE
import com.mj.data.repo.remote.paging.PREFETCH_DISTANCE
import com.mj.data.repo.remote.paging.ShoppingPagingSource
import com.mj.domain.bridge.DataRepository
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class DataRepositoryImpl(
    private val dataSource: DataSource
): DataRepository {
    override suspend fun getShoppingList(query: String): Flow<PagingData<ShoppingData>> {
        Timber.d("check DataRepositoryImpl")
        return Pager(
            config = PagingConfig(pageSize = MAX_PAGE_SIZE, prefetchDistance = PREFETCH_DISTANCE),
            pagingSourceFactory = {
                ShoppingPagingSource(dataSource = dataSource, query = query)
            }
        ).flow
    }
}