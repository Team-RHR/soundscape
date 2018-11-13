package com.example.kkgroup.soundscape_v2.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Networking
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        submitLoginBtn.setOnClickListener {
            val usernameInput = usrInput.text.toString()
            val passwordInput = passInput.text.toString()

            Log.d("DBG", "USR: $usernameInput PASS: $passwordInput")

            // create json object that gets sent to API in a POST body
            val json: JsonObject = JsonObject()
            json.addProperty("username", usernameInput)
            json.addProperty("password", passwordInput)

            Log.d("DBG", "b4 retrofit: ${Networking.API_TOKEN}")

            callWebService(json)
        }
    }

    private fun callWebService(json: JsonObject) {
        val call = Networking.service.login(json)

        val value = object: Callback<JsonObject> {

            // this method gets called after a http call, no matter the http code
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>?) {
                if (response != null) {
                    val res: JsonObject = response.body()!!

                    val apiKey = res["api_key"].asString

                    if (Networking.loginResponseValidation(apiKey)) {
                        Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()

                        Networking.API_TOKEN = res["api_key"].toString()

                        Log.d("DBG", "after retrofit: ${Networking.API_TOKEN}")

                        // TODO: go to next screen, login succesfull
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // this method gets called if the http call fails (no internet etc)
            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("DBG", "onFailure: " + t.toString())
                Toast.makeText(this@LoginActivity, "There was a problem trying to log in", Toast.LENGTH_SHORT).show()
            }
        }

        call.enqueue(value) // asyncronous request
    }

}
