/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
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
 * Description: The Class ResultManagementEditTablePreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ResultManagementEditTablePreferencePage extends AbstractPreferencePage {

    /**
     * Instantiates a new result management edit table preference page.
     */
    public ResultManagementEditTablePreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.EDITTABLE_PREFERENCE_OPTION));
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

        GridData gdComposite = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite.heightHint = 500;
        gdComposite.widthHint = 500;
        gdComposite.horizontalIndent = 18;
        gdComposite.verticalIndent = 0;
        comp.setLayoutData(gdComposite);

        Group editTableGr = new Group(comp, SWT.NONE);
        editTableGr.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDITTABLE_TITLE));
        editTableGr.setLayout(new GridLayout());
        editTableGr.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        yesButton = new Button(editTableGr, SWT.RADIO);
        yesButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_ENABLE_OPTION));

        Label yesEditTableLabel = new Label(editTableGr, SWT.NONE);
        yesEditTableLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.YES_EDITTABLE_OPTION));

        if (preferenceStore != null
                && preferenceStore.getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE)) {
            yesButton.setSelection(true);
        }
        yesButton.addSelectionListener(
                new ButtonSelectionListener(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE, true));
        noButton = new Button(editTableGr, SWT.RADIO);

        noButton.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DISABLE_OPTION));

        Label noExpiryLabel = new Label(editTableGr, SWT.NONE);
        noExpiryLabel.setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.NO_EDITTABLE_OPTION));
        if (preferenceStore != null
                && !preferenceStore.getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE)) {
            noButton.setSelection(true);
        }

        noButton.addSelectionListener(
                new ButtonSelectionListener(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE, false));

        return comp;

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
            if (!yesButton.getSelection() && noButton.getSelection()) {
                flag = false;
            }
            if (flag != preferenceStore.getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE)) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Table Data save option in Preferences setting is set from %b to %b",
                        preferenceStore.getBoolean(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE),
                        flag));
                preferenceStore.setValue(EditTableOptionProviderForPreferences.EDITTABLE_COMMIT_ON_FAILURE, flag);
            }
        }

        return true;
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore preferenceStre = getPreferenceStore();

        if (preferenceStre != null) {

            yesButton.setSelection(true);
            noButton.setSelection(false);
        }
        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Table data save option in preferences is set to default: true");

    }
}
