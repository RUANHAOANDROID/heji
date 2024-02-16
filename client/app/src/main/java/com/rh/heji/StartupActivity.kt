package com.rh.heji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar
import com.rh.heji.config.Config
import com.rh.heji.config.LocalUser
import com.rh.heji.ui.MainActivity
import com.rh.heji.ui.user.login.LoginActivity

/**
 * @date 2022/11/13
 * @author hao
 * app start page
 */
class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Config.enableOfflineMode) {
            startMainActivity()
        } else if (!Config.isInitUser()) {
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
        MainActivity.start(this)
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