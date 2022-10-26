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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opengauss.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import org.opengauss.mppdbide.bl.mock.debug.MockDebugServiceHelper;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.debug.DebugState;
import org.opengauss.mppdbide.debuger.debug.DebugState.State;
import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.service.DebuggerReportService;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.service.SourceCodeService.CodeDescription;
import org.opengauss.mppdbide.debuger.service.chain.PrepareMsgChian;
import org.opengauss.mppdbide.debuger.service.chain.ServerPortMsgChain;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.view.handler.debug.DBConnectionProvider;

/**
 * Title: DbeDebugerTest for use
 *
 * @since 3.0.0
 */
public class DbeDebugerTest extends DebugerJdbcTestCaseBase {
    /**
     * debugger Report Service
     */
    public DebuggerReportService debuggerReportService = null;

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception {
        basicSetUp(new String[]{
                "(openGauss 3.0.0 build 02c14696) compiled at 2022-04-01 18:29:12 commit 0 last mr  release"});
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        basicTearDown();
    }

    @Override
    protected void startDebugService() throws SQLException, InterruptedException {
        serviceFactory = createServiceFactory();
        queryService = serviceFactory.getQueryService();
        functionVo = queryService.queryFunction(funcDescAddTest.proname);
        mockHelper = new MockDebugServiceHelper(preparedstatementHandler, functionVo);
        debuggerReportService = DebuggerReportService.getInstance();
        IConnectionProvider provider = new DBConnectionProvider(database);
        debuggerReportService.setAttr(provider, functionVo);
        debugService = serviceFactory.getDebugService(functionVo);
        debugService.init();
        debugService.noticeReceived(new SQLWarning(PrepareMsgChian.PREPARE_SUCCESS + " SERVER "));
        debugService.noticeReceived(new SQLWarning(ServerPortMsgChain.SERVER_PORT_MATCH + "0"));
        try {
            debugService.begin(Arrays.asList(null, null, null, null));
        } catch (SQLException sqlExp) {
            DebugState debugState = debugService.getServerDebugState();
            forceModifyDebugState(debugState, State.RUNNING);
            debugService.attachDebug();
        }
    }

    private void forceModifyDebugState(DebugState state, DebugState.State newState) {
        try {
            Field stackLockField = DebugState.class.getDeclaredField("stateLocked");
            stackLockField.setAccessible(true);
            stackLockField.set(state, false);
            state.setState(newState);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testDbeSourceCodeVo() {
        mockHelper.mockDbeInfoCode("select * from DBE_PLDEBUGGER.info_code(?)");
        try {
            Optional<SourceCodeVo> sourceCode = queryService.getSourceCode(functionVo.oid);
            SourceCodeService.CodeDescription.getRunLines(sourceCode.get().getSourceCode());
            SourceCodeService.CodeDescription.getRunLinesNums(sourceCode.get().getSourceCode());
        } catch (SQLException e) {
            fail("get breakpoints failed!");
        }
    }

    @Test
    public void testReportService() {
        StringBuffer base = new StringBuffer();
        String sep = System.getProperty("line.separator");
        String baseParam1 = String.format(Locale.ENGLISH, "AS  DECLARE%s BEGIN%s IF param1 > 100%s", sep, sep, sep);
        base.append(baseParam1);
        String baseParam2 = String.format(Locale.ENGLISH,
                "THEN%s param4=1;%s END IF;%s IF param4 > 100 THEN%s param4=1;%s END IF;%s",
                sep, sep, sep, sep, sep, sep);
        base.append(baseParam2);
        String baseParam3 = String.format(Locale.ENGLISH,
                "IF param2 > 100 THEN%s param4=2;%s END IF;%s END;%s ", sep, sep, sep, sep);
        base.append(baseParam3);
        CodeDescription baseCode = new CodeDescription(base.toString());
        debuggerReportService.setBaseCode(baseCode);
        StringBuffer total = new StringBuffer();
        total.append("CREATE OR REPLACE PROCEDURE gaussdb.insert_777");
        String toParam1 = String.format(Locale.ENGLISH,
                "(param1 INT = 0, param2 CHAR(20), param3 CHAR(20), param4 INT = 0)%s AS  ", sep);
        total.append(toParam1);
        String toParam2 = String.format(Locale.ENGLISH,
                "DECLARE%s BEGIN%s IF param1 > 100 THEN%s param4=1;%s END IF;%s", sep, sep, sep, sep, sep);
        total.append(toParam2);
        String toParam3 = String.format(Locale.ENGLISH,
                "IF param4 > 100 THEN%s param4=1;%s END IF;%s IF param2 > 100 THEN%s", sep, sep, sep, sep);
        total.append(toParam3);
        String toParam4 = String.format(Locale.ENGLISH,
                "param4=2;%s END IF;%s END;%s", sep, sep, sep);
        total.append(toParam4);
        CodeDescription totalCode = new CodeDescription(total.toString());
        debuggerReportService.setTotalCode(totalCode);
        debuggerReportService.makeReport();
    }

    @Test
    public void testDbeBreakPoints() {
        mockHelper.mockDbeBreakPoint("select * from DBE_PLDEBUGGER.info_breakpoints()");
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
    public void testDbeVariable() {
        mockHelper.mockDbeVariable("select * from DBE_PLDEBUGGER.print_var(?)");
        try {
            List<VariableVo> variables = debugService.getVariables();
            assertEquals(1, variables.size());
            VariableVo var = variables.get(0);
            assertEquals(var.name, "param1");
            assertTrue(var.value instanceof Object);
            if (var.value instanceof String) {
                assertEquals((Long.parseLong((String) var.value)), 210101);
            }
        } catch (SQLException e) {
            fail("get variable failed!");
        }
    }

    @Test
    public void testDbeStack() {
        mockHelper.mockDbeStack("SELECT * FROM DBE_PLDEBUGGER.backtrace()");
        try {
            List<StackVo> stacks = debugService.getStacks();
            assertEquals(1, stacks.size());
            StackVo stackVo = stacks.get(0);
            assertEquals(stackVo.targetname, funcDescAddTest.proname);
            assertEquals(stackVo.func.longValue(), functionVo.oid.longValue());
        } catch (SQLException e) {
            fail("get breakpoints failed!");
        }
    }

    @Test
    public void testDbeStepDebug() {
        debugService.updateServerPort(1);
        DebugOpt[] toTestOpt = new DebugOpt[]{DebugOpt.DBE_STEP_OVER, DebugOpt.DBE_CONTINUE_EXEC};
        List<String> sqls = Arrays
                .asList("select * from DBE_PLDEBUGGER.next()", "select * from DBE_PLDEBUGGER.continue()");
        for (String sql : sqls) {
            mockHelper.mockDbePositionOneLine(sql);
        }
        for (DebugOpt opt : toTestOpt) {
            try {
                PositionVo positionVo = debugService.getPositionVo(opt).get();
            } catch (SQLException e) {
                fail("get step pos failed!");
            } catch (DebugExitException e) {
                assertTrue(true);
            }
        }
    }

    @Test
    public void testDbeSupportDebugVersion() {
        mockHelper.mockDbeDebugVersion("select * from version()");
        try {
            VersionVo versionVo = debugService.version().get();
            StringBuffer sb = new StringBuffer();
            sb.append("(openGauss 3.0.0 build 02c14696) ");
            sb.append("compiled at 2022-04-01 18:29:12 commit 0 last mr  release");
            assertEquals(versionVo.version, sb.toString());
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        }
    }

    @Test
    public void testDbeStepOutDebug() {
        try {
            debugService.stepOut().get();
            fail("can\'t run here!");
        } catch (SQLException sqlExp) {
            assertTrue(true);
        } catch (DebugExitException e) {
            fail("can\'t run here!");
        }
    }
}
