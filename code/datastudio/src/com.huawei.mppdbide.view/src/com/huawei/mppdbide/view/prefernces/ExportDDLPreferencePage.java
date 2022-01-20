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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.connectiondialog.DBConnectionValidator;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExportDDLPreferencePage.
 *
 * @since 3.0.0
 */
public class ExportDDLPreferencePage extends PreferencePage {

    private Button btnIncludeTablespaceOptions;
    private Button btnIncludeExportDataOptions;
    private static boolean maintainTablespaceOption;
    private static boolean maintainExportDataOption = true;
    private Button btnDfltTimeOut;
    private Button btnCustomTimeOut;
    private Text txtRecordTimeOut;
    private boolean isDefaultTimeOut;
    private int recordTimeOut;
    private Text txtImportExportLimit;

    private static final int PREF_CUSTOM_TIME = MPPDBIDEConstants.PROCESS_TIMEOUT;
    private int importExportLimit = MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_DEFAULT;

    /**
     * Instantiates a new export DDL preference page.
     */
    public ExportDDLPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_PREFERENCES));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(composite);

        Group grpEcportDdlPrefences = new Group(composite, SWT.None);
        grpEcportDdlPrefences.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(grpEcportDdlPrefences);

        grpEcportDdlPrefences.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DDL_PREFERENCES));
        btnIncludeTablespaceOptions = new Button(grpEcportDdlPrefences, SWT.CHECK);
        btnIncludeTablespaceOptions
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.INCLUDE_TABLESPACE_IN_DDL));

        if (maintainTablespaceOption) {
            btnIncludeTablespaceOptions.setSelection(true);

        }
        btnIncludeTablespaceOptions.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                getDefaultsButton().setEnabled(true);
                getApplyButton().setEnabled(true);

            }

        });

        Label lblHintContent = new Label(grpEcportDdlPrefences, SWT.NONE);
        GridData data = new GridData();
        data.horizontalIndent = 18;
        lblHintContent.setLayoutData(data);
        lblHintContent.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_EXPORT_DDL_W_TABLESPACE_HINT));

        addConfigbasedExportPrefUI(composite);
        addExportTimeOutPreferenceUI(composite);
        addLimitExportPreferenceUI(composite);
        return composite;
    }

    private void addLimitExportPreferenceUI(Composite parent) {
        Group compExportTimeOut = new Group(parent, SWT.NONE);
        compExportTimeOut.setLayout(new GridLayout(1, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compExportTimeOut);
        // HEADER
        compExportTimeOut.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_PREF_TITLE));

        Composite limitComposite = new Composite(compExportTimeOut, SWT.NONE);
        limitComposite.setLayout(new GridLayout(3, false));
        limitComposite.setLayoutData(new GridData());

        Label limitLbl = new Label(limitComposite, SWT.NONE);
        limitLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_PREF_TITLE));

        IPreferenceStore preferenceStore = getPreferenceStore();
        importExportLimit = preferenceStore.getInt(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF);

        txtImportExportLimit = new Text(limitComposite, SWT.BORDER);
        txtImportExportLimit.setText(Integer.toString(importExportLimit));
        GridData dataText = new GridData();
        dataText.widthHint = 70;
        txtImportExportLimit.setLayoutData(dataText);
        DBConnectionValidator txtCustomWidthtVerifyListener = new DBConnectionValidator(txtImportExportLimit,
                MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_MAX);
        txtImportExportLimit.addVerifyListener(txtCustomWidthtVerifyListener);
        Label rangeLbl = new Label(limitComposite, SWT.NONE);
        rangeLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_PREF_RANGE));

        Composite infoComposite = new Composite(compExportTimeOut, SWT.NONE);
        infoComposite.setLayout(new GridLayout());
        infoComposite.setLayoutData(new GridData());

        Label infoLbl = new Label(infoComposite, SWT.NONE);
        infoLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_LIMIT_PREF_INFO));

    }

    private void addConfigbasedExportPrefUI(Composite parent) {
        Group grpExportDataPrefences = new Group(parent, SWT.None);
        grpExportDataPrefences.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().grab(true, false).applyTo(grpExportDataPrefences);

        grpExportDataPrefences.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DATA_PREFERENCES));
        btnIncludeExportDataOptions = new Button(grpExportDataPrefences, SWT.CHECK);
        btnIncludeExportDataOptions
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.INCLUDE_EXPORT_DATA_OPTION));

        if (maintainExportDataOption) {
            btnIncludeExportDataOptions.setSelection(true);
        }
        btnIncludeExportDataOptions.addListener(SWT.Selection, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                getDefaultsButton().setEnabled(true);
                getApplyButton().setEnabled(true);

            }

        });
    }

    private void addExportTimeOutPreferenceUI(Composite parent) {
        Group compExportTimeOut = new Group(parent, SWT.NONE);
        compExportTimeOut.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compExportTimeOut);
        // HEADER
        compExportTimeOut.setText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_TIMEOUT));

        IPreferenceStore preferenceStore = getPreferenceStore();
        recordTimeOut = preferenceStore.getInt(MPPDBIDEConstants.TIMEOUT_VALUE);
        isDefaultTimeOut = preferenceStore.getBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT);

        btnDfltTimeOut = new Button(compExportTimeOut, SWT.RADIO);
        btnDfltTimeOut.setText(MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_TIMEOUT) + " [ "
                + MPPDBIDEConstants.PROCESS_TIMEOUT + " " + MessageConfigLoader.getProperty(IMessagesConstants.SECONDS)
                + " ]");
        GridData data = new GridData();
        data.horizontalSpan = 2;
        btnDfltTimeOut.setLayoutData(data);

        btnCustomTimeOut = new Button(compExportTimeOut, SWT.RADIO);
        btnCustomTimeOut.setText(MessageConfigLoader.getProperty(IMessagesConstants.CUSTOM_TIMEOUT));

        Composite timeoutTextComp = new Composite(compExportTimeOut, SWT.NONE);
        timeoutTextComp.setLayout(new GridLayout(2, false));

        txtRecordTimeOut = new Text(timeoutTextComp, SWT.BORDER);
        txtRecordTimeOut.setText(Integer.toString(recordTimeOut));
        GridData dataText = new GridData();
        dataText.widthHint = 70;
        txtRecordTimeOut.setLayoutData(dataText);

        DBConnectionValidator txtCustomWidthtVerifyListener = new DBConnectionValidator(txtRecordTimeOut,
                PREF_CUSTOM_TIME);
        txtRecordTimeOut.addVerifyListener(txtCustomWidthtVerifyListener);
        Label timeUnit = new Label(timeoutTextComp, SWT.NONE);
        timeUnit.setText(MessageConfigLoader.getProperty(IMessagesConstants.SECONDS));

        setDefaultSelection(isDefaultTimeOut);
        addSelectionListener(btnCustomTimeOut, txtRecordTimeOut, true);
        addSelectionListener(btnDfltTimeOut, txtRecordTimeOut, false);

        this.txtRecordTimeOut.addKeyListener(txtKeyListener);
    }

    /**
     * The txt key listener.
     */
    KeyListener txtKeyListener = new KeyListener() {

        @Override
        public void keyReleased(KeyEvent e) {
            // Ignore
        }

        @Override
        public void keyPressed(KeyEvent e) {
            getApplyButton().setEnabled(true);
        }
    };

    private void addSelectionListener(Button btn, final Text txt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txt.setEnabled(value);
                getDefaultsButton().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void setDefaultSelection(boolean decider) {
        if (decider) {
            btnDfltTimeOut.setSelection(true);
            btnCustomTimeOut.setSelection(false);
            txtRecordTimeOut.setEnabled(false);
        } else {
            btnDfltTimeOut.setSelection(false);
            btnCustomTimeOut.setSelection(true);
            txtRecordTimeOut.setEnabled(true);
        }
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
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {
            btnIncludeTablespaceOptions.setSelection(false);
            btnIncludeExportDataOptions.setSelection(true);
            isDefaultTimeOut = preferenceStore.getDefaultBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT);
            recordTimeOut = preferenceStore.getDefaultInt(MPPDBIDEConstants.TIMEOUT_VALUE);
            importExportLimit = preferenceStore.getDefaultInt(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF);
        }
        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setDefaultStore(true);
        txtRecordTimeOut.setText(Integer.toString(recordTimeOut));
        txtImportExportLimit.setText(Integer.toString(importExportLimit));
        setDefaultSelection(isDefaultTimeOut);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Export/Import values in Preferences setting are set to default");
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    @Override
    public boolean performCancel() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {
            isDefaultTimeOut = preferenceStore.getBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT);
            recordTimeOut = preferenceStore.getInt(MPPDBIDEConstants.TIMEOUT_VALUE);
        }
        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setPreferenceApply(false);
        PreferenceWrapper.getInstance().setDefaultStore(false);
        return true;
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {
            if (preferenceStore.getBoolean(
                    MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS) != btnIncludeTablespaceOptions.getSelection()) {
                MPPDBIDELoggerUtility
                        .operationInfo(String.format(Locale.ENGLISH, "Include Tablespace in DDL is set from %b to %b",
                                preferenceStore.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS),
                                btnIncludeTablespaceOptions.getSelection()));
            }
            preferenceStore.setValue(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS,
                    btnIncludeTablespaceOptions.getSelection());
            if (preferenceStore
                    .getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS) != btnIncludeExportDataOptions
                            .getSelection()) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH, "Export Data is set from %b to %b",
                        preferenceStore.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS),
                        btnIncludeExportDataOptions.getSelection()));
            }
            preferenceStore.setValue(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS,
                    btnIncludeExportDataOptions.getSelection());

            setPreferenceTablespaceOption((PreferenceStore) preferenceStore);
            setPreferenceExportDataOption((PreferenceStore) preferenceStore);
            if (btnDfltTimeOut.getSelection() && (preferenceStore
                    .getBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT) != btnDfltTimeOut.getSelection())) {
                MPPDBIDELoggerUtility.operationInfo("Default Export Timeout is enabled in Preferences setting");
            }
            preferenceStore.setValue(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT, btnDfltTimeOut.getSelection());
            if (!preferenceStore.getString(MPPDBIDEConstants.TIMEOUT_VALUE).equals(txtRecordTimeOut.getText())) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Custom Time out in Preferences setting is changed from %s to %s",
                        preferenceStore.getString(MPPDBIDEConstants.TIMEOUT_VALUE), txtRecordTimeOut.getText()));
            }
            preferenceStore.setValue(MPPDBIDEConstants.TIMEOUT_VALUE, txtRecordTimeOut.getText());
            if (!preferenceStore.getString(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF)
                    .equals(txtImportExportLimit.getText())) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Parallel Import/Export limit is in Preferences setting is changed from %s to %s",
                        preferenceStore.getString(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF),
                        txtImportExportLimit.getText()));
            }
            preferenceStore.setValue(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF, txtImportExportLimit.getText());
            isDefaultTimeOut = preferenceStore.getBoolean(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT);
            recordTimeOut = preferenceStore.getInt(MPPDBIDEConstants.TIMEOUT_VALUE);
            importExportLimit = preferenceStore.getInt(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF);
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
    }

    /**
     * Sets the preference tablespace option.
     *
     * @param ps the new preference tablespace option
     */
    public static void setPreferenceTablespaceOption(PreferenceStore ps) {
        setMaintainTablespaceOption(ps.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS));
    }

    /**
     * Sets the preference export data option.
     *
     * @param ps the new preference export data option
     */
    public static void setPreferenceExportDataOption(PreferenceStore ps) {
        setMaintainExportDataOption(ps.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS));
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS, false);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS, true);
        setMaintainTablespaceOption(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_TABLESPACE_OPTIONS));
        setMaintainExportDataOption(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_MAINTAIN_EXPORT_DATA_OPTIONS));
        preferenceStore.setDefault(MPPDBIDEConstants.TIMEOUT_VALUE, PREF_CUSTOM_TIME);
        preferenceStore.setDefault(MPPDBIDEConstants.IS_DEFAULT_TIMEOUT, true);
        preferenceStore.setDefault(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF,
                MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_DEFAULT);
    }

    /**
     * Checks if is maintain tablespace option.
     *
     * @return true, if is maintain tablespace option
     */
    public static boolean isMaintainTablespaceOption() {
        return maintainTablespaceOption;
    }

    /**
     * Sets the maintain tablespace option.
     *
     * @param maintainTablespaceOption the new maintain tablespace option
     */
    public static void setMaintainTablespaceOption(boolean maintainTablespaceOption) {
        ExportDDLPreferencePage.maintainTablespaceOption = maintainTablespaceOption;
    }

    /**
     * Sets the maintain Export Data option.
     *
     * @param maintainExportDataOption the new maintain export data option
     */
    public static void setMaintainExportDataOption(boolean maintainExportDataOption) {
        ExportDDLPreferencePage.maintainExportDataOption = maintainExportDataOption;
    }

    /**
     * validateImportExportData method
     * 
     * @param ps preference store
     */
    public static void validateImportExportData(PreferenceStore ps) {
        int importExLimit = ps.getInt(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF);
        if (!(importExLimit >= 0 && importExLimit <= MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_MAX)) {
            ps.setValue(MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_PREF,
                    MPPDBIDEConstants.PARALLEL_IMPORT_EXPORT_DEFAULT);
        }
    }
}
