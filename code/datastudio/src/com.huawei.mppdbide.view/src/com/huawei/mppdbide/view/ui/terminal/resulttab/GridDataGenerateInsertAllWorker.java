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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Observer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.exportdata.ExportZipData;
import com.huawei.mppdbide.presentation.exportdata.GenerateSQLDataCore;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileCompressException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import com.huawei.mppdbide.view.ui.dialog.ExportZipOptionDialog;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.DSDeleteFileExport;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridDataGenerateInsertAllWorker.
 *
 * @since 3.0.0
 */
public class GridDataGenerateInsertAllWorker extends ImportExportPreUIWorker {
    private TerminalExecutionConnectionInfra termConnection;
    private IDSGridDataProvider dataProvider;
    private ConsoleMessageWindow consoleMessageWindow;
    private GenerateSQLDataCore generateSQLDataCore;
    private IExecTimer execTimer = new ExecTimer(null);
    private String resultTabName;
    private DBConnection conn;
    private boolean isSQLTermContext;
    private BottomStatusBar statusBar;
    private StatusMessage statusMessage;
    private Path newPath = null;
    private Path zipPath;
    private boolean isZip;

    /**
     * Instantiates a new grid data generate insert all worker.
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
     * @param encode the encode
     */
    public GridDataGenerateInsertAllWorker(TerminalExecutionConnectionInfra termConnectionCopy,
            ConsoleMessageWindow consoleMessageWindow, String resultTabName, String name,
            IDSGridDataProvider dataProvider, Observer exportObserver, boolean isSQLTermContext,
            BottomStatusBar statusBar, StatusMessage statusMessage, String encode) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE);
        this.termConnection = termConnectionCopy;
        this.consoleMessageWindow = consoleMessageWindow;
        this.dataProvider = dataProvider;
        execTimer = new ExecTimer("Generate All Data Insert Sql Of " + resultTabName);
        this.resultTabName = resultTabName;
        generateSQLDataCore = new GenerateSQLDataCore(termConnection.getDatabase(), getQuery(), encode, getUserName());
        generateSQLDataCore.addObserver(exportObserver);
        this.conn = null;
        this.isSQLTermContext = isSQLTermContext;
        this.statusBar = statusBar;
        this.statusMessage = statusMessage;

    }

    /**
     * Gets the query.
     *
     * @return the query
     *
     * @Title: getQuery
     * @Description: Extract Query from data provider .
     */
    private String getQuery() {
        if (this.dataProvider instanceof DSResultSetGridDataProvider) {
            return ((DSResultSetGridDataProvider) this.dataProvider).getSummary().getQuery();
        }

        return "";
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return termConnection.getDatabase();
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
        if (isSQLTermContext && termConnection instanceof TerminalExecutionSQLConnectionInfra) {
            setServerPwd(!((TerminalExecutionSQLConnectionInfra) termConnection).getReuseConnectionFlag()
                    && termConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            conn = ((TerminalExecutionSQLConnectionInfra) termConnection).getSecureConnection(this);
            while (conn == null) {
                Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
                if (this.isCancel()) {
                    return null;
                }
                if (this.isNotified()) {
                    conn = this.termConnection.getConnection();
                }
            }
        } else {
            setServerPwd(
                    termConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        }
        generateSQLDataCore.setExportIsInProgress(true);
        execTimer.start();
        long generateRowCount = 0;

        generateSQLDataCore.setExportIsInProgress(true);
        generateRowCount = generateSQLDataCore.executeExportData(conn, newPath);
        doCompress();
        execTimer.stop();
        MPPDBIDELoggerUtility
                .info(MessageConfigLoader.getProperty(IMessagesConstants.DATA_EXPORTED_TO, "user selected path"));
        return generateRowCount;
    }

    private void doCompress() throws FileCompressException {
        if (this.isZip) {
            new ExportZipData().doCompress(this.newPath.toString(), this.zipPath.toString());
        }
    }

    /**
     * On out of memory error.
     *
     * @param e the e
     */
    @Override
    public void onOutOfMemoryError(OutOfMemoryError exception) {
        generateSQLDataCore.cleanUpDataCore();
        deleteStaleFile();
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_GENERATE_INSERT_ERROR),
                exception);
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        long countRows = (long) obj;
        long consumptionTime = 0;
        try {
            consumptionTime = execTimer.getElapsedTimeInMs();
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }

        StringBuilder succMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        succMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_SQL, resultTabName));

        succMsg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(
                MessageConfigLoader.getProperty(IMessagesConstants.MSG_TOTAL_ROWS_EXPORT_INSERT_SQL, countRows));
        succMsg.append(MPPDBIDEConstants.LINE_SEPARATOR)
                .append(MessageConfigLoader.getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG, consumptionTime));
        MPPDBIDEDialogs.clearExistingDialog();
        String msgStr = succMsg.toString();
        logMsgs(msgStr);
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_SQL_SUCCES_DAILOG_TITLE), msgStr);
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(exception.getServerMessage()).append(MPPDBIDEConstants.LINE_SEPARATOR)
                .append(exception.getDBErrorMessage());
        logMsgs(msg.toString());
        deleteStaleFile();
        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception,
                this.generateSQLDataCore.getDatabase());
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        String msg = null == exception.getServerMessage() ? exception.getDBErrorMessage()
                : exception.getServerMessage();
        if (null != msg) {
            generateSQLDataCore.cleanUpDataCore();
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQLL_OPERATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));
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
        if (isSQLTermContext && termConnection instanceof TerminalExecutionSQLConnectionInfra) {
            ((TerminalExecutionSQLConnectionInfra) termConnection).releaseSecureConnection(this.conn);
        }
        generateSQLDataCore.cleanUpDataCore();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        this.generateSQLDataCore.importExportCleanUp();
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
            generateSQLDataCore.cancelExportOperation();
        } catch (DatabaseCriticalException exception) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
        } catch (DatabaseOperationException exception) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_CANCELMSG));
        }
    }

    /**
     * Handle exception.
     *
     * @param exception the exception
     */
    public void handleException(final MPPDBIDEException exception) {
        String msg = null == exception.getServerMessage() ? exception.getDBErrorMessage()
                : exception.getServerMessage();
        if (null != msg && msg.contains("canceling statement due to user request")) {
            logMsgs(MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_CONSOLE_MESSAGE));
        }

    }

    /**
     * 
     * @Title: deleteStaleFile
     * @Description: Delete the stale file created because of export operation
     * failure.
     */
    private void deleteStaleFile() {
        try {
            if (this.generateSQLDataCore.getPath() != null) {
                Files.deleteIfExists(this.generateSQLDataCore.getPath());
            }
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
        return isSQLTermContext && termConnection instanceof TerminalExecutionSQLConnectionInfra
                && ((TerminalExecutionSQLConnectionInfra) termConnection).getReuseConnectionFlag() ? true
                        : promptAndValidatePassword();
    }

    /**
     * Checks if is save sql file dialog.
     *
     * @return true, if is save sql file dialog
     */
    public boolean isSaveSqlFileDialog() {
        boolean mark = true;
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyyHHmmss");
        String defaultFileName = resultTabName + "_" + dateFormat.format(new Date());

        ExportZipOptionDialog exportZipOptionDialog = new ExportZipOptionDialog(Display.getDefault().getActiveShell(),
                defaultFileName, true, false,
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_SQL_EXPORT_WINDOW_TITLE, resultTabName));
        int returnValue = exportZipOptionDialog.open();
        if (returnValue != 0) {
            mark = false;
            return mark;
        }

        isZip = exportZipOptionDialog.getExportOption().isZip();

        String sqlPath = exportZipOptionDialog.getExportOption().getFilePathWithSuffixFormat();
        newPath = Paths.get(sqlPath);

        boolean fileExists = Files.exists(newPath);

        // If file already exists , confirm for overwriting the file.
        if (fileExists) {
            DSDeleteFileExport deleteFileExport = new DSDeleteFileExport();
            deleteFileExport.deleteFile(newPath, "Error generate SQL file:", "Error generate SQL file:", sqlPath);
        }
        ISetFilePermission setFilePermission = FilePermissionFactory.getFilePermissionInstance();
        try {
            // create the file with security permissions
            setFilePermission.createFileWithPermission(sqlPath, false, null, false);
        } catch (DatabaseOperationException e) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty("Error generate SQL file:", sqlPath)));
            mark = false;
            return mark;
        }

        if (isZip) {
            this.zipPath = newPath;
            String tempPath = null;
            tempPath = Normalizer.normalize(System.getenv(MPPDBIDEConstants.TEMP_ENVIRONMENT_VARIABLE),
                    Normalizer.Form.NFD);
            if (!DSFilesWrapper.isExistingDirectory(tempPath)) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                MPPDBIDEConstants.LINE_SEPARATOR,
                                MessageConfigLoader.getProperty(IMessagesConstants.INVALID_TEMP_ENVIRONMENT_VARIABLE)));
                MPPDBIDELoggerUtility.error("TEMP environment varibale is not an existing directory.");
                return false;
            }
            this.newPath = Paths.get(ExportZipData.getTempPathStr(newPath.toString(), ".sql", tempPath));
        }

        return mark;
    }

    private void logMsgs(String msgs) {
        if (null != consoleMessageWindow) {
            consoleMessageWindow.logInfo(msgs);
        }
    }

    private String getUserName() {
        return termConnection.getDatabase().getServer().getServerConnectionInfo().getDsUsername();
    }
}
