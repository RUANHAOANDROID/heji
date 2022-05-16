package com.rh.heji.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.rh.heji.data.db.Bill
import com.rh.heji.data.db.mongo.ObjectId
import junit.framework.TestCase
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal

@RunWith(AndroidJUnit4::class)
class DataRepositoryTest : TestCase() {
    private val dataRepository = DataRepository()

    // Context of the app under test.
    var appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun bookTest() {

    }
}