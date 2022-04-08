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

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;

import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.presentation.edittabledata.CursorQueryExecutor;
import org.opengauss.mppdbide.presentation.edittabledata.QueryResultMaterializer;
import org.opengauss.mppdbide.utils.ConvertTimeStampValues;
import org.opengauss.mppdbide.utils.ConvertTimeValues;
import org.opengauss.mppdbide.utils.DateTimeFormatValidator;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.ResultSetDatatypeMapping;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class ExportCursorExecuteVisitor.
 *
 * @since 3.0.0
 */
public class ExportCursorExecuteVisitor implements ICursorExecuteRecordVisitor {

    private Path path;

    private String encoding;

    private String safeSheetName;

    private String fileFormat;

    private ExportExcelApachePOI exportExcel;

    private int rowNo = 1;

    private List<ColumnDataType> columnDatatype;

    private int columnCount;

    private long totalRows;

    private boolean isOLAP;

    private int outParaIndex = 0;

    /**
     * Instantiates a new export cursor execute visitor.
     *
     * @param path the path
     * @param encoding the encoding
     * @param fileFormat the file format
     * @param safeSheetName the safe sheet name
     * @param isOLAP the is OLAP
     */
    public ExportCursorExecuteVisitor(Path path, String encoding, String fileFormat, String safeSheetName,
            boolean isOLAP) {
        this.path = path;
        this.encoding = encoding;
        this.safeSheetName = safeSheetName;
        this.fileFormat = fileFormat;
        this.isOLAP = isOLAP;
    }

    /**
     * Visit record.
     *
     * @param rs the rs
     * @param isFirstBatch the is first batch
     * @param isFirstBatchFirstRecord the is first batch first record
     * @return the long
     * @throws ParseException the parse exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord,
            boolean isFuncProcExport) throws ParseException, MPPDBIDEException {
        try {
            // Only for the firstbatch and firstrecord, exportexcel object will
            // be created and it will be reused further.
            long rowsCount = writeToExcel(rs, columnCount, columnDatatype, isFirstBatch, null, null, false,
                    isFuncProcExport);
            totalRows += rowsCount;
        } catch (SQLException ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET), ex);
            throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET);
        }
        return totalRows;
    }

    /**
     * Visit record.
     *
     * @param rs the rs
     * @param isFirstBatch the is first batch
     * @param isFirstBatchFirstRecord the is first batch first record
     * @return the long
     * @throws ParseException the parse exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public long visitRecord(ResultSet rs, boolean isFirstBatch, boolean isFirstBatchFirstRecord,
            ArrayList<DefaultParameter> inputDailogValueList, ArrayList<Object> outResultList,
            boolean isCursorResultSet, boolean isFuncProcExport) throws ParseException, MPPDBIDEException {
        try {
            // Only for the firstbatch and firstrecord, exportexcel object will
            // be created and it will be reused further.

            int size = 0;
            if (inputDailogValueList != null) {
                size = inputDailogValueList.size();
            }
            long rowsCount = 0;
            if (isFuncProcExport) {
                for (int i = 0; i < size; i++) {
                    rowsCount = writeToExcel(rs, columnCount, columnDatatype, isFirstBatch, inputDailogValueList.get(i),
                            outResultList, isCursorResultSet, isFuncProcExport);
                    totalRows += rowsCount;
                }
            }
            rowsCount = writeToExcel(rs, columnCount, columnDatatype, isFirstBatch, null, outResultList,
                    isCursorResultSet, isFuncProcExport);
            totalRows += rowsCount;
        } catch (SQLException ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET), ex);
            throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET);
        }
        return totalRows;

    }

    /**
     * Gets the header of record.
     *
     * @param rs the rs
     * @param isFirstBatch the is first batch
     * @return the header of record
     * @throws DatabaseOperationException the database operation exception
     */
    @Override
    public void getHeaderOfRecord(ResultSet rs, boolean isFirstBatch, boolean isFuncProcExport)
            throws DatabaseOperationException {
        try {
            // Only for the firstbatch and firstrecord, exportexcel object will
            // be created and it will be reused further.
            ArrayList<DefaultParameter> inputDailogValueList = getInputDailogValue();
            if (isFirstBatch && rs != null && rs.getMetaData() != null) {
                if (isFuncProcExport) {
                    columnCount = MPPDBIDEConstants.FUNC_PROC_COLUMN_COUNT;
                    columnDatatype = getColumnDatatype(columnCount, rs, inputDailogValueList);
                } else {
                    columnCount = rs.getMetaData().getColumnCount();
                    columnDatatype = getColumnDatatype(columnCount, rs, null);
                }

                TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy());

                ThreadLocal<TempFileCreationStrategy> threadLocal = new ThreadLocal<TempFileCreationStrategy>() {
                    @Override
                    protected TempFileCreationStrategy initialValue() {
                        // to do create thread folder
                        return createTempFileCreationStrategy();
                    }
                };
                initColumnHeaderList(rs, isFuncProcExport, threadLocal);

            } else if (inputDailogValueList != null) {
                columnCount = MPPDBIDEConstants.FUNC_PROC_COLUMN_COUNT;
                columnDatatype = getColumnDatatype(columnCount, rs, inputDailogValueList);
                ThreadLocal<TempFileCreationStrategy> threadLocal = new ThreadLocal<TempFileCreationStrategy>() {
                    @Override
                    protected TempFileCreationStrategy initialValue() {
                        // to do create thread folder
                        return createTempFileCreationStrategy();
                    }
                };
                initColumnHeaderList(rs, isFuncProcExport, threadLocal);
            }
        } catch (SQLException ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET));
            throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET);
        }
    }

    private void initColumnHeaderList(ResultSet rs, boolean isFuncProcExport,
            ThreadLocal<TempFileCreationStrategy> threadLocal) throws DatabaseOperationException, SQLException {
        exportExcel = new ExportExcelApachePOI(fileFormat, true);
        List<String> columnHeaderName = new ArrayList<String>(columnCount);
        columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_NAME));
        columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_DATA_TYPE));
        columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_PARAMETER_TYPE));
        columnHeaderName.add(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_COL_VALUE));
        try {
            if (MPPDBIDEConstants.STR_EXCEL_XLSX.equalsIgnoreCase(fileFormat)) {
                TempFileCreationStrategy tempFileCreationStrategy = threadLocal.get();
                if (tempFileCreationStrategy != null) {
                    tempFileCreationStrategy.createTempFile("", "");
                }
            }
            if (exportExcel.checkColLength(columnCount)) {
                exportExcel.createSheet(safeSheetName);
                List<String> headerList = new ArrayList<String>();
                for (int i = 1; i <= columnCount; i++) {
                    if (isFuncProcExport) {
                        headerList.add(columnHeaderName.get(i - 1));
                    } else if (null != rs) {
                        headerList.add(rs.getMetaData().getColumnName(i));
                    }
                }
                exportExcel.createHeaderRow(headerList);
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT));
                throw new DatabaseOperationException(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT);
            }
        } catch (IOException ex) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT), ex);
            throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET, ex);
        }
    }

    /**
     * gets the list of input dailog values
     * 
     * @return the list of InputDailogValue
     */
    public ArrayList<DefaultParameter> getInputDailogValue() {
        ArrayList<DefaultParameter> olapInputValueList = CursorQueryExecutor.getInputDailogValueList();
        ArrayList<DefaultParameter> oltpInputValueList = QueryResultMaterializer.getInputDailogValueList();
        if (olapInputValueList != null && !olapInputValueList.isEmpty()) {
            return olapInputValueList;
        }
        if (oltpInputValueList != null && !oltpInputValueList.isEmpty()) {
            return oltpInputValueList;
        }
        return null;
    }

    /**
     * Creates the temp file creation strategy.
     *
     * @return the temp file creation strategy
     */
    private TempFileCreationStrategy createTempFileCreationStrategy() {
        return new TempFileCreationStrategy() {

            @Override
            public File createTempFile(String arg0, String arg1) throws IOException {
                Path pathParent = path.getParent();
                File dir = null;
                if (pathParent != null) {
                    System.setProperty("java.io.tmpdir", pathParent.normalize().toString());
                    String stdizedPath = null;
                    try {
                        stdizedPath = new File(pathParent.toString()).getCanonicalPath();
                    } catch (IOException ex) {
                        MPPDBIDELoggerUtility.error("Invalid File Path", ex);
                    }
                    if (stdizedPath != null) {
                        System.setProperty("java.io.tmpdir", stdizedPath);

                        if (dir == null && FileValidationUtils.validateFilePathName(MPPDBIDEConstants.TEMP_FILE_PATH)) {
                            dir = new File(MPPDBIDEConstants.TEMP_FILE_PATH, stdizedPath);
                        }
                    }
                    return dir;
                } else {
                    return path.toFile();
                }
            }

            @Override
            public File createTempDirectory(String arg0) throws IOException {
                return null;
            }
        };
    }

    /**
     * Title: enum Description: The Enum ColumnDataType.
     *
     * @since 17 May, 2019
     */
    public enum ColumnDataType {

        /**
         * The double.
         */
        DOUBLE,

        /**
         * The date.
         */
        DATE,

        /**
         * The calendar.
         */
        CALENDAR,

        /**
         * The boolean.
         */
        BOOLEAN,

        /**
         * The string.
         */
        STRING,
        
        /**
         * The TIME
         */
        TIME,

        /**
         * The timestamp.
         */
        TIMESTAMP,

        /**
         * The blob.
         */
        BLOB,

        /**
         * The cursor.
         */
        CURSOR,

        /**
         * The bytea.
         */
        BYTEA,
        
        /**
         * the SMALLINT
         */
        SMALLINT,
        
        /**
         * The money
         */
        MONEY, 
        
        /**
         * The bit
         */
        BIT
    }

    /**
     * Gets the column datatype.
     *
     * @param colCount the col count
     * @param rs the rs
     * @return the column datatype
     * @throws SQLException ColumnDatatype will be stored in a list so that
     * while setCellValue we can check for the datatype for the particular
     * rowValue
     */
    private List<ColumnDataType> getColumnDatatype(int colCount, ResultSet rs,
            ArrayList<DefaultParameter> inputDailogValueList) throws SQLException {
        List<ColumnDataType> columnList = new ArrayList<ColumnDataType>();
        String columnType = null;
        for (int index = 1; index <= colCount; index++) {

            if (inputDailogValueList != null && !inputDailogValueList.isEmpty()) {
                if (index <= 3) {
                    columnType = inputDailogValueList.get(0).getDefaultParameterType();
                } else if (rs != null) {
                    columnType = rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount())
                            .toLowerCase(Locale.ENGLISH);
                }
            } else if (rs != null) {
                columnType = rs.getMetaData().getColumnTypeName(index).toLowerCase(Locale.ENGLISH);
            }
            prepareColumnList(columnList, columnType);
        }
        return columnList;
    }

    private void prepareColumnList(List<ColumnDataType> columnList, String columnType) {
        switch (columnType) {
            case "float8": {
                columnList.add(ColumnDataType.DOUBLE);
                break;
            }
            case "float4": {
                columnList.add(ColumnDataType.DOUBLE);
                break;
            }
            case "date": {
                columnList.add(ColumnDataType.DATE);
                break;
            }
            case "timestamp": {
                columnList.add(ColumnDataType.DATE);
                break;
            }
            case "timestamptz": {
                columnList.add(ColumnDataType.DATE);
                break;
            }
            case "bool": {
                columnList.add(ColumnDataType.BOOLEAN);
                break;
            }
            case "boolean": {
                columnList.add(ColumnDataType.BOOLEAN);
                break;
            }
            case "blob": {
                columnList.add(ColumnDataType.BLOB);
                break;
            }
            case "refcursor": 
                columnList.add(ColumnDataType.CURSOR);
                
                // fall through       
            case MPPDBIDEConstants.BYTEA: {
                columnList.add(ColumnDataType.BYTEA);
                break;
            }
            default: {
                columnList.add(ColumnDataType.STRING);
                break;
            }
        }
    }

    /**
     * Write to excel.
     *
     * @param rs the rs
     * @param colCount the col count
     * @param colDatatype the col datatype
     * @param isFirstBatch the is first batch
     * @return the long
     * @throws ParseException the parse exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private long writeToExcel(ResultSet rs, int colCount, List<ColumnDataType> colDatatype, boolean isFirstBatch,
            DefaultParameter inputDailogValueList, ArrayList<Object> outResultList, boolean isCursorResultSet,
            boolean isFuncProcExport)
            throws ParseException, DatabaseOperationException, SQLException, MPPDBIDEException {
        long rowsCount = 0;
        if (null != exportExcel && exportExcel.checkRowLength(rowNo) && exportExcel.checkColLength(colCount)) {
            // From resultset, list will be made for a single row and then
            // setCellValue is done for that particular row
            boolean needEncode = null != encoding && !encoding.isEmpty();

            List<String> rows = new ArrayList<String>();
            String content = null;
            int index = 0;
            try {
                for (index = 1; index <= colCount; ++index) {
                    content = addValuesToRows(rs, colDatatype, inputDailogValueList, outResultList, isCursorResultSet,
                            needEncode, rows, content, index, isFuncProcExport);
                }
                exportExcel.setCellValue(rows, rowNo);
            } catch (ParseException exception) {
                throw new ParseException(IMessagesConstants.ERROR_EXPORT_EXCEL_PARSER, rowNo);
            } catch (Exception exception) {
                if (exception.getMessage().contains(MPPDBIDEConstants.DISK_FULL_ERR_MSG)) {
                    MPPDBIDELoggerUtility.error(
                            MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE),
                            exception);
                    throw new DatabaseOperationException(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE);
                } else {
                    MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE),
                            exception);
                    throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE);
                }
            }
            rowNo++;
            rows.clear();
            rowsCount++;
            return rowsCount;
        } else {
            writeToFile();
            throw new DatabaseOperationException(IMessagesConstants.MAXIMUM_EXCEL_ROW_REACHED);
        }
    }

    private String addValuesToRows(ResultSet rs, List<ColumnDataType> colDatatype,
            DefaultParameter inputDailogValueList, ArrayList<Object> outResultList, boolean isCursorResultSet,
            boolean needEncode, List<String> rows, String content, int index, boolean isFuncProcExport)
            throws SQLException, UnsupportedEncodingException {
        // If the datatype is Blob, [BLOB] watermark is added
        // instead of the content since it can be huge

        if ((colDatatype.size() > index - 1) && colDatatype.get(index - 1) == ColumnDataType.BYTEA && null != rs
                && rs.getBytes(index) != null) {
            rows.add(MPPDBIDEConstants.BYTEA_WATERMARK);
            return content;
        }
        if (inputDailogValueList != null) {
            content = readInputParaContent(inputDailogValueList, index, outResultList);
        } else {
            content = readContent(rs, needEncode, index, isCursorResultSet, isFuncProcExport);
        }
        rows.add(content);
        return content;
    }

    /**
     * Read content.
     *
     * @param rs the rs
     * @param needEncode the need encode
     * @param index the index
     * @return the string
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws SQLException the SQL exception
     */
    private String readContent(ResultSet rs, boolean needEncode, int index, boolean isCursorResultSet,
            boolean isFuncProcExport) throws UnsupportedEncodingException, SQLException {
        if (isFuncProcExport) {
            return readResultTabContent(rs, isCursorResultSet, needEncode, index);
        } else {
            return readTableContent(rs, needEncode, index);
        }

    }

    /**
     * gets the result tab content
     * 
     * @param rs the rs
     * @param isCursorResultSet the isCursorResultSet
     * @param needEncode the needEncode
     * @param index the index
     * @return the result tab content
     * @throws SQLException the SQLException
     * @throws UnsupportedEncodingException
     */
    private String readResultTabContent(ResultSet rs, boolean isCursorResultSet, boolean needEncode, int index)
            throws SQLException, UnsupportedEncodingException {
        if (null == rs) {
            return "";
        }
        switch (index) {

            case 1: {
                return rs.getMetaData().getColumnLabel(rs.getMetaData().getColumnCount());
            }
            case 2: {
                return (String) ResultSetDatatypeMapping.getColumnDataTypeName(rs);
            }
            case 3: {
                return MPPDBIDEConstants.OUT;
            }
            case 4: {
                String typeName = rs.getMetaData().getColumnTypeName(rs.getMetaData().getColumnCount());
                if (isCursorResultSet || MPPDBIDEConstants.REF_CURSOR.equals(typeName)
                        || MPPDBIDEConstants.RECORD.equals(typeName)) {
                    return MPPDBIDEConstants.CURSOR_WATERMARK;
                } else {
                    return rs.getString(rs.getMetaData().getColumnCount());
                }
            }
            default: {
                break;
            }
        }
        return "";
    }

    /**
     * gets the table content
     * 
     * @param rs the rs
     * @param needEncode the needEncode
     * @param index the index
     * @return the  table content
     * @throws SQLException the SQLException
     * @throws UnsupportedEncodingException the UnsupportedEncodingException
     */
    private String readTableContent(ResultSet rs, boolean needEncode, int index)
            throws SQLException, UnsupportedEncodingException {
        if (null == rs) {
            return "";
        }
        if (rs.getMetaData().getColumnType(index) == Types.TIMESTAMP
                || rs.getMetaData().getColumnType(index) == Types.TIMESTAMP_WITH_TIMEZONE) {
            return readTimeStampValue(rs, index);
        }
        if (rs.getMetaData().getColumnType(index) == Types.TIME
                || rs.getMetaData().getColumnType(index) == Types.TIME_WITH_TIMEZONE) {
            return readTimeValue(rs, index);
        }
        if (rs.getMetaData().getColumnType(index) == Types.DATE) {
            return readDateValue(rs, index);
        }
        if (isOLAP && needEncode) {
            byte[] content = rs.getBytes(index);
            if (null == content) {
                return null;
            }
            return new String(content, encoding);
        } else {
            return rs.getString(index);
        }
    }

    private String readDateValue(ResultSet rs, int index) throws SQLException {
        Date date = rs.getDate(index);
        String value = "";
        if (null != date) {
            value = new ConvertTimeStampValues(date.getTime(),
                    BLPreferenceManager.getInstance().getBLPreference().getDateFormat()).toString();
        }
        return value;
    }

    private String readTimeValue(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        String value = "";
        if (null != timestamp) {
            value = new ConvertTimeValues(timestamp.getTime(),
                    BLPreferenceManager.getInstance().getBLPreference().getTimeFormat()).toString();
        }
        return value;
    }

    private String readTimeStampValue(ResultSet rs, int index) throws SQLException {
        Timestamp timestamp = rs.getTimestamp(index);
        String value = "";
        if (null != timestamp) {
            value = new ConvertTimeStampValues(timestamp.getTime(),
                    DateTimeFormatValidator.getDatePlusTimeFormat(
                            BLPreferenceManager.getInstance().getBLPreference().getDateFormat(),
                            BLPreferenceManager.getInstance().getBLPreference().getTimeFormat())).toString();
        }
        return value;
    }

    /**
     * gets the input parameter values
     * 
     * @param inputDailogValue the inputDailogValue
     * @param inputIndex the inputIndex
     * @param outResultList the outResultList
     * @return input parameter content
     */
    private String readInputParaContent(DefaultParameter inputDailogValue, int inputIndex,
            ArrayList<Object> outResultList) {
        switch (inputIndex) {
            case 1: {
                return getParameterName(inputDailogValue, inputIndex);
            }
            case 2: {
                return inputDailogValue.getDefaultParameterType();
            }
            case 3: {
                return inputDailogValue.getDefaultParameterMode().name();
            }
            case 4: {
                if (MPPDBIDEConstants.OUT.equals(inputDailogValue.getDefaultParameterMode().name())) {
                    if (outResultList != null) {
                        return convertObjectValueToString(outResultList.get(outParaIndex++));
                    }
                }
                return inputDailogValue.getDefaultParameterValue();
            }
            default: {
                break;
            }
        }
        return "";

    }

    /**
     * convert object values into string
     * 
     * @param object the object
     * @return the object
     */
    private String convertObjectValueToString(Object object) {
        String value = "";
        if (object instanceof Integer) {
            value = Integer.toString((int) object);
        }

        if (object instanceof ResultSet) {
            value = MPPDBIDEConstants.CURSOR_WATERMARK;
        }
        return value;
    }

    /**
     * gets the parameter name
     * 
     * @param inputDailogValue the inputDailogValue
     * @param inputIndex the inputIndex
     * @return the parameter name
     */
    private String getParameterName(DefaultParameter inputDailogValue, int inputIndex) {
        if (inputDailogValue.getDefaultParameterName() == null
                || inputDailogValue.getDefaultParameterName().isEmpty()) {
            return MPPDBIDEConstants.PARAM + inputIndex;
        }
        return inputDailogValue.getDefaultParameterName();
    }

    /**
     * Clean upworkbook.
     *
     * @throws DatabaseOperationException the database operation exception
     */
    public void cleanUpworkbook() throws DatabaseOperationException {
        if (exportExcel != null && path != null) {
            exportExcel.cleanUpWorkbookPOIFiles(path.toString());
        }
        exportExcel = null;
    }

    /**
     * Write to file.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void writeToFile() throws MPPDBIDEException {
        if (exportExcel != null) {
            exportExcel.writeToWorkbook(path.toString(), encoding);
        }
    }

}
