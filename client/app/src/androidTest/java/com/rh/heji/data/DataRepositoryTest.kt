package com.rh.heji.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DataRepositoryTest : TestCase() {
    private val dataRepository = DataRepository()

    // Context of the app under test.
    var appContext = InstrumentationRegistry.getInstrumentation().targetContext
    @Test
    fun bookTest() {

    }
}