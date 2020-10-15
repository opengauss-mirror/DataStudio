package com.huawei.mppdbide.test.bl.database;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.adapter.IConnectionDriver;
import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.keywordssyntax.Keywords;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsFactoryProvider;
import com.huawei.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.export.BatchExportDDLFilter;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.export.ExportManager;
import com.huawei.mppdbide.bl.export.ExportParameters;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.preferences.IBLPreference;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.mock.bl.CommonLLTUtils;
import com.huawei.mppdbide.mock.bl.MockBLPreferenceImpl;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DataStudioSecurityException;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExportDDLTest extends BasicJDBCTestCaseAdapter {
    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;
    private Database database;
    BatchExportDDLFilter filter = null;
    BufferedOutputStream bfs = null;
    String infile = "the-file-name.txt";
    String outfile = "output.txt";

    int processTimeout = MPPDBIDEConstants.PROCESS_TIMEOUT;

    @Before
    protected void setUp() throws Exception {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MockBLPreferenceImpl.setFileEncoding("UTF-8");

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.prepareShowDDLResultSet(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName3");
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
        // database.addNamespace(new Namespace(1, "pg_catalog", database));
        database = connProfCache.getDbForProfileId(profileId);

    }

    @After
    protected void tearDown() throws Exception {
        super.tearDown();

        database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext()) {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();
    }

    @Test
    public void testTTA_exportManagerTest_Sequence() {
        File dir = new File("Test");
        try {
            CommonLLTUtils.getOwnerId(statementHandler);
            CommonLLTUtils.getSequenceDDL(preparedstatementHandler);
            CommonLLTUtils.getNamespaceDDL(preparedstatementHandler,statementHandler);
            Namespace namespace = new Namespace(1, "schema", database);
            namespace.setLoaded();
            SequenceMetadata seq=new SequenceMetadata(1, "test", namespace);
            seq.getSeqNameSpace();
            String own = seq.getSequenceOwner();
            assertNull(own);
            boolean isDrop = seq.isTableDropped();
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if (!fileExists) {
                Files.createDirectory(exportFilePath);
            }
          
            ExportParameters exp = new ExportParameters("", "Test1", database, EXPORTTYPE.SQL_DDL,
                    seq, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFile(exp);
            seq.isExportAllowed(EXPORTTYPE.SQL_DDL);
            
            
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("Operation exception not expected");
        } catch (DatabaseCriticalException e) {
            fail("Critical exception not expected");
        } catch (DataStudioSecurityException e) {
            fail("Security exception not expected");
        } catch (IOException e) {
            fail("Security exception not expected");
        } catch (MPPDBIDEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            dir.delete();
        }
    }
    
    @Test
    public void testTTA_exportManager_DroppedTable_Sequence() {
        File dir = new File("Test");
        try {
            CommonLLTUtils.getOwnerId(statementHandler);
            CommonLLTUtils.getSequenceDDL(preparedstatementHandler);
            CommonLLTUtils.getNamespaceDDL(preparedstatementHandler,statementHandler);
            Namespace namespace = null;
            SequenceMetadata seq=new SequenceMetadata(1, "test", namespace);
            assertTrue(seq.isExportAllowed(EXPORTTYPE.SQL_DDL));
            assertTrue(seq.isExportAllowed(EXPORTTYPE.SQL_DDL_DATA));
            assertFalse(seq.isExportAllowed(EXPORTTYPE.SQL_DATA));
            assertTrue(seq.isTableDropped());
            
		} finally {
            dir.delete();
        }
    }
    

    @Test
    public void testTTA_exportManagerTest_Sequence1() {
        File dir = new File("Test");
        try {
            CommonLLTUtils.getOwnerId(statementHandler);
            CommonLLTUtils.getSequenceDDL(preparedstatementHandler);
            CommonLLTUtils.getNamespaceDDL(preparedstatementHandler,statementHandler);
            Namespace namespace = new Namespace(1, "schema", database);
            namespace.setLoaded();
            SequenceMetadata seq=new SequenceMetadata(1, "test", namespace);
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if (!fileExists) {
                Files.createDirectory(exportFilePath);
            }
          
            ExportParameters exp = new ExportParameters("", "Test1", database, EXPORTTYPE.SQL_DDL,
                    seq, true, dir);
            ExportManager exm = new ExportManager();
            
            exm.exportSqlToFiles(exp.getPath(), exp.getExportType(), exp.getServerObjList(),
                    exp.isTablespaceOption(), exp.getWorkingDir(), "");
        } catch (DatabaseOperationException e) {
            e.printStackTrace();
            fail("Operation exception not expected");
        } catch (DatabaseCriticalException e) {
            fail("Critical exception not expected");
        } catch (DataStudioSecurityException e) {
            fail("Security exception not expected");
        } catch (IOException e) {
            fail("Security exception not expected");
        } finally {
            dir.delete();
        }
    }
    

}
