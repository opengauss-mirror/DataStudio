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

package com.huawei.mppdbide.presentation.objectproperties;

import java.util.List;
import java.util.Map;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesInfoExecuteQueryUtility.
 *
 * @since 3.0.0
 */
public class PropertiesInfoExecuteQueryUtility {

    /**
     * Adds the column query.
     *
     * @param column the column
     * @param conn the conn
     * @param row the row
     */
    public static void addColumnQuery(ColumnMetaData column, DBConnection conn, IDSGridEditDataRow row) {
        if (column != null) {
            try {
                column.execAlterAddColumn(conn);
                if (row.getValue(3) != null) {

                    column.setDescription(column.getObjectFullName(), row.getValue(3).toString(), conn);
                }
                row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
            } catch (DatabaseOperationException e) {

                handleFailureOperations(e, row);
            } catch (DatabaseCriticalException e) {
                handleFailureOperations(e, row);
            }
        }

    }

    /**
     * Adds the constraint query.
     *
     * @param constraint the constraint
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void addConstraintQuery(ConstraintMetaData constraint, DBConnection freeConnection,
            IDSGridEditDataRow row) {

        if (constraint != null) {
            try {

                constraint.execAlterAddConstraint(constraint.getParent(), freeConnection);
                row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
            } catch (DatabaseOperationException e) {

                handleFailureOperations(e, row);
            } catch (DatabaseCriticalException e) {
                handleFailureOperations(e, row);
            }

        }

    }

    /**
     * Handle failure operations.
     *
     * @param exception the e
     * @param row the row
     */
    public static void handleFailureOperations(MPPDBIDEException exception, IDSGridEditDataRow row) {
        row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
        row.setCommitStatusMessage(generateFailureMessages(exception));
    }

    /**
     * Generate failure messages.
     *
     * @param exception the e
     * @return the string
     */
    private static String generateFailureMessages(MPPDBIDEException exception) {
        StringBuilder strBlr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        strBlr.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDIT_TABLE_COMMIT_FAIL));
        strBlr.append("[").append(exception.getServerMessage()).append("]").append(MPPDBIDEConstants.LINE_SEPARATOR);
        strBlr.append("Cancel changes,refresh and try again");

        return strBlr.toString();

    }

    /**
     * Delete column query.
     *
     * @param column the column
     * @param dbConnection the db connection
     * @param row the row
     */
    public static void deleteColumnQuery(ColumnMetaData column, DBConnection dbConnection, IDSGridEditDataRow row) {
        try {
            column.execDrop(dbConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        }
    }

    /**
     * Update column info query.
     *
     * @param dbConnection the db connection
     * @param row the row
     * @param column the column
     */
    public static void updateColumnInfoQuery(DBConnection dbConnection, IDSGridEditDataRow row, ColumnMetaData column) {
        List<Integer> modifiedColumns = row.getModifiedColumns();
        int size = modifiedColumns.size();
        try {
            for (int i = 0; i < size; i++) {
                switch (modifiedColumns.get(i)) {
                    case 0: {
                        setFirstClmData(dbConnection, row, column);
                        break;
                    }
                    case 1: {
                        setSecondClmData(dbConnection, row, column);
                        break;
                    }
                    case 2: {
                        setThirdClmData(dbConnection, row, column);
                        break;
                    }
                    case 3: {
                        setFourthClmData(dbConnection, row, column);
                        break;
                    }
                    default: {
                        break;
                    }

                }
            }

        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        }
    }

    private static void setFourthClmData(DBConnection dbConnection, IDSGridEditDataRow row, ColumnMetaData column)
            throws DatabaseOperationException, DatabaseCriticalException {
        Object obj = row.getValue(3);
        String updatedcomment = "";
        if (obj != null) {
            updatedcomment = row.getValue(3).toString();
        }
        column.setDescription(column.getObjectFullName(), updatedcomment, dbConnection);
        row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
    }

    private static void setThirdClmData(DBConnection dbConnection, IDSGridEditDataRow row, ColumnMetaData column)
            throws DatabaseOperationException, DatabaseCriticalException {
        /*
         * modifiedIsNullValue is used to update the isNotNull value as the
         * existing rows on update does not update the value on server object as
         * the server object for existings rows is null. Same fix has been done
         * in rename column
         */
        Boolean modifiedIsNullValue = (Boolean) row.getValue(2);
        if (modifiedIsNullValue != column.isNotNull()) {
            column.setNotNull(modifiedIsNullValue);
        }
        column.execAlterToggleSetNull(dbConnection);
        row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
    }

    private static void setSecondClmData(DBConnection dbConnection, IDSGridEditDataRow row, ColumnMetaData column)
            throws DatabaseOperationException, DatabaseCriticalException {
        Object value = row.getValue(1);
        setModifiedValuesToColumn(column, value);
        column.execChangeDataType(dbConnection);
        row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
    }

    private static void setFirstClmData(DBConnection dbConnection, IDSGridEditDataRow row, ColumnMetaData column)
            throws DatabaseCriticalException, DatabaseOperationException {
        column.execRename((String) row.getValue(0), dbConnection);
        row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        column.setName((String) row.getValue(0));
    }

    /**
     * Sets the modified values to column.
     *
     * @param column the column
     * @param value the value
     */
    private static void setModifiedValuesToColumn(ColumnMetaData column, Object value) {

        column.setLenOrPrecision(((ObjectPropColumnTabInfo) value).getPrecision());
        column.setScale(((ObjectPropColumnTabInfo) value).getScale());
        column.setDataType(((ObjectPropColumnTabInfo) value).getColDatatype());

        String datatypeSchema = ((ObjectPropColumnTabInfo) value).getDataTypeSchema();
        if (datatypeSchema != null && !datatypeSchema.isEmpty()) {
            column.setDataTypeScheam(datatypeSchema);
        }

    }

    /**
     * Perform refresh.
     *
     * @param propertyName the property name
     * @param objectPropertyObject the object property object
     * @param conn the conn
     * @return the list
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static List<String[]> performRefresh(String propertyName, IServerObjectProperties objectPropertyObject,
            DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {

        switch (propertyName) {
            case "General": {
                Map<String, String> commentsList = ((PropertiesTableImpl) objectPropertyObject).getComments(conn);
                return ((PropertiesTableImpl) objectPropertyObject).getGeneralProperty(conn, commentsList);
            }

            case "Columns": {
                Map<String, String> commentsList1 = ((PropertiesTableImpl) objectPropertyObject).getComments(conn);
                return ((PropertiesTableImpl) objectPropertyObject).getColumnInfo(conn, commentsList1);
            }
            case "Constraints": {
                return ((PropertiesTableImpl) objectPropertyObject).getConstraintInfo(conn);
            }
            case "Index": {
                return ((PropertiesTableImpl) objectPropertyObject).getIndexInfo(conn);
            }
            default: {
                break;
            }

        }
        return null;
    }

    /**
     * Delete constraint query.
     *
     * @param cons the cons
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void deleteConstraintQuery(ConstraintMetaData cons, DBConnection freeConnection,
            IDSGridEditDataRow row) {

        try {
            cons.execDrop(freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        }

    }

    /**
     * Update constraint info query.
     *
     * @param freeConnection the free connection
     * @param row the row
     * @param cons the cons
     */
    public static void updateConstraintInfoQuery(DBConnection freeConnection, IDSGridEditDataRow row,
            ConstraintMetaData cons) {
        try {
            cons.execRenameConstraint((String) row.getValue(0), freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        }

    }

    /**
     * Adds the index query.
     *
     * @param index the index
     * @param freeConnection the free connection
     * @param row the row
     * @param table the table
     */
    public static void addIndexQuery(IndexMetaData index, DBConnection freeConnection, IDSGridEditDataRow row,
            TableMetaData table) {

        try {
            table.execCreateIndex(index, freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);

        }
    }

    /**
     * Delete index query.
     *
     * @param index the index
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void deleteIndexQuery(IndexMetaData index, DBConnection freeConnection, IDSGridEditDataRow row) {
        try {
            index.drop(freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);

        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);

        }
    }

    /**
     * Update index info query.
     *
     * @param freeConnection the free connection
     * @param row the row
     * @param index the index
     */
    public static void updateIndexInfoQuery(DBConnection freeConnection, IDSGridEditDataRow row, IndexMetaData index) {

        try {
            index.rename((String) row.getValue(0), freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        }
    }

    /**
     * Sets the table description query.
     *
     * @param table the table
     * @param freeConnection the free connection
     * @param row the row
     */
    public static void setTableDescriptionQuery(TableMetaData table, DBConnection freeConnection,
            IDSGridEditDataRow row) {
        try {
            table.execSetTableDescription((String) row.getValue(1), freeConnection);
            row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS);
        } catch (DatabaseCriticalException e) {
            handleFailureOperations(e, row);
        } catch (DatabaseOperationException e) {
            handleFailureOperations(e, row);

        }
    }

}
