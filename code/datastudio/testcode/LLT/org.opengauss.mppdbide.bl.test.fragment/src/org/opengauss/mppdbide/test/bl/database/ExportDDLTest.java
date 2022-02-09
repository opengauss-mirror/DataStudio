package org.opengauss.mppdbide.test.bl.database;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.keywordssyntax.Keywords;
import org.opengauss.mppdbide.adapter.keywordssyntax.KeywordsFactoryProvider;
import org.opengauss.mppdbide.adapter.keywordssyntax.KeywordsToTrieConverter;
import org.opengauss.mppdbide.adapter.keywordssyntax.SQLSyntax;
import org.opengauss.mppdbide.bl.export.BatchExportDDLFilter;
import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.export.ExportManager;
import org.opengauss.mppdbide.bl.export.ExportParameters;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
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
    public void setUp() throws Exception {
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
    public void tearDown() throws Exception {
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
