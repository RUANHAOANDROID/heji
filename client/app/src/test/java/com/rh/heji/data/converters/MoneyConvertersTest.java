package com.rh.heji.data.converters;

import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * Date: 2021/6/17
 * Author: 锅得铁
 * #
 */
public class MoneyConvertersTest extends TestCase {

    public void testFromLong() {
        BigDecimal value = MoneyConverters.fromLong(1000l);
        assertEquals(value.toString(), "10.00");

        BigDecimal value2 = MoneyConverters.fromLong(1234l);
        assertEquals(value2.toString(), "12.34");

    }

    public void testToLong() {
        Long value = MoneyConverters.toLong(new BigDecimal("10.00"));
        assertEquals(value.longValue(), 1000l);
        Long value2 = MoneyConverters.toLong(new BigDecimal("12.34"));
        assertEquals(value2.longValue(), 1234l);
    }

    public void testZERO_00() {
    }
}