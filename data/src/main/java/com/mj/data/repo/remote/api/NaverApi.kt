package com.mj.data.repo.remote.api

import com.mj.data.BuildConfig
import com.mj.data.repo.remote.data.ShoppingVo
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverApi {

    @GET(Endpoints.GET_SHOPPING)
    suspend fun getShoppingData(
        @Header("X-Naver-Client-Id") id: String = BuildConfig.NAVER_CLIENT_ID,
        @Header("X-Naver-Client-Secret") secret: String = BuildConfig.NAVER_CLIENT_SECRET,
        @Query("query") q: String,
        @Query("start") start: Int = 1,
        @Query("display") display: Int = 20,
        @Query("exclude") exclude: String = "used:rental",
    ): ShoppingVo
}