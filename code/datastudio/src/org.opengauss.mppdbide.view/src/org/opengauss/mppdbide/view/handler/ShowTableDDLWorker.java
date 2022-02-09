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

package org.opengauss.mppdbide.view.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jface.text.Document;

import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.bl.util.IExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.FileOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowTableDDLWorker.
 *
 * @since 3.0.0
 */
public class ShowTableDDLWorker extends PromptPasswordUIWorkerJob {

    /**
     * The object.
     */
    protected TableMetaData object;
    private String tableDDL;

    /**
     * The bottom status bar.
     */
    protected BottomStatusBar bottomStatusBar;

    /**
     * The stat message.
     */
    protected StatusMessage statMessage;

    /**
     * The elapsed time.
     */
    protected String elapsedTime = null;

    /**
     * The exc.
     */
    protected IExecTimer exc = new ExecTimer("show DDL Table Worker");

    /**
     * The db.
     */
    protected Database db;
    private ShowTableDDLUsingSysTable ddlTable;
    private boolean isShowDDLSupported;

    /**
     * Instantiates a new show table DDL worker.
     *
     * @param name the name
     * @param table the table
     * @param bottomStatusBar2 the bottom status bar 2
     * @param statusMessage the status message
     * @param db the db
     */
    public ShowTableDDLWorker(String name, TableMetaData table, BottomStatusBar bottomStatusBar2,
            StatusMessage statusMessage, Database db) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.SHOW_DDL_FAILED_TITLE);
        this.object = table;
        this.bottomStatusBar = bottomStatusBar2;
        this.statMessage = statusMessage;
        this.tableDDL = "";
        this.db = db;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws FileOperationException the file operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws IOException Signals that an I/O exception has occurred.
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, FileOperationException,
            MPPDBIDEException, IOException, Exception {
        isShowDDLSupported = db.isShowDDLSupportByServer();
        if (!isShowDDLSupported && !ShowDDLViewLayerHelper.getClientSSLKeyFile(db)) {
                return null;
            }
            Path profileFolderPath = ConnectionProfileManagerImpl.getInstance().getDiskUtility().getProfileFolderPath();
            Path tempFolderPath = Paths.get(profileFolderPath.toString(), MPPDBIDEConstants.TEMP_FOLDER_PATH);
            ddlTable = new ShowTableDDLUsingSysTable(object, EXPORTTYPE.SQL_DDL, tempFolderPath,
                    IHandlerUtilities.getTablespaceSelectionOptions(), getTaskDB(), exc, getEncrpytedProfilePrd());
            ddlTable.doJobShowDDL();
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        if (isCancel()) {
            exc.stopAndLogNoException();
            String errorMsg = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_CANCELING,
                    object.getNamespace().getName(), object.getName());
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE), errorMsg);
            return;
        }

        if (ddlTable != null) {

            ddlTable.showDDLinTerminalGSDump();
        } else {
            return;
        }
    }

    /**
     * Show DD lin terminal.
     */
    protected void showDDLinTerminal() {
        SQLTerminal terminal = UIElement.getInstance().createNewTerminal(object.getDatabase());
        if (null != terminal) {
            Document doc = new Document(tableDDL);
            terminal.getTerminalCore().setDocument(doc, 0);
            terminal.resetSQLTerminalButton();
            terminal.resetAutoCommitButton();
            terminal.setModified(true);
            terminal.setModifiedAfterCreate(true);
            terminal.registerModifyListener();
        }
        exc.stopAndLogNoException();
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
        exc.stopAndLogNoException();
        MPPDBIDEDialogs.clearExistingDialog();
        String failMessage = MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_FAILED,
                object.getNamespace().getName(), object.getName());
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.SHOW_DDL_FAILED_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.SHOW_TABLE_DDL_FAILED,
                        object.getNamespace().getName(), object.getName()));
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(failMessage));

    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        if (isShowDDLSupported) {
            exc.stopAndLogNoException();
            String title = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_FAIL_PROCESS_TITLE);
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, title,
                    dbOperationException.getServerMessage());
        } else {
            ddlTable.onOperationalExceptionUIActionGsDump(dbOperationException);
        }

    }

    /**
     * On out of memory error.
     *
     * @param outOfMemoryErr the out of memory err
     */
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

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        super.finalCleanup();
        if (ddlTable != null) {
            ddlTable.finalCleanupGsDump();
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        bottomStatusBar.hideStatusbar(statMessage);
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return getTaskDB();
    }

    /**
     * Password validation failed.
     *
     * @param e the e
     */
    @Override
    protected void passwordValidationFailed(MPPDBIDEException exception) {
        super.passwordValidationFailed(exception);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader.getProperty(
                IMessagesConstants.SHOW_TABLE_DDL_FAILED, object.getNamespace().getName(), object.getName())));
        MPPDBIDELoggerUtility.error("ShowDDLTableHandler: Validation failed.", exception);
    }
}
