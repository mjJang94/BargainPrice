package com.mj.data.repo.local.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mj.data.mapper.parseJsonOrNull
import com.mj.data.mapper.toJson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val CLIENT_INFORMATION_KEY = stringPreferencesKey("CLIENT_INFORMATION_KEY")
        val PRICE_CHECK_ALARM_ACTIVATION_KEY = booleanPreferencesKey("PRICE_CHECK_ALARM_ACTIVATION_KEY")
        val RECENT_SEARCH_QUERIES_KEY = stringSetPreferencesKey("RECENT_SEARCH_QUERIES_KEY")
        val RECENT_REFRESH_TIME = longPreferencesKey("RECENT_REFRESH_TIME")
    }

    suspend fun storeClientInformation(client: ClientInfo) {
        context.dataStore.edit { store ->
            store[CLIENT_INFORMATION_KEY] = client.toJson()
        }
    }

    suspend fun storePriceCheckAlarmActivation(checked: Boolean) {
        context.dataStore.edit { store ->
            store[PRICE_CHECK_ALARM_ACTIVATION_KEY] = checked
        }
    }

    suspend fun storeRecentSearchQueries(queries: Set<String>) {
        context.dataStore.edit { store ->
            store[RECENT_SEARCH_QUERIES_KEY] = queries
        }
    }

    suspend fun storeRecentRefreshTime(time: Long) {
        context.dataStore.edit { store ->
            store[RECENT_REFRESH_TIME] = time
        }
    }

    val clientInformationFlow: Flow<ClientInfo?> = context.dataStore.data.map {
        it[CLIENT_INFORMATION_KEY]?.parseJsonOrNull<ClientInfo>()
    }

    val priceCheckAlarmActiveFlow: Flow<Boolean> = context.dataStore.data.map {
        it[PRICE_CHECK_ALARM_ACTIVATION_KEY] ?: false
    }

    val recentSearchQueriesFlow: Flow<Set<String>> = context.dataStore.data.map {
        it[RECENT_SEARCH_QUERIES_KEY] ?: emptySet()
    }

    val recentRefreshTimeFlow: Flow<Long> = context.dataStore.data.map {
        it[RECENT_REFRESH_TIME] ?: 0L
    }
}