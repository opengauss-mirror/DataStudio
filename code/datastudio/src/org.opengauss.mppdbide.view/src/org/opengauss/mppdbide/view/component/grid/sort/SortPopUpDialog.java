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

package org.opengauss.mppdbide.view.component.grid.sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.component.grid.core.GridSaveSortState;
import org.opengauss.mppdbide.view.component.grid.core.SortEntryData;
import org.opengauss.mppdbide.view.component.grid.sort.SortErrors.SORTERRORTYPE;
import org.opengauss.mppdbide.view.ui.table.IDialogWorkerInteraction;
import org.opengauss.mppdbide.view.utils.icon.IconUtility;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class SortPopUpDialog.
 *
 * @since 3.0.0
 */
public class SortPopUpDialog extends Dialog implements IDialogWorkerInteraction {
    private DataGrid grid;
    private Table table;
    private TableViewer viewer;

    private String[] colNames = null;
    private String[] dataTypes = null;
    private List<SortColumnSetting> sortColumnsModel = new ArrayList<SortColumnSetting>(1);
    private static final String[] COLUMN_HEADERS = {MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_PRIORITY),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_COLUMN_NAME),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_DATATYPE),
        MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_SORT_ORDER)};
    private TableViewerFocusCellManager focusCellManager;

    /**
     * Instantiates a new sort pop up dialog.
     *
     * @param grid the grid
     */
    public SortPopUpDialog(DataGrid grid) {
        super(grid.getDataGrid().getShell());
        this.grid = grid;
        this.colNames = grid.getDataProvider().getColumnDataProvider().getColumnNames();
        setDataTypes(grid.getDataProvider());
    }

    private void setDataTypes(IDSGridDataProvider dataProvider) {
        this.dataTypes = new String[this.colNames.length];
        for (int index = 0; index < this.colNames.length; index++) {
            this.dataTypes[index] = dataProvider.getColumnDataProvider().getColumnDataTypeName(index);
        }
    }

    /**
     * Sets the shell style.
     *
     * @param arg0 the new shell style
     */
    @Override
    protected void setShellStyle(int arg0) {
        super.setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | getDefaultOrientation() | SWT.RESIZE);
    }

    /**
     * Configure shell.
     *
     * @param shell the shell
     */
    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(MessageConfigLoader.getProperty(IMessagesConstants.MULTISORT_POPUP_WINDOW_TITLE));
        shell.setImage(IconUtility.getIconImage(IiconPath.ICO_SORT, this.getClass()));
    }

    private void swapItems(int selIndex, int otherIndex) {
        String selPriority = sortColumnsModel.get(selIndex).getPriority();
        sortColumnsModel.get(selIndex).setPriority(sortColumnsModel.get(otherIndex).getPriority());
        sortColumnsModel.get(otherIndex).setPriority(selPriority);

        Collections.swap(sortColumnsModel, selIndex, otherIndex);
        viewer.refresh();
    }

    @SuppressWarnings("rawtypes")
    private GridSaveSortState prepareSortSetting() {
        if (sortColumnsModel.size() == 0) {
            SortErrors.generateErrorDialog(SORTERRORTYPE.BLANK_COLUMN, new String[] {"1"});
            return null;
        }

        Object viewerInput = viewer.getInput();
        Object[] inputArray = null;
        if (!(viewerInput instanceof ArrayList<?>)) {
            return null;
        }
        inputArray = ((ArrayList) viewerInput).toArray();
        SortColumnSetting[] sortColumns = Arrays.copyOf(inputArray, inputArray.length, SortColumnSetting[].class);

        GridSaveSortState sortState = new GridSaveSortState();
        List<String> columns = new ArrayList<String>(sortColumnsModel.size());

        for (SortColumnSetting sort : sortColumns) {
            int index = Integer.parseInt(sort.getPriority());

            if (-1 == Arrays.asList(colNames).indexOf(sort.getColumnName())) {
                /*
                 * priority starts from 0 in code, but starts from 1 in
                 * multicolumn sort popup. So adding 1 in error dialog.
                 */
                SortErrors.generateErrorDialog(SORTERRORTYPE.BLANK_COLUMN, new String[] {String.valueOf(index + 1)});
                return null;
            }
            columns.add(sort.getColumnName());
            Set<String> uniqList = new HashSet<String>(columns);
            if (uniqList.size() < columns.size()) {
                /*
                 * priority starts from 0 in code, but starts from 1 in
                 * multicolumn sort popup. So adding 1 in error dialog.
                 */
                SortErrors.generateErrorDialog(SORTERRORTYPE.DUPLICATE_COLUMN,
                        new String[] {sort.getColumnName(), String.valueOf(index + 1)});
                return null;
            }
            SortDirectionEnum sortEnum = SortColumnSetting.getSortDirectionEnumFromComboText(sort.getSortOrder());
            SortEntryData entryData = new SortEntryData(sort.getColumnName(), sortEnum);
            sortState.saveSortEntry(entryData);
        }

        getShell().close(); // close pop-up only in success case

        return sortState;
    }

    private void configureColumnCursor() {
        focusCellManager = new TableViewerFocusCellManager(viewer, new FocusCellOwnerDrawHighlighter(viewer));
        ColumnViewerEditorActivationStrategy activationSupport = new ColumnViewerEditorActivationStrategy(viewer) {

            /**
             * Checks if is editor activation event.
             *
             * @param event the event
             * @return true, if is editor activation event
             */
            protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
                if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION) {
                    EventObject source = event.sourceEvent;
                    if (source instanceof MouseEvent && ((MouseEvent) source).button == 3) {
                        return false;

                    }
                }
                return super.isEditorActivationEvent(event)
                        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
                                && event.keyCode == SWT.CR);
            }
        };

        TableViewerEditor.create(viewer, focusCellManager, activationSupport,
                ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                        | ColumnViewerEditor.TABBING_VERTICAL | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    }

    /**
     * Creates the contents.
     *
     * @param parent the parent
     * @return the control
     */
    @Override
    protected Control createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));

        viewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.V_SCROLL | SWT.RESIZE | SWT.H_SCROLL);
        viewer.setContentProvider(new SortSettingTableContentProvider());
        ColumnViewerToolTipSupport.enableFor(viewer);
        addUIColumns();
        loadSavedSortState();

        table = viewer.getTable();
        table.setLayoutData(new GridData(GridData.FILL_BOTH));
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        configureColumnCursor();

        Composite buttonSet = new Composite(parent, SWT.NONE);
        buttonSet.setLayout(new GridLayout());
        buttonSet.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createAddColumnButton(buttonSet);

        Composite upDown = new Composite(buttonSet, SWT.NONE);
        GridLayout upDownLayout = new GridLayout(2, true);
        upDownLayout.marginHeight = 0;
        upDownLayout.marginWidth = 0;
        upDown.setLayout(upDownLayout);

        createUpColumnButton(upDown);
        createDownColumnButton(upDown);
        createDeleteColumnButton(buttonSet);
        createApplySortButton(buttonSet);

        viewer.setColumnProperties(COLUMN_HEADERS);
        viewer.setCellModifier(new SortColumnSettingModifier(viewer, colNames, dataTypes));

        return parent;
    }

    private void loadSavedSortState() {
        GridSaveSortState sortState = grid.getCurrentSortSnapshot();

        if (!sortState.hasSortKeys()) {
            sortColumnsModel.add(new SortColumnSetting());
        } else {
            List<SortEntryData> savedSortList = sortState.getSavedSortList();
            for (int index = 0; index < savedSortList.size(); index++) {
                SortEntryData sortComboNames = savedSortList.get(index);
                int columnIndex = Arrays.asList(colNames).indexOf(sortComboNames.getColumnName());
                sortColumnsModel.add(new SortColumnSetting(String.valueOf(index), sortComboNames.getColumnName(),
                        dataTypes[columnIndex],
                        SortColumnSetting.getComboTextFromSortDirectionEnum(sortComboNames.getSortDirection())));
            }
        }

        viewer.setInput(sortColumnsModel);
    }

    private TableViewerColumn createTableViewerColumn(String title, final int bound, final int colNumber) {
        final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE, colNumber);
        final TableColumn column = viewerColumn.getColumn();
        column.setText(title);
        column.setToolTipText(title);
        column.setWidth(bound);
        column.setResizable(true);
        column.setMoveable(true);
        return viewerColumn;
    }

    private void addUIColumns() {
        TableViewerColumn vColumn1 = createTableViewerColumn(
                MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_PRIORITY),
                MulticolumnSortConstants.PRIORITY_COL_LEN, MulticolumnSortConstants.PRIORITY_INDEX);
        vColumn1.setLabelProvider(vColumn1ColumnLabelProvider());

        TableViewerColumn vColumn2 = createTableViewerColumn(
                MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_COLUMN_NAME),
                MulticolumnSortConstants.COLNAME_COL_LEN, MulticolumnSortConstants.COLUMN_INDEX);

        vColumn2.setLabelProvider(vColumn2ColumnLabelProvider());

        vColumn2.setEditingSupport(new SortColumnComboEditingSupport(viewer, colNames, dataTypes,
                MessageConfigLoader.getProperty(IMessagesConstants.COMBO_TEXT_SORT_COLUMN)));

        TableViewerColumn vColumn3 = createTableViewerColumn(
                MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_DATATYPE),
                MulticolumnSortConstants.DATATYPE_COL_LEN, MulticolumnSortConstants.DATATYPE_INDEX);
        vColumn3.setLabelProvider(vColumn3ColumnLabelProvider());

        TableViewerColumn vColumn4 = createTableViewerColumn(
                MessageConfigLoader.getProperty(IMessagesConstants.COLUMN_SORT_ORDER),
                MulticolumnSortConstants.ORDER_COL_LEN, MulticolumnSortConstants.ORDER_INDEX);
        vColumn4.setLabelProvider(vColumn4ColumnLabelProvider());
        vColumn4.setEditingSupport(new SortColumnComboEditingSupport(viewer));

    }

    private ColumnLabelProvider vColumn4ColumnLabelProvider() {
        return new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getSortOrder();
            }

            @Override
            public String getToolTipText(Object element) {
                SortColumnSetting elmnt = (SortColumnSetting) element;
                return elmnt.getSortOrder();
            }
        };
    }

    private ColumnLabelProvider vColumn3ColumnLabelProvider() {
        return new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getDataType();
            }

            @Override
            public String getToolTipText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getDataType();
            }
        };
    }

    private ColumnLabelProvider vColumn2ColumnLabelProvider() {
        return new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getColumnName();
            }

            @Override
            public String getToolTipText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getColumnName();
            }

        };
    }

    private ColumnLabelProvider vColumn1ColumnLabelProvider() {
        return new ColumnLabelProvider() {
            @Override
            public String getText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getPriorityText();
            }

            @Override
            public String getToolTipText(Object element) {
                SortColumnSetting ele = (SortColumnSetting) element;
                return ele.getPriorityText();
            }
        };
    }

    private void createApplySortButton(Composite buttonSet) {
        Button btnApplySort = new Button(buttonSet, SWT.NONE);
        GridData gdBtnApplySort = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        btnApplySort.setLayoutData(gdBtnApplySort);
        btnApplySort.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_APPLY_SORT));
        btnApplySort.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                GridSaveSortState state = prepareSortSetting();
                if (null != state) {
                    grid.applySortSnapshot(state);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    private void createDeleteColumnButton(Composite buttonSet) {
        Button btnDelColumn = new Button(buttonSet, SWT.NONE);
        GridData gdBtnDelCol = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        btnDelColumn.setLayoutData(gdBtnDelCol);
        btnDelColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_DELETE_COLUMN));
        btnDelColumn.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                int selIndex = table.getSelectionIndex();

                if (selIndex == -1) {
                    return;
                }

                sortColumnsModel.remove(selIndex);

                // update priority of the columns that are after the deleted
                // column
                for (int i = selIndex; i < sortColumnsModel.size(); i++) {
                    sortColumnsModel.get(i).reducePriority();
                }

                if (selIndex == 0 && table.getItemCount() > 0) {
                    table.setSelection(selIndex);
                } else {
                    table.setSelection(selIndex - 1);
                }
                viewer.refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }

        });
    }

    private void createDownColumnButton(Composite upDown) {
        Button btnDwn = new Button(upDown, SWT.NONE);
        GridData gdBtnDwn = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        btnDwn.setLayoutData(gdBtnDwn);
        btnDwn.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_DOWN_COULMN));
        btnDwn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selIndex = table.getSelectionIndex();
                if (selIndex == table.getItemCount() - 1 || selIndex == -1) {
                    return;
                }
                int otherIndex = selIndex + 1;
                swapItems(selIndex, otherIndex);
                table.setSelection(otherIndex);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    private void createUpColumnButton(Composite upDown) {
        Button btnUp = new Button(upDown, SWT.NONE);
        GridData gdBtnUp = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        btnUp.setLayoutData(gdBtnUp);
        btnUp.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_UP_COLUMN));
        btnUp.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                int selIndex = table.getSelectionIndex();
                if (selIndex == 0 || selIndex == -1) {
                    return;
                }
                int otherIndex = selIndex - 1;
                swapItems(selIndex, otherIndex);
                table.setSelection(otherIndex);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    private void createAddColumnButton(Composite buttonSet) {
        Button btnAddColumn = new Button(buttonSet, SWT.NONE);
        GridData gdBtnAddColumn = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        btnAddColumn.setLayoutData(gdBtnAddColumn);
        btnAddColumn.setText(MessageConfigLoader.getProperty(IMessagesConstants.BUTTON_ADD_COLUMN));
        btnAddColumn.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                SortColumnSetting newSortCol = new SortColumnSetting(String.valueOf(sortColumnsModel.size()));
                sortColumnsModel.add(newSortCol);
                viewer.editElement(newSortCol, MulticolumnSortConstants.COLUMN_INDEX);
                viewer.editElement(newSortCol, MulticolumnSortConstants.ORDER_INDEX);
                viewer.refresh();
                table.setSelection(table.getItemCount());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent event) {

            }
        });
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        MPPDBIDELoggerUtility.trace("multicolumn sort setting succesful");
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        MPPDBIDELoggerUtility.trace("Exception: multicolumn sort setting");

    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        MPPDBIDELoggerUtility.trace("Exception: multicolumn sort setting");

    }

    /**
     * On presetup failure UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onPresetupFailureUIAction(MPPDBIDEException exception) {
        MPPDBIDELoggerUtility.trace("pre-setup fail: multicolumn sort setting");
    }

}
