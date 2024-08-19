package com.mj.app.bargainprice.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mj.app.bargainprice.ui.MainActivity
import com.mj.core.alarm.AlarmHelper
import com.mj.core.flags
import com.mj.core.ktx.Calendar
import com.mj.core.ktx.startOfNextDay
import com.mj.core.notification.NotificationHelper
import com.mj.core.notification.NotificationType
import com.mj.data.repo.datasource.DataSource
import com.mj.data.repo.local.AppDatabase
import com.mj.data.repo.local.dao.ShoppingDao
import com.mj.data.repo.local.entity.ShoppingEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class PriceCheckService : Service(), CoroutineScope by CoroutineScope(Companion) {

    @Inject
    lateinit var noti: NotificationHelper

    @Inject
    lateinit var alarm: AlarmHelper

    @Inject
    lateinit var dataSource: DataSource

    companion object : CoroutineContext by SupervisorJob() + Dispatchers.Main.immediate {
        fun start(context: Context) = context.startService(intent(context))
        fun intent(context: Context) = Intent(context, PriceCheckService::class.java)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand($startId)")
        launch(Dispatchers.Default) {
            runCatching {

                val prevFavorites = dataSource.getAllShoppingItems()
                Timber.d("prev favorites = $prevFavorites")
                prevFavorites.forEach { prev ->
                    val refreshItem = dataSource.refreshFavoriteList(
                        query = prev.title,
                        page = 1,
                        pageSize = 10,
                    ).items.firstOrNull {
                        it.productId == prev.productId
                    }

                    val entity = if (refreshItem != null) {
                        Timber.d("refreshItem not null")
                        ShoppingEntity(
                            title = refreshItem.title,
                            link = refreshItem.link,
                            image = refreshItem.image,
                            lowestPrice = refreshItem.lprice,
                            highestPrice = refreshItem.hprice,
                            prevLowestPrice = prev.lowestPrice,
                            prevHighestPrice = prev.highestPrice,
                            mallName = refreshItem.mallName,
                            productId = refreshItem.productId,
                            productType = refreshItem.productType,
                            maker = refreshItem.maker,
                            brand = refreshItem.brand,
                            category1 = refreshItem.category1,
                            category2 = refreshItem.category2,
                            category3 = refreshItem.category3,
                            category4 = refreshItem.category4,
                            isRefreshFail = false,
                        )

                    } else {
                        Timber.d("refreshItem is null")
                        ShoppingEntity(
                            title = prev.title,
                            link = prev.link,
                            image = prev.image,
                            lowestPrice = prev.lowestPrice,
                            highestPrice = prev.highestPrice,
                            prevLowestPrice = prev.lowestPrice,
                            prevHighestPrice = prev.highestPrice,
                            mallName = prev.mallName,
                            productId = prev.productId,
                            productType = prev.productType,
                            maker = prev.maker,
                            brand = prev.brand,
                            category1 = prev.category1,
                            category2 = prev.category2,
                            category3 = prev.category3,
                            category4 = prev.category4,
                            isRefreshFail = true,
                        )
                    }
                    Timber.d("new entity = $entity")
                    dataSource.insertShoppingItem(entity)
                }

                //갱신 작업 수행
                val notiType = NotificationType.Action(
                    title = "즐겨찾기 상품 갱신",
                    message = "즐겨찾기에 추가한 상품의 정보가 갱신 되었어요.",
                    actionText = "확인하기",
                    actionIntent = PendingIntent.getActivity(
                        this@PriceCheckService,
                        0,
                        MainActivity.intent(this@PriceCheckService),
                        flags(),
                    )
                )
                noti.showNotification(notiType)
                setAlarmSchedule()
            }.onSuccess {
                stopSelf(startId)
            }.onFailure { tr ->
                Timber.e(tr)
                stopSelf(startId)
            }
        }
        return START_STICKY
    }

    private fun setAlarmSchedule() {
        val time = Calendar(System.currentTimeMillis()).startOfNextDay()
        alarm.set(
            triggerTime = time.timeInMillis,
            type = AlarmHelper.ComponentType.Service,
            intent = intent(this)
        )
    }
}