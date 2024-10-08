package com.mj.data.repo.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mj.data.mapper.translate
import com.mj.data.repo.datasource.DataSource
import com.mj.domain.model.Shopping
import timber.log.Timber

class ShoppingPagingSource(
    private val dataSource: DataSource,
    private val query: String,
) : PagingSource<Int, Shopping>() {
    override fun getRefreshKey(state: PagingState<Int, Shopping>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Shopping> {
        return try {
            val start = params.key ?: 1

            val shoppingData = dataSource.getShoppingData(query, start)

            LoadResult.Page(
                data = shoppingData.items.translate(),
                prevKey = if (start == 1) null else start - 1,
                nextKey = if (shoppingData.total < MAX_PAGE_SIZE) null else start + shoppingData.display,
            )
        } catch (e: Exception) {
            Timber.d("error = $e")
            return LoadResult.Error(e)
        }
    }
}