/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PreDestroy;

import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.IObjectPropertyData;
import com.huawei.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import com.huawei.mppdbide.utils.DsEncodingEnum;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.DataText;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridToolbar.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class GridToolbar implements Observer {
    private Composite toolbarComposite;
    private ToolBar toolbar;
    private ToolItem copyItem;
    private ToolItem copyAdvancedItem;
    private ToolItem exportAllItem;
    private ToolItem exportCurrentItem;
    private ToolItem exportExecutionPlan;
    private ToolItem toggleSearchArea;
    private ToolItem toggleSqlArea;
    private ToolItem multiColumnSort;
    private ToolItem clearSort;
    private ToolItem insertRow;
    private ToolItem deleteRow;
    private ToolItem commitRecord;
    private ToolItem rollbackChanges;
    private ToolItem refresh;
    private ToolItem pasteData;

    private ToolItem savePlan;
    private ToolItem textView;
    private ToolItem treeView;

    private DSEventTable eventEditTable;
    private IDSGridDataProvider dataProvider;
    private ToolItem forgetOption;
    private boolean forgetOptionisEnabled;
    private DSGridStateMachine dsExportState;
    private DSGridStateMachine dsRefreshState;

    private Database resultTabDB;
    private boolean deleteRowState;
    private boolean isSearched;

    private boolean copyAdvanceFlag;
    private boolean insertRowFlag;
    private boolean deleteRowFlag;
    private boolean commitRecordFlag;
    private boolean rollbackChangesFlag;
    private boolean forgetOptionFlag;
    private boolean pasteDataFlag;
    private boolean multiColumnSortFlag;
    private boolean clearSortFlag;
    private boolean exportCurrentItemFlag;
    private boolean exportAllItemFlag;
    private boolean comboFlag;
    private int gridAndTextButSwitchCount;

    private Label batchDropObjectsParent;
    private Label batchDropRuns;
    private Label batchDropErrors;
    private Button batchDropCascadeBtn;
    private Button batchDropAtomicBtn;
    private Button batchDropStartBtn;
    private Button batchDropStopBtn;
    private Combo combo;
    private ArrayList<String> dsEncodingList;
    private ILayerListener iLayerListener;
    private DataGrid dataGrid;
    private boolean isExplainQuery;
    private ToolItem showGrid;
    private ToolItem showText;
    private DSGridComponent dsGridComponent;

    private HashMap<String, ToolItem> toolManagerMap;

    private final Object INSTANCE_LOCK = new Object();

    private ToolItem loadMoreRecord;

    /**
     * Gets the tool manager map.
     *
     * @return the tool manager map
     */
    public HashMap<String, ToolItem> getToolManagerMap() {
        return toolManagerMap;
    }

    /**
     * Creates the component.
     *
     * @param parent the parent
     * @param gridDataProvider the grid data provider
     * @param dsGrid the ds grid
     */
    public void createComponent(Composite parent, IDSGridDataProvider gridDataProvider, DSGridComponent dsGrid) {
        this.toolbarComposite = createComposite(parent);
        GridLayout gl = (GridLayout) this.toolbarComposite.getLayout();
        gl.numColumns = 2;
        toolbarComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 0));
        ToolBarManager toolbarMgr = new ToolBarManager();
        setDataProvider(gridDataProvider);
        toolbarMgr.setStyle(SWT.FLAT);
        this.toolbar = toolbarMgr.createControl(this.toolbarComposite);
        this.dsGridComponent = dsGrid;
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.addObserver(this);
        }
        dsEncodingList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        toolManagerMap = new HashMap<String, ToolItem>();
    }

    /**
     * 
     * @param txt Name to be displayed on the tool bar item. Can be empty if
     * only image is expected.
     * @param imgPath Image path understandable by IconUtility.
     * @param swtType Supports SWT.PUSH, SWT.CHECK, SWT.RADIO, SWT.SEPARATOR,
     * SWT.DROP_DOWN
     * @return Tool bar button/item
     */
    private ToolItem getToolItem(int swtType, String txt, String imgPath, String toolTip) {
        ToolItem item = new ToolItem(this.toolbar, swtType);
        if (txt != null) {
            item.setText(txt);
        }

        if (imgPath != null) {
            Image icon = IconUtility.getIconImage(imgPath, getClass());
            item.setImage(icon);
        }

        return item;
    }

    private Composite createComposite(Composite parent) {
        Composite newComp = new Composite(parent, SWT.NONE);
        DSGridComponent.setLayoutProperties(newComp);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(newComp);
        return newComp;
    }

    /**
     * Adds the item copy.
     *
     * @param stateMachine the state machine
     */
    public void addItemCopy(DSGridStateMachine stateMachine, boolean isOltpCreateTable) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.COPY_RESULT_WINDOW_CONTENTS);
        this.copyItem = getToolItem(SWT.PUSH, null, IiconPath.ICO_COPY, toolTipMsg);
        toolManagerMap.put(ToolBarConstants.COPY_TOOLITEM, this.copyItem);
        this.copyItem.setToolTipText(toolTipMsg);
        this.copyItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
        this.copyItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (!isOltpCreateTable && IHandlerUtilities.disableToolBarIfEnabled(copyItem,
                        IHandlerUtilities.getExportDataSelectionOptions())) {
                    return;
                }

                if (null != dsGridComponent.getDataText()) {
                    if (showGrid.getSelection()) {
                        dsGridComponent.getDataGrid().doCopy();
                    } else if (showText.getSelection()) {
                        dsGridComponent.getDataText().doCopy();
                    }
                } else {
                    dsGridComponent.getDataGrid().doCopy();
                }

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!toolbar.isDisposed()) {
                        copyItem.setEnabled(newState != DSGridStateMachine.State.LOADING);
                        IHandlerUtilities.setToolItemEnabled(copyItem,
                                IHandlerUtilities.getExportDataSelectionOptions());
                    }

                }
            }
        });
    }

    /**
     * Adds the item advanced copy.
     *
     * @param stateMachine the state machine
     */
    public void addItemAdvancedCopy(DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.COPY_ADVANCED_RESULT_WINDOW_CONTENTS);
        this.copyAdvancedItem = getToolItem(SWT.PUSH, null, IiconPath.ICO_COPY_ADVANCED, toolTipMsg);
        this.copyAdvancedItem.setToolTipText(toolTipMsg);
        this.copyAdvancedItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
        this.copyAdvancedItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (IHandlerUtilities.disableToolBarIfEnabled(copyAdvancedItem,
                        IHandlerUtilities.getExportDataSelectionOptions())) {
                    return;
                }
                dsGridComponent.getDataGrid().doAdvancedCopy();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!toolbar.isDisposed()) {
                        copyAdvancedItem.setEnabled(newState != DSGridStateMachine.State.LOADING);
                        IHandlerUtilities.setToolItemEnabled(copyAdvancedItem,
                                IHandlerUtilities.getExportDataSelectionOptions());
                    }
                }
            }
        });

    }

    /**
     * Adds the item paste cells.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemPasteCells(DSEventTable eventTable, DSGridStateMachine stateMachine) {

        if (dsGridComponent.getDataGrid().isPropertiesGrid()) {
            return;
        }
        dsGridComponent.getDataGrid().commitAndCloseActiveCellEditor();
        String toolTip = MessageConfigLoader.getProperty(IMessagesConstants.PASTE_TOOTIP);
        pasteData = getToolItem(SWT.PUSH, null, IiconPath.ICO_PASTE, toolTip);
        this.pasteData.setToolTipText(toolTip);
        this.pasteData.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {

                dsGridComponent.getDataGrid().paste();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });

    }

    /**
     * Adds the item export current page.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemExportCurrentPage(final DSEventTable eventTable, final DSGridStateMachine stateMachine) {
        this.exportCurrentItem = getToolItem(SWT.PUSH, null, IiconPath.ICO_EXPORT_CURRENT_PAGE,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CSV));
        this.exportCurrentItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CSV));
        this.exportCurrentItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
        this.exportCurrentItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (IHandlerUtilities.disableToolBarIfEnabled(exportCurrentItem,
                        IHandlerUtilities.getExportDataSelectionOptions())) {
                    return;
                }

                if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                    eventTable.sendEvent(
                            new DSEvent(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, new Observer() {
                                @Override
                                public void update(Observable observable, Object arg) {
                                    if (arg instanceof Boolean && ((boolean) arg)) {
                                        stateMachine.set(DSGridStateMachine.State.IDLE);
                                    }
                                }
                            }));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!toolbar.isDisposed()) {
                        exportCurrentItem.setEnabled(newState != DSGridStateMachine.State.EXPORTING
                                && newState != DSGridStateMachine.State.LOADING);
                        IHandlerUtilities.setToolItemEnabled(exportCurrentItem,
                                IHandlerUtilities.getExportDataSelectionOptions());
                    }

                }
            }
        });
    }

    /**
     * Adds the item export execution plan.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemExportExecutionPlan(final DSEventTable eventTable, final DSGridStateMachine stateMachine) {
        this.exportExecutionPlan = getToolItem(SWT.PUSH, null, IiconPath.ICO_EXPORT_CURRENT_PAGE,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_EXEC));
        this.exportExecutionPlan.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_EXEC));
        this.exportExecutionPlan.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
        this.exportExecutionPlan.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (IHandlerUtilities.disableToolBarIfEnabled(exportExecutionPlan,
                        IHandlerUtilities.getExportDataSelectionOptions())) {
                    return;
                }

                if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                    eventTable.sendEvent(
                            new DSEvent(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, new Observer() {
                                @Override
                                public void update(Observable observable, Object obj) {
                                    if (obj instanceof Boolean && ((boolean) obj)) {
                                        stateMachine.set(DSGridStateMachine.State.IDLE);
                                    }
                                }
                            }));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectEvent) {

            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object obj) {
                if (obj instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) obj;
                    if (!toolbar.isDisposed()) {
                        exportExecutionPlan.setEnabled(newState != DSGridStateMachine.State.EXPORTING
                                && newState != DSGridStateMachine.State.LOADING);
                        IHandlerUtilities.setToolItemEnabled(exportExecutionPlan,
                                IHandlerUtilities.getExportDataSelectionOptions());
                    }

                }
            }
        });
    }

    /**
     * Adds the item export all.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemExportAll(final DSEventTable eventTable, final DSGridStateMachine stateMachine,
            boolean isCursorPopup) {

        this.exportAllItem = getToolItem(SWT.PUSH, null, IiconPath.ICO_EXPORT_ALL_DATA,
                MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CSV_STAR));
        this.exportAllItem.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CSV_STAR));
        this.exportAllItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
        this.exportAllItem.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (IHandlerUtilities.disableToolBarIfEnabled(exportAllItem,
                        IHandlerUtilities.getExportDataSelectionOptions())) {
                    return;
                }

                if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                    eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA, new Observer() {
                        // once Export is over ImportExportDataCore,
                        // we will be notified here.
                        @Override
                        public void update(Observable observable, Object arg) {
                            // If export is not progress then Update
                            // state machine
                            if (arg instanceof Boolean && !((boolean) arg)) {
                                stateMachine.set(DSGridStateMachine.State.IDLE);
                            }
                        }
                    }));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
        if (!isCursorPopup) {
            setExplainQuery();
        }
        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    enableDisableExportAllButton();
                }
            }
        });
    }

    /**
     * Adds the item multi sort.
     *
     * @param multiColumnSortPoUpListener the multi column sort po up listener
     */
    public void addItemMultiSort(final SelectionListener multiColumnSortPoUpListener) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.GRIDDATA_SORT);
        this.multiColumnSort = getToolItem(SWT.PUSH, null, IiconPath.ICO_SORT_MULTI_COLUMN, toolTipMsg);
        this.multiColumnSort.setToolTipText(toolTipMsg);

        this.multiColumnSort.addSelectionListener(multiColumnSortPoUpListener);

    }

    /**
     * Adds the item clear sort.
     */
    public void addItemClearSort() {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.CLEAR_SORT);
        this.clearSort = getToolItem(SWT.PUSH, null, IiconPath.ICO_SORT_CLEAR, toolTipMsg);
        this.clearSort.setToolTipText(toolTipMsg);

        this.clearSort.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                dsGridComponent.getDataGrid().clearSort();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });

    }

    /**
     * Adds the item toggle query area.
     *
     * @param queryArea the query area
     */
    public void addItemToggleQueryArea(final GridQueryArea queryArea) {
        this.toggleSqlArea = getToolItem(SWT.PUSH, null, IiconPath.SQL_BAR_TOGGLE_ENABLE,
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EXECUTED_QUERY_TOOLTIP));
        this.toggleSqlArea.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_HIDE_QUERY_BAR));
        final ToolItem item = this.toggleSqlArea;
        this.toggleSqlArea.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (queryArea.isQuueryAreaVisible()) {
                    queryArea.doHideQueryArea();
                    item.setImage(IconUtility.getIconImage(IiconPath.SQL_BAR_TOGGLE_DISABLE, getClass()));
                } else {
                    queryArea.doShowQueryArea();
                    item.setImage(IconUtility.getIconImage(IiconPath.SQL_BAR_TOGGLE_ENABLE, getClass()));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
    }

    /**
     * Update toggle query area image.
     */
    public void updateToggleQueryAreaImage() {
        final ToolItem item = this.toggleSqlArea;
        item.setImage(IconUtility.getIconImage(IiconPath.SQL_BAR_TOGGLE_DISABLE, getClass()));
    }

    /**
     * Adds the item toggle search area.
     *
     * @param searchArea the search area
     */
    public void addItemToggleSearchArea(final GridSearchArea searchArea) {
        this.toggleSearchArea = getToolItem(SWT.PUSH, null, IiconPath.SEARCH_BAR_TOGGLE_ENABLE,
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EXECUTED_QUERY_TOOLTIP));
        final ToolItem item = this.toggleSearchArea;
        this.toggleSearchArea.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.SHOW_HIDE_SEARCH_BAR));
        this.toggleSearchArea.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (searchArea.isSearchAreaVisible()) {
                    searchArea.doHideSearchArea();
                    item.setImage(IconUtility.getIconImage(IiconPath.SEARCH_BAR_TOGGLE_DISABLE, getClass()));
                } else {
                    searchArea.doShowSearchArea();
                    item.setImage(IconUtility.getIconImage(IiconPath.SEARCH_BAR_TOGGLE_ENABLE, getClass()));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
    }

    /**
     * Adds the item insert row.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemInsertRow(DSEventTable eventTable, DSGridStateMachine stateMachine) {

        final DataGrid grid = dsGridComponent.getDataGrid();
        final ServerObjectTypeForDialog type = grid.getTypeOfDialogRequired();
        grid.commitAndCloseActiveCellEditor();
        String toolTip = MessageConfigLoader.getProperty(IMessagesConstants.INSERT_ROW_TOOLTIP);
        this.insertRow = getToolItem(SWT.PUSH, null, IiconPath.ICO_EDIT_ADD, toolTip);
        toolManagerMap.put(ToolBarConstants.ADD_TOOLITEM, this.insertRow);
        this.insertRow.setToolTipText(toolTip);
        /*
         * The general tab of the properties window of a table should not have
         * insert button enabled
         */
        if (type != null && type == ServerObjectTypeForDialog.GENERAL) {
            insertRow.setEnabled(false);
            return;
        }
        this.insertRow.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (grid.isPropertiesGrid()) {

                    // code for properties window insert operation
                    AddPropertiesInfoDialog dialog = new AddPropertiesInfoDialog(type, grid.getDataGrid().getShell(),
                            (TableMetaData) dataProvider.getTable());
                    dialog.createDialog();

                    // serverObject is null when user press cancel or close in
                    // dialog
                    if (dialog.getServerObject() == null) {
                        return;
                    }
                    grid.insertEmptyRow(isSearched, dialog.getServerObject());

                } else {
                    grid.insertEmptyRow(isSearched, null);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!insertRow.isDisposed()) {
                        insertRow.setEnabled(newState != DSGridStateMachine.State.LOADING);
                    }
                }
            }
        });

    }

    /**
     * Adds the item delete row.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemDeleteRow(DSEventTable eventTable, DSGridStateMachine stateMachine) {
        final DataGrid grid = dsGridComponent.getDataGrid();
        String toolTip = MessageConfigLoader.getProperty(IMessagesConstants.DELETE_ROW_TOOLTIP);
        this.deleteRow = getToolItem(SWT.PUSH, null, IiconPath.ICO_EDIT_DELETE, toolTip);
        toolManagerMap.put(ToolBarConstants.DELETE_TOOLITEM, this.deleteRow);
        this.deleteRow.setEnabled(false);
        this.deleteRow.setToolTipText(toolTip);
        setEventEditTable(eventTable);
        /*
         * The general tab of the properties window of a table should not have
         * delete button enabled
         */
        if (dataProvider instanceof IObjectPropertyData) {
            String objectPropertyName = ((IObjectPropertyData) dataProvider).getObjectPropertyName();
            if (objectPropertyName != null && objectPropertyName.equals("General")) {
                deleteRow.setEnabled(false);
                return;
            }
        }
        setRowListener(grid, eventTable);
        this.deleteRow.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                grid.closeEditor();
                grid.deleteRow();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });

    }

    private void setRowListener(final DataGrid grid, DSEventTable eventTable) {
        iLayerListener = new ILayerListener() {
            @Override
            public void handleLayerEvent(ILayerEvent event) {
                boolean isEncodingChanged = false;
                if (event instanceof CellSelectionEvent || event instanceof RowSelectionEvent
                        || event instanceof ColumnSelectionEvent) {
                    deleteRowState = true;
                    if (dataProvider instanceof DSResultSetGridDataProvider
                            && ((DSResultSetGridDataProvider) dataProvider).isIncludeEncoding()
                            && ((DSResultSetGridDataProvider) dataProvider).isEncodingChanged()) {
                        isEncodingChanged = true;
                    }
                    deleteRow.setEnabled(dataProvider.getDatabse() != null && dataProvider.getDatabse().isConnected()
                            && (grid.isRowsOnlySelected() && !isEncodingChanged));
                }
                deleteRowState = false;
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_ROW_SELECTED, grid.isRowsOnlySelected()));
            }
        };
        grid.addGridLayerListener(iLayerListener);
        dataGrid = grid;
    }

    /**
     * Adds the item commit record.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemCommitRecord(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        final DataGrid grid = dsGridComponent.getDataGrid();
        final DataText text = dsGridComponent.getDataText();
        grid.commitAndCloseActiveCellEditor();

        final CommitRecordEventData commitEventData = new CommitRecordEventData();
        commitEventData.setDatagrid(grid);
        commitEventData.setDataText(text);
        commitEventData.setEventTable(eventTable);
        setEventEditTable(eventTable);

        String toolTip = MessageConfigLoader.getProperty(IMessagesConstants.COMMIT_CHANGES_TOOLTIP);
        this.commitRecord = getToolItem(SWT.PUSH, null, IiconPath.ICO_EDIT_COMMIT, toolTip);
        toolManagerMap.put(ToolBarConstants.COMMIT_TOOLITEM, this.commitRecord);
        this.commitRecord.setToolTipText(toolTip);
        commitRecord.setEnabled(false);
        this.commitRecord.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                commitEventData.setDataProvider(dataProvider);
                commitRecord.setEnabled(false);
                if (rollbackChanges != null) {
                    rollbackChanges.setEnabled(false);
                }
                eventTable.sendEvent(
                        new DSEvent(IDSGridUIListenable.LISTEN_TYPE_PROPERITES_COMMIT_DATA, commitEventData));
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA, commitEventData));

            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
    }

    /**
     * Gets the tool bar.
     *
     * @return the tool bar
     */
    public ToolBar getToolBar() {
        return this.toolbar;
    }

    /**
     * Adds the item roll back changes.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemRollBackChanges(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTip = MessageConfigLoader.getProperty(IMessagesConstants.ROLLBACK_CHANGES_TOOLTIP);
        this.rollbackChanges = getToolItem(SWT.PUSH, null, IiconPath.ICO_DELETE, toolTip);
        toolManagerMap.put(ToolBarConstants.CANCEL_TOOLITEM, this.rollbackChanges);
        this.rollbackChanges.setToolTipText(toolTip);

        this.rollbackChanges.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                rollbackChangesForEditedData(eventTable);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
    }

    private void rollbackChangesForEditedData(final DSEventTable eventTable) {
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA, null));
        dsGridComponent.getDataGrid().closeEditor();
        dsGridComponent.getDataGrid().rollBackChanges();
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LIRTEN_ROLLBACK_PROPERTIES, dataProvider));
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA_COMPLETE, dataProvider));
    }

    /**
     * Adds the refresh item.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addRefreshItem(DSEventTable eventTable, DSGridStateMachine stateMachine) {
        this.refresh = getToolItem(SWT.PUSH, null, IiconPath.ICO_REFRESH, "");
        toolManagerMap.put(ToolBarConstants.REFRESH_TOOLITEM, this.refresh);
        if (null != dsGridComponent.getDataText() && dsGridComponent.getDataText().isInitDataTextFlag()) {
            refresh.setEnabled(false);
        }
        setEventEditTable(eventTable);
        this.refresh.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent event) {
                if (null != dsGridComponent.getDataText()) {
                    dsGridComponent.getDataText().setRefreshFlag(true);
                }
                dsGridComponent.getDataGrid().closeEditor();
                dsGridComponent.getDataGrid().refresh();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!refresh.isDisposed()) {
                        refresh.setEnabled(newState != DSGridStateMachine.State.LOADING);
                    }
                }
            }
        });
        updateRefreshButtonTooltip(true);
    }

    /**
     * Adds the forget item.
     *
     * @param eventTable the event table
     */
    public void addForgetItem(final DSEventTable eventTable) {
        if (dsGridComponent.getDataGrid().isPropertiesGrid()) {
            return;
        }
        this.forgetOption = getToolItem(SWT.PUSH, null, IiconPath.ICON_EDITTABLE_FORGET_DISABLE, null);
        this.forgetOption.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_FORGET_OPTIONS));
        setForgotOptionEnabled(false);

        this.forgetOption.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (GridToolbar.this.forgetOptionisEnabled) {
                    eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_USER_FORGET_OPTION, null));
                    setForgotOptionEnabled(false);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Nothing to do. ignore.
            }
        });
    }

    /**
     * Adds the encoding.
     *
     * @param eventTable the event table
     * @param defaultEncoding the default encoding
     */
    public void addEncoding(final DSEventTable eventTable, String defaultEncoding) {

        // Add a separator pipe
        new ToolItem(toolbar, SWT.SEPARATOR);

        ToolItem seperatorLabel = new ToolItem(toolbar, SWT.SEPARATOR);

        Label encodingLbl = new Label(toolbar, SWT.NONE);
        encodingLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_MSG) + " : ");
        encodingLbl.pack();
        seperatorLabel.setWidth(encodingLbl.getSize().x);
        seperatorLabel.setControl(encodingLbl);

        ToolItem sep = new ToolItem(toolbar, SWT.SEPARATOR);

        combo = new Combo(this.toolbar, SWT.READ_ONLY);
        for (DsEncodingEnum dsEncoding : DsEncodingEnum.values()) {

            dsEncodingList.add(dsEncoding.getEncoding());
        }
        for (String encoding : dsEncodingList) {

            combo.add(encoding);
        }

        for (String item : dsEncodingList) {
            if (item.equals(defaultEncoding)) {
                combo.setText(item);
            }
        }
        combo.pack();
        if (null != dsGridComponent.getDataText() && dsGridComponent.getDataText().isInitDataTextFlag()) {
            combo.setEnabled(false);
        }
        combo.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (null != dsGridComponent.getDataText()) {
                    dsGridComponent.getDataText().setEndodingFlag(true);
                }
                String selectedEncoding = combo.getItem(combo.getSelectionIndex());
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING, selectedEncoding));
                if (dataProvider instanceof DSResultSetGridDataProvider
                        && ((DSResultSetGridDataProvider) dataProvider).isEditSupported()) {

                    rollbackChangesForEditedData(eventTable);
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
            }
        });
        sep.setWidth(combo.getSize().x);
        sep.setControl(combo);
    }

    /**
     * Gets the selected encoding.
     *
     * @return the selected encoding
     */
    public String getSelectedEncoding() {
        if (combo != null) {
            return combo.getText();
        }
        return null;
    }

    private void setForgotOptionEnabled(boolean isEnabled) {
        this.forgetOptionisEnabled = isEnabled;
        setIconToDisplay();
    }

    // To enable or disable ICON_EDITTABLE_FORGET
    private void setIconToDisplay() {
        String iconToDisplay = (forgetOptionisEnabled) ? IiconPath.ICON_EDITTABLE_FORGET_ENABLE
                : IiconPath.ICON_EDITTABLE_FORGET_DISABLE;
        if (!this.forgetOption.isDisposed()) {
            this.forgetOption.setImage(IconUtility.getIconImage(iconToDisplay, getClass()));
        }
    }

    private void updateRefreshButtonTooltip(boolean isEnabled) {
        String toolTip = "";
        if (isEnabled) {
            toolTip = MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_TABLE_TOOLTIP);
        } else {
            toolTip = MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_TABLE_TOOLTIP_DISABLED);
        }

        this.refresh.setToolTipText(toolTip);
    }

    /**
     * Handle data edit event.
     *
     * @param isDbConnected the is db connected
     */
    public void handleDataEditEvent(boolean isDbConnected) {
        if (this.dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDP = (IDSEditGridDataProvider) dataProvider;
            boolean isEdited = editDP.isGridDataEdited();

            handleEditDataProvider(isDbConnected, isEdited);
        }
    }

    /**
     * Handle non edit events
     *
     * @param isDbConnected the is db connected
     */
    public void handleNonDataEditEvent(boolean isDbConnected) {
        if (this.refresh != null && !this.refresh.isDisposed()) {
            this.refresh.setEnabled(!isSearched && isDbConnected);
        }

        if (this.exportAllItem != null && !this.exportAllItem.isDisposed()) {
            this.exportAllItem.setEnabled(isDbConnected && IHandlerUtilities.getExportDataSelectionOptions());
        }

        if (isDbConnected) {
            resetLoadMoreRecordStatus();
        } else {
            if (this.loadMoreRecord != null && !this.loadMoreRecord.isDisposed()) {
                this.loadMoreRecord.setEnabled(false);
            }
        }
    }

    private void handleEditDataProvider(boolean isDbConnected, boolean isEdited) {
        handleRefreshButtonEnabling(isDbConnected, isEdited);
        handleCommitButtonEnabling(isDbConnected, isEdited);
        handleRollbackButtonEnabling(isDbConnected, isEdited);

        if (this.exportCurrentItem != null && !this.exportCurrentItem.isDisposed()) {
            this.exportCurrentItem.setEnabled(true);
        }

        IHandlerUtilities.setToolItemEnabled(exportCurrentItem, IHandlerUtilities.getExportDataSelectionOptions());

        if (this.exportAllItem != null && !this.exportAllItem.isDisposed()) {
            this.exportAllItem
                    .setEnabled(isDbConnected && IHandlerUtilities.getExportDataSelectionOptions() && !isEdited);
        }

        if (this.insertRow != null && !this.insertRow.isDisposed()) {
            this.insertRow.setEnabled(isDbConnected && !dsExportState.isLoading()
                    && checkForConditionalEnableDisable(isDbConnected) && !isEncodingToBeModified());
        }
        if (this.deleteRow != null && !this.deleteRow.isDisposed()) {
            this.deleteRow.setEnabled(isDbConnected && deleteRowState && checkForConditionalEnableDisable(isDbConnected)
                    && !isEncodingToBeModified());
        }
        if (this.pasteData != null && !this.pasteData.isDisposed()) {
            this.pasteData.setEnabled(isDbConnected && !isEncodingToBeModified());
        }
        handShowGridAndShowTextButEnabling(isDbConnected);

        if (isDbConnected) {
            resetLoadMoreRecordStatus();
        } else {
            if (this.loadMoreRecord != null && !this.loadMoreRecord.isDisposed()) {
                this.loadMoreRecord.setEnabled(false);
            }
        }

    }

    private void handShowGridAndShowTextButEnabling(boolean isDbConnected) {
        if (this.showGrid != null && !this.showGrid.isDisposed()) {
            this.showGrid.setEnabled(isDbConnected);
        }
        if (this.showText != null && !this.showText.isDisposed()) {
            this.showText.setEnabled(isDbConnected);
        }
        if (null != this.dsGridComponent.getDataText() && null != this.showText && !this.showText.isDisposed()
                && showText.getSelection()) {
            disableButtons();
        }
    }

    private void handleRollbackButtonEnabling(boolean isDbConnected, boolean isEdited) {
        if (this.rollbackChanges != null && !this.rollbackChanges.isDisposed()) {
            this.rollbackChanges.setEnabled(isEdited && isDbConnected && !isEncodingToBeModified());
        }
    }

    private void handleCommitButtonEnabling(boolean isDbConnected, boolean isEdited) {
        if (this.commitRecord != null && !this.commitRecord.isDisposed()) {
            this.commitRecord.setEnabled(isEdited && isDbConnected && !isEncodingToBeModified());
        }
    }

    private void handleRefreshButtonEnabling(boolean isDbConnected, boolean isEdited) {
        if (this.refresh != null && !this.refresh.isDisposed()) {
            this.refresh.setEnabled(!isSearched && !isEdited && isDbConnected);
            updateRefreshButtonTooltip(!isSearched && !isEdited && isDbConnected);
        }
    }

    private boolean isEncodingToBeModified() {
        if (this.dataProvider instanceof DSResultSetGridDataProvider
                && ((DSResultSetGridDataProvider) this.dataProvider).isIncludeEncoding()
                && ((DSResultSetGridDataProvider) this.dataProvider).isEncodingChanged()) {
            return true;
        }
        return false;
    }

    /**
     * Enable disable export all button.
     */
    public void enableDisableExportAllButton() {
        if (isExportAllButtonDisposed() && null != getResultTabDB()) {
            // getResultTabDB().isDdlOperationSupported() :this check is added
            // for OLTP V1R6 version as copy manager syntax is not supported can
            // be removed once issue fixed
            exportAllItem.setEnabled(getResultTabDB().isConnected() && canExportAllEnabled()
                    && !dataProvider.getResultTabDirtyFlag() && !isExplainQuery);

            if (isExplainQuery) {
                exportAllItem.setToolTipText(
                        MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_DISABLE_TOOLTIP));
            }
        }

        IHandlerUtilities.setToolItemEnabled(exportAllItem, IHandlerUtilities.getExportDataSelectionOptions());
        if (null != this.dsGridComponent.getDataText() && !this.showText.isDisposed() && showText.getSelection()) {
            this.disableButtons();
        }
    }

    /**
     * Enable disable export all button.
     *
     * @param value the value
     */
    public void enableDisableExportAllButton(boolean value) {
        if (isExportAllButtonDisposed() && null != getResultTabDB()) {
            exportAllItem.setEnabled(value);
        }
        IHandlerUtilities.setToolItemEnabled(exportAllItem, IHandlerUtilities.getExportDataSelectionOptions());
    }

    private boolean canExportAllEnabled() {
        return null != getDsExportState() && !getDsExportState().isExporting() && !getDsExportState().isLoading();
    }

    private boolean isExportAllButtonDisposed() {
        return null != exportAllItem && !exportAllItem.isDisposed();
    }

    private void setExplainQuery() {
        // For Explain Plan Query ExportAllData icon will be disabled.
        isExplainQuery = ((DSResultSetGridDataProvider) this.dataProvider).getSummary().getQuery()
                .toLowerCase(Locale.ENGLISH).startsWith("explain");
    }

    /**
     * Update.
     *
     * @param o the o
     * @param arg the arg
     */
    @Override
    public void update(Observable observable, Object arg) {
        DSEventTable eventEditTbl = getEventEditTable();
        updateDatabaseConnectDisconnect(eventEditTbl);
    }

    private void updateDatabaseConnectDisconnect(DSEventTable eventResultTab) {
        if (null != eventResultTab) {
            eventResultTab.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS, this));
        }
        enableDisableExportAllButton();
        enableDisableShowGridAndShowTextBttton();
    }

    private void enableDisableShowGridAndShowTextBttton() {
        if (null != getResultTabDB()) {
            handShowGridAndShowTextButEnabling(getResultTabDB().isConnected());
        }
    }

    private DSEventTable getEventEditTable() {
        return eventEditTable;
    }

    private void setEventEditTable(DSEventTable eventEditTable) {
        this.eventEditTable = eventEditTable;
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        if ((dataProvider instanceof ExecutionPlanTextDisplayGrid
                || dataProvider instanceof ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat)
                && this.dataProvider != null) {
            this.dataProvider.preDestroy();
            this.dataProvider = null;
        }
        this.dataProvider = dataProvider;
    }

    /**
     * Gets the rember user option listener.
     *
     * @return the rember user option listener
     */
    public IDSListener getRemberUserOptionListener() {
        return new IDSListener() {
            @Override
            public void handleEvent(final DSEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        Object val = event.getObject();
                        if (val instanceof Boolean) {
                            GridToolbar.this.setForgotOptionEnabled((Boolean) val);
                        }
                    }
                });

            }
        };
    }

    private DSGridStateMachine getDsRefreshState() {
        return dsRefreshState;
    }

    /**
     * Sets the ds refresh state.
     *
     * @param dsRefreshState the new ds refresh state
     */
    public void setDsRefreshState(DSGridStateMachine dsRefreshState) {
        this.dsRefreshState = dsRefreshState;
    }

    private DSGridStateMachine getDsExportState() {
        return dsExportState;
    }

    /**
     * Sets the ds export state.
     *
     * @param dsExportState the new ds export state
     */
    public void setDsExportState(DSGridStateMachine dsExportState) {
        this.dsExportState = dsExportState;
    }

    /**
     * Gets the result tab DB.
     *
     * @return the result tab DB
     */
    public Database getResultTabDB() {
        return resultTabDB;
    }

    /**
     * Sets the result tab DB.
     *
     * @param resultTabDB the new result tab DB
     */
    public void setResultTabDB(Database resultTabDB) {
        this.resultTabDB = resultTabDB;
    }

    /**
     * Enable disable on search.
     *
     * @param enable the enable
     * @param isConnected the is connected
     */
    public void enableDisableOnSearch(boolean enable, boolean isConnected) {
        isSearched = enable;
        if (!this.refresh.isDisposed()) {
            IDSEditGridDataProvider editDP = (IDSEditGridDataProvider) dataProvider;
            boolean isEdited = editDP.isGridDataEdited();
            this.refresh.setEnabled(!isSearched && !isEdited && isConnected);
            updateRefreshButtonTooltip(!isSearched && !isEdited && isConnected);
        }
    }

    /**
     * Enable disable on search non-edit
     *
     * @param enable the enable
     * @param isConnected the is connected
     */
    public void enableDisableOnSearchNonEdit(boolean enable, boolean isConnected) {
        isSearched = enable;
        if (!this.refresh.isDisposed()) {
            this.refresh.setEnabled(!isSearched && isConnected);
        }
    }

    private boolean checkForConditionalEnableDisable(boolean isDbConnected) {
        if (dataProvider instanceof IObjectPropertyData) {
            String objectPropertyName = ((IObjectPropertyData) dataProvider).getObjectPropertyName();
            if (objectPropertyName != null && objectPropertyName.equals("General")) {
                return false;
            } else {
                return true;
            }
        }
        return isDbConnected;
    }

    /**
     * Adds the batch drop toolbar.
     *
     * @param eventTable the event table
     */
    public void addBatchDropToolbar(final DSEventTable eventTable) {
        int maxObjectNameChars = 50;
        int maxObjectsLoadableChars = 6;
        Color color = new Color(null, 0xf9, 0xf9, 0xf9);

        ToolItem separator = new ToolItem(toolbar, SWT.SEPARATOR);
        separator.setEnabled(true);

        GC gc = new GC(toolbarComposite.getShell());
        FontMetrics fontMetrics = gc.getFontMetrics();

        Composite comp = getComposite();

        Label name = new Label(comp, SWT.NONE);
        name.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_DB_NAME));
        name.setLayoutData(new GridData());

        addBatchDropObjectParentLbl(maxObjectNameChars, color, fontMetrics, comp);

        addBatchDropAtomicBtn(comp);

        addBatchDropCascadeStartStopBtns(comp);

        addRunsImageLbl(comp);

        addBatchDropRunsLbl(maxObjectsLoadableChars, color, fontMetrics, comp);

        addErrorImgAndLbl(comp);

        addBatchDropErrorLbl(maxObjectsLoadableChars, color, fontMetrics, comp);

        this.batchDropAtomicBtn.addSelectionListener(new BatchDropAtomicBtnSelectionListener(eventTable));

        this.batchDropCascadeBtn.addSelectionListener(new BatchDropCascadeBtnSelectionListener(eventTable));

        this.batchDropStartBtn.addListener(SWT.Selection, batchDropStartBtnSelectionListener(eventTable));

        this.batchDropStopBtn.addListener(SWT.Selection, batchDropStopBtnSelectionListener(eventTable));

        gc.dispose();
    }

    private void addBatchDropErrorLbl(int maxObjectsLoadableChars, Color color, FontMetrics fontMetrics,
            Composite comp) {
        this.batchDropErrors = new Label(comp, SWT.NONE);
        this.batchDropErrors.setText("0");
        GridData gd2 = new GridData();
        gd2.widthHint = fontMetrics.getAverageCharWidth() * (maxObjectsLoadableChars + 1);
        gd2.horizontalAlignment = SWT.BEGINNING;
        this.batchDropErrors.setLayoutData(gd2);
        this.batchDropErrors.setBackground(color);
    }

    private void addErrorImgAndLbl(Composite comp) {
        Label errorsImg = new Label(comp, SWT.NONE);
        errorsImg.setLayoutData(new GridData());
        errorsImg.setImage(IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_ERRORS, this.getClass()));

        Label errors = new Label(comp, SWT.NONE);
        errors.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_LBL_ERRORS));
        errors.setLayoutData(new GridData());
    }

    private void addBatchDropRunsLbl(int maxObjectsLoadableChars, Color color, FontMetrics fontMetrics,
            Composite comp) {
        this.batchDropRuns = new Label(comp, SWT.NONE);
        this.batchDropRuns.setText("0/0");
        GridData gd1 = new GridData();
        gd1.widthHint = fontMetrics.getAverageCharWidth() * (2 * maxObjectsLoadableChars + 3);
        gd1.horizontalAlignment = SWT.BEGINNING;
        this.batchDropRuns.setLayoutData(gd1);
        this.batchDropRuns.setBackground(color);
    }

    private void addRunsImageLbl(Composite comp) {
        Label runsImg = new Label(comp, SWT.NONE);
        runsImg.setLayoutData(new GridData());
        runsImg.setImage(IconUtility.getIconImage(IiconPath.ICON_DROP_OBJECTS_RUNS, this.getClass()));
        Label runs = new Label(comp, SWT.NONE);
        runs.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_LBL_RUNS));
        runs.setLayoutData(new GridData());
    }

    private void addBatchDropCascadeStartStopBtns(Composite comp) {
        this.batchDropCascadeBtn = new Button(comp, SWT.CHECK);
        this.batchDropCascadeBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_CASCADE));

        this.batchDropStartBtn = new Button(comp, SWT.PUSH);
        this.batchDropStartBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_START));

        this.batchDropStopBtn = new Button(comp, SWT.PUSH);
        this.batchDropStopBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STOP));
        this.batchDropStopBtn.setEnabled(false);
    }

    private void addBatchDropAtomicBtn(Composite comp) {
        this.batchDropAtomicBtn = new Button(comp, SWT.CHECK);
        this.batchDropAtomicBtn.setText(MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_ATOMIC));
        if (null != dataProvider.getDatabse()) {
            this.batchDropAtomicBtn.setEnabled(dataProvider.getDatabse().hasSupportForAtomicDDL());
        }
    }

    private void addBatchDropObjectParentLbl(int maxObjectNameChars, Color color, FontMetrics fontMetrics,
            Composite comp) {
        this.batchDropObjectsParent = new Label(comp, SWT.NONE);
        this.batchDropObjectsParent.setText("");
        GridData gd = new GridData();
        gd.widthHint = fontMetrics.getAverageCharWidth() * maxObjectNameChars;
        gd.horizontalAlignment = SWT.BEGINNING;
        this.batchDropObjectsParent.setLayoutData(gd);
        this.batchDropObjectsParent.setBackground(color);
    }

    private Composite getComposite() {
        Composite comp = createComposite(toolbarComposite);
        GridLayout gl = (GridLayout) comp.getLayout();
        gl.numColumns = 13;
        gl.marginLeft = 5;
        gl.horizontalSpacing = 7;
        comp.setLayout(gl);
        return comp;
    }

    private Listener batchDropStopBtnSelectionListener(final DSEventTable eventTable) {
        return new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_STOP_OPTION, null));
            }
        };
    }

    private Listener batchDropStartBtnSelectionListener(final DSEventTable eventTable) {
        return new Listener() {

            /**
             * Handle event.
             *
             * @param e the e
             */
            public void handleEvent(Event e) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_START_OPTION, null));
            }
        };
    }

    /**
     * The listener interface for receiving batchDropCascadeBtnSelection events.
     * The class that is interested in processing a batchDropCascadeBtnSelection
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addBatchDropCascadeBtnSelectionListener<code> method. When the
     * batchDropCascadeBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * BatchDropCascadeBtnSelectionEvent
     */
    private class BatchDropCascadeBtnSelectionListener implements SelectionListener {
        private DSEventTable eventTable;

        /**
         * Instantiates a new batch drop cascade btn selection listener.
         *
         * @param eventTable the event table
         */
        public BatchDropCascadeBtnSelectionListener(DSEventTable eventTable) {
            this.eventTable = eventTable;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_CASCADE_OPTION, batchDropCascadeBtn));
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

        }
    }

    /**
     * The listener interface for receiving batchDropAtomicBtnSelection events.
     * The class that is interested in processing a batchDropAtomicBtnSelection
     * event implements this interface, and the object created with that class
     * is registered with a component using the component's
     * <code>addBatchDropAtomicBtnSelectionListener<code> method. When the
     * batchDropAtomicBtnSelection event occurs, that object's appropriate
     * method is invoked.
     *
     * BatchDropAtomicBtnSelectionEvent
     */
    private class BatchDropAtomicBtnSelectionListener implements SelectionListener {
        private DSEventTable eventTable;

        /**
         * Instantiates a new batch drop atomic btn selection listener.
         *
         * @param eventTable the event table
         */
        public BatchDropAtomicBtnSelectionListener(DSEventTable eventTable) {
            this.eventTable = eventTable;
        }

        @Override
        public void widgetSelected(SelectionEvent event) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_BATCHDROP_ATOMIC_OPTION, batchDropAtomicBtn));
        }

        @Override
        public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

        }
    }

    /**
     * Update batch drop atomic button.
     *
     * @param isEnabled the is enabled
     */
    public void updateBatchDropAtomicButton(boolean isEnabled) {
        if (!batchDropAtomicBtn.isDisposed()) {
            this.batchDropAtomicBtn.setEnabled(isEnabled);
        }
    }

    /**
     * Update batch drop cascade button.
     *
     * @param isEnabled the is enabled
     */
    public void updateBatchDropCascadeButton(boolean isEnabled) {
        if (!batchDropCascadeBtn.isDisposed()) {
            this.batchDropCascadeBtn.setEnabled(isEnabled);
        }
    }

    /**
     * Update batch drop start button.
     *
     * @param isEnabled the is enabled
     */
    public void updateBatchDropStartButton(boolean isEnabled) {
        if (!this.batchDropStartBtn.isDisposed()) {
            this.batchDropStartBtn.setEnabled(isEnabled);
        }
    }

    /**
     * Update batch drop stop button.
     *
     * @param isEnabled the is enabled
     */
    public void updateBatchDropStopButton(boolean isEnabled) {
        if (!this.batchDropStopBtn.isDisposed()) {
            this.batchDropStopBtn.setEnabled(isEnabled);
        }
    }

    /**
     * Update batch drop runs label.
     *
     * @param successDropCount the success drop count
     * @param totalDropCount the total drop count
     */
    public void updateBatchDropRunsLabel(int successDropCount, int totalDropCount) {
        if (!batchDropRuns.isDisposed()) {
            batchDropRuns.setText(successDropCount + "/" + totalDropCount);
        }
    }

    /**
     * Update batch drop errors label.
     *
     * @param errorDropCount the error drop count
     */
    public void updateBatchDropErrorsLabel(int errorDropCount) {
        if (!batchDropErrors.isDisposed()) {
            batchDropErrors.setText("" + errorDropCount);
        }
    }

    /**
     * Update batch drop object label.
     *
     * @param objectNameParam the object name param
     */
    public void updateBatchDropObjectLabel(String objectNameParam) {
        String objectName = objectNameParam;
        if (objectName.length() > 50) {
            objectName = objectName.substring(0, 47) + "...";
        }

        if (!batchDropObjectsParent.isDisposed()) {
            batchDropObjectsParent.setText(objectName);
        }
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.deleteObserver(this);
        }
        dataProvider = null;
        eventEditTable = null;
        resultTabDB = null;
        dsExportState = null;
        dsRefreshState = null;
        dsGridComponent = null;

        if (null != dsEncodingList) {
            this.dsEncodingList.clear();
            this.dsEncodingList = null;
        }
        if (null != toolManagerMap) {
            this.toolManagerMap.clear();
            this.toolManagerMap = null;
        }
        if (dataGrid != null && null != dataGrid.getDataGrid() && !dataGrid.getDataGrid().isDisposed()) {
            dataGrid.getDataGrid().removeLayerListener(iLayerListener);
            iLayerListener = null;
        }
        this.multiColumnSort = null;
        copyItem = null;
        copyAdvancedItem = null;
        exportAllItem = null;
        exportCurrentItem = null;
        toggleSearchArea = null;
        insertRow = null;
        deleteRow = null;
        commitRecord = null;
        refresh = null;
        rollbackChanges = null;
        pasteData = null;
        eventEditTable = null;
        forgetOption = null;
        if (!toolbar.isDisposed()) {
            toolbar.dispose();
        }
    }

    /**
     * Adds the item save plan.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemSavePlan(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.MENU_SAVE);
        this.savePlan = getToolItem(SWT.PUSH, null, IiconPath.ICO_SAVE_SQL, toolTipMsg);
        this.savePlan.setToolTipText(toolTipMsg);
        this.savePlan.addSelectionListener(savePlanSelectionListener(eventTable, stateMachine));

        stateMachine.addObserver(stateMachineSavePlanObserver());

    }

    private Observer stateMachineSavePlanObserver() {
        return new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (INSTANCE_LOCK) {
                                enableDisableSavePlanButton();
                            }
                        }
                    });

                }
            }
        };
    }

    private SelectionListener savePlanSelectionListener(final DSEventTable eventTable,
            DSGridStateMachine stateMachine) {
        return new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                    eventTable.sendEvent(
                            new DSEvent(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_SAVE_PLAN, new Observer() {
                                @Override
                                public void update(Observable observable, Object arg) {
                                    if (arg instanceof Boolean && !((boolean) arg)) {
                                        stateMachine.set(DSGridStateMachine.State.IDLE);
                                    }
                                }
                            }));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        };
    }

    /**
     * Enable disable save plan button.
     */
    protected void enableDisableSavePlanButton() {
        if (null != savePlan && !savePlan.isDisposed()) {
            savePlan.setEnabled(null != getDsExportState() && !getDsExportState().isExporting());
        }

    }

    /**
     * Enable disable refresh plan button.
     */
    public void enableDisableRefreshPlanButton() {
        if (null != refresh && !refresh.isDisposed()) {
            refresh.setEnabled(null != getDsRefreshState() && !getDsRefreshState().isRefreshing()
                    && UIElement.getInstance().isDatabaseConnected());
        }
    }

    /**
     * Adds the item tree view.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemTreeView(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.TOOLBAR_TREE_VIEW);
        this.treeView = getToolItem(SWT.PUSH, null, IiconPath.ICON_EXECUTION_PLAN_TREE_VIEW, toolTipMsg);
        this.treeView.setToolTipText(toolTipMsg);
        this.treeView.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TREE_VIEW, this));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });

        stateMachine.addObserver(new Observer() {

            @Override
            public void update(Observable arg0, Object arg1) {

            }

        });

    }

    /**
     * Adds the item text view.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addItemTextView(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.TOOLBAR_TEXT_VIEW);
        this.textView = getToolItem(SWT.PUSH, null, IiconPath.ICON_EXECUTION_PLAN_TEXT_VIEW, toolTipMsg);
        this.textView.setToolTipText(toolTipMsg);
        this.textView.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TEXT_VIEW, this));
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });

        stateMachine.addObserver(new Observer() {

            @Override
            public void update(Observable arg0, Object arg1) {

            }

        });

    }

    /**
     * Adds the refresh plan item.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addRefreshPlanItem(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_REFRESH);
        this.refresh = getToolItem(SWT.PUSH, null, IiconPath.ICO_REFRESH, toolTipMsg);
        this.refresh.setToolTipText(toolTipMsg);
        setEventEditTable(eventTable);
        this.refresh.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (stateMachine.set(DSGridStateMachine.State.REFRESHING)) {
                    eventTable
                            .sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_REFRESH, new Observer() {
                                @Override
                                public void update(Observable observable, Object arg) {
                                    if (arg instanceof Boolean && !((boolean) arg)) {
                                        stateMachine.set(DSGridStateMachine.State.IDLE);
                                    }
                                }
                            }));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable observable, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            synchronized (INSTANCE_LOCK) {
                                enableDisableRefreshPlanButton();
                            }
                        }
                    });

                }
            }
        });
        updateRefreshButtonTooltip(true);
    }

    /**
     * Adds the item show grid.
     *
     * @param eventTable the event table
     */
    public void addItemShowGrid(DSEventTable eventTable) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_SHOW_GRID);
        this.showGrid = getToolItem(SWT.CHECK, null, IiconPath.ICO_GRID, toolTipMsg);
        this.showGrid.setToolTipText(toolTipMsg);
        this.showGrid.setSelection(true);
        DataText dataText = dsGridComponent.getDataText();
        this.showGrid.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                showGrid.setSelection(true);
                dsGridComponent.updateGridStatusBarForDataGrid();
                gridAndTextButSwitchCount = 0;
                enableButtons();
                if (dataText.isSearchStatus()) {
                    dataText.changeButStatus(true);
                }
                if (showText.getSelection()) {
                    showText.setSelection(false);
                }
                // Display grid content
                dsGridComponent.hideStatusBar();
                dsGridComponent.getDataGrid().doShowGrid(dsGridComponent.getDataGrid().getParent());
                dataText.doHideText(dataText.getTextParent());
                dataText.doHideText(dataText.getSearchParent());
                dsGridComponent.showStatusBar();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });
    }

    /**
     * Adds the item show text.
     *
     * @param eventTable the event table
     */
    public void addItemShowText(DSEventTable eventTable) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_SHOW_TEXT);
        this.showText = getToolItem(SWT.CHECK, null, IiconPath.ICO_TEXT, toolTipMsg);
        this.showText.setToolTipText(toolTipMsg);
        DataText dataText = dsGridComponent.getDataText();
        if (dataText.isInitDataTextFlag()) {
            showText.setEnabled(false);
        }
        this.showText.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                showText.setSelection(true);
                dsGridComponent.updateGridStatusBarForDataText(dataText.isPartloaded(), dataText.getLoadedRowCnt());
                gridAndTextButSwitchCount++;
                if (gridAndTextButSwitchCount == 1) {
                    recordButtonStatus();
                }
                if (dataText.isSearchStatus()) {
                    dataText.changeButStatus(false);
                }
                if (showGrid.getSelection()) {
                    showGrid.setSelection(false);
                }
                disableButtons();
                dsGridComponent.hideStatusBar();
                dsGridComponent.getDataGrid().doHideGrid(dsGridComponent.getDataGrid().getParent());
                if (dataText.isSearchHideOrShowFlag()) {
                    dataText.doShowText(dataText.getSearchParent());
                } else {
                    dataText.doShowText(dataText.getTextParent());
                }
                dsGridComponent.showStatusBar();
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {
                // Ignore
            }
        });
    }

    /**
     * Disable buttons.
     */
    private void disableButtons() {
        if (null != copyAdvancedItem) {
            copyAdvancedItem.setEnabled(false);
        }
        if (null != exportAllItem) {
            exportAllItem.setEnabled(false);
        }

        if (null != exportCurrentItem) {
            exportCurrentItem.setEnabled(false);
        }

        IHandlerUtilities.setToolItemEnabled(exportAllItem, IHandlerUtilities.getExportDataSelectionOptions());
        IHandlerUtilities.setToolItemEnabled(exportCurrentItem, IHandlerUtilities.getExportDataSelectionOptions());
        IHandlerUtilities.setToolItemEnabled(copyAdvancedItem, IHandlerUtilities.getExportDataSelectionOptions());

        if (null != insertRow) {
            insertRow.setEnabled(false);
        }
        if (null != deleteRow) {
            deleteRow.setEnabled(false);
        }
        if (null != commitRecord) {
            commitRecord.setEnabled(false);
        }
        if (null != rollbackChanges) {
            rollbackChanges.setEnabled(false);
        }
        if (null != forgetOption) {
            forgetOption.setEnabled(false);
        }
        if (null != pasteData) {
            pasteData.setEnabled(false);
        }
        if (null != multiColumnSort) {
            multiColumnSort.setEnabled(false);
        }
        if (null != clearSort) {
            clearSort.setEnabled(false);
        }
        if (null != this.combo) {
            combo.setEnabled(false);
        }
    }

    /**
     * Enable buttons.
     */
    public void enableButtons() {
        enableCopyAdvancedItem();
        enableExportCurrentItem();
        enableExportAllItem();
        enableInsertRow();
        enableDeleteRow();
        enableCommitRecord();
        enableRollbackChanges();
        enableForgetOption();
        enablePasteData();
        enableMultiColumnSort();
        enableClearSort();
        enableCombo();
    }

    private void enableCombo() {
        if (null != combo && comboFlag) {
            combo.setEnabled(true);
        }
    }

    private void enableClearSort() {
        if (null != clearSort && clearSortFlag) {
            clearSort.setEnabled(true);
        }
    }

    private void enableMultiColumnSort() {
        if (null != multiColumnSort && multiColumnSortFlag) {
            multiColumnSort.setEnabled(true);
        }
    }

    private void enablePasteData() {
        if (null != pasteData && pasteDataFlag) {
            pasteData.setEnabled(true);
        }
    }

    private void enableForgetOption() {
        if (null != forgetOption && forgetOptionFlag) {
            forgetOption.setEnabled(true);
        }
    }

    private void enableRollbackChanges() {
        if (null != rollbackChanges && rollbackChangesFlag) {
            rollbackChanges.setEnabled(true);
        }
    }

    private void enableCommitRecord() {
        if (null != commitRecord && commitRecordFlag) {
            commitRecord.setEnabled(true);
        }
    }

    private void enableDeleteRow() {
        if (null != deleteRow && deleteRowFlag) {
            deleteRow.setEnabled(true);
        }
    }

    private void enableInsertRow() {
        if (null != insertRow && insertRowFlag) {
            insertRow.setEnabled(true);
        }
    }

    private void enableExportAllItem() {
        if (null != exportAllItem && exportAllItemFlag) {
            exportAllItem.setEnabled(true);
        }

        IHandlerUtilities.setToolItemEnabled(exportAllItem, IHandlerUtilities.getExportDataSelectionOptions());

    }

    private void enableExportCurrentItem() {
        if (null != exportCurrentItem && exportCurrentItemFlag) {
            exportCurrentItem.setEnabled(true);
        }

        IHandlerUtilities.setToolItemEnabled(exportCurrentItem, IHandlerUtilities.getExportDataSelectionOptions());
    }

    private void enableCopyAdvancedItem() {
        if (null != copyAdvancedItem && copyAdvanceFlag) {
            copyAdvancedItem.setEnabled(true);
        }
        IHandlerUtilities.setToolItemEnabled(copyAdvancedItem, IHandlerUtilities.getExportDataSelectionOptions());
    }

    private void recordButtonStatus() {
        recordCopyAdvancedItemStatus();
        recordExportAllItemStatus();
        recordExportCurrentItemStatus();
        recordInsertRowStatus();
        recordDeleteRowStatus();
        recordCommitRecordStatus();
        recordRollbackChangesStatus();
        recordForgetOptionStatus();
        recordPasteDataStatus();
        recordMultiColumnSortStatus();
        recordClearSortStatus();

        recordComboStatus();
    }

    private void recordComboStatus() {
        if (null != combo && combo.isEnabled()) {
            this.comboFlag = true;
        } else {
            this.comboFlag = false;
        }
    }

    private void recordClearSortStatus() {
        if (null != clearSort && clearSort.isEnabled()) {
            this.clearSortFlag = true;
        } else {
            this.clearSortFlag = false;
        }
    }

    private void recordMultiColumnSortStatus() {
        if (null != multiColumnSort && multiColumnSort.isEnabled()) {
            this.multiColumnSortFlag = true;
        } else {
            this.multiColumnSortFlag = false;
        }
    }

    private void recordPasteDataStatus() {
        if (null != pasteData && pasteData.isEnabled()) {
            this.pasteDataFlag = true;
        } else {
            this.pasteDataFlag = false;
        }
    }

    private void recordForgetOptionStatus() {
        if (null != forgetOption && forgetOption.isEnabled()) {
            this.forgetOptionFlag = true;
        } else {
            this.forgetOptionFlag = false;
        }
    }

    private void recordRollbackChangesStatus() {
        if (null != rollbackChanges && rollbackChanges.isEnabled()) {
            this.rollbackChangesFlag = true;
        } else {
            this.rollbackChangesFlag = false;
        }
    }

    private void recordCommitRecordStatus() {
        if (null != commitRecord && commitRecord.isEnabled()) {
            this.commitRecordFlag = true;
        } else {
            this.commitRecordFlag = false;
        }
    }

    private void recordDeleteRowStatus() {
        if (null != deleteRow && deleteRow.isEnabled()) {
            this.deleteRowFlag = true;
        } else {
            this.deleteRowFlag = false;
        }
    }

    private void recordInsertRowStatus() {
        if (null != insertRow && insertRow.isEnabled()) {
            this.insertRowFlag = true;
        } else {
            this.insertRowFlag = false;
        }
    }

    private void recordExportCurrentItemStatus() {
        if (null != exportCurrentItem && exportCurrentItem.isEnabled()) {
            this.exportCurrentItemFlag = true;
        } else {
            this.exportCurrentItemFlag = false;
        }
    }

    private void recordExportAllItemStatus() {
        if (null != exportAllItem && exportAllItem.isEnabled()) {
            this.exportAllItemFlag = true;
        } else {
            this.exportAllItemFlag = false;
        }
    }

    private void recordCopyAdvancedItemStatus() {
        if (null != copyAdvancedItem && copyAdvancedItem.isEnabled()) {
            this.copyAdvanceFlag = true;
        } else {
            this.copyAdvanceFlag = false;
        }
    }

    /**
     * Updata but status on init data text.
     */
    public void updataButStatusOnInitDataText() {
        if (null != showText && !showText.isDisposed()) {
            showText.setEnabled(true);
        }
        if (null != refresh) {
            refresh.setEnabled(true);
        }
        if (null != combo) {
            combo.setEnabled(true);
        }
    }

    /**
     * Updata but status on load data text.
     *
     * @param loadStatus the load status
     */
    public void updataButStatusOnLoadDataText(boolean loadStatus) {
        if (UIElement.getInstance().getSqlTerminalModel() == null) {
            return;
        }
        boolean isDbConnected = UIElement.getInstance().isDatabaseConnected();
        if (isShowGridOrShowTextSelect()) {
            showText.setEnabled(isDbConnected && loadStatus);
            if (null != combo) {
                combo.setEnabled(loadStatus);
            }
        } else {
            showGrid.setEnabled(isDbConnected && loadStatus);
        }
        if (null != refresh) {
            refresh.setEnabled(isDbConnected && loadStatus);
        }
    }

    /**
     * Updata but status on load data text exception.
     */
    public void updataButStatusOnLoadDataTextException() {
        if (isShowGridOrShowTextSelect()) {
            showText.setEnabled(false);
            if (null != combo) {
                combo.setEnabled(true);
            }
            if (null != refresh) {
                refresh.setEnabled(true);
            }
        } else {
            showGrid.setEnabled(false);
            if (null != refresh) {
                refresh.setEnabled(false);
            }
        }
    }

    /**
     * Updata but status on init data text cancel or exception.
     */
    public void updataButStatusOnInitDataTextCancelOrException() {
        if (null != showText) {
            showText.setEnabled(false);
        }
        if (null != refresh) {
            refresh.setEnabled(true);
        }
        if (null != combo) {
            combo.setEnabled(true);
        }
    }

    /**
     * Checks if is show grid or show text select.
     *
     * @return true, if is show grid or show text select
     */
    public boolean isShowGridOrShowTextSelect() {
        if (this.showGrid.getSelection()) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is refresh but enable.
     *
     * @return true, if is refresh but enable
     */
    public boolean isRefreshButEnable() {
        if (null != refresh && refresh.isEnabled()) {
            return true;
        }
        return false;
    }

    /**
     * Adds the load more record.
     *
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public void addLoadMoreRecord(final DSEventTable eventTable, DSGridStateMachine stateMachine) {
        String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.LOAD_MORE_RECORD_TOOL_ITEM);
        this.loadMoreRecord = getToolItem(SWT.PUSH, null, IiconPath.ICON_LOAD_MORE_RECORD, toolTipMsg);
        this.loadMoreRecord.setToolTipText(toolTipMsg);
        this.loadMoreRecord.setEnabled(false);

        if (!dsGridComponent.getDataGrid().getDataProvider().isEndOfRecords()) {
            this.loadMoreRecord.setEnabled(true);
        }

        this.loadMoreRecord.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                if (stateMachine.set(DSGridStateMachine.State.LOADING)) {
                    eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY,
                            dsGridComponent.getDataGrid().getDataProvider()));
                }
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent selectDefaultEvent) {

            }
        });
    }

    /**
     * Reset load more record status.
     */
    public void resetLoadMoreRecordStatus() {
        DSGridStateMachine stateMachine = this.dsGridComponent.getStateMachine();
        if (stateMachine == null || this.loadMoreRecord == null || this.loadMoreRecord.isDisposed()) {
            return;
        }

        if (stateMachine.isLoading() || stateMachine.isExporting() || stateMachine.isRefreshing()) {
            this.loadMoreRecord.setEnabled(false);
        } else {
            DataGrid dGrid = this.dsGridComponent.getDataGrid();
            if (dGrid != null && dGrid.getDataProvider() != null && !dGrid.getDataProvider().isEndOfRecords()) {
                this.loadMoreRecord.setEnabled(true);
            }
        }
    }
}
