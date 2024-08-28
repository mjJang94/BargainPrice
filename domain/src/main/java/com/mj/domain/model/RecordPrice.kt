package com.mj.domain.model

data class RecordPrice(
    val productId: String,
    val lowestPrice: String,
    val highestPrice: String,
    val timeStamp: Long,
)
