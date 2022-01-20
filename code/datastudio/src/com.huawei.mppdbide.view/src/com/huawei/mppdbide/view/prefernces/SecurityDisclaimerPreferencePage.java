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

package com.huawei.mppdbide.view.prefernces;

import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
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

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SecurityDisclaimerPreferencePage.
 *
 * @since 3.0.0
 */
public class SecurityDisclaimerPreferencePage extends PreferencePage {

    private IPreferenceStore preferenceStore;
    private Button enableButton;
    private Button disableButton;
    private Button chkBtn;

    /**
     * Instantiates a new security disclaimer preference page.
     */
    public SecurityDisclaimerPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_WARNING_OPTION));
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
        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));

        Group securityDisclaimerGrp = new Group(comp, SWT.NONE);
        securityDisclaimerGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.SECURITY_WARNING_OPTION));
        securityDisclaimerGrp.setLayout(new GridLayout());
        securityDisclaimerGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        addEnableButtonWithDescription(securityDisclaimerGrp);

        addDisableBtnWithDescription(comp, securityDisclaimerGrp);
        addCheckBtn(securityDisclaimerGrp);

        return comp;

    }

    /**
     * Adds the check btn.
     *
     * @param securityDisclaimerGrp the security disclaimer grp
     */
    private void addCheckBtn(Group securityDisclaimerGrp) {
        GridData chkBtnGd = new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1);
        chkBtnGd.horizontalIndent = 20;
        chkBtnGd.verticalIndent = 0;

        chkBtn = new Button(securityDisclaimerGrp, SWT.CHECK);
        chkBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IAGREE_TEXT));
        chkBtn.setEnabled(false);
        chkBtn.setLayoutData(chkBtnGd);

        enableDisableChkBtn();
        chkBtn.addSelectionListener(new CheckButtonSelectionListener());
    }

    /**
     * Adds the disable btn with description.
     *
     * @param comp the comp
     * @param securityDisclaimerGrp the security disclaimer grp
     */
    private void addDisableBtnWithDescription(Composite comp, Group securityDisclaimerGrp) {
        disableButton = new Button(securityDisclaimerGrp, SWT.RADIO);

        disableButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.DISABLE_OPTION));

        disableButton.addSelectionListener(new DisableButtonSelectionListener());

        GridData gdComposite2 = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite2.heightHint = 100;
        gdComposite2.widthHint = 500;
        gdComposite2.horizontalIndent = 18;
        gdComposite2.verticalIndent = 0;

        Label securitylabel = new Label(securityDisclaimerGrp, SWT.NONE);
        securitylabel.setLayoutData(gdComposite2);
        securitylabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.DISABLE_WARNING_MSG));
        securitylabel.setBackground(comp.getBackground());
    }

    /**
     * Adds the enable button with description.
     *
     * @param securityDisclaimerGrp the security disclaimer grp
     */
    private void addEnableButtonWithDescription(Group securityDisclaimerGrp) {
        GridData gdComposite1 = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite1.heightHint = 18;
        gdComposite1.widthHint = 500;
        gdComposite1.horizontalIndent = 18;
        gdComposite1.verticalIndent = 0;

        enableButton = new Button(securityDisclaimerGrp, SWT.RADIO);
        enableButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.ENABLE_OPTION));

        enableButton.addSelectionListener(new EnableButtonSelectionListener());

        if (preferenceStore != null
                && preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING)) {
            enableButton.setSelection(true);
        }

        Label enableLabel = new Label(securityDisclaimerGrp, SWT.NONE);
        enableLabel.setLayoutData(gdComposite1);
        enableLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENABLE_WARNING_MSG));
    }

    /**
     * Enable disable chk btn.
     */
    private void enableDisableChkBtn() {
        if (preferenceStore != null
                && !preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING)) {
            disableButton.setSelection(true);
            chkBtn.setSelection(true);
            chkBtn.setEnabled(true);

        }
    }

    /**
     * The listener interface for receiving checkButtonSelection events. The
     * class that is interested in processing a checkButtonSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addCheckButtonSelectionListener<code> method. When the
     * checkButtonSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * CheckButtonSelectionEvent
     */
    private class CheckButtonSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (chkBtn.getSelection()) {
                if (preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING)) {
                    PreferenceWrapper.getInstance().setChangeDone(true);

                }
                enableOkApplyBtn();
            } else {
                PreferenceWrapper.getInstance().setChangeDone(false);
                disableOkApplyBtn();
            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * The listener interface for receiving disableButtonSelection events. The
     * class that is interested in processing a disableButtonSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addDisableButtonSelectionListener<code> method. When the
     * disableButtonSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * DisableButtonSelectionEvent
     */
    private class DisableButtonSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            chkBtn.setEnabled(true);
            disableOkApplyBtn();

            if (getDefaultsButton() != null && !getDefaultsButton().getEnabled()
                    && PreferenceWrapper.getInstance().isChangeDone()) {
                getDefaultsButton().setEnabled(true);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    /**
     * The listener interface for receiving enableButtonSelection events. The
     * class that is interested in processing a enableButtonSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addEnableButtonSelectionListener<code> method. When the
     * enableButtonSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * EnableButtonSelectionEvent
     */
    private class EnableButtonSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {

            if (!preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING)) {
                PreferenceWrapper.getInstance().setChangeDone(true);

            }
            chkBtn.setEnabled(false);
            chkBtn.setSelection(false);
            enableOkApplyBtn();
            if (getDefaultsButton() != null && !getDefaultsButton().getEnabled()
                    && PreferenceWrapper.getInstance().isChangeDone()) {
                getDefaultsButton().setEnabled(true);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
        }
    }

    /**
     * Enable ok apply btn.
     */
    protected void enableOkApplyBtn() {
        getApplyButton().setEnabled(true);
        if (getShell().getDefaultButton() != null) {
            getShell().getDefaultButton().setEnabled(true);
        }

    }

    /**
     * Disable ok apply btn.
     */
    protected void disableOkApplyBtn() {
        getApplyButton().setEnabled(false);
        getShell().getDefaultButton().setEnabled(false);

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
            if (!enableButton.getSelection() && chkBtn.getSelection()) {
                flag = false;
            }
            if (flag != preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING)) {
                MPPDBIDELoggerUtility.securityInfo(String.format(Locale.ENGLISH,
                        "Security Warning message for unsecure connections/file operations in"
                                + " Preferences setting is set from %b to %b",
                        preferenceStore.getBoolean(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING),
                        flag));
                PreferenceWrapper.getInstance().setNeedRestart(true);
                preferenceStore.setValue(SecurityOptionProviderForPreferences.ENABLE_SECURITY_WARNING, flag);
            }

        }
        if (null != getApplyButton() && !getApplyButton().isEnabled()) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
        }

        return true;
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        PreferenceWrapper.getInstance().setPreferenceApply(true);
        performOk();
        getApplyButton().setEnabled(false);
        if (!getDefaultsButton().isEnabled()) {
            getDefaultsButton().setEnabled(true);
        }

    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore prefernceStore = getPreferenceStore();

        if (prefernceStore != null) {
            enableButton.setSelection(true);
            disableButton.setSelection(false);
            chkBtn.setEnabled(false);
            chkBtn.setSelection(false);

        }
        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        enableOkApplyBtn();
        MPPDBIDELoggerUtility.securityInfo("Security Warning message for unsecure connections/file operations in"
                + " Preferences setting is set to default: Enabled");
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    @Override
    public boolean performCancel() {

        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setPreferenceApply(false);
        PreferenceWrapper.getInstance().setDefaultStore(false);
        return true;
    }

}
