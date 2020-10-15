/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserInputDialogUIWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UserInputDialogUIWorkerJob extends UIWorkerJob {

    /**
     * The dialog.
     */
    protected UserInputDialog dialog;

    /**
     * The oldname.
     */
    protected String oldname;
    private TerminalExecutionConnectionInfra connInfra;

    /**
     * The status msg.
     */
    protected StatusMessage statusMsg;

    /**
     * The error const for op exception.
     */
    protected String errorConstForOpException = null;

    /**
     * The error const for critical exception.
     */
    protected String errorConstForCriticalException = null;

    /**
     * Sets the error msg constants.
     *
     * @param str1 the str 1
     * @param str2 the str 2
     */
    private void setErrorMsgConstants(String str1, String str2) {
        errorConstForOpException = str1;
        errorConstForCriticalException = str2;
    }

    /**
     * Instantiates a new user input dialog UI worker job.
     *
     * @param name the name
     * @param family the family
     * @param dialog the dialog
     * @param statusMsg the status msg
     * @param oldname the oldname
     * @param str1 the str 1
     * @param str2 the str 2
     */
    public UserInputDialogUIWorkerJob(String name, Object family, UserInputDialog dialog, StatusMessage statusMsg,
            String oldname, String str1, String str2) {
        super(name, family);
        this.statusMsg = statusMsg;
        this.dialog = dialog;
        this.oldname = oldname;
        setErrorMsgConstants(str1, str2);
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

        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {

    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
        dialog.printErrorMessage(MessageConfigLoader.getProperty(errorConstForCriticalException,
                MPPDBIDEConstants.LINE_SEPARATOR, dbCriticalException.getDBErrorMessage()), false);
        dialog.enableButtons();

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        String msg = dbOperationException.getServerMessage();
        if (null == msg) {
            msg = dbOperationException.getDBErrorMessage();
        }

        if (msg.contains("Position:")) {
            msg = msg.split("Position:")[0];
        }
        dialog.printErrorMessage(MessageConfigLoader.getProperty(errorConstForOpException, oldname,
                MPPDBIDEConstants.LINE_SEPARATOR, msg), false);
        dialog.enableButtons();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bttmStatusBar != null) {
            bttmStatusBar.hideStatusbar(this.statusMsg);
        }
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        if (this.getConnInfra() != null) {
            this.getConnInfra().releaseConnection();
        }
    }

    /**
     * Gets the conn infra.
     *
     * @return the conn infra
     */
    public TerminalExecutionConnectionInfra getConnInfra() {
        return connInfra;
    }

    /**
     * Sets the conn infra.
     *
     * @param connInfra the new conn infra
     */
    public void setConnInfra(TerminalExecutionConnectionInfra connInfra) {
        this.connInfra = connInfra;
    }

}
