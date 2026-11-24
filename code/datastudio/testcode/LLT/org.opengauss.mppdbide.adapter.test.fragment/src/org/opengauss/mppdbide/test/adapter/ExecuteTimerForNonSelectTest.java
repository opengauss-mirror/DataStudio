/*
 * Copyright (c) 2026 Huawei Technologies Co., Ltd.
 * DataStudio is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.test.adapter;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.ExecuteTimerForNonSelect;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtils;
import org.opengauss.mppdbide.mock.adapter.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

public class ExecuteTimerForNonSelectTest extends BasicJDBCTestCaseAdapter
{

    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;

    @Before
	public void setUp() throws Exception
    {
        super.setUp();
        connection = new MockConnection();
        // test for logging
        MPPDBIDELoggerUtility
                .setArgs(new String[] {"-logfolder=.", "-detailLogging=true"});

        // MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        preparedstatementHandler = connection
                .getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtilsHelper.prepareProxyInfo(preparedstatementHandler);

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

        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();

    }

    @Test
    public void test_executeTimerForNonSelect_Start()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

            DBConnection dbConnection = new DBConnection();

            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            ExecuteTimerForNonSelect executeTimer = new ExecuteTimerForNonSelect(
                    dbConnection, 0L);
            boolean dbClose = dbConnection.isClosed();
            dbClose = false;
            executeTimer.start();
        }
        catch (DatabaseOperationException e)
        {
            fail(e.getMessage());
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

    @Test
    public void test_executeTimerForNonSelect_Start_Exception()
    {
        try
        {
            // CommonTestUtils.createTableRS(preparedstatementHandler);

            DBConnection dbConnection = new DBConnection();

            String url = null;
            Properties props = new Properties();

            CommonLLTUtils.initDriver("org.postgresql.Driver");

            props.setProperty("user", "test");
            props.setProperty("password", "test");
            props.setProperty("allowEncodingChanges", "true");
            String encoding = System.getProperty("file.encoding");
            props.setProperty("characterEncoding", encoding);
            props.setProperty("ApplicationName", "MPP IDE");

            url = "jdbc:postgresql://127.0.0.1:1234/testDB";

            dbConnection.dbConnect(props, url);
            dbConnection.closeResultSet(null);
            // statementHandler.prepareThrowsSQLException("select * from t1");
            ExecuteTimerForNonSelect executeTimer = new ExecuteTimerForNonSelect(
                    dbConnection, 0L);
            // dbConnection.cancelQuery();
          //  dbConnection.closeStatement(null);
            executeTimer.start();
        }
        catch (DatabaseOperationException e)
        {
            assertTrue(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            fail(e.getMessage());
        }
    }

}
