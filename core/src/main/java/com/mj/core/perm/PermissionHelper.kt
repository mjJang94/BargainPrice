package com.mj.core.perm

import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PermissionHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun permissionCheck(permission: String): Boolean {
        return when (ContextCompat.checkSelfPermission(context, permission)) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }

    fun checkExactAlarmPerm(): Boolean {
        val am = context.getSystemService() as AlarmManager? ?: return false
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            am.canScheduleExactAlarms()
        }
    }
}