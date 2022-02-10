package org.opengauss.mppdbide.bl.mock.debug;

import com.mockrunner.mock.jdbc.JDBCMockObjectFactory;
import com.mockrunner.mock.jdbc.MockDriver;

public class GaussJDBCOLAPFactory extends JDBCMockObjectFactory
{
    GaussOlapMockDriver driver;

    @Override
    public MockDriver createMockDriver()
    {
        if (driver == null)
        {
            driver = new GaussOlapMockDriver();
        }
        return driver;
    }
}
