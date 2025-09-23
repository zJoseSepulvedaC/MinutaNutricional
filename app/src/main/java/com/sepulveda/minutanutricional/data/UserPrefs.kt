package com.sepulveda.minutanutricional.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPrefs {
    private val KEY_REMEMBER = booleanPreferencesKey("remember_session")
    private val KEY_LAST_EMAIL = stringPreferencesKey("last_email")

    fun rememberFlow(context: Context): Flow<Boolean> =
        context.dataStore.data.map { it[KEY_REMEMBER] ?: false }

    fun lastEmailFlow(context: Context): Flow<String> =
        context.dataStore.data.map { it[KEY_LAST_EMAIL] ?: "" }

    suspend fun setRemember(context: Context, value: Boolean) {
        context.dataStore.edit { it[KEY_REMEMBER] = value }
    }

    suspend fun setLastEmail(context: Context, email: String) {
        context.dataStore.edit { it[KEY_LAST_EMAIL] = email }
    }

    suspend fun clearSession(context: Context) {
        context.dataStore.edit {
            it[KEY_REMEMBER] = false
            it[KEY_LAST_EMAIL] = ""
        }
    }
}
