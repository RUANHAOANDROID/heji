package com.rh.heji

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.blankj.utilcode.util.FragmentUtils
import com.gyf.immersionbar.ktx.immersionBar
import com.rh.heji.data.db.Book
import com.rh.heji.ui.user.login.LoginFragment

/**
 * 用户登录注册
 *
 * @author 锅得铁
 * @date 2022/5/10
 * @since v1.0
 */
class LoginActivity : AppCompatActivity() {
    lateinit var loginFragment: LoginFragment

    companion object {
        fun start(activity: Activity) {
            val intent = Intent(activity, LoginActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FragmentContainerView>(R.id.nav_user_host_fragment)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }

        })
    }

    override fun onResume() {
        super.onResume()
        immersionBar {
            statusBarColor(R.color.colorPrimary)
            navigationBarColor(R.color.white)
            titleBar(findViewById(R.id.toolbar))
        }
    }

    fun startMainActivity() {
        initUserBaseData()
        MainActivity.startMainActivity(this)
        this.finish()
    }

    private fun initUserBaseData() {
        App.setDataBase(App.user.username)
        App.setCurrentBook(Book(name = "个人账本"))
    }
}