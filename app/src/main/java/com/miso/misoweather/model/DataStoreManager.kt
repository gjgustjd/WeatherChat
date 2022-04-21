package com.miso.misoweather.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.settingDataStore: DataStore<Preferences> by preferencesDataStore(
        name = "misoweather",
        produceMigrations = { context ->
            listOf(SharedPreferencesMigration(context, "misoweather"))
        })

    companion object {
        val SOCIAL_ID = stringPreferencesKey("socialId")
        val SOCIAL_TYPE = stringPreferencesKey("socialType")
        val ACCESS_TOKEN = stringPreferencesKey("accessToken")
        val MISO_TOKEN = stringPreferencesKey("misoToken")
        val SURVEY_REGION = stringPreferencesKey("surveyRegion")
        val BIGSCALE_REGION = stringPreferencesKey("BigScaleRegion")
        val MIDSCALE_REGION = stringPreferencesKey("MidScaleRegion")
        val SMALLSCALE_REGION = stringPreferencesKey("SmallScaleRegion")
        val DEFAULT_REGION_ID = stringPreferencesKey("defaultRegionId")
        val IS_SURVEYED = stringPreferencesKey("isSurveyed")
        val LAST_SURVEYED_DATE = stringPreferencesKey("LastSurveyedDate")
        val EMOJI = stringPreferencesKey("emoji")
        val NICKNAME = stringPreferencesKey("nickname")
    }

    fun getPreferenceAsFlow(pref: Preferences.Key<String>) =
        context.settingDataStore.data.map {
            it[pref] ?: ""
        }

    fun getPreference(pref: Preferences.Key<String>) = runBlocking {
        getPreferenceAsFlow(pref).first()
    }

    fun savePreference(key: Preferences.Key<String>, value: String) {
        CoroutineScope(Dispatchers.IO).launch()
        {
            context.settingDataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    fun removePreference(key: Preferences.Key<String>) {
        CoroutineScope(Dispatchers.IO).launch()
        {
            context.settingDataStore.edit { preferences ->
                preferences[key] = ""
            }
        }
    }

    fun removePreferences(vararg keys: Preferences.Key<String>) {
        for (element in keys) {
            removePreference(element)
        }
    }
}