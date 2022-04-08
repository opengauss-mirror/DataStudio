package org.opengauss.mppdbide.mock.bl;

import java.sql.SQLException;

import com.mockrunner.mock.jdbc.MockResultSet;

public class ResultSetThrowsException extends MockResultSet
{

    public ResultSetThrowsException(String id)
    {
        super(id);
    }

    @Override
    public boolean isAfterLast() throws SQLException
    {
        throw new SQLException("Some error occurred");
    }
    
    @Override
    public boolean next() throws SQLException
    {
        throw new SQLException("Some error occurred");
    }    
}
