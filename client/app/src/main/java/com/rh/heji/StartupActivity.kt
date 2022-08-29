package com.rh.heji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.rh.heji.data.db.Book

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (App.userIsInit()) {
            //TODO 当登录状态下，清除了本地数据再次进入账本会报错，应进入主页前同步账本没有则创建默认账本
            startMainActivity()
        } else {
            startLoginActivity()
        }

    }

    private fun startLoginActivity() {
        LoginActivity.start(this)
        this.finish()
    }

    private fun startMainActivity() {
        MainActivity.startMainActivity(this)
        this.finish()
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            statusBarColor(R.color.colorPrimary)
            navigationBarColor(R.color.colorPrimary)
        }
    }
}