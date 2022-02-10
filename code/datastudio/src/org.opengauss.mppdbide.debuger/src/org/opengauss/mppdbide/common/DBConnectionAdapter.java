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
import java.sql.SQLException;
import java.util.List;

import org.postgresql.core.NoticeListener;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * Title: DBConnectionAdapter for use
 * Description: IConnection instance
 *
 * @since 3.0.0
 */
public class DBConnectionAdapter implements IConnection {
    /**
     * the default of debug function operation timeout
     */
    private static final int DEFAULT_DEBUG_QUERY_TIMEOUT = 5;
    private DBConnection conn;
    private IConnectionDisconnect<DBConnection> disconnect;
    private NoticeListener listener;

    public DBConnectionAdapter(
            DBConnection conn,
            IConnectionDisconnect<DBConnection> disconnect) {
        this.conn = conn;
        this.disconnect = disconnect;
    }

    /**
     * get PreparedStatement from connection
     *
     * @param sql sql to execute
     * @return PreparedStatement preparedstatement instance
     * @throws SQLException sql exception
     */
    @Override
    public PreparedStatement getStatement(String sql) throws SQLException {
        try {
            PreparedStatement ps =  conn.getPrepareStmt(sql);
            if (this.listener != null) {
                GaussManager.INSTANCE.addNoticeListener(ps, this.listener);
            }
            return ps;
        } catch (DatabaseCriticalException | DatabaseOperationException dbException) {
            throw new SQLException(dbException.getMessage(), "", dbException.getErrorCode());
        }
    }

    /**
     * get templated of DebugOpt PreparedStatement from connection
     *
     * @param debugOpt enum opt
     * @param params the param to set to PreparedStatement
     * @return PreparedStatement preparedstatement instance
     * @throws SQLException sql exception
     */
    @Override
    public PreparedStatement getDebugOptPrepareStatement(
            DebugOpt debugOpt,
            List<Object> params) throws SQLException {
        String sql = DebugConstants.getSql(debugOpt);
        PreparedStatement ps = getStatement(sql);
        ps.setQueryTimeout(DEFAULT_DEBUG_QUERY_TIMEOUT);
        for (int i = 1 ; i < params.size() + 1 ; i ++) {
            ps.setObject(i, params.get(i - 1));
        }
        return ps;
    }

    @Override
    public void setNoticeListener(NoticeListener listener) {
        this.listener = listener;
    }

    @Override
    public void close() throws SQLException {
        if (this.conn != null) {
            this.disconnect.releaseConnection(conn);
            this.conn = null;
        }
    }

}
