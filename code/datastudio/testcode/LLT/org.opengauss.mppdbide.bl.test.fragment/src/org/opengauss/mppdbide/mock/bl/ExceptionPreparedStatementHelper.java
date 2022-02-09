package org.opengauss.mppdbide.mock.bl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import org.opengauss.mppdbide.mock.bl.CommonLLTUtilsHelper.EXCEPTIONENUM;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockPreparedStatement;

public class ExceptionPreparedStatementHelper implements PreparedStatement
{
    private boolean outOfMemoryErrorRequired = false;
    private boolean stmtCloseSQLExceptionNeede = false;
    private boolean setLongExceptionRequired = false;
    private boolean setExceptionResultSetRequired = false;
    private boolean throwExceptionCloseResultSet = false;
    private boolean throwExceptionGetInt = false;
    private boolean throwExceptionSetInt = false;
    private EXCEPTIONENUM thrownResultSetNext = EXCEPTIONENUM.NO;
    private boolean throwExceptionGetString = false;
    private boolean throwExceptionGetBoolean = false;
    private boolean throwExceptionSetString = false;
    private boolean throwExceptionGetLong = false;
    private boolean throwIndexoutOfBondException=false;
    public void setLongExceptionRequired(boolean setLongExceptionRequired)
    {
        this.setLongExceptionRequired = setLongExceptionRequired;
    }
    
    public void setThrowExceptionGetInt(boolean throwExceptionGetInt)
    {
        this.throwExceptionGetInt = throwExceptionGetInt;
    }
   
	public void setThrowIndexoutOfBondException(boolean throwIndexoutOfBondException) {
		this.throwIndexoutOfBondException = throwIndexoutOfBondException;
	}

	public void setThrowExceptionGetLong(boolean throwExceptionGetLong)
    {
        this.throwExceptionGetLong = throwExceptionGetLong;
    }
    
    public void setThrowExceptionSetInt(boolean throwExceptionSetInt)
    {
        this.throwExceptionSetInt = throwExceptionSetInt;
    }
    
    public void setThrowExceptionGetBoolean(boolean throwExceptionGetBoolean)
    {
        this.throwExceptionGetBoolean = throwExceptionGetBoolean;
    }
    
    public void setThrowExceptionGetString(boolean throwExceptionGetString)
    {
        this.throwExceptionGetString = throwExceptionGetString;
    }
    
    public void setThrowExceptionSetString(boolean throwExceptionSetString)
    {
        this.throwExceptionSetString = throwExceptionSetString;
    }
    
    public void setStmtCloseSQLExceptionNeede(boolean stmtCloseSQLExceptionNeede)
    {
        this.stmtCloseSQLExceptionNeede = stmtCloseSQLExceptionNeede;
    }
    
    public void setExceptionResultSetRequired(boolean setExceptionResultSetRequired)
    {
        this.setExceptionResultSetRequired = setExceptionResultSetRequired;
    }
    
    public void setThrowExceptionCloseResultSet(boolean throwExceptionCloseResultSet)
    {
        this.throwExceptionCloseResultSet = throwExceptionCloseResultSet;
    }

    public void setThrownResultSetNext(EXCEPTIONENUM thrownResultSetNext)
    {
        this.thrownResultSetNext = thrownResultSetNext;
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
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
        return false;
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
        if (outOfMemoryErrorRequired)
        {
            throw new OutOfMemoryError();
        }
        
        
        return false;
    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet executeQuery(String arg0) throws SQLException,IndexOutOfBoundsException
    {
        if (setExceptionResultSetRequired)
        {
            ExceptionResultsetHelper exceptionResultset = new ExceptionResultsetHelper();
            
            if (throwExceptionCloseResultSet)
            {
                exceptionResultset.setThrowExceptionCloseResultSet(true);
            }
            
            if (!thrownResultSetNext.equals(EXCEPTIONENUM.NO))
            {
                exceptionResultset.setResultSetNext(thrownResultSetNext);
            }
            
            if (throwExceptionGetInt)
            {
                exceptionResultset.setThrowExceptionGetInt(true);
            }
            if (throwExceptionGetLong)
            {
                exceptionResultset.setThrowExceptionGetLong(true);
            }
            if (throwExceptionGetBoolean)
            {
                exceptionResultset.setThrowExceptionGetBoolean(true);
            }
            
            if (throwExceptionGetString)
            {
                exceptionResultset.setThrowExceptionGetString(true);
            }
            
            if(throwIndexoutOfBondException){
            	throw new IndexOutOfBoundsException();
            }
            return exceptionResultset;
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
        if (setExceptionResultSetRequired)
        {
            ExceptionResultsetHelper exceptionResultset = new ExceptionResultsetHelper();
            
            if (throwExceptionCloseResultSet)
            {
                exceptionResultset.setThrowExceptionCloseResultSet(true);
            }
            
            return exceptionResultset;
        }
        
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return null;
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
    public void addBatch() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearParameters() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean execute() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public ResultSet executeQuery() throws SQLException,IndexOutOfBoundsException
    {
        if (setExceptionResultSetRequired)
        {
            ExceptionResultsetHelper exceptionResultset = new ExceptionResultsetHelper();
            
            if (throwExceptionCloseResultSet)
            {
                exceptionResultset.setThrowExceptionCloseResultSet(true);
            }
            
            exceptionResultset.setResultSetNext(thrownResultSetNext);
            
            if (throwExceptionGetInt)
            {
                exceptionResultset.setThrowExceptionGetInt(true);
            }
            if (throwExceptionGetLong)
            {
                exceptionResultset.setThrowExceptionGetLong(true);
            }
            if (throwExceptionGetBoolean)
            {
                exceptionResultset.setThrowExceptionGetBoolean(true);
            }
            
            if (throwExceptionGetString)
            {
                exceptionResultset.setThrowExceptionGetString(true);
            }
            if(throwIndexoutOfBondException){
            	throw new IndexOutOfBoundsException();
            }
            
            return exceptionResultset;
        }
        
        return null;
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setArray(int arg0, Array arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAsciiStream(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryStream(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlob(int arg0, Blob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlob(int arg0, InputStream arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBlob(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBoolean(int arg0, boolean arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setByte(int arg0, byte arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBytes(int arg0, byte[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClob(int arg0, Clob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClob(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setClob(int arg0, Reader arg1, long arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDate(int arg0, Date arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDouble(int arg0, double arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFloat(int arg0, float arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInt(int arg0, int arg1) throws SQLException
    {
        if (throwExceptionSetInt)
        {
            throw new SQLException();
        }
    }

    @Override
    public void setLong(int arg0, long arg1) throws SQLException
    {
        if (setLongExceptionRequired)
        {
            throw new SQLException();
        }
    }

    @Override
    public void setNCharacterStream(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClob(int arg0, NClob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClob(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNString(int arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNull(int arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNull(int arg0, int arg1, String arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setObject(int arg0, Object arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setObject(int arg0, Object arg1, int arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setObject(int arg0, Object arg1, int arg2, int arg3)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRef(int arg0, Ref arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setRowId(int arg0, RowId arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setSQLXML(int arg0, SQLXML arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setShort(int arg0, short arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setString(int arg0, String arg1) throws SQLException
    {
        if (throwExceptionSetString)
        {
            throw new SQLException();
        }
        // throw new SQLException();        
    }

    @Override
    public void setTime(int arg0, Time arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTimestamp(int arg0, Timestamp arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setURL(int arg0, URL arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setUnicodeStream(int arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    public boolean isOutOfMemoryErrorRequired()
    {
        return outOfMemoryErrorRequired;
    }

    public void setOutOfMemoryErrorRequired(boolean outOfMemoryErrorRequired)
    {
        this.outOfMemoryErrorRequired = outOfMemoryErrorRequired;
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
