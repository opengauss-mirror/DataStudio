/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.synonym.handler;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.ISynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.JobCancelStatus;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropSynonymWorker extends PromptPasswordUIWorkerJob {
    private ISynonymMetaData synonymMetaData;
    private StatusMessage staMsg;
    private DBConnection connection;
    private JobCancelStatus cancelStatus = null;
    private Boolean isCasecade;
    private Database db;

    /**
     * Instantiates a new drop synonym worker.
     *
     * @param objName the name
     * @param family the family
     * @param selectdSynonym the selected synonym
     * @param statsMsg the status msg
     */
    public DropSynonymWorker(String objName, ISynonymMetaData selectdSynonym, StatusMessage statsMsg,
            boolean isCascade) {
        super(objName, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.VALIDATE_PASSWORD_FAIL);
        this.synonymMetaData = selectdSynonym;
        this.staMsg = statsMsg;
        this.isCasecade = isCascade;
        this.cancelStatus = new JobCancelStatus();
    }

    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (getDatabase() == null) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE));
            throw new MPPDBIDEException(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE);
        }
        setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        this.connection = getDatabase().getConnectionManager().getObjBrowserConn();
        synonymMetaData.dropSynonym(connection, this.isCasecade);
        return null;
    }

    @Override
    protected void canceling() {
        super.canceling();
        try {
            connection.cancelQuery();
            cancelStatus.setCancel(true);
        } catch (Exception exception) {
            MPPDBIDELoggerUtility.error("Failed to cancel query", exception);
        }
    }

    @Override
    public void onSuccessUIAction(Object obj) {
        if (cancelStatus.getCancel()) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG)));
            return;
        }

        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();

        if (objectBrowserModel != null) {
            if (synonymMetaData instanceof SynonymMetaData) {
                objectBrowserModel.setSelection(((SynonymMetaData) (synonymMetaData)).getParent().getSynonyms());
                objectBrowserModel.refreshObject(((SynonymMetaData) synonymMetaData).getParent());
            }
        }
        String message = "";
        if (synonymMetaData instanceof SynonymMetaData) {
            message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_SUCCESS,
                    (((SynonymMetaData) synonymMetaData).getParent()).getQualifiedObjectName(),
                    synonymMetaData.getQualifiedObjectName());
        }

        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
    }

    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException databaseOperationException) {
        showErrorPopupMsg(databaseOperationException);
    }

    @Override
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {

        super.onMPPDBIDEExceptionUIAction(exception);
        showErrorPopupMsg(exception);
    }

    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException databaseCriticalException) {
        showErrorPopupMsg(databaseCriticalException);
    }

    /**
     * Show error popup msg.
     *
     * @param exception the exception
     */
    private void showErrorPopupMsg(MPPDBIDEException exception) {
        if (synonymMetaData instanceof SynonymMetaData) {
            MPPDBIDEDialogs.generateDSErrorDialog(
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_ERROR,
                            ((SynonymMetaData) synonymMetaData).getParent().getQualifiedObjectName(),
                            synonymMetaData.getQualifiedObjectName()),
                    MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_UNABLE),
                    exception.getServerMessage(), null);
        }

        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_SYNONYM_ERROR,
                        synonymMetaData.getQualifiedObjectName(),
                        (synonymMetaData).getServer().getServerConnectionInfo().getConectionName())));
    }

    @Override
    public void finalCleanup() {
        super.finalCleanup();
        if (this.connection != null) {
            getDatabase().getConnectionManager().releaseConnection(this.connection);
        }
    }

    @Override
    public void finalCleanupUI() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (null != bttmStatusBar) {

            bttmStatusBar.hideStatusbar(this.staMsg);
        }
    }

    @Override
    protected Database getDatabase() {
        if (this.db != null) {
            return this.db;
        }

        this.db = (synonymMetaData).getDatabase();
        return this.db;
    }

}
