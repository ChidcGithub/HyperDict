package com.hyperdict.app.data.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class AppSettings(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "hyperdict_settings",
        Context.MODE_PRIVATE
    )

    // Search settings
    var autoSearch by mutableStateOf(prefs.getBoolean(KEY_AUTO_SEARCH, true))
        private set

    var searchHistoryLimit by mutableStateOf(prefs.getInt(KEY_SEARCH_HISTORY_LIMIT, 50))
        private set

    // Display settings
    var darkTheme by mutableStateOf(prefs.getBoolean(KEY_DARK_THEME, false))
        private set

    var dynamicColor by mutableStateOf(prefs.getBoolean(KEY_DYNAMIC_COLOR, true))
        private set

    var fontSizeScale by mutableStateOf(prefs.getFloat(KEY_FONT_SIZE_SCALE, 1.0f))
        private set

    // Dictionary settings
    var preferOffline by mutableStateOf(prefs.getBoolean(KEY_PREFER_OFFLINE, true))
        private set

    var showUkPhonetic by mutableStateOf(prefs.getBoolean(KEY_SHOW_UK_PHONETIC, true))
        private set

    var showUsPhonetic by mutableStateOf(prefs.getBoolean(KEY_SHOW_US_PHONETIC, true))
        private set

    // Network settings
    var wifiOnlyDownload by mutableStateOf(prefs.getBoolean(KEY_WIFI_ONLY_DOWNLOAD, true))
        private set

    // Save settings
    fun updateAutoSearch(value: Boolean) {
        autoSearch = value
        prefs.edit().putBoolean(KEY_AUTO_SEARCH, value).apply()
    }

    fun updateSearchHistoryLimit(value: Int) {
        searchHistoryLimit = value
        prefs.edit().putInt(KEY_SEARCH_HISTORY_LIMIT, value).apply()
    }

    fun updateDarkTheme(value: Boolean) {
        darkTheme = value
        prefs.edit().putBoolean(KEY_DARK_THEME, value).apply()
    }

    fun updateDynamicColor(value: Boolean) {
        dynamicColor = value
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, value).apply()
    }

    fun updateFontSizeScale(value: Float) {
        fontSizeScale = value.coerceIn(0.8f, 1.5f)
        prefs.edit().putFloat(KEY_FONT_SIZE_SCALE, fontSizeScale).apply()
    }

    fun updatePreferOffline(value: Boolean) {
        preferOffline = value
        prefs.edit().putBoolean(KEY_PREFER_OFFLINE, value).apply()
    }

    fun updateShowUkPhonetic(value: Boolean) {
        showUkPhonetic = value
        prefs.edit().putBoolean(KEY_SHOW_UK_PHONETIC, value).apply()
    }

    fun updateShowUsPhonetic(value: Boolean) {
        showUsPhonetic = value
        prefs.edit().putBoolean(KEY_SHOW_US_PHONETIC, value).apply()
    }

    fun updateWifiOnlyDownload(value: Boolean) {
        wifiOnlyDownload = value
        prefs.edit().putBoolean(KEY_WIFI_ONLY_DOWNLOAD, value).apply()
    }

    companion object {
        private const val KEY_AUTO_SEARCH = "auto_search"
        private const val KEY_SEARCH_HISTORY_LIMIT = "search_history_limit"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_FONT_SIZE_SCALE = "font_size_scale"
        private const val KEY_PREFER_OFFLINE = "prefer_offline"
        private const val KEY_SHOW_UK_PHONETIC = "show_uk_phonetic"
        private const val KEY_SHOW_US_PHONETIC = "show_us_phonetic"
        private const val KEY_WIFI_ONLY_DOWNLOAD = "wifi_only_download"
    }
}
