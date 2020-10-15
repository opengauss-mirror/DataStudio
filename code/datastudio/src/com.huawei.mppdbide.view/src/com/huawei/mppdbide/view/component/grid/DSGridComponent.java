/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid;

import java.util.Observable;
import java.util.Observer;

import javax.annotation.PreDestroy;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ToolItem;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.DSGridStateMachine.State;
import com.huawei.mppdbide.view.component.grid.core.DSCursorResultSetTable;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.DataText;
import com.huawei.mppdbide.view.component.grid.listeners.MultiColumnSortPoUpListener;
import com.huawei.mppdbide.view.ui.DatabaseListControl;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class DSGridComponent.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DSGridComponent implements IDSGridUIListenable, Observer {
    // Children

    /**
     * The query area.
     */
    protected GridQueryArea queryArea;

    /**
     * The search area.
     */
    protected GridSearchArea searchArea;

    /**
     * The toolbar.
     */
    protected GridToolbar toolbar;

    /**
     * The grid.
     */
    protected DataGrid grid;

    /**
     * The text.
     */
    protected DataText text;

    /**
     * The status bar.
     */
    protected GridStatusBar statusBar;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;

    /**
     * The state machine.
     */
    protected DSGridStateMachine stateMachine;

    /**
     * The ui pref.
     */
    protected volatile IGridUIPreference uiPref;

    /**
     * The data provider.
     */
    protected IDSGridDataProvider dataProvider;
    private GridSearchAreaToDataGrid gridSearchAreaToDataGrid;
    private MultiColumnSortPoUpListener multiColumnSortPoUpListener;
    private final Object INSTANCE_LOCK = new Object();

    /**
     * Instantiates a new DS grid component.
     *
     * @param uiPref the ui pref
     * @param dataProvider the data provider
     */
    public DSGridComponent(IGridUIPreference uiPref, IDSGridDataProvider dataProvider) {
        this.uiPref = uiPref;
        this.dataProvider = dataProvider;
        this.eventTable = new DSEventTable();
        this.stateMachine = new DSGridStateMachine();
        this.grid = null;
        this.statusBar = null;
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.addObserver(this);
        }
    }

    /**
     * Creates the components.
     *
     * @param parent the parent
     */
    public void createComponents(Composite parent) {
        Composite gridComposite = createNewComposite(parent);
        createToolbarAndSearchArea(gridComposite);

        addToolbarOptionsAndUiPrefs(false);

        addUiPrefs(false);

        if (null != this.grid && (!uiPref.isEnableEdit() || uiPref.isEditQueryResultsFlow())) {
            ((DataGrid) this.grid).configureLoadOnScroll(this.eventTable);
            if (null != this.text) {
                ((DataText) this.text).configureLoadOnScroll(this.eventTable);
            }

        }
        if (uiPref.isShowQueryArea()) {
            this.queryArea.doHideQueryArea();
            this.toolbar.updateToggleQueryAreaImage();
        }

        this.eventTable.sendEvent(new DSEvent(LISTEN_TYPE_ON_GRID_CREATION, toolbar));
        statusMsgEventRegister();
        if (null != this.grid) {
            this.grid.setFocus();
        }
    }

    /**
     * return component
     * 
     * @param parent the parent
     * @return the cursor popup component
     */
    public Composite cursorPopupComponents(Composite parent, boolean isCursorPopup) {
        Composite gridComposite = createNewComposite(parent);

        createCursorResultGridDataArea(gridComposite);
        addToolbarOptionsAndUiPrefs(true);
        addUiPrefs(isCursorPopup);

        if (null != this.grid && (!uiPref.isEnableEdit() || uiPref.isEditQueryResultsFlow())) {
            ((DataGrid) this.grid).configureLoadOnScroll(this.eventTable);
            if (null != this.text) {
                ((DataText) this.text).configureLoadOnScroll(this.eventTable);
            }

        }
        if (null != this.grid) {
            this.grid.setFocus();
        }

        return gridComposite;
    }

    /**
     * create grid area for cursor popup window
     * 
     * @param gridComposite the gridComposite
     */
    private void createCursorResultGridDataArea(Composite gridComposite) {
        this.toolbar = createToolBar(gridComposite, this.dataProvider);
        this.searchArea = createSearchArea(gridComposite);
        if (uiPref.isNeedCreateTextMode()) {
            this.text = createDataTextArea(gridComposite);
            this.searchArea.setText(this.text);
            this.searchArea.setToolbar(toolbar);
        }
        this.grid = createDataGridArea(gridComposite);

        this.searchArea.setGrid(this.grid);
        this.toolbar.addItemCopy(this.stateMachine, false);
        this.toolbar.setDsExportState(this.stateMachine);

        if (this.uiPref.isNeedAdvancedCopy()) {
            this.toolbar.addItemAdvancedCopy(this.stateMachine);
        }

    }

    private void addUiPrefs(boolean isCursorPopup) {
        if (uiPref.isEnableSort()) {
            ToolItem beginSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
            beginSeparator.setEnabled(true);
            multiColumnSortPoUpListener = new MultiColumnSortPoUpListener(this.grid);
            this.toolbar.addItemMultiSort(multiColumnSortPoUpListener);
            this.toolbar.addItemClearSort();
        }
        if (uiPref.isAddBatchDropTool()) {
            this.toolbar.addBatchDropToolbar(this.eventTable);
        }
        if (uiPref.isEnableEdit()) {
            this.toolbar.handleDataEditEvent(true);
        }
        if (uiPref.isNeedCreateTextMode()) {
            // Add grid text button
            ToolItem beginSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
            beginSeparator.setEnabled(true);
            this.toolbar.addItemShowGrid(this.eventTable);
            this.toolbar.addItemShowText(this.eventTable);
        }
        if (uiPref.isShowLoadMoreRecordButton()) {
            ToolItem beginSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
            beginSeparator.setEnabled(true);
            this.toolbar.addLoadMoreRecord(this.eventTable, this.stateMachine);
        }
        if (uiPref.isIncludeEncoding()) {
            this.toolbar.addEncoding(this.eventTable, uiPref.getDefaultEncoding());
        }

        if (uiPref.isShowStatusBar() && !isCursorPopup) {
            this.statusBar.updateDataProviderSummaryData(this.dataProvider);
        }
    }

    private void addToolbarOptionsAndUiPrefs(boolean isCursorPopup) {
        if (this.uiPref.isSupportDataExport()) {
            if (this.uiPref.isAddItemExportAll() && !this.dataProvider.isFuncProcExport()) {
                this.toolbar.addItemExportAll(this.eventTable, this.stateMachine, isCursorPopup);
            }
            this.toolbar.addItemExportCurrentPage(this.eventTable, this.stateMachine);
        }

        // check for edit table data
        if (uiPref.isEnableEdit() && ((IDSEditGridDataProvider) dataProvider).isEditSupported()) {
            ToolItem beginSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
            beginSeparator.setEnabled(true);

            this.toolbar.addItemPasteCells(this.eventTable, this.stateMachine);
            boolean needToBeValidated = isNeedToValidate();
            if (needToBeValidated) {
                this.toolbar.addItemInsertRow(this.eventTable, this.stateMachine);
                this.toolbar.addItemDeleteRow(this.eventTable, this.stateMachine);
            }
            this.toolbar.addItemCommitRecord(this.eventTable, this.stateMachine);
            this.toolbar.addItemRollBackChanges(this.eventTable, this.stateMachine);
            this.toolbar.addForgetItem(this.eventTable);

            ToolItem endSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
            endSeparator.setEnabled(true);
        }
        if (uiPref.isRefreshSupported()) {
            this.toolbar.addRefreshItem(this.eventTable, this.stateMachine);
        }
        if (uiPref.isShowQueryArea()) {
            this.toolbar.addItemToggleQueryArea(this.queryArea);
        }

        // check for edit table data

        this.toolbar.addItemToggleSearchArea(this.searchArea);
    }

    private void createToolbarAndSearchArea(Composite gridComposite) {
        this.toolbar = createToolBar(gridComposite, this.dataProvider);
        this.searchArea = createSearchArea(gridComposite);
        if (uiPref.isShowQueryArea()) {
            this.queryArea = createQueryArea(gridComposite);
        }
        if (uiPref.isNeedCreateTextMode()) {
            this.text = createDataTextArea(gridComposite);
            this.searchArea.setText(this.text);
            this.searchArea.setToolbar(toolbar);
        }
        this.grid = createDataGridArea(gridComposite);

        if (uiPref.isShowStatusBar()) {
            this.statusBar = createStatusBar(gridComposite, this.dataProvider);
        }
        this.searchArea.setGrid(this.grid);
        this.toolbar.addItemCopy(this.stateMachine, false);
        this.toolbar.setDsExportState(this.stateMachine);

        if (this.uiPref.isNeedAdvancedCopy()) {
            this.toolbar.addItemAdvancedCopy(this.stateMachine);
        }
    }

    private DataText createDataTextArea(Composite gridComposite) {
        // Create a text container
        Composite textComposite = createNewComposite(gridComposite);
        // Create a search container
        Composite searchComposite = createNewComposite(gridComposite);
        this.text = new DataText(this.uiPref, this.dataProvider, this.eventTable, this.stateMachine, this);
        this.text.setInitDataTextFlag(true);
        this.text.setSearchParent(searchComposite);
        this.text.setSearArea(searchArea);
        if (uiPref.isEnableEdit() && ((IDSEditGridDataProvider) dataProvider).isEditSupported()) {
            if (isNeedToValidate()) {
                this.text.setShowOrHiderefFlag(true);
            }
        }
        text.createComponent(textComposite);
        text.createSearchComponent();
        if (uiPref.isNeedCreateTextMode()) {
            doHideTextComposite(textComposite);
            doHideTextComposite(searchComposite);
        }
        return text;
    }

    /**
     * Do hide text composite.
     *
     * @param textComposite the text composite
     */
    protected void doHideTextComposite(Composite textComposite) {
        GridUIUtils.toggleCompositeSectionVisibility(textComposite, true, null, false);
    }

    private boolean isNeedToValidate() {
        return !(dataProvider instanceof DSObjectPropertiesGridDataProvider)
                || (dataProvider instanceof DSObjectPropertiesGridDataProvider
                        && !(((DSObjectPropertiesGridDataProvider) dataProvider)
                                .getObjectPropertyObject() instanceof PropertiesUserRoleImpl));
    }

    /**
     * Status msg event register.
     */
    protected void statusMsgEventRegister() {
        if (this.uiPref.isShowStatusBar()) {
            addListener(LISTEN_TYPE_POST_GRID_DATA_LOAD, this.statusBar.getUpdateMessageListener());
            addListener(LISTEN_TYPE_ON_ERROR, this.statusBar.getDataLoadErrorListener(this.stateMachine));
            addListener(LISTEN_EDITTABLE_COMMIT_STATUS, this.statusBar.getUpdateEditTableStatus());
        }
        if (this.grid != null) {
            addListener(LISTEN_EDITTABLE_COMMIT_STATUS, ((DataGrid) this.grid).getCommitStatusListener());
        }
        addListener(LISTEN_EDITTABLE_REMEMBERED_USER_OPTION, this.toolbar.getRemberUserOptionListener());
    }

    /**
     * Gets the data edit listener.
     *
     * @return the data edit listener
     */
    public IDSListener getDataEditListener() {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (INSTANCE_LOCK) {

                            if (uiPref.isEnableEdit()) {
                                Database db = dataProvider.getDatabse();
                                if (db != null) {
                                    DSGridComponent.this.toolbar.handleDataEditEvent(db.isConnected());
                                }
                                if (uiPref.isShowStatusBar()) {
                                    DSGridComponent.this.statusBar.handleDataEditEvent();
                                }
                            }
                        }
                    }
                });
            }
        };

    }

    /**
     * Save sort state.
     */
    public void saveSortState() {
        if (null == this.grid) {
            return;
        }
        this.grid.saveSortState();
    }

    /**
     * Restore last sort state.
     */
    public void restoreLastSortState() {
        if (null == this.grid) {
            return;
        }
        this.grid.restoreLastSortState(null);
    }

    /**
     * Save reorder state.
     */
    public void saveReorderState() {
        if (null == this.grid) {
            return;
        }
        this.grid.saveReorderState();
    }

    /**
     * Restore last reorder state.
     */
    public void restoreLastReorderState() {
        if (null == this.grid) {
            return;
        }
        this.grid.restoreReorderState();
    }

    /**
     * Creates the query area.
     *
     * @param gridComposite the grid composite
     * @return the grid query area
     */
    protected GridQueryArea createQueryArea(Composite gridComposite) {
        GridQueryArea area = new GridQueryArea(this.dataProvider);
        if (this.dataProvider.getDatabse() != null) {
            area.setSQLSyntax(this.dataProvider.getDatabse().getSqlSyntax());
        }
        area.createComponent(gridComposite, false);
        return area;
    }

    /**
     * Creates the search area.
     *
     * @param gridComposite the grid composite
     * @return the grid search area
     */
    protected GridSearchArea createSearchArea(Composite gridComposite) {
        GridSearchArea area = new GridSearchArea();
        area.createComponent(gridComposite, stateMachine);
        return area;
    }

    /**
     * Creates the tool bar.
     *
     * @param gridComposite the grid composite
     * @param gridDataProvider the grid data provider
     * @return the grid toolbar
     */
    protected GridToolbar createToolBar(Composite gridComposite, IDSGridDataProvider gridDataProvider) {
        GridToolbar gridToolbar = new GridToolbar();
        stateMachine.addObserver(gridToolbar);
        gridToolbar.createComponent(gridComposite, gridDataProvider, this);
        return gridToolbar;
    }

    /**
     * Creates the data grid area.
     *
     * @param gridComposite the grid composite
     * @return the data grid
     */
    protected DataGrid createDataGridArea(Composite gridComposite) {
        Composite composite = new Composite(gridComposite, SWT.None);
        DSGridComponent.setLayoutProperties(composite);
        DataGrid dataGrid = null;

        if (this.uiPref.isIncludeEncoding() && dataProvider instanceof DSResultSetGridDataProvider) {
            ((DSResultSetGridDataProvider) dataProvider).setIncludeEncoding(true);
            ((DSResultSetGridDataProvider) dataProvider).changeEncoding(this.uiPref.getDefaultEncoding());
        }

        dataGrid = createDataGrid(composite);
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_POST_GRID_DATA_LOAD, dataProvider));

        return dataGrid;
    }

    /**
     * Creates the data grid.
     *
     * @param composite the composite
     * @return the data grid
     */
    protected DataGrid createDataGrid(Composite composite) {
        DataGrid dataGrid;
        dataGrid = new DataGrid(this.uiPref, this.dataProvider, this.eventTable, this.stateMachine);
        dataGrid.createComponent(composite);
        return dataGrid;
    }

    /**
     * Creates the status bar.
     *
     * @param gridComposite the grid composite
     * @param gridDataProvider the grid data provider
     * @return the grid status bar
     */
    protected GridStatusBar createStatusBar(Composite gridComposite, IDSGridDataProvider gridDataProvider) {
        GridStatusBar gridStatusBar = new GridStatusBar();
        gridStatusBar.createComponent(gridComposite, stateMachine, gridDataProvider);
        return gridStatusBar;
    }

    /**
     * Creates the new composite.
     *
     * @param parent the parent
     * @return the composite
     */
    protected Composite createNewComposite(Composite parent) {
        DSGridComponent.setLayoutProperties(parent);
        Composite gridComposite = new Composite(parent, SWT.BORDER);
        DSGridComponent.setLayoutProperties(gridComposite);

        return gridComposite;
    }

    /**
     * Sets the layout properties.
     *
     * @param parent the new layout properties
     */
    public static void setLayoutProperties(Composite parent) {
        GridLayout gridlayout = new GridLayout();

        gridlayout.marginTop = 0;
        gridlayout.marginBottom = 0;
        gridlayout.marginRight = 0;
        gridlayout.marginLeft = 0;
        gridlayout.marginHeight = 0;
        gridlayout.marginWidth = 0;
        gridlayout.verticalSpacing = 0;
        gridlayout.horizontalSpacing = 0;
        parent.setLayout(gridlayout);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(parent);
    }

    /**
     * Adds the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    @Override
    public void addListener(int type, IDSListener listener) {
        eventTable.hook(type, listener);
    }

    /**
     * Removes the listener.
     *
     * @param type the type
     * @param listener the listener
     */
    @Override
    public void removeListener(int type, IDSListener listener) {
        eventTable.unhook(type, listener);
    }

    /**
     * On pre destroy.
     */
    @PreDestroy
    public void onPreDestroy() {
        // Cleanup
        DatabaseListControl databaseListControl = UIElement.getInstance().getDatabaseListControl();
        if (null != databaseListControl) {
            databaseListControl.deleteObserver(this);
        }
        if (null != this.stateMachine) {
            this.stateMachine.deleteObservers();
            this.stateMachine.preDestroy();
            this.stateMachine = null;
        }

        this.uiPref = null;

        if (null != this.eventTable) {
            this.eventTable.unhookall();
            this.eventTable = null;
        }

        if (null != this.queryArea) {
            this.queryArea.preDestroy();
            this.queryArea = null;
        }

        if (gridSearchAreaToDataGrid != null) {
            gridSearchAreaToDataGrid.onPreDestroy();
            gridSearchAreaToDataGrid = null;

        }

        if (this.multiColumnSortPoUpListener != null) {
            multiColumnSortPoUpListener.onPreDestroy();
            multiColumnSortPoUpListener = null;
        }
        if (null != this.searchArea) {
            this.searchArea.preDestroy();
            this.searchArea = null;
        }

        cleanupOnPreDestroy();
    }

    private void cleanupOnPreDestroy() {
        if (null != this.toolbar) {
            this.toolbar.preDestroy();
            this.toolbar = null;
        }

        if (null != this.grid) {
            this.grid.onPreDestroy();
            this.grid = null;
        }
        if (null != this.text) {
            this.text.onPreDestroy();
            this.text = null;
        }
        if (null != this.statusBar) {
            this.statusBar.preDestroy();
            this.statusBar = null;
        }

        if (null != this.dataProvider) {
            this.dataProvider.preDestroy();
            this.dataProvider = null;
        }
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {

        final boolean preserveScrollLocation = true;
        String encoding = null;
        try {
            if (this.uiPref.isIncludeEncoding() && dataProvider instanceof DSResultSetGridDataProvider) {
                ((DSResultSetGridDataProvider) dataProvider).setIncludeEncoding(true);
                encoding = this.toolbar.getSelectedEncoding();
                if (encoding != null) {
                    ((DSResultSetGridDataProvider) dataProvider).changeEncoding(encoding);
                } else {
                    ((DSResultSetGridDataProvider) dataProvider).changeEncoding(this.uiPref.getDefaultEncoding());
                }
            }
            this.grid.inputChanged(dataProvider, preserveScrollLocation);
            // Load data after triggering scroll bar or refresh button
            if (null != this.text && !text.isInitDataTextFlag()) {
                this.text.resetTextData(dataProvider);
            }
            this.toolbar.setDataProvider(dataProvider);
            if (this.uiPref.isShowStatusBar()) {
                this.statusBar.setDataProvider(dataProvider);
            }
            // updating the data provider
            this.dataProvider = dataProvider;
        } catch (OutOfMemoryError e) {
            dataLoadOutOfMemError(e.getCause());
        }
    }

    /**
     * Data load out of mem error.
     *
     * @param throwable the throwable
     */
    public void dataLoadOutOfMemError(Throwable throwable) {
        String msg = MessageConfigLoader.getProperty(IMessagesConstants.ERR_OUT_OF_MEMORY_OCCURED);
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_ON_ERROR, new DatabaseOperationException(msg, throwable)));
    }

    /**
     * Data load error.
     */
    public void dataLoadError() {
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_ON_ERROR, null));
    }

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Gets the UI data iterator.
     *
     * @return the UI data iterator
     */
    public GridViewPortData getUIDataIterator() {
        return this.grid.getViewLayerDataIterator();
    }

    /**
     * Gets the select data iterator.
     *
     * @return the select data iterator
     */
    public GridSelectionLayerPortData getSelectDataIterator() {
        return this.grid.getSelectionLayerDataIterator();
    }

    /**
     * Focus.
     */
    public void focus() {
        if (this.grid != null && this.grid.getDataGrid() != null && !this.grid.getDataGrid().isDisposed()) {
            this.grid.setFocus();
        }
    }

    /**
     * Sets the loading status.
     *
     * @param status the new loading status
     */
    public void setLoadingStatus(boolean status) {
        State state = status ? State.LOADING : State.IDLE;
        stateMachine.set(state);
    }

    /**
     * Enable disable grid.
     *
     * @param enable the enable
     */
    public void enableDisableGrid(boolean enable) {
        if (grid == null || grid.getDataGrid() == null || grid.getDataGrid().isDisposed()) {
            return;
        }
        grid.getDataGrid().setEnabled(enable);
    }

    /**
     * Saved user option.
     */
    public void savedUserOption() {

        Boolean boolTrue = Boolean.valueOf(true);
        eventTable.sendEvent(new DSEvent(LISTEN_EDITTABLE_REMEMBERED_USER_OPTION, boolTrue));

    }

    /**
     * Gets the toolbar.
     *
     * @return the toolbar
     */
    public GridToolbar getToolbar() {
        return this.toolbar;
    }

    /**
     * Checks if is disposed.
     *
     * @return true, if is disposed
     */
    public boolean isDisposed() {
        return this.grid.getDataGrid().isDisposed();
    }

    /**
     * Update grid data.
     */
    public void updateGridData() {
        grid.updateGridData();
    }

    /**
     * Update grid status bar.
     *
     * @param summary the summary
     */
    public void updateGridStatusBar(IQueryExecutionSummary summary) {
        this.statusBar.updateDataProviderSummaryData(summary);
    }

    /**
     * Gets the search area.
     *
     * @return the search area
     */
    public GridSearchAreaToDataGrid getSearchArea() {
        if (this.gridSearchAreaToDataGrid == null) {
            gridSearchAreaToDataGrid = new GridSearchAreaToDataGrid(this.searchArea);
        }
        return gridSearchAreaToDataGrid;
    }

    /**
     * Update.
     *
     * @param o the o
     * @param arg the arg
     */
    @Override
    public void update(Observable o, Object arg) {
        SQLSyntax syntax = null;
        if (dataProvider != null && dataProvider.getDatabse() != null && dataProvider.getDatabse().isConnected()) {
            syntax = dataProvider.getDatabse().getSqlSyntax();
        }
        if (this.queryArea != null) {
            this.queryArea.reconfigure(syntax);
        }
    }

    /**
     * Gets the data grid.
     *
     * @return the data grid
     */
    public DataGrid getDataGrid() {
        return this.grid;
    }

    /**
     * Gets the data text.
     *
     * @return the data text
     */
    public DataText getDataText() {
        return this.text;
    }

    /**
     * Sets the commit status.
     *
     * @param commitStatus the new commit status
     */
    public void setCommitStatus(CommitStatus commitStatus) {

    }

    /**
     * Gets the selected encoding.
     *
     * @return the selected encoding
     */
    public String getSelectedEncoding() {
        return this.toolbar.getSelectedEncoding();
    }

    /**
     * Update grid status bar for data text.
     *
     * @param partloaded the partloaded
     * @param loadedRowCnt the loaded row cnt
     */
    public void updateGridStatusBarForDataText(boolean partloaded, int loadedRowCnt) {
        if (null != this.statusBar && uiPref.isNeedCreateTextMode()) {
            this.statusBar.setEditableOrNonEditableStatusMessageForDataText(this.dataProvider, partloaded,
                    loadedRowCnt);
        }
    }

    /**
     * Update grid status bar for data grid.
     */
    public void updateGridStatusBarForDataGrid() {
        if (null != this.statusBar && this.uiPref.isShowStatusBar()) {
            this.statusBar.setEditableOrNonEditableStatusMessageForDataGrid(this.dataProvider);
        }
    }

    /**
     * Show status bar.
     */
    public void showStatusBar() {
        if (null != this.statusBar && this.uiPref.isShowStatusBar()) {
            this.statusBar.showStatusBar();
        }
    }

    /**
     * Hide status bar.
     */
    public void hideStatusBar() {
        if (null != this.statusBar && this.uiPref.isShowStatusBar()) {
            this.statusBar.hideStatusBar();
        }
    }

    /**
     * Validate search option.
     *
     * @return true, if successful
     */
    public boolean validateSearchOption() {
        return getSearchArea().getCmbSearchOpt().getText().equals(SEARCHOPTIONS.SRCH_NULL.getDisplayName());
    }

    /**
     * Gets the search txt string.
     *
     * @return the search txt string
     */
    public String getSearchTxtString() {
        return getSearchArea().getTxtSearchStr().getText();
    }

    /**
     * Checks if is show grid or show text select.
     *
     * @return true, if is show grid or show text select
     */
    public boolean isShowGridOrShowTextSelect() {
        return getToolbar().isShowGridOrShowTextSelect();
    }

    /**
     * Checks if is refresh but enable.
     *
     * @return true, if is refresh but enable
     */
    public boolean isRefreshButEnable() {
        return getToolbar().isRefreshButEnable();
    }

    /**
     * Updata but status on load data text.
     *
     * @param boolValue the boolValue
     */
    public void updataButStatusOnLoadDataText(boolean boolValue) {
        getToolbar().updataButStatusOnLoadDataText(boolValue);

    }

    /**
     * Gets the trigger search.
     *
     * @param string the string
     * @param boolValue the boolValue
     * @return the trigger search
     */
    public void getTriggerSearch(String string, boolean boolValue) {
        getSearchArea().getTriggerSearch(string, boolValue);

    }

    /**
     * Sets the txt seatrch text.
     *
     * @param seleText the new txt seatrch text
     */
    public void setTxtSeatrchText(String seleText) {
        getSearchArea().getTxtSearchStr().setText(seleText);

    }

    /**
     * Refresh data grid.
     */
    public void refreshDataGrid() {
        getDataGrid().closeEditor();
        getDataGrid().refresh();

    }

    /**
     * Gets the state machine.
     *
     * @return the state machine
     */
    public DSGridStateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * updateBatchDropButtons method
     */
    public void updateBatchDropButtons() {
        getToolbar().updateBatchDropStartButton(true);
        getToolbar().updateBatchDropStopButton(false);
        getToolbar().updateBatchDropAtomicButton(true);
        getToolbar().updateBatchDropCascadeButton(true);
    }
}
