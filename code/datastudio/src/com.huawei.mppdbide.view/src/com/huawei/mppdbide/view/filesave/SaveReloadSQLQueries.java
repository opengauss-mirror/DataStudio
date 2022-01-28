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

package com.huawei.mppdbide.view.filesave;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.Document;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.huawei.mppdbide.bl.errorlocator.IErrorLocator;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.files.ISetFilePermission;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.ImportExportPreUIWorker;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.ui.connectiondialog.FileSaveNoAccessErrorDialog;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.Preferencekeys;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SaveReloadSQLQueries.
 *
 * @since 3.0.0
 */
public final class SaveReloadSQLQueries {	
    private StatusMessage statusMessage;

    private String originalDocContent;
    private String prependDocContent;
    private BottomStatusBar bottomStatusBar;
    private PLSourceEditorCore plSourceEditor;
    
    /**
     * Instantiates a new save reload SQL queries.
     */
    public SaveReloadSQLQueries() {
        bottomStatusBar = null;
    }

    /**
     * Gets the file name from user.
     *
     * @return the file name from user
     */
    private String getFileNameFromUser() {
        FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
        // Require Translation?
        dialog.setFilterNames(new String[] {"sql", "All Files (*.*)"});
        dialog.setFilterExtensions(new String[] {"*.sql"});

        String sql = dialog.open();
        if (sql == null || sql.isEmpty()) {
            return null;
        }

        Path file = Paths.get(sql).getFileName();
        if (file == null) {
            return null;
        }
        String fileName = file.toString();
        String fileFormat = FilenameUtils.getExtension(fileName);

        if (!"sql".equalsIgnoreCase(fileFormat)) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OPEN_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_SELECT_SQL_FILE));
            return null;
        }

        return sql;
    }

    /**
     * Gets the file.
     *
     * @param path the path
     * @return the file
     */
    private File getFile(String path) {
        final File file = new File(path);

        if (!file.exists()) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OPEN_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_SELECT_FILE_DOES_NOT_EXIST));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message
                    .getError(MessageConfigLoader.getProperty(IMessagesConstants.MSG_SELECT_FILE_DOES_NOT_EXIST)));
            return null;
        }

        boolean readable = file.canRead();

        if (!readable) {
            ObjectBrowserStatusBarProvider.getStatusBar()
                    .displayMessage(Message.getErrorFromConst(IMessagesConstants.COULD_NOT_SET_READABLE));
            return null;
        }

        return file;
    }

    /**
     * Open SQL file.
     *
     * @param sqlTerminal the sql terminal
     */
    public void openSQLFile(final SQLTerminal sqlTerminal) {
        this.plSourceEditor = sqlTerminal.getTerminalCore();

        /*
         * 1. Get file name - UI 2. Use Job to read the file - Job 3. Take a
         * copy of the document. - Job 4. Set text document in viewer - UI 5. On
         * Out of memory handle - UI 6.
         */

        String sql = getFileNameFromUser();
        if (null == sql) {
            return;
        }

        File file = getFile(sql);
        if (null == file) {
            return;
        }

        double fileSizeInMB = FileUtils.sizeOf(file) / (double) (1024 * 1024);
        double fileLimit = PreferenceWrapper.getInstance().getPreferenceStore()
                .getInt(Preferencekeys.FILE_LIMIT_FOR_SQL);
        if (fileLimit != 0 && fileSizeInMB > fileLimit) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_HEADER),
                    MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT_WARNING_MESSAGE));
            return;

        }
        this.originalDocContent = this.plSourceEditor.getSourceViewer().getDocument().get();
        if (!this.originalDocContent.isEmpty()) {
            int choice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OPEN_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_NEED_APPEND_OVERWRITE),
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_LABEL_APPEND),
                    MessageConfigLoader.getProperty(IMessagesConstants.BTN_LABEL_OVERWRITE));

            if (choice == 1) {
                this.prependDocContent = "";

                sqlTerminal.setOpenSqlFlag(true);
            } else if (choice == 0) {
                this.prependDocContent = MPPDBIDEConstants.LINE_SEPARATOR;
            } else {
                return;
            }
        } else {
            this.originalDocContent = "";
            this.prependDocContent = "";

            sqlTerminal.setOpenSqlFlag(true);
        }

        scheduleLoadSqlJob(sqlTerminal, sql, fileSizeInMB);

    }

    private void scheduleLoadSqlJob(final SQLTerminal sqlTerminal, String sql, double fileSizeInMB) {
        initFileTerminalInfo(sql, sqlTerminal);

        bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        StatusMessage statMessage = new StatusMessage("Loading SQL...");
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar(); 
        }
        
        LoadSqlJob job = new LoadSqlJob(sql, this, fileSizeInMB);
        job.setUser(true);
        job.schedule();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class LoadSqlJob.
     */
    private static final class LoadSqlJob extends ImportExportPreUIWorker {

        private String path;
        private SaveReloadSQLQueries parent;
        private SQLTerminal sqlTerminal;
        private double fileSizeInMB;

        /**
         * Instantiates a new load sql job.
         *
         * @param path the path
         * @param parent the parent
         */
        private LoadSqlJob(String path, SaveReloadSQLQueries parent, double fileSizeInMB) {
            super("Load SQL file", null, "");
            this.path = path;
            this.parent = parent;
            this.fileSizeInMB = fileSizeInMB;
        }

        @Override
        public boolean callPresetUp() {
            return true;
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, IOException {
            Path pathObj = FileSystems.getDefault().getPath(this.path);

            String encoding = UserPreference.getInstance().getFileEncoding();
            BufferedReader bufferReader = null;
            InputStream inputStream = null;
            BOMInputStream bomInputStream = null;
            InputStreamReader isr = null;
            try {
                String content = "";
                IExecTimer totalExect = new ExecTimer("LoadSqlJob-Read File From Disk");
                totalExect.start();

                if (pathObj != null) {
                    if (encoding.isEmpty()) {
                        encoding = Charset.defaultCharset().name();
                    }
                    String fileContent = null;
                    inputStream = new FileInputStream(pathObj.toFile());
                    bomInputStream = new BOMInputStream(inputStream);
                    isr = new InputStreamReader(bomInputStream, encoding);
                    bufferReader = new BufferedReader(isr);
                    fileContent = bufferReader.readLine();
                    StringBuffer buffer = readSqlFileContent(bufferReader, fileContent);            
                    content = buffer.toString();
                }
                totalExect.stopAndLogNoException();
                return content;
            } catch (IOException exe) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_SQL_FILE_READ), exe);
                throw new DatabaseOperationException(IMessagesConstants.ERR_SQL_FILE_READ);
            } catch (OutOfMemoryError exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED);
            } finally {
                IHandlerUtilities.closeBufferReader(bufferReader);
                IHandlerUtilities.closeInputStream(inputStream);
                IHandlerUtilities.closeInputStream(bomInputStream);
                IHandlerUtilities.closeInputStreamReader(isr);
            }
        }

        private StringBuffer readSqlFileContent(BufferedReader bufferReader, String fileContent) throws IOException {
            String sqlFileContent = fileContent;
            StringBuffer buffer = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            while (sqlFileContent != null) {
                buffer.append(sqlFileContent);
                buffer.append(MPPDBIDEConstants.LINE_SEPARATOR);
                sqlFileContent = bufferReader.readLine();
            }
            return buffer;
        }

        private long getSemiColonCount(String fileContent) {
            return fileContent.chars().filter(ch -> ch == ';').count();
        }
        
        @Override
        public void onSuccessUIAction(Object obj) {
            try {
                IExecTimer totalExect = new ExecTimer("LoadSqlJob-Load File Content To Terminal");
                totalExect.start();

                String docContent = new StringBuilder(parent.prependDocContent).append((String) obj).toString();

                if (parent.prependDocContent.length() == 0) {
                    parent.plSourceEditor
                                .setDocument(new Document(docContent), fileSizeInMB);
                } else {
                    parent.plSourceEditor.loadSQLData(docContent,
                            parent.originalDocContent.length() + parent.prependDocContent.length(),
                            parent.originalDocContent.length(), fileSizeInMB);
                }

                sqlTerminal = UIElement.getInstance().getVisibleTerminal();
                boolean isConnected = false;
                boolean isSrcViewrEmpty = parent.plSourceEditor.getSourceViewer().getDocument().get().trim().isEmpty();
                if (null != sqlTerminal) {
                    isConnected = sqlTerminal.getSelectedDatabase() == null ? false
                            : sqlTerminal.getSelectedDatabase().isConnected();
                    sqlTerminal.getExecuteButton().setEnabled(isConnected && !isSrcViewrEmpty);
                    sqlTerminal.getNewTabExecuteButton().setEnabled(isConnected && !isSrcViewrEmpty);
                    sqlTerminal.setModified(true);
                    sqlTerminal.setModifiedAfterCreate(true);
                    if (parent.prependDocContent.length() == 0) {
                        sqlTerminal.registerModifyListener();
                    }

                    UIElement.getInstance().bringPartOnTop(sqlTerminal.getUiID());
                }
                String appendOverwrite = MessageConfigLoader.getProperty(IMessagesConstants.MSG_LOADED);

                totalExect.stopAndLogNoException();

                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                        .getProperty(IMessagesConstants.MSG_APPEND_OVERWRITE_SUCCESS, appendOverwrite)));
                MPPDBIDELoggerUtility.info(MessageConfigLoader
                        .getProperty(IMessagesConstants.MSG_APPEND_OVERWRITE_SUCCESS, appendOverwrite));
            } catch (OutOfMemoryError exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED), exception);
                parent.plSourceEditor.clearDocContent();
                handleOutOfMemoryError();
            }

        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            // Not expected to come here. No DB operation Involved.

        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            // Unable to load data. So revert back to old content. No special
            // flow for any of the errors.
            handleOutOfMemoryError();
        }

        @Override
        public void finalCleanup() {
            parent.originalDocContent = null;
            parent.prependDocContent = null;
            super.finalCleanup();
        }

        @Override
        public void finalCleanupUI() {
            parent.bottomStatusBar.hideStatusbar(parent.getStatusMessage());
        }

        /**
         * Handle out of memory error.
         */
        public void handleOutOfMemoryError() {
            boolean isOOMReOccurred = false;
            try {
                parent.plSourceEditor.setDocument(new Document(parent.originalDocContent), 0);
                if (null != sqlTerminal) {
                    sqlTerminal.registerModifyListener();
                }
            } catch (OutOfMemoryError e) {
                // Reset to empty.
                parent.plSourceEditor.clearDocContent();
                isOOMReOccurred = true;
            }

            StringBuilder errMsg = new StringBuilder(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));

            if (isOOMReOccurred) {
                errMsg.append(' ')
                        .append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_REOCCURED));
            }

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY), errMsg.toString());
        }

        /**
         * getDatabase get database
         */
        protected Database getDatabase() {
            return sqlTerminal.getDatabase();
        }
    }

    /**
     * Save to new file.
     *
     * @param sqlTerminal the sql terminal
     * @return true, if successful
     */
    public boolean saveToNewFile(SQLTerminal sqlTerminal) {
        String query = sqlTerminal.getQuery();

        String sql = openDialogToSelectFileLocation(sqlTerminal);

        if (sql == null || sql.isEmpty()) {
            handleCancelOperation(sqlTerminal);
            return true;
        }

        if (!sql.endsWith(".sql")) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_SAVE_SQL),
                    MessageConfigLoader.getProperty(IMessagesConstants.MSG_SELECT_SQL_FILE));
            return false;
        }
        Path newPath = Paths.get(sql);

        ISetFilePermission setFilePermission = FilePermissionFactory.getFilePermissionInstance();
        Path path = null;
        try {
            // If file already exists , confirm for overwriting the file.
            Files.deleteIfExists(newPath);
            // create the file with security permissions
            path = setFilePermission.createFileWithPermission(sql, false, null, false);
        } catch (DatabaseOperationException | IOException e) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.ERR_SAVE_SQL_HANDLER, sql)));

            return false;
        }

        bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();

        StatusMessage statMessage = new StatusMessage("Saving SQL...");
        setStatusMessage(statMessage);
        StatusMessageList.getInstance().push(statMessage);
        bottomStatusBar.activateStatusbar();

        boolean isExceptionThrown = writeToFileAfterValidation(sqlTerminal, query, sql, path);

        postWriteToFileOperation(sqlTerminal, isExceptionThrown);

        return !isExceptionThrown;
    }

    /**
     * Post write to file operation.
     *
     * @param sqlTerminal the sql terminal
     * @param isExceptionThrown the is exception thrown
     */
    private void postWriteToFileOperation(SQLTerminal sqlTerminal, boolean isExceptionThrown) {
        if (isExceptionThrown) {
            sqlTerminal.getDirtyHandler().setDirty(true);
        } else {
            sqlTerminal.setOpenSqlFlag(true);
            sqlTerminal.getDirtyHandler().setDirty(false);
            sqlTerminal.getMenuSaveSQL().setEnabled(false);
        }

        sqlTerminal.setLastSaveTime(new Date());
    }

    /**
     * Handle cancel operation.
     *
     * @param sqlTerminal the sql terminal
     */
    private void handleCancelOperation(SQLTerminal sqlTerminal) {
        // handle cancel action
        if (sqlTerminal.isFileTerminalFlag() && !new File(sqlTerminal.getFilePath()).exists()) {
            sqlTerminal.transferFileTerminalToSQLTerminal();
            sqlTerminal.getDirtyHandler().setDirty(false);
        }
    }

    /**
     * Write to file after validation.
     *
     * @param sqlTerminal the sql terminal
     * @param query the query
     * @param sql the sql
     * @param path the path
     * @return true, if successful
     */
    private boolean writeToFileAfterValidation(SQLTerminal sqlTerminal, String query, String sql, Path path) {
        FileOutputStream fileOutStream = null;
        OutputStreamWriter filewriter = null;
        boolean isExceptionThrown = false;
        try {
            // This file operation is done deliberately in UI thread as the
            // operation takes very less time, mostly 1-2 seconds, no need to
            // run in separate thread.

            writeToFile(path.toFile(), query);
            initFileTerminalInfo(sql, sqlTerminal);
        } catch (IOException e) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_FILE_ERROR,
                            sqlTerminal.getFilePath()),
                    e.getMessage(), null);
            isExceptionThrown = true;
        } catch (OutOfMemoryError exc) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    exc);

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
            isExceptionThrown = true;
        } catch (Exception e) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_FILE_ERROR,
                            sqlTerminal.getFilePath()),
                    e.getMessage(), null);
            isExceptionThrown = true;
        } finally {
            bottomStatusBar.hideStatusbar(getStatusMessage());
            closeStreams(sql, path, fileOutStream, filewriter, isExceptionThrown);
        }
        return isExceptionThrown;
    }

    /**
     * Open dialog to select file location.
     *
     * @param sqlTerminal the sql terminal
     * @return the string
     */
    private String openDialogToSelectFileLocation(SQLTerminal sqlTerminal) {
        FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
        dialog.setOverwrite(true);
        dialog.setFilterNames(new String[] {"sql", "All Files (*.*)"});
        dialog.setFilterExtensions(new String[] {"*.sql"});

        // default to original file name
        String location = sqlTerminal.getFilePath();
        Path fileName = location == null ? null : Paths.get(location).getFileName();
        if (fileName != null) {
            dialog.setFileName(fileName.toString());
        } else {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
            dialog.setFileName("DataStudio_" + dateFormat.format(date) + ".sql");
        }

        String sql = dialog.open();
        return sql;
    }

    /**
     * Close streams.
     *
     * @param sql the sql
     * @param path the path
     * @param fileOutStream the file out stream
     * @param filewriter the filewriter
     * @param isExceptionThrownParam the is exception thrown param
     */
    private void closeStreams(String sql, Path path, FileOutputStream fileOutStream, OutputStreamWriter filewriter,
            boolean isExceptionThrownParam) {
        boolean isExceptionThrown = isExceptionThrownParam;
        try {
            if (filewriter != null) {
                filewriter.close();
            }
            if (fileOutStream != null) {
                fileOutStream.close();
            }

        } catch (IOException e) {
            isExceptionThrown = true;
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

        if (isExceptionThrown) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e1) {
                MPPDBIDELoggerUtility.trace("Error while deleting file in exception.");
            }
        } else {
            SQLTerminal sqlTerminal = UIElement.getInstance().getVisibleTerminal();
            if (null != sqlTerminal) {
                sqlTerminal.setModified(false);
                sqlTerminal.setModifiedAfterCreate(false);
            }
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                    .getProperty(IMessagesConstants.MSG_SQL_SAVE_SUCCESS, sql, new Date().toString())));
            MPPDBIDELoggerUtility.info("SQL successfully saved.");
        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return this.statusMessage;
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
     * Save to exist file.
     *
     * @param sqlTerminal the sql terminal
     * @return true, if successful
     */
    public boolean saveToExistFile(SQLTerminal sqlTerminal) {
        if (null == sqlTerminal) {
            return true;
        }

        File file = new File(Optional.ofNullable(sqlTerminal.getFilePath()).orElse(""));

        if (file.exists()) {

            if (promptOverrideConfirmation(sqlTerminal, file)) {
                if (MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.FILE_UPDATE_CONFLICT_DIALOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.FILE_UPDATE_CONFLICT_DIALOG_DETAIL,
                                sqlTerminal.getFilePath()),
                        MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_YES),
                        MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_NO)) != 0) {
                    return true;
                }
            }

            boolean isSuccessFlag = saveToFileIfExists(sqlTerminal, file);

            sqlTerminal.setSqlTerminalPartLabel(sqlTerminal.getPartLabel());

            if (isSuccessFlag) {
                sqlTerminal.setDirtyHandler(false);
                sqlTerminal.setOpenSqlFlag(true);
                sqlTerminal.enableDisableMenuSaveSQL(false);
            }

            UIElement.getInstance().resetTabIcon(sqlTerminal.getSqlTerminalPart(),
                    sqlTerminal.getSelectedDatabase() == null ? false
                            : sqlTerminal.getSelectedDatabase().isConnected());

            sqlTerminal.setLastSaveTime(new Date());
            return isSuccessFlag;
        } else {
            return saveToNewFile(sqlTerminal);
        }
    }

    /**
     * Save to file if exists.
     *
     * @param sqlTerminal the sql terminal
     * @param file the file
     * @return true, if successful
     */
    private boolean saveToFileIfExists(SQLTerminal sqlTerminal, File file) {
        boolean isSuccessFlag = false;
        try {
            writeToFile(file, sqlTerminal.getQuery());
            isSuccessFlag = true;
        } catch (IOException e) {
            String pluginId = IErrorLocator.class.getCanonicalName();
            List<Status> childStatuses = new ArrayList<Status>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

            Status childStatus = new Status(IStatus.WARNING, pluginId, e.getMessage());
            childStatuses.add(childStatus);
            MultiStatus multiStatus = new MultiStatus(pluginId, IStatus.WARNING,
                    childStatuses.toArray(new Status[childStatuses.size()]), MessageConfigLoader.getProperty(
                            IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_FILE_ERROR, sqlTerminal.getFilePath()),
                    null);

            FileSaveNoAccessErrorDialog fileSaveErrorDialog = new FileSaveNoAccessErrorDialog(
                    Display.getCurrent().getActiveShell(),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE), null, multiStatus,
                    IStatus.OK | IStatus.INFO | IStatus.WARNING | IStatus.ERROR, sqlTerminal);
            fileSaveErrorDialog.open();
        } catch (OutOfMemoryError exc) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED),
                    exc);

            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));
        } catch (Exception e) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SAVE_FILE_ERROR,
                            sqlTerminal.getFilePath()),
                    e.getMessage(), null);
        } finally {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.MSG_SQL_SAVE_SUCCESS,
                            sqlTerminal.getFilePath(), new Date().toString())));
        }
        return isSuccessFlag;
    }

    /**
     * Inits the file terminal info.
     *
     * @param location the location
     * @param sqlTerminal the sql terminal
     */
    private void initFileTerminalInfo(String location, SQLTerminal sqlTerminal) {
        Path path = Paths.get(location).getFileName();
        String fileName = "";
        if (path != null) {
            fileName = path.toString();
        } else {
            String[] split = location.split("\\\\");
            fileName = split[split.length - 1];
        }

        // Keeping last 30 characters of file name if file name beyond 30
        // characters
        String sqlTerminalPartLabel = fileName.length() > 30 ? "..." + fileName.substring(fileName.length() - 30)
                : fileName;
        sqlTerminal.setFilePath(location);
        sqlTerminal.setTabLabel(sqlTerminalPartLabel);
        sqlTerminal.getSqlTerminalPart().setLabel(sqlTerminalPartLabel);

        StringBuffer tooltipSB = new StringBuffer();
        tooltipSB.append("Location: ").append(location).append(System.lineSeparator());
        tooltipSB.append("Connection: ").append(sqlTerminal.getDatabaseName()).append("@")
                .append(sqlTerminal.getConnectionName());
        sqlTerminal.getSqlTerminalPart().setTooltip(tooltipSB.toString());

        sqlTerminal.setFileTerminalFlag(true);
        // File Terminal doesn't support open file
        sqlTerminal.getMenuOpenSQL().setEnabled(false);

        UIElement.getInstance().resetTabIcon(sqlTerminal.getSqlTerminalPart(),
                sqlTerminal.getSelectedDatabase() == null ? false : sqlTerminal.getSelectedDatabase().isConnected());
    }

    /**
     * Prompt override confirmation.
     *
     * @param sqlTerminal the sql terminal
     * @param localFile the local file
     * @return true, if successful
     */
    private boolean promptOverrideConfirmation(SQLTerminal sqlTerminal, File localFile) {
        // get last modified date of local file
        Date lastModifiedDate = new Date(localFile.lastModified());

        return lastModifiedDate.after(sqlTerminal.getLastSaveTime());
    }

    /**
     * Write to file.
     *
     * @param file the file
     * @param content the content
     * @throws Exception the exception
     */
    private void writeToFile(File file, String content) throws Exception {
        IExecTimer totalExect = new ExecTimer("SaveReloadSQLQueries#writeToFile method");
        totalExect.start();

        String encoding = getFileEncoding(file);

        try (FileOutputStream fileOutPutStream = new FileOutputStream(file);
                OutputStreamWriter filewriter = new OutputStreamWriter(fileOutPutStream, encoding);) {
            IExecTimer partExect = new ExecTimer("WriteToSQLFile");
            partExect.start();

            filewriter.write(content);

            partExect.stopAndLogNoException();
        } finally {
            totalExect.stopAndLogNoException();
        }
    }

    /**
     * Gets the file encoding.
     *
     * @param file the file
     * @return the file encoding
     */
    private static String getFileEncoding(File file) {
        String encoding = UserPreference.getInstance().getFileEncoding().isEmpty() ? Charset.defaultCharset().name()
                : UserPreference.getInstance().getFileEncoding();

        return encoding;
    }
}
