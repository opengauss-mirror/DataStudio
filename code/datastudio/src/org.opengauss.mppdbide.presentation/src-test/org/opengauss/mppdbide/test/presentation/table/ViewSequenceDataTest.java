package org.opengauss.mppdbide.test.presentation.table;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.IViewTableDataCore;
import org.opengauss.mppdbide.presentation.IWindowDetail;
import org.opengauss.mppdbide.presentation.ViewSequenceCore;
import org.opengauss.mppdbide.presentation.ViewTableData;
import org.opengauss.mppdbide.presentation.ViewTableDataCore;
import org.opengauss.mppdbide.presentation.ViewTableSequenceDataCore;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
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
    
   @Test
    public void test_queryRelated001() {
        Database database = connProfCache.getDbForProfileId(profileId);
        TableMetaData tablemetaData;
        try {
            tablemetaData = new TableMetaData(1, "MyTable", database.getNameSpaceById(1), "tablespace");
            IViewTableDataCore core = new ViewTableSequenceDataCore();
            core.init(tablemetaData);
            core.getWindowDetails().getShortTitle();
            core.getServerObject();
            core.getWindowDetails().isCloseable();
            assertNotNull(core.getQuery());            
        } catch (DatabaseOperationException e) {
            fail("not expeted");
        }
       
   }

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

    @Test
    public void test_viewcore_02() {
        Database database = connProfCache.getDbForProfileId(profileId);
        try {
            SequenceMetadata seqData = null;
            seqData = new SequenceMetadata(1, "seq001", database.getNameSpaceById(1));
            seqData.setTableName("MyTable");
            seqData.setSequenceName("seq001");
            seqData.setSchemaName("pubilc");
            seqData.setMinValue("1");
            seqData.setMaxValue("9223372036854775807");
            seqData.setIncrementBy("1");
            seqData.setColumnName("id");

            MockResultSet sequenceset = preparedstatementHandler.createResultSet();
            sequenceset.addColumn("sequenceName");
            sequenceset.addColumn("sequenceuser");
            sequenceset.addColumn("minValue");
            sequenceset.addColumn("maxValue");
            sequenceset.addColumn("increment");
            sequenceset.addColumn("columnName");
            sequenceset.addColumn("tableuser");
            sequenceset.addColumn("tableName");
            sequenceset.addRow(new Object[] {"seq001", "public", 1, "9223372036854775807", 1, "id", "tpcc", "MyTable"});
            String tableBySequenceSql = " SELECT seq.sequence_name as sequenceName, seq.sequence_schema as sequenceuser,"
                    + " seq.minimum_value as minValue,seq.maximum_value as maxValue , seq.increment as increment, tc.attname as columnName, tu.rolname as tableuser"
                    + " , tb.relname as tableName FROM information_schema.sequences seq, pg_namespace sch, pg_class scl, pg_depend sdp"
                    + " , pg_attrdef sc, pg_attribute tc, pg_class tb, pg_roles tu"
                    + " WHERE seq.sequence_schema = ? AND seq.sequence_name = ? AND sch.nspname = seq.sequence_schema"
                    + " AND scl.relnamespace = sch.oid AND scl.relname = seq.sequence_name AND scl.relkind = 'S' AND sdp.refobjid = scl.oid "
                    + " AND sc.oid = sdp.objid AND tc.attrelid = sc.adrelid AND tc.attnum = sc.adnum AND tb.oid = tc.attrelid"
                    + " AND tu.oid = tb.relowner;";
            preparedstatementHandler.prepareResultSet(tableBySequenceSql, sequenceset);

            ViewSequenceCore viewSeqCore = new ViewSequenceCore();
            viewSeqCore.init(seqData);
            assertNotNull(viewSeqCore.getServerObject());
            assertNotNull(viewSeqCore.getProgressBarLabel());
            assertNotNull(viewSeqCore.getWindowDetails());
            String result = viewSeqCore.getQuery();
            if (!result.contains("com.mockrunner.mock.jdbc.MockPreparedStatement")) {
                assertEquals(result, tableBySequenceSql);
            } else {
                assertEquals(tableBySequenceSql, ViewSequenceCore.getTableBySequenceSql());
            }
            String query = tableBySequenceSql;
            try {
                DBConnection conn = database.getConnectionManager().getFreeConnection();
                ResultSet rs = conn.execSelectAndReturnRs(query);
                while (rs.next()) {
                    for (int i = 0 ; i < 8; i++) {
                        System.out.print(String.format(Locale.ENGLISH, "col%d: %10s  ", i, rs.getString(i + 1)));
                    }
                    System.out.println("");
                }
            } catch (Exception e) {
                fail("some error occur");
            }
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
        }
    }
}
