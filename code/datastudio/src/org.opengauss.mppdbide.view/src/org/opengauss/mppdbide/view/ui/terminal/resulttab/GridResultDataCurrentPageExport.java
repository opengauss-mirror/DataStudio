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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import org.apache.poi.util.DefaultTempFileCreationStrategy;
import org.apache.poi.util.TempFile;
import org.apache.poi.util.TempFileCreationStrategy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import org.opengauss.mppdbide.bl.export.ExportManager;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ExportOption;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import org.opengauss.mppdbide.presentation.exportdata.ExportExcelApachePOI;
import org.opengauss.mppdbide.presentation.exportdata.ExportZipData;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileCompressException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.files.DSFilesWrapper;
import org.opengauss.mppdbide.utils.files.FilePermissionFactory;
import org.opengauss.mppdbide.utils.files.FileValidationUtils;
import org.opengauss.mppdbide.utils.files.ISetFilePermission;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.component.grid.GridViewPortData;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import org.opengauss.mppdbide.view.ui.dialog.ExportZipOptionDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class GridResultDataCurrentPageExport.
 *
 * @since 3.0.0
 */
public class GridResultDataCurrentPageExport extends Observable {
    private GridViewPortData rows;

    private ExecutionPlanTextDisplayGrid textView;

    private ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView;

    private ConsoleMessageWindow consoleMessageWindow;

    private IQueryExecutionSummary querySummary;

    private String windowName;

    private String userGivenFileName;

    private StatusMessage statusMessage;

    private String fileFormat;

    private List<String> headerList;

    private boolean cancelled;

    private boolean isZip;

    private boolean isText;

    private boolean isTree;

    private String zipFileName;

    /**
     * Instantiates a new grid result data current page export.
     *
     * @param gridViewPortData the grid view port data
     * @param consoleMessageWindow the console message window
     * @param queryExecutionSummary the query execution summary
     * @param windowName the window name
     */
    public GridResultDataCurrentPageExport(GridViewPortData gridViewPortData, ConsoleMessageWindow consoleMessageWindow,
            IQueryExecutionSummary queryExecutionSummary, String windowName) {
        this.rows = gridViewPortData;
        this.consoleMessageWindow = consoleMessageWindow;
        this.querySummary = queryExecutionSummary;
        this.windowName = windowName;
    }

    /**
     * Instantiates a new grid result data current page export.
     *
     * @param gridViewPortDataExec the grid view port data execution
     * @param execPlanTextDisplayGrid the execution plan text display grid
     * @param explainAnalyzePlanNodeTreeDisplayDataTreeFormat the execution plan
     * tree display data
     * @param consoleMessageWindow the console message window
     * @param queryExecutionSummary the query execution summary
     * @param windowName the window name
     */
    public GridResultDataCurrentPageExport(GridViewPortData gridViewPortDataExec,
            ExecutionPlanTextDisplayGrid executionPlanTextDisplayGrid,
            ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat explainAnalyzePlanNodeTreeDisplayDataTreeFormat,
            ConsoleMessageWindow consoleMessageWindow, IQueryExecutionSummary queryExecutionSummary, String windowName,
            boolean isExecPlan, boolean isTreeMode) {
        this.rows = gridViewPortDataExec;
        this.textView = executionPlanTextDisplayGrid;
        this.treeView = explainAnalyzePlanNodeTreeDisplayDataTreeFormat;
        this.consoleMessageWindow = consoleMessageWindow;
        this.querySummary = queryExecutionSummary;
        this.windowName = windowName;
        this.isText = isExecPlan;
        this.isTree = isTreeMode;
    }

    /**
     * Export.
     */
    public void export(boolean isCursorPopType) {
        // Get file path => display thread -> current thread
        // Create file => UIWorkerJob
        // Write data => UIWorkerJob
        // Inform user => UIWorkerJob->display

        int userOption = 0;
        if (isCursorPopType) {

            DateFormat df = new SimpleDateFormat(MPPDBIDEConstants.DATE_FORMAT);
            String currentDate = df.format(new Date());
            userOption = confirmOverridingDialog(windowName + '_'
                    + CustomStringUtility.convertStringDateFormat(currentDate, MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
        } else {
            String currentDate = null;
            if (null == querySummary) {
                DateFormat dateFormat = new SimpleDateFormat(MPPDBIDEConstants.DATE_COLLAPSE_FORMAT);
                Date date = new Date();
                currentDate = dateFormat.format(date);
                userOption = confirmOverridingDialog(windowName + '_' + CustomStringUtility
                        .convertStringDateFormat(currentDate, MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
            } else {
                userOption = confirmOverridingDialog(windowName + '_' + CustomStringUtility.convertStringDateFormat(
                        querySummary.getQueryStartDate(), MPPDBIDEConstants.DATE_COLLAPSE_FORMAT));
            }

        }

        if (userOption == 0) {
            // User not interested, skip.
            endOfExport();
            return;
        }
        if (isTree) {
            this.treeView.treeExport();
        }
        this.rows.initializeLayer();
        headerList = Arrays.asList(this.rows.getRow());
        List<String[]> gridData = (isTree) ? this.treeView.getListExec() : readDataFromView();
        checkExportFormatType(gridData);
    }

    private void checkExportFormatType(List<String[]> gridDataList) {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMssage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        if ("Text".equals(fileFormat)) {
            ExportTextWorker worker = new ExportTextWorker(windowName, rows, this.userGivenFileName, statMssage);
            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bttmStatusBar != null) {
                bttmStatusBar.activateStatusbar();
            }
            worker.schedule();
        } else {
            ExportExcelWorker worker = new ExportExcelWorker(windowName, rows, this.userGivenFileName, fileFormat,
                    statMssage, gridDataList);
            setStatusMessage(statMssage);
            StatusMessageList.getInstance().push(statMssage);
            if (bttmStatusBar != null) {
                bttmStatusBar.activateStatusbar();
            }
            worker.schedule();
        }
    }

    private List<String[]> readDataFromView() {
        List<String[]> listOfRows = new ArrayList<String[]>();
        this.rows.initializeLayer();
        Iterator<String[]> iter = this.rows.iterator();
        while (iter.hasNext()) {
            listOfRows.add(iter.next());
        }
        this.rows.finalizeLayer();
        return listOfRows;
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

    private int confirmOverridingDialog(String defaultFileName) {
        int res = 1;
        ExportZipOptionDialog dialog = null;
        if (isText) {
            dialog = new ExportZipOptionDialog(Display.getDefault().getActiveShell(), defaultFileName, false, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_EXEC_TITLE, windowName));
        } else {
            dialog = new ExportZipOptionDialog(Display.getDefault().getActiveShell(), defaultFileName, false, false,
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_RESULT_TITLE) + windowName);
        }
        int returnValue = dialog.open();
        if (returnValue != 0) {
            res = 0;
            return res;
        }

        ExportOption exportOption = dialog.getExportOption();

        fileFormat = exportOption.getFormat();
        userGivenFileName = exportOption.getFilePathWithSuffixFormat();

        isZip = exportOption.isZip();
        if (isZip) {
            zipFileName = userGivenFileName;
            String tempPath = Normalizer.normalize(System.getenv(MPPDBIDEConstants.TEMP_ENVIRONMENT_VARIABLE),
                    Normalizer.Form.NFD);
            if (!DSFilesWrapper.isExistingDirectory(tempPath)) {
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                                MPPDBIDEConstants.LINE_SEPARATOR,
                                MessageConfigLoader.getProperty(IMessagesConstants.INVALID_TEMP_ENVIRONMENT_VARIABLE)));
                MPPDBIDELoggerUtility.error("TEMP environment varibale is not an existing directory.");
                return 0;
            }
            userGivenFileName = ExportZipData.getTempPathStr(userGivenFileName, exportOption.getFileFormatSuffix(),
                    tempPath);

            File tempFile = new File(userGivenFileName);
            if (tempFile.exists()) {
                deleteExistingFile(tempFile.toPath());
            }
        }

        return res;
    }

    private void deleteExistingFile(Path newPath) {
        try {
            // Delete the file if file exists and chooses to overwrite.
            Files.delete(newPath);
        } catch (IOException exception) {
            String msg = exception.getMessage();
            if (consoleMessageWindow != null) {
                consoleMessageWindow
                        .logInfoInUI(MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + msg);
            }
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE_TO_CSV_HANDLER,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY),
                    exception);
        }
    }

    /**
     * End of export.
     */
    public void endOfExport() {
        setChanged();
        notifyObservers(true);
    }

    private void performSuccessAction(IExecTimer timer) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String elapsedTime = null;
        try {
            elapsedTime = timer.getElapsedTime();
        } catch (DatabaseOperationException e) {
            // Cautious ignore. No impact code.
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }
        if (!isText) {
            msg.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_EXPORT_SUCC_FILE, windowName))
                    .append(MPPDBIDEConstants.LINE_SEPARATOR).append(MessageConfigLoader
                            .getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG_RESULT, elapsedTime));
        } else {
            msg.append(MessageConfigLoader.getProperty(IMessagesConstants.MSG_EXPORT_EXEC_SUCC_FILE, windowName))
                    .append(MPPDBIDEConstants.LINE_SEPARATOR).append(MessageConfigLoader
                            .getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG_RESULT, elapsedTime));
        }
        MPPDBIDEDialogs.clearExistingDialog();
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_EXPORT_SUCCES_DAILOG_TITLE), msg.toString());
        if (consoleMessageWindow != null) {
            consoleMessageWindow.logInfoInUI(msg.toString());
        }
    }

    private void handleOperationalException(DatabaseOperationException dbOperationExcp) {
        String msg = dbOperationExcp.getServerMessage();
        if (null == msg) {
            msg = dbOperationExcp.getDBErrorMessage();
        }
        if (!(dbOperationExcp.getCause() instanceof FileNotFoundException
                || dbOperationExcp.getCause() instanceof FileAlreadyExistsException)) {
            if (consoleMessageWindow != null) {
                consoleMessageWindow.logInfo(dbOperationExcp.getMessage());
            }
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR), MessageConfigLoader.getProperty(
                            IMessagesConstants.ERR_EXPORT_EXCEL_OPERATION, MPPDBIDEConstants.LINE_SEPARATOR, msg));
        } else if (null != msg && dbOperationExcp.getCause() instanceof FileCompressException) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.COMPRESS_FAIL_DAILOG_TITLE),
                    dbOperationExcp.getCause().getMessage(), dbOperationExcp);
        }
        clearFailedFile();
    }

    private void clearFailedFile() {
        try {
            if (this.userGivenFileName != null) {
                Files.deleteIfExists(Paths.get(this.userGivenFileName));
            }
            if (this.zipFileName != null) {
                Files.deleteIfExists(Paths.get(this.zipFileName));
            }
        } catch (IOException e1) {
            MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
        }
    }

    private void handleCriticalException(DatabaseCriticalException e) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(e.getServerMessage());

        msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(e.getDBErrorMessage());
        if (consoleMessageWindow != null) {
            consoleMessageWindow.logInfoInUI(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + e.getMessage());
        }
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + msg.toString());
        clearFailedFile();
    }

    private void handleException(Exception exception) {
        if (consoleMessageWindow != null) {
            consoleMessageWindow.logInfoInUI(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exception.getMessage());
        }
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_EXPORTING) + exception.getMessage());
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_EXPORT_QUERY), exception);
        clearFailedFile();
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
     * Title: class Description: The Class ExportTextWorker.
     */
    private final class ExportTextWorker extends ImportExportPreUIWorker {
        private String userGivenFileName;

        private FileOutputStream fileOutStream;

        private OutputStreamWriter filewriter;

        private IExecTimer timer;

        private StatusMessage statMssge;

        private ExportZipData exportZipData;

        private ExportTextWorker(String windowName, GridViewPortData data, String name, StatusMessage statMssage) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CURRENT_RESULTDATA_PROGRESS_NAME,
                    windowName), MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.IMPORT_EXPORT_LOGIN_FAILED);
            this.userGivenFileName = name;
            this.statMssge = statMssage;
            this.exportZipData = new ExportZipData();
            timer = new ExecTimer("Export Current Data To text ");
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            timer.start();
            createFile();
            try {
                List<IDSGridDataRow> allRows = textView.getAllFetchedRows();
                for (IDSGridDataRow oneRow : allRows) {
                    Object[] values = oneRow.getValues();
                    if (values != null) {
                        for (Object value : values) {
                            if (value instanceof String) {
                                filewriter.write((String) value);
                            }
                        }
                    }
                    filewriter.write(MPPDBIDEConstants.LINE_SEPARATOR);
                    filewriter.flush();
                }
            } catch (IOException exe) {
                closeResources(filewriter, fileOutStream);
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT), exe);
                String msg = (exe.getMessage().contains(MPPDBIDEConstants.DISK_FULL_ERR_MSG))
                        ? IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE
                        : IMessagesConstants.IO_EXCEPTION_WHILE_EXPORT;
                throw new DatabaseOperationException(msg, exe);
            }
            timer.stop();
            doCompress();
            return null;
        }

        private void doCompress() throws FileCompressException {
            if (isZip) {
                closeResources(filewriter, fileOutStream);
                exportZipData.doCompress(userGivenFileName, zipFileName);
            }
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            performSuccessAction(timer);
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            handleCriticalException(e);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            handleOperationalException(e);
        }

        @Override
        public void onException(Exception e) {
            handleException(e);
        }

        @Override
        public void finalCleanup() {
            statusMessage = null;
            closeResources(filewriter, fileOutStream);
            super.finalCleanup();
        }

        @Override
        public void finalCleanupUI() {
            endOfExport();
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.statMssge);
            }
        }

        @Override
        protected void canceling() {
            super.canceling();
        }

        private void closeResources(OutputStreamWriter fileStrmwriter, FileOutputStream fileOutStrm) {
            try {
                if (fileStrmwriter != null) {
                    fileStrmwriter.close();
                }
            } catch (IOException ex) {
                fileStrmwriter = null;
            }

            try {
                if (fileOutStrm != null) {
                    fileOutStrm.close();
                }
            } catch (IOException ex) {
                fileOutStrm = null;
            }
        }

        private void createFile() throws DatabaseOperationException, IOException {
            ISetFilePermission withFilePermission = FilePermissionFactory.getFilePermissionInstance();
            Path path = withFilePermission.createFileWithPermission(this.userGivenFileName, false, null, false);

            fileOutStream = new FileOutputStream(path.toString(), true);
            if (UserPreference.getInstance().getFileEncoding().isEmpty()) {
                filewriter = new OutputStreamWriter(fileOutStream, Charset.defaultCharset().name());
            } else {
                filewriter = new OutputStreamWriter(fileOutStream, MPPDBIDEConstants.GS_DUMP_ENCODING);
            }
            ExportManager.prependBomForUtf8(UserPreference.getInstance().getFileEncoding(), fileOutStream);
        }

        @Override
        protected Database getDatabase() {
            return null;
        }

        @Override
        public boolean callPresetUp() {
            return true;
        }
    }

    /**
     * Title: class Description: The Class ExportExcelWorker.
     */
    private final class ExportExcelWorker extends ImportExportPreUIWorker {
        private GridViewPortData rows;

        private String userGivenFileName;

        private IExecTimer timer;

        private String fileFormat;

        private StatusMessage statusMsg;

        private List<String[]> gridData;

        private ExportExcelApachePOI exportExcel;

        private ExportZipData exportZipData;

        public ExportExcelWorker(String windowName, GridViewPortData data, String userGivenFileName, String fileFormat,
                StatusMessage statusMsg, List<String[]> gridData) {
            super(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CURRENT_RESULTDATA_PROGRESS_NAME,
                    windowName), MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.IMPORT_EXPORT_LOGIN_FAILED);
            this.rows = data;
            this.userGivenFileName = userGivenFileName;
            this.fileFormat = fileFormat;
            this.statusMsg = statusMsg;
            this.gridData = gridData;
            this.exportZipData = new ExportZipData();
            timer = new ExecTimer("Export Current Data To Excel");
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            timer.start();
            writeToExcel();
            timer.stop();
            doCompress();
            return null;
        }

        private void doCompress() throws FileCompressException {
            if (isZip) {
                exportZipData.doCompress(userGivenFileName, zipFileName);
            }
        }

        private void writeToExcel() throws MPPDBIDEException {
            if (MPPDBIDEConstants.STR_EXCEL_XLSX.equalsIgnoreCase(fileFormat)) {
                ThreadLocal<TempFileCreationStrategy> threadLocal = new ThreadLocal<TempFileCreationStrategy>() {
                    @Override
                    protected TempFileCreationStrategy initialValue() {
                        // to do create thread folder
                        return createTempFileCreationStrategy();
                    }
                };
                TempFile.setTempFileCreationStrategy(new DefaultTempFileCreationStrategy());
                TempFileCreationStrategy tempFileCreationStrategy = threadLocal.get();
                try {
                    if (tempFileCreationStrategy != null) {
                        tempFileCreationStrategy.createTempFile("", "");
                    }
                } catch (IOException e1) {
                    MPPDBIDELoggerUtility.error(
                            MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET), e1);
                    throw new DatabaseOperationException(IMessagesConstants.ERROR_EXPORT_EXCEL_RESULTSET, e1);
                }
            }
            exportExcel = new ExportExcelApachePOI(fileFormat);
            if (exportExcel.checkRowLength(this.rows.getRowCount())
                    && exportExcel.checkColLength(this.rows.getColCount())) {
                writeToExcelIfValid();
            } else {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT));
                throw new DatabaseOperationException(IMessagesConstants.ERROR_EXCEL_ROW_COLUMN_LIMIT);
            }
        }

        private void writeToExcelIfValid() throws DatabaseOperationException, MPPDBIDEException {
            exportExcel.createSheet(windowName);
            exportExcel.createHeaderRow(headerList);
            int rowNo = 1;
            try {
                List<String> singleRow = new ArrayList<String>();
                for (String[] string : gridData) {
                    if (!cancelled) {
                        singleRow.addAll(Arrays.asList(string));
                        try {
                            exportExcel.setCellValue(singleRow, rowNo);
                        } catch (ParseException e) {
                            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                                    getWindowImage(),
                                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_INFORMATION),
                                    MessageConfigLoader.getProperty(IMessagesConstants.ERROR_EXPORT_EXCEL_PARSER));
                        }
                        rowNo++;
                        singleRow.clear();
                    } else {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                        throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
                    }
                }

            } catch (Exception e) {
                handleExceptionWhileWrite(e);
            }
            exportExcel.writeToWorkbook(userGivenFileName, UserPreference.getInstance().getFileEncoding());
        }

        private void handleExceptionWhileWrite(Exception exe) throws DatabaseOperationException {
            if (exe.getMessage().contains(MPPDBIDEConstants.DISK_FULL_ERR_MSG)) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE), exe);
                throw new DatabaseOperationException(IMessagesConstants.EXPORT_ALL_DATA_NOT_ENOUGH_SPACE);
            }
            if (exe.getMessage().contains("Operation canceled on user request")) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG), exe);
                throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EXPORT_TABLE), exe);
                throw new DatabaseOperationException(IMessagesConstants.ERR_EXPORT_TABLE);
            }
        }

        private TempFileCreationStrategy createTempFileCreationStrategy() {
            return new TempFileCreationStrategy() {
                @Override
                public File createTempFile(String arg0, String arg1) throws IOException {
                    String stdizedPath = null;
                    try {
                        stdizedPath = new File(userGivenFileName).getCanonicalPath();
                    } catch (IOException exception) {
                        MPPDBIDELoggerUtility.error("Invalid File Path", exception);
                    }
                    Path path = Paths.get(stdizedPath);
                    Path parent = path.getParent();
                    if (null != parent) {
                        System.setProperty("java.io.tmpdir", parent.normalize().toString());
                        File dir = null;
                        if (FileValidationUtils.validateFilePathName(MPPDBIDEConstants.TEMP_FILE_PATH)) {
                            dir = new File(MPPDBIDEConstants.TEMP_FILE_PATH, parent.normalize().toString());
                        }
                        return dir;
                    } else {
                        return path.toFile();
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
            performSuccessAction(timer);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            handleOperationalException(e);
        }

        @Override
        public void onException(Exception e) {
            handleException(e);
        }

        @Override
        public void finalCleanup() {
            try {
                if (null != exportExcel) {
                    exportExcel.cleanUpWorkbookPOIFiles(userGivenFileName);
                }
            } catch (DatabaseOperationException e1) {
                MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
            }
            statusMessage = null;
            super.finalCleanup();
        }

        @Override
        protected void canceling() {
            super.canceling();
            setCancelFlag(true);
        }

        @Override
        public void finalCleanupUI() {
            endOfExport();
            final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bttmStatusBar != null) {
                bttmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            handleCriticalException(e);

        }

        @Override
        protected Database getDatabase() {
            return null;
        }

        @Override
        public boolean callPresetUp() {
            return true;
        }
    }

}
