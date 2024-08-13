package com.mj.app.bargainprice.service

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mj.app.bargainprice.di.CoreBridge
import com.mj.app.bargainprice.ui.MainActivity
import com.mj.core.flags
import com.mj.core.notification.NotificationType
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
    lateinit var bridge: CoreBridge

    companion object : CoroutineContext by SupervisorJob() + Dispatchers.Main.immediate {
        fun start(context: Context) = context.startService(intent(context))
        fun intent(context: Context) = Intent(context, PriceCheckService::class.java)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand($startId)")
        launch(Dispatchers.Default) {
            runCatching {
                //갱신 작업 수행
                val notiType = NotificationType.Action(
                    title = "타이틀",
                    message = "메세지",
                    actionText = "더보기",
                    actionIntent = PendingIntent.getActivity(
                        this@PriceCheckService,
                        0,
                        MainActivity.intent(this@PriceCheckService),
                        flags(),
                    )
                )
                bridge.fireNotification(notiType)
            }.onSuccess {
                stopSelf(startId)
            }.onFailure { tr ->
                Timber.e(tr)
                stopSelf(startId)
            }
        }
        return START_STICKY
    }
}