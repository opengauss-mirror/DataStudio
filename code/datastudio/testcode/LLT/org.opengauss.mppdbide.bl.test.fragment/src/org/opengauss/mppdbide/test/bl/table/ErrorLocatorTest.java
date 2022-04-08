package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.errorlocator.ErrorLocator;
import org.opengauss.mppdbide.bl.errorlocator.IErrorLocator;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ErrorLocatorTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    DBConnProfCache                   connProfCache            = null;
    ConnectionProfileId               profileId                = null;
    ServerConnectionInfo              serverInfo               = null;
    String                            userName                 = System.getProperty("user.name");
    ErrorLocator                      errorLocator             = new ErrorLocator();

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.prepareProxyInfoForDB(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName1");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setProfileId("" + 1);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
	public void tearDown() throws Exception
    {
        super.tearDown();

        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_001()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        IErrorLocator errorLocator = database.getErrorLocator();
        assertNotNull(errorLocator);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_002()
    {
        int textStartOffset =
                errorLocator.textStartOffset("select * from public.mysample", "select * from public.mysample;", 0);
        assertEquals(0, textStartOffset);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_003()
    {
        int textEndOffset =
                errorLocator.textEndOffset("select * from public.mysample", "select * from public.mysample;", 0);
        assertEquals(29, textEndOffset);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_004()
    {
        MPPDBIDEException exception = new MPPDBIDEException(
                "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist Position: 65",
                new Exception("SQL Error Code = 42P01 ERROR: relation public.mysample does not exist Position: 65"));
        int errorPosition = errorLocator.errorPosition("select * from public.mysample", 0, 32, 1, 4, exception);
        assertEquals(29, errorPosition);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_005()
    {
        String errorMessage = errorLocator.errorMessage("select * from public.mysample;", 10, 5, 10);
        assertEquals("f", errorMessage);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_006()
    {
        MPPDBIDEException exception = new MPPDBIDEException(
                "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist Position: 65",
                new Exception("SQL Error Code = 42P01 ERROR: relation public.mysample does not exist Position: 65"));
        String serverErrorMessage = errorLocator.serverErrorMessage(exception);
        assertEquals(serverErrorMessage, exception.getServerMessage());
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_007()
    {
        int errorLineNumber = errorLocator.errorLineNumber(0, 4);
        assertEquals(4, errorLineNumber);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_008()
    {
        MPPDBIDEException exception = new MPPDBIDEException(
                "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist \n Position: 65 \n",
                new Exception(
                        "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist \n Position: 65 \n"));
        int errorPosition = errorLocator.errorPosition("select + \n" + "* +" + "from +" + "public.mysample", 0, 32, 1,
                4, exception);
        assertEquals(34, errorPosition);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_009()
    {
        int textStartOffset = errorLocator.textStartOffset("select+ \n" + " * +" + "from+" + " public.mysample",
                "select * from public.mysample;", 1);
        assertEquals(-1, textStartOffset);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_010()
    {
        String errorMessage = errorLocator.errorMessage("select * from public.\" \"mysample;", 22, 15, 31);
        assertEquals("\"", errorMessage);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_011()
    {
        if (CommonLLTUtils.isWindows())
        {
            String errorMessage = errorLocator.errorMessage("se\r\nlect  * from public. \n mysample;", 1, 0, 7);
            assertEquals("se", errorMessage);
        }
        else
        {
            String errorMessage = errorLocator.errorMessage("se\nlect  * from public. \n mysample;", 1, 0, 7);
            assertEquals("se", errorMessage);
        }
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_012()
    {
        String errorMessage = errorLocator.errorMessage("se;lect  * from public. \n mysample;", 1, 0, 7);
        assertEquals("se", errorMessage);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_013()
    {
        MPPDBIDEException exception = new MPPDBIDEException(" ", new Throwable());
        int errorPosition = errorLocator.errorPosition("select + \n" + "* +" + "from +" + "public.mysample", 0, 32, 1,
                4, exception);
        assertEquals(33, errorPosition);
    }

    @Test
    public void testTTA_BL_ERRORLOC_FUNC_014()
    {
        MPPDBIDEException exception = new MPPDBIDEException(
                "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist \n Position: test \n",
                new Exception(
                        "SQL Error Code = 42P01 ERROR: relation public.mysample does not exist \n Position: test \n"));
        int errorPosition = errorLocator.errorPosition("select + \n" + "* +" + "from +" + "public.mysample", 0, 32, 1,
                4, exception);
        assertEquals(33, errorPosition);
    }

}
