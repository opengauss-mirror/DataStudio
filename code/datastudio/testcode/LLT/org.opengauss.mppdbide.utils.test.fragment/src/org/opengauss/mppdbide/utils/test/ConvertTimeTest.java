/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.utils.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;

import org.junit.Test;

import org.opengauss.mppdbide.utils.ConvertTimeStampValues;
import org.opengauss.mppdbide.utils.ConvertTimeValues;
import org.opengauss.mppdbide.utils.DateTimeFormatValidator;

public class ConvertTimeTest
{
    @Test
    public void test_convert_timestamp_values()
    {
        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        ConvertTimeStampValues  obj = new ConvertTimeStampValues(timestamp.getTime(), "yyyy-MM-dd HH:mm:ss");
        
        String str = timestamp.toString();
        
        assertEquals(obj.toString(), str.split("\\.")[0]);
        int hash = obj.hashCode();
        assertEquals(hash, obj.hashCode());
        
    }
    
    @Test
    public void test_convert_time_values()
    {
        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        
        ConvertTimeValues  obj = new ConvertTimeValues(timestamp.getTime(), "HH:mm:ss");
        obj.hashCode();
        String str = timestamp.toString();
        assertEquals(obj.toString(), str.split(" ")[1].split("\\.")[0]);
    }
    
    @Test
    public void test_dateFormatValidator()
    {
        DateTimeFormatValidator dtimeVal = new DateTimeFormatValidator();
        assertTrue(DateTimeFormatValidator.validateDateFormat("yyyy/MM/dd"));
    }
    
    @Test
    public void test_timeFormatValidator()
    {
        assertTrue(DateTimeFormatValidator.validateTimeFormat("HH:mm:ss"));
    }
    
    @Test
    public void test_getDatePlusTimeFormatValidator()
    {
        String timeFormat = "HH:mm:ss";
        String dateFormat = "yyyy/MM/dd";
        assertNotEquals("", DateTimeFormatValidator.getDatePlusTimeFormat(
                dateFormat,
                timeFormat)
                );
    }
}