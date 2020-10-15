/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.table;

import com.huawei.mppdbide.bl.serverdatacache.ConstraintMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
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
 * Description: The Class ValidateConstraintWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class ValidateConstraintWorker extends AbstractModalLessWindowOperationUIWokerJob {
    private ConstraintMetaData constraintMeta;

    /**
     * Instantiates a new validate constraint worker.
     *
     * @param name the name
     * @param constraintMeta the constraint meta
     */
    public ValidateConstraintWorker(String name, ConstraintMetaData constraintMeta) {
        super(name, constraintMeta, MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_VALIDATE_CONSTRAINT),
                MPPDBIDEConstants.CANCELABLEJOB);
        this.constraintMeta = constraintMeta;
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
        constraintMeta.execValidateConstraint(conn);
        MPPDBIDELoggerUtility.info("Validate Constraint successfull");
        return null;
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return MessageConfigLoader.getProperty(IMessagesConstants.VALIDATE_CONSTRAINT_SUCCESS,
                constraintMeta.getNamespace().getName(), constraintMeta.getTable().getName(), constraintMeta.getName());
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(getSuccessMsgForOBStatusBar()));
        super.additionalDoJobhandling();
    }

    /**
     * On critical exception UI action.
     *
     * @param uiCriticalException the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException uiCriticalException) {
        onException(uiCriticalException);
    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        onException(dbOperationException);
    }

    /**
     * On exception.
     *
     * @param exception the e
     */
    public void onException(MPPDBIDEException exception) {
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.VALIDATE_CONSTRAINT_UNABLE,
                constraintMeta.getNamespace().getName(), constraintMeta.getTable().getName(), constraintMeta.getName())
                + MPPDBIDEConstants.LINE_SEPARATOR + exception.getServerMessage();
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.VALIDATE_CONSTRAINT_ERROR), msg);
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(
                        IMessagesConstants.VALIDATE_CONSTRAINT_UNABLE, constraintMeta.getNamespace().getName(),
                        constraintMeta.getTable().getName(), constraintMeta.getName())));
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
