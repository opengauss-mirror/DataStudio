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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SQLHistoryAndQueryPreference.
 *
 * @since 3.0.0
 */
public class SQLHistoryAndQueryPreference extends PreferencePage {
    private static final int SQL_SIZE_MIN_VALUE_INT = 1;
    private static final int SQL_HISTORY_MAX_SIZE_COUNT_INT = 1000;

    private IPreferenceStore preferenceStore;
    private Text textHistSize;
    private Text textQueryLength;
    private Label lblHistoryErrorMsg;
    private Label lblQueryErrorMsg;
    private int filteredHistorySize = -1;
    private int filteredQueryLength = -1;
    private boolean errorFlag;
    private static final String SQL_SIZE_MIN_VALUE = "1";
    private static final String SQL_QUERY_NO_LIMIT = "0";

    // Query Length - Default=1000, Max=1000
    // Number of Queries - Default=50, Max=1000
    private static final String SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT = "1000";
    private static final String SQL_HISTORY_MAX_SIZE_COUNT = "1000";
    private static final String SQL_HISTORY_DEFAULT_SIZE_COUNT = "50";

    /**
     * Instantiates a new SQL history and query preference.
     */
    public SQLHistoryAndQueryPreference() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY));
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
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @SuppressWarnings("restriction")
    @Override
    protected Control createContents(Composite parent) {
        getPrefStore();
        GridLayout grpHistoryFieldLayout = getGrpHistoryFieldLayout();

        GridLayout grpQueryFieldLayout = getGrpQueryFieldLayout();

        GridLayout gridSetSize = new GridLayout(2, false);
        gridSetSize.horizontalSpacing = 4;

        GridLayout gridQryLength = new GridLayout(2, false);
        gridQryLength.horizontalSpacing = 4;

        GridLayout lblGridLayout = new GridLayout(1, false);
        lblGridLayout.marginLeft = 15;

        Group parentGroup = new Group(parent, SWT.FILL);
        parentGroup.setLayout(new GridLayout(1, true));
        GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true);
        grid.minimumWidth = 150;
        parentGroup.setLayoutData(grid);

        Composite compHistorySize = new Composite(parentGroup, SWT.FILL);
        compHistorySize.setLayout(grpHistoryFieldLayout);

        Composite compSetHistorySize = new Composite(compHistorySize, SWT.FILL);
        Label lblSetHistorySize = new Label(compSetHistorySize, SWT.BOLD);
        lblSetHistorySize.setText(MessageConfigLoader.getProperty(IMessagesConstants.SET_SQL_SIZE));
        compSetHistorySize.setLayout(gridSetSize);

        addHistSizeTxt(compSetHistorySize);

        addMinMaxDefaultLbl(compHistorySize);
        compHistorySize.pack();
        addHistoryDescLbl(lblGridLayout, parentGroup);

        Composite compQueryLength = new Composite(parentGroup, SWT.FILL);
        compQueryLength.setLayout(grpQueryFieldLayout);

        addsetQueryLengthlbl(gridQryLength, compQueryLength);
        compQueryLength.pack();

        addQueryLengthLbl(lblGridLayout, parentGroup);
        addErrorMsgLables(parent);

        verifyHistoryFieldsListener();
        verifyQueryListener();

        return parent;
    }

    /**
     * Adds the query length lbl.
     *
     * @param lblGridLayout the lbl grid layout
     * @param parentGroup the parent group
     */
    private void addQueryLengthLbl(GridLayout lblGridLayout, Group parentGroup) {
        Composite labelQueryLengthComp = new Composite(parentGroup, SWT.NONE);
        labelQueryLengthComp.setLayout(lblGridLayout);
        Label lblQueryDescription1 = new Label(labelQueryLengthComp, NONE);
        lblQueryDescription1.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_DESC_LABEL1));
    }

    /**
     * Adds the hist size txt.
     *
     * @param compSetHistorySize the comp set history size
     */
    private void addHistSizeTxt(Composite compSetHistorySize) {
        textHistSize = new Text(compSetHistorySize, SWT.BORDER | SWT.SINGLE);
        setInitTextProperties(textHistSize);

        textHistSize
                .setText(Integer.toString(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE)));

        textHistSize.addVerifyListener(new TextLengthVerifyListner());
    }

    /**
     * Addset query lengthlbl.
     *
     * @param gridQryLength the grid qry length
     * @param compQueryLength the comp query length
     */
    private void addsetQueryLengthlbl(GridLayout gridQryLength, Composite compQueryLength) {
        Composite compSetQueryLength = new Composite(compQueryLength, SWT.FILL);
        Label lblSetQueryLength = new Label(compSetQueryLength, SWT.BOLD);
        lblSetQueryLength.setAlignment(SWT.LEFT);
        lblSetQueryLength.setText(MessageConfigLoader.getProperty(IMessagesConstants.SET_SQL_QUERY_LENGTH));
        textQueryLength = new Text(compSetQueryLength, SWT.BORDER);
        setInitTextProperties(textQueryLength);

        textQueryLength
                .setText(Integer.toString(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH)));
        textQueryLength.addVerifyListener(new TextLengthVerifyListner());
        compSetQueryLength.setLayout(gridQryLength);
        compSetQueryLength.pack();
        addMinMaxDefaultQueryLbl(compQueryLength);
    }

    /**
     * Adds the history desc lbl.
     *
     * @param lblGridLayout the lbl grid layout
     * @param parentGroup the parent group
     */
    private void addHistoryDescLbl(GridLayout lblGridLayout, Group parentGroup) {
        Composite labelHistorySizeComp = new Composite(parentGroup, SWT.FILL);
        labelHistorySizeComp.setLayout(lblGridLayout);
        Label lblHistoryDescription1 = new Label(labelHistorySizeComp, SWT.FILL);
        lblHistoryDescription1.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_SIZE_DESC_LABEL));
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
     * Adds the min max default query lbl.
     *
     * @param compQueryLength the comp query length
     */
    private void addMinMaxDefaultQueryLbl(Composite compQueryLength) {
        Label lblMinMaxDefaultQuery = new Label(compQueryLength, NONE);
        lblMinMaxDefaultQuery.setText("(" + MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_RANGE)
                + MPPDBIDEConstants.SPACE_CHAR + SQL_SIZE_MIN_VALUE + MPPDBIDEConstants.SEPARATOR
                + SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT + ";" + MPPDBIDEConstants.SPACE_CHAR
                + MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_VALUE) + MPPDBIDEConstants.SPACE_CHAR
                + SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT + ";" + MPPDBIDEConstants.SPACE_CHAR
                + MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_LENGTH_NO_LIMIT)
                + MPPDBIDEConstants.SPACE_CHAR + SQL_QUERY_NO_LIMIT + ")");
        new Label(compQueryLength, NONE);
    }

    /**
     * Adds the min max default lbl.
     *
     * @param compHistorySize the comp history size
     */
    private void addMinMaxDefaultLbl(Composite compHistorySize) {
        Label lblMinMaxDefault = new Label(compHistorySize, SWT.FILL);
        lblMinMaxDefault.setText("(" + MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_RANGE)
                + MPPDBIDEConstants.SPACE_CHAR + SQL_SIZE_MIN_VALUE + MPPDBIDEConstants.SEPARATOR + ' '
                + SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT + ";" + MPPDBIDEConstants.SPACE_CHAR
                + MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_VALUE) + MPPDBIDEConstants.SPACE_CHAR
                + SQL_HISTORY_DEFAULT_SIZE_COUNT + ")");
        new Label(compHistorySize, SWT.FILL);
    }

    /**
     * Gets the grp query field layout.
     *
     * @return the grp query field layout
     */
    private GridLayout getGrpQueryFieldLayout() {
        GridLayout grpQueryFieldLayout = new GridLayout(5, false);
        grpQueryFieldLayout.horizontalSpacing = 6;
        grpQueryFieldLayout.marginWidth = 0;
        grpQueryFieldLayout.marginHeight = 0;
        grpQueryFieldLayout.verticalSpacing = 0;
        grpQueryFieldLayout.marginBottom = 0;
        return grpQueryFieldLayout;
    }

    /**
     * Gets the grp history field layout.
     *
     * @return the grp history field layout
     */
    private GridLayout getGrpHistoryFieldLayout() {
        GridLayout grpHistoryFieldLayout = new GridLayout(4, false);
        grpHistoryFieldLayout.horizontalSpacing = 8;
        grpHistoryFieldLayout.marginWidth = 0;
        grpHistoryFieldLayout.marginHeight = 0;
        grpHistoryFieldLayout.verticalSpacing = 0;
        grpHistoryFieldLayout.marginBottom = 0;
        return grpHistoryFieldLayout;
    }

    /**
     * Adds the error msg lables.
     *
     * @param parent the parent
     */
    private void addErrorMsgLables(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));

        lblHistoryErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblHistoryErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblHistoryErrorMsg.setBackground(compErrorMsg.getBackground());
        lblHistoryErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_SIZE_ERROR_MSG));
        lblHistoryErrorMsg.setVisible(false);

        lblQueryErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblQueryErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblQueryErrorMsg.setBackground(compErrorMsg.getBackground());
        lblQueryErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_ERROR_MSG));
        lblQueryErrorMsg.setVisible(false);
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
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        convertTexttoString();
        if (validateQueryAndHistoryFields()) {
            if (isConfigurationChanged()) {
                if (generatePopUpForMemoryDiskUsageAndDataLoss()) {
                    return false;
                }

            } else if (handleChangedData(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE),
                    Integer.parseInt(textHistSize.getText()))
                    || handleChangedData(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH),
                            Integer.parseInt(textQueryLength.getText()))
                            && Integer.parseInt(textQueryLength.getText()) != MPPDBIDEConstants.ZER0) {
                if (generatePopUPForDataLoss() != IDialogConstants.OK_ID) {
                    return false;

                }
            }

            else if (isSQLQueryLengthModified(
                    preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH),
                    Integer.parseInt(textQueryLength.getText()))
                    && MPPDBIDEConstants.ZER0 == Integer.parseInt(textQueryLength.getText())) {
                if (generatePopUpForMemoryDiskUsage()) {
                    return false;
                }

            }
            if (preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE) != filteredHistorySize) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "SQL History query count that can be saved in Preferences settings is changed from %d to %d",
                        preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE),
                        filteredHistorySize));
            }
            preferenceStore.setValue(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE, filteredHistorySize);
            setSQLCharLengthHistory();
            preferenceStore.setValue(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH, filteredQueryLength);
            PreferenceWrapper.getInstance().setPreferenceApply(true);
        } else if (filteredQueryLength == MPPDBIDEConstants.ZER0 && !lblHistoryErrorMsg.getVisible()) {
            setSQLCharLengthHistory();
            preferenceStore.setValue(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH, filteredQueryLength);
            PreferenceWrapper.getInstance().setPreferenceApply(true);

        } else {
            PreferenceWrapper.getInstance().setPreferenceApply(false);

        }

        return super.performOk();

    }

    private void setSQLCharLengthHistory() {
        if (preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH) != filteredQueryLength) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Query Characters to be saved in SQL history of Preferences settings is changed from %d to %d",
                    preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH), filteredQueryLength));
        }
    }

    /**
     * Checks if is configuration changed.
     *
     * @return true, if is configuration changed
     */
    private boolean isConfigurationChanged() {
        return handleChangedData(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE),
                Integer.parseInt(textHistSize.getText()))
                && handleChangedData(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH),
                        Integer.parseInt(textQueryLength.getText()))
                && Integer.parseInt(textQueryLength.getText()) == MPPDBIDEConstants.ZER0;
    }

    /**
     * Validate query and history fields.
     *
     * @return true, if successful
     */
    private boolean validateQueryAndHistoryFields() {
        return validateHistoryTextFields() && validateQueryTextFields();
    }

    /**
     * Handle changed data.
     *
     * @param oldvalue the oldvalue
     * @param newValue the new value
     * @return true, if successful
     */
    private boolean handleChangedData(int oldvalue, int newValue) {
        if (newValue < oldvalue) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is SQL query length modified.
     *
     * @param oldvalue the oldvalue
     * @param newValue the new value
     * @return true, if is SQL query length modified
     */
    private boolean isSQLQueryLengthModified(int oldvalue, int newValue) {
        if (newValue != oldvalue) {
            return true;
        }
        return false;
    }

    /**
     * Generate pop up for memory disk usage.
     *
     * @return true, if successful
     */
    private boolean generatePopUpForMemoryDiskUsage() {
        return IDialogConstants.OK_ID != MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_SETTING_ZERO_DATA_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_SETTING_ZERO_DATA_BODY),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                    MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                1);
    }

    /**
     * Generate pop up for memory disk usage and data loss.
     *
     * @return true, if successful
     */
    private boolean generatePopUpForMemoryDiskUsageAndDataLoss() {
        return IDialogConstants.OK_ID != MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_SQL_HISTORY_SETTING_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUERY_SETTING_COMBO_DATA_BODY),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                    MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                1);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {

        textHistSize.setText(SQL_HISTORY_DEFAULT_SIZE_COUNT);
        textHistSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        textQueryLength.setText(SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT);
        textQueryLength.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblHistoryErrorMsg.setVisible(false);
        lblQueryErrorMsg.setVisible(false);
        PreferenceWrapper.getInstance().setDefaultStore(true);

        getDefaultsButton().setEnabled(false);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility
                .operationInfo("SQL History count and characters in Preferences setting is restored to defaults");
        return;
    }

    /**
     * Validate history text fields.
     *
     * @return true, if successful
     */
    public boolean validateHistoryTextFields() {
        if (null != textHistSize) {
            try {
                if (textHistSize.getText().isEmpty()) {
                    setErrorMessage(textHistSize, lblHistoryErrorMsg);
                    return false;

                }

                if (Integer.parseInt(textHistSize.getText()) >= Integer.parseInt(SQL_SIZE_MIN_VALUE)
                        && (Integer.parseInt(textHistSize.getText()) <= Integer.parseInt(SQL_HISTORY_MAX_SIZE_COUNT))) {
                    filteredHistorySize = Integer.parseInt(textHistSize.getText());
                    errorFlag = false;
                    textHistSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                    lblHistoryErrorMsg.setVisible(false);
                    return true;
                }

                else {
                    setErrorMessage(textHistSize, lblHistoryErrorMsg);
                    return false;

                }

            } catch (NumberFormatException e) {
                setErrorMessage(textHistSize, lblHistoryErrorMsg);

            }

        }
        return false;
    }

    /**
     * Validate query text fields.
     *
     * @return true, if successful
     */
    public boolean validateQueryTextFields() {
        if (null != textQueryLength) {
            try {
                if (textQueryLength.getText().isEmpty()) {
                    setErrorMessage(textQueryLength, lblQueryErrorMsg);
                    return false;

                }

                if (Integer.parseInt(textQueryLength.getText()) >= Integer.parseInt(SQL_QUERY_NO_LIMIT)
                        && (Integer.parseInt(textQueryLength.getText()) <= Integer
                                .parseInt(SQL_HISTORY_MAX_DEFAULT_QUERY_COUNT))) {
                    filteredQueryLength = Integer.parseInt(textQueryLength.getText());
                    textQueryLength.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                    lblQueryErrorMsg.setVisible(false);
                    errorFlag = false;
                    return true;
                } else {
                    setErrorMessage(textQueryLength, lblQueryErrorMsg);

                }
            } catch (NumberFormatException e) {
                setErrorMessage(textQueryLength, lblQueryErrorMsg);
            }
        }
        return false;
    }

    /**
     * Sets the error message.
     *
     * @param inputText the input text
     * @param errorLbl the error lbl
     */
    public void setErrorMessage(Text inputText, Label errorLbl) {
        inputText.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        errorLbl.setVisible(true);
        getApplyButton().setEnabled(false);
        errorFlag = true;
        PreferenceWrapper.getInstance().setPreferenceApply(false);

    }

    /**
     * Generate pop UP for data loss.
     *
     * @return the int
     */
    public int generatePopUPForDataLoss() {

        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_SQL_HISTORY_SETTING_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_SQL_HISTORY_SETTING_BODY),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                    MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                1);
        return userChoice;
    }

    /**
     * Ok to leave.
     *
     * @return true, if successful
     */
    @Override
    public boolean okToLeave() {
        int choice;
        if (isFieldsModified()) {
            choice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.WARNING, true,
                    IconUtility.getIconImage(IiconPath.SQL_HISTORY1, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.UPDATE_SQL_HISTORY_SETTING_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_SETTING_UNSAVED_DATA_BODY),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.YES_OPTION),
                        MessageConfigLoader.getProperty(IMessagesConstants.NO_OPTION)},
                    1);
            // Check whether any error msg is not present and user has selected
            // ok button
            if ((choice == IDialogConstants.OK_ID)
                    && (!lblHistoryErrorMsg.getVisible() && !lblQueryErrorMsg.getVisible())) {
                performOk();
                return true;
            }

            else {
                rollBackChanges();
                return super.okToLeave();
            }
        }

        return true;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TextLengthVerifyListner.
     */
    // To restrict the length of the text
    private static final class TextLengthVerifyListner implements VerifyListener {

        @Override
        public void verifyText(VerifyEvent event) {
            String textStr = ((Text) event.widget).getText() + event.text;
            try {

                if (textStr.length() > 5400) {
                    event.doit = false;
                }

            } catch (NumberFormatException e) {
                event.doit = false;
            }

        }

    }

    /**
     * Verify history fields listener.
     */
    public void verifyHistoryFieldsListener() {

        if (null != textHistSize) {
            textHistSize.addKeyListener(new KeyListener() {

                @Override
                public void keyReleased(KeyEvent event) {
                    validateHistoryTextFields();
                    if (!errorFlag) {
                        validateQueryTextFields();
                    }
                    enableDisableApplyButton();

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }
            });
        }

    }

    /**
     * Verify query listener.
     */
    public void verifyQueryListener() {
        if (null != textQueryLength) {
            textQueryLength.addKeyListener(new KeyListener() {

                @Override
                public void keyReleased(KeyEvent event) {
                    validateQueryTextFields();
                    if (!errorFlag) {
                        validateHistoryTextFields();
                    }
                    enableDisableApplyButton();

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }
            });
        }

    }

    /**
     * Convert textto string.
     */
    private void convertTexttoString() {
        if (null != textHistSize && null != textQueryLength) {
            try {
                if (!textHistSize.getText().isEmpty() && !textQueryLength.getText().isEmpty()) {
                    filteredHistorySize = Integer.parseInt(textHistSize.getText());
                    filteredQueryLength = Integer.parseInt(textQueryLength.getText());
                }
            } catch (NumberFormatException e) {
                setErrorMessage(textHistSize, lblHistoryErrorMsg);
                setErrorMessage(textQueryLength, lblQueryErrorMsg);

            }
        }
    }

    /**
     * Checks if is fields modified.
     *
     * @return true, if is fields modified
     */
    private boolean isFieldsModified() {
        if ((textHistSize.getText()
                .equals(String.valueOf(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE))))
                && (textQueryLength.getText().equals(
                        String.valueOf(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH))))) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Roll back changes.
     */
    private void rollBackChanges() {

        textHistSize
                .setText(Integer.toString(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE)));
        textHistSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblHistoryErrorMsg.setVisible(false);
        textQueryLength
                .setText(Integer.toString(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH)));
        textQueryLength.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblQueryErrorMsg.setVisible(false);
        getApplyButton().setEnabled(true);

    }

    /**
     * Enable disable apply button.
     */
    private void enableDisableApplyButton() {
        if (errorFlag || (textHistSize.getText().isEmpty() || textQueryLength.getText().isEmpty()) || (textHistSize
                .getText()
                .equals(String.valueOf(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE)))
                && textQueryLength.getText().equals(
                        String.valueOf(preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH))))) {

            getApplyButton().setEnabled(false);
            return;
        } else {
            getApplyButton().setEnabled(true);
        }
    }

    /**
     * Sets the inits the text properties.
     *
     * @param ctrl the new inits the text properties
     */
    private static void setInitTextProperties(Text ctrl) {
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = 60;
        ctrl.setLayoutData(gd);

        ctrl.setText("");

    }

    /**
     * Validate history size.
     *
     * @param preferenceStore the preference store
     */
    public static void validateSQLHistoryInfo(PreferenceStore preferenceStore) {
        int sqlHistory = preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE);
        if (!(sqlHistory <= SQL_HISTORY_MAX_SIZE_COUNT_INT && sqlHistory >= SQL_SIZE_MIN_VALUE_INT)) {
            preferenceStore.setToDefault(ISQLHistoryPreferencesLabelFactory.SQL_HISTORY_SIZE);
        }
        int queryLength = preferenceStore.getInt(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH);
        if (queryLength != 0
                && !(queryLength <= SQL_HISTORY_MAX_SIZE_COUNT_INT && queryLength >= SQL_SIZE_MIN_VALUE_INT)) {
            preferenceStore.setToDefault(ISQLHistoryPreferencesLabelFactory.SQL_QUERY_LENGTH);
        }
    }

}
