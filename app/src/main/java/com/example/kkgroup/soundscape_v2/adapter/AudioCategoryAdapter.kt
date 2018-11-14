package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation

import java.util.ArrayList

class AudioCategoryAdapter(
        val ctx: Context,
        val items: List<String>,
        val animation_type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var lastPosition = -1
    private var on_attach = true

    interface OnItemClickListener {
        fun onItemClick(view: View, categoryName: String, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var tv_category_name: TextView = v.findViewById(R.id.tv_category_name)
        internal var lyt_parent: View = v.findViewById(R.id.lyt_parent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.audio_category_item, parent, false)
        viewHolder = OriginalViewHolder(v)
        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {
            holder.tv_category_name.text = items[position]
            holder.lyt_parent.setOnClickListener { view ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(view, items[position], position)
                }
            }
        }
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                on_attach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        super.onAttachedToRecyclerView(recyclerView)
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, if (on_attach) position else -1, animation_type)
            lastPosition = position
        }
    }
}