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

package org.opengauss.mppdbide.view.ui.terminal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import org.opengauss.mppdbide.bl.serverdatacache.ColumnMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.ITableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.view.utils.consts.UIConstants;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnsListDialog.
 *
 * @since 3.0.0
 */
public class ColumnsListDialog extends Dialog {

    private Text tableNameText;
    private Button checkButton;
    private Button selectAllBtn;
    private Button clearAllBtn;
    private Label lblTableName;
    private Table colTable;
    private TableMetaData table;
    private String tableName;
    private List<ColumnMetaData> columnMetadaList;
    private String[] colNames;
    private String[] datatypes;
    private List<String> userSelectedColNames;
    private List<Button> chkBtnList;
    private boolean okPressedFlag = false;
    private Button okBtn;
    private Button cancelBtn;

    /**
     * Instantiates a new columns list dialog.
     *
     * @param parentShell the parent shell
     * @param table the table
     * @param tableName the table name
     * @param columnNames the column names
     * @param columnDataTypeNames the column data type names
     */
    public ColumnsListDialog(Shell parentShell, ITableMetaData table, String tableName, List<String> columnNames,
            List<String> columnDataTypeNames) {
        super(parentShell);
        if (table instanceof TableMetaData) {
            this.table = (TableMetaData) table;
        }
        this.tableName = tableName;
        if (table == null) {
            this.colNames = new String[columnNames.size()];
            this.datatypes = new String[columnNames.size()];
            this.colNames = columnNames.toArray(new String[0]);
            this.datatypes = columnDataTypeNames.toArray(new String[0]);
        } else {
            if (table instanceof TableMetaData) {
                columnMetadaList = this.table.getColumnMetaDataList();
                colNames = new String[columnMetadaList.size()];
                datatypes = new String[columnMetadaList.size()];
            }
        }
        userSelectedColNames = new ArrayList<String>();
        chkBtnList = new ArrayList<Button>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);

    }

    /**
     * Configure shell.
     *
     * @param newShell the new shell
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(MessageConfigLoader.getProperty(IMessagesConstants.DEFINE_UNIQUE_KEY_TITLE));
        newShell.setImage(IconUtility.getIconImage(IiconPath.ICO_COLUMN, this.getClass()));
    }

    /**
     * Creates the dialog area.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createDialogArea(Composite parent) {

        int columnCount = 0;
        // composite for the table name label and table name textbox
        Composite tableNameComposite = getTableNameComposite(parent);

        addTableNameUi(tableNameComposite);
        // Group to contain the tableviewer
        Group columnGrp = getClmGroupUi(parent);
        // tableviewer to shows the column names and datatypes
        Composite tableViewerComp = getTableViewerComposite(columnGrp);

        getTableViewerUi(tableViewerComp);

        // getting the list of columns in the tables
        getListOfClmsInTable(columnCount);

        // column names and datatypes of a table is send to prepare the
        // tableviewer
        createTableItems(colNames, datatypes, colTable);
        colTable.pack();
        // composite to contain the buttons of the group to select all the check
        // box or clear all the checkbox
        addDialogButtonsUi(columnGrp);
        return parent;
    }

    private void addDialogButtonsUi(Group columnGrp) {
        Composite grpButtonsComp = new Composite(columnGrp, SWT.NONE);

        GridData grpButtonsGridData = new GridData();

        grpButtonsGridData.horizontalIndent = 150;

        grpButtonsComp.setLayout(new GridLayout(2, false));
        grpButtonsComp.setLayoutData(grpButtonsGridData);

        selectAllBtn = new Button(grpButtonsComp, SWT.NONE);
        selectAllBtn
                .setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_SELECT_ALL_BTN_LABEL));
        selectAllBtn.addSelectionListener(new SelectAllBtnSelectionListener());

        clearAllBtn = new Button(grpButtonsComp, SWT.NONE);
        clearAllBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_CLEAR_ALL_BTN_LABEL));
        clearAllBtn.addSelectionListener(new ClearAllBtnSelectionListener());
    }

    private void getTableViewerUi(Composite tableViewerComp) {
        TableViewer viewer = new TableViewer(tableViewerComp,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

        viewer.setContentProvider(new ArrayContentProvider());
        colTable = viewer.getTable();
        colTable.setVisible(true);
        colTable.setHeaderVisible(true);
        colTable.setSize(200, 200);
        GridData colTablegridData = new GridData();
        colTablegridData.verticalAlignment = GridData.FILL;
        colTablegridData.grabExcessHorizontalSpace = false;
        colTablegridData.grabExcessVerticalSpace = true;
        colTablegridData.horizontalAlignment = GridData.FILL;
        colTable.setLayoutData(colTablegridData);
        // first column of the tableviewer
        TableColumn checkBoxColumn = new TableColumn(colTable, SWT.NONE);
        checkBoxColumn.setText("");
        checkBoxColumn.setWidth(20);

        // second column of the tableviewer
        TableColumn colNameColumn = new TableColumn(colTable, SWT.NONE);
        colNameColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_COLNAME_HEADER));
        colNameColumn.setWidth(130);

        // third column of the tableviewer
        TableColumn dataTypeColumn = new TableColumn(colTable, SWT.NONE);
        dataTypeColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_DATATYPE_HEADER));
        dataTypeColumn.setWidth(130);

        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        viewer.getControl().setLayoutData(gridData);
    }

    private Composite getTableViewerComposite(Group columnGrp) {
        Composite tableViewerComp = new Composite(columnGrp, SWT.NONE);
        GridData tableviewerGrpData = new GridData();
        tableviewerGrpData.grabExcessHorizontalSpace = false;
        tableviewerGrpData.grabExcessVerticalSpace = true;
        tableviewerGrpData.horizontalAlignment = GridData.FILL;
        tableviewerGrpData.verticalAlignment = GridData.FILL;
        tableviewerGrpData.horizontalIndent = 5;
        tableviewerGrpData.verticalIndent = 10;
        tableviewerGrpData.minimumWidth = 300;
        tableviewerGrpData.heightHint = 200;
        tableViewerComp.setLayoutData(tableviewerGrpData);
        tableViewerComp.setLayout(new GridLayout());
        return tableViewerComp;
    }

    private Group getClmGroupUi(Composite parent) {
        Group columnGrp = new Group(parent, SWT.NONE);
        GridData columnGrpData = new GridData();
        columnGrpData.grabExcessHorizontalSpace = false;
        columnGrpData.grabExcessVerticalSpace = true;
        columnGrpData.horizontalAlignment = GridData.FILL;
        columnGrpData.verticalAlignment = GridData.FILL;
        columnGrpData.horizontalIndent = 5;
        columnGrpData.verticalIndent = 10;
        columnGrpData.minimumWidth = 300;
        columnGrpData.minimumHeight = 300;
        columnGrp.setText(MessageConfigLoader.getProperty(IMessagesConstants.GROUP_COLUMNS_HEADER));
        columnGrp.setLayoutData(columnGrpData);
        columnGrp.setLayout(new GridLayout());
        return columnGrp;
    }

    private void addTableNameUi(Composite tableNameComposite) {
        lblTableName = new Label(tableNameComposite, SWT.NONE);
        lblTableName.setText(MessageConfigLoader.getProperty(IMessagesConstants.UNIQUE_CONSTRAINT_TABLE_LABEL) + ':');
        tableNameText = new Text(tableNameComposite, SWT.READ_ONLY | SWT.BORDER);
        GridData tableNameTextGridData = new GridData();
        tableNameTextGridData.widthHint = 200;
        tableNameText.setLayoutData(tableNameTextGridData);
        if (table != null) {
            tableNameText.setText(table.getDisplayName());
        } else {
            tableNameText.setText(this.tableName);
        }

    }

    private Composite getTableNameComposite(Composite parent) {
        Composite tableNameComposite = (Composite) super.createDialogArea(parent);

        GridLayout tableInfoLayout = new GridLayout();
        tableInfoLayout.numColumns = 2;
        tableInfoLayout.makeColumnsEqualWidth = false;
        tableInfoLayout.horizontalSpacing = 20;

        GridData tableInfogridData = getTableInfoGridData();

        tableNameComposite.setLayout(tableInfoLayout);
        tableNameComposite.setLayoutData(tableInfogridData);
        return tableNameComposite;
    }

    private GridData getTableInfoGridData() {
        GridData tableInfogridData = new GridData();
        tableInfogridData.grabExcessHorizontalSpace = true;
        tableInfogridData.horizontalAlignment = GridData.FILL;
        tableInfogridData.verticalAlignment = GridData.FILL;
        tableInfogridData.horizontalIndent = 5;
        tableInfogridData.verticalIndent = 10;
        tableInfogridData.minimumWidth = 200;
        return tableInfogridData;
    }

    private void getListOfClmsInTable(int columnCount) {
        if (table != null) {
            columnMetadaList = this.table.getColumnMetaDataList();
            for (ColumnMetaData col : columnMetadaList) {
                colNames[columnCount] = col.getName();
                datatypes[columnCount] = col.getDataType().getName();
                columnCount++;
            }
        }
    }

    /**
     * The listener interface for receiving selectAllBtnSelection events. The
     * class that is interested in processing a selectAllBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addSelectAllBtnSelectionListener<code> method. When the
     * selectAllBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * SelectAllBtnSelectionEvent
     */
    private class SelectAllBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {

            int size = chkBtnList.size();
            for (int i = 0; i < size; i++) {
                chkBtnList.get(i).setSelection(true);
                userSelectedColNames.add(colTable.getItem(i).getText(1));

            }
            okBtn.setEnabled(true);

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * The listener interface for receiving clearAllBtnSelection events. The
     * class that is interested in processing a clearAllBtnSelection event
     * implements this interface, and the object created with that class is
     * registered with a component using the component's
     * <code>addClearAllBtnSelectionListener<code> method. When the
     * clearAllBtnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * ClearAllBtnSelectionEvent
     */
    private class ClearAllBtnSelectionListener implements SelectionListener {

        @Override
        public void widgetSelected(SelectionEvent e) {

            int size = chkBtnList.size();
            for (int i = 0; i < size; i++) {
                chkBtnList.get(i).setSelection(false);
                userSelectedColNames.remove(colTable.getItem(i).getText(1));
                okBtn.setEnabled(false);

            }

        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }

    }

    /**
     * Creates the buttons for button bar.
     *
     * @param prnt the prnt
     */
    @Override
    protected void createButtonsForButtonBar(Composite prnt) {
        String cancelLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC)
                + "     ";
        String okLbl = "     " + MessageConfigLoader.getProperty(IMessagesConstants.EXEC_PLAN_OK) + "     ";

        okBtn = createButton(prnt, UIConstants.OK_ID, okLbl, true);
        okBtn.setEnabled(false);
        cancelBtn = createButton(prnt, UIConstants.CANCEL_ID, cancelLbl, false);
        cancelBtn.setEnabled(true);
    }

    /**
     * Gets the user selected column names.
     *
     * @return the user selected column names
     */
    public List<String> getUserSelectedColumnNames() {
        return new ArrayList<String>(userSelectedColNames);
    }

    private void createTableItems(String[] columnNames, String[] arrDatatypes, Table tbl) {
        TableItem item = null;
        TableEditor editor = null;

        for (int i = 0; i < columnNames.length; i++) {
            item = new TableItem(tbl, SWT.NONE);
            editor = new TableEditor(tbl);
            checkButton = new Button(tbl, SWT.CHECK);

            checkButton.pack();
            chkBtnList.add(checkButton);
            registerCheckBtnListener(checkButton, item);
            editor.minimumWidth = checkButton.getSize().x;
            editor.horizontalAlignment = SWT.LEFT;
            editor.setEditor(checkButton, item, 0);

            item.setText(1, columnNames[i]);
            item.setText(2, arrDatatypes[i]);

        }

    }

    /**
     * Ok pressed.
     */
    @Override
    protected void okPressed() {
        this.okPressedFlag = true;
        super.okPressed();

    }

    /**
     * Gets the ok pressed.
     *
     * @return the ok pressed
     */
    public boolean getOkPressed() {
        return this.okPressedFlag;
    }

    /**
     * Button pressed.
     *
     * @param buttonId the button id
     */
    @Override
    protected void buttonPressed(int buttonId) {
        if (UIConstants.OK_ID == buttonId) {
            okPressed();
        } else if (UIConstants.CANCEL_ID == buttonId) {
            cancelPressed();
        }
    }

    private void registerCheckBtnListener(Button checkBtn, TableItem item) {
        checkBtn.addSelectionListener(new CheckButtonListener(checkBtn, item));
    }

    /**
     * The listener interface for receiving checkButton events. The class that
     * is interested in processing a checkButton event implements this
     * interface, and the object created with that class is registered with a
     * component using the component's <code>addCheckButtonListener<code>
     * method. When the checkButton event occurs, that object's appropriate
     * method is invoked.
     *
     * CheckButtonEvent
     */
    private final class CheckButtonListener implements SelectionListener {
        private Button chkBtn;
        private TableItem tableItem;

        private CheckButtonListener(Button btn, TableItem item) {
            this.chkBtn = btn;
            this.tableItem = item;
        }

        @Override
        public void widgetSelected(SelectionEvent e) {
            if (chkBtn.getSelection()) {
                userSelectedColNames.add(tableItem.getText(1));
            } else {
                userSelectedColNames.remove(tableItem.getText(1));
            }
            if (userSelectedColNames.size() > 0) {
                okBtn.setEnabled(true);
            } else {
                okBtn.setEnabled(false);
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {
            // Ignore. Nothing to do.
        }

    }
}
