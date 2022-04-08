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

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.presentation.exportdata.ExportExcelApachePOI;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFileAttributes;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.component.grid.GridSelectionLayerPortData;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.ui.connectiondialog.SecurityDisclaimerDialog;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.uidisplay.uidisplayif.UIDisplayStateIf;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * Title: class Description: The Class GridResultDataSelectedCopyToExcel.
 *
 * @since 3.0.0
 */
public class GridResultDataSelectedCopyToExcel extends Observable {

    private GridSelectionLayerPortData selectData;

    private ConsoleMessageWindow consoleMessageWindow;

    private String userGivenFileName;

    private StatusMessage statusMessage;

    private String fileFormat;

    private List<String> headerList;

    private boolean cancelled;

    private Path newPath = null;

    private File newFileExcel = null;

    private int formatIndex;

    private static final String TEMPEXECLFILE = "tempFile";

    private String windowName = null;

    private String parentPath = null;

    /**
     * Instantiates a new grid result data selected copy to excel.
     *
     * @param selectData the select data
     * @param formatIndex the format index
     * @param consoleMessageWindow the console message window
     * @param queryExecutionSummary the query execution summary
     */
    public GridResultDataSelectedCopyToExcel(GridSelectionLayerPortData selectData, int formatIndex,
            ConsoleMessageWindow consoleMessageWindow, IQueryExecutionSummary queryExecutionSummary) {
        this.selectData = selectData;
        this.consoleMessageWindow = consoleMessageWindow;
        this.formatIndex = formatIndex;
    }

    /**
     * Export.
     */
    public void export() {
        int userOption = confirmOverridingDialog(
                MessageConfigLoader.getProperty(IMessagesConstants.COPY_TO_EXCEL_RESULT_FILE_NAME));

        if (userOption == 0) {
            endOfCopyToEXCEL();
            return;
        }

        List<String[]> gridData = readSelectDataFromView();
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMssage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        ExportExcelWorker worker = new ExportExcelWorker(windowName, selectData, this.userGivenFileName, fileFormat,
                statMssage, gridData);
        setStatusMessage(statMssage);
        StatusMessageList.getInstance().push(statMssage);
        if (bttmStatusBar != null) {
            bttmStatusBar.activateStatusbar();
        }
        worker.schedule();
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
     * Sets the cancel flag.
     *
     * @param iscancel the new cancel flag
     */
    public void setCancelFlag(boolean iscancel) {
        this.cancelled = iscancel;
    }

    private List<String[]> readSelectDataFromView() {
        this.selectData.initializeLayer();
        headerList = this.selectData.getHeaderList();
        List<String[]> listOfSelectRows = this.selectData.getRow();
        this.selectData.finalizeLayer();
        return listOfSelectRows;
    }

    private int confirmOverridingDialog(String defaultFileName) {
        int save = 1;
        UIDisplayStateIf uiDisplayState = UIDisplayFactoryProvider.getUIDisplayStateIf();
        if (uiDisplayState.isDisclaimerReq() && UserPreference.getInstance().getEnableSecurityWarningOption()) {
            Shell currentActiveShell = (Display.getCurrent() != null) ? Display.getCurrent().getActiveShell() : null;
            SecurityDisclaimerDialog copyFileDialog = new SecurityDisclaimerDialog(currentActiveShell);

            int returnValue = copyFileDialog.open();
            if (returnValue != 0) {
                save = 0;
                return save;
            }
        }
        parentPath = MPPDBIDEConstants.TEMP_FILE_PATH;
        if (parentPath == null && !FileValidationUtils.validateFilePathName(MPPDBIDEConstants.TEMP_FILE_PATH)) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FILE_FAIL_ERR));
            save = 0;
            return save;
        }

        newPath = Paths.get(parentPath + File.separator + TEMPEXECLFILE);
        boolean fileExists = Files.exists(newPath);
        if (!fileExists) {
            Set<String> supportedAttr = newPath.getFileSystem().supportedFileAttributeViews();
            FileAttribute<List<AclEntry>> fileAttributes = new DSFileAttributes(null);
            try {
                newPath = Files.createDirectory(newPath, fileAttributes);
                if (supportedAttr.contains("acl")) {
                    FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(newPath.toString(), true,
                            null, true);
                }
            } catch (IOException | DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FILE_FAIL_ERR),
                        exception);
            }
        }
        removeHistoricalFile(newPath);

        String newFileName = CustomStringUtility.sanitizeExportFileName(defaultFileName);

        fileFormat = getFileFormat();
        deleteExcelOrNot(newPath, newFileName);
        return save;
    }

    /**
     * Delete excel or not.
     *
     * @param newPath2 the new path 2
     * @param excelName the excel name
     *
     * @Title: deleteExcelOrNot
     * @Description: If the file is open, generate a new file, otherwise, delete
     * it and generate a file with the same name as the file
     */
    private void deleteExcelOrNot(Path newPath2, String excelName) {
        String lastCopyExcelFile = newPath + File.separator + excelName;
        DecimalFormat decimalFormat = new DecimalFormat("000");
        for (long i = 1; i < Long.MAX_VALUE; i++) {
            windowName = excelName + decimalFormat.format(i);
            if ("Excel(xlsx)".equals(fileFormat)) {
                userGivenFileName = lastCopyExcelFile + decimalFormat.format(i) + ".xlsx";
                newFileExcel = new File(userGivenFileName);
                newPath = Paths.get(userGivenFileName);
                if (newFileExcel.exists()) {
                    if (newFileExcel.renameTo(newFileExcel)) {
                        deleteExistingFile(newPath);
                    } else {
                        continue;
                    }
                }
                break;
            } else {
                userGivenFileName = lastCopyExcelFile + decimalFormat.format(i) + ".xls";
                newFileExcel = new File(userGivenFileName);
                newPath = Paths.get(userGivenFileName);
                if (newFileExcel.exists()) {
                    if (newFileExcel.renameTo(newFileExcel)) {
                        deleteExistingFile(newPath);
                        break;
                    } else {
                        continue;
                    }
                } else {
                    break;
                }
            }
        }
    }

    /**
     * Removes the historical file.
     *
     * @param historyPath the history path
     *
     * @Title: removeHistoricalFile
     * @Description: Delete history file
     */
    private void removeHistoricalFile(Path historyPath) {
        String[] fileList = new File(String.valueOf(historyPath)).list();
        if (fileList != null) {
            for (String tmpFile : fileList) {
                newFileExcel = new File(historyPath + File.separator + tmpFile);
                if (newFileExcel.exists() && newFileExcel.renameTo(newFileExcel)) {
                    if (newFileExcel.delete()) {
                        continue;
                    }
                }
            }
        }
    }

    private String getFileFormat() {
        String fileFormt = null;
        int filterIndex = formatIndex;
        if (filterIndex == 0) {
            fileFormt = "Excel(xlsx)";
        } else if (filterIndex == 1) {
            fileFormt = "Excel(xls)";
        } 
        return fileFormt;
    }

    private void deleteExistingFile(Path excelPath) {
        try {
            Files.delete(excelPath);
        } catch (IOException exception) {
            String message = exception.getMessage();

            consoleMessageWindow
                    .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + message);

            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, message));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    exception);
        }
    }

    /**
     * End of copy to EXCEL.
     */
    public void endOfCopyToEXCEL() {
        setChanged();
        notifyObservers(true);
    }

    private void performCopyToExcelSuccessAction(ExecTimer timer) {
        try {
            Desktop.getDesktop().open(new File(userGivenFileName));
        } catch (IOException e) {
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.FAILED_TO_OPEN_FILE_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.FAILED_TO_OPEN_FILE_MESSAGE));
        }
        if (consoleMessageWindow != null) {
            consoleMessageWindow
                    .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.MSG_EXPORT_SUCC_FILE, windowName));
        }
    }

    private void handleOperationalException(DatabaseOperationException e) {
        String exceptionMsg = e.getServerMessage();
        if (null == exceptionMsg) {
            exceptionMsg = e.getDBErrorMessage();
        }
        if (!(e.getCause() instanceof FileNotFoundException || e.getCause() instanceof FileAlreadyExistsException)) {
            consoleMessageWindow.logInfo(e.getMessage());
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OPEN_IN_EXCEL_OPERATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, exceptionMsg));
        }
        clearCopyFailedFile();
    }

    private void clearCopyFailedFile() {
        try {
            Files.deleteIfExists(Paths.get(this.userGivenFileName));
        } catch (IOException e1) {
            MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
        }
    }

    private void handleCriticalException(DatabaseCriticalException e) {
        StringBuilder exceptionMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        exceptionMsg.append(e.getServerMessage());

        exceptionMsg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(e.getDBErrorMessage());
        consoleMessageWindow
                .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + e.getMessage());
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exceptionMsg.toString());
        clearCopyFailedFile();
    }

    private void handleCopyToExcelException(Exception exception) {
        consoleMessageWindow.logInfoInUI(
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exception.getMessage());
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exception.getMessage());
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY), exception);
        clearCopyFailedFile();
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
     * Title: class Description: The Class ExportExcelWorker.
     */
    private final class ExportExcelWorker extends UIWorkerJob {
        private GridSelectionLayerPortData selectData;

        private String userGivenFileName;

        private ExecTimer timer;

        private String fileFormat;

        private StatusMessage statusMsg;

        private List<String[]> gridData;

        private ExportExcelApachePOI copyToExcel;

        public ExportExcelWorker(String windowName, GridSelectionLayerPortData selectData, String userGivenFileName,
                String fileFormat, StatusMessage statusMsg, List<String[]> gridData) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_OPEN_IN_EXCEL_PROGRESS_NAME, windowName),
                    MPPDBIDEConstants.CANCELABLEJOB);
            this.selectData = selectData;
            this.userGivenFileName = userGivenFileName;
            this.fileFormat = fileFormat;
            this.statusMsg = statusMsg;
            this.gridData = gridData;
            timer = new ExecTimer("Open In Excel");
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            timer.start();
            copyToExcel();
            timer.stop();
            return null;
        }

        private void copyToExcel() throws MPPDBIDEException {
            if (MPPDBIDEConstants.STR_EXCEL_XLSX.equalsIgnoreCase(fileFormat)) {
                ThreadLocal<TempFileCreationStrategy> threadLocal = new ThreadLocal<TempFileCreationStrategy>() {
                    @Override
                    protected TempFileCreationStrategy initialValue() {
                        return createTempFileCreationStrategy();
                    }
                };
                TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy());
                TempFileCreationStrategy tempFileCreationStrategy = threadLocal.get();
                createTempFile(tempFileCreationStrategy);
            }
            copyToExcel = new ExportExcelApachePOI(fileFormat);
            if (copyToExcel.checkRowLength(this.selectData.getRowSelectCount())
                    && copyToExcel.checkColLength(this.selectData.getHeaderList().size())) {
                copyToExcelIfValid();
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT));
                throw new DatabaseOperationException(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT);
            }
        }

        private void copyToExcelIfValid() throws DatabaseOperationException, MPPDBIDEException {
            copyToExcel.createSheet(windowName);
            copyToExcel.createHeaderRow(headerList);
            int rowNum = 1;
            try {
                List<String> outputRow = new ArrayList<String>();
                for (String[] valueStr : gridData) {
                    if (!cancelled) {
                        outputRow.addAll(Arrays.asList(valueStr));
                        try {
                            copyToExcel.setCellValue(outputRow, rowNum);
                        } catch (ParseException e) {
                            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                                    getWindowImage(),
                                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_INFORMATION),
                                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_PARSER));
                        }
                        rowNum++;
                        outputRow.clear();
                    } else {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                        throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
                    }
                }
            } catch (Exception e) {
                if (e.getMessage().contains(MPPDBIDEConstants.DISK_FULL_ERR_MSG)) {
                    throw new DatabaseOperationException(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE);
                }
                if (e.getMessage().contains("Operation canceled on user request")) {
                    throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
                } else {
                    throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE);
                }
            }
            copyToExcel.writeToWorkbook(userGivenFileName, UserPreference.getInstance().getFileEncoding());
        }

        private void createTempFile(TempFileCreationStrategy tempFileCreationStrategy)
                throws DatabaseOperationException {
            try {
                TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy());
                if (tempFileCreationStrategy != null) {
                    tempFileCreationStrategy.createTempFile("", "");
                }
            } catch (IOException e1) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET), e1);
                throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET, e1);
            }
        }

        private TempFileCreationStrategy createTempFileCreationStrategy() {
            return new TempFileCreationStrategy() {

                @Override
                public File createTempFile(String arg0, String arg1) throws IOException {
                    Path copyToExcelPath = Paths.get(userGivenFileName);
                    Path parent = copyToExcelPath.getParent();
                    Path superParent = parent != null ? parent.getParent() : null;
                    if (null != superParent) {
                        System.setProperty("java.io.tmpdir", superParent.toString());
                        File dir = null;
                        if (FileValidationUtils.validateFilePathName(MPPDBIDEConstants.TEMP_FILE_PATH)) {
                            dir = new File(MPPDBIDEConstants.TEMP_FILE_PATH, superParent.toString());
                        }
                        return dir;
                    } else {
                        return copyToExcelPath.toFile();
                    }
                }

                @Override
                public File createTempDirectory(String arg0) throws IOException {
                    return null;
                }
            };
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            performCopyToExcelSuccessAction(timer);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            handleOperationalException(e);
        }

        @Override
        public void onException(Exception e) {
            handleCopyToExcelException(e);
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            try {
                copyToExcel.cleanUpWorkbookPOIFiles(userGivenFileName);
            } catch (DatabaseOperationException e1) {
                MPPDBIDELoggerUtility.error("Error while deleting file in exception.", e1);
            }
            statusMessage = null;
        }

        @Override
        protected void canceling() {
            super.canceling();
            setCancelFlag(true);
        }

        @Override
        public void finalCleanupUI() {
            endOfCopyToEXCEL();
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            handleCriticalException(e);

        }
    }

}
