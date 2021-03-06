package com.rh.heji.ui.bill.iteminfo

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.core.ImageViewerPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.AppCache
import com.rh.heji.BuildConfig
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.data.AppDatabase
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.PopBilliInfoBinding
import com.rh.heji.ui.bill.img.ImageLoader
import com.rh.heji.ui.user.JWTParse.getUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors
import kotlin.String as String

/**
 * Date: 2020/9/20
 * Author: 锅得铁
 * #
 */
class BillInfoPop(
    val activity: MainActivity,
    val bill: Bill,
    var popClickListener: BillPopClickListenerImpl = BillPopClickListenerImpl(),
) : BottomPopupView(activity), Observer<List<Image>> {
    private val imageObservable by lazy { activity.mainViewModel.getBillImages(billId = bill.id) }
    fun setBill() {
        binding.tvMonney.text = bill.money.toString()
        binding.tvType.text = bill.category
        binding.tvRecordTime.text = TimeUtils.millis2String(bill.createTime)
        binding.tvTicketTime.text = DateConverters.date2Str(bill.billTime)
        binding.rePeople.text = bill.dealer
    }

    lateinit var binding: PopBilliInfoBinding
    private var imageAdapter = ImageAdapter()

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
            popClickListener?.update(bill)
        }
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            resources.getColor(R.color._xpopup_light_color, null),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
        setBill()
        initBillImageList()//初始化列表和适配器
        if (bill.imgCount>0){
            imageObservable.observeForever(this)
        }
    }

    private fun initBillImageList() {
        binding.ticketRecycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.ticketRecycler.adapter = imageAdapter
        imageAdapter.setOnItemClickListener { adapter: BaseQuickAdapter<*, *>?, view: View, position: Int ->
            showImage(
                view,
                position
            )
        }
    }

    private fun showImage(itemView: View, itemPosition: Int) {
        val objects = imageAdapter.data.stream()
            .map(Function<Image?, Any> { image: Image? -> imageAdapter.getImagePath(image) })
            .collect(Collectors.toList())
        if (objects.isEmpty()) return
        XPopup.Builder(context)
            .asImageViewer(
                itemView as ImageView,
                itemPosition,
                objects,
                { popupView: ImageViewerPopupView, position1: Int ->
                    popupView.updateSrcView(
                        imageAdapter.getViewByPosition(itemPosition, R.id.itemImage) as ImageView?
                    )
                },
                ImageLoader()
            )
            .isShowSaveButton(false)
            .show()
    }

    /**
     * 删除该条账单
     */
    private fun deleteTip() {
        XPopup.Builder(context).asConfirm(
            "删除提示", "确认删除该条账单吗？"
        ) {
            val mainActivity = context as MainActivity
            mainActivity.lifecycleScope.launch(Dispatchers.Default) {
                val user = getUser(AppCache.getInstance().token.tokenString)
                if (bill.createUser == null || bill.createUser == user.username) {
                    popClickListener?.let {
                        mainActivity.runOnUiThread {
                            it.delete(bill)
                            dismiss()
                        }

                    }
                } else {
                    ToastUtils.showLong("只有账单创建人有权删除该账单")
                }

            }

        }.show()
    }

    override fun onChanged(images: List<Image>) {
        if (images.isEmpty()) return
        //服务器返回的是图片的ID、需要加上前缀
        val imagePaths = images.stream().map { image: Image ->
            val onlinePath = image.onlinePath
            if (onlinePath != null && !image.onlinePath!!.contains("http")) { //在线Image路径
                val path = BuildConfig.HTTP_URL + "/image/" + image.onlinePath
                image.onlinePath = path
            }
            image
        }.collect(Collectors.toList())
        imageAdapter.setNewInstance(imagePaths)
    }

    override fun onDismiss() {
        super.onDismiss()
        if (bill.imgCount>0){
            imageObservable.removeObserver(this)
        }
    }
}

/**
 * 不对外开放该接口，通过覆写BillDefPopClickListener实现
 */
private interface PopClickListener {
    fun delete(bill: Bill)
    fun update(bill: Bill)
}

open class BillPopClickListenerImpl : PopClickListener {

    override fun delete(bill: Bill) {
        AppCache.getInstance().appViewModule.billDelete(bill)
    }

    override fun update(bill: Bill) {

    }

}