package com.rh.heji.ui.base

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.lxj.xpopup.XPopup
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.ui.bill.YearSelectPop
import com.rh.heji.utlis.Logger
import java.util.*

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
abstract class BaseFragment : Fragment() {
    lateinit var rootView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        rootView = inflater.inflate(layoutId(), container, false)
        initView(rootView)
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            ImmersionBar.with(this)
                    .titleBar(toolBar)
                    .autoDarkModeEnable(true)
                    .transparentStatusBar()
                    .hideBar(BarHide.FLAG_SHOW_BAR)
                    .navigationBarColor(R.color.white)
                    .fullScreen(false)
                    .init();
        } catch (e: Exception) {
            Logger.w(e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        setUpToolBar()
    }

    /**
     * 执行在onCreateView的时候
     *
     * @return layoutID
     */
    protected abstract fun layoutId(): Int

    /**
     * 初始化View
     *
     * @param view
     */
    protected abstract fun initView(rootView: View)
    protected open fun setUpToolBar() {
        try {
            toolBar.menu.clear()
        } catch (e: Exception) {
            Logger.w(e.message)
        }
    }

    protected fun showBlack() {
        toolBar.navigationIcon = blackDrawable()
        toolBar.setNavigationOnClickListener { Navigation.findNavController(rootView).navigateUp() }
    }

    val toolBar: Toolbar
        get() = rootView.findViewById(R.id.toolbar)
    val centerTitle: TextView
        get() = rootView.findViewById(R.id.toolbar_center_title)

    fun <T : ViewModel?> getViewModel(clazz: Class<T>): T {
        return ViewModelProvider(this).get(clazz)
    }

    fun <T : ViewModel?> getActivityViewModel(clazz: Class<T>): T {
        return ViewModelProvider(mainActivity).get(clazz)
    }

    val mainActivity: MainActivity
        get() = activity as MainActivity

    fun blackDrawable(): Drawable {
        val ico = androidx.appcompat.R.drawable.abc_ic_ab_back_material
        return resources.getDrawable(ico, mainActivity.theme)
    }

    /**
     *   拦截回退直接退出  object : Class 内部类指定owner 仅在该Fragment生命周期下有效
     */
    fun registerBackPressed(block: () -> Unit) = mainActivity.onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            block()
        }
    })

    /**
     * 该Menu属于全局所以在这里控制
     */
    fun showYearMonthTitle(selected: YearSelectPop.OnTabSelected,
                           year: Int = Calendar.getInstance().get(Calendar.YEAR),
                           month: Int = Calendar.getInstance().get(Calendar.MONTH) + 1) {
        rootView.post {
            centerTitle.visibility = View.VISIBLE
            centerTitle.setCompoundDrawablesWithIntrinsicBounds(null, null, resources.getDrawable(R.drawable.ic_baseline_arrow_down_white_32, null), null)
            centerTitle.compoundDrawablePadding = 8
            val yearMonth = "$year.$month"
            centerTitle.text = yearMonth
            centerTitle.setOnClickListener { v: View? ->
                XPopup.Builder(mainActivity) //.hasBlurBg(true)//模糊
                        .hasShadowBg(true)
                        .maxHeight(ViewGroup.LayoutParams.WRAP_CONTENT) //.isDestroyOnDismiss(true) //对于只使用一次的弹窗，推荐设置这个
                        .asCustom(YearSelectPop(mainActivity) { selectYear, selectMonth ->
                            centerTitle.text = "$selectYear.$selectMonth"
                            selected.selected(selectYear, selectMonth)
                        }) /*.enableDrag(false)*/
                        .show()
            }
        }

    }
}