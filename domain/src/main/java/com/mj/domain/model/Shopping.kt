package com.mj.domain.model


data class Shopping(
    val title: String,
    val link: String,
    val image: String,
    val lowestPrice: String,
    val highestPrice: String,
    val prevLowestPrice: String,
    val prevHighestPrice: String,
    val mallName: String,
    val productId: String,
    val productType: String,
    val maker: String,
    val brand: String,
    val category1: String,
    val category2: String,
    val category3: String,
    val category4: String,
    val isRefreshFail: Boolean,
)
