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
import androidx.annotation.RequiresApi
import com.mj.app.bargainprice.core.price.PriceCheckService
import com.mj.core.alarm.Alarm
import com.mj.core.notification.NotificationHelper
import com.mj.core.notification.NotificationType
import com.mj.core.perm.Perm.checkExactAlarmPerm
import com.mj.core.perm.Perm.permissionCheck
import com.mj.core.perm.Perm.requestPermission
import com.mj.core.perm.Perm.requestPermissionSetting
import com.mj.core.theme.BargainPriceTheme
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {

    companion object {
        fun start(context: Context) =
            context.startActivity(intent(context))

        fun intent(context: Context) = Intent(context, MainActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //알람 권한
        if (!permissionCheck(Manifest.permission.POST_NOTIFICATIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermission(Manifest.permission.POST_NOTIFICATIONS) { isGranted ->
                    if (isGranted) {
                        Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        //정확한 알람 권한
        if (!checkExactAlarmPerm()) {
            Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                exactAlarmLauncher.launch(Uri.fromParts("package", packageName, null))
            }
        } else {
            Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
        }

        Alarm.PriceCheck(
            context = this@MainActivity,
            intent = PriceCheckService.intent(this@MainActivity)
        ).set(System.currentTimeMillis() + 5.milliseconds.inWholeMilliseconds)

        setContent {
            BargainPriceTheme {
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    private val exactAlarmLauncher = requestPermissionSetting(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM) {
        if (checkExactAlarmPerm()) {
            Toast.makeText(this, "동작 수행", Toast.LENGTH_SHORT).show()
            //동작 수행
        } else {
            //동작 취소
            Toast.makeText(this, "동작 취소", Toast.LENGTH_SHORT).show()
        }
    }
}
