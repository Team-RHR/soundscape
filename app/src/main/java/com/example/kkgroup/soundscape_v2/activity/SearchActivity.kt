package com.example.kkgroup.soundscape_v2.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import com.example.kkgroup.soundscape_v2.Model.SearchApiModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Networking
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.SearchItemAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mSearchItemAdapter: SearchItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.searchRecyclerView)

        // click listener for search button in phone keyboard
        search_input.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //Log.d("DBG", search_input.text.toString())
                searchAudioFiles()
            }
            true
        }
    }

    private fun searchAudioFiles() {
        val call = Networking.service.searchAudioFiles(Networking.API_TOKEN, "22", search_input.text.toString())

        val value = object: retrofit2.Callback<JsonArray> {

            // this method gets called after a http call, no matter the http code
            override fun onResponse(call: retrofit2.Call<JsonArray>, response: retrofit2.Response<JsonArray>?) {
                if (response != null) {
                    Tools.toastShow(this@SearchActivity, "Search successfull")

                    /** here we filter the response and alter the json so format is
                     * [
                     *  {},
                     *  {}
                     * ]
                     *
                     * instead of
                     *
                     * [
                     *  [{}],
                     *  [{}]
                     * ]
                     */
                    val res = "[" + response.body().toString().filter { c:Char -> (c.toString() != "[" && c.toString() != "]")  } + "]"

                    /**
                     * here we create a array of SearchApiModels that we can better use for adapters etc.
                     */
                    val gson = GsonBuilder()
                            .setLenient()       // fix parse json failed on android 6.0 by setLenient()
                            .create()
                    val model: Array<SearchApiModel> = gson.fromJson(res, Array<SearchApiModel>::class.java)

                    showSearchResults(model)

                }
            }

            // this method gets called if the http call fails (no internet etc)
            override fun onFailure(call: retrofit2.Call<JsonArray>, t: Throwable) {
                Tools.toastShow(this@SearchActivity, "Search failed, check your network")
                Tools.log_e("${t.message}")
            }
        }
        call.enqueue(value) // asynchronous request
    }

    fun showSearchResults(searchList: Array<SearchApiModel>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        //set data and list adapter
        mSearchItemAdapter = SearchItemAdapter(this, searchList, ItemAnimation.FADE_IN)
        recyclerView.adapter = mSearchItemAdapter

        // on item list clicked
        mSearchItemAdapter.setOnItemClickListener(object : SearchItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: SearchItemAdapter, position: Int) {
                Log.d("DBG", "Clicked ${obj.items[position].title}")
                // TODO: make the item selected go to the "create soundscape" activity
            }
        })
    }
}
