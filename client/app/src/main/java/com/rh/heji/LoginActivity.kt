package com.rh.heji

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.rh.heji.data.db.Book
import com.rh.heji.ui.user.login.LoginFragment

/**
 * TODO
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
        findViewById<FragmentContainerView>(R.id.nav_user_host_fragment)
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