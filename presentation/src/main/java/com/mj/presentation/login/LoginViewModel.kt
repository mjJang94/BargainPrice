package com.mj.presentation.login

import androidx.lifecycle.ViewModel
import com.mj.core.base.BaseViewModel
import com.mj.presentation.login.LoginContract.Effect
import com.mj.presentation.login.LoginContract.Event
import com.mj.presentation.login.LoginContract.State
import timber.log.Timber


class LoginViewModel : BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State(
        tt = ""
    )

    override fun handleEvents(event: Event) {
        Timber.d("handleEvent : $event")
        when (event) {
            is Event.Login -> setEffect { Effect.Login }
        }
    }

    private fun setABTest() {
//        val remoteConfig = Firebase.remoteConfig
//        val configSettings = remoteConfigSettings {
//            minimumFetchIntervalInSeconds = 3600
//        }
//        remoteConfig.setConfigSettingsAsync(configSettings)
//        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
//
//        remoteConfig.fetchAndActivate()
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val btnSnack = findViewById<TextView>(R.id.btnSnack)
//                    val btnFloating = findViewById<FloatingActionButton>(R.id.btnFloating)
//
//                    val data = remoteConfig.getString("button_style")
//                    when(data){
//                        "floating_button" -> {
//                            btnSnack.visibility = View.GONE
//                            btnFloating.visibility = View.VISIBLE
//                        }
//                        "snack_button" -> {
//                            btnSnack.visibility = View.VISIBLE
//                            btnFloating.visibility = View.GONE
//                        }
//                    }
//                } else {
//                    Log.d(TAG, "Fail")
//                }
//            }
    }
}