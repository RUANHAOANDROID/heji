package com.rh.heji.ui.bill.iteminfo

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.AppCache.Companion.instance
import com.rh.heji.BuildConfig
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Constant
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.PopBilliInfoBinding
import com.rh.heji.ui.bill.img.ImageLoader
import com.rh.heji.ui.user.JWTParse.getUser
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
class BillInfoPop(context: Context) : BottomPopupView(context) {
    var bill: Bill = Bill()
        set(value) {
            field = value
            binding.tvMonney.text = bill.getMoney().toString()
            binding.tvType.text = bill.getCategory()
            binding.tvRecordTime.text = TimeUtils.millis2String(bill.getCreateTime())
            binding.tvTicketTime.text = DateConverters.date2Str(bill.billTime)
            binding.rePeople.text = bill.getDealer()
        }
    lateinit var binding: PopBilliInfoBinding
    private lateinit var imageAdapter: ImageAdapter
    lateinit var popClickListener: PopClickListener

    fun setBillImages(images: List<Image>) {
        if (images.isEmpty()) {
            if (this::imageAdapter.isInitialized) {//判断是否初始化 ::表示作用域
                imageAdapter?.let {
                    it.setNewInstance(ArrayList())
                    it.notifyDataSetChanged()
                }
            }
            return
        }
        initBillImage()//初始化列表和适配器
        //服务器返回的是图片的ID、需要加上前缀
        val imagePaths = images.stream().map { image: Image ->
            val onlinePath = image.onlinePath
            if (onlinePath != null && !image.onlinePath.contains("http")) { //在线Image路径
                val path = BuildConfig.HTTP_URL + "/image/" + image.onlinePath
                image.onlinePath = path
            }
            image
        }.collect(Collectors.toList())
        imageAdapter.setNewInstance(imagePaths)
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_billi_info
    }

    override fun onCreate() {
        super.onCreate()
        binding = PopBilliInfoBinding.bind(popupContentView.findViewById(R.id.billInfoCard))
        binding.tvDelete.setOnClickListener { v: View? ->
            deleteTip()
        }
        binding.tvUpdate.setOnClickListener { v: View? ->
            popClickListener?.let {
                it.update(bill.id)
            }
        }
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(resources.getColor(R.color._xpopup_light_color, null),
                popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f)
    }

    private fun initBillImage() {
        binding.ticketRecycler.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        imageAdapter = ImageAdapter()
        binding.ticketRecycler.adapter = imageAdapter
        imageAdapter.setDiffCallback(object : DiffUtil.ItemCallback<Image>() {
            override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
                return oldItem == newItem
            }
        })
        imageAdapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View, position: Int -> showImage(view, position) }
    }

    private fun showImage(itemView: View, itemPosition: Int) {
        val objects = imageAdapter.data.stream().map(Function<Image?, Any> { image: Image? -> imageAdapter.getImagePath(image) }).collect(Collectors.toList())
        if (objects.isEmpty()) return
        XPopup.Builder(context)
                .asImageViewer(itemView as ImageView,
                        itemPosition,
                        objects,
                        { popupView: ImageViewerPopupView, position1: Int -> popupView.updateSrcView(imageAdapter.getViewByPosition(itemPosition, R.id.itemImage) as ImageView?) },
                        ImageLoader())
                .isShowSaveButton(false)
                .show()
    }

    /**
     * 删除该条账单
     */
    private fun deleteTip() {
        XPopup.Builder(context).asConfirm("删除提示", "确认删除该条账单吗？"
        ) {
            val (username) = getUser(instance.token)
            val createUser = bill.createUser
            if (createUser == null || bill.createUser == username) {
                instance.appViewModule.billDelete(bill.getId())
                popClickListener.let {
                    it.delete(bill.getId())
                }
            } else {
                ToastUtils.showLong("只有账单创建人有权删除该账单")
            }
            dismiss()
        }.show()
    }

    interface PopClickListener {
        fun delete(_id: String)
        fun update(_id: String)
    }
}