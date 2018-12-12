package com.example.kkgroup.soundscape_v2.widget

import android.graphics.Canvas
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Reference from https://github.com/WuXiaolong/AndroidSamples/
 */
class ItemTouchHelperCallback (private val itemTouchHelperAdapter: ItemTouchHelperAdapter) : ItemTouchHelper.Callback() {

    private val ALPHA_FULL = 1.0f

    private var dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN   // enable up/down drag
    private var swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END    // enable left/right drag

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        if (recyclerView.layoutManager is GridLayoutManager || recyclerView.layoutManager is StaggeredGridLayoutManager) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT

            val swipeFlags = 0
            return ItemTouchHelper.Callback.makeMovementFlags(dragFlags, swipeFlags)
        } else {
//            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
//            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END

            return ItemTouchHelper.Callback.makeMovementFlags(dragFlag, swipeFlag)
        }
    }

    fun setDraggable(isDraggable: Boolean){
        if (isDraggable) {
            dragFlag = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            swipeFlag = ItemTouchHelper.START or ItemTouchHelper.END
        } else {
            dragFlag = 0
            swipeFlag = 0
        }
    }

    /**
     * Notify the adapter of the move
     */
    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        if (viewHolder.itemViewType != target.itemViewType) {
            return false
        }
        itemTouchHelperAdapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        itemTouchHelperAdapter.onItemDismiss(viewHolder.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val alpha = ALPHA_FULL - Math.abs(dX) / viewHolder.itemView.width.toFloat()
            viewHolder.itemView.alpha = alpha
            viewHolder.itemView.translationX = dX
        } else {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    /**
     * We only want the active item to change
     * Let the view holder know that this item is being moved or dragged
     */
    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        //
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder is ItemTouchHelperViewHolder) {
                val itemViewHolder = viewHolder as ItemTouchHelperViewHolder?
                itemViewHolder!!.onItemSelected()
            }
        }
        super.onSelectedChanged(viewHolder, actionState)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        super.clearView(recyclerView, viewHolder)
        viewHolder.itemView.alpha = ALPHA_FULL
        if (viewHolder is ItemTouchHelperViewHolder) {
            val itemViewHolder = viewHolder as ItemTouchHelperViewHolder
            itemViewHolder.onItemClear()
        }
    }
}

interface ItemTouchHelperViewHolder {

    fun onItemSelected()

    fun onItemClear()
}

interface ItemTouchHelperAdapter {

    fun onItemMove(fromPosition: Int, toPosition: Int): Boolean

    fun onItemDismiss(position: Int)
}
