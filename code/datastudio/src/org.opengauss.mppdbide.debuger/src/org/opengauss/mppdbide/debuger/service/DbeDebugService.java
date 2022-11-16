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

package org.opengauss.mppdbide.debuger.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.QueryResVoConvertHelper;
import org.opengauss.mppdbide.common.VersionHelper;
import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.exception.DebugExitException;
import org.opengauss.mppdbide.debuger.vo.BreakPointListVo;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.StackVo;
import org.opengauss.mppdbide.debuger.vo.VariableVo;
import org.opengauss.mppdbide.debuger.vo.dbe.AttachVo;
import org.opengauss.mppdbide.debuger.vo.dbe.InfoCodeVo;
import org.opengauss.mppdbide.debuger.vo.dbe.TurnOnVo;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.vo.DebuggerStartInfoVo;

/**
 * Title: the DbeDebugService class
 *
 * @since 3.0.0
 */
public class DbeDebugService extends DebugService {
    /**
     * save parameters
     */
    public static final Map<Long, List<String>> map = new ConcurrentHashMap<>();

    /**
     * save param type
     */
    public static final Map<Long, List<String>> paramType = new ConcurrentHashMap<>();

    private TurnOnVo turnOnVo = null;
    private DebuggerReportService reportService = DebuggerReportService.getInstance();

    /**
     * prepare to debug
     *
     * @return void
     * @throws SQLException the exp
     */
    @Override
    public void prepareDebug() throws SQLException {
        List<Object> inputsParams = Arrays.asList(getFunctionVo().oid);
        try (PreparedStatement ps = getServerConn().getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DBE_DEBUG_ON, inputsParams)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                turnOnVo = ParseVo.parse(rs, TurnOnVo.class);
                SQLWarning sql = new SQLWarning("Pldebugger is started successfully, you are SERVER now.");
                noticeReceived(sql);
            }
        }
    }

    /**
     * when server backthread started, this will callback
     *
     * @param args input args to function
     * @return Optional<Object> the function result
     * @throws SQLException the exp
     */
    @Override
    public Optional<Object> serverDebugCallBack(List<?> args) throws SQLException {
        try {
            getServerCallBackBegin();
            String sql = DebugConstants.getSql(getFunctionVo().proname, args.size());
            try (PreparedStatement ps = getServerConn().getStatement(sql)) {
                for (int i = 1; i < args.size() + 1; i++) {
                    ps.setObject(i, args.get(i - 1));
                }
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    /**
                     *  run
                     */
                    public void run() {
                        SQLWarning sqlWarning = new SQLWarning("YOUR PROXY PORT ID IS:" + turnOnVo.port);
                        noticeReceived(sqlWarning);
                    }
                }, 2000);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int totalCount = rs.getMetaData().getColumnCount();
                        int initCount = 0;
                        List<String> resList = new ArrayList<String>();
                        while (initCount < totalCount) {
                            initCount++;
                            String columnName = rs.getMetaData().getColumnName(initCount);
                            Object result = rs.getObject(initCount);
                            String coverRes = String.format(Locale.ENGLISH, columnName+"%s "+result,":");
                            resList.add(coverRes);
                        }
                        return Optional.ofNullable(String.join("; ", resList));
                    }
                    return Optional.empty();
                }
            }
        } finally {
            getServerCallBackEnd();
        }
    }

    /**
     * client attach debug
     *
     * @return void
     * @throws SQLException the exp
     */
    @Override
    public void attachDebug() throws SQLException {
        try {
            Thread.sleep(getDelay());
        } catch (InterruptedException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
        getWaitServerStart();
        List<Object> inputParams = Arrays.asList(turnOnVo.nodename, getSessionVo().serverPort);
        DebugOpt opt = VersionHelper.getDebugOptByDebuggerVersion(getClientConn(),
                DebugConstants.DebugOpt.ATTACH_SESSION);
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(opt, inputParams)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    getClientState().attached();
                    AttachVo attachVo = ParseVo.parse(rs, AttachVo.class);
                    getSessionVo().clientPort = attachVo.lineno;
                    return;
                }
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }
        throw new SQLException("client attach failed, please check");
    }

    /**
     * server set debug session off
     *
     * @return void
     * @throws SQLException the exp
     */
    @Override
    public void debugOff() throws SQLException {
        List<Object> inputParams = new ArrayList<Object>(1);
        DebugOpt opt = DebugConstants.DebugOpt.DBE_DEBUG_OFF;
        inputParams = Arrays.asList(getFunctionVo().oid);
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(opt, inputParams)) {
            ps.execute();
        }
    }

    /**
     * client abort debug
     *
     * @return Optional<Boolean> true if success
     * @throws SQLException the exp
     */
    @Override
    public Optional<Boolean> abortDebug() throws SQLException {
        if (getClientState().isStopped()) {
            return Optional.empty();
        }
        List<Object> inputParams = new ArrayList<Object>();
        DebugConstants.DebugOpt opt = VersionHelper.getDebugOptByDebuggerVersion(getClientConn(),
                DebugConstants.DebugOpt.ABORT_TARGET);
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(opt, inputParams)) {
            getClientState().stop();
            getClientState().stateLocked();
            try (ResultSet rs = ps.executeQuery()) {
                Boolean hasResult = false;
                if (rs.next()) {
                    hasResult = rs.getBoolean(1);
                }
                return Optional.of(hasResult);
            } catch (SQLException e) {
                return Optional.of(true);
            }
        }
    }

    /**
     * step run common command
     *
     * @param debugOpt which opteration to exec
     * @return Optional<PositionVo> the breakpoint line position
     * @throws SQLException       the exp
     * @throws DebugExitException the debug exit exp
     */
    @Override
    public Optional<PositionVo> getPositionVo(DebugOpt debugOpt) throws SQLException, DebugExitException {
        getClientState().running();
        if (debugOpt == DebugOpt.STEP_OUT) {
            throw new SQLException("not support method!");
        }
        List<Object> inputParams = new ArrayList<Object>();
        DebugOpt opt = VersionHelper.getDebugOptByDebuggerVersion(getClientConn(), debugOpt);
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(opt, inputParams)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (!getServerState().isRunning()) {
                    throw new DebugExitException();
                }
                PositionVo positionVo = null;
                if (rs.next()) {
                    AttachVo vo = new AttachVo();
                    vo = ParseVo.parse(rs, AttachVo.class);
                    positionVo = new PositionVo(vo.funcoid, vo.lineno, vo.funcname);
                }
                return Optional.ofNullable(positionVo);
            } catch (SQLException e) {
                return Optional.empty();
            }
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
        return getAllVariable(DebugConstants.DebugOpt.DBE_GET_VARIABLES, VariableVo.class);
    }

    /**
     * get cur stacks
     *
     * @return List<VariableVo>  all stacks
     * @throws SQLException the exp
     */
    @Override
    public List<StackVo> getStacks() throws SQLException {
        return getDbeListVos(DebugConstants.DebugOpt.GET_STACKS, StackVo.class, new ArrayList<>());
    }

    /**
     * get cur breakpoints
     *
     * @return List<VariableVo>  all breakpoints
     * @throws SQLException the exp
     */
    @Override
    public List<PositionVo> getBreakPoints() throws SQLException {
        List<BreakPointListVo> list = getDbeListVos(DebugConstants.DebugOpt.DBE_GET_BREAKPOINTS,
                BreakPointListVo.class, new ArrayList<>());
        List<PositionVo> vos = new ArrayList<>();
        list.forEach(item -> {
            PositionVo vo = new PositionVo();
            vo.breakpointno = item.breakpointno;
            vo.isEnable = item.enable;
            vo.func = item.funcoid;
            vo.linenumber = item.lineno;
            vo.targetname = item.query;
            vos.add(vo);
        });
        return vos;
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
        return disposeDbeBreakpoint(DebugConstants.DebugOpt.DBE_SET_BREAKPOINT, positionVo, false);
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
        return disposeDbeBreakpoint(DebugConstants.DebugOpt.DBE_DROP_BREAKPOINT, positionVo, true);
    }

    /**
     * close all connection
     *
     * @return void
     */
    @Override
    public void closeService() {
        super.closeService();
        if (reportService != null) {
            reportService.close();
        }
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
        VariableRunLine.isContinue = null;
        VariableRunLine.isPldebugger = null;
        VariableRunLine.passLine.clear();
        VariableRunLine.isTerminate = true;
        DebuggerStartInfoVo startInfo = DebuggerStartVariable.getStartInfo(getFunctionVo().oid);
        startInfo.args = args;
        startInfo.oid = getFunctionVo().oid;
        startInfo.isMakeReport = true;
        DebuggerStartVariable.setStartInfo(getFunctionVo().oid, startInfo);
        getVersion();
        super.begin(args);
    }

    /**
     * get version
     *
     * @return the version
     */
    public boolean getVersion() {
        if (VariableRunLine.isPldebugger == null) {
            try {
                VariableRunLine.isPldebugger = VersionHelper.getDebuggerVersion(getClientConn()).isPldebugger();
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }
        return VariableRunLine.isPldebugger;
    }

    /**
     * get infoCodes
     *
     * @param conn     the dbConnection
     * @param debugOpt debugOpt
     * @param params   oid
     * @return List InfoCodeVo
     * @throws SQLException Exception
     */
    public static List<InfoCodeVo> getInfoCodes(IConnection conn, List<Object> params) throws SQLException {
        try (PreparedStatement ps = conn.getDebugOptPrepareStatement(
                DebugConstants.DebugOpt.DBE_GET_SOURCE_CODE, params)) {
            try (ResultSet rs = ps.executeQuery()) {
                return ParseVo.parseList(rs, InfoCodeVo.class);
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }
        return Collections.emptyList();
    }

    private boolean disposeDbeBreakpoint(DebugOpt debugOpt, PositionVo positionVo, Boolean isDelete)
            throws SQLException {
        if (positionVo.func == null || positionVo.func.intValue() == 0) {
            positionVo.func = getFunctionVo().oid;
        }
        List<Object> inputParams;
        if (isDelete) {
            List<PositionVo> res = getBreakPoints();
            Optional<PositionVo> vo = res.stream()
                    .filter(item -> item.linenumber.equals(positionVo.linenumber)).findFirst();
            inputParams = new ArrayList<>();
            if (vo.isPresent()) {
                inputParams = Arrays.asList(vo.get().getBreakpointno());
            }
        } else {
            inputParams = Arrays.asList(positionVo.func, positionVo.linenumber);
        }
        getValidCheckForConnection();
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(debugOpt, inputParams)) {
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResult = false;
                if (rs.next() && getVersion()) {
                    hasResult = rs.getBoolean(1);
                }
                return hasResult;
            }
        }
    }

    private <T> List<T> getAllVariable(DebugConstants.DebugOpt debugOpt, Class<T> clazz) throws SQLException {
        List<T> vos = new ArrayList<T>();
        map.get(getFunctionVo().oid).forEach(item -> {
            try {
                List<Object> inputParams = Arrays.asList(item);
                try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(debugOpt, inputParams)) {
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            vos.addAll(QueryResVoConvertHelper.parseList(rs, clazz, getClientConn()));
                        }
                    } catch (SQLException e) {
                        MPPDBIDELoggerUtility.error(e.getMessage());
                    }
                }
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        });
        return vos;
    }

    private <T> List<T> getDbeListVos(DebugConstants.DebugOpt debugOpt, Class<T> clazz, List<Object> inputParams)
            throws SQLException {
        DebugOpt opt = VersionHelper.getDebugOptByDebuggerVersion(getClientConn(), debugOpt);
        try (PreparedStatement ps = getClientConn().getDebugOptPrepareStatement(opt, inputParams)) {
            try (ResultSet rs = ps.executeQuery()) {
                return QueryResVoConvertHelper.parseList(rs, clazz, getClientConn());
            } catch (SQLException e) {
                return new ArrayList();
            }
        }
    }
}
