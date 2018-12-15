package com.example.kkgroup.soundscape_v2.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.Model.SearchApiModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.LocaleManager
import com.example.kkgroup.soundscape_v2.Tools.Networking
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.adapter.SearchItemAdapter
import com.example.kkgroup.soundscape_v2.adapter.SuggestionSearchAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.startActivity
import java.io.File

/**
 * description: This activity is for Searching function
 * create time: 14:00 2018/12/15
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var mAdapterSuggestion: SuggestionSearchAdapter
    private lateinit var mSearchItemAdapter: SearchItemAdapter
    private lateinit var lytSuggestion: LinearLayout
    private var searchResultCount = 0

    /**
     * This Interface is used for adding audio item from search result to new soundscape page
     */
    companion object {
        private var myAddToTrackListener: SearchActivity.AddToTrackListener? = null
        fun setMyAddToTrackListener(myAddToTrackListener: AddToTrackListener) {
            this.myAddToTrackListener = myAddToTrackListener
        }
    }

    interface AddToTrackListener {
        fun addToTrack(trackNum: Int, audioCardModel: AudioCardModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LocaleManager(this).getLocale()
        setContentView(R.layout.activity_search)

        Tools.updateAudioFiles()
        initComponent()
        initListeners()
    }

    private fun initComponent() {
        bt_clear.visibility = View.GONE
        lytSuggestion = findViewById(R.id.lyt_suggestion)
        recyclerView = findViewById(R.id.searchResultRecyclerView)
        recyclerSuggestion.layoutManager = LinearLayoutManager(this)
        recyclerSuggestion.setHasFixedSize(true)

        /**
         * set data and list adapter suggestion
         */
        mAdapterSuggestion = SuggestionSearchAdapter(this)
        recyclerSuggestion.adapter = mAdapterSuggestion
        showSuggestionSearch()
    }

    private fun initListeners() {
        /**
         * when text is not empty, then show clear button, otherwise disappear clear button
         */
        search_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {}

            override fun beforeTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(c: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (c.toString().trim().isEmpty()) {
                    bt_clear.visibility = View.GONE
                } else {
                    bt_clear.visibility = View.VISIBLE
                }
            }
        })

        /**
         * Handle click event when user clicks the item from the search history list
         */
        mAdapterSuggestion.setOnItemClickListener(object : SuggestionSearchAdapter.OnItemClickListener {
            override fun onItemClick(view: View, viewModel: String, pos: Int) {
                search_input.setText(viewModel)
                Tools.viewCollapse(lytSuggestion)
                hideKeyboard()
                searchAction()
            }
        })

        bt_clear.setOnClickListener { search_input.text.clear() }

        bt_back.setOnClickListener { this.finish() }

        /**
         * Start searching when user clicks search on the keyboard
         */
        search_input.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
                searchAction()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        /**
         * When user touches the input bar, then popup the search history list
         */
        search_input.setOnTouchListener { view, motionEvent ->
            showSuggestionSearch()
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
            return@setOnTouchListener false
        }
    }

    /**
     * Show progress bar while loading search result
     */
    private fun searchAction() {
        progress_bar.visibility = View.VISIBLE
        Tools.viewCollapse(lytSuggestion)
        lyt_no_result.visibility = View.GONE

        val query = search_input.text.toString().trim { it <= ' ' }
        if (query != "") {
            Handler().postDelayed({
                progress_bar.visibility = View.GONE
                searchAudioFiles()
            }, 2000)
            mAdapterSuggestion.addSearchHistory(query)
        } else {
            Tools.toastShow(this, "Please fill search input first")
        }
    }

    /**
     * hide Keyboard
     */
    private fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Popup the search history list
     */
    private fun showSuggestionSearch() {
        mAdapterSuggestion.refreshItems()
        Tools.viewExpand(lytSuggestion)
    }

    /**
     * Loading search result based on keyword
     */
    private fun searchAudioFiles() {
        val call = Networking.service.searchAudioFiles(Networking.API_TOKEN, "29", "mp3", search_input.text.trim().toString())

        val value = object : retrofit2.Callback<JsonArray> {
            override fun onResponse(call: retrofit2.Call<JsonArray>,
                                    response: retrofit2.Response<JsonArray>?) {

                response?.let {
                    if (response.isSuccessful) {
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
                        val res = "[" + response.body().toString().filter { c: Char -> (c.toString() != "[" && c.toString() != "]") } + "]"

                        /**
                         * here we create a array of SearchApiModels that we can better use for adapters etc.
                         */
                        val gson = GsonBuilder()
                                .setLenient()       // fix parse json failed on android 6.0 by setLenient()
                                .create()
                        val model: Array<SearchApiModel> = gson.fromJson(res, Array<SearchApiModel>::class.java)

                        showSearchResults(model)

                        searchResultCount = model.size

                        if (model.isNotEmpty()) {
                            searchResultRecyclerView.visibility = View.VISIBLE
                            lyt_no_result.visibility = View.GONE
                        } else {
                            searchResultRecyclerView.visibility = View.GONE
                            lyt_no_result.visibility = View.VISIBLE
                        }
                    }
                }
            }

            /**
             * this method gets called if the http call fails (no internet etc)
             */
            override fun onFailure(call: retrofit2.Call<JsonArray>, t: Throwable) {
                Tools.toastShow(this@SearchActivity, "Search failed, check your network")
                Tools.log_e("${t.message}")
                searchResultCount = 0

                searchResultRecyclerView.visibility = View.GONE
                lyt_no_result.visibility = View.VISIBLE
            }

        }
        call.enqueue(value)
    }

    /**
     * Set data and list adapter
     */
    fun showSearchResults(searchList: Array<SearchApiModel>) {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        mSearchItemAdapter = SearchItemAdapter(this, searchList, ItemAnimation.FADE_IN)
        recyclerView.adapter = mSearchItemAdapter

        /**
         * on item list clicked, then go to the preview page
         */
        mSearchItemAdapter.setOnItemClickListener(object : SearchItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, obj: SearchApiModel, position: Int) {
                Tools.getAudioPathByObj(obj)?.let {
                    startActivity<PlayActivity>("obj" to File(it),
                            "category" to obj.category,
                            "title" to obj.title)
                }
            }
        })

        /**
         * Show a dialog when "add to track" button clicked
         */
        mSearchItemAdapter.setOnAddToTrackListener(object : SearchItemAdapter.OnAddToTrackListener {
            override fun onAddToTrack(view: View, obj: SearchApiModel, position: Int) {
                if (Tools.getAudioPathByObj(obj) != null) {
                    showSingleChoiceDialog(obj, view as ImageView)
                }
            }
        })
    }

    /**
     * Show single choice dialog, and pass the selected audio to the new soundscape page
     * Each color stands for each category
     */
    private var single_choice_selected: String? = null
    private val trackNumbers = arrayOf("1", "2")
    private val trackStrs = arrayOf("Track 1", "Track 2")
    private fun showSingleChoiceDialog(searchApiModel: SearchApiModel, imageView: ImageView) {
        single_choice_selected = trackNumbers[0]
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add this audio to")
        builder.setSingleChoiceItems(trackStrs, 0) { _, i ->
            single_choice_selected = trackNumbers[i]
        }

        builder.setPositiveButton("OK") { _, i ->
            var bgColor: Int
            when(searchApiModel.category){
                "human" -> { bgColor = R.color.teal_700 }
                "machine" -> { bgColor = R.color.blue_700 }
                "nature" -> { bgColor = R.color.green_700 }
                else -> { bgColor = R.color.deep_orange_500 }
            }
            val audioCardModel = AudioCardModel(searchApiModel.category.toString(), File(Tools.getAudioPathByObj(searchApiModel)), bgColor)
            myAddToTrackListener?.addToTrack(single_choice_selected!!.toInt(), audioCardModel)
            imageView.setImageDrawable(resources.getDrawable(R.drawable.ic_playlist_add_check))
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

}
