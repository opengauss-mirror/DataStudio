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

import java.sql.Timestamp;

import org.junit.Test;

import org.opengauss.mppdbide.utils.ConvertTimeValues;

public class ConvertTimeValuesTest {
    @Test
    public void test_roundDoubleValues() {
        Timestamp timestamp = new Timestamp (System.currentTimeMillis());
        ConvertTimeValues  obj = new ConvertTimeValues(timestamp.getTime(), "HH:mm:ss");
        
        String str = timestamp.toString();
        
        assertEquals(obj.toString(), str.split(" ")[1].split("\\.")[0]);
    }
}
