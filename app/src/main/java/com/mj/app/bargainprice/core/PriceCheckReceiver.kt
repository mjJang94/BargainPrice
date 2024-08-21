package com.mj.app.bargainprice.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class PriceCheckReceiver: BroadcastReceiver(){

    companion object {
        fun intent(context: Context) =
            Intent(context, PriceCheckReceiver::class.java)
    }

    override fun onReceive(context: Context, intent: Intent) {
        PriceCheckService.start(context)
    }
}