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

package org.opengauss.mppdbide.view.filesave;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.Server;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionOptionsDialog.
 *
 * @since 3.0.0
 */
public class ConnectionOptionsDialog extends Dialog {

    private TreeViewer connOptionsTreeViewer;
    private Button finishButton;

    /**
     * Instantiates a new connection options dialog.
     *
     * @param shell the shell
     */
    public ConnectionOptionsDialog(Shell shell) {
        super(shell);
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite mainComposite = (Composite) super.createDialogArea(parent);

        ObjectBrowser objBrowser = UIElement.getInstance().getObjectBrowserModel();
        TreeItem[] treeItems = null != objBrowser ? objBrowser.getTreeViewer().getTree().getItems() : null;

        connOptionsTreeViewer = new TreeViewer(mainComposite, SWT.SINGLE);

        GridData treeGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        treeGridData.widthHint = 383;
        treeGridData.heightHint = 398;
        connOptionsTreeViewer.getTree().setLayoutData(treeGridData);

        connOptionsTreeViewer.setLabelProvider(new ConnectionOptionsTreeViewerLabelProvider());
        connOptionsTreeViewer.setContentProvider(new ConnectionOptionsTreeViewerContentProvider());
        connOptionsTreeViewer.setInput(treeItems);

        connOptionsTreeViewer.getTree().addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (Server.class.equals(event.item.getData().getClass())) {
                    finishButton.setEnabled(false);
                } else {
                    finishButton.setEnabled(true);
                }
            }
        });

        return mainComposite;
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_OPTION_DIALOG_TITLE));
        newShellWindow.setSize(428, 520);
        newShellWindow.setImage(IconUtility.getIconImage(IiconPath.ICON_NEW_TERMINAL_CONNECTION, this.getClass()));
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, CANCEL, MessageConfigLoader.getProperty(IMessagesConstants.BTN_CANCEL), false);

        finishButton = createButton(parent, OK, MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK), false);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        SQLTerminal sqlTerminal = new SQLTerminal();
        Object element = ((TreeSelection) connOptionsTreeViewer.getSelection()).getFirstElement();
        if (null != element) {
            if (element instanceof Database) {
                sqlTerminal = UIElement.getInstance().createNewTerminal((Database) element);
            }
        }

        if (sqlTerminal != null) {
            sqlTerminal.setOpenSqlFlag(true);

            close();

            SaveReloadSQLQueries saveReloadSQLQueries = new SaveReloadSQLQueries();
            saveReloadSQLQueries.openSQLFile(sqlTerminal);
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ConnectionOptionsTreeViewerContentProvider.
     */
    private static class ConnectionOptionsTreeViewerContentProvider implements ITreeContentProvider {
        @Override
        public Object[] getChildren(Object parentElement) {
            if (Server.class.equals(parentElement.getClass())) {
                List<Database> connectedDBList = new ArrayList<>();
                ((Server) parentElement).getAllDatabases().stream().forEach(db -> {
                    if (db.isConnected()) {
                        connectedDBList.add(db);
                    }
                });
                return connectedDBList.toArray();
            }
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            if (Server.class.equals(element.getClass())) {
                return true;
            }
            return false;
        }

        @Override
        public void dispose() {

        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

        }

        @Override
        public Object[] getElements(Object inputElement) {
            return Arrays.asList((TreeItem[]) inputElement).stream().map(treeItem -> treeItem.getData()).toArray();
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ConnectionOptionsTreeViewerLabelProvider.
     */
    private static class ConnectionOptionsTreeViewerLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            if (element instanceof Server) {
                return ((Server) element).getDisplayName();
            }
            if (element instanceof Database) {
                Database db = (Database) element;
                return db.isConnected() ? db.getName() : null;
            }
            return null;
        }
    }

}
