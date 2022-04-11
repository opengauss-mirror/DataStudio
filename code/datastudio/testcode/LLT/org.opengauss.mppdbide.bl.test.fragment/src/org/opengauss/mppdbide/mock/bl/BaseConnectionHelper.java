package org.opengauss.mppdbide.mock.bl;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.TimerTask;
import java.util.concurrent.Executor;

import org.postgresql.PGNotification;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.postgresql.core.CachedQuery;
import org.postgresql.core.Encoding;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.ReplicationProtocol;
import org.postgresql.core.TransactionState;
import org.postgresql.core.TypeInfo;
import org.postgresql.core.Version;
import org.postgresql.fastpath.Fastpath;
import org.postgresql.log.Log;
import org.postgresql.jdbc.AutoSave;
import org.postgresql.jdbc.FieldMetadata;
import org.postgresql.jdbc.FieldMetadata.Key;
import org.postgresql.jdbc.PreferQueryMode;
import org.postgresql.largeobject.LargeObjectManager;
import org.postgresql.replication.PGReplicationConnection;
import org.postgresql.util.HostSpec;
import org.postgresql.util.LruCache;
import org.postgresql.xml.PGXmlFactoryFactory;

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
    public void setSchema(String schema)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getSchema()
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
    public TransactionState getTransactionState()
    {
        // TODO Auto-generated method stub
        return TransactionState.IDLE;
    }

    @Override
    public TypeInfo getTypeInfo()
    {
        // TODO Auto-generated method stub
        return null;
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

    @Override
    public Array createArrayOf(String arg0, Object arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String escapeIdentifier(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String escapeLiteral(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AutoSave getAutosave() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getDefaultFetchSize() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PGNotification[] getNotifications(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreferQueryMode getPreferQueryMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PGReplicationConnection getReplicationAPI() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAutosave(AutoSave arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setDefaultFetchSize(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addTimerTask(TimerTask arg0, long arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CachedQuery createQuery(String arg0, boolean arg1, boolean arg2, String... arg3) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LruCache<Key, FieldMetadata> getFieldMetadataCache() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Log getLogger() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReplicationProtocol getReplicationProtocol() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public org.postgresql.jdbc.TimestampUtils getTimestampUtils() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean haveMinimumServerVersion(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean haveMinimumServerVersion(Version arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void purgeTimerTasks() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFlushCacheOnDeallocate(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

	@Override
	public PGXmlFactoryFactory getXmlFactoryFactory() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public String getSocketAddress() {
        return "";
    }

}
