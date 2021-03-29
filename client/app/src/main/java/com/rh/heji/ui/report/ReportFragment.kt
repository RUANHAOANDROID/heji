package com.rh.heji.ui.report

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.TimeUtils
import com.lxj.xpopup.XPopup
import com.rh.heji.R
import com.rh.heji.ui.base.BaseFragment
import com.rh.heji.ui.bill.YearSelectPop
import com.rh.heji.ui.home.BillsHomeFragment
import java.util.function.Consumer

/**
 * 报告统计页面
 */
class ReportFragment : BaseFragment() {

    private val reportViewModel: ReportViewModel by lazy {
        getViewModel(ReportViewModel::class.java)
    }

    override fun layoutId(): Int {
        return R.layout.fragment_gallery
    }

    override fun setUpToolBar() {
        super.setUpToolBar()
        showBlack()
        toolBar.title = "统计"
        addYearMonthView()
    }

    override fun initView(rootView: View) {
        reportViewModel.text.observe(viewLifecycleOwner, { })
    }
    /**
     * 该Menu属于全局所以在这里控制
     */
    fun addYearMonthView() {
        toolBarCenterTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_baseline_arrow_down_white_32,null), null)
        toolBarCenterTitle.compoundDrawablePadding = 8
        val thisYear = reportViewModel.thisYear
        val thisMonth = reportViewModel.thisMonth
        val yearMonth = "$thisYear.$thisMonth"
        toolBarCenterTitle.text = yearMonth
        toolBarCenterTitle.setOnClickListener { v: View? ->
            XPopup.Builder(mainActivity) //.hasBlurBg(true)//模糊
                    .hasShadowBg(true)
                    .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT) //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                    .asCustom(YearSelectPop(mainActivity) { year: Int, month: Int ->
                        toolBarCenterTitle.text = "$year.$month"
                        val fragments = mainActivity.fragments
                        fragments.forEach(Consumer { fragment: Fragment? ->
                            if (fragment is BillsHomeFragment) {
                                fragment.notifyData(year, month)
                            }
                        })
                    }) /*.enableDrag(false)*/
                    .show()
        }
    }
}