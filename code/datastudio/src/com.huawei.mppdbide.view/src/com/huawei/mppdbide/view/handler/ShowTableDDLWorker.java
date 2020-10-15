/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.eclipse.jface.text.Document;

import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileManagerImpl;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.FileOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ShowTableDDLWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
