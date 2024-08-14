package com.mj.data.repo.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.mj.data.mapper.translate
import com.mj.data.repo.datasource.DataSource
import com.mj.domain.model.ShoppingData
import timber.log.Timber

class ShoppingPagingSource(
    private val dataSource: DataSource,
    private val query: String,
) : PagingSource<Int, ShoppingData>() {
    override fun getRefreshKey(state: PagingState<Int, ShoppingData>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ShoppingData> {
        return try {
            val currentPage = params.key ?: 1
            val shoppingData = dataSource.getShoppingData(query, currentPage)

            LoadResult.Page(
                data = shoppingData.items.translate(),
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = shoppingData.start + 1,
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}