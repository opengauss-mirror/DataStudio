/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.ForeignPartitionTable;
import com.huawei.mppdbide.bl.serverdatacache.PartitionMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropPartitionHandler.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
