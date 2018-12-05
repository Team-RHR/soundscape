package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.Model.LibraryCategoryModel
import com.example.kkgroup.soundscape_v2.R
import java.util.*

class LibraryCategoryAdapter(private val ctx: Context,
                             private val items: ArrayList<LibraryCategoryModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, obj: LibraryCategoryModel, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var image: ImageView
        var categoryName: TextView
        var lyt_bottom: LinearLayout
        var lyt_parent: View

        init {
            image = v.findViewById<View>(R.id.image) as ImageView
            categoryName = v.findViewById<View>(R.id.categoryName) as TextView
            lyt_bottom = v.findViewById(R.id.lyt_bottom) as LinearLayout
            lyt_parent = v.findViewById(R.id.lyt_parent) as View
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_library_category, parent, false)
        vh = OriginalViewHolder(v)
        return vh
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val obj = items[position]
        if (holder is OriginalViewHolder) {
            holder.categoryName.text = obj.category
            holder.image.setImageResource(obj.coverImage)
            holder.lyt_bottom.setBackgroundColor(obj.bottomColor)
            holder.lyt_parent.setOnClickListener { view ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(view, items[position], position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}