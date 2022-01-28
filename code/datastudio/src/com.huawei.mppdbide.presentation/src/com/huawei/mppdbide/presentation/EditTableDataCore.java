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

package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataCore.
 * 
 * @since 3.0.0
 */
public class EditTableDataCore extends AbstractEditTableDataCore {

    private TableMetaData serverObj;
    private IWindowDetail details;
    private TerminalExecutionConnectionInfra termConnection;
    private static final String SELECT_QUERY = "select * from ";

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return SELECT_QUERY + getTable().getDisplayName();
    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    public IWindowDetail getWindowDetails() {
        return details;
    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    public String getWindowTitle() {
        TableMetaData table = (TableMetaData) getTable();
        return table.getNameSpaceName() + '.' + table.getName() + '-' + table.getDatabaseName() + '@'
                + table.getServerName();
    }

    /**
     * Gets the progress bar label.
     *
     * @return the progress bar label
     */
    public String getProgressBarLabel() {
        TableMetaData table = (TableMetaData) getTable();
        return ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(table.getName(),
                table.getNamespace().getName(), table.getDatabaseName(), table.getServerName(),
                IMessagesConstants.EDIT_TABLE_PROGRESS_NAME);
    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == this.termConnection) {
            this.termConnection = new TerminalExecutionConnectionInfra();
            this.termConnection.setDatabase(getTable().getDatabase());
        }
        return this.termConnection;
    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    public TableMetaData getTable() {
        return serverObj;
    }

    @Override
    public void init(ServerObject serverObject) {
        this.serverObj = (TableMetaData) serverObject;
        details = new EditTableDataWindowDetails();

    }

    @Override
    public void refreshTable(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        getTable().getNamespace().refreshTable(getTable(), conn, false);
    }

    @Override
    public boolean isTableDropped() {
        return serverObj.isTableDropped();
    }
}
