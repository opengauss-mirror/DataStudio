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

package com.huawei.mppdbide.presentation.view;

import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.presentation.AbstractViewTableDataCore;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewViewDataCore.
 * 
 * @since 3.0.0
 */
public class ViewViewDataCore extends AbstractViewTableDataCore {
    private static final String VIEW_DATA = "VIEW_DATA_";
    private static final String SELECT_ALL_QUERY = "select * from %s";
    private ViewMetaData serverObject;
    private ViewWindowDetails details;

    /**
     * Gets the server object.
     *
     * @return the server object
     */
    public ViewMetaData getServerObject() {
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
        String windowTitle = null;
        if (serverObject != null) {
            Database database = serverObject.getDatabase();
            windowTitle = serverObject.getNameSpaceName() + '.' + serverObject.getName() + '-' + database.getName()
                    + '@' + database.getServer().getName();
        }
        return windowTitle;
    }

    /**
     * Gets the progress bar label.
     *
     * @return the progress bar label
     */
    public String getProgressBarLabel() {
        String progressLabel = null;

        if (serverObject != null) {
            Database database = serverObject.getDatabase();
            progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(serverObject.getName(),
                    serverObject.getNamespace().getName(), database.getName(), database.getServer().getName(),
                    IMessagesConstants.VIEW_PROGRESS_NAME);
        }
        return progressLabel;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewWindowDetails.
     */
    private class ViewWindowDetails implements IWindowDetail {
        @Override
        public String getTitle() {
            return getWindowTitle();
        }

        @Override
        public String getUniqueID() {
            return VIEW_DATA + getTitle();
        }

        @Override
        public String getShortTitle() {
            return serverObject.getDisplayName();
        }
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    public String getQuery() {
        return String.format(Locale.ENGLISH, SELECT_ALL_QUERY, serverObject.getDisplayName());
    }

    
    /**
     * the init
     * 
     * @param obj the obj
     */
    public void init(ServerObject obj) {
        if (obj instanceof ViewMetaData) {
            this.serverObject = (ViewMetaData) obj;
            details = new ViewWindowDetails();
        }
    }

    @Override
    public boolean isTableDropped() {
        return serverObject.isViewDropped();
    }

    @Override
    public void refreshTable(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        serverObject.getNamespace().refreshView(serverObject, conn, false);
    }
}
