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

import java.net.URL;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.MarginPainter;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.ISharedTextColors;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.LineNumberRulerColumn;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.internal.editors.text.EditorsPlugin;

import org.opengauss.mppdbide.gauss.sqlparser.comm.SQLFoldingRuleManager;
import org.opengauss.mppdbide.gauss.sqlparser.comm.SQLFormatEditorParser;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLDocumentPartitioner;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSourceViewerConfig;
import org.opengauss.mppdbide.view.core.sourceeditor.SQLSourceViewerDecorationSupport;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSFormatterPreferencePage.
 *
 * @since 3.0.0
 */
public class DSFormatterPreferencePage extends PreferencePage {
    /**
     * The Constant INDENT_TAB_LIMIT.
     */
    public static final int INDENT_TAB_LIMIT = 100;

    /**
     * The Constant RIGHT_MARGIN_LIMIT.
     */
    public static final int RIGHT_MARGIN_LIMIT = 50000;

    private static final String[] FORMAT_ITEMS = {
        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ONE_PARAM_PER_LINE),
        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ON_ONE_LINE),
        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FIT)};

    private static final String[] INDENT_STYLE_ITEMS = {
        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_TABS),
        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SPACES)};

    private static IPreferenceStore preferenceStore;

    private SourceViewer viewer;
    private boolean isPageCreated;
    private MarginPainter marginPainter;

    // General Tab
    private Spinner indentSpinner = null;
    private Spinner rightMarginSpinner = null;
    private Combo tabCharBtn = null;
    private Spinner tabCharSizeSpinner = null;
    private Button alignDeclarationBtn = null;
    private Button alignAssignmentsBtn = null;

    // Control structures
    private Button controlStructThenOnNewLineBtn = null;
    private Button controlStructSplitAndOrBtn = null;
    private Button controlStructAndOrAfterExpressionBtn = null;
    private Button controlStructLoopOnNewLineBtn = null;

    // DML Common
    private Button dmlLeftAlignKeywordsBtn = null;
    private Button dmlLeftAlignItemsBtn = null;
    private Button dmlWhrSplitAndOrBtn = null;
    private Button dmlWhrAndOrAfterExpBtn = null;
    private Button dmlWhrAndOrUnderWhereBtn = null;

    // DML Select
    private Combo dmlSelectFormatComboBtn = null;
    private Button dmlSelectAlignBtn = null;
    private Button dmlSelectCommaAfterBtn = null;

    // DML Insert
    private Combo dmlInsertFormatComboBtn = null;
    private Button dmlInsertCommaAfterBtn = null;

    // DML Update
    private Combo dmlUpdateFormatComboBtn = null;
    private Button dmlUpdateAlignBtn = null;
    private Button dmlUpdateCommaAfterBtn = null;

    // DML Others
    private Combo dmlOtherFormatComboBtn = null;
    private Button itemListAlignBtn = null;
    private Button itemListCommaAfterBtn = null;

    // Parameter Declaration
    private Combo parameterComboBtn = null;
    private Button parameterAlignBtn = null;
    private Button parameterCommaAfterBtn = null;
    private Button parameterAtLeftMarginBtn = null;

    /**
     * Instantiates a new DS formatter preference page.
     */
    public DSFormatterPreferencePage() {
        super(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_NODE));
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
        Composite mainComposite = new Composite(parent, SWT.FILL);
        mainComposite.setLayout(new GridLayout(2, false));
        GridData mainCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        mainComposite.setLayoutData(mainCompositeGD);

        createInputScrolledComposite(mainComposite);
        createPreviewGroup(mainComposite);
        createImportExportBtnComposite(mainComposite);
        isPageCreated = true;
        return null;
    }

    private void createInputScrolledComposite(Composite mainComposite) {
        ScrolledComposite inputCompositeSC = new ScrolledComposite(mainComposite,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        GridLayout inputCompositeScGL = new GridLayout(1, false);
        inputCompositeScGL.marginWidth = 0;
        inputCompositeScGL.marginHeight = 0;
        inputCompositeSC.setLayout(inputCompositeScGL);
        GridData inputCompositeSCGD = new GridData(SWT.NONE, SWT.FILL, false, false);
        inputCompositeSCGD.heightHint = 550;
        inputCompositeSC.setLayoutData(inputCompositeSCGD);

        Composite inputMainComp = new Composite(inputCompositeSC, SWT.NONE);
        inputCompositeSC.setContent(inputMainComp);
        inputMainComp.setLayout(new GridLayout(2, false));
        GridData inputMainCompGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        inputMainComp.setLayoutData(inputMainCompGD);

        createGeneralItems(inputMainComp);
        createConditionalStmtsOnLineLineItems(inputMainComp);
        createConditionalStmtsAndOrItems(inputMainComp);
        createDMLGroup(inputMainComp);
        createFuncProcParamGroup(inputMainComp);

        inputCompositeSC.setExpandHorizontal(true);
        inputCompositeSC.setExpandVertical(true);
        inputCompositeSC.setMinSize(inputMainComp.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        inputCompositeSC.pack();
    }

    private void createGeneralItems(Composite inputMainComp) {
        createRightMarginSpinner(inputMainComp);
        createIndentSpinner(inputMainComp);
        createGeneralTabCharBtn(inputMainComp);
        createTabCharSizeSpinner(inputMainComp);
        createGeneralAlignDeclareBtn(inputMainComp);
        createGeneralAlignAssignmentBtn(inputMainComp);
    }

    private void createRightMarginSpinner(Composite generalTabLeftComp) {
        Label rightMarginLbl = new Label(generalTabLeftComp, SWT.WRAP);
        rightMarginLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_RIGHT_MARGIN));
        rightMarginLbl.pack();
        rightMarginSpinner = new Spinner(generalTabLeftComp, SWT.BORDER);
        GridData rightMarginSpinnerGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        rightMarginSpinner.setLayoutData(rightMarginSpinnerGD);
        setSpinnerMaxValueListner(rightMarginSpinner, RIGHT_MARGIN_LIMIT);
        rightMarginSpinner.setEnabled(false);
        rightMarginSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF));
        rightMarginSpinner.addSelectionListener(getFormatterSelectionListener());
        rightMarginSpinner.addSelectionListener(getMarginPaintSelectionListener());
    }

    private void createIndentSpinner(Composite generalTabLeftComp) {
        Label indentLbl = new Label(generalTabLeftComp, SWT.WRAP);
        indentLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_INDENT));
        indentLbl.pack();
        indentSpinner = new Spinner(generalTabLeftComp, SWT.BORDER);
        GridData indentSpinnerGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        indentSpinner.setLayoutData(indentSpinnerGD);
        setSpinnerMaxValueListner(indentSpinner, INDENT_TAB_LIMIT);
        indentSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_INDENT_PREF));
        indentSpinner.addSelectionListener(getFormatterSelectionListener());
    }

    private void createGeneralTabCharBtn(Composite generalTabLeftComp) {
        Label tabCharBtnLbl = new Label(generalTabLeftComp, SWT.NONE);
        tabCharBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_USE_TAB_CHAR));

        tabCharBtn = new Combo(generalTabLeftComp, SWT.READ_ONLY);
        tabCharBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        tabCharBtn.setItems(INDENT_STYLE_ITEMS);
        tabCharBtn.select(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF) ? 1 : 0);
        tabCharBtn.addSelectionListener(getFormatterSelectionListener());
        tabCharBtn.pack();
    }

    private boolean getTabCharSelection() {
        return (tabCharBtn.getSelectionIndex() == 1) ? true : false;
    }

    private void createTabCharSizeSpinner(Composite generalTabLeftComp) {
        Label tabCharSizeLbl = new Label(generalTabLeftComp, SWT.WRAP);
        tabCharSizeLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_TAB_CHAR_SIZE));
        tabCharSizeLbl.pack();
        tabCharSizeSpinner = new Spinner(generalTabLeftComp, SWT.BORDER);
        GridData tabCharSizeSpinnerGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        tabCharSizeSpinner.setLayoutData(tabCharSizeSpinnerGD);
        setSpinnerMaxValueListner(tabCharSizeSpinner, INDENT_TAB_LIMIT);
        tabCharSizeSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF));
        tabCharSizeSpinner.addSelectionListener(getFormatterSelectionListener());
    }

    private void createGeneralAlignDeclareBtn(Composite generalTabLeftComp) {
        Label alignDeclarationBtnLbl = new Label(generalTabLeftComp, SWT.NONE);
        alignDeclarationBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN_DECLARATION));

        alignDeclarationBtn = new Button(generalTabLeftComp, SWT.CHECK);
        alignDeclarationBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        alignDeclarationBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_DECLARATION_PREF));
        alignDeclarationBtn.pack();
        alignDeclarationBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createGeneralAlignAssignmentBtn(Composite generalTabLeftComp) {
        Label alignAssignmentsBtnLbl = new Label(generalTabLeftComp, SWT.NONE);
        alignAssignmentsBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN_ASSIGNMENTS));

        alignAssignmentsBtn = new Button(generalTabLeftComp, SWT.CHECK);
        alignAssignmentsBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        alignAssignmentsBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT_PREF));
        alignAssignmentsBtn.pack();
        alignAssignmentsBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createConditionalStmtsOnLineLineItems(Composite inputMainComp) {
        createThenOnNewLineBtn(inputMainComp);
        createLoopOnNewLineBtn(inputMainComp);
    }

    private void createThenOnNewLineBtn(Composite inputMainComp) {
        Label controlStructThenOnNewLineBtnLbl = new Label(inputMainComp, SWT.NONE);
        controlStructThenOnNewLineBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_THEN_ON_NEW_LINE));

        controlStructThenOnNewLineBtn = new Button(inputMainComp, SWT.CHECK);
        controlStructThenOnNewLineBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        controlStructThenOnNewLineBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE_PREF));
        controlStructThenOnNewLineBtn.pack();
        controlStructThenOnNewLineBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createLoopOnNewLineBtn(Composite inputMainComp) {
        Label controlStructLoopOnNewLineBtnLbl = new Label(inputMainComp, SWT.NONE);
        controlStructLoopOnNewLineBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_LOOP_ON_NEW_LINE));

        controlStructLoopOnNewLineBtn = new Button(inputMainComp, SWT.CHECK);
        controlStructLoopOnNewLineBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        controlStructLoopOnNewLineBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF));
        controlStructLoopOnNewLineBtn.pack();
        controlStructLoopOnNewLineBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createConditionalStmtsAndOrItems(Composite inputMainComp) {
        createAndOrSplitAtZeroLevelBtn(inputMainComp);
        createAndOrAfterExpBtn(inputMainComp);

        controlStructSplitAndOrBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                controlStructAndOrAfterExpressionBtn.setEnabled(controlStructSplitAndOrBtn.getSelection());
                if (!controlStructSplitAndOrBtn.getSelection()) {
                    controlStructAndOrAfterExpressionBtn.setSelection(false);
                }
            }
        });
    }

    private void createAndOrSplitAtZeroLevelBtn(Composite inputMainComp) {
        Label controlStructSplitAndOrBtnLbl = new Label(inputMainComp, SWT.NONE);
        controlStructSplitAndOrBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SPLIT_AND_OR));

        controlStructSplitAndOrBtn = new Button(inputMainComp, SWT.CHECK);
        controlStructSplitAndOrBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        controlStructSplitAndOrBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR_PREF));
        controlStructSplitAndOrBtn.pack();
        controlStructSplitAndOrBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createAndOrAfterExpBtn(Composite inputMainComp) {
        Label controlStructAndOrAfterExpressionBtnLbl = new Label(inputMainComp, SWT.NONE);
        controlStructAndOrAfterExpressionBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_AND_OR_AFTER_EXP));

        controlStructAndOrAfterExpressionBtn = new Button(inputMainComp, SWT.CHECK);
        controlStructAndOrAfterExpressionBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        controlStructAndOrAfterExpressionBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP_PREF));
        controlStructAndOrAfterExpressionBtn.setEnabled(controlStructSplitAndOrBtn.getSelection());
        controlStructAndOrAfterExpressionBtn.pack();
        controlStructAndOrAfterExpressionBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDMLGroup(Composite inputMainComp) {
        Group dmlGrp = new Group(inputMainComp, SWT.NONE);
        dmlGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_DML));
        dmlGrp.setLayout(new GridLayout(1, false));
        GridData dmlGrpGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlGrpGD.horizontalSpan = 2;
        dmlGrpGD.verticalIndent = 10;
        dmlGrp.setLayoutData(dmlGrpGD);

        TabFolder dmlTabFolder = new TabFolder(dmlGrp, SWT.NONE);
        dmlTabFolder.setLayout(new GridLayout(1, false));
        GridData dmlTabFolderGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlTabFolderGD.heightHint = 120;
        dmlTabFolder.setLayoutData(dmlTabFolderGD);

        createDmlCommonTab(dmlTabFolder);
        createDmlSelectTab(dmlTabFolder);
        createDmlInsertTab(dmlTabFolder);
        createDmlUpdateTab(dmlTabFolder);
        createDmlOthersTab(dmlTabFolder);
    }

    private void createDmlCommonTab(final TabFolder tabFolder) {
        TabItem dmlCommonTab = new TabItem(tabFolder, SWT.NULL);
        dmlCommonTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_GENERAL));

        Composite dmlCommonTabComp = new Composite(tabFolder, SWT.NONE);
        dmlCommonTabComp.setLayout(new GridLayout(2, false));
        GridData dmlCommonTabCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlCommonTabComp.setLayoutData(dmlCommonTabCompGD);

        createDmlLeftAlignCompositeItems(dmlCommonTabComp);
        createDmlAndOrCompositeItems(dmlCommonTabComp);

        dmlCommonTab.setControl(dmlCommonTabComp);
    }

    private void createDmlLeftAlignCompositeItems(Composite dmlCommonTabComp) {
        Label dmlLeftAlignKeywordsBtnLbl = new Label(dmlCommonTabComp, SWT.NONE);
        dmlLeftAlignKeywordsBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_LEFT_ALIGN_KEYWORDS));

        dmlLeftAlignKeywordsBtn = new Button(dmlCommonTabComp, SWT.CHECK);
        dmlLeftAlignKeywordsBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlLeftAlignKeywordsBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF));
        dmlLeftAlignKeywordsBtn.pack();
        dmlLeftAlignKeywordsBtn.addSelectionListener(getFormatterSelectionListener());

        Label dmlLeftAlignItemsBtnLbl = new Label(dmlCommonTabComp, SWT.NONE);
        dmlLeftAlignItemsBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_LEFT_ALIGN_ITEMS));

        dmlLeftAlignItemsBtn = new Button(dmlCommonTabComp, SWT.CHECK);
        dmlLeftAlignItemsBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlLeftAlignItemsBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_ITEMS_PREF));
        dmlLeftAlignItemsBtn.setEnabled(dmlLeftAlignKeywordsBtn.getSelection());
        dmlLeftAlignItemsBtn.pack();
        dmlLeftAlignItemsBtn.addSelectionListener(getFormatterSelectionListener());

        dmlLeftAlignKeywordsBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dmlLeftAlignItemsBtn.setEnabled(dmlLeftAlignKeywordsBtn.getSelection());
            }
        });
    }

    private void createDmlAndOrCompositeItems(Composite dmlCommonTabComp) {
        Label dmlWhrSplitAndOrBtnLbl = new Label(dmlCommonTabComp, SWT.NONE);
        dmlWhrSplitAndOrBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SPLIT_AND_OR));

        dmlWhrSplitAndOrBtn = new Button(dmlCommonTabComp, SWT.CHECK);
        dmlWhrSplitAndOrBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlWhrSplitAndOrBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SPLIT_AND_OR_PREF));
        dmlWhrSplitAndOrBtn.pack();
        dmlWhrSplitAndOrBtn.addSelectionListener(getFormatterSelectionListener());

        Label dmlWhrAndOrAfterExpBtnLbl = new Label(dmlCommonTabComp, SWT.NONE);
        dmlWhrAndOrAfterExpBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_AND_OR_AFTER_EXP));

        dmlWhrAndOrAfterExpBtn = new Button(dmlCommonTabComp, SWT.CHECK);
        dmlWhrAndOrAfterExpBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlWhrAndOrAfterExpBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP_PREF));
        dmlWhrAndOrAfterExpBtn.setEnabled(dmlWhrSplitAndOrBtn.getSelection());
        dmlWhrAndOrAfterExpBtn.pack();
        dmlWhrAndOrAfterExpBtn.addSelectionListener(getFormatterSelectionListener());

        Label dmlWhrAndOrUnderWhereBtnLbl = new Label(dmlCommonTabComp, SWT.NONE);
        dmlWhrAndOrUnderWhereBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_AND_OR_UNDER));

        dmlWhrAndOrUnderWhereBtn = new Button(dmlCommonTabComp, SWT.CHECK);
        dmlWhrAndOrUnderWhereBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlWhrAndOrUnderWhereBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE_PREF));
        dmlWhrAndOrUnderWhereBtn
                .setEnabled(dmlWhrSplitAndOrBtn.getSelection() && !dmlWhrAndOrAfterExpBtn.getSelection());
        dmlWhrAndOrUnderWhereBtn.pack();
        dmlWhrAndOrUnderWhereBtn.addSelectionListener(getFormatterSelectionListener());

        addDMLSplitAndOrBtnListener();
        addDMLWhrAndOrAfterExpBtnListner();
    }

    private void addDMLSplitAndOrBtnListener() {
        dmlWhrSplitAndOrBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dmlWhrAndOrAfterExpBtn.setEnabled(dmlWhrSplitAndOrBtn.getSelection());
                dmlWhrAndOrUnderWhereBtn.setEnabled(dmlWhrSplitAndOrBtn.getSelection());
                if (!dmlWhrSplitAndOrBtn.getSelection()) {
                    dmlWhrAndOrAfterExpBtn.setSelection(false);
                    dmlWhrAndOrUnderWhereBtn.setSelection(false);
                }
            }
        });
    }

    private void addDMLWhrAndOrAfterExpBtnListner() {
        dmlWhrAndOrAfterExpBtn.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                dmlWhrAndOrUnderWhereBtn.setEnabled(!dmlWhrAndOrAfterExpBtn.getSelection());
                if (dmlWhrAndOrAfterExpBtn.getSelection()) {
                    dmlWhrAndOrUnderWhereBtn.setSelection(false);
                }
            }
        });
    }

    private void createDmlSelectTab(final TabFolder tabFolder) {
        TabItem dmlSelectTab = new TabItem(tabFolder, SWT.NULL);
        dmlSelectTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_SELECT));

        Composite dmlSelectTabComp = new Composite(tabFolder, SWT.NONE);
        dmlSelectTabComp.setLayout(new GridLayout(2, false));
        GridData dmlSelectTabCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlSelectTabComp.setLayoutData(dmlSelectTabCompGD);

        createDmlSelectFormatComboBtn(dmlSelectTabComp);
        createDmlSelectAlignBtn(dmlSelectTabComp);
        createDmlSelectCommaAfterBtn(dmlSelectTabComp);

        dmlSelectTab.setControl(dmlSelectTabComp);
    }

    private void createDmlSelectFormatComboBtn(Composite dmlSelectTabComp) {
        Label dmlSelectFormatComboBtnLbl = new Label(dmlSelectTabComp, SWT.NONE);
        dmlSelectFormatComboBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));

        dmlSelectFormatComboBtn = new Combo(dmlSelectTabComp, SWT.READ_ONLY);
        dmlSelectFormatComboBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlSelectFormatComboBtn.setItems(FORMAT_ITEMS);
        dmlSelectFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_SELECT_FORMAT_PREF));
        dmlSelectFormatComboBtn.addSelectionListener(getFormatterSelectionListener());
        dmlSelectFormatComboBtn.pack();
    }

    private void createDmlSelectAlignBtn(Composite dmlSelectGrp) {
        Label dmlSelectAlignBtnLbl = new Label(dmlSelectGrp, SWT.NONE);
        dmlSelectAlignBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN));

        dmlSelectAlignBtn = new Button(dmlSelectGrp, SWT.CHECK);
        dmlSelectAlignBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlSelectAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_ALIGN_PREF));
        dmlSelectAlignBtn.pack();
        dmlSelectAlignBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlSelectCommaAfterBtn(Composite dmlSelectGrp) {
        Label dmlSelectCommaAfterBtnLbl = new Label(dmlSelectGrp, SWT.NONE);
        dmlSelectCommaAfterBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_COMMA_AFTER_ITEM));

        dmlSelectCommaAfterBtn = new Button(dmlSelectGrp, SWT.CHECK);
        dmlSelectCommaAfterBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlSelectCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER_PREF));
        dmlSelectCommaAfterBtn.pack();
        dmlSelectCommaAfterBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlInsertTab(final TabFolder tabFolder) {
        TabItem dmlInsertTab = new TabItem(tabFolder, SWT.NULL);
        dmlInsertTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_INSERT));

        Composite dmlInsertTabComp = new Composite(tabFolder, SWT.NONE);
        dmlInsertTabComp.setLayout(new GridLayout(2, false));
        GridData dmlInsertTabCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlInsertTabComp.setLayoutData(dmlInsertTabCompGD);

        createDmlInsertFormatComboBtn(dmlInsertTabComp);
        createDmlInsertCommaAfterBtn(dmlInsertTabComp);

        dmlInsertTab.setControl(dmlInsertTabComp);
    }

    private void createDmlInsertFormatComboBtn(Composite dmlInsertTabComp) {
        Label dmlInsertFormatComboBtnLbl = new Label(dmlInsertTabComp, SWT.NONE);
        dmlInsertFormatComboBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));

        dmlInsertFormatComboBtn = new Combo(dmlInsertTabComp, SWT.READ_ONLY);
        dmlInsertFormatComboBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlInsertFormatComboBtn.setItems(FORMAT_ITEMS);
        dmlInsertFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_INSERT_FORMAT_PREF));
        dmlInsertFormatComboBtn.addSelectionListener(getFormatterSelectionListener());
        dmlInsertFormatComboBtn.pack();
    }

    private void createDmlInsertCommaAfterBtn(Composite dmlInsertGrp) {
        Label dmlInsertCommaAfterBtnLbl = new Label(dmlInsertGrp, SWT.NONE);
        dmlInsertCommaAfterBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_COMMA_AFTER_ITEM));

        dmlInsertCommaAfterBtn = new Button(dmlInsertGrp, SWT.CHECK);
        dmlInsertCommaAfterBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlInsertCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER_PREF));
        dmlInsertCommaAfterBtn.pack();
        dmlInsertCommaAfterBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlUpdateTab(final TabFolder tabFolder) {
        TabItem dmlUpdateTab = new TabItem(tabFolder, SWT.NULL);
        dmlUpdateTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_UPDATE));

        Composite dmlUpdateTabComp = new Composite(tabFolder, SWT.NONE);
        dmlUpdateTabComp.setLayout(new GridLayout(2, false));
        GridData dmlUpdateTabCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlUpdateTabComp.setLayoutData(dmlUpdateTabCompGD);

        createDmlUpdateFormatComboBtn(dmlUpdateTabComp);
        createDmlUpdateAlignBtn(dmlUpdateTabComp);
        createDmlUpdateCommaAfterBtn(dmlUpdateTabComp);

        dmlUpdateTab.setControl(dmlUpdateTabComp);
    }

    private void createDmlUpdateFormatComboBtn(Composite dmlUpdateTabComp) {
        Label dmlUpdateFormatComboBtnLbl = new Label(dmlUpdateTabComp, SWT.NONE);
        dmlUpdateFormatComboBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));

        dmlUpdateFormatComboBtn = new Combo(dmlUpdateTabComp, SWT.READ_ONLY);
        dmlUpdateFormatComboBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlUpdateFormatComboBtn.setItems(FORMAT_ITEMS);
        dmlUpdateFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_UPDATE_FORMAT_PREF));
        dmlUpdateFormatComboBtn.addSelectionListener(getFormatterSelectionListener());
        dmlUpdateFormatComboBtn.pack();
    }

    private void createDmlUpdateAlignBtn(Composite dmlUpdateGrp) {
        Label dmlUpdateAlignBtnLbl = new Label(dmlUpdateGrp, SWT.NONE);
        dmlUpdateAlignBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN));

        dmlUpdateAlignBtn = new Button(dmlUpdateGrp, SWT.CHECK);
        dmlUpdateAlignBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlUpdateAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_ALIGN_PREF));
        dmlUpdateAlignBtn.pack();
        dmlUpdateAlignBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlUpdateCommaAfterBtn(Composite dmlUpdateGrp) {
        Label dmlUpdateCommaAfterBtnLbl = new Label(dmlUpdateGrp, SWT.NONE);
        dmlUpdateCommaAfterBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_COMMA_AFTER_ITEM));

        dmlUpdateCommaAfterBtn = new Button(dmlUpdateGrp, SWT.CHECK);
        dmlUpdateCommaAfterBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlUpdateCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER_PREF));
        dmlUpdateCommaAfterBtn.pack();
        dmlUpdateCommaAfterBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlOthersTab(final TabFolder tabFolder) {
        TabItem dmlOthersTab = new TabItem(tabFolder, SWT.NULL);
        dmlOthersTab.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_OTHERS));

        Composite dmlOthersTabComp = new Composite(tabFolder, SWT.NONE);
        dmlOthersTabComp.setLayout(new GridLayout(2, false));
        GridData dmlOthersTabCompGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        dmlOthersTabComp.setLayoutData(dmlOthersTabCompGD);

        createDmlOtherFormatComboBtn(dmlOthersTabComp);
        createDmlOthersAlignBtn(dmlOthersTabComp);
        createDmlOthersCommaAfterBtn(dmlOthersTabComp);

        dmlOthersTab.setControl(dmlOthersTabComp);
    }

    private void createDmlOtherFormatComboBtn(Composite dmlOthersTabComp) {
        Label dmlOtherFormatComboBtnLbl = new Label(dmlOthersTabComp, SWT.NONE);
        dmlOtherFormatComboBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));

        dmlOtherFormatComboBtn = new Combo(dmlOthersTabComp, SWT.READ_ONLY);
        dmlOtherFormatComboBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        dmlOtherFormatComboBtn.setItems(FORMAT_ITEMS);
        dmlOtherFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT_PREF));
        dmlOtherFormatComboBtn.addSelectionListener(getFormatterSelectionListener());
        dmlOtherFormatComboBtn.pack();
    }

    private void createDmlOthersCommaAfterBtn(Composite dmlOthersTabComp) {
        Label itemListCommaAfterBtnLbl = new Label(dmlOthersTabComp, SWT.NONE);
        itemListCommaAfterBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_COMMA_AFTER_ITEM));

        itemListCommaAfterBtn = new Button(dmlOthersTabComp, SWT.CHECK);
        itemListCommaAfterBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        itemListCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER_PREF));
        itemListCommaAfterBtn.pack();
        itemListCommaAfterBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createDmlOthersAlignBtn(Composite dmlOthersTabComp) {
        Label itemListAlignBtnLbl = new Label(dmlOthersTabComp, SWT.NONE);
        itemListAlignBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN));

        itemListAlignBtn = new Button(dmlOthersTabComp, SWT.CHECK);
        itemListAlignBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        itemListAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN_PREF));
        itemListAlignBtn.pack();
        itemListAlignBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createFuncProcParamGroup(Composite inputMainComp) {
        Group funcProcParamGrp = new Group(inputMainComp, SWT.NONE);
        funcProcParamGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_PARAMATER));
        funcProcParamGrp.setLayout(new GridLayout(2, false));
        GridData funcProcParamGrpGD = new GridData(SWT.FILL, SWT.FILL, true, false);
        funcProcParamGrpGD.horizontalSpan = 2;
        funcProcParamGrp.setLayoutData(funcProcParamGrpGD);

        createParamFormatComboBtn(funcProcParamGrp);
        createParamAlignBtn(funcProcParamGrp);
        createParmCommaAfterBtn(funcProcParamGrp);
        createParmAtLeftMarginBtn(funcProcParamGrp);
    }

    private void createParamFormatComboBtn(Group funcProcParamGrp) {
        Label parameterAlignBtnLbl = new Label(funcProcParamGrp, SWT.NONE);
        parameterAlignBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_FORMAT));

        parameterComboBtn = new Combo(funcProcParamGrp, SWT.READ_ONLY);
        parameterComboBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        parameterComboBtn.setItems(FORMAT_ITEMS);
        parameterComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.PARAM_FORMAT_PREF));
        parameterComboBtn.addSelectionListener(getFormatterSelectionListener());
        parameterComboBtn.pack();
    }

    private void createParamAlignBtn(Group paramDeclareGrp) {
        Label parameterAlignBtnLbl = new Label(paramDeclareGrp, SWT.NONE);
        parameterAlignBtnLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_ALIGN_DATATYPES));

        parameterAlignBtn = new Button(paramDeclareGrp, SWT.CHECK);
        parameterAlignBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        parameterAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_ALIGN_PREF));
        parameterAlignBtn.pack();
        parameterAlignBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createParmCommaAfterBtn(Group paramDeclareGrp) {
        Label parameterCommaAfterBtnLbl = new Label(paramDeclareGrp, SWT.NONE);
        parameterCommaAfterBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_COMMA_AFTER_DATATYPE));

        parameterCommaAfterBtn = new Button(paramDeclareGrp, SWT.CHECK);
        parameterCommaAfterBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        parameterCommaAfterBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_COMMA_AFTER_PREF));
        parameterCommaAfterBtn.pack();
        parameterCommaAfterBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createParmAtLeftMarginBtn(Group paramDeclareGrp) {
        Label parameterAtLeftMarginBtnLbl = new Label(paramDeclareGrp, SWT.NONE);
        parameterAtLeftMarginBtnLbl
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_LIST_AT_LEFT_MARGIN));

        parameterAtLeftMarginBtn = new Button(paramDeclareGrp, SWT.CHECK);
        parameterAtLeftMarginBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false));
        parameterAtLeftMarginBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN_PREF));
        parameterAtLeftMarginBtn.pack();
        parameterAtLeftMarginBtn.addSelectionListener(getFormatterSelectionListener());
    }

    private void createPreviewGroup(Composite mainComposite) {
        Group preview = new Group(mainComposite, SWT.NONE);
        preview.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_PREVIEW));
        preview.setLayout(new GridLayout(1, false));
        GridData previewGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        previewGD.minimumWidth = 400;
        preview.setLayoutData(previewGD);

        loadTabDisplayContentFromJson();
        createViewer(preview);
    }

    private void loadTabDisplayContentFromJson() {
        FormatterContent.getInstance().loadContent(getTemplateFilePath());
    }

    private static URL getTemplateFilePath() {
        URL formatterTemplateURL = null;
        ClassLoader classLoader = DSTemplatePreferencePage.class.getClassLoader();
        if (null != classLoader) {
            formatterTemplateURL = classLoader.getResource("formatter_template.json");
            return formatterTemplateURL;
        }

        return formatterTemplateURL;
    }

    /**
     * Creates the viewer.
     *
     * @param parent the parent
     * @return the source viewer
     */
    private SourceViewer createViewer(Composite parent) {
        viewer = new SourceViewer(parent, createVerticalRuler(), null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        viewer.configure(new SQLSourceViewerConfig(null));
        viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        ResourceManager resManager = new LocalResourceManager(JFaceResources.getResources(), parent);
        setViewerFont(resManager);
        setActiveTabDocAfterFormat();
        setDecoration(viewer);
        viewer.setEditable(false);
        createMarginPainter();
        return viewer;
    }

    /**
     * Creates the vertical ruler.
     *
     * @return the i vertical ruler
     */
    protected IVerticalRuler createVerticalRuler() {
        CompositeRuler ruler = new CompositeRuler(1);
        ruler.addDecorator(0, createLineNumberRulerColumn());
        return ruler;
    }

    private IVerticalRulerColumn createLineNumberRulerColumn() {
        LineNumberRulerColumn column = new LineNumberRulerColumn();
        column.setForeground(new Color(Display.getDefault(), 104, 99, 94));
        return column;
    }

    /**
     * Sets the viewer font.
     *
     * @param resManager the new viewer font
     */
    private void setViewerFont(ResourceManager resManager) {
        Font font = resManager.createFont(FontDescriptor.createFrom("Courier New",
                preferenceStore.getInt(MPPDBIDEConstants.PREF_FONT_STYLE_SIZE), SWT.NORMAL));
        viewer.getTextWidget().setFont(font);
    }

    /**
     * Sets the active tab doc after format.
     */
    private void setActiveTabDocAfterFormat() {
        PreferenceStore pf = new PreferenceStore();
        performOkOnTabs(pf);

        SQLFormatEditorParser lSQLEditorParser = new SQLFormatEditorParser();
        lSQLEditorParser.setDocument(getActiveTabDocument());
        SQLFoldingRuleManager lSQLRuleManager = new SQLFoldingRuleManager();
        lSQLRuleManager.refreshRules();
        lSQLEditorParser.setRuleManager(lSQLRuleManager);
        String formattedQuery = null;
        try {
            formattedQuery = lSQLEditorParser.parseSQLDocuement(pf, 0, getActiveTabDocument().getLength(), 0);
        } catch (Exception exp) {
            MPPDBIDELoggerUtility.error("Formatting cancelled by user.");
        }
        viewer.setDocument(new Document(formattedQuery));
        SQLDocumentPartitioner.connectDocument(viewer.getDocument(), 0);
    }

    /**
     * Gets the active tab document.
     *
     * @return the active tab document
     */
    private Document getActiveTabDocument() {
        return new Document(FormatterContent.getInstance().getGeneral());
    }

    /**
     * Sets the decoration.
     *
     * @param viewer the new decoration
     */
    @SuppressWarnings("restriction")
    private static void setDecoration(SourceViewer viewer) {
        ISharedTextColors sharedColors = EditorsPlugin.getDefault().getSharedTextColors();

        SQLSourceViewerDecorationSupport sourceViewerDecorationSupport = new SQLSourceViewerDecorationSupport(viewer,
                null, null, sharedColors);
        sourceViewerDecorationSupport.installDecorations();
    }

    private void createMarginPainter() {
        marginPainter = new MarginPainter(viewer);
        marginPainter.setMarginRulerColumn(preferenceStore.getInt(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF));
        marginPainter.setMarginRulerColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
        viewer.addPainter(marginPainter);
    }

    private void createImportExportBtnComposite(Composite mainComposite) {
        Composite importExportBtnComposite = new Composite(mainComposite, SWT.NONE);
        importExportBtnComposite.setLayout(new GridLayout(2, false));
        GridData importExportBtnCompositeGD = new GridData(SWT.FILL, SWT.FILL, true, true);
        importExportBtnCompositeGD.horizontalSpan = 2;
        importExportBtnComposite.setLayoutData(importExportBtnCompositeGD);

        Button importBtn = new Button(importExportBtnComposite, SWT.NONE);
        importBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_IMPORT));
        importBtn.pack();
        importBtn.addSelectionListener(importFileSelectionListener());

        Button exportBtn = new Button(importExportBtnComposite, SWT.NONE);
        exportBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_EXPORT));
        exportBtn.pack();
        exportBtn.addSelectionListener(exportFileSelectionListener());
    }

    private SelectionListener exportFileSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.SAVE);
                String[] filterExt = {"*.json"};
                String[] filterNames = {"JSON Files (*.json)"};
                dialog.setFilterExtensions(filterExt);
                dialog.setFilterNames(filterNames);
                String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                BeautifierRules br = BeautifierRules.getInstance();
                exportGeneralRules(br);
                exportCtrlStructRules(br);
                exportDMLRules(br);
                exportParamDeclarRules(br);
                br.writeBeautifierToDisk(fileName);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        };
    }

    private void exportParamDeclarRules(BeautifierRules br) {
        DSFormatterParameterDeclarationRules paramaterDeclaration = new DSFormatterParameterDeclarationRules();
        paramaterDeclaration.setParameterFormat(parameterComboBtn.getSelectionIndex());
        paramaterDeclaration.setParameterAlign(parameterAlignBtn.getSelection());
        paramaterDeclaration.setParameterCommaAfter(parameterCommaAfterBtn.getSelection());
        paramaterDeclaration.setParameterAtLeftMargin(parameterAtLeftMarginBtn.getSelection());
        br.setParameterDeclaration(paramaterDeclaration);
    }

    private void exportDMLRules(BeautifierRules br) {
        DSFormatterDMLRules dml = new DSFormatterDMLRules();
        dml.setDmlLeftAlignKeywords(dmlLeftAlignKeywordsBtn.getSelection());
        dml.setDmlLeftAlignItems(dmlLeftAlignItemsBtn.getSelection());
        dml.setDmlSelectFormat(dmlSelectFormatComboBtn.getSelectionIndex());
        dml.setDmlSelectAlign(dmlSelectAlignBtn.getSelection());
        dml.setDmlSelectCommaAfter(dmlSelectCommaAfterBtn.getSelection());
        dml.setDmlWhereSplitAndOr(dmlWhrSplitAndOrBtn.getSelection());
        dml.setDmlWhereAndOrAfterExpression(dmlWhrAndOrAfterExpBtn.getSelection());
        dml.setDmlWhereAndOrUnderWhere(dmlWhrAndOrUnderWhereBtn.getSelection());
        dml.setDmlInsertFormat(dmlInsertFormatComboBtn.getSelectionIndex());
        dml.setDmlInsertCommaAfter(dmlInsertCommaAfterBtn.getSelection());
        dml.setDmlUpdateFormat(dmlUpdateFormatComboBtn.getSelectionIndex());
        dml.setDmlUpdateAlign(dmlUpdateAlignBtn.getSelection());
        dml.setDmlUpdateCommaAfter(dmlUpdateCommaAfterBtn.getSelection());
        br.setDml(dml);
    }

    private void exportCtrlStructRules(BeautifierRules br) {
        DSFormatterControlStructureRules ctrlstructure = new DSFormatterControlStructureRules();
        ctrlstructure.setControlStructureThenOnNewLine(controlStructThenOnNewLineBtn.getSelection());
        ctrlstructure.setControlStructureSplitAndOr(controlStructSplitAndOrBtn.getSelection());
        ctrlstructure.setControlStructureAndOrAfterExpression(controlStructAndOrAfterExpressionBtn.getSelection());
        ctrlstructure.setControlStructureLoopOnNewLine(controlStructLoopOnNewLineBtn.getSelection());
        br.setControlStructures(ctrlstructure);
    }

    private void exportGeneralRules(BeautifierRules br) {
        DSFormatterGeneralRules general = new DSFormatterGeneralRules();
        general.setGeneralIndent(Integer.parseInt(indentSpinner.getText()));
        general.setGeneralRightMargin(Integer.parseInt(rightMarginSpinner.getText()));
        general.setGeneralUseTabCharacter(getTabCharSelection());
        general.setGeneralTabCharacterSize(Integer.parseInt(tabCharSizeSpinner.getText()));
        general.setGeneralAlignDeclarationGroups(alignDeclarationBtn.getSelection());
        general.setGeneralAlignAssignmentGroups(alignAssignmentsBtn.getSelection());
        general.setGeneralItemListFormat(dmlOtherFormatComboBtn.getSelectionIndex());
        general.setGeneralItemListAlign(itemListAlignBtn.getSelection());
        general.setGeneralItemListCommaAfter(itemListCommaAfterBtn.getSelection());
        br.setGeneral(general);
    }

    private SelectionListener importFileSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
                String[] filterExt = {"*.json"};
                String[] filterNames = {"JSON Files (*.json)"};
                dialog.setFilterExtensions(filterExt);
                dialog.setFilterNames(filterNames);
                String fileName = dialog.open();
                if (fileName == null) {
                    return;
                }
                BeautifierRules newBr = BeautifierRules.getInstance().readBeautifierFromDisk(fileName);
                if (newBr == null) {
                    return;
                }
                importGeneralRules(newBr);
                importCtrlStructRules(newBr);
                importDMLRules(newBr);
                importParamDeclarRules(newBr);
                setActiveTabDocAfterFormat();
                MPPDBIDEDialogs.generateOKMessageDialogInUI(MESSAGEDIALOGTYPE.INFORMATION, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_IMPORT),
                        MessageConfigLoader.getProperty(IMessagesConstants.CANCEL_IMPORT_SUCCES_DAILOG_TITLE));
                getApplyButton().setEnabled(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        };
    }

    private void importParamDeclarRules(BeautifierRules newBr) {
        DSFormatterParameterDeclarationRules paramaterDeclaration = newBr.getParameterDeclaration();
        if (paramaterDeclaration == null) {
            return;
        }
        parameterComboBtn.select(paramaterDeclaration.getParameterFormat());
        parameterAlignBtn.setSelection(paramaterDeclaration.isParameterAlign());
        parameterCommaAfterBtn.setSelection(paramaterDeclaration.isParameterCommaAfter());
        parameterAtLeftMarginBtn.setSelection(paramaterDeclaration.isParameterAtLeftMargin());
    }

    private void importDMLRules(BeautifierRules newBr) {
        DSFormatterDMLRules dml = newBr.getDml();
        if (dml == null) {
            return;
        }
        dmlLeftAlignKeywordsBtn.setSelection(dml.isDmlLeftAlignKeywords());
        dmlLeftAlignItemsBtn.setSelection(dml.isDmlLeftAlignItems());
        dmlLeftAlignItemsBtn.setEnabled(dml.isDmlLeftAlignKeywords());
        dmlSelectFormatComboBtn.select(dml.getDmlSelectFormat());
        dmlSelectAlignBtn.setSelection(dml.isDmlSelectAlign());
        dmlSelectCommaAfterBtn.setSelection(dml.isDmlSelectCommaAfter());
        dmlWhrSplitAndOrBtn.setSelection(dml.isDmlWhereSplitAndOr());
        dmlWhrAndOrAfterExpBtn.setSelection(dml.isDmlWhereAndOrAfterExpression());
        dmlWhrAndOrAfterExpBtn.setEnabled(dml.isDmlWhereSplitAndOr());
        dmlWhrAndOrUnderWhereBtn.setSelection(dml.isDmlWhereAndOrUnderWhere());
        dmlWhrAndOrUnderWhereBtn.setEnabled(dml.isDmlWhereSplitAndOr() && !dml.isDmlWhereAndOrAfterExpression());
        dmlInsertFormatComboBtn.select(dml.getDmlInsertFormat());
        dmlInsertCommaAfterBtn.setSelection(dml.isDmlInsertCommaAfter());
        dmlUpdateFormatComboBtn.select(dml.getDmlUpdateFormat());
        dmlUpdateAlignBtn.setSelection(dml.isDmlUpdateAlign());
        dmlUpdateCommaAfterBtn.setSelection(dml.isDmlUpdateCommaAfter());
    }

    private void importCtrlStructRules(BeautifierRules newBr) {
        DSFormatterControlStructureRules ctrlstructure = newBr.getControlStructures();
        if (ctrlstructure == null) {
            return;
        }
        controlStructThenOnNewLineBtn.setSelection(ctrlstructure.isControlStructureThenOnNewLine());
        controlStructSplitAndOrBtn.setSelection(ctrlstructure.isControlStructureSplitAndOr());
        controlStructAndOrAfterExpressionBtn.setSelection(ctrlstructure.isControlStructureAndOrAfterExpression());
        controlStructAndOrAfterExpressionBtn.setEnabled(ctrlstructure.isControlStructureSplitAndOr());
        controlStructLoopOnNewLineBtn.setSelection(ctrlstructure.isControlStructureLoopOnNewLine());
    }

    private void importGeneralRules(BeautifierRules newBr) {
        DSFormatterGeneralRules general = newBr.getGeneral();
        if (general == null) {
            return;
        }
        indentSpinner.setSelection(general.getGeneralIndent());
        rightMarginSpinner.setSelection(general.getGeneralRightMargin());
        marginPainter.setMarginRulerColumn(general.getGeneralRightMargin());
        tabCharBtn.select(general.isGeneralUseTabCharacter() ? 1 : 0);
        tabCharSizeSpinner.setSelection(general.getGeneralTabCharacterSize());
        alignDeclarationBtn.setSelection(general.isGeneralAlignDeclarationGroups());
        alignAssignmentsBtn.setSelection(general.isGeneralAlignAssignmentGroups());
        dmlOtherFormatComboBtn.select(general.getGeneralItemListFormat());
        itemListAlignBtn.setSelection(general.isGeneralItemListAlign());
        itemListCommaAfterBtn.setSelection(general.isGeneralItemListCommaAfter());
    }

    /**
     * Perform apply.
     */
    @Override
    protected void performApply() {
        performOk();
        getApplyButton().setEnabled(false);
    }

    /**
     * Perform ok.
     *
     * @return true, if successful
     */
    @Override
    public boolean performOk() {
        if (isPageCreated) {
            performOkOnTabs(preferenceStore);
        }
        return super.performOk();
    }

    private void performOkOnTabs(IPreferenceStore preferenceStore) {
        performGeneralOk(preferenceStore);
        performCtrlStructOk(preferenceStore);
        performDMLOk(preferenceStore);
        performParamDeclarOk(preferenceStore);
        if (isFieldsModified()) {
            MPPDBIDELoggerUtility.operationInfo("Formatter configuration in Preferences setting is changed");
        }
    }

    private void performGeneralOk(IPreferenceStore pfStore) {
        pfStore.setValue(FormatterPreferenceKeys.GEN_INDENT_PREF, Integer.parseInt(indentSpinner.getText()));
        pfStore.setValue(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF, Integer.parseInt(rightMarginSpinner.getText()));
        pfStore.setValue(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF, getTabCharSelection());
        pfStore.setValue(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF, Integer.parseInt(tabCharSizeSpinner.getText()));
        pfStore.setValue(FormatterPreferenceKeys.GEN_ALIGN_DECLARATION_PREF, alignDeclarationBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT_PREF, alignAssignmentsBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT_PREF, dmlOtherFormatComboBtn.getSelectionIndex());
        pfStore.setValue(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN_PREF, itemListAlignBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER_PREF, itemListCommaAfterBtn.getSelection());
    }

    private void performCtrlStructOk(IPreferenceStore pfStore) {
        pfStore.setValue(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE_PREF,
                controlStructThenOnNewLineBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR_PREF,
                controlStructSplitAndOrBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP_PREF,
                controlStructAndOrAfterExpressionBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF,
                controlStructLoopOnNewLineBtn.getSelection());
    }

    private void performDMLOk(IPreferenceStore pfStore) {
        pfStore.setValue(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF, dmlLeftAlignKeywordsBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_LEFT_ALIGN_ITEMS_PREF, dmlLeftAlignItemsBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_SELECT_FORMAT_PREF, dmlSelectFormatComboBtn.getSelectionIndex());
        pfStore.setValue(FormatterPreferenceKeys.DML_SELECT_ALIGN_PREF, dmlSelectAlignBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER_PREF, dmlSelectCommaAfterBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_SPLIT_AND_OR_PREF, dmlWhrSplitAndOrBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP_PREF, dmlWhrAndOrAfterExpBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE_PREF, dmlWhrAndOrUnderWhereBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_INSERT_FORMAT_PREF, dmlInsertFormatComboBtn.getSelectionIndex());
        pfStore.setValue(FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER_PREF, dmlInsertCommaAfterBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_UPDATE_FORMAT_PREF, dmlUpdateFormatComboBtn.getSelectionIndex());
        pfStore.setValue(FormatterPreferenceKeys.DML_UPDATE_ALIGN_PREF, dmlUpdateAlignBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER_PREF, dmlUpdateCommaAfterBtn.getSelection());
    }

    private void performParamDeclarOk(IPreferenceStore pfStore) {
        pfStore.setValue(FormatterPreferenceKeys.PARAM_FORMAT_PREF, parameterComboBtn.getSelectionIndex());
        pfStore.setValue(FormatterPreferenceKeys.PARAM_ALIGN_PREF, parameterAlignBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.PARAM_COMMA_AFTER_PREF, parameterCommaAfterBtn.getSelection());
        pfStore.setValue(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN_PREF, parameterAtLeftMarginBtn.getSelection());
    }

    /**
     * Perform cancel.
     *
     * @return true, if successful
     */
    @Override
    public boolean performCancel() {
        return true;
    }

    /**
     * Perform defaults.
     */
    @Override
    protected void performDefaults() {
        performGeneralDefaults();
        performCtrlStructDefaults();
        performDMLDefaults();
        performParamDeclarDefaults();
        setActiveTabDocAfterFormat();
        getApplyButton().setEnabled(true);
        MPPDBIDELoggerUtility.operationInfo("Formatter in Preferences setting are restored to defaults");
    }

    private void performGeneralDefaults() {
        indentSpinner.setSelection(2);
        rightMarginSpinner.setSelection(76);
        marginPainter.setMarginRulerColumn(76);
        tabCharBtn.select(0);
        tabCharSizeSpinner.setSelection(2);
        alignDeclarationBtn.setSelection(true);
        alignAssignmentsBtn.setSelection(true);
        dmlOtherFormatComboBtn.select(1);
        itemListAlignBtn.setSelection(true);
        itemListCommaAfterBtn.setSelection(true);
    }

    private void performCtrlStructDefaults() {
        controlStructThenOnNewLineBtn.setSelection(false);
        controlStructSplitAndOrBtn.setSelection(false);
        controlStructAndOrAfterExpressionBtn.setSelection(false);
        controlStructAndOrAfterExpressionBtn.setEnabled(false);
        controlStructLoopOnNewLineBtn.setSelection(false);
    }

    private void performDMLDefaults() {
        dmlLeftAlignKeywordsBtn.setSelection(false);
        dmlLeftAlignItemsBtn.setSelection(false);
        dmlSelectFormatComboBtn.select(1);
        dmlSelectAlignBtn.setSelection(true);
        dmlSelectCommaAfterBtn.setSelection(true);
        dmlWhrSplitAndOrBtn.setSelection(true);
        dmlWhrAndOrAfterExpBtn.setSelection(false);
        dmlWhrAndOrAfterExpBtn.setEnabled(true);
        dmlWhrAndOrUnderWhereBtn.setSelection(true);
        dmlWhrAndOrUnderWhereBtn.setEnabled(true);
        dmlInsertFormatComboBtn.select(1);
        dmlInsertCommaAfterBtn.setSelection(true);
        dmlUpdateFormatComboBtn.select(1);
        dmlUpdateAlignBtn.setSelection(true);
        dmlUpdateCommaAfterBtn.setSelection(true);
    }

    private void performParamDeclarDefaults() {
        parameterComboBtn.select(1);
        parameterAlignBtn.setSelection(true);
        parameterCommaAfterBtn.setSelection(true);
        parameterAtLeftMarginBtn.setSelection(false);
    }

    /**
     * Sets the default preferences for formatter.
     *
     * @param preferenceStore the new default preferences for formatter
     */
    public static void setDefaultPreferencesForFormatter(PreferenceStore preferenceStore) {
        setDefaultGeneralPreferences(preferenceStore);
        setDefaultControlStructPreferences(preferenceStore);
        setDefaultDMLPreferences(preferenceStore);
        setDefaultParameterTypePreferences(preferenceStore);
        setDefaultRecordTypePreferences(preferenceStore);
    }

    private static void setDefaultGeneralPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_INDENT_PREF, 2);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF, 76);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF, 2);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_ALIGN_DECLARATION_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_EMPTY_LINES_PRFE, 2);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT_PREF, 1);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER_PREF, true);
    }

    private static void setDefaultControlStructPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF, false);
    }

    private static void setDefaultDMLPreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_ON_ONE_LINE_IFPOSSIBLE_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_SELECT_FORMAT_PREF, 1);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_SELECT_ALIGN_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_SPLIT_AND_OR_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP_PREF, false);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_INSERT_FORMAT_PREF, 1);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_JOIN_SPLIT_BEFORE_ON_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_UPDATE_FORMAT_PREF, 1);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_UPDATE_ALIGN_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER_PREF, true);
    }

    private static void setDefaultParameterTypePreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(FormatterPreferenceKeys.PARAM_FORMAT_PREF, 1);
        preferenceStore.setDefault(FormatterPreferenceKeys.PARAM_ALIGN_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.PARAM_COMMA_AFTER_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN_PREF, false);
    }

    private static void setDefaultRecordTypePreferences(PreferenceStore preferenceStore) {
        preferenceStore.setDefault(FormatterPreferenceKeys.RECORD_TYPE_ALIGN_PREF, true);
        preferenceStore.setDefault(FormatterPreferenceKeys.RECORD_TYPE_COMMA_AFTER_PREF, true);
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
                    MessageConfigLoader.getProperty(IMessagesConstants.FORMATTER_PREFPAGE_UNSAVEDCHANGED_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.PREFERENCE_CHANGE_NOT_APPLIED_MESSAGE));

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
        boolean isChangePresent = false;
        isChangePresent = isGeneralFieldsModified() || isCtrlStructFieldsModified() || isDMLFieldsModified()
                || isParamDeclarFieldsModified();
        return isChangePresent;
    }

    private boolean isGeneralFieldsModified() {
        if ((preferenceStore.getInt(FormatterPreferenceKeys.GEN_INDENT_PREF) != Integer
                .parseInt(indentSpinner.getText()))
                || (preferenceStore.getInt(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF) != Integer
                        .parseInt(rightMarginSpinner.getText()))
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF) != getTabCharSelection())
                || (preferenceStore.getInt(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF) != Integer
                        .parseInt(tabCharSizeSpinner.getText()))
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.GEN_ALIGN_DECLARATION_PREF) != alignDeclarationBtn.getSelection())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT_PREF) != alignAssignmentsBtn
                        .getSelection())
                || (preferenceStore.getInt(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT_PREF) != dmlOtherFormatComboBtn
                        .getSelectionIndex())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN_PREF) != itemListAlignBtn
                        .getSelection())
                || (preferenceStore
                        .getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER_PREF) != itemListCommaAfterBtn
                                .getSelection())) {
            return true;
        }
        return false;
    }

    private boolean isCtrlStructFieldsModified() {
        if ((preferenceStore
                .getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE_PREF) != controlStructThenOnNewLineBtn
                        .getSelection())
                || (preferenceStore
                        .getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR_PREF) != controlStructSplitAndOrBtn
                                .getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP_PREF) != controlStructAndOrAfterExpressionBtn
                                .getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF) != controlStructLoopOnNewLineBtn
                                .getSelection())) {
            return true;
        }
        return false;
    }

    private boolean isDMLFieldsModified() {
        if ((preferenceStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF) != dmlLeftAlignKeywordsBtn
                .getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.DML_LEFT_ALIGN_ITEMS_PREF) != dmlLeftAlignItemsBtn.getSelection())
                || (preferenceStore.getInt(FormatterPreferenceKeys.DML_SELECT_FORMAT_PREF) != dmlSelectFormatComboBtn
                        .getSelectionIndex())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_ALIGN_PREF) != dmlSelectAlignBtn
                        .getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER_PREF) != dmlSelectCommaAfterBtn.getSelection())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SPLIT_AND_OR_PREF) != dmlWhrSplitAndOrBtn
                        .getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP_PREF) != dmlWhrAndOrAfterExpBtn.getSelection())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE_PREF) != dmlWhrAndOrUnderWhereBtn.getSelection())
                || (preferenceStore.getInt(FormatterPreferenceKeys.DML_INSERT_FORMAT_PREF) != dmlInsertFormatComboBtn
                        .getSelectionIndex())
                || (preferenceStore.getBoolean(
                        FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER_PREF) != dmlInsertCommaAfterBtn.getSelection())
                || (preferenceStore.getInt(FormatterPreferenceKeys.DML_UPDATE_FORMAT_PREF) != dmlUpdateFormatComboBtn
                        .getSelectionIndex())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_ALIGN_PREF) != dmlUpdateAlignBtn
                        .getSelection())
                || (preferenceStore
                        .getBoolean(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER_PREF) != dmlUpdateCommaAfterBtn
                                .getSelection())) {
            return true;
        }
        return false;
    }

    private boolean isParamDeclarFieldsModified() {
        if ((preferenceStore.getInt(FormatterPreferenceKeys.PARAM_FORMAT_PREF) != parameterComboBtn.getSelectionIndex())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_ALIGN_PREF) != parameterAlignBtn
                        .getSelection())
                || (preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_COMMA_AFTER_PREF) != parameterCommaAfterBtn
                        .getSelection())
                || (preferenceStore
                        .getBoolean(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN_PREF) != parameterAtLeftMarginBtn
                                .getSelection())) {
            return true;
        }
        return false;
    }

    /**
     * Roll back changes.
     */
    private void rollBackChanges() {
        rollBackGeneralChanges();
        rollBackCtrlStructChanges();
        rollBackDMLChanges();
        rollBackParamDeclarChanges();
        getApplyButton().setEnabled(true);
    }

    private void rollBackGeneralChanges() {
        indentSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_INDENT_PREF));
        rightMarginSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_RIGHT_MARGIN_PREF));
        tabCharBtn.select(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF) ? 1 : 0);
        tabCharSizeSpinner.setSelection(preferenceStore.getInt(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF));
        alignDeclarationBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_DECLARATION_PREF));
        alignAssignmentsBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ALIGN_ASSIGNMENT_PREF));
        dmlOtherFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.GEN_ITEMLIST_FORMAT_PREF));
        itemListAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_ALIGN_PREF));
        itemListCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_ITEMLIST_COMMA_AFTER_PREF));
    }

    private void rollBackCtrlStructChanges() {
        controlStructThenOnNewLineBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_THEN_ON_NEW_LINE_PREF));
        controlStructSplitAndOrBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_SPLIT_AND_OR_PREF));
        controlStructAndOrAfterExpressionBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_AND_OR_AFTER_EXP_PREF));
        controlStructLoopOnNewLineBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.CTRL_STRUCT_LOOP_ONE_NEW_LINE_PREF));
    }

    private void rollBackDMLChanges() {
        dmlLeftAlignKeywordsBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_KEYWORDS_PREF));
        dmlLeftAlignItemsBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_LEFT_ALIGN_ITEMS_PREF));
        dmlSelectFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_SELECT_FORMAT_PREF));
        dmlSelectAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_ALIGN_PREF));
        dmlSelectCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SELECT_COMMA_AFTER_PREF));
        dmlWhrSplitAndOrBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_SPLIT_AND_OR_PREF));
        dmlWhrAndOrAfterExpBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_AFTER_EXP_PREF));
        dmlWhrAndOrUnderWhereBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_AND_OR_UDER_WHERE_PREF));
        dmlInsertFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_INSERT_FORMAT_PREF));
        dmlInsertCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_INSERT_COMMA_AFTER_PREF));
        dmlUpdateFormatComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.DML_UPDATE_FORMAT_PREF));
        dmlUpdateAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_ALIGN_PREF));
        dmlUpdateCommaAfterBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.DML_UPDATE_COMMA_AFTER_PREF));
    }

    private void rollBackParamDeclarChanges() {
        parameterComboBtn.select(preferenceStore.getInt(FormatterPreferenceKeys.PARAM_FORMAT_PREF));
        parameterAlignBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_ALIGN_PREF));
        parameterCommaAfterBtn.setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_COMMA_AFTER_PREF));
        parameterAtLeftMarginBtn
                .setSelection(preferenceStore.getBoolean(FormatterPreferenceKeys.PARAM_AT_LEFT_MARGIN_PREF));
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
        getApplyButton().setEnabled(true);
    }

    /**
     * Enable apply btn.
     */
    protected void enableApplyBtn() {
        if (!getApplyButton().isEnabled()) {
            getApplyButton().setEnabled(true);
        }
    }

    /**
     * Gets the indent size.
     *
     * @return the indent size
     */
    public static int getIndentSize() {
        return preferenceStore.getInt(FormatterPreferenceKeys.GEN_CHAR_SIZE_PREF);
    }

    /**
     * Checks if is tab to space enabled.
     *
     * @return true, if is tab to space enabled
     */
    public static boolean isTabToSpaceEnabled() {
        return !preferenceStore.getBoolean(FormatterPreferenceKeys.GEN_TAB_CHAR_PREF);
    }

    /**
     * Gets the string with spaces.
     *
     * @return the string with spaces
     */
    public static String getStringWithSpaces() {
        StringBuilder spaceBuilder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        for (int i = 0; i < getIndentSize(); i++) {
            spaceBuilder.append(" ");
        }
        return spaceBuilder.toString();
    }

    /**
     * Sets the preferences.
     *
     * @param ps the new preferences
     */
    public static void setPreferenceStore(PreferenceStore ps) {
        preferenceStore = ps;
    }

    /**
     * Gets the formatter selection listener.
     *
     * @return the formatter selection listener
     */
    private SelectionListener getFormatterSelectionListener() {
        return new SelectionAdapter() {

            /**
             * Widget selected.
             *
             * @param e the e
             */
            @Override
            public void widgetSelected(final SelectionEvent e) {
                setActiveTabDocAfterFormat();
                getApplyButton().setEnabled(true);
            }
        };
    }

    private SelectionListener getMarginPaintSelectionListener() {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                marginPainter.setMarginRulerColumn(rightMarginSpinner.getSelection());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        };
    }

    private void setSpinnerMaxValueListner(Spinner spinner, int maxValue) {
        spinner.setMaximum(maxValue);
        spinner.addModifyListener(new ModifyListener() {
            /**
             * Modify text.
             *
             * @param event the event
             */
            public void modifyText(ModifyEvent event) {
                try {
                    String spinnerText = spinner.getText();
                    int spinnerValue = Integer.parseInt(spinnerText);
                    if (spinnerText.equals("00")) {
                        spinner.setSelection(0);
                    }
                    if (spinnerValue > maxValue) {
                        spinner.setSelection(maxValue);
                        getApplyButton().setEnabled(true);
                    }
                } catch (NumberFormatException excep) {
                    return;
                }
            }
        });
    }
}
