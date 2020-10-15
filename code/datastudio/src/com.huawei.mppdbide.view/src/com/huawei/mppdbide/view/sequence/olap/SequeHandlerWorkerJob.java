/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.olap;

import com.huawei.mppdbide.presentation.SequenceDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.ObjectBrowser;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequeHandlerWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class SequeHandlerWorkerJob extends UIWorkerJob {
    private SequenceDataCore sequenceDataCore;
    private StatusMessage statMsg;
    private static final CharSequence POSITIONS = "Position:";
    private CreateSequenceDialoge dialog;

    /**
     * Instantiates a new seque handler worker job.
     *
     * @param statMsg the stat msg
     * @param name the name
     * @param sequenceDataCore the sequence data core
     * @param createSequenceDialoge the create sequence dialoge
     */
    protected SequeHandlerWorkerJob(StatusMessage statMsg, String name, SequenceDataCore sequenceDataCore,
            CreateSequenceDialoge createSequenceDialoge) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB);

        this.sequenceDataCore = sequenceDataCore;
        this.statMsg = statMsg;
        this.dialog = createSequenceDialoge;

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
        sequenceDataCore.executeCreateSequence();
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
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(sequenceDataCore.getSequenceMetadata().getNamespace());
        }
        if (null != dialog && !dialog.isDisposed()) {
            this.dialog.close();
        }
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.SEQUENCE_CREATED_SUCESSFULLY,
                        sequenceDataCore.getSequenceMetadata().getNamespace().getName(),
                        sequenceDataCore.getSequenceMetadata().getSequenceName())));

    }

    /**
     * On critical exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        handleException(exception);
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        handleException(exception);

    }

    /**
     * Canceling.
     */
    protected void canceling() {

        super.canceling();
        try {
            sequenceDataCore.cancelQuery();
        } catch (DatabaseCriticalException e) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SEQUENCE_ERROR,
                            sequenceDataCore.getSequenceMetadata().getNamespace().getName(),
                            sequenceDataCore.getSequenceMetadata().getSequenceName())));
        } catch (DatabaseOperationException e) {
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                    Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SEQUENCE_ERROR,
                            sequenceDataCore.getSequenceMetadata().getNamespace().getName(),
                            sequenceDataCore.getSequenceMetadata().getSequenceName())));
        }
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        sequenceDataCore.releaseConnection();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bttmStatusBar != null) {
            bttmStatusBar.hideStatusbar(this.statMsg);
        }
    }

    private void handleException(MPPDBIDEException exception) {
        String msg = exception.getServerMessage();
        if (null == msg) {
            msg = exception.getDBErrorMessage() == null ? exception.getMessage() : exception.getDBErrorMessage();
        }

        if (msg.contains(POSITIONS)) {
            msg = msg.split((String) POSITIONS)[0];
        }
        if (null != dialog && !dialog.isDisposed()) {
            dialog.setErrorMsg(msg);
            dialog.enableDisableButton(true);
        }
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(
                Message.getError(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_SEQUENCE_ERROR,
                        sequenceDataCore.getSequenceMetadata().getNamespace().getName(),
                        sequenceDataCore.getSequenceMetadata().getSequenceName())));
        MPPDBIDELoggerUtility.error("Failed to create sequence", exception);
    }

}
