package com.npos.app.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppThemeMode(val id: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark");

    fun getDisplayName(isMm: Boolean): String = when (this) {
        SYSTEM -> if (isMm) "စနစ်အတိုင်း (System)" else "System Default"
        LIGHT -> if (isMm) "အလင်းရောင် (Light)" else "Light Mode"
        DARK -> if (isMm) "အမှောင်ရောင် (Dark)" else "Dark Mode"
    }
}

enum class AppLanguage(val code: String, val displayName: String, val shortName: String, val flag: String) {
    MM("mm", "မြန်မာစာ", "MM", "🇲🇲"),
    EN("en", "English", "EN", "🇬🇧");

    val isBurmese: Boolean get() = this == MM
}

class AppSettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("npos_app_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_THEME_MODE = "app_theme_mode"
        private const val KEY_LANGUAGE = "app_language"

        private val _globalThemeMode = MutableStateFlow(AppThemeMode.SYSTEM)
        val globalThemeMode: StateFlow<AppThemeMode> = _globalThemeMode.asStateFlow()

        private val _globalLanguage = MutableStateFlow(AppLanguage.MM)
        val globalLanguage: StateFlow<AppLanguage> = _globalLanguage.asStateFlow()
    }

    init {
        _globalThemeMode.value = getThemeMode()
        _globalLanguage.value = getLanguage()
    }

    fun getThemeMode(): AppThemeMode {
        val id = prefs.getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.id) ?: AppThemeMode.SYSTEM.id
        return AppThemeMode.values().find { it.id == id } ?: AppThemeMode.SYSTEM
    }

    fun setThemeMode(mode: AppThemeMode) {
        _globalThemeMode.value = mode
        prefs.edit().putString(KEY_THEME_MODE, mode.id).apply()
    }

    fun getLanguage(): AppLanguage {
        val code = prefs.getString(KEY_LANGUAGE, AppLanguage.MM.code) ?: AppLanguage.MM.code
        return AppLanguage.values().find { it.code == code } ?: AppLanguage.MM
    }

    fun setLanguage(language: AppLanguage) {
        _globalLanguage.value = language
        prefs.edit().putString(KEY_LANGUAGE, language.code).apply()
    }
}
