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

package org.opengauss.mppdbide.view.ui.trigger;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;

import java.awt.ItemSelectable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.TableColumn;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.ui.trigger.CreateTriggerUiData.ErrType;
import org.opengauss.mppdbide.view.utils.UIVerifier;

/**
 * Title: class
 * Description: the class CreateTriggerMainDialog
 *
 * @since 3.0.0
 */
public class CreateTriggerMainDialog extends Dialog {
    /**
     * Dialog open result
     */
    protected int result = -1;
    /**
     * Dialog shell
     */
    protected Shell shell;
    private Text textPreview;
    private CreateTriggerRelyInfo relyInfo;
    private CTabFolder tabFolder;
    private CTabItem tabItemTrigger;
    private CTabItem tableItemPreview;
    private Text triggerNameText;
    private Combo triggerTableCombo;
    private Button firesBefore;
    private Button firesAfter;
    private Button firesInsteadOf;
    private Button btnInsert;
    private Button btnDelete;
    private Button btnTruncate;
    private Button btnUpdate;
    private CheckboxTableViewer tableViewer;
    private Button rowLevel;
    private Button statementLevel;
    private Text whenText;
    private Combo functionNameCombo;
    private Table table;
    private Label lblInfoShow;
    private boolean isModifyTrigger = false;
    private CreateTriggerDataModel dataModel;
    private Button btnCheckTriggerNameCase;

    /**
     * Sets trigger data model
     *
     * @param String the trigger name
     * @param CreateTriggerDataModel the create trigger data model
     */
    public void setTriggerDataModel(
            String triggerName,
            CreateTriggerDataModel dataModel) {
        this.isModifyTrigger = true;
        this.dataModel = dataModel;
        if (this.dataModel == null) {
            this.dataModel = new CreateTriggerDataModel();
        }
        this.dataModel.setTriggerName(triggerName);
    }

    private boolean isModify() {
        return isModifyTrigger;
    }

    private void refreshUiByDataModel() {
        if (!isModify()) {
            return;
        }
        triggerNameText.setText(dataModel.getTriggerName());
        triggerNameText.setEnabled(false);
        btnCheckTriggerNameCase.setSelection(dataModel.getTriggerNameCase());
        btnCheckTriggerNameCase.setEnabled(false);

        if (Objects.isNull(dataModel.getTriggerTableName())
                || "".equals(dataModel.getTriggerTableName())) {
            return;
        }
        int valueIndex = triggerTableCombo.indexOf(dataModel.getTriggerTableName());
        triggerTableCombo.select(valueIndex);
        triggerTableCombo.notifyListeners(SWT.Selection, null);

        refreshStageAndSelection();
        refreshParamAndOther();
    }

    @SuppressWarnings("unchecked")
    private void refreshParamAndOther() {
        Object data = tableViewer.getInput();
        if (data != null && data instanceof List<?>) {
            List<CreateTriggerParam> listOfInputs = (List<CreateTriggerParam>) data;
            Set<String> alreadySelectedColumns = new HashSet<>(dataModel.getUpdateColumn());
            List<CreateTriggerParam> selectParams = new ArrayList<>();
            for (int i = 0, n = listOfInputs.size(); i < n; i++) {
                String columnName = listOfInputs.get(i).getValue(1);
                if (alreadySelectedColumns.contains(columnName)) {
                    selectParams.add(listOfInputs.get(i));
                }
            }
            tableViewer.setCheckedElements(selectParams.toArray());
        }

        statementLevel.setSelection(false);
        rowLevel.setSelection(false);
        Button statementBtn = dataModel.isStatementLevel() ? statementLevel : rowLevel;
        statementBtn.setSelection(true);
        statementBtn.notifyListeners(SWT.Selection, null);

        whenText.setText(dataModel.getWhenCodition());
        functionNameCombo.select(functionNameCombo.indexOf(dataModel.getTriggerFunc()));
        functionNameCombo.notifyListeners(SWT.Selection, null);
    }

    private void refreshStageAndSelection() {
        int firesNum = 0;
        for (Button btn: new Button[] {firesBefore, firesAfter, firesInsteadOf}) {
            if (firesNum == dataModel.getTriggerStage()) {
                btn.setSelection(true);
                btn.notifyListeners(SWT.Selection, null);
            } else {
                btn.setSelection(false);
            }
            firesNum += 1;
        }

        int bitPos = 0;
        for (Button btn: new Button[] {btnInsert, btnDelete, btnTruncate, btnUpdate}) {
            if ((dataModel.getSelectOptration() & (1 << bitPos)) != 0) {
                btn.setSelection(true);
                btn.notifyListeners(SWT.Selection, null);
            } else {
                btn.setSelection(false);
            }
            bitPos += 1;
        }
    }

    /**
     * Gets save data model
     *
     * @return CreateTriggerDataModel the trigger data model
     */
    public CreateTriggerDataModel getSaveDataModel() {
        return this.dataModel;
    }

    private CreateTriggerDataModel getCurrentDataModel() {
        CreateTriggerDataModel tmpModel = new CreateTriggerDataModel();
        tmpModel.setTriggerName(triggerNameText.getText());
        tmpModel.setTriggerNamespaceName(relyInfo.getNamespaceName());
        tmpModel.setTriggerTableName(triggerTableCombo.getText());
        tmpModel.setTriggerNameCase(btnCheckTriggerNameCase.getSelection());

        int firesNum = 0;
        for (Button btn:  new Button [] {firesBefore, firesAfter, firesInsteadOf} ) {
            if (btn.getSelection()) {
                tmpModel.setTriggerStage(firesNum);
                break;
            }
            firesNum += 1;
        }

        int selectOperation = 0;
        int bitPos = 0;
        for (Button btn: new Button[] {btnInsert, btnDelete, btnTruncate, btnUpdate}) {
            if (btn.getSelection()) {
                selectOperation |= (1 << bitPos);
            }
            bitPos += 1;
        }
        tmpModel.setSelectOptration(selectOperation);

        for (Object obj: tableViewer.getCheckedElements()) {
            if (obj instanceof CreateTriggerParam) {
                String columnName = ((CreateTriggerParam) obj).getValue(1);
                tmpModel.getUpdateColumn().add(columnName);
            }
        }

        tmpModel.setStatementLevel(statementLevel.getSelection());

        tmpModel.setWhenCodition(whenText.getText());
        tmpModel.setTriggerFunc(functionNameCombo.getText());
        return tmpModel;
    }

    /**
     * Gets trigger ui data
     *
     * @return CreateTriggerUiData the trigger ui data
     */
    public CreateTriggerUiData getCreateTriggerUiData() {
        dataModel = getCurrentDataModel();
        return new CreateTriggerUiData(dataModel);
    }

    /**
     * Create the dialog.
     *
     * @param Shell the parent
     * @param int the style
     */
    public CreateTriggerMainDialog(Shell parent, int style) {
        super(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        setText("Create Trigger");
    }

    /**
     * Open the dialog.
     *
     * @return int the result
     */
    public int open() {
        createContents();
        shell.open();
        shell.layout();
        initUiData(false);
        addWidgetListeners();
        refreshUiByDataModel();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    private void addWidgetListeners() {
        triggerTableCombo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                btnUpdate.setSelection(false);
                tableViewer.setInput(null);
                tableViewer.refresh();
            }
        });
        firesBefore.addSelectionListener(new PeriodSelectionAdapter(true, null));
        firesAfter.addSelectionListener(new PeriodSelectionAdapter(true, null));
        firesInsteadOf.addSelectionListener(new PeriodSelectionAdapter(false,
                "INSTEAD OF\u4e0d\u652f\u6301\u89e6\u53d1\u6761\u4ef6"));
        btnTruncate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.widget;
                if (btn.getSelection()) {
                    rowLevel.setSelection(false);
                    statementLevel.setSelection(true);
                    rowLevel.setEnabled(false);
                    statementLevel.setEnabled(false);
                } else {
                    rowLevel.setEnabled(true);
                    statementLevel.setEnabled(true);
                }
            }
        });
        btnUpdate.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                Button btn = (Button) event.widget;
                if (btn.getSelection() && !firesInsteadOf.getSelection()) {
                    setTableData();
                } else {
                    tableViewer.setInput(null);
                    tableViewer.refresh();
                }
            }
        });
    }

    private void initUiData(boolean isIgnoreTables) {
    	ArrayList<String> items = new ArrayList<>();
    	if (!isIgnoreTables) {
    		items.addAll(relyInfo.getTableNames());
		}

    	items.addAll(relyInfo.getViewNames());
        triggerTableCombo.setItems(items.toArray(new String[0]));
        
        for (String name : relyInfo.getFunctionNames()) {
            functionNameCombo.add(name);
        }
    }

    /**
     *
     * description: set rely info, must called before open dialog
     *
     * @param CreateTriggerRelyInfo the info
     */
    public void setRelyInfo(CreateTriggerRelyInfo info) {
        this.relyInfo = info;
    }

    /**
     * Set the title
     *
     * @param String the title to set
     */
    public void setTitle(String string) {
        setText(string);
        shell.setText(string);
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        createMainShell();
        createGeneralPage();
        createPreviewPage();
    }

    private void createMainShell() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(664, 724);
        shell.setText(isModify() ? MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_EDIT_TRIGGER)
                : MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_CREATE_TRIGGER));
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        tabFolder = new CTabFolder(shell, SWT.BORDER);
        tabFolder.setBorderVisible(false);
        tabFolder.marginHeight = 20;
        tabFolder.marginWidth = 20;
        tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
    }

    private void createPreviewPage() {
        tableItemPreview = new CTabItem(tabFolder, SWT.NONE);
        tableItemPreview.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_SQL_PREVIEW));

        SashForm sashFormPreview = new SashForm(tabFolder, SWT.VERTICAL);
        tableItemPreview.setControl(sashFormPreview);

        textPreview = new Text(sashFormPreview, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);

        threeButtonUi(sashFormPreview);

        sashFormPreview.setWeights(new int[] {576, 37});
    }

    private void threeButtonUi(SashForm sashFormTemp) {
        SashForm sashForm = new SashForm(sashFormTemp, SWT.NONE);

        Label labelPreview = new Label(sashForm, SWT.NONE);
        labelPreview.setText("");

        Button btnBack = new Button(sashForm, SWT.NONE);
        btnBack.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(tabItemTrigger);
            }
        });
        btnBack.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_PREVIOUS));

        Button btnCancelPreview = new Button(sashForm, SWT.NONE);
        btnCancelPreview.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });
        btnCancelPreview.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_CANCEL));

        Button btnOk = new Button(sashForm, SWT.NONE);
        btnOk.setToolTipText("enter datastudio ternimal and auto compile!");
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = 0;
                String sourceCode = textPreview.getText().trim();
                relyInfo.execute(sourceCode);
                getParent().dispose();
            }
        });
        btnOk.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_FINISH));

        sashForm.setWeights(new int[] {4, 1, 1, 1});
    }

    private void createGeneralPage() {
        tabItemTrigger = new CTabItem(tabFolder, SWT.NONE);
        tabItemTrigger.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_GENERAL_PAGE));

        SashForm mainForm = new SashForm(tabFolder, SWT.VERTICAL);
        tabItemTrigger.setControl(mainForm);

        triggerAndTableName(mainForm);
        triggerTime(mainForm);
        operationType(mainForm);
        triggerCondition(mainForm);
        twoButton(mainForm);

        mainForm.setWeights(new int[] {55, 19, 378, 118, 37});
    }

    private void twoButton(SashForm mainForm) {
        SashForm footSashForm = new SashForm(mainForm, SWT.NONE);

        lblInfoShow = new Label(footSashForm, SWT.NONE);
        lblInfoShow.setForeground(
                new Color(getParent().getDisplay(),
                    new RGB(255, 0, 0))
                );

        nextButton(footSashForm);

        cancelButton(footSashForm);

        footSashForm.setWeights(new int[] {5, 1, 1});
    }

    private void cancelButton(SashForm footSashForm) {
        Button btnCancel = new Button(footSashForm, SWT.NONE);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });
        btnCancel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_CANCEL));
    }

    private void nextButton(SashForm footSashForm) {
        Button btnNext = new Button(footSashForm, SWT.NONE);
        btnNext.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CreateTriggerUiData uiData = getCreateTriggerUiData();
                ErrType errType = uiData.valid();
                if (errType == ErrType.ERR_SUCCESS) {
                    tabFolder.setSelection(tableItemPreview);
                    textPreview.setText(uiData.getTriggerDefine());
                    lblInfoShow.setText("");
                } else {
                    lblInfoShow.setText(errType.errMsg);
                }
            }
        });
        btnNext.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_NEXT));
    }

    private void triggerCondition(SashForm mainForm) {
        SashForm conditionSashForm = new SashForm(mainForm, SWT.VERTICAL);

        triggerLevel(conditionSashForm);
        triggerConditions(conditionSashForm);
        triggerFunction(conditionSashForm);
        blankLabel(conditionSashForm);

        conditionSashForm.setWeights(new int[] {23, 23, 29, 31});
    }

    private void blankLabel(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label blank = new Label(sashForm, SWT.NONE);
        blank.setForeground(new Color(Display.getCurrent(), new RGB(255, 0, 0)));
        sashForm.setWeights(new int[] {505});
    }

    private void triggerFunction(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label functionNameLabel = new Label(sashForm, SWT.NONE);
        functionNameLabel.setText(MessageConfigLoader.getProperty(
                IMessagesConstants.CREATE_TRIGGER_UI_TRIGGER_FUNCTION));

        functionNameCombo = new Combo(sashForm, SWT.READ_ONLY);

        sashForm.setWeights(new int[] {150, 553});
    }

    private void triggerConditions(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label whenLabel = new Label(sashForm, SWT.NONE);
        whenLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_TRIGGER_CONDITION));

        whenText = new Text(sashForm, SWT.BORDER);

        sashForm.setWeights(new int[] {150, 550});
    }

    private void triggerLevel(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label levelLabel = new Label(sashForm, SWT.NONE);
        levelLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_TRIGGER_LEVEL));

        rowLevel = new Button(sashForm, SWT.RADIO);
        rowLevel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_TUPLE_LEVEL_TRIGGER));

        statementLevel = new Button(sashForm, SWT.RADIO);
        statementLevel.setSelection(true);
        statementLevel.setText(MessageConfigLoader.getProperty(
                IMessagesConstants.CREATE_TRIGGER_UI_STATEMENT_LEVEL_TRIGGER));

        sashForm.setWeights(new int[] {162, 200, 400});
    }

    private void operationType(SashForm mainForm) {
        SashForm operateSashForm = new SashForm(mainForm, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);

        SashForm sashForm = new SashForm(operateSashForm, SWT.VERTICAL);

        operationTypes(sashForm);

        paramTable(sashForm);

        sashForm.setWeights(new int[] {20, 24, 24, 22, 23, 278});
        operateSashForm.setWeights(new int[] {3});
    }

    private void paramTable(SashForm sashForm) {
        SashForm paramTableSashFrom = new SashForm(sashForm, SWT.NONE);

        Composite composite = new Composite(paramTableSashFrom, SWT.NONE);
        composite.setEnabled(true);
        FillLayout fillLayout = new FillLayout(SWT.HORIZONTAL);
        composite.setLayout(fillLayout);
        tableViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.FULL_SELECTION);
        tableViewer.setAllChecked(false);

        table = tableViewer.getTable();
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        initTableViewer(tableViewer, table);

        TableColumn tableColumn = new TableColumn(table, SWT.NONE);
        tableColumn.setWidth(100);

        paramTableSashFrom.setWeights(new int[] {1});
    }

    private void operationTypes(SashForm sashForm) {
        SashForm paramSearchSashForm = new SashForm(sashForm, SWT.NONE);

        Label operateLabel = new Label(paramSearchSashForm, SWT.NONE);
        operateLabel.setAlignment(SWT.LEFT);
        operateLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_OPERATION_TYPE));
        paramSearchSashForm.setWeights(new int[] {100});

        btnInsert = new Button(sashForm, SWT.CHECK);
        btnInsert.setText("INSERT");
        btnInsert.setSelection(true);

        btnDelete = new Button(sashForm, SWT.CHECK);
        btnDelete.setText("DELETE");

        btnTruncate = new Button(sashForm, SWT.CHECK);
        btnTruncate.setText("TRUNCATE");

        btnUpdate = new Button(sashForm, SWT.CHECK);
        btnUpdate.setText("UPDATE");
        btnUpdate.setSelection(false);
    }

    private void triggerTime(SashForm mainForm) {
        SashForm firesSashForm = new SashForm(mainForm, SWT.NONE);

        SashForm sashForm = new SashForm(firesSashForm, SWT.NONE);

        Label firesLabel = new Label(sashForm, SWT.NONE);
        firesLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_TIME_TO_TRIGGER));

        firesBefore = new Button(sashForm, SWT.RADIO);
        firesBefore.setSelection(true);
        firesBefore.setText("BEFORE");

        firesAfter = new Button(sashForm, SWT.RADIO);
        firesAfter.setText("AFTER");

        firesInsteadOf = new Button(sashForm, SWT.RADIO);
        firesInsteadOf.setText("INSTEAD OF");
        firesInsteadOf.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				boolean isSelected = ((Button)e.getSource()).getSelection();
				if (isSelected) {
					initUiData(true);
				}else {
					initUiData(false);
				}
			}
		});

        sashForm.setWeights(new int[] {140, 103, 100, 304});
        firesSashForm.setWeights(new int[] {2});
    }

    private void triggerAndTableName(SashForm mainForm) {
        SashForm nameSshForm = new SashForm(mainForm, SWT.SMOOTH | SWT.VERTICAL);

        SashForm nameSshFormAll = new SashForm(nameSshForm, SWT.VERTICAL);

        SashForm schemaSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        schemaSshForm.setSashWidth(0);

        Label triggerNameLabel = new Label(schemaSshForm, SWT.NONE);
        triggerNameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TRIGGER_UI_TRIGGER_NAME));

        triggerNameText = new Text(schemaSshForm, SWT.BORDER);
        UIVerifier.verifyTextSize(triggerNameText, 63);

        new Label(schemaSshForm, SWT.NONE);

        btnCheckTriggerNameCase = new Button(schemaSshForm, SWT.CHECK | SWT.CENTER);
        btnCheckTriggerNameCase.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_TABLE_CASE));
        schemaSshForm.setWeights(new int[] {150, 445, 5, 95});

        SashForm functionNameSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        functionNameSshForm.setSashWidth(0);

        Label triggerTableLabel = new Label(functionNameSshForm, SWT.NONE);
        triggerTableLabel.setText(MessageConfigLoader.getProperty(
                IMessagesConstants.CREATE_TRIGGER_UI_TRIGGER_TABLE_NAME));

        triggerTableCombo = new Combo(functionNameSshForm, SWT.READ_ONLY);

        functionNameSshForm.setWeights(new int[] {150, 546});
        nameSshFormAll.setWeights(new int[] {27, 27});
        nameSshForm.setWeights(new int[] {1});
    }

    private void initTableViewer(CheckboxTableViewer tableViewer, Table table) {
        tableViewer.setLabelProvider(new CreateTriggerDataLableProvider());
        tableViewer.setContentProvider(new ContentProvider());
        CreateTriggerParamsTitle paramsTitles = new CreateTriggerParamsTitle();
        List<String> titles = paramsTitles.getTitles();
        IntStream.iterate(0, seed -> seed + 1).limit(titles.size()).forEach(idx -> {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
            column.getColumn().setWidth(paramsTitles.getWidth(idx));
            column.getColumn().setText(titles.get(idx));
            column.setLabelProvider(new CreateTriggerDataLableProvider(idx));
        });
        table.addControlListener(new CreateTriggerControlAdapter(table, paramsTitles));
    }

    private void setTableData() {
        if (triggerTableCombo.getText() == null || "".equals(triggerTableCombo.getText().trim())) {
            lblInfoShow.setText(ErrType.ERR_TABLENAME.errMsg);
            return;
        }
        List<CreateTriggerParam> input = relyInfo.getTableColumns(triggerTableCombo.getText());
        tableViewer.setInput(input);
        tableViewer.refresh();
        lblInfoShow.setText("");
    }

    private class ContentProvider implements IStructuredContentProvider {
        @Override
        public Object[] getElements(Object inputElement) {
            if (inputElement instanceof List) {
                return ((List<?>) inputElement).toArray();
            }
            return new Object[0];
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }
    }

    private class PeriodSelectionAdapter extends SelectionAdapter {
        private String toolTipText;
        private boolean editable;

        public PeriodSelectionAdapter(boolean editable, String toolTipText) {
            this.editable = editable;
            this.toolTipText = toolTipText;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button btn = (Button) event.widget;
            if (btn.getSelection()) {
                whenText.setEditable(editable);
                btnTruncate.setEnabled(editable);
                rowLevel.setEnabled(editable);
                statementLevel.setEnabled(editable);
                whenText.setToolTipText(toolTipText);
                if (!editable) {
                    // INSTEAD OF
                    tableViewer.setInput(null);
                    btnTruncate.setSelection(false);
                    rowLevel.setSelection(true);
                    statementLevel.setSelection(false);
                } else {
                    if (btnUpdate.getSelection()) {
                        setTableData();
                    }
                    statementLevel.setSelection(true);
                    rowLevel.setSelection(false);
                }
            }
        }
    }
}
