package org.opengauss.mppdbide.test.adapter;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import org.opengauss.mppdbide.adapter.gauss.Activator;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtils;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ActivatorTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        connection = new MockConnection();
        // test for logging
        MPPDBIDELoggerUtility
                .setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);

    }

   
    /**
     * Tear down.
     *
     * @throws Exception the exception
     */
    @After
	public void tearDown() throws Exception
    {
        super.tearDown();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();

    }

    @Test
    public void test_ActivatorStart()
    {
        try
        {

            BundleContext bundleActivator = Activator.getContext();
            org.opengauss.mppdbide.adapter.gauss.Activator act = new org.opengauss.mppdbide.adapter.gauss.Activator();
            act.start(bundleActivator);
            act.stop(bundleActivator);
        }

        catch (Exception e)
        {

            fail(e.getMessage());
        }
    }

}
