/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.util.Locale;
import java.util.Objects;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
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

/**
 * 
 * Title: class AutoCompletePreferencePage
 * 
 * Description: The Class AutoCompletePreferencePage.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author swx316469
 * @version [DataStudio 8.0.0, 28 Aug, 2019]
 * @since 28 Aug, 2019
 */
public class AutoCompletePreferencePage extends PreferencePage {

    private IPreferenceStore preferenceStore;
    private Text textWordSize;
    private Label lblHistoryErrorMsg;

    /**
     * Instantiates a new auto complete preference page.
     */
    public AutoCompletePreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.AUTO_COMPLETE_SETTING));
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

        Group parentGroup = new Group(parent, SWT.FILL);
        parentGroup.setLayout(new GridLayout(1, true));
        GridData grid = new GridData(SWT.FILL, SWT.FILL, true, true);
        parentGroup.setLayoutData(grid);

        Composite mainComp = new Composite(parentGroup, SWT.NONE);
        mainComp.setLayoutData(grid);
        mainComp.setLayout(new GridLayout(1, true));

        Composite autoSuggestComp = new Composite(mainComp, SWT.NONE);
        GridData grid1 = new GridData(SWT.FILL, SWT.TOP, true, false);

        autoSuggestComp.setLayoutData(grid1);
        autoSuggestComp.setLayout(new GridLayout(3, false));

        Label autoSuggestTitle = new Label(autoSuggestComp, SWT.BOLD);
        autoSuggestTitle.setText(MessageConfigLoader.getProperty(IMessagesConstants.AUTO_COMPLETE_MIN_SIZE));

        textWordSize = new Text(autoSuggestComp, SWT.BORDER);
        textWordSize.setText(
                Integer.toString(preferenceStore.getInt(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY)));
        textWordSize.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        textWordSize.addVerifyListener(textWordVerifyListener());

        Label rangeLabel = new Label(autoSuggestComp, SWT.BOLD);
        rangeLabel.setText("(" + MessageConfigLoader.getProperty(IMessagesConstants.SQL_HISTORY_RANGE)
                + MPPDBIDEConstants.SPACE_CHAR + 2 + MPPDBIDEConstants.SEPARATOR + ' ' + 10 + ";"
                + MPPDBIDEConstants.SPACE_CHAR + MessageConfigLoader.getProperty(IMessagesConstants.DEFAULT_VALUE)
                + MPPDBIDEConstants.SPACE_CHAR + 2 + ")");

        mainComp.pack();

        Composite autoSuggestDescComp = new Composite(mainComp, SWT.FILL);
        autoSuggestDescComp.setLayout(new GridLayout(1, true));

        Label descLabel = new Label(autoSuggestDescComp, SWT.WRAP | SWT.BOLD);
        descLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.AUTO_COMPLETE_MIN_SIZE_DESC));

        addErrorMsgLables(parent);
        return parent;
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
    
    private VerifyListener textWordVerifyListener() {
        return new VerifyListener() {

            @Override
            public void verifyText(VerifyEvent event) {

                String oldValue = textWordSize.getText();
                if (event.keyCode == 8 || event.keyCode == 0 || event.keyCode == 127
                        || Character.isDigit(event.character)) {

                    if (lblHistoryErrorMsg.isVisible()) {
                        lblHistoryErrorMsg.setVisible(false);
                        textWordSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                        getApplyButton().setEnabled(true);
                    }

                    String newValue = oldValue.substring(0, event.start) + event.text + oldValue.substring(event.end);
                    if (Objects.isNull(newValue) || newValue.isEmpty()) {
                        setErrorMessage();
                        return;
                    }

                    try {
                        int newSize = Integer.parseInt(newValue);
                        if (newSize <= 1) {
                            setErrorMessage();
                            return;
                        }
                        if (newSize > 10) {
                            event.doit = false;
                            if (Integer.parseInt(oldValue) < 2) {
                                setErrorMessage();
                            }
                            return;
                        }
                        getApplyButton().setEnabled(true);
                    } catch (final NumberFormatException numberFormatException) {
                        getApplyButton().setEnabled(false);
                        setErrorMessage();
                        event.doit = false;
                        return;
                    }

                } else {
                    event.doit = false;
                    return;
                }

            }
        };
    }

    /**
     * Sets the error message.
     */
    public void setErrorMessage() {
        textWordSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblHistoryErrorMsg.setVisible(true);
        lblHistoryErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.AUTO_SUGGEST_ERROR_MSG));
        getApplyButton().setEnabled(false);

        PreferenceWrapper.getInstance().setPreferenceApply(false);

    }

    private void addErrorMsgLables(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));
        compErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        lblHistoryErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblHistoryErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblHistoryErrorMsg.setBackground(compErrorMsg.getBackground());
        lblHistoryErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.SQL_SIZE_ERROR_MSG));
        lblHistoryErrorMsg.setVisible(false);
        lblHistoryErrorMsg.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

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
        textWordSize.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        lblHistoryErrorMsg.setVisible(false);
        textWordSize.setText(
                Integer.toString(preferenceStore.getDefaultInt(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY)));

        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Minimum characters for Auto Suggest in preferences is set to Default: 2");
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (lblHistoryErrorMsg != null && !lblHistoryErrorMsg.isDisposed() && lblHistoryErrorMsg.isVisible()) {
            return false;
        }
        if (textWordSize != null && !textWordSize.isDisposed()) {
            if (preferenceStore.getInt(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY) != Integer
                    .parseInt(textWordSize.getText())) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Minimum characters for Auto Suggest in preferences changed from %d to %d",
                        preferenceStore.getInt(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY),
                        Integer.parseInt(textWordSize.getText())));
            }
            preferenceStore.setValue(IAutoCompletePreference.AUTO_COMPLETE_PREFERENCE_KEY,
                    Integer.parseInt(textWordSize.getText()));
        }
        return true;

    }

}
