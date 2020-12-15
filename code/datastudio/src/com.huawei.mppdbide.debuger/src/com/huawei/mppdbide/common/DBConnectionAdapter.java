/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.postgresql.core.NoticeListener;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * Title: DBConnectionAdapter for use
 * Description: IConnection instance
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00589921
 * @version [DataStudio for openGauss 1.0.0, 19 Sep, 2019]
 * @since 19 Sep, 2019
 */
public class DBConnectionAdapter implements IConnection {
    private DBConnection conn;
    private NoticeListener listener;

    public DBConnectionAdapter(DBConnection conn) {
        this.conn = conn;
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
            this.conn.disconnect();
            this.conn = null;
        }
    }

}
