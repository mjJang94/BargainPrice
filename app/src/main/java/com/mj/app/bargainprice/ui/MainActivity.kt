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
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.mj.app.bargainprice.core.PriceCheckReceiver
import com.mj.app.bargainprice.core.PriceCheckService
import com.mj.core.alarm.AlarmHelper
import com.mj.core.alarm.AlarmHelper.ComponentType
import com.mj.core.alarm.AlarmHelper.ComponentType.Receiver
import com.mj.core.ktx.Calendar
import com.mj.core.ktx.startOfNextDay
import com.mj.core.perm.PermissionHelper
import com.mj.core.theme.BargainPriceTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var perm: PermissionHelper

    @Inject
    lateinit var alarm: AlarmHelper

    companion object {
        fun start(context: Context) =
            context.startActivity(intent(context))

        fun intent(context: Context) = Intent(context, MainActivity::class.java)
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            viewModel.alarmActive
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { active ->
                    when (active) {
                        true -> when {
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> checkNotificationPermission()
                            else -> setAlarmSchedule()
                        }

                        else -> cancelAlarm()
                    }
                }
        }
        setContent {
            BargainPriceTheme {
                AppNavigation()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        when {
            perm.permissionCheck(Manifest.permission.POST_NOTIFICATIONS) -> when (perm.checkExactAlarmPerm()) {
                true -> {
                    Timber.d("All permissions granted!")
                    setAlarmSchedule()
                }

                else -> exactAlarmPermissionLauncher.launch(Uri.fromParts("package", packageName, null))
            }

            shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) ->
                postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

            else -> postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        }
    }

    private fun setAlarmSchedule() {
        val time = Calendar(System.currentTimeMillis()).startOfNextDay()
        alarm.set(
            triggerTime = time.timeInMillis,
            type = Receiver,
            intent = PriceCheckReceiver.intent(this@MainActivity)
        )
    }

    private fun cancelAlarm() {
        alarm.cancel(
            type = ComponentType.Service,
            intent = PriceCheckService.intent(this@MainActivity)
        )
    }


    private val postNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                exactAlarmPermissionLauncher.launch(Uri.fromParts("package", packageName, null))
            } else {
                Toast.makeText(this@MainActivity, "post notification permission denied", Toast.LENGTH_SHORT).show()
                cancelAlarm()
                viewModel.inactiveAlarm()
            }
        }

    private val exactAlarmPermissionLauncher =
        registerForActivityResult(object : ActivityResultContract<Uri?, Unit>() {
            @RequiresApi(Build.VERSION_CODES.S)
            override fun createIntent(context: Context, input: Uri?): Intent =
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply { data = input }

            override fun parseResult(resultCode: Int, intent: Intent?) = Unit
        }) {
            if (!perm.checkExactAlarmPerm()) {
                Toast.makeText(this@MainActivity, "exact alarm permission denied", Toast.LENGTH_SHORT).show()
                viewModel.inactiveAlarm()
            } else {
                setAlarmSchedule()
            }
        }
}
