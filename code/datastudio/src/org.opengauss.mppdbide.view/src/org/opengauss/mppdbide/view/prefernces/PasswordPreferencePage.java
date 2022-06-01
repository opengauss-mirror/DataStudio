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
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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
 * Title: class
 * 
 * Description: The Class PasswordPreferencePage.
 * 
 * @since 3.0.0
 */
@SuppressWarnings("restriction")
public class PasswordPreferencePage extends AbstractPreferencePage {
    private Button paswdExpiryYesButton;
    private Button paswdExpiryNoButton;

    /**
     * Instantiates a new password preference page.
     */
    public PasswordPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_PREFERENCE_OPTION));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {

        getPrefStore();
        Composite comp = new Composite(parent, SWT.NONE);

        comp.setLayout(new GridLayout(1, false));

        Composite cmp1 = new Composite(comp, SWT.NONE);
        cmp1.setLayout(new GridLayout(1, false));
        cmp1.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        createSavePasswordGroup(cmp1);

        // Password Expiry

        createPasswordExpiryGroup(cmp1);

        return comp;
    }

    /**
     * Creates the password expiry group.
     *
     * @param cmp1 the cmp 1
     */
    private void createPasswordExpiryGroup(Composite cmp1) {
        Group passwordExpireGr = new Group(cmp1, SWT.NONE);
        passwordExpireGr.setText(MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_EXPIRY_TITLE));
        passwordExpireGr.setLayout(new GridLayout());
        passwordExpireGr.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        paswdExpiryYesButton = new Button(passwordExpireGr, SWT.RADIO);
        paswdExpiryYesButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION));

        Label yesExpiryLabel = new Label(passwordExpireGr, SWT.NONE);
        yesExpiryLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.YES_EXPIRY_OPTION));

        if (preferenceStore.getBoolean(SecurityOptionProviderForPreferences.YES_PD_EXPIRY_PERMANENTLY)) {
            paswdExpiryYesButton.setSelection(true);
        }
        paswdExpiryYesButton.addSelectionListener(new PasswordExpireButtonSelectionListener());

        paswdExpiryNoButton = new Button(passwordExpireGr, SWT.RADIO);

        paswdExpiryNoButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION));

        Label noExpiryLabel = new Label(passwordExpireGr, SWT.NONE);
        noExpiryLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.NO_EXPIRY_OPTION));
        if (!preferenceStore.getBoolean(SecurityOptionProviderForPreferences.YES_PD_EXPIRY_PERMANENTLY)) {
            paswdExpiryNoButton.setSelection(true);
        }

        paswdExpiryNoButton.addSelectionListener(new PasswordExpireButtonSelectionListener());
    }

    /**
     * Creates the save password group.
     *
     * @param cmp1 the cmp 1
     */
    private void createSavePasswordGroup(Composite cmp1) {
        Group savePasswordGrp = new Group(cmp1, SWT.NONE);
        savePasswordGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.CIPHER_PREFERENCE_OPTION_TITLE));
        savePasswordGrp.setLayout(new GridLayout());
        savePasswordGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        yesButton = new Button(savePasswordGrp, SWT.RADIO);
        yesButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION));

        Label yesLabel = new Label(savePasswordGrp, SWT.NONE);
        yesLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.YES_TEXT));

        if (preferenceStore.getBoolean(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY)) {
            yesButton.setSelection(true);
        }
        yesButton.addSelectionListener(
                new ButtonSelectionListener(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY, true));

        noButton = new Button(savePasswordGrp, SWT.RADIO);

        noButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION));

        Label noLabel = new Label(savePasswordGrp, SWT.NONE);

        noLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.NO_TEXT));
        if (!preferenceStore.getBoolean(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY)) {
            noButton.setSelection(true);
        }

        noButton.addSelectionListener(
                new ButtonSelectionListener(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY, false));
    }

    /**
     * Gets the pref store.
     *
     * @return the pref store
     */
    private void getPrefStore() {
        preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            preferenceStore = WorkbenchPlugin.getDefault().getPreferenceStore();
        }
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        boolean flag = true;
        preferenceStore = getPreferenceStore();
        if (preferenceStore != null) {

            if (!yesButton.getSelection()) {
                flag = false;
            }
            if (preferenceStore.getBoolean(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY) != flag) {
                MPPDBIDELoggerUtility.securityInfo(String.format(Locale.ENGLISH,
                        "Enable permanent save of password option in preferences setting is set from %b to %b",
                        preferenceStore.getBoolean(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY), flag));
            }
            preferenceStore.setValue(SecurityOptionProviderForPreferences.SAVE_PD_PERMANENTLY, flag);
            boolean expiryFlag = true;
            
            // Expiry password
            if (!paswdExpiryYesButton.getSelection()) {
                expiryFlag = false;
            }
            if (preferenceStore
                    .getBoolean(SecurityOptionProviderForPreferences.YES_PD_EXPIRY_PERMANENTLY) != expiryFlag) {
                MPPDBIDELoggerUtility.securityInfo(String.format(Locale.ENGLISH,
                        "Allow login after password expiry in preferences setting is set from %b to %b",
                        preferenceStore.getBoolean(SecurityOptionProviderForPreferences.YES_PD_EXPIRY_PERMANENTLY),
                        expiryFlag));
            }
            preferenceStore.setValue(SecurityOptionProviderForPreferences.YES_PD_EXPIRY_PERMANENTLY, expiryFlag);
        }
        if (null != getApplyButton() && !getApplyButton().isEnabled()) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
        }
        PreferenceWrapper.getInstance().setNeedRestart(true);

        return true;
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore prefernceStore = getPreferenceStore();

        if (prefernceStore != null) {
            noButton.setSelection(true);
            yesButton.setSelection(false);
            paswdExpiryYesButton.setSelection(true);
            paswdExpiryNoButton.setSelection(false);
        }
        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility
                .securityInfo("Enable permanently save password option and Allow login after password expiry in "
                        + "preferences are set to Defaults: No and Yes respectively");

    }

    /**
     * The listener interface for receiving buttonSelection events. The class
     * that is interested in processing a buttonSelection event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addButtonSelectionListener<code>
     * method. When the buttonSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * ButtonSelectionEvent
     */
    protected class PasswordExpireButtonSelectionListener implements SelectionListener {

        /**
         * Widget selected.
         *
         * @param e the e
         */
        @Override
        public void widgetSelected(SelectionEvent e) {
            PreferenceWrapper.getInstance().setChangeDone(true);
            enableDisableApplyBtn();

            if (getDefaultsButton() != null && !getDefaultsButton().getEnabled()
                    && PreferenceWrapper.getInstance().isChangeDone()) {
                getDefaultsButton().setEnabled(true);
            }
        }

        /**
         * Widget default selected.
         *
         * @param e the e
         */
        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // Auto-generated method stub

        }

    }
}
