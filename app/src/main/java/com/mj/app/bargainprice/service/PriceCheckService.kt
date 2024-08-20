package com.mj.app.bargainprice.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.mj.app.bargainprice.ui.MainActivity
import com.mj.core.pricecheck.PriceCheckManager
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
    lateinit var priceCheckManager: PriceCheckManager

    companion object : CoroutineContext by SupervisorJob() + Dispatchers.Main.immediate {
        fun start(context: Context) = context.startService(intent(context))
        fun intent(context: Context) = Intent(context, PriceCheckService::class.java)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("onStartCommand($startId)")
        launch(Dispatchers.Default) {
            priceCheckManager.refresh(
                interactIntent = intent(this@PriceCheckService),
                actionIntent = MainActivity.intent(this@PriceCheckService),
            ) {
                stopSelf(startId)
            }
        }
        return START_STICKY
    }
}