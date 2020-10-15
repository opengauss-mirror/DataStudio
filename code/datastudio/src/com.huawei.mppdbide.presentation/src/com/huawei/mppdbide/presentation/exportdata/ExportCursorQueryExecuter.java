/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.edittabledata.CursorQueryExecutor;
import com.huawei.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import com.huawei.mppdbide.utils.IMessagesConstants;
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
 * Description: The Class ExportCursorQueryExecuter.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ExportCursorQueryExecuter {

    private boolean needAutoCommitReset;
    private Statement stmt;
    private String cursorName;
    private int fetchSize;
    private static final String CURSOR_QUERY_PREPEND = "CURSOR %s NO SCROLL FOR %s";
    private static final int CURSOR_FETCH_BATCH_SIZE = 1000;
    private static final String FETCH_QUERY = "FETCH FORWARD %d FROM %s";
    private static final String CLOSE_QUERY = "CLOSE %s";
    private long totalRows;
    private boolean cancelled = false;
    private String queryForExport;
    private DBConnection currentConnection;

    /**
     * Instantiates a new export cursor query executer.
     *
     * @param queryForExport the query for export
     * @param currentConnection the current connection
     */
    public ExportCursorQueryExecuter(String queryForExport, DBConnection currentConnection) {
        this.queryForExport = queryForExport;
        this.currentConnection = currentConnection;
    }

    /**
     * Export excel data.
     *
     * @param visitor the visitor
     * @return the long
     * @throws ParseException the parse exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public long exportExcelData(ExportCursorExecuteVisitor visitor, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException {
        this.fetchSize = -1;
        startTxn();
        try {
            executeCursor();
            try {
                fetchRecords(visitor, isFuncProcExport);
            } finally {
                closeCursor();
            }
        } finally {
            stopTxn();
        }

        return totalRows;
    }

    /**
     * Export SQL data.
     *
     * @param visitor the visitor
     * @return the long
     * @throws ParseException the parse exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public long exportSQLData(GenerateCursorExecuteVisitor visitor) throws ParseException, MPPDBIDEException {
        this.fetchSize = -1;
        startTxn();
        try {
            executeCursor();
            try {
                fetchFileRecords(visitor);
            } finally {
                closeCursor();
            }
        } finally {
            stopTxn();
        }

        return totalRows;
    }

    private DBConnection getDBConnection() {
        return currentConnection;
    }

    private Connection getSqlConnection() {
        return getDBConnection().getConnection();
    }

    private void startTxn() throws DatabaseCriticalException, DatabaseOperationException {
        try {
            if (getSqlConnection().getAutoCommit()) {
                // if already inside a transaction, no need to reset.
                getSqlConnection().setAutoCommit(false);
                this.needAutoCommitReset = true;
            }
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        }
    }

    private void executeCursor() throws DatabaseCriticalException, DatabaseOperationException {
        IExecTimer timer = new ExecTimer("Start Cursor");
        timer.start();
        try {
            this.stmt = getSqlConnection().createStatement();
            stmt.execute(getCursorQuery());
        } catch (SQLException ex) {
            GaussUtils.handleCriticalException(ex);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ex);
        } finally {
            timer.stopAndLogNoException();
        }
    }

    private String getCursorQuery() {
        return String.format(Locale.ENGLISH, CURSOR_QUERY_PREPEND, getUniqCursorName(), queryForExport);
    }

    /**
     * To be changed to a uniq name. __DS_QRY_CRSR_<TIMESTAMP>__
     *
     * @return the uniq cursor name
     */
    public String getUniqCursorName() {
        if (null == cursorName) {
            String timeStamp = new SimpleDateFormat("HHmmssSSS").format(new Date());
            cursorName = "__DS_QRY_CRSR_" + timeStamp + "__";
        }

        return cursorName;
    }

    private void fetchRecords(ICursorExecuteRecordVisitor visitor, boolean isFuncProcExport)
            throws MPPDBIDEException, ParseException {
        IExecTimer timer = new ExecTimer("fetch records");
        timer.start();
        int fetchedBatchSize = 0;
        int currentFetchSize = 0;
        int toFetchRowCount = 0;

        for (int index = 0; index < this.fetchSize || this.fetchSize < 1;) {
            if (this.fetchSize == -1) {
                // Fetch All case.
                currentFetchSize = CURSOR_FETCH_BATCH_SIZE;
            } else {
                toFetchRowCount = this.fetchSize - index;
                currentFetchSize = toFetchRowCount > CURSOR_FETCH_BATCH_SIZE ? CURSOR_FETCH_BATCH_SIZE
                        : toFetchRowCount;
            }
            boolean isFirstBatch = false;
            if (fetchedBatchSize == 0) {
                isFirstBatch = true;
            }
            fetchedBatchSize = fetchRecordBatch(currentFetchSize, visitor, isFirstBatch, isFuncProcExport);
            index += fetchedBatchSize;
            if (fetchedBatchSize < currentFetchSize) {
                ((ExportCursorExecuteVisitor) visitor).writeToFile();
                timer.stop();
                return;
            }
        }
        timer.stopAndLogNoException();
    }

    private void fetchFileRecords(ICursorExecuteRecordVisitor visitor) throws MPPDBIDEException, ParseException {
        IExecTimer timer = new ExecTimer("fetch records");
        timer.start();
        int fetchedBatchSize = 0;
        int currentFetchSize = 0;
        int toFetchRowCount = 0;

        for (int index = 0; index < this.fetchSize || this.fetchSize < 1;) {
            if (this.fetchSize == -1) {
                // Fetch All case.
                currentFetchSize = CURSOR_FETCH_BATCH_SIZE;
            } else {
                toFetchRowCount = this.fetchSize - index;
                currentFetchSize = toFetchRowCount > CURSOR_FETCH_BATCH_SIZE ? CURSOR_FETCH_BATCH_SIZE
                        : toFetchRowCount;
            }
            boolean isFirstBatch = false;
            if (fetchedBatchSize == 0) {
                isFirstBatch = true;
            }
            fetchedBatchSize = fetchSQLFileRecordBatch(currentFetchSize, visitor, isFirstBatch);
            index += fetchedBatchSize;
            if (fetchedBatchSize < currentFetchSize) {
                ((GenerateCursorExecuteVisitor) visitor).writeToSqlFile();
                timer.stop();
                return;
            }
        }
        timer.stopAndLogNoException();
    }

    /**
     * Sets the cancel flag.
     *
     * @param iscancel the new cancel flag
     */
    public void setCancelFlag(boolean iscancel) {
        this.cancelled = iscancel;
    }

    private int fetchSQLFileRecordBatch(int currentFetchSize, ICursorExecuteRecordVisitor visitor, boolean isFirstBatch)
            throws MPPDBIDEException, ParseException {
        ResultSet rs = null;
        int recordFetchCounter = 0;
        try {
            rs = stmt.executeQuery(getFetchQuery(currentFetchSize));
            visitor.getHeaderOfRecord(rs, isFirstBatch, false);
            // header
            while (rs.next()) {
                boolean isFirstBatchFirstRecord = false;
                recordFetchCounter++;
                if (recordFetchCounter == 1) {
                    isFirstBatchFirstRecord = true;
                }
                // data
                if (!cancelled) {

                    this.totalRows = visitor.visitRecord(rs, isFirstBatch, isFirstBatchFirstRecord, false);
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

    private int fetchRecordBatch(int currentFetchSize, ICursorExecuteRecordVisitor visitor, boolean isFirstBatch,
            boolean isFuncProcExport) throws MPPDBIDEException, ParseException {
        ResultSet rs = null;
        int recordFetchCounter = 0;
        try {
            rs = stmt.executeQuery(getFetchQuery(currentFetchSize));
            visitor.getHeaderOfRecord(rs, isFirstBatch, isFuncProcExport);

            ArrayList<DefaultParameter> inputDailogValueList = getInputDailogValue();
            boolean isRecordType = false;
            while (rs.next() && !isRecordType) {
                boolean isFirstBatchFirstRecord = false;
                recordFetchCounter++;
                if (recordFetchCounter == 1) {
                    isFirstBatchFirstRecord = true;
                }

                if (MPPDBIDEConstants.RECORD
                        .equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
                    isRecordType = true;
                }
                // data
                if (!cancelled) {
                    this.totalRows = visitor.visitRecord(rs, isFirstBatch, isFirstBatchFirstRecord,
                            inputDailogValueList, null, false, isFuncProcExport);
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

    /**
     * gets the list of input dailog values
     * 
     * @return the list of getInputDailogValue
     */
    public ArrayList<DefaultParameter> getInputDailogValue() {
        ArrayList<DefaultParameter> olapInputValueList = null;
        if (currentConnection.isOLAPConnection()) {
            olapInputValueList = CursorQueryExecutor.getInputDailogValueList();
        }

        if (olapInputValueList != null && !olapInputValueList.isEmpty()) {
            return olapInputValueList;
        }
      
        return null;
    }

    private void closeResultSet(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException ex) {
            // Ignore. Nothing can be done to recover.
            MPPDBIDELoggerUtility.debug("Resultset close failed while materializing the records");
        }
    }

    private String getFetchQuery(int currentFetchSize) {
        return String.format(Locale.ENGLISH, FETCH_QUERY, currentFetchSize, getUniqCursorName());
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
            getDBConnection().closeStatement(stmt);
            timer.stopAndLogNoException();
        }
    }

    private String getCloseCursorQuery() {
        return String.format(Locale.ENGLISH, CLOSE_QUERY, getUniqCursorName());
    }

    private void stopTxn() {
        if (!needAutoCommitReset) {
            return;
        }
        IExecTimer timer = new ExecTimer("Stop transaction");
        timer.start();

        try {
            Connection sqlConnection = getSqlConnection();
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

}
