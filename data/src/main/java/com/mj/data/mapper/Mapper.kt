package com.mj.data.mapper

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import com.mj.domain.model.RecordPrice
import com.mj.domain.model.Shopping

@JvmName("translateFromRemote")
fun List<ShoppingVo.Item>.translate(): List<Shopping> = this.map {
    Shopping(
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
fun List<ShoppingEntity>.translate(): List<Shopping> = this.map {
    it.translate()
}

fun ShoppingEntity.translate(): Shopping = Shopping(
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

fun List<RecordPriceEntity>.translate(): List<RecordPrice> = this.map {
    RecordPrice(
        productId = it.productId,
        lowestPrice = it.lowestPrice,
        highestPrice = it.highestPrice,
        timeStamp = it.timeStamp,
    )
}

fun Shopping.translate(): ShoppingEntity = ShoppingEntity(
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

fun RecordPrice.translate(): RecordPriceEntity = RecordPriceEntity(
    productId = productId,
    lowestPrice = lowestPrice,
    highestPrice = highestPrice,
    timeStamp = timeStamp,
)

inline fun <reified T> String?.parseJsonOrNull(): T? =
    Gson().fromJsonOrNull(this)

inline fun <reified T> Gson.fromJson(json: String?): T =
    fromJson(json, object : TypeToken<T>() {}.type)

inline fun <reified T> Gson.fromJsonOrNull(json: String?): T? =
    runCatching { fromJson<T>(json) }.getOrNull()

inline fun <reified T> T.toJson(): String =
    Gson().toJson(this, object : TypeToken<T>() {}.type)