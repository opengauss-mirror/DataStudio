package org.opengauss.mppdbide.mock.adapter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import org.opengauss.mppdbide.mock.adapter.CommonLLTUtilsHelper.EXCEPTIONENUM;


public class ExceptionResultset implements ResultSet
{

    private boolean throwExceptionCloseResultSet = false;
    private boolean throwExceptionGetInt = false;
    private boolean throwExceptionGetString = false;
    private boolean throwExceptionGetLong= false;
    private boolean throwExceptionGetBoolean = false;
    private EXCEPTIONENUM resultSetNext = EXCEPTIONENUM.NO;
    
    private Statement statement = null;
    
    String sqlState = "";
   
    private boolean throwExceptionGetStatement = false;
    private int counter;
   
    String exceptionForNextOn;
    
    String exceptioncounter;
    
    public ExceptionResultset()
    {
        counter = 0;
    }
    
    public void setExceptioncounter(String exceptioncounter)
    {
        this.exceptioncounter = exceptioncounter;
    }
    
    public void setExceptionForNextOn(String exceptionForNextOn)
    {
        this.exceptionForNextOn = exceptionForNextOn;
    }
    
    
    public void setStatement(Statement statement)
    {
        this.statement = statement;
    }
    
    public void setSqlState(String sqlState)
    {
        this.sqlState = sqlState;
    }
    
    public void setThrowExceptionGetStatement(boolean throwExceptionGetStatement)
    {
        this.throwExceptionGetStatement = throwExceptionGetStatement;
    }
    
    public void setThrowExceptionCloseResultSet(boolean throwExceptionCloseResultSet)
    {
        this.throwExceptionCloseResultSet = throwExceptionCloseResultSet;
    }
    
   	public void setThrowExceptionGetLong(boolean throwExceptionGetLong) {
		this.throwExceptionGetLong = throwExceptionGetLong;
	}

	public void setThrowExceptionGetInt(boolean throwExceptionGetInt)
    {
        this.throwExceptionGetInt = throwExceptionGetInt;
    }
    
    public void setThrowExceptionGetString(boolean throwExceptionGetString)
    {
        this.throwExceptionGetString = throwExceptionGetString;
    }
    
    public void setThrowExceptionGetBoolean(boolean throwExceptionGetBoolean)
    {
        this.throwExceptionGetBoolean = throwExceptionGetBoolean;
    }
    
    public void setResultSetNext(EXCEPTIONENUM resultSetNext)
    {
        this.resultSetNext = resultSetNext;
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
    public boolean absolute(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void afterLast() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void beforeFirst() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void cancelRowUpdates() throws SQLException
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
        if (throwExceptionCloseResultSet)
        {
            throw new SQLException();
        }
    }

    @Override
    public void deleteRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int findColumn(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean first() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Array getArray(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Array getArray(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAsciiStream(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getAsciiStream(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(int arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BigDecimal getBigDecimal(String arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getBinaryStream(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getBinaryStream(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob getBlob(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Blob getBlob(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getBoolean(int arg0) throws SQLException
    {
        if (throwExceptionGetBoolean)
        {
            throw new SQLException();
        }
        
        return false;
    }

    @Override
    public boolean getBoolean(String arg0) throws SQLException
    {
        if (throwExceptionGetBoolean)
        {
            throw new SQLException();
        }
        
        return false;
    }

    @Override
    public byte getByte(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte getByte(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public byte[] getBytes(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] getBytes(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getCharacterStream(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getCharacterStream(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clob getClob(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clob getClob(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getConcurrency() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCursorName() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(int arg0, Calendar arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getDate(String arg0, Calendar arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getDouble(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public double getDouble(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
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
    public float getFloat(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getFloat(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getHoldability() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(int arg0) throws SQLException
    {
        if (throwExceptionGetInt)
        {
            if(null != sqlState && sqlState.length() > 0)
            {
                throw new SQLException(sqlState, sqlState);
            }
            else
            {
            throw new SQLException();
            }
        }
        
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getInt(String arg0) throws SQLException
    {
        if (throwExceptionGetInt)
        {
            if(null != sqlState && sqlState.length() > 0)
            {
                throw new SQLException(sqlState, sqlState);
            }
            else
            {
            throw new SQLException();
            }
        }
        
        return 0;
    }

    @Override
    public long getLong(int arg0) throws SQLException
    {
    	 if (throwExceptionGetLong)
         {
             if(null != sqlState && sqlState.length() > 0)
             {
                 throw new SQLException(sqlState, sqlState);
             }
             else
             {
             throw new SQLException();
             }
         }
         
        return 0;
    }

    @Override
    public long getLong(String arg0) throws SQLException
    {
    	 if (throwExceptionGetLong)
         {
             if(null != sqlState && sqlState.length() > 0)
             {
                 throw new SQLException(sqlState, sqlState);
             }
             else
             {
             throw new SQLException();
             }
         }
        return 0;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException
    {
        // TODO Auto-generated method stub
        return new ExceptionRSMetadata();
    }

    @Override
    public Reader getNCharacterStream(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Reader getNCharacterStream(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob getNClob(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NClob getNClob(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNString(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getNString(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(int arg0, Map<String, Class<?>> arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String arg0, Map<String, Class<?>> arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ref getRef(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Ref getRef(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getRow() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public RowId getRowId(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public RowId getRowId(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML getSQLXML(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLXML getSQLXML(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public short getShort(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public short getShort(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Statement getStatement() throws SQLException
    {
        if(throwExceptionGetStatement)
        {
            throwExceptionGetStatement = false;
            if(null != sqlState && sqlState.length() > 0)
            {
                throw new SQLException(sqlState, sqlState);
            }
            else
            {
                throw new SQLException();
            }
        }
        
        return statement;
    }

    @Override
    public String getString(int arg0) throws SQLException
    {
        if (throwExceptionGetString)
        {
            throw new SQLException();
        }
        
        return null;
    }

    @Override
    public String getString(String arg0) throws SQLException
    {
        if (throwExceptionGetString)
        {
            throw new SQLException();
        }
        
        return "(Dummy   )";
    }

    @Override
    public Time getTime(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(int arg0, Calendar arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Time getTime(String arg0, Calendar arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Timestamp getTimestamp(String arg0, Calendar arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getType() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public URL getURL(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public URL getURL(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getUnicodeStream(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public InputStream getUnicodeStream(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void insertRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        throw new SQLException();
    }

    @Override
    public boolean isBeforeFirst() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isClosed() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isLast() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean last() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void moveToCurrentRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void moveToInsertRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean next() throws SQLException
    {
        if (EXCEPTIONENUM.EXCEPTION.equals(resultSetNext))
        {
            counter++;
            if(exceptionForNextOn != null && exceptionForNextOn.equals("loadview"))
            {
                if("SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE (c.relkind = 'v'::\"char\") and has_table_privilege(c.oid,'SELECT') ".equals(exceptioncounter))
                if(null != sqlState && sqlState.length() > 0)
                {
                    throw new SQLException(sqlState, sqlState);
                }
                else
                {
                    throw new SQLException();
                }
            }
            else
            {
                if(null != sqlState && sqlState.length() > 0)
                {
                    throw new SQLException(sqlState, sqlState);
                }
                else
                {
                    throw new SQLException();
                }
            }
            
        }
        else if(EXCEPTIONENUM.YES.equals(resultSetNext))
        {
            return true;
        }
        
        return false;
    }

    @Override
    public boolean previous() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void refreshRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean relative(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean rowUpdated() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
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
    public void updateArray(int arg0, Array arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateArray(String arg0, Array arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBigDecimal(String arg0, BigDecimal arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(int arg0, Blob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(String arg0, Blob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(int arg0, InputStream arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(String arg0, InputStream arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(int arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBlob(String arg0, InputStream arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBoolean(int arg0, boolean arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBoolean(String arg0, boolean arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateByte(int arg0, byte arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateByte(String arg0, byte arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBytes(int arg0, byte[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateBytes(String arg0, byte[] arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateCharacterStream(String arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(int arg0, Clob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(String arg0, Clob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(String arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateClob(String arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDate(int arg0, Date arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDate(String arg0, Date arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDouble(int arg0, double arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateDouble(String arg0, double arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFloat(int arg0, float arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateFloat(String arg0, float arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateInt(int arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateInt(String arg0, int arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateLong(int arg0, long arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateLong(String arg0, long arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNCharacterStream(int arg0, Reader arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNCharacterStream(String arg0, Reader arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(int arg0, NClob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(String arg0, NClob arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(int arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(String arg0, Reader arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(int arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNClob(String arg0, Reader arg1, long arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNString(int arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNString(String arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNull(int arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateNull(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateObject(int arg0, Object arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateObject(String arg0, Object arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateObject(int arg0, Object arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateObject(String arg0, Object arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRef(int arg0, Ref arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRef(String arg0, Ref arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRow() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRowId(int arg0, RowId arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateRowId(String arg0, RowId arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateShort(int arg0, short arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateShort(String arg0, short arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateString(int arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateString(String arg0, String arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTime(int arg0, Time arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTime(String arg0, Time arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTimestamp(String arg0, Timestamp arg1)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean wasNull() throws SQLException
    {
        // TODO Auto-generated method stub
        return false;
    }

	@Override
	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

}
