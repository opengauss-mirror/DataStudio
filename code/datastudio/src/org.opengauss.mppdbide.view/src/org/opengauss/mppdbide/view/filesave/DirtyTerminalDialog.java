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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.autosave.IAutoSaveObject;
import org.opengauss.mppdbide.view.ui.terminal.SQLTerminal;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DirtyTerminalDialog.
 *
 * @since 3.0.0
 */
public class DirtyTerminalDialog extends Dialog {
    private Button selectAllButton;
    private Button deselectAllButton;

    private TreeViewer dirtyTerminalTreeViewer;

    private List<IAutoSaveObject> dirtyTerminals;

    private boolean saveAllSuccessFlag = true;

    /**
     * Instantiates a new dirty terminal dialog.
     *
     * @param shell the shell
     * @param dirtyTerminals the dirty terminals
     */
    public DirtyTerminalDialog(Shell shell, List<IAutoSaveObject> dirtyTerminals) {
        super(shell);
        this.dirtyTerminals = dirtyTerminals;
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

        Label label = new Label(mainComposite, SWT.NONE);
        label.setText(MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SELECTION_MESSAGE));

        dirtyTerminalTreeViewer = new TreeViewer(mainComposite, SWT.SINGLE | SWT.CHECK | SWT.BORDER);

        GridData treeGridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        treeGridData.widthHint = 520;
        treeGridData.heightHint = 248;
        dirtyTerminalTreeViewer.getTree().setLayoutData(treeGridData);

        dirtyTerminalTreeViewer.setLabelProvider(new DirtyTerminalTreeViewerLabelProvider());
        dirtyTerminalTreeViewer.setContentProvider(new DirtyTerminalTreeViewerContentProvider());
        dirtyTerminalTreeViewer.setInput(this.dirtyTerminals.toArray());

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
        newShellWindow.setText(MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_TITLE));
        newShellWindow.setSize(600, 398);
        newShellWindow.setImage(IconUtility.getIconImage(IiconPath.ICO_SQL_TERMINAL, this.getClass()));
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        selectAllButton = createButton(parent, -1,
                MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_SELECT_ALL_BUTTON), false);
        selectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Arrays.asList(dirtyTerminalTreeViewer.getTree().getItems()).stream()
                        .forEach(treeItem -> treeItem.setChecked(true));
            }
        });

        deselectAllButton = createButton(parent, -2,
                MessageConfigLoader.getProperty(IMessagesConstants.DIRTY_TERMINAL_DIALOG_DESELECT_ALL_BUTTON), false);
        deselectAllButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Arrays.asList(dirtyTerminalTreeViewer.getTree().getItems()).stream()
                        .forEach(treeItem -> treeItem.setChecked(false));
            }
        });

        createButton(parent, OK, MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK), false);
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        this.getShell().setVisible(false);

        SaveReloadSQLQueries saveReloadSQLQueries = new SaveReloadSQLQueries();

        Arrays.asList(this.dirtyTerminalTreeViewer.getTree().getItems()).stream().forEach(treeItem -> {
            if (treeItem.getChecked()) {
                if (!saveReloadSQLQueries.saveToExistFile((SQLTerminal) treeItem.getData())) {
                    this.saveAllSuccessFlag = false;
                }
            }
        });

        close();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DirtyTerminalTreeViewerContentProvider.
     */
    private static class DirtyTerminalTreeViewerContentProvider implements ITreeContentProvider {

        @Override
        public Object[] getChildren(Object parentElement) {
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
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
            return (Object[]) inputElement;
        }

    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DirtyTerminalTreeViewerLabelProvider.
     */
    private static class DirtyTerminalTreeViewerLabelProvider extends LabelProvider {

        @Override
        public String getText(Object element) {
            SQLTerminal sqlTerminal = (SQLTerminal) element;
            return sqlTerminal.getPartLabel() + " [" + sqlTerminal.getFilePath() + "]";
        }
    }

    /**
     * Checks if is save all success flag.
     *
     * @return true, if is save all success flag
     */
    public boolean isSaveAllSuccessFlag() {
        return saveAllSuccessFlag;
    }

    /**
     * Sets the save all success flag.
     *
     * @param saveAllSuccessFlag the new save all success flag
     */
    public void setSaveAllSuccessFlag(boolean saveAllSuccessFlag) {
        this.saveAllSuccessFlag = saveAllSuccessFlag;
    }

}
