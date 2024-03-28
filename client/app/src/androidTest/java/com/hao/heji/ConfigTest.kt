package com.hao.heji

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hao.heji.config.Config
import com.hao.heji.config.InitBook
import com.hao.heji.ui.user.JWTParse
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
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
        val book = InitBook
        runBlocking {
            Config.saveBook(book)
            assert(book == Config.book)
            val user =JWTParse.User("localUser", "user0","")
            Config.saveUser(user)
            assert(user== Config.user)
        }

    }
}