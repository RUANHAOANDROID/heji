package com.rh.heji

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rh.heji.data.db.Book

class StartupActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (App.userIsInit()) {
            startMainActivity()
        } else {
            LoginActivity.start(this)
        }
        setContentView(R.layout.activity_startup)
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