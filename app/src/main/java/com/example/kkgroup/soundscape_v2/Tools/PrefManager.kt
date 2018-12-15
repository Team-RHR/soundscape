package com.example.kkgroup.soundscape_v2.Tools

import android.content.Context
import android.content.SharedPreferences

/**
 * description: SharedPreference manager, mainly used for storing the api key and lanaguage selection
 * create time: 15:33 2018/12/15
 */
class PrefManager(context: Context) {
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private val API_KEY = "apikey"
    private val LOCALE_KEY = "locale"

    init {
        pref = context.getSharedPreferences("soundscape_v2", Context.MODE_PRIVATE)
        editor = pref!!.edit()
    }

    fun setApiKey(api: String?) {
        editor!!.putString(API_KEY, api).commit()
    }

    fun getApiKey(): String {
        return pref!!.getString(API_KEY, null)
    }

    fun isApiKeySet(): Boolean {
        return pref!!.getString(API_KEY, null) != null
    }

    fun setLocale(locale: String) {
        Tools.log_e(locale)
        editor!!.putString(LOCALE_KEY, locale).commit()
    }

    fun getLocale(): String {
        Tools.log_e(pref!!.getString(LOCALE_KEY, "us"))
        return pref!!.getString(LOCALE_KEY, "us")
    }
}