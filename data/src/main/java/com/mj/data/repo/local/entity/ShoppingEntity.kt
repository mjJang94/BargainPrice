package com.mj.data.repo.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ShoppingEntity(
    @PrimaryKey val productId: String,
    val title: String,
    val link: String,
    val image: String,
    val lowestPrice: String,
    val highestPrice: String,
    val mallName: String,
    val productType: String,
    val maker: String,
    val brand: String,
    val category1: String,
    val category2: String,
    val category3: String,
    val category4: String,
)
