package com.rh.heji.data.converters;

import junit.framework.TestCase;

import java.math.BigDecimal;

/**
 * @date: 2021/6/17
 * @author: 锅得铁
 * #
 */
public class MoneyConvertersTest extends TestCase {

    public void testFromLong() {
        BigDecimal value = MoneyConverters.fromLong(1000L);
        assertEquals(value.toString(), "10.00");

        BigDecimal value2 = MoneyConverters.fromLong(1234L);
        assertEquals(value2.toString(), "12.34");

    }

    public void testToLong() {
        Long value = MoneyConverters.toLong(new BigDecimal("10.00"));
        assertEquals(value.longValue(), 1000L);
        Long value2 = MoneyConverters.toLong(new BigDecimal("12.34"));
        assertEquals(value2.longValue(), 1234L);
    }

    public void testZERO_00() {
        assertEquals("0.00",MoneyConverters.ZERO_00().toPlainString());
    }
}