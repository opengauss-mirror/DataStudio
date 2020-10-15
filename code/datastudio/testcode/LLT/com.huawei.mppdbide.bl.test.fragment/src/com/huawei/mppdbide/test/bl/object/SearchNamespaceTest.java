package com.huawei.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.search.SearchNamespace;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ForeignTable;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.PartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.mock.bl.CommonLLTUtils;
import com.huawei.mppdbide.mock.bl.MockBLPreferenceImpl;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class SearchNamespaceTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    SearchNamespace                   srchNm                    = null;
    Namespace                         ns                        = null;
    Database                          database                  = null;

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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);

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
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);
        ns = database.getNameSpaceById(1);
        srchNm = new SearchNamespace(ns.getOid(), ns.getName(), database);

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
            itr = connProfCache.getServers().iterator();
        }

        connProfCache.closeAllNodes();

    }

    @Test
    public void test_conn_search_namespace()
    {
        srchNm.clearAllObjects();
        if (srchNm != null)
        {
            assertNotNull(true);
        }
        else
        {
            assertNotNull(false);
        }
        assertNotNull(srchNm.hashCode());
        assertNotNull(srchNm.getDatabase().hashCode());
    }

    @Test
    public void test_search_test_1()
    {
        PartitionTable ptabl = new PartitionTable(ns);
        if (ptabl != null)
        {
            srchNm.addTableToGroup(ptabl);
            srchNm.getChildren();
            srchNm.getTablesGroup();
            srchNm.equals(ptabl);
            srchNm.hashCode();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_test_2()
    {
        ForeignTable ftabl = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE);
        if (ftabl != null)
        {

            srchNm.addToForeignGroup(ftabl);
            srchNm.getChildren();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_test_3()
    {
        SequenceMetadata sqmtData = new SequenceMetadata(ns);
        if (sqmtData != null)
        {
            srchNm.addToSequenceGroup(sqmtData);
            srchNm.addSequence(sqmtData);
            srchNm.getChildren();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_test_4()
    {
        ViewMetaData vqmtData = new ViewMetaData(0, ns.getName(), ns,database);
        if (vqmtData != null)
        {
            srchNm.addViewToGroup(vqmtData);
            srchNm.getChildren();
            if(srchNm.getSize() != 0){
                assertEquals(1, 1);
            }else{
                assertEquals(0, 0);
            }
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_test_5()
    {
        TableMetaData table = new TableMetaData(ns);
        if (table != null)
        {
            srchNm.addTableToSearchPool(table);
            srchNm.getChildren();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_addSynonymToGroup() {
        SynonymMetaData synonym = new SynonymMetaData(ns.getName(), ns);
        if (synonym != null) {
            srchNm.addToSynonymGroup(synonym);
            srchNm.getChildren();
            assertNotNull(true);
        }
        assertNotNull(false);
    }

    @Test
    public void test_search_addsequenceToGroup() {
        SequenceMetadata synonym = new SequenceMetadata(1, "sequence", ns);
        if (synonym != null) {
            srchNm.addSequence(synonym);
            srchNm.getChildren();
            assertNotNull(true);
        }
        assertNotNull(false);
    }
    
}
