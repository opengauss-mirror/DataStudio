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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.exportdata.AbstractImportExportDataCore;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * The Class ColumnComponent.
 *
 * @ClassName: ColumnComponent
 * @Description: The Class ColumnComponent
 *
 * @since 3.0.0
 */
public class ColumnComponent extends Dialog {

    private Table tblAvailCols;
    private Button addToColumn;
    private Button removeFromColumn;
    private Button moveUp;
    private Button moveDown;
    private Button allColumns;
    private Table tblSelectedCols;
    private ArrayList<String> availColsList;
    private ArrayList<String> selectedColsList;
    private TableColumn tblclmnAvailableColumns;
    private TableColumn tblclmnSelectedColumns;

    /**
     * The import export data core.
     */
    protected AbstractImportExportDataCore importExportDataCore;
    private Button OKButton;
    private static volatile Button agreementBtn;
    private static volatile Text fileNameToBeExporte;
    private static volatile Text outputFolder;
    private static volatile Text txtUserExpr;

    /**
     * Instantiates a new column component.
     *
     * @param parent the parent
     * @param core the core
     */
    public ColumnComponent(Composite parent, AbstractImportExportDataCore core) {
        super(parent.getShell());
        importExportDataCore = core;
        setDefaultImage(getWindowImage());
        availColsList = new ArrayList<String>(4);
        selectedColsList = new ArrayList<String>(4);
    }

    /**
     * Creates the components.
     *
     * @param parent the parent
     */
    public void createComponents(Composite parent) {

        // List of column details
        Group group = new Group(parent, SWT.NONE);

        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        addDefaultColumnSelectionUI(group);

        groupBodyComponent(group);

    }

    private void addMessageForSelectedColumns(Composite maincomp) {
        Label lblNotice = new Label(maincomp, SWT.WRAP);

        StringBuilder notice = new StringBuilder(
                MessageConfigLoader.getProperty(IMessagesConstants.DESCRIBE_IMPACT_OF_COLUMN_SEQUENCE));
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        lblNotice.setText(notice.toString());
        lblNotice.setLayoutData(gridData);
    }

    private void groupBodyComponent(Group group) {
        GridLayout gridLayout;
        Composite grpBodyComposite = new Composite(group, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        gridLayout.makeColumnsEqualWidth = false;
        grpBodyComposite.setLayout(gridLayout);

        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.heightHint = 200;
        grpBodyComposite.setLayoutData(gridData);
        createAvailableCols(grpBodyComposite);

        Composite grpButton1Composite = new Composite(grpBodyComposite, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        grpButton1Composite.setLayout(gridLayout);
        createAddColButton(grpButton1Composite);
        createRemoveColButton(grpButton1Composite);

        createSelectedCols(grpBodyComposite);

        Composite grpButton2Composite = new Composite(grpBodyComposite, SWT.NONE);
        gridLayout = new GridLayout();
        gridLayout.numColumns = 1;
        grpButton2Composite.setLayout(gridLayout);
        createColMoveUp(grpButton2Composite);
        createColMoveDown(grpButton2Composite);
    }

    private void addDefaultColumnSelectionUI(Group group) {
        GridLayout gridLayout;
        Composite grpHeadComposite = new Composite(group, SWT.NONE);

        gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.makeColumnsEqualWidth = true;
        grpHeadComposite.setLayout(gridLayout);
        grpHeadComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        if (!importExportDataCore.getImportExportoptions().isExport()) {
            addMessageForSelectedColumns(grpHeadComposite);
        }

        Label lblUserDefinedExpression = new Label(grpHeadComposite, SWT.NONE);
        lblUserDefinedExpression.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_SELECTCOLUMNS));
        lblUserDefinedExpression.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

        allColumns = new Button(grpHeadComposite, SWT.CHECK);
        allColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_ALLCOLUMNS));
        allColumns.setSelection(true);
        allColumns.addSelectionListener(new AllColumnSelectionListener());
        allColumns.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
    }

    /**
     * The listener interface for receiving allColumnSelection events. The class
     * that is interested in processing a allColumnSelection event implements
     * this interface, and the object created with that class is registered with
     * a component using the component's
     * <code>addAllColumnSelectionListener<code> method. When the
     * allColumnSelection event occurs, that object's appropriate method is
     * invoked.
     *
     * AllColumnSelectionEvent
     */
    private class AllColumnSelectionListener implements SelectionListener {
        @Override
        public void widgetSelected(SelectionEvent e) {
            if (!allColumns.getSelection()) {
                addToColumn.setEnabled(true);
                removeFromColumn.setEnabled(true);
                moveUp.setEnabled(true);
                moveDown.setEnabled(true);
            } else {
                addToColumn.setEnabled(false);
                removeFromColumn.setEnabled(false);
                moveDown.setEnabled(false);
                moveUp.setEnabled(false);
                moveAllColumnToSelected();
            }
            if (importExportDataCore.getImportExportoptions().isExport()) {
                verificateOKButtonForExport();
            } else {
                verificateOKButtonForImport();
            }
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent e) {

        }
    }

    private void createSelectedCols(Composite comp) {
        tblSelectedCols = new Table(comp, SWT.BORDER);
        tblSelectedCols.setLinesVisible(true);
        tblSelectedCols.setHeaderVisible(true);

        tblclmnSelectedColumns = new TableColumn(tblSelectedCols, SWT.NONE);
        tblclmnSelectedColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_SELECTED_COL));
        displayAvailableColumns();
        tblclmnSelectedColumns.pack();
        tblSelectedCols.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    }

    private void createAvailableCols(Composite comp) {
        tblAvailCols = new Table(comp, SWT.BORDER);
        tblAvailCols.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tblAvailCols.setHeaderVisible(true);
        tblAvailCols.setLinesVisible(true);
        tblclmnAvailableColumns = new TableColumn(tblAvailCols, SWT.NONE);
        tblclmnAvailableColumns.setText(MessageConfigLoader.getProperty(IMessagesConstants.OPTION_AVAILABLE_COL));
        tblclmnAvailableColumns.pack();
    }

    private void displayAvailableColumns() {
        TableItem item = null;
        List<String> clmList;
        ServerObject serverObject = importExportDataCore.getImportExportServerObj();
        if (serverObject instanceof TableMetaData) {
            clmList = sortColumns(((TableMetaData) serverObject).getColumnMetaDataList());
        } else {
            clmList = importExportDataCore.getColumns();
        }

        if (null != clmList && clmList.size() > 0) {
            for (String clmName : clmList) {
                item = new TableItem(tblSelectedCols, SWT.NONE);
                item.setText(clmName);
                selectedColsList.add(clmName);
            }
        }
    }

    /**
     * Sort the Columns
     * 
     * @param columnMetaDataList List<ColumnMetaData>
     * @return column name
     */
    private List<String> sortColumns(List<ColumnMetaData> columnMetaDataList) {
        ColumnMetaData temp = null;
        for (int index = 0; index < columnMetaDataList.size() - 1; index++) {
            for (int ix = columnMetaDataList.size() - 1; ix > index; ix--) {
                if (columnMetaDataList.get(ix - 1).getOid() > columnMetaDataList.get(ix).getOid()) {
                    temp = columnMetaDataList.get(ix - 1);
                    columnMetaDataList.set(ix - 1, columnMetaDataList.get(ix));
                    columnMetaDataList.set(ix, temp);
                }
            }
        }

        List<String> clmList = new ArrayList<>();
        for (ColumnMetaData columnMetaData : columnMetaDataList) {
            clmList.add(columnMetaData.getName());
        }
        return clmList;
    }

    private void createAddColButton(Composite comp) {
        addToColumn = new Button(comp, SWT.ARROW | SWT.RIGHT);
        addToColumn.setEnabled(false);
        addToColumn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedIdx = tblAvailCols.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = availColsList.get(selectedIdx);
                    availColsList.remove(selectedIdx);
                    tblAvailCols.remove(selectedIdx);
                    selectedColsList.add(colName);
                    repopulateSelectedcolumns();
                    packColumn();
                    if (importExportDataCore.getImportExportoptions().isExport()) {
                        verificateOKButtonForExport();
                    } else {
                        verificateOKButtonForImport();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        });
    }

    /**
     * Pack column.
     */
    protected void packColumn() {
        tblclmnSelectedColumns.pack();
        tblclmnAvailableColumns.pack();
    }

    private void createRemoveColButton(Composite comp) {
        removeFromColumn = new Button(comp, SWT.ARROW | SWT.LEFT);
        removeFromColumn.setEnabled(false);
        removeFromColumn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedIdx = tblSelectedCols.getSelectionIndex();
                if (selectedIdx > -1) {
                    String colName = selectedColsList.get(selectedIdx);
                    selectedColsList.remove(selectedIdx);
                    tblSelectedCols.remove(selectedIdx);
                    availColsList.add(colName);
                    repopulateAvailablecolumns();
                    packColumn();
                    if (importExportDataCore.getImportExportoptions().isExport()) {
                        verificateOKButtonForExport();
                    } else {
                        verificateOKButtonForImport();
                    }
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do.
            }
        });
    }

    /**
     * Repopulate availablecolumns.
     */
    protected void repopulateAvailablecolumns() {
        TableItem item = null;
        tblAvailCols.removeAll();
        Collections.sort(availColsList, new ListComparator(importExportDataCore.getOriginalColumns()));
        Iterator<String> colsItr = this.availColsList.iterator();
        boolean hasNext = colsItr.hasNext();
        String col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(tblAvailCols, SWT.NONE);
            item.setText(col);
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Repopulate selectedcolumns.
     */
    protected void repopulateSelectedcolumns() {
        TableItem item = null;
        tblSelectedCols.removeAll();
        Iterator<String> colsItr = this.selectedColsList.iterator();
        boolean hasNext = colsItr.hasNext();
        String col = null;

        while (hasNext) {
            col = colsItr.next();
            item = new TableItem(tblSelectedCols, SWT.NONE);
            item.setText(col);
            hasNext = colsItr.hasNext();
        }
    }

    /**
     * Move all column to selected.
     */
    protected void moveAllColumnToSelected() {
        availColsList.clear();
        tblAvailCols.removeAll();
        selectedColsList.clear();
        tblSelectedCols.removeAll();
        displayAvailableColumns();
        repopulateAvailablecolumns();
        tblclmnAvailableColumns.pack();
    }

    private void createColMoveUp(Composite comp) {

        moveUp = new Button(comp, SWT.ARROW | SWT.UP);
        moveUp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        moveUp.setEnabled(false);
        moveUp.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String colName = "";
                int selectedIdx = tblSelectedCols.getSelectionIndex();
                if (selectedIdx > 0) {
                    colName = selectedColsList.get(selectedIdx);
                    selectedColsList.remove(selectedIdx);
                    selectedColsList.add(selectedIdx - 1, colName);
                    repopulateSelectedcolumns();
                }

                setSelectionOfMovedItem(colName);

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do
            }
        });
    }

    private void setSelectionOfMovedItem(String selectedColname) {
        TableItem[] items = tblSelectedCols.getItems();

        for (TableItem item : items) {

            if (item.getText().equals(selectedColname)) {
                tblSelectedCols.setSelection(item);
                break;
            }
        }

    }

    private void createColMoveDown(Composite comp) {
        moveDown = new Button(comp, SWT.ARROW | SWT.DOWN);
        moveDown.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        moveDown.setEnabled(false);
        moveDown.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int selectedIdx = tblSelectedCols.getSelectionIndex();
                String colName = "";
                if (selectedIdx > -1 && selectedIdx < (tblSelectedCols.getItemCount() - 1)) {
                    colName = selectedColsList.get(selectedIdx);
                    selectedColsList.remove(selectedIdx);
                    selectedColsList.add(selectedIdx + 1, colName);
                    repopulateSelectedcolumns();
                }
                setSelectionOfMovedItem(colName);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // Nothing to do

            }
        });
    }

    /**
     * Gets the window image.
     *
     * @return the window image
     */
    protected Image getWindowImage() {
        if (importExportDataCore.getImportExportServerObj() instanceof TableMetaData) {
            return IconUtility.getIconImage(IiconPath.ICO_EXPORTTABLE, this.getClass());
        } else {
            return IconUtility.getIconImage(IiconPath.ICO_EXPORT_ALL_DATA, this.getClass());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ListComparator.
     */
    private static final class ListComparator implements Serializable, Comparator<String> {
        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private List<String> referenceList = new ArrayList<String>(4);

        private ListComparator(ArrayList<String> selectedTable) {
            Iterator<String> colItr = selectedTable.iterator();
            boolean hasNext = colItr.hasNext();
            String column = null;
            while (hasNext) {
                column = colItr.next();
                referenceList.add(column);
                hasNext = colItr.hasNext();
            }
        }

        @Override
        public int compare(String col1, String col2) {
            int col1int = referenceList.indexOf(col1);
            int col2int = referenceList.indexOf(col2);
            if (col1int > col2int) {
                return 1;
            } else if (col1int < col2int) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    /**
     * Gets the all columns.
     *
     * @return the all columns
     */
    public Button getAllColumns() {
        return allColumns;
    }

    /**
     * Sets the all columns.
     *
     * @param allColumns the new all columns
     */
    public void setAllColumns(Button allColumns) {
        this.allColumns = allColumns;
    }

    /**
     * Gets the selected cols list.
     *
     * @return the selected cols list
     */
    public ArrayList<String> getSelectedColsList() {
        return selectedColsList;
    }

    /**
     * Sets the selected cols list.
     *
     * @param selectedColsList the new selected cols list
     */
    public void setSelectedColsList(ArrayList<String> selectedColsList) {
        this.selectedColsList = selectedColsList;
    }

    /**
     * Check selected columns.
     *
     * @return true, if successful
     */
    protected boolean checkSelectedColumns() {
        boolean isSelectedColumns = false;
        if (selectedColsList.size() > 0) {
            isSelectedColumns = true;
        }

        return isSelectedColumns;
    }

    /**
     * Checks if is ok button to enabled.
     *
     * @return true, if is ok button to enabled
     */
    private boolean isOkButtonToEnabledForExport() {
        if (null != agreementBtn && !agreementBtn.isDisposed()) {
            return agreementBtn.getSelection() && null != outputFolder && !outputFolder.isDisposed()
                    && !outputFolder.getText().isEmpty() && checkSelectedColumns();

        } else {
            return null != outputFolder && !outputFolder.isDisposed() && !outputFolder.getText().isEmpty()
                    && checkSelectedColumns();
        }
    }

    private void verificateOKButtonForExport() {
        if (isOkButtonToEnabledForExport()) {
            toggleOKButtons(true);
        } else {
            toggleOKButtons(false);
        }
    }

    /**
     * Toggle OK buttons.
     *
     * @param value the value
     */
    private void toggleOKButtons(boolean value) {
        if (!OKButton.isDisposed()) {
            OKButton.setEnabled(value);
        }

    }

    /**
     * Gets the OK btn.
     *
     * @return the OK btn
     */
    public Button getOKBtn() {
        return OKButton;
    }

    /**
     * Sets the OK btn.
     *
     * @param oKButton the new OK btn
     */
    public void setOKBtn(Button oKButton) {
        OKButton = oKButton;
    }

    /**
     * Gets the agreement btn.
     *
     * @return the agreement btn
     */
    public Button getAgreementBtn() {
        return agreementBtn;
    }

    /**
     * Sets the agreement btn.
     *
     * @param agreementButton the new agreement btn
     */
    public static void setAgreementBtn(Button agreementButton) {
        agreementBtn = agreementButton;
    }

    /**
     * Gets the file nametobe exporte.
     *
     * @return the file nametobe exporte
     */
    public Text getFileNametobeExporte() {
        return fileNameToBeExporte;
    }

    /**
     * Sets the file nametobe exporte.
     *
     * @param fileNametobeExporte the new file nametobe exporte
     */
    public static void setFileNametobeExporte(Text fileNametobeExporte) {
        fileNameToBeExporte = fileNametobeExporte;
    }

    /**
     * Output folder.
     *
     * @return the text
     */
    public Text outputFolder() {
        return outputFolder;
    }

    /**
     * Sets the output folder.
     *
     * @param folder the new output folder
     */
    public static void setOutputFolder(Text folder) {
        outputFolder = folder;
    }

    /**
     * Gets the txt user expr.
     *
     * @return the txt user expr
     */
    public static Text getTxtUserExpr() {
        return txtUserExpr;
    }

    /**
     * Sets the txt user expr.
     *
     * @param txtUserExpr the new txt user expr
     */
    public static void setTxtUserExpr(Text filePath) {
        txtUserExpr = filePath;
    }

    /**
     * verify OK button for import table data
     */
    private void verificateOKButtonForImport() {
        if (isOkButtonToEnabledForImport()) {
            toggleOKButtons(true);
        } else {
            toggleOKButtons(false);
        }
    }

    /**
     * Checks if is ok button to enabled.
     *
     * @return true, if is ok button to enabled
     */
    private boolean isOkButtonToEnabledForImport() {
        return null != txtUserExpr && !txtUserExpr.isDisposed() && !txtUserExpr.getText().isEmpty()
                && checkSelectedColumns();
    }
}
