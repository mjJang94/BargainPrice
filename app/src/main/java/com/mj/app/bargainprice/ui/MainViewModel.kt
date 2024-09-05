package com.mj.app.bargainprice.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mj.data.repo.local.pref.ClientInfo
import com.mj.data.repo.local.pref.DataStoreManager
import com.navercorp.nid.profile.data.NidProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
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

    val proceed = dataStore.clientInformationFlow.map { it != null }
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily
        )
    fun configure(result: NidProfile?) {
        viewModelScope.launch {
            if (result == null) return@launch

            val clientInfo = ClientInfo(
                id = result.id,
                name = result.name,
                gender = result.gender,
                age = result.age,
                birthday = result.birthday,
                profileImage = result.profileImage,
                birthYear = result.birthYear,
            )
            dataStore.storeClientInformation(clientInfo)
        }
    }
}