package com.mj.app.bargainprice.di

import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.mj.core.alarm.AlarmHelper.ComponentType
import com.mj.core.notification.NotificationType as Type

interface CoreBridge {

    //noti
    fun fireNotification(type: Type)

    //permission
    fun permissionCheck(permission: String): Boolean
    fun checkExactAlarm(): Boolean
    fun requestPermission(activity: ComponentActivity, permission: String, action: (Boolean) -> Unit)
    fun requestPermissionSetting(activity: ComponentActivity, permission: String, action: () -> Unit): ActivityResultLauncher<Uri?>

    //alarm
    fun setAlarm(triggerTime: Long, type: ComponentType, intent: Intent)
    fun cancelAlarm(type: ComponentType, intent: Intent)
}