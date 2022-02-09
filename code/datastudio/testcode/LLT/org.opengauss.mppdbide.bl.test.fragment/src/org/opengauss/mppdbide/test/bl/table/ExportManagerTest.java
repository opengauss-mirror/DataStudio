package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DataStudioSecurityException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExportManagerTest extends BasicJDBCTestCaseAdapter
{
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    ServerConnectionInfo              serverInfo                = null;
    private Database                  database;
    BatchExportDDLFilter filter = null;
    BufferedOutputStream bfs = null;
    String infile = "the-file-name.txt";
    String outfile = "output.txt";

    final String[] LINESTOWRITE =
            {"PostgreSQL database dump", "Dumped from database version", "Dumped by gs_dump version",
                "SET statement_timeout", "SET client_encoding", "SET standard_conforming_strings",
                "SET check_function_bodies", "SET client_min_messages", "SET default_with_oids"};
    int processTimeout = MPPDBIDEConstants.PROCESS_TIMEOUT;

    @Before
	public void setUp() throws Exception
    {
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
        CommonLLTUtils.prepareShowTableDDLResultSet(preparedstatementHandler);

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
	public void tearDown() throws Exception
    {
        super.tearDown();

        database = connProfCache.getDbForProfileId(profileId);
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

    @Test
    public void testTTA_exportManagerTest_001()
    {
        final String parent = "D:/dstest/";
        final String path = "abc1.sql";
        ExportManager exporter = new ExportManager();
        Namespace ns;
        File file = new File(parent);
        file.setReadOnly();

        try
        {
            CommonLLTUtils.prepareShowDDLResultSet(preparedstatementHandler);
            ns = database.getNameSpaceById(1);
            ViewMetaData view = new ViewMetaData(1, "testView", ns,database);
            view.getNamespaceQualifiedName();
            assertTrue(view.isExportAllowed(EXPORTTYPE.SQL_DDL));
            assertFalse(view.isExportAllowed(EXPORTTYPE.SQL_DDL_DATA));
            assertFalse(view.isExportAllowed(EXPORTTYPE.SQL_DATA));
            view.getChildren();
            exporter.exportSqlToFile(path, EXPORTTYPE.SQL_DDL, view, true, file);

        }
        catch (DatabaseOperationException e)
        {
            assertTrue(e.getServerMessage().contains("Export process failed."));
            assertTrue(e.getServerMessage().contains(path.toUpperCase()));
        }
        catch (DatabaseCriticalException e)
        {
            fail("Critical exception not expected");
        }
        catch (DataStudioSecurityException e)
        {
            fail("Security exception not expected");
        }
        finally
        {
            file.delete();
        }
    }

    @Test
    public void testTTA_exportManagerTest_002()
    {
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("TempTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            ArrayList<ServerObject> objlist = new ArrayList<ServerObject>(1);
            objlist.add(tablemetaData);
            ExportManager exporter = new ExportManager();
            exporter.cancel();
            assertTrue(exporter != null);
        }
        catch (Exception e)
        {
            fail("Exception not expected here.");
        }
    }

    @Test
    public void testTTA_BL_exportSqlToFile_debug()
    {
        try
        {
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            CommonLLTUtils.getOwnerId(statementHandler);
            Namespace namespace = new Namespace(1, "schema", database);
           
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if(!fileExists) {
            Files.createDirectory(exportFilePath);}
            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL, debugObject, true, dir);
            ArrayList<ServerObject> obj = new ArrayList<ServerObject>();
            assertNotNull(exp.getDb());
            
            ExportParameters exp1 =
                    new ExportParameters("mypassword1", "Test2", database, EXPORTTYPE.SQL_DDL, obj, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFile(exp);
            assertEquals(exp1.getPassword(), "mypassword1");
            
        }

        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
            fail("fail");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("fail");
        }
    }
    
    @Test
    public void testTTA_BL_exportSqlToFile_debug1()
    {
        try
        {
            CommonLLTUtils.refreshSourceCodeRS(preparedstatementHandler);
            Namespace namespace = new Namespace(1, "schema", database);
            DebugObjects debugObject = new DebugObjects(1, "test",
                    OBJECTTYPE.PLSQLFUNCTION, database);
            debugObject.setNamespace(namespace);
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if(!fileExists) {
            Files.createDirectory(exportFilePath);}
            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL, debugObject, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFiles(exp);
        }

        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
            fail("fail");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("fail");
        }
    }
    
    @Test
    public void testTTA_exportManagerTest_0021()
    {
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("TempTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if(!fileExists) {
            Files.createDirectory(exportFilePath);}
            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL, tablemetaData, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFiles(exp);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception not expected here.");
        }
    }
    
    @Test
    public void testTTA_exportManagerTest_0011()
    {
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("TempTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if(!fileExists) {
            Files.createDirectory(exportFilePath);}
            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL, tablemetaData, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFiles(exp.getPath(), exp.getExportType(), exp.getServerObjList(),
                    exp.isTablespaceOption(), exp.getWorkingDir(), infile);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception not expected here.");
        }
    }
    
    @Test
    public void testTTA_exportManagerSQL_And_Data_Test_0011()
    {
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("TempTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");
            Path exportFilePath = Paths.get("Test" + File.separator).toAbsolutePath().normalize();
            boolean fileExists = Files.exists(exportFilePath);
            if(!fileExists) {
            Files.createDirectory(exportFilePath);}
            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL_DATA, tablemetaData, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFiles(exp.getPath(), exp.getExportType(), exp.getServerObjList(),
                    exp.isTablespaceOption(), exp.getWorkingDir(), infile);
        }
        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("Exception not expected here.");
        }
    }
    
    @Test
    public void testTTA_BL_exportSqlToFile()
    {
        try
        {
            TableMetaData tablemetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tablemetaData.setTempTable(true);
            tablemetaData.setIfExists(true);
            tablemetaData.setName("TempTable");
            tablemetaData.setHasOid(true);
            tablemetaData.setDistributeOptions("HASH");
            tablemetaData.setNodeOptions("Node1");
            tablemetaData.setDescription("Table description");

            File dir = new File("Test");
            ExportParameters exp =
                    new ExportParameters("mypassword", "Test1", database, EXPORTTYPE.SQL_DDL, tablemetaData, true, dir);
            ExportManager exm = new ExportManager();
            exm.exportSqlToFile(exp);
        }

        catch (DatabaseOperationException e)
        {
            System.out.println("Expected to come here");
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
            fail("fail");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail("fail");
        }
    }

    @Test
    public void test_remove_all_comments_01()
    {
        String infile = "test_01.txt";
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(infile, "UTF-8");
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        catch (UnsupportedEncodingException e1)
        {
            fail("fail");
        }
        for (int i = 0; i < LINESTOWRITE.length; i++)
        {
            writer.println(LINESTOWRITE[i]);
        }
        writer.close();

        String outputfile = "test_1.txt";
        File out = new File(outputfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
            filter.closeOutputStream();
        }
        catch (FileOperationException e)
        {
            fail("fail");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }

        File check = new File(outputfile);
        System.out.println(check.length());
        if(check.length() != 0)
        {
            fail("Not expected to come here");
        }
               
        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outputfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void test_remove_all_comments_02()
    {
        String infile = "test_02.txt";
        String outfile = "testout_02.txt";
        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(infile, "UTF-8");
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        catch (UnsupportedEncodingException e1)
        {
            fail("fail");
        }
        
        writer.println("hello");
        for (int i = 0; i < LINESTOWRITE.length; i++)
        {
            writer.println(LINESTOWRITE[i]);
        }
        writer.println("world");
        writer.close();

        File out = new File(outfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            fail("Not expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("unexpected error"); 
        }

        File outputfile = new File(outfile);
        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new FileReader(outputfile));
        }
        catch (FileNotFoundException e2)
        {
            fail("fail");
        }
        int lines = 0;
        try
        {
            while (reader.readLine() != null) lines++;
            assertTrue(lines == 2);
            
            reader.close();
        }
        catch (IOException e1)
        {
            fail("fail");
        }
        
        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void test_remove_all_comments_03()
    {

        String infile = "test_03.txt";
        String outfile = "testout_03.txt";
        File out = new File(outfile);

        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            System.out.println("Expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("unexpected error"); 
        }

        File outputfile = new File(outfile);
        assertTrue(outputfile.length() == 0);
        
        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error"); 
        }
    }
    
    @Test
    public void test_remove_all_comments_04()
    {

        String infile = "test_04.txt";
        String outfile = "testout_04.txt";

        File newfile = new File(infile);
        try
        {
            newfile.createNewFile();
        }
        catch (IOException e2)
        {
            System.out.println("new file creation fail");
            return;
        }
        
        assertTrue(newfile.length() == 0);
        String checkfile = "test_4.txt";
        File out = new File(checkfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            System.out.println("Expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }

        File outputfile = new File(checkfile);
        assertTrue(outputfile.length() == 0);
        
        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void test_remove_all_comments_05()
    {

        String infile = "test_05.txt";
        String outfile = "testout_05.txt";

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(infile, "UTF-8");
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        catch (UnsupportedEncodingException e1)
        {
            fail("fail");
        }
        
       
        for (int i =0; i< 1005; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        for (int i =0; i< 95; i++)
        {
            writer.print('b');
        }
        writer.print('\n');
        writer.close();
        
        File out = new File(outfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            System.out.println("Expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("fail");
        }

        List<String> lines;
        try
        {
            lines = Files.readAllLines(Paths.get(outfile));
            assertTrue(lines.size() == 2);

            assertTrue(lines.get(0).length() == 1005);

            assertTrue(lines.get(1).length() == 95);
        }
        catch (IOException e1)
        {
            fail("fail");
        }
      
   //     System.out.println(checkfile.length());

        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void test_remove_all_comments_readline_limit()
    {

        String infile = "test_readline.txt";
        String outfile = "testout_readline.txt";

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(infile, "UTF-8");
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        catch (UnsupportedEncodingException e1)
        {
            fail("fail");
        }
        
       
        for (int i =0; i< 8005; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        writer.close();
        
        File out = new File(outfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            System.out.println("Expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("fail");
        }

        List<String> lines;
        try
        {
            lines = Files.readAllLines(Paths.get(outfile));
            assertTrue(lines.size() == 1);
            
            System.out.println("line length: "+lines.get(0).length());

            assertTrue(lines.get(0).length() == 8005);
        }
        catch (IOException e1)
        {
            fail("fail");
        }

        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void test_remove_all_comments_readline_limit_1()
    {

        String infile = "test_readline.txt";
        String outfile = "testout_readline.txt";

        PrintWriter writer = null;
        try
        {
            writer = new PrintWriter(infile, "UTF-8");
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        catch (UnsupportedEncodingException e1)
        {
            fail("fail");
        }
        
       
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("hello world");
        writer.print('\n');
        
        writer.print("hello world");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print('\n');
        

        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("Dumped from database version");
        writer.print("hello world");
        writer.print("Dumped from database version");
        writer.print('\n');
        
        writer.close();
        
        File out = new File(outfile);
        FileOutputStream fs = null;
        try
        {
            fs = new FileOutputStream(out);
        }
        catch (FileNotFoundException e1)
        {
            fail("fail");
        }
        bfs = new BufferedOutputStream(fs);
        filter = new BatchExportDDLFilter(bfs);
        try
        {
            filter.removeComments(infile);
        }
        catch (FileOperationException e)
        {
            System.out.println("Expected to come here");
        }

        try
        {
            bfs.close();
        }
        catch (IOException e)
        {
            fail("fail");
        }

        List<String> lines;
        try
        {
            lines = Files.readAllLines(Paths.get(outfile));
            assertTrue(lines.size() == 0);
        }
        catch (IOException e1)
        {
            fail("fail");
        }

        try
        {
            Files.deleteIfExists(Paths.get(infile));
            Files.deleteIfExists(Paths.get(outfile));
        }
        catch (IOException e)
        {
            fail("unexpected error");
        }
    }
    
    @Test
    public void testTTA_BL_CheckNamespaceDetails_001_02()
    {
        try
        {
            ExportManager exm = new ExportManager();
            Namespace namespace = new Namespace(1, "schema", database);
            assertTrue(namespace.isExportAllowed(EXPORTTYPE.SQL_DDL));
            assertNull(namespace.getPackages());
            assertTrue(namespace.isSynoymSupported());
            namespace.setSynoymSupported(true);
            namespace.getChildren();
            namespace.getSearchedSynonym(1213, "jdfkj");
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace);
            ArrayList<ServerObject> objlist = new ArrayList<ServerObject>(1);
            objlist.add(namespace);
            
            exm.exportSqlToFile("Test", EXPORTTYPE.SQL_DDL_DATA, namespace, true, null);
            fail("Not expected to come here");
        }
        catch (DatabaseOperationException e)
        {
            e.printStackTrace();
        }
        catch (DataStudioSecurityException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}