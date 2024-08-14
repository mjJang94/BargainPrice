package com.mj.data.repo.datasource

import com.mj.data.repo.remote.data.ShoppingVo


interface DataSource {
    suspend fun getShoppingData(query: String, page: Int, pageSize: Int = 20): ShoppingVo
}