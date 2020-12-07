/**
 * 
 */
package com.huawei.mppdbide.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.postgresql.core.NoticeListener;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * @author z00588921
 *
 */
public class DBConnectionAdapter implements IConnection {
    private DBConnection conn;
    private NoticeListener listener;
    public DBConnectionAdapter(DBConnection conn) {
        this.conn = conn;
    }

    @Override
    public PreparedStatement getStatement(String sql) throws SQLException {
        try {
            PreparedStatement ps =  conn.getPrepareStmt(sql);
            if (this.listener != null) {
                GaussManager.instance.addNoticeListener(ps, this.listener);
            }
            return ps;
        } catch (DatabaseCriticalException | DatabaseOperationException dbException) {
            throw new SQLException(dbException.getMessage(), "", dbException.getErrorCode());
        }
    }

    @Override
    public PreparedStatement getDebugOptPrepareStatement(DebugOpt debugOpt, Object[] params) throws SQLException {
        String sql = DebugConstants.getSql(debugOpt);
        PreparedStatement ps = getStatement(sql);
        for (int i = 1 ; i < params.length + 1 ; i ++) {
            ps.setObject(i, params[i - 1]);
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
