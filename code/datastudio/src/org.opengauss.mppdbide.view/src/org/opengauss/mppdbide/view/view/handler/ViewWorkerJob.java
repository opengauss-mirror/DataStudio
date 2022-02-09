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

package org.opengauss.mppdbide.view.view.handler;

import org.eclipse.jface.dialogs.Dialog;

import org.opengauss.mppdbide.bl.serverdatacache.IViewMetaData;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserComboDialog;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewWorkerJob.
 *
 * @since 3.0.0
 */
public class ViewWorkerJob extends UIWorkerJob {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum VIEWOPTYPE.
     */
    public enum VIEWOPTYPE {

        /**
         * The drop view.
         */
        DROP_VIEW,
        /**
         * The rename view.
         */
        RENAME_VIEW,
        /**
         * The set schema.
         */
        SET_SCHEMA
    };

    private VIEWOPTYPE type;
    private IViewMetaData view;
    private String addInfo;
    private String printMsg;
    private Dialog dialog;
    private String oldName;
    private Object oldSchema;
    private String oldSchemaName;
    private TerminalExecutionConnectionInfra conn;

    /**
     * Instantiates a new view worker job.
     *
     * @param name the name
     * @param type the type
     * @param family the family
     * @param view the view
     * @param additionalInfo the additional info
     * @param dialogObj the dialog obj
     */
    public ViewWorkerJob(String name, VIEWOPTYPE type, Object family, IViewMetaData view, String additionalInfo,
            Object dialogObj) {
        super(name, family);
        this.type = type;
        this.oldName = view.getName();
        this.view = view;
        this.addInfo = additionalInfo;
        this.oldSchema = view.getParent();
        this.oldSchemaName = view.getNameSpaceName();

        if (dialogObj instanceof UserInputDialog) {
            this.dialog = (UserInputDialog) dialogObj;
        } else if (dialogObj instanceof UserComboDialog) {
            this.dialog = (UserComboDialog) dialogObj;
        }
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
        conn = PromptPrdGetConnection.getConnection(view.getDatabase());
        switch (this.type) {
            case DROP_VIEW: {
                boolean isAppendCascade = "true".equals(this.addInfo);
                view.dropView(conn.getConnection(), isAppendCascade);
                String msgConst = isAppendCascade ? IMessagesConstants.DROP_VIEW_CASCADE_SUCCESS
                        : IMessagesConstants.DROP_VIEW_SUCCESS;
                printMsg = MessageConfigLoader.getProperty(msgConst, view.getNamespaceQualifiedName(),
                        view.getQualifiedObjectName());
                break;
            }
            case RENAME_VIEW: {
                view.rename(this.addInfo, conn.getConnection());
                printMsg = MessageConfigLoader.getProperty(IMessagesConstants.RENAME_VIEW_SUCCESS, oldName,
                        view.getNamespaceQualifiedName(), view.getQualifiedObjectName());
                break;
            }
            default: {
                view.setNamespaceTo(this.addInfo, conn.getConnection());
                printMsg = MessageConfigLoader.getProperty(IMessagesConstants.SET_VIEW_SCHEMA_SUCCESS, oldSchemaName,
                        view.getQualifiedObjectName(), view.getNamespaceQualifiedName(), view.getQualifiedObjectName(),
                        this.addInfo);
                break;
            }
        }

        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (null != objectBrowserModel) {
            objectBrowserModel.refreshObject(view.getParent());
            objectBrowserModel.refreshObject(oldSchema);
        }

        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(printMsg));
        if (null != dialog) {
            this.dialog.close();
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        if (null != this.dialog && this.dialog instanceof UserInputDialog) {
            ((UserInputDialog) dialog).enableButtons();
        } else if (null != this.dialog && this.dialog instanceof UserComboDialog) {
            ((UserComboDialog) dialog).setOkButtonEnabled(true);
        }
        displayErrMsgDialog(exception);
        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, view.getDatabase());
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        if (null != this.dialog && this.dialog instanceof UserInputDialog) {
            ((UserInputDialog) dialog).enableButtons();
        } else if (null != this.dialog && this.dialog instanceof UserComboDialog) {
            ((UserComboDialog) dialog).setOkButtonEnabled(true);
        }
        displayErrMsgDialog(exception);
    }

    private void displayErrMsgDialog(MPPDBIDEException exception) {
        String msgConst = null;

        switch (this.type) {
            case DROP_VIEW: {
                msgConst = IMessagesConstants.DROP_VIEW_FAILURE;
                break;
            }
            case RENAME_VIEW: {
                msgConst = IMessagesConstants.RENAME_VIEW_FAILURE;
                break;
            }
            default: {
                msgConst = IMessagesConstants.SET_VIEW_SCHEMA_FAILURE;
                break;
            }
        }

        String msg = MessageConfigLoader.getProperty(msgConst, view.getNameSpaceName() + '.' + view.getName(),
                MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage());

        if (null != this.dialog && this.dialog instanceof UserInputDialog) {
            ((UserInputDialog) dialog).printErrorMessage(msg, false);
        } else if (null != this.dialog && this.dialog instanceof UserComboDialog) {
            ((UserComboDialog) dialog).printErrorMessage(msg);
        } else {
            showErrorPopupMsg(exception, msg);
        }

    }

    /**
     * Show error popup msg.
     *
     * @param exception the e
     * @param msg the msg
     */
    private void showErrorPopupMsg(MPPDBIDEException exception, String msg) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_VIEW_FAILURE_TITLE), msg);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.DROP_VIEW_UNABLE_MSG, view.getNameSpaceName() + '.' + view.getName())));
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        if (this.conn != null) {
            this.conn.releaseConnection();
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        // Nothing to be done here.
    }

}
