package com.huawei.mppdbide.test.presentation.table;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.mock.presentation.CommonLLTUtils;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.ViewSequenceCore;
import com.huawei.mppdbide.presentation.ViewTableData;
import com.huawei.mppdbide.presentation.ViewTableDataCore;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockDriver;
import com.mockrunner.mock.jdbc.MockPreparedStatement;
import com.mockrunner.mock.jdbc.MockResultSet;

import static org.junit.Assert.*;

public class ViewSequenceDataTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;

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
        MockDriver mockDriver = getJDBCMockObjectFactory().getMockDriver();
        mockDriver.setupConnection(connection);
        CommonLLTUtils.mockConnection(mockDriver);
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);

        profileId = connProfCache.initConnectionProfile(serverInfo,status);
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
    public void test_initializeCore()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);

        try
        {
            SequenceMetadata seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);

        }
        catch (DatabaseOperationException e)
        {
            
            fail("not expected");
        }
        catch (MPPDBIDEException e)
        {
            
            fail("not expected");
        }
    }

    

   
    

    public void test_getWindowDetails()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        SequenceMetadata seqData;
        try
        {
            seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);
            if (core.getWindowDetails() instanceof IWindowDetail)
            {
                assertTrue(true);
            }
            else
                fail("not expected");

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        
        catch (MPPDBIDEException e)
        {
            fail("not expected to come here");
        }
    }

    
    public void test_getTitle()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        SequenceMetadata seqData;
        try
        {
            seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);
            assertEquals(core.getWindowDetails().getTitle(), "pg_catalog.seq001-Gauss@TestConnectionName");

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected to come here");
        }

    }

    public void test_getUniqueID()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        SequenceMetadata seqData;
        try
        {
            seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);
            assertEquals(core.getWindowDetails().getUniqueID(),
                    "VIEW_TABLE_DATA_pg_catalog.seq001-Gauss@TestConnectionName");

        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }

    }

    public void test_getShortTitle()
    {

        Database database = connProfCache.getDbForProfileId(profileId);
        SequenceMetadata seqData;
        try
        {
            seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);
            core.getWindowDetails().getShortTitle();
            core.getTermConnection();
            core.getServerObject();
            core.getWindowDetails().isCloseable();
            assertEquals("pg_catalog.seq001",
                    core.getWindowDetails().getShortTitle());

        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }

    }

    

        public void test_cancelQuery()
    {
        Database database = connProfCache.getDbForProfileId(profileId);
        SequenceMetadata seqData;
        try
        {
            seqData = new SequenceMetadata(1, "seq001",
                    database.getNameSpaceById(1));
            IViewTableDataCore core = new ViewSequenceCore();
            core.init(seqData);
            assertTrue(true);
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected to come here");
        }
        catch (MPPDBIDEException e)
        {
            fail("not expected to come here");

        }
    }

    @Test
    public void test_getElapsedTime()
    {
        IExecTimer timer=new ExecTimer("test");
        timer.start();
        ViewTableData data= new ViewTableData();
        
        try
        {
            timer.stop();
            data.setElapsedTime(timer.getElapsedTime());
            assertNotNull(data.getElapsedTime());
        }
        catch (DatabaseOperationException e)
        {
           fail("not expeted");
        }
    }
    
   /*@Test
    public void test_queryRelated001() {
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try {
            tablemetaData = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableDataCore();
            core.init(tablemetaData);
            core.getWindowDetails().getShortTitle();
            core.getServerObject();
            core.getWindowDetails().isCloseable();
            String sequenceByTableSql =  "WITH temp_sql as (SELECT tu.rolname as tableuser"
                    + ",tb.relname as tableName,tc.attname as columnName,scl.relname,sch.nspname FROM "
                    + "pg_class  scl,pg_depend sdp,pg_attrdef sc,pg_attribute tc,pg_class tb,pg_roles tu"
                    + ",pg_namespace sch WHERE tu.rolname = pg_catalog and tb.relname = MyTable AND scl.relnamespace = sch.oid "
                    + "AND scl.relkind = 'S' AND sdp.refobjid = scl.oid AND sc.oid = sdp.objid AND tc.attrelid = sc.adrelid "
                    + "AND tc.attnum = sc.adnum AND tb.oid = tc.attrelid AND tu.oid = tb.relowner)"
                    + "select t.tableuser,t.tableName,t.columnName,decode(seq.sequence_name,t.relname,seq.sequence_name,'[No_Privilege]') as sequenceName"
                    +" ,decode(seq.sequence_name,t.relname,seq.sequence_schema,'[No_Privilege]') as sequenceuser " 
                    +" ,decode(seq.sequence_name,t.relname,seq.minimum_value,'[No_Privilege]') as minValue " 
                    +" ,decode(seq.sequence_name,t.relname,seq.maximum_value,'[No_Privilege]')  as maxValue " 
                    +" ,decode(seq.sequence_name,t.relname,seq.increment,'[No_Privilege]')  as increment "
                    + "from temp_sql t LEFT OUTER join information_schema.sequences seq on (t.nspname = seq.sequence_schema "
                    + "AND t.relname = seq.sequence_name )";

            assertEquals(sequenceByTableSql, core.getRelatedQuery());
        } catch (DatabaseOperationException e) {
            fail("not expeted");
        }
       
   }*/
    
    @Test
    public void test_viewcore_01() {
        Database database = connProfCache.getDbForProfileId(profileId);
        CommonLLTUtils.addViewTableData(preparedstatementHandler);
        try {
            SequenceMetadata seqData = null;
            seqData = new SequenceMetadata(1, "seq001", database.getNameSpaceById(1));
            ViewSequenceCore viewSeqCore = new ViewSequenceCore();
            viewSeqCore.init(seqData);
            assertNotNull(viewSeqCore.getServerObject());
            assertNotNull(viewSeqCore.getProgressBarLabel());
            assertNotNull(viewSeqCore.getWindowDetails());
            assertNotNull(viewSeqCore.getQuery());
            assertNotNull(viewSeqCore.getWindowTitle());
            assertNotNull(viewSeqCore.getWindowDetails().getShortTitle());
            assertNotNull(viewSeqCore.getWindowDetails().getTitle());
            assertNotNull(viewSeqCore.getWindowDetails().getUniqueID());
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
        }
    }
}
