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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionManager;
import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.bl.serverdatacache.IQueryResult;
import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.presentation.edittabledata.CommitStatus;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordExecutionStatus;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordStates;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSEditGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridColumnProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.grid.IRowEffectedConfirmation;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.utils.observer.IDSListenable;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSObjectPropertiesGridDataProvider.
 * 
 * @since 3.0.0
 */
public class DSObjectPropertiesGridDataProvider extends AbstractDSObjectPropertiesGridDataProvider
        implements IObjectPropertyData, IDSEditGridDataProvider, IDSListenable {

    private List<String[]> serverObjectPropertiesList;
    private IDSGridColumnProvider colProvider;
    private String propertyName;
    private TableMetaData table;
    private static final String GENERAL_TAB = "General";
    private static final String COLUMNS_TAB = "Columns";
    private static final String CONSTRAINTS_TAB = "Constraints";
    private static final String INDEX_TAB = "Index";
    private IServerObjectProperties objectPropertyObject;
    private DBConnection connection;
    private boolean cancelled;
    private CommitStatus lastCommitStat;

    /**
     * Instantiates a new DS object properties grid data provider.
     *
     * @param serverObjectPropertiesList the server object properties list
     * @param propertyName the property name
     * @param table the table
     * @param objectPropertyObject the object property object
     */
    public DSObjectPropertiesGridDataProvider(List<String[]> serverObjectPropertiesList, String propertyName,
            ServerObject table, IServerObjectProperties objectPropertyObject) {
        super();
        this.serverObjectPropertiesList = serverObjectPropertiesList;
        this.propertyName = propertyName;
        this.objectPropertyObject = objectPropertyObject;
        this.table = (TableMetaData) table;

    }

    @Override
    public void init() throws DatabaseOperationException, DatabaseCriticalException {
        DSObjectPropertiesGridColumnDataProvider provider = new DSObjectPropertiesGridColumnDataProvider();
        provider.init(serverObjectPropertiesList);
        this.colProvider = provider;
        prepareGridRow();
    }

    /**
     * Prepare grid row.
     */
    private void prepareGridRow() {

        DSObjectPropertiesGridDataRow gridRowData = null;
        for (int index = 1; index < serverObjectPropertiesList.size(); index++) {
            String[] col = serverObjectPropertiesList.get(index);

            gridRowData = new DSObjectPropertiesGridDataRow(col);
            gridRowData.setEventTable(eventTable);
            rowProviderList.add(gridRowData);
        }
    }

    @Override
    public void close() throws DatabaseOperationException, DatabaseCriticalException {

    }

    @Override
    public List<IDSGridDataRow> getNextBatch() throws DatabaseOperationException, DatabaseCriticalException {

        return null;
    }

    /**
     * Commit.
     *
     * @return the commit status
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public CommitStatus commit() throws MPPDBIDEException {
        // first need inserted list to commit
        // modified info will be committed

        List<ColumnMetaData> columnMetaDataList = this.table.getColumnMetaDataList();
        List<ConstraintMetaData> constraintList = this.table.getConstraintMetaDataList();
        List<IndexMetaData> indexList = this.table.getIndexMetaDataList();

        if (this.table.getConnectionManager() == null) {
            return null;
        }

        connection = this.table.getConnectionManager().getFreeConnection();
        for (IDSGridEditDataRow row : insertedList) {
            ServerObject serverObject = ((DSObjectPropertiesGridDataRow) row).getServerObject();

            performAddQueries(row, serverObject);
        }

        for (IDSGridEditDataRow row : deleteList) {

            performDeleteQueries(columnMetaDataList, constraintList, indexList, row);

        }
        List<IDSGridEditDataRow> updatedRows = getModifiedRows();

        performUpdateQueries(columnMetaDataList, constraintList, indexList, updatedRows);

        this.table.refresh(connection);

        releaseConnection();
        this.lastCommitStat = commitStatus();
        return lastCommitStat;

    }

    /**
     * Perform update queries.
     *
     * @param columnMetaDataList the column meta data list
     * @param constraintList the constraint list
     * @param indexList the index list
     * @param updatedRows the updated rows
     */
    private void performUpdateQueries(List<ColumnMetaData> columnMetaDataList, List<ConstraintMetaData> constraintList,
            List<IndexMetaData> indexList, List<IDSGridEditDataRow> updatedRows) {
        switch (this.propertyName) {
            case GENERAL_TAB: {
                IDSGridEditDataRow idsGridEditDataRow = updatedRows.get(updatedRows.size() - 1);
                PropertiesInfoExecuteQueryUtility.setTableDescriptionQuery(table, connection, idsGridEditDataRow);
                break;
            }

            case COLUMNS_TAB: {
                DSColumnPropertiesGridDataProvider.performColumnUpdate(columnMetaDataList, connection, updatedRows);
                break;
            }
            case CONSTRAINTS_TAB: {
                DSConstraintsPropertiesGridDataProvider.performConstraintUpdate(constraintList, connection,
                        updatedRows);
                break;
            }
            case INDEX_TAB: {
                DSIndexPropertiesGridDataProvider.performIndexUpdate(indexList, connection, updatedRows);
            }
        }
    }

    /**
     * Perform delete queries.
     *
     * @param columnMetaDataList the column meta data list
     * @param constraintList the constraint list
     * @param indexList the index list
     * @param row the row
     */
    private void performDeleteQueries(List<ColumnMetaData> columnMetaDataList, List<ConstraintMetaData> constraintList,
            List<IndexMetaData> indexList, IDSGridEditDataRow row) {
        switch (this.propertyName) {
            case COLUMNS_TAB: {
                DSColumnPropertiesGridDataProvider.performColumnDelete(columnMetaDataList, connection, row);
                break;
            }
            case CONSTRAINTS_TAB: {
                DSConstraintsPropertiesGridDataProvider.performConstraintDelete(constraintList, connection, row);
                break;
            }
            case INDEX_TAB: {
                DSIndexPropertiesGridDataProvider.performIndexDelete(indexList, connection, row);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Perform add queries.
     *
     * @param row the row
     * @param serverObject the server object
     */
    private void performAddQueries(IDSGridEditDataRow row, ServerObject serverObject) {
        switch (this.propertyName) {

            case COLUMNS_TAB: {
                PropertiesInfoExecuteQueryUtility.addColumnQuery((ColumnMetaData) serverObject, connection, row);
                break;
            }
            case CONSTRAINTS_TAB: {
                PropertiesInfoExecuteQueryUtility.addConstraintQuery((ConstraintMetaData) serverObject, connection,
                        row);
                break;
            }
            case INDEX_TAB: {
                PropertiesInfoExecuteQueryUtility.addIndexQuery((IndexMetaData) serverObject, connection, row,
                        this.table);
                break;
            }
            default: {
                break;
            }

        }
    }

    /**
     * Generate user role property change preview sql.
     *
     * @param conn the conn
     * @return the list
     * @throws Exception the exception
     */
    public List<String> generateUserRolePropertyChangePreviewSql(DBConnection conn) throws Exception {
        List<IDSGridEditDataRow> updatedRows = getModifiedRows();
        try {
            UserRole userRole = new UserRole();
            PropertiesUserRoleImpl propertiesUserRoleImpl = (PropertiesUserRoleImpl) this.objectPropertyObject;
            userRole.setOid(propertiesUserRoleImpl.getUserRole().getOid());

            String userRoleName = UserRoleManager.getUserRoleNameByOid(conn, userRole.getOid());
            boolean canLogin = UserRoleManager.getUserRoleLoginByOid(conn, userRole.getOid());
            userRole.setIsUser(canLogin);
            if (userRoleName == null || userRoleName.isEmpty()) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST, String.valueOf(userRole.getOid())));
                throw new MPPDBIDEException(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST,
                        String.valueOf(userRole.getOid()));
            }

            switch (this.propertyName) {
                case PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL: {
                    DSUserRolePropertiesGridDataProvider.generateUserRolePropertyGeneralTab(updatedRows, userRole);
                    break;
                }
                case PropertiesConstants.USER_ROLE_PROPERTY_TAB_PRIVILEGE: {
                    DSUserRolePropertiesGridDataProvider.generateUserRolePropertyTabPrivilege(updatedRows, userRole);
                    break;
                }
                case PropertiesConstants.USER_ROLE_PROPERTY_TAB_MEMBERSHIP: {
                    DSUserRolePropertiesGridDataProvider.generateUserRolePropertyTabMembership(updatedRows, userRole,
                            userRoleName);
                    break;
                }
                default: {
                    break;
                }
            }
            return UserRoleManager.generatePropertyChangePreviewSQL(conn, userRole, userRoleName);
        } catch (MPPDBIDEException exception) {
            updatedRows.stream().forEach(row -> row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED));
            throw exception;
        } finally {
            this.lastCommitStat = commitStatus();
        }

    }

    /**
     * Commit user role property.
     *
     * @param conn the conn
     * @param sqls the sqls
     * @throws ParseException the parse exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void commitUserRoleProperty(DBConnection conn, List<String> sqls) throws ParseException, MPPDBIDEException {
        List<IDSGridEditDataRow> updatedRows = getModifiedRows();
        try {
            conn.execNonSelect("START TRANSACTION;");
            UserRoleManager.alterUserRole(conn, sqls);
            conn.execNonSelect("COMMIT;");
            updatedRows.stream().forEach(row -> row.setExecutionStatus(EditTableRecordExecutionStatus.SUCCESS));
        } catch (Exception exception) {
            conn.execNonSelect("ROLLBACK;");
            updatedRows.stream().forEach(row -> {
                row.setExecutionStatus(EditTableRecordExecutionStatus.FAILED);
                if (exception instanceof MPPDBIDEException) {
                    row.setCommitStatusMessage(((MPPDBIDEException) exception).getServerMessage());
                } else {
                    row.setCommitStatusMessage(exception.getMessage());
                }
            });
            throw exception;
        } finally {
            this.lastCommitStat = commitStatus();
        }
    }

    /**
     * Release connection.
     */
    public void releaseConnection() {
        this.table.getDatabase().getConnectionManager().releaseConnection(this.connection);
        connection = null;
    }

    /**
     * Gets the modified rows.
     *
     * @return the modified rows
     */
    private List<IDSGridEditDataRow> getModifiedRows() {
        List<IDSGridEditDataRow> modifiedrows = new ArrayList<>();
        for (IDSGridDataRow row : rowProviderList) {
            if (((IDSGridEditDataRow) row).getUpdatedState() == EditTableRecordStates.UPDATE) {
                modifiedrows.add((IDSGridEditDataRow) row);
            }
        }

        return modifiedrows;

    }

    @Override
    public boolean isEndOfRecords() {

        return true;
    }

    @Override
    public IDSGridColumnProvider getColumnDataProvider() {

        return this.colProvider;
    }

    @Override
    public String getObjectPropertyName() {

        return this.propertyName;
    }

    @Override
    public IDSGridColumnGroupProvider getColumnGroupProvider() {

        return null;
    }

    @Override
    public void preDestroy() {
        // Nothing to do. Ignore
    }

    /**
     * Creates the new row.
     *
     * @param serverObject the server object
     * @param index the index
     * @return the IDS grid edit data row
     */
    public IDSGridEditDataRow createNewRow(ServerObject serverObject, int index) {

        int columnCount = getColumnDataProvider().getColumnCount();
        Object[] tabFieldsInfoFromServerObject = FilterServerObjPropInfoUtility.fetchInfoForRow(serverObject,
                columnCount);
        DSObjectPropertiesGridDataRow row = new DSObjectPropertiesGridDataRow(tabFieldsInfoFromServerObject);

        for (int cnt = 0; cnt < columnCount; cnt++) {
            row.setEventTable(eventTable);
            row.setValue(cnt, tabFieldsInfoFromServerObject[cnt]);
        }
        row.setRowIndex(index);
        row.createNewRow(tabFieldsInfoFromServerObject);
        row.setServerObject(serverObject);
        insertedList.add(row);
        rearrangeInsertedRowIndex(row.getRowIndex(), true);
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, null));
        return row;

    }

    @Override
    public TableMetaData getTable() {
        return this.table;
    }

    @Override
    public boolean isEditSupported() {
        if (objectPropertyObject instanceof PropertiesUserRoleImpl) {
            return true;
        }

        if (table == null) {
            return false;
        }
        if (table instanceof PartitionTable) {
            return false;
        }

        return objectPropertyObject instanceof PropertiesTableImpl;
    }

    @Override
    public boolean getResultTabDirtyFlag() {

        return false;
    }

    @Override
    public void setResultTabDirtyFlag(boolean flag) {

    }

    @Override
    public boolean isUniqueKeyPresent() {

        return false;
    }

    @Override
    public IDSGridEditDataRow getEmptyRowForInsert(int index) {

        return null;
    }

    @Override
    public CommitStatus getLastCommitStatus() {

        return this.lastCommitStat;
    }

    @Override
    public boolean isDistributionColumnsRequired() {

        return false;
    }

    @Override
    public List<String> getDistributedColumnList() {

        return new ArrayList<String>();
    }

    @Override
    public void cancelCommit() throws DatabaseCriticalException, DatabaseOperationException {
        if (connection != null) {
            connection.cancelQuery();
        }

    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancel(boolean cancel) {
        this.cancelled = cancel;

    }

    @Override
    public boolean isDistributionColumn(int columnIndex) {

        return false;
    }

    /**
     * Refresh.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void refresh() throws MPPDBIDEException {
        ConnectionManager connectionManager = table.getConnectionManager();
        if (connectionManager != null) {
            connection = connectionManager.getFreeConnection();
            List<String[]> propertyListPostRefresh = PropertiesInfoExecuteQueryUtility.performRefresh(this.propertyName,
                    this.objectPropertyObject, connection);
            if (propertyListPostRefresh != null) {

                serverObjectPropertiesList.clear();
                serverObjectPropertiesList = propertyListPostRefresh;
                rowProviderList.clear();
                init();
                releaseConnection();
            }
        }

    }

    /**
     * Checks if is valid object name.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     * @param rowObject the row object
     * @return the string
     */
    public String isValidObjectName(int columnIndex, Object newValue, IDSGridDataRow rowObject) {

        switch (this.propertyName) {

            case COLUMNS_TAB:
            case CONSTRAINTS_TAB:
            case INDEX_TAB: {
                return isValidIndexTabObjName(columnIndex, newValue);
            }
            case GENERAL_TAB: {
                return isValidGeneralTabObjName(columnIndex, newValue, rowObject);
            }
            case PropertiesConstants.USER_ROLE_PROPERTY_TAB_GENERAL: {
                return isValidUserRolePropertyObjName(columnIndex, newValue, rowObject);
            }
            default: {
                break;
            }
        }

        return null;
    }

    /**
     * Checks if is valid index tab obj name.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     * @return the string
     */
    private String isValidIndexTabObjName(int columnIndex, Object newValue) {
        if (columnIndex == 0) {
            if (newValue instanceof String && newValue.toString().length() > 63) {
                return MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_NAME_MORE_THAN_LIMIT_ERROR_MESSAGE);
            }
        }
        return null;
    }

    /**
     * Checks if is valid general tab obj name.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     * @param rowObject the row object
     * @return the string
     */
    private String isValidGeneralTabObjName(int columnIndex, Object newValue, IDSGridDataRow rowObject) {
        if (columnIndex == 1) {
            if (rowObject.getValue(0).equals(MessageConfigLoader.getProperty(IMessagesConstants.DESC_MSG))) {
                if (newValue instanceof String && newValue.toString().length() > 5000) {
                    return MessageConfigLoader.getProperty(IMessagesConstants.MAX_CHARATCTERS_ALLOWED_FOR_TABLE_DESC);
                }
            }
        }
        return null;
    }

    /**
     * Checks if is valid user role property obj name.
     *
     * @param columnIndex the column index
     * @param newValue the new value
     * @param rowObject the row object
     * @return the string
     */
    private String isValidUserRolePropertyObjName(int columnIndex, Object newValue, IDSGridDataRow rowObject) {
        if (columnIndex == 1) {
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_NAME).equals(rowObject.getValue(0))) {
                if (null == newValue) {
                    return MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_NAME_EMPTY);
                }
            }

            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_CONNECT_LIMIT)
                    .equals(rowObject.getValue(0))) {
                Pattern pattern = Pattern.compile("^(((-)?[1-9]+[0-9]*)|0)$");
                Matcher matcher = pattern.matcher(newValue.toString());
                if (!matcher.find()) {
                    return MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_CONNECT_LIMIT_INVALID);
                }
            }

            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_BEGIN)
                    .equals(rowObject.getValue(0))) {
                if (StringUtils.isEmpty(newValue.toString())) {
                    return MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_BEGIN_DATE_NULL);
                }
            }
            if (MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_VALID_UNTIL)
                    .equals(rowObject.getValue(0))) {
                if (StringUtils.isEmpty(newValue.toString())) {
                    return MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_VALID_UNTIL_DATE_NULL);
                }
            }
            if (newValue instanceof String
                    && newValue.toString().length() > PropertiesConstants.USER_ROLE_COMMENT_MAXIMUM_CHARACTERS) {
                return MessageConfigLoader.getProperty(IMessagesConstants.USER_ROLE_COMMENT_ERROR_MESSAGE);
            }
        }
        return null;
    }

    @Override
    public String getTableName() {

        return null;
    }

    @Override
    public int getColumnCount() {

        return 0;
    }

    @Override
    public List<String> getColumnNames() {

        return null;
    }

    @Override
    public List<String> getColumnDataTypeNames() {

        return new ArrayList<>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    @Override
    public Database getDatabse() {
        return this.objectPropertyObject.getDatabase();
    }

    @Override
    public CommitStatus commit(List<String> uniqueKeys, boolean isAtomic, IRowEffectedConfirmation rowEffectedConfirm,
            DBConnection termConnection) throws MPPDBIDEException {

        return null;
    }

    /**
     * Gets the object property object.
     *
     * @return the object property object
     */
    public IServerObjectProperties getObjectPropertyObject() {
        return this.objectPropertyObject;
    }

    /**
     * init
     */
    @Override
    public void init(IQueryResult irq, ArrayList<DefaultParameter> debugInputValueList, boolean isCallableStmt)
            throws DatabaseOperationException, DatabaseCriticalException {
    }

    /**
     * gets next batch
     */
    @Override
    public List<IDSGridDataRow> getNextBatch(ArrayList<DefaultParameter> debugInputValueList)
            throws DatabaseOperationException, DatabaseCriticalException {
        return null;
    }

    @Override
    public void setFuncProcExport(boolean isFuncProcExport) {
    }

    @Override
    public boolean isFuncProcExport() {
        return false;
    }
}