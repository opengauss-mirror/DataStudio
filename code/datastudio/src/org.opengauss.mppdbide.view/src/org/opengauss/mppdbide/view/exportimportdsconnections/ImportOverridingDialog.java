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

package org.opengauss.mppdbide.view.exportimportdsconnections;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileCore.MatchedConnectionProfiles;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.ImportConnectionProfileManager;
import org.opengauss.mppdbide.presentation.exportimportdsconnectionprofiles.OverridingOptions;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportOverridingDialog.
 *
 * @since 3.0.0
 */
public class ImportOverridingDialog extends Dialog {

    private Button conflictsButton;
    private Button dontCopyButton;
    private Button copyAndKeepBothBtn;
    private Button replaceButton;
    private boolean isContinue = true;
    private ImportConnectionProfileManager manager;
    private MatchedConnectionProfiles matchedProfiles;

    /**
     * Instantiates a new import overriding dialog.
     *
     * @param parentShell the parent shell
     * @param manager the manager
     * @param matchedProfiles the matched profiles
     */
    public ImportOverridingDialog(Shell parentShell, ImportConnectionProfileManager manager,
            MatchedConnectionProfiles matchedProfiles) {
        super(parentShell);
        this.manager = manager;
        this.matchedProfiles = matchedProfiles;
    }

    /**
     * Configure shell.
     *
     * @param importConnectionProfileShell the import connection profile shell
     */
    @Override
    protected void configureShell(Shell importConnectionProfileShell) {
        importConnectionProfileShell
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONNECTIONS_PROFILE_DIALOG_HEADER));
        super.configureShell(importConnectionProfileShell);
        importConnectionProfileShell
                .setImage(IconUtility.getIconImage(IiconPath.IMPORT_CONN_PROFILES, this.getClass()));
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite tableNameComposite = (Composite) super.createDialogArea(parent);
        GridLayout tableInfoLayout = new GridLayout();
        tableInfoLayout.numColumns = 2;
        tableInfoLayout.makeColumnsEqualWidth = false;
        tableInfoLayout.horizontalSpacing = 20;

        GridData tableInfogridData = new GridData();
        tableInfogridData.grabExcessHorizontalSpace = true;
        tableInfogridData.horizontalAlignment = GridData.FILL;
        tableInfogridData.verticalAlignment = GridData.FILL;
        tableInfogridData.horizontalIndent = 5;
        tableInfogridData.verticalIndent = 10;
        tableInfogridData.minimumWidth = 200;

        tableNameComposite.setLayout(tableInfoLayout);
        tableNameComposite.setLayoutData(tableInfogridData);

        Label connectionName = new Label(tableNameComposite, SWT.NONE);
        connectionName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_NAME));

        createConnNameArea(tableNameComposite);

        createRelpaceArea(tableNameComposite);

        createCopyAndKeepBothArea(tableNameComposite);

        createDontCopyArea(tableNameComposite);

        createConflictArea(tableNameComposite);

        return tableNameComposite;
    }

    private void createConnNameArea(Composite tableNameComposite) {
        Label connectionNameValue = new Label(tableNameComposite, SWT.NONE);
        GridData tableNameTextGridData = new GridData();
        tableNameTextGridData.widthHint = 200;
        connectionNameValue.setLayoutData(tableNameTextGridData);
        connectionNameValue.setEnabled(true);
        connectionNameValue.setText(matchedProfiles.getSourceProfile().getConectionName());
    }

    private void createConflictArea(Composite tableNameComposite) {
        Label conflicts = new Label(tableNameComposite, SWT.NONE);
        conflicts.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONN_PROFILE_CONFLICTS_LBL));

        conflictsButton = new Button(tableNameComposite, SWT.CHECK);
        GridData conflictsGridData = new GridData();
        conflictsGridData.widthHint = 200;
        conflictsButton.setLayoutData(conflictsGridData);
        conflictsButton.setSelection(false);
        conflictsButton.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                isContinue = !conflictsButton.getSelection();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
    }

    private void createDontCopyArea(Composite tableNameComposite) {
        Label dontCopy = new Label(tableNameComposite, SWT.NONE);
        dontCopy.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONN_PROFILE_DONT_CPY_LBL));

        dontCopyButton = new Button(tableNameComposite, SWT.RADIO);
        GridData dontCopyButtonGridData = new GridData();
        dontCopyButtonGridData.widthHint = 200;
        dontCopyButton.setLayoutData(dontCopyButtonGridData);
        dontCopyButton.setSelection(false);
    }

    private void createCopyAndKeepBothArea(Composite tableNameComposite) {
        Label copyAndKeepBoth = new Label(tableNameComposite, SWT.NONE);
        copyAndKeepBoth
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONN_PROFILE_KEEP_BOTH_FILES_LBL));

        copyAndKeepBothBtn = new Button(tableNameComposite, SWT.RADIO);
        GridData copyAndKeepBothGridData = new GridData();
        copyAndKeepBothGridData.widthHint = 200;
        copyAndKeepBothBtn.setLayoutData(copyAndKeepBothGridData);
        copyAndKeepBothBtn.setSelection(false);
    }

    private void createRelpaceArea(Composite tableNameComposite) {
        Label replace = new Label(tableNameComposite, SWT.NONE);
        replace.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_CONN_PROFILE_REPLACE_LABEL));

        replaceButton = new Button(tableNameComposite, SWT.RADIO);
        GridData replaceGridData = new GridData();
        replaceGridData.widthHint = 200;
        replaceButton.setLayoutData(replaceGridData);
        replaceButton.setSelection(true);
    }

    /**
     * Checks if is continue operation.
     *
     * @return true, if is continue operation
     */
    public boolean isContinueOperation() {
        return this.isContinue;
    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        OverridingOptions option = null;
        if (copyAndKeepBothBtn.getSelection()) {
            option = OverridingOptions.COPYANDKEEPBOTH;
        } else if (dontCopyButton.getSelection()) {
            option = OverridingOptions.DONTCOPY;
        } else {
            option = OverridingOptions.REPLACE;
        }
        if (!isContinue) {
            manager.handleAllProfilesWithConflicts(option);
        } else {
            manager.addProfilesToBeOverriden(matchedProfiles, option);
        }
        super.okPressed();
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param prnt the prnt
     */
    @Override
    protected void createButtonsForButtonBar(Composite prnt) {
        String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        createButton(prnt, IDialogConstants.OK_ID, okLbl, true);

        createButton(prnt, IDialogConstants.CANCEL_ID, cancelLbl, false);

    }

}
