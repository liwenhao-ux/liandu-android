package com.example.qingxue.ui.theme

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.themeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "theme_preferences"
)

class ThemePreferenceStore(context: Context) {
    private val dataStore = context.applicationContext.themeDataStore

    val accent: Flow<AppAccent> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { preferences -> AppAccent.fromStorage(preferences[ACCENT]) }

    suspend fun setAccent(accent: AppAccent) {
        dataStore.edit { preferences ->
            preferences[ACCENT] = accent.storageKey
        }
    }

    private companion object {
        val ACCENT = stringPreferencesKey("accent")
    }
}