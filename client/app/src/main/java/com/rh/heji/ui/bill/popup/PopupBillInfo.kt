package com.rh.heji.ui.bill.popup

import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.App
import com.rh.heji.BuildConfig
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Image
import com.rh.heji.databinding.PopLayoutBilliInfoBinding
import com.rh.heji.ui.bill.add.AddBillFragmentArgs
import com.rh.heji.ui.bill.add.ArgAddBill

/**
 * Date: 2020/9/20
 * @author: 锅得铁
 * #
 */
class PopupBillInfo(
    val activity: MainActivity,
    val bill: Bill,
    val delete: (Bill) -> Unit, val update: (Bill) -> Unit
) : BottomPopupView(activity), Observer<List<Image>> {
    //观察 当前账单下图片
    private val imageObservable by lazy {
        App.dataBase.imageDao().findByBillId(billId = bill.id).asLiveData()
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
            update(bill)
            val bundle =
                AddBillFragmentArgs.Builder(ArgAddBill(isModify = true, bill)).build().toBundle()
            activity.navController.navigate(R.id.nav_bill_add, bundle)
            dismiss()
        }
        //设置圆角背景
        popupImplView.background = XPopupUtils.createDrawable(
            resources.getColor(R.color._xpopup_light_color, null),
            popupInfo.borderRadius, popupInfo.borderRadius, 0f, 0f
        )
        binding.apply {
            tvMonney.text = bill.money.toString()
            tvType.text = bill.category
            tvRecordTime.text = bill.createTime?.let { TimeUtils.millis2String(it) }
            tvTicketTime.text = DateConverters.date2Str(bill.billTime)
            rePeople.text = bill.dealer
        }
        initBillImageList()//初始化列表和适配器
        if (bill.images.isNotEmpty()) {
            imageObservable.observeForever(this)
        }
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
            val mainActivity = context as MainActivity
            bill.also {
                if (it.createUser == App.user.name) {
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

    override fun onChanged(images: List<Image>) {
        if (images.isEmpty()) return
        //服务器返回的是图片的ID、需要加上前缀
        val imagePaths = images.map { image: Image ->
            val onlinePath = image.onlinePath
            if (onlinePath != null && !image.onlinePath!!.contains("http")) { //在线Image路径
                val path = BuildConfig.HTTP_URL + "/image/" + image.onlinePath
                image.onlinePath = path
            }
            image
        }.toMutableList()
        imageAdapter.setNewInstance(imagePaths)
    }

    override fun onDismiss() {
        super.onDismiss()
        if (bill.images.isNotEmpty()) {
            imageObservable.removeObserver(this)
        }
    }
}
