/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021. All rights reserved.
 */

package com.huawei.mppdbide.view.view.createview;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * Title: class
 * Description: The Class CreateViewMainDialog.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2021.
 *
 * @version [DataStudio 2.1.0, 21 Oct., 2021]
 * @since 21 Oct., 2021
 */
public class CreateViewMainDialog extends Dialog {
    /**
     * Dialog result
     */
    protected int result = -1;
    /**
     * The shell
     */
    protected Shell shell;

    private Text viewNameText;
    private Table viewBodyTable;
    private Text ddlText;
    private Table tableAliasTable;
    private Table whereTable;

    private CTabFolder tabFolder;
    private CTabItem firstItem;
    private CTabItem secondItem;

    private Combo schemaCombo;
    private Combo tableCombo;
    private Combo columnCombo;

    private Combo schema1Combo;
    private Combo table1Combo;
    private Combo column1Combo;
    private Combo schema2Combo;
    private Combo table2Combo;
    private Combo column2Combo;

    private String schema;
    private String table;

    private CreateViewDataModel createViewdataModel;
    private ICreateViewRelyInfo createViewRelyInfo;
    private String viewFullName;

    private Map<String, String> tableAliasMap = new HashMap<String, String>();
    private List<String> schemaList = new ArrayList<String>();
    private Map<String, List<String>> schema2Tables = new HashMap<String, List<String>>();
    private Map<String, List<String>> schemaTable2Columns = new HashMap<String, List<String>>();
    private Map<String, List<String>> whereConditionList = new HashMap<String, List<String>>();

    /**
     * Gets save data model
     *
     * @return CreateViewDataModel create view data model
     */
    public CreateViewDataModel getSaveDataModel() {
        return createViewdataModel;
    }

    /**
     * Sets Save data model
     *
     * @param CreateViewDataModel the data model
     */
    public void setSaveDataModel(CreateViewDataModel dataModel) {
        this.createViewdataModel = dataModel;
    }

    /**
     * Sets schema
     *
     * @param String the schema
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Sets table
     *
     * @param String the table
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * Gets view name Text
     *
     * @return Text view name text
     */
    public Text getViewNameText () {
        return this.viewNameText;
    }

    /**
     * Sets create view rely info
     *
     * @param ICreateViewRelyInfo the create view rely info
     */
    public void setCreateViewRelyInfo(ICreateViewRelyInfo createViewRelyInfo) {
        this.createViewRelyInfo = createViewRelyInfo;
    }

    /**
     * Gets create view rely info
     *
     * @return ICreateViewRelyInfo the create view rely info
     */
    public ICreateViewRelyInfo getCreateViewRelyInfo() {
        return createViewRelyInfo;
    }

    /**
     * Gets view full name
     *
     * @return String the view full name
     */
    public String getViewNameFullName() {
        return viewFullName;
    }

    /**
     * Create view main dialog.
     *
     * @param Shell the parent
     * @param int the style
     */
    public CreateViewMainDialog(Shell parent, int style) {
        super(parent, style);
        setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_CREATE_VIEW));
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
        Display display = getParent().getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        return result;
    }

    /**
     * Create contents of the dialog.
     */
    private void createContents() {
        createMainShell();
        generalPage();
        sqlPreviewPage();
        displayDataForEditView();
    }

    private void generalPage() {
        firstItem = new CTabItem(tabFolder, SWT.NONE);
        if (createViewRelyInfo.getIsEditView()) {
            firstItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_VIEW_UI_EDIT_VIEW));
        } else {
            firstItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_CREATE_VIEW));
        }

        SashForm firstMainForm = new SashForm(tabFolder, SWT.VERTICAL);
        firstItem.setControl(firstMainForm);

        schemaAndViewName(firstMainForm);
        selectSchamaTableColumn(firstMainForm);
        viewBodyTable(firstMainForm);
        tableAliasAndCondition(firstMainForm);
        nextAndCancelButton(firstMainForm);

        firstMainForm.setWeights(new int[] { 1, 1, 6, 7, 1 });
    }

    private void nextAndCancelButton(SashForm firstMainForm) {
        SashForm nextSashForm = new SashForm(firstMainForm, SWT.NONE);

        Label hideLabel = new Label(nextSashForm, SWT.NONE);

        Button nextButton = new Button(nextSashForm, SWT.NONE);
        nextButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_NEXT));
        nextButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(secondItem);
                getTableAlias();
                ddlText.setText(getViewDdl());
            }
        });

        Button cancelButton = new Button(nextSashForm, SWT.NONE);
        cancelButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_CANCEL));
        cancelButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getParent().dispose();
            }
        });

        nextSashForm.setWeights(new int[] { 6, 1, 1 });
    }

    private void tableAliasAndCondition(SashForm firstMainForm) {
        SashForm sashForm = new SashForm(firstMainForm, SWT.NONE);

        tableAliasUi(sashForm);
        whereConditionUi(sashForm);

        sashForm.setWeights(new int[] { 4, 6 });
    }

    private void whereConditionUi(SashForm sashForm) {
        SashForm sashFormCondition = new SashForm(sashForm, SWT.VERTICAL);
        whereConditionComboUi(sashFormCondition);
        whereConditionTableUi(sashFormCondition);
        sashFormCondition.setWeights(new int[] { 2, 6 });
    }

    private void whereConditionComboUi(SashForm sashFormCondition) {
        SashForm sashForm = new SashForm(sashFormCondition, SWT.NONE);
        expressionCombo(sashForm);
        addDeleteButton(sashForm);
        sashForm.setWeights(new int[] { 5, 1 });
    }

    private void expressionCombo(SashForm sashForm) {
        SashForm sashFormCurrent = new SashForm(sashForm, SWT.VERTICAL);
        leftExpressionUi(sashFormCurrent);
        rightExpressionUi(sashFormCurrent);
        sashFormCurrent.setWeights(new int[] { 1, 1 });
    }

    private void whereConditionTableUi(SashForm sashForm) {
        SashForm sashFormCurrent = new SashForm(sashForm, SWT.NONE);

        Composite composite = new Composite(sashFormCurrent, SWT.H_SCROLL | SWT.V_SCROLL);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        whereTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION);
        whereTable.setHeaderVisible(true);
        whereTable.setLinesVisible(true);
        whereTable.addSelectionListener(new WhereTableSelection(whereTable));

        TableColumn tblclmnNewColumn1 = new TableColumn(whereTable, SWT.NONE);
        tblclmnNewColumn1.setWidth(170);
        tblclmnNewColumn1.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_COLUMN_1));

        TableColumn tblclmnNewColumn2 = new TableColumn(whereTable, SWT.NONE);
        tblclmnNewColumn2.setWidth(236);
        tblclmnNewColumn2.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_COLUMN_2));

        sashFormCurrent.setWeights(new int[] { 1 });
    }

    private void addDeleteButton(SashForm sashForm) {
        SashForm sashFormCurrent = new SashForm(sashForm, SWT.VERTICAL);

        Button addConditionButton = new Button(sashFormCurrent, SWT.NONE);
        addConditionButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_ADD));
        addConditionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                addWhereConditionData();
            }
        });

        Button delButton = new Button(sashFormCurrent, SWT.NONE);
        delButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_DELETE));
        delButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int rowNum = whereTable.getSelectionIndex();
                if (rowNum != -1) {
                    deleteWhereConditionData(rowNum);
                    whereTable.remove(rowNum);
                }
            }
        });

        sashFormCurrent.setWeights(new int[] {1, 1});
    }

    private void rightExpressionUi(SashForm sashForm) {
        SashForm sashFormCurrent = new SashForm(sashForm, SWT.NONE);

        Label equalLabel = new Label(sashFormCurrent, SWT.CENTER);
        equalLabel.setText("=");

        schema2Combo = new Combo(sashFormCurrent, SWT.NONE);
        schema2Combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String schemaName = schema2Combo.getText();
                List<String> tables = schema2Tables.get(schemaName);
                String[] table1Names = tables.toArray(new String[0]);
                table2Combo.setItems(table1Names);
                column2Combo.setItems(new String[] {});
            }
        });

        table2Combo = new Combo(sashFormCurrent, SWT.NONE);
        table2Combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String tableName = table2Combo.getText();
                String schemaName = schema2Combo.getText();
                String tableFullName = schemaName + "." + tableName;
                List<String> columns = schemaTable2Columns.get(tableFullName);
                String[] column1Names = columns.toArray(new String[0]);
                column2Combo.setItems(column1Names);
            }
        });

        column2Combo = new Combo(sashFormCurrent, SWT.NONE);

        sashFormCurrent.setWeights(new int[] { 1, 1, 1, 1 });
    }

    private void leftExpressionUi(SashForm sashFormCurrent) {
        SashForm sashForm = new SashForm(sashFormCurrent, SWT.NONE);

        Label lblNewLabel = new Label(sashForm, SWT.CENTER);
        lblNewLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_WHERE));

        schema1Combo = new Combo(sashForm, SWT.NONE);
        schema1Combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String schemaName = schema1Combo.getText();
                List<String> tables = schema2Tables.get(schemaName);
                String[] table1Names = tables.toArray(new String[0]);
                table1Combo.setItems(table1Names);
                column1Combo.setItems(new String[] {});
            }
        });

        table1Combo = new Combo(sashForm, SWT.NONE);
        table1Combo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String tableName = table1Combo.getText();
                String schemaName = schema1Combo.getText();
                String tableFullName = schemaName + "." + tableName;
                List<String> columns = schemaTable2Columns.get(tableFullName);
                String[] column1Names = columns.toArray(new String[0]);
                column1Combo.setItems(column1Names);
            }
        });

        column1Combo = new Combo(sashForm, SWT.NONE);

        sashForm.setWeights(new int[] { 1, 1, 1, 1 });
    }

    private void tableAliasUi(SashForm sashForm) {
        SashForm sashFormCurrent = new SashForm(sashForm, SWT.VERTICAL);

        Label lblNewLabel = new Label(sashFormCurrent, SWT.NONE);
        lblNewLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_TABLE_ALIAS_NAME));

        Composite table2omposite = new Composite(sashFormCurrent, SWT.H_SCROLL | SWT.V_SCROLL);
        table2omposite.setLayout(new FillLayout(SWT.HORIZONTAL));

        tableAliasTable = new Table(table2omposite, SWT.BORDER | SWT.FULL_SELECTION);
        tableAliasTable.setHeaderVisible(true);
        tableAliasTable.setLinesVisible(true);
        tableAliasTable.addSelectionListener(new TableSelection(tableAliasTable, 1));

        TableColumn tblclmnNewColumn1 = new TableColumn(tableAliasTable, SWT.NONE);
        tblclmnNewColumn1.setWidth(117);
        tblclmnNewColumn1.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_TABLE_FULL_NAME));

        TableColumn tblclmnNewColumn2 = new TableColumn(tableAliasTable, SWT.NONE);
        tblclmnNewColumn2.setWidth(113);
        tblclmnNewColumn2.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_TABLE_ALIAS_NAME));

        sashFormCurrent.setWeights(new int[] { 1, 7 });
    }

    private void viewBodyTable(SashForm firstMainForm) {
        SashForm table1SashForm = new SashForm(firstMainForm, SWT.NONE);

        Composite table1Composite = new Composite(table1SashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        table1Composite.setLayout(new FillLayout(SWT.HORIZONTAL));

        viewBodyTable = new Table(table1Composite, SWT.BORDER | SWT.FULL_SELECTION);
        viewBodyTable.setHeaderVisible(true);
        viewBodyTable.setLinesVisible(true);
        viewBodyTable.addSelectionListener(new TableSelection(viewBodyTable, 3));

        TableColumn schemaColumn = new TableColumn(viewBodyTable, SWT.NONE);
        schemaColumn.setWidth(153);
        schemaColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_SCHEMA_NAME));

        TableColumn tableColumn = new TableColumn(viewBodyTable, SWT.NONE);
        tableColumn.setWidth(147);
        tableColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_TABLE_NAME));

        TableColumn aliasColumn = new TableColumn(viewBodyTable, SWT.NONE);
        aliasColumn.setWidth(162);
        aliasColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_COLUMN_NAME));

        TableColumn tblclmnNewColumn = new TableColumn(viewBodyTable, SWT.NONE);
        tblclmnNewColumn.setWidth(309);
        tblclmnNewColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_COLUMN_ALIAS_NAME));

        table1SashForm.setWeights(new int[] { 1 });
    }

    private static class TableSelection extends SelectionAdapter {
        private Table table;
        private int editableColumn;

        public TableSelection(Table table, int editableColumn) {
            this.table = table;
            this.editableColumn = editableColumn;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            // Clean up any previous editor control
            final TableEditor editor = new TableEditor(table);
            // The editor must have the same size as the cell and must
            // not be any smaller than 50 pixels.
            editor.horizontalAlignment = SWT.LEFT;
            editor.grabHorizontal = true;
            editor.minimumWidth = 50;
            Control oldEditor = editor.getEditor();
            if (oldEditor != null) {
                oldEditor.dispose();
            }

            // Identify the selected row
            TableItem item = (TableItem) event.item;
            if (item == null) {
                return;
            }

            // The control that will be the editor must be a child of the
            // Table
            Text newEditor = new Text(table, SWT.NONE);
            newEditor.setText(item.getText(editableColumn));

            newEditor.addModifyListener(new ModifyListener() {
                /**
                 * Mofify text
                 *
                 * @param ModifyEvent modify event
                 */
                public void modifyText(ModifyEvent event) {
                    Text text = (Text) editor.getEditor();
                    editor.getItem()
                            .setText(editableColumn, text.getText());
                }
            });
            newEditor.selectAll();
            newEditor.setFocus();
            editor.setEditor(newEditor, item, editableColumn);

            newEditor.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                    Text text = (Text) editor.getEditor();
                    text.dispose();
                }

                @Override
                public void focusGained(FocusEvent e) {
                }
            });
        }
    }

    private class WhereTableSelection extends SelectionAdapter {
        private Table table;

        public WhereTableSelection(Table table) {
            this.table = table;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            // Clean up any previous editor control
            final TableEditor editor = new TableEditor(table);
            // The editor must have the same size as the cell and must
            // not be any smaller than 50 pixels.
            editor.horizontalAlignment = SWT.LEFT;
            editor.grabHorizontal = true;
            editor.minimumWidth = 50;
            Control oldEditor = editor.getEditor();
            if (oldEditor != null) {
                oldEditor.dispose();
            }

            // Identify the selected row
            TableItem item = (TableItem) event.item;
            if (item == null) {
                return;
            }

            // The control that will be the editor must be a child of the
            // Table
            int editableColumn = 1;
            Text newEditor = new Text(table, SWT.NONE);
            newEditor.setText(item.getText(editableColumn));

            newEditor.addModifyListener(new ModifyListener() {
                /**
                 * Modify text
                 *
                 * @param ModifyEvent modify event
                 */
                public void modifyText(ModifyEvent event) {
                    Text text = (Text) editor.getEditor();
                    editor.getItem()
                            .setText(editableColumn, text.getText());
                }
            });
            newEditor.selectAll();
            newEditor.setFocus();
            editor.setEditor(newEditor, item, editableColumn);
            String leftExpression = item.getText(0);
            String rightExpression = item.getText(1);
            newEditor.addFocusListener(new FocusListener() {
                @Override
                public void focusLost(FocusEvent e) {
                    Text text = (Text) editor.getEditor();
                    if (!text.getText().equals(rightExpression)) {
                        List<String> rightExpressionList = whereConditionList.get(leftExpression);
                        if (!rightExpressionList.contains(text.getText())) {
                            deleteWhereConditionData(leftExpression, rightExpression);
                            updateWhereConditionListOnly(leftExpression, text.getText());
                        } else {
                            text.setText(rightExpression);
                        }
                    }
                    text.dispose();
                }

                @Override
                public void focusGained(FocusEvent e) {
                }
            });
        }
    }

    private void selectSchamaTableColumn(SashForm firstMainForm) {
        SashForm selectSashForm = new SashForm(firstMainForm, SWT.NONE);

        threeCombo(selectSashForm);
        twoButton(selectSashForm);

        selectSashForm.setWeights(new int[] { 2, 2, 2, 1, 1 });
    }

    private void twoButton(SashForm selectSashForm) {
        Button addButton = new Button(selectSashForm, SWT.NONE);
        addButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                insertViewBodyData();
            }
        });

        Button deleteButton = new Button(selectSashForm, SWT.NONE);
        deleteButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_DELETE));
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                deleteViewBodyData();
            }
        });
    }

    private void threeCombo(SashForm selectSashForm) {
        SashForm schemaSashForm = new SashForm(selectSashForm, SWT.NONE);

        Label schemaLabel = new Label(schemaSashForm, SWT.NONE);
        schemaLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_SCHEMA_NAME));

        schemaCombo = new Combo(schemaSashForm, SWT.NONE);
        schemaSashForm.setWeights(new int[] { 1, 1 });
        setSchemaNameList();
        schemaCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String schemaName = schemaCombo.getText();
                setSchema(schemaName);
                updateTableList();
                columnCombo.setItems(new String[] {});
            }
        });

        SashForm tableSashForm = new SashForm(selectSashForm, SWT.NONE);

        Label tableLabel = new Label(tableSashForm, SWT.CENTER);
        tableLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_TABLE_NAME));

        tableCombo = new Combo(tableSashForm, SWT.NONE);
        tableSashForm.setWeights(new int[] { 1, 1 });
        tableCombo.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String tableName = tableCombo.getText();
                setTable(tableName);
                updateColumnList();
            }
        });

        SashForm columnSashForm = new SashForm(selectSashForm, SWT.NONE);

        Label columnLabel = new Label(columnSashForm, SWT.CENTER);
        columnLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_COLUMN_NAME));

        columnCombo = new Combo(columnSashForm, SWT.NONE);
        columnSashForm.setWeights(new int[] { 1, 1 });
    }

    private void schemaAndViewName(SashForm firstMainForm) {
        SashForm nameSashForm = new SashForm(firstMainForm, SWT.NONE);

        SashForm schemaNameForm = new SashForm(nameSashForm, SWT.NONE);

        Label schemaNameLabel = new Label(schemaNameForm, SWT.HORIZONTAL);
        schemaNameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_SCHEMA_NAME));

        Combo combo = new Combo(schemaNameForm, SWT.NONE);
        combo.setItems(new String[] { createViewRelyInfo.getFixedSchemaName() });
        combo.select(0);
        combo.setEnabled(false);

        schemaNameForm.setWeights(new int[] { 2, 5 });

        SashForm viewNameSashForm = new SashForm(nameSashForm, SWT.NONE);

        Label viewNameLabel = new Label(viewNameSashForm, SWT.CENTER);
        viewNameLabel.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_VIEW_NAME));

        viewNameText = new Text(viewNameSashForm, SWT.BORDER);
        if (createViewRelyInfo.getIsEditView()) {
            viewNameText.setText(createViewRelyInfo.getFixedViewName());
            viewNameText.setEnabled(false);
        }

        viewNameSashForm.setWeights(new int[] { 2, 5 });

        nameSashForm.setWeights(new int[] { 5, 5 });
    }

    private void sqlPreviewPage() {
        secondItem = new CTabItem(tabFolder, SWT.NONE);
        secondItem.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_PREVIEW));

        SashForm secondMainForm = new SashForm(tabFolder, SWT.VERTICAL);
        secondItem.setControl(secondMainForm);

        SashForm textSashForm = new SashForm(secondMainForm, SWT.NONE);
        Composite textComposite = new Composite(textSashForm, SWT.H_SCROLL | SWT.V_SCROLL);
        textComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
        ddlText = new Text(textComposite, SWT.BORDER | SWT.MULTI);
        textSashForm.setWeights(new int[] {1});

        twoButtonForPreviewPage(secondMainForm);

        secondMainForm.setWeights(new int[] { 16, 1 });
    }

    private void twoButtonForPreviewPage(SashForm secondMainForm) {
        SashForm finishSashForm = new SashForm(secondMainForm, SWT.NONE);

        Label hideLabel1 = new Label(finishSashForm, SWT.NONE);

        Button previousButton = new Button(finishSashForm, SWT.NONE);
        previousButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_PREVIOUS));
        previousButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                tabFolder.setSelection(firstItem);
            }
        });

        Button finishButton = new Button(finishSashForm, SWT.NONE);
        finishButton.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_FINISH));
        finishButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                result = 0;
                createViewRelyInfo.setDdlSentence(ddlText.getText());
                createViewdataModel = constructCreateViewDataModel();
                viewFullName = createViewRelyInfo.getFixedSchemaName() + "." + viewNameText.getText();
                getParent().dispose();
            }
        });

        finishSashForm.setWeights(new int[] { 6, 1, 1 });
    }

    private void displayDataForEditView() {
        if (createViewRelyInfo.getIsEditView()) {
            if (createViewdataModel != null) {
                List<ViewBody> viewBodyList = createViewdataModel.getViewBodyList();
                List<TableAlias> tableAliasList = createViewdataModel.getTableAliasList();
                List<WhereCondition> whereCondition = createViewdataModel.getWhereConditionList();
                displayViewBody(viewBodyList);
                displayTableAlias(tableAliasList);
                displayWhereCondition(whereCondition);
            }
        }
    }

    private void createMainShell() {
        shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.MIN | SWT.RESIZE | SWT.APPLICATION_MODAL);
        shell.setSize(650, 600);
        if (createViewRelyInfo.getIsEditView()) {
            shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_VIEW_UI_EDIT_VIEW));
        } else {
            shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.CREATE_VIEW_UI_CREATE_VIEW));
        }
        shell.setLayout(new FillLayout(SWT.HORIZONTAL));

        tabFolder = new CTabFolder(shell, SWT.BORDER);
        tabFolder.setSelectionBackground(
                Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
    }

    /**
     * Edit view body data
     *
     * @param ViewBody the view body
     */
    public void editViewBodyData(ViewBody viewBody) {
        String schemaName = viewBody.getSchemaName();
        String tableName = viewBody.getTableName();
        String column = viewBody.getColumnName();
        String tableFullName = schemaName + "." + tableName;
        if (!("".equals(schemaName) || "".equals(tableName) || "".equals(column))) {
            if (!schemaList.contains(schemaName)) {
                schemaList.add(schemaName);
                List<String> tables = new ArrayList<String>();
                tables.add(tableName);
                schema2Tables.put(schemaName, tables);
                List<String> columns = new ArrayList<String>();
                columns.add(column);
                schemaTable2Columns.put((tableFullName), columns);
            } else {
                List<String> tablesList = schema2Tables.get(schemaName);
                if (!tablesList.contains(tableName) && !("".equals(tableName))) {
                    tablesList.add(tableName);
                    List<String> columns = new ArrayList<String>();
                    columns.add(column);
                    schemaTable2Columns.put((tableFullName), columns);
                } else {
                    ArrayList<String> columnList = (ArrayList<String>) schemaTable2Columns.get(tableFullName);
                    if (!columnList.contains(column) && !("".equals(column))) {
                        columnList.add(column);
                        schemaTable2Columns.put((tableFullName), columnList);
                    }
                }
            }
        }
        String[] schema1Names = schemaList.toArray(new String[0]);
        schema1Combo.setItems(schema1Names);
        schema2Combo.setItems(schema1Names);
    }

    /**
     * Display view body
     *
     * @param List<ViewBody> the view body list
     */
    public void displayViewBody(List<ViewBody> viewBodyList) {
        for (int i = 0; i < viewBodyList.size(); i++) {
            TableItem item = new TableItem(viewBodyTable, SWT.NONE);
            ViewBody viewBody = viewBodyList.get(i);
            item.setText(new String[] {viewBody.getSchemaName(), viewBody.getTableName(),
                    viewBody.getColumnName(), viewBody.getColumnAlias()});
            editViewBodyData(viewBody);
        }
    }

    /**
     * Display table alias
     *
     * @param List<TableAlias> the table alias list
     */
    public void displayTableAlias(List<TableAlias> tableAliasList) {
        for (int i = 0; i < tableAliasList.size(); i++) {
            TableItem item = new TableItem(tableAliasTable, SWT.NONE);
            TableAlias tableAlias = tableAliasList.get(i);
            item.setText(new String[]{tableAlias.getTableFullName(), tableAlias.getTableAliasName()});
        }
    }

    /**
     * Display where condition
     *
     * @param List<WhereCondition> the where condition list
     */
    public void displayWhereCondition(List<WhereCondition> whereConditionList) {
        for (int i = 0; i < whereConditionList.size(); i++) {
            TableItem item = new TableItem(whereTable, SWT.NONE);
            WhereCondition whereCondition = whereConditionList.get(i);
            String leftExpression = whereCondition.getLeftExpression();
            String rightExpression = whereCondition.getRightExcepssion();
            item.setText(new String[] {leftExpression, rightExpression});
            updateWhereConditionListOnly(leftExpression, rightExpression);
        }
    }

    /**
     * Sets schema name list
     */
    public void setSchemaNameList() {
        List<String> schemaNameList = createViewRelyInfo.getAllSchemas();
        String[] schemaNames = schemaNameList.toArray(new String[0]);
        schemaCombo.setItems(schemaNames);
    }

    /**
     * Update table list
     */
    public void updateTableList() {
        List<String> tableNameList = createViewRelyInfo.getAllTablesBySchema(schema);
        String[] tableNames = tableNameList.toArray(new String[0]);
        tableCombo.setItems(tableNames);
    }

    /**
     * Update column list
     */
    public void updateColumnList() {
        List<String> columnNameList = createViewRelyInfo.getAllColumnsByTable(schema, table);
        String[] columnNames = columnNameList.toArray(new String[0]);
        columnCombo.setItems(columnNames);
    }

    /**
     * Gets view ddl
     *
     * @return String the view ddl
     */
    public String getViewDdl() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("CREATE OR REPLACE VIEW ");
        sb.append(createViewRelyInfo.getFixedSchemaName() + ".");
        String viewName = viewNameText.getText();
        sb.append(viewName);
        sb.append(System.lineSeparator()).append("AS").append(System.lineSeparator());
        sb.append(getViewBodyDdl());
        sb.append(System.lineSeparator()).append("FROM").append(System.lineSeparator());
        sb.append(getTableDdl());
        sb.append(getConditionDdl());
        sb.append(";");
        return sb.toString();
    }

    /**
     * Gets view body ddl
     *
     * @return String the view body ddl
     */
    public String getViewBodyDdl() {
        TableItem[] tableItems = viewBodyTable.getItems();
        TableItem tableItem;
        StringBuilder sb = new StringBuilder(128);
        sb.append("SELECT").append(System.lineSeparator());
        for (int i = 0; i < tableItems.length; i++) {
            sb.append("\t");
            tableItem = tableItems[i];
            String tableFullName = tableItem.getText(0) + "." + tableItem.getText(1);
            sb.append(tableAliasMap.getOrDefault(tableFullName, tableFullName));
            sb.append(".");
            sb.append(tableItem.getText(2));
            if (!"".equals(tableItem.getText(3))) {
                sb.append(" AS " + tableItem.getText(3));
            }
            sb.append(",").append(System.lineSeparator());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * Gets table ddl
     *
     * @return String the table ddl
     */
    public String getTableDdl() {
        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < schemaList.size(); i++) {
            String schemaName = schemaList.get(i);
            List<String> tables = schema2Tables.get(schemaName);
            for (int j = 0; j < tables.size(); j++) {
                String tableName = tables.get(j);
                String tableFullName = schemaName + "." + tableName;
                String alias = tableAliasMap.getOrDefault(tableFullName, "");
                String tableReferenceName = "".equals(alias) ? tableFullName : tableFullName + " " + alias;
                sb.append("\t" + tableReferenceName + "," + System.lineSeparator());
            }
        }
        if (sb.toString().length() >= 3) {
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Gets table alias in condition
     *
     * @param String the column
     * @return String the table alias
     */
    public String getTableAliasInCondition (String column) {
        int index = column.lastIndexOf(".");
        if (index <= 1) {
            return column;
        } else {
            String fullName = column.substring(0, index);
            String alias = tableAliasMap.getOrDefault(fullName, "");
            String condition = "".equals(alias) ? column : alias + column.substring(index);
            return condition;
        }
    }

    /**
     * Gets condition ddl
     *
     * @return String the table ddl
     */
    public String getConditionDdl() {
        StringBuilder sb = new StringBuilder(128);
        TableItem[] tableItems = whereTable.getItems();
        if (tableItems == null || tableItems.length == 0) {
            return "";
        }
        sb.append(System.lineSeparator()).append("WHERE").append(System.lineSeparator());
        for (int i = 0; i < tableItems.length; i++) {
            TableItem tableItem = tableItems[i];
            sb.append("\t");
            sb.append(getTableAliasInCondition(tableItem.getText(0)));
            sb.append(" = ");
            sb.append(getTableAliasInCondition(tableItem.getText(1)));
            sb.append(" and").append(System.lineSeparator());
        }
        String resultString = sb.toString();
        return resultString.substring(0, resultString.length() - 6);
    }

    /**
     * Delete view body data
     */
    public void deleteViewBodyData() {
        int rowNum = viewBodyTable.getSelectionIndex();
        if (rowNum != -1) {
            TableItem tableItem = viewBodyTable.getItem(rowNum);
            String schemaName = tableItem.getText(0);
            String tableName = tableItem.getText(1);
            String column = tableItem.getText(2);
            String tableFullName = schemaName + "." + tableName;
            List<String> columnList = schemaTable2Columns.get(tableFullName);
            if (columnList.size() == 1) {
                schemaTable2Columns.remove(tableFullName);
                List<String> tableList = schema2Tables.get(schemaName);
                if (tableList.size() == 1) {
                    schema2Tables.remove(schemaName);
                    schemaList.remove(schemaName);
                    TableItem[] tableAliasTableItems = tableAliasTable.getItems();
                    for (int i = 0; i < tableAliasTableItems.length; i++) {
                        if (tableAliasTableItems[i].getText(0).equals(tableFullName)) {
                            tableAliasTable.remove(i);
                        }
                    }
                } else {
                    tableList.remove(tableName);
                    TableItem[] tableAliasTableItems = tableAliasTable.getItems();
                    for (int i = 0; i < tableAliasTableItems.length; i++) {
                        if (tableAliasTableItems[i].getText(0).equals(tableFullName)) {
                            tableAliasTable.remove(i);
                        }
                    }
                    schema2Tables.put(schemaName, tableList);
                }
            } else {
                columnList.remove(column);
                schemaTable2Columns.put(tableFullName, columnList);
            }
            viewBodyTable.remove(rowNum);
        }
        String[] schema1Names = schemaList.toArray(new String[0]);
        schema1Combo.setItems(schema1Names);
        schema2Combo.setItems(schema1Names);
    }

    /**
     * Constructs create view data model
     *
     * @return CreateViewDataModel create view data model
     */
    public CreateViewDataModel constructCreateViewDataModel () {
        List<ViewBody> viewBodyList = new ArrayList<ViewBody>();
        TableItem[] tableItem1 = viewBodyTable.getItems();
        for (int i = 0; i < tableItem1.length; i++) {
            viewBodyList.add(new ViewBody(tableItem1[i].getText(0), tableItem1[i].getText(1),
                    tableItem1[i].getText(2), tableItem1[i].getText(3)));
        }

        List<TableAlias> tableAliasList = new ArrayList<TableAlias>();
        TableItem[] tableItem2 = tableAliasTable.getItems();
        for (int i = 0; i < tableItem2.length; i++) {
            tableAliasList.add(new TableAlias(tableItem2[i].getText(0), tableItem2[i].getText(1)));
        }

        List<WhereCondition> whereCondition = new ArrayList<WhereCondition>();
        TableItem[] tableItem3 = whereTable.getItems();
        for (int i = 0; i < tableItem3.length; i++) {
            whereCondition.add(new WhereCondition(tableItem3[i].getText(0), tableItem3[i].getText(1)));
        }

        return new CreateViewDataModel(viewBodyList, tableAliasList, whereCondition);
    }

    /**
     * Insert view body data
     */
    public void insertViewBodyData() {
        String schemaName = schemaCombo.getText();
        String tableName = tableCombo.getText();
        String column = columnCombo.getText();
        String tableFullName = schemaName + "." + tableName;
        if (!("".equals(schemaName) || "".equals(tableName) || "".equals(column))) {
            if (!schemaList.contains(schemaName)) {
                schemaList.add(schemaName);
                List<String> tables = new ArrayList<String>();
                tables.add(tableName);
                schema2Tables.put(schemaName, tables);
                List<String> columns = new ArrayList<String>();
                columns.add(column);
                schemaTable2Columns.put((tableFullName), columns);
                TableItem aliasTableitem = new TableItem(tableAliasTable, SWT.NONE);
                aliasTableitem.setText(new String[] { tableFullName, "" });
                TableItem item = new TableItem(viewBodyTable, SWT.NONE);
                item.setText(new String[] { schemaName, tableName, column });
            } else {
                List<String> tablesList = schema2Tables.get(schemaName);
                if (!tablesList.contains(tableName) && !("".equals(tableName))) {
                    tablesList.add(tableName);
                    List<String> columns = new ArrayList<String>();
                    columns.add(column);
                    schemaTable2Columns.put((tableFullName), columns);
                    TableItem aliasTableitem = new TableItem(tableAliasTable, SWT.NONE);
                    aliasTableitem.setText(new String[] { tableFullName, "" });
                    TableItem item = new TableItem(viewBodyTable, SWT.NONE);
                    item.setText(new String[] { schemaName, tableName, column });
                } else {
                    ArrayList<String> columnList = (ArrayList<String>) schemaTable2Columns.get(tableFullName);
                    if (!columnList.contains(column) && !("".equals(column))) {
                        columnList.add(column);
                        schemaTable2Columns.put((tableFullName), columnList);
                        TableItem item = new TableItem(viewBodyTable, SWT.NONE);
                        item.setText(new String[] { schemaName, tableName, column });
                    }
                }
            }
        }
        String[] schema1Names = schemaList.toArray(new String[0]);
        schema1Combo.setItems(schema1Names);
        schema2Combo.setItems(schema1Names);
    }

    /**
     * Add where condition data
     */
    public void addWhereConditionData() {
        String column1 = schema1Combo.getText() + "." + table1Combo.getText() + "." + column1Combo.getText();
        String column2 = schema2Combo.getText() + "." + table2Combo.getText() + "." + column2Combo.getText();
        updateWhereConditionList(column1, column2);
    }

    /**
     * Update where condition list
     *
     * @param String the column1
     * @param String the column2
     */
    public void updateWhereConditionList(String column1, String column2) {
        if (!("..".equals(column1) && "..".equals(column2))) {
            if (!whereConditionList.containsKey(column1)) {
                List<String> column2Condition = new ArrayList<String>();
                column2Condition.add(column2);
                whereConditionList.put(column1, column2Condition);
                TableItem tableItem = new TableItem(whereTable, SWT.NONE);
                tableItem.setText(new String[] { column1, column2 });
            } else {
                List<String> column2Condition = whereConditionList.get(column1);
                if (!column2Condition.contains(column2)) {
                    column2Condition.add(column2);
                    whereConditionList.put(column1, column2Condition);
                    TableItem tableItem = new TableItem(whereTable, SWT.NONE);
                    tableItem.setText(new String[] { column1, column2 });
                }
            }
        }
    }

    /**
     * Update where condition list only
     *
     * @param String the column1
     * @param String the column2
     */
    public void updateWhereConditionListOnly(String column1, String column2) {
        if (!("..".equals(column1) && "..".equals(column2))) {
            if (!whereConditionList.containsKey(column1)) {
                List<String> column2Condition = new ArrayList<String>();
                column2Condition.add(column2);
                whereConditionList.put(column1, column2Condition);
            } else {
                List<String> column2Condition = whereConditionList.get(column1);
                if (!column2Condition.contains(column2)) {
                    column2Condition.add(column2);
                    whereConditionList.put(column1, column2Condition);
                }
            }
        }
    }

    /**
     * Delete where condition data
     *
     * @param int the row num
     */
    public void deleteWhereConditionData(int rowNum) {
        TableItem tableItem = whereTable.getItem(rowNum);
        String leftExpression = tableItem.getText(0);
        List<String> rightExpression = whereConditionList.get(leftExpression);
        if (rightExpression.size() == 1) {
            whereConditionList.remove(leftExpression);
        } else {
            rightExpression.remove(tableItem.getText(1));
            whereConditionList.put(leftExpression, rightExpression);
        }
    }

    /**
     * Delete where condition data
     *
     * @param String the left expression
     * @param String the right expression
     */
    public void deleteWhereConditionData(String leftExpre, String rigthExpre) {
        List<String> rightExpression = whereConditionList.get(leftExpre);
        if (rightExpression.size() == 1) {
            whereConditionList.remove(leftExpre);
        } else {
            rightExpression.remove(rigthExpre);
            whereConditionList.put(leftExpre, rightExpression);
        }
    }

    /**
     * Gets table alias
     */
    public void getTableAlias() {
        TableItem[] tableItems = tableAliasTable.getItems();
        if (tableItems == null || tableItems.length == 0) {
            return;
        }
        for (int i = 0; i < tableItems.length; i++) {
            if (!"".equals(tableItems[i].getText(1))) {
                tableAliasMap.put(tableItems[i].getText(0), tableItems[i].getText(1));
            }
        }
    }
}