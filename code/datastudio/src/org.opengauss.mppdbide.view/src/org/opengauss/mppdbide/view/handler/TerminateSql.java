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

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class TerminateSql.
 *
 * @since 3.0.0
 */
public class TerminateSql {
    private StatusMessage statusMessage;

    private String terminalPartLabel;
    private String dbName;
    private String serverName;

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        SQLTerminal terminal = UIElement.getInstance().getVisibleTerminal();
        int choice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORTEXPORT_CONSOLE),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORTEXPORT_CONSOLE_MSG),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO));

        if (choice == 0) {
            if (terminalPartLabel == null) {
                if (terminal == null) {
                    return;
                } else {
                    terminalPartLabel = terminal.getPartLabel();
                    dbName = terminal.getSelectedDatabase().getDbName();
                    serverName = terminal.getSelectedDatabase().getServerName();
                }

                if (terminalPartLabel == null) {
                    return;
                }
            }
        } else {
            return;
        }
        final BottomStatusBar btnStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statusMsg = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        setStatusMessage(statusMsg);
        StatusMessageList.getInstance().push(statusMsg);
        if (btnStatusBar != null) {
            btnStatusBar.activateStatusbar();
        }
        final IJobManager jobMan = Job.getJobManager();
        // JobManager list of jobs not idle
        final Job[] allJobs = jobMan.find(MPPDBIDEConstants.CANCELABLEJOB);
        for (final Job job : allJobs) {
            if (job.getName().equalsIgnoreCase(ProgressBarLabelFormatter.getProgressLabelForSchema(terminalPartLabel,
                    dbName, serverName, IMessagesConstants.SQL_QUERY_EXECUTE))) {
                job.cancel();
                break;
            }
        }
        if (btnStatusBar != null) {
            btnStatusBar.hideStatusbar(statusMsg);
        }

        terminal = null;
        terminalPartLabel = null;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        SQLTerminal terminal = UIElement.getInstance().getVisibleTerminal();
        return terminal != null && terminal.isExecuteInProgress();

    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param statusMessage the new status message
     */
    public void setStatusMessage(StatusMessage statusMessage) {
        this.statusMessage = statusMessage;
    }
}
