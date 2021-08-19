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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.lxj.xpopup.XPopup
import com.matisse.Matisse.Companion.obtainResult
import com.matisse.entity.ConstValue.REQUEST_CODE_CHOOSE
import com.rh.heji.*
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Category
import com.rh.heji.databinding.FragmentIncomeBinding
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.add.adapter.BillPhotoEntity
import com.rh.heji.ui.bill.category.CategoryTabFragment
import com.rh.heji.ui.bill.category.CategoryViewModule
import com.rh.heji.utlis.YearMonth
import com.rh.heji.widget.KeyBoardView.OnKeyboardListener
import java.io.File
import java.math.BigDecimal
import java.util.*
import java.util.function.Consumer

/**
 * 支出 收入
 */
class AddBillFragment : BaseFragment() {
    private val billViewModel by lazy { getViewModel(AddBillViewModel::class.java) }
    val categoryViewModule by lazy { getViewModel(CategoryViewModule::class.java) }

    private lateinit var binding: FragmentIncomeBinding
    private lateinit var categoryTabFragment: CategoryTabFragment
    var selectImagePou: SelectImagePop? = null//图片弹窗

    override fun layoutId(): Int {
        return R.layout.fragment_income
    }

    override fun initView(rootView: View) {
        billViewModel.bill = AddBillFragmentArgs.fromBundle(requireArguments()).bill
        binding = FragmentIncomeBinding.bind(rootView)
        selectImage()
        selectPerson()
        selectYearAndDay()
        remark()
        category()
        keyboardListener()
        billViewModel.billChanged().observe(this, { bill ->
            binding.tvMoney.text = bill.money.toString()
            binding.tvUserLabel.text = bill.dealer
            binding.tvBillTime.text = bill.billTime?.string()
            bill.remark?.let { remark ->
                binding.eidtRemark.setText(remark)
            }
            (bill.imgCount > 0).let {
                billViewModel.getBillImages()
                    .observe(this) { images ->
                        val imageUrl =
                            images.map { img ->
                                val url =
                                    if (img.localPath.isNullOrEmpty()) img.onlinePath else img.localPath
                                url?.let { url -> billViewModel.addImgUrl(url) }
                                return@map url
                            }.toList()
                        selectImagePou?.setData(imageUrl)
                        if (imageUrl.isNotEmpty())
                            binding.imgTicket.text = "图片(x" + images.size + ")"
                    }

            }
            bill.money.toPlainString().let { money ->
                if (bill.money.compareTo(BigDecimal.ZERO) == 1) {
                    val inputString = if (money.contains(".00"))
                        money.replace(".00", "") else money
                    inputString.forEach { element ->
                        binding.keyboard.input(element.toString())
                    }
                }
            }
            bill.category?.let {
                categoryTabFragment.setCategory(it, bill.type)
            }

        })
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
        categoryViewModule.getCategoryType().observe(this, {
            binding.keyboard.setType(it)
        })
        categoryViewModule.getSelectCategory().observe(this, { category: Category? ->
            if (null != category) {
                val billType = BillType.transform(category.type)
                changeMoneyTextColor(billType)
                var categoryName: String? = category.category
                if (category.category == "管理") {
                    categoryName = billType.text()
                }
                billViewModel.bill.category = categoryName
                billViewModel.bill.type = category.type
                binding.keyboard.setType(billType)
            }
        })
    }

    private fun remark() {
        binding.eidtRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                billViewModel.bill.remark = s.toString().trim { it <= ' ' }
            }
        })
    }

    private fun selectYearAndDay(initialTime: Date = billViewModel.bill.billTime) {
        binding.tvBillTime.text = initialTime.string() //设置日历初始选中时间
        binding.tvBillTime.setOnClickListener {
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
            OnTimeSetListener { timePicker: TimePicker?, hourOfDay: Int, minute: Int ->
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
        binding.tvBillTime.text = selectTime
        billViewModel.bill.billTime = selectTime.date()
        LogUtils.d(selectTime)
    }

    /**
     * 选择经手人
     */
    private fun selectPerson() {
        billViewModel.dealersLiveDatabase.observe(this, Observer { names ->
            //经手人名单
            if (names.size > 0) {
                binding.tvUserLabel.text = "经手人:" + names[0] //默认经手人
                billViewModel.bill.dealer = names[0] //设置默经手人
            }
            binding.tvUserLabel.setOnClickListener {
                XPopup.Builder(requireContext())
                    .maxHeight(binding.keyboard.height)
                    .asBottomList(
                        "请选择经手人", names.toTypedArray()
                    ) { position: Int, text: String ->
                        binding.tvUserLabel.text = "经手人:$text"
                        billViewModel.bill.dealer = text
                    }
                    .show()
            }
        })

    }

    private fun keyboardListener() {
        binding.keyboard.setKeyboardListener(object : OnKeyboardListener {
            override fun save(result: String) {
                ToastUtils.showLong(result)
                val category = categoryViewModule.selectCategory
                saveBill(result, category, close = true)
            }

            override fun calculation(result: String) {
                binding.tvMoney.text = result
            }

            override fun saveAgain(result: String) {
                ToastUtils.showLong(result)
                saveBill(result, categoryViewModule.selectCategory, close = false)
                clear()
            }
        })
    }

    private fun changeMoneyTextColor(billType: BillType) {
        val color = if (billType == BillType.EXPENDITURE) R.color.expenditure else R.color.income
        binding.tvMoney.setTextColor(resources.getColor(color, null))
    }

    private fun saveBill(money: String, category: Category, close: Boolean) {
        if (TextUtils.isEmpty(money) || money == "0") {
            ToastUtils.showShort("未填写金额")
            return
        }
        billViewModel.bill.category = category.category
        billViewModel.save(money, category) { bill: Bill ->
            App.getInstance().appViewModule.billPush(bill)
            if (close)
                findNavController().popBackStack()
        }
    }

    /**
     * 票据图片
     */
    private fun selectImage() {
        selectImagePou = context?.let { SelectImagePop(it, mainActivity) }
        binding.imgTicket.setOnClickListener {
            if (selectImagePou == null) selectImagePou = SelectImagePop(mainActivity, mainActivity)
            XPopup.Builder(requireContext())
                .asCustom(selectImagePou)
                .show()
            //selectImagePou.getLayoutParams().height = binding.keyboard.getRoot().getHeight();
            selectImagePou?.setDeleteClickListener { data: List<String> ->
                billViewModel.imgUrls = data as MutableList<String>
            }
            selectImagePou?.setData(ArrayList())
        }
        val imgObserver = Observer { data: List<String?> ->
            binding.imgTicket.text = "图片(x" + data.size + ")"
            selectImagePou?.setData(data)
        }
        billViewModel.imgUrlsLive.observe(this, imgObserver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CHOOSE) { //选中的照片
                val mSelected: MutableList<String> = ArrayList()
                obtainResult(data!!)!!.forEach(Consumer { uri: Uri ->
                    val imgUrl = UriUtils.uri2File(uri).absolutePath
                    mSelected.add(imgUrl)
                })
                setImages(mSelected)
                if (billViewModel.imgUrls.size > 0) {
                    mSelected.forEach { s: String ->
                        /**
                         * 包含的话就删除重新加
                         */
                        if (billViewModel.imgUrls.contains(s)) {
                            billViewModel.imgUrls.remove(s)
                        }
                        billViewModel.addImgUrl(s)
                    }
                } else {
                    billViewModel.imgUrls = mSelected
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

    private fun setImages(selected: List<String>) {
        val photos: MutableList<BillPhotoEntity> = ArrayList()
        for (item in selected) {
            val info = BillPhotoEntity()
            val fileTime = File(item).lastModified()
            val time = TimeUtils.millis2String(fileTime)
            info.createTime = time
            info.path = item
            photos.add(info)
        }
    }

    fun clear() {
        binding.keyboard.clear()
        binding.eidtRemark.setText("")
        binding.tvMoney.text = "0"
        billViewModel.imgUrls = ArrayList()
        selectImagePou!!.clear()
    }

    companion object {
        private const val PATTERN_DB = "yyyy-MM-dd HH:mm:ss"
        private const val PATTERN_SHOW = "yyyy-MM-dd HH:mm"
    }
}