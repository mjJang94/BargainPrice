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

object Perm {

    fun Context.permissionCheck(permission: String): Boolean {
        return when (ContextCompat.checkSelfPermission(this, permission)) {
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }
    }

    fun ComponentActivity.requestPermission(permission: String, action: (Boolean) -> Unit) {
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { action(it) }.launch(permission)
    }


    fun Context.checkExactAlarmPerm(): Boolean {
        val am = getSystemService() as AlarmManager? ?: return false
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            am.canScheduleExactAlarms()
        }
    }

    fun ComponentActivity.requestPermissionSetting(
        permission: String, action: () -> Unit
    ) = registerForActivityResult(
        object : ActivityResultContract<Uri?, Unit>() {
            override fun createIntent(context: Context, input: Uri?): Intent =
                Intent(permission)
                    .apply { data = input }

            override fun parseResult(resultCode: Int, intent: Intent?) = Unit
        }
    ) {
        action()
    }
}