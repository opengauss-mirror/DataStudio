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

package org.opengauss.mppdbide.bl.export;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.ConvertValueToInsertSqlFormat;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.ResultSetDatatypeMapping;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: GenerateCursorExecuteUtil
 * 
 */

public class GenerateCursorExecuteUtil {
    private List<Integer> columnDatatype;
    private int columnCount;
    private List<String> headerList;
    private List<String> rows;
    private String typeName;
    private List<String> columnTypeName;
    private String targetValue = null;
    private ConvertValueToInsertSqlFormat convertValueToInsertSqlFormat;
    private StringBuilder outPutInsertSql;

    private String tableNames;
    private String encoding;
    private boolean isOLAP;

    /**
     * GenerateCursorExecuteUtil generate cursor util
     */
    public GenerateCursorExecuteUtil(String tableNames, String encoding, boolean isOLAP) {
        this.tableNames = tableNames;
        this.encoding = encoding;
        this.isOLAP = isOLAP;

        headerList = new ArrayList<String>();
        convertValueToInsertSqlFormat = new ConvertValueToInsertSqlFormat();
        rows = new ArrayList<String>();
        outPutInsertSql = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
    }

    /**
     * getAllRowsCount get all row
     * 
     * @param rs result set
     * @param isFirstBatch is first batch
     * @return long count
     * @throws DatabaseOperationException db exeception
     * @throws SQLException sql exception
     * @throws MPPDBIDEException mpp exception
     */
    public long getAllRowsCount(ResultSet rs, boolean isFirstBatch)
            throws DatabaseOperationException, SQLException, MPPDBIDEException {
        long rowsCount = 0;
        columnCount = rs.getMetaData().getColumnCount();
        columnDatatype = getColumnDatatype(columnCount, rs);
        columnTypeName = getColumnDatatypeName(columnCount, rs);
        headerList = getHeaderName(rs);
        rows = new ArrayList<String>();

        int cnt = 0;
        boolean suffix = false;

        try {
            outPutInsertSql.append("INSERT INTO ").append(tableNames).append(" (");
            for (cnt = 1; cnt <= columnCount; ++cnt) {
                suffix = addColumnNames(cnt, suffix);
            }
            suffix = false;
            outPutInsertSql.append(")").append(MPPDBIDEConstants.LINE_SEPARATOR).append(" VALUES (");
            suffix = addInsertValues(rs, suffix);
            suffix = false;
            outPutInsertSql.append(");").append(MPPDBIDEConstants.LINE_SEPARATOR);
        } catch (OutOfMemoryError exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE), exception);
            throw new DatabaseOperationException(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE);
        } catch (UnsupportedEncodingException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE);
        }
        rows.clear();
        rowsCount++;
        return rowsCount;
    }

    private boolean addColumnNames(int cnt, boolean suffixParam) {
        boolean suffix = suffixParam;
        targetValue = headerList.get(cnt - 1);
        if (suffix) {
            outPutInsertSql.append(",");
        }
        outPutInsertSql.append(ServerObject.getQualifiedSimpleObjectName(targetValue));
        suffix = true;
        return suffix;
    }

    private boolean addInsertValues(ResultSet rs, boolean suffixParam)
            throws UnsupportedEncodingException, SQLException {
        boolean suffix = suffixParam;
        for (int j = 0; j <= columnCount - 1; ++j) {
            if (suffix) {
                outPutInsertSql.append(",");
            }
            String rowData = readContent(rs, true, j + 1);
            typeName = columnTypeName.get(j);
            outPutInsertSql
                    .append(convertValueToInsertSqlFormat.convertValueToSQL(columnDatatype.get(j), rowData, typeName));
            suffix = true;
        }
        return suffix;
    }

    /**
     * Gets the header name.
     *
     * @param rs the rs
     * @return the header name
     * @throws SQLException the SQL exception
     */
    public List<String> getHeaderName(ResultSet rs) throws SQLException {
        headerList = new ArrayList<String>();
        if (rs.getMetaData() != null) {
            for (int index = 1; index <= columnCount; index++) {
                headerList.add(rs.getMetaData().getColumnName(index));
            }
        }
        return headerList;
    }

    private String readContent(ResultSet rs, boolean needEncode, int index)
            throws UnsupportedEncodingException, SQLException {
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

    /**
     * Gets the column datatype.
     *
     * @param colCount the col count
     * @param rs the rs
     * @return the column datatype
     * @throws SQLException the SQL exception
     */
    public List<Integer> getColumnDatatype(int colCount, ResultSet rs) throws SQLException {
        List<Integer> columnList = new ArrayList<Integer>();
        if (rs.getMetaData() != null) {
            for (int i = 1; i <= colCount; i++) {
                columnList.add(rs.getMetaData().getColumnType(i));
            }
        }
        return columnList;
    }

    /**
     * Gets the column datatype name.
     *
     * @param colCount the col count
     * @param rs the rs
     * @return the column datatype name
     * @throws SQLException the SQL exception
     */
    private List<String> getColumnDatatypeName(int colCount, ResultSet rs) throws SQLException {
        List<String> columnNameList = new ArrayList<String>();
        if (rs.getMetaData() != null) {
            for (int index = 1; index <= colCount; index++) {
                columnNameList.add(rs.getMetaData().getColumnTypeName(index));
            }
        }
        return columnNameList;
    }

    /**
     * @return getOutPutInsertSql sql
     */
    public StringBuilder getOutPutInsertSql() {
        return outPutInsertSql;
    }

    /**
     * cleanOutputInsertSql clean sql
     */
    public void cleanOutputInsertSql() {
        outPutInsertSql = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
    }

}