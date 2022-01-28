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

package com.huawei.mppdbide.view.ui;

import java.sql.SQLException;

import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionManager;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SetDefaultSchemaWorker.
 *
 * @since 3.0.0
 */
public class SetDefaultSchemaWorker extends UIWorkerJob {

    private String schemaName;
    private ConnectionManager connectionManager;
    private static final String SET_DEFAULT_SCHEMA_QUERY = "SET search_path TO ";

    /**
     * Instantiates a new sets the default schema worker.
     *
     * @param ipSchemaName the ip schema name
     */
    public SetDefaultSchemaWorker(String ipSchemaName) {
        super("Default Schema Worker", null);
        this.schemaName = ipSchemaName;
        this.connectionManager = null;
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
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        Display.getDefault().syncExec(() -> {
            if (databaseListControl != null && databaseListControl.getSelectedConnection() != null) {
                connectionManager = databaseListControl.getSelectedConnection().getConnectionManager();
            } 
        });

        connectionManager.setDefaultSchemaForAllUsedConnections(SET_DEFAULT_SCHEMA_QUERY + this.schemaName);
        return null;
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        generateErrorPopUp(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        generateErrorPopUp(exception);
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        this.connectionManager = null;
    }

    /**
     * On exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onExceptionUIAction(Exception exception) {
        if (exception instanceof SQLException) {
            DBConnection sqlTerminalConn = connectionManager.getSqlTerminalConn();
            if (null != sqlTerminalConn) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG), exception);
                String extractedMsg = sqlTerminalConn
                        .extractErrorCodeAndErrorMsgFromServerError((SQLException) exception);
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG)
                                + MPPDBIDEConstants.LINE_SEPARATOR + extractedMsg);

                clearSelectionInDefaultSchemaCombo();
            }
        }
    }

    private void clearSelectionInDefaultSchemaCombo() {
        DefaultSchemaControl defaultSchemaControl = UIElement.getInstance().getDefaultSchemaControl();
        if (null != defaultSchemaControl) {
            defaultSchemaControl.clearSelection();
        }
    }

    private void generateErrorPopUp(MPPDBIDEException exception) {
        MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG),
                exception);
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_SCHEMA_ERROR_MSG)
                        + MPPDBIDEConstants.LINE_SEPARATOR + exception.getServerMessage());

        clearSelectionInDefaultSchemaCombo();
    }

}
