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
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
 * Description: The Class ResultManagementViewDataPreferencePage.
 *
 * @since 3.0.0
 */
public class ResultManagementViewDataPreferencePage extends PreferencePage {
    private static final int PREF_CUSTOM_RECORDS = 1000;
    private static final int MIN_VALUE = 100;
    private static final int COLUMN_WIDTH_MAX_VALUE = 500;
    private static final int MAX_CUSTOM_RECORDS = 5000;
    private static final int MAX_RESULT_WINDOWS = 300;
    private static final int MIN_RESULT_WINDOWS = 100;
    private static final int DEFAULT_MAX_RESULT_WINDOWS = 200;

    private static int gridColumnWidth;
    private static boolean isColumnValueLength;
    private static boolean isGenerateNew;
    private static int recordFetchCount;
    private static boolean isFetchAll;
    private static boolean isCopyColumnHeader;
    private static boolean isCopyRowHeader;
    private static int resultWindowCount;
    private Button btnColValueLen;
    private Button btnCustomColumnLenValue;
    private Text txtCustomWidth;
    private Button btnFetchAll;
    private Button btnFetchCustom;
    private Text txtRecordCount;
    private Label lblErrMsg;
    private Button btnIsCopyColHeader;
    private Button btnIsCopyRowHeader;
    private Button btnIsEncodingShown;
    private Button btnIsTextModeShown;
    private Button btnGenerateNew;
    private Button btnRetainOld;
    private Text maxResultsetCountTxt;
    private boolean ispreferenceChanged;
    private static boolean isShowEncoding;
    private static boolean isShowTextMode;

    /**
     * Instantiates a new result management view data preference page.
     */
    public ResultManagementViewDataPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RESULT_VIEW_TITLE));
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite comp = createComposite(parent, 1, false, true);

        addFetchSizePreferenceUI(comp);
        addDisplayColumnSizeMethod(comp);
        addCopySettingsUI(comp);
        addEncodingSettingsUI(comp);
        addTextModeSettingsUI(comp);
        addResultWindowSettingsUI(comp);
        addErrorMsgPlaceHolder(comp);
        // Last item, is to add listeners to created components
        addApplyBtnListeners();
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

    /**
     * Adds the copy settings UI.
     *
     * @param parent the parent
     */
    private void addCopySettingsUI(Composite parent) {
        Composite comp = createComposite(parent, 1, true, false);

        Label lblTitle = new Label(comp, SWT.NONE);
        lblTitle.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_COPY_HEADING));

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblTitle.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(lblTitle.getDisplay());
        lblTitle.setFont(boldFont);

        this.btnIsCopyColHeader = new Button(comp, SWT.CHECK);
        this.btnIsCopyColHeader
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_DATA_COPY_COL_HEADER_MSG));
        this.btnIsCopyColHeader.setSelection(isCopyColumnHeader);

        Label lblColCopyHintContent = getHintLabel(comp);
        lblColCopyHintContent
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_COPY_COLUMN_HEADER_HINT));

        this.btnIsCopyRowHeader = new Button(comp, SWT.CHECK);
        this.btnIsCopyRowHeader
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_DATA_COPY_ROW_HEADER_MSG));
        this.btnIsCopyRowHeader.setSelection(isCopyRowHeader);

        Label lblRowCopyHintContent = getHintLabel(comp);
        lblRowCopyHintContent.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_COPY_ROW_HEADER_HINT));

    }

    /**
     * Adds the encoding settings UI.
     *
     * @param parent the parent
     */
    private void addEncodingSettingsUI(Composite parent) {
        Composite comp = createComposite(parent, 1, true, false);

        Label lblTitle = new Label(comp, SWT.NONE);
        lblTitle.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_QUERY_RESULT_DATA_ENCODING_HEADER));

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblTitle.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(lblTitle.getDisplay());
        lblTitle.setFont(boldFont);

        this.btnIsEncodingShown = new Button(comp, SWT.CHECK);
        this.btnIsEncodingShown
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_QUERY_RESULT_DATA_ENCODING_BODY));
        this.btnIsEncodingShown.setSelection(isShowEncoding);

        Label lblEncodingDesc = getHintLabel(comp);
        lblEncodingDesc
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_QUERY_RESULT_DATA_ENCODING_DESC));

    }

    /**
     * Adds the text mode settings UI.
     *
     * @param parent the parent
     */
    private void addTextModeSettingsUI(Composite parent) {
        Composite comp = createComposite(parent, 1, true, false);

        Label lblTitle = new Label(comp, SWT.NONE);
        lblTitle.setText(
                MessageConfigLoader.getProperty(IMessagesConstants.TEXTMODE_QUERY_RESULT_DATA_TEXTMODE_HEADER));

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblTitle.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(lblTitle.getDisplay());
        lblTitle.setFont(boldFont);

        this.btnIsTextModeShown = new Button(comp, SWT.CHECK);
        this.btnIsTextModeShown
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.TEXTMODE_QUERY_RESULT_DATA_TEXTMODE_BODY));
        this.btnIsTextModeShown.setSelection(isShowTextMode);

        Label lblTextModeDesc = getHintLabel(comp);
        lblTextModeDesc.setText(MessageConfigLoader.getProperty(
                IMessagesConstants.TEXTMODE_QUERY_RESULT_DATA_TEXTMODE_DESC, MPPDBIDEConstants.TEXT_MODE_LOAD_MAXIMUM));

    }

    private void addResultWindowSettingsUI(Composite parent) {
        Composite comp = createComposite(parent, 1, true, false);

        // HEADER
        Label generateNewLbl = new Label(comp, SWT.NONE);
        generateNewLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW));
        generateNewLbl.pack();

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(generateNewLbl.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(generateNewLbl.getDisplay());
        generateNewLbl.setFont(boldFont);

        btnGenerateNew = new Button(comp, SWT.RADIO);
        btnGenerateNew.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_GENERATE_NEW));
        btnGenerateNew.addSelectionListener(getGenerateBtnSelectionListener());

        Label lblGenerateNewHint = getHintLabel(comp);
        lblGenerateNewHint.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_GENERATE_NEW_HINT));

        addRetainOldOption(comp);

        Label lblRetainCurrentHint = getHintLabel(comp);
        lblRetainCurrentHint
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_RETAIN_CURRENT_HINT));

        Composite composite = new Composite(comp, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 5;
        composite.setLayout(layout);

        Label lbl = new Label(composite, SWT.NONE);
        lbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.MAX_RESULT_SET_LBL));
        maxResultsetCountTxt = new Text(composite, SWT.BORDER);
        maxResultsetCountTxt.setText(String.valueOf(resultWindowCount));
        GridData data = new GridData();
        data.widthHint = 70;
        maxResultsetCountTxt.setLayoutData(data);
        maxResultsetCountTxt.setEnabled(false);
        DBConnectionValidator txtRecordCountVerifyListener = new DBConnectionValidator(maxResultsetCountTxt,
                MAX_RESULT_WINDOWS);
        maxResultsetCountTxt.addVerifyListener(txtRecordCountVerifyListener);

        Label maxLbl = new Label(composite, SWT.NONE);
        maxLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.MAX_RESULT_SET_RANGE));

        Label lblMaxResultWindow = getHintLabel(comp);
        lblMaxResultWindow.setText(MessageConfigLoader.getProperty(IMessagesConstants.MAX_RESULT_WINDOW_HINT));

        setDefaultResultWindowGeneration(isGenerateNew);
    }

    private void addRetainOldOption(Composite composite) {
        btnRetainOld = new Button(composite, SWT.RADIO);
        btnRetainOld.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_RETAIN_CURRENT));
        btnRetainOld.addSelectionListener(getRetainOldBtnSelectionListener());
    }

    private SelectionListener getRetainOldBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                maxResultsetCountTxt.setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        };
    }

    private SelectionListener getGenerateBtnSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                maxResultsetCountTxt.setEnabled(false);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        };
    }

    private void setDefaultResultWindowGeneration(boolean decider) {
        if (decider) {
            btnGenerateNew.setSelection(true);
            btnRetainOld.setSelection(false);
        } else {
            btnGenerateNew.setSelection(false);
            btnRetainOld.setSelection(true);
        }
        if (btnRetainOld.getSelection()) {
            maxResultsetCountTxt.setEnabled(true);
        }
    }

    /**
     * Adds the error msg place holder.
     *
     * @param parent the parent
     */
    private void addErrorMsgPlaceHolder(Composite parent) {
        Composite comp = createComposite(parent, 1, false, true);
        GridData data = (GridData) comp.getLayoutData();
        data.verticalAlignment = GridData.END;

        lblErrMsg = new Label(comp, SWT.None);
        lblErrMsg.setLayoutData(data);
        lblErrMsg.setForeground(lblErrMsg.getDisplay().getSystemColor(SWT.COLOR_RED));
    }

    /**
     * Adds the fetch size preference UI.
     *
     * @param parent the parent
     */
    private void addFetchSizePreferenceUI(Composite parent) {
        Composite compFectchSize = createComposite(parent, 2, true, false);

        // HEADER
        Label lblTitle = new Label(compFectchSize, SWT.NONE);
        lblTitle.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_DATA_FETCH_COUNT_SUBTITLE));

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblTitle.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(lblTitle.getDisplay());
        lblTitle.setFont(boldFont);
        lblTitle.setLayoutData(getGridDataHorizontalSpanTwo());

        btnFetchAll = new Button(compFectchSize, SWT.RADIO);
        btnFetchAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_DATA_FETCH_ALL_MSG));
        btnFetchAll.setLayoutData(getGridDataHorizontalSpanTwo());

        btnFetchCustom = new Button(compFectchSize, SWT.RADIO);
        btnFetchCustom.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_DATA_FETCH_COUNT_MSG));

        txtRecordCount = new Text(compFectchSize, SWT.BORDER);
        txtRecordCount.setText(Integer.toString(recordFetchCount));
        GridData data = new GridData();
        data.widthHint = 70;
        txtRecordCount.setLayoutData(data);
        DBConnectionValidator txtRecordCountVerifyListener = new DBConnectionValidator(txtRecordCount,
                MAX_CUSTOM_RECORDS);
        txtRecordCount.addVerifyListener(txtRecordCountVerifyListener);

        setDefaultSelection(isFetchAll, btnFetchAll, btnFetchCustom, txtRecordCount);
        addSelectionListener(btnFetchCustom, txtRecordCount, true);
        addSelectionListener(btnFetchAll, txtRecordCount, false);
    }

    /**
     * Adds the display column size method.
     *
     * @param parent the parent
     */
    private void addDisplayColumnSizeMethod(Composite parent) {
        Composite comp = createComposite(parent, 2, true, false);

        // HEADER
        Label lblTitle = new Label(comp, SWT.NONE);
        lblTitle.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_COLUMN_WIDTH));

        FontDescriptor boldDescriptor = FontDescriptor.createFrom(lblTitle.getFont()).setStyle(SWT.BOLD);
        Font boldFont = boldDescriptor.createFont(lblTitle.getDisplay());
        lblTitle.setFont(boldFont);
        lblTitle.setLayoutData(getGridDataHorizontalSpanTwo());

        btnColValueLen = new Button(comp, SWT.RADIO);
        btnColValueLen.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_CONTENT_LENGTH));
        btnColValueLen.setLayoutData(getGridDataHorizontalSpanTwo());

        Label lblHintContent = getHintLabel(comp);
        lblHintContent.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_CONTENT_LENGTH_HINT));

        btnCustomColumnLenValue = new Button(comp, SWT.RADIO);
        btnCustomColumnLenValue.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_CUSTOM_LENGTH));

        txtCustomWidth = new Text(comp, SWT.BORDER);

        txtCustomWidth.setText(Integer.toString(gridColumnWidth));
        GridData data = new GridData();
        data.widthHint = 70;
        txtCustomWidth.setLayoutData(data);

        Label lblHintCustom = getHintLabel(comp);
        lblHintCustom.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_CUSTOM_LENGTH_HINT));

        setDefaultSelection(isColumnValueLength, btnColValueLen, btnCustomColumnLenValue, txtCustomWidth);
        addSelectionListener(btnCustomColumnLenValue, txtCustomWidth, true);
        addSelectionListener(btnColValueLen, txtCustomWidth, false);

        DBConnectionValidator txtCustomWidthtVerifyListener = new DBConnectionValidator(txtCustomWidth,
                COLUMN_WIDTH_MAX_VALUE);
        txtCustomWidth.addVerifyListener(txtCustomWidthtVerifyListener);
    }

    /**
     * Gets the hint label.
     *
     * @param comp the comp
     * @return the hint label
     */
    private Label getHintLabel(Composite comp) {
        Label lbl = new Label(comp, SWT.NONE);
        GridData data = getGridDataHorizontalSpanTwo();
        data.horizontalIndent = 18;
        lbl.setLayoutData(data);
        return lbl;
    }

    /**
     * Gets the grid data horizontal span two.
     *
     * @return the grid data horizontal span two
     */
    private GridData getGridDataHorizontalSpanTwo() {
        GridData data = new GridData();
        data.horizontalSpan = 2;
        return data;
    }

    /**
     * Adds the selection listener.
     *
     * @param btn the btn
     * @param txt the txt
     * @param value the value
     */
    private void addSelectionListener(Button btn, final Text txt, final boolean value) {
        btn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                txt.setEnabled(value);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Ignore
            }
        });
    }

    /**
     * Sets the default selection.
     *
     * @param decider the decider
     * @param btn1 the btn 1
     * @param btn2 the btn 2
     * @param txt the txt
     */
    private void setDefaultSelection(boolean decider, Button btn1, Button btn2, Text txt) {
        if (decider) {
            btn1.setSelection(true);
            btn2.setSelection(false);
            txt.setEnabled(false);
        } else {
            btn1.setSelection(false);
            btn2.setSelection(true);
            txt.setEnabled(true);
        }
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (isIspreferenceChanged()) {
            PreferenceWrapper prefStore = PreferenceWrapper.getInstance();
            IPreferenceStore preferenceStore = prefStore.getPreferenceStore();
            if (null != preferenceStore) {
                if (isUserInputValid(preferenceStore)) {
                    setPreferenceResultWindow((PreferenceStore) preferenceStore);
                } else {
                    return false;
                }

            }
        }
        return true;
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
     * Perform apply.
     */
    @Override
    protected void performApply() {
        cleanupMsgs();
        PreferenceWrapper prefStore = PreferenceWrapper.getInstance();
        IPreferenceStore preferenceStore = prefStore.getPreferenceStore();
        if (null != preferenceStore) {
            if (!isUserInputValid(preferenceStore)) {
                return;
            }
            setPreferenceResultWindow((PreferenceStore) preferenceStore);
        }
        PreferenceWrapper.getInstance().setPreferenceApply(true);
        getApplyButton().setEnabled(false);
    }

    /**
     * Cleanup msgs.
     */
    private void cleanupMsgs() {
        lblErrMsg.setText("");
    }

    /**
     * Checks if is user input valid.
     *
     * @param preferenceStore the preference store
     * @return true, if is user input valid
     */
    private boolean isUserInputValid(IPreferenceStore preferenceStore) {
        boolean isColValue = btnColValueLen.getSelection();
        boolean isRetainOld = btnRetainOld.getSelection();
        int customWidth = 0;
        int maxResultWindow = 0;
        boolean isFtchAll = btnFetchAll.getSelection();
        int fetchCount = 0;
        try {
            if (!isFtchAll) {
                fetchCount = Integer.parseInt(txtRecordCount.getText());
                if (fetchCount < MIN_VALUE || fetchCount > MAX_CUSTOM_RECORDS) {
                    lblErrMsg.setText(
                            MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_FETCH_COUNT_INPUT_ERR_MSG));
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            lblErrMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_FETCH_COUNT_INPUT_ERR_MSG));
            return false;
        }
        try {
            if (!isColValue) {
                customWidth = Integer.parseInt(txtCustomWidth.getText());
                if (customWidth < MIN_VALUE || customWidth > COLUMN_WIDTH_MAX_VALUE) {
                    lblErrMsg.setText(
                            MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_COLUMN_WIDTH_INPUT_ERR_MSG));
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            lblErrMsg.setText(MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_COLUMN_WIDTH_INPUT_ERR_MSG));
            return false;
        }
        try {
            if (isRetainOld) {
                maxResultWindow = Integer.parseInt(maxResultsetCountTxt.getText());
                if (maxResultWindow < MIN_RESULT_WINDOWS || maxResultWindow > MAX_RESULT_WINDOWS) {
                    lblErrMsg.setText(MessageConfigLoader
                            .getProperty(IMessagesConstants.PREF_RS_MAX_RESULT_WINDOW_INPUT_ERR_MSG));
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            lblErrMsg.setText(
                    MessageConfigLoader.getProperty(IMessagesConstants.PREF_RS_MAX_RESULT_WINDOW_INPUT_ERR_MSG));
            return false;
        }
        setPreferenceValueOnSuccess(isColValue, isFtchAll, customWidth, fetchCount, maxResultWindow);
        return true;
    }

    private void setPreferenceValueOnSuccess(boolean isColValue, boolean isFtchAll, int customWidth, int fetchCount,
            int maxResultWindow) {
        // Validate success. lets set the value to preference

        IPreferenceStore preferenceStore = PreferenceWrapper.getInstance().getPreferenceStore();
        setColumnWidthValues(isColValue, customWidth, preferenceStore);
        setResultFetchCountValues(isFtchAll, fetchCount, preferenceStore);
        setResultAdvancedCopyValues(preferenceStore);
        setIsResultDataEncodingValues(preferenceStore);
        setIsResultDataTextModeValues(preferenceStore);
        setResultWindowValues(maxResultWindow, preferenceStore);
    }

    private void setResultWindowValues(int maxResultWindow, IPreferenceStore preferenceStore) {
        if (this.btnGenerateNew.getSelection() && (preferenceStore
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE) != this.btnGenerateNew.getSelection())) {
            MPPDBIDELoggerUtility
                    .operationInfo("Overwrite resultset for Result Window in Preferences setting is enabled");
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE, this.btnGenerateNew.getSelection());
        if (maxResultWindow != 0) {
            if (preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT) != maxResultWindow) {
                MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                        "Maximum Result windows that can be opened in"
                                + " Result Window of Preferences setting is changed from %d to %d",
                        preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT), maxResultWindow));
            }
            preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT, maxResultWindow);
        }
    }

    private void setIsResultDataTextModeValues(IPreferenceStore preferenceStore) {
        if (preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE) != this.btnIsTextModeShown
                .getSelection()) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Include Result data text mode in Preferences setting is set from %b to %b",
                    preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE),
                    this.btnIsTextModeShown.getSelection()));
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE,
                this.btnIsTextModeShown.getSelection());
    }

    private void setIsResultDataEncodingValues(IPreferenceStore preferenceStore) {
        if (preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING) != this.btnIsEncodingShown
                .getSelection()) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Include Result data encoding in Preferences setting is set from %b to %b",
                    preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING),
                    this.btnIsEncodingShown.getSelection()));
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING,
                this.btnIsEncodingShown.getSelection());
    }

    private void setResultAdvancedCopyValues(IPreferenceStore preferenceStore) {
        if (preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER) != this.btnIsCopyColHeader
                .getSelection()) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Copy along with column header in Preferences setting is set from %b to %b",
                    preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER),
                    this.btnIsCopyColHeader.getSelection()));
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER,
                this.btnIsCopyColHeader.getSelection());
        if (preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER) != this.btnIsCopyRowHeader
                .getSelection()) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Copy along with row header in Preferences setting is set from %b to %b",
                    preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER),
                    this.btnIsCopyRowHeader.getSelection()));
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER,
                this.btnIsCopyRowHeader.getSelection());
    }

    private void setResultFetchCountValues(boolean isFtchAll, int fetchCount, IPreferenceStore preferenceStore) {
        if (isFtchAll && (preferenceStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL) != isFtchAll)) {
            MPPDBIDELoggerUtility.operationInfo("ResultSet fetches all record in Preferences setting is enabled");
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL, isFtchAll);
        if (!isFtchAll && (preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT) != fetchCount)) {
            MPPDBIDELoggerUtility.operationInfo(String.format(Locale.ENGLISH,
                    "Fetch custom number of records of Preferences setting is changed from %d to %d",
                    preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT), fetchCount));
            preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT, fetchCount);
        }
    }

    private void setColumnWidthValues(boolean isColValue, int customWidth, IPreferenceStore preferenceStore) {
        if (isColValue && (preferenceStore
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE) != isColValue)) {
            MPPDBIDELoggerUtility.operationInfo("Content length of column in Preferences setting is enabled");
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE, isColValue);
        int colWidth = isColValue ? MIN_VALUE : customWidth;
        if (!isColValue && (preferenceStore.getInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH) != colWidth)) {
            MPPDBIDELoggerUtility.operationInfo(
                    String.format(Locale.ENGLISH, "Column width length of Preferences setting is changed from %d to %d",
                            preferenceStore.getInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH), colWidth));
        }
        preferenceStore.setValue(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH, colWidth);
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        PreferenceWrapper.getInstance().setNeedRestart(false);
        PreferenceWrapper.getInstance().setDefaultStore(true);
        PreferenceWrapper prefStore = PreferenceWrapper.getInstance();
        PreferenceStore preferenceStore = prefStore.getPreferenceStore();

        setDefaultPreferenceResultWindow(preferenceStore);

        // Reset selection values.
        txtCustomWidth.setText(Integer.toString(gridColumnWidth));
        setDefaultSelection(isColumnValueLength, btnColValueLen, btnCustomColumnLenValue, txtCustomWidth);
        setDefaultResultWindowGeneration(isGenerateNew);
        txtRecordCount.setText(Integer.toString(recordFetchCount));
        setDefaultSelection(isFetchAll, btnFetchAll, btnFetchCustom, txtRecordCount);
        maxResultsetCountTxt.setText(String.valueOf(DEFAULT_MAX_RESULT_WINDOWS));
        maxResultsetCountTxt.setEnabled(false);
        this.btnIsCopyColHeader.setSelection(isCopyColumnHeader);
        this.btnIsCopyRowHeader.setSelection(isCopyRowHeader);
        this.btnIsEncodingShown.setSelection(isShowEncoding);
        this.btnIsTextModeShown.setSelection(isShowTextMode);

        performApply();
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Query Results in Preferences setting are set to default");
    }

    /**
     * Creates the composite.
     *
     * @param parent the parent
     * @param columnCount the column count
     * @param needBorder the need border
     * @param grabVerticalSpace the grab vertical space
     * @return the composite
     */
    private Composite createComposite(Composite parent, int columnCount, boolean needBorder,
            boolean grabVerticalSpace) {
        Composite comp = new Composite(parent, needBorder ? SWT.BORDER : SWT.NONE);
        comp.setLayout(new GridLayout(columnCount, false));
        GridDataFactory.fillDefaults().grab(true, grabVerticalSpace).applyTo(comp);
        return comp;
    }

    /**
     * Sets the preference result window.
     *
     * @param ps the new preference result window
     */
    public static void setPreferenceResultWindow(PreferenceStore ps) {
        ResultManagementViewDataPreferencePage.isColumnValueLength = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE);
        ResultManagementViewDataPreferencePage.gridColumnWidth = ps.getInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH);

        ResultManagementViewDataPreferencePage.isFetchAll = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL);
        ResultManagementViewDataPreferencePage.recordFetchCount = ps
                .getInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT);

        ResultManagementViewDataPreferencePage.isCopyColumnHeader = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER);
        ResultManagementViewDataPreferencePage.isCopyRowHeader = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER);
        ResultManagementViewDataPreferencePage.isShowEncoding = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING);
        ResultManagementViewDataPreferencePage.isShowTextMode = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE);

        ResultManagementViewDataPreferencePage.isGenerateNew = ps
                .getBoolean(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE);

        ResultManagementViewDataPreferencePage.resultWindowCount = ps
                .getInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT);
    }

    /**
     * Sets the default preference result window.
     *
     * @param ps the new default preference result window
     */
    private static void setDefaultPreferenceResultWindow(PreferenceStore ps) {
        ResultManagementViewDataPreferencePage.isColumnValueLength = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE);
        ResultManagementViewDataPreferencePage.gridColumnWidth = ps
                .getDefaultInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH);

        ResultManagementViewDataPreferencePage.isFetchAll = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL);
        ResultManagementViewDataPreferencePage.recordFetchCount = ps
                .getDefaultInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT);

        ResultManagementViewDataPreferencePage.isCopyColumnHeader = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER);
        ResultManagementViewDataPreferencePage.isCopyRowHeader = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER);

        ResultManagementViewDataPreferencePage.isShowEncoding = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_ENCODING);
        ResultManagementViewDataPreferencePage.isShowTextMode = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE);

        ResultManagementViewDataPreferencePage.isGenerateNew = ps
                .getDefaultBoolean(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE);

        ResultManagementViewDataPreferencePage.resultWindowCount = ps
                .getDefaultInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT);
    }

    /**
     * Sets the default preferences.
     *
     * @param preferenceStore the new default preferences
     */
    public static void setDefaultPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH, MIN_VALUE);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_IS_COLUMN_LENGTH_BY_VALUE, true);

        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT, PREF_CUSTOM_RECORDS);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_IS_RECORD_FETCH_ALL, false);

        // Copy header
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_IS_COPY_COLUMN_HEADER, true);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_IS_COPY_ROW_HEADER, false);

        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_WINDOW_GENERATE, true);
        preferenceStore.setDefault(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT, DEFAULT_MAX_RESULT_WINDOWS);
    }

    /**
     * Adds the apply btn listeners.
     */
    private void addApplyBtnListeners() {
        SelectionListener btnSelectionListener = new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setIspreferenceChanged(true);
                getApplyButton().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Ignore
            }
        };
        KeyListener txtKeyListener = new KeyListener() {

            @Override
            public void keyReleased(KeyEvent e) {
                // Ignore
            }

            @Override
            public void keyPressed(KeyEvent e) {
                setIspreferenceChanged(true);
                getApplyButton().setEnabled(true);
            }
        };

        this.btnColValueLen.addSelectionListener(btnSelectionListener);
        this.btnCustomColumnLenValue.addSelectionListener(btnSelectionListener);
        this.btnFetchAll.addSelectionListener(btnSelectionListener);
        this.btnFetchCustom.addSelectionListener(btnSelectionListener);
        this.btnIsCopyColHeader.addSelectionListener(btnSelectionListener);
        this.btnIsCopyRowHeader.addSelectionListener(btnSelectionListener);
        this.btnIsEncodingShown.addSelectionListener(btnSelectionListener);
        this.btnIsTextModeShown.addSelectionListener(btnSelectionListener);
        this.txtCustomWidth.addKeyListener(txtKeyListener);
        this.txtRecordCount.addKeyListener(txtKeyListener);
        this.btnGenerateNew.addSelectionListener(btnSelectionListener);
        this.maxResultsetCountTxt.addKeyListener(txtKeyListener);
    }

    /**
     * Checks if is ispreference changed.
     *
     * @return true, if is ispreference changed
     */
    private boolean isIspreferenceChanged() {
        return ispreferenceChanged;
    }

    /**
     * Sets the ispreference changed.
     *
     * @param ispreferenceChanged the new ispreference changed
     */
    private void setIspreferenceChanged(boolean ispreferenceChanged) {
        this.ispreferenceChanged = ispreferenceChanged;
    }

    /**
     * Validate fetch count.
     *
     * @param preferenceStore the preference store
     */
    public static void validateFetchCount(PreferenceStore preferenceStore) {
        int fetchCount = preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT);
        int resultWindCount = preferenceStore.getInt(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT);
        if (!(fetchCount <= MAX_CUSTOM_RECORDS && fetchCount >= MIN_VALUE)) {
            preferenceStore.setToDefault(MPPDBIDEConstants.PREF_RESULT_RECORD_FETCH_COUNT);
        }

        if (!(resultWindCount > MIN_RESULT_WINDOWS && resultWindCount < MAX_RESULT_WINDOWS)) {
            preferenceStore.setToDefault(MPPDBIDEConstants.PREF_RESULT_WINDOW_COUNT);
        }
    }
}
