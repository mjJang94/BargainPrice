package com.mj.app.bargainprice.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.mj.app.bargainprice.di.CoreBridge
import com.mj.app.bargainprice.service.PriceCheckService
import com.mj.core.alarm.AlarmHelper
import com.mj.core.theme.BargainPriceTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var bridge: CoreBridge

    companion object {
        fun start(context: Context) =
            context.startActivity(intent(context))

        fun intent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //알람 권한
        if (!bridge.permissionCheck(Manifest.permission.POST_NOTIFICATIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                bridge.requestPermission(this, Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
                    if (isGranted) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //정확한 알람 권한
        if (!bridge.checkExactAlarm()) {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                bridge.requestPermissionSetting(this@MainActivity, Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM) {
                    if (bridge.checkExactAlarm()) {
                        Toast.makeText(this, "동작 수행", Toast.LENGTH_SHORT).show()
                        //동작 수행
                        bridge.setAlarm(
                            triggerTime = System.currentTimeMillis() + 5.minutes.inWholeMilliseconds,
                            type = AlarmHelper.ComponentType.Service,
                            intent = PriceCheckService.intent(this@MainActivity)
                        )
                    } else {
                        //동작 취소
                        Toast.makeText(this, "동작 취소", Toast.LENGTH_SHORT).show()
                    }
                }.launch(
                    Uri.fromParts("package", packageName, null)
                )
            }
        } else {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()

            bridge.setAlarm(
                triggerTime = System.currentTimeMillis() + 5.minutes.inWholeMilliseconds,
                type = AlarmHelper.ComponentType.Service,
                intent = PriceCheckService.intent(this@MainActivity)
            )
        }

        setContent {
            BargainPriceTheme {
            }
        }
    }
}
