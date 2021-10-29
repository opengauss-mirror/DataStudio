/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.trigger;

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

import java.util.ArrayList;
import java.util.List;
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

import com.huawei.mppdbide.view.ui.trigger.CreateTriggerUiData.ErrType;

/**
 * Title: class
 * Description: the class CreateTriggerMainDialog
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
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

    /**
     * Get the trigger ui data
     *
     * @return CreateTriggerUiData the trigger ui data
     */
    public CreateTriggerUiData getCreateTriggerUiData() {
        CreateTriggerUiData createTriggerUiData = new CreateTriggerUiData();
        createTriggerUiData.setTriggerName(triggerNameText.getText());
        createTriggerUiData.setTableName(triggerTableCombo.getText());
        if (firesBefore.getSelection()) {
            createTriggerUiData.setPeriod(TriggerKeyword.BEFORE.keyword);
        }
        if (firesAfter.getSelection()) {
            createTriggerUiData.setPeriod(TriggerKeyword.AFTER.keyword);
        }
        if (firesInsteadOf.getSelection()) {
            createTriggerUiData.setPeriod(TriggerKeyword.INSTEAD_OF.keyword);
        }
        createTriggerUiData.setOperate(getOperate());
        if (btnUpdate.getSelection()) {
            createTriggerUiData.setColumn(getColumns());
        }
        if (rowLevel.getSelection()) {
            createTriggerUiData.setLevel(TriggerKeyword.ROW.keyword);
        }
        if (statementLevel.getSelection()) {
            createTriggerUiData.setLevel(TriggerKeyword.STATEMENT.keyword);
        }
        createTriggerUiData.setCondition(whenText.getText());
        createTriggerUiData.setFunctionName(functionNameCombo.getText());
        createTriggerUiData.setSchemaName(relyInfo.getNamespace().getName());
        return createTriggerUiData;
    }

    private List<String> getOperate() {
        List<String> operate = new ArrayList<>();
        if (btnInsert.getSelection()) {
            operate.add(TriggerKeyword.INSERT.keyword);
        }
        if (btnDelete.getSelection()) {
            operate.add(TriggerKeyword.DELETE.keyword);
        }
        if (btnTruncate.getSelection()) {
            operate.add(TriggerKeyword.TRUNCATE.keyword);
        }
        if (btnUpdate.getSelection()) {
            operate.add(TriggerKeyword.UPDATE.keyword);
        }
        return operate;
    }

    private List<String> getColumns() {
        List<String> list = new ArrayList<String>();
        Object[] checkedInput = tableViewer.getCheckedElements();
        for (Object check : checkedInput) {
            if (check instanceof CreateTriggerParam) {
                String columnName = ((CreateTriggerParam) check).getValue(1);
                list.add(columnName);
            }
        }
        return list;
    }

    /**
     * Create the dialog.
     *
     * @param Shell the parent
     * @param int the style
     */
    public CreateTriggerMainDialog(Shell parent, int style) {
        super(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        setText("Create Function");
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
        initUiData();
        addWidgetListeners();
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

    private void initUiData() {
        for (String name : relyInfo.getTableNames()) {
            triggerTableCombo.add(name);
        }
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
        shell.setText("\u65B0\u5EFA\u89E6\u53D1\u5668");
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
        tableItemPreview.setText("SQL\u9884\u89C8");

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
        btnBack.setText("\u4E0A\u4E00\u6B65");

        Button btnCancelPreview = new Button(sashForm, SWT.NONE);
        btnCancelPreview.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });
        btnCancelPreview.setText("\u53D6\u6D88");

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
        btnOk.setText("\u5B8C\u6210");

        sashForm.setWeights(new int[] {4, 1, 1, 1});
    }

    private void createGeneralPage() {
        tabItemTrigger = new CTabItem(tabFolder, SWT.NONE);
        tabItemTrigger.setText("\u5E38\u89C4");

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
        btnCancel.setText("\u53D6\u6D88");
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
        btnNext.setText("\u4E0B\u4E00\u6B65");
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
        functionNameLabel.setText("\u89E6\u53D1\u5668\u51FD\u6570");

        functionNameCombo = new Combo(sashForm, SWT.READ_ONLY);

        sashForm.setWeights(new int[] {100, 553});
    }

    private void triggerConditions(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label whenLabel = new Label(sashForm, SWT.NONE);
        whenLabel.setText("\u89E6\u53D1\u6761\u4EF6(W)");

        whenText = new Text(sashForm, SWT.BORDER);

        sashForm.setWeights(new int[] {100, 550});
    }

    private void triggerLevel(SashForm conditionSashForm) {
        SashForm sashForm = new SashForm(conditionSashForm, SWT.NONE);

        Label levelLabel = new Label(sashForm, SWT.NONE);
        levelLabel.setText("\u7EA7\u522B:");

        rowLevel = new Button(sashForm, SWT.RADIO);
        rowLevel.setText("\u5143\u7EC4\u7EA7\u89E6\u53D1\u5668(R)");

        statementLevel = new Button(sashForm, SWT.RADIO);
        statementLevel.setSelection(true);
        statementLevel.setText("\u8BED\u53E5\u7EA7\u89E6\u53D1\u5668(M)");

        sashForm.setWeights(new int[] {100, 128, 419});
    }

    private void operationType(SashForm mainForm) {
        SashForm operateSashForm = new SashForm(mainForm, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);

        SashForm sashForm = new SashForm(operateSashForm, SWT.VERTICAL);

        operationTypes(sashForm);

        paramTable(sashForm);

        sashForm.setWeights(new int[] {16, 24, 24, 22, 23, 278});
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
        operateLabel.setAlignment(SWT.RIGHT);
        operateLabel.setText("\u64CD\u4F5C\u7C7B\u578B");
        Label lineLabel = new Label(paramSearchSashForm, SWT.SEPARATOR | SWT.HORIZONTAL);
        paramSearchSashForm.setWeights(new int[] {56, 565});

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
        firesLabel.setText("\u89E6\u53D1\u65F6\u95F4:");

        firesBefore = new Button(sashForm, SWT.RADIO);
        firesBefore.setSelection(true);
        firesBefore.setText("BEFORE");

        firesAfter = new Button(sashForm, SWT.RADIO);
        firesAfter.setText("AFTER");

        firesInsteadOf = new Button(sashForm, SWT.RADIO);
        firesInsteadOf.setText("INSTEAD OF");

        sashForm.setWeights(new int[] {94, 103, 100, 304});
        firesSashForm.setWeights(new int[] {2});
    }

    private void triggerAndTableName(SashForm mainForm) {
        SashForm nameSshForm = new SashForm(mainForm, SWT.SMOOTH | SWT.VERTICAL);

        SashForm nameSshFormAll = new SashForm(nameSshForm, SWT.VERTICAL);

        SashForm schemaSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        schemaSshForm.setSashWidth(0);

        Label triggerNameLabel = new Label(schemaSshForm, SWT.NONE);
        triggerNameLabel.setText("\u89E6\u53D1\u5668\u540D(N):");

        triggerNameText = new Text(schemaSshForm, SWT.BORDER);
        schemaSshForm.setWeights(new int[] {100, 546});

        SashForm functionNameSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        functionNameSshForm.setSashWidth(0);

        Label triggerTableLabel = new Label(functionNameSshForm, SWT.NONE);
        triggerTableLabel.setText("\u89E6\u53D1\u8868\u540D:");

        triggerTableCombo = new Combo(functionNameSshForm, SWT.READ_ONLY);

        functionNameSshForm.setWeights(new int[] {100, 546});
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
