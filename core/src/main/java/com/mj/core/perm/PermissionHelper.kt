package com.mj.core.perm

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import javax.inject.Inject

class PermissionHelper @Inject constructor(private val context: Context) {

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

    fun requestPermission(activity: ComponentActivity, permission: String, action: (Boolean) -> Unit) {
        activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { action(it) }.launch(permission)
    }

    fun requestPermissionSetting(
        activity: ComponentActivity,
        permission: String, action: () -> Unit,
    ) = activity.registerForActivityResult(
        object : ActivityResultContract<Uri?, Unit>() {
            override fun createIntent(context: Context, input: Uri?): Intent =
                Intent(permission).apply { data = input }

            override fun parseResult(resultCode: Int, intent: Intent?) = Unit
        }
    ) {
        action()
    }
}