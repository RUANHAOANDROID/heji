package com.hao.heji.ui.create

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
import com.google.android.material.tabs.TabLayout
import com.lxj.xpopup.XPopup
import com.hao.heji.*
import com.hao.heji.config.Config
import com.hao.heji.data.BillType
import com.hao.heji.data.converters.DateConverters
import com.hao.heji.data.converters.MoneyConverters.ZERO_00
import com.hao.heji.data.db.*
import com.hao.heji.data.db.mongo.ObjectId
import com.hao.heji.databinding.FragmentCreatebillBinding
import com.hao.heji.ui.base.BaseFragment
import com.hao.heji.ui.base.FragmentViewPagerAdapter
import com.hao.heji.ui.base.render
import com.hao.heji.ui.category.manager.CategoryManagerFragmentArgs
import com.hao.heji.ui.create.*
import com.hao.heji.ui.popup.SelectImagePopup
import com.hao.heji.utils.YearMonth
import com.hao.heji.utils.matisse.MatisseUtils
import com.hao.heji.widget.KeyBoardView.OnKeyboardListener
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
class CreateBillFragment : BaseFragment() {

    internal val viewModel by lazy {
        ViewModelProvider(
            this,
            CreateBillViewModelFactory(mainActivity.mService.getSyncPusher())
        )[CreateBillViewModel::class.java]
    }

    private lateinit var pagerAdapter: FragmentViewPagerAdapter

    //支出|收入
    private val tabTitles = listOf(
        BillType.EXPENDITURE.valueString, BillType.INCOME.valueString
    )

    //SelectCategoryFragment
    internal lateinit var categoryFragment: CategoryFragment

    private val categoryFragments = listOf(
        CategoryFragment.newInstance(BillType.EXPENDITURE),
        CategoryFragment.newInstance(BillType.INCOME)
    )

    val binding: FragmentCreatebillBinding by lazy {
        FragmentCreatebillBinding.inflate(layoutInflater)
    }

    //图片弹窗
    lateinit var popupSelectImage: SelectImagePopup

    //照片选择
    lateinit var imageSelectLauncher: ActivityResultLauncher<Intent>

    /**
     * 是否是修改账单
     * 当isModify为true时为要修改的账单
     * 默认新增
     */
    private var isModify = false

    /**
     * 使用 bill控制页面（TODO 优化为vm托管bill，页面回复后根据vm bill重建）
     */
    private lateinit var mBill: Bill

    override fun layout() = binding.root

    /**
     * 修改时预先选中类别
     *
     * @param category
     * @param type
     */
    private fun setSelectCategory(category: String, type: Int) {
        //内容页绘制完成后选中类别
        binding.vpContent.post {
            if (type == BillType.EXPENDITURE.valueInt) {
                binding.tab.getTabAt(0)?.select()
                categoryFragments[0].setSelectCategory(category)
            } else if (type == BillType.INCOME.valueInt) {
                binding.tab.getTabAt(1)?.select()
                categoryFragments[1].setSelectCategory(category)
            }
        }
    }

    /**
     *
     * @see CategoryFragment.setCategories
     * @param type
     * @param categories
     */
    private fun setCategories(type: Int, categories: MutableList<Category>) {
        LogUtils.d(
            "TimeTest",
            TimeUtils.millis2String(System.currentTimeMillis(), "yyyy/MM/dd HH:mm:ss")
        )
        if (type == BillType.EXPENDITURE.valueInt) {
            categoryFragments[0].setCategories(categories)
        } else if (type == BillType.INCOME.valueInt) {
            categoryFragments[1].setCategories(categories)
        }
        val billType = BillType.transform(type)
        binding.keyboard.setType(billType)
        val color = if (billType == BillType.EXPENDITURE) R.color.expenditure else R.color.income
        binding.tvMoney.setTextColor(resources.getColor(color, null))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pagerAdapter = FragmentViewPagerAdapter(
            childFragmentManager, categoryFragments, tabTitles
        )
        /**
         * 选择照片
         */
        imageSelectLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
                if (result.resultCode != Activity.RESULT_OK) {
                    return@ActivityResultCallback
                }
                val obtainResult = Matisse.obtainResult(result.data)
                val obtainPathResult = Matisse.obtainPathResult(result.data)
                LogUtils.d("OnActivityResult ${Matisse.obtainOriginalState(result.data)}")

                val mSelected: MutableList<String> = ArrayList()
                obtainResult.forEach(Consumer { uri: Uri ->
                    val imgUrl = UriUtils.uri2File(uri).absolutePath
                    mSelected.add(imgUrl)
                })

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
                            syncStatus = STATUS.NOT_SYNCED
                        }
                    }.toMutableList())
                }
            })

        val mArgs = CreateBillFragmentArgs.fromBundle(requireArguments()).argAddBill
        isModify = mArgs.isModify
        mBill = mArgs.bill ?: Bill(time = Date())
        LogUtils.d(mBill.toString())
    }

    private fun showPager() {
        val pagerAdapter = FragmentViewPagerAdapter(
            childFragmentManager,
            categoryFragments,
            tabTitles
        )

        binding.vpContent.apply {
            adapter = pagerAdapter
            //TabLayout+ViewPager联动 1
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.tab))
        }
        binding.tab.apply {
            setupWithViewPager(binding.vpContent)
            //TabLayout+ViewPager联动 2
            addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(binding.vpContent))
            getTabAt(0)!!.select()
            //mSelectedCategoryListener.selected(categoryFragments[0].getSelectedCategory()!!)//默认支出
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabSelected", tab.position)
                    val type =
                        if (tab.position == 0) BillType.EXPENDITURE.valueInt else BillType.INCOME.valueInt
                    viewModel.doAction(CreateBillAction.GetCategories(type))
                    mBill.type = type
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabUnselected", tab.position)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    LogUtils.d("onTabReselected", tab.position)
                }
            })
            getTabAt(0)!!.select()
        }

    }

    override fun initView(rootView: View) {
        binding.imgAddCategory.setOnClickListener {
            findNavController().navigate(
                R.id.nav_category_manager,
                CategoryManagerFragmentArgs.Builder().setIeType(mBill.type).build()
                    .toBundle()
            )
        }

        showPager()
        popupSelectImage = SelectImagePopup(requireActivity()).apply {
            deleteListener = {
                ToastUtils.showLong(it.toString())
                viewModel.doAction(CreateBillAction.DeleteImage(it))
            }
            selectedImagesCall = {
                getImagesPath()
            }
            selectListener = { maxCount ->
                MatisseUtils.selectMultipleImage(
                    requireActivity(),
                    maxCount,
                    launcher = imageSelectLauncher
                )
            }
        }

        binding.imgTicket.setOnClickListener {
            XPopup.Builder(requireContext())
                .asCustom(popupSelectImage)
                .show()
        }


        binding.eidtRemark.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                mBill.remark = s.toString().trim { it <= ' ' }
            }
        })

        keyboardListener()
        viewModel.doAction(CreateBillAction.GetDealers(mBill.id))
        with(mBill) {
            setTime(time)
            setDealer(dealer)
            category?.let { setSelectCategory(it, type) }
            setMoney(money)
            if (images.isNotEmpty()) {
                viewModel.doAction(CreateBillAction.GetImages(images))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        render(viewModel) { uiState ->
            when (uiState) {
                is CreateBillUIState.BillChange -> {
                    val bill = uiState.bill
                    LogUtils.d(bill)
                }
                is CreateBillUIState.Images -> {
                    val images = uiState.images
                    LogUtils.d(uiState.images)
                    popupSelectImage.setImage(images)
                }
                is CreateBillUIState.Error -> {
                    ToastUtils.showLong(uiState.throws.message)
                }
                is CreateBillUIState.Dealers -> {
                    //经手人名单
                    if (uiState.dealers.size > 0) {
                        setDealer(uiState.dealers[0])//设置默经手人
                    } else {
                        setDealer(Config.user.name) //设置默经手人当前用户
                    }
                    binding.tvUserLabel.setOnClickListener {
                        XPopup.Builder(requireContext())
                            .maxHeight(binding.keyboard.height)
                            .asBottomList(
                                "请选择经手人", uiState.dealers.toTypedArray()
                            ) { _: Int, text: String ->
                                setDealer(text)
                            }
                            .show()
                    }
                }
                is CreateBillUIState.Save -> {
                    if (uiState.again) reset() else findNavController().popBackStack()
                }
                is CreateBillUIState.Categories -> {
                    setCategories(uiState.type, uiState.categories)
                }
            }
        }
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        binding.toolbar.setNavigationIcon(R.drawable.ic_baseline_close_24)
        binding.toolbar.setNavigationOnClickListener {
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
                this@CreateBillFragment.save(false)
            }

            override fun calculation(result: String) {
                binding.tvMoney.text = result
            }

            override fun saveAgain(result: String) {
                ToastUtils.showLong(result)
                this@CreateBillFragment.save(true)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val stack: Stack<String>? = viewModel.keyBoardStack
        if (!stack.isNullOrEmpty()) {
            binding.keyboard.post {
                binding.keyboard.stack = stack
            }
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.keyBoardStack = binding.keyboard.stack
    }

    /**
     * @param type 首先返回Type保证页面响应
     * @param category
     */
    fun selectedCategory(type: Int, category: Category?) {
        LogUtils.d("selectedCategory : type=$type category= $category")
        val billType = BillType.transform(type)
        if (null != category) {
            mBill.category = category.name
        } else {
            mBill.category = null
        }
    }

    private fun setMoney(money: BigDecimal) {
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
        binding.apply {
            tvMoney.text = mBill.money.toString()
            mBill.dealer?.let { setDealer(it) }
            tvBillTime.text = mBill.time.string()
            mBill.remark?.let { remark ->
                eidtRemark.setText(remark)
            }
            val existImages = mBill.images.isNotEmpty()
            if (existImages) {
                imgTicket.text = "图片(x${mBill.images.size})"
            }
        }
    }

    private fun setDealer(dealer: String?) {
        dealer?.let {
            binding.tvUserLabel.text = "经手人: $dealer"
            mBill.dealer = dealer
        }
    }

    private fun setTime(selectTime: Date) {
        binding.tvBillTime.text = DateConverters.date2Str(selectTime)
        mBill.time = selectTime
        LogUtils.d(selectTime)
        binding.tvBillTime.text = mBill.time.string() //设置日历初始选中时间
        binding.tvBillTime.setOnClickListener {
            val onDateSetListener =
                OnDateSetListener { datePicker: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
                    val selectCalendar = mBill.time.calendar()
                    selectCalendar[year, month] = dayOfMonth
                    binding.tvBillTime.text = selectCalendar.time.string()
                    mBill.time = selectTime
                    selectHourAndMinute(
                        year = year,
                        month = month + 1,//实际保存时，选择的时间需要+1（month：0-11 ）
                        dayOfMonth = dayOfMonth,
                        hourOfDay = selectCalendar[Calendar.HOUR_OF_DAY],
                        minute = selectCalendar[Calendar.MINUTE]
                    )
                }
            val yearMonth = YearMonth.format(mBill.time)
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


    private fun save(again: Boolean) {
        try {
            mBill.money = BigDecimal(binding.tvMoney.text.toString())
            //check value is false throw error
            check(mBill.money != ZERO_00()) { "金额不能为 ${ZERO_00().toPlainString()}" }
            check(mBill.money != BigDecimal.ZERO) { "金额不能为 ${BigDecimal.ZERO.toPlainString()}" }
            check(mBill.category != null) { "未选类别" }
            viewModel.doAction(CreateBillAction.Save(mBill, again))
        } catch (e: Exception) {
            ToastUtils.showLong(e.message)
        }
    }

    private fun reset() {
        mBill.apply {
            id = ObjectId().toHexString()
            money = BigDecimal.ZERO
        }
        binding.keyboard.clear()
        binding.eidtRemark.setText("")
        binding.tvMoney.text = "0"
        popupSelectImage.clear()
    }
}