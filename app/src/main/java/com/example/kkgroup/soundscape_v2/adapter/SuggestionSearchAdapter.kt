package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.content.SharedPreferences
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.R
import com.google.gson.Gson
import java.io.Serializable
import java.util.*

class SuggestionSearchAdapter(context: Context) : RecyclerView.Adapter<SuggestionSearchAdapter.ViewHolder>() {

    private var items: List<String> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    private val SEARCH_HISTORY_KEY = "_SEARCH_HISTORY_KEY"
    private val MAX_HISTORY_ITEMS = 5

    // using SharedPreferences to store our search history
    private val prefs: SharedPreferences = context.getSharedPreferences("PREF_RECENT_SEARCH", Context.MODE_PRIVATE)

    private val searchHistory: MutableList<String>
        get() {
            val json = prefs.getString(SEARCH_HISTORY_KEY, "")
            if (json == "") {
                return ArrayList()
            } else {
                val searchObject = Gson().fromJson(json, SearchObject::class.java)
                return searchObject.items
            }
        }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var title = v.findViewById<View>(R.id.title) as TextView
        var iv_delete = v.findViewById<View>(R.id.iv_delete) as ImageView
        var lyt_parent = v.findViewById<View>(R.id.lyt_parent) as LinearLayout
    }

    init {
        this.items = searchHistory
        Collections.reverse(this.items)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_suggestion, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = items[position]
        holder.title.text = p
        holder.lyt_parent.setOnClickListener { v -> onItemClickListener!!.onItemClick(v, p, position) }

        holder.iv_delete.setOnClickListener {
            removeSearchHistory(holder.title.text.trim().toString())
        }
    }

    /**
     * Return the size of your dataset (invoked by the layout manager)
     */
    override fun getItemCount(): Int {
        return items.size
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, viewModel: String, pos: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    /**
     * refresh recyclerView after datalist has been changed
     */
    fun refreshItems() {
        this.items = searchHistory
        Collections.reverse(this.items)
        notifyDataSetChanged()
    }

    /**
     * To save last state request
     */
    fun addSearchHistory(s: String) {
        val searchObject = SearchObject(searchHistory)
        if (searchObject.items.contains(s)) searchObject.items.remove(s)
        searchObject.items.add(s)
        if (searchObject.items.size > MAX_HISTORY_ITEMS) searchObject.items.removeAt(0)
        val json = Gson().toJson(searchObject, SearchObject::class.java)
        prefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()
    }

    /**
     * To remove one history item
     */
    private fun removeSearchHistory(s: String) {
        val searchObject = SearchObject(searchHistory)
        if (searchObject.items.contains(s)) {
            searchObject.items.remove(s)
        }
        val json = Gson().toJson(searchObject, SearchObject::class.java)
        prefs.edit().putString(SEARCH_HISTORY_KEY, json).apply()

        refreshItems()
    }

    /**
     * Private SearchObject for SharedPreferences
     */
    private inner class SearchObject(items: MutableList<String>) : Serializable {
        var items: MutableList<String> = ArrayList()
        init {
            this.items = items
        }
    }
}