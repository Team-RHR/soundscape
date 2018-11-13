package com.example.kkgroup.soundscape_v2.Tools

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

object Networking {
    private const val BASE_URL = "http://resourcespace.tekniikanmuseo.fi/"
    private const val LOGIN_FAIL_RESPONSE = "Incorrect credentials! Try again."

    var API_TOKEN = ""

    object NetworkModels {
        data class LoginUser(val username: String, val password: String)
    }

    interface NetworkServices {
        @POST("plugins/api_auth/auth.php")
        fun login(@Body user: JsonObject): Call<JsonObject>
    }

    fun loginResponseValidation(apiKey: String): Boolean {
        return apiKey != LOGIN_FAIL_RESPONSE
    }

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val service = retrofit.create(NetworkServices::class.java)!!
}