package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.widget.ItemAnimation

import java.io.File

class AudioItemAdapter(
        val ctx: Context,
        val items: List<File>,
        val animation_type: Int) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var lastPosition = -1
    private var on_attach = true

    interface OnItemClickListener {
        fun onItemClick(view: View, obj: File, position: Int)
    }

    interface OnMoreButtonClickListener {
        fun onItemClick(view: View, obj: File, menuItem: MenuItem)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    private var onMoreButtonClickListener: OnMoreButtonClickListener? = null

    fun setOnMoreButtonClickListener(onMoreButtonClickListener: OnMoreButtonClickListener) {
        this.onMoreButtonClickListener = onMoreButtonClickListener
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        internal var image: ImageView = v.findViewById(R.id.image)
        internal var name: TextView = v.findViewById(R.id.name)
        internal var duration: TextView = v.findViewById(R.id.duration)
        internal var lyt_parent: View = v.findViewById(R.id.lyt_parent)
        internal var iv_more: ImageView = v.findViewById(R.id.iv_more)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val viewHolder: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.audio_file_item, parent, false)
        viewHolder = OriginalViewHolder(v)
        return viewHolder
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {
            holder.name.text = items[position].name
            holder.duration.text = ""

            holder.lyt_parent.setOnClickListener { view ->
                mOnItemClickListener?.onItemClick(view, items[position], position)
            }

            holder.iv_more.setOnClickListener { view ->
                onMoreButtonClickListener?.let {
                    onMoreButtonClick(view, items[position])
                }
            }

        }
        setAnimation(holder.itemView, position)
    }

    private fun onMoreButtonClick(view: View, file: File) {
        val popupMenu = PopupMenu(ctx, view)
        popupMenu.setOnMenuItemClickListener { item ->
            onMoreButtonClickListener?.onItemClick(view, file, item)
            true
        }
        popupMenu.inflate(R.menu.menu_audio_more)
        popupMenu.show()
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