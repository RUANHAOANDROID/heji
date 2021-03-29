package com.rh.heji.ui.base

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.rh.heji.MainActivity
import com.rh.heji.R
import com.rh.heji.utlis.Logger

/**
 * Date: 2020/8/28
 * Author: 锅得铁
 * #
 */
abstract class BaseFragment : Fragment() {
    lateinit var rootView: View
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (!this::rootView.isInitialized) {
            val viewGroup = rootView.parent as ViewGroup
            viewGroup?.removeView(rootView)
        } else {
            rootView = inflater.inflate(layoutId(), container, false)
            initView(rootView)
        }
        setHasOptionsMenu(true)
        return rootView
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        setUpToolBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
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
    protected abstract fun initView(view: View)
    protected open fun setUpToolBar() {
        try {
            toolBar.menu.clear()
        } catch (e: Exception) {
            Logger.i(e.message)
        }
    }

    protected fun showBlack() {
        toolBar.navigationIcon = blackDrawable()
        toolBar.setNavigationOnClickListener { v: View? -> Navigation.findNavController(rootView).navigateUp() }
    }

    val toolBar: Toolbar
        get() = rootView.findViewById(R.id.toolbar)

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

    companion object {
        const val TAG = "BaseFragment"
    }
}