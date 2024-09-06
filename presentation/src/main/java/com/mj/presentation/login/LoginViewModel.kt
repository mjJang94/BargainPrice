package com.mj.presentation.login

import androidx.lifecycle.viewModelScope
import com.mj.core.base.BaseViewModel
import com.mj.domain.usecase.login.CombinedLoginUseCases
import com.mj.presentation.login.LoginContract.Effect
import com.mj.presentation.login.LoginContract.Event
import com.mj.presentation.login.LoginContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val combinedLoginUseCases: CombinedLoginUseCases
) : BaseViewModel<Event, State, Effect>() {

    override fun setInitialState() = State(
        showLogin = MutableStateFlow(false)
    )

    override fun handleEvents(event: Event) {
        when (event) {
            is Event.Login -> setEffect { Effect.Login }
            is Event.Skip -> setEffect { Effect.Skip }
        }
    }

    fun checkRequireLogin() {
        viewModelScope.launch {
            val skipLogin = combinedLoginUseCases.getSkipLoginUseCase().firstOrNull() ?: false
            delay(2000L)
            setState { copy(showLogin = MutableStateFlow(!skipLogin)) }
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