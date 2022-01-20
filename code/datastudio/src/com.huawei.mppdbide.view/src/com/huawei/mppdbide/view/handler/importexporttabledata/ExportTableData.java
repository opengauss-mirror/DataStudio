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

package com.huawei.mppdbide.view.handler.importexporttabledata;

import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.core.runtime.jobs.Job;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.presentation.exportdata.ImportExportDataCore;
import com.huawei.mppdbide.presentation.objectbrowser.ObjectBrowserObjectRefreshPresentation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportTableData.
 *
 * @since 3.0.0
 */
public class ExportTableData extends AbstractExportTableData {
    private long starttime;

    /**
     * Excute export.
     *
     * @param selectedTable the selected table
     */
    public void excuteExport(TableMetaData selectedTable) {
        try {
            if (!selectedTable.isLoaded()) {
                ObjectBrowserObjectRefreshPresentation.refreshSeverObject(selectedTable);
                if (null == selectedTable.getNamespace().getTables().getObjectById(selectedTable.getOid())) {
                    generateExportErrorMessageDialog();
                    return;
                }
            }
        } catch (MPPDBIDEException exception) {
            generateExportErrorMessageDialog(exception);
            return;
        }

        ImportExportDataCore importExportCore = new ImportExportDataCore(selectedTable,
                ImportExportTableData.getColoumns(selectedTable.getColumns()), null, null, null);
        importExportCore.setExport(true);
        boolean result = getInformationForExport(importExportCore);
        if (!result) {
            importExportCore.importExportCleanUp();
            return;
        }
        scheduleExportDataJob(importExportCore);
    }

    /**
     * Schedule export data job.
     *
     * @param importExportCore the import export core
     */
    public void scheduleExportDataJob(ImportExportDataCore importExportCore) {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();

        StatusMessage statMssage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));

        starttime = System.currentTimeMillis();
        ExportTableDataWorker exportTableDatWorker = new ExportTableDataWorker(importExportCore, statMssage, starttime);
        exportTableDatWorker.setTaskDB(importExportCore.getDatabase());
        setStatusMessage(statMssage);
        StatusMessageList.getInstance().push(statMssage);
        if (bttmStatusBar != null) {
            bttmStatusBar.activateStatusbar();
        }
        exportTableDatWorker.schedule();

    }

    /**
     *
     * Title: class
     * 
     * Description: The Class ExportTableDataWorker.
     */
    private final class ExportTableDataWorker extends ExportDataWorker {

        /**
         * Instantiates a new export data worker.
         *
         * @param core the core
         * @param statusMsg the status msg
         */
        protected ExportTableDataWorker(AbstractImportExportDataCore core, StatusMessage statusMsg, long starttime) {
            super(core, statusMsg, starttime);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            StringBuilder message = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            message.append(exception.getServerMessage());

            message.append(MPPDBIDEConstants.LINE_SEPARATOR).append(exception.getDBErrorMessage());
            String objectName = importExportDataCore.getImportExportServerObj().getName();
            String schemaName = null;
            ServerObject serverObject = importExportDataCore.getImportExportServerObj().getNamespace();
            if (serverObject != null) {
                schemaName = serverObject.getName();
            }

            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.ERR_EXPORT_TABLE_FAIL, schemaName, objectName)));

            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, message.toString()),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));
            // Delete the file on exception
            deleteFileIfExists();

            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                    importExportDataCore.getDatabase());

        }

        private void deleteFileIfExists() {
            try {
                if (null != this.importExportDataCore.getFilePath()) {
                    Files.deleteIfExists(this.importExportDataCore.getFilePath());
                }
            } catch (IOException ioException) {
                MPPDBIDEDialogs.generateErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_DELETE_FILE_MSG), ioException);
            }
        }

        /**
         * Handle exception.
         *
         * @param exception the e
         */
        protected void handleException(final MPPDBIDEException exception) {
            String msg = exception.getServerMessage();
            if (null == msg) {
                msg = exception.getDBErrorMessage();
            }
            String objectName = importExportDataCore.getImportExportServerObj().getName();
            String schemaName = null;
            ServerObject serverObject = importExportDataCore.getImportExportServerObj().getNamespace();
            if (serverObject != null) {
                schemaName = serverObject.getName();
            }
            if (exception.getServerMessage() != null
                    && exception.getServerMessage().contains("canceling statement due to user request")) {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_EXPORT_TABLE_FAIL, schemaName, objectName)));

                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_CONSOLE_MESSAGE),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK));

            } else {
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_EXPORT_TABLE_FAIL, schemaName, objectName)));
                
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                MPPDBIDEConstants.LINE_SEPARATOR, msg));
            }

        }

        /**
         * Gets the cancel export error message.
         *
         * @return the cancel export error message
         */
        protected void getCancelExportErrorMessage() {
            if (importExportDataCore.getImportExportServerObj() instanceof TableMetaData) {
                ObjectBrowserStatusBarProvider.getStatusBar()
                        .displayMessage(Message.getInfo(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
            } else {
                ConsoleMessageWindow consoleMessageWindow = getConsoleMessageWindow(importExportDataCore);
                if (null != consoleMessageWindow) {
                    consoleMessageWindow
                            .logInfo(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
                }
            }
        }

        /**
         * Gets the console messages.
         *
         * @param endtime the endtime
         */
        protected void getConsoleMessages(long endtime) {
            super.getConsoleMessages(endtime);
        }
    }
}
