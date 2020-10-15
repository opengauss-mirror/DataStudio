/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ViewTableDataCore extends AbstractViewTableDataCore {
    private static final String VIEW_TABLE_DATA = "VIEW_TABLE_DATA_";
    private static final String SELECT_ALL_QUERY = "select * from %s";
    /**
     * server object
     */
    protected TableMetaData serverObject;
    private ViewTableWindowDetails details;
    private String handlerParameter = null;

    /**
     * Gets the handler parameter.
     *
     * @return the handler parameter
     */
    public String getHandlerParameter() {
        return handlerParameter;
    }

    /**
     * Sets the handler parameter.
     *
     * @param handlerParameter the new handler parameter
     */
    public void setHandlerParameter(String handlerParameter) {
        this.handlerParameter = handlerParameter;
    }

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public TableMetaData getServerObject() {
        return serverObject;
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
        TableMetaData table = null;
        String windowTitle = null;
        if (serverObject != null) {
            table = (TableMetaData) serverObject;
            windowTitle = table.getNameSpaceName() + '.' + table.getName()
                    + ((!getHandlerParameter().isEmpty()) ? "." + getHandlerParameter() : "") + '-'
                    + table.getDatabaseName() + '@' + table.getServerName();
        }
        return windowTitle;

    }

    /**
     * Gets the progress bar label.
     *
     * @return the progress bar label
     */
    public String getProgressBarLabel() {
        String progressLabelForTableWithMsg = null;

        if (serverObject != null) {
            TableMetaData table = (TableMetaData) serverObject;

            progressLabelForTableWithMsg = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(table.getName(),
                    table.getNamespace().getName(), table.getDatabaseName(), table.getServerName(),
                    IMessagesConstants.VIEW_TABLE_PROGRESS_NAME);
        }
        return progressLabelForTableWithMsg;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewTableWindowDetails.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class ViewTableWindowDetails implements IWindowDetail {

        @Override
        public String getTitle() {
            return getWindowTitle();
        }

        @Override
        public String getUniqueID() {
            return VIEW_TABLE_DATA + getTitle();
        }

        @Override
        public String getShortTitle() {
            TableMetaData table = (TableMetaData) serverObject;
            return table.getDisplayName();
        }
    }

    /**
     * Gets the query.
     *
     * @return the query
     * @throws DatabaseOperationException 
     */
    public String getQuery() throws DatabaseOperationException {
        return String.format(Locale.ENGLISH, SELECT_ALL_QUERY, serverObject.getDisplayName());
    }

    
    /**
     * the init
     * 
     * @param obj the obj
     */
    public void init(ServerObject obj) {
        if (obj instanceof TableMetaData) {
            this.serverObject = (TableMetaData) obj;
            details = new ViewTableWindowDetails();
        }
    }

    @Override
    public boolean isTableDropped() {
        return serverObject.isTableDropped();
    }

    @Override
    public void refreshTable(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        serverObject.getNamespace().refreshTable(serverObject, conn, false);
    }
}
