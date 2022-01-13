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

package com.huawei.mppdbide.view.handler.table;

import com.huawei.mppdbide.bl.serverdatacache.IndexMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeIndexTablespaceWorker.
 *
 * @since 3.0.0
 */
public class ChangeIndexTablespaceWorker extends AbstractDialogWindowOperationUIWorkerJob {
    private IndexMetaData idxToAlter;
    private String newTablespaceName;
    private String oldTablespaceName;

    /**
     * Instantiates a new change index tablespace worker.
     *
     * @param name the name
     * @param index the index
     * @param newTblspc the new tblspc
     * @param msg the msg
     * @param dialog the dialog
     */
    public ChangeIndexTablespaceWorker(String name, IndexMetaData index, String newTblspc, String msg,
            IDialogWorkerInteraction dialog) {
        super(name, index, msg, dialog, MPPDBIDEConstants.CANCELABLEJOB);
        idxToAlter = index;
        newTablespaceName = newTblspc;
        oldTablespaceName = idxToAlter.getTablespc();
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
        idxToAlter.changeTablespace(newTablespaceName, conn);
        MPPDBIDELoggerUtility.info("Changing index tablespace succesful ");
        return null;

    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        String oldTblSpace = oldTablespaceName != null ? oldTablespaceName
                : MessageConfigLoader.getProperty(IMessagesConstants.DFLT_CLM_UI);
        return MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_TABLESPACE_TO, oldTblSpace,
                newTablespaceName);
    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {

        return null;
    }

}
