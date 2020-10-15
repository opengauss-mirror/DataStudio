/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.INamespace;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.ILogger;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class Description: The Class DropObject. Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropObject {

    /**
     * Execute.
     */
    @Execute
    public void execute() {

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.GUI_DROPDEBUGOBJECT_DROP_DEBUG_OBJECT));

        /*
         * The object will never be null. In case of null, the Menu item will be
         * disabled automatically.
         */
        final IDebugObject debugObject = (IDebugObject) IHandlerUtilities.getObjectBrowserSelectedObject();
        if (debugObject == null) {
            return;
        }
        final Database db = debugObject.getDatabase();

        String dropMsg = IMessagesConstants.DROP_FUNC_PROC;
        String confirmMsg = IMessagesConstants.DROP_FUNC_PROC_CONFIRMATION;

        if (UIConstants.OK_ID != MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                MessageConfigLoader.getProperty(dropMsg),
                MessageConfigLoader.getProperty(confirmMsg, debugObject.getDisplayNameWithArgName()))) {
            return;
        }

        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_DEBUG_OBJECT));
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(debugObject.getName(),
                debugObject.getNamespace().getName(), debugObject.getDatabase().getName(),
                debugObject.getDatabase().getServerName(), IMessagesConstants.DROP_DEBUGOBJECT_PROGRESS_NAME);
        DropObjectWorker workerJob = new DropObjectWorker(progressLabel, statMessage, db, debugObject);
        workerJob.setTaskDB(db);
        StatusMessageList.getInstance().push(statMessage);
        final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (null != btmStatusBar) {

            btmStatusBar.activateStatusbar();
        }
        workerJob.schedule();

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IDebugObject debugObject = IHandlerUtilities.getSelectedDebugObject();

        if (debugObject != null) {
            return true;
        }

        return false;
    }

    /**
     * Title: class Description: The Class DropObjectWorker. Copyright (c)
     * Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class DropObjectWorker extends UIWorkerJob {
        private Database db;

        private IDebugObject debugObject;

        private StatusMessage statMessage;

        DropObjectWorker(String name, StatusMessage statMessage, Database db, IDebugObject debugObject2) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB);
            this.statMessage = statMessage;
            this.db = db;
            this.debugObject = debugObject2;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, true);

            ((INamespace) ((ServerObject) debugObject).getParent()).dropDbObject(debugObject,
                    db.getConnectionManager().getObjBrowserConn());

            MPPDBIDELoggerUtility.perf(MPPDBIDEConstants.GUI, ILogger.PERF_EXECUTE_SQLTERMINAL_QUERY, false);

            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            final String name = debugObject.getDisplayName(false);
            UIElement.getInstance().closeSourceViewerById(debugObject.getPLSourceEditorElmId());
            ObjectBrowser objectBrowser = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowser != null) {
                objectBrowser.refreshObject(debugObject.getNamespace());
            }
            UIDisplayFactoryProvider.getUIDisplayStateIf().clearSqlObject();
            String dropSuccessMsg = IMessagesConstants.FUNC_PROC_DROPPED;
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(dropSuccessMsg, name)));

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, db);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            consoleLogExecutionFailure(exception);
        }

        @Override
        public void onExceptionUIAction(Exception exception) {
            String dropErrorMsg = IMessagesConstants.ERR_DROP_FUNC_PROC;
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(dropErrorMsg), exception);

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PLSQL_ERR),
                    MessageConfigLoader.getProperty(IMessagesConstants.UNKNOWN_INTERNAL_ERR));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfoFromConst(IMessagesConstants.UNKNOWN_INTERNAL_ERR));
        }

        @Override
        public void finalCleanup() {

        }

        @Override
        public void finalCleanupUI() {
            BottomStatusBar btstat = UIElement.getInstance().getProgressBarOnTop();
            if (null != btstat) {

                btstat.hideStatusbar(statMessage);
            }
        }

        /**
         * Print the error message on console.
         *
         * @param e the e
         */
        private void consoleLogExecutionFailure(final MPPDBIDEException exception) {
            final String name = debugObject.getDisplayName(false);
            String message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED,
                    MPPDBIDEConstants.LINE_SEPARATOR, exception.getErrorCode(), exception.getServerMessage());
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.DRP_OBJ_ERRTITLE), message);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECT_ERROR, name)));
            MPPDBIDELoggerUtility.error("DropObject: log messages to console.", exception);

        }
    }

}
