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

package org.opengauss.mppdbide.view.coverage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.debuger.annotation.ParseVo;
import org.opengauss.mppdbide.debuger.service.IService;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.utils.DebuggerStartVariable;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.vo.DebuggerEndInfoVo;
import org.opengauss.mppdbide.utils.vo.DebuggerStartInfoVo;
import org.opengauss.mppdbide.view.vo.CoverageVo;

/**
 * CoverageService
 *
 * @since 3.0.0
 */
public class CoverageService implements IService {
    private IConnection conn = null;

    public void setConn(IConnection conn) {
        this.conn = conn;
    }

    /**
     * setRemarkInfo
     *
     * @param oid      the oid
     * @param rowLines the row line
     * @throws SQLException the exception
     */
    public void setRemarkInfo(long oid, String rowLines) throws SQLException {
        DebuggerStartInfoVo startInfo = DebuggerStartVariable.getStartInfo(oid);
        startInfo.remarLinesStr = rowLines;
        DebuggerStartVariable.setStartInfo(oid, startInfo);
    }

    /**
     * getRemarkInfo
     *
     * @param oid the oid
     * @return the value
     * @throws SQLException the exception
     */
    public String getRemarkInfo(long oid) throws SQLException {
        DebuggerStartInfoVo startInfo = DebuggerStartVariable.getStartInfo(oid);
        return startInfo.remarLinesStr;
    }

    /**
     * getCoverageInfoByOid
     *
     * @param oid the oid
     * @return the value
     */
    public List<CoverageVo> getCoverageInfoByOid(long oid) {
        String sql = "select * from his_coverage where oid=" + oid + " order by cid desc;";
        try {
            List<CoverageVo> res = this.queryList(sql, CoverageVo.class);
            res.stream().forEach(cov -> {
                if (cov.sourceCode != null) {
                    List<String> toRunLines = SourceCodeService.CodeDescription.getRunLinesNums(cov.sourceCode);
                    cov.totalLineNum = toRunLines.size();
                    cov.coverageLineNum = cov.getRunList().size();
                    cov.coverageLinesArr = cov.getRunList();
                    cov.totalPercent = Double.parseDouble(
                            String.format("%.2f",
                                    ((double) cov.coverageLineNum * 100 / (double) cov.totalLineNum)))
                            + "%";
                    // if not remark
                    if (cov.remarkLines == null || "".equals(cov.remarkLines)) {
                        cov.remarkLines = toRunLines.stream()
                                .map(String::valueOf).collect(Collectors.joining(","));
                        cov.remarkLinesArr = cov.getRemarkList();
                    } else {
                        cov.remarkLinesArr = cov.getRemarkList().stream().map(item ->
                                Integer.parseInt(item) + 1).sorted()
                                .map(item -> String.valueOf(item)).collect(Collectors.toList());
                    }
                    cov.remarkLineNum = cov.remarkLinesArr.size();
                    cov.remarkCoverageLinesArr = cov.coverageLinesArr.stream().map(item ->
                            Integer.parseInt(item) + 1 + "")
                            .filter(ite -> cov.remarkLinesArr.contains(ite)).collect(Collectors.toList());
                    cov.remarkCoverageLineNum = cov.remarkCoverageLinesArr.size();
                    cov.remarkPercent = Double.parseDouble(
                            String.format("%.2f",
                                    ((double) cov.remarkCoverageLineNum * 100 / (double) cov.remarkLineNum)))
                            + "%";
                }
            });
            return res.stream().sorted(Comparator.comparing(CoverageVo::getEndTime)
                    .reversed()).collect(Collectors.toList());
        } catch (SQLException e) {
            return new ArrayList();
        }
    }

    /**
     * delCoverageInfoByOid
     *
     * @param oid the oid
     * @param cid the cid
     */
    public void delCoverageInfoByOid(long oid, Long cid) {
        String sql = "delete from his_coverage where oid=" + oid + " and cid=" + cid + ";";
        try {
            conn.getStatement(sql).executeUpdate();
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.error(e.getMessage());
        }

        List<DebuggerEndInfoVo> historyList = DebuggerStartVariable.getHistoryList(oid);
        historyList = historyList.stream().filter(a -> !a.cid.equals(cid)).collect(Collectors.toList());
        DebuggerStartVariable.setHistoryList(oid, historyList);
    }

    private Boolean executeSql(String sql) throws SQLException {
        Boolean isFlag = false;
        try (PreparedStatement ps = conn.getStatement(sql)) {
            isFlag = ps.execute(sql);
        } catch (SQLException e) {
            throw new SQLException("sql execute exception!");
        }
        return isFlag;
    }

    private <T> T queryObj(String sql, Class<T> clazz) throws SQLException {
        T t = null;
        try (PreparedStatement ps = conn.getStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    t = ParseVo.parse(rs, clazz);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("sql execute exception!");
        }
        return t;
    }

    private <T> List<T> queryList(String sql, Class<T> clazz) throws SQLException {
        List<T> list = null;
        try (PreparedStatement ps = conn.getStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                list = ParseVo.parseList(rs, clazz);
            }
        } catch (SQLException e) {
            throw new SQLException("sql execute exception!");
        }
        return list;
    }

    @Override
    public void closeService() {
        try {
            if (this.conn != null) {
                this.conn.close();
                this.conn = null;
            }
        } catch (SQLException e) {
            MPPDBIDELoggerUtility.warn("close conn with err:" + e.toString());
        }
    }
}
