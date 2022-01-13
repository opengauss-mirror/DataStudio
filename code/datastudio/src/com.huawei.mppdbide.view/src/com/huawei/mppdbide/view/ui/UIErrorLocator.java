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

package com.huawei.mppdbide.view.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.errorlocator.IErrorLocator;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.ui.connectiondialog.SQLExecutionErrorDialog;

/**
 * 
 * Title: class
 * 
 * Description: The Class UIErrorLocator.
 *
 * @since 3.0.0
 */
public class UIErrorLocator {
    private boolean checkErrorDialog;

    /**
     * Error pop up on qry execution failure.
     *
     * @param shell the shell
     * @param exception the e
     * @param lineNumber the line number
     * @return true, if successful
     */
    public boolean errorPopUpOnQryExecutionFailure(Shell shell, MPPDBIDEException exception, int lineNumber,
            boolean isViewSource, TerminalExecutionConnectionInfra terminalExecutionConnectionInfra) {
        int popUpreturn = generateErrorPopup(shell, exception, lineNumber, isViewSource,
                terminalExecutionConnectionInfra);

        if (popUpreturn == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Generate error popup.
     *
     * @param shell the shell
     * @param exception the e
     * @param lineNumber the line number
     * @param terminalExecutionConnectionInfra connection infra
     * @return the int
     */
    public int generateErrorPopup(Shell shell, MPPDBIDEException exception, int lineNumber, boolean isViewSource,
            TerminalExecutionConnectionInfra terminalExecutionConnectionInfra) {
        int btnPressedValue = 0;
        SQLExecutionErrorDialog dialog;
        MultiStatus info;

        String pluginId = IErrorLocator.class.getCanonicalName();
        // Temp holder of child statuses
        List<Status> childStatuses = new ArrayList<Status>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

        if (lineNumber != 0) {
            // Details for showing detailed error message
            Status childStatus = new Status(IStatus.WARNING, pluginId,
                    exception.getServerMessage() + MPPDBIDEConstants.SPACE_CHAR
                            + MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILURE_LINE_NUMEBR)
                            + MPPDBIDEConstants.SPACE_CHAR + lineNumber);
            childStatuses.add(childStatus);
            info = new MultiStatus(pluginId, IStatus.WARNING, childStatuses.toArray(new Status[childStatuses.size()]),
                    exception.getLocalizedMessage() + MPPDBIDEConstants.SPACE_CHAR
                            + MessageConfigLoader.getProperty(IMessagesConstants.ERROR_POPUP_MESSAGE_TEXT)
                            + MPPDBIDEConstants.LINE_SEPARATOR
                            + MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILURE_DETAILS_DESCRIPTION),

                    null);
            dialog = new SQLExecutionErrorDialog(shell,
                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_POPUP_HEADER), null, info,
                    IStatus.WARNING);
        } else {
            // Details for showing detailed error message
            dialog = showDetailsErrorMsg(shell, exception, pluginId, childStatuses);

        }
        btnPressedValue = dialog.open();

        this.checkErrorDialog = dialog.isRemember();

        return btnPressedValue;
    }

    private SQLExecutionErrorDialog showDetailsErrorMsg(Shell shell1, MPPDBIDEException e1, String plginId,
            List<Status> childSttuses) {
        SQLExecutionErrorDialog dilog;
        MultiStatus info;
        Status childStatus = new Status(IStatus.WARNING, plginId, e1.getServerMessage());
        childSttuses.add(childStatus);
        info = new MultiStatus(plginId, IStatus.WARNING, childSttuses.toArray(new Status[childSttuses.size()]),
                e1.getLocalizedMessage() + MPPDBIDEConstants.SPACE_CHAR
                        + MessageConfigLoader.getProperty(IMessagesConstants.ERROR_POPUP_MESSAGE_TEXT)
                        + MPPDBIDEConstants.LINE_SEPARATOR
                        + MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILURE_DETAILS_DESCRIPTION),

                null);

        dilog = new SQLExecutionErrorDialog(shell1,
                MessageConfigLoader.getProperty(IMessagesConstants.ERROR_POPUP_HEADER), null, info, IStatus.WARNING);
        return dilog;
    }

    /**
     * Gets the check error dialog.
     *
     * @return the check error dialog
     */
    public boolean getCheckErrorDialog() {
        return checkErrorDialog;
    }

    /**
     * Sets the check error dialog.
     *
     * @param checkErrorDialog the new check error dialog
     */
    public void setCheckErrorDialog(boolean checkErrorDialog) {
        this.checkErrorDialog = checkErrorDialog;
    }

}
