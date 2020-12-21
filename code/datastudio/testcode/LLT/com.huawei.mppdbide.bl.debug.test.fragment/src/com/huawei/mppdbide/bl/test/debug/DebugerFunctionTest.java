/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.bl.test.debug;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.debuger.vo.VariableVo;

/**
 * Title: DebugerFunctionTest for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-11]
 * @since 2020-12-11
 */
public class DebugerFunctionTest extends DebugerJdbcTestCaseBase {

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        basicSetUp();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception
    {
        basicTearDown();
    }
    
    @Test
    public void testBreakPoints() {
        mockHelper.mockBreakPoint(DebugConstants.getSql(DebugOpt.GET_BREAKPOINTS));
        try {
            List<PositionVo> breakpoints = debugService.getBreakPoints();
            assertEquals(1, breakpoints.size());
            PositionVo positionVo = breakpoints.get(0);
            assertEquals(positionVo.func.longValue(), functionVo.oid.longValue());
            assertEquals(positionVo.linenumber.intValue(), -1);
            assertEquals(positionVo.targetname, functionVo.proname);
        } catch (SQLException e) {
            fail("get breakpoints failed!");
        }
    }
    
    @Test
    public void testVariable() {
        mockHelper.mockVariable(DebugConstants.getSql(DebugOpt.GET_VARIABLES));
        try {
            List<VariableVo> variables = debugService.getVariables();
            assertEquals(1, variables.size());
            VariableVo var = variables.get(0);
            assertEquals(var.name, "v1");
            assertTrue(var.value instanceof Integer);
            assertEquals(((Integer)var.value).intValue(), 1);
        } catch (SQLException e) {
            fail("get variable failed!");
        }
    }
    
    @Test
    public void testStack() {
        mockHelper.mockStack(DebugConstants.getSql(DebugOpt.GET_STACKS));
        try {
            List<StackVo> stacks = debugService.getStacks();
            assertEquals(1, stacks.size());
            StackVo stackVo = stacks.get(0);
            assertEquals(stackVo.targetname, funcDescAddTest.proname);
            assertEquals(stackVo.func.longValue(), functionVo.oid.longValue());
            assertEquals(stackVo.level.intValue(), 1);
        } catch (SQLException e) {
            fail("get breakpoints failed!");
        }
    }
    
    @Test
    public void testStepDebug() {
        DebugOpt[] toTestOpt = new DebugOpt[] {DebugOpt.STEP_INTO, DebugOpt.STEP_OVER,
            DebugOpt.CONTINUE_EXEC};
        for (DebugOpt opt: toTestOpt) {
            mockHelper.mockPositionOneLine(DebugConstants.getSql(opt));
        }

        for (DebugOpt opt: toTestOpt) {
            try {
                PositionVo positionVo = debugService.getPositionVo(opt).get();
                assertEquals(positionVo.func.longValue(), functionVo.oid.longValue());
                assertEquals(positionVo.linenumber.intValue(), -1);
                assertEquals(positionVo.targetname, functionVo.proname);
            } catch (SQLException e) {
                fail("get step pos failed!");
            } catch (DebugExitException e) {
                assertTrue(true);
            }
        }
    }
    
    @Test
    public void testStepInto() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.STEP_INTO));
        try {
            debugService.stepInto().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testStepOver() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.STEP_OVER));
        try {
            debugService.stepOver().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testContinue() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.CONTINUE_EXEC));
        try {
            debugService.continueExec().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            assertTrue(true);
        }
    }
    
    @Test
    public void testStepOutDebug() {
        try {
            debugService.stepOut().get();
            fail("can\'t run here!");
        } catch (SQLException sqlExp) {
            assertTrue(true);
        } catch (DebugExitException e) {
            fail("can\'t run here!");
        }
    }
    
    @Test
    public void testResult() {
        assertTrue(debugService.isNormalEnd());
        assertFalse(debugService.isRunning());
        assertTrue(debugService.getResult().get() instanceof Integer);
        assertEquals(((Integer) debugService.getResult().get()).intValue(), 7);
    }
}