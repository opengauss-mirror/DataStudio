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

package org.opengauss.mppdbide.bl.mock.debug;

import java.sql.SQLException;
import java.sql.SQLWarning;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.contentassist.ContentAssistProcesserData;
import org.opengauss.mppdbide.bl.mock.debug.CommonLLTUtils;
import org.opengauss.mppdbide.bl.mock.debug.CommonLLTUtilsHelper;
import org.opengauss.mppdbide.bl.mock.debug.GaussMockPreparedStatementToHang;
import org.opengauss.mppdbide.bl.mock.debug.MockConnectionStubPS;
import org.opengauss.mppdbide.bl.mock.debug.MockDebugServiceHelper;
import org.opengauss.mppdbide.bl.mock.debug.MockDebugServiceHelper.FunctionDesc;
import org.opengauss.mppdbide.bl.mock.debug.MockStatementToHang;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.ForeignTable;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.OBJECTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.SequenceMetadata;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.ViewColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ViewMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.test.debug.MockDebugBLPreferenceImpl;
import org.opengauss.mppdbide.common.DBConnectionAdapter;
import org.opengauss.mppdbide.common.IConnection;
import org.opengauss.mppdbide.common.IConnectionDisconnect;
import org.opengauss.mppdbide.common.IConnectionProvider;
import org.opengauss.mppdbide.debuger.dao.FunctionDao;
import org.opengauss.mppdbide.debuger.debug.DebugConstants;
import org.opengauss.mppdbide.debuger.debug.DebugConstants.DebugOpt;
import org.opengauss.mppdbide.debuger.service.DebugService;
import org.opengauss.mppdbide.debuger.service.QueryService;
import org.opengauss.mppdbide.debuger.service.ServiceFactory;
import org.opengauss.mppdbide.debuger.service.chain.PrepareMsgChian;
import org.opengauss.mppdbide.debuger.service.chain.ServerPortMsgChain;
import org.opengauss.mppdbide.debuger.thread.DebugServerThreadProxy;
import org.opengauss.mppdbide.debuger.vo.FunctionVo;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

/**
 * Title: DebugerFunctionTest for use
 *
 * @since 3.0.0
 */
public class DebugerJdbcTestCaseBase extends BasicJDBCTestCaseAdapter {
    protected MockConnection connection = null;
    protected DebugObjects debugObject = null;
    protected DebugObjects nestedDebugObject = null;
    protected PreparedStatementResultSetHandler preparedstatementHandler = null;
    protected ServerConnectionInfo serverInfo = null;
    protected ConnectionProfileId profileId = null;
    
    protected DBConnProfCache connProfCache = null;
    protected ContentAssistProcesserData data;
    protected Database database = null;
    
    protected FunctionDesc funcDescAddTest = new FunctionDesc("add_test",
            Arrays.asList(new Integer(1), new Integer(2)));
    protected ServiceFactory serviceFactory = null;
    protected DebugService debugService = null;
    protected QueryService queryService = null;
    protected FunctionVo functionVo = null;
    protected DebugServerThreadProxy serverThreadProxy = null;
    protected MockDebugServiceHelper mockHelper = null;
    

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#setUp()
     */
    protected void basicSetUp(String... version) throws Exception
    {
        super.setUp();
        CommonLLTUtilsHelper.runLinuxFilePermissionInstance();
        IBLPreference sysPref = new MockDebugBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockDebugBLPreferenceImpl.setDsEncoding("UTF-8");
        MockDebugBLPreferenceImpl.setFileEncoding("UTF-8");
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        connection = new MockConnectionStubPS(false);
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler, version);
        CommonLLTUtilsHelper.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        
        connProfCache = DBConnProfCache.getInstance();

        ServerConnectionInfo serverInfo = new ServerConnectionInfo();
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
        database = connProfCache.getDbForProfileId(profileId);
        data = new ContentAssistProcesserData(database);
        getAllDatabaseObjects();
        
        prepareDebugResultSets();
        startDebugService();
    }
    
    protected void startDebugService() throws SQLException, InterruptedException {
        serviceFactory = createServiceFactory();
        queryService = serviceFactory.getQueryService();
        functionVo = queryService.queryFunction(funcDescAddTest.proname);
        mockHelper = new MockDebugServiceHelper(preparedstatementHandler, functionVo);
        debugService = serviceFactory.getDebugService(functionVo);
        debugService.init();
        debugService.prepareDebug();
        debugService.noticeReceived(new SQLWarning(PrepareMsgChian.PREPARE_SUCCESS + " SERVER "));
        debugService.noticeReceived(new SQLWarning(ServerPortMsgChain.SERVER_PORT_MATCH + "0"));
        debugService.attachDebug();
        Thread.sleep(50);
        debugService.startDebug(funcDescAddTest.params);
        Thread.sleep(50);
    }

    protected ServiceFactory createServiceFactory() {
        ServiceFactory serviceFactory = new ServiceFactory(new IConnectionProvider() {
            @Override
            public Optional<IConnection> getFreeConnection() {
                DBConnection dbConn;
                try {
                    dbConn = database.getConnectionManager().getFreeConnection();
                    return Optional.of(new DBConnectionAdapter(
                            dbConn,
                            new IConnectionDisconnect<DBConnection>() {
                                @Override
                                public void releaseConnection(
                                        DBConnection connection) {
                                    database.getConnectionManager()
                                        .releaseAndDisconnection(connection);
                                }
                            }));
                } catch (MPPDBIDEException e) {
                }
                return Optional.empty();
            }
        });
        return serviceFactory;
    }
    protected void prepareDebugResultSets() {
        String sql = new FunctionDao().getSql(funcDescAddTest.proname);
        MockDebugServiceHelper helper = new MockDebugServiceHelper(
                preparedstatementHandler,
                null);
        helper.mockFunctionVo(sql, new Object[] {new Long(1), funcDescAddTest.proname});
        helper.mockPrepareDebug(
                DebugConstants.getSql(DebugOpt.START_SESSION));
        helper.mockStartDebug(
                DebugConstants.getSql(funcDescAddTest.proname, funcDescAddTest.params.size()),
                new Integer(7));
        helper.mockAttachDebug(DebugConstants.getSql(DebugOpt.ATTACH_SESSION),
                new Integer(1));
        helper.mockAbortDebug(DebugConstants.getSql(DebugOpt.ABORT_TARGET));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    public void basicTearDown() throws Exception
    {
        super.tearDown();

        debugService.end();
        debugService = null;
        queryService.closeService();
        queryService = null;
        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearThrowsSQLException();
        MockStatementToHang.resetHangQueries();
        GaussMockPreparedStatementToHang.resetHangqueries();
        database = null;
        
        Iterator<Server> itr = DBConnProfCache.getInstance().getServers().iterator();
        
        while(itr.hasNext())
        {
            DBConnProfCache.getInstance().removeServer(itr.next().getId());
        }
    }
    
    
    protected void getAllDatabaseObjects()
    {
        try
        {

            database.getServer().refresh();
            Namespace ns1 = new UserNamespace(6, "ns1", database);
            Namespace ns2 = new UserNamespace(2, "NS1", database);
            Namespace ns3 = new UserNamespace(3, "NS2", database);
            Namespace ns4 = new UserNamespace(4, "yns2", database);
            Namespace ns6 = new UserNamespace(5, "\"NS1\"", database);
            Namespace ns7 = new UserNamespace(7, "Namespc", database);

                
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns1);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns2);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns3);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns4);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns6);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) ns7);

            TableMetaData ptab1 = new PartitionTable(ns7);
            ns7.addTableToSearchPool(ptab1);

            TableMetaData tbl1 = new TableMetaData(1, "tbl1", ns1, "");
            ColumnMetaData clm = new ColumnMetaData(tbl1, 1, "Col1",  new TypeMetaData(11, "Integer", ns3));
            tbl1.addColumn(clm);
            TableMetaData tbl2 = new TableMetaData(2, "tbl2", ns1, "");
            ColumnMetaData clm1 = new ColumnMetaData(tbl2, 1, "Col2", null);
            tbl2.addColumn(clm1);
            TableMetaData tbl3 = new TableMetaData(3, "TBL1", ns1, "");
            ColumnMetaData clm2 = new ColumnMetaData(tbl3, 1, "Col3", null);
            tbl3.addColumn(clm2);

            ns1.addTableToSearchPool(tbl1);
            ns1.addTableToSearchPool(tbl2);
            ns1.addTableToSearchPool(tbl3);

            TableMetaData tbl4 = new TableMetaData(4, "tbl1", ns2, "");
            ColumnMetaData clm3 = new ColumnMetaData(tbl4, 1, "Col4", new TypeMetaData(11, "Integer", ns3));
            tbl4.addColumn(clm3);
            TableMetaData tbl5 = new TableMetaData(5, "tbl2", ns2, "");
            ColumnMetaData clm4 = new ColumnMetaData(tbl5, 1, "Col5", null);
            tbl5.addColumn(clm4);
            TableMetaData tbl6 = new TableMetaData(6, "TBL1", ns2, "");
            ColumnMetaData clm5 = new ColumnMetaData(tbl6, 1, "Col6", null);
            tbl6.addColumn(clm5);
            TableMetaData tbl7 = new TableMetaData(7, "TBL2", ns2, "");
            ColumnMetaData clm6 = new ColumnMetaData(tbl7, 1, "Col7", null);
            tbl7.addColumn(clm6);
            TableMetaData tbl110 = new TableMetaData(110, "T}BL1", ns2, "");
            ColumnMetaData clm110 = new ColumnMetaData(tbl110, 1, "Col7", null);
            tbl7.addColumn(clm110);

            ViewMetaData view = new ViewMetaData(1, "Yiew1", ns2,ns2.getDatabase());
            ViewColumnMetaData viewCol = new ViewColumnMetaData(view, 2, "viewclm", new TypeMetaData(0, "int", ns2));
            view.getColumns().addItem(viewCol);
            
            SequenceMetadata seq = new SequenceMetadata(ns2);
            seq.setName("seq");

            ns2.addTableToSearchPool(tbl5);
            ns2.addTableToSearchPool(tbl6);
            ns2.addTableToSearchPool(tbl7);
            ns2.addTableToSearchPool(tbl4);
            ns2.addTableToSearchPool(tbl110);
            ns2.addView(view);
            ns2.addSequence(seq);
            database.getSearchPoolManager().addsequenceToSearchPool(seq);

            TableMetaData tbl8 = new TableMetaData(8, "tbl1", ns3, "");
            ColumnMetaData clm8 = new ColumnMetaData(tbl8, 1, "Col8", new TypeMetaData(11, "Integer", ns3));
            tbl8.addColumn(clm8);
            TableMetaData tbl9 = new TableMetaData(9, "tbl2", ns3, "");
            ColumnMetaData clm9 = new ColumnMetaData(tbl9, 1, "Col9", null);
            tbl9.addColumn(clm9);

            ns3.addTableToSearchPool(tbl8);
            ns3.addTableToSearchPool(tbl9);

            TableMetaData tbl10 = new TableMetaData(10, "Tbl11", ns4, "");
            ColumnMetaData clm10 = new ColumnMetaData(tbl10, 1, "Col10", null);
            tbl10.addColumn(clm10);
            TableMetaData tbl11 = new TableMetaData(11, "xtbl2", ns4, "");
            ColumnMetaData clm11 = new ColumnMetaData(tbl11, 1, "Col11", null);
            tbl11.addColumn(clm11);
            TableMetaData tbl12 = new TableMetaData(12, "NS1", ns4, "");
            ColumnMetaData clm12 = new ColumnMetaData(tbl12, 1, "Col12", null);
            tbl12.addColumn(clm12);

            ns4.addTableToSearchPool(tbl10);
            ns4.addTableToSearchPool(tbl11);
            ns4.addTableToSearchPool(tbl12);

            TableMetaData tbl14 = new TableMetaData(13, "ybl1", ns6, "");
            ColumnMetaData clm14 = new ColumnMetaData(tbl14, 1, "Col14", null);
            tbl14.addColumn(clm14);

            ns6.addTableToSearchPool(tbl14);

         
            ForeignTable ForeignTbl = new ForeignTable(ns1, OBJECTTYPE.FOREIGN_TABLE_GDS);
            ForeignTbl.setName("Ftable1");
            ForeignTbl.setOid(1);
            ns1.addForeignTableToGroup(ForeignTbl);
            ns1.addTableToSearchPool(ForeignTbl);
            ColumnMetaData fClm10 = new ColumnMetaData(ForeignTbl, 1, "FCol10", null);
            ForeignTbl.addColumn(fClm10);
            
            ForeignTable ForeignTbl2 = new ForeignTable(ns1, OBJECTTYPE.FOREIGN_TABLE_GDS);
            ForeignTbl2.setName("Ftable2");
            ForeignTbl.setOid(2);
            ns1.addForeignTableToGroup(ForeignTbl2);
            ns1.addTableToSearchPool(ForeignTbl2);
            ColumnMetaData fClm11 = new ColumnMetaData(ForeignTbl, 1, "FCol11", null);
            ForeignTbl2.addColumn(fClm11);
            
            PartitionTable ptab = new PartitionTable(ns2);
            ptab.setName("partTable");
            ptab.setOid(1);
            ns2.addTableToGroup(ptab);
            ns2.addTableToSearchPool(ptab);
            ColumnMetaData pClm11 = new ColumnMetaData(ptab, 1, "PCol11", null);
            ptab.addColumn(pClm11);
            
            PartitionTable ptab11 = new PartitionTable(ns2);
            ptab11.setName("partTable1");
            ptab.setOid(2);
            ns2.addTableToGroup(ptab11);
            ns2.addTableToSearchPool(ptab11);
            ColumnMetaData pClm12 = new ColumnMetaData(ptab11, 1, "PCol12", null);
            ptab11.addColumn(pClm12);
            
            System.out.println(database.getAllNameSpaces());
        }
        catch (DatabaseOperationException e)
        {
            
            e.printStackTrace();
        }
        catch (DatabaseCriticalException e)
        {
            
            e.printStackTrace();
        }
        catch (Exception e)
        {
            
            e.printStackTrace();
        }
    }
}
