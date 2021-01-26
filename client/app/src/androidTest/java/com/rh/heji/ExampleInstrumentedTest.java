package com.rh.heji;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.rh.heji.ui.bill.add.Calculation;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.rh.heji", appContext.getPackageName());

        Calculation calculation = new Calculation();
        String rs1 = calculation.input("1").input("2").input("3").saveResult();
        assertEquals("123", rs1);

        String rs2 = calculation.delete().saveResult();
        assertEquals("12", rs2);

        String rs3 = calculation.input("+").input("10").input("-").saveResult();
        assertEquals("22", rs3);

        String rs4 = calculation.input("-").input("10").input("+").saveResult();
        assertEquals("12", rs4);

        String rs5 = calculation.input("+").input("10").input(".").saveResult();
        assertEquals("22", rs5);

        String rs6 = calculation.input("+").input("0.10").input(".").saveResult();
        assertEquals("22.1",rs6);

        String rs7 = calculation.input("+").input("0.10").input(".").saveResult();
        assertEquals("22.1",rs6);

    }

}