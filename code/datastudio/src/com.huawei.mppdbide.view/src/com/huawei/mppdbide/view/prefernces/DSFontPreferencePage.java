/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.prefernces;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import com.huawei.mppdbide.view.core.sourceeditor.PLSourceEditorCore;
import com.huawei.mppdbide.view.ui.PLSourceEditor;
import com.huawei.mppdbide.view.ui.autosave.IAutoSaveObject;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: DSFontPreferencePage
 * 
 * Description:preference page of editor - font
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author lijialiang(l00448174)
 * @version [DataStudio 6.5.1, Aug 28, 2019]
 * @since Aug 28, 2019
 */

@SuppressWarnings("restriction")
public class DSFontPreferencePage extends PreferencePage {

    /**
     * The Constant FONT_SIZE_DEFAULT.
     */
    public static final int FONT_SIZE_DEFAULT = 10;

    /**
     * The Constant FONT_SIZE_MAX.
     */
    public static final int FONT_SIZE_MAX = 50;

    /**
     * The Constant FONT_SIZE_MIN.
     */
    public static final int FONT_SIZE_MIN = 1;

    private IPreferenceStore preferenceStore;
    private Text sizeText;
    private Label sizeExplaination;

    /**
     * Instantiates a new DS font preference page.
     */
    public DSFontPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FONT_SETTING));
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

        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));

        Group group = new Group(comp, SWT.NONE);
        group.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FONT_STYLE));
        group.setLayout(new GridLayout(3, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Label sizeLabel = new Label(group, SWT.NONE);
        sizeLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FONT_STYLE_SIZE));

        sizeText = new Text(group, SWT.BORDER);
        sizeText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeText.setText(preferenceStore.getString(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE));
        sizeText.addVerifyListener(new VerifyFontSize());

        sizeExplaination = new Label(group, SWT.NONE);
        sizeExplaination.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        sizeExplaination.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_FONT_STYLE_SIZE_EXPLANATION));

        return comp;
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
        Button applyButton = getApplyButton();
        applyButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
        applyButton.setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (preferenceStore != null) {
            if (!isUserInputValid()) {
                return false;
            }
            if (!preferenceStore.getString(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE).equals(sizeText.getText())) {
                MPPDBIDELoggerUtility.operationInfo(
                        String.format(Locale.ENGLISH, "Font size in preferences has been set from %s to %s",
                                preferenceStore.getString(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE), sizeText.getText()));
            }
            preferenceStore.setValue(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE, sizeText.getText());

            postUISetup();
        }
        return true;
    }

    private void postUISetup() {
        List<IAutoSaveObject> terminals = UIElement.getInstance().getAllOpenTerminals();

        for (IAutoSaveObject terminal : terminals) {
            if (terminal instanceof SQLTerminal) {
                PLSourceEditorCore terminalCore = ((SQLTerminal) terminal).getTerminalCore();
                if (terminalCore != null) {
                    terminalCore.resetFont();
                }
            }
        }

        List<PLSourceEditor> allOpenedSourceViewer = UIElement.getInstance().getAllOpenedSourceViewer();
        for (PLSourceEditor plSourceEditor : allOpenedSourceViewer) {
            PLSourceEditorCore terminalCore = plSourceEditor.getTerminalCore();
            if (terminalCore != null) {
                terminalCore.resetFont();
            }
        }
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        getApplyButton().setEnabled(false);
        performOk();
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        sizeText.setText(String.valueOf(FONT_SIZE_DEFAULT));
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Font size in preferences is set to default value:10");
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(IPreferenceStore preferenceStore) {
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE, FONT_SIZE_DEFAULT);
    }

    private boolean isUserInputValid() {
        if (Objects.isNull(sizeExplaination)) {
            return false;
        }

        if (Objects.isNull(sizeText) || sizeText.getText().isEmpty()) {
            sizeExplaination.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
            return false;
        }
        return true;
    }

    private final class VerifyFontSize implements VerifyListener {

        @Override
        public void verifyText(VerifyEvent e) {
            sizeExplaination.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            String oldValue = sizeText.getText();
            String newValue = oldValue.substring(0, e.start) + e.text + oldValue.substring(e.end);
            if (Objects.isNull(newValue) || newValue.isEmpty()) {
                getApplyButton().setEnabled(false);
                return;
            }
            try {
                // Validates the input is between 1 and FONT_SIZE_MAX
                int newSize = Integer.parseInt(newValue);
                if (newSize > FONT_SIZE_MAX || newSize < FONT_SIZE_MIN) {
                    e.doit = false;
                    return;
                }
                getApplyButton().setEnabled(true);
            } catch (final NumberFormatException numberFormatException) {
                e.doit = false;
            }
        }
    }

}
