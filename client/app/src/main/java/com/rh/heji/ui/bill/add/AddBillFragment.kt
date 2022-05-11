package com.rh.heji.ui.bill.add

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.*
import com.lxj.xpopup.XPopup
import com.matisse.Matisse.Companion.obtainResult
import com.matisse.entity.ConstValue.REQUEST_CODE_CHOOSE
import com.rh.heji.*
import com.rh.heji.data.BillType
import com.rh.heji.data.DataBus
import com.rh.heji.data.SyncEvent
import com.rh.heji.data.db.*
import com.rh.heji.databinding.FragmentAddbillBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.category.CategoryTabFragment
import com.rh.heji.ui.bill.category.CategoryViewModel
import com.rh.heji.ui.bill.category.ISelectedCategory
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.KeyBoardView.OnKeyboardListener
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

/**
 * 添加账单（支出/收入）
 * -----------title------------
 * 收入|指出
 * -----------category---------
 * 账单类别
 * ----------
 */
class AddBillFragment : BaseFragment(), ISelectedCategory {
    private val billViewModel by lazy { ViewModelProvider(this)[AddBillViewModel::class.java] }
    val categoryViewModel by lazy { ViewModelProvider(this)[CategoryViewModel::class.java] }

    private lateinit var binding: FragmentAddbillBinding
    private lateinit var categoryTabFragment: CategoryTabFragment

    lateinit var popupSelectImage: PopSelectImage//图片弹窗

    var isModify = false//默认新增

    override fun layoutId(): Int {
        return R.layout.fragment_addbill
    }

    override fun initView(rootView: View) {
        val argAddBill = AddBillFragmentArgs.fromBundle(requireArguments()).argAddBill
        billViewModel.setBill(argAddBill.bill)
        isModify = argAddBill.isModify

        binding = FragmentAddbillBinding.bind(rootView)
        setupImage()
        setupPerson()
        setupYearAndDay()
        remark()
        category()
        keyboardListener()
        billViewModel.billChanged().observe(this) { bill ->
            //填充输入信息
            binding.inputInfo.apply {
                tvMoney.text = bill.money.toString()
                bill.dealer?.let { setDealerUser(it) }
                tvBillTime.text = bill.billTime.string()
                bill.remark?.let { remark ->
                    eidtRemark.setText(remark)
                }
                val existImages = bill.images.isNotEmpty()
                if (existImages) {
                    imgTicket.text = "图片(x${bill.images.size})"
                }
            }
            //是否是变更账单
            val isChangeBill = bill.money.compareTo(BigDecimal.ZERO) == 1//money > 0 修改时金额大于零
            if (isChangeBill) {
                //抹0再输入到键盘
                with(bill.money.toPlainString()) {
                    if (contains(".00"))
                        replace(".00", "")
                    else
                        this
                }.forEach { element -> binding.keyboard.input(element.toString()) }
            }
            //设置类别
            bill.category?.let {
                categoryTabFragment.setSelectCategory(it, bill.type)
            }
        }
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        categoryTabFragment.toolBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        categoryTabFragment.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun category() {
        categoryTabFragment =
            childFragmentManager.findFragmentById(R.id.categoryFragment) as CategoryTabFragment
        categoryTabFragment.setIndex()
    }

    private fun remark() {
        binding.inputInfo.eidtRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                billViewModel.setRemark(s.toString().trim { it <= ' ' })
            }
        })
    }

    private fun setupYearAndDay(initialTime: Date = billViewModel.getBill().billTime) {
        binding.inputInfo.tvBillTime.text = initialTime.string() //设置日历初始选中时间
        binding.inputInfo.tvBillTime.setOnClickListener {
            val onDateSetListener =
                OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val selectCalendar = initialTime.calendar()
                    selectCalendar[year, month] = dayOfMonth
                    setBillTime(selectCalendar.time.string())
                    selectHourAndMinute(
                        year = year,
                        month = month + 1,//实际保存时，选择的时间需要+1（month：0-11 ）
                        dayOfMonth = dayOfMonth,
                        hourOfDay = selectCalendar[Calendar.HOUR_OF_DAY],
                        minute = selectCalendar[Calendar.MINUTE]
                    )
                }
            val yearMonth = YearMonth.format(initialTime)
            val dialog = DatePickerDialog(
                mainActivity,
                onDateSetListener,
                yearMonth.year,
                yearMonth.month - 1,
                yearMonth.day
            )
            dialog.setOnDateSetListener(onDateSetListener)
            dialog.show()
        }
    }

    /**
     * 选择小时和分钟
     *
     * @param yearTime 年份-月份
     */
    private fun selectHourAndMinute(
        year: Int,
        month: Int,
        dayOfMonth: Int,
        hourOfDay: Int,
        minute: Int
    ) {
        val onTimeSetListener =
            OnTimeSetListener { _: TimePicker?, hourOfDay: Int, minute: Int ->
                if (hourOfDay == 0 && minute == 0) return@OnTimeSetListener
                val selectTime =
                    "$year-$month-$dayOfMonth $hourOfDay:$minute:00"//yyyy-MM-dd hh:mm:00
                setBillTime(selectTime)
            }
        val timePickerDialog =
            TimePickerDialog(mainActivity, onTimeSetListener, hourOfDay, minute, true)
        timePickerDialog.show()
    }

    /**
     * 赋值
     *
     * @param selectTime 选中的日期或更精确的
     */
    private fun setBillTime(selectTime: String) {
        binding.inputInfo.tvBillTime.text = selectTime
        billViewModel.setTime(selectTime.date())
        LogUtils.d(selectTime)
    }

    /**
     * 选择经手人
     */
    private fun setupPerson() {
        billViewModel.getDealers().observe(this) { names ->
            //经手人名单
            if (names.size > 0) {
                setDealerUser(names[0])//默认经手人
                billViewModel.setDealer(names[0]) //设置默经手人
            } else {
                setDealerUser(currentUser.name)
                billViewModel.setDealer(currentUser.name) //设置默经手人当前用户
            }
            binding.inputInfo.tvUserLabel.setOnClickListener {
                XPopup.Builder(requireContext())
                    .maxHeight(binding.keyboard.height)
                    .asBottomList(
                        "请选择经手人", names.toTypedArray()
                    ) { _: Int, text: String ->
                        setDealerUser(text)
                        billViewModel.setDealer(text)
                    }
                    .show()
            }
        }

    }

    private fun setDealerUser(dealerUser: String) {
        binding.inputInfo.tvUserLabel.text = "经手人: $dealerUser"
    }

    private fun keyboardListener() {
        binding.keyboard.setKeyboardListener(object : OnKeyboardListener {
            override fun save(result: String) {
                ToastUtils.showLong(result)
                billViewModel.setImages(popupSelectImage.getImagesPath())
                saveBill(result, close = true)
            }

            override fun calculation(result: String) {
                binding.inputInfo.tvMoney.text = result
            }

            override fun saveAgain(result: String) {
                ToastUtils.showLong(result)
                saveBill(result, close = false)
                reset()
            }
        })
    }

    private fun changeMoneyTextColor(billType: BillType) {
        val color = if (billType == BillType.EXPENDITURE) R.color.expenditure else R.color.income
        binding.inputInfo.tvMoney.setTextColor(resources.getColor(color, null))
    }

    private fun saveBill(money: String, close: Boolean) {
        if (TextUtils.isEmpty(money) || money == "0") {
            ToastUtils.showShort("未填写金额")
            return
        }
        billViewModel.apply {
            setMoney(money)
            save { bill: Bill ->
                if (close) {
                    findNavController().navigateUp()
                }
                DataBus.post(SyncEvent.ADD, bill.copy())
            }
        }
    }

    /**
     * 票据图片
     */
    private fun setupImage() {
        popupSelectImage = PopSelectImage(mainActivity).apply {
            deleteListener = {
                ToastUtils.showLong(it.toString())
            }
            selectImages = {
                billViewModel.setImages(getImagesPath())
            }
        }
        billViewModel.getBillImages().observe(this, popupSelectImage)
        binding.inputInfo.imgTicket.setOnClickListener {
            if (popupSelectImage == null)
                popupSelectImage = PopSelectImage(mainActivity)
            XPopup.Builder(requireContext())
                .asCustom(popupSelectImage)
                .show()
            //selectImagePou.getLayoutParams().height = binding.keyboard.getRoot().getHeight();
            //popupSelectImage.clear()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) { //选中的照片
                val mSelected: MutableList<String> = ArrayList()
                obtainResult(data!!)!!.forEach(Consumer { uri: Uri ->
                    val imgUrl = UriUtils.uri2File(uri).absolutePath
                    mSelected.add(imgUrl)
                })
                //setImages(mSelected)

                if (popupSelectImage.getImages().size > 0) {
                    mSelected.forEach { localPath: String ->
                        popupSelectImage.getImages().forEach { image ->
                            /**
                             * 包含的话就删除重新加
                             */
                            if (image.localPath == localPath) {
                            }
                        }

                    }
                } else {
                    popupSelectImage.setImages(mSelected.map { selectPath ->
                        Image(billID = billViewModel.getBill().id).apply {
                            localPath = selectPath
                            synced = STATUS.NOT_SYNCED
                        }
                    }.toMutableList())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val stack: Stack<String>? = billViewModel.keyBoardStack
        if (null != stack && !stack.isEmpty()) {
            binding.keyboard.post {
                binding.keyboard.stack = stack
            }
        }
    }

    override fun onPause() {
        super.onPause()
        billViewModel.keyBoardStack = binding.keyboard.stack
    }

//    private fun setImages(selected: List<String>): MutableList<BillPhotoEntity> {
//        return selected.map {
//            BillPhotoEntity().apply {
//                val lastModified = File(it).lastModified()
//                createTime = TimeUtils.millis2String(lastModified)
//                path = it
//            }
//        }.toMutableList()
//    }

    fun reset() {
        binding.keyboard.clear()
        binding.inputInfo.eidtRemark.setText("")
        binding.inputInfo.tvMoney.text = "0"
        popupSelectImage.clear()
    }

    override fun selected(category: Category) {
        val billType = BillType.transform(category.type)
        billViewModel.setCategory(category)
        binding.keyboard.setType(billType)
        changeMoneyTextColor(billType)
    }
}