package org.opengauss.mppdbide.test.bl.windows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.FileAttribute;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import org.opengauss.mppdbide.mock.bl.windows.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.windows.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.files.DSFileAttributes;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.files.SetFilePermission;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class CreateFilePermissionTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
    ServerConnectionInfo serverInfo = null;
    
    private static final Set<AclEntryPermission> DEFAULT_PERMISSIONS = EnumSet
            .of(AclEntryPermission.DELETE, 
                    AclEntryPermission.DELETE_CHILD, 

                    AclEntryPermission.WRITE_DATA, 
                    AclEntryPermission.WRITE_ATTRIBUTES, 
                    AclEntryPermission.WRITE_NAMED_ATTRS, 
                    AclEntryPermission.APPEND_DATA, 
                    AclEntryPermission.SYNCHRONIZE, 

                    AclEntryPermission.READ_ATTRIBUTES, 
                    AclEntryPermission.READ_DATA, 
                    AclEntryPermission.READ_NAMED_ATTRS, 

                    AclEntryPermission.READ_ACL 
            );
    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    protected void setUp() throws Exception
    {
        super.setUp();
        CommonLLTUtils.runLinuxFilePermissionInstance();
        MPPDBIDELoggerUtility.setArgs(null);
        connection = new MockConnection();
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        MockBLPreferenceImpl.setFileEncoding("UTF-8");
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
        connProfCache = DBConnProfCache.getInstance();
        
        serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        //serverInfo.setSslPassword("12345");
        //serverInfo.setServerType(DATABASETYPE.GAUSS);
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
    protected void tearDown() throws Exception
    {
        super.tearDown();
        
        Database database = connProfCache.getDbForProfileId(profileId);
        database.getServer().close();
        
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();
        
        Iterator<Server> itr = connProfCache.getServers().iterator();
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_01()
    {
        // Test for simple file
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("sample.txt"));
            Path filePath = permission.createFileWithPermission("sample.txt", false, null, true);
            Object attrib = Files.getAttribute(filePath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
            
            List<AclEntry> attributes = (List<AclEntry>) attrib;
            FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(null);
            List<AclEntry> dsDefaultAttributes = defaultAttrib.value();
            assertEquals(dsDefaultAttributes, attributes);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_011()
    {
        // Test for simple file
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("sample.txt"));
            Path filePath = permission.createFileWithPermission("sample.txt", false, null, false);
            Object attrib = Files.getAttribute(filePath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_012()
    {
        // Test for simple file
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("dummy"));
            Path filePath = permission.createFileWithPermission("dummy", true, null, true);
            Object attrib = Files.getAttribute(filePath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_013()
    {
        // Test for simple file
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("dummy"));
            Path filePath = permission.createFileWithPermission("dummy", true, null, false);
            Object attrib = Files.getAttribute(filePath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_02()
    {
        // Test for directory
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("sample"));
            Path dirPath = permission.createFileWithPermission("sample", true, null, true);
            
            Object attrib = Files.getAttribute(dirPath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
            
            List<AclEntry> attributes = (List<AclEntry>) attrib;
            FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(null);
            List<AclEntry> dsDefaultAttributes = defaultAttrib.value();
            assertEquals(dsDefaultAttributes, attributes);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
        
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_03()
    {
        // Test for directory with default permission
        if (CommonLLTUtils.isLinux())
        {
            return;
        }
        
        ISetFilePermission permission = new SetFilePermission();
        try
        {
            Files.deleteIfExists(Paths.get("sample"));
            Path dirPath = permission.createFileWithPermission("sample", true, DEFAULT_PERMISSIONS, true);
            Object attrib = Files.getAttribute(dirPath, "acl:acl");
            assertTrue(attrib instanceof List<?>);
            
            List<AclEntry> attributes = (List<AclEntry>) attrib;
            FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(null);
            List<AclEntry> dsDefaultAttributes = defaultAttrib.value();
            assertEquals(dsDefaultAttributes, attributes);
        }
        catch (IOException e)
        {
            
            e.printStackTrace();
            fail("not expected to come here.");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
            fail("not expected to come here.");
        }
        
    }
    
    @Test
    public void test_TTA_CRAETE_FILE_001_FUNC_001_04()
    {
        if (CommonLLTUtils.isWindows())
        {
            ISetFilePermission permission = new SetFilePermission();
            try
            {
                Files.deleteIfExists(Paths.get("sample.txt"));
                permission.createFileWithPermission("dd\\sample.txt", true, DEFAULT_PERMISSIONS, true);
    
                //fail("expected to throw exception");
            }
            catch (IOException e)
            {
                assertEquals("Setting file permission failed.", e.getMessage());
            }
            catch (DatabaseOperationException e)
            {
                assertEquals("Setting file permission failed.", e.getMessage());
            }
        }
        else
        {
            /* For Linux */
            ISetFilePermission filePermissionInstance = FilePermissionFactory.getFilePermissionInstance();
            try
            {
                Files.deleteIfExists(Paths.get("sample.txt"));
                filePermissionInstance.createFileWithPermission("dd\\sample.txt", true, DEFAULT_PERMISSIONS, true);
    
                //fail("expected to throw exception");
            }
            catch (IOException e)
            {
                assertEquals("Setting file permission failed.", e.getMessage());
            }
            catch (DatabaseOperationException e)
            {
                assertEquals("Setting file permission failed.", e.getMessage());
            }
        }
        
    }
    
    @Test
    public void test_TTA_CREATE_FILE_001_FUNC_001_05()
    {
        // Test to assert the non default valid permission set
        if (CommonLLTUtils.isWindows())
        {
            Set<AclEntryPermission> myFilePerm = EnumSet
                    .of(AclEntryPermission.READ_ATTRIBUTES, 
                            AclEntryPermission.READ_DATA, 
                            AclEntryPermission.READ_NAMED_ATTRS
                    );
            ISetFilePermission permission = new SetFilePermission();
            
            try
            {
                Files.deleteIfExists(Paths.get("sample.txt"));
                Path filePath = permission.createFileWithPermission("sample.txt", false, myFilePerm, true);
                Object attrib = Files.getAttribute(filePath, "acl:acl");
                assertTrue(attrib instanceof List<?>);
                
                List<AclEntry> attributes = (List<AclEntry>) attrib;
                FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(myFilePerm);
                List<AclEntry> dsDefaultAttributes = defaultAttrib.value();
                assertEquals(dsDefaultAttributes, attributes);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                fail("not expected to come here.");
            }
            catch (DatabaseOperationException e)
            {
                e.printStackTrace();
                fail("not expected to come here.");
            }
        }
    }
    
    @Test
    public void test_TTA_CREATE_FILE_001_FUNC_001_06()
    {
        // Test for empty permission set
        if (CommonLLTUtils.isWindows())
        {
            Set<AclEntryPermission> myFilePerm = new HashSet<AclEntryPermission>(); 
                    /*EnumSet
                    .of(AclEntryPermission.READ_ATTRIBUTES, 
                            AclEntryPermission.READ_DATA, 
                            AclEntryPermission.READ_NAMED_ATTRS
                    );*/
            ISetFilePermission permission = new SetFilePermission();
            
            try
            {
                Files.deleteIfExists(Paths.get("sample.txt"));
                Path filePath = permission.createFileWithPermission("sample.txt", false, myFilePerm, true);
                Object attrib = Files.getAttribute(filePath, "acl:acl");
                assertTrue(attrib instanceof List<?>);
                
                List<AclEntry> attributes = (List<AclEntry>) attrib;
                FileAttribute<List<AclEntry>> defaultAttrib = new DSFileAttributes(myFilePerm);
                List<AclEntry> dsDefaultAttributes = defaultAttrib.value();
                assertEquals(dsDefaultAttributes, attributes);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                fail("not expected to come here.");
            }
            catch (DatabaseOperationException e)
            {
                e.printStackTrace();
                fail("not expected to come here.");
            }
        }
    }
    
    @Test
    public void test_TTA_CREATE_FILE_001_FUNC_001_07()
    {
    	Set<AclEntryPermission> myFilePerm = new HashSet<AclEntryPermission>();
    	myFilePerm.add(AclEntryPermission.READ_DATA);
    	myFilePerm.add(AclEntryPermission.WRITE_DATA);
    	DSFileAttributes fileAttributes = new DSFileAttributes(myFilePerm);
    	assertEquals("acl:acl",fileAttributes.name());
    	assertNotNull(fileAttributes.value());
    }
}
