package com.rh.heji.ui.home

import android.util.DisplayMetrics
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout.DrawerListener
import com.gyf.immersionbar.ktx.navigationBarHeight
import com.rh.heji.MainActivity

class HomeDrawerListener(private val mainActivity: MainActivity, var listener: DrawerSlideListener) : DrawerListener {

    override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
        val displayMetrics = DisplayMetrics()
        mainActivity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        listener.offset(
                drawerView.right,
                drawerView.top,
                (displayMetrics.widthPixels + displayMetrics.widthPixels * slideOffset).toInt(),
                drawerView.bottom)
    }

    override fun onDrawerOpened(drawerView: View) {}
    override fun onDrawerClosed(drawerView: View) {}
    override fun onDrawerStateChanged(newState: Int) {}
}

interface DrawerSlideListener {
    fun offset(left: Int,//drawerView右边缘
               top: Int,//顶部 0
               right: Int,//drawerView右边缘+屏幕宽度
               bottom: Int//屏幕高度
    )
}