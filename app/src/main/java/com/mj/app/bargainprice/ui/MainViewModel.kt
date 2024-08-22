package com.mj.app.bargainprice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.data.repo.local.pref.DataStoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val dataStore: DataStoreManager
) : ViewModel() {

    val alarmActive = dataStore.priceCheckAlarmActiveFlow
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
        )

    fun inactiveAlarm() {
        viewModelScope.launch {
            Timber.d("inactiveAlarm()")
            dataStore.storePriceCheckAlarmActivation(false)
        }
    }
}