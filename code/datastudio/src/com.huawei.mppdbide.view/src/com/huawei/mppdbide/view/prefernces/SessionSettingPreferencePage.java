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

import java.util.ArrayList;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.WorkbenchPlugin;

import com.huawei.mppdbide.utils.DsEncodingEnum;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.autosave.AutoSaveManager;
import com.huawei.mppdbide.view.utils.Preferencekeys;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

/**
 * 
 * Title: class
 * 
 * Description: The Class SessionSettingPreferencePage.
 * 
 * @since 3.0.0
 */
public class SessionSettingPreferencePage extends PreferencePage implements Preferencekeys {

    private static final int AUTOSAVE_MIN_INTERVAL = 2;

    private static final int AUTOSAVE_MAX_INTERVAL = 60;

    private static final int OBJECT_COUNT_MIN = 1000;

    private static final int OBJECT_COUNT_MAX = 100000;

    private IPreferenceStore preferenceStore;

    private ArrayList<String> dsEncodingList;

    private Combo dsEncodingCombo;

    private ArrayList<String> fileEncodingList;

    private Combo fileEncodingCombo;

    private Button enableAssistantBtn;

    private Button disableAssistantBtn;

    private Button autosaveBtn;

    private Button encryptionBtn;

    private Text autosaveIntervalTxt;

    private Label lblAutosaveErrorMsg;

    private int modifiedInterval;

    private boolean isNeedRestart = false;

    private Text importFileData;

    private Text importTableData;

    private Text importByteaData;

    private Text objectCountInOb;

    private int modifiedObjectCount;

    private Label lblObjectCountErrorMsg;

    private Label lblErrorMsg;

    /**
     * Instantiates a new session setting preference page.
     */
    public SessionSettingPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.SESSION_SETTNG_NODE));
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
        getPreferenceStoreIfNotExists();

        Composite comp = new Composite(parent, SWT.NONE);
        comp.setLayout(new GridLayout(1, false));

        GridData gdComposite = getGridData1(comp);

        addEncodingPrefUI(comp, gdComposite);

        addSqlAssistPrefUi(comp);

        getGridData(comp);

        Group autosaveGrp = getAutoSaveGroup(comp);

        // AutoSave Button
        addAutoSavePrefUi(autosaveGrp);

        // Encryption Button
        addEncryptionPrefUi(autosaveGrp);

        addAutoSaveErrorMsgUi(parent);
        addDataLimitErrorMsgUi(parent);
        addObjectCountErrorMsgUi(parent);
        verifyIntervalTextListener();

        Group fileLimit = getFileLimitGroup(comp);

        Composite fileLimitComp = new Composite(fileLimit, SWT.NONE);
        fileLimitComp.setLayout(new GridLayout(3, false));
        fileLimitComp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Label importTaleLable = new Label(fileLimitComp, SWT.NONE);
        importTaleLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_TABLE_DATA_LIMIT));

        addImportTableDataOptions(fileLimitComp);

        importFileData = importDataLimit(fileLimitComp, preferenceStore.getInt(Preferencekeys.FILE_LIMIT_FOR_SQL));

        Label zeroHint1 = new Label(fileLimitComp, SWT.NONE);
        zeroHint1.setText(MessageConfigLoader.getProperty(IMessagesConstants.SUFFIX_DATA_LIMIT));
        verifyDataLimit(importFileData);

        addImportByteaSize(fileLimitComp);
        addWarningNote(fileLimit);
        addLazyRenderingOptionForOb(comp);

        return comp;
    }

    private void addWarningNote(Group fileLimit) {
        Composite note = new Composite(fileLimit, SWT.NONE);
        note.setLayout(new GridLayout(1, false));
        note.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        Label notLabel = new Label(note, SWT.NONE);
        notLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_UNLIMITED_NOTE));
    }

    private void addImportByteaSize(Composite fileLimitComp) {
        Label importBytea = new Label(fileLimitComp, SWT.NONE);
        importBytea.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_BYTEA_DATA_LIMIT));
        importByteaData = importDataLimit(fileLimitComp, preferenceStore.getInt(Preferencekeys.FILE_LIMIT_FOR_BYTEA));
        Label zeroHint = new Label(fileLimitComp, SWT.NONE);
        zeroHint.setText(MessageConfigLoader.getProperty(IMessagesConstants.SUFFIX_DATA_LIMIT));
        verifyDataLimit(importByteaData);
    }

    private void addImportTableDataOptions(Composite fileLimitComp) {
        importTableData = importDataLimit(fileLimitComp,
                preferenceStore.getInt(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA));
        Label zeroHint = new Label(fileLimitComp, SWT.NONE);
        zeroHint.setText(MessageConfigLoader.getProperty(IMessagesConstants.SUFFIX_DATA_LIMIT));
        verifyDataLimit(importTableData);
        Label importFileLable = new Label(fileLimitComp, SWT.NONE);
        importFileLable.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_FILE_DATA_LIMIT));
    }

    private GridData getGridData1(Composite comp) {
        GridData gdComposite = new GridData(SWT.FILL, SWT.NONE, true, false);
        gdComposite.horizontalIndent = 0;
        gdComposite.verticalIndent = 0;
        comp.setLayoutData(gdComposite);
        return gdComposite;
    }

    private Group getAutoSaveGroup(Composite comp) {
        Group autosaveGrp = new Group(comp, SWT.NONE);
        autosaveGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_AUTOSAVE));
        autosaveGrp.setLayout(new GridLayout());
        autosaveGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return autosaveGrp;
    }

    private void getGridData(Composite comp) {
        /* AutoSave Preferences in Session Setting */
        GridData gdComposite3 = new GridData(SWT.LEFT, SWT.UP, false, false, 1, 1);
        gdComposite3.heightHint = 500;
        gdComposite3.widthHint = 500;
        gdComposite3.horizontalIndent = 0;
        gdComposite3.verticalIndent = 0;
        comp.setLayoutData(gdComposite3);
    }

    private Group getFileLimitGroup(Composite comp) {
        Group fileLimit = new Group(comp, SWT.NONE);
        fileLimit.setText(MessageConfigLoader.getProperty(IMessagesConstants.FILE_LIMIT));
        fileLimit.setLayout(new GridLayout());
        fileLimit.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));
        return fileLimit;
    }

    /**
     * Add UI for lazy rendering preference
     * 
     * @param parent composite
     */
    private void addLazyRenderingOptionForOb(Composite comp) {
        Group lazyRenderOption = new Group(comp, SWT.NONE);
        lazyRenderOption.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAZY_RENDER_PREFERENCE_GROUP_NAME));
        lazyRenderOption.setLayout(new GridLayout());
        lazyRenderOption.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Composite lazyRenderSubOption = new Composite(lazyRenderOption, SWT.NONE);
        lazyRenderSubOption.setLayout(new GridLayout(3, false));
        lazyRenderSubOption.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        Label objectCountLabel = new Label(lazyRenderSubOption, SWT.NONE);
        objectCountLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAZY_RENDER_OBJECT_COUNT_LABEL));

        objectCountInOb = importDataLimit(lazyRenderSubOption,
                preferenceStore.getInt(Preferencekeys.OBECT_COUNT_FOR_LAZY_RENDERING));
        Label zeroHint = new Label(lazyRenderSubOption, SWT.NONE);
        zeroHint.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAZY_RENDER_SUFFIX_LABEL));
        verifyLazyRenderingDataLimit();
    }

    private Text importDataLimit(Composite fileLimitSubComp1, int limit) {
        Text txt = new Text(fileLimitSubComp1, SWT.BORDER);
        GridData gd = new GridData();
        gd.widthHint = 60;
        txt.setLayoutData(gd);
        txt.setText(String.valueOf(limit));
        txt.setTextLimit(8);
        txt.addListener(SWT.Verify, new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                String string = e.text;
                char[] chars = new char[string.length()];
                string.getChars(0, chars.length, chars, 0);
                for (int i = 0; i < chars.length; i++) {
                    if (!('0' <= chars[i] && chars[i] <= '9')) {
                        e.doit = false;
                        return;
                    }
                }
            }
        });
        txt.addModifyListener(new ModifyListener() {

            /**
             * Modify text.
             *
             * @param event the event
             */
            public void modifyText(ModifyEvent event) {
                enableDisableApplyButton();
            }
        });
        return txt;
    }

    /**
     * Adds the auto save error msg ui.
     *
     * @param parent the parent
     */
    private void addAutoSaveErrorMsgUi(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));
        lblAutosaveErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblAutosaveErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblAutosaveErrorMsg.setBackground(compErrorMsg.getBackground());
        lblAutosaveErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_INTERVAL_ERROR_MSG,
                AUTOSAVE_MIN_INTERVAL, AUTOSAVE_MAX_INTERVAL));
        lblAutosaveErrorMsg.setVisible(false);
    }

    /**
     * UI for Error message for lazy load object count
     * 
     * @param parent composite
     */
    private void addObjectCountErrorMsgUi(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));
        lblObjectCountErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblObjectCountErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblObjectCountErrorMsg.setBackground(compErrorMsg.getBackground());
        lblObjectCountErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.LAZY_RENDERING_ERROR_MSG,
                OBJECT_COUNT_MIN, OBJECT_COUNT_MAX));
        lblObjectCountErrorMsg.setVisible(false);
    }

    private void addDataLimitErrorMsgUi(Composite parent) {
        Composite compErrorMsg = new Composite(parent, SWT.NONE);
        compErrorMsg.setLayout(new GridLayout(1, false));
        lblErrorMsg = new Label(compErrorMsg, SWT.NONE);

        lblErrorMsg.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
        lblErrorMsg.setBackground(compErrorMsg.getBackground());
        lblErrorMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_TABLE_FILE_INVALID_DATA_LIMIT));
        lblErrorMsg.setVisible(false);
    }

    /**
     * Adds the encryption pref ui.
     *
     * @param autosaveGrp the autosave grp
     */
    private void addEncryptionPrefUi(Group autosaveGrp) {
        encryptionBtn = new Button(autosaveGrp, SWT.CHECK);
        encryptionBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_ENABLE_ENCRYPTION));
        encryptionBtn.setSelection(preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG));

        Label lblEncryptBtn = new Label(autosaveGrp, SWT.NONE);
        lblEncryptBtn
                .setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_ENCRYPTION_DESC));

        addAutoSaveSelectionListener();

        if (!autosaveBtn.getSelection()) {
            encryptionBtn.setEnabled(false);
            autosaveIntervalTxt.setEnabled(false);
        }
    }

    /**
     * Adds the auto save pref ui.
     *
     * @param autosaveGrp the autosave grp
     */
    private void addAutoSavePrefUi(Group autosaveGrp) {
        autosaveBtn = new Button(autosaveGrp, SWT.CHECK);
        autosaveBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_ENABLE_AUTOSAVE));
        autosaveBtn.setSelection(preferenceStore.getBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY));
        Label lblAutosaveBtnDesc = new Label(autosaveGrp, SWT.NONE);
        lblAutosaveBtnDesc
                .setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_AUTOSAVE_DESC));

        GridLayout grpAutosaveInterval = new GridLayout(4, false);
        grpAutosaveInterval.horizontalSpacing = 4;
        grpAutosaveInterval.marginWidth = 0;
        grpAutosaveInterval.marginHeight = 0;
        grpAutosaveInterval.verticalSpacing = 0;
        grpAutosaveInterval.marginBottom = 0;

        Composite compInterval = new Composite(autosaveGrp, SWT.FILL);
        compInterval.setLayout(grpAutosaveInterval);

        GridLayout gridSetSize = new GridLayout(2, false);
        gridSetSize.horizontalSpacing = 21;
        Composite compSetInterval = new Composite(compInterval, SWT.FILL);
        compSetInterval.setLayout(gridSetSize);

        Label lblInterval = new Label(compSetInterval, SWT.FILL);
        lblInterval.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_INTERVAL_AUTOSAVE));

        // AutoSave Interval Textbox
        autosaveIntervalTxt = new Text(compSetInterval, SWT.BORDER | SWT.SINGLE);
        setInitTextProperties(autosaveIntervalTxt);
        autosaveIntervalTxt.setText(Integer.toString(preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));

        Label lblIntrvlRange = new Label(compInterval, SWT.FILL);
        lblIntrvlRange.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_INTERVAL_AUTOSAVE_RANGE,
                AUTOSAVE_MIN_INTERVAL, AUTOSAVE_MAX_INTERVAL,
                preferenceStore.getDefaultInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));
        new Label(compInterval, SWT.FILL);
        compInterval.pack();

        Label lblIntervalDesc = new Label(autosaveGrp, SWT.NONE);
        lblIntervalDesc
                .setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_INTERVAL_DESC));
    }

    /**
     * Adds the sql assist pref ui.
     *
     * @param comp the comp
     */
    private void addSqlAssistPrefUi(Composite comp) {
        comp.setLayout(new GridLayout(1, false));

        getGridData(comp);

        Group assitantGrp = new Group(comp, SWT.NONE);
        assitantGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.DB_ASSISTANT_NAME));
        assitantGrp.setLayout(new GridLayout());
        assitantGrp.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

        enableAssistantBtn = new Button(assitantGrp, SWT.RADIO);
        enableAssistantBtn.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.DB_ASSISTANT_ENABLE));

        Label enableAssistantLabel = new Label(assitantGrp, SWT.NONE);
        enableAssistantLabel
                .setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.DB_ASSIST_ENABLE_DESC));

        disableAssistantBtn = new Button(assitantGrp, SWT.RADIO);

        disableAssistantBtn.setText(' ' + MessageConfigLoader.getProperty(IMessagesConstants.DB_ASSISTANT_DISABLE));

        Label disableAssistantLabel = new Label(assitantGrp, SWT.NONE);
        disableAssistantLabel
                .setText("      " + MessageConfigLoader.getProperty(IMessagesConstants.DB_ASSIST_DISABLE_DESC));

        enabledisableDbAssist();

        enableAssistantBtn.addSelectionListener(enableDbAssistSelectionListener());

        disableAssistantBtn.addSelectionListener(enableDbAssistSelectionListener());
    }

    /**
     * Adds the encoding pref UI.
     *
     * @param comp the comp
     * @param gdComposite the gd composite
     */
    private void addEncodingPrefUI(Composite comp, GridData gdComposite) {
        Group encodingGrp = getEncodingGroupUI(comp, gdComposite);
        addDsEncodingComboUi(encodingGrp);
        addFileEncodingComboUi(encodingGrp);
    }

    /**
     * Adds the file encoding combo ui.
     *
     * @param encodingGrp the encoding grp
     */
    private void addFileEncodingComboUi(Group encodingGrp) {
        Composite compfile = getFileEncodingComposite(encodingGrp);

        Label fileEncoding = new Label(compfile, SWT.NONE);
        fileEncoding.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, true, false));
        fileEncoding.setText(MessageConfigLoader.getProperty(IMessagesConstants.FILE_ENCODING));
        addFileEncodingList();
        fileEncodingCombo = new Combo(compfile, SWT.NONE | SWT.READ_ONLY);
        fileEncodingCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, true, false));

        addEncodingToCombo();

        String storedFileEncoding = preferenceStore.getString(UserEncodingOption.FILE_ENCODING);

        fileEncodingCombo.select(fileEncodingList.indexOf(storedFileEncoding));
        fileEncodingCombo.addSelectionListener(fileEncodingComboSelectionListener());

        Label gbkHintText = new Label(compfile, SWT.NONE);
        gbkHintText.setText(MessageConfigLoader.getProperty(IMessagesConstants.SET_GBK_FILE_ENCODE_PREF));
        gbkHintText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    }

    /**
     * Adds the ds encoding combo ui.
     *
     * @param encodingGrp the encoding grp
     */
    private void addDsEncodingComboUi(Group encodingGrp) {
        Label dsEncoding = new Label(encodingGrp, SWT.NONE);
        dsEncoding.setText(MessageConfigLoader.getProperty(IMessagesConstants.DATA_STUDIO_ENCODING));

        addDsEncodingList();
        dsEncodingCombo = new Combo(encodingGrp, SWT.NONE | SWT.READ_ONLY);
        dsEncodingCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, true, false));
        dsEncodingCombo.add(dsEncodingList.get(0));
        dsEncodingCombo.add(dsEncodingList.get(1));
        String storedDSEncoding = preferenceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING);

        dsEncodingCombo.select(dsEncodingList.indexOf(storedDSEncoding));
        dsEncodingCombo.addSelectionListener(dsEncodingComboSelectionListener());
    }

    /**
     * Gets the file encoding composite.
     *
     * @param encodingGrp the encoding grp
     * @return the file encoding composite
     */
    private Composite getFileEncodingComposite(Group encodingGrp) {
        Composite compfile = new Composite(encodingGrp, SWT.NONE);
        GridLayout grid1 = new GridLayout(2, true);
        grid1.marginTop = 0;
        grid1.marginLeft = 0;
        grid1.marginRight = 0;
        grid1.marginBottom = 0;
        grid1.horizontalSpacing = 30;
        grid1.verticalSpacing = 2;
        grid1.marginWidth = 0;
        compfile.setLayout(grid1);
        GridData gdComposit1 = new GridData(SWT.FILL, SWT.NONE, true, false);
        gdComposit1.horizontalIndent = 0;
        gdComposit1.verticalIndent = 0;
        gdComposit1.horizontalSpan = 2;
        compfile.setLayoutData(gdComposit1);
        return compfile;
    }

    /**
     * Adds the ds encoding list.
     */
    private void addDsEncodingList() {
        dsEncodingList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        dsEncodingList.add(DsEncodingEnum.UTF_8.getEncoding());
        dsEncodingList.add(DsEncodingEnum.GBK.getEncoding());
    }

    /**
     * Gets the encoding group UI.
     *
     * @param comp the comp
     * @param gdComposite the gd composite
     * @return the encoding group UI
     */
    private Group getEncodingGroupUI(Composite comp, GridData gdComposite) {
        Group encodingGrp = new Group(comp, SWT.NONE);
        encodingGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_PREFERENCES_TITLE));

        GridLayout grid = new GridLayout(2, true);
        grid.marginTop = 20;
        grid.marginLeft = 20;
        grid.horizontalSpacing = 30;
        grid.verticalSpacing = 25;
        encodingGrp.setLayout(grid);
        encodingGrp.setLayoutData(gdComposite);
        return encodingGrp;
    }

    /**
     * Adds the file encoding list.
     */
    private void addFileEncodingList() {
        fileEncodingList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        fileEncodingList.add(MPPDBIDEConstants.FILEENCODING_UTF);
        fileEncodingList.add(MPPDBIDEConstants.FILEENCODING_GBK);
        fileEncodingList.add(MPPDBIDEConstants.FILEENCODING_LATIN1);
    }

    private SelectionAdapter enableDbAssistSelectionListener() {
        return new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!enableAssistantBtn.getSelection() == preferenceStore
                        .getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE)) {
                    if (!lblAutosaveErrorMsg.getVisible()) {
                        enableDisableApplyButton();
                    }
                }

            }
        };
    }

    /**
     * Enabledisable db assist.
     */
    private void enabledisableDbAssist() {
        if (preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE)) {
            enableAssistantBtn.setSelection(true);
        } else {
            disableAssistantBtn.setSelection(true);
        }
    }

    private SelectionAdapter fileEncodingComboSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!fileEncodingCombo.getText().equals(preferenceStore.getString(UserEncodingOption.FILE_ENCODING))) {
                    PreferenceWrapper.getInstance().setChangeDone(true);
                    if (!lblAutosaveErrorMsg.getVisible()) {
                        enableDisableApplyButton();
                    }
                }
            }
        };
    }

    /**
     * Adds the encoding to combo.
     */
    private void addEncodingToCombo() {
        for (String item : fileEncodingList) {
            fileEncodingCombo.add(item);
        }
    }

    private SelectionAdapter dsEncodingComboSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!dsEncodingCombo.getText()
                        .equals(preferenceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING))) {
                    PreferenceWrapper.getInstance().setChangeDone(true);
                    if (!lblAutosaveErrorMsg.getVisible()) {
                        enableDisableApplyButton();
                    }
                }
            }
        };
    }

    /**
     * Gets the preference store if not exists.
     *
     * @return the preference store if not exists
     */
    private void getPreferenceStoreIfNotExists() {
        preferenceStore = getPreferenceStore();
        if (preferenceStore == null) {
            preferenceStore = WorkbenchPlugin.getDefault().getPreferenceStore();
        }
    }

    private SelectionAdapter addAutoSaveSelectListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                boolean isSelected = autosaveBtn.getSelection();
                encryptionBtn.setEnabled(isSelected);
                autosaveIntervalTxt.setEnabled(isSelected);

                if (!isSelected) {
                    // remove error message as not used by apply and enable
                    // apply
                    lblAutosaveErrorMsg.setVisible(false);
                } else {
                    validateAutosaveIntervalText();
                }

                if (!lblAutosaveErrorMsg.getVisible()) {
                    enableDisableApplyButton();
                }

            }
        };
    }

    private SelectionAdapter encryptionBtnSelectionListener() {
        return new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (!lblAutosaveErrorMsg.getVisible()) {
                    enableDisableApplyButton();
                }
            }
        };
    }

    /**
     * Adds the auto save selection listener.
     */
    private void addAutoSaveSelectionListener() {
        autosaveBtn.addSelectionListener(addAutoSaveSelectListener());

        encryptionBtn.addSelectionListener(encryptionBtnSelectionListener());
    }

    /**
     * Validate autosave interval text.
     *
     * @return true, if successful
     */
    public boolean validateAutosaveIntervalText() {
        if (null != autosaveIntervalTxt) {
            return validateIntervalTxt();
        }

        return false;
    }

    /**
     * Validate value entered in object count text box
     * 
     * @return true if validation success
     */
    public boolean validateObjectCountText() {
        if (null != objectCountInOb) {
            return validateObjectCountTxt();
        }

        return false;
    }

    /**
     * Validate interval txt.
     *
     * @return true, if successful
     */
    private boolean validateIntervalTxt() {
        try {
            if (isAutoSaveIntervalTxtEmpty()) {
                setErrorMessage(autosaveIntervalTxt, lblAutosaveErrorMsg);
                return false;
            }

            return onValidatingIntervalTxt();
        } catch (NumberFormatException e) {
            setErrorMessage(autosaveIntervalTxt, lblAutosaveErrorMsg);
        }
        return false;
    }

    /**
     * Validate value entered in object count text box
     * 
     * @return : true if validation success
     */
    private boolean validateObjectCountTxt() {
        try {
            if (isObjectCountTxtEmpty()) {
                setErrorMessage(objectCountInOb, lblObjectCountErrorMsg);
                return false;
            }

            return onValidatingObjectCountTxt();
        } catch (NumberFormatException exception) {
            setErrorMessage(objectCountInOb, lblObjectCountErrorMsg);
        }
        return false;
    }

    /**
     * On validating interval txt.
     *
     * @return true, if successful
     */
    private boolean onValidatingIntervalTxt() {
        if (validateAutoSaveIntervalBoundry()) {
            modifiedInterval = Integer.parseInt(autosaveIntervalTxt.getText());
            autosaveIntervalTxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            lblAutosaveErrorMsg.setVisible(false);
            enableDisableApplyButton();
            return true;
        } else {
            setErrorMessage(autosaveIntervalTxt, lblAutosaveErrorMsg);
            return false;
        }
    }

    /**
     * UI changes for validation success/failure
     * 
     * @return false on failure
     */
    private boolean onValidatingObjectCountTxt() {
        if (validateObjectCountForLazyRendering()) {
            modifiedObjectCount = Integer.parseInt(objectCountInOb.getText());
            objectCountInOb.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            lblObjectCountErrorMsg.setVisible(false);
            enableDisableApplyButton();
            return true;
        } else {
            setErrorMessage(objectCountInOb, lblObjectCountErrorMsg);
            return false;
        }
    }

    /**
     * Validate auto save interval boundry.
     *
     * @return true, if successful
     */
    private boolean validateAutoSaveIntervalBoundry() {
        return Integer.parseInt(autosaveIntervalTxt.getText()) >= AUTOSAVE_MIN_INTERVAL
                && (Integer.parseInt(autosaveIntervalTxt.getText()) <= AUTOSAVE_MAX_INTERVAL);
    }

    /**
     * Validate range entered for object count text box
     * 
     * @return true if entered value in range
     */
    private boolean validateObjectCountForLazyRendering() {
        return Integer.parseInt(objectCountInOb.getText()) >= OBJECT_COUNT_MIN
                && (Integer.parseInt(objectCountInOb.getText()) <= OBJECT_COUNT_MAX);
    }

    /**
     * Checks if is auto save interval txt empty.
     *
     * @return true, if is auto save interval txt empty
     */
    private boolean isAutoSaveIntervalTxtEmpty() {
        return autosaveIntervalTxt.getText().isEmpty();
    }

    private boolean isObjectCountTxtEmpty() {
        return objectCountInOb.getText().isEmpty();
    }

    /**
     * Verify interval text listener.
     */
    public void verifyIntervalTextListener() {
        autosaveIntervalTxt.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent event) {
                validateAutosaveIntervalText();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }
        });
    }

    /**
     * Verify data limit.
     *
     * @param txtInput the txt input
     */
    public void verifyDataLimit(Text txtInput) {
        txtInput.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent event) {
                if (StringUtils.isEmpty(importTableData.getText()) || StringUtils.isEmpty(importFileData.getText())
                        || StringUtils.isEmpty(importByteaData.getText())) {
                    lblErrorMsg.setVisible(true);
                    enableDisableApplyButton();
                    PreferenceWrapper.getInstance().setPreferenceApply(false);
                } else {
                    txtInput.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
                    lblErrorMsg.setVisible(false);
                }

                enableDisableApplyButton();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }
        });
    }

    /**
     * Add key listener and action for object count text box
     */
    private void verifyLazyRenderingDataLimit() {
        objectCountInOb.addKeyListener(new KeyListener() {

            @Override
            public void keyReleased(KeyEvent event) {
                validateObjectCountText();
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }
        });
    }

    /**
     * Enable disable apply button.
     */
    protected void enableDisableApplyButton() {
        getApplyButton().setEnabled(!(StringUtils.isEmpty(autosaveIntervalTxt.getText())
                || StringUtils.isEmpty(importTableData.getText()) || StringUtils.isEmpty(importFileData.getText())
                || StringUtils.isEmpty(importByteaData.getText()) || StringUtils.isEmpty(objectCountInOb.getText())));

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
        enableDisableApplyButton();
        PreferenceWrapper.getInstance().setPreferenceApply(false);
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
     * Creates the control.
     *
     * @param parent the parent
     */
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);
        getDefaultsButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_DEFAULT));
        getApplyButton().setText(MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_APPLY));
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        isNeedRestart = false;
        boolean isContinue = true;
        if (preferenceStore != null) {
            isContinue = onPrefStoreNotNull();
        }

        setPrefParametersOnNeedRestart(isContinue);

        return isContinue;
    }

    /**
     * On pref store not null.
     *
     * @return true, if successful
     */
    private boolean onPrefStoreNotNull() {
        // validate Interval only if autosave enabled.
        if (validateIntervalOnAutoSaveOn()) {
            return false;
        }

        if (!validateObjectCountText()) {
            return false;
        }

        isRestartNeeded();
        setDsEncoding();
        setFileEncoding();

        setSessionSettingValues();
        DBAssistantOption.updateDBAssistantEnable(preferenceStore);

        saveAutoSaveParameters();

        lblAutosaveErrorMsg.setVisible(false);

        AutoSaveManager.updateAutosavePreferences(preferenceStore);
        return true;
    }

    private void setSessionSettingValues() {
        if (enableAssistantBtn.getSelection() != preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE)) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "SQL assistant in Session setting of preferences is set from %b to %b",
                    preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE),
                    enableAssistantBtn.getSelection()));
        }
        preferenceStore.setValue(DBAssistantOption.DB_ASSISTANT_ENABLE, enableAssistantBtn.getSelection());
        if (!preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA).equals(importTableData.getText())) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "File Import table data limit in Session setting of preferences is set from %s MB to %s MB",
                    preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA), importTableData.getText()));
        }
        preferenceStore.setValue(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA, importTableData.getText());
        if (!preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_SQL).equals(importFileData.getText())) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "File Import File data limit in Session setting of preferences is set from %s MB to %s MB",
                    preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_SQL), importFileData.getText()));
        }
        preferenceStore.setValue(Preferencekeys.FILE_LIMIT_FOR_SQL, importFileData.getText());
        if (!preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_BYTEA).equals(importByteaData.getText())) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "File Import bytea data limit in Session setting of preferences is set from %s MB to %s MB",
                    preferenceStore.getString(Preferencekeys.FILE_LIMIT_FOR_BYTEA), importByteaData.getText()));
        }
        preferenceStore.setValue(Preferencekeys.FILE_LIMIT_FOR_BYTEA, importByteaData.getText());
        if (preferenceStore.getInt(Preferencekeys.OBECT_COUNT_FOR_LAZY_RENDERING) != modifiedObjectCount) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Number of Objects in a batch allowed for lazy Rendering "
                            + "in Session setting of preferences is set from %d to %d",
                    preferenceStore.getInt(Preferencekeys.OBECT_COUNT_FOR_LAZY_RENDERING), modifiedObjectCount));
        }
        preferenceStore.setValue(Preferencekeys.OBECT_COUNT_FOR_LAZY_RENDERING, modifiedObjectCount);
    }

    /**
     * Validate interval on auto save on.
     *
     * @return true, if successful
     */
    private boolean validateIntervalOnAutoSaveOn() {
        return isAutoSaveBtnSelected() && !validateAutosaveIntervalText();
    }

    /**
     * Sets the pref parameters on need restart.
     *
     * @param isContinue the new pref parameters on need restart
     */
    private void setPrefParametersOnNeedRestart(boolean isContinue) {
        // Only if Encoding changed
        if (isContinue && isNeedRestart) {
            PreferenceWrapper.getInstance().setPreferenceApply(false);
            PreferenceWrapper.getInstance().setChangeDone(true);
            PreferenceWrapper.getInstance().setNeedRestart(true);
        }
    }

    /**
     * Save auto save parameters.
     */
    private void saveAutoSaveParameters() {
        if (isAutoSaveBtnSelected()) {
            if (preferenceStore.getBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY) != true) {
                MPPDBIDELoggerUtility.operationInfo("Auto save of unsaved SQL information in preferences is enabled");  
            }
            preferenceStore.setValue(AUTOSAVE_ENABLE_PREFERENCE_KEY, true);
            if (preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG) != encryptionBtn.getSelection()) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Auto save Encryption of SQL information in preferences is set from %b to %b",
                        preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG), encryptionBtn.getSelection()));
            }
            preferenceStore.setValue(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG, encryptionBtn.getSelection());
            if (preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY) != modifiedInterval) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Auto save Time interval(minutes) of SQL information in preferences is set from %d to %d ",
                        preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY), modifiedInterval));
            }
            preferenceStore.setValue(AUTOSAVE_INTERVAL_PREFERENCE_KEY, modifiedInterval);
            autosaveIntervalTxt.setText(Integer.toString(preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));
        } else {
            MPPDBIDELoggerUtility.operationInfo("Auto save of unsaved SQL information is disabled");
            preferenceStore.setValue(AUTOSAVE_ENABLE_PREFERENCE_KEY, false);
            if (preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG) != encryptionBtn.getSelection()) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Auto save Encryption of SQL information in preferences is set from %b to %b",
                        preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG), encryptionBtn.getSelection()));
            }
            encryptionBtn.setSelection(preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG));
            autosaveIntervalTxt.setText(Integer.toString(preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));
        }
    }

    /**
     * Sets the file encoding.
     */
    private void setFileEncoding() {
        String fileEncode = fileEncodingList.get(fileEncodingCombo.getSelectionIndex());
        if (!preferenceStore.getString(UserEncodingOption.FILE_ENCODING)
                .equals(fileEncodingList.get(fileEncodingCombo.getSelectionIndex()))) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "File Encoding in Session setting of preferences is changed from %s to %s",
                    preferenceStore.getString(UserEncodingOption.FILE_ENCODING),
                    fileEncodingList.get(fileEncodingCombo.getSelectionIndex())));
        }
        setFileEncodingInPrefStore(fileEncode);
    }

    /**
     * Sets the ds encoding.
     */
    private void setDsEncoding() {
        if (isDsEncodingSelected()) {
            String dsEncode = dsEncodingList.get(dsEncodingCombo.getSelectionIndex());
            if (!preferenceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING)
                    .equals(dsEncodingList.get(dsEncodingCombo.getSelectionIndex()))) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "DataStudio Encoding in Session setting of preferences is changed from %s to %s",
                        preferenceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING),
                        dsEncodingList.get(dsEncodingCombo.getSelectionIndex())));
            }
            setDsEncodingInPrefStore(dsEncode);
        }
    }

    /**
     * Sets the file encoding in pref store.
     *
     * @param fileEncode the new file encoding in pref store
     */
    private void setFileEncodingInPrefStore(String fileEncode) {
        if (fileEncode != null) {
            preferenceStore.setValue(UserEncodingOption.FILE_ENCODING,
                    fileEncodingList.get(fileEncodingCombo.getSelectionIndex()));
        }
    }

    /**
     * Sets the ds encoding in pref store.
     *
     * @param dsEncode the new ds encoding in pref store
     */
    private void setDsEncodingInPrefStore(String dsEncode) {
        if (dsEncode != null) {
            preferenceStore.setValue(UserEncodingOption.DATA_STUDIO_ENCODING,
                    dsEncodingList.get(dsEncodingCombo.getSelectionIndex()));
        }
    }

    /**
     * Checks if is ds encoding selected.
     *
     * @return true, if is ds encoding selected
     */
    private boolean isDsEncodingSelected() {
        return dsEncodingCombo.getSelectionIndex() != -1;
    }

    /**
     * Checks if is restart needed.
     */
    private void isRestartNeeded() {
        if (isEncodingModified()) {
            isNeedRestart = true;
        }
    }

    /**
     * Validate ds encoding.
     *
     * @return true, if successful
     */
    private boolean validateDsEncoding() {
        return dsEncodingCombo.getSelectionIndex() != dsEncodingList
                .indexOf(preferenceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING));
    }

    /**
     * Validate file encoding.
     *
     * @return true, if successful
     */
    private boolean validateFileEncoding() {
        return fileEncodingCombo.getSelectionIndex() != fileEncodingList
                .indexOf(preferenceStore.getString(UserEncodingOption.FILE_ENCODING));
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        if (!performOk()) {
            return;
        }

        setPrefParametersOnNeedRestart(true);

        getApplyButton().setEnabled(false);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        IPreferenceStore prefernceStore = getPreferenceStore();

        if (prefernceStore != null) {
            int defaultDSEncodingIndex = dsEncodingList
                    .indexOf(prefernceStore.getDefaultString(UserEncodingOption.DATA_STUDIO_ENCODING));
            dsEncodingCombo.select(defaultDSEncodingIndex);
            int defaultFileEncodingIndex = fileEncodingList
                    .indexOf(prefernceStore.getDefaultString(UserEncodingOption.FILE_ENCODING));
            fileEncodingCombo.select(defaultFileEncodingIndex);

            disableAssistantBtn.setSelection(false);
            enableAssistantBtn.setSelection(true);

            importFileData.setText(String.valueOf(prefernceStore.getDefaultInt(Preferencekeys.FILE_LIMIT_FOR_SQL)));
            importByteaData.setText(String.valueOf(prefernceStore.getDefaultInt(Preferencekeys.FILE_LIMIT_FOR_BYTEA)));
            importTableData
                    .setText(String.valueOf(prefernceStore.getDefaultInt(Preferencekeys.FILE_LIMIT_FOR_TABLE_DATA)));
            autosaveBtn.setSelection(preferenceStore.getDefaultBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY));

            encryptionBtn.setSelection(preferenceStore.getDefaultBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG));
            autosaveIntervalTxt
                    .setText(Integer.toString(preferenceStore.getDefaultInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));

            objectCountInOb.setText(Integer.toString(prefernceStore.getDefaultInt(OBECT_COUNT_FOR_LAZY_RENDERING)));
            autosaveIntervalTxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
            boolean isSelected = isAutoSaveBtnSelected();
            encryptionBtn.setEnabled(isSelected);
            autosaveIntervalTxt.setEnabled(isSelected);
            lblAutosaveErrorMsg.setVisible(false);
            lblErrorMsg.setVisible(false);
        }

        PreferenceWrapper.getInstance().setDefaultStore(true);
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Session setting values in Preferences are restored to defaults");
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
     * Ok to leave.
     *
     * @return true, if successful
     */
    @Override
    public boolean okToLeave() {
        if (isFieldsModified()) {
            int choice = MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.SESSIONSETTINGS_PREFPAGE_UNSAVEDCHANGED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_CHANGE_NOT_APPLIED_MESSAGE));

            // Check whether any error msg is not present and user has selected
            // ok button
            if (choice == IDialogConstants.OK_ID) {
                return performOk();
            } else {
                rollBackChanges();
                return super.okToLeave();
            }
        }

        return true;
    }

    /**
     * Checks if is fields modified.
     *
     * @return true, if is fields modified
     */
    private boolean isFieldsModified() {
        if (isAutoSaveOrDbAssistModified() || isEncodingModified()) {
            return true;
        }

        return false;
    }

    /**
     * Checks if is encoding modified.
     *
     * @return true, if is encoding modified
     */
    private boolean isEncodingModified() {
        return validateFileEncoding() || validateDsEncoding();
    }

    /**
     * Checks if is auto save or db assist modified.
     *
     * @return true, if is auto save or db assist modified
     */
    private boolean isAutoSaveOrDbAssistModified() {
        return hasAutoSaveOptionsModified() || isEnableAssistBtnUpdated();
    }

    /**
     * Checks for auto save options modified.
     *
     * @return true, if successful
     */
    private boolean hasAutoSaveOptionsModified() {
        return isAutoSaveSelectionChanged()
                || (isAutoSaveBtnSelected() && (isEncryptionBtnUpdated() || isAutoSaveIntervalTxtUpdated()));
    }

    /**
     * Checks if is enable assist btn updated.
     *
     * @return true, if is enable assist btn updated
     */
    private boolean isEnableAssistBtnUpdated() {
        return enableAssistantBtn.getSelection() != preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE);
    }

    /**
     * Checks if is auto save interval txt updated.
     *
     * @return true, if is auto save interval txt updated
     */
    private boolean isAutoSaveIntervalTxtUpdated() {
        return !autosaveIntervalTxt.getText()
                .equals(Integer.toString(preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));
    }

    /**
     * Checks if is encryption btn updated.
     *
     * @return true, if is encryption btn updated
     */
    private boolean isEncryptionBtnUpdated() {
        return encryptionBtn.getSelection() != preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG);
    }

    /**
     * Checks if is auto save btn selected.
     *
     * @return true, if is auto save btn selected
     */
    private boolean isAutoSaveBtnSelected() {
        return autosaveBtn.getSelection();
    }

    /**
     * Checks if is auto save selection changed.
     *
     * @return true, if is auto save selection changed
     */
    private boolean isAutoSaveSelectionChanged() {
        return isAutoSaveBtnSelected() != preferenceStore.getBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY);
    }

    /**
     * Roll back changes.
     */
    private void rollBackChanges() {
        IPreferenceStore prefernceStore = getPreferenceStore();

        if (prefernceStore != null) {
            int defaultDSEncodingIndex = dsEncodingList
                    .indexOf(prefernceStore.getString(UserEncodingOption.DATA_STUDIO_ENCODING));
            dsEncodingCombo.select(defaultDSEncodingIndex);
            int defaultFileEncodingIndex = fileEncodingList
                    .indexOf(prefernceStore.getString(UserEncodingOption.FILE_ENCODING));
            fileEncodingCombo.select(defaultFileEncodingIndex);

            enableAssistantBtn.setSelection(preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE));
            disableAssistantBtn.setSelection(!preferenceStore.getBoolean(DBAssistantOption.DB_ASSISTANT_ENABLE));

            autosaveBtn.setSelection(preferenceStore.getBoolean(AUTOSAVE_ENABLE_PREFERENCE_KEY));
            encryptionBtn.setSelection(preferenceStore.getBoolean(AUTOSAVE_ENCRYPTION_PREFERENCE_FLAG));
            autosaveIntervalTxt.setText(Integer.toString(preferenceStore.getInt(AUTOSAVE_INTERVAL_PREFERENCE_KEY)));
            objectCountInOb.setText(Integer.toString(prefernceStore.getInt(OBECT_COUNT_FOR_LAZY_RENDERING)));

            boolean isSelect = isAutoSaveBtnSelected();
            encryptionBtn.setEnabled(isSelect);
            autosaveIntervalTxt.setEnabled(isSelect);
            lblAutosaveErrorMsg.setVisible(false);
            autosaveIntervalTxt.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
        }

        getApplyButton().setEnabled(false);
    }
}
