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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLTerminalPreferencePage.
 *
 * @since 3.0.0
 */
public class SQLTerminalPreferencePage extends PreferencePage {

    /**
     * The Constant IS_SHOW_AUTOCOMMIT.
     */
    public static final String IS_SHOW_AUTOCOMMIT = "sqlterminal.showautocommit";

    /**
     * The Constant CONN_AUTOCOMMIT_PREF.
     */
    public static final String CONN_AUTOCOMMIT_PREF = "sqlterminal.autocommit";

    /**
     * Instantiates a new SQL terminal preference page.
     */
    public SQLTerminalPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_NODE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Label lable = new Label(parent, SWT.NONE);
        lable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
        lable.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITOR_NODE_LABLE));
        return null;
    }

    /**
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {

        super.createControl(parent);
        getDefaultsButton().setVisible(false);
        getApplyButton().setVisible(false);

    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        // Default setup to be false for now. Will be added to a new preference
        // page.
        preferenceStore.setDefault(IS_SHOW_AUTOCOMMIT, true);
        preferenceStore.setDefault(CONN_AUTOCOMMIT_PREF, true);
    }

}
