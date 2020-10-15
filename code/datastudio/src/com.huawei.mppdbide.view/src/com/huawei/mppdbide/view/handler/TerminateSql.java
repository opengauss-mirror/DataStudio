/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class TerminateSql.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
