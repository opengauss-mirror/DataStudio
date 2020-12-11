/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.debuger.service;

import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.EventHander;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugExitException;
import com.huawei.mppdbide.debuger.service.chain.MsgChainHelper;
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

/**
 * Title: the DebugService class
 * <p>
 * Description: this is module use to debug openGauss database's pl/sql function. 
 * you can use by this step:
 * 1. create and DebugService by new DebugService()
 * 2. set DebugService.functionVo which come from QueryService.queryFunction
     * by functionName
 * 3. set DebugService.clientConn and DebugService.serverConn,
     * this is IConnection instance, your can create ConnectionAdapter objects
 * 4. set DebugService.clinetConn and DebugService.serverConn's NoticeListener,
     * which is DebugService instance self
 * 5. call DebugService.prepareDebug
 * 6. call DebugService.startDebug and receive return value for close back server
     * thread in end
 * 7. call DebugService.attachDebug
 * 8. then you can use stepOver/StepInto/continueExec to control debug process
 * 9. you can use getVariables and getStacks get variables and stacks info
 * 10. you can use getBreakPoints/setBreakpoint/dropBreakpoint function to manager
     * breakpoints
 * 11. when debug over, call DebugService.abortDebug, if forget this operation,
     * threadleak will occur
 * 12. call DebugService.debugOff, if forget this operation, openGauss Database
     * will exit!!
 * 13. call DebugService.closeConn
 * sample use you can see DebugTest.java
 * <p>
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio 1.0.0, 2020/11/18]
 * @since 2020/11/18
 */
public class DebugService implements NoticeListener, EventHander, IDebugService {
    private static final int DEFAULT_WAIT_LOCK_TIME = 2000; // ms
    private IConnection serverConn;
    private IConnection clientConn;
    private FunctionVo functionVo;
    private MsgChainHelper msgChainHelper;
    private SessionVo sessionVo = new SessionVo();
    private final Object waitLock = new int[0];
    private DebugState serverState = new DebugState();
    private DebugState clientState = new DebugState();
    private EventQueueThread eventQueueThread = new EventQueueThread();
    private DebugServerThreadProxy serverThreadProxy = new DebugServerThreadProxy();

    public DebugService() {
        eventQueueThread.addHandler(this);
        eventQueueThread.start();
        msgChainHelper = new MsgChainHelper(this);
    }

    /**
     * prepare to debug
     *
     * @return void
     * @throws SQLException the exp
     */
    public void prepareDebug() throws SQLException {
        List<Object> inputsParams = Arrays.asList(functionVo.oid);
        serverConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.START_SESSION,
                inputsParams).execute();
    }

    /**
     * start to debug
     *
     * @param args input args to function
     * @return DebugServerThreadProxy debug manager thread
     */
    public DebugServerThreadProxy startDebug(List<?> args) {
        DebugServerRunable debugServerRunable = new DebugServerRunable(
                this,
                args,
                eventQueueThread);
        serverThreadProxy.setDebugServerRunable(debugServerRunable);
        serverThreadProxy.start();
        return serverThreadProxy;
    }

    /**
     * get debug manager thread
     *
     * @return DebugServerThreadProxy debug manager thread
     */
    public DebugServerThreadProxy getServerThreadProxy() {
        return serverThreadProxy;
    }

    /**
     * when server backthread started, this will callback
     *
     * @param args input args to function
     * @return ResultSet the function result
     * @throws SQLException the exp
     */
    public ResultSet serverDebugCallBack(List<?> args) throws SQLException {
        String sql = DebugConstants.getSql(functionVo.proname, args.size());
        PreparedStatement ps = serverConn.getStatement(sql);
        for (int i = 1 ; i < args.size() + 1; i ++) {
            ps.setObject(i, args.get(i - 1));
        }
        return ps.executeQuery();
    }

    /**
     * client attach debug
     *
     * @return void
     * @throws SQLException the exp
     */
    public void attachDebug() throws SQLException {
        waitServerStart();
        List<Object> inputParams = Arrays.asList(sessionVo.serverPort);
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.ATTACH_SESSION,
                inputParams
                ).executeQuery()) {
            if (rs.next()) {
                clientState.attached();
                sessionVo.clientPort = rs.getInt(1);
                return;
            }
        }
        throw new SQLException("client attach failed, please check");
    }
    
    private void waitServerStart() throws SQLException {
        synchronized (waitLock) {
            try {
                waitLock.wait(DEFAULT_WAIT_LOCK_TIME);
            } catch (InterruptedException intExp) {
                MPPDBIDELoggerUtility.debug("wait has error!!!! err=" + intExp.toString());
            }
        }

        if (!serverState.isRunning()) {
            throw new SQLException("server not running, please check!");
        }
    }

    /**
     * server set debug session off
     *
     * @return void
     * @throws SQLException the exp
     */
    public void debugOff() throws SQLException {
        serverConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DEBUG_OFF,
                new ArrayList<Object>(1)).execute();
    }

    /**
     * client abort debug
     *
     * @return Optional<Boolean> true if success
     * @throws SQLException the exp
     */
    public Optional<Boolean> abortDebug() throws SQLException {
        if (clientState.isStopped()) {
            return Optional.empty();
        }
        List<Object> inputParams = Arrays.asList(sessionVo.clientPort);
        PreparedStatement ps = clientConn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.ABORT_TARGET,
                inputParams
                );
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

    /**
     * step over run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> stepOver() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.STEP_OVER);
    }

    /**
     * step into run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> stepInto() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.STEP_INTO);
    }

    /**
     * step out run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> stepOut() throws SQLException, DebugExitException {
        throw new SQLException("not support method");
    }

    /**
     * continue exec run
     *
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> continueExec() throws SQLException, DebugExitException {
        return getPositionVo(DebugConstants.DebugOpt.CONTINUE_EXEC);
    }

    /**
     * step run common command
     *
     * @param debugOpt which opteration to exec
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> getPositionVo(
            DebugConstants.DebugOpt debugOpt
            ) throws SQLException, DebugExitException {
        clientState.running();
        List<Object> inputParams = Arrays.asList(sessionVo.clientPort);
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                debugOpt,
                inputParams
                ).executeQuery()) {
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

    /**
     * get cur variables
     *
     * @return List<VariableVo>  all variables
     * @throws SQLException the exp
     */
    @Override
    public List<VariableVo> getVariables() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_VARIABLES, VariableVo.class);
    }

    /**
     * get cur stacks
     *
     * @return List<VariableVo>  all stacks
     * @throws SQLException the exp
     */
    @Override
    public List<StackVo> getStacks() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_STACKS, StackVo.class);
    }

    /**
     * get cur breakpoints 
     *
     * @return List<VariableVo>  all breakpoints
     * @throws SQLException the exp
     */
    @Override
    public List<PositionVo> getBreakPoints() throws SQLException {
        return getListVos(DebugConstants.DebugOpt.GET_BREAKPOINTS, PositionVo.class);
    }

    private <T> List<T> getListVos(DebugConstants.DebugOpt debugOpt, Class<T> clazz) throws SQLException {
        List<Object> inputParams = Arrays.asList(sessionVo.clientPort);
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                debugOpt,
                inputParams
                ).executeQuery()
                ) {
            List<T> results = ParseVo.parseList(rs, clazz);
            return results;
        }
    }

    /**
     * set breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @return boolean true if success
     * @throws SQLException the exp
     */
    @Override
    public boolean setBreakPoint(PositionVo positionVo) throws SQLException {
        return disposeBreakpoint(DebugConstants.DebugOpt.SET_BREAKPOINT, positionVo);
    }

    /**
     * delete breakpoint
     *
     * @param positionVo which line will set breakpoint
     * @return boolean true if success
     * @throws SQLException the exp
     */
    @Override
    public boolean dropBreakPoint(PositionVo positionVo) throws SQLException {
        return disposeBreakpoint(DebugConstants.DebugOpt.DROP_BREAKPOINT, positionVo);
    }

    /**
     * set/delete breakpoint
     *
     * @param debugOpt which opteration to exec
     * @param positionVo which line will set breakpoint
     * @return boolean true if success
     * @throws SQLException the exp
     */
    public boolean disposeBreakpoint(DebugConstants.DebugOpt debugOpt,
            PositionVo positionVo) throws SQLException {
        if (positionVo.func == null || positionVo.func.intValue() == 0) {
            positionVo.func = new Long(functionVo.oid);
        }
        
        List<Object> inputParams = Arrays.asList(
                sessionVo.clientPort,
                positionVo.func,
                positionVo.linenumber
                );
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(
                debugOpt,
                inputParams).executeQuery()
                ) {
            boolean result = false;
            if (rs.next()) {
                result = rs.getBoolean(1);
            }
            return result;
        }
    }

    /**
     * set server connection
     *
     * @param IConnection connection
     * @return void
     */
    public void setServerConn(IConnection serverConn) {
        this.serverConn = serverConn;
        this.serverConn.setNoticeListener(this);
    }

    /**
     * set client connection
     *
     * @param IConnection connection
     * @return void
     */
    public void setClientConn(IConnection clientConn) {
        this.clientConn = clientConn;
        this.clientConn.setNoticeListener(this);
    }

    /**
     * close all connection
     *
     * @return void
     */
    @Override
    public void closeService() {
        try {
            if (clientConn != null) {
                clientConn.close();
                clientConn = null;
            }
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("clientConn close failed!err=" + sqlErr.toString());
        }

        try {
            if (serverConn != null) {
                serverConn.close();
                serverConn = null;
            }
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("serverConn close failed, err=" + sqlErr.toString());
        }
        if (eventQueueThread.isAlive()) {
            eventQueueThread.stopThread();
        }
    }

    /**
     * dispose sql warning of notice 
     *
     * @param notice the notice
     * @return void
     */
    @Override
    public void noticeReceived(SQLWarning notice) {
        if (null == notice || null == notice.getMessage()) {
            return;
        }
        String msgString = notice.getMessage();
        MPPDBIDELoggerUtility.debug("sql message:" + msgString);
        msgChainHelper.handleSqlMsg(new Event(EventMessage.ON_SQL_MSG, msgString));
    }

    /**
     * handle event
     *
     * @param Event event to handle
     * @return void
     */
    @Override
    public void handleEvent(Event event) {
        msgChainHelper.handleEventMsg(event);
    }

    /**
     * update server port
     *
     * @param serverPort the port to set
     * @return void
     */
    public void updateServerPort(int serverPort) {
        sessionVo.serverPort = serverPort;
        serverState.running();
        synchronized (waitLock) {
            waitLock.notifyAll();
        } 
    }
    
    /**
     * update server state with exception
     *
     * @return void
     */
    public void updateServerWithException() {
        serverState.terminaled();
    }
    
    /**
     * update server state with normal exit
     *
     * @param result the execute result
     * @return void
     */
    public void updateServerWithResult(Object result) {
        serverState.stop();
        serverState.stateLocked();
        sessionVo.result = result;
        serverThreadProxy.start();
    }

    /**
     * begin debug
     *
     * @param args function input args
     * @return void
     * @throws SQLException sql exception
     */
    @Override
    public void begin(List<?> args) throws SQLException {
        prepareDebug();
        startDebug(args);
        attachDebug();
    }

    /**
     * end debug
     *
     * @return void
     */
    @Override
    public void end() {
        try {
            abortDebug();
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.debug("abortDebug with error:" + e.toString());
        }
        serverThreadProxy.join();
        try {
            debugOff();
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.debug("debugOff with error:" + e.toString());
        }
        closeService();
    }

    /**
     * get normal end result
     *
     * @return Optional<Object> the result
     */
    @Override
    public Optional<Object> getResult() {
        if (isNormalEnd()) {
            return Optional.ofNullable(sessionVo.result);
        }
        return Optional.empty();
    }

    /**
     * is normal end
     *
     * @return true if normal end
     */
    @Override
    public boolean isNormalEnd() {
        return serverState.isNormalStopped();
    }
    
    /**
     * is debug server still running
     *
     * @return true if running
     */
    @Override
    public boolean isRunning() {
        return getServerDebugState().isRunning();
    }
    
    /**
     * set function vo
     *
     * @param functionVo function vo
     * @return void
     */
    public void setFunctionVo(FunctionVo functionVo) {
        this.functionVo = functionVo;
    }
    
    /**
     * get server state
     *
     * @return DebugState state of server
     */
    public DebugState getServerDebugState() {
        return serverState;
    }
    
    /**
     * get server state
     *
     * @return DebugState state of client
     */
    public DebugState getClientDebugState() {
        return clientState;
    }

    /**
     * add server debug exit listener
     *
     * @param handler event handler of exit
     * @return void
     */
    @Override
    public void addServerExistListener(EventHander handler) {
        eventQueueThread.addHandler(handler);
    }
}
