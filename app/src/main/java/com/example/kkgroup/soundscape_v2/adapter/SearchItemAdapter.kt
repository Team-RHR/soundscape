package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.Model.SearchApiModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation

/**
 * description: This adapter is used to inflate audio_file_item.xml in search activity
 * create time: 14:22 2018/12/15
 */
class SearchItemAdapter(val ctx: Context,
                        val items: Array<SearchApiModel>,
                        val animation_type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnAddToTrackListener: OnAddToTrackListener? = null
    private var lastPosition = -1
    private var on_attach = true

    interface OnItemClickListener {
        fun onItemClick(view: View, obj: SearchApiModel, position: Int)
    }

    interface OnAddToTrackListener {
        fun onAddToTrack(view: View, obj: SearchApiModel, position: Int)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    fun setOnAddToTrackListener(mOnAddToTrackListener: OnAddToTrackListener) {
        this.mOnAddToTrackListener = mOnAddToTrackListener
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var playBtn: ImageView = v.findViewById(R.id.iv_play)
        internal var name: TextView = v.findViewById(R.id.name)
        internal var category: TextView = v.findViewById(R.id.tv_category)
        internal var lyt_parent: View = v.findViewById(R.id.lyt_parent)
        internal var addToTrackBtn: ImageView = v.findViewById(R.id.iv_add_to_track)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.audio_file_item, parent, false)
        viewHolder = OriginalViewHolder(v)
        return viewHolder
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {
            holder.name.text = items[position].title
            holder.category.text = items[position].category
            holder.lyt_parent.setOnClickListener { view ->
                mOnItemClickListener?.onItemClick(view, items[position], position)

            }
            holder.addToTrackBtn.setOnClickListener { view ->
                mOnAddToTrackListener?.onAddToTrack(view, items[position], position)
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