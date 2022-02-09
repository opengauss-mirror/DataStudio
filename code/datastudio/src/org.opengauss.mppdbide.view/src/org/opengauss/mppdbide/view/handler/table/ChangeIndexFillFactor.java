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

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

import org.opengauss.mppdbide.bl.serverdatacache.IndexMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.PartitionTable;
import org.opengauss.mppdbide.bl.serverdatacache.TableOrientation;
import org.opengauss.mppdbide.presentation.TerminalExecutionConnectionInfra;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.Message;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.messaging.StatusMessageList;
import org.opengauss.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import org.opengauss.mppdbide.view.handler.IHandlerUtilities;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.connectiondialog.PromptPrdGetConnection;
import org.opengauss.mppdbide.view.ui.connectiondialog.UserInputDialog;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;
import org.opengauss.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ChangeIndexFillFactor.
 *
 * @since 3.0.0
 */
public class ChangeIndexFillFactor {
    private StatusMessage statusMessage;

    /**
     * Execute.
     *
     * @param shell the shell
     */
    @Execute
    public void execute(final Shell shell) {
        Object obj = IHandlerUtilities.getObjectBrowserSelectedObject();
        if (obj instanceof IndexMetaData) {
            final IndexMetaData idx = (IndexMetaData) obj;

            if (idx.getParent().getOrientation() == TableOrientation.COLUMN) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.CONSTRAINT_INDEXES_HANDLER_NOT_SUPPORTED_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FOR_COLUMN_ORIENTATION_TBL));
            } else {
                UserInputDialog changeTablespaceDialog = new ChangeIndexFillFactorInner(shell, idx, idx);

                changeTablespaceDialog.open();
            }
        }

    }

    /**
     * Can execute.
     *
     * @return true, if successful
     */
    @CanExecute
    public boolean canExecute() {
        IndexMetaData idxMetaData = IHandlerUtilities.getSelectedIndex();
        // if accessmethod is "gin" or "psort" fill factor change is not supported
        if (null != idxMetaData && (idxMetaData.getAccessMethId() != 2742 && idxMetaData.getAccessMethId() != 4039)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ChangeIndexFillFactorInner.
     */
    private final class ChangeIndexFillFactorInner extends UserInputDialog {

        /**
         * Instantiates a new change index fill factor inner.
         *
         * @param parent the parent
         * @param serverObject the server object
         * @param idx the idx
         */
        private ChangeIndexFillFactorInner(Shell parent, Object serverObject, IndexMetaData idx) {
            super(parent, serverObject);
        }

        @Override
        public void performOkOperation() {

            final BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
            IndexMetaData indx = (IndexMetaData) getObject();
            StatusMessage statMsg = new StatusMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.STATUS_MSG_CHANGE_FILLFACTOR));
            String indxName = indx.getName();
            String usrInpt = getUserInput();

            if ("".equals(usrInpt)) {
                printErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_NEW, indx.getName()),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            }

            int value = Integer.parseInt(usrInpt);
            if (value < 10) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_MIN),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                enableButtons();
                return;
            } else if (value > 100) {
                printErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_MAX),
                        false);
                if (bottomStatusBar != null) {
                    bottomStatusBar.hideStatusbar(getStatusMessage());
                }
                return;
            }

            printMessage(MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_FOR, indxName),
                    true);

            ChangeIndexFillFactorWorker changeIndexFillFactorWorker = new ChangeIndexFillFactorWorker(indx, usrInpt,
                    this, statMsg);
            setStatusMessage(statMsg);
            setStatusMessage(statMsg);
            StatusMessageList.getInstance().push(statMsg);
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            changeIndexFillFactorWorker.schedule();
        }

        @Override
        protected Object userInputControl(Composite comp) {
            final Spinner fillFactor = new Spinner(comp, SWT.BORDER);
            fillFactor.setSelection(((IndexMetaData) getObject()).getFillFactor());
            fillFactor.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            fillFactor.setMinimum(0);
            fillFactor.setMaximum(100);
            fillFactor.addListener(SWT.Verify, new Listener() {
                @Override
                public void handleEvent(Event e) {

                    String initialValue = ((Spinner) e.widget).getText();

                    final String finalValue = initialValue.substring(0, e.start) + e.text
                            + initialValue.substring(e.end);
                    try {
                        printErrorMessage("", false);
                        if (!finalValue.isEmpty()) {
                            if (Integer.parseInt(finalValue) < 10) {
                                printErrorMessage(
                                        MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_MIN),
                                        false);
                                disableButtons();
                            } else if (Integer.parseInt(finalValue) > 100) {
                                e.doit = false;
                                disableButtons();
                            } else {
                                enableButtons();
                            }
                        }

                    } catch (NumberFormatException ex) {
                        e.doit = false;
                        disableButtons();
                    }

                }
            });
            fillFactor.forceFocus();

            final ControlDecoration deco = new ControlDecoration(fillFactor, SWT.TOP | SWT.LEFT);

            // use an existing image
            Image image = IconUtility.getIconImage(IiconPath.MANDATORY_FIELD, this.getClass());

            // set description and image
            deco.setImage(image);
            fillFactor.addModifyListener(new ModifyListener() {

                @Override
                public void modifyText(ModifyEvent e) {
                    if (fillFactor.getText().isEmpty()) {
                        disableButtons();
                    }

                }
            });
            // always show decoration
            deco.setShowOnlyOnFocus(false);

            return fillFactor;
        }

        @Override
        protected String getUserInput() {
            int data = ((Spinner) inputControl).getSelection();
            return Integer.toString(data);
        }

        @Override
        protected String getWindowTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_TITLE);
        }

        @Override
        protected String getHeader() {

            return MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_SELECT,
                    ((IndexMetaData) getObject()).getName());
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            return;
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            return;
        }

        @Override
        protected Image getWindowImage() {
            return IconUtility.getIconImage(IiconPath.ICO_INDEX, this.getClass());
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            return;
        }

        @Override
        public void onPresetupFailureUIAction(MPPDBIDEException exception) {
            return;
        }
        
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ChangeIndexFillFactorWorker.
     */
    private static final class ChangeIndexFillFactorWorker extends UIWorkerJob {

        private IndexMetaData idxMetaData;
        private String oldname;
        private String newname;
        private UserInputDialog dialog;
        private StatusMessage statusMsg;
        private TerminalExecutionConnectionInfra conn;

        /**
         * Instantiates a new change index fill factor worker.
         *
         * @param idxMetaData the idx meta data
         * @param nwname the nwname
         * @param dialog the dialog
         * @param statusMessage the status message
         */
        private ChangeIndexFillFactorWorker(IndexMetaData idxMetaData, String nwname, UserInputDialog dialog,
                StatusMessage statusMessage) {
            super("Change Index Fill Factor", null);
            this.idxMetaData = idxMetaData;
            this.dialog = dialog;
            this.oldname = idxMetaData.getName();
            this.newname = nwname;
            this.statusMsg = statusMessage;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            conn = PromptPrdGetConnection.getConnection(idxMetaData.getDatabase());
            idxMetaData.changeFillFactor(Integer.parseInt(newname), conn.getConnection());

            MPPDBIDELoggerUtility.info("Changing index, fill factor succesfully ");
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            dialog.close();
            String message = MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_CHANGED,
                    newname);
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
            if (objectBrowserModel != null) {
                objectBrowserModel.refreshObject(idxMetaData.getParent());
            }
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException e) {
            dialog.printErrorMessage(
                    MessageConfigLoader.getProperty(IMessagesConstants.CHANGE_INDEX_FILLFACTOR_CONN_ERROR, oldname,
                            MPPDBIDEConstants.LINE_SEPARATOR, e.getDBErrorMessage()),
                    false);
            dialog.enableButtons();
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException e) {
            String msg = e.getServerMessage();
            if (null == msg) {
                msg = e.getDBErrorMessage();
            }

            dialog.printErrorMessage(msg, false);
            dialog.enableButtons();
        }

        @Override
        public void finalCleanupUI() {
            final BottomStatusBar btmStatusBar = UIElement.getInstance().getProgressBarOnTop();
            if (btmStatusBar != null) {
                btmStatusBar.hideStatusbar(this.statusMsg);
            }
        }

        @Override
        public void finalCleanup() {
            if (this.conn != null) {
                this.conn.releaseConnection();
            }
        }
    }

    /**
     * Gets the status message.
     *
     * @return the status message
     */
    public StatusMessage getStatusMessage() {
        return statusMessage;
    }

    /**
     * Sets the status message.
     *
     * @param message the new status message
     */
    public void setStatusMessage(StatusMessage message) {
        this.statusMessage = message;
    }
}
