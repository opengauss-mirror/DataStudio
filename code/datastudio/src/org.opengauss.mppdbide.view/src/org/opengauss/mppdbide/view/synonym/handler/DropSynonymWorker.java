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

package org.opengauss.mppdbide.view.synonym.handler;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.ISynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.JobCancelStatus;
import org.opengauss.mppdbide.bl.serverdatacache.SynonymMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceWorkerJob.
 *
 * @since 3.0.0
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
        super(objName, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.VALIDATE_CIPHER_FAIL);
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
