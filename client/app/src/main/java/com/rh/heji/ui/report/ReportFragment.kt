package com.rh.heji.ui.report
import android.view.View
import com.blankj.utilcode.util.ToastUtils
import com.rh.heji.R
import com.rh.heji.ui.base.BaseFragment


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
        showYearMonthTitle({ year, month ->
            ToastUtils.showLong("$year,$month")
        })
    }

    override fun initView(rootView: View) {
        reportViewModel.text.observe(viewLifecycleOwner, { })
    }

}