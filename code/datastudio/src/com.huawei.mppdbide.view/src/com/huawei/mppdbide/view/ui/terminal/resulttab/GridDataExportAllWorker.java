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

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Observer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.presentation.exportdata.ExportZipData;
import com.huawei.mppdbide.presentation.exportdata.ImportExportDataCore;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileCompressException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import com.huawei.mppdbide.view.ui.dialog.ExportOptionDialog;
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
 * Description: The Class GridDataExportAllWorker.
 *
 * @since 3.0.0
 */
public class GridDataExportAllWorker extends ImportExportPreUIWorker {
    private TerminalExecutionConnectionInfra terminalConnection;
    private IDSGridDataProvider dataProvider;
    private ConsoleMessageWindow consoleMessageWindow;
    private AbstractImportExportDataCore importExportCore;
    private IExecTimer execTimer = new ExecTimer(null);
    private String resultTabName;
    private DBConnection dbConnection;
    private boolean isSQLTermContext;
    private BottomStatusBar statusBar;
    private StatusMessage statusMessage;
    private ExportZipData exportZipData;

    /**
     * Instantiates a new grid data export all worker.
     *
     * @param termConnectionCopy the term connection copy
     * @param consoleMessageWindow the console message window
     * @param resultTabName the result tab name
     * @param name the name
     * @param dataProvider the data provider
     * @param exportObserver the export observer
     * @param isSQLTermContext the is SQL term context
     * @param statusBar the status bar
     * @param statusMessage the status message
     */
    public GridDataExportAllWorker(TerminalExecutionConnectionInfra termConnectionCopy,
            ConsoleMessageWindow consoleMessageWindow, String resultTabName, String name,
            IDSGridDataProvider dataProvider, Observer exportObserver, boolean isSQLTermContext,
            BottomStatusBar statusBar, StatusMessage statusMessage) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE);
        this.terminalConnection = termConnectionCopy;
        this.consoleMessageWindow = consoleMessageWindow;
        this.dataProvider = dataProvider;
        execTimer = new ExecTimer("Export All data of " + resultTabName);
        this.resultTabName = resultTabName;
        importExportCore = new ImportExportDataCore(terminalConnection.getDatabase(), getColumnNames(), getQuery(),
                resultTabName, getQueryTime());
        importExportCore.setExport(true);
        importExportCore.addObserver(exportObserver);
        this.dbConnection = null;
        this.isSQLTermContext = isSQLTermContext;
        this.statusBar = statusBar;
        this.statusMessage = statusMessage;
        this.exportZipData = new ExportZipData();

    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return terminalConnection.getDatabase();
    }

    /**
     * Extract Query from data provider .
     *
     * @return the query
     */
    private String getQuery() {
        if (this.dataProvider instanceof DSResultSetGridDataProvider) {
            return ((DSResultSetGridDataProvider) this.dataProvider).getSummary().getQuery();
        }

        return "";
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
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (isSQLTermContext && terminalConnection instanceof TerminalExecutionSQLConnectionInfra) {
            setServerPwd(!((TerminalExecutionSQLConnectionInfra) terminalConnection).getReuseConnectionFlag()
                    && terminalConnection.getDatabase().getServer().getSavePrdOption()
                    .equals(SavePrdOptions.DO_NOT_SAVE));
            dbConnection = ((TerminalExecutionSQLConnectionInfra) terminalConnection).getSecureConnection(this);
            while (dbConnection == null) {
                Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
                if (this.isCancel()) {
                    return null;
                }
                if (this.isNotified()) {
                    dbConnection = this.terminalConnection.getConnection();
                }
            }
        } else {
            setServerPwd(
                terminalConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        }
        importExportCore.setExportIsInProgress(true);
        execTimer.start();
        long exportedRowCount = 0;
        try {
            exportedRowCount = importExportCore.executeExportData(dbConnection, dataProvider.isFuncProcExport());
        } catch (ParseException parseException) {
            Display.getDefault().syncExec(new Runnable() {

                /**
                 * Run.
                 */
                public void run() {
                    MPPDBIDEDialogs.generateErrorPopup(
                            MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_INFORMATION),
                            Display.getDefault().getActiveShell(), parseException, "",
                            MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_PARSER), null);
                }
            });
        }
        doCompress();
        execTimer.stop();

        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.DATA_EXPORTED_TO, "user selected path"));
        return exportedRowCount;
    }

    /**
     * On out of memory error.
     *
     * @param e the e
     */
    @Override
    public void onOutOfMemoryError(OutOfMemoryError exception) {
        String elapsedTime = "";
        try {
            execTimer.stop();
            elapsedTime = execTimer.getElapsedTime();
        } catch (DatabaseOperationException e2) {

            MPPDBIDELoggerUtility.error("Execute timer operation failed.", e2);
        }
        UIElement.getInstance().outOfMemoryCatch(elapsedTime, exception.getMessage());
    }

    private void doCompress() throws FileCompressException {
        if (importExportCore.getImportExportoptions().getZip()) {
            exportZipData.doCompress(importExportCore.getTempFilePath().toString(),
                    importExportCore.getFilePath().toString());
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        long totalRows = (long) obj;
        long elapsedTime = 0;
        try {
            elapsedTime = execTimer.getElapsedTimeInMs();
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }

        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_QUERY, resultTabName));

        msg.append(MPPDBIDEConstants.LINE_SEPARATOR)
                .append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_TOTAL_ROWS_EXPORT, totalRows));
        msg.append(MPPDBIDEConstants.LINE_SEPARATOR)
                .append(MessageConfigLoader.getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG, elapsedTime));
        MPPDBIDEDialogs.clearExistingDialog();
        String msgStr = msg.toString();
        logMsgs(msgStr);
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE), msgStr);
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(exception.getServerMessage()).append(MPPDBIDEConstants.LINE_SEPARATOR)
                .append(exception.getDBErrorMessage());

        logMsgs(msg.toString());
        deleteStaleFile();
        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                this.importExportCore.getDatabase());
    }

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
                importExportCore.cleanUp();
            } catch (DatabaseOperationException e1) {
                MPPDBIDELoggerUtility.error("Error while deleting POI file in exception.", e1);
            }
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXCEL_ROW_LIMIT_REACHED));
        } else {
            handleException(exception);
            deleteStaleFile();
        }
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        super.finalCleanup();
        if (isSQLTermContext && terminalConnection instanceof TerminalExecutionSQLConnectionInfra) {
            ((TerminalExecutionSQLConnectionInfra) terminalConnection).releaseSecureConnection(this.dbConnection);
        }
        try {
            importExportCore.cleanUp();
        } catch (DatabaseOperationException e1) {
            MPPDBIDELoggerUtility.error("Error while deleting POI file in exception.", e1);
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        this.importExportCore.importExportCleanUp();
        if (statusBar != null) {
            statusBar.hideStatusbar(statusMessage);
        }
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        try {
            this.importExportCore.cancelImportExportOperation();
        } catch (DatabaseCriticalException e) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
        } catch (DatabaseOperationException e) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
        }
    }

    private void handleException(final MPPDBIDEException mppdbExcp) {
        String msg = null == mppdbExcp.getServerMessage() ? mppdbExcp.getDBErrorMessage()
                : mppdbExcp.getServerMessage();
        if (null != msg && msg.contains("canceling statement due to user request")) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_CONSOLE_MESSAGE));
        } else if (null != msg && mppdbExcp.getCause() instanceof FileCompressException) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                    mppdbExcp.getCause().getMessage(), mppdbExcp);
        } else {
            String userMsg = MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                    MPPDBIDEConstants.LINE_SEPARATOR, msg);
            logMsgs(userMsg);
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE), userMsg);
        }
    }

    /**
     * Delete the stale file created because of export operation failure.
     */
    private void deleteStaleFile() {
        try {
            Files.deleteIfExists(this.importExportCore.getFilePath());

        } catch (IOException e1) {
            MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
        }
    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        super.preUISetup(preHandlerObject);
        return isSQLTermContext && terminalConnection instanceof TerminalExecutionSQLConnectionInfra
                && ((TerminalExecutionSQLConnectionInfra) terminalConnection).getReuseConnectionFlag() ? true
                        : promptAndValidatePassword();
    }

    // To be restructured.
    private boolean getInformationForExport(AbstractImportExportDataCore imprtExprtCore) {
        ExportOptionDialog exportOptDialog = new ExportOptionDialog(Display.getDefault().getActiveShell(),
                imprtExprtCore);
        int returnVal = exportOptDialog.open();
        if (returnVal != 0) {
            imprtExprtCore.setExportIsInProgress(false);
            return false;
        }
        return true;
    }

    /**
     * Checks if is export dialog.
     *
     * @return true, if is export dialog
     */
    public boolean isExportDialog() {
        return getInformationForExport(importExportCore);
    }

    /**
     * Find the query execution time from the summary
     */
    private String getQueryTime() {
        String queryTime = "";
        if (dataProvider instanceof DSResultSetGridDataProvider) {
            IQueryExecutionSummary summary = ((DSResultSetGridDataProvider) dataProvider).getSummary();
            queryTime = summary.getQueryStartDate();
        }
        return queryTime;
    }

    /**
     * Extract column names from the data provider. Should be extendable to
     * other type of data provider also, when need arises.
     *
     * @return the column names
     */
    private ArrayList<String> getColumnNames() {
        ArrayList<String> columns = new ArrayList<String>(5);
        if (this.dataProvider.getColumnDataProvider() != null) {
            String[] columnNames = this.dataProvider.getColumnDataProvider().getColumnNames();
            columns.addAll(Arrays.asList(columnNames));
        }
        return columns;
    }

    private void logMsgs(String msgs) {
        if (null != consoleMessageWindow) {
            consoleMessageWindow.logInfo(msgs);
        }
    }
}
