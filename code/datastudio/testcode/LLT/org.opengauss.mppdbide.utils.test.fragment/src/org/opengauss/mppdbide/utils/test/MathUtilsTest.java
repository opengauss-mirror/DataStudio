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

import org.junit.Test;

import org.opengauss.mppdbide.utils.MathUtils;

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
