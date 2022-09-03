package com.rh.heji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ktx.immersionBar

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (App.userIsInit()&&App.bookIsInit()) {
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