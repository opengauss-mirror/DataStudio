/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.bl.test.debug;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.huawei.mppdbide.bl.mock.debug.DebugerJdbcTestCaseBase;
import com.huawei.mppdbide.common.DBConnectionAdapter;
import com.huawei.mppdbide.common.GaussManager;
import com.huawei.mppdbide.debuger.dao.FunctionDao;
import com.huawei.mppdbide.debuger.debug.DebugConstants;
import com.huawei.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg;
import com.huawei.mppdbide.debuger.event.DebugAddtionMsg.State;
import com.huawei.mppdbide.debuger.event.Event;
import com.huawei.mppdbide.debuger.event.Event.EventMessage;
import com.huawei.mppdbide.debuger.exception.DebugPositionNotFoundException;
import com.huawei.mppdbide.debuger.service.SourceCodeService;
import com.huawei.mppdbide.debuger.vo.FunctionVo;
import com.huawei.mppdbide.debuger.vo.PositionVo;
import com.huawei.mppdbide.debuger.vo.SourceCodeVo;
import com.huawei.mppdbide.debuger.vo.TotalSourceCodeVo;
import com.huawei.mppdbide.debuger.vo.VersionVo;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * Title: DebugerFactoryServiceTest for use
 * Description: 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-16]
 * @since 2020-12-16
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
        DBConnectionAdapter adapter = new DBConnectionAdapter(
                database.getConnectionManager().getFreeConnection()
                );
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
        DBConnectionAdapter adapter = new DBConnectionAdapter(
                database.getConnectionManager().getFreeConnection()
                );
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
        assertTrue(EventMessage.values().length ==  8);
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
