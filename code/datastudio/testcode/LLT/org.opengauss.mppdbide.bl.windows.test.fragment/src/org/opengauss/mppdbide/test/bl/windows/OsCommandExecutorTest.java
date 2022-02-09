package org.opengauss.mppdbide.test.bl.windows;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntryPermission;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.mock.bl.windows.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.windows.DBMSDriverManagerHelper;
import org.opengauss.mppdbide.mock.bl.windows.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.OsCommandExecutor;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.files.SetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class OsCommandExecutorTest extends BasicJDBCTestCaseAdapter {
    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;
    static int count = 0;
    boolean fileExists;
    private Database database;
    private Path newPath;
    private String absPath = "";
    private StringBuilder contain;
    private int processTimeout = MPPDBIDEConstants.PROCESS_TIMEOUT;
    private static final Set<AclEntryPermission> DEFAULT_PERMISSIONS = EnumSet.of(AclEntryPermission.DELETE,
            AclEntryPermission.DELETE_CHILD,

            AclEntryPermission.WRITE_DATA, AclEntryPermission.WRITE_ATTRIBUTES, AclEntryPermission.WRITE_NAMED_ATTRS,
            AclEntryPermission.APPEND_DATA

    );
    private ISetFilePermission filePermissionInstance;
    private String scriptName = null;

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        scriptName = CommonLLTUtils.isLinux() ? "pg_dump.bat" : "pg_dump.sh";
        filePermissionInstance = FilePermissionFactory.getFilePermissionInstance();
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);

        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName3");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
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

        filePermissionInstance.createFileWithPermission("Test", true, null, true);
        filePermissionInstance.createFileWithPermission("tools" + count, true, null, true);
        newPath = Paths.get("." + File.separator + "tools" + count + File.separator + scriptName);
        Path newPath1 = Paths.get("." + File.separator + "tools" + count);
        absPath = newPath1.toAbsolutePath().toString();

        fileExists = Files.exists(newPath);
        if (!fileExists) {
            filePermissionInstance.createFileWithPermission(newPath.toString(), false, null, true);
        }
        contain = new StringBuilder();
        contain.append("@echo off");
        contain.append(MPPDBIDEConstants.LINE_SEPARATOR);
        contain.append("echo %* >>dump.txt");

        Files.write(newPath, String.valueOf(contain).getBytes());

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
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
        fileExists = Files.exists(newPath);
        try {
            if (fileExists) {
                String cmd = "cmd /C rmdir /s /q \"" + absPath + "\"";
                System.out.println(cmd);
                Runtime.getRuntime().exec(cmd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        count++;

    }

    @Test
    public void test_start_001() {
        try {

            String cmd = "." + File.separator + "tools" + count + File.separator + scriptName;
            String[] cmds = new String[1];
            cmds[0] = cmd;

            OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

            os.start("".toCharArray());

            assertTrue(os != null);

        } catch (Exception e) {

            fail("not expected");

        }

    }

    @Test
    public void test_start_001_1() {
        try {

            String cmd = "." + File.separator + "tools" + count + File.separator + scriptName;
            String[] cmds = new String[1];
            cmds[0] = cmd;

            OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

            os.start("".toCharArray());

            assertTrue(os != null);

        } catch (Exception e) {

            fail("not expected");

        }

    }

    @Test
    public void test_start_002() {
        try {

            String cmd = "." + File.separator + "tools" + count + File.separator + scriptName;
            String[] cmds = new String[1];
            cmds[0] = cmd;

            OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

            os.start("".toCharArray());

            assertEquals(true, os.isFinished());

        } catch (Exception e) {
            fail("not expected");
        }
    }

    // test case when the file created by the batch file does not have the write
    // permission
    @Test
    public void test_start_003() {
        try {

            String cmd = "." + File.separator + "tools" + count + File.separator + scriptName;
            String[] cmds = new String[1];
            cmds[0] = cmd;

            OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

            os.start("".toCharArray());

        } catch (Exception e) {

            assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED),
                    e.getMessage());
        }

    }

    @Test
    public void test_start_004() {
        try {

            String cmd = "." + File.separator + "tools" + count + File.separator + scriptName;
            String[] cmds = new String[1];
            cmds[0] = cmd;

            OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

            os.isCancel();
            os.cancel();

            os.start("".toCharArray());

        } catch (Exception e) {

            assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CANCEL_ON_USER_REQUEST),
                    e.getMessage());
        }

    }

    @Test
    public void test_export() {
        try {

            Namespace namespace = database.getNameSpaceById(1);
            // TypeMetaData type=new TypeMetaData(1, "myView1", namespace);
            ViewMetaData viewMetaData = new ViewMetaData(2, "anything", namespace, database);

            ArrayList<ServerObject> objlist = new ArrayList<ServerObject>(1);
            objlist.add(viewMetaData);

        } catch (DatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Test
    public void test_export_FAIL_FILE_NOT_FOUND() {
        try {
            Namespace namespace = database.getNameSpaceById(1);
            ArrayList<ViewMetaData> views = namespace.getViewGroup().getSortedServerObjectList();
            ViewMetaData viewMetaData = new ViewMetaData(2, "anything", namespace, database);// views.get(0);
            ArrayList<ServerObject> objlist = new ArrayList<ServerObject>(1);
            objlist.add(viewMetaData);

        } catch (DatabaseOperationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public String convertToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String em : list) {
            builder.append(em);
            builder.append(" ");
        }
        return builder.toString();
    }

    @Test
    // test case when the tools folder does not have read and execute permission
    // file permission added for file
    public void test_start_005() {
        if (CommonLLTUtils.isWindows()) {
            try {

                Files.deleteIfExists(Paths.get("./tools" + count + "/pg_dump1.bat"));
                ISetFilePermission permission = new SetFilePermission();
                permission.createFileWithPermission("./tools" + count + "/pg_dump1.bat", false, DEFAULT_PERMISSIONS,
                        true);

                String cmd = "./tools" + count + "/pg_dump1.bat";
                String[] cmds = new String[1];
                cmds[0] = cmd;

                OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

                os.start("".toCharArray());

                fail("not expected");

            } catch (Exception e) {
                assertEquals(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_DISK_WRITE_ERROR),
                        e.getMessage());
            }
        }

    }

    @Test
    public void test_start_006() throws InterruptedException {
        String cmd = "./tools" + count + "/pg_dump.bat";
        Process process;
        String[] cmds = new String[1];
        cmds[0] = cmd;

        OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

        boolean a = false;
        try {
            process = Runtime.getRuntime().exec(cmd);
            os.start("".toCharArray());

            a = os.isCancel();
        } catch (Exception e) {

            assertTrue(true);
        }

    }

    @Test
    public void test_start_007() throws InterruptedException {
        String cmd = "./tools" + count + "/pg_dump.bat";
        Process process;
        String[] cmds = new String[1];
        cmds[0] = cmd;

        OsCommandExecutor os = new OsCommandExecutor(cmds, null, null, processTimeout);

        boolean a = false;
        try {
            process = Runtime.getRuntime().exec(cmd);
            os.isCancel();
            os.isFinished();
            os.getErrorMessage();
            os.cancel();
            os.start("".toCharArray());

            a = os.isCancel();
        } catch (Exception e) {

            assertTrue(true);
        }

    }
}
