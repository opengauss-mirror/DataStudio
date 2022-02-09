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

package org.opengauss.mppdbide.view.userrole;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.UserRole;
import org.opengauss.mppdbide.bl.serverdatacache.UserRoleManager;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.handler.connection.AbstractDialogWindowOperationUIWorkerJob;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class CreateUserRoleWorker.
 *
 * @since 3.0.0
 */
public class CreateUserRoleWorker extends AbstractDialogWindowOperationUIWorkerJob {

    private UserRole newUserRole;

    /**
     * Instantiates a new creates the user role worker.
     *
     * @param name the name
     * @param role the role
     * @param msg the msg
     * @param iDialogWorkerInt the iDialogWorkerInt
     */
    public CreateUserRoleWorker(String name, UserRole role, String msg, IDialogWorkerInteraction iDialogWorkerInt) {
        super(name, role, msg, iDialogWorkerInt, MPPDBIDEConstants.CANCELABLEJOB);
        newUserRole = role;
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
        UserRoleManager.execCreate(conn, newUserRole);
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
