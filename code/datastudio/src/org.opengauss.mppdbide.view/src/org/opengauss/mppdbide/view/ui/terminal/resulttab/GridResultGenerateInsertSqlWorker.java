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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.graphics.Image;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.presentation.exportdata.ExportZipData;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.ConvertValueToInsertSqlFormat;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.JSQLParserUtils;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileCompressException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.component.grid.GridSelectionLayerPortData;
import org.opengauss.mppdbide.view.core.ConsoleMessageWindow;
import org.opengauss.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.UserPreference;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridResultGenerateInsertSqlWorker.
 *
 * @since 3.0.0
 */
public class GridResultGenerateInsertSqlWorker extends ImportExportPreUIWorker {
    private GridSelectionLayerPortData selectData;
    private ConsoleMessageWindow consoleMessageWindow;
    private IQueryExecutionSummary resultSummary;
    private StatusMessage statusMessage;
    private String windowName;
    private GridResultGenerateSelectedLineInsertSql generateSelectedLineInsertSql;
    private IDSGridDataProvider dataProvider;
    private IExecTimer timer;
    private Path path = null;
    private FileOutputStream fileOutStream = null;
    private OutputStreamWriter filewriter = null;
    private boolean haveSuffix = false;
    private boolean generateCurrentSql;
    private int type;
    private String generateInsertSql;
    private String resultName = null;
    private Matcher matcher = null;
    private Pattern pattern = null;
    private String typeName = null;
    private List<String> columns;
    private boolean cancelled;
    private List<Integer> selectRow;
    private StringBuffer outPutInsertSql;
    private int rowCount;
    private ConvertValueToInsertSqlFormat convertValueToInsertSqlFormat;
    private boolean isZip;
    private Path zipPath;

    /**
     * Instantiates a new grid result generate insert sql worker.
     *
     * @param termConnection the term connection
     * @param resultSummary the result summary
     * @param dataProvider the data provider
     * @param selectData the select data
     * @param windowName the window name
     * @param statMssage the stat mssage
     * @param consoleMessageWindow the console message window
     * @param path the path
     * @param sqlPath the sql path
     * @param generateCurrentSql the generate current sql
     * @param isZip the is zip
     * @param tempPath path
     */
    public GridResultGenerateInsertSqlWorker(TerminalExecutionConnectionInfra termConnection,
            IQueryExecutionSummary resultSummary, IDSGridDataProvider dataProvider,
            GridSelectionLayerPortData selectData, String windowName, StatusMessage statMssage,
            ConsoleMessageWindow consoleMessageWindow, Path path, String sqlPath, Boolean generateCurrentSql,
            boolean isZip, String tempPath) {
        super(MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_SQL, windowName),
                MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.IMPORT_EXPORT_LOGIN_FAILED);
        this.resultSummary = resultSummary;
        this.consoleMessageWindow = consoleMessageWindow;
        this.windowName = windowName;
        this.isZip = isZip;
        if (isZip) {
            this.zipPath = path;
            this.path = Paths.get(ExportZipData.getTempPathStr(path.toString(), ".sql", tempPath));
        } else {
            this.path = path;
        }
        this.statusMessage = statMssage;
        generateSelectedLineInsertSql = new GridResultGenerateSelectedLineInsertSql(termConnection, selectData,
                consoleMessageWindow, dataProvider, resultSummary, windowName, generateCurrentSql);
        this.dataProvider = dataProvider;
        this.selectData = selectData;
        this.generateCurrentSql = generateCurrentSql;
        convertValueToInsertSqlFormat = new ConvertValueToInsertSqlFormat();
        pattern = Pattern.compile("^(.*[A-Z]{1,}.*)|(.*\\s+.*)$");
        this.columns = new ArrayList<>();
        this.selectRow = new ArrayList<>();
        outPutInsertSql = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        timer = new ExecTimer("Generate Insert Sql...");

    }

    /**
     * Sets the cancel flag.
     *
     * @param iscancel the new cancel flag
     */
    public void setCancelFlag(boolean iscancel) {
        this.cancelled = iscancel;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public Object doJob() throws MPPDBIDEException {
        timer.start();
        generateInsertSql = null;
        if (generateCurrentSql) {
            generateInsertSql = generateCurrentDataInsertSql();
        } else {
            generateInsertSql = generateSelectLineInsertSql();
        }

        writeToSqlFile(path.toFile(), generateInsertSql);
        doCompress();

        timer.stop();
        return null;
    }

    private void doCompress() throws FileCompressException {
        if (this.isZip) {
            new ExportZipData().doCompress(this.path.toString(), this.zipPath.toString());
        }
    }

    private String generateCurrentDataInsertSql() throws DatabaseOperationException {
        columns = getColumnNames();
        rowCount = selectData.getRowCount();
        getTableName();
        for (int i = 0; i < rowCount; i++) {
            if (!cancelled) {
                appendInsertSqlAction();
                for (int j = 0; j < columns.size(); j++) {
                    appendInsertValueAction(j);

                    outPutInsertSql.append(convertValueToInsertSqlFormat.convertValueToSQL(type,
                            selectData.getSelectRow(i, j), typeName));
                    haveSuffix = true;
                }
                outPutInsertSql.append(");").append(MPPDBIDEConstants.LINE_SEPARATOR);
                haveSuffix = false;
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
            }
        }
        return outPutInsertSql.toString();
    }

    private void appendInsertValueAction(int index) {
        if (haveSuffix) {
            outPutInsertSql.append(",");
        }
        type = dataProvider.getColumnDataProvider().getColumnDatatype(index);
        typeName = dataProvider.getColumnDataProvider().getColumnDataTypeName(index);

    }

    private void appendInsertSqlAction() {
        outPutInsertSql.append("INSERT INTO ").append(resultName).append(" (");
        for (String columnName : columns) {
            if (haveSuffix) {
                outPutInsertSql.append(",");
            }
            matcher = pattern.matcher(columnName);
            if (matcher.matches()) {
                outPutInsertSql.append("\"").append(columnName).append("\"");
            } else {
                outPutInsertSql.append(columnName);
            }

            haveSuffix = true;
        }
        haveSuffix = false;
        outPutInsertSql.append(")").append(MPPDBIDEConstants.LINE_SEPARATOR).append(" VALUES (");

    }

    private void getTableName() {
        String querySql = resultSummary.getQuery();
        resultName = JSQLParserUtils.getSelectQueryMainTableName(querySql);
    }

    private String generateSelectLineInsertSql() throws DatabaseOperationException {
        getTableName();
        columns = getColumnNames();
        selectRow = selectData.getRowCoordinate();
        for (int i = 0; i < selectRow.size(); i++) {
            if (!cancelled) {
                appendInsertSqlAction();
                for (int j = 0; j < columns.size(); j++) {
                    appendInsertValueAction(j);
                    outPutInsertSql.append(convertValueToInsertSqlFormat.convertValueToSQL(type,
                            selectData.getSelectRow(selectRow.get(i), j), typeName));
                    haveSuffix = true;
                }
                outPutInsertSql.append(");").append(MPPDBIDEConstants.LINE_SEPARATOR);
                haveSuffix = false;
            } else {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
                throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
            }
        }
        return outPutInsertSql.toString();
    }

    private void closeStream() {
        try {
            if (filewriter != null) {
                filewriter.close();
            }
            if (fileOutStream != null) {
                fileOutStream.close();
            }

        } catch (IOException e) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_IOEXCEPTION), e.getMessage());
            try {
                if (fileOutStream != null) {
                    fileOutStream.close();
                }
            } catch (IOException e1) {
                MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
            }
        }

    }

    private void writeToSqlFile(File file, String content) throws MPPDBIDEException {

        try {
            String encoding = getFileEncoding(file);
            fileOutStream = new FileOutputStream(file);
            filewriter = new OutputStreamWriter(fileOutStream, encoding);
            filewriter.write(content);

        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);
            throw new DatabaseOperationException(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_GENERATE_INSERT_DIALOG_SAVE_FILE_ERROR),
                    exception);

        } finally {
            closeStream();
        }
    }

    private String getFileEncoding(File file) {
        String encoding = UserPreference.getInstance().getFileEncoding().isEmpty() ? Charset.defaultCharset().name()
                : UserPreference.getInstance().getFileEncoding();

        return encoding;
    }

    private List<String> getColumnNames() {
        if (this.dataProvider.getColumnDataProvider() != null) {
            String[] columnNames = this.dataProvider.getColumnDataProvider().getColumnNames();
            columns.addAll(Arrays.asList(columnNames));
        }
        return columns;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        performSuccessAction(timer);
    }

    private void performSuccessAction(IExecTimer suctimer) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String elapsedTime = null;

        try {
            elapsedTime = suctimer.getElapsedTime();
        } catch (DatabaseOperationException e) {
            // Cautious ignore. No impact code.
            MPPDBIDELoggerUtility.none("Nothing to do here");
        }
        msg.append(MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_SQL, windowName))
                .append(MPPDBIDEConstants.LINE_SEPARATOR).append(MessageConfigLoader
                        .getProperty(IMessagesConstants.EXE_TERMINAL_EXC_TIME_MSG_RESULT, elapsedTime));

        MPPDBIDEDialogs.clearExistingDialog();
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_SQL_SUCCES_DAILOG_TITLE), msg.toString());
        printToConsole(MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_SQL, windowName));

    }

    private void printToConsole(String message) {
        if (null != consoleMessageWindow) {
            consoleMessageWindow.logInfoInUI(message);
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
        handleCriticalException(e);
    }

    private void handleCriticalException(DatabaseCriticalException e) {
        StringBuilder msg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        msg.append(e.getServerMessage());

        msg.append(MPPDBIDEConstants.LINE_SEPARATOR).append(e.getDBErrorMessage());
        printToConsole(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQL) + e.getMessage());
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQL) + msg.toString());
        clearFailedFile();

    }

    private void clearFailedFile() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e1) {
            MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
        }

    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException e) {
        handleOperationalException(e);
    }

    private void handleOperationalException(DatabaseOperationException e) {
        String msg = e.getServerMessage();
        if (null == msg) {
            msg = e.getDBErrorMessage();
        }
        if (!(e.getCause() instanceof FileNotFoundException || e.getCause() instanceof FileAlreadyExistsException)) {
            if (null != consoleMessageWindow) {
                consoleMessageWindow.logInfo(e.getMessage());
            }
            MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                    MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQLL_OPERATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, msg));
        }
        clearFailedFile();

    }

    /**
     * On exception.
     *
     * @param e the e
     */
    @Override
    public void onException(Exception exception) {
        handleException(exception);
    }

    private void handleException(Exception exception) {
        printToConsole(
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQL) + exception.getMessage());
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.GENERATE_INSERT_ERROR),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_GENERATE_INSERT_SQL) + exception.getMessage());
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_GENERATE_INSERT_ERROR),
                exception);
        clearFailedFile();

    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() {
        outPutInsertSql = null;
        closeStream();
        super.finalCleanup();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        generateSelectedLineInsertSql.endOfGenerate();
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bttmStatusBar != null) {
            bttmStatusBar.hideStatusbar(this.statusMessage);
        }

    }

    /**
     * On out of memory error.
     *
     * @param e the e
     */
    @Override
    public void onOutOfMemoryError(OutOfMemoryError exception) {
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true, getWindowImage(),
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_UI_GENERATE_INSERT_ERROR),
                exception);
        clearFailedFile();

    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        setCancelFlag(true);
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        return IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass());
    }

    @Override
    protected Database getDatabase() {
        return null;
    }

    /**
     * callPresetUp
     */
    public boolean callPresetUp() {
        return true;
    }

}
