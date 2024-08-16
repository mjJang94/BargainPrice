package com.mj.data.repo.datasource

import com.mj.data.repo.remote.api.NaverApi
import javax.inject.Inject

class DataSourceImpl @Inject constructor(
    private val naverApi: NaverApi,
) : DataSource {
    override suspend fun getShoppingData(query: String, page: Int, pageSize: Int) =
        naverApi.getShoppingData(q = query, start = page)
}