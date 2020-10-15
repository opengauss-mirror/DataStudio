/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.functionchange;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.IDebugObject;
import com.huawei.mppdbide.bl.serverdatacache.ObjectChange;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.functionchange.ObjectChangeEvent.ButtonPressed;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ObjectChangeWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @param <T> the generic type
 * @since 17 May, 2019
 */
public class ObjectChangeWorker<T extends ObjectChange> extends UIWorkerJob {

    /**
     * The dbg obj.
     */
    IDebugObject dbgObj;

    /**
     * The editor.
     */
    PLSourceEditor editor;

    /**
     * The execute wrapper.
     */
    ExecuteWrapper executeWrapper;

    /**
     * The popup message.
     */
    String popupMessage;

    /**
     * The button text.
     */
    String buttonText;

    /**
     * The dialog.
     */
    FunctionChangeNotifyDialog dialog;

    /**
     * The result.
     */
    int result = -1;
    private ObjectChangeEvent event;
    private String serverCode;
    private boolean isChanged;
    private String latestInfo;
    private boolean restartJob;

    /**
     * Instantiates a new object change worker.
     *
     * @param name the name
     * @param family the family
     * @param debugObject the debug object
     * @param editor the editor
     * @param executeWrapper the execute wrapper
     * @param popupMessage the popup message
     * @param buttonText the button text
     */
    public ObjectChangeWorker(String name, Object family, IDebugObject debugObject, PLSourceEditor editor,
            ExecuteWrapper executeWrapper, String popupMessage, String buttonText) {
        super(name, family);
        this.dbgObj = debugObject;
        this.editor = editor;
        this.executeWrapper = executeWrapper;
        this.popupMessage = popupMessage;
        this.buttonText = buttonText;
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
        restartJob = false;
        event = new ObjectChangeEvent();
        event.setDbgObj(dbgObj);
        event.setEditor(editor);
        if (dbgObj != null) {
            latestInfo = dbgObj.getLatestInfo();
            serverCode = latestInfo;
            isChanged = dbgObj.isChanged(latestInfo);
        }
        return null;
    }

    /**
     * Execution of Handler Execute
     * 
     * @param event1 - Object Event
     */
    private void handleExecution(final ObjectChangeEvent event1) {
        try {
            executeWrapper.handleExecute(event1);
        } catch (DatabaseCriticalException e) {
            restartJob = UIDisplayFactoryProvider.getUIDisplayStateIf().handleCriticalExceptionForReconnect(dbgObj);
            if (!restartJob) {
                editor.setCompileInProgress(false);
                editor.setExecuteInProgress(false);
                editor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);
            }
        }
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        MPPDBIDELoggerUtility.info("Object Change Worker Successful");
        if (isChanged) {
            dialog = new FunctionChangeNotifyDialog(Display.getDefault().getActiveShell(), popupMessage, buttonText,
                    serverCode, IMessagesConstants.FUNCTN_CHANGE_TITLE, IMessagesConstants.FUNCTN_CHANGE_REFRESH,
                    IMessagesConstants.FUNCTN_CHANGE_PREVIEW);
            dialog.setSyntax(dbgObj.getDatabase() != null ? dbgObj.getDatabase().getSqlSyntax() : null);
            result = dialog.open();

            if (IDialogConstants.OK_ID == result) {
                dbgObj.handleChange(latestInfo);
                event.updateStatus(ButtonPressed.REFRESH);
                handleExecution(event);
            } else if (IDialogConstants.PROCEED_ID == result) {
                event.updateStatus(ButtonPressed.OVERWRITE);
                handleExecution(event);
            } else {
                editor.setCompileInProgress(false);
                editor.setExecuteInProgress(false);
                editor.getSourceEditorCore().getSourceViewer().getTextWidget().setEnabled(true);

            }
        } else {
            event.updateStatus(ButtonPressed.NOCHANGE);
            handleExecution(event);
        }
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(exception, dbgObj.getDatabase());
        handleException(exception, event);
    }

    /**
     * On operational exception UI action.
     *
     * @param e the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.error("Database operation occured while checking for function change..", exception);
        handleException(exception, event);
    }

    private void handleException(Exception exception, ObjectChangeEvent objEvent) {
        executeWrapper.handleException(exception, objEvent);

    }

    /**
     * Final cleanup.
     *
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    @Override
    public void finalCleanup() throws MPPDBIDEException {
        if (restartJob) {
            this.schedule();
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (restartJob) {
            UIDisplayFactoryProvider.getUIDisplayStateIf().resetConnectionOnCriticalError(dbgObj);

        }

    }

}