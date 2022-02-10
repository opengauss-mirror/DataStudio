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

import java.nio.file.Path;
import java.text.ParseException;
import java.util.Observable;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class GenerateSQLDataCore.
 *
 * @since 3.0.0
 */
public class GenerateSQLDataCore extends Observable {

    private ServerObject importExportServerObj;
    private DBConnection dbCon;
    private String executedQuery;
    private boolean isExportIsInProgress;
    private ExportCursorQueryExecuter exportCursorExecuter;
    private GenerateCursorExecuteVisitor visitor;
    private boolean isOLAP;
    private String encode;
    private String tableNames;

    /**
     * Instantiates a new generate SQL data core.
     *
     * @param obj the obj
     * @param executedQuery the executed query
     * @param encode the encode
     * @param userName the user name
     */
    public GenerateSQLDataCore(ServerObject obj, String executedQuery, String encode, String userName) {
        this.importExportServerObj = obj;
        this.executedQuery = executedQuery;
        this.encode = encode;
        setDatabaseType(obj);
    }

    private void setDatabaseType(ServerObject obj) {
        Database database = obj.getDatabase();
        if (database != null) {
            switch (database.getDBType()) {
                case OPENGAUSS: {
                    this.isOLAP = true;
                    break;
                }
                default: {
                    // fall through
                    break;
                }
            }
        }
    }

    /**
     * Checks if is olapdb.
     *
     * @return true, if is olapdb
     */
    public boolean isOLAPDB() {
        return isOLAP;
    }

    /**
     * Initialize core.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void initializeCore() throws MPPDBIDEException {
        createConnection();
    }

    private void createConnection() throws MPPDBIDEException {
        if (dbCon == null && importExportServerObj.getConnectionManager() != null) {
            dbCon = importExportServerObj.getConnectionManager().getFreeConnection();
        }
    }

    /**
     * Compose SQL query.
     *
     * @return the string
     */
    public String composeSQLQuery() {
        StringBuffer queryBuff = new StringBuffer();
        if (importExportServerObj instanceof TableMetaData) {
            queryBuff.append("SELECT");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);

            queryBuff.append("*");

            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            queryBuff.append("FROM");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            queryBuff.append(this.importExportServerObj.getDisplayName());
        } else {
            queryBuff.append(executedQuery);

        }
        return queryBuff.toString();
    }

    /**
     * Execute export data.
     *
     * @param conn the conn
     * @param newPath the new path
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws ParseException the parse exception
     */
    public long executeExportData(DBConnection conn, Path newPath)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException, ParseException {
        long totalRows = 0;
        boolean isExportTable = null == conn;
        DBConnection currentConnection = null;
        try {
            if (isExportTable) {
                if (dbCon == null) {
                    try {
                        initializeCore();
                    } catch (MPPDBIDEException exe) {
                        MPPDBIDELoggerUtility.error(
                                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION),
                                exe);
                        throw new DatabaseOperationException(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION);
                    }
                }
                currentConnection = dbCon;
            } else {
                currentConnection = conn;
            }

            String querySql = composeSQLQuery();
            tableNames = JSQLParserUtils.getSelectQueryMainTableName(querySql);
            visitor = new GenerateCursorExecuteVisitor(newPath, encode, isOLAP, tableNames);
            exportCursorExecuter = new ExportCursorQueryExecuter(querySql, currentConnection);
            totalRows = exportCursorExecuter.exportSQLData(visitor);
        } finally {
            releaseConnection();
        }

        return totalRows;
    }

    private void releaseConnection() {
        if (dbCon != null && importExportServerObj.getConnectionManager() != null) {
            importExportServerObj.getConnectionManager().releaseAndDisconnection(dbCon);
        }
        dbCon = null;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        if (importExportServerObj instanceof TableMetaData) {
            return ((TableMetaData) importExportServerObj).getDatabase();
        }
        if (importExportServerObj instanceof Database) {
            return (Database) importExportServerObj;
        }
        return null;
    }

    /**
     * Import export clean up.
     */
    public void importExportCleanUp() {
        setExportIsInProgress(false);
    }


    /**
     * Sets the export is in progress.
     *
     * @param isExportIsInProgres the new export is in progress
     */
    public void setExportIsInProgress(boolean isExportIsInProgres) {
        this.isExportIsInProgress = isExportIsInProgres;
        setChanged();
        notifyObservers(this.isExportIsInProgress);
    }

    /**
     * Clean up.
     */
    public void cleanUpDataCore() {
        if (visitor != null) {
            visitor.cleanUpFileContent();
        }
    }

    /**
     * Gets the path.
     *
     * @return the path
     */
    public Path getPath() {
        return visitor.getPath();
    }

    /**
     * Cancel export operation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelExportOperation() throws DatabaseCriticalException, DatabaseOperationException {
        if (dbCon != null) {
            dbCon.cancelQuery();
        }
        if (isOLAPDB() && exportCursorExecuter != null) {
            exportCursorExecuter.setCancelFlag(true);
        }

    }

}
