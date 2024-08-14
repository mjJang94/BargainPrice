package com.mj.data.repo.remote.api

import com.mj.data.repo.remote.api.Endpoints.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

class RemoteApiService {
    inline operator fun <reified T> invoke(): T = create(T::class.java)

    fun <T> create(
        service: Class<T>,
    ): T = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(
            OkHttpClient.Builder()
                .addInterceptor(
                    HttpLoggingInterceptor { message ->
                        Timber.d(message)
                    }.apply {
                        setLevel(HttpLoggingInterceptor.Level.BODY)
                    })
                .readTimeout(65.seconds.toJavaDuration())
                .writeTimeout(65.seconds.toJavaDuration())
                .connectTimeout(65.seconds.toJavaDuration())
                .retryOnConnectionFailure(false)
                .build()
        )
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(service)
}