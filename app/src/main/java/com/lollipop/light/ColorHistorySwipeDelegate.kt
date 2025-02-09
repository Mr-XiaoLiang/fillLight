package com.lollipop.light

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class ColorHistorySwipeDelegate(
    private val recyclerView: RecyclerView,
    private val onSwap: (Int, Int) -> Boolean,
    private val onSwipe: (Int) -> Boolean,
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT,
    ItemTouchHelper.UP
) {

    init {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val targetPosition = target.adapterPosition
        if (onSwap(fromPosition, targetPosition)) {
            recyclerView.adapter?.notifyItemMoved(
                fromPosition,
                targetPosition
            )
            return true
        }
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val adapterPosition = viewHolder.adapterPosition
        if (onSwipe(adapterPosition)) {
            recyclerView.adapter?.notifyItemRemoved(adapterPosition)
        } else {
            recyclerView.adapter?.notifyItemChanged(adapterPosition)
        }
    }


}