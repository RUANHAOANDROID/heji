package com.rh.heji.ui.book

import android.view.View
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ImageUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.App
import com.rh.heji.R
import com.rh.heji.data.db.Book
import com.rh.heji.databinding.FragmentBookItemBinding
import com.rh.heji.utlis.ColorUtils


/**
 * @date: 2021/7/9
 * @author: 锅得铁
 * #
 */
class BookListAdapter constructor(val settingClickListener: (Book) -> Unit) :
    BaseQuickAdapter<Book, BaseViewHolder>(
        layoutResId = R.layout.fragment_book_item,
        mutableListOf()
    ) {

    private val colors = ColorUtils.groupColors()
    override fun convert(holder: BaseViewHolder, item: Book) {
        FragmentBookItemBinding.bind(holder.itemView).let { binding ->
            binding.tvTitle.text = item.name
            binding.tvContext.text = item.type
            val bannerBitmap = ImageUtils.drawable2Bitmap(
                ContextCompat.getDrawable(
                    context,
                    R.drawable.banner_tree
                )
            )
            if (holder.layoutPosition == 0) {
                binding.root.background =
                    ImageUtils.bitmap2Drawable(ImageUtils.toRoundCorner(bannerBitmap, 10f))
            } else {
                val colorDrawable = colors[holder.layoutPosition % 24]
                binding.root.background =
                    XPopupUtils.createDrawable(colorDrawable, 10f, 10f, 10f, 10f)
            }


            if (item.id == App. currentBook.id) {
                binding.imgSelected.visibility = View.VISIBLE
            } else {
                binding.imgSelected.visibility = View.INVISIBLE
            }
            binding.imgSetting.setOnClickListener {
                settingClickListener(item)
            }
        }
    }
}