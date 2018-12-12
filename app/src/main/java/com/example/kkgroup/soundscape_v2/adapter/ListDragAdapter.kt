package com.example.kkgroup.soundscape_v2.adapter

import android.content.Context
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.kkgroup.soundscape_v2.Model.AudioCardModel
import com.example.kkgroup.soundscape_v2.R
import com.example.kkgroup.soundscape_v2.Tools.Tools
import com.example.kkgroup.soundscape_v2.widget.ItemTouchHelperAdapter
import com.example.kkgroup.soundscape_v2.widget.ItemTouchHelperViewHolder
import java.io.File
import java.util.*

class ListDragAdapter(private val ctx: Context,
                      private var items: ArrayList<AudioCardModel>) :
        RecyclerView.Adapter<ListDragAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    private var itemTouchHelper: ItemTouchHelper? = null
    private var mOnItemClickListener: OnItemClickListener? = null
    private var mOnItemsChangeListener: OnItemsChangeListener? = null
    private var mOnItemDeleteListener: OnItemDeleteListener? = null

    interface OnItemClickListener {
        fun onItemClick(view: View, audioCardModel: AudioCardModel, position: Int)
    }

    interface OnItemsChangeListener {
        fun onItemsChange(fromPosition: Int, toPosition: Int)
    }

    interface OnItemDeleteListener {
        fun onItemDelete(audioCardModel: AudioCardModel)
    }

    fun setOnItemClickListener(mItemClickListener: OnItemClickListener) {
        this.mOnItemClickListener = mItemClickListener
    }

    fun setOnItemsChangeListener(mOnItemsChangeListener: OnItemsChangeListener) {
        this.mOnItemsChangeListener = mOnItemsChangeListener
    }

    fun setOnItemDeleteListener(mOnItemDeleteListener: OnItemDeleteListener) {
        this.mOnItemDeleteListener = mOnItemDeleteListener
    }

    fun setOnItemTouchHelper(itemTouchHelper: ItemTouchHelper) {
        this.itemTouchHelper = itemTouchHelper
    }

    fun getItems(): ArrayList<AudioCardModel>{
        return items
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        mOnItemsChangeListener?.onItemsChange(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        val removeAt = items.removeAt(position)
        Tools.log_e("items size: ${items.size}")
        notifyItemRemoved(position)
        mOnItemDeleteListener?.onItemDelete(removeAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_file_card_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = items[position].file.name
        holder.cardView.setCardBackgroundColor(ctx.resources.getColor(items[position].bgColor))
        holder.time.text = items[position].category

        holder.reorder.setOnTouchListener { v, event ->
            if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                itemTouchHelper?.startDrag(holder)
            }
            false
        }

    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v), ItemTouchHelperViewHolder {

        var title: TextView = v.findViewById(R.id.card_title)
        var time: TextView = v.findViewById(R.id.card_format)
        var reorder: ImageButton = v.findViewById(R.id.iv_move)
        var lyt_parent: View = v.findViewById(R.id.lyt_parent)
        var lyt_playing: LinearLayout = v.findViewById(R.id.lyt_playing)
        var cardView: CardView = v.findViewById(R.id.cardView)

        init {
            lyt_parent.setOnClickListener {
                mOnItemClickListener?.onItemClick(it,items[layoutPosition],layoutPosition)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(ctx.resources.getColor(R.color.grey_10))
        }

        override fun onItemClear() {
            itemView.setBackgroundColor(0)
        }
    }

}