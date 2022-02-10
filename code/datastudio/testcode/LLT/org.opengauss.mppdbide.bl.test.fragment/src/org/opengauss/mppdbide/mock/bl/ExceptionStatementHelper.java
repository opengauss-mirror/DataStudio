package org.opengauss.mppdbide.mock.bl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;

public class ExceptionStatementHelper implements Statement
{
 private boolean stmtCloseSQLExceptionNeede = false;
 
 private boolean needExceptionResultset = false;
 
 private boolean throwoutofmemerror = false;
    
 private boolean throwExceptionNext = false;
 
 private boolean throwIndexoutOfBondException=false;
 
    public void setThrowExceptionNext(boolean throwExceptionNext)
{
    this.throwExceptionNext = throwExceptionNext;
}
    public void setThrowoutofmemerror(boolean throwoutofmemerror)
{
    this.throwoutofmemerror = throwoutofmemerror;
}
    public void setNeedExceptionResultset(boolean needExceptionResultset)
    {
        this.needExceptionResultset = needExceptionResultset;
    }
    public void setStmtCloseSQLExceptionNeede(boolean stmtCloseSQLExceptionNeede)
    {
        this.stmtCloseSQLExceptionNeede = stmtCloseSQLExceptionNeede;
    }
    
    public void setThrowIndexoutOfBondException(boolean throwIndexoutOfBondException) {
		this.throwIndexoutOfBondException = throwIndexoutOfBondException;
	}
	@Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addBatch(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void cancel() throws SQLException
    {
        throw new SQLException();

    }

    @Override
    public void clearBatch() throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearWarnings() throws SQLException
    {
        throw new SQLException();

    }

    @Override
    public void close() throws SQLException
    {
        if(stmtCloseSQLExceptionNeede)
        {
            throw new SQLException();
        }
    }

    @Override
    public boolean execute(String arg0) throws SQLException
    {
        if(throwoutofmemerror)
        {
            throw new OutOfMemoryError();
        }
        return true;
    }

    @Override
    public boolean execute(String arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean execute(String arg0, int[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean execute(String arg0, String[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet executeQuery(String arg0) throws SQLException
    {
    	if(throwIndexoutOfBondException)
    	{
    		throw new IndexOutOfBoundsException();
    	}
        return null;
    }

    @Override
    public int executeUpdate(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, int[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int executeUpdate(String arg0, String[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getFetchDirection() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getFetchSize() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getMaxFieldSize() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getMaxRows() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getMoreResults(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getQueryTimeout() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSet getResultSet() throws SQLException
    {
        if(needExceptionResultset)
        {
            ExceptionResultsetHelper exceptionResultset = new ExceptionResultsetHelper();
            if(throwExceptionNext)
            {
                exceptionResultset.setResultSetNext(EXCEPTIONENUM.EXCEPTION);
            }
            else
            {
                exceptionResultset.setResultSetNext(EXCEPTIONENUM.YES);
            }
            return exceptionResultset;
        }
        return null;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getResultSetHoldability() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getUpdateCount() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        throw new SQLException();
        
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isPoolable() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setCursorName(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setEscapeProcessing(boolean arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFetchDirection(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFetchSize(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxFieldSize(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setMaxRows(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setPoolable(boolean arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void setQueryTimeout(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub

    }
	@Override
	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

}
