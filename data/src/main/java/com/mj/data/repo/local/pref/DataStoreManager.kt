package com.mj.data.repo.local.pref

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("settings")

class DataStoreManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        val PRICE_CHECK_ALARM_ACTIVATION_KEY = booleanPreferencesKey("PRICE_CHECK_ALARM_ACTIVATION_KEY")
        val RECENT_SEARCH_QUERIES = stringSetPreferencesKey("RECENT_SEARCH_QUERIES")
    }

    suspend fun storePriceCheckAlarmActivation(checked: Boolean) {
        context.dataStore.edit { store ->
            store[PRICE_CHECK_ALARM_ACTIVATION_KEY] = checked
        }
    }

    suspend fun storeRecentSearchQueries(queries: Set<String>) {
        context.dataStore.edit { store ->
            store[RECENT_SEARCH_QUERIES] = queries
        }
    }

    val priceCheckAlarmActiveFlow: Flow<Boolean> = context.dataStore.data.map {
        it[PRICE_CHECK_ALARM_ACTIVATION_KEY] ?: false
    }

    val recentSearchQueries: Flow<Set<String>> = context.dataStore.data.map {
        it[RECENT_SEARCH_QUERIES] ?: emptySet()
    }
}