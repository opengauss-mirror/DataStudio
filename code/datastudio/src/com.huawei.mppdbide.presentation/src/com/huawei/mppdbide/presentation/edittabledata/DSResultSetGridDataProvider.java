/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.edittabledata;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.adapter.gauss.StmtExecutor;
import com.huawei.mppdbide.bl.preferences.BLPreferenceManager;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.IQueryResult;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataAccessListenable;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.IDSResultRowVisitor;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.ResultSetDatatypeMapping;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.utils.stringparse.IServerMessageParseUtils;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSResultSetGridDataProvider.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class DSResultSetGridDataProvider
        implements IDSGridDataProvider, IDSGridDataAccessListenable, IDSResultRowVisitor {

    /**
     * The query result.
     */
    protected IQueryResult queryResult;

    private IResultConfig config;

    /**
     * The summary.
     */
    protected IQueryExecutionSummary summary;

    private IDSGridColumnProvider columnDataProvider;

    private List<IDSGridDataRow> rows;

    private boolean isEndOfRecordsReached;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;

    private boolean isResultTabDirty = false;

    private Database db;

    private String encoding = null;

    private boolean isIncludeEncoding = false;

    private boolean isEncodingChanged = false;

    private boolean editSupported = true;

    private boolean olapCursorExec = false;

    private boolean isFuncProcExport = false;

    /**
     * Instantiates a new DS result set grid data provider.
     *
     * @param result the result
     * @param rsConfig the rs config
     * @param summary the summary
     */
    public DSResultSetGridDataProvider(IQueryResult result, IResultConfig rsConfig, IQueryExecutionSummary summary) {
        this.queryResult = result;
        this.config = rsConfig;
        this.summary = summary;
        this.rows = new ArrayList<IDSGridDataRow>(5);
        this.eventTable = new DSEventTable();
    }

    /**
     * Inits the.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public void init() throws DatabaseOperationException, DatabaseCriticalException {
        DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
        colData.init(queryResult);
        this.columnDataProvider = colData;
        getNextBatch();
    }

    /**
     * Inits the.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException
     */
    @Override
    public void init(IQueryResult iQueryResult, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
        boolean isSingleFuncProcExecution = getSingleFuncProcExecution(iQueryResult);
        if (debugInputValueList != null || isSingleFuncProcExecution) {
            colData.initColHeaderName(null, isCallableStmt, iQueryResult);
        } else {
            colData.init(queryResult);
        }
        this.columnDataProvider = colData;
        if (isSingleFuncProcExecution || debugInputValueList != null) {
            getNextBatch(debugInputValueList);
            if (debugInputValueList != null) {
                setFuncProcExport(true);
            }

        } else {
            getNextBatch();
        }

    }

    /**
     * gets boolean isFuncProcExport
     * 
     * @return isFuncProcExport the isFuncProcExport flag
     */
    public boolean isFuncProcExport() {
        return isFuncProcExport;
    }

    /**
     * sets boolean isFuncProcExport
     * 
     * @param isFuncProcExport the isFuncProcExport
     */
    public void setFuncProcExport(boolean isFuncProcExport) {
        this.isFuncProcExport = isFuncProcExport;
    }

    private boolean getSingleFuncProcExecution(IQueryResult iQueryResult) throws DatabaseOperationException {
        StmtExecutor stmtExecutor = iQueryResult.getStmtExecutor();
        String queryWithoutCommnent = null;
        if (stmtExecutor != null) {
            queryWithoutCommnent = IServerMessageParseUtils.extractQueryWithoutComments(stmtExecutor.getQuery());
            if (queryWithoutCommnent.startsWith("EXEC") || queryWithoutCommnent.startsWith("exec")
                    || queryWithoutCommnent.startsWith("CALL") || queryWithoutCommnent.startsWith("call")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Inits the by visitor.
     *
     * @return the IDS result row visitor
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public IDSResultRowVisitor initByVisitor(boolean isfuncProcResultFlow)
            throws DatabaseOperationException, DatabaseCriticalException {
        DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
        if (isfuncProcResultFlow) {
            colData.initByVisitorColHeaderName(null, false, queryResult);
        } else {
            colData.init(queryResult);
        }

        this.columnDataProvider = colData;

        return this;
    }

    /**
     * Update data to rows.
     *
     * @param values the values
     */
    protected void updateDataToRows(Object[][] values) {
        for (int rowIndex = 0; rowIndex < values.length; rowIndex++) {
            this.rows.add(createRowFromValues(values[rowIndex]));
        }

        summary.setNumRecordsFetched(this.rows.size());
    }

    /**
     * Close.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {
        this.queryResult.closeStament();
    }

    /**
     * Pre destroy.
     */
    @Override
    public void preDestroy() {
        if (eventTable != null) {
            this.eventTable.unhookall();
        }
        if (queryResult != null) {
            this.queryResult.closeStament();
            this.queryResult = null;
        }
        this.rows = new ArrayList<IDSGridDataRow>(5);
        this.config = null;
        this.columnDataProvider = null;
        this.summary = null;
        this.eventTable = null;
    }

    /**
     * Gets the next batch.
     *
     * @return the next batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        /* refetch cleanup? */
        ResultSetDatatypeMapping.setIncludeEncoding(isIncludeEncoding);
        Object[][] values = this.queryResult.getNextObjectRecordBatch(this.config.getFetchCount());
        updateDataToRows(values);
        actionAfterNextBatch();
        return rows;
    }

    /**
     * Gets the next batch.
     *
     * @return the next batch
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        /* refetch cleanup? */
        ResultSetDatatypeMapping.setIncludeEncoding(isIncludeEncoding);
        Object[][] inputValues = null;
        int regOutParaIndex = 0;
        int size = 0;
        ArrayList<Object[]> rowsList = new ArrayList<>();
        if (debugInputValueList != null) {
            size = debugInputValueList.size();
        }
        for (int i = 0; i < size; i++) {
            if (MPPDBIDEConstants.OUT.equals(debugInputValueList.get(i).getDefaultParameterMode().name())) {
                regOutParaIndex++;
                rowsList.add(getInputRowValues(debugInputValueList.get(i), regOutParaIndex, i + 1));
            } else {
                rowsList.add(getInputRowValues(debugInputValueList.get(i), 0, i + 1));
            }
        }

        inputValues = rowsList.toArray(new Object[rowsList.size()][this.columnDataProvider.getColumnCount()]);
        updateDataToRows(inputValues);

        int columnCount = this.columnDataProvider.getColumnCount();
        if (queryResult.getResultsSet() != null) {
            Object[][] values = this.queryResult.getNextObjectRecordBatch(this.config.getFetchCount(), columnCount,
                    debugInputValueList != null, true);
            updateDataToRows(values);
        }
        actionAfterNextBatch();
        return rows;
    }

    private void actionAfterNextBatch() throws DatabaseOperationException, DatabaseCriticalException {
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_POST_FETCH, summary));

        this.isEndOfRecordsReached = this.queryResult.isEndOfRecordsReached();

        if (this.isEndOfRecordsReached) {
            eventTable.sendEvent(new DSEvent(LISTEN_TYPE_ENDOF_RS, summary));
        }

        /* Commit & close not possible? */
        switch (this.config.getActionAfterFetch()) {
            case ISSUE_ROLLBACK_CONNECTION_AFTER_FETCH: {
                this.queryResult.rollback();
                break;
            }
            case ISSUE_COMMIT_CONNECTION_AFTER_FETCH: {
                this.queryResult.commitConnection();
                break;
            }
            case CLOSE_CONNECTION_AFTER_FETCH: {
                this.close();
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Gets the all fetched rows.
     *
     * @return the all fetched rows
     */
    @Override
    public List<IDSGridDataRow> getAllFetchedRows() {
        return rows;
    }

    /**
     * Checks if is end of records.
     *
     * @return true, if is end of records
     */
    @Override
    public boolean isEndOfRecords() {
        return this.isEndOfRecordsReached;
    }

    /**
     * Gets the record count.
     *
     * @return the record count
     */
    @Override
    public int getRecordCount() {
        return rows.size();
    }

    /**
     * Adds the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    @Override
    public void addListener(int type, IDSListener listener) {
        eventTable.hook(type, listener);
    }

    /**
     * Removes the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    @Override
    public void removeListener(int type, IDSListener listener) {
        if (eventTable != null) {
            eventTable.unhook(type, listener);
        }
    }

    /**
     * Gets the column data provider.
     *
     * @return the column data provider
     */
    @Override
    public IDSGridColumnProvider getColumnDataProvider() {
        return this.columnDataProvider;
    }

    /**
     * Gets the summary.
     *
     * @return the summary
     */
    public IQueryExecutionSummary getSummary() {
        return summary;
    }

    /**
     * Gets the data provider config.
     *
     * @return the data provider config
     */
    public IResultConfig getDataProviderConfig() {
        return config;
    }

    /**
     * Gets the query results.
     *
     * @return the query results
     */
    public IQueryResult getQueryResults() {
        return this.queryResult;
    }

    /**
     * Gets the column group provider.
     *
     * @return the column group provider
     */
    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {
        return null;
    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    @Override
    public ServerObject getTable() {
        return null;
    }

    /**
     * Gets the result tab dirty flag.
     *
     * @return the result tab dirty flag
     */
    @Override
    public boolean getResultTabDirtyFlag() {
        return this.isResultTabDirty;
    }

    /**
     * Sets the result tab dirty flag.
     *
     * @param flag the new result tab dirty flag
     */
    @Override
    public void setResultTabDirtyFlag(boolean flag) {
        this.isResultTabDirty = flag;
    }

    /**
     * Gets the databse.
     *
     * @return the databse
     */
    @Override
    public Database getDatabse() {

        return this.db;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(Database database) {
        this.db = database;
    }

    /**
     * Gets the encoding.
     *
     * @return the encoding
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Change encoding.
     *
     * @param newEncoding the new encoding
     */
    public void changeEncoding(String newEncoding) {
        this.encoding = newEncoding.trim();
        if (!this.encoding.trim().equals(BLPreferenceManager.getInstance().getBLPreference().getDSEncoding().trim())) {
            setEncodingChanged(true);
        } else {
            setEncodingChanged(false);
        }

        setEncodingForRow(rows, newEncoding.trim());
    }

    /**
     * Sets the encoding for row.
     *
     * @param rows2 the rows 2
     * @param newEncoding the new encoding
     */
    protected void setEncodingForRow(List<IDSGridDataRow> rows2, String newEncoding) {
        for (IDSGridDataRow row : rows2) {
            if (row instanceof DSResultSetGridDataRow) {
                // Checking instance for one record is sufficient, but static
                // tools may not think so.
                DSResultSetGridDataRow rsRow = (DSResultSetGridDataRow) row;
                rsRow.setEncoding(newEncoding);
            }
        }
    }

    /**
     * Checks if is include encoding.
     *
     * @return true, if is include encoding
     */
    public boolean isIncludeEncoding() {
        return isIncludeEncoding;
    }

    /**
     * Sets the include encoding.
     *
     * @param isEncodingIncluded the new include encoding
     */
    public void setIncludeEncoding(boolean isEncodingIncluded) {
        this.isIncludeEncoding = isEncodingIncluded;
        ResultSetDatatypeMapping.setIncludeEncoding(isEncodingIncluded);
        setIncludeEncodingForRow(rows);
    }

    /**
     * Sets the include encoding for row.
     *
     * @param rows2 the new include encoding for row
     */
    protected void setIncludeEncodingForRow(List<IDSGridDataRow> rows2) {
        for (IDSGridDataRow row : rows2) {
            if (row instanceof DSResultSetGridDataRow) {
                // Checking instance for one record is sufficient, but static
                // tools may not think so.
                DSResultSetGridDataRow rsRow = (DSResultSetGridDataRow) row;
                rsRow.setIncludeEncoding(isIncludeEncoding());
            }
        }
    }

    /**
     * Checks if is encoding changed.
     *
     * @return true, if is encoding changed
     */
    public boolean isEncodingChanged() {
        return isEncodingChanged;
    }

    /**
     * Sets the encoding changed.
     *
     * @param isDSEncodingChanged the new encoding changed
     */
    public void setEncodingChanged(boolean isDSEncodingChanged) {
        this.isEncodingChanged = isDSEncodingChanged;
    }

    /**
     * Visit.
     *
     * @param rs the rs
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException
     */
    @Override
    public void visit(ResultSet rs) throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        if (null == this.columnDataProvider) {
            DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
            colData.init(queryResult);
            this.columnDataProvider = colData;
        }

        Object[] rowValues = null;
        rowValues = getRowValues(rs);
        IDSGridDataRow row = createRowFromValues(rowValues);
        this.rows.add(row);
        summary.setNumRecordsFetched(this.rows.size());
    }

    /**
     * Visit.
     *
     * @param rs the rs
     * @param isfuncProcResultFlow the function proc flow
     * @param isPopupCursorType the popup cursor type
     * @param valueList the list of input values
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException
     */
    @Override
    public void visit(ResultSet rs, boolean isfuncProcResultFlow, boolean isPopupCursorType, Object[] valueList)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        if (null == this.columnDataProvider) {
            DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
            colData.init(queryResult);
            this.columnDataProvider = colData;
        }

        Object[] rowValues = null;
        rowValues = getFuncProcRowValues(rs);
        IDSGridDataRow row = createRowFromValues(rowValues);
        this.rows.add(row);
        if (!isPopupCursorType) {
            summary.setNumRecordsFetched(this.rows.size());

        }
    }

    private Object[] getFuncProcRowValues(ResultSet rs)
            throws SQLException, NumberFormatException, DatabaseOperationException {
        boolean isShowCursorPopup = getShowCursorPopup(rs);
        int columnCount = this.columnDataProvider.getColumnCount();
        Object[] row = new Object[columnCount];
        int colIndex;
        for (colIndex = 1; colIndex < columnCount; colIndex++) {
            row[colIndex - 1] = ResultSetDatatypeMapping.getFuncProcColObjectExceptValue(rs, colIndex, false);
        }
        if (isShowCursorPopup) {
            row[colIndex - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs,
                    rs.getMetaData().getColumnCount());
            List<IDSGridDataRow> cursorGridrow = new ArrayList<IDSGridDataRow>();
            List<List<Object>> cursorRowValues = (List<List<Object>>) row[colIndex - 1];
            Object[] rowValues = null;
            IDSGridDataRow cursorRow = null;
            int size = 0;
            if (cursorRowValues != null) {
                size = cursorRowValues.size();
            }
            for (int i = 0; i < size; i++) {
                rowValues = convertCursorValueToObject(cursorRowValues.get(i));
                cursorRow = createRowFromValues(rowValues);
                cursorGridrow.add(cursorRow);
            }
            row[columnCount - 1] = cursorGridrow;
            this.olapCursorExec = true;

        } else {
            row[colIndex - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs,
                    rs.getMetaData().getColumnCount());
        }
        return row;
    }

    /**
     * Visit the input values
     * 
     * @param dp the default parameter
     * @param index the index
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException
     */
    public void visitInputValues(DefaultParameter dp, int index)
            throws DatabaseOperationException, DatabaseCriticalException {

        Object[] rowValues = null;
        try {
            rowValues = getInputRowValues(dp, 0, index);
        } catch (SQLException exe) {
            GaussUtils.handleCriticalException(exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exe);
        }
        IDSGridDataRow row = createRowFromValues(rowValues);
        this.rows.add(row);
        summary.setNumRecordsFetched(this.rows.size());
    }

    /**
     * Visit the unNamed cursor values
     * 
     * @param rs the result set
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    @Override
    public void visitUnNameCursor(ResultSet rs)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        if (null == this.columnDataProvider) {
            DSResultSetGridColumnDataProvider colData = new DSResultSetGridColumnDataProvider();
            colData.init(queryResult);
            this.columnDataProvider = colData;
        }

        Object[] rowValues = null;
        rowValues = getUnnamedCursorRowValues(rs);
        IDSGridDataRow row = createRowFromValues(rowValues);
        this.rows.add(row);
    }

    private Object[] getUnnamedCursorRowValues(ResultSet rs) throws SQLException, DatabaseCriticalException {
        try {
            int columnCount = this.columnDataProvider.getColumnCount();
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = ResultSetDatatypeMapping.resultSetDataTypeMapDefaultAction(rs, i);
            }
            return row;
        } catch (OutOfMemoryError exe) {
            throw new DatabaseCriticalException(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED, exe);
        }
    }

    /**
     * Creates the row from values.
     *
     * @param rowValues the row values
     * @return the IDS grid data row
     */
    protected IDSGridDataRow createRowFromValues(Object[] rowValues) {
        DSResultSetGridDataRow row = new DSResultSetGridDataRow(this);
        createCursorRowFromValues(rowValues);
        row.setValues(rowValues);
        row.setIncludeEncoding(isIncludeEncoding());
        return row;
    }

    private void createCursorRowFromValues(Object[] rowValues) {
        List<List<Object>> cursorRowValues = null;
        if (!this.olapCursorExec && rowValues.length >= 4 && rowValues[3] instanceof List) {
            List<IDSGridDataRow> cursorGridrow = new ArrayList<IDSGridDataRow>();
            cursorRowValues = (List<List<Object>>) rowValues[3];
            IDSGridDataRow cursorRow = null;
            Object[] cursorRowObject = null;
            for (int i = 0; i < cursorRowValues.size(); i++) {
                cursorRowObject = convertCursorValueToObject(cursorRowValues.get(i));
                cursorRow = handleCursorRowFromValues(cursorRowObject);
                cursorGridrow.add(cursorRow);
            }
            rowValues[3] = cursorGridrow;
        }
    }

    private IDSGridDataRow handleCursorRowFromValues(Object[] rowValues) {
        DSResultSetGridDataRow row = new DSResultSetGridDataRow(this);
        row.setValues(rowValues);
        row.setIncludeEncoding(isIncludeEncoding());
        return row;
    }

    /**
     * Gets the row values.
     *
     * @param rs the rs
     * @return the row values
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    protected Object[] getRowValues(ResultSet rs)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        // Shouldn't be a static variable. Need to revisit this.
        ResultSetDatatypeMapping.setIncludeEncoding(isIncludeEncoding);
        try {
            int columnCount = this.columnDataProvider.getColumnCount();
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = ResultSetDatatypeMapping.getReadColumnValueObject(rs, i);
            }
            return row;
        } catch (OutOfMemoryError exe) {
            throw new DatabaseCriticalException(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED, exe);
        }
    }

    private Object[] convertCursorValueToObject(List<Object> cursorRowValue) {
        int columnCount = cursorRowValue.size();
        Object[] row = new Object[columnCount];
        for (int i = 0; i < columnCount; i++) {
            row[i] = cursorRowValue.get(i);
        }
        return row;
    }

    private boolean getShowCursorPopup(ResultSet rs) throws SQLException {
        if (MPPDBIDEConstants.REF_CURSOR.equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))
                || MPPDBIDEConstants.RECORD
                        .equals(rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount()))) {
            return true;
        }
        return false;
    }

    /**
     * gets the input row values
     * 
     * @param dp the dp
     * @param regOutParaIndex the regOutParaIndex
     * @param index the index
     * @return the input row values
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     * @throws SQLException the SQLException
     */
    protected Object[] getInputRowValues(DefaultParameter dp, int regOutParaIndex, int index)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        // Shouldn't be a static variable. Need to revisit this.
        ResultSetDatatypeMapping.setIncludeEncoding(isIncludeEncoding);
        try {
            int columnCount = this.columnDataProvider.getColumnCount();
            Object[] row = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                row[i - 1] = getReadInputColumnValueObject(dp, i, regOutParaIndex, index);
            }

            return row;
        } catch (OutOfMemoryError e) {
            throw new DatabaseCriticalException(IMessagesConstants.ERR_MSG_OUT_OF_MEMORY_ERROR_OCCURRED, e);
        }
    }

    /**
     * gets the input column value object
     * 
     * @param dp the dp
     * @param columnIndex the columnIndex
     * @param regOutParaIndex the regOutParaIndex
     * @param inParamIndex the inParamIndex
     * @return the column value object
     * @throws SQLException the SQLException
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    public Object getReadInputColumnValueObject(DefaultParameter dp, int columnIndex, int regOutParaIndex,
            int inParamIndex) throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        Object colValue = "";
        switch (columnIndex) {
            case 1: {
                colValue = getParameterName(dp, inParamIndex);
                break;
            }
            case 2: {
                colValue = dp.getDefaultParameterType();
                break;
            }
            case 3: {
                colValue = dp.getDefaultParameterMode().name();
                break;
            }
            case 4: {
                if (MPPDBIDEConstants.OUT.equals(dp.getDefaultParameterMode().name())) {
                    colValue = getOutParameterValue(dp, regOutParaIndex);
                } else {
                    colValue = dp.getDefaultParameterValue();
                }
                break;
            }
            default: {
                break;
            }
        }
        return colValue;
    }

    /**
     * gets the parameter name
     * 
     * @param dp the dp
     * @param inParamIndex the inParamIndex
     * @return the parameter name
     */
    private Object getParameterName(DefaultParameter dp, int inParamIndex) {
        Object colValue = "";
        colValue = dp.getDefaultParameterName();
        if (colValue == null) {
            return MPPDBIDEConstants.PARAM + inParamIndex;
        }
        return colValue;
    }

    /**
     * gets the value of Out parameter value
     * 
     * @param dp the default parameter
     * @param regOutParaIndex the index of registered out parameter
     * @return the out parameter value
     * @throws SQLException the SQLException
     * @throws DatabaseOperationException the DatabaseOperationException
     * @throws DatabaseCriticalException the DatabaseCriticalException
     */
    private Object getOutParameterValue(DefaultParameter dp, int regOutParaIndex)
            throws SQLException, DatabaseCriticalException, DatabaseOperationException {
        String query = summary.getQuery();
        ArrayList<Object> outResultList = StmtExecutor.getOutResultList();
        boolean isCallableStmt = StmtExecutor.isCallableStmtExecuted();
        DBConnection conn = queryResult.getConnection();
        StmtExecutor stmt = null;
        try {
            if (conn != null) {
                stmt = new StmtExecutor(query, conn);
                stmt.execute();

                ResultSet rs = stmt.getResultSet();
                if (rs != null && MPPDBIDEConstants.CURSOR.equals(dp.getDefaultParameterType())) {
                    return ResultSetDatatypeMapping.convertResultSetToObject(rs, rs.getMetaData().getColumnCount(),
                            true, true);
                } else {
                    if (regOutParaIndex > 0) {
                        return stmt.getCalStmt().getObject(regOutParaIndex);
                    }

                }
            } else if (isCallableStmt) {
                Object resObject = (Object) outResultList.get(regOutParaIndex - 1);
                if (resObject instanceof ResultSet) {
                    return ResultSetDatatypeMapping.convertResultSetToObject((ResultSet) resObject,
                            ((ResultSet) resObject).getMetaData().getColumnCount(), true, true);
                } else {
                    return outResultList.get(regOutParaIndex - 1);
                }
            }
        } finally {
            if (null != stmt) {
                stmt.closeResultSet();
                stmt.closeStatement();
            }
        }

        return "";
    }

    /**
     * Sets the end of records.
     */
    @Override
    public void setEndOfRecords() {
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_POST_FETCH, summary));
        this.isEndOfRecordsReached = true;
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_ENDOF_RS, summary));
    }

    /**
     * Checks if is edits the supported.
     *
     * @return true, if is edits the supported
     */
    public boolean isEditSupported() {
        return editSupported;
    }

    /**
     * Sets the edits the supported.
     *
     * @param isEditSupported the new edits the supported
     */
    public void setEditSupported(boolean isEditSupported) {
        this.editSupported = isEditSupported;
    }

    /**
     * Adds the default to col data provider.
     *
     * @param rows the rows
     */
    public void addDefaultToColDataProvider(Map<String, String> rows) {
        ((DSResultSetGridColumnDataProvider) this.columnDataProvider).addDefaultValues(rows);
    }

    @Override
    public void visit(DSResultSetGridDataRow gridDataRow)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {

    }
}
