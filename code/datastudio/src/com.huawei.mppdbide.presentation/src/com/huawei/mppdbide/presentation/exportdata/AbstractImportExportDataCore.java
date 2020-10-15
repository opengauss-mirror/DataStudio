/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation.exportdata;

import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.importexportdata.ImportExportDataExecuter;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor.ColumnDataType;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.TableImporExportException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractImportExportDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 6 September, 2019
 */
public abstract class AbstractImportExportDataCore extends Observable {

    /**
     * The import export server obj.
     */
    protected ServerObject importExportServerObj;

    /**
     * The import exportoptions.
     */
    protected ImportExportOption importExportoptions;

    /**
     * The import export executer.
     */
    protected ImportExportDataExecuter importExportExecuter;

    /**
     * The db con.
     */
    protected DBConnection dbCon;

    /**
     * The executed query.
     */
    protected String executedQuery;

    /**
     * The path.
     */
    protected Path path;

    /**
     * The display table name.
     */
    protected String displayTableName;

    /**
     * The visitor.
     */
    protected ExportCursorExecuteVisitor visitor;

    /**
     * The is OLAP.
     */
    protected boolean isOLAP;

    /**
     * The import excel executer.
     */
    protected ImportExcelExecuter importExcelExecuter;

    /**
     * The temp file path.
     */
    protected Path tempFilePath;

    /**
     * The query time.
     */
    protected String queryTime;
    private ArrayList<String> columns;
    private ArrayList<String> originalColumns;
    private String terminalName;
    private boolean isExportIsInProgress;
    private String fileLocation;

    /**
     * Instantiates a new abstract import export data core.
     *
     * @param obj the obj
     * @param clmList the clm list
     * @param executedQuery the executed query
     * @param terminalID the terminal ID
     * @param querySubmitTime the query submit time
     */
    public AbstractImportExportDataCore(ServerObject obj, ArrayList<String> clmList, String executedQuery,
            String terminalID, String querySubmitTime) {
        this.importExportServerObj = obj;
        importExportExecuter = new ImportExportDataExecuter();
        importExportoptions = new ImportExportOption();
        this.columns = clmList;
        this.originalColumns = clmList;
        this.executedQuery = executedQuery;
        this.terminalName = terminalID;
        this.queryTime = querySubmitTime;
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
     * Compose excel query.
     *
     * @param queryBuff the query buff
     * @return the string
     */
    protected String composeExcelQuery(StringBuffer queryBuff) {
        if (importExportServerObj instanceof Database) {
            if (!importExportoptions.isAllColunms()) {
                queryBuff.append("SELECT ").append(getSelectedColumn(importExportoptions.getTablecolumns()))
                        .append(" FROM (").append(executedQuery).append(")");
            } else {
                queryBuff.append(executedQuery);
            }
        }
        return queryBuff.toString();
    }

    /**
     * Compose excel query.
     *
     * @return the string
     */
    protected abstract String composeExcelQuery();

    /**
     * Compose query.
     *
     * @return the string
     */
    public String composeQuery() {
        checkForSingleQoutes(importExportoptions);
        StringBuffer queryBuff = new StringBuffer("COPY");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        appendTblNameOrExecutedQuery(queryBuff);

        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        appendQueryForImportOrExport(queryBuff);

        appendFileFormat(queryBuff);
        appendEncoding(queryBuff);
        queryBuff.append(";");
        return queryBuff.toString();
    }

    private void appendEncoding(StringBuffer queryBuff) {
        if (null != importExportoptions.getEncoding() && !importExportoptions.getEncoding().isEmpty()) {
            queryBuff.append("ENCODING");
            stringSpacer(importExportoptions.getEncoding(), queryBuff);
        }
    }

    private void appendFileFormat(StringBuffer queryBuff) {
        if (null != importExportoptions.getFileFormat()) {
            if ("Binary".equalsIgnoreCase(importExportoptions.getFileFormat())) {
                queryBuff.append("BINARY");
                queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            } else if ("Excel(xlsx)".equalsIgnoreCase(importExportoptions.getFileFormat())) {
                queryBuff.append("Excel(xlsx)");
                queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            } else if ("Excel(xls)".equalsIgnoreCase(importExportoptions.getFileFormat())) {
                queryBuff.append("Excel(xls)");
                queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            } else {
                appendQueryFormatForCsvFormat(queryBuff);
            }

        }
    }

    /**
     * Append delimiter option.
     *
     * @param queryBuff the query buff
     */
    protected void appendDelimiterOption(StringBuffer queryBuff) {
        if (importExportServerObj instanceof Database) {
            queryBuff.append("DELIMITER");
        }
    }

    private void appendQueryFormatForCsvFormat(StringBuffer queryBuff) {
        if (null != importExportoptions.getDelimiter() && !importExportoptions.getDelimiter().isEmpty()) {
            appendDelimiterOption(queryBuff);
            stringSpacer(importExportoptions.getDelimiter(), queryBuff);
        }
        if (null != importExportoptions.getReplaceNull() && !importExportoptions.getReplaceNull().isEmpty()) {
            queryBuff.append("NULL");
            stringSpacer(importExportoptions.getReplaceNull(), queryBuff);
        }
        if (importExportoptions.isHeader()) {
            queryBuff.append("HEADER");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        }
        queryBuff.append("CSV");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);

        if (null != importExportoptions.getQuotes() && !importExportoptions.getQuotes().isEmpty()) {
            queryBuff.append("QUOTE");
            stringSpacer(importExportoptions.getQuotes(), queryBuff);
        }
        if (null != importExportoptions.getEscape() && !importExportoptions.getEscape().isEmpty()) {
            queryBuff.append("ESCAPE");
            stringSpacer(importExportoptions.getEscape(), queryBuff);
        }
    }

    private void appendQueryForImportOrExport(StringBuffer queryBuff) {
        if (importExportoptions.isExport()) {
            queryBuff.append("TO");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            queryBuff.append("STDOUT");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        } else {
            queryBuff.append("FROM");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
            queryBuff.append("STDIN");
            queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);

        }
    }

    /**
     * Append tbl name or executed query.
     *
     * @param queryBuff the query buff
     */
    protected void appendTblNameOrExecutedQuery(StringBuffer queryBuff) {
        if (importExportServerObj instanceof Database) {
            queryBuff.append('(');

            if (!importExportoptions.isAllColunms()) {
                queryBuff.append("SELECT ").append(getSelectedColumn(importExportoptions.getTablecolumns()))
                        .append(" FROM (").append(executedQuery).append(")");
            } else {
                queryBuff.append(executedQuery);
            }

            queryBuff.append(')');
        }
    }

    private void checkForSingleQoutes(ImportExportOption importExportOptions) {
        // check to form query if ' is used as input from UI by adding one more
        // ' to the query
        if (null != importExportOptions.getEscape() && importExportOptions.getEscape().equals("'")) {
            importExportOptions.setEscape(MPPDBIDEConstants.ADD_QUOTE);
        }
        if (null != importExportOptions.getReplaceNull() && importExportOptions.getReplaceNull().equals("'")) {
            importExportOptions.setReplaceNull(MPPDBIDEConstants.ADD_QUOTE);
        }
        if (null != importExportOptions.getQuotes() && importExportOptions.getQuotes().equals("'")) {
            importExportOptions.setQuotes(MPPDBIDEConstants.ADD_QUOTE);
        }
        if (null != importExportOptions.getDelimiter() && importExportOptions.getDelimiter().equals("'")) {
            importExportOptions.setDelimiter(MPPDBIDEConstants.ADD_QUOTE);
        }
    }

    /**
     * Gets the selected column.
     *
     * @param selectedColsList the selected cols list
     * @return the selected column
     */
    public static String getSelectedColumn(ArrayList<String> selectedColsList) {
        StringBuilder strBuild = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (String clm : selectedColsList) {
            strBuild.append(ServerObject.getQualifiedObjectName(clm));
            strBuild.append(",");
        }
        if (selectedColsList.size() > 0) {
            strBuild.deleteCharAt(strBuild.length() - 1);
        }
        return strBuild.toString();

    }

    private void stringSpacer(String string, StringBuffer queryBuff) {
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        queryBuff.append(ServerObject.getLiteralName(string));
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
    }

    /**
     * Execute export data.
     *
     * @param conn the conn
     * @return the long
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws ParseException the parse exception
     */
    public long executeExportData(DBConnection conn, boolean isFuncProcExport)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException, ParseException {
        long totalRows = 0;
        try {
            DBConnection currentConnection = getCurrentConnection(conn);

            if (!"Excel(xlsx)".equalsIgnoreCase(importExportoptions.getFileFormat())
                    && !"Excel(xls)".equalsIgnoreCase(importExportoptions.getFileFormat())) {
                totalRows = excuteExportDataExceptExcel(currentConnection);
            } else {

                totalRows = excuteExportExcelData(currentConnection, isFuncProcExport);
            }
        } finally {
            releaseConnection();
        }
        return totalRows;

    }

    /**
     * get current connection
     * 
     * @param conn the DBConnection
     * @return the current connection
     * @throws MPPDBIDEException
     */
    private DBConnection getCurrentConnection(DBConnection conn) throws MPPDBIDEException {
        boolean isExportTable = null == conn;
        DBConnection currentConnection = null;
        if (isExportTable) {
            if (dbCon == null) {
                initializeCore();
            }
            currentConnection = dbCon;
        } else {
            currentConnection = conn;
        }
        return currentConnection;
    }

    /**
     * excute export data except Excel files.
     * 
     * @param currentConnection the current connection
     * @param totalRows total rows
     * @return the total rows
     * @throws MPPDBIDEException the MppDBIDEException
     */
    private long excuteExportDataExceptExcel(DBConnection currentConnection) throws MPPDBIDEException {
        String queryForExport = composeQuery();
        long totalRows = 0;
        if (currentConnection != null) {
            totalRows = importExportExecuter.exportData(importExportoptions.getZip() ? tempFilePath : path,
                    queryForExport, currentConnection, importExportoptions.getEncoding(),
                    importExportoptions.getFileFormat());
        } else {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION);
        }
        return totalRows;
    }

    /**
     * excute export excel data.
     *
     * @param currentConnection the current connection
     * @return the total rows
     * @throws ParseException the ParseException
     * @throws MPPDBIDEException the MPPDBIDEException
     */
    private long excuteExportExcelData(DBConnection currentConnection, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException {
        long totalRows = 0;
        if (currentConnection != null) {
            visitor = new ExportCursorExecuteVisitor(importExportoptions.getZip() ? tempFilePath : path,
                    importExportoptions.getEncoding(), importExportoptions.getFileFormat(), getSafeSheetName(), isOLAP);
            totalRows = getExportExcelTotalRows(currentConnection, isFuncProcExport);
        } else {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION));
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_RESULT_INVALID_CONNECTION);
        }

        return totalRows;
    }

    /** 
     * gets the ExportExcelTotalRows
     * 
     * @param currentConnection the currentConnection
     * @param isFuncProcExport the isFuncProcExport
     * @return the count of exported rows
     * @throws ParseException the ParseException
     * @throws MPPDBIDEException the MPPDBIDEException
     */
    protected abstract long getExportExcelTotalRows(DBConnection currentConnection, boolean isFuncProcExport)
            throws ParseException, MPPDBIDEException;

    /**
     * Validate import export opt parameters.
     *
     * @throws TableImporExportException the table impor export exception
     */
    public void validateImportExportOptParameters() throws TableImporExportException {
        if (!"Excel(xlsx)".equalsIgnoreCase(importExportoptions.getFileFormat())
                && !"Excel(xls)".equalsIgnoreCase(importExportoptions.getFileFormat())
                && !MPPDBIDEConstants.BINARY.equalsIgnoreCase(importExportoptions.getFileFormat())) {
            validateBasicChecks();
            validateNullAndQuotes();
            validateNullAndDelimiter();
            validateDelimiterAndQuotes();
            validateEscapeAndQuotes();
        }
        validateColumns();
    }

    private void validateColumns() throws TableImporExportException {

        if (null != importExportoptions.getTablecolumns() && importExportoptions.getTablecolumns().isEmpty()
                && !importExportoptions.isExport()) {
            MPPDBIDELoggerUtility.error("IMPORT_VALIDATION_QUOTE_NO_COLS_SELECTED");
            throw new TableImporExportException("IMPORT_VALIDATION_QUOTE_NO_COLS_SELECTED");
        }
        if (null != importExportoptions.getTablecolumns() && importExportoptions.getTablecolumns().isEmpty()
                && importExportoptions.isExport()) {
            MPPDBIDELoggerUtility.error("EXPORT_VALIDATION_QUOTE_NO_COLS_SELECTED");
            throw new TableImporExportException("EXPORT_VALIDATION_QUOTE_NO_COLS_SELECTED");
        }
    }

    private void validateBasicChecks() throws TableImporExportException {
        // To check if delimiter is empty
        if (null != importExportoptions.getDelimiter() && importExportoptions.getDelimiter().isEmpty()) {
            MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_OTHER");
            throw new TableImporExportException("IMPORTEXPORT_VALIDATION_OTHER");
        }
        // To check if replace null contains new line or carriage
        // check
        if (null != importExportoptions.getReplaceNull()
                && (importExportoptions.getReplaceNull().matches(System.lineSeparator()))) {
            MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_NULL_STRING");
            throw new TableImporExportException("IMPORTEXPORT_VALIDATION_NULL_STRING");
        }
        // To check if replace null is more than 100 character
        if (null != importExportoptions.getReplaceNull() && (importExportoptions.getReplaceNull().length() > 100)) {
            MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_NULL_LENGTH");
            throw new TableImporExportException("IMPORTEXPORT_VALIDATION_NULL_LENGTH");
        }
    }

    private void validateNullAndQuotes() throws TableImporExportException {
        // To check if replace null and quotes are same
        if (null != importExportoptions.getReplaceNull() && null != importExportoptions.getQuotes()) {
            if (isValidInput(importExportoptions.getReplaceNull(), importExportoptions.getQuotes())) {
                MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_NULL_AS_DELIMITOR");
                throw new TableImporExportException("IMPORTEXPORT_VALIDATION_NULL_AS_DELIMITOR");
            }
        }
    }

    private void validateNullAndDelimiter() throws TableImporExportException {
        // To check if replace null and delimiter are same
        if (null != importExportoptions.getReplaceNull() && null != importExportoptions.getDelimiter()) {
            if (isValidInput(importExportoptions.getReplaceNull(), importExportoptions.getDelimiter())) {
                MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_NULL_AS_DELIMITOR");
                throw new TableImporExportException("IMPORTEXPORT_VALIDATION_NULL_AS_DELIMITOR");
            }
        }
        if (null != importExportoptions.getQuotes()
                && (importExportoptions.getQuotes().length() > 1 || importExportoptions.getEscape().length() > 1)) {
            MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_QUOTE_SINGLE_CHAR");
            throw new TableImporExportException("IMPORTEXPORT_VALIDATION_QUOTE_SINGLE_CHAR");
        }
    }

    private void validateDelimiterAndQuotes() throws TableImporExportException {
        // To check if replace delimiter and quotes are same
        if (null != importExportoptions.getQuotes() && null != importExportoptions.getDelimiter()) {
            if (isValidInput(importExportoptions.getQuotes(), importExportoptions.getDelimiter())) {
                MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_QUOTE_AS_DELI");
                throw new TableImporExportException("IMPORTEXPORT_VALIDATION_QUOTE_AS_DELI");
            }
        }
        if (null != importExportoptions.getDelimiter() && (importExportoptions.getDelimiter().length() > 10)) {
            MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_DELIMITER_LENGTH");
            throw new TableImporExportException("IMPORTEXPORT_VALIDATION_DELIMITER_LENGTH");
        }
        if (null != importExportoptions.getReplaceNull() && null != importExportoptions.getQuotes()) {
            if (importExportoptions.getQuotes().isEmpty()
                    && ("\"".equalsIgnoreCase(importExportoptions.getReplaceNull()))) {
                MPPDBIDELoggerUtility.error("IMPORTEXPORT_VALIDATION_NULL_AS_QUOTE");
                throw new TableImporExportException("IMPORTEXPORT_VALIDATION_NULL_AS_QUOTE");
            }
        }
    }

    private void validateEscapeAndQuotes() throws TableImporExportException {
        // To check if replace Escape and quotes are same
        if (null != importExportoptions.getQuotes() && null != importExportoptions.getEscape()) {
            if (isValidInput(importExportoptions.getQuotes(), importExportoptions.getEscape())) {
                throw new TableImporExportException("IMPORTEXPORT_VALIDATION_QUOTE_AS_ESCAPE");
            }
        }
    }

    private boolean isValidInput(String strfirst, String strsecond) {
        if (!strfirst.isEmpty() && !strsecond.isEmpty()) {
            if (strfirst.equals(strsecond)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Cancel import export operation.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void cancelImportExportOperation() throws DatabaseCriticalException, DatabaseOperationException {
        if (dbCon != null) {
            dbCon.cancelQuery();
        }
        if (importExcelExecuter != null) {
            importExcelExecuter.setCancelFlag(true);
        }

    }

    /**
     * Release connection.
     */
    protected void releaseConnection() {
        if (dbCon != null && importExportServerObj.getConnectionManager() != null) {
            importExportServerObj.getConnectionManager().releaseAndDisconnection(dbCon);
        }
        dbCon = null;
    }

    /**
     * Execute import data.
     *
     * @return the long
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public long executeImportData() throws MPPDBIDEException {
        long totalRows = 0;
        try {
            if (dbCon == null) {
                initializeCore();
            }

            if (!"Excel(xlsx)".equalsIgnoreCase(importExportoptions.getFileFormat())
                    && !"Excel(xls)".equalsIgnoreCase(importExportoptions.getFileFormat())) {
                String query = composeQuery();

                if (null != dbCon) {
                    totalRows = importExportExecuter.importTabledata(query, importExportoptions.getFileName(), dbCon);
                } else {
                    MPPDBIDELoggerUtility.error(
                            MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_RESULT_INVALID_CONNECTION));
                    throw new DatabaseOperationException(IMessagesConstants.IMPORT_RESULT_INVALID_CONNECTION);
                }
            } else {
                // Import excel data execution method
                if (dbCon != null) {
                    importExcelExecuter = new ImportExcelExecuter(dbCon, importExportoptions, importExportServerObj);
                    totalRows = importExcelExecuter.executeExcImp();
                } else {
                    throw new DatabaseOperationException(IMessagesConstants.IMPORT_RESULT_INVALID_CONNECTION);
                }
            }

        } finally {
            releaseConnection();
        }
        return totalRows;
    }

    /**
     * Gets the import exportoptions.
     *
     * @return the import exportoptions
     */
    public ImportExportOption getImportExportoptions() {
        return importExportoptions;
    }

    /**
     * Sets the import exportoptions.
     *
     * @param importExportoptions the new import exportoptions
     */
    public void setImportExportoptions(ImportExportOption importExportoptions) {
        this.importExportoptions = importExportoptions;
    }

    /**
     * Gets the file format.
     *
     * @return the file format
     */
    public String getFileFormat() {
        return importExportoptions.getFileFormat();

    }

    /**
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName() {
        String fileName = null;
        if (importExportServerObj instanceof Database) {
            fileName = getTerminalId() + '_'
                    + CustomStringUtility.convertStringDateFormat(queryTime, MPPDBIDEConstants.DATE_COLLAPSE_FORMAT);
        }
        return fileName;
    }

    /**
     * Gets the safe sheet name.
     *
     * @return the safe sheet name
     */
    public String getSafeSheetName() {
        String fileName = null;
        if (importExportServerObj instanceof Database) {
            fileName = getTerminalId();
        }
        return fileName;
    }

    /**
     * Gets the file path.
     *
     * @return the file path
     */
    public Path getFilePath() {
        return path;
    }

    /**
     * Sets the file path.
     *
     * @param filePath the new file path
     */
    public void setFilePath(Path filePath) {
        this.path = filePath;
    }

    /**
     * Gets the display table name.
     *
     * @return the display table name
     */
    public String getDisplayTableName() {
        if (importExportServerObj instanceof Database) {
            displayTableName = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_QUERY, getDisplayName());

        }
        return displayTableName;
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        if (importExportServerObj instanceof Database) {
            String query = executedQuery;
            if (query.length() > 50) {
                query = query.substring(0, 50) + " ..";
            }
            displayTableName = query + " - " + ((Database) importExportServerObj).getServer().getName();

        }
        return displayTableName;
    }

    /**
     * Gets the progress label name.
     *
     * @return the progress label name
     */
    public String getProgressLabelName() {
        if (importExportServerObj instanceof Database) {
            String query = executedQuery;
            if (query.length() > 50) {
                query = query.substring(0, 50) + " ..";
            }
            displayTableName = query + " @ " + ((Database) importExportServerObj).getServer().getName();

        }
        return displayTableName;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        if (importExportServerObj instanceof Database) {
            return (Database) importExportServerObj;
        }
        return null;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public ArrayList<String> getColumns() {
        return columns;
    }

    /**
     * Sets the export.
     *
     * @param isExport the new export
     */
    public void setExport(boolean isExport) {
        importExportoptions.setExport(isExport);
    }

    /**
     * Gets the import export server obj.
     *
     * @return the import export server obj
     */
    public ServerObject getImportExportServerObj() {
        return importExportServerObj;
    }

    /**
     * Gets the terminal id.
     *
     * @return the terminal id
     */
    public String getTerminalId() {
        return terminalName;
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
     * Gets the original columns.
     *
     * @return the original columns
     */
    public ArrayList<String> getOriginalColumns() {
        return originalColumns;
    }

    /**
     * Gets the file location.
     *
     * @return the file location
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * Sets the file location.
     *
     * @param fileLocation the new file location
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * Clean up.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void cleanUp() throws DatabaseOperationException {
        if (visitor != null) {
            visitor.cleanUpworkbook();
        }
    }

    /**
     * Gets the temp file path.
     *
     * @return the temp file path
     */
    public Path getTempFilePath() {
        return tempFilePath;
    }

    /**
     * Sets the temp file path.
     *
     * @param tempFilePath the new temp file path
     */
    public void setTempFilePath(Path tempFilePath) {
        this.tempFilePath = tempFilePath;
    }

}
