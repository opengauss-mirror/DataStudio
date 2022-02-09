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

package org.opengauss.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import org.opengauss.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropPartitionHandler.
 *
 * @since 3.0.0
 */
public class DropPartitionHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        PartitionMetaData selPartition = IHandlerUtilities.getSelectedPartitionMetadata();
        if (null != selPartition) {

            // to be changed
            int returnValue = MPPDBIDEDialogs.generateOKCancelMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION, selPartition.getName(),
                            selPartition.getParent().getNamespace().getName(), selPartition.getParent().getName()));
            if (returnValue != 0) {
                return;
            }
            String progressLabel = ProgressBarLabelFormatter.getProgressLabelForColumn(selPartition.getName(),
                    selPartition.getParent().getName(), selPartition.getParent().getNamespace().getName(),
                    selPartition.getParent().getDatabaseName(), selPartition.getParent().getServerName(),
                    IMessagesConstants.DROP_PARTITION_PROGRESS_NAME);
            DropPartitionHandlerWorker worker = new DropPartitionHandlerWorker(progressLabel, selPartition);
            worker.schedule();
        }
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        PartitionMetaData partitionMetaData = IHandlerUtilities.getSelectedPartitionMetadata();
        if (null == partitionMetaData) {
            return false;
        } else {
            return !(partitionMetaData.getParent() instanceof ForeignPartitionTable);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DropPartitionHandlerWorker.
     */
    private static final class DropPartitionHandlerWorker extends AbstractModalLessWindowOperationUIWokerJob {
        private PartitionMetaData selPartitionTable;

        /**
         * Instantiates a new drop partition handler worker.
         *
         * @param name the name
         * @param selPartition the sel partition
         */
        private DropPartitionHandlerWorker(String name, PartitionMetaData selPartition) {
            super(name, selPartition, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_PARTITION),
                    MPPDBIDEConstants.CANCELABLEJOB);
            this.selPartitionTable = selPartition;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            selPartitionTable.execDrop(conn);
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(getSuccessMsgForOBStatusBar()));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (null != objectBrowserModel) {
                objectBrowserModel.refreshObject(selPartitionTable.getParent());
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException uiCriticalException) {
            showErrorPopupMsg(uiCriticalException);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            showErrorPopupMsg(dbOperationException);
        }

        /**
         * Show error popup msg.
         *
         * @param exception the e
         */
        private void showErrorPopupMsg(MPPDBIDEException exception) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION_UNABLE_MSG,
                            selPartitionTable.getParent().getNamespace().getName(),
                            selPartitionTable.getParent().getName(), selPartitionTable.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION_UNABLE_MSG,
                            selPartitionTable.getParent().getNamespace().getName(),
                            selPartitionTable.getParent().getName(), selPartitionTable.getName())));

        }

        @Override
        protected String getSuccessMsgForOBStatusBar() {
            return MessageConfigLoader.getProperty(IMessagesConstants.DROP_PARTITION_SUCCESS,
                    selPartitionTable.getParent().getNamespace().getName(), selPartitionTable.getParent().getName(),
                    selPartitionTable.getName());
        }

        @Override
        protected ServerObject getObjectBrowserRefreshItem() {

            return null;
        }
    }

}
