package com.rh.heji.ui.popup

import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ClipboardUtils
import com.blankj.utilcode.util.ToastUtils
import com.lxj.xpopup.core.BottomPopupView
import com.lxj.xpopup.util.XPopupUtils
import com.rh.heji.R

/**
 * 邀请码POP
 */
class BookSharePopup(context: Context, val code: String) : BottomPopupView(context) {

    init {
        addInnerContent()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.pop_layout_share
    }

    override fun onCreate() {
        super.onCreate()
        val colorDrawable = XPopupUtils.createDrawable(
            ContextCompat.getColor(context, R.color._xpopup_light_color),
            10f,
            10f,
            0f,
            0f
        )
        popupImplView.apply {
            background = colorDrawable
            findViewById<TextView>(R.id.tvContext).text = code
            setOnClickListener {
                ClipboardUtils.copyText(code)
                ToastUtils.showLong("邀请码已经复制到粘贴板")
            }
        }

    }
}