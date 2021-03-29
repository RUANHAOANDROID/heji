package com.rh.heji.widget

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.R
import com.rh.heji.ui.bill.adapter.TYPE_TITLE

class CardDecoration(val padding: Int = 8) : ItemDecoration() {
    private fun drawCardBackground(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i) // Item View
            val params = child.layoutParams as RecyclerView.LayoutParams
            val position = params.viewAdapterPosition
            if (parent.adapter!!.getItemViewType(position) == TYPE_TITLE) {
                child.background = XPopupUtils.createDrawable(ContextCompat.getColor(parent.context,R.color._xpopup_light_color), 15f, 15f, 0f, 0f)
            } else if (isLastInItemList(parent, position) || isLastItem(parent, position)) {
                child.background = XPopupUtils.createDrawable(ContextCompat.getColor(parent.context,R.color._xpopup_light_color), 0f, 0f, 15f, 15f)
            }
        }
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        //super.onDrawOver(c, parent, state);
        drawCardBackground(c, parent)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val resources = parent.context.resources
        val padding = getPadding(resources)
        val params = view.layoutParams as RecyclerView.LayoutParams
        val position = params.viewAdapterPosition
        val viewType = parent.adapter!!.getItemViewType(position)
        if (viewType == TYPE_TITLE) {
            // header
            outRect[0, (padding / 2), 0] = 0
        } else {
            if (isLastInItemList(parent, position) || isLastItem(parent, position)) {
                // last item before next header
                outRect[0, 0, 0] = (padding / 2)
            }
        }
        //        outRect.inset((int) size16dp, 0);
        outRect.left = padding
        outRect.right = padding
        if (isLastItem(parent, position)) { //最后一个
            outRect.bottom = padding / 2
        }
    }

    private fun isLastItem(parent: RecyclerView, position: Int): Boolean {
        return position == parent.adapter!!.itemCount - 1
    }

    private fun isLastInItemList(parent: RecyclerView, position: Int): Boolean {
        return parent.adapter!!.getItemViewType(position + 1) == TYPE_TITLE
    }

    private fun getPadding(resources: Resources): Int {
        val size16dp = padding * 1f
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size16dp, resources.displayMetrics).toInt()
    }
}