/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.UserNamespace;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSchemaWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DropSchemaWorker extends AbstractModalLessWindowOperationUIWokerJob {
    private UserNamespace schemaToDrop;

    /**
     * Instantiates a new drop schema worker.
     *
     * @param name the name
     * @param ns the ns
     */
    public DropSchemaWorker(String name, UserNamespace ns) {
        super(name, ns, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_DROP_SCHEMA),
                MPPDBIDEConstants.CANCELABLEJOB);
        this.schemaToDrop = ns;
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
        schemaToDrop.drop(conn);
        MPPDBIDELoggerUtility.info("Dropped schema successfully.");
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
        super.onSuccessUIAction(obj);
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return MessageConfigLoader.getProperty(IMessagesConstants.DB_DROPPED,
                schemaToDrop.getDatabase().getServer().getServerConnectionInfo().getConectionName(),
                schemaToDrop.getName());
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_SCHEMA),
                MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_ERR_WHEN_DROPPING_SCHEMA,
                        schemaToDrop.getName(), MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_SCHEMA_FAIL,
                        schemaToDrop.getDatabase().getServer().getServerConnectionInfo().getConectionName(),
                        schemaToDrop.getName())));

    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHEN_DROPPING_SCHEMA),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_WHILE_DROPPING_SCHEMS_MESSAGE,
                        schemaToDrop.getName(), MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage()));
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.DROP_SCHEMA_FAIL,
                        schemaToDrop.getDatabase().getServer().getServerConnectionInfo().getConectionName(),
                        schemaToDrop.getName())));

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
