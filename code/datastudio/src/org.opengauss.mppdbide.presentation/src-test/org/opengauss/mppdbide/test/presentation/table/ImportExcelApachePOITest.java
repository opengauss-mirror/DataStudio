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
package org.opengauss.mppdbide.test.presentation.table;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.opengauss.mppdbide.adapter.driver.Gauss200V1R7Driver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.preferences.IBLPreference;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileId;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintType;
import org.opengauss.mppdbide.bl.serverdatacache.DBConnProfCache;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ImportExportOption;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.Namespace;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TypeMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserNamespace;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.ServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.mock.presentation.CommonLLTUtils;
import org.opengauss.mppdbide.presentation.exportdata.ImportExcelApachePOI;
import org.opengauss.mppdbide.presentation.exportdata.ImportExportDataCore;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.exceptions.PasswordExpiryException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.security.SecureUtil;
import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.PreparedStatementResultSetHandler;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;

/**
 * 
 * Title: ImportExcelApachePOITest
 *
 * @since 3.0.0
 */
public class ImportExcelApachePOITest extends BasicJDBCTestCaseAdapter {
    MockConnection                    connection                = null;
    PreparedStatementResultSetHandler preparedstatementHandler  = null;
    StatementResultSetHandler         statementHandler          = null;

    PreparedStatementResultSetHandler epreparedstatementHandler = null;
    StatementResultSetHandler         estatementHandler         = null;
    DBConnProfCache                   connProfCache             = null;
    ConnectionProfileId               profileId                 = null;
    private DBConnection              dbconn;
    Gauss200V1R7Driver mockDriver =null;
    ServerConnectionInfo serverInfo = null;


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
        MPPDBIDELoggerUtility.setArgs(null);
        getJDBCMockObjectFactory().getMockDriver().setupConnection(connection);
        mockDriver = CommonLLTUtils.mockConnection(getJDBCMockObjectFactory().getMockDriver());
        this.dbconn = CommonLLTUtils.getDBConnection();
        preparedstatementHandler = connection.getPreparedStatementResultSetHandler();
        statementHandler = connection.getStatementResultSetHandler();

        CommonLLTUtils.prepareProxyInfo(preparedstatementHandler);
        CommonLLTUtils.createTableSpaceRS(preparedstatementHandler);

        connProfCache = DBConnProfCache.getInstance();

        serverInfo = new ServerConnectionInfo();
        IBLPreference sysPref = new MockPresentationBLPreferenceImpl();
        BLPreferenceManager.getInstance().setBLPreference(sysPref);
        MockPresentationBLPreferenceImpl.setDsEncoding("UTF-8");
        MockPresentationBLPreferenceImpl.setFileEncoding("UTF-8");

        JobCancelStatus status = new JobCancelStatus();
        status.setCancel(false);
        serverInfo.setConectionName("TestConnectionName");
        serverInfo.setServerIp("");
        serverInfo.setServerPort(5432);
        serverInfo.setDatabaseName("Gauss");
        serverInfo.setDriverName("FusionInsight LibrA");
        serverInfo.setUsername("myusername");
        serverInfo.setPrd("mypassword".toCharArray());
        serverInfo.setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
        serverInfo.setPrivilegeBasedObAccess(true);
        // serverInfo.setSslPassword("12345");
        // serverInfo.setServerType(DATABASETYPE.GAUSS);
        ConnectionProfileManagerImpl.getInstance().getDiskUtility().setOsCurrentUserFolderPath(".");
        ConnectionProfileManagerImpl.getInstance().generateSecurityFolderInsideProfile(serverInfo);
        profileId = connProfCache.initConnectionProfile(serverInfo, status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mockrunner.jdbc.BasicJDBCTestCaseAdapter#tearDown()
     */
    @After
    public void tearDown() throws Exception {
        super.tearDown();


        preparedstatementHandler.clearPreparedStatements();
        preparedstatementHandler.clearResultSets();
        statementHandler.clearStatements();
        connProfCache.closeAllNodes();

        Iterator<Server> itr = connProfCache.getServers().iterator();

        while (itr.hasNext()) {
            connProfCache.removeServer(itr.next().getId());
        }

        connProfCache.closeAllNodes();

    }


    private TableMetaData getTableMetaData() {
        TableMetaData tableMetaData = null;
        try {

            Server server = new Server(serverInfo);
            Database database = new Database(server, 2, "Gauss");
            try {
                CommonLLTUtils.setConnectionManagerConnectionDriver(mockDriver,database);
            } catch (Exception e) {
                System.out.println("not expected to come here");
            }
            Namespace namespace = new UserNamespace(1, "namespace1", database);
            database.getUserNamespaceGroup().addToGroup((UserNamespace) namespace);
            tableMetaData = new TableMetaData(1, "Table1", database.getNameSpaceById(1), "tablespace");
            tableMetaData.setTempTable(true);
            tableMetaData.setIfExists(true);
            tableMetaData.setName("MyTable");
            tableMetaData.setHasOid(true);
            tableMetaData.setDistributeOptions("HASH");
            tableMetaData.setNodeOptions("Node1");
            tableMetaData.setDescription("Table description");

            ConstraintMetaData constraintMetaData =
                    new ConstraintMetaData(1, "MyConstarint", ConstraintType.UNIQUE_KEY_CONSTRSINT);

            tableMetaData.addConstraint(constraintMetaData);

            ColumnMetaData newTempColumn = new ColumnMetaData(tableMetaData, 1, "col01",
                    new TypeMetaData(1, "bigint", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn);

            ColumnMetaData newTempColumn1 = new ColumnMetaData(tableMetaData, 1, "col02",
                    new TypeMetaData(1, "varchar", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn1);

            ColumnMetaData newTempColumn2 = new ColumnMetaData(tableMetaData, 1, "col03",
                    new TypeMetaData(1, "integer", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn2);

            ColumnMetaData newTempColumn3 = new ColumnMetaData(tableMetaData, 1, "col04",
                    new TypeMetaData(1, "double", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn3);

            ColumnMetaData newTempColumn4 = new ColumnMetaData(tableMetaData, 1, "col05",
                    new TypeMetaData(1, "boolean", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn4);

            ColumnMetaData newTempColumn5 = new ColumnMetaData(tableMetaData, 1, "col06",
                    new TypeMetaData(1, "date", database.getNameSpaceById(1)));
            tableMetaData.getColumns().addItem(newTempColumn5);
            IndexMetaData indexMetaData = new IndexMetaData("Idx1");

            indexMetaData.setTable(tableMetaData);
            indexMetaData.setNamespace(tableMetaData.getNamespace());
            tableMetaData.addIndex(indexMetaData);

        } catch (OutOfMemoryError outofMemoryError) {
            outofMemoryError.printStackTrace();
        } catch (MPPDBIDEException mppdbideException) {
            mppdbideException.printStackTrace();
        }
        return tableMetaData;
    }

    private void createExcel(String fileName) {
        FileOutputStream fos = null;
        try {
            // Create Workbook instance holding reference to .xlsx file
            SXSSFWorkbook workbook = new SXSSFWorkbook();
            // Get first/desired sheet from the workbook
            SXSSFSheet sheet = createSheet(workbook, "Sheet 1", false);
            // Write some information in the cells or do what you want
            SXSSFRow row1 = sheet.createRow(0);
            SXSSFCell r1c2 = row1.createCell(0);
            r1c2.setCellValue("col01");
            SXSSFCell r1c3 = row1.createCell(1);
            r1c3.setCellValue("col02");
            SXSSFCell r1c4 = row1.createCell(2);
            r1c4.setCellValue("col03");
            SXSSFCell r1c5 = row1.createCell(3);
            r1c5.setCellValue("col04");
            SXSSFCell r1c6 = row1.createCell(4);
            r1c6.setCellValue("col05");
            SXSSFCell r1c7 = row1.createCell(5);
            r1c7.setCellValue("col06");
            SXSSFRow row2 = sheet.createRow(1);
            SXSSFCell r2c2 = row2.createCell(0);
            r2c2.setCellValue("1");
            SXSSFCell r2cc3 = row2.createCell(1);
            r2cc3.setCellValue("item1");
            SXSSFCell r2cc4 = row2.createCell(2);
            r2cc4.setCellValue("25");
            SXSSFCell r2cc5 = row2.createCell(3);
            r2cc5.setCellValue(Double.parseDouble("25.5"));
            SXSSFCell r2cc6 = row2.createCell(4);
            r2cc6.setCellValue(Boolean.getBoolean("TRUE"));
            SXSSFCell r2cc7 = row2.createCell(5);
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse("2019-10-16 11:01:00");
            r2cc7.setCellValue(date);

            // Save excel to HDD Drive
            File pathToFile = new File(fileName);
            if (!pathToFile.exists()) {
                pathToFile.createNewFile();
            }

            // Save excel to HDD Drive
            fos = new FileOutputStream(pathToFile);
            workbook.write(fos);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }

    private static SXSSFSheet createSheet(SXSSFWorkbook wb, String prefix, boolean isHidden) {
        SXSSFSheet sheet = null;
        int count = 0;
        for (int i = 0; i < wb.getNumberOfSheets(); i++) {
            String sName = wb.getSheetName(i);
            if (sName.startsWith(prefix)) count++;
        }
        if (count > 0) {
            sheet = wb.createSheet(prefix + count);
        } else {
            sheet = wb.createSheet(prefix);
        }
        return sheet;
    }


    @Test
    public void testGetCellValues3() {
        try {
            if (CommonLLTUtils.isLinux())
            {
                return;
            }
            
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("EXCEL(xls)");
            option.setAllColunms(true);
            option.setHeader(true);
            option.setEncoding("UTF-8");
            option.setDateSelector("yyyy-MM-dd HH:mm:ss");
            option.setZip(false);
            option.setTablecolumns(
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")));
            option.setFileName("./firstExcel.xls");
            TableMetaData tableMetaData = getTableMetaData();

            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")), null,
                    null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            ImportExcelApachePOI importExcelPOI = new ImportExcelApachePOI(option);
            List<ArrayList<String>> list = importExcelPOI.getCellValues();
            assertEquals(1, list.size());

        } catch (MPPDBIDEException | IOException exception) {
            fail("Not excepted to come here");
        }
    }

    @Test
    public void testGetCellValues4() {
        try {
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("EXCEL(xlsx)");
            option.setAllColunms(true);
            option.setHeader(true);
            option.setEncoding("UTF-8");
            option.setDateSelector("yyyy-MM-dd HH:mm:ss");
            option.setZip(false);
            option.setTablecolumns(
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")));
            String filePath = "./testGetCellValues4.xlsx";
            createExcel(filePath);
            option.setFileName(filePath);
            TableMetaData tableMetaData = getTableMetaData();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")), null,
                    null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            ImportExcelApachePOI importExcelPOI = new ImportExcelApachePOI(option);
            List<ArrayList<String>> list = importExcelPOI.getCellValues();
            assertEquals(1, list.size());

        } catch (MPPDBIDEException | IOException exception) {
            fail("Not excepted to come here");
        }
    }

    @Test
    public void testGetCellValues_Failure() {
        try {
            ImportExportOption option = new ImportExportOption();
            option.setExport(false);
            option.setFileFormat("EXCEL(xlsx)");
            option.setAllColunms(true);
            option.setHeader(true);
            option.setEncoding("UTF-8");
            option.setDateSelector("yyyy-MM-dd HH:mm:ss");
            option.setZip(false);
            option.setTablecolumns(
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")));
            String filePath = "./testGetCellValues.xlsx";
            createExcel(filePath);
            option.setFileName(filePath);
            TableMetaData tableMetaData = getTableMetaData();
            ImportExportDataCore core = new ImportExportDataCore(tableMetaData,
                    new ArrayList<String>(Arrays.asList("col01", "coll02", "col03", "col04", "col05", "col06")), null,
                    null, null);
            core.setImportExportoptions(option);
            core.initializeCore();
            core.validateImportExportOptParameters();
            ImportExcelApachePOI importExcelPOI = new ImportExcelApachePOI(option);
            importExcelPOI.getCellValues();
        } catch (MPPDBIDEException e) {
            assertTrue(true);
        }
        catch (IOException exception) {
            fail("Not excepted to come here");
        }
    }

}
