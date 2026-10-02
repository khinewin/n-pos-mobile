package com.npos.app.data

import android.content.Context
import android.content.SharedPreferences

data class FirebaseConfig(
    val projectId: String = "ntgpos-47e62",
    val apiKey: String = "AIzaSyBkhkIJ0GkHANgBOcBtesbLNJDP-XGw7wU"
) {
    val isConfigured: Boolean
        get() = projectId.isNotBlank() && apiKey.isNotBlank()
}

class FirebaseConfigManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("npos_firebase_device_config", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PROJECT_ID = "custom_firebase_project_id"
        private const val KEY_API_KEY = "custom_firebase_api_key"
        private const val DEFAULT_PROJECT_ID = "ntgpos-47e62"
        private const val DEFAULT_API_KEY = "AIzaSyBkhkIJ0GkHANgBOcBtesbLNJDP-XGw7wU"
    }

    fun getConfig(): FirebaseConfig {
        val projId = prefs.getString(KEY_PROJECT_ID, DEFAULT_PROJECT_ID)?.ifBlank { DEFAULT_PROJECT_ID } ?: DEFAULT_PROJECT_ID
        val key = prefs.getString(KEY_API_KEY, DEFAULT_API_KEY)?.ifBlank { DEFAULT_API_KEY } ?: DEFAULT_API_KEY
        return FirebaseConfig(projectId = projId, apiKey = key)
    }

    fun saveConfig(projectId: String, apiKey: String): FirebaseConfig {
        val cleanProj = projectId.trim()
        val cleanKey = apiKey.trim()

        prefs.edit()
            .putString(KEY_PROJECT_ID, cleanProj)
            .putString(KEY_API_KEY, cleanKey)
            .apply()

        return FirebaseConfig(projectId = cleanProj, apiKey = cleanKey)
    }

    fun clearConfig(): FirebaseConfig {
        prefs.edit().clear().apply()
        return FirebaseConfig(projectId = "", apiKey = "")
    }
}
