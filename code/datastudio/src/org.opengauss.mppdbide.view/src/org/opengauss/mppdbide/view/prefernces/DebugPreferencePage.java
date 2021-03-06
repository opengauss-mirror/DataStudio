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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class DebugPreferencePage extends PreferencePage {
    private Button btnIfRollback = null;
    private boolean isSelectChange = false;

    public DebugPreferencePage(String id) {
        super(id);
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite current = new Composite(parent, 0);
        current.setLayout(new RowLayout(SWT.VERTICAL));

        btnIfRollback = new Button(current, SWT.CHECK);
        btnIfRollback.setText(MessageConfigLoader.getProperty(IMessagesConstants.DEBUG_PREFREENCE_WHEN_ROLLBACK));
        btnIfRollback.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                isSelectChange = true;
            }
        });
        btnIfRollback.setSelection(getDebugRollbackSetting(false));
        return current;
    }

    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
    }

    @Override
    protected void performApply() {
        PreferenceWrapper wrapper = PreferenceWrapper.getInstance();
        PreferenceStore store = wrapper.getPreferenceStore();
        if (store != null) {
            boolean isRollback = btnIfRollback.getSelection();
            setRollback(store, isRollback);
        }
        wrapper.setPreferenceApply(true);
        getApplyButton().setEnabled(false);
        getDefaultsButton().setEnabled(true);
    }

    @Override
    public boolean performCancel() {
        PreferenceWrapper.getInstance().setPreferenceApply(false);
        PreferenceWrapper.getInstance().setDefaultStore(false);
        return true;
    }

    @Override
    protected void performDefaults() {
        PreferenceWrapper.getInstance().setDefaultStore(true);
        boolean defaultRollback = getDebugRollbackSetting(true);
        btnIfRollback.setSelection(defaultRollback);
        isSelectChange = true;
        getApplyButton().setEnabled(true);
        getDefaultsButton().setEnabled(false);
    }

    @Override
    public boolean performOk() {
        if (isSelectChange) {
            performApply();
            isSelectChange = false;
        }
        if (null != getApplyButton() && !getApplyButton().isEnabled()) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
        }
        return true;
    }

    /**
     * description: set the all debug default preference
     *
     * @param store  the save instance
     */
    public static void setAllDefault(IPreferenceStore store) {
        store.setDefault(MPPDBIDEConstants.DEBUG_PREFERENCE_IF_ROLLBACK, false);
    }

    /**
     * set the debug rollback preference
     *
     * @param store the store to save
     * @param state the save state
     */
    public static void setRollback(IPreferenceStore store, boolean state) {
        store.setValue(MPPDBIDEConstants.DEBUG_PREFERENCE_IF_ROLLBACK, state);
    }

    private boolean getDebugRollbackSetting(boolean isLoadDefault) {
        PreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        if (isLoadDefault) {
            return preferenceStore.getDefaultBoolean(MPPDBIDEConstants.DEBUG_PREFERENCE_IF_ROLLBACK);
        } else {
            return preferenceStore.getBoolean(MPPDBIDEConstants.DEBUG_PREFERENCE_IF_ROLLBACK);
        }
    }
}
