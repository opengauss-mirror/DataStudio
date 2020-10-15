package com.hauwei.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;

import org.junit.Test;

import com.huawei.mppdbide.utils.ConvertTimeValues;

public class ConvertTimeValuesTest {
    @Test
    public void test_roundDoubleValues() {
        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        ConvertTimeValues  obj = new ConvertTimeValues(timestamp.getTime(), "HH:mm:ss");
        
        String str = timestamp.toString();
        
        assertEquals(obj.toString(), str.split(" ")[1].split("\\.")[0]);
    }
}
