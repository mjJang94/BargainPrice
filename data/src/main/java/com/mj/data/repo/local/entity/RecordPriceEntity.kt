package com.mj.data.repo.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class RecordPriceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: String,
    val lowestPrice: String,
    val highestPrice: String,
    val timeStamp: Long,
)