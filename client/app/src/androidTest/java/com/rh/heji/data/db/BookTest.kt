package com.rh.heji.data.db

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rh.heji.data.AppDatabase
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BookTest : TestCase() {

    @Test
    fun myTest() {
        // Context of the app under test.
        var appContext = InstrumentationRegistry.getInstrumentation().targetContext
        var book = Book(name = "test1")

        var createUser = BookUser(bookId = book.id, name = "RH", authority = "CREATE")
        var bookUser = BookUser(bookId = book.id, name = "ZY", authority = "USER")

        AppDatabase.getInstance(appContext).bookDao().insert(book)
        AppDatabase.getInstance(appContext).bookUserDao().insert(createUser, bookUser)
        book.name = "ABC"
        AppDatabase.getInstance(appContext).bookDao().update(book)

        //var bookWhitUsers = BookWhitUsers(book = book, users = users)
    }
}