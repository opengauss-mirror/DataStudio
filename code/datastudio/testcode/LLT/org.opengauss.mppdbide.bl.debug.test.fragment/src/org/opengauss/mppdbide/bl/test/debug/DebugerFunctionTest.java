/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.bl.test.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;
import org.opengauss.mppdbide.debuger.vo.VersionVo;

/**
 * Title: DebugerFunctionTest for use
 *
 * @since 3.0.0
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
    public void testSupportDebugVersion() {
        mockHelper.mockDebugVersion(DebugConstants.getSql(DebugOpt.DEBUG_VERSION));
        try {
            VersionVo versionVo = debugService.version().get();
            assertEquals(versionVo.serverversionstr, "server_version");
            assertEquals(versionVo.serverprocessid.longValue(), 1L);
            assertEquals(versionVo.proxyapiver.intValue(), 1);
            assertEquals(versionVo.serverversionnum.intValue(), 1);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
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
