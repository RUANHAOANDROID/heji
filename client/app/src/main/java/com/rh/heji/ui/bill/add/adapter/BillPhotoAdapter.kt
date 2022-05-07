package com.rh.heji.ui.bill.add.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.rh.heji.App
import com.rh.heji.GlideApp
import com.rh.heji.MyAppGlideModule
import com.rh.heji.R
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.ItemPopSelectTicketImageBinding
import com.rh.heji.utlis.ImageUtils

/**
 * Date: 2020/9/16
 * Author: 锅得铁
 * #
 */
class BillPhotoAdapter :
    BaseQuickAdapter<Image, BaseViewHolder>(R.layout.item_pop_select_ticket_image, mutableListOf(Image(id = "0", billID = "0"))) {
    private lateinit var binding: ItemPopSelectTicketImageBinding
    override fun convert(holder: BaseViewHolder, image: Image) {
        binding = ItemPopSelectTicketImageBinding.bind(holder.itemView)
        val path = ImageUtils.getImagePath(image)
        if (image.id == "0") {
            binding.imgTicket.setImageDrawable(context.resources.getDrawable(R.drawable.ic_add_image_red_32dp))
            binding.imgDelete.visibility = View.INVISIBLE
        } else {
            GlideApp.with(binding.imgTicket)
                .asBitmap()
                .load(path)
                .error(R.drawable.ic_baseline_image_load_error_24)
                .placeholder(R.drawable.ic_baseline_image_24)
                .into(binding.imgTicket)
            //MyAppGlideModule.loadImageFile(App.context(), image, binding.imgTicket)
            binding.imgDelete.visibility = View.VISIBLE
            binding.imgTicket.setOnClickListener { v -> showGallery(holder.layoutPosition) }
        }
    }

    init {
        addChildClickViewIds(R.id.imgDelete)
    }

    private fun showGallery(startPosition: Int) {
        val srcViewUpdateListener =
            OnSrcViewUpdateListener { popupView: ImageViewerPopupView, position: Int ->
                popupView.updateSrcView(
                    recyclerView.getChildAt(position) as ImageView
                )
            }
        XPopup.Builder(context)
            .asImageViewer(
                recyclerView.getChildAt(startPosition) as ImageView,
                startPosition,
                ImageUtils.getPaths(data),
                false,
                false,
                -1,
                -1,
                -1,
                false,
                Color.rgb(32, 36, 46),
                srcViewUpdateListener,
                SmartGlideImageLoader(R.mipmap.ic_launcher),
                null
            )
            .show()
    }
}