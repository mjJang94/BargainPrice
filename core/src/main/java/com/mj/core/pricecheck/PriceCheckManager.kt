package com.mj.core.pricecheck

import android.content.Context
import android.content.Intent
import com.mj.core.alarm.AlarmHelper
import com.mj.core.alarm.AlarmHelper.ComponentType.Receiver
import com.mj.core.common.compose.removeHtmlTag
import com.mj.core.ktx.Calendar
import com.mj.core.ktx.startOfNextDay
import com.mj.core.notification.NotificationHelper
import com.mj.core.notification.NotificationType
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.local.entity.RecordPriceEntity
import com.mj.data.repo.local.entity.ShoppingEntity
import com.mj.data.repo.remote.data.ShoppingVo
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class PriceCheckManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val notification: NotificationHelper,
    private val alarm: AlarmHelper,
    private val dataSource: DataSource,
) {

    suspend fun refresh(
        interactIntent: Intent,
        actionIntent: Intent,
        result: () -> Unit,
    ) {
        runCatching {
            refresh()
        }.onSuccess {
            setRefreshAlarm(System.currentTimeMillis())
            fireSuccessNotification(context, actionIntent)
            setAlarmSchedule(interactIntent)
            result()
        }.onFailure { tr ->
            Timber.e(tr)
            setRefreshAlarm(System.currentTimeMillis())
            fireFailureNotification(context, tr)
            setAlarmSchedule(interactIntent)
            result()
        }
    }

    private suspend fun refresh() {
        val cachedFavorite = dataSource.getAllShoppingItems()
        cachedFavorite.forEach { cache ->
            val refreshItem = dataSource.refreshFavoriteList(
                query = cache.title,
                start = 1,
                pageSize = 10,
            ).items.firstOrNull {
                it.productId == cache.productId
            }

            val shoppingEntity = when (refreshItem) {
                null -> cache.copy(isRefreshFail = true)
                else -> refreshItem.formalize(cache)
            }

            val recordPriceEntity = when (refreshItem) {
                null -> cache.translate()
                else -> refreshItem.formalize()
            }
            Timber.d("new entity = $shoppingEntity")
            dataSource.insertShoppingItem(shoppingEntity)
            dataSource.insertRecordPriceItem(recordPriceEntity)
        }
    }

    private fun setAlarmSchedule(interactIntent: Intent) {
        alarm.cancel(type = Receiver, intent = interactIntent)
        val triggerTime = Calendar(System.currentTimeMillis()).startOfNextDay()
        alarm.set(
            triggerTime = triggerTime.timeInMillis,
            type = Receiver,
            intent = interactIntent,
        )
    }

    private suspend fun setRefreshAlarm(time: Long) {
        dataSource.setRefreshTime(time)
    }

    private fun fireSuccessNotification(context: Context, notificationActionIntent: Intent) {
        notification.fire(
            NotificationType.RefreshSuccess(
                context = context,
                intent = notificationActionIntent,
            )
        )
    }

    private fun fireFailureNotification(context: Context, tr: Throwable) {
        notification.fire(
            NotificationType.RefreshFailure(
                context = context,
                reason = tr.message.orEmpty(),
            )
        )
    }

    private fun ShoppingVo.Item.formalize(cache: ShoppingEntity): ShoppingEntity =
        ShoppingEntity(
            title = title.removeHtmlTag(),
            link = link,
            image = image,
            lowestPrice = lprice,
            highestPrice = hprice,
            prevLowestPrice = cache.lowestPrice,
            prevHighestPrice = cache.highestPrice,
            mallName = mallName,
            productId = productId,
            productType = productType,
            maker = maker,
            brand = brand,
            category1 = category1,
            category2 = category2,
            category3 = category3,
            category4 = category4,
            isRefreshFail = false,
        )

    private fun ShoppingVo.Item.formalize(): RecordPriceEntity =
        RecordPriceEntity(
            productId = productId,
            lowestPrice = lprice,
            highestPrice = hprice,
            timeStamp = System.currentTimeMillis(),
        )

    private fun ShoppingEntity.translate(): RecordPriceEntity =
        RecordPriceEntity(
            productId = productId,
            lowestPrice = lowestPrice,
            highestPrice = highestPrice,
            timeStamp = System.currentTimeMillis(),
        )
}