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

package com.huawei.mppdbide.view.ui.connectiondialog;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.connection.IConnectionProfileManager;
import com.huawei.mppdbide.bl.serverdatacache.ConnectionProfileId;
import com.huawei.mppdbide.bl.serverdatacache.DBConnProfCache;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.handler.OpenHelp;
import com.huawei.mppdbide.view.ui.ObjectBrowserFilterUtility;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.consts.UIConstants;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionDialog.
 *
 * @since 3.0.0
 */
public class ConnectionDialog extends Dialog {

    /**
     * The progress bar.
     */
    protected ProgressBar progressBar;

    /**
     * The gauss connection name.
     */
    protected Text gaussConnectionName;

    /**
     * The gauss host addr.
     */
    protected Text gaussHostAddr;

    /**
     * The gauss host port.
     */
    protected Text gaussHostPort;

    /**
     * The gauss db name.
     */
    protected Text gaussDbName;

    /**
     * The gauss user name.
     */
    protected Text gaussUserName;

    /**
     * The gauss prd.
     */
    protected Text gaussPrd;

    /**
     * The gauss SSL enable button.
     */
    protected Button gaussSSLEnableButton;

    /**
     * The save pswd options.
     */
    protected Combo savePswdOptions;

    /**
     * The ssl cert browse btn.Buttons for Client SSL Certificate , SSL key and
     * root Certificate
     */
    protected Button sslCertBrowseBtn;

    /**
     * The ssl key browse btn.
     */
    protected Button sslKeyBrowseBtn;

    /**
     * The root cert browse btn.
     */
    protected Button rootCertBrowseBtn;

    /**
     * The ssl mode options.
     */
    protected Combo sslModeOptions;

    /**
     * The cl SSL cert file path text.Text for Client SSL Certificate , SSL key
     * and root Certificate
     */
    protected Text clSSLCertFilePathText;

    /**
     * The cl SSL key file path text.
     */
    protected Text clSSLKeyFilePathText;

    /**
     * The root cert file path text.
     */
    protected Text rootCertFilePathText;

    /**
     * The schema include.Text for advanced options
     */
    protected Text schemaInclude;

    /**
     * The schema exclude.
     */
    protected Text schemaExclude;

    /**
     * The load limit.
     */
    protected Text loadLimit;

    /**
     * The enable privilege.
     */
    protected Button enablePrivilege;

    /**
     * The disable privilege.
     */
    protected Button disablePrivilege;

    /**
     * The ok button. Dialog Buttons
     */
    protected Button okButton;

    /**
     * The close button.
     */
    protected Button closeButton;

    /**
     * The clear button.
     */
    protected Button clearButton;
    /**
     * The viewer.
     */
    protected TableViewer viewer;

    /**
     * The btn remove.
     */
    protected Button btnRemove;
    /**
     * The profile manager.
     */
    protected IConnectionProfileManager profileManager = null;

    /**
     * The ssl modes.
     */
    protected String[] sslModes = new String[] {"verify-full", "require", "verify-ca", "allow"};

    /**
     * The Constant DEFAULT_LOAD_LIMIT.
     */
    protected static final String DEFAULT_LOAD_LIMIT = "30000";
    /**
     *  the ok id
     */
    protected static final int OK_ID = UIConstants.OK_ID;

    /**
     * The job.
     */
    protected Job job;

    /**
     * The get driver combotext.
     */
    protected String getDriverCombotext;

    /**
     * The is exception occured.
     */
    protected boolean isExceptionOccured;

    /**
     * Checks if is exception occured.
     *
     * @return true, if is exception occured
     */
    public boolean isExceptionOccured() {
        return isExceptionOccured;
    }

    /**
     * Sets the exception occured.
     *
     * @param isException the new exception occured
     */
    public void setExceptionOccured(boolean isException) {
        this.isExceptionOccured = isException;
    }

    private static final float DOUBLE_VALUE = 2.0f;
    /**
     * The lbl info.
     */
    protected Label lblInfo;

    /**
     * The lbl sub info.
     */
    protected Label lblSubInfo;

    /**
     * The form composite.
     */
    protected Composite formComposite;

    /**
     * The ok label.
     */
    protected final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK)
            + "     ";

    /**
     * The clear label.
     */
    protected final String clearLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_CONSOLE)
            + "     ";

    /**
     * The close label.
     */
    protected final String closeLabel = "     "
            + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CLOSE) + "     ";

    /**
     * The cancel label.
     */
    protected final String cancelLabel = "     "
            + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC) + "     ";

    /**
     * The db type combo.
     */
    protected Combo dbTypeCombo;

    /**
     * The ssl prd.
     */
    protected Text sslPrd;

    /**
     * The lbl DB name.
     */
    protected Label lblDBName;

    /**
     * The container.
     */
    protected Composite container;

    public ConnectionDialog(Shell parent) {
        super(parent);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_CONNECTED, getClass()));
    }

    /**
     * Gets the profile manager.
     *
     * @return the profile manager
     */
    public IConnectionProfileManager getProfileManager() {
        return profileManager;
    }

    /**
     * Gets the viewer.
     *
     * @return the viewer
     */
    public TableViewer getViewer() {
        return viewer;
    }

    /**
     * Check SSL populate.method to check SSL is off when populatation for saved
     * deatils upon
     *
     * @param info the info
     */
    public void checkSSLPopulate(final IServerConnectionInfo info) {
        boolean sslCheck = info.isSSLEnabled();

        gaussSSLEnableButton.setSelection(sslCheck);

        if (!sslCheck) {
            sslModeOptions.setEnabled(false);
            sslCertBrowseBtn.setEnabled(false);
            sslKeyBrowseBtn.setEnabled(false);
            rootCertBrowseBtn.setEnabled(false);

            clSSLCertFilePathText.setEnabled(false);
            clSSLCertFilePathText.setText("");

            clSSLKeyFilePathText.setEnabled(false);
            clSSLKeyFilePathText.setText("");

            rootCertFilePathText.setEnabled(false);
            rootCertFilePathText.setText("");

            sslPrd.setEnabled(false);
            sslPrd.setText("");
        } else {
            sslCertBrowseBtn.setEnabled(true);
            clSSLCertFilePathText.setEnabled(true);

            sslKeyBrowseBtn.setEnabled(true);
            clSSLKeyFilePathText.setEnabled(true);

            rootCertBrowseBtn.setEnabled(true);
            rootCertFilePathText.setEnabled(true);

            clSSLCertFilePathText.setText(checkConnectionProperties(info.getClientSSLCertificate()));
            clSSLKeyFilePathText.setText(checkConnectionProperties(info.getClientSSLKey()));
            rootCertFilePathText.setText(checkConnectionProperties(info.getRootCertificate()));
            sslModeOptions.setEnabled(true);

            sslModeOptions.setText(info.getSSLMode());

            sslPrd.setEnabled(true);
            sslPrd.setText("");
        }
    }

    /**
     * Check connection properties.
     *
     * @param arg the arg
     * @return the string
     */
    public String checkConnectionProperties(String arg) {
        if (arg == null) {
            arg = "";
            return arg;
        } else {
            return arg;
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TextLengthVerifyListner.
     */
    protected static final class TextLengthVerifyListner implements VerifyListener {
        @Override
        public void verifyText(VerifyEvent event) {
            String textStr = ((Text) event.widget).getText() + event.text;
            try {
                if (textStr.length() > 5400) {
                    event.doit = false;
                }
            } catch (NumberFormatException numberFormatException) {
                event.doit = false;
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PortKeyListner.
     */
    protected static final class PortKeyListner implements KeyListener {
        protected PortKeyListner() {
        }

        @Override
        public void keyPressed(KeyEvent event) {
            String eChar = event.character + "";
            try {
                // Validates the input is long value only.
                if (event.keyCode != 8 && event.keyCode != 127 && event.keyCode != 16777219 && event.keyCode != 16777220
                        && event.character != '.' && Long.parseLong(eChar) < 0) {
                    event.doit = true;
                }
            } catch (final NumberFormatException numberFormatException) {
                event.doit = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent event) {
        }
    }

    /**
     * The listener interface for receiving linePaint events. The class that is
     * interested in processing a linePaint event implements this interface, and
     * the object created with that class is registered with a component using
     * the component's <code>addLinePaintListener<code> method. When the
     * linePaint event occurs, that object's appropriate method is invoked.
     *
     * LinePaintEvent
     */
    private static class LinePaintListener implements PaintListener {

        /**
         * Paint control.
         *
         * @param event the event
         */
        public void paintControl(PaintEvent event) {
            Canvas canvas = (Canvas) event.widget;
            Point pnt = canvas.getSize();
            int halfY = Math.round(pnt.y / DOUBLE_VALUE);

            event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLACK));
            event.gc.setLineWidth(1);
            event.gc.drawLine(0, halfY, pnt.x, halfY);
        }
    }

    /**
     * The listener interface for receiving footerMouse events. The class that
     * is interested in processing a footerMouse event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addFooterMouseListener<code>
     * method. When the footerMouse event occurs, that object's appropriate
     * method is invoked.
     *
     * FooterMouseEvent
     */
    protected static class FooterMouseListener implements MouseListener {
        @Override
        public void mouseUp(MouseEvent event) {
            new OpenHelp().execute();
        }

        @Override
        public void mouseDown(MouseEvent event) {
            // No need to do anything
        }

        @Override
        public void mouseDoubleClick(MouseEvent event) {
            // No need to do anything
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class HandleDeleteProfile.
     */
    protected final class HandleDeleteProfile implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent event) {
            ISelection selection = viewer.getSelection();
            if (selection.isEmpty()) {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_CONN_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.SELECT_CONN_TO_REMOVE));
                return;
            }

            Object object = ((IStructuredSelection) selection).getFirstElement();
            if (object instanceof String[]) {
                String[] item = (String[]) object;
                IServerConnectionInfo info = profileManager.getProfile(item[0]);
                String conectionName = "";
                if (null != info) {
                    conectionName = info.getConectionName();
                }
                Server server = DBConnProfCache.getInstance().getServerByName(conectionName);
                if (server != null) {
                    if (displayRemoveServerMsg(server)) {
                        return;
                    }
                    handleServerClose(object, server);
                } else {
                    int returnVal = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_CONN_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_CONN_MSG));
                    if (returnVal != 0) {
                        return;
                    }
                    viewer.remove(object);
                }
                try {
                    if (null != info) {
                        profileManager.deleteProfile(info);
                    }
                } catch (DatabaseOperationException e1) {
                    MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                            MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_CONNPROF_TITLE),
                            MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_CONN_ERROR, e1.getMessage()));
                }
            }
        }

        private boolean displayRemoveServerMsg(Server server) {
            return OK_ID != MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_SEVER),
                    MessageConfigLoader.getProperty(IMessagesConstants.REMOVE_SERVER_CONFIRMATION,
                            MPPDBIDEConstants.LINE_SEPARATOR, server.getName()));
        }

        private void handleServerClose(Object object, Server server) {
            viewer.remove(object);
            if (UIDisplayFactoryProvider.getUIDisplayStateIf().cleanupOnServerRemoval(server)) {
                UIDisplayFactoryProvider.getUIDisplayStateIf().deleteSecurityFolderFromProfile(server);
                server.close();
                ObjectBrowserFilterUtility.getInstance().removeRefreshedServerFromList(server.getName());
                DBConnProfCache.getInstance().removeServer(server.getId());
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(
                        MessageConfigLoader.getProperty(IMessagesConstants.SERVER_REMOVED, server.getName())));
            }

            UIElement.getInstance().refreshObjectBrowserPart();
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent event) {
        }

    }

}
