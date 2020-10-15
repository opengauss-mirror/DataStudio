/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.util.Locale;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.ui.connectiondialog.DBConnectionValidator;

/** 
 * Title: ObjectBrowserPreferncePage
 * 
 * Description:
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 27-May-2020]
 * @since 27-May-2020
 */
public class ObjectBrowserPreferncePage extends PreferencePage implements IObjectBrowserPreference {
    /**
     * ObjectBrowserPreferncePage constructor
     */
    public ObjectBrowserPreferncePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER));
    }

    private IPreferenceStore preferenceStore;
    private Text textFilterTimeout;
    private Label lblErrorMsg;

    @Override
    protected Control createContents(Composite parent) {
        preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            preferenceStore = WorkbenchPlugin.getDefault().getPreferenceStore();
        }

        Group filterTimeoutGroup = new Group(parent, SWT.FILL);
        filterTimeoutGroup.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT));
        filterTimeoutGroup.setLayout(new GridLayout(1, true));
        GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true);
        filterTimeoutGroup.setLayoutData(grid);

        Composite mainComp = new Composite(filterTimeoutGroup, SWT.NONE);
        mainComp.setLayoutData(grid);
        mainComp.setLayout(new GridLayout(1, true));

        Composite autoSuggestComp = new Composite(mainComp, SWT.NONE);
        GridData grid1 = new GridData(SWT.FILL, SWT.TOP, true, false);

        autoSuggestComp.setLayoutData(grid1);
        autoSuggestComp.setLayout(new GridLayout(4, false));

        Label autoSuggestTitle = new Label(autoSuggestComp, SWT.BOLD);
        autoSuggestTitle.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT));

        addTextFilterTimeoutText(autoSuggestComp);
        Label secLabel = new Label(autoSuggestComp, SWT.BOLD);
        secLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.SECONDS));

        getRangeLabel(autoSuggestComp);

        mainComp.pack();

        Composite autoSuggestDescComp = new Composite(mainComp, SWT.FILL);
        autoSuggestDescComp.setLayout(new GridLayout(1, true));

        Label descLabel = new Label(autoSuggestDescComp, SWT.WRAP | SWT.BOLD);
        descLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_INFO));

        addErrorMsgLables(parent);
        return parent;
    }

    private void getRangeLabel(Composite autoSuggestComp) {
        Label rangeLabel = new Label(autoSuggestComp, SWT.BOLD);
        GridData gdata = new GridData();
        gdata.horizontalIndent = 10;
        rangeLabel.setLayoutData(gdata);
        rangeLabel.setText("(" + MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_RANGE)
                + MPPDBIDEConstants.SPACE_CHAR + 1 + MPPDBIDEConstants.SEPARATOR + ' ' + 10 + ";"
                + MPPDBIDEConstants.SPACE_CHAR + MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_VALUE)
                + MPPDBIDEConstants.SPACE_CHAR + 2 + ")");
    }

    private void addTextFilterTimeoutText(Composite autoSuggestComp) {
        textFilterTimeout = new Text(autoSuggestComp, SWT.BORDER);
        textFilterTimeout.setText(Integer.toString(preferenceStore.getInt(OB_FILTER_TIMEOUT_PREFERENCE_KEY)));
        GridData data = new GridData();
        data.widthHint = 100;
        textFilterTimeout.setLayoutData(data);
        DBConnectionValidator txtRecordCountVerifyListener = new DBConnectionValidator(textFilterTimeout,
                OB_FILTER_TIMEOUT_MAX);
        textFilterTimeout.addVerifyListener(txtRecordCountVerifyListener);
        textFilterTimeout.addVerifyListener(new VerifyListener() {
            @Override
            public void verifyText(VerifyEvent e) {
                setErrorMessage("", false);
            }
        });
    }

    private void addErrorMsgLables(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));
        compErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        lblErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblErrorMsg.setBackground(compErrorMsg.getBackground());
        lblErrorMsg.setVisible(false);
        lblErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
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
     * Perform apply.
     */
    @Override
    protected void performApply() {
        performOk();
        getApplyButton().setEnabled(false);
        getDefaultsButton().setEnabled(true);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        preferenceStore.setDefault(OB_FILTER_TIMEOUT_PREFERENCE_KEY, OB_FILTER_TIMEOUT_DEFAULT);
        lblErrorMsg.setVisible(false);
        textFilterTimeout.setText(Integer.toString(preferenceStore.getDefaultInt(OB_FILTER_TIMEOUT_PREFERENCE_KEY)));
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Filter timeout in preferences is set to default value");
    }
    
    /**
     * setDefaultPreferences set default values
     * 
     * @param preferenceStore pref store
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(OB_FILTER_TIMEOUT_PREFERENCE_KEY, OB_FILTER_TIMEOUT_DEFAULT);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (textFilterTimeout != null && !textFilterTimeout.isDisposed() && validateFilterTimeout()) {
            return false;
        }
        if (preferenceStore != null && textFilterTimeout != null) {
            int prevValue = preferenceStore.getInt(OB_FILTER_TIMEOUT_PREFERENCE_KEY);
            int currValue = Integer.parseInt(textFilterTimeout.getText());
            if (prevValue != currValue) {
                MPPDBIDELoggerUtility.operationInfo(
                        String.format(Locale.ENGLISH, "Filter Timeout value in preferences changed from %d to %d",
                                prevValue, Integer.parseInt(textFilterTimeout.getText())));
                preferenceStore.setValue(OB_FILTER_TIMEOUT_PREFERENCE_KEY,
                        Integer.parseInt(textFilterTimeout.getText()));
            }
        }
        return true;
    }

    private boolean validateFilterTimeout() {
        try {
            if (Integer.parseInt(textFilterTimeout.getText()) < OB_FILTER_TIMEOUT_MIN
                    || Integer.parseInt(textFilterTimeout.getText()) > OB_FILTER_TIMEOUT_MAX) {
                setErrorMessage(
                        MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_ERR_MSG),
                        true);
                return true;
            }
        } catch (NumberFormatException exception) {
            setErrorMessage(MessageConfigLoader.getProperty(IMessagesConstants.OBJECT_BROWSER_FILTER_TIMEOUT_ERR_MSG),
                    true);
            return true;
        }
        return false;
    }

    private void setErrorMessage(String errorMsg, boolean isVisible) {
        lblErrorMsg.setVisible(isVisible);
        lblErrorMsg.setText(errorMsg);
        getApplyButton().setEnabled(!isVisible);
    }

    /**
     * validateObjectBrowserPref validate pref
     * 
     * @param ps pref store
     */
    public static void validateObjectBrowserPref(PreferenceStore ps) {
        if (ps == null) {
            return;
        }
        int filterTimeout = ps.getInt(OB_FILTER_TIMEOUT_PREFERENCE_KEY);
        if (filterTimeout < OB_FILTER_TIMEOUT_MIN || filterTimeout > OB_FILTER_TIMEOUT_MAX) {
            ps.setValue(OB_FILTER_TIMEOUT_PREFERENCE_KEY, OB_FILTER_TIMEOUT_DEFAULT);
        }
    }
}