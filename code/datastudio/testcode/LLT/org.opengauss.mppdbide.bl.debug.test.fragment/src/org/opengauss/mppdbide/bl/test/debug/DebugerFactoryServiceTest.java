/* 
 * Copyright (c) 2022 Huawei Technologies Co.,Ltd.
 *
 * openGauss is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *
 *           http://license.coscl.org.cn/MulanPSL2
 *        
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package org.opengauss.mppdbide.bl.test.debug;

import static org.junit.Assert.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import org.opengauss.mppdbide.common.DBConnectionAdapter;
import org.opengauss.mppdbide.common.GaussManager;
import org.opengauss.mppdbide.common.IConnectionDisconnect;
import org.opengauss.mppdbide.debuger.dao.FunctionDao;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg;
import org.opengauss.mppdbide.debuger.event.DebugAddtionMsg.State;
import org.opengauss.mppdbide.debuger.event.Event;
import org.opengauss.mppdbide.debuger.event.Event.EventMessage;
import org.opengauss.mppdbide.debuger.exception.DebugPositionNotFoundException;
import org.opengauss.mppdbide.debuger.service.SourceCodeService;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.debuger.vo.PositionVo;
import org.opengauss.mppdbide.debuger.vo.SourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.TotalSourceCodeVo;
import org.opengauss.mppdbide.debuger.vo.VersionVo;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * Title: DebugerFactoryServiceTest for use
 *
 * @since 3.0.0
 */
public class DebugerFactoryServiceTest extends DebugerJdbcTestCaseBase {
    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    @Before
    public void setUp() throws Exception
    {
        basicSetUp();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception
    {
        basicTearDown();
    }
    
    @Test
    public void testDbConnectionAdapter() throws MPPDBIDEException, SQLException {
        FunctionDao dao = new FunctionDao();
        String sql = dao.getSql(funcDescAddTest.proname);
        DBConnectionAdapter adapter = getConnectionAdapter();
        PreparedStatement ps = adapter.getStatement(sql);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FunctionVo vo = new FunctionDao().parse(rs);
            adapter.close();
            assertNotNull(vo);
            assertEquals(vo.proname, funcDescAddTest.proname);
        } else {
            adapter.close();
            fail("query failed!");
        }
    }
    
    @Test
    public void testGaussManager() throws MPPDBIDEException, SQLException {
        FunctionDao dao = new FunctionDao();
        String sql = dao.getSql(funcDescAddTest.proname);
        DBConnectionAdapter adapter = getConnectionAdapter();
        PreparedStatement ps = adapter.getStatement(sql);
        GaussManager.INSTANCE.addNoticeListener(ps);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            FunctionVo vo = new FunctionDao().parse(rs);
            adapter.close();
            assertNotNull(vo);
            assertEquals(vo.proname, funcDescAddTest.proname);
        } else {
            adapter.close();
            fail("query failed!");
        }
    }
    
    private DBConnectionAdapter getConnectionAdapter() throws MPPDBIDEException {
       return new DBConnectionAdapter(
               database.getConnectionManager().getFreeConnection(),
               new IConnectionDisconnect<DBConnection>() {
                   public void releaseConnection(DBConnection connection) {
                       database
                       .getConnectionManager()
                       .releaseAndDisconnection(connection);
                   }
               }
            );
    }
    
    @Test
    public void testEvent() {
        PositionVo vo = new PositionVo(functionVo.oid, 1, funcDescAddTest.proname);
        DebugAddtionMsg addtionMsg = new DebugAddtionMsg(State.START, vo);
        assertNotNull(addtionMsg.toString());
        assertTrue(addtionMsg.getPositionVo().isPresent());
        assertEquals(addtionMsg.getState(), State.START);
        
        DebugAddtionMsg addMsg1 = new DebugAddtionMsg(State.END);
        assertFalse(addMsg1.getPositionVo().isPresent());
        
        assertEquals(State.valueOf("START"), State.START);
        assertEquals(State.values().length, 2);

        Event event = new Event(EventMessage.ON_SQL_MSG, addtionMsg);
        assertTrue(event.getId() != -1);
        assertNotNull(event.getStringAddition());
        assertNotNull(event.toString());
        assertFalse(event.hasException());
        assertTrue(EventMessage.values().length == 9);
        assertEquals(EventMessage.valueOf("ON_EXIT"), EventMessage.ON_EXIT);
    }
    
    @Test
    public void testNotSupportDebugVersion() {
        try {
            assertFalse(serviceFactory.isSupportDebug());
        } catch (NullPointerException nullExp) {
            assertTrue(true);
        }
    }

    @Test
    public void testSupportDebugVersion() {
        mockHelper.mockDebugVersion(DebugConstants.getSql(DebugOpt.DEBUG_VERSION));
        assertTrue(serviceFactory.isSupportDebug());
        try {
            VersionVo versionVo = serviceFactory.getVersion().get();
            assertEquals(versionVo.serverversionstr, "server_version");
            assertEquals(versionVo.serverprocessid.longValue(), 1L);
            assertEquals(versionVo.proxyapiver.intValue(), 1);
            assertEquals(versionVo.serverversionnum.intValue(), 1);
        } catch (SQLException sqlExp) {
            fail("can\'t run here!");
        }
    }

    @Test
    public void testRollbackTest() {
        debugService.setRollback(true);
        assertEquals(true, debugService.isRollback());
    }

    @Test
    public void testQueryService() throws SQLException {
        assertNotNull(queryService.getFunctionDao());
        assertNotNull(queryService.getConn());
        mockHelper.mockSourceCode(
                DebugConstants.getSql(DebugOpt.GET_SOURCE_CODE),
                "");

        Optional<SourceCodeVo> sourceCode = queryService.getSourceCode(functionVo.oid);
        assertTrue(sourceCode.isPresent());
        
        mockHelper.mockTotalSourceCode(
                DebugConstants.getSql(DebugOpt.GET_TOTAL_SOURCE_CODE),
                2,
                "");

        Optional<TotalSourceCodeVo> totalSourceCode = queryService.getTotalSourceCode(functionVo.oid);
        assertTrue(totalSourceCode.isPresent());
    }
    
    @Test
    public void testSourceCodeService() throws SQLException, DebugPositionNotFoundException {
        String baseCode = "\r\n" + 
                "DECLARE\r\n" + 
                " tmp1 integer;\r\n" + 
                " tmp2 integer;\r\n" + 
                "BEGIN\r\n" + 
                "  tmp1 := input1 * 2;\r\n" + 
                "  tmp2 := input2 + 3;\r\n" + 
                "  return tmp1 + tmp2;\r\n" + 
                "END; ";
        String totalCode = "CREATE OR REPLACE FUNCTION public.func_add_test(input1 integer, input2 integer)\r\n" + 
                " RETURNS integer\r\n" + 
                " LANGUAGE plpgsql\r\n" + 
                " NOT FENCED NOT SHIPPABLE\r\n" + 
                "AS $function$\r\n" + 
                "DECLARE\r\n" + 
                " tmp1 integer;\r\n" + 
                " tmp2 integer;\r\n" + 
                "BEGIN\r\n" + 
                "  tmp1 := input1 * 2;\r\n" + 
                "  tmp2 := input2 + 3;\r\n" + 
                "  return tmp1 + tmp2;\r\n" + 
                "END; $function$\r\n" + 
                "";
        SourceCodeService codeService = serviceFactory.getCodeService();
        codeService.setBaseCode(baseCode);
        codeService.setTotalCode(totalCode);
        
        assertNotNull(codeService.getTotalCodeDesc());
        assertNotNull(codeService.getBaseCodeDesc());

        assertEquals(codeService.getFirstValidDebugPos(), 5);
        assertEquals(codeService.getBeginTotalAndBaseDiff(), 4);
        assertEquals(codeService.getBeginDebugCodeLine(), 9);

        assertEquals(codeService.getBeignOfBaseCode(), 4);
        assertEquals(codeService.getBeignfTotalCode(), 8);
        
        assertEquals(codeService.showLine2CodeLine(3), 0);
        assertEquals(codeService.codeLine2ShowLine(0), 3);
        
        assertEquals(codeService.getMaxValidDebugPos(), 9);
        codeService.closeService();
    }
}
