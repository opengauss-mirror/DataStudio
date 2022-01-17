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

package com.huawei.mppdbide.view.handler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.text.Normalizer.Form;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.export.BatchExportDDLFilter;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.export.ExportManager;
import com.huawei.mppdbide.bl.export.ExportParameters;
import com.huawei.mppdbide.bl.export.IExportManager;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.FilePermissionFactory;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.security.SecureRandomGenerator;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowTableDDLGsDump.
 *
 * @since 3.0.0
 */
public class ShowTableDDLUsingSysTable {
    private EXPORTTYPE exportType;
    private IExportManager exportManager;
    private File file;
    private Path tempFolderPath;
    private boolean isTablespaceOption;
    private BufferedOutputStream bfs;
    private BatchExportDDLFilter filter;
    private ByteArrayOutputStream bArrOutStream = new ByteArrayOutputStream();
    private TableMetaData object;
    private IExecTimer exc;
    private Database db;
    private String pwd;

    /**
     * Instantiates a new show table DDL gs dump.
     *
     * @param object the object
     * @param exportType the export type
     * @param tempFolderPath the temp folder path
     * @param isTablespaceOption the is tablespace option
     * @param db the db
     * @param exc the exc
     * @param pwd the pwd
     */
    public ShowTableDDLUsingSysTable(TableMetaData object, EXPORTTYPE exportType, Path tempFolderPath,
            boolean isTablespaceOption, Database db, IExecTimer exc, String pwd) {
        this.object = object;
        this.db = db;
        this.exc = exc;
        this.pwd = pwd;
        exportManager = new ExportManager();
        this.exportType = exportType;
        this.tempFolderPath = tempFolderPath;
        this.isTablespaceOption = isTablespaceOption;
    }

    /**
     * Do job gs dump.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws FileOperationException the file operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws Exception the exception
     */
    public Object doJobShowDDL() throws DatabaseOperationException, DatabaseCriticalException, FileOperationException,
            MPPDBIDEException, IOException, Exception {
        if (!Files.exists(tempFolderPath)) {
            FilePermissionFactory.getFilePermissionInstance().createFileWithPermission(tempFolderPath.toString(), true,
                    null, true);
        }
        byte[] byteRandomArray = SecureRandomGenerator.getRandomNumber();
        BigInteger generatedRandom = new BigInteger(byteRandomArray);

        File workingDir = new File(tempFolderPath.toString());
        String fileName = generatedRandom.abs() + "";
        file = new File(tempFolderPath + File.separator + fileName);

        exc.start();

        bfs = new BufferedOutputStream(bArrOutStream);
        filter = new BatchExportDDLFilter(bfs);

        if (null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
            exportManager.exportSqlToFile(
                    new ExportParameters(pwd, fileName, db, exportType, object, isTablespaceOption, workingDir));
        } else {
            exportManager.exportSqlToFile(fileName, exportType, object, isTablespaceOption,
                    new File(workingDir.getCanonicalPath()));
        }

        try {
            UIElement.getInstance().validateFileSize(file.getCanonicalPath());
            filter.removeComments(file.getCanonicalPath());
            bfs.flush();
        } catch (FileOperationException exception) {
            Files.deleteIfExists(Paths.get(file.getCanonicalPath()));
            MPPDBIDELoggerUtility.error("ShowDDLTableHandler: FileOperationException occurred.", exception);
        }
        return null;
    }

    /**
     * Show DD lin terminal GS dump.
     */
    protected void showDDLinTerminalGSDump() {
        String content = null;
        try {
            if (UserPreference.getInstance().getFileEncoding().isEmpty()) {
                content = bArrOutStream.toString(Charset.defaultCharset().name());
            } else {
                content = bArrOutStream.toString(UserPreference.getInstance().getFileEncoding());
            }
        } catch (IOException exception) {
            MPPDBIDELoggerUtility.error("ShowDDLTableHandler: IOException occurred.", exception);
        }
        SQLTerminal terminal = null;
        if (null != db) {
            terminal = UIElement.getInstance().createNewTerminal(db);
        }
        if (null != terminal) {
            terminal.setDocumentContent(content);
            terminal.resetSQLTerminalButton();
            terminal.resetAutoCommitButton();
            terminal.setModified(true);
            terminal.setModifiedAfterCreate(true);
        }
        exc.stopAndLogNoException();
    }

    /**
     * Final cleanup gs dump.
     */
    public void finalCleanupGsDump() {
        if (file != null && file.exists()) {
            if (file.delete()) {
                MPPDBIDELoggerUtility.info("file deleted");
            } else {
                MPPDBIDELoggerUtility.error("file not deleted");
            }
        }
        if (bArrOutStream != null) {
            try {
                bArrOutStream.close();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("byte array outputStream not closed", exception);
            }
        }
        if (bfs != null) {
            try {
                bfs.close();
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("byte array outputStream not closed", exception);
            }
        }
    }

    /**
     * On operational exception UI action gs dump.
     *
     * @param dbOperationException the e
     */
    public void onOperationalExceptionUIActionGsDump(DatabaseOperationException dbOperationException) {
        MPPDBIDEDialogs.clearExistingDialog();
        String errMsg = dbOperationException.getMessage();
        String errorMsg = "";
        if (errMsg.contains(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED, ""))) {
            errMsg = errMsg.replace(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED, ""),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE));
        }
        errMsg = Normalizer.normalize(errMsg, Form.NFKC);
        if (errMsg.startsWith("Cancel")) {
            errorMsg = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_CANCELING,
                    object.getNamespace().getName(), object.getName()) + errMsg;
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE), errorMsg);
        } else {
            String hintMsg = "";
            if (dbOperationException.getDBErrorMessage().contains("No matching")) {
                hintMsg = MessageConfigLoader.getProperty(IMessagesConstants.CHECK_FILE_ENCODE_SET);
            }
            String msgString = MessageConfigLoader
                    .getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED_WITHOUT_SERVER_MSG);

            MPPDBIDEDialogs.generateErrorPopup(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_TITLE),
                    Display.getDefault().getActiveShell(), dbOperationException, hintMsg, msgString, null);
        }

        String failMessage = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_FAILED,
                object.getNamespace().getName(), object.getName());
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(failMessage));
    }
}
