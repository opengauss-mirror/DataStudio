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

package org.opengauss.mppdbide.view.synonym.handler;

import javax.inject.Named;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.core.di.annotations.Optional;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ISequenceMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ISynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.sequence.handler.DropSequenceObjectManager;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: Class
 * 
 * Description: The Class DropSynonymHandler
 *
 * @since 3.0.0
 */
public class DropSynonymHandler {

    private StatusMessage statusMsg;

    /**
     * Execute.
     *
     * @param isCascade the is cascade
     */
    @Execute
    public void execute(@Optional @Named("iscascade") String isCascade) {
        boolean isAppendCasecade = "true".equals(isCascade);
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();

        ISynonymMetaData selectedSynonym = (ISynonymMetaData) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null == selectedSynonym) {
            return;
        }
        String name = selectedSynonym.getParent().getQualifiedObjectName() + '.'
                + selectedSynonym.getQualifiedObjectName();
        int returnValue = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_CONFIRM_MSG, name),
                MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION));

        if (returnValue != 0) {
            return;
        }

        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();

        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_SYNONYM));
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_PROGRESS_NAME, name,
                selectedSynonym.getServer().getServerConnectionInfo().getConectionName());
        DropSynonymWorker dropjob = new DropSynonymWorker(progressLabel, selectedSynonym, statusMessage,
                isAppendCasecade);
        setStatusMessage(statusMessage);
        StatusMessageList.getInstance().push(statusMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        dropjob.schedule();
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMsg;
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMsg = statMessage;
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (null != obj && (obj instanceof SynonymMetaData)) {
            return true;
        }
        return false;
    }

}