package org.opengauss.mppdbide.mock.presentation.windows;

import java.sql.SQLException;

import org.postgresql.copy.CopyOperation;
import org.postgresql.core.ParameterList;
import org.postgresql.core.Query;
import org.postgresql.core.QueryExecutor;
import org.postgresql.core.ResultCursor;
import org.postgresql.core.ResultHandler;

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
    public void execute(Query[] queries, ParameterList[] parameterLists,
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
    public Query createParameterizedQuery(String sql)
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
    

}
