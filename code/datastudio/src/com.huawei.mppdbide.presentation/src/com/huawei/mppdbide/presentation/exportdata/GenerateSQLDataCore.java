/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.Observable;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class GenerateSQLDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
