/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole;

import java.util.List;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.userrole.GrantRevokeCore;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectPrivilegeModificationUIWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ObjectPrivilegeModificationUIWorkerJob extends AbstractDialogWindowOperationUIWorkerJob {
    private MessageQueue messageQueue;
    private GrantRevokeCore grantRevokeCore;
    private List<String> sqls;

    /**
     * Instantiates a new object privilege modification UI worker job.
     *
     * @param jobName the job name
     * @param serverObject the server object
     * @param statusBarMsg the status bar msg
     * @param dialog the dialog
     * @param grantRevokeCore the grant revoke core
     * @param sqls the sqls
     * @param messageQueue the message queue
     */
    public ObjectPrivilegeModificationUIWorkerJob(String jobName, ServerObject serverObject, String statusBarMsg,
            IDialogWorkerInteraction dialog, GrantRevokeCore grantRevokeCore, List<String> sqls,
            MessageQueue messageQueue) {
        super(jobName, serverObject, statusBarMsg, dialog, MPPDBIDEConstants.CANCELABLEJOB);
        this.grantRevokeCore = grantRevokeCore;
        this.sqls = sqls;
        this.messageQueue = messageQueue;
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
        grantRevokeCore.modifyObjectPrivilege(conn, sqls, messageQueue);
        return null;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {

        return null;
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
