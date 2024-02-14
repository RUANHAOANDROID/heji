package com.rh.heji

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.rh.heji.config.Config
import com.rh.heji.ui.user.JWTParse
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith


/**
 * @date: 2022/11/19
 * @author: 锅得铁
 * #
 */
@RunWith(AndroidJUnit4::class)
class ConfigTest: TestCase(){
    @Test
    fun readWrite(){
        val book = Config.defaultBook
        Config.setBook(book)
        assert(book == Config.book)
        val user =JWTParse.User("localUser", listOf(),"")
        Config.setUser(user)
        assert(user== Config.user)
    }
}