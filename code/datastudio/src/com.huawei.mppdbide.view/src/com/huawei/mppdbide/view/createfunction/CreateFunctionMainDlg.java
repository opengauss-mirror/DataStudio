/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.createfunction;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.createfunction.CreateFunctionUiData.ErrType;

import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Group;

/**
 * Title: CreateFunctionMainDlg for use
 * Description:
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2021-04-25]
 * @since 2021-04-25
 */
public class CreateFunctionMainDlg extends Dialog {
    private static final String MOVE_UP = "\u2191";
    private static final String MOVE_DOWN = "\u2193";
    /**
     * Dialog open result
     */
    protected int result = -1;

    /**
     * Dialog shell
     */
    protected Shell shell;

    private String initLanguage;
    private Table tableParam;
    private Text textPreview;
    private CreateFunctionRelyInfo relyInfo;
    private Label lblSchemaText;
    private TableViewer tableViewer;
    private CTabFolder tabFolder;
    private CTabItem tabItemFunction;
    private CTabItem tableItemPreview;
    private Text text;
    private Combo comboRetType;
    private Text textFunctionBodyTemp;
    private SashForm sashFormOfFunctionSelect;
    private Combo comboLanguage;
    private Button btnFunction;
    private Button btnProcedure;
    private Button btnTrigger;
    private FuncTypeEnum curFuncType;
    private SashForm paramSashForm;
    private SashForm sashForm;

    /**
     * Create the dialog.
     *
     * @param parent the parent
     * @param style the style
     */
    public CreateFunctionMainDlg(Shell parent, int style) {
        super(parent, SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_CREATE_FUNCTION));
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
        initLanguageUi();
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /**
     *
     * description: set rely info, must called before open dialog
     *
     * @param CreateFunctionRelyInfo the info
     * @return void no need return value
     */
    public void setRelyInfo(CreateFunctionRelyInfo info) {
        this.relyInfo = info;
    }

    /**
     * Set the title
     *
     * @param String the title string
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
        createFunctionPage();
        previewPage();
    }

    private void previewPage() {
        tableItemPreview = new CTabItem(tabFolder, SWT.NONE);
        tableItemPreview.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_SQL_PREVIEW));

        SashForm sashForm4 = new SashForm(tabFolder, SWT.VERTICAL);
        tableItemPreview.setControl(sashForm4);

        Label lblNewLabel1 = new Label(sashForm4, SWT.NONE);
        lblNewLabel1.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_SQL_PREVIEW));

        textPreview = new Text(sashForm4, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);

        fourButtonUi(sashForm4);

        sashForm4.setWeights(new int[] {1, 13, 1});
    }

    private void fourButtonUi(SashForm sashForm4) {
        SashForm sashForm5 = new SashForm(sashForm4, SWT.NONE);

        hideLabel(sashForm5);

        backButton(sashForm5);

        cancelButton(sashForm5);

        okEditButton(sashForm5);

        okButton(sashForm5);

        sashForm5.setWeights(new int[] {4, 1, 1, 1, 1});
    }

    private void okButton(SashForm sashForm5) {
        Button btnOk = new Button(sashForm5, SWT.NONE);
        btnOk.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_TOOLTIP_COMPILE));
        btnOk.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = 0;
                String sourceCode = textPreview.getText().trim();
                relyInfo.execute(sourceCode, true);
                getParent().dispose();
            }
        });
        btnOk.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_COMPILE));
    }

    private void okEditButton(SashForm sashForm5) {
        Button btnOkEdit = new Button(sashForm5, SWT.NONE);
        btnOkEdit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = 0;
                String sourceCode = textPreview.getText().trim();
                relyInfo.execute(sourceCode, false);
                getParent().dispose();
            }
        });
        btnOkEdit.setToolTipText(MessageConfigLoader.getProperty(
                IMessagesConstants.CREATE_FUNCTION_UI_TOOLTIP_NO_COMPILE));
        btnOkEdit.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_NO_COMPILE));
    }

    private void cancelButton(SashForm sashForm5) {
        Button btnCancelPreview = new Button(sashForm5, SWT.NONE);
        btnCancelPreview.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });
        btnCancelPreview.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_CANCEL));
    }

    private void hideLabel(SashForm sashForm5) {
        Label labelPreview = new Label(sashForm5, SWT.NONE);
        labelPreview.setText("");
    }

    private void backButton(SashForm sashForm5) {
        Button btnBack = new Button(sashForm5, SWT.NONE);
        btnBack.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(tabItemFunction);
            }
        });
        btnBack.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_BACK));
    }

    private void createMainShell() {
        shell = new Shell(getParent(), getStyle());
        shell.setSize(650, 600);
        shell.setText(getText());
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));
        tabFolder = new CTabFolder(shell, SWT.BORDER);
        tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(
                SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
    }

    private void createFunctionPage() {
        tabItemFunction = new CTabItem(tabFolder, SWT.NONE);
        tabItemFunction.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_CREATE_FUNCTION));

        SashForm mainForm = new SashForm(tabFolder, SWT.VERTICAL);
        tabItemFunction.setControl(mainForm);

        schemaAndFunctionNameUi(mainForm);
        functionOrProcedureUi(mainForm);
        paramListUi(mainForm);
        functionBody(mainForm);
        buttonUi(mainForm);

        mainForm.setWeights(new int[] {4, 4, 15, 15, 2});
    }

    private void buttonUi(SashForm mainForm) {
        SashForm sashForm2 = new SashForm(mainForm, SWT.NONE);

        Label lblInfoShow = new Label(sashForm2, SWT.NONE);
        lblInfoShow.setForeground(
                new Color(getParent().getDisplay(),
                    new RGB(255, 0, 0))
                );

        Button btnNext = new Button(sashForm2, SWT.NONE);
        btnNext.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                CreateFunctionUiData uiData = getFunctionUiData();
                ErrType errType = uiData.valid();
                if (errType == ErrType.ERR_SUCCESS) {
                    tabFolder.setSelection(tableItemPreview);
                    textPreview.setText(uiData.getFunctionDefine());
                    lblInfoShow.setText("");
                } else {
                    lblInfoShow.setText(errType.getProperty(errType));
                }
            }
        });
        btnNext.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_NEXT));

        Button btnCancel = new Button(sashForm2, SWT.NONE);
        btnCancel.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });
        btnCancel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_CANCEL));

        sashForm2.setWeights(new int[] {5, 1, 1});
    }

    private void functionBody(SashForm mainForm) {
        SashForm sashForm3 = new SashForm(mainForm, SWT.VERTICAL);

        Label lblNewLabel = new Label(sashForm3, SWT.NONE);
        lblNewLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_FUNCTION_BODY));

        textFunctionBodyTemp = new Text(sashForm3, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
        textFunctionBodyTemp.setText(relyInfo.getFunctionBodyTemplate(comboLanguage.getText()));
        sashForm3.setWeights(new int[] {1, 6});
    }

    private void paramListUi(SashForm mainForm) {
        paramSashForm = new SashForm(mainForm, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);

        SashForm sashForm1 = new SashForm(paramSashForm, SWT.VERTICAL);

        paramListButtonUi(sashForm1);
        paramListTableUi(sashForm1);

        sashForm1.setWeights(new int[] {1, 6});
        paramSashForm.setWeights(new int[] {3});
    }

    private void paramListTableUi(SashForm sashForm1) {
        SashForm paramTableSashFrom = new SashForm(sashForm1, SWT.NONE);

        paramTableUi(paramTableSashFrom);
        paramMoveUi(paramTableSashFrom);

        paramTableSashFrom.setWeights(new int[] {8, 1});
    }

    private void paramMoveUi(SashForm paramTableSashFrom) {
        SashForm tableOptSashForm = new SashForm(paramTableSashFrom, SWT.VERTICAL);

        hideLabel(tableOptSashForm);

        Button btnRollToHead = new Button(tableOptSashForm, SWT.NONE);
        btnRollToHead.addSelectionListener(new OrderChangeAdapter(OrderChangeAdapter.UP_HEAD, this));
        btnRollToHead.setText(MOVE_UP + MOVE_UP);

        Button btnRollStep = new Button(tableOptSashForm, SWT.NONE);
        btnRollStep.addSelectionListener(new OrderChangeAdapter(OrderChangeAdapter.UP, this));
        btnRollStep.setText(MOVE_UP);

        Button btnDownStep = new Button(tableOptSashForm, SWT.NONE);
        btnDownStep.addSelectionListener(new OrderChangeAdapter(OrderChangeAdapter.DOWN, this));
        btnDownStep.setText(MOVE_DOWN);

        Button btnDownToBottom = new Button(tableOptSashForm, SWT.NONE);
        btnDownToBottom.addSelectionListener(new OrderChangeAdapter(OrderChangeAdapter.DOWN_BOTTOM, this));
        btnDownToBottom.setText(MOVE_DOWN + MOVE_DOWN);

        hideLabel(tableOptSashForm);

        tableOptSashForm.setWeights(new int[] {1, 1, 1, 1, 1, 1});
    }

    private void paramTableUi(SashForm paramTableSashFrom) {
        Composite composite = new Composite(paramTableSashFrom, SWT.H_SCROLL | SWT.V_SCROLL);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
        tableParam = tableViewer.getTable();
        tableParam.setHeaderVisible(true);
        tableParam.setLinesVisible(true);
        initTableViewer();
    }

    private void paramListButtonUi(SashForm sashForm1) {
        SashForm paramSearchSashForm = new SashForm(sashForm1, SWT.NONE);

        Label lblParamSearch = new Label(paramSearchSashForm, SWT.NONE);
        lblParamSearch.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_PARAM_LIST));

        Button btnResetAll = new Button(paramSearchSashForm, SWT.NONE);
        btnResetAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                removeAllData();
            }
        });
        btnResetAll.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_RESET));

        Composite compSplit = new Composite(paramSearchSashForm, SWT.NONE);
        compSplit.setEnabled(false);

        Button btnAddParam = new Button(paramSearchSashForm, SWT.NONE);
        btnAddParam.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addData(new CreateFunctionParam(relyInfo, new CreateFunctionParamsTitle()));
            }
        });
        btnAddParam.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_ADD));

        Button btnDelParam = new Button(paramSearchSashForm, SWT.NONE);
        btnDelParam.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Optional<CreateFunctionParam> data = getSelectCreateFunctionParam();
                if (data.isPresent()) {
                    removeData(data.get());
                }
            }
        });
        btnDelParam.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_BUTTON_DELETE));
        paramSearchSashForm.setWeights(new int[] {4, 2, 8, 2, 2});
    }

    private void functionOrProcedureUi(SashForm mainForm) {
        SashForm retTypeSashForm = new SashForm(mainForm, SWT.BORDER);

        sashForm = new SashForm(retTypeSashForm, SWT.NONE);

        functionOrProcedureButton();
        languageAndReturnType();

        sashForm.setWeights(new int[] {1, 1});
        retTypeSashForm.setWeights(new int[] {2});
    }

    private void languageAndReturnType() {
        sashFormOfFunctionSelect = new SashForm(sashForm, SWT.VERTICAL);

        SashForm sashForm8 = new SashForm(sashFormOfFunctionSelect, SWT.NONE);

        Label lblNewLabel2 = new Label(sashForm8, SWT.NONE);
        lblNewLabel2.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_LANGUAGE));

        comboLanguage = new Combo(sashForm8, SWT.READ_ONLY);
        comboLanguage.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                updateTemplateBody(comboLanguage.getText());
            }
        });
        initComboLanguage(comboLanguage);
        sashForm8.setWeights(new int[] {1, 1});

        SashForm sashForm7 = new SashForm(sashFormOfFunctionSelect, SWT.NONE);

        Label lblRetType = new Label(sashForm7, SWT.NONE);
        lblRetType.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_RETURN_TYPE));

        comboRetType = new Combo(sashForm7, SWT.NONE);
        initReturnTypeCombo(comboRetType);
        sashForm7.setWeights(new int[] {1, 1});
        sashFormOfFunctionSelect.setWeights(new int[] {1, 1});
    }

    private void functionOrProcedureButton() {
        Group group = new Group(sashForm, SWT.NONE);
        group.setLayout(new FillLayout(SWT.HORIZONTAL));

        btnFunction = new Button(group, SWT.RADIO);
        btnFunction.setSelection(true);
        btnFunction.addSelectionListener(new FunctionOrProcedureAdapter(this, tabItemFunction, FuncTypeEnum.FUNCTION));
        btnFunction.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_FUNCTION));
        curFuncType = FuncTypeEnum.FUNCTION;

        btnProcedure = new Button(group, SWT.RADIO);
        btnProcedure.addSelectionListener(
                new FunctionOrProcedureAdapter(this, tabItemFunction, FuncTypeEnum.PROCEDURE));
        btnProcedure.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_PROCEDURE));

        btnTrigger = new Button(group, SWT.RADIO);
        btnTrigger.addSelectionListener(new FunctionOrProcedureAdapter(this, tabItemFunction, FuncTypeEnum.TRIGGER));
        btnTrigger.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_TRIGGER));
    }

    private void schemaAndFunctionNameUi(SashForm mainForm) {
        SashForm nameSshForm = new SashForm(mainForm, SWT.BORDER | SWT.SMOOTH | SWT.VERTICAL);

        SashForm nameSshFormAll = new SashForm(nameSshForm, SWT.VERTICAL);

        SashForm schemaSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        schemaSshForm.setSashWidth(0);

        Label lblSchema = new Label(schemaSshForm, SWT.NONE);
        lblSchema.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_SCHEMA));

        lblSchemaText = new Label(schemaSshForm, SWT.NONE);
        lblSchemaText.setText(relyInfo.getSchameName());
        schemaSshForm.setWeights(new int[] {1, 4});

        SashForm functionNameSshForm = new SashForm(nameSshFormAll, SWT.NONE);
        functionNameSshForm.setSashWidth(0);

        Label lblFunctionName = new Label(functionNameSshForm, SWT.NONE);
        lblFunctionName.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_FUNCTION_NAME));

        text = new Text(functionNameSshForm, SWT.BORDER);
        functionNameSshForm.setWeights(new int[] {1, 4});
        nameSshFormAll.setWeights(new int[] {1, 1});
        nameSshForm.setWeights(new int[] {1});
    }

    private void initReturnTypeCombo(Combo tempCombo) {
        for (String type: relyInfo.getSupportTypes()) {
            tempCombo.add(type);
        }
    }

    private void initTableViewer() {
        tableViewer.setContentProvider(new CreateFunctionContentProvider());
        CreateFunctionParamsTitle paramsTitles = new CreateFunctionParamsTitle();
        List<String> titles = paramsTitles.getTitles();
        IntStream.iterate(0, seed -> seed + 1).limit(titles.size()).forEach(idx -> {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
            column.getColumn().setWidth(20);
            column.getColumn().setText(titles.get(idx));
            column.setEditingSupport(new CreateFunctionEditingSupport(tableViewer, idx));
            column.setLabelProvider(new CreateFunctionDataLableProvider(idx));
        });
        tableParam.addControlListener(new CreateFunctionControlAdapter(tableParam, paramsTitles));
    }

    /**
     * Get function ui data
     *
     * @return CreateFunctionUiData the create function ui data
     */
    public CreateFunctionUiData getFunctionUiData() {
        String functionName = text.getText();
        String functionReturnType = comboRetType.getText();
        String functionBody = textFunctionBodyTemp.getText();
        String language = btnFunction.getSelection() ? comboLanguage.getText() : curFuncType.language;
        LinkedList<CreateFunctionParam> params = getDataList().orElse(new LinkedList<CreateFunctionParam>());
        return new CreateFunctionUiData(
                relyInfo,
                functionName,
                language,
                functionReturnType,
                params,
                functionBody);
    }

    /**
     * description: get data list from tableviewer
     *
     * @return Optional<List<?>> the data list
     */
    @SuppressWarnings("unchecked")
    public Optional<LinkedList<CreateFunctionParam>> getDataList() {
        Object inputs = tableViewer.getInput();
        if (inputs instanceof LinkedList<?>) {
            return Optional.of((LinkedList<CreateFunctionParam>) inputs);
        }
        return Optional.empty();
    }

    /**
     * description: add data
     *
     * @param data the data to add
     * @return boolean true if add success
     */
    public boolean addData(CreateFunctionParam data) {
        Optional<LinkedList<CreateFunctionParam>> inputOptional = getDataList();
        List<CreateFunctionParam> newInputs;
        if (!inputOptional.isPresent()) {
            newInputs = new LinkedList<CreateFunctionParam>();
        } else {
            newInputs = inputOptional.get();
        }
        newInputs.add(data);
        tableViewer.setInput(newInputs);
        tableViewer.refresh();
        return true;
    }

    /**
     * description: remove data
     *
     * @param data the data to remove, must already in tableviewer
     * @return boolean true if success
     */
    public boolean removeData(CreateFunctionParam data) {
        boolean isRemoved = false;
        Optional<LinkedList<CreateFunctionParam>> inputOptional = getDataList();
        if (inputOptional.isPresent()) {
            LinkedList<CreateFunctionParam> inputs = inputOptional.get();
            isRemoved = inputs.remove(data);
            tableViewer.refresh();
        }
        return isRemoved;
    }

    /**
     * description: remove all data
     */
    public void removeAllData() {
        tableViewer.setInput(null);
        tableViewer.refresh();
    }

    /**
     * description: reset all data
     *
     * @param dataList the set data list
     */
    public void resetAllData(LinkedList<CreateFunctionParam> dataList) {
        tableViewer.setInput(dataList);
        tableViewer.refresh();
    }

    /**
     * Get select create function param
     *
     * @return Optional<CreateFunctionParm> the function param
     */
    public Optional<CreateFunctionParam> getSelectCreateFunctionParam() {
        IStructuredSelection structSelect = tableViewer.getStructuredSelection();
        Object obj = structSelect.getFirstElement();
        if (obj instanceof CreateFunctionParam) {
            return Optional.of((CreateFunctionParam) obj);
        } else {
            return Optional.empty();
        }
    }

    private class FunctionOrProcedureAdapter extends SelectionAdapter {
        private CreateFunctionMainDlg dlg;
        private CTabItem tableItemFunction;
        private FuncTypeEnum funcType;

        public FunctionOrProcedureAdapter(CreateFunctionMainDlg dlg,
                CTabItem tableItemFunction, FuncTypeEnum funcType) {
            this.dlg = dlg;
            this.tableItemFunction = tableItemFunction;
            this.funcType = funcType;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            Button btn = (Button) event.widget;
            if (btn.getSelection()) {
                curFuncType = funcType;
                dlg.getSashFormOfFunctionSelect().setVisible(funcType == FuncTypeEnum.FUNCTION);
                dlg.setTitle(functionOrProcedureText(funcType));
                tableItemFunction.setText(functionOrProcedureText(funcType));
                boolean paramEnable = true;
                if (funcType == FuncTypeEnum.TRIGGER) {
                    dlg.removeAllData();
                    paramEnable = false;
                }
                dlg.paramSashForm.setEnabled(paramEnable);
                updateTemplateBody(funcType == FuncTypeEnum.FUNCTION ? comboLanguage.getText() : funcType.language);
            }
        }

        private String functionOrProcedureText (FuncTypeEnum funcType) {
            return MessageConfigLoader.getProperty(IMessagesConstants.CREATE_FUNCTION_UI_CREATE) +
                    MessageConfigLoader.getProperty(funcType.name) +
                    ((funcType == FuncTypeEnum.TRIGGER) ?
                            MessageConfigLoader.getProperty(FuncTypeEnum.FUNCTION.name) : "");
        }
    }

    private static class OrderChangeAdapter extends SelectionAdapter {
        /**
         * Move to top
         */
        public static final int UP_HEAD = 0x1;
        /**
         * Move up
         */
        public static final int UP = 0x2;
        /**
         * Move down
         */
        public static final int DOWN = 0x4;
        /**
         * Move to bottom
         */
        public static final int DOWN_BOTTOM = 0x8;
        private int optMove = 0;
        private CreateFunctionMainDlg dlg;

        public OrderChangeAdapter(int optMove, CreateFunctionMainDlg dlg) {
            this.optMove = optMove;
            this.dlg = dlg;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            Optional<CreateFunctionParam> optData = dlg.getSelectCreateFunctionParam();
            if (!optData.isPresent()) {
                return;
            }
            CreateFunctionParam data = optData.get();
            // because these already have data, so this must have list data
            LinkedList<CreateFunctionParam> linkList = dlg.getDataList().get();
            int pos = linkList.indexOf(data);
            int desPos = -1;
            if (optMove == UP_HEAD) {
                desPos = 0;
            } else if (optMove == UP) {
                desPos = pos - 1;
            } else if (optMove == DOWN) {
                desPos = pos + 1;
            } else {
                desPos = linkList.size() - 1;
            }

            if (desPos == pos || desPos < 0 || desPos >= linkList.size()) {
                return;
            }
            Collections.swap(linkList, pos, desPos);
            dlg.resetAllData(linkList);
        }
    }

    /**
     * Set init language
     *
     * @param String the language
     */
    public void setInitLanguage(String language) {
        this.initLanguage = language;
    }

    /**
     * Init language ui
     */
    public void initLanguageUi() {
        FuncTypeEnum funcType;
        if (CreateFunctionRelyInfo.PROCEDURE.equals(initLanguage)) {
            funcType = FuncTypeEnum.PROCEDURE;
        } else if (CreateFunctionRelyInfo.LANGUAGE_TRIGGER.equals(initLanguage)) {
            funcType = FuncTypeEnum.TRIGGER;
        } else {
            funcType = FuncTypeEnum.FUNCTION;
        }
        btnStateChange(funcType);
    }

    private void btnStateChange(FuncTypeEnum funcType) {
        btnFunction.setSelection(funcType == FuncTypeEnum.FUNCTION);
        btnProcedure.setSelection(funcType == FuncTypeEnum.PROCEDURE);
        btnTrigger.setSelection(funcType == FuncTypeEnum.TRIGGER);
        Button nodifyBtn = null;
        switch (funcType) {
            case PROCEDURE:
                nodifyBtn = btnProcedure;
                break;
            case TRIGGER:
                nodifyBtn = btnTrigger;
                break;
            case FUNCTION:
            default:
                nodifyBtn = btnFunction;
                break;
        }
        if (funcType == FuncTypeEnum.FUNCTION) {
            int index = relyInfo.getSupportLanguage().indexOf(initLanguage);
            comboLanguage.select(Math.max(0, index));
        }
        nodifyBtn.notifyListeners(SWT.Selection, null);
    }

    /**
     * Gets the sash form when function selected
     *
     * @return SashForm the sash form when function selected
     */
    public SashForm getSashFormOfFunctionSelect() {
        return sashFormOfFunctionSelect;
    }

    private void initComboLanguage(Combo comboLanguage) {
        List<String> languages = relyInfo.getSupportLanguage();
        for (String lang : languages) {
            comboLanguage.add(lang);
        }
        comboLanguage.select(0);
    }

    private void updateTemplateBody(String language) {
        textFunctionBodyTemp.setText(relyInfo.getFunctionBodyTemplate(language));
    }

    private enum FuncTypeEnum {
        FUNCTION(IMessagesConstants.CREATE_FUNCTION_UI_FUNCTION, CreateFunctionRelyInfo.LANGUAGE_PLP),
        PROCEDURE(IMessagesConstants.CREATE_FUNCTION_UI_PROCEDURE, CreateFunctionRelyInfo.PROCEDURE),
        TRIGGER(IMessagesConstants.CREATE_FUNCTION_UI_TRIGGER, CreateFunctionRelyInfo.LANGUAGE_TRIGGER);

        /**
         * The name
         */
        public final String name;

        /**
         * The language
         */
        public final String language;
        private FuncTypeEnum(String name, String language) {
            this.name = name;
            this.language = language;
        }
    }
}
