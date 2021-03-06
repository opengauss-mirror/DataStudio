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

package org.opengauss.mppdbide.view.ui.terminal.resulttab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.export.ExportManager;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.ui.connectiondialog.SecurityDisclaimerDialog;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanSaveWorker.
 *
 * @since 3.0.0
 */
public class ExecutionPlanSaveWorker extends UIWorkerJob {
    private IDSGridDataProvider data;
    private String windowName;
    private String userGivenFileName;
    private ConsoleMessageWindow consoleMessageWindow;
    private String defaultFileName;
    private IExecTimer timer;
    private OutputStreamWriter filewriter;
    private FileOutputStream fileOutStream;
    private SavePlanWorker worker;
    private Shell shell;
    private int userOption;

    /**
     * Instantiates a new execution plan save worker.
     *
     * @param data the data
     * @param windowName the window name
     * @param consoleMessageWindow the console message window
     * @param on the on
     */
    public ExecutionPlanSaveWorker(IDSGridDataProvider data, String windowName,
            ConsoleMessageWindow consoleMessageWindow, Observer on) {
        super(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_SAVE, windowName),
                MPPDBIDEConstants.CANCELABLEJOB);
        this.data = data;
        this.windowName = windowName;
        this.consoleMessageWindow = consoleMessageWindow;
        this.defaultFileName = windowName + '_' + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        worker = new SavePlanWorker();
        worker.addObserver(on);
    }

    /**
     * Gets the user confirm.
     *
     * @return the user confirm
     */
    public void getUserConfirm() {
        shell = Display.getDefault().getActiveShell();
        userOption = confirmOverridingDialog(this.defaultFileName);
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
        if (userOption != UIConstants.OK_ID) {
            worker.setExportIsInProgress(false);
            return null;
        }
        return worker.save();
    }

    private int confirmOverridingDialog(String fineName) {
        int res = UIConstants.OK_ID;
        UIDisplayStateIf uiDisplayState = UIDisplayFactoryProvider.getUIDisplayStateIf();
        if (uiDisplayState.isDisclaimerReq() && UserPreference.getInstance().getEnableSecurityWarningOption()) {
            SecurityDisclaimerDialog dialog = new SecurityDisclaimerDialog(shell);

            int returnValue = dialog.open();
            if (returnValue != UIConstants.OK_ID) {
                return res;
            }
        }

        fineName = CustomStringUtility.sanitizeExportFileName(fineName) + ".txt";
        FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        dialog.setOverwrite(true);
        dialog.setFilterNames(new String[] {"txt", "All Files (*.*)"});
        dialog.setFilterExtensions(new String[] {"*.txt"});
        dialog.setFileName(fineName);
        userGivenFileName = dialog.open();
        if (userGivenFileName == null || userGivenFileName.length() < 1) {
            return res;
        }

        if (!userGivenFileName.toLowerCase(Locale.ENGLISH).endsWith(".txt")) {
            userGivenFileName = userGivenFileName + ".txt";
        }

        Path newPath = Paths.get(userGivenFileName);
        boolean isFileExist = Files.exists(newPath);

        // If file already exists , confirm for overwriting the file.
        if (isFileExist) {
            deleteExistingFile(newPath);
        }
        return res;
    }

    private void deleteExistingFile(Path newPath) {
        try {
            // Delete the file if file exists and chooses to overwrite.
            Files.delete(newPath);
        } catch (IOException exception) {
            String msg = exception.getMessage();

            consoleMessageWindow
                    .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + msg);

            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    exception);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class SavePlanWorker.
     */
    private class SavePlanWorker extends Observable {

        /**
         * Instantiates a new save plan worker.
         */
        public SavePlanWorker() {
            timer = new ExecTimer("Save execution plan to text file");
        }

        /**
         * Save.
         *
         * @return the object
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         * @throws MPPDBIDEException the MPPDBIDE exception
         * @throws Exception the exception
         */
        public Object save()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            timer.start();
            createFile();
            List<IDSGridDataRow> allRows = data.getAllFetchedRows();
            for (IDSGridDataRow oneRow : allRows) {
                Object[] values = oneRow.getValues();
                if (values != null) {
                    for (Object value : values) {
                        filewriter.write((String) value);
                    }
                }
                filewriter.write(MPPDBIDEConstants.LINE_SEPARATOR);
            }
            timer.stop();
            return null;
        }

        /**
         * Sets the export is in progress.
         *
         * @param isExportIsInProgres the new export is in progress
         */
        public void setExportIsInProgress(boolean isExportIsInProgres) {
            setChanged();
            notifyObservers(isExportIsInProgres);
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String elapsedTime = null;
        try {
            elapsedTime = timer.getElapsedTime();
        } catch (DatabaseOperationException e) {
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }
        msg.append(MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_SAVE_POPUP_MSG))
                .append(MPPDBIDEConstants.LINE_SEPARATOR).append(MessageConfigLoader
                        .getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG_RESULT, elapsedTime));

        MPPDBIDEDialogs.clearExistingDialog();
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_PLAN_SAVE_POPUP_TITLE), msg.toString());
        consoleMessageWindow
                .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.MSG_EXPORT_SUCC_FILE, windowName));

    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {

    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        consoleMessageWindow.logInfo(exception.getMessage());
        clearFailedFile();
    }

    private void clearFailedFile() {
        try {
            Files.deleteIfExists(Paths.get(this.userGivenFileName));
        } catch (IOException e1) {
            MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
        }
    }

    /**
     * On exception.
     *
     * @param exception the exception
     */
    @Override
    public void onException(Exception exception) {
        if (exception instanceof IOException) {
            consoleMessageWindow.logInfoInUI(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exception.getMessage());
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    exception);
            clearFailedFile();
        }
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        closeResources(filewriter, fileOutStream);
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        worker.setExportIsInProgress(false);
    }

    private void createFile() throws DatabaseOperationException, IOException {
        ISetFilePermission withPermission = FilePermissionFactory.getFilePermissionInstance();
        Path path = withPermission.createFileWithPermission(this.userGivenFileName, false, null, false);

        fileOutStream = new FileOutputStream(path.toString(), true);
        if (UserPreference.getInstance().getFileEncoding().isEmpty()) {
            filewriter = new OutputStreamWriter(fileOutStream, Charset.defaultCharset().name());
        } else {
            filewriter = new OutputStreamWriter(fileOutStream, MPPDBIDEConstants.GS_DUMP_ENCODING);
        }
        ExportManager.prependBomForUtf8(UserPreference.getInstance().getFileEncoding(), fileOutStream);
    }

    private void closeResources(OutputStreamWriter outputFileStrmwriter, FileOutputStream fileOutputStream) {
        try {
            if (outputFileStrmwriter != null) {
                outputFileStrmwriter.close();
            }
        } catch (IOException ex) {
            outputFileStrmwriter = null;
        }

        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException ex) {
            fileOutputStream = null;
        }
    }

}
