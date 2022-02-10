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

package org.opengauss.mppdbide.view.handler.table;

import org.opengauss.mppdbide.bl.serverdatacache.ConstraintMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
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
 * Description: The Class ValidateConstraintWorker.
 *
 * @since 3.0.0
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
