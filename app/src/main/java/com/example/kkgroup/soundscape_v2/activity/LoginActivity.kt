package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.example.kkgroup.soundscape_v2.Model.SearchApiModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Networking
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private var mExitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        prefManager = PrefManager(this)
        // check if user is already logged in and has API key in shared preferences
        if (prefManager.isApiKeySet()) {
            // set api token from sharedpreferences before continuing to another activity
            Networking.API_TOKEN = prefManager.getApiKey()
            // TODO: go to next screen, user already logged in
            Tools.log_e("Already logged in")
            startActivity<LocalAudioFilesActivity>()
            return
        } else {
            Tools.log_e("Need to log in")
        }

        submitLoginBtn.setOnClickListener {
            val usernameInput = usrInput.text.toString()
            val passwordInput = passInput.text.toString()

            // create json object that gets sent to API in a POST body
            val json: JsonObject = JsonObject()
            json.addProperty("username", usernameInput)
            json.addProperty("password", passwordInput)

            callWebService(json)
        }
    }
    // this is to prevent from going back in activity stack, so if user clicks logout and is redirected to login page they cant go back to soundscapes activity
    override fun onBackPressed() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Tools.toastShow(this, getString(R.string.toast_press_again_exit))
            mExitTime = System.currentTimeMillis()
        } else {
            Tools.toastCancel()
            moveTaskToBack(true)
            // finish()
        }
    }

    private fun callWebService(json: JsonObject) {
        val call = Networking.service.login(json)

        val value = object: Callback<JsonObject> {
            // this method gets called after a http call, no matter the http code
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>?) {
                if (response != null) {
                    val res: JsonObject = response.body()!!

                    // HACK: here we filter the inverted commas away from the response ("apikey" -> apikey)
                    val apiKey = res["api_key"].asString.filter { c:Char -> c.toString() != "\"" }

                    if (Networking.loginResponseValidation(apiKey)) {
                        Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_success))
                        Networking.API_TOKEN = apiKey
                        prefManager.setApiKey(Networking.API_TOKEN)
                        // TODO: go to next screen, login succesfull
                        startActivity<LocalAudioFilesActivity>()
                    } else {
                        Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_fail))
                    }
                }
            }

            // this method gets called if the http call fails (no internet etc)
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Tools.log_e("onFailure: " + t.toString())
                Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_connection_fail))
            }
        }

        call.enqueue(value) // asynchronous request
    }
}
