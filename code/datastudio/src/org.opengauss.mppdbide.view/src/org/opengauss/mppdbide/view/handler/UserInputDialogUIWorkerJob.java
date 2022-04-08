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

package org.opengauss.mppdbide.view.handler;

import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserInputDialogUIWorkerJob.
 *
 * @since 3.0.0
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
