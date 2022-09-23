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

package org.opengauss.mppdbide.common;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class
 * <p>
 * Description: check version.
 *
 * @since 3.0.0
 */
public class VersionHelper {
    /**
     * Description: get openGauss debugger version
     *
     * @param serverConn database connection
     * @return pl_debugger or dbe_debugger
     * @throws SQLException the exception
     */
    public static VersionVo getDebuggerVersion(IConnection serverConn) throws SQLException {
        if (serverConn == null) {
            throw new SQLException("serverConn is null!");
        }
        String sql = DebugConstants.getDbVersionSql();
        VersionVo versionVo = new VersionVo();
        try (PreparedStatement ps = serverConn.getStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // (openGauss 2.1.0 build 590b0f8e) openGauss version info
                    String version = rs.getObject(1).toString();
                    MPPDBIDELoggerUtility.debug("openGauss db info version:" + version);
                    versionVo.version = version;
                }
                MPPDBIDELoggerUtility.debug("openGauss db info is empty!");
                return versionVo;
            }
        }
    }

    /**
     * Description: get getDebugOptByDebuggerVersion
     *
     * @param serverConn database connection
     * @param debugOpt   param
     * @return the return value
     * @throws SQLException the exception
     */
    public static DebugOpt getDebugOptByDebuggerVersion(IConnection serverConn, DebugOpt debugOpt) throws SQLException {
        if (serverConn == null) {
            return debugOpt;
        }
        VersionVo versionVo = getDebuggerVersion(serverConn);
        DebugOpt res = DebugConstants.getDebugOptByDebuggerVersion(debugOpt, versionVo.getDebuggerVersion());
        if (res == null) {
            return debugOpt;
        }
        return res;
    }
}
