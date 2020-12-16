/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.bl.test.debug;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import com.huawei.mppdbide.bl.mock.debug.MockDebugServiceHelper;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.debug.DebugState;
import com.huawei.mppdbide.debuger.debug.DebugState.State;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.service.WrappedDebugService;
import com.huawei.mppdbide.debuger.service.chain.PrepareMsgChian;
import com.huawei.mppdbide.debuger.service.chain.ServerPortMsgChain;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.debuger.vo.VariableVo;

/**
 * Title: DebugerWrappedServiceTest for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-16]
 * @since 2020-12-16
 */
public class DebugerWrappedServiceTest extends DebugerJdbcTestCaseBase {
    protected WrappedDebugService wrappedDebugService;
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
        DebugState debugState = debugService.getServerDebugState();
        debugState.setState(State.STOP);
        debugState.stateLocked();
        basicTearDown();
    }
    
    @Override
    protected void startDebugService() throws SQLException, InterruptedException {
//        super.startDebugService();
        serviceFactory = createServiceFactory();
        queryService = serviceFactory.getQueryService();
        functionVo = queryService.queryFunction(funcDescAddTest.proname);
        mockHelper = new MockDebugServiceHelper(preparedstatementHandler, functionVo);
        debugService = serviceFactory.getDebugService(functionVo);
        wrappedDebugService = new WrappedDebugService(debugService);
        wrappedDebugService.addHandler(new EventHander() {
            @Override
            public void handleEvent(Event event) {
            }
        });
        wrappedDebugService.init();
        debugService.noticeReceived(new SQLWarning(PrepareMsgChian.PREPARE_SUCCESS + " SERVER "));
        debugService.noticeReceived(new SQLWarning(ServerPortMsgChain.SERVER_PORT_MATCH + "0"));
        try {
            wrappedDebugService.begin(funcDescAddTest.params);
        } catch (SQLException sqlExp) {
            // because server quick return, so attach will get error
            // we here force update server state to running, and call attach
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
        } catch (NoSuchFieldException | SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Test
    public void testStacks() {
        mockHelper.mockStack(DebugConstants.getSql(DebugOpt.GET_STACKS));
        try {
            List<StackVo> stacks = wrappedDebugService.getStacks();
            assertEquals(1, stacks.size());
            StackVo stackVo = stacks.get(0);
            assertEquals(stackVo.targetname, funcDescAddTest.proname);
            assertEquals(stackVo.func.longValue(), functionVo.oid.longValue());
            assertEquals(stackVo.level.intValue(), 1);
        } catch (SQLException sqlException) {
            fail("get stack failed!");
        }
    }

    @Test
    public void testBreakPoints() {
        mockHelper.mockBreakPoint(DebugConstants.getSql(DebugOpt.GET_BREAKPOINTS));
        try {
            List<PositionVo> breakpoints = wrappedDebugService.getBreakPoints();
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
            List<VariableVo> variables = wrappedDebugService.getVariables();
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
    public void testStepDebug() {
        DebugOpt[] toTestOpt = new DebugOpt[] {DebugOpt.STEP_INTO, DebugOpt.STEP_OVER,
            DebugOpt.CONTINUE_EXEC};
        for (DebugOpt opt: toTestOpt) {
            mockHelper.mockPositionOneLine(DebugConstants.getSql(opt));
        }

        for (DebugOpt opt: toTestOpt) {
            try {
                PositionVo positionVo = wrappedDebugService.getPositionVo(opt).get();
                assertEquals(positionVo.func.longValue(), functionVo.oid.longValue());
                assertEquals(positionVo.linenumber.intValue(), -1);
                assertEquals(positionVo.targetname, functionVo.proname);
            } catch (SQLException e) {
                fail("get step pos failed!");
            } catch (DebugExitException e) {
                fail("can\'t run here, because force reset in running mode");
            }
        }
    }
    
    @Test
    public void testStepInto() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.STEP_INTO));
        try {
            wrappedDebugService.stepInto().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            fail("can\'t run here, because force reset in running mode");
        }
    }
    
    @Test
    public void testStepOver() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.STEP_OVER));
        try {
            wrappedDebugService.stepOver().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            fail("can\'t run here, because force reset in running mode");
        }
    }
    
    @Test
    public void testContinue() {
        mockHelper.mockPositionOneLine(DebugConstants.getSql(DebugOpt.CONTINUE_EXEC));
        try {
            wrappedDebugService.continueExec().get();
            assertTrue(true);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        } catch (DebugExitException e) {
            fail("can\'t run here, because force reset in running mode");
        }
    }
    
    @Test
    public void testStepOutDebug() {
        try {
            wrappedDebugService.stepOut().get();
            fail("can\'t run here!");
        } catch (SQLException sqlExp) {
            assertTrue(true);
        } catch (DebugExitException e) {
            fail("can\'t run here!");
        }
    }
    
    @Test
    public void testResult() {
        assertFalse(wrappedDebugService.isNormalEnd());
        assertTrue(wrappedDebugService.isRunning());
        
        debugService.getServerDebugState().stop();
        debugService.getServerDebugState().stateLocked();
        assertTrue(wrappedDebugService.getResult().get() instanceof Integer);
        assertEquals(((Integer) wrappedDebugService.getResult().get()).intValue(), 7);
    }
}
