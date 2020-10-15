package com.huawei.mppdbide.bl.mock.debug;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockDriver;

public class GaussOlapMockDriver extends MockDriver
{

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException
    {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public Connection connect(String arg0, Properties arg1) throws SQLException
    {
       MockConnection connection= new MockConnectionStubPS(true);
        return connection;
    }

}
