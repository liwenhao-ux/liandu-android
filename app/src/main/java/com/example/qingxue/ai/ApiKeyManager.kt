package com.example.qingxue.ai

import android.content.Context
import com.example.qingxue.BuildConfig

object ApiKeyManager {
    private const val PREFS_NAME = "ai_prefs"
    private const val KEY_API_KEY = "deepseek_api_key"

    fun getApiKey(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_API_KEY, null)
            ?: BuildConfig.DEEPSEEK_API_KEY
            ?: ""
    }

    fun saveApiKey(context: Context, key: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_API_KEY, key)
            .apply()
    }

    fun hasApiKey(context: Context): Boolean = getApiKey(context).isNotBlank()
}
