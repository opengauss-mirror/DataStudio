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

package org.opengauss.mppdbide.presentation.exportdata;

import java.text.ParseException;
import java.util.ArrayList;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDataCore.
 * 
 * @since 3.0.0
 */
public class ImportExportDataCore extends AbstractImportExportDataCore {

    private ImportExportData importExportData;
    private ExportCursorQueryExecuter exportCursorExecuter;

    /**
     * Instantiates a new import export data core.
     *
     * @param obj the obj
     * @param clmList the clm list
     * @param executedQuery the executed query
     * @param terminalID the terminal ID
     * @param querySubmitTime the query submit time
     */
    public ImportExportDataCore(ServerObject obj, ArrayList<String> clmList, String executedQuery, String terminalID,
            String querySubmitTime) {
        super(obj, clmList, executedQuery, terminalID, querySubmitTime);
        importExportData = new ImportExportData(obj);
    }

    /**
     * Compose excel query.
     *
     * @return the string
     */
    public String composeExcelQuery() {
        StringBuffer queryBuff = new StringBuffer();
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.composeExcelQuery(queryBuff, importExportoptions);
        } else {
            composeExcelQuery(queryBuff);
        }
        return queryBuff.toString();
    }

    /**
     * Append delimiter option.
     *
     * @param queryBuff the query buff
     */
    protected void appendDelimiterOption(StringBuffer queryBuff) {
        super.appendDelimiterOption(queryBuff);
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.appendQueryFormatForCsvFormat(queryBuff);
        }
    }

    /**
     * Append tbl name or executed query.
     *
     * @param queryBuff the query buff
     */
    protected void appendTblNameOrExecutedQuery(StringBuffer queryBuff) {
        super.appendTblNameOrExecutedQuery(queryBuff);
        if (importExportServerObj instanceof TableMetaData) {
            importExportData.appaendTblNameOrExecutedQuery(queryBuff, importExportoptions);
        }
    }

    /**
     * Cancel import export operation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelImportExportOperation() throws DatabaseCriticalException, DatabaseOperationException {
        super.cancelImportExportOperation();
        if (isOLAPDB() && exportCursorExecuter != null) {
            exportCursorExecuter.setCancelFlag(true);
        }
    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        String fileName = super.getFileName();
        if (importExportServerObj instanceof TableMetaData) {
            fileName = importExportData.getFileName();
        }
        return  CustomStringUtility.sanitizeExportFileName(fileName);
    }

    /**
     * Gets the safe sheet name.
     *
     * @return the safe sheet name
     */
    public String getSafeSheetName() {
        String fileName = super.getSafeSheetName();
        if (importExportServerObj instanceof TableMetaData) {
            fileName = importExportData.getFileName();
        }
        return CustomStringUtility.sanitizeExcelSheetName(fileName);
    }

    /**
     * Gets the display table name.
     *
     * @return the display table name
     */
    public String getDisplayTableName() {
        super.getDisplayTableName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getDisplayTableName();
        }
        return displayTableName;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        displayTableName = super.getDisplayName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getDisplayName();
        }
        return displayTableName;
    }

    /**
     * Gets the progress label name.
     *
     * @return the progress label name
     */
    public String getProgressLabelName() {
        displayTableName = super.getProgressLabelName();
        if (importExportServerObj instanceof TableMetaData) {
            displayTableName = importExportData.getProgressLabelName();
        }
        return displayTableName;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        Database database = super.getDatabase();
        if (importExportServerObj instanceof TableMetaData) {
            database = importExportData.getDatabase();
        }
        return database;
    }

    @Override
    protected long getExportExcelTotalRows(DBConnection currentConnection, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException {
        String queryForExport = composeExcelQuery();
        exportCursorExecuter = new ExportCursorQueryExecuter(queryForExport, currentConnection);
        long totalRows = exportCursorExecuter.exportExcelData(visitor, isFuncProcExport);
        return totalRows;
    }
}
