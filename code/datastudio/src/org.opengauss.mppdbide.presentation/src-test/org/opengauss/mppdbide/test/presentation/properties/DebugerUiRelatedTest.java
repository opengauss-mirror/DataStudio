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

package org.opengauss.mppdbide.test.presentation.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Test;

import org.opengauss.mppdbide.debuger.vo.BreakpointList;
import org.opengauss.mppdbide.debuger.vo.BreakpointVo;

public class DebugerUiRelatedTest {
    @Test
    public void testVoCreate() {
        BreakpointVo breakpointVo = new BreakpointVo(1, "begin", true);
        assertNotNull(breakpointVo);
        breakpointVo.setLineNum(2);
        breakpointVo.setStatement("end");
        breakpointVo.setEnable(false);
        assertEquals(breakpointVo.getLineNum(), 2);
        assertEquals(breakpointVo.getStatement(), "end");
        assertEquals(breakpointVo.getEnable(), false);

        BreakpointList.resetInstance();
        assertNull(BreakpointList.getInstance());
        BreakpointList.initialInstance();
        assertNotNull(BreakpointList.getInstance());
        BreakpointList.setBreakpointList(new HashMap<Integer, BreakpointVo>());
        assertNotNull(BreakpointList.getInstance());
    }
}