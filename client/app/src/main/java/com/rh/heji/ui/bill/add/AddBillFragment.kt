package com.rh.heji.ui.bill.add

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.UriUtils
import com.lxj.xpopup.XPopup
import com.matisse.Matisse.Companion.obtainResult
import com.matisse.entity.ConstValue.REQUEST_CODE_CHOOSE
import com.rh.heji.AppCache.Companion.instance
import com.rh.heji.BuildConfig
import com.rh.heji.R
import com.rh.heji.data.BillType
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.Category
import com.rh.heji.data.db.mongo.ObjectId
import com.rh.heji.databinding.IncomeFragmentBinding
import com.rh.heji.network.request.BillEntity
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.add.adapter.TicketEntity
import com.rh.heji.ui.category.CategoryTabFragment
import com.rh.heji.ui.category.CategoryViewModule
import com.rh.heji.widget.KeyBoardView.OnKeyboardListener
import java.io.File
import java.util.*
import java.util.function.Consumer

/**
 * 支出 收入
 */
class AddBillFragment : BaseFragment() {
    private val billViewModel by lazy { getViewModel(AddBillViewModel::class.java) }
    lateinit var categoryViewModule: CategoryViewModule

    lateinit var binding: IncomeFragmentBinding
    lateinit var categoryTabFragment: CategoryTabFragment
    var selectImagePou: SelectImagePop? = null//图片弹窗

    override fun layoutId(): Int {
        return R.layout.income_fragment
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        categoryViewModule = getViewModel(CategoryViewModule::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /**
         * 不复用View，否则xml fragment不触发生命周期
         */
        view = inflater.inflate(layoutId(), container, false)
        initView(view)
        return view
    }

    override fun initView(view: View) {
        binding = IncomeFragmentBinding.bind(view)
        selectImage()
        selectPerson()
        selectYearAndDay()
        remark()
        category()
        keyboardListener()
    }

    private fun category() {
        categoryTabFragment = childFragmentManager.findFragmentById(R.id.categoryFragment) as CategoryTabFragment
        categoryViewModule.selectCategoryLiveData.observe(viewLifecycleOwner, Observer { category: Category? ->
            if (null != category) {
                val billType = BillType.transform(category.type)
                changeMoneyTextColor(billType)
                var categoryName: String? = category.category
                if (category.category == "管理") {
                    categoryName = billType.text()
                }
                billViewModel.bill.setCategory(categoryName)
                billViewModel.bill.setType(category.type)
                changeMoneyTextColor(billType)
            }
        })
    }

    private fun remark() {
        binding.eidtRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                billViewModel.bill.setRemark(s.toString().trim { it <= ' ' })
            }
        })
    }

    private fun selectYearAndDay() {
        val arguments = arguments
        val calendar = AddBillFragmentArgs.fromBundle(arguments!!).calendar
        billViewModel.time = TimeUtils.date2String(calendar.time) //设置日历选中的时间
        val nowTime = billViewModel.time
        binding.tvBillTime.text = nowTime
        binding.tvBillTime.setOnClickListener {
            val onDateSetListener = OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                val selectCalendar = Calendar.getInstance()
                selectCalendar[year, month] = dayOfMonth
                val yearTime = TimeUtils.date2String(selectCalendar.time, "yyyy-MM-dd") + " 00:00" //未选时自动补全
                setNoteTime(yearTime)
                selectHourAndMinute(yearTime)
            }
            val dialog = DatePickerDialog(mainActivity, onDateSetListener, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])
            dialog.setOnDateSetListener(onDateSetListener)
            dialog.show()
        }
    }

    /**
     * 选择小时和分钟
     *
     * @param yearTime 年份-月份
     */
    private fun selectHourAndMinute(yearTime: String) {
        val onTimeSetListener = OnTimeSetListener { timePicker: TimePicker?, hourOfDay: Int, minute: Int ->
            if (hourOfDay == 0 && minute == 0) return@OnTimeSetListener
            val calendar2 = Calendar.getInstance()
            calendar2.time = TimeUtils.string2Date(yearTime, "yyyy-MM-dd HH:mm")
            calendar2[Calendar.HOUR_OF_DAY] = hourOfDay
            calendar2[Calendar.MINUTE] = minute
            val dayTime = TimeUtils.date2String(calendar2.time, "yyyy-MM-dd HH:mm")
            setNoteTime(dayTime)
        }
        val timePickerDialog = TimePickerDialog(mainActivity, onTimeSetListener, 0, 0, true)
        timePickerDialog.show()
    }

    /**
     * 赋值
     *
     * @param selectTime 选中的日期或更精确的
     */
    private fun setNoteTime(selectTime: String) {
        binding.tvBillTime.text = selectTime
        billViewModel.time = selectTime
        if (BuildConfig.DEBUG) {
            LogUtils.d(selectTime)
            val billTime = TimeUtils.string2Millis(billViewModel.time, "yyyy-MM-dd HH:mm:ss")
            LogUtils.d(TimeUtils.millis2String(billTime, "yyyy-MM-dd HH:mm:ss"))
        }
    }

    /**
     * 选择经手人
     */
    private fun selectPerson() {
        val names = billViewModel.dealers //经手人名单
        if (names.size > 0) {
            binding.tvUserLabel.text = "经手人:" + names[0] //默认经手人
            billViewModel.bill.setDealer(names[0]) //设置默经手人
        }
        binding.tvUserLabel.setOnClickListener {
            XPopup.Builder(context)
                    .maxHeight(binding.keyboard.height)
                    .asBottomList("请选择经手人", names.toTypedArray()
                    ) { position: Int, text: String ->
                        binding.tvUserLabel.text = "经手人:$text"
                        billViewModel.bill.setDealer(text)
                    }
                    .show()
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun keyboardListener() {
        binding.keyboard.setKeyboardListener(object : OnKeyboardListener {
            override fun save(result: String) {
                ToastUtils.showLong(result)
                val category = categoryViewModule.selectCategory
                saveBill(result, category, Observer { bill: Bill? ->
                    instance.appViewModule.billPush(BillEntity(bill))
                    mainActivity.navController.popBackStack()
                })
            }

            override fun calculation(result: String) {
                binding.tvMoney.text = result
            }

            override fun saveAgain(result: String) {
                ToastUtils.showLong(result)
                saveBill(result, categoryViewModule.selectCategory, Observer { bill: Bill? -> instance.appViewModule.billPush(BillEntity(bill)) })
                clear()
            }
        })
    }

    private fun changeMoneyTextColor(billType: BillType) {
        val textColor = if (billType == BillType.EXPENDITURE) android.R.color.holo_red_dark else android.R.color.holo_green_dark
        binding.tvMoney.setTextColor(resources.getColor(textColor, null))
    }

    private fun saveBill(money: String, category: Category, saveObserver: Observer<Bill>) {
        if (TextUtils.isEmpty(money) || money == "0") {
            ToastUtils.showShort("未填写金额")
            return
        }
        billViewModel.bill.setCategory(category.category)
        billViewModel.save(ObjectId().toString(), money, category).observe(this, saveObserver)
    }

    /**
     * 票据图片
     */
    private fun selectImage() {
        selectImagePou = context?.let { SelectImagePop(it, mainActivity) }
        binding.imgTicket.setOnClickListener {
            if (selectImagePou == null) selectImagePou = SelectImagePop(context!!, mainActivity)
            XPopup.Builder(context)
                    .asCustom(selectImagePou)
                    .show()
            //selectImagePou.getLayoutParams().height = binding.keyboard.getRoot().getHeight();
            selectImagePou?.setDeleteClickListener { data: List<String> -> billViewModel.imgUrls = data as MutableList<String> }
            selectImagePou?.setData(ArrayList())
        }
        val imgObserver = Observer { data: List<String?> ->
            binding.imgTicket.text = "图片(x" + data.size + ")"
            selectImagePou?.setData(data)
        }
        billViewModel.imgUrlsLive.observe(viewLifecycleOwner, imgObserver)
    }

    override fun onDetach() {
        super.onDetach()
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
                    mSelected.stream().forEach { s: String ->
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
            binding.keyboard.setStack(stack)
        }
    }

    override fun onPause() {
        super.onPause()
        if (binding != null && billViewModel != null) {
            billViewModel.keyBoardStack = binding.keyboard.stack
        }
    }

    private fun setImages(selected: List<String>) {
        val photos: MutableList<TicketEntity> = ArrayList()
        for (item in selected) {
            val info = TicketEntity()
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