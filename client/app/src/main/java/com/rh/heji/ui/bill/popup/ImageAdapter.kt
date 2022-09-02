package com.rh.heji.ui.bill.popup

import android.graphics.Color
import android.view.View
import android.widget.ImageView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.R
import com.rh.heji.GlideApp
import com.blankj.utilcode.util.LogUtils
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.ItemImgBinding
import com.rh.heji.utlis.ImageUtils

/**
 * 票据图片Adapter
 */
class ImageAdapter : BaseQuickAdapter<Image, BaseViewHolder>(R.layout.item_img) {
    lateinit var binding: ItemImgBinding
    override fun convert(holder: BaseViewHolder, image: Image) {
        binding = ItemImgBinding.bind(holder.itemView)
        val path = ImageUtils.getImagePath(image)
        GlideApp.with(binding!!.itemImage)
            .asBitmap()
            .load(path)
            .error(R.drawable.ic_baseline_image_load_error_24)
            .placeholder(R.drawable.ic_baseline_image_24)
            .into(binding.itemImage)
        binding.itemImage.setOnClickListener { v: View? -> showGallery(holder.layoutPosition) }
        LogUtils.d(
            "local path:${image.localPath}",
            "online path:${image.onlinePath}",
            "use path:${path}"
        )
    }

    private fun showGallery(startPosition: Int) {
        val srcViewUpdateListener =
            OnSrcViewUpdateListener { popupView: ImageViewerPopupView, position: Int ->
                popupView.updateSrcView(recyclerView.getChildAt(position) as ImageView)
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