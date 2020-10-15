/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.userrole;

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.UserRoleManager;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.view.component.grid.CommitRecordEventData;
import com.huawei.mppdbide.view.functionchange.FunctionChangeNotifyDialog;
import com.huawei.mppdbide.view.handler.connection.DBOperationUIWorkerJob;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class UserRolePropertiesCommitUIWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class UserRolePropertiesCommitUIWorkerJob extends DBOperationUIWorkerJob {

    private CommitRecordEventData eventData;
    private IDSGridDataProvider dataProvider;
    private volatile DBConnection conn;
    private String statusBarMessage;
    private volatile Boolean executeFlag = false;

    /**
     * The pre UI setup.
     */
    boolean preUISetup;

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */

    @Override
    public boolean preUISetup(Object preHandlerObject) {
        preUISetup = super.preUISetup(preHandlerObject);
        if (!preUISetup) {
            this.eventData.getEventTable()
                    .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_CANCEL_PASSWORD, null));
        }
        return preUISetup;
    }

    /**
     * Instantiates a new user role properties commit UI worker job.
     *
     * @param name the name
     * @param family the family
     * @param eventData the event data
     * @param statusBarMessage the status bar message
     */
    public UserRolePropertiesCommitUIWorkerJob(String name, Object family, CommitRecordEventData eventData,
            String statusBarMessage) {
        super(name, family);
        this.eventData = eventData;
        this.statusBarMessage = statusBarMessage;
        this.dataProvider = eventData.getDataProvider();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return this.dataProvider.getDatabse();
    }

    /**
     * Db conn operation.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void dbConnOperation() throws MPPDBIDEException {
        Database database = getDatabase();
        if (null != database) {
            this.conn = database.getConnectionManager().getFreeConnection();
        }
    }

    /**
     * Gets the status bar msg.
     *
     * @return the status bar msg
     */
    @Override
    public String getStatusBarMsg() {
        return this.statusBarMessage;
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
        List<String> previewSqls = ((DSObjectPropertiesGridDataProvider) this.dataProvider)
                .generateUserRolePropertyChangePreviewSql(this.conn);
        StringBuffer strBuf = new StringBuffer();
        previewSqls.stream().forEach(previewSQL -> strBuf.append(previewSQL).append(MPPDBIDEConstants.NEW_LINE_SIGN));
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                int feedback = new FunctionChangeNotifyDialog(Display.getDefault().getActiveShell(),
                        StringUtils.isEmpty(strBuf.toString()) ? IMessagesConstants.USER_ROLE_PREVIEW_SQL_CHANGE_NOTHING
                                : IMessagesConstants.USER_ROLE_PREVIEW_SQL_CONFIRM_MESSAGE,
                        IMessagesConstants.MPPDBIDE_DIA_BTN_CANC, strBuf.toString(),
                        IMessagesConstants.USER_ROLE_PREVIEW_SQL_CONFIRM, IMessagesConstants.MPPDBIDE_DIA_BTN_YES,
                        IMessagesConstants.SQL_PREVIEW).open();

                if (feedback == IDialogConstants.OK_ID) {
                    executeFlag = true;
                }
            }
        });
        if (executeFlag) {
            ((DSObjectPropertiesGridDataProvider) this.dataProvider).commitUserRoleProperty(this.conn, previewSqls);
        } else {
            this.eventData.getEventTable()
                    .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, dataProvider));
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
        if (this.executeFlag) {
            this.eventData.getEventTable()
                    .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, dataProvider));

            // refresh the user/role
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
                    if (null != objectBrowserModel) {
                        UserRole userRole = ((PropertiesUserRoleImpl) ((DSObjectPropertiesGridDataProvider) dataProvider)
                                .getObjectPropertyObject()).getUserRole();
                        UserRole freshUserRole = null;
                        try {
                            freshUserRole = UserRoleManager.fetchUserRoleDetailInfoByOid(userRole.getServer(), conn,
                                    userRole);
                        } catch (MPPDBIDEException exception) {
                            // don't affect functionality, no need to inform
                            // user, write a log is OK
                            MPPDBIDELoggerUtility.error("Failed to fetch user role details from server", exception);
                        }
                        if (freshUserRole != null) {
                            userRole.setRolCanLogin(freshUserRole.getRolCanLogin());
                            userRole.setName(freshUserRole.getName());
                        }

                        objectBrowserModel.refreshObject(userRole.getServer().getUserRoleObjectGroup());
                        MPart part = (MPart) UIElement.getInstance().getActivePart();
                        String title = (freshUserRole != null)
                                ? MessageFormat.format("{0}@{1}", freshUserRole.getName(),
                                        freshUserRole.getServer().getName())
                                : "";
                        part.setLabel(title);
                        part.setTooltip(title);
                    }
                }
            });
        }
    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        this.statusBarMessage = null;
        Database database = getDatabase();
        if (null != database) {
            database.getConnectionManager().releaseConnection(this.conn);
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        // when user presses the cancel button of the password dialog it comes
        // here then event to reset the status bar should not be triggered.
        if (preUISetup) {
            this.eventData.getEventTable()
                    .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
        }
    }

    /**
     * Log msgs.
     *
     * @param msgs the msgs
     */
    @Override
    public void logMsgs(String msgs) {
        MPPDBIDELoggerUtility.error(msgs);
    }

    /**
     * Log connection failure.
     *
     * @param failureMsg the failure msg
     */
    @Override
    public void logConnectionFailure(String failureMsg) {
        MPPDBIDELoggerUtility.error(failureMsg);
    }

    /**
     * On critical exception UI action.
     *
     * @param dbCriticalException the db critical exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException dbCriticalException) {
        exceptionEventCall(dbCriticalException);
    }

    /**
     * On operational exception UI action.
     *
     * @param dbOperationException the db operation exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException dbOperationException) {
        exceptionEventCall(dbOperationException);
    }

    /**
     * On MPPDBIDE exception UI action.
     *
     * @param mppDbException the mpp db exception
     */
    @Override
    public void onMPPDBIDEExceptionUIAction(MPPDBIDEException mppDbException) {
        exceptionEventCall(mppDbException);
    }

    /**
     * On exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onExceptionUIAction(Exception exception) {
        exceptionEventCall(exception);
    }

    /**
     * Exception event call.
     *
     * @param exception the exception
     */
    public void exceptionEventCall(Exception exception) {
        String message = null;
        if (exception instanceof MPPDBIDEException) {
            message = ((MPPDBIDEException) exception).getServerMessage();
        } else {
            message = exception.getMessage();
        }
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.ERR_USER_ROLE_ALTER_FAILURE, message));

        this.eventData.getEventTable()
                .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
    }
}
