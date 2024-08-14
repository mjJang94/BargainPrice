package com.mj.data.mapper

import com.mj.data.repo.remote.data.ShoppingVo
import com.mj.domain.model.ShoppingData

fun List<ShoppingVo.Item>.translate(): List<ShoppingData> = this.map {
    ShoppingData(
        title = it.title,
        link = it.link,
        image = it.image,
        lowestPrice = it.lprice,
        highestPrice = it.hprice,
        mallName = it.mallName,
        productId = it.productId,
        productType = it.productType,
        maker = it.maker,
        brand = it.brand,
        category1 = it.category1,
        category2 = it.category2,
        category3 = it.category3,
        category4 = it.category4,
    )
}