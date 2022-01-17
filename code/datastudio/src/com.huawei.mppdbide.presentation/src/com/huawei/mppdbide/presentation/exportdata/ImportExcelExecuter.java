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

package com.huawei.mppdbide.presentation.exportdata;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.BooleanUtils;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.ExportCursorExecuteVisitor.ColumnDataType;
import com.huawei.mppdbide.utils.ConvertTimeValues;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ImportExcelExecuter
 * 
 * @since 3.0.0
 */
public class ImportExcelExecuter {
    private DBConnection dbCon;
    private ImportExportOption importExportoptions;
    private ServerObject importExportServerObj;
    private ImportExcelApachePOI importExcelPOI;
    private boolean cancelled = false;
    private static final String DEFAULT_BOOL_VALUE = "FALSE";
    private static final int EXCEL_PARAM_MAXIMUM = 10000;

    /**
     * Instantiates a new import excel executer.
     *
     * @param queryForImport the query for import
     * @param dbCon the db con
     * @param importExportoptions the import exportoptions
     * @param importExportServerObj the import export server obj
     */
    public ImportExcelExecuter(DBConnection dbCon, ImportExportOption importExportoptions,
            ServerObject importExportServerObj) {
        this.dbCon = dbCon;
        this.importExportoptions = importExportoptions;
        this.importExportServerObj = importExportServerObj;
        this.importExcelPOI = new ImportExcelApachePOI(importExportoptions);
    }

    /**
     * Execute exc imp.
     *
     * @return the int
     * @throws DatabaseOperationException the database operation exception
     * @Title: executeExcImp
     * @Description: Import data execution method
     */
    public int executeExcImp() throws DatabaseOperationException {
        int talCount = 0;
        boolean failFlag = false;
        try {
            List<ArrayList<String>> countList = importExcelPOI.getCellValues();
            talCount = countList.size();
            int excelCountMaximum = getPeriodExcuteMaxCount(countList);
            if (!countList.isEmpty()) {
                dbCon.getConnection().setAutoCommit(false);
                if (excelCountMaximum == 0) {
                    periodExecuteExc(countList);
                } else {
                    do {
                        List<ArrayList<String>> subList = countList.subList(0, excelCountMaximum);
                        periodExecuteExc(subList);
                        countList.subList(0, excelCountMaximum).clear();
                    } while (countList.size() > excelCountMaximum);
                    periodExecuteExc(countList);
                }
                dbCon.getConnection().commit();
            }
        } catch (SQLException | DateTimeParseException exception) {
            failFlag = true;
            handleException(exception);
        } catch (IOException ioException) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE), ioException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, ioException);
        } finally {
            try {
                if (failFlag) {
                    dbCon.getConnection().rollback();
                }
            } catch (SQLException sqlException) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE),
                        sqlException);
            }
        }
        return talCount;
    }

    /**
     * Gets the period excute max count.
     *
     * @param countList the count list
     * @return the period excute max count
     * @Title: getPeriodExcuteMaxCount
     * @Description: Get the maximum number of rows executed by the segment
     */
    private int getPeriodExcuteMaxCount(List<ArrayList<String>> countList) {
        long allParam = countList.size() * (long) importExportoptions.getTablecolumns().size();
        if (allParam > EXCEL_PARAM_MAXIMUM) {
            double periodCount = Math
                    .floor(EXCEL_PARAM_MAXIMUM / (double) (importExportoptions.getTablecolumns().size()));
            return (int) periodCount;
        }
        return 0;
    }

    /**
     * Period execute exc.
     *
     * @param subList the sub list
     * @throws DatabaseOperationException the database operation exception
     * @Title: periodExecuteExc
     * @Description: Period execute excel data
     */

    private void periodExecuteExc(List<ArrayList<String>> subList) throws DatabaseOperationException {
        List<ColumnDataType> columnDataType = getColumnDatatype();
        String newQuery = composeBatchFormatSql(columnDataType.size(), subList, columnDataType);
        try {
            setValuesPeriodExecuteExc(subList, columnDataType, newQuery);
        } catch (RuntimeException runex) {
            throw runex;
        } catch (Exception exception) {
            handleException(exception);
        }
    }

    private void setValuesPeriodExecuteExc(List<ArrayList<String>> subList, List<ColumnDataType> columnDataType,
            String newQuery)
            throws DatabaseCriticalException, DatabaseOperationException, 
            UnsupportedEncodingException, SQLException, ParseException {
        PreparedStatement preState = dbCon.getPrepareStmt(newQuery);
        int kcount = 1;
        try {
            for (int index = 0; index < subList.size(); index++) {
                kcount = setValuesPeriodExecuteExcInner(subList, columnDataType, preState, kcount, index);
            }
            preState = dbCon.getPrepareStmt(preState.toString());
            validateForCancel(preState);
        } finally {
            dbCon.closeStatement(preState);
        }
    }

    private int setValuesPeriodExecuteExcInner(List<ArrayList<String>> subList, List<ColumnDataType> columnDataType,
            PreparedStatement preState, int kcount, int index) 
            throws UnsupportedEncodingException, SQLException, ParseException {
        int retCount = kcount;
        for (int j = 0; j < columnDataType.size(); j++) {
            String cellValue = getCellValue(subList, index, j);
            switch (columnDataType.get(j)) {
                case SMALLINT: {
                    preState.setObject(retCount++, Short.parseShort(cellValue));
                    break;
                }
                case BOOLEAN: {
                    retCount = setBooleanValue(preState, retCount, cellValue);
                    break;
                }
                case MONEY: {
                    retCount = setMoneyTypeValue(preState, retCount, cellValue);
                    break;
                }
                case TIME: {
                    retCount = setTimeValue(preState, retCount, cellValue);	
                    break;
                }
                case DATE: {
                    retCount = setDateValue(preState, retCount, cellValue);
                    break;
                }
                case TIMESTAMP: {
                    retCount = setTimeStampValue(preState, retCount, cellValue);
                    break;
                }
                case BLOB: {
                    Blob blob = null;
                    preState.setBlob(retCount++, blob);
                    break;
                }
                case BYTEA: {
                    preState.setBytes(retCount++, null);
                    break;
                }
                default: {
                    retCount = setDefaultValue(preState, retCount, cellValue);
                    break;
                }
            }
        }
        return retCount;
    }

    private int setDefaultValue(PreparedStatement preState, int kcount, String cellValue) throws SQLException {
        int retCount = kcount;
        if (null == cellValue || cellValue.isEmpty()) {
            preState.setObject(retCount++, null);
        } else {
            preState.setString(retCount++, cellValue);
        }
        return retCount;
    }

    private int setMoneyTypeValue(PreparedStatement preState, int kcount, String cellValue) throws SQLException {
        int retCount = kcount;
        String value = cellValue;
        if (null == value || value.isEmpty()) {
            preState.setObject(retCount++, null);
        } else {
            if (value.charAt(0) == '$') {
                value = value.substring(1, value.length());
            }
            preState.setObject(retCount++, value, java.sql.Types.NUMERIC, 2);
        }
        return retCount;
    }

    private int setTimeStampValue(PreparedStatement preState, int kcount, String cellValue) throws SQLException {
        int retCount = kcount;
        if (null == cellValue || cellValue.isEmpty()) {
            preState.setTimestamp(retCount++, null);
        } else {
            DateTimeFormatter fommatter = getDateTimeFormatter();
            LocalDateTime dateTime;
            dateTime = LocalDateTime.parse(cellValue, fommatter);
            if (dateTime != null) {
                Timestamp ts = Timestamp.valueOf(dateTime); 
                preState.setTimestamp(retCount++, ts);
            }   
        }
        return retCount;
    }

    private int setDateValue(PreparedStatement preState, int kcount, String cellValue) 
        throws SQLException, ParseException {
        int retCount = kcount;
        if (null == cellValue || cellValue.isEmpty()) {
            preState.setDate(retCount++, null);
        } else {
            String timeFormat = getDateFormatValue(importExportoptions.getDateSelector());
            SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
            java.sql.Date date = new java.sql.Date(sdf.parse(cellValue).getTime());
            preState.setDate(retCount++, date);
        }
        return retCount;
    }

    private int setTimeValue(PreparedStatement preState, int kcount, String cellValue)
        throws SQLException, ParseException {
        int retCount = kcount;
        String timeFormat = getTimeFormatValue(importExportoptions.getDateSelector());
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        if (null == cellValue || cellValue.isEmpty()) {
            preState.setTimestamp(retCount++, null);
        } else {
            Timestamp timestamp = new Timestamp(sdf.parse(cellValue).getTime());
            ConvertTimeValues value = new ConvertTimeValues(timestamp.getTime(), timeFormat);
            preState.setTime(retCount++, value);
        }
        return retCount;
    }

    private String getDateFormatValue(String dateSelector) {
        String[] formatValues = dateSelector.split(" ");
        return formatValues[0];
    }

    private String getTimeFormatValue(String dateSelector) {
        String[] formatValues = dateSelector.split(" ");
        return formatValues[formatValues.length - 1];
    }

    private void handleException(Exception exception) throws DatabaseOperationException {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE),
                exception);
        throw new DatabaseOperationException(IMessagesConstants.ERR_DATABASE_OPERATION_FAILURE, exception);
    }

    private String getCellValue(List<ArrayList<String>> subList, int index, int j) throws UnsupportedEncodingException {
        String cellValue = String.valueOf(subList.get(index).get(j));
        cellValue = getCelEncode(cellValue);
        return cellValue;
    }

    private DateTimeFormatter getDateTimeFormatter() {
        DateTimeFormatter fommatter = DateTimeFormatter.ofPattern(importExportoptions.getDateSelector(),
                Locale.ENGLISH);
        return fommatter;
    }

    private void validateForCancel(PreparedStatement preState) throws SQLException, DatabaseOperationException {
        if (!cancelled) {
            preState.execute();
        } else {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
            throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
        }
    }

    private int setBooleanValue(PreparedStatement preState, int kcount, String cellValue) throws SQLException {
        if (cellValue.equalsIgnoreCase("")) {
            cellValue = DEFAULT_BOOL_VALUE;
        }
        preState.setBoolean(kcount++, BooleanUtils.toBooleanObject(cellValue));
        return kcount;
    }

    /**
     * Compose batch format sql.
     *
     * @param colCount the col count
     * @param subList the sub list
     * @return the string
     * @Title: composeBatchFormatSql
     * @Description: Compose batch import excel format SQL
     */
    private String composeBatchFormatSql(int colCount, List<ArrayList<String>> subList, 
        List<ColumnDataType> columnDataType) {
        StringBuffer queryBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (colCount > 0) {
            String query = composeImportExcelQuery(columnDataType, subList.get(0));
            queryBuff.append(query);
            // insert into tab_name values(?,?,?)
            for (int index = 1; index < subList.size(); index++) {
                queryBuff.append(MPPDBIDEConstants.COMMA_SEPARATE);
                queryBuff.append(MPPDBIDEConstants.LEFT_PARENTHESIS);
                for (int j = 0; j < colCount; j++) {  
                    if (ColumnDataType.BIT.equals(columnDataType.get(j))) {
                        String bitColValue = subList.get(index).get(j);
                        if (bitColValue.length() > 1) {
                            queryBuff.append("?::bit(" + bitColValue.length() + ")");
                        } else {
                            queryBuff.append("?::bit");
                        }
                        
                    } else {
                        queryBuff.append("?");
                    }
                    queryBuff.append(MPPDBIDEConstants.COMMA_SEPARATE);
                }
                queryBuff.deleteCharAt(queryBuff.length() - 1);
                queryBuff.append(MPPDBIDEConstants.RIGHT_PARENTHESIS);
            }
        }
        return queryBuff.toString();
    }

    private String composeImportExcelQuery(List<ColumnDataType> columnDataType, ArrayList<String> colValueList) {
        StringBuffer queryBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        queryBuff.append("INSERT");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        queryBuff.append("INTO");
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        queryBuff.append(this.importExportServerObj.getDisplayName());
        queryBuff.append(MPPDBIDEConstants.SPACE_CHAR);
        if (!importExportoptions.isAllColunms()) {
            queryBuff.append(MPPDBIDEConstants.LEFT_PARENTHESIS);
            queryBuff.append(AbstractImportExportDataCore.getSelectedColumn(importExportoptions.getTablecolumns()));
            queryBuff.append(MPPDBIDEConstants.RIGHT_PARENTHESIS);
        }
        queryBuff.append("VALUES");
        queryBuff.append(MPPDBIDEConstants.LEFT_PARENTHESIS);
        queryBuff.append(formatSelectedColumn(importExportoptions.getTablecolumns(), columnDataType, colValueList));
        queryBuff.append(MPPDBIDEConstants.RIGHT_PARENTHESIS);
        return queryBuff.toString();
    }
    
    /**
     * Format selected column.
     *
     * @param tablecolumns the tablecolumns
     * @return the object
     * @Title: formatSelectedColumn
     * @Description: Format table column value
     */
    private String formatSelectedColumn(ArrayList<String> tablecolumns, List<ColumnDataType> columnDataType,
                                        List<String>colValueList) {
        StringBuilder strBuild = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (int i = 0; i < tablecolumns.size(); i++) {
            if (ColumnDataType.BIT.equals(columnDataType.get(i))) {
                String bitColValue = colValueList.get(i);
                if (bitColValue.length() > 1) {
                    strBuild.append("?::bit(" + bitColValue.length() + ")");
                } else {
                    strBuild.append("?::bit");
                }
            } else {
                strBuild.append("?");
            }
            strBuild.append(MPPDBIDEConstants.COMMA_SEPARATE);
        }
        if (tablecolumns.size() > 0) {
            strBuild.deleteCharAt(strBuild.length() - 1);
        }
        return strBuild.toString();
    }

    /**
     * Gets the cel encode.
     *
     * @param cell the cell
     * @return the cel encode
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @Title: getCelEncode
     * @Description: Get the transcode string
     */
    private String getCelEncode(String cell) throws UnsupportedEncodingException {
        if (null != importExportoptions.getEncoding() && !importExportoptions.getEncoding().isEmpty()) {
            byte[] content = cell.getBytes(importExportoptions.getEncoding());
            return new String(content, importExportoptions.getEncoding());
        }
        return cell;
    }

    /**
     * Gets the column datatype.
     *
     * @return the column datatype
     * @Title: getColumnDatatype
     * @Description: Get the data type of the data table column
     */
    private List<ColumnDataType> getColumnDatatype() {
        ArrayList<ColumnDataType> columnDataTypeList = new ArrayList<ColumnDataType>();
        ArrayList<String> columnTypeList = new ArrayList<String>();
        List<String> columns = importExportoptions.getTablecolumns();
        if (importExportServerObj instanceof TableMetaData) {
            for (int cnt = 0; cnt < columns.size(); cnt++) {
                ColumnMetaData columnMetaData = ((TableMetaData) importExportServerObj).getColumns()
                        .get(columns.get(cnt));
                if (columnMetaData != null) {
                    columnTypeList.add(columnMetaData.getDataTypeName());
                }  
            }
        }
        handleColumnDataType(columnDataTypeList, columnTypeList);
        return columnDataTypeList;
    }

    private void handleColumnDataType(ArrayList<ColumnDataType> columnDataTypeList, ArrayList<String> columnTypeList) {
        for (int indx = 0; indx < columnTypeList.size(); indx++) {
            String columnType = columnTypeList.get(indx);
            switch (columnType) {
                case "int2": {
                    columnDataTypeList.add(ColumnDataType.SMALLINT);
                    break;
                }
                case "bool": {
                    columnDataTypeList.add(ColumnDataType.BOOLEAN);
                    break;
                }
                case "money": {
                    columnDataTypeList.add(ColumnDataType.MONEY);
                    break;
                }
                case "bit": {
                    columnDataTypeList.add(ColumnDataType.BIT);
                    break;
                }
                case "time":
                case "timetz": {
                    columnDataTypeList.add(ColumnDataType.TIME);
                    break;
                }
                case "date": {
                    columnDataTypeList.add(ColumnDataType.DATE);
                    break;
                }
                case "timestamp":
                case "timestamptz":
                case "TIMESTAMP": {
                    columnDataTypeList.add(ColumnDataType.TIMESTAMP);
                    break;
                }
                case "BLOB": {
                    columnDataTypeList.add(ColumnDataType.BLOB);
                    break;
                }
                case MPPDBIDEConstants.BYTEA: {
                    columnDataTypeList.add(ColumnDataType.BYTEA);
                    break;
                }
                default: {
                    columnDataTypeList.add(ColumnDataType.STRING);
                    break;
                }
            }
        }
    }

    /**
     * Sets the cancel flag.
     *
     * @param iscancel the new cancel flag
     * @Title: setCancelFlag
     * @Description: Set cancel query flag
     */
    public void setCancelFlag(boolean iscancel) {
        this.cancelled = iscancel;
    }
}
