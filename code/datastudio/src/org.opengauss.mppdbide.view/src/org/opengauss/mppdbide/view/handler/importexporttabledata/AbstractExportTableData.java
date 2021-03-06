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

package org.opengauss.mppdbide.view.handler.importexporttabledata;

import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseUtils;
import org.opengauss.mppdbide.bl.serverdatacache.DebugObjects;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import org.opengauss.mppdbide.presentation.exportdata.ExportZipData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileCompressException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import org.opengauss.mppdbide.view.ui.PLSourceEditor;
import org.opengauss.mppdbide.view.ui.dialog.ExportOptionDialog;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractExportTableData.
 *
 * @since 3.0.0
 */
public abstract class AbstractExportTableData {
    private StatusMessage statusMessage;

    /**
     * Gets the information for export.
     *
     * @param importExportCore the import export core
     * @return the information for export
     */
    public static boolean getInformationForExport(AbstractImportExportDataCore importExportCore) {

        ExportOptionDialog exportOptDialog = new ExportOptionDialog(Display.getDefault().getActiveShell(),
                importExportCore);
        int returnVal = exportOptDialog.open();
        if (returnVal != 0) {
            return false;
        }
        return true;
    }

    /**
     * Generate export error message dialog.
     *
     * @param exception the exception
     */
    public void generateExportErrorMessageDialog(MPPDBIDEException exception) {
        MPPDBIDELoggerUtility.error("ExportTableHandler: exporting table failed.", exception);
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                        MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()),
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
    }

    /**
     * Generate export error message dialog.
     */
    public void generateExportErrorMessageDialog() {
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_DROP_TABLE),
                MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ExportDataWorker.
     */
    public abstract class ExportDataWorker extends ImportExportPreUIWorker {

        /**
         * The status msg.
         */
        protected StatusMessage statusMsg;

        /**
         * The total rows.
         */
        protected long totalRows;
        private String elapsedTime = null;
        private IExecTimer exc = new ExecTimer(null);

        /**
         * The import export data core.
         */
        protected AbstractImportExportDataCore importExportDataCore;

        /**
         * The export zip data.
         */
        protected ExportZipData exportZipData;
        private long starttime;

        /**
         * Instantiates a new export data worker.
         *
         * @param core the core
         * @param statusMsg the status msg
         * @param starttime the starttime
         */
        protected ExportDataWorker(AbstractImportExportDataCore core, StatusMessage statusMsg, long starttime) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_JOB_NAME,
                    core.getProgressLabelName()), MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.EXPORT_ERROR);
            this.statusMsg = statusMsg;
            this.importExportDataCore = core;
            this.exportZipData = new ExportZipData();
            this.starttime = starttime;

        }

        /**
         * Do job.
         *
         * @return the object
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws MPPDBIDEException the MPPDBIDE exception
         * @throws InterruptedException
         */
        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, InterruptedException {
            // Execute query, export data to file and return number of rows
            // exported
            exc.start();
            Database db = getDatabase();
            setServerPwd(null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            try {
                this.totalRows = importExportDataCore.executeExportData(null, false);
                doCompress();
            } catch (ParseException e) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_INFORMATION),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_PARSER));
            }

            MPPDBIDELoggerUtility
                    .info(MessageConfigLoader.getProperty(IMessagesConstants.DATA_EXPORTED_TO, "user selected path."));
            return null;
        }

        private void doCompress() throws FileCompressException {
            if (importExportDataCore.getImportExportoptions().getZip()) {
                exportZipData.doCompress(importExportDataCore.getTempFilePath().toString(),
                        importExportDataCore.getFilePath().toString());
            }
        }

        /**
         * On out of memory error.
         *
         * @param err the err
         */
        @Override
        public void onOutOfMemoryError(OutOfMemoryError err) {

            try {
                exc.stop();
                elapsedTime = exc.getElapsedTime();
            } catch (DatabaseOperationException e2) {

                MPPDBIDELoggerUtility.error("Execute timer operation failed.", e2);
            }
            UIElement.getInstance().outOfMemoryCatch(elapsedTime, err.getMessage());

        }

        /**
         * On success UI action.
         *
         * @param obj the obj
         */
        @Override
        public void onSuccessUIAction(Object obj) {

            long endtime = System.currentTimeMillis();
            getConsoleMessages(endtime);

            StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            msg.append(importExportDataCore.getDisplayTableName());

            msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(MPPDBIDEConstants.LINE_SEPARATOR)
                    .append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_TOTAL_ROWS_EXPORT, this.totalRows));
            msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG, endtime - starttime));
            MPPDBIDEDialogs.clearExistingDialog();
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE),
                    msg.toString());

        }

        /**
         * On MPPDBIDE exception.
         *
         * @param exception the exception
         */
        @Override
        public void onMPPDBIDEException(MPPDBIDEException exception) {
            handleException(exception);
        }

        /**
         * On critical exception UI action.
         *
         * @param exception the exception
         */
        @Override
        public abstract void onCriticalExceptionUIAction(DatabaseCriticalException exception);

        /**
         * Gets the console messages.
         *
         * @param endtime the endtime
         * @return the console messages
         */
        protected void getConsoleMessages(long endtime) {
            // Print success message
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getInfo(importExportDataCore.getDisplayTableName()));
        }

        /**
         * Handle exception.
         *
         * @param exception the exception
         */
        protected abstract void handleException(final MPPDBIDEException exception);

        /**
         * On operational exception UI action.
         *
         * @param exception the exception
         */
        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            String msg = null == exception.getServerMessage() ? exception.getDBErrorMessage()
                    : exception.getServerMessage();
            if (null != msg && msg.contains("Maximum excel row limits reached")) {
                try {
                    importExportDataCore.cleanUp();
                } catch (DatabaseOperationException e1) {
                    MPPDBIDELoggerUtility.error("Error while deleting POI file in exception.", e1);
                }
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXCEL_ROW_LIMIT_REACHED));
            } else {
                handleException(exception);

                try {
                    if (null != this.importExportDataCore.getFilePath()) {
                        Files.deleteIfExists(this.importExportDataCore.getFilePath());
                    }

                } catch (IOException e1) {
                    MPPDBIDELoggerUtility.error("Error while deleting file in exception.", e1);
                }
            }
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
            super.finalCleanup();
            statusMessage = null;
            try {
                importExportDataCore.cleanUp();
            } catch (DatabaseOperationException e1) {
                MPPDBIDELoggerUtility.error("Error while deleting POI file in exception.", e1);
            }
        }

        /**
         * Canceling.
         */
        @Override
        protected void canceling() {
            super.canceling();
            try {
                importExportDataCore.cancelImportExportOperation();
            } catch (DatabaseCriticalException e) {
                getCancelExportErrorMessage();
            } catch (DatabaseOperationException e) {
                getCancelExportErrorMessage();
            }
        }

        /**
         * Gets the cancel export error message.
         *
         * @return the cancel export error message
         */
        protected abstract void getCancelExportErrorMessage();

        /**
         * Gets the console message window.
         *
         * @param importExportCore the import export core
         * @return the console message window
         */
        protected ConsoleMessageWindow getConsoleMessageWindow(AbstractImportExportDataCore importExportCore) {
            SQLTerminal terminal = UIElement.getInstance().getTerminal(importExportCore.getTerminalId());
            ConsoleMessageWindow consoleMessageWindow = null;
            if (terminal != null) {
                consoleMessageWindow = terminal.getConsoleMessageWindow(false);
            } else if (UIElement.getInstance().isEditorOnTopById()) {
                Database db = importExportCore.getDatabase();
                if (null != db && importExportCore.getTerminalId() != null) {
                    DebugObjects debugObjects = DatabaseUtils.getDebugObjects(db,
                            Long.parseLong(importExportCore.getTerminalId()));
                    if (debugObjects != null) {
                        PLSourceEditor plSourceEditor = UIElement.getInstance().getEditorModelById(debugObjects);
                        if (null != plSourceEditor) {
                            consoleMessageWindow = plSourceEditor.getConsoleMessageWindow(false);
                        }
                    }
                }
            }

            return consoleMessageWindow;
        }

        /**
         * Gets the database.
         *
         * @return the database
         */
        @Override
        protected Database getDatabase() {
            return importExportDataCore.getDatabase();
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
