package com.mj.data.repo.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RecordPriceEntity(
    @PrimaryKey val productId: String,
    val lowestPrice: String,
    val highestPrice: String,
    val timeStamp: Long,
)