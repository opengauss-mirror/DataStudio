/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.sequence.handler;

import com.huawei.mppdbide.bl.serverdatacache.ISequenceMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
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
import com.huawei.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceWorkerJob.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public final class DropSequenceWorkerJob extends AbstractModalLessWindowOperationUIWokerJob {
    private Boolean isCascade;
    private StatusMessage statusMsg;
    private String printMsg;
    private ISequenceMetaData metaData;

    /**
     * Instantiates a new drop sequence worker job.
     *
     * @param name the name
     * @param metaData the meta data
     * @param isCascade the is cascade
     */
    public DropSequenceWorkerJob(String name, ServerObject metaData, boolean isCascade) {
        super(name, metaData, MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CONFIRM_TITLE),
                MPPDBIDEConstants.CANCELABLEJOB);

        this.metaData = (ISequenceMetaData) metaData;

        this.isCascade = isCascade;
        this.statusMsg = new StatusMessage("Drop sequence");
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
        metaData.dropSequence(super.conn, isCascade);

        String successmsg = isCascade ? IMessagesConstants.DROP_SEQUENCE_CASCADE_SUCCESS
                : IMessagesConstants.DROP_SEQUENCE_SUCCESS;
        printMsg = MessageConfigLoader.getProperty(successmsg, metaData.getSeqNameSpace().getQualifiedObjectName(),
                metaData.getQualifiedObjectName());

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
        super.additionalDoJobhandling();
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        displayErrMsgDialog(exception);
        ObjectBrowserStatusBarProvider.getStatusBar()
                .displayMessage(Message.getInfo(MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_ERROR,
                        metaData.getSeqNameSpace(), metaData.getSeqNameSpace())));
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the e
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        displayErrMsgDialog(exception);
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(MessageConfigLoader
                .getProperty(IMessagesConstants.DROP_SEQUENCE_ERROR, metaData.getObjectFullName(), "")));
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        final BottomStatusBar bttmStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bttmStatusBar != null) {
            bttmStatusBar.hideStatusbar(this.statusMsg);
        }
    }

    /**
     * Display err msg dialog.
     *
     * @param exception the e
     */
    private void displayErrMsgDialog(MPPDBIDEException exception) {

        String msg = MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_FAILURE,
                "" + metaData.getObjectFullName(), MPPDBIDEConstants.LINE_SEPARATOR, exception.getServerMessage());
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.DROP_SEQUENCE_CONFIRM_TITLE), msg);
    }

    /**
     * Gets the success msg for OB status bar.
     *
     * @return the success msg for OB status bar
     */
    @Override
    protected String getSuccessMsgForOBStatusBar() {
        return printMsg;
    }

    /**
     * Gets the object browser refresh item.
     *
     * @return the object browser refresh item
     */
    @Override
    protected ServerObject getObjectBrowserRefreshItem() {

        try {
            metaData.refreshSequence(super.conn);
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("Refreshing of Sequences failed", exception);
        }

        return null;

    }

}
