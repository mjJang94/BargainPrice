package com.mj.app.bargainprice

import android.app.Application
import com.mj.data.repo.remote.api.Endpoints
import com.navercorp.nid.NaverIdLoginSDK
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BargainPriceApp : Application() {

    private val context
        get() = this.applicationContext

    override fun onCreate() {
        super.onCreate()

        NaverIdLoginSDK.initialize(context, BuildConfig.NAVER_CLIENT_ID, BuildConfig.NAVER_CLIENT_SECRET, context.applicationInfo.name)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            NaverIdLoginSDK.showDevelopersLog(true)
        }
    }
}