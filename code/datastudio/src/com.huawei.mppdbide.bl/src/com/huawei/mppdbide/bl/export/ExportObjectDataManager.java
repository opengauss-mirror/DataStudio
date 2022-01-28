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

package com.huawei.mppdbide.bl.export;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ExportObjetDataManager
 * 
 * @since 3.0.0
 */
public class ExportObjectDataManager {
    private static final String CURSOR_QUERY_PREPEND = "CURSOR %s NO SCROLL FOR %s";
    private static final int CURSOR_FETCH_BATCH_SIZE = 1000;
    private static final String FETCH_QUERY = "FETCH FORWARD %d FROM %s";
    private static final String CLOSE_QUERY = "CLOSE %s";
    private int fetchSize;
    private Statement stmt;
    private String cursorName;
    private boolean needAutoCommitReset;
    private DBConnection dbConn;
    private Path path;
    private String encoding;
    private String exportQuery;
    private GenerateCursorExecuteUtil genrateUtil;
    private boolean cancelled = false;

    /**
     * Constructor ExportObjetDataManager
     */
    public ExportObjectDataManager(DBConnection dbConn, Path path, String encoding, String query,
            GenerateCursorExecuteUtil genrateUtil) {
        this(dbConn, path, encoding);
        this.exportQuery = query;
        this.genrateUtil = genrateUtil;
    }

    /**
     * Constructor ExportObjetDataManager
     */
    public ExportObjectDataManager(DBConnection dbConn, Path path, String encoding) {
        this.dbConn = dbConn;
        this.path = path;
        this.encoding = encoding;
    }

    /**
     * exportTableData method
     * 
     * @throws MPPDBIDEException exception
     */
    public void exportTableData() throws MPPDBIDEException {
        this.fetchSize = -1;
        startTxn();
        try {
            executeCursor();
            try {
                fetchFileRecords();
            } finally {
                closeCursor();
            }
        } finally {
            stopTxn();
        }
    }

    /**
     * <Detailed description of function>
     * 
     * @param seqObject object
     * @return string query
     * @throws DatabaseCriticalException exception
     * @throws DatabaseOperationException exception
     */
    public String getSequenceNextValue(SequenceMetadata seqObject)
            throws DatabaseCriticalException, DatabaseOperationException {
        boolean called = false;
        long nextVal = 0;
        String seqDDL;
        long maxValue = 0;
        long minValue = 0;
        String seqMaxQry = String.format(Locale.ENGLISH,
                "SELECT start_value, increment_by, max_value, min_value, is_called FROM %s ;",
                seqObject.getDisplayName());
        ResultSet rs = null;
        try {
            this.stmt = getExportConn().createStatement();
            rs = stmt.executeQuery(seqMaxQry);
            while (rs.next()) {
                maxValue = rs.getLong("max_value");
                minValue = rs.getLong("min_value");
                called = rs.getBoolean("is_called");
            }
            nextVal = getSeqNextValue(seqObject);
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            String msg = exception.getMessage();
            if (msg.contains("Sequence reached maximum value")) {
                nextVal = maxValue;
                called = true;
            }
            if (msg.contains("Sequence reached minimum value")) {
                nextVal = minValue;
                called = true;
            }
        } finally {
            try {
                if (stmt != null) {
                    stmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility
                        .error("Error while closing statement or resultset while fetching sequence next value.");
            }
        }
        seqDDL = exportSequenceData(ServerObject.getQualifiedObjectName(seqObject.getName()), nextVal, called);
        return seqDDL;
    }
    
    private long getSeqNextValue(SequenceMetadata seqObject) throws SQLException {
        String seqQuery = String.format(Locale.ENGLISH, "SELECT pg_catalog.nextval(?)");
        IExecTimer timer = new ExecTimer("Start getting next value for sequence");
        timer.start();
        ResultSet rs = null;
        PreparedStatement prStmt = null;
        long nextVal = 0;
        try {
            prStmt = getExportConn().prepareStatement(seqQuery);
            prStmt.setString(1, seqObject.getDisplayName());
            rs = prStmt.executeQuery();
            while (rs.next()) {
                nextVal = rs.getInt("nextval");
            }
        } finally {
            try {
                if (prStmt != null) {
                    prStmt.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility
                        .error("Error while closing statement or resultset while fetching sequence next value.");
            }
        }
        return nextVal;
    }

    private void startTxn() throws DatabaseCriticalException, DatabaseOperationException {
        try {
            if (getExportConn().getAutoCommit()) {
                getExportConn().setAutoCommit(false);
                this.needAutoCommitReset = true;
            }
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        }
    }

    private Connection getExportConn() {
        return dbConn.getConnection();
    }

    private void executeCursor() throws DatabaseCriticalException, DatabaseOperationException {
        IExecTimer timer = new ExecTimer("Start Cursor");
        timer.start();
        try {
            this.stmt = getExportConn().createStatement();
            stmt.execute(getCursorQuery());
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        } finally {
            timer.stopAndLogNoException();
        }
    }

    private void fetchFileRecords() throws MPPDBIDEException {
        IExecTimer timer = new ExecTimer("fetch records");
        timer.start();
        int fetchedBatSize = 0;
        int currFetchSize = 0;
        int toFethRowCount = 0;

        for (int index = 0; index < this.fetchSize || this.fetchSize < 1;) {
            if (this.fetchSize == -1) {
                // Fetch All case.
                currFetchSize = CURSOR_FETCH_BATCH_SIZE;
            } else {
                toFethRowCount = this.fetchSize - index;
                currFetchSize = toFethRowCount > CURSOR_FETCH_BATCH_SIZE ? CURSOR_FETCH_BATCH_SIZE : toFethRowCount;
            }
            boolean isFirstBatch = false;
            if (fetchedBatSize == 0) {
                isFirstBatch = true;
            }
            fetchedBatSize = fetchSQLFileRecordBatch(currFetchSize, isFirstBatch);
            index += fetchedBatSize;
            writeToSqlFile(genrateUtil.getOutPutInsertSql().toString());
            genrateUtil.getOutPutInsertSql().delete(0, genrateUtil.getOutPutInsertSql().length());
            genrateUtil.getOutPutInsertSql().setLength(0);
            if (fetchedBatSize < currFetchSize) {
                timer.stop();
                return;
            }
        }
        timer.stopAndLogNoException();
    }

    private void closeCursor() {
        IExecTimer timer = new ExecTimer("close cursor");
        timer.start();
        try {
            this.stmt.execute(getCloseCursorQuery());
        } catch (SQLException ex) {
            // Ignore. No way to recover from close failure.
            MPPDBIDELoggerUtility.error("Error closing a cursor.", ex);
        } finally {
            dbConn.closeStatement(stmt);
            timer.stopAndLogNoException();
        }
    }

    private String getCloseCursorQuery() {
        return String.format(Locale.ENGLISH, CLOSE_QUERY, getUniqCursorName());
    }

    private String getCursorQuery() {
        return String.format(Locale.ENGLISH, CURSOR_QUERY_PREPEND, getUniqCursorName(), exportQuery);
    }

    /**
     * To be changed to a uniq name. __DS_QRY_CRSR_<TIMESTAMP>__
     *
     * @return the uniq cursor name
     */
    public String getUniqCursorName() {
        if (null == cursorName) {
            String timeStamp = new SimpleDateFormat("HHmmssSSS").format(new Date());
            cursorName = "__DS_GET_EXPORT_DATA_FOR_TABLE_CRSR_" + timeStamp + "__";
        }

        return cursorName;
    }

    private void stopTxn() {
        if (!needAutoCommitReset) {
            return;
        }
        IExecTimer timer = new ExecTimer("Stop transaction");
        timer.start();

        try {
            Connection sqlConnection = getExportConn();
            // Set auto commit would issue a commit before changing the flag.
            // Its a cautious call that "select fns()", might do an DML
            // operation and need commit.
            sqlConnection.setAutoCommit(true);
        } catch (SQLException exception) {
            // Ignore. Not mechanism to recover from this failure.
            MPPDBIDELoggerUtility.error(
                    "Stop transaction after query execution failed. " + "No way to recover, and skiping this error",
                    exception);
        }
        timer.stopAndLogNoException();
    }

    /**
     * writeToSqlFile write queries to file
     * 
     * @param listOFQueries query list
     * @throws MPPDBIDEException exception
     */
    public void writeToSqlFile(String listOFQueries) throws MPPDBIDEException {
        try {
            Files.write(path, listOFQueries.getBytes(this.encoding), StandardOpenOption.APPEND);
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);
            throw new MPPDBIDEException(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);
        }
    }

    private int fetchSQLFileRecordBatch(int currentFetchSize, boolean isFirstBatch) throws MPPDBIDEException {
        ResultSet rs = null;
        int recordFetchCounter = 0;
        try {
            rs = stmt.executeQuery(String.format(Locale.ENGLISH, FETCH_QUERY, currentFetchSize, getUniqCursorName()));
            // header
            while (rs.next()) {
                recordFetchCounter++;
                // data
                if (!cancelled) {
                    genrateUtil.getAllRowsCount(rs, isFirstBatch);
                    if (recordFetchCounter == currentFetchSize) {
                        break;
                    }
                } else {
                    MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                    throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
                }
            }
            return recordFetchCounter;
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        } finally {
            closeResultSet(rs);
        }
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException ex) {
            MPPDBIDELoggerUtility.error("Resultset close failed while materializing the records");
        }
    }

    /**
     * exportSequenceData export sequence data ddl
     * 
     * @param name seq name
     * @param nextValue seq next value
     * @param called is end of value
     * @return string ddl
     */
    public String exportSequenceData(String name, long nextValue, boolean called) {
        String queryDDL = String.format(Locale.ENGLISH, "SELECT pg_catalog.setVal('%s',%d,%b);", name, nextValue,
                called);
        return queryDDL;
    }

    /**
     * clean data
     */
    public void cleanData() {
        genrateUtil.cleanOutputInsertSql();
    }
}
