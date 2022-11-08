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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.common.DbeCommonUtils;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.exception.DebugPositionNotFoundException;
import org.opengauss.mppdbide.debuger.service.SourceCodeService.CodeDescription;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.debuger.vo.dbe.AttachVo;
import org.opengauss.mppdbide.debuger.vo.dbe.TurnOnVo;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.VariableRunLine;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.vo.DebuggerEndInfoVo;
import org.opengauss.mppdbide.utils.vo.DebuggerStartInfoVo;

/**
 * Description: Coverage record generation
 *
 * @since 3.0.0
 */
public class DebuggerReportService {
    /**
     * default offset value
     */
    public static final int CODE_BASE_OFFSET = 1;
    private static final String CREAT_TABLE = "CREATE TABLE IF NOT EXISTS his_coverage( oid BIGINT,";
    private static final String TABLE_FIELD_ONE = " cid BIGINT, coverageLines VARCHAR, remarkLines VARCHAR, ";
    private static final String TABLE_FIELD_TWO = "endTime BIGINT, sourceCode VARCHAR, params VARCHAR,"
            + " canBreakLine VARCHAR);";
    private static final String INSERT = "insert into his_coverage VALUES(?,?,?,?,?,?,?,?);";

    /**
     * default value
     */
    private static DebuggerReportService debuggerReportService = new DebuggerReportService();

    private IConnection serverConn;
    private IConnection clientConn;
    private IConnection queryConn;
    private FunctionVo functionVo;
    private TurnOnVo turnOnVo;
    private DebuggerStartInfoVo startInfo;
    private CodeDescription baseCodeDesc = null;
    private CodeDescription totalCodeDesc = null;

    private DebuggerReportService() {

    }

    /**
     * Gets the single instance of DebuggerReportService.
     *
     * @return single instance of DebuggerReportService
     */
    public static DebuggerReportService getInstance() {
        return debuggerReportService;
    }

    /**
     * set base code
     *
     * @param baseCodeDesc the baseCodeDesc
     */
    public void setBaseCode(CodeDescription baseCodeDesc) {
        this.baseCodeDesc = baseCodeDesc;
    }

    /**
     * set total code
     *
     * @param totalCodeDesc the code
     */
    public void setTotalCode(CodeDescription totalCodeDesc) {
        this.totalCodeDesc = totalCodeDesc;
    }

    private String getCurLine(int breakPointLine) {
        try {
            return String.valueOf(codeLine2ShowLine(breakPointLine));
        } catch (DebugPositionNotFoundException dbgExp) {
            MPPDBIDELoggerUtility.error("receive invalid position:" + dbgExp.toString());
        }
        return "-1";
    }

    private String getCurLine() {
        List<String> terminalCodes = this.totalCodeDesc.getCodeList();
        int index = DbeCommonUtils.compluteIndex(DbeCommonUtils.infoCodes, terminalCodes);
        return String.valueOf(index);
    }

    /**
     * set attribute value
     *
     * @param connectProvider the connect provider
     * @param functionVo      the function
     */
    public void setAttr(IConnectionProvider connectProvider, FunctionVo functionVo) {
        try {
            this.serverConn = connectProvider.getValidFreeConnection();
            this.clientConn = connectProvider.getValidFreeConnection();
            this.queryConn = connectProvider.getValidFreeConnection();
            this.functionVo = functionVo;
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
    }

    /**
     * set server connection
     *
     * @param serverConn connection to set of server
     * @return void
     */
    public void setServerConn(IConnection serverConn) {
        this.serverConn = serverConn;
    }

    /**
     * set client connection
     *
     * @param clientConn connection
     * @return void
     */
    public void setClientConn(IConnection clientConn) {
        this.clientConn = clientConn;
    }

    /**
     * close connection
     *
     * @return void
     */
    public void close() {
        try {
            if (serverConn != null) {
                serverConn.close();
                serverConn = null;
            }
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("reportService serverConn close failed, err=" + sqlErr.toString());
        }
        try {
            if (clientConn != null) {
                clientConn.close();
                clientConn = null;
            }
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("reportService clientConn close failed, err=" + sqlErr.toString());
        }
        try {
            if (queryConn != null) {
                queryConn.close();
                queryConn = null;
            }
        } catch (SQLException sqlErr) {
            MPPDBIDELoggerUtility.warn("reportService queryConn close failed, err=" + sqlErr.toString());
        }
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
     * make dbedebugger report info
     */
    public void makeReport() {
        startInfo = DebuggerStartVariable.getStartInfo(functionVo.oid);
        if (!startInfo.isMakeReport) {
            return;
        }
        List<Object> inputsParams = Arrays.asList(functionVo.oid);
        ResultSet rs = null;
        try {
            // DBE_DEBUG_OFF
            DebugOpt opt = DebugConstants.DebugOpt.DBE_DEBUG_OFF;
            rs = serverConn.getDebugOptPrepareStatement(opt, inputsParams).executeQuery();
            opt = DebugConstants.DebugOpt.DBE_START_SESSION;
            rs = serverConn.getDebugOptPrepareStatement(opt, inputsParams).executeQuery();
            if (rs.next()) {
                turnOnVo = ParseVo.parse(rs, TurnOnVo.class);
            }

            // call proc
            List<?> args = startInfo.args;
            String sql = DebugConstants.getSql(functionVo.proname, args.size());
            try (PreparedStatement ps = serverConn.getStatement(sql)) {
                for (int i = 1; i < args.size() + 1; i++) {
                    ps.setObject(i, args.get(i - 1));
                }
                CompletableFuture.delayedExecutor(100, TimeUnit.MILLISECONDS).execute(() -> {
                    doClient();
                });
                ps.executeQuery();
            }
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                MPPDBIDELoggerUtility.error(e.getMessage());
            }
        }
    }

    private void doClient() {
        VariableRunLine.runList.clear();
        // attach
        List<Object> inputsParams = new ArrayList();
        inputsParams.add(turnOnVo.nodename);
        inputsParams.add(turnOnVo.port);
        DebugOpt opt = DebugConstants.DebugOpt.DBE_ATTACH_SESSION;
        try (ResultSet rs = clientConn.getDebugOptPrepareStatement(opt, inputsParams).executeQuery()) {
            MPPDBIDELoggerUtility.info("attach start...");
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
        List<String> runLinks = new ArrayList();
        runLinks.add(getCurLine());
        // next
        inputsParams = new ArrayList();
        opt = DebugConstants.DebugOpt.DBE_STEP_OVER;
        Boolean hasNext = true;
        while (hasNext) {
            try (ResultSet rs = clientConn.getDebugOptPrepareStatement(opt, inputsParams).executeQuery()) {
                if (rs.next()) {
                    AttachVo attachVo = ParseVo.parse(rs, AttachVo.class);
                    Integer lineNo = attachVo.lineno;
                    if (lineNo > 1) {
                        runLinks.add(getCurLine(lineNo));
                    }
                } else {
                    hasNext = false;
                }
            } catch (SQLException e) {
                hasNext = false;
            }
        }
        VariableRunLine.runList.addAll(runLinks);
        DebuggerEndInfoVo endInfo = new DebuggerEndInfoVo();
        String runStr = String.join(",", runLinks);
        endInfo.runStr = runStr;
        endInfo.setInfo(startInfo);
        List<DebuggerEndInfoVo> historyList = DebuggerStartVariable.getHistoryList(functionVo.oid);
        historyList.add(endInfo);
        DebuggerStartVariable.setHistoryList(functionVo.oid, historyList);
        List<String> toRunLines = DbeCommonUtils.getCanBreakLinesByInfo(queryConn,
                Arrays.asList(functionVo.oid), SourceCodeService.CodeDescription.getLines(endInfo.sourceCode))
                .stream().map(item -> String.valueOf(Integer.parseInt(item) + 1))
                .collect(Collectors.toList());
        endInfo.canBreakLine = String.join(",", toRunLines);
        createTbale(endInfo);
    }

    private void createTbale(DebuggerEndInfoVo endInfo) {
        StringBuffer sb = new StringBuffer();
        sb.append(CREAT_TABLE);
        sb.append(TABLE_FIELD_ONE + TABLE_FIELD_TWO);
        String createTableSql = sb.toString();
        try {
            clientConn.getStatement(createTableSql).execute();
            PreparedStatement preparedStatement = clientConn.getStatement(INSERT);
            preparedStatement.setObject(1, endInfo.oid);
            preparedStatement.setObject(2, endInfo.cid);
            preparedStatement.setObject(3, endInfo.runStr);
            preparedStatement.setObject(4, endInfo.remarLinesStr);
            preparedStatement.setObject(5, endInfo.endDateLong);
            preparedStatement.setObject(6, endInfo.sourceCode);
            if (endInfo.args != null) {
                preparedStatement.setObject(7, endInfo.args.toString());
            }
            preparedStatement.setObject(8, endInfo.canBreakLine);
            preparedStatement.execute();
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }
    }

    private int getBeginDebugCodeLine() throws DebugPositionNotFoundException {
        return getFirstValidDebugPos() + getBeginTotalAndBaseDiff();
    }

    private int getFirstValidDebugPos() throws DebugPositionNotFoundException {
        return getBeignOfBaseCode() + CODE_BASE_OFFSET;
    }

    private int codeLine2ShowLine(int codeLine) throws DebugPositionNotFoundException {
        return codeLine + getBeginTotalAndBaseDiff() - CODE_BASE_OFFSET;
    }

    private int getBeginTotalAndBaseDiff() throws DebugPositionNotFoundException {
        return getBeignfTotalCode() - getBeignOfBaseCode();
    }

    private int getBeignOfBaseCode() throws DebugPositionNotFoundException {
        return this.baseCodeDesc.getBeginPosition();
    }

    private int getBeignfTotalCode() throws DebugPositionNotFoundException {
        return this.totalCodeDesc.getBeginPosition();
    }
}
