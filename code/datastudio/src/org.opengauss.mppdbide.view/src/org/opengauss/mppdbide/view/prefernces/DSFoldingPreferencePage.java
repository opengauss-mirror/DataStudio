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

package org.opengauss.mppdbide.view.prefernces;

import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.internal.WorkbenchPlugin;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: DSFoldingPreferencePage
 * 
 * @since 3.0.0
 */
@SuppressWarnings("restriction")
public class DSFoldingPreferencePage extends PreferencePage {
    private IPreferenceStore preferenceStore;
    private Button enableCodeFoldingBtn;
    private Button disableCodeFoldingBtn;
    private static final String SPACE = " ";
    private static final String TAB = "      ";

    /**
     * The Constant SQL_EDITOR_FOLDING.
     */
    public static final String SQL_EDITOR_FOLDING = "sqlterminal.folding";

    /**
     * Instantiates a new DS folding preference page.
     */
    public DSFoldingPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FOLDING_SETTING));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            preferenceStore = WorkbenchPlugin.getDefault().getPreferenceStore();
        }
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));
        Group autoCommitGrp = new Group(comp, SWT.NONE);
        autoCommitGrp.setLayout(new GridLayout());
        autoCommitGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        enableCodeFoldingBtn = new Button(autoCommitGrp, SWT.RADIO);
        enableCodeFoldingBtn.setText(SPACE + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_ENABLE));
        Label enableAssistantLabel = new Label(autoCommitGrp, SWT.NONE);
        enableAssistantLabel
                .setText(TAB + MessageConfigLoader.getProperty(IMessagesConstants.PREF_FOLDING_ENABLE_DESC));
        disableCodeFoldingBtn = new Button(autoCommitGrp, SWT.RADIO);
        disableCodeFoldingBtn.setText(SPACE + MessageConfigLoader.getProperty(IMessagesConstants.AUTOCOMMIT_DISABLE));
        Label disableAssistantLabel = new Label(autoCommitGrp, SWT.NONE);
        disableAssistantLabel
                .setText(TAB + MessageConfigLoader.getProperty(IMessagesConstants.PREF_FOLDING_DISABLE_DESC));

        addMouseEvents();

        addHistoryDescLbl(autoCommitGrp);

        return comp;
    }

    /**
     * Adds the history desc lbl.
     *
     * @param lblGridLayout the lbl grid layout
     * @param parentGroup the parent group
     */
    private void addHistoryDescLbl(Group parentGroup) {
        GridLayout lblGridLayout = new GridLayout(1, false);
        lblGridLayout.marginLeft = 15;
        Composite labelHistorySizeComp = new Composite(parentGroup, SWT.FILL);
        labelHistorySizeComp.setLayout(lblGridLayout);
        Label lblHistoryDescription1 = new Label(labelHistorySizeComp, SWT.FILL);
        lblHistoryDescription1.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FOLDING_DESC));
    }

    private void addMouseEvents() {
        if (preferenceStore.getBoolean(SQL_EDITOR_FOLDING)) {
            enableCodeFoldingBtn.setSelection(true);
        } else {
            disableCodeFoldingBtn.setSelection(true);
        }
        MouseListener selectListener = new MouseListener() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {

            }

            @Override
            public void mouseDown(MouseEvent e) {

            }

            @Override
            public void mouseUp(MouseEvent e) {
                if (enableCodeFoldingBtn.getSelection()) {
                    disableCodeFoldingBtn.setSelection(false);
                    enableCodeFoldingBtn.setSelection(true);
                }
                if (!enableCodeFoldingBtn.getSelection() == preferenceStore
                        .getBoolean(DSFoldingPreferencePage.SQL_EDITOR_FOLDING)) {
                    getApplyButton().setEnabled(true);
                }
            }
        };
        enableCodeFoldingBtn.addMouseListener(selectListener);
        disableCodeFoldingBtn.addMouseListener(selectListener);

    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (preferenceStore != null) {
            if (preferenceStore.getBoolean(SQL_EDITOR_FOLDING) != enableCodeFoldingBtn.getSelection()) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Folding for SQL statements in preferences has been set from %b to %b",
                        preferenceStore.getBoolean(SQL_EDITOR_FOLDING), enableCodeFoldingBtn.getSelection()));
            }
            preferenceStore.setValue(SQL_EDITOR_FOLDING, enableCodeFoldingBtn.getSelection());
        }
        return true;
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        performOk();
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        getApplyButton().setEnabled(true);
        disableCodeFoldingBtn.setSelection(false);
        enableCodeFoldingBtn.setSelection(true);
        MPPDBIDELoggerUtility.operationInfo("Folding SQL statements in preferences is set to default: true");
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(IPreferenceStore preferenceStore) {
        preferenceStore.setDefault(SQL_EDITOR_FOLDING, true);
    }

}
