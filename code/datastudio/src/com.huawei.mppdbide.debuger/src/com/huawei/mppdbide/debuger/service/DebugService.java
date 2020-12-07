/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */
package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.annotation.ParseVo;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugState;
import com.huawei.mppdbide.debuger.thread.DebugServerRunable;
import com.huawei.mppdbide.debuger.thread.DebugServerThreadProxy;
import com.huawei.mppdbide.debuger.thread.EventQueueThread;
import com.huawei.mppdbide.common.IConnection;
import com.huawei.mppdbide.debuger.vo.*;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.postgresql.core.NoticeListener;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Title: the DebugService class
 * <p>
 * Description: this is module use to debug openGauss database's pl/sql function. you can use by this step:
 * 1. create and DebugService by new DebugService()
 * 2. set DebugService.functionVo which come from QueryService.queryFunction by functionName
 * 3. set DebugService.clientConn and DebugService.serverConn, this is IConnection instance, your can create ConnectionAdapter objects
 * 4. set DebugService.clinetConn and DebugService.serverConn's NoticeListener, which is DebugService instance self
 * 5. call DebugService.prepareDebug
 * 6. call DebugService.startDebug and receive return value for close back server thread in end
 * 7. call DebugService.attachDebug
 * 8. then you can use stepOver/StepInto/continueExec to control debug process
 * 9. you can use getVariables and getStacks get variables and stacks info
 * 10. you can use getBreakPoints/setBreakpoint/dropBreakpoint function to manager breakpoints
 * 11. when debug over, call DebugService.abortDebug, if forget this operation, threadleak will occur
 * 12. call DebugService.debugOff, if forget this operation, openGauss Database will exit!!
 * 13. call DebugService.closeConn
 * sample use you can see DebugTest.java
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/18]
 * @since 2020/11/18
 */
public class DebugService implements NoticeListener, EventHander, IService {
    private static final int DEFAULT_WAIT_LOCK_TIME = 2000; //ms
    private IConnection serverConn;
    private IConnection clientConn;
    public FunctionVo functionVo;
    public SessionVo sessionVo = new SessionVo();
    public DebugState serverState = new DebugState();
    private DebugState clientState = new DebugState();
    private EventQueueThread eventQueueThread = new EventQueueThread();
    private DebugServerThreadProxy serverThreadProxy = new DebugServerThreadProxy();

    public DebugService() {
        eventQueueThread.setEventHandler(this);
        eventQueueThread.start();
    }

    public void prepareDebug() throws SQLException {
         serverConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.START_SESSION,
                new Object[]{functionVo.oid}).execute();
    }

    public DebugServerThreadProxy startDebug(Object[] args) {
        DebugServerRunable debugServerRunable =new DebugServerRunable(
               this,
                args,
                eventQueueThread);
        serverThreadProxy.setDebugServerRunable(debugServerRunable);
        serverThreadProxy.start();
        return serverThreadProxy;
    }

    public DebugServerThreadProxy getServerThreadProxy() {
        return serverThreadProxy;
    }

    public ResultSet serverDebugCallBack(Object[] args) throws SQLException {
        String sql = DebugConstants.getSql(functionVo.proname, args.length);
        PreparedStatement ps = serverConn.getStatement(sql);
        for (int i = 1 ; i < args.length + 1; i ++) {
            ps.setObject(i, args[i - 1]);
        }
        return ps.executeQuery();
    }

    public void attachDebug() throws SQLException {
        synchronized (sessionVo.waitLock) {
            try {
                sessionVo.waitLock.wait(DEFAULT_WAIT_LOCK_TIME);
            } catch (InterruptedException intExp) {
                MPPDBIDELoggerUtility.debug("wait has error!!!! err=" + intExp.toString());
            }
        }

        if (!serverState.isRunning()) {
            throw new SQLException("server not running, please check!");
        }
        clientState.attached();
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.ATTACH_SESSION,
                new Object[] {sessionVo.serverPort}
                ).executeQuery() ) {
            if (rs.next()) {
                sessionVo.clientPort = rs.getInt(1);
                return;
            }
        }
        throw new SQLException("client attach failed, please check");
    }

    public void debugOff() throws SQLException {
        serverConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DEBUG_OFF,
                new Object[] {}).execute();
//        clientConn.getDebugOptPrepareStatement(
//                DebugConstants.DebugOpt.DEBUG_OFF,
//                new Object[] {}).execute();
    }

    public Optional<Boolean> abortDebug() throws SQLException {
        if (clientState.isStopped()) {
            return Optional.empty();
        }
        PreparedStatement ps = clientConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.ABORT_TARGET,
                new Object[] {sessionVo.clientPort});
        clientState.stop();
        clientState.stateLocked();
        try (ResultSet rs = ps.executeQuery()) {
            Boolean result = false;
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            return Optional.of(result);
        }
    }

    public Optional<PositionVo> stepOver() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.STEP_OVER);
    }

    public Optional<PositionVo> stepInto() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.STEP_INTO);
    }

    public Optional<PositionVo> continueExec() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.CONTINUE_EXEC);
    }

    private Optional<PositionVo> getPositionVo(DebugConstants.DebugOpt debugOpt) throws SQLException, DebugExitException {
        clientState.running();

        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                debugOpt,
                new Object[] {sessionVo.clientPort}).executeQuery()) {
            // this order is very important, becase when debug opt is return,
            // maybe server is already over, so must stop debug!
            if (!serverState.isRunning()) {
                throw new DebugExitException();
            }
            PositionVo positionVo = null;
            if (rs.next()) {
                positionVo = ParseVo.parse(rs, PositionVo.class);
            }
            return Optional.ofNullable(positionVo);
        }
    }

    public List<VariableVo> getVariables() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_VARIABLES, VariableVo.class);
    }

    public List<StackVo> getStacks() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_STACKS, StackVo.class);
    }

    public List<PositionVo> getBreakPoints() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_BREAKPOINTS, PositionVo.class);
    }

    private <T> List<T> getListVos(DebugConstants.DebugOpt debugOpt, Class<T> clazz) throws SQLException {
        ResultSet rs = clientConn.getDebugOptPrepareStatement(debugOpt,
                new Object[] {sessionVo.clientPort}).executeQuery();
        List<T> results = ParseVo.parseList(rs, clazz);
        rs.close();
        return results;
    }

    public boolean setBreakPoint(PositionVo positionVo) throws SQLException {
        return disposeBreakpoint(DebugConstants.DebugOpt.SET_BREAKPOINT, positionVo);
    }

    public boolean dropBreakPoint(PositionVo positionVo) throws SQLException {
        return disposeBreakpoint(DebugConstants.DebugOpt.DROP_BREAKPOINT, positionVo);
    }

    public boolean disposeBreakpoint(DebugConstants.DebugOpt debugOpt, PositionVo positionVo) throws SQLException {
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                debugOpt,
                new Object[] {sessionVo.clientPort, positionVo.func, positionVo.linenumber}).executeQuery()) {
            boolean result = false;
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            return result;
        }
    }

    public void setServerConn(IConnection serverConn) {
        this.serverConn = serverConn;
        this.serverConn.setNoticeListener(this);
    }

    public void setClientConn(IConnection clientConn) {
        this.clientConn = clientConn;
        this.clientConn.setNoticeListener(this);
    }

    @Override
    public void closeService() {
        try {
            clientConn.close();
            clientConn = null;
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("clientConn close failed!err=" + sqlErr.toString());
        }

        try {
            serverConn.close();
            serverConn = null;
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("serverConn close failed, err=" + sqlErr.toString());
        }
        eventQueueThread.stopThread();
    }

    @Override
    public void noticeReceived(SQLWarning notice) {
        if (null == notice || null == notice.getMessage()) {
            return;
        }
        String msgString = notice.getMessage();
        MPPDBIDELoggerUtility.debug("sql message:" + msgString);
        if (msgString.contains("Pldebugger is started successfully, you are SERVER now")) {
            serverState.prepared();
        } else if (msgString.contains("YOUR PROXY PORT ID IS:")) {
            Matcher matcher = Pattern.compile("YOUR PROXY PORT ID IS:(\\d+)").matcher(msgString);
            if (matcher.find()) {
                sessionVo.serverPort = Integer.parseInt(matcher.group(1).trim());
                serverState.running();
                synchronized (sessionVo.waitLock) {
                    sessionVo.waitLock.notify();
                }
            }
        } else {
            MPPDBIDELoggerUtility.debug("not processed msg");
        }
    }

    @Override
    public void handleEvent(Event event) {
        if (event.getMsg().equals(Event.EventMessage.ON_EXIT)) {
            if (event.hasException()) {
                serverState.ternimaled();
                MPPDBIDELoggerUtility.error("thread exit with error:" + event.getException().toString());
            } else {
                serverState.stop();
                serverState.stateLocked();
                Object addition = event.getAddition().orElse("");
                MPPDBIDELoggerUtility.debug("thread exit and ret value = " + addition);
                sessionVo.result = addition;
                serverThreadProxy.start();
            }
        }
    }
}
