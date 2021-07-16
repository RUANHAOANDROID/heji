package com.rh.heji.ui.book

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ImageUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.rh.heji.AppCache
import com.rh.heji.R
import com.rh.heji.data.db.Book
import com.rh.heji.databinding.FragmentBookItemBinding


/**
 * Date: 2021/7/9
 * Author: 锅得铁
 * #
 */
class BookListAdapter :

    BaseQuickAdapter<Book, BaseViewHolder>(
        layoutResId = R.layout.fragment_book_item,
        mutableListOf()
    ) {
    override fun convert(holder: BaseViewHolder, item: Book) {
        FragmentBookItemBinding.bind(holder.itemView)?.let { binding ->
            binding.tvTitle.text = item.name
            binding.tvContext.text = item.type
            val bannerBitmap = ImageUtils.drawable2Bitmap(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.banner_tree
                )
            )
            if (holder.layoutPosition==0){
                binding.root.background = ImageUtils.bitmap2Drawable(ImageUtils.toRoundCorner(bannerBitmap, 10f))
            }else{
                binding.root.background = ColorDrawable(Color.rgb(140, 234, 255))
            }


            if (item.id == AppCache.getInstance().currentBook.id) {
                binding.imgSelected.visibility = View.VISIBLE
            } else {
                binding.imgSelected.visibility = View.INVISIBLE
            }
            binding.imgSetting.setOnClickListener {

            }
        }
    }


    val colors = com.rh.heji.utlis.ColorUtils.groupColors()
}