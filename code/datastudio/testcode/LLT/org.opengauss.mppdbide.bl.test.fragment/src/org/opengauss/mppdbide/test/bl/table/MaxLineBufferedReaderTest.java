package org.opengauss.mppdbide.test.bl.table;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.bl.export.BatchExportDDLFilter;
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
import org.opengauss.mppdbide.mock.bl.CommonLLTUtils;
import org.opengauss.mppdbide.mock.bl.MockBLPreferenceImpl;
import org.opengauss.mppdbide.utils.MaxLineBufferedReader;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class MaxLineBufferedReaderTest extends BasicJDBCTestCaseAdapter
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
    public void test_MaxLineBufferedReader_read_a_line()
    {
        int lineLen = 8191;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i< lineLen; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader);
            
            String line = mf.readMaxLenLine();
            assertTrue(line != null);
            assertTrue(line.length() == lineLen);
            assertTrue(line.matches("^[a]+$"));
            assertTrue(mf.isWholeLine());
            
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
    
    @Test
    public void test_MaxLineBufferedReader_read_a_line_2()
    {
        int lineLen = 499 ;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i < lineLen; i++)
        {
            writer.print('b');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader, lineLen+1);
            
            String line = mf.readMaxLenLine();
            
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == lineLen);
            assertTrue(line.matches("^[b]+$"));
            
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
    
    @Test
    public void test_MaxLineBufferedReader_read_a_bigline()
    {
        int maxLineLen = 499 ;
        int actualLineLen = maxLineLen + 5;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);

            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader, maxLineLen);
            
            String line = mf.readMaxLenLine();
            
            assertTrue(line != null);
            assertTrue(line.length() == maxLineLen);
            assertTrue(line.length() != actualLineLen);
            assertTrue(!mf.isWholeLine());
            assertTrue(line.matches("^[a]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine();            
            assertTrue(line != null);
            assertTrue(line.length() == actualLineLen - maxLineLen);
            assertTrue(line.matches("^[a]+$"));
            assertTrue(mf.isWholeLine());
            
            line = mf.readMaxLenLine();            
            assertTrue(line == null);
                        
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
    
    @Test
    public void test_MaxLineBufferedReader_read_few_lines()
    {
        int maxLineLen = 499 ;
        int actualLineLen = maxLineLen;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('b');
        }
        writer.print('\n');
        
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('c');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);

            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader, maxLineLen);
            
            String line = mf.readMaxLenLine();            
            assertTrue(line.length() == actualLineLen);
            assertTrue(line.matches("^[a]+$"));
            
            line = "";
            line = mf.readMaxLenLine();            
            assertTrue(line.length() == actualLineLen);
            assertTrue(line.matches("^[b]+$"));
            
            line = "";
            line = mf.readMaxLenLine();            
            assertTrue(line.length() == actualLineLen);
            assertTrue(line.matches("^[c]+$"));
            
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
    
    @Test
    public void test_MaxLineBufferedReader_read_few_Biglines()
    {
        int maxLineLen = 499 ;
        int actualLineLen = maxLineLen + 5;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('b');
        }
        writer.print('\n');
        
        for (int i =0; i < actualLineLen; i++)
        {
            writer.print('c');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);

            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader, maxLineLen);
            
            String line = mf.readMaxLenLine();    
            assertTrue(line != null);
            assertTrue(!mf.isWholeLine());
            assertTrue(line.length() != actualLineLen);
            assertTrue(line.length() == maxLineLen);
            assertTrue(line.matches("^[a]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine(); 
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == actualLineLen - maxLineLen);
            assertTrue(line.matches("^[a]+$"));
            
            line = mf.readMaxLenLine();    
            assertTrue(line != null);
            assertTrue(!mf.isWholeLine());
            assertTrue(line.length() != actualLineLen);
            assertTrue(line.length() == maxLineLen);
            assertTrue(line.matches("^[b]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine(); 
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == actualLineLen - maxLineLen);
            assertTrue(line.matches("^[b]+$"));
            
            line = mf.readMaxLenLine();    
            assertTrue(line != null);
            assertTrue(!mf.isWholeLine());
            assertTrue(line.length() != actualLineLen);
            assertTrue(line.length() == maxLineLen);
            assertTrue(line.matches("^[c]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine(); 
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == actualLineLen - maxLineLen);
            assertTrue(line.matches("^[c]+$"));
            
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
    
    @Test
    public void test_MaxLineBufferedReader_read_few_lines_short_long()
    {
        int maxLineLen = 499 ;
        int longlen = maxLineLen + 5;
        int shortlen = maxLineLen - 5;
        String infile = "sample_input.txt";
        File file = new File(infile);
        
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
        
       
        for (int i =0; i < shortlen; i++)
        {
            writer.print('a');
        }
        writer.print('\n');
        
        for (int i =0; i < longlen; i++)
        {
            writer.print('b');
        }
        writer.print('\n');
        
        for (int i =0; i < shortlen; i++)
        {
            writer.print('c');
        }
        writer.print('\n');
        
        writer.close();
        
        try
        {
            FileInputStream input = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(input);
            MaxLineBufferedReader mf = new MaxLineBufferedReader(reader, maxLineLen);
            
            String line = mf.readMaxLenLine();   
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == shortlen);
            assertTrue(line.matches("^[a]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine();   
            assertTrue(line != null);
            assertTrue(!mf.isWholeLine());
            assertTrue(line.length() == maxLineLen);
            assertTrue(line.matches("^[b]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine();   
            assertTrue(line != null);
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == longlen - maxLineLen);
            assertTrue(line.matches("^[b]+$"));
            
            line = "";
            assertTrue(line.equals(""));
            line = mf.readMaxLenLine();
            assertTrue(mf.isWholeLine());
            assertTrue(line.length() == shortlen);
            assertTrue(line.matches("^[c]+$"));
            
            line = mf.readMaxLenLine();
            assertTrue(line == null);
            
            mf.close();

        }
        catch (IOException e)
        {
            fail("IO exception not expected");
        }
        catch (DatabaseOperationException e)
        {
            fail("not expected");
        }
        finally
        {
            file.delete();
        }
    }
}
