package org.opengauss.mppdbide.mock.bl;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.postgresql.PGNotification;
import org.postgresql.copy.CopyOperation;
import org.postgresql.core.CachedQuery;
import org.postgresql.core.Encoding;
import org.postgresql.core.NativeQuery;
import org.postgresql.core.ParameterList;
import org.postgresql.core.Query;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.ReplicationProtocol;
import org.postgresql.core.ResultCursor;
import org.postgresql.core.ResultHandler;
import org.postgresql.core.TransactionState;
import org.postgresql.jdbc.AutoSave;
import org.postgresql.jdbc.BatchResultHandler;
import org.postgresql.jdbc.PreferQueryMode;
import org.postgresql.util.HostSpec;

public class Connfactory implements QueryExecutor
{

    private boolean isSQLExceptionReq;
    
    public Connfactory(boolean isSQLExceptionReq)
    {
        this.isSQLExceptionReq = isSQLExceptionReq;
    }
    
    @Override
    public void execute(Query query, ParameterList parameters,
            ResultHandler handler, int maxRows, int fetchSize, int flags)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }



    @Override
    public void fetch(ResultCursor cursor, ResultHandler handler, int fetchSize)
            throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Query createSimpleQuery(String sql)
    {
        // TODO Auto-generated method stub
        return null;
    }



    @Override
    public void processNotifies() throws SQLException
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ParameterList createFastpathParameters(int count)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public byte[] fastpathCall(int fnid, ParameterList params,
            boolean suppressBegin) throws SQLException
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CopyOperation startCopy(String sql, boolean suppressBegin)
            throws SQLException
    {
        
        if(isSQLExceptionReq)
        {
            isSQLExceptionReq = false;
            throw new SQLException();
        }
        
        return new CopyOperationHelper();
    }

    @Override
    public boolean useBinaryForReceive(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean useBinaryForSend(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void abort() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CachedQuery borrowCallableQuery(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CachedQuery borrowQuery(String arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CachedQuery borrowQueryByKey(Object arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CachedQuery borrowReturningQuery(String arg0, String[] arg1) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public CachedQuery createQuery(String arg0, boolean arg1, boolean arg2, String... arg3) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CachedQuery createQueryByKey(Object arg0) throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object createQueryKey(String arg0, boolean arg1, boolean arg2, String... arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void execute(Query[] arg0, ParameterList[] arg1, BatchResultHandler arg2, int arg3, int arg4, int arg5)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void executeBatch(Query[] arg0, ParameterList[] arg1, BatchResultHandler arg2, int arg3, int arg4, int arg5)
            throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String getApplicationName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AutoSave getAutoSave() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getBackendPID() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getDatabase() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Encoding getEncoding() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HostSpec getHostSpec() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getIntegerDateTimes() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getNetworkTimeout() throws IOException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PGNotification[] getNotifications() throws SQLException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PreferQueryMode getPreferQueryMode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getProtocolVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ReplicationProtocol getReplicationProtocol() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getServerVersion() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getServerVersionNum() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean getStandardConformingStrings() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TimeZone getTimeZone() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TransactionState getTransactionState() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUser() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SQLWarning getWarnings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isColumnSanitiserDisabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isReWriteBatchedInsertsEnabled() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void processNotifies(int arg0) throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void releaseQuery(CachedQuery arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void sendQueryCancel() throws SQLException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setAutoSave(AutoSave arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinaryReceiveOids(Set<Integer> arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setBinarySendOids(Set<Integer> arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setFlushCacheOnDeallocate(boolean arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setNetworkTimeout(int arg0) throws IOException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setProtocolVersion(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean willHealOnRetry(SQLException arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Query wrap(List<NativeQuery> arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getSocketAddress() {
        return "";
    }

    @Override
    public void setGaussdbVersion(String gaussdbVersion) {
        
    }

    @Override
    public String getApplicationType() {
        return "";
    }

    @Override
    public String getGaussdbVersion() {
        return "";
    }

}
