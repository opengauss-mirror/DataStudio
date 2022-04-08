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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.utils.DateTimeFormatValidator;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DateTimePreferencePage.
 *
 * @since 3.0.0
 */
public class DateTimePreferencePage extends PreferencePage {
    private static boolean userDefinedDateOption;
    private static boolean userDefinedTimeOption;
    private static boolean windowsFormatDateOption;
    private static boolean windowsFormatTimeOption;
    private static String dateFormatValue;
    private static String timeFormatValue;
    private Button btnUserDefinedDate;
    private Button btnWindowsFormatDate;
    private Button btnUserDefinedTime;
    private Button btnWindowsFormatTime;
    private Text dateFormatText;
    private Text timeFormatText;
    private Label lblDateFormatErrorMsg;
    private Label lblTimeFormatErrorMsg;

    /**
     * Instantiates a new Date Time preference page.
     */
    public DateTimePreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.DATE_TIME_PREFERENCES));
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
        addDatePreferenceUI(composite);
        addTimePreferenceUI(composite);
        addDateFormatErrorUi(composite);
        addTimeFormatErrorUi(composite);
        return composite;
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
    
    private void addDateFormatErrorUi(Composite parent) {
        Composite compDateErrorMsg = new Composite(parent, SWT.NONE);
        compDateErrorMsg.setLayout(new GridLayout(1, false));
        lblDateFormatErrorMsg = new Label(compDateErrorMsg, SWT.NONE);
        lblDateFormatErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblDateFormatErrorMsg.setBackground(compDateErrorMsg.getBackground());
        lblDateFormatErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATE_FORMAT_ERROR_MSG));
        lblDateFormatErrorMsg.setVisible(false);
    }

    private void addTimeFormatErrorUi(Composite parent) {
        Composite compTimeErrorMsg = new Composite(parent, SWT.NONE);
        compTimeErrorMsg.setLayout(new GridLayout(1, false));
        lblTimeFormatErrorMsg = new Label(compTimeErrorMsg, SWT.NONE);
        lblTimeFormatErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblTimeFormatErrorMsg.setBackground(compTimeErrorMsg.getBackground());
        lblTimeFormatErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.TIME_FORMAT_ERROR_MSG));
        lblTimeFormatErrorMsg.setVisible(false);
    }

    private void addDatePreferenceUI(Composite parent) {
        Group compDatePref = new Group(parent, SWT.NONE);
        compDatePref.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compDatePref);
        compDatePref.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATE_PREFERENCE));

        IPreferenceStore preferenceStore = getPreferenceStore();
        dateFormatText = new Text(compDatePref, SWT.BORDER);
        dateFormatText.setText(preferenceStore.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE));
        GridData dateFormatGrid = new GridData();
        dateFormatGrid.widthHint = 180;
        dateFormatGrid.heightHint = 15;
        dateFormatText.setLayoutData(dateFormatGrid);

        btnUserDefinedDate = new Button(compDatePref, SWT.RADIO);
        btnUserDefinedDate.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_DEFINED));
        btnUserDefinedDate.setSelection(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_DATE_FORMAT));
        GridData data = new GridData();
        data.horizontalSpan = 2;
        btnUserDefinedDate.setLayoutData(data);

        Label lbldateFormatSugg = getHintLabel(compDatePref);
        lbldateFormatSugg.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATE_FORMAT_EXAMPLE_HINT));

        btnWindowsFormatDate = new Button(compDatePref, SWT.RADIO);
        btnWindowsFormatDate.setText(MessageConfigLoader.getProperty(IMessagesConstants.SYSTEM_FORMAT));
        btnWindowsFormatDate.setSelection(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_SYSTEM_DATE_FORMAT));
        dateFormatText.setEnabled(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_DATE_FORMAT));
        addWindowsFormatDateSelListener(btnWindowsFormatDate, dateFormatText, false);
        addUsrDefFormatDateSelListener(btnUserDefinedDate, dateFormatText, true);
        this.dateFormatText.addKeyListener(dateTextKeyListener);
    }

    private Label getHintLabel(Group compDatePref) {
        Label lbl = new Label(compDatePref, SWT.NONE);
        GridData data = getGridDataHorizontalSpanTwo();
        data.horizontalIndent = 18;
        lbl.setLayoutData(data);
        return lbl;
    }

    private GridData getGridDataHorizontalSpanTwo() {
        GridData data = new GridData();
        data.horizontalSpan = 2;
        return data;
    }

    private void addTimePreferenceUI(Composite parent) {
        Group compTimePref = new Group(parent, SWT.NONE);
        compTimePref.setLayout(new GridLayout(2, false));
        GridDataFactory.fillDefaults().grab(true, false).applyTo(compTimePref);
        compTimePref.setText(MessageConfigLoader.getProperty(IMessagesConstants.TIME_PREFERENCE));

        IPreferenceStore preferenceStore = getPreferenceStore();
        timeFormatText = new Text(compTimePref, SWT.BORDER);
        timeFormatText.setText(preferenceStore.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE));
        GridData timeFormatGrid = new GridData();
        timeFormatGrid.widthHint = 180;
        timeFormatGrid.heightHint = 15;
        timeFormatText.setLayoutData(timeFormatGrid);
        btnUserDefinedTime = new Button(compTimePref, SWT.RADIO);
        btnUserDefinedTime.setText(MessageConfigLoader.getProperty(IMessagesConstants.USER_DEFINED));
        btnUserDefinedTime.setSelection(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_TIME_FORMAT));
        GridData data = new GridData();
        data.horizontalSpan = 2;
        btnUserDefinedTime.setLayoutData(data);

        Label lbldateFormatSugg = getHintLabel(compTimePref);
        lbldateFormatSugg.setText(MessageConfigLoader.getProperty(IMessagesConstants.TIME_FORMAT_EXAMPLE_HINT));

        btnWindowsFormatTime = new Button(compTimePref, SWT.RADIO);
        btnWindowsFormatTime.setText(MessageConfigLoader.getProperty(IMessagesConstants.SYSTEM_FORMAT));
        btnWindowsFormatTime.setSelection(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_SYSTEM_TIME_FORMAT));
        timeFormatText.setEnabled(preferenceStore.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_TIME_FORMAT));
        addWindowsFormatTimeSelListener(btnWindowsFormatTime, timeFormatText, false);
        addUsrDefFormatTimeSelListener(btnUserDefinedTime, timeFormatText, true);
        this.timeFormatText.addKeyListener(timeTextKeyListener);
    }

    /**
     * The date text key listener.
     */
    private KeyListener dateTextKeyListener = new KeyListener() {
        @Override
        public void keyReleased(KeyEvent e) {
            validateDateInput();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            validateDateInput();
        }

        private void validateDateInput() {
            boolean isValidDateFormat = DateTimeFormatValidator.validateDateFormat(dateFormatText.getText());
            if (isValidDateFormat) {
                getApplyButton().setEnabled(true);
                dateFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                lblDateFormatErrorMsg.setVisible(false);
            } else {
                dateFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                lblDateFormatErrorMsg.setVisible(true);
                getApplyButton().setEnabled(false);
            }
        }
    };

    /**
     * The date text key listener.
     */
    private KeyListener timeTextKeyListener = new KeyListener() {
        @Override
        public void keyReleased(KeyEvent e) {
            validateTimeInputText();
        }

        @Override
        public void keyPressed(KeyEvent e) {
            validateTimeInputText();
        }

        private void validateTimeInputText() {
            boolean isValidTimeFormat = DateTimeFormatValidator.validateTimeFormat(timeFormatText.getText());
            if (isValidTimeFormat) {
                getApplyButton().setEnabled(true);
                timeFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                lblTimeFormatErrorMsg.setVisible(false);
            } else {
                timeFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
                lblTimeFormatErrorMsg.setVisible(true);
                getApplyButton().setEnabled(false);
            }
        }
    };

    private void addWindowsFormatDateSelListener(Button btn, final Text winFormatDatetxt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                winFormatDatetxt.setEnabled(value);
                winFormatDatetxt.setText(MPPDBIDEConstants.DEFAULT_DATE_FORMAT);
                getDefaultsButton().setEnabled(true);
                lblDateFormatErrorMsg.setVisible(false);
                lblTimeFormatErrorMsg.setVisible(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void addUsrDefFormatDateSelListener(Button btn, final Text usrDefDatetxt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                usrDefDatetxt.setEnabled(value);
                usrDefDatetxt.setText(MPPDBIDEConstants.DEFAULT_DATE_FORMAT);
                usrDefDatetxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                getDefaultsButton().setEnabled(true);
                lblDateFormatErrorMsg.setVisible(false);
                lblTimeFormatErrorMsg.setVisible(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void addWindowsFormatTimeSelListener(Button btn, final Text winFormatTimetxt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                winFormatTimetxt.setEnabled(value);
                winFormatTimetxt.setText(MPPDBIDEConstants.DEFAULT_TIME_FORMAT);
                getDefaultsButton().setEnabled(true);
                lblDateFormatErrorMsg.setVisible(false);
                lblTimeFormatErrorMsg.setVisible(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    private void addUsrDefFormatTimeSelListener(Button btn, final Text usrDefTimetxt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                usrDefTimetxt.setEnabled(value);
                usrDefTimetxt.setText(MPPDBIDEConstants.DEFAULT_TIME_FORMAT);
                usrDefTimetxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                getDefaultsButton().setEnabled(true);
                lblDateFormatErrorMsg.setVisible(false);
                lblTimeFormatErrorMsg.setVisible(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        if (null != preferenceStore) {
            btnWindowsFormatDate.setSelection(true);
            btnWindowsFormatTime.setSelection(true);
            btnUserDefinedDate.setSelection(false);
            btnUserDefinedTime.setSelection(false);
            dateFormatText.setText(MPPDBIDEConstants.DEFAULT_DATE_FORMAT);
            timeFormatText.setText(MPPDBIDEConstants.DEFAULT_TIME_FORMAT);
        }
        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setDefaultStore(true);
        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Date/Time in Preferences setting are restored to default");
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

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        IPreferenceStore preferenceStore = getPreferenceStore();
        boolean isValidFormat = true;
        if (dateFormatText != null && !DateTimeFormatValidator.validateDateFormat(dateFormatText.getText())) {
            dateFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            lblDateFormatErrorMsg.setVisible(true);
            isValidFormat = false;
        }

        if (timeFormatText != null && !DateTimeFormatValidator.validateTimeFormat(timeFormatText.getText())) {
            timeFormatText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            lblTimeFormatErrorMsg.setVisible(true);
            isValidFormat = false;
        }
        if (!isValidFormat) {
            return isValidFormat;
        } else {
            if (lblDateFormatErrorMsg != null && lblTimeFormatErrorMsg != null) {
                lblDateFormatErrorMsg.setVisible(false);
                lblTimeFormatErrorMsg.setVisible(false);
            }
        }

        if (null != preferenceStore) {
            setDateTimePrefValues(preferenceStore);
            setPrefUserDefinedDateFormat((PreferenceStore) preferenceStore);
            setPrefUserDefinedTimeFormat((PreferenceStore) preferenceStore);
            setPrefWindowsDateFormat((PreferenceStore) preferenceStore);
            setPrefWindowsTimeFormat((PreferenceStore) preferenceStore);
            setPrefDateFormatValue((PreferenceStore) preferenceStore);
            setPrefTimeFormatValue((PreferenceStore) preferenceStore);
        }
        return true;
    }

    private void setDateTimePrefValues(IPreferenceStore preferenceStore) {
        preferenceStore.setValue(MPPDBIDEConstants.PREF_USER_DEFINE_DATE_FORMAT, btnUserDefinedDate.getSelection());
        preferenceStore.setValue(MPPDBIDEConstants.PREF_USER_DEFINE_TIME_FORMAT, btnUserDefinedTime.getSelection());
        if (btnWindowsFormatDate.getSelection() && (preferenceStore
                .getBoolean(MPPDBIDEConstants.PREF_SYSTEM_DATE_FORMAT) != btnWindowsFormatDate.getSelection())) {
            MPPDBIDELoggerUtility.operationInfo("Default Date format is enabled in Preferences setting");
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_SYSTEM_DATE_FORMAT, btnWindowsFormatDate.getSelection());
        if (btnWindowsFormatTime.getSelection() && (preferenceStore
                .getBoolean(MPPDBIDEConstants.PREF_SYSTEM_TIME_FORMAT) != btnWindowsFormatTime.getSelection())) {
            MPPDBIDELoggerUtility.operationInfo("Default Time format is enabled in Preferences setting");
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_SYSTEM_TIME_FORMAT, btnWindowsFormatTime.getSelection());
        if (dateFormatText != null
                && (!preferenceStore.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE).equals(dateFormatText.getText()))) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "User defined Date format in Preferences setting is changed from %s to %s",
                    preferenceStore.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE), dateFormatText.getText()));
            preferenceStore.setValue(MPPDBIDEConstants.DATE_FORMAT_VALUE, dateFormatText.getText());
        }
        if (timeFormatText != null
                && (!preferenceStore.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE).equals(timeFormatText.getText()))) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "User defined Time format in Preferences setting is changed from %s to %s",
                    preferenceStore.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE), timeFormatText.getText()));
            preferenceStore.setValue(MPPDBIDEConstants.TIME_FORMAT_VALUE, timeFormatText.getText());
        }
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
     * sets the DefaultPreferences
     * 
     * @param preferenceStore the pref
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_USER_DEFINE_DATE_FORMAT, false);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_USER_DEFINE_TIME_FORMAT, false);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_SYSTEM_DATE_FORMAT, true);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_SYSTEM_TIME_FORMAT, true);
        preferenceStore.setValue(MPPDBIDEConstants.DATE_FORMAT_VALUE, MPPDBIDEConstants.DEFAULT_DATE_FORMAT);
        preferenceStore.setValue(MPPDBIDEConstants.TIME_FORMAT_VALUE, MPPDBIDEConstants.DEFAULT_TIME_FORMAT);
    }

    /**
     * sets UserDefinedDateFormat preference
     * 
     * @param ps the preference
     */
    public static void setPrefUserDefinedDateFormat(PreferenceStore ps) {
        setUserDefTimeOption(ps.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_DATE_FORMAT));
    }

    /**
     * sets UserDefinedTimeFormat preference
     * 
     * @param ps the preference
     */
    public static void setPrefUserDefinedTimeFormat(PreferenceStore ps) {
        setUserDefTimeOption(ps.getBoolean(MPPDBIDEConstants.PREF_USER_DEFINE_TIME_FORMAT));
    }

    /**
     * sets WindowsDateFormat preference
     * 
     * @param ps the preference
     */
    public static void setPrefWindowsDateFormat(PreferenceStore ps) {
        setUserDefTimeOption(ps.getBoolean(MPPDBIDEConstants.PREF_SYSTEM_DATE_FORMAT));
    }

    /**
     * sets WindowsTimeFormat preference
     * 
     * @param ps the preference
     */
    public static void setPrefWindowsTimeFormat(PreferenceStore ps) {
        setUserDefTimeOption(ps.getBoolean(MPPDBIDEConstants.PREF_SYSTEM_TIME_FORMAT));
    }

    /**
     * sets DateFormatValue
     * 
     * @param ps the preference
     */
    public static void setPrefDateFormatValue(PreferenceStore ps) {
        setDateFormatValue(ps.getString(MPPDBIDEConstants.DATE_FORMAT_VALUE));
    }

    /**
     * sets TimeFormatValue
     * 
     * @param ps the preference
     */
    public static void setPrefTimeFormatValue(PreferenceStore ps) {
        setTimeFormatValue(ps.getString(MPPDBIDEConstants.TIME_FORMAT_VALUE));
    }

    /**
     * sets UserDefDateOption
     * 
     * @param ps the preference
     */
    public static void setUserDefDateOption(boolean luserDefinedDateOption) {
        userDefinedDateOption = luserDefinedDateOption;
    }

    /**
     * gets UserDefDateOption
     * 
     * @return the flag
     */
    public static boolean getUserDefDateOption() {
        return userDefinedDateOption;
    }

    /**
     * sets UserDefTimeOption
     * 
     * @param ps the preference
     */
    public static void setUserDefTimeOption(boolean luserDefinedTimeOption) {
        userDefinedTimeOption = luserDefinedTimeOption;
    }

    /**
     * gets UserDefTimeOption
     * 
     * @return the flag
     */
    public static boolean getUserDefTimeOption() {
        return userDefinedTimeOption;
    }

    /**
     * sets WindowsDateOption
     * 
     * @param ps the preference
     */
    public static void setWindowsDateOption(boolean lwindowsFormatDateOption) {
        windowsFormatDateOption = lwindowsFormatDateOption;
    }

    /**
     * gets WindowsDateOption
     * 
     * @return the flag
     */
    public static boolean getWindowsDateOption() {
        return windowsFormatDateOption;
    }

    /**
     * sets WindowsDateOption
     * 
     * @param ps the preference
     */
    public static void setWindowsTimeOption(boolean lwindowsFormatTimeOption) {
        windowsFormatTimeOption = lwindowsFormatTimeOption;
    }

    /**
     * gets WindowsDateOption
     * 
     * @return the flag
     */
    public static boolean getWindowsTimeOption() {
        return windowsFormatTimeOption;
    }

    /**
     * sets DateFormatValue
     * 
     * @param ps the preference
     */
    public static void setDateFormatValue(String ldateFormatValue) {
        dateFormatValue = ldateFormatValue;
    }

    /**
     * gets WindowsDateOption
     * 
     * @return the dateFormatValue
     */
    public static String getDateFormatValue() {
        return dateFormatValue;
    }

    /**
     * sets TimeFormatValue
     * 
     * @param ps the preference
     */
    public static void setTimeFormatValue(String ltimeFormatValue) {
        timeFormatValue = ltimeFormatValue;
    }

    /**
     * gets WindowsDateOption
     * 
     * @return the timeFormatValue
     */
    public static String getTimeFormatValue() {
        return timeFormatValue;
    }
}
