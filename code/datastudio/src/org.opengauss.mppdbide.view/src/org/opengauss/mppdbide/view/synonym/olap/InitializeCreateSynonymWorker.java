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

package org.opengauss.mppdbide.view.synonym.olap;

import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;

/**
 * 
 * Title: Class
 * 
 * Description: The Class InitializeCreateSynonymWorker.
 *
 * @since 3.0.0
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