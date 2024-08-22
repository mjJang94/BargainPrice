package com.mj.data.mapper

import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import com.mj.domain.model.ShoppingData

@JvmName("translateFromRemote")
fun List<ShoppingVo.Item>.translate(): List<ShoppingData> = this.map {
    ShoppingData(
        title = it.title,
        link = it.link,
        image = it.image,
        lowestPrice = it.lprice,
        highestPrice = it.hprice,
        prevLowestPrice = "",
        prevHighestPrice = "",
        mallName = it.mallName,
        productId = it.productId,
        productType = it.productType,
        maker = it.maker,
        brand = it.brand,
        category1 = it.category1,
        category2 = it.category2,
        category3 = it.category3,
        category4 = it.category4,
        isRefreshFail = false,
    )
}

@JvmName("translateFromLocal")
fun List<ShoppingEntity>.translate(): List<ShoppingData> = this.map {
    ShoppingData(
        title = it.title,
        link = it.link,
        image = it.image,
        lowestPrice = it.lowestPrice,
        highestPrice = it.highestPrice,
        prevLowestPrice = it.prevLowestPrice,
        prevHighestPrice = it.prevHighestPrice,
        mallName = it.mallName,
        productId = it.productId,
        productType = it.productType,
        maker = it.maker,
        brand = it.brand,
        category1 = it.category1,
        category2 = it.category2,
        category3 = it.category3,
        category4 = it.category4,
        isRefreshFail = it.isRefreshFail,
    )
}

fun ShoppingData.translate(): ShoppingEntity = ShoppingEntity(
    title = title,
    link = link,
    image = image,
    lowestPrice = lowestPrice,
    highestPrice = highestPrice,
    prevLowestPrice = prevLowestPrice,
    prevHighestPrice = prevHighestPrice,
    mallName = mallName,
    productId = productId,
    productType = productType,
    maker = maker,
    brand = brand,
    category1 = category1,
    category2 = category2,
    category3 = category3,
    category4 = category4,
    isRefreshFail = isRefreshFail,
)