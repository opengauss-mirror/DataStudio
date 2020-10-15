/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.connection;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.view.ui.table.IDialogWorkerInteraction;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractDialogWindowOperationUIWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
