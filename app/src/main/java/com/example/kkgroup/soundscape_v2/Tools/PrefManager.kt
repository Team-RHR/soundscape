package com.example.kkgroup.soundscape_v2.Tools

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private var pref: SharedPreferences? = null
    private var editor: SharedPreferences.Editor? = null

    private val API_KEY = "apikey"

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
        if (pref!!.getString(API_KEY, null) == null) {
            return false
        } else {
            return true
        }
    }
}