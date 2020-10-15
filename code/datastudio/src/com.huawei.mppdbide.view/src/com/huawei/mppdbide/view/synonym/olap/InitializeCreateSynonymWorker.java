/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.synonym.olap;

import org.eclipse.swt.widgets.Shell;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: Class
 * 
 * Description: The Class InitializeCreateSynonymWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author c00550043
 * @version
 * @since Mar 12, 2020
 */
public class InitializeCreateSynonymWorker extends PromptPasswordUIWorkerJob {
    private Shell shell;
    private Database db;
    private DBConnection conn;
    private SynonymObjectGroup synonymObjectGroup;

    /**
     * Instantiates a new initial create synonym worker.
     *
     * @param shell the shell
     * @param name the name
     * @param family the family
     * @param errorWindowTitle the error window title
     */
    public InitializeCreateSynonymWorker(Shell shell, String name, Object family, String errorWindowTitle) {
        super(name, family, errorWindowTitle);
        this.shell = shell;
    }

    @Override
    protected Database getDatabase() {
        if (null != this.db) {
            return this.db;
        }
        if (null != IHandlerUtilities.getSelectedSynonymGroup()) {
            this.synonymObjectGroup = IHandlerUtilities.getSelectedSynonymGroup();
            this.db = synonymObjectGroup.getDatabase();
        }
        return this.db;
    }

    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (null == getDatabase()) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE));
            throw new MPPDBIDEException(IMessagesConstants.CREATE_SYNONYM_NO_DATABASE);
        }
        setServerPwd(getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        this.conn = getDatabase().getConnectionManager().getFreeConnection();
        return null;
    }

    @Override
    public void onSuccessUIAction(Object obj) {
        if (null != IHandlerUtilities.getSelectedSynonymGroup()) {
            CreateDBMSSynonymDialog dialog = new CreateDBMSSynonymDialog(this.shell, synonymObjectGroup, this.conn);

            dialog.open();

        }
    }

    @Override
    public void finalCleanupUI() {
        if (null != this.conn) {
            getDatabase().getConnectionManager().releaseConnection(this.conn);
        }
    }

    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException databaseCriticalException) {
        exceptionCreateSynonymEventCall(databaseCriticalException);
    }

    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException databaseOperationException) {
        exceptionCreateSynonymEventCall(databaseOperationException);
    }

    /**
     * Exception create synonym event call.
     *
     * @param exception the exception
     */
    private void exceptionCreateSynonymEventCall(Exception exception) {
        String message = null;
        if (exception instanceof MPPDBIDEException) {
            message = ((MPPDBIDEException) exception).getServerMessage();
        } else {
            message = exception.getMessage();
        }
        MPPDBIDEDialogs.generateDSErrorDialog(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_NEW_SYNONYM),
                MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SYNONYM_ERROR, message), message, null);
    }
}