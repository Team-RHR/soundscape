package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView

import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.widget.DragItemTouchHelper

import java.io.File
import java.util.ArrayList
import java.util.Collections

class AdapterListDrag(private val ctx: Context,
                      private val items: List<File>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), DragItemTouchHelper.MoveHelperAdapter {

    private var mOnItemClickListener: OnItemClickListener? = null
    private var mDragStartListener: OnStartDragListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, file: File, position: Int)
    }

    interface OnStartDragListener {
        fun onStartDrag(viewHolder: RecyclerView.ViewHolder)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    fun setDragListener(dragStartListener: OnStartDragListener) {
        this.mDragStartListener = dragStartListener
    }

    inner class OriginalViewHolder(v: View) : RecyclerView.ViewHolder(v), DragItemTouchHelper.TouchViewHolder {
        var title: TextView
        var volume: TextView
        var time: TextView
        var lyt_parent: View

        init {
            title = v.findViewById(R.id.card_title)
            volume = v.findViewById(R.id.card_volume)
            time = v.findViewById(R.id.card_time)
            lyt_parent = v.findViewById(R.id.lyt_parent)
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(ctx.resources.getColor(R.color.grey_5))
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val vh: RecyclerView.ViewHolder
        val v = LayoutInflater.from(parent.context).inflate(R.layout.audio_file_card_item, parent, false)
        vh = OriginalViewHolder(v)
        return vh
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is OriginalViewHolder) {

            val p = items[position]
            holder.title.text = p.name
            holder.lyt_parent.setOnClickListener { view ->
                if (mOnItemClickListener != null) {
                    mOnItemClickListener!!.onItemClick(view, items[position], position)
                }
            }

            // Start a drag whenever the handle view it touched
            holder.lyt_parent.setOnTouchListener { v, event ->
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN && mDragStartListener != null) {
                    mDragStartListener!!.onStartDrag(holder)
                }
                false
            }

        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

}