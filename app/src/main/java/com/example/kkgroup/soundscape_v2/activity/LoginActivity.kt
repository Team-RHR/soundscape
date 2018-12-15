package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.Networking
import com.example.kkgroup.soundscape_v2.Tools.PrefManager
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * description: This Page refers to the login page, main functionality is to login
 * create time: 13:32 2018/12/15
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var prefManager: PrefManager
    private var mExitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_login)

        /**
         * use sharedPreference to detect if user is already logged in and has API key in shared preferences
         *
         */
        prefManager = PrefManager(this)
        if (prefManager.isApiKeySet()) {
            /**
             * set api token from sharedpreferences before continuing to another activity
             * Already logged in
             */
            Networking.API_TOKEN = prefManager.getApiKey()
            startActivity<MainActivity>()
            return
        }

        /**
         * login button listener
         * create json object that gets sent to API in a POST body
         */
        submitLoginBtn.setOnClickListener {

            val usernameInput = usrInput.text.toString()
            val passwordInput = passInput.text.toString()

            val json: JsonObject = JsonObject()
            json.addProperty("username", usernameInput)
            json.addProperty("password", passwordInput)

            callWebService(json)
        }

        /**
         * use Locale to set up application language, suomi or english
         */
        LocaleManager(this).getLocale()
    }

    /**
     * this is to prevent from going back in activity stack,
     * so if user clicks logout and is redirected to login page they cant go back to soundscapes activity
     */
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

    /**
     * do login using retrofit 2
     */
    private fun callWebService(json: JsonObject) {
        val call = Networking.service.login(json)

        val value = object: Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>?) {
                if (response != null) {
                    val res: JsonObject = response.body()!!
                    /**
                     * HACK: here we filter the inverted commas away from the response ("apikey" -> apikey)
                     */
                    val apiKey = res["api_key"].asString.filter { c:Char -> c.toString() != "\"" }

                    if (Networking.loginResponseValidation(apiKey)) {
                        Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_success))
                        Networking.API_TOKEN = apiKey
                        prefManager.setApiKey(Networking.API_TOKEN)
                        /**
                         * go to next screen, login succesfull
                         */
                        startActivity<MainActivity>()
                    } else {
                        /**
                         * show a toast, login failed
                         */
                        Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_fail))
                    }
                }
            }

            /**
             * this method gets called if the http call fails (no internet etc)
             */
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Tools.log_e("onFailure: " + t.toString())
                Tools.toastShow(this@LoginActivity, getString(R.string.toast_login_connection_fail))
            }
        }

        call.enqueue(value)
    }
}
