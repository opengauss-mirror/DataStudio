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

package com.huawei.mppdbide.view.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ImportExportOption;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;
import com.huawei.mppdbide.view.utils.InitListener;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * 
 * Title: class
 * 
 * Description: The Class ImportExportDialog.
 *
 * @since 3.0.0
 */
public abstract class ImportExportDialog extends Dialog {
    private Button okButton;
    private Button cancelButton;
    private Combo formatSelector;
    private Button hearder;
    private Text quoteText;
    private Text escapeText;
    private Text nullString;
    private Combo encodingSelector;
    private Group grpOptions;
    private Text otherText;
    private Button commaBtn;
    private Button tabBtn;
    private Button pipeBtn;
    private Button semiColonBtn;
    private Button otherslabel;
    private ColumnComponent columnComponent;

    private boolean isImport;

    /**
     * The import export data core.
     */
    protected AbstractImportExportDataCore importExportDataCore;

    /**
     * The Constant FILEENCODING_UTF.
     */
    protected static final String FILEENCODING_UTF = "UTF-8";

    /**
     * The Constant FILEENCODING_GBK.
     */
    protected static final String FILEENCODING_GBK = "GBK";

    /**
     * The Constant FILEENCODING_LATIN1.
     */
    protected static final String FILEENCODING_LATIN1 = "LATIN1";

    /**
     * Instantiates a new import export dialog.
     *
     * @param parent the parent
     * @param core the core
     */
    public ImportExportDialog(Shell parent, AbstractImportExportDataCore core) {
        super(parent);
        this.importExportDataCore = core;
        this.columnComponent = new ColumnComponent(parent, importExportDataCore);
        setDefaultImage(getWindowImage());
    }

    /**
     * Creates the buttons for button bar.
     *
     * @param parent the parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        final String okLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";
        final String cancelLabel = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        okButton = createButton(parent, UIConstants.OK_ID, okLabel, true);
        okButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_OK_001");
        okButton.setEnabled(false);
        columnComponent.setOKBtn(okButton);
        cancelButton = createButton(parent, CANCEL, cancelLabel, false);
        cancelButton.setData(MPPDBIDEConstants.SWTBOT_KEY, "ID_BUT_CONNECT_DB_CANCEL_001");
        setButtonLayoutData(okButton);
    }

    /**
     * Configure shell.
     *
     * @param newShellWindow the new shell window
     */
    @Override
    protected void configureShell(Shell newShellWindow) {
        super.configureShell(newShellWindow);
        newShellWindow.setText(getWindowTitle());
        newShellWindow.setImage(getWindowImage());
    }

    @Override
    protected boolean isResizable() {
        return true;
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        Composite curComposite = (Composite) super.createDialogArea(parent);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        curComposite.setLayout(gridLayout);
        curComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // Create the column components
        columnComponent.createComponents(curComposite);

        return curComposite;
    }

    /**
     * Grp general info main composite area.
     *
     * @param curComposite the cur composite
     * @return the composite
     */
    protected Composite grpGeneralInfoMainCompositeArea(Composite curComposite) {
        Composite grpGeneralInfoMain = new Composite(curComposite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = true;
        grpGeneralInfoMain.setLayout(gridLayout);
        grpGeneralInfoMain.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        if (importExportDataCore.isOLAPDB()) {
            addCommonOptions(grpGeneralInfoMain);
        } else {
            if (isImport()) {
                addZCommonOptionsForImport(grpGeneralInfoMain);
            } else {
                addZCommonOptionsForExport(grpGeneralInfoMain);
            }
        }
        return grpGeneralInfoMain;
    }

    /**
     * add common options
     * 
     * @param composite the composite
     */
    private void addCommonOptions(Composite composite) {
        Composite grpGeneralInfoSub1 = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        grpGeneralInfoSub1.setLayout(gridLayout);
        grpGeneralInfoSub1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addFormatSelectorUi(grpGeneralInfoSub1);

        addQuotesUi(grpGeneralInfoSub1);

        addEscapeUi(grpGeneralInfoSub1);

        addNullableUi(grpGeneralInfoSub1);

        addEncodingSelectionUi(grpGeneralInfoSub1);
        // Import the Excel date for importing table data.
        addDateSelection(grpGeneralInfoSub1);

        Composite grpGeneralInfoSub2 = new Composite(composite, SWT.NONE);

        gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        grpGeneralInfoSub2.setLayout(gridLayout);
        grpGeneralInfoSub2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addHeaderSelectionUi(grpGeneralInfoSub2);
        addDelimiterSelectionUi(grpGeneralInfoSub2);
        if (isImport()) {
            addEncodingNote(composite);
        }
    }

    /**
     * add common options for import
     * 
     * @param composite the composite
     */
    private void addZCommonOptionsForImport(Composite composite) {

        Composite grpGeneralInfoSub1 = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        grpGeneralInfoSub1.setLayout(gridLayout);
        grpGeneralInfoSub1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addFormatSelectorUi(grpGeneralInfoSub1);
        addEncodingSelectionUi(grpGeneralInfoSub1);

        Composite grpGeneralInfoSub2 = new Composite(composite, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        grpGeneralInfoSub2.setLayout(gridLayout);
        grpGeneralInfoSub2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
        addHeaderSelectionUi(grpGeneralInfoSub2);
        Label newLabel = new Label(grpGeneralInfoSub2, SWT.NONE);
        newLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        addDateSelection(grpGeneralInfoSub2);
        addEncodingNote(composite);
    }

    /**
     * add common options for export
     * 
     * @param composite the composite
     */
    private void addZCommonOptionsForExport(Composite composite) {
        Composite grpGeneralInfoSub1 = new Composite(composite, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        grpGeneralInfoSub1.setLayout(gridLayout);
        grpGeneralInfoSub1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addFormatSelectorUi(grpGeneralInfoSub1);

        Composite grpGeneralInfoSub2 = new Composite(composite, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        grpGeneralInfoSub2.setLayout(gridLayout);
        grpGeneralInfoSub2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        addEncodingSelectionUi(grpGeneralInfoSub2);
    }

    /**
     * Adds the date selection.
     *
     * @param curComposite the cur composite
     */
    protected void addDateSelection(Composite curComposite) {
    }

    /**
     * Checks if is import.
     *
     * @return true, if is import
     */
    public boolean isImport() {
        return isImport;
    }

    /**
     * Sets the import.
     *
     * @param isImport the new import
     */
    public void setImport(boolean isImport) {
        this.isImport = isImport;
    }

    private void addDelimiterSelectionUi(Composite grpGeneralInfo) {
        grpOptions = new Group(grpGeneralInfo, SWT.NONE);

        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        grpOptions.setLayout(gridLayout);
        grpOptions.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));

        grpOptions.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_DELIMITER));

        commaBtn = new Button(grpOptions, SWT.RADIO);
        commaBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        commaBtn.addSelectionListener(new DelimiterselectionHelper());
        commaBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_COMMA));

        tabBtn = new Button(grpOptions, SWT.RADIO);
        tabBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_TAB));
        tabBtn.addSelectionListener(new DelimiterselectionHelper());
        tabBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        pipeBtn = new Button(grpOptions, SWT.RADIO);
        pipeBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_PIPE));
        pipeBtn.addSelectionListener(new DelimiterselectionHelper());
        pipeBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        semiColonBtn = new Button(grpOptions, SWT.RADIO);
        semiColonBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_SEMICOLON));
        semiColonBtn.addSelectionListener(new DelimiterselectionHelper());

        semiColonBtn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        otherslabel = new Button(grpOptions, SWT.RADIO);

        otherslabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        otherslabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_OTHER));
        otherslabel.addSelectionListener(new DelimiterselectionHelper());
        otherText = new Text(grpOptions, SWT.BORDER);
        otherText.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_OTHERTOOLTIP));
        otherText.setEnabled(false);

        otherText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        otherText.addListener(SWT.MenuDetect, new InitListener());
    }

    private void addEncodingSelectionUi(Composite grpGeneralInfo) {
        Label encodingLabel = new Label(grpGeneralInfo, SWT.NONE);
        encodingLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        ;
        encodingLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_ENCODING));
        encodingSelector = new Combo(grpGeneralInfo, SWT.READ_ONLY);
        encodingSelector.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        encodingSelector.add(FILEENCODING_UTF);
        encodingSelector.add(FILEENCODING_GBK);
        encodingSelector.add(FILEENCODING_LATIN1);
        encodingSelector.select(0);
        encodingSelector.setText(
                PreferenceWrapper.getInstance().getPreferenceStore().getString(UserEncodingOption.FILE_ENCODING));
    }

    private void addNullableUi(Composite grpGeneralInfo) {
        Label nullLabel = new Label(grpGeneralInfo, SWT.NONE);

        nullLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        nullLabel.setText(getNULLLabel());
        nullString = new Text(grpGeneralInfo, SWT.NONE | SWT.BORDER);
        nullString.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        nullString.addListener(SWT.MenuDetect, new InitListener());
        nullString.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_NULL_TOOLTIP));
    }

    private void addEscapeUi(Composite grpGeneralInfo) {
        Label escape = new Label(grpGeneralInfo, SWT.NONE);
        escape.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        escape.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_ESCAPE));
        escapeText = new Text(grpGeneralInfo, SWT.NONE | SWT.BORDER);

        escapeText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        escapeText.addListener(SWT.MenuDetect, new InitListener());
        escapeText.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_TOOLTIP));
    }

    private void addQuotesUi(Composite grpGeneralInfo) {
        Label quotes = new Label(grpGeneralInfo, SWT.NONE);

        quotes.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        quotes.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_QUOTE));
        quoteText = new Text(grpGeneralInfo, SWT.NONE | SWT.BORDER);

        quoteText.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        quoteText.addListener(SWT.MenuDetect, new InitListener());
        quoteText.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_TOOLTIP));
    }

    private void addHeaderSelectionUi(Composite grpGeneralInfo) {
        hearder = new Button(grpGeneralInfo, SWT.CHECK);
        hearder.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_HEADER));

        hearder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        hearder.setSelection(true);
    }

    private void addFormatSelectorUi(Composite grpGeneralInfo) {
        Label formatLabel = new Label(grpGeneralInfo, SWT.NONE);
        formatLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_FORMAT));

        formatLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
        formatSelector = new Combo(grpGeneralInfo, SWT.READ_ONLY);

        formatSelector.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, true));
        getFormatSelector(formatSelector);
    }

    /**
     * Gets the format selector.
     *
     * @param formatSelector the format selector
     * @return the format selector
     */
    protected abstract void getFormatSelector(Combo formatSelector);

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        okButton.setEnabled(false);
        cancelButton.setEnabled(false);
        performOkOperation();
    }

    /**
     * Enable disable componens.
     *
     * @param value the value
     */
    protected void enableDisableComponens(boolean value) {
        /*
         * need to control include header status excel: mandatory included
         * binary: mandatory non-included others: could be designed by user
         */
        int formatSelectionIndex = formatSelector.getSelectionIndex();
        if (this.isImport) {
            hearder.setSelection(true);
            if (formatSelectionIndex == 0 || formatSelectionIndex == 1) {
                hearder.setSelection(!value);
                hearder.setEnabled(!value);
            } else {
                hearder.setEnabled(value);
            }
        } else if (importExportDataCore.isOLAPDB()) {
            hearder.setSelection(true);
            if (formatSelectionIndex == 0 || formatSelectionIndex == 1) {
                hearder.setSelection(!value);
                hearder.setEnabled(value);
            } else {
                hearder.setEnabled(value);
            }
        }

        if (importExportDataCore.isOLAPDB()) {
            componentChangesOLAPDB(value);
        }
    }

    private void componentChangesOLAPDB(boolean value) {
        /*
         * we need to clear the contents only if the component is going to be
         * disabled.
         */
        if (!value) {
            otherText.setText("");
        }
        quoteText.setText("");
        nullString.setText("");
        escapeText.setText("");
        grpOptions.setEnabled(value);
        otherText.setEnabled(value);
        quoteText.setEnabled(value);
        nullString.setEnabled(value);
        escapeText.setEnabled(value);
        commaBtn.setSelection(value);
        if (value) {
            otherText.setEnabled(false);
        }
        /*
         * Manually selecting a Radio button fires BN_CLICKED event. If button's
         * setSelection(boolean) method is used programmatically, BN_CLICKED
         * event is not fired. This event is responsible for resetting other
         * Radio buttons. Since it does not get fired, We need to manually reset
         * every other Radio button even if they all belong to same parent and
         * not created with SWT.NO_RADIO_GROUP
         */
        tabBtn.setSelection(false);
        pipeBtn.setSelection(false);
        semiColonBtn.setSelection(false);
        otherslabel.setSelection(false);
    }

    /**
     * Enable buttons.
     */
    public void enableButtons() {
        if (okButton.isDisposed() || cancelButton.isDisposed()) {
            return;
        }
        okButton.setEnabled(true);
        cancelButton.setEnabled(true);
    }

    /**
     * Toggle OK buttons.
     *
     * @param value the value
     */
    public void toggleOKButtons(boolean value) {
        if (!okButton.isDisposed()) {
            okButton.setEnabled(value);
        }

    }

    /**
     * Gets the window title.
     *
     * @return the window title
     */
    protected abstract String getWindowTitle();

    /**
     * Gets the NULL label.
     *
     * @return the NULL label
     */
    protected abstract String getNULLLabel();

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected abstract Image getWindowImage();

    /**
     * Perform ok operation.
     */
    protected abstract void performOkOperation();

    /**
     * Change file format.
     *
     * @param format the format
     */
    protected void changeFileFormat(String format) {
        if (MPPDBIDEConstants.STR_TEXT.equalsIgnoreCase(format)) {
            if (!formatSelector.getText().equalsIgnoreCase(format)) {
                formatSelector.select(4);
                enableDisableComponens(true);
            }

        } else if (MPPDBIDEConstants.STR_BINARY.equalsIgnoreCase(format)) {
            if (!formatSelector.getText().equalsIgnoreCase(format)) {
                formatSelector.select(3);
                enableDisableComponens(false);
            }
        } else if (MPPDBIDEConstants.STR_XLSX.equals(format)) {
            if (!formatSelector.getText().equalsIgnoreCase(format)) {
                formatSelector.select(0);
                enableDisableComponens(false);
            }
        } else if (MPPDBIDEConstants.STR_XLS.equals(format)) {
            if (!formatSelector.getText().equalsIgnoreCase(format)) {
                formatSelector.select(1);
                enableDisableComponens(false);
            }
        } else {
            if (!formatSelector.getText().equalsIgnoreCase(format)) {
                formatSelector.select(2);
                enableDisableComponens(true);
            }
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DelimiterselectionHelper.
     */
    private final class DelimiterselectionHelper implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent event) {
            if (MessageConfigLoader.getProperty(IMessagesConstants.IMPORT_EXPORT_DELIMETER_OTHER)
                    .equalsIgnoreCase(((Button) event.getSource()).getText())) {
                otherText.setEnabled(true);
            } else {
                otherText.setText("");
                otherText.setEnabled(false);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * Sets the import export data.
     *
     * @param importexportOption the new import export data
     */
    public void setimportExportData(ImportExportOption importexportOption) {
        importexportOption.setFileFormat(formatSelector.getItem(formatSelector.getSelectionIndex()));
        if (null != hearder && !hearder.isDisposed()) {
            importexportOption.setHeader(hearder.getSelection());
        }
        if (importExportDataCore.getImportExportServerObj() instanceof TableMetaData) {
            importexportOption.setQuotes(quoteText.getText());
            importexportOption.setDelimiter(getDelimiter());
            importexportOption.setEscape(escapeText.getText());
            importexportOption.setReplaceNull(nullString.getText());
        }
        importexportOption.setEncoding(encodingSelector.getItem(encodingSelector.getSelectionIndex()));
        importexportOption.setAllColunms(columnComponent.getAllColumns().getSelection());
        importexportOption.setTablecolumns(columnComponent.getSelectedColsList());
    }

    private String getDelimiter() {
        String delimiter = ",";
        if (commaBtn.getSelection()) {
            delimiter = ",";
        } else if (tabBtn.getSelection()) {
            delimiter = "\t";
        } else if (pipeBtn.getSelection()) {
            delimiter = "|";
        } else if (semiColonBtn.getSelection()) {
            delimiter = ";";
        } else {
            delimiter = otherText.getText();
        }

        return delimiter;
    }

    /**
     * Enable ok but.
     *
     * @param status the status
     */
    public void enableOkBut(boolean status) {
        if (this.okButton != null && !this.okButton.isDisposed()) {
            this.okButton.setEnabled(status);
        }
    }

    /**
     * Check selected columns.
     *
     * @return true, if successful
     */
    protected boolean checkSelectedColumns() {
        boolean isSelectedColumns = false;
        if (columnComponent.getSelectedColsList() != null && columnComponent.getSelectedColsList().size() > 0) {
            isSelectedColumns = true;
        }

        return isSelectedColumns;
    }

    private void addEncodingNote(Composite maincomp) {
        Label lblNotice = new Label(maincomp, SWT.WRAP);
        StringBuilder notice = new StringBuilder(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_NOTE));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        lblNotice.setText(notice.toString());
        lblNotice.setLayoutData(gridData);
    }

}
