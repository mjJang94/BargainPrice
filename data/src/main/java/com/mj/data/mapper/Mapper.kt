package com.mj.data.mapper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import com.mj.domain.model.ShoppingData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.net.URL
import kotlin.math.ceil
import kotlin.math.max

@JvmName("translateFromRemote")
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

@JvmName("translateFromLocal")
fun List<ShoppingEntity>.translate(): List<ShoppingData> = this.map {
    ShoppingData(
        title = it.title,
        link = it.link,
        image = it.image,
        lowestPrice = it.lowestPrice,
        highestPrice = it.highestPrice,
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

fun ShoppingData.translate(): ShoppingEntity = ShoppingEntity(
    title = title,
    link = link,
    image = image,
    lowestPrice = lowestPrice,
    highestPrice = highestPrice,
    mallName = mallName,
    productId = productId,
    productType = productType,
    maker = maker,
    brand = brand,
    category1 = category1,
    category2 = category2,
    category3 = category3,
    category4 = category4,
)

//https://medium.com/@blucky8649/android-glide-picasso-%EC%99%80-%EA%B0%99%EC%9D%80-%EB%9D%BC%EC%9D%B4%EB%B8%8C%EB%9F%AC%EB%A6%AC%EB%8A%94-%EC%96%B4%EB%96%BB%EA%B2%8C-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EB%A1%9C%EB%93%9C%EB%A5%BC-%EC%B5%9C%EC%A0%81%ED%99%94%ED%95%A0%EA%B9%8C-1%ED%8E%B8-down-sampling-4d7c7797c018
private suspend fun getByteArray(url: String): ByteArray {
    val byteArrayDeferred = CoroutineScope(Dispatchers.IO).async {
        URL(url).readBytes()
    }
    return byteArrayDeferred.await()
}

private fun ByteArray.downSample(
    requestWidth: Int,
    requestHeight: Int
): Bitmap = BitmapFactory.Options().run {
    inJustDecodeBounds = true
    BitmapFactory.decodeByteArray(this@downSample, 0, this@downSample.size, this)

    inSampleSize = calcInSampleSize(this, requestWidth, requestHeight)

    inJustDecodeBounds = false
    BitmapFactory.decodeByteArray(this@downSample, 0, this@downSample.size, this)
}

fun calcInSampleSize(
    options: BitmapFactory.Options,
    requestWidth: Int,
    requestHeight: Int
): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (requestHeight < height || requestWidth < width) {
        inSampleSize = max(
            ceil(height.toFloat() / requestHeight).toInt(),
            ceil(width.toFloat() / requestWidth).toInt()
        )
    }

    return inSampleSize
}