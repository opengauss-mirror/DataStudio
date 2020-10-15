package com.huawei.mppdbide.mock.presentation.windows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.postgresql.PGNotification;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.core.Encoding;
import org.postgresql.core.Logger;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.TypeInfo;
import org.postgresql.fastpath.Fastpath;
import org.postgresql.jdbc2.TimestampUtils;
import org.postgresql.largeobject.LargeObjectManager;
import org.postgresql.util.HostSpec;

import com.mockrunner.mock.jdbc.MockConnection;

public class BaseConnectionHelper extends MockConnection implements BaseConnection
{

    QueryExecutor queryExecutor;
    boolean returnBaseStmt = false;
    String encoding = "UTF8";
    
    public void setReturnBaseStmt(boolean returnBaseStmt)
    {
        this.returnBaseStmt = returnBaseStmt;
    }
    
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }
    
    public BaseConnectionHelper(String url, Properties info, HostSpec[] hostSpec, String database, String user, boolean isSQLExceptionReq) throws SQLException
    {
        queryExecutor = new Connfactory(isSQLExceptionReq);
    }
    
    @Override
    public void addDataType(String arg0, String arg1)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addDataType(String arg0, Class arg1) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CopyManager getCopyAPI() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Fastpath getFastpathAPI() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LargeObjectManager getLargeObjectAPI() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PGNotification[] getNotifications() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getPrepareThreshold()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void setPrepareThreshold(int arg0)
    {
        // TODO Auto-generated method stub
        
    }

   

    @Override
    public void setSchema(String schema) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSchema() throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getNetworkTimeout() throws SQLException
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean binaryTransferSend(int arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void cancelQuery() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public byte[] encodeString(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String escapeString(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet execSQLQuery(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResultSet execSQLQuery(String arg0, int arg1, int arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execSQLUpdate(String arg0) throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Encoding getEncoding() throws SQLException
    {
        // TODO Auto-generated method stub
        return new EncodingHelper(encoding);
    }

    @Override
    public Logger getLogger()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object getObject(String arg0, String arg1, byte[] arg2)
            throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public QueryExecutor getQueryExecutor()
    {
        // TODO Auto-generated method stub
        return queryExecutor;
    }

    @Override
    public boolean getStandardConformingStrings()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean getStringVarcharFlag()
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TimestampUtils getTimestampUtils()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getTransactionState()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TypeInfo getTypeInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean haveMinimumCompatibleVersion(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean haveMinimumServerVersion(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }

private boolean throwSQLException;
    
    public void setThrowSQLException(boolean throwSQLException)
    {
        this.throwSQLException = throwSQLException;
    }
    
    @Override
    public Statement createStatement() throws SQLException
    {
        // TODO Auto-generated method stub
        if(returnBaseStmt)
        {
            returnBaseStmt = false;
            
            BaseStatementHelper bs = new BaseStatementHelper(); 
            bs.setThrowSQLException(throwSQLException);
            
            throwSQLException = false;
            
            return bs;
        }
        else
        {
            return super.createStatement();    
        }
        
    }


    @Override
    public boolean isColumnSanitiserDisabled()
    {
        // TODO Auto-generated method stub
        return false;
    }


}
