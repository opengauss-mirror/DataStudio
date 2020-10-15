/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.view.handler;

import org.eclipse.jface.dialogs.Dialog;

import com.huawei.mppdbide.bl.serverdatacache.IViewMetaData;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import com.huawei.mppdbide.view.ui.connectiondialog.UserComboDialog;
import com.huawei.mppdbide.view.ui.connectiondialog.UserInputDialog;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewWorkerJob extends UIWorkerJob {

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum VIEWOPTYPE.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
