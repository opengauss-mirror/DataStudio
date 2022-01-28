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

package com.huawei.mppdbide.view.sequence.handler;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileAttribute;
import java.security.SecureRandom;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.export.BatchExportDDLFilter;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.export.ExportManager;
import com.huawei.mppdbide.bl.export.ExportParameters;
import com.huawei.mppdbide.bl.export.IExportManager;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.SequenceMetadata;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.files.DSFileAttributes;
import com.huawei.mppdbide.utils.files.DSFilesWrapper;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.security.SecureRandomGenerator;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.ShowDDLViewLayerHelper;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowSequenceDDLHandler.
 *
 * @since 3.0.0
 */
public class ShowSequenceDDLHandler {

    /**
     * Execute.
     */
    @Execute
    public void execute() {
        SequenceMetadata sequence = IHandlerUtilities.getSelectedSequenceObject();
        if (sequence == null) {
            return;
        }

        Database db = sequence.getDatabase();

        if (!IHandlerUtilities.isDDLOperationsSupported(db)) {
            return;
        }

        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForSequenceWithMsg(sequence.getName(),
                sequence.getNamespace().getName(), sequence.getDatabase().getName(),
                sequence.getDatabase().getServerName(), IMessagesConstants.SHOW_SEQUENCE_DDL_PROGRESS_NAME);

        Path profileFolderPath = ConnectionProfileManagerImpl.getInstance().getDiskUtility().getProfileFolderPath();
        Path tempFolderPath = Paths.get(profileFolderPath.toString(), MPPDBIDEConstants.TEMP_FOLDER_PATH);

        ShowSequenceDDLWorker showSequenceDDLWorker = new ShowSequenceDDLGsDumpWorker(progressLabel, sequence,
                EXPORTTYPE.SQL_DDL, tempFolderPath, IHandlerUtilities.getTablespaceSelectionOptions());

        showSequenceDDLWorker.setTaskDB(db);
        showSequenceDDLWorker.schedule();
    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        return null != IHandlerUtilities.getSelectedSequenceObject();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ShowSequenceDDLWorker.
     */
    private abstract static class ShowSequenceDDLWorker extends PromptPasswordUIWorkerJob {

        /**
         * The sequence.
         */
        protected SequenceMetadata sequence;
        private ByteArrayOutputStream bArrOutStream = new ByteArrayOutputStream();

        /**
         * The elapsed time.
         */
        protected String elapsedTime = null;

        /**
         * The exc.
         */
        protected IExecTimer exc = new ExecTimer("Show Sequence DDL Worker");

        /**
         * Instantiates a new show sequence DDL worker.
         *
         * @param name the name
         * @param sequence the sequence
         */
        public ShowSequenceDDLWorker(String name, SequenceMetadata sequence) {
            super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.SHOW_DDL_FAILED_TITLE);
            this.sequence = sequence;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            if (isCancel()) {
                exc.stopAndLogNoException();
                String errorMsg = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_SEQUENCE_DDL_CANCELING,
                        sequence.getNamespace().getName(), sequence.getName());
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE), errorMsg);
                return;
            }

            showDDLinTerminal();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            exc.stopAndLogNoException();
            MPPDBIDEDialogs.clearExistingDialog();
            String failMessage = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_SEQUENCE_DDL_FAILED,
                    sequence.getNamespace().getName(), sequence.getName());
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_SEQUENCE_DDL_FAILED,
                            sequence.getNamespace().getName(), sequence.getName()));
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(failMessage));

        }

        @Override
        public void onOutOfMemoryError(OutOfMemoryError outOfMemoryErr) {
            exc.stopAndLogNoException();
            try {
                elapsedTime = exc.getElapsedTime();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("error in fetching elapsed time from timer", exception);
            }
            UIElement.getInstance().outOfMemoryCatch(elapsedTime, outOfMemoryErr.getMessage());

        }

        @Override
        public Database getDatabase() {
            return getTaskDB();
        }

        @Override
        public void finalCleanupUI() {
        }

        @Override
        protected void passwordValidationFailed(MPPDBIDEException exception) {
            super.passwordValidationFailed(exception);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_SEQUENCE_DDL_FAILED,
                            sequence.getNamespace().getName(), sequence.getName())));
        }

        /**
         * Show DD lin terminal.
         */
        private void showDDLinTerminal() {
            String content = null;
            try {
                if (UserPreference.getInstance().getFileEncoding().isEmpty()) {
                    content = bArrOutStream.toString(Charset.defaultCharset().name());
                } else {
                    content = bArrOutStream.toString(UserPreference.getInstance().getFileEncoding());
                }
            } catch (IOException exception) {
                MPPDBIDELoggerUtility.error("Failed to show ddl in terminal.", exception);
            }
            SQLTerminal terminal = null;
            if (null != getTaskDB()) {
                terminal = UIElement.getInstance().createNewTerminal(getTaskDB());
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
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ShowSequenceDDLGsDumpWorker.
     */
    private static final class ShowSequenceDDLGsDumpWorker extends ShowSequenceDDLWorker {
        private EXPORTTYPE exportType;
        private IExportManager exportManager;
        private File file;
        private Path tempFolderPath;
        private boolean isTablespaceOption;
        private BufferedOutputStream bfs;
        private BatchExportDDLFilter filter;

        private FileAttribute<List<AclEntry>> fileAttributes;

        /**
         * Instantiates a new show sequence DDL gs dump worker.
         *
         * @param name the name
         * @param sequence the sequence
         * @param exportType the export type
         * @param tempFolderPath the temp folder path
         * @param isTablespaceOption the is tablespace option
         */
        public ShowSequenceDDLGsDumpWorker(String name, SequenceMetadata sequence, EXPORTTYPE exportType,
                Path tempFolderPath, boolean isTablespaceOption) {
            super(name, sequence);
            this.exportManager = new ExportManager();
            this.exportType = exportType;
            this.tempFolderPath = tempFolderPath;
            this.isTablespaceOption = isTablespaceOption;
            this.fileAttributes = new DSFileAttributes(null);
        }

        @Override
        public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, FileOperationException,
                MPPDBIDEException, IOException, Exception {
            if (!Files.exists(tempFolderPath)) {
                DSFilesWrapper.createDirectory(tempFolderPath, fileAttributes);
            }
            byte[] byteRandomArray = SecureRandomGenerator.getRandomNumber();
            BigInteger generatedRandom = new BigInteger(byteRandomArray);

            String fileNme = generatedRandom.abs() + "";
            file = new File(tempFolderPath + File.separator + fileNme);

            exc.start();

            bfs = new BufferedOutputStream(super.bArrOutStream);
            filter = new BatchExportDDLFilter(bfs);
            
            if (!sequence.getDatabase().isShowDDLSupportByServer()
                    && !ShowDDLViewLayerHelper.getClientSSLKeyFile(getTaskDB())) {
                return null;
            }
            File wrkingDir = new File(tempFolderPath.toString());
            if (null != getTaskDB() && getTaskDB().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)) {
                exportManager.exportSqlToFile(new ExportParameters(getEncrpytedProfilePrd(), fileNme, getTaskDB(),
                        exportType, sequence, isTablespaceOption, wrkingDir));
            } else {
                exportManager.exportSqlToFile(fileNme, exportType, sequence, isTablespaceOption,
                        new File(wrkingDir.getCanonicalPath()));
            }

            try {
                UIElement.getInstance().validateFileSize(file.getCanonicalPath());
                filter.removeComments(file.getCanonicalPath());
                bfs.flush();
            } catch (FileOperationException exception) {
                Files.deleteIfExists(Paths.get(file.getCanonicalPath()));
                MPPDBIDELoggerUtility.error("Failed to read temp file content to buffer output stream.", exception);
            }

            return null;
        }

        @Override
        public void finalCleanup() {
            super.finalCleanup();
            if (file != null && file.exists()) {
                if (file.delete()) {
                    MPPDBIDELoggerUtility.debug("file deleted.");
                } else {
                    MPPDBIDELoggerUtility.error("file is not deleted.");
                }
            }
            if (super.bArrOutStream != null) {
                try {
                    super.bArrOutStream.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("fail to close byte array output stream.", exception);
                }
            }
            if (bfs != null) {
                try {
                    bfs.close();
                } catch (IOException exception) {
                    MPPDBIDELoggerUtility.error("fail to close buffer output stream.", exception);
                }
            }
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            MPPDBIDEDialogs.clearExistingDialog();

            String hintMsg = "";

            // the database object of show ddl is not existed.
            if (exception.getDBErrorMessage().contains("No matching")) {
                hintMsg = MessageConfigLoader.getProperty(IMessagesConstants.CHECK_FILE_ENCODE_SET);
            }

            String msgString = MessageConfigLoader
                    .getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_INTRUPTED_WITHOUT_SERVER_MSG);

            MPPDBIDEDialogs.generateErrorPopup(
                    MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_TITLE),
                    Display.getDefault().getActiveShell(), exception, hintMsg, msgString, null);

            // display error message at object browser status bar
            String failMessage = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_SEQUENCE_DDL_FAILED,
                    sequence.getNamespace().getName(), sequence.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(failMessage));

        }

    }

}
