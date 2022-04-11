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

package org.opengauss.mppdbide.view.core;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyServerObjectBackendLoader.
 *
 * @since 3.0.0
 */
public class LazyServerObjectBackendLoader extends LazyBackendLoader {
    private ServerObject objToLoad;

    /**
     * Instantiates a new lazy server object backend loader.
     *
     * @param tableToLoad the table to load
     * @param statusMsg the status msg
     * @param timer the timer
     */
    LazyServerObjectBackendLoader(ServerObject tableToLoad, StatusMessage statusMsg, IExecTimer timer) {
        super("Backend Lazy load Table-", null, statusMsg, timer);
        this.objToLoad = tableToLoad;
        setDb(tableToLoad.getDatabase());
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        ObjectBrowserObjectRefreshPresentation.refreshSeverObject(objToLoad);
        return null;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    @Override
    String getErrorMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.TABLE_RETRIVE_ERROR);
    }

    /**
     * Sets the load failed.
     */
    @Override
    void setLoadFailed() {
        super.setLoadFailed();
    }

    /**
     * Gets the message dialog title.
     *
     * @return the message dialog title
     */
    @Override
    String getMessageDialogTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ERR_LAZY_LOADING_TABLE);
    }

    /**
     * Gets the message dialog message.
     *
     * @return the message dialog message
     */
    @Override
    String getMessageDialogMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.TABLE_RETRIVE_ERROR, objToLoad.getName());
    }

}
