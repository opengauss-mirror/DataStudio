package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.adapter.gauss.GaussConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter.PARAMETERTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class GaussConnectionTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection               = null;
    PreparedStatementResultSetHandler preparedstatementHandler = null;
    StatementResultSetHandler         statementHandler         = null;
    
    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache connProfCache = null;
    ConnectionProfileId profileId = null;
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
        IBLPreference sysPref = new MockBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockBLPreferenceImpl.setDsEncoding("UTF-8");
        connection = new MockConnection();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());

        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();
        
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        
        connProfCache = DBConnProfCache.getInstance();
        JobCancelStatus status=new JobCancelStatus();
        status.setCancel(false);
        
        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
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
        
        while(itr.hasNext())
        {
            connProfCache.removeServer(itr.next().getId());
        }
        
        connProfCache.closeAllNodes();
        
    }
    
    @Test
    public void testTTA_BL_GAUSSCON_FUNC_001_001()
    {
        try
        {
            CommonLLTUtils.createTableRS(preparedstatementHandler);
            
            Database database = connProfCache.getDbForProfileId(profileId);
            
            GaussConnection connection = new GaussConnection(null);
            DebugObjects debugObject = new DebugObjects(1, "DebugObject", OBJECTTYPE.PLSQLFUNCTION, database);
            
            debugObject.setNamespace(database.getNameSpaceById(1));
            
            ArrayList<ObjectParameter> templateParameters = new ArrayList<ObjectParameter>();
            
            
            ObjectParameter objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("char");
            objectParameter1.setName("param1");
            objectParameter1.setValue("Val1");
            
            templateParameters.add(objectParameter1);
            
            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("text");
            objectParameter1.setName("param2");
            objectParameter1.setValue("null");
            templateParameters.add(objectParameter1);
            
            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("text");
            objectParameter1.setName("param3");
            objectParameter1.setValue("'text123'");
            templateParameters.add(objectParameter1);
            
            objectParameter1 = new ObjectParameter();
            objectParameter1.setType(PARAMETERTYPE.IN);
            objectParameter1.setDataType("<unknown>");
            objectParameter1.setName("param3");
            objectParameter1.setValue("'text123'");
            templateParameters.add(objectParameter1);
            
            debugObject.setTemplateParameters(templateParameters);
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }
    
}
