package com.rh.heji.ui.popup

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.interfaces.OnSrcViewUpdateListener
import com.lxj.xpopup.util.SmartGlideImageLoader
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.*
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.ItemImgBinding
import com.rh.heji.databinding.PopLayoutBilliInfoBinding
import com.rh.heji.ui.MainActivity
import com.rh.heji.ui.create.ArgAddBill
import com.rh.heji.ui.create.CreateBillFragmentArgs
import com.rh.heji.utlis.ImageUtils

/**
 * @date: 2020/9/20
 * @author: 锅得铁
 * #
 */
class PopupBillInfo(
    val activity: MainActivity,
    val delete: (Bill) -> Unit, val update: (Bill) -> Unit
) : BottomPopupView(activity) {
    private lateinit var mBill: Bill

    companion object {
        fun create(
            activity: MainActivity,
            delete: (Bill) -> Unit,
            update: (Bill) -> Unit,
        ): PopupBillInfo {
            return XPopup.Builder(activity).hasNavigationBar(false)
                .asCustom(PopupBillInfo(activity, delete, update)) as PopupBillInfo
        }
    }

    fun show(bill: Bill) {
        mBill = bill
        show()
        post {
            binding.apply {
                tvMonney.text = mBill.money.toString()
                tvType.text = mBill.category
                tvRecordTime.text = mBill.createTime?.let { TimeUtils.millis2String(it) }
                tvTicketTime.text = DateConverters.date2Str(mBill.billTime)
                rePeople.text = mBill.dealer
            }
        }
    }

    lateinit var binding: PopLayoutBilliInfoBinding
    private var imageAdapter = ImageAdapter()

    override fun getImplLayoutId(): Int {
        return R.layout.pop_layout_billi_info
    }

    override fun onCreate() {
        super.onCreate()
        binding = PopLayoutBilliInfoBinding.bind(popupContentView.findViewById(R.id.billInfoCard))
        binding.tvDelete.setOnClickListener {
            deleteTip()
        }
        binding.tvUpdate.setOnClickListener {
            update(mBill)
            val bundle =
                CreateBillFragmentArgs.Builder(ArgAddBill(isModify = true, mBill)).build()
                    .toBundle()
            activity.navController.navigate(R.id.nav_bill_add, bundle)
            dismiss()
        }
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            resources.getColor(R.color._xpopup_light_color, null),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )

        initBillImageList()//初始化列表和适配器
    }

    private fun initBillImageList() {
        binding.ticketRecycler.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }


    /**
     * 删除该条账单
     */
    private fun deleteTip() {
        val mBillSync = activity.mService.getBillSyncManager()
        XPopup.Builder(context).asConfirm(
            "删除提示", "确认删除该条账单吗？"
        ) {
            context as MainActivity
            mBill.also {
                if (it.createUser == Config.user.name) {
                    //状态删除
                    App.dataBase.billDao().preDelete(it.id)
                    //异步删除->删除成功->本地删除
                    mBillSync.delete(it.id)
                    delete(it)
                    dismiss()
                    LogUtils.d("删除账单")
                } else {
                    ToastUtils.showLong("只有账单创建人有权删除该账单")
                }
            }
        }.show()
    }

    fun setImages(data: List<Image>) {
        if (data.isEmpty()) return
        //服务器返回的是图片的ID、需要加上前缀
        val imagePaths = data.map { image: Image ->
            val onlinePath = image.onlinePath
            if (onlinePath != null && !image.onlinePath!!.contains("http")) { //在线Image路径
                val path = BuildConfig.HTTP_URL + "/image/" + image.onlinePath
                image.onlinePath = path
            }
            image
        }.toMutableList()
        imageAdapter.setNewInstance(imagePaths)
    }

    /**
     * 票据图片Adapter
     */
    private class ImageAdapter : BaseQuickAdapter<Image, BaseViewHolder>(R.layout.item_img) {
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
}

