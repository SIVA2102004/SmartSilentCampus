package com.smartsilentcampus.data.local.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smart_silent_preferences")

@Singleton
class AppPreferencesDataStore @Inject constructor(
    private val context: Context
) {
    companion object {
        val KEY_AUTOMATION_ENABLED = booleanPreferencesKey("automation_enabled")
        val KEY_AUTOMATION_PAUSED_UNTIL = longPreferencesKey("automation_paused_until")
        val KEY_NOTIFICATIONS_ENTRY = booleanPreferencesKey("notifications_entry")
        val KEY_NOTIFICATIONS_EXIT = booleanPreferencesKey("notifications_exit")
        val KEY_NOTIFICATIONS_WARNINGS = booleanPreferencesKey("notifications_warnings")
        val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val KEY_TEST_MODE_ENABLED = booleanPreferencesKey("test_mode_enabled")
    }

    val isAutomationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTOMATION_ENABLED] ?: true
    }

    val automationPausedUntil: Flow<Long> = context.dataStore.data.map { preferences ->
        preferences[KEY_AUTOMATION_PAUSED_UNTIL] ?: 0L
    }

    val isEntryNotificationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_ENTRY] ?: true
    }

    val isExitNotificationEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_NOTIFICATIONS_EXIT] ?: true
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val isTestModeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_TEST_MODE_ENABLED] ?: false
    }

    suspend fun setAutomationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTOMATION_ENABLED] = enabled
        }
    }

    suspend fun pauseAutomationUntil(timestamp: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTOMATION_PAUSED_UNTIL] = timestamp
        }
    }

    suspend fun clearPause() {
        context.dataStore.edit { preferences ->
            preferences[KEY_AUTOMATION_PAUSED_UNTIL] = 0L
        }
    }

    suspend fun setEntryNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_ENTRY] = enabled
        }
    }

    suspend fun setExitNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_NOTIFICATIONS_EXIT] = enabled
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun setTestModeEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_TEST_MODE_ENABLED] = enabled
        }
    }
}
