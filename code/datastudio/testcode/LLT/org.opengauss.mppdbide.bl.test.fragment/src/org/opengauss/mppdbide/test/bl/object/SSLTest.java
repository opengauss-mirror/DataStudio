package org.opengauss.mppdbide.test.bl.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.export.ExportManager;
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
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.DBMSDriverManagerHelper;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.SSLUtility;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class SSLTest extends BasicJDBCTestCaseAdapter
{

    MockConnection connection = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler statementHandler = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler estatementHandler = null;
    DBConnProfCache connProfCache = null;
   
    ConnectionProfileId profileIdSSL=null;
   
    ServerConnectionInfo sslServerInfo = null;
    static int count=0;
    boolean fileExists;
   
    private Database databaseSSL;
    private Path newPath;
    private String absPath = "";
    private StringBuilder contain;
    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
	public void setUp() throws Exception {
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

        connProfCache = DBConnProfCache.getInstance();

       
        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        sslServerInfo = new ServerConnectionInfo();
        sslServerInfo.setConectionName("SSLTestConnectionName");
        sslServerInfo.setServerIp("");
        sslServerInfo.setServerPort(5432);
        sslServerInfo.setDatabaseName("Gauss");
        sslServerInfo.setUsername("myusername");
        sslServerInfo.setSSLEnabled(true);
        sslServerInfo.setSSLMode( "require");
        sslServerInfo.setRootCertificate( "D:\\projects\\dstools\\secured_connection\\Latest_SSL_Files_Iteration3\\Latest_SSL_Files_Iteration3\\cacert.pem");
        sslServerInfo.setClientSSLCertificate("PGSSLCERT=" + "D:\\projects\\dstools\\secured_connection\\Latest_SSL_Files_Iteration3\\Latest_SSL_Files_Iteration3\\client.crt");
        sslServerInfo.setClientSSLKey("PGSSLKEY=" + "D:\\projects\\dstools\\secured_connection\\Latest_SSL_Files_Iteration3\\Latest_SSL_Files_Iteration3\\client.key");
       
        sslServerInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        sslServerInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(sslServerInfo);
       
        profileIdSSL=connProfCache.initConnectionProfile(sslServerInfo,status);
        // database.addNamespace(new Namespace(1, "pg_catalog", database));
      
        databaseSSL=connProfCache.getDbForProfileId(profileIdSSL);

        File dir = new File("Test");
        File dirtool = new File("tools");
        dir.mkdir();
        dirtool.mkdir();
        newPath = Paths.get("./tools/pg_dump.bat");
        Path newPath1 = Paths.get("./tools");
        absPath = newPath1.toAbsolutePath().toString();
        
        fileExists = Files.exists(newPath);
          //Files.deleteIfExists(Paths.get("./tools/pg_dump.bat"));
         // SetFilePermission permission = new SetFilePermission();
        //  newPath=permission.createFileWithPermission("./tools/pg_dump.bat", false, DEFAULT_PERMISSIONS, true);
        if (!fileExists) {
            newPath = Files.createFile(newPath);
        }
        contain = new StringBuilder();
        contain.append("@echo off");
        contain.append("\n");
        contain.append("echo %* >>dump.txt");

        Files.write(newPath, String.valueOf(contain).getBytes());
        
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
	public void tearDown() throws Exception {
        super.tearDown();

     
        databaseSSL=connProfCache.getDbForProfileId(profileIdSSL);
       
        databaseSSL.getServer().close();

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
        try
        {
            if (fileExists) {
                String cmd = "cmd /C rmdir /s /q \""+absPath+"\"";
                System.out.println(cmd);
                Runtime.getRuntime().exec(cmd);
            }   
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        count++;

    }
   
    
    @Test
    public void testTTA_getEnv() {

        try {
            ExportManager exm = new ExportManager();

            Namespace namespace = new Namespace(1, "schema", databaseSSL);
            databaseSSL.getUserNamespaceGroup().addToGroup((UserNamespace) namespace);
            sslServerInfo.setSSLEnabled(true);
            sslServerInfo.getClientSSLCertificate();
            sslServerInfo.getClientSSLKey();
            ArrayList<ServerObject> objlist = new ArrayList<ServerObject>(1);
            objlist.add(namespace);

            exm.exportSqlToFile("Test\\abc.txt", EXPORTTYPE.SQL_DDL, namespace, true, null);

        } catch (DataStudioSecurityException e) {
            // fail("fail");
        } catch (Exception e) {
            // fail("fail");
        }
    }

    @Test
    public void testTTA_removeSSLLoginStatus()
    {
        SSLUtility.putSSLLoginStatus("sslKey", true);
        assertTrue(SSLUtility.removeSSLLoginStatus("sslKey"));
        assertFalse(SSLUtility.removeSSLLoginStatus("ssl_key"));
    }

}
