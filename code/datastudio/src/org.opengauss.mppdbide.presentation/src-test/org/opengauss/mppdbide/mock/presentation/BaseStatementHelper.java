package org.opengauss.mppdbide.mock.presentation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;

import org.postgresql.core.BaseStatement;
import org.postgresql.core.CachedQuery;
import org.postgresql.core.Field;
import org.postgresql.core.NoticeListener;
import org.postgresql.core.Query;
import org.postgresql.core.ResultCursor;

public class BaseStatementHelper implements BaseStatement
{

    @Override
    public long getLastOID() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void setUseServerPrepare(boolean flag) throws SQLException
    {
        

    }

    @Override
    public boolean isUseServerPrepare()
    {
        
        return false;
    }

    @Override
    public void setPrepareThreshold(int threshold) throws SQLException
    {
        

    }

    @Override
    public int getPrepareThreshold()
    {
        
        return 0;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException
    {
        
        return null;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException
    {
        
        return 0;
    }

    @Override
    public void close() throws SQLException
    {
        

    }

    @Override
    public int getMaxFieldSize() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException
    {
        

    }

    @Override
    public int getMaxRows() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException
    {
        

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        

    }

    @Override
    public int getQueryTimeout() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException
    {
        

    }

    @Override
    public void cancel() throws SQLException
    {
        

    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException
    {
        

    }

    @Override
    public void setCursorName(String name) throws SQLException
    {
        

    }

    @Override
    public boolean execute(String sql) throws SQLException
    {
        
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException
    {
        
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException
    {
        
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException
    {
        
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException
    {
        

    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException
    {
        

    }

    @Override
    public int getFetchSize() throws SQLException
    {
        
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException
    {
        
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException
    {
        
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException
    {
        

    }

    @Override
    public void clearBatch() throws SQLException
    {
        

    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException
    {
        
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException
    {
        
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys)
            throws SQLException
    {
        
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes)
            throws SQLException
    {
        
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames)
            throws SQLException
    {
        
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys)
            throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames)
            throws SQLException
    {
        
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException
    {
        
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException
    {
        

    }

    @Override
    public boolean isPoolable() throws SQLException
    {
        
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException
    {
        

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException
    {
        
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException
    {
        
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException
    {
        
        return false;
    }

    @Override
    public ResultSet createDriverResultSet(Field[] fields, List tuples)
            throws SQLException
    {
        
        return null;
    }

    @Override
    public ResultSet createResultSet(Query originalQuery, Field[] fields,
            List tuples, ResultCursor cursor) throws SQLException
    {
        
        return null;
    }

    @Override
    public boolean executeWithFlags(String p_sql, int flags)
            throws SQLException
    {
        
        return false;
    }

    @Override
    public boolean executeWithFlags(int flags) throws SQLException
    {
        
        return false;
    }

    private boolean throwSQLException;
    
    public void setThrowSQLException(boolean throwSQLException)
    {
        this.throwSQLException = throwSQLException;
    }
    
    @Override
    public void addNoticeListener(NoticeListener listener) throws SQLException
    {
        if(throwSQLException)
        {
            this.throwSQLException = false;
            throw new SQLException(); 
        }

    }

    @Override
    public boolean executeWithFlags(CachedQuery arg0, int arg1) throws SQLException {
        // TODO Auto-generated method stub
        return false;
    }

}
