package com.rh.heji.ui.book

import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ImageUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
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
            binding.root.background=ImageUtils.bitmap2Drawable(ImageUtils.toRoundCorner(bannerBitmap,10f))

        }
    }

}