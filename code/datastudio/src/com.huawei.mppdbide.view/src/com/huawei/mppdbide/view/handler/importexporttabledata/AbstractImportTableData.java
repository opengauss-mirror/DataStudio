/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.importexporttabledata;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import com.huawei.mppdbide.view.ui.dialog.ImportOptionDialog;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractImportTableData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author gWX773294
 * @version
 * @since 30-August-2019
 */
public abstract class AbstractImportTableData {
    private StatusMessage statusMessage;

    /**
     * Schedule import data job.
     *
     * @param importExportDataCore the import export data core
     */
    public void scheduleImportDataJob(AbstractImportExportDataCore importExportDataCore) {
        final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        long starttime = System.currentTimeMillis();
        ImportDataWorker importDataWorker = new ImportDataWorker(bottomStatusBar, importExportDataCore, starttime);
        importDataWorker.setTaskDB(importExportDataCore.getDatabase());
        StatusMessage statMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_IMPORT_TABLE_DATA));
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        importDataWorker.schedule();

    }

    /**
     * Gets the information for import.
     *
     * @param shell the shell
     * @param importExportDataCore the import export data core
     * @return the information for import
     */
    public boolean getInformationForImport(Shell shell, AbstractImportExportDataCore importExportDataCore) {
        ImportOptionDialog dialog = new ImportOptionDialog(shell, importExportDataCore);
        dialog.setImport(true);
        int returnVal = dialog.open();
        if (returnVal != 0) {
            return false;
        }
        return true;
    }

    /**
     * Generate import error message dialog.
     *
     * @param exception the exception
     */
    public void generateImportErrorMessageDialog(MPPDBIDEException exception) {
        MPPDBIDELoggerUtility.error("Error while refresh object while importing table data..", exception);
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IMPORT_TBL_DATA),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                        MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()),
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                        MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage())));
    }

    /**
     * Generate import error message dialog.
     */
    public void generateImportErrorMessageDialog() {
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_DROP_TABLE),
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ImportDataWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public class ImportDataWorker extends ImportExportPreUIWorker {
        private long totalRows = 0;

        /**
         * The bottom status bar.
         */
        protected BottomStatusBar bottomStatusBar;
        private String elapsedTime = null;
        private IExecTimer exc = new ExecTimer(null);

        /**
         * The import export core.
         */
        protected AbstractImportExportDataCore importExportCore;
        private long starttime;

        /**
         * Instantiates a new import data worker.
         *
         * @param bottomStatusBar the bottom status bar
         * @param importExportCore the import export core
         * @param starttime the starttime
         */
        protected ImportDataWorker(BottomStatusBar bottomStatusBar, AbstractImportExportDataCore importExportCore,
                long starttime) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_JOB_NAME,
                    importExportCore.getProgressLabelName()), MPPDBIDEConstants.CANCELABLEJOB,
                    IMessagesConstants.IMPORT_ERROR);
            this.importExportCore = importExportCore;
            this.bottomStatusBar = bottomStatusBar;
            this.starttime = starttime;
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
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            exc.start();
            Database db = getDatabase();
            setServerPwd(null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            totalRows = importExportCore.executeImportData();
            return null;
        }

        /**
         * On out of memory error.
         *
         * @param ex the ex
         */
        @Override
        public void onOutOfMemoryError(OutOfMemoryError ex) {
            try {
                exc.stop();
                elapsedTime = exc.getElapsedTime();
            } catch (DatabaseOperationException e2) {
                MPPDBIDELoggerUtility.error("Execute timer operation failed.", e2);
            }
            UIElement.getInstance().outOfMemoryCatch(elapsedTime, ex.getMessage());

        }

        /**
         * On success UI action.
         *
         * @param obj the obj
         */
        @Override
        public void onSuccessUIAction(Object obj) {
            ServerObject servObj = importExportCore.getImportExportServerObj();
            if (null == servObj || null == servObj.getNamespace()) {
                return;
            }
            String message = MessageConfigLoader.getProperty(IMessagesConstants.MSG_IMPORT_SUCCESS,
                    servObj.getNamespace().getName(), servObj.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            long endtime = System.currentTimeMillis();

            StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

            msg.append(message);
            msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(MPPDBIDEConstants.LINE_SEPARATOR)
                    .append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_TOTAL_ROWS_IMPORT, totalRows));
            msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG, endtime - starttime));
            MPPDBIDEDialogs.clearExistingDialog();
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_SUCCES_DAILOG_TITLE),
                    msg.toString());
            MPPDBIDELoggerUtility.info("Table data succesfully imported.");
        }

        /**
         * On critical exception UI action.
         *
         * @param exception the exception
         */
        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            msg.append(exception.getServerMessage());

            msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(exception.getDBErrorMessage());

            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IMPORT_TBL_DATA),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg.toString()),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg.toString())));
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                    importExportCore.getDatabase());
        }

        /**
         * Print the error message on console.
         *
         * @param exception the e
         */
        private void consoleLogExecutionFailure(final MPPDBIDEException exception) {
            ServerObject servObj = importExportCore.getImportExportServerObj();
            if (null == servObj || null == servObj.getNamespace()) {
                return;
            }
            String statusMsg = MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                    servObj.getNamespace().getName(), "." + servObj.getName());
            String msg = exception.getServerMessage();
            if (null == msg) {
                msg = exception.getDBErrorMessage();
            }
            if (exception.getServerMessage() != null
                    && exception.getServerMessage().contains("canceling statement due to user request")) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IMPORT_TBL_DATA),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_SUCCES_CONSOLE_MESSAGE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_SUCCES_CONSOLE_MESSAGE)));
            } else {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_FAIL_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_IMPORT_TABLE_TO_CSV_HANDLER,
                                MPPDBIDEConstants.LINE_SEPARATOR, msg),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(statusMsg));
            }

        }

        /**
         * On operational exception UI action.
         *
         * @param dbOperationException the e
         */
        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
            consoleLogExecutionFailure(dbOperationException);
        }

        /**
         * Final cleanup UI.
         */
        @Override
        public void finalCleanupUI() {
            if (bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(getStatusMessage());
            }
        }

        /**
         * Canceling.
         */
        @Override
        protected void canceling() {
            if (isCancel()) {
                return;
            } else {
                super.canceling();
                try {
                    importExportCore.cancelImportExportOperation();
                } catch (DatabaseCriticalException e) {
                    handleException();
                } catch (DatabaseOperationException e) {
                    handleException();
                }
            }
        }

        /**
         * Handle exception.
         */
        private void handleException() {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_CANCELMSG),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfoFromConst(IMessagesConstants.CANCEL_IMPORT_CANCELMSG));
        }

        /**
         * Final cleanup.
         */
        @Override
        public void finalCleanup() {
            super.finalCleanup();
            importExportCore.importExportCleanUp();
        }

        @Override
        protected Database getDatabase() {
            return importExportCore.getDatabase();
        }
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
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return !IHandlerUtilities.isSelectedTableForignPartition();
    }

    /**
     * Sets the status message.
     *
     * @param statMessage the new status message
     */
    public void setStatusMessage(StatusMessage statMessage) {
        this.statusMessage = statMessage;
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
    }

}
