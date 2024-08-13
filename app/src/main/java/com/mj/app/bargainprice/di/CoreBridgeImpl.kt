package com.mj.app.bargainprice.di

import android.content.Intent
import androidx.activity.ComponentActivity
import com.mj.core.alarm.AlarmHelper
import com.mj.core.notification.NotificationHelper
import com.mj.core.notification.NotificationType
import com.mj.core.perm.PermissionHelper
import javax.inject.Inject

class CoreBridgeImpl @Inject constructor(
    private val notificationHelper: NotificationHelper,
    private val permissionHelper: PermissionHelper,
    private val alarmHelper: AlarmHelper,
) : CoreBridge {

    override fun fireNotification(type: NotificationType) =
        notificationHelper.showNotification(type)

    override fun permissionCheck(permission: String): Boolean =
        permissionHelper.permissionCheck(permission)

    override fun checkExactAlarm(): Boolean =
        permissionHelper.checkExactAlarmPerm()

    override fun requestPermission(activity: ComponentActivity, permission: String, action: (Boolean) -> Unit) =
        permissionHelper.requestPermission(activity, permission, action)

    override fun requestPermissionSetting(activity: ComponentActivity, permission: String, action: () -> Unit) =
        permissionHelper.requestPermissionSetting(activity, permission, action)

    override fun setAlarm(triggerTime: Long, type: AlarmHelper.ComponentType, intent: Intent) =
        alarmHelper.set(triggerTime, type, intent)

    override fun cancelAlarm(type: AlarmHelper.ComponentType, intent: Intent) =
        alarmHelper.cancel(type, intent)

}