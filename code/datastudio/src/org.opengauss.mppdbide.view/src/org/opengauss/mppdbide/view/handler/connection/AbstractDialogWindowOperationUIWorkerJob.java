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

package org.opengauss.mppdbide.view.handler.connection;

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractDialogWindowOperationUIWorkerJob.
 *
 * @since 3.0.0
 */
public abstract class AbstractDialogWindowOperationUIWorkerJob extends ObjectBrowserOperationUIWorkerJob {

    /**
     * The user dialog.
     */
    protected IDialogWorkerInteraction userDialog;

    /**
     * Instantiates a new abstract dialog window operation UI worker job.
     *
     * @param name the name
     * @param obj the obj
     * @param msg the msg
     * @param dialog the dialog
     * @param family the family
     */
    public AbstractDialogWindowOperationUIWorkerJob(String name, ServerObject obj, String msg,
            IDialogWorkerInteraction dialog, Object family) {
        super(name, obj, msg, family);
        this.userDialog = dialog;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        this.userDialog.onSuccessUIAction(obj);
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        if (this.userDialog.getShell() != null && !this.userDialog.getShell().isDisposed()) {
            this.userDialog.onCriticalExceptionUIAction(exception);
        }

    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        if (this.userDialog.getShell() != null && !this.userDialog.getShell().isDisposed()) {
            this.userDialog.onOperationalExceptionUIAction(exception);
        }

    }

    /**
     * On pre setup failure.
     *
     * @param exception the exception
     */
    @Override
    public void onPreSetupFailure(MPPDBIDEException exception) {
        if (this.userDialog.getShell() != null && !this.userDialog.getShell().isDisposed()) {
            this.userDialog.onPresetupFailureUIAction(exception);
        }

    }
}
