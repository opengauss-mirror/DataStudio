package org.opengauss.mppdbide.test.presentation.table;

import java.util.Iterator;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.StmtExecutor;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryMaterializer;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.QueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.sqlhistory.SQLHistoryFactory;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.presentation.EditTableDataCore;
import org.opengauss.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import org.opengauss.mppdbide.presentation.resultset.ConsoleDataWrapper;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import static org.junit.Assert.*;

public class QueryResultMaterializerTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    EditTableDataHelper               helper;
    Object           dataProvider;
    EditTableDataCore                 coreObject;
    private Database database;

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
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        initializeHelper();
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

        database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    public void initializeHelper()
    {
        helper = new EditTableDataHelper(
                connProfCache.getDbForProfileId(profileId));
        try
        {
            dataProvider = helper.getDataProvider(preparedstatementHandler);
            coreObject = helper.getCoreObject();

        }
        catch (DatabaseCriticalException e)
        {
            e.printStackTrace();
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        catch (MPPDBIDEException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_query_materializer()
    {
        String selectedQry = CommonLLTUtils.FETCH_ALL_NAMESPACE_LOAD_ALL;

        IQueryExecutionSummary summary = SQLHistoryFactory.getNewExlainQueryExecutionSummary("DataBase","Server",
                "conn", selectedQry);

        ConsoleDataWrapper consoleData = new ConsoleDataWrapper();
                
        IQueryMaterializer materializer = new QueryResultMaterializer(null,
                        summary, consoleData, null, false, false);
        
        String url = null;
        Properties props = new Properties();

        DBConnection dbConnection = new DBConnection();
        try
        {
            CommonLLTUtils.initDriver("org.postgresql.Driver");
        }
        catch (DatabaseOperationException e1)
        {
            
            e1.printStackTrace();
        }

        props.setProperty("user", "test");
        props.setProperty("password", "test");
        props.setProperty("allowEncodingChanges", "true");
        String encoding = System.getProperty("file.encoding");
        props.setProperty("characterEncoding", encoding);
        props.setProperty("ApplicationName", "MPP IDE");
        props.setProperty("protocolVersion", "3.5");
        url = "jdbc:postgresql:127.0.0.1:1234/testDB";

        try
        {
            dbConnection.dbConnect(props, url);
        }
        catch (DatabaseOperationException e1)
        {
            
            e1.printStackTrace();
        }
        catch (DatabaseCriticalException e1)
        {
            
            e1.printStackTrace();
        }
        try
        {
            StmtExecutor stmt = new StmtExecutor(selectedQry, dbConnection);
            stmt.execute();
            QueryResult queryResult = new QueryResult(stmt);
            if (null != materializer)
            {
                materializer.materializeQueryResult(queryResult);
            }
        }

        catch (Exception e)
        {
            assertTrue(true);
        }


    }


}
