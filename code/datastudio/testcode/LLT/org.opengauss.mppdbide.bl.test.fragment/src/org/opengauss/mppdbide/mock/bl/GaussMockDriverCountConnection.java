package org.opengauss.mppdbide.mock.bl;

import com.mockrunner.mock.jdbc.MockConnection;

public class GaussMockDriverCountConnection extends MockConnection
{
    public GaussMockDriverCountConnection()
    {
        super();
    }
    
    public int getConnectionCount()
    {
        return 1;
    }
}
