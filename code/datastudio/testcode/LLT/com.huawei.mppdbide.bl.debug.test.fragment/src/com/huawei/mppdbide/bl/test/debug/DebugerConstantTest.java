/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.bl.test.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.SessionVo;
import com.huawei.mppdbide.debuger.vo.SourceCodeVo;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.debuger.vo.TotalSourceCodeVo;
import com.huawei.mppdbide.debuger.vo.VariableVo;

/**
 * Title: DebugerDebugOptTest for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-11]
 * @since 2020-12-11
 */
public class DebugerConstantTest {
    @Test
    public void testNoParam() {
        String sql1 = DebugConstants.getSql(DebugOpt.DEBUG_ON);
        String compareResults = "select * from pldbg_on()";
        assertEquals(compareResults, sql1);
    }

    @Test
    public void testOneParam() {
        String sql1 = DebugConstants.getSql(DebugConstants.DebugOpt.START_SESSION);
        assertEquals(sql1, "select * from plpgsql_oid_debug(?)");
    }

    @Test
    public void testThreeParam() {
        String sql1 = DebugConstants.getSql(DebugConstants.DebugOpt.SET_BREAKPOINT);
        assertEquals(sql1, "select * from pldbg_set_breakpoint(?,?,?)");
    }

    @Test
    public void testAnotherInput() {
        String sql1 = DebugConstants.getSql("test_function", 3);
        String result = "select * from test_function(?,?,?)";
        assertEquals(sql1, result);
    }
    
    @Test
    public void testVoCreate() {
        FunctionVo funVo = new FunctionVo();
        assertNotNull(funVo);

        PositionVo positionVo = new PositionVo();
        assertNotNull(positionVo);

        PositionVo positionVo2 = new PositionVo(new Long(0), 1, null);
        assertNotNull(positionVo2);
        assertEquals(positionVo2.linenumber.intValue(), 1);
        assertNotEquals(PositionVo.title(), "");
        assertNotEquals(positionVo2.formatSelf(), "");

        SessionVo sessionVo = new SessionVo();
        assertNotNull(sessionVo);

        SourceCodeVo codeVo = new SourceCodeVo();
        assertNotNull(codeVo);
        codeVo.pldbg_get_source = "test";
        assertEquals(codeVo.getSourceCode(), "test");

        StackVo stackVo = new StackVo();
        assertNotNull(stackVo);
        stackVo.level = new Integer(1);
        stackVo.targetname = "name";
        assertNotEquals(stackVo.toString(), "");

        TotalSourceCodeVo totalSourceCodeVo = new TotalSourceCodeVo();
        assertNotNull(totalSourceCodeVo);
        totalSourceCodeVo.definition = "test11";
        totalSourceCodeVo.headerlines = new Integer(1);
        assertEquals(totalSourceCodeVo.getHeadlines(), 1);
        assertEquals(totalSourceCodeVo.getSourceCode(), "test11/");

        VariableVo variableVo = new VariableVo();
        assertNotNull(variableVo);
        variableVo.name = "var1";
        variableVo.linenumber = new Integer(1);
        variableVo.value = "value";
        variableVo.dtype = new Long(3);
        variableVo.isnotnull = new Boolean(true);
        assertNotEquals(VariableVo.title(), "");
        assertNotEquals(variableVo.formatSelf(), "");
    }
    
    @Test
    public void testDebugException() {
        DebugExitException exitExp = new DebugExitException();
        assertEquals(DebugExitException.DEBUG_EXIT, exitExp.getMessage());
        
        DebugPositionNotFoundException notFoundPosExp = new DebugPositionNotFoundException();
        assertEquals("debug_position_not_found", notFoundPosExp.getMessage());
    }
}
