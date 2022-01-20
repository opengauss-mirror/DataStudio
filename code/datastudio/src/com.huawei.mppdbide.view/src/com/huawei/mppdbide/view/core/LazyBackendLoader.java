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

package com.huawei.mppdbide.view.core;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.util.IExecTimer;
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
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class LazyBackendLoader.
 *
 * @since 3.0.0
 */
public abstract class LazyBackendLoader extends UIWorkerJob {

    private DBConnection conn;
    private Database db;

    private boolean isFailed = false;
    private StatusMessage statusMsg;
    private IExecTimer timer;

    /**
     * Checks if is failed.
     *
     * @return true, if is failed
     */
    public boolean isFailed() {
        return isFailed;
    }

    /**
     * Instantiates a new lazy backend loader.
     *
     * @param name the name
     * @param family the family
     * @param statusMsg the status msg
     * @param timer the timer
     */
    LazyBackendLoader(String name, Object family, StatusMessage statusMsg, IExecTimer timer) {
        super(name, family);

        this.statusMsg = statusMsg;
        this.timer = timer;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        return null;
    }

    /**
     * On MPPDBIDE exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException exception) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(getErrorMessage()));
        setLoadFailed();
    }

    /**
     * Sets the load failed.
     */
    void setLoadFailed() {
        isFailed = true;
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    String getErrorMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.NAMESPACE_RETRIVE_ERROR);
    }

    /**
     * On MPPDBIDE exception.
     *
     * @param exception the exception
     */
    @Override
    public void onMPPDBIDEException(MPPDBIDEException exception) {
        super.onMPPDBIDEException(exception);
        setLoadFailed();
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDELoggerUtility.error("Database critical exception occured while lazy loading..", exception);
        setLoadFailed();
        if (getDb().isConnected()) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, getDb());
        } else {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true, getMessageDialogTitle(),
                    MessageConfigLoader.getProperty(IMessagesConstants.NAMESPACE_RETRIVE_CRITICAL_ERROR)
                            + MPPDBIDEConstants.LINE_SEPARATOR + exception.getServerMessage());
        }
    }

    /**
     * Gets the message dialog message.
     *
     * @return the message dialog message
     */
    String getMessageDialogMessage() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ERR_LAZY_BACKEND_LOAD_TITLE);
    }

    /**
     * Gets the message dialog title.
     *
     * @return the message dialog title
     */
    String getMessageDialogTitle() {
        return MessageConfigLoader.getProperty(IMessagesConstants.ERR_TITLE_DB_CRITICAL_ERROR);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        setLoadFailed();
        MPPDBIDELoggerUtility.error("Database operation exception occured while lazy loading..", exception);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(getMessageDialogMessage()));
    }

    /**
     * On out of memory UI error.
     *
     * @param error the error
     */
    @Override
    public void onOutOfMemoryUIError(OutOfMemoryError error) {
        MPPDBIDELoggerUtility.error("Out of memory occured while lazy loading..", error);
        setLoadFailed();
        MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_OUT_OF_MEMORY),

                MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED));

    }

    /**
     * On exception.
     *
     * @param exception the exception
     */
    @Override
    public void onException(Exception exception) {
        setLoadFailed();
        super.onException(exception);
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        getDb().setLoadingNamespaceInProgress(false);
        try {
            timer.stopAndLog();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Exception while getting elapsed time", exception);
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (null != statusMsg) {
            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (bottomStatusBar != null) {
                bottomStatusBar.hideStatusbar(statusMsg);
            }
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {

    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    public Database getDb() {
        return db;
    }

    /**
     * Sets the db.
     *
     * @param db the new db
     */
    public void setDb(Database db) {
        this.db = db;
    }

    /**
     * Gets the conn.
     *
     * @return the conn
     */
    public DBConnection getConn() {
        return conn;
    }

    /**
     * Sets the conn.
     *
     * @param conn the new conn
     */
    public void setConn(DBConnection conn) {
        this.conn = conn;
    }

}
