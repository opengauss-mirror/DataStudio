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

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class ReindexTableWorker.
 *
 * @since 3.0.0
 */
public class ReindexTableWorker extends AbstractModalLessWindowOperationUIWokerJob {
    private TableMetaData selectedTable;

    /**
     * Instantiates a new reindex table worker.
     *
     * @param name the name
     * @param tab the tab
     */
    public ReindexTableWorker(String name, TableMetaData tab) {
        super(name, tab, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_REINDEX_TABLE),
                MPPDBIDEConstants.CANCELABLEJOB);
        selectedTable = tab;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE, selectedTable.getNamespace().getName(),
                selectedTable.getName());
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
        selectedTable.execReindex(conn);
        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(getSuccessMsgForOBStatusBar()));
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE,
                        selectedTable.getNamespace().getName(), selectedTable.getName()));
        super.additionalDoJobhandling();
    }

    /**
     * On critical exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_CONN_ERROR,
                        selectedTable.getNamespace().getName(), selectedTable.getName(), exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_FAIL,
                        selectedTable.getNamespace().getName(), selectedTable.getName())));
        MPPDBIDELoggerUtility.error("ReindexTableWorker: Reindexing table failed.", exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_ERROR,
                        selectedTable.getNamespace().getName(), selectedTable.getName(), exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.REINDEX_TABLE_FAIL,
                        selectedTable.getNamespace().getName(), selectedTable.getName())));
        MPPDBIDELoggerUtility.error("ReindexTableWorker: Reindexing table failed.", exception);

    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {

        return null;
    }

}
