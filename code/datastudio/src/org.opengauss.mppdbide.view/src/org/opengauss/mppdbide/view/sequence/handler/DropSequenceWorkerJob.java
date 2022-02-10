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

package org.opengauss.mppdbide.view.sequence.handler;

import org.opengauss.mppdbide.bl.serverdatacache.ISequenceMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.connection.AbstractModalLessWindowOperationUIWokerJob;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class DropSequenceWorkerJob.
 *
 * @since 3.0.0
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
