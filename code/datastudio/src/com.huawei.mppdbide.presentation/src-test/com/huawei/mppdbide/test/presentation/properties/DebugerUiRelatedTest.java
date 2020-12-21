package com.huawei.mppdbide.test.presentation.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;

import org.junit.Test;

import com.huawei.mppdbide.debuger.vo.BreakpointList;
import com.huawei.mppdbide.debuger.vo.BreakpointVo;

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