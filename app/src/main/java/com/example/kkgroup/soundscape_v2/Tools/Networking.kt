package com.example.kkgroup.soundscape_v2.Tools

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

object Networking {
    private const val BASE_URL = "http://resourcespace.tekniikanmuseo.fi/"
    private const val LOGIN_FAIL_RESPONSE = "Incorrect credentials! Try again."

    var API_TOKEN = ""

    interface NetworkServices {
        @POST("plugins/api_auth/auth.php")
        fun login(@Body user: JsonObject): Call<JsonObject>

        @GET("plugins/api_audio_search/index.php/")
        fun searchAudioFiles(@Query("key") key: String,
                             @Query("collection") collection: String,
                             @Query("search") search: String): Call<JsonArray>

        @GET ("plugins/api_audio_search/index.php/")
        fun getAllMp3FilesWithLink(@Query("key") key: String,
                                   @Query("link") link: String,
                                   @Query("format") format: String): Call<JsonArray>


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