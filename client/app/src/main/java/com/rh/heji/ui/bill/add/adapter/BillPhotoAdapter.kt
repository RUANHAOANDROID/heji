package com.rh.heji.ui.bill.add.adapter

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.AppCache.Companion.instance
import com.rh.heji.MyAppGlideModule
import com.rh.heji.R
import com.rh.heji.databinding.ItemPopSelectTicketImageBinding
import java.util.*

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
class BillPhotoAdapter :
    BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_pop_select_ticket_image, ArrayList()) {
    private lateinit var binding: ItemPopSelectTicketImageBinding
    override fun convert(viewHolder: BaseViewHolder, item: String) {
        binding = ItemPopSelectTicketImageBinding.bind(viewHolder.itemView)
        if (item == "+") {
            binding.imgTicket.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_image_red_32dp))
            binding.imgDelete.visibility = View.INVISIBLE
        } else {
            MyAppGlideModule.loadImageFile(instance.context, item, binding.imgTicket)
            binding.imgDelete.visibility = View.VISIBLE
        }
    }

    init {
        addChildClickViewIds(R.id.imgDelete)
    }
}