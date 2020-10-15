package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.huawei.mppdbide.utils.MathUtils;

public class MathUtilsTest {
    @Test
    public void test_roundDoubleValues() {
        double result = MathUtils.roundDoubleValues(20.4567, 2);
        assertEquals(20.46, result, 0);
    }
    
    @Test
    public void test_roundDoubleValues_whenPlaceParmIsNegative() {
        double result = MathUtils.roundDoubleValues(20.4567, -1);
        assertEquals(20.0, result, 0);
    }
}
