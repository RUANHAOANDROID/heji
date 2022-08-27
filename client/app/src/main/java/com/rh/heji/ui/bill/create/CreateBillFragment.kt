package com.rh.heji.ui.bill.create

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.*
import com.lxj.xpopup.XPopup

import com.rh.heji.*
import com.rh.heji.data.BillType
import com.rh.heji.data.converters.DateConverters
import com.rh.heji.data.db.*
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.databinding.FragmentAddbillBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.category.CategoryTabFragment
import com.rh.heji.ui.bill.category.CategoryViewModel
import com.rh.heji.ui.bill.category.ISelectedCategory
import com.rh.heji.utlis.YearMonth
import com.rh.heji.utlis.matisse.MatisseUtils
import com.rh.heji.widget.KeyBoardView.OnKeyboardListener
import com.zhihu.matisse.Matisse
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

/**
 * 添加账单（支出/收入）
 * -----------title------------
 * 收入|
 * -----------category---------
 * 账单类别
 * ----------
 */
class CreateBillFragment : BaseFragment(), ISelectedCategory, ICreateBillUIState {
    companion object {
        const val SAVE_SUCCESS = 1
        const val SAVE_SUCCESS_AGAIN = 2
        const val SAVE_ERROR = 3
    }

    private val billViewModel by lazy {
        ViewModelProvider(
            this,
            CreateBillViewModelFactory(mainActivity.mService.getBillSyncManager())
        )[CreateBillViewModel::class.java]
    }
    val categoryViewModel by lazy { ViewModelProvider(this)[CategoryViewModel::class.java] }

    private lateinit var binding: FragmentAddbillBinding
    private lateinit var categoryTabFragment: CategoryTabFragment

    lateinit var popupSelectImage: PopSelectImage//图片弹窗
    lateinit var imageSelectLauncher: ActivityResultLauncher<Intent>

    /**
     * 是否修改
     */
    private var isModify = false//默认新增

    /**
     * 当isModify为true时为要修改的账单
     */
    private lateinit var mBill: Bill

    override fun layoutId(): Int {
        return R.layout.fragment_addbill
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        imageSelectLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback() { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    return@ActivityResultCallback
                }
                val data = result.data
                val obtainResult = Matisse.obtainResult(data)
                val obtainPathResult = Matisse.obtainPathResult(data)
                LogUtils.e("OnActivityResult ${Matisse.obtainOriginalState(data)}")

                val mSelected: MutableList<String> = ArrayList()
                obtainResult.forEach(Consumer { uri: Uri ->
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
                        Image(billID = mBill.id).apply {
                            localPath = selectPath
                            synced = STATUS.NOT_SYNCED
                        }
                    }.toMutableList())
                }
            })
        val mArgs = AddBillFragmentArgs.fromBundle(requireArguments()).argAddBill
        isModify = mArgs.isModify
        mBill = mArgs.bill ?: Bill(billTime = Date())
        billViewModel.getSaveResult().observe(this) {
            when (it) {
                SAVE_SUCCESS -> findNavController().popBackStack()
                SAVE_SUCCESS_AGAIN -> reset()
                SAVE_ERROR -> ToastUtils.showLong("保存失败")
            }
        }
    }

    override fun initView(rootView: View) {
        binding = FragmentAddbillBinding.bind(rootView)
        initBill(mBill)
        popupSelectImage = PopSelectImage(mainActivity).apply {
            deleteListener = {
                ToastUtils.showLong(it.toString())
            }
            selectedImagesCall = {
                getImagesPath()
            }
            selectListener = { maxCount ->
                MatisseUtils.selectMultipleImage(
                    mainActivity,
                    maxCount,
                    launcher = imageSelectLauncher
                )
            }
        }

        binding.inputInfo.imgTicket.setOnClickListener {
            XPopup.Builder(requireContext())
                .asCustom(popupSelectImage)
                .show()
        }

        billViewModel.getDealers().observe(this) { names ->
            //经手人名单
            if (names.size > 0) {
                setDealer(names[0])//设置默经手人
            } else {
                setDealer(App.user.name) //设置默经手人当前用户
            }
            binding.inputInfo.tvUserLabel.setOnClickListener {
                XPopup.Builder(requireContext())
                    .maxHeight(binding.keyboard.height)
                    .asBottomList(
                        "请选择经手人", names.toTypedArray()
                    ) { _: Int, text: String ->
                        setDealer(text)
                    }
                    .show()
            }
        }

        binding.inputInfo.eidtRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mBill.remark = s.toString().trim { it <= ' ' }
            }
        })
        categoryTabFragment =
            childFragmentManager.findFragmentById(R.id.categoryFragment) as CategoryTabFragment
        categoryTabFragment.setIndex()

        keyboardListener()

    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        categoryTabFragment.toolBar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        categoryTabFragment.toolBar.setNavigationOnClickListener {
            findNavController().popBackStack()
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
                setTime(DateConverters.str2Date(selectTime))
            }
        val timePickerDialog =
            TimePickerDialog(mainActivity, onTimeSetListener, hourOfDay, minute, true)
        timePickerDialog.show()
    }


    private fun keyboardListener() {
        binding.keyboard.setKeyboardListener(object : OnKeyboardListener {
            override fun save(result: String) {
                ToastUtils.showLong(result)
                mBill.images = popupSelectImage.getImagesPath()
                this@CreateBillFragment.save(mBill)
            }

            override fun calculation(result: String) {
                binding.inputInfo.tvMoney.text = result
            }

            override fun saveAgain(result: String) {
                ToastUtils.showLong(result)
                this@CreateBillFragment.saveAgain(mBill)
                reset()
            }
        })
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

    override fun selected(category: Category) {
        val billType = BillType.transform(category.type)
        //billViewModel.setCategory(category)
        mBill.type = category.type
        mBill.category = category.category
        binding.keyboard.setType(billType)
        val color = if (billType == BillType.EXPENDITURE) R.color.expenditure else R.color.income
        binding.inputInfo.tvMoney.setTextColor(resources.getColor(color, null))
    }

    override fun setCategory(category: String?) {
        //设置类别
        mBill.category?.let {
            if (this::categoryTabFragment.isInitialized)
                categoryTabFragment.setSelectCategory(it, mBill.type)
        }
    }

    override fun setRemark(remark: String?) {

    }

    override fun setMoney(money: BigDecimal) {
        //填充money到键盘，抹0再输入到键盘
        with(money.toPlainString()) {
            if (contains(".00"))
                replace(".00", "")
            else
                this
        }.forEach { element ->
            binding.keyboard.input(element.toString())
        }
        //填充输入信息
        binding.inputInfo.apply {
            tvMoney.text = mBill.money.toString()
            mBill.dealer?.let { setDealer(it) }
            tvBillTime.text = mBill.billTime.string()
            mBill.remark?.let { remark ->
                eidtRemark.setText(remark)
            }
            val existImages = mBill.images.isNotEmpty()
            if (existImages) {
                imgTicket.text = "图片(x${mBill.images.size})"
            }
        }
    }

    override fun setDealer(dealer: String?) {
        dealer?.let {
            binding.inputInfo.tvUserLabel.text = "经手人: $dealer"
            mBill.dealer = dealer
        }
    }

    override fun setImages(images: List<String>) {

    }

    override fun setTime(selectTime: Date) {
        binding.inputInfo.tvBillTime.text = DateConverters.date2Str(selectTime)
        mBill.billTime = selectTime
        LogUtils.d(selectTime)
        binding.inputInfo.tvBillTime.text = mBill.billTime.string() //设置日历初始选中时间
        binding.inputInfo.tvBillTime.setOnClickListener {
            val onDateSetListener =
                OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val selectCalendar = mBill.billTime.calendar()
                    selectCalendar[year, month] = dayOfMonth
                    binding.inputInfo.tvBillTime.text = selectCalendar.time.string()
                    mBill.billTime = selectTime
                    selectHourAndMinute(
                        year = year,
                        month = month + 1,//实际保存时，选择的时间需要+1（month：0-11 ）
                        dayOfMonth = dayOfMonth,
                        hourOfDay = selectCalendar[Calendar.HOUR_OF_DAY],
                        minute = selectCalendar[Calendar.MINUTE]
                    )
                }
            val yearMonth = YearMonth.format(mBill.billTime)
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

    override fun saveAgain(bill: Bill) {
        try {
            checkBill()
            billViewModel.save(bill, SAVE_SUCCESS_AGAIN)
        } catch (e: Exception) {
            ToastUtils.showLong(e.message)
        }

    }

    override fun save(bill: Bill) {
        try {
            mBill.category = categoryTabFragment.getSelectCategory().category
            checkBill()
            bill.createTime = System.currentTimeMillis()
            billViewModel.save(bill, SAVE_SUCCESS)
        } catch (e: Exception) {
            ToastUtils.showLong(e.message)
        }
    }

    private fun checkBill() {
        val inputMoney = binding.inputInfo.tvMoney.text.toString()
        check(inputMoney != "0") { "未输入金额" }
        mBill.apply {
            money = BigDecimal(inputMoney)
        }
        check(mBill.category != null) { "类别未选" }
    }

    fun reset() {
        mBill.apply {
            id = ObjectId().toHexString()
            money = BigDecimal.ZERO
        }
        binding.keyboard.clear()
        binding.inputInfo.eidtRemark.setText("")
        binding.inputInfo.tvMoney.text = "0"
        popupSelectImage.clear()
    }
}