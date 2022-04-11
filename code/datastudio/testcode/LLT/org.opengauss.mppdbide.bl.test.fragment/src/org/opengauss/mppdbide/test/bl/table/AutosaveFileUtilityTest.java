package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.autosave.AutoSaveDbgObjInfo;
import org.opengauss.mppdbide.bl.autosave.AutoSaveInfo;
import org.opengauss.mppdbide.bl.autosave.AutoSaveMetadata;
import org.opengauss.mppdbide.bl.autosave.AutosaveFileUtility;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class AutosaveFileUtilityTest extends BasicJDBCTestCaseAdapter
{
    
    
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    DBConnProfCache                   connProfCache            = null;
    ConnectionProfileId               profileId                = null;
    ServerConnectionInfo              serverInfo               = null;
    String                            userName                 = System.getProperty("user.name");
    List<AutoSaveMetadata>            metaDataList             = new ArrayList<AutoSaveMetadata>();
    AutoSaveMetadata                  metaDataTerminal         = new AutoSaveMetadata();
    AutoSaveMetadata                  metaDataDbg              = new AutoSaveMetadata();
    AutosaveFileUtility               fileUtility              = new AutosaveFileUtility();
    AutoSaveInfo                      autosaveInfo             = new AutoSaveInfo();
    AutoSaveDbgObjInfo                autosaveDbgObj           = new AutoSaveDbgObjInfo();

         

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
       // SecureUtil.setPackagePath(".");
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");

        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
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
        serverInfo.setProfileId(""+1);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
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
        }

        connProfCache.closeAllNodes();

    }
    
    public AutoSaveInfo getAutoSaveInfo()
    {
        autosaveInfo.setActiveTerminalName("test@postgres");
        assertEquals(autosaveInfo.getActiveTerminalName(), "test@postgres");
        autosaveInfo.setAutosaveMD(getMetaDataList());
        autosaveInfo.setTimestamp(new Date().toString());
        String timestamp = autosaveInfo.getTimestamp();
        assertEquals(autosaveInfo.getTimestamp(), timestamp);
        autosaveInfo.setVersion("1.0.0");
        assertEquals(autosaveInfo.getVersion(), "1.0.0");
        return autosaveInfo;
    }
    
    public List<AutoSaveMetadata> getMetaDataList()
    {
        metaDataList.add(getMetaDataForTerminal());
        metaDataList.add(getMetaDataForDbgObj());
        return metaDataList;
    }
    
    public AutoSaveMetadata getMetaDataForTerminal()
    {
        metaDataTerminal.setConnectionName("test");
        assertEquals(metaDataTerminal.getConnectionName(),"test");
        metaDataTerminal.setDatabaseName("postgres");
        assertEquals(metaDataTerminal.getDatabaseName(),"postgres");
        metaDataTerminal.setDbgObjInfo(null);
        metaDataTerminal.setEncoding("UTF-8");
        assertEquals(metaDataTerminal.getEncoding(),"UTF-8");
        metaDataTerminal.setEncrypted(false);
        metaDataTerminal.setAutoSaveFileName("autoSaveFile_001");
        assertEquals(metaDataTerminal.getAutoSaveFileName(),"autoSaveFile_001");
        metaDataTerminal.setTabID("test@postgres(1)");
        assertEquals(metaDataTerminal.getTabID(),"test@postgres(1)");
        metaDataTerminal.setTabLabel("test@postgres(1)");
        assertEquals(metaDataTerminal.getTabLabel(),"test@postgres(1)");
        metaDataTerminal.setTabToolTip("test : postgres");
        assertEquals(metaDataTerminal.getTabToolTip(),"test : postgres");
        metaDataTerminal.setTimestamp(new Date().toString());
        String date = metaDataTerminal.getTimestamp();
        assertEquals(metaDataTerminal.getTimestamp(), date);
        metaDataTerminal.setType("SQL_TERMINAL");
        assertEquals(metaDataTerminal.getType(), "SQL_TERMINAL");
        metaDataTerminal.setVersionNumber(0);
        assertEquals(metaDataTerminal.getVersionNumber(), 0);
        return metaDataTerminal;
    }
    
    public AutoSaveMetadata getMetaDataForDbgObj()
    {
        metaDataDbg.setConnectionName("DS_TEST");
        metaDataDbg.setDatabaseName("postgres");
        metaDataDbg.setDbgObjInfo(null);
        metaDataDbg.setEncoding("UTF-8");
        metaDataDbg.setEncrypted(false);
        metaDataDbg.setAutoSaveFileName("autoSaveFile_002");
        metaDataDbg.setTabID("func_moon()");
        metaDataDbg.setTabLabel("func_moon()");
        metaDataDbg.setTabToolTip("DS_TEST:postgres");
        metaDataDbg.setTimestamp(new Date().toString());
        metaDataDbg.setType("PLSQLEDITOR");
        metaDataDbg.setVersionNumber(0);
        metaDataDbg.setDbgObjInfo(getAutosaveDbgInfo());
        return metaDataDbg;
    }
    
    public AutoSaveDbgObjInfo getAutosaveDbgInfo()
    {
        autosaveDbgObj.setDirty(true);
        assertEquals(autosaveDbgObj.isDirty(), true);
        autosaveDbgObj.setName("func_moon()");
        assertEquals(autosaveDbgObj.getName(),"func_moon()");
        autosaveDbgObj.setObjType(OBJECTTYPE.PLSQLFUNCTION);
        autosaveDbgObj.setOid(1134);
        assertEquals(autosaveDbgObj.getOid(),1134);
        autosaveDbgObj.setSchemaName("public");
        assertEquals(autosaveDbgObj.getSchemaName(), "public");
        return autosaveDbgObj;
    }
    
    public void prepareAutoSaveFolder()
    {
        AutoSaveInfo info = getAutoSaveInfo();
        try
        {
            fileUtility.setOsCurrentUserFolderPath(".");
            fileUtility.createFolderStructure();
            Path backup = Paths.get(fileUtility.getAutosaveFolderPath().toString(), "autosave.info");
            boolean fileExists = Files.exists(backup);
            if (!fileExists) {
              Files.createFile(backup);
            }
            FileOutputStream fileWriter = new FileOutputStream(backup.toFile());
            BufferedOutputStream writer = new BufferedOutputStream(fileWriter);
            String text = "{\"version\":\"1.0.0\",\"timestamp\":\"Thu Dec 26 20:52:36 IST 2019\",\"activeterminal\":\"Gauss@test123 (2)\",\"tabinfo\":[{\"database\":\"Gauss\",\"connection\":\"test123\",\"terminaltype\":\"SQL_TERMINAL\",\"encrypted\":true,\"encoding\":\"UTF-8\",\"filename\":\"tabinfo15768472467751.autosave\",\"id\":\"Gauss@test123 (1)\",\"label\":\"Gauss@test123 (1)\",\"tooltip\":\"Gauss@test123 (1)\",\"timestamp\":\"Thu Dec 26 20:52:36 IST 2019\",\"versionNumber\":0,\"Verifier\":[3,-63,76,-83,-126,-69,-49,-127,-97,-11,107,78,107,-53,89,80,-119,-113,-11,40,-119,80,-101,75,40,-32,113,-63,-97,45,68,44]}]}";
            byte[] writeBytes = null;
            String encoding = metaDataTerminal.getEncoding();
            if (text.length() != 0) {
                writeBytes = text.getBytes(encoding);
                writer.write(writeBytes);
                writer.flush();
                writer.close();
            }
            fileUtility.getAutosaveInfo("autosave.info", 100);
           // fileUtility.deleteFolderStructure();
        }
        catch (FileOperationException | DatabaseOperationException | IOException e)
        {
            e.printStackTrace();
        }
    }
    
    @Test
    public void test_writeReadFile_Terminal_001()
    {
        try
        {
            prepareAutoSaveFolder();
            byte[] writeBytes = null;
            FileOutputStream saveAutosaveContent = fileUtility.getFileOutputStream(metaDataTerminal);
            BufferedOutputStream writer = new BufferedOutputStream(saveAutosaveContent); 
            String text = "Select * from version";
            String encoding = metaDataTerminal.getEncoding();
            if (text.length() != 0)
            {
                writeBytes = text.getBytes(encoding);
                writer.write(writeBytes);
                writer.flush();
                writer.close();
            }
            byte[] readBytes = fileUtility.read(metaDataTerminal.getAutoSaveFileName(),100);
            String writeData = new String(writeBytes, encoding);
            String readData = new String(readBytes, encoding);
            assertEquals(writeData, readData);
            boolean validFileTerminal = fileUtility.isValidFile(metaDataTerminal.getAutoSaveFileName(), 50000);
            assertTrue(validFileTerminal);
            fileUtility.deleteFolderStructureIfEmpty();
        }
        catch (FileOperationException e)
        {
            System.out.println("Not expected to come here");
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println("Not expected to come here");
        }
        catch (IOException e)
        {
            System.out.println("Not expected to come here");
        }
    }
    
    @Test
    public void test_writeReadFile_Editor_002()
    {
        try
        {
            prepareAutoSaveFolder();
            byte[] writeBytes = null;
            FileOutputStream saveAutosaveContent = fileUtility.getFileOutputStream(metaDataDbg);
            BufferedOutputStream writer = new BufferedOutputStream(saveAutosaveContent);
            String text = "CREATE OR REPLACE FUNCTION public.moon()"
                          +"RETURNS integer"
                          +"LANGUAGE plpgsql"
                          +"FENCED"
                          +"AS $$"
                          +"declare"
                          +"m int;"
                          +"begin"
                          +"m := 4;"
                          +"m := m-1;"
                          +"return m;"
                          +"end $$"
                          +"/";
            String encoding = metaDataDbg.getEncoding();
            if (text.length() != 0)
            {
                writeBytes = text.getBytes(encoding);
                writer.write(writeBytes);
                writer.flush();
                writer.close();
            }
            byte[] readBytes = fileUtility.read(metaDataDbg.getAutoSaveFileName(),100);
            String writeData = new String(writeBytes, encoding);
            String readData = new String(readBytes, encoding);
            assertEquals(writeData, readData);
            boolean validFileDbg = fileUtility.isValidFile(metaDataDbg.getAutoSaveFileName(),50000);
            assertTrue(validFileDbg);
        }
        catch (FileOperationException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
    }
    
    
    @Test
    public void test_saveAutosaveInfoTerminal_003()
    {
    	 AutoSaveInfo info = getAutoSaveInfo();
         fileUtility.setOsCurrentUserFolderPath(".");
         
        try
        {
            fileUtility.saveAutosaveInfo(autosaveInfo, metaDataTerminal.getAutoSaveFileName(), "autosaveFile_temp");
            metaDataTerminal.updateShaval();
            info.removeAutoSaveMetadata(metaDataTerminal);
            info.invalidate();
            info.getAutosaveMD();
            
        }
        catch (FileOperationException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
    }
    
    @Test
    public void test_saveAutosaveInfoEditor_004()
    {
       prepareAutoSaveFolder();
        try
        {
            fileUtility.saveAutosaveInfo(autosaveInfo, metaDataDbg.getAutoSaveFileName(), "autosaveFile_temp");
            String id = metaDataDbg.getTabID();
            autosaveInfo.getMetaData(id);
            assertNull(autosaveInfo.getMetaData("1"));
            boolean encrypt = metaDataDbg.isEncrypted();
            assertEquals(encrypt, metaDataDbg.isEncrypted());
            metaDataDbg.getDbgObjInfo();
            boolean val = metaDataDbg.calcAndCompare();
            assertEquals(val, metaDataDbg.calcAndCompare());
            int hashCode = metaDataDbg.hashCode();
        }
        catch (FileOperationException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
    }
    
    
    @Test
    public void test_deleteFile_005()
    {
        prepareAutoSaveFolder();
        fileUtility.deleteFile(metaDataTerminal.getAutoSaveFileName());
        fileUtility.deleteFile(metaDataDbg.getAutoSaveFileName());
        Path terminalPath = Paths.get(fileUtility.getAutosaveFolderPath().toString(), metaDataTerminal.getAutoSaveFileName());
        Path editorPath = Paths.get(fileUtility.getAutosaveFolderPath().toString(), metaDataDbg.getAutoSaveFileName());
        assertEquals(false, Files.exists(terminalPath));
        assertEquals(false, Files.exists(editorPath));
    }
    
    @Test
    public void test_deleteFolder_006()
    {
        prepareAutoSaveFolder();
        try
        {
            fileUtility.deleteFolderStructure();
        }
        catch (FileOperationException e)
        {
            e.printStackTrace();
            System.out.println("Not expected to come here");
        }
    }
    
}
