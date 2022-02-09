package org.opengauss.mppdbide.mock.bl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mockrunner.mock.jdbc.MockPreparedStatement;

public class GaussMockPreparedStatementToHang extends MockPreparedStatement
{
    private static ArrayList<String> hangQueries = new ArrayList<String>(); 
    
    public GaussMockPreparedStatementToHang(Connection connection, String sql)
    {
        super(connection, sql);
    }

    public GaussMockPreparedStatementToHang(Connection connection, String sql,
            boolean returnGeneratedKeys)
    {
        super(connection, sql, returnGeneratedKeys);
    }

    public GaussMockPreparedStatementToHang(Connection connection, String sql,
            int resultSetType, int resultSetConcurrency,
            int resultSetHoldability)
    {
        super(connection, sql, resultSetType, resultSetConcurrency,
                resultSetHoldability);
    }

    public GaussMockPreparedStatementToHang(Connection connection, String sql,
            int resultSetType, int resultSetConcurrency)
    {
        super(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public boolean execute() throws SQLException
    {
        hang();
        return super.execute();
    }

    @Override
    public ResultSet executeQuery() throws SQLException
    {
        hang();
        return super.executeQuery();
    }

    @Override
    public int executeUpdate() throws SQLException
    {
        hang();
        return super.executeUpdate();
    }

    @Override
    public int[] executeBatch() throws SQLException
    {
        hang();
        return super.executeBatch();
    }

    @Override
    protected int[] executeBatch(List batchParams) throws SQLException
    {
        hang();
        return super.executeBatch(batchParams);
    }

    public static void setHang(String query)
    {
        hangQueries.add(query);
    }
    
    public static void resetHang(String query)
    {
        hangQueries.remove(query);
    }
    
    private void hang()
    {
        if (!hangQueries.contains(getSQL()))
        {
            return;
        }
        int counter = 0;
        while (hangQueries.contains(getSQL()))
        {
            try
            {
                Thread.sleep(100);
                if(counter++ > 50)
                {
                    break;
                }
            }
            catch (InterruptedException e)
            {
                break;
            }
        }
    }
    
    public static void resetHangqueries()
    {
        hangQueries.clear();
    }
}
