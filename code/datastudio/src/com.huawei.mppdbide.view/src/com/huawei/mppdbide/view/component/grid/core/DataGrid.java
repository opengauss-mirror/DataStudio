/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.component.grid.core;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.command.ILayerCommand;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.PositionCoordinate;
import org.eclipse.nebula.widgets.nattable.copy.action.CopyDataAction;
import org.eclipse.nebula.widgets.nattable.copy.action.PasteDataAction;
import org.eclipse.nebula.widgets.nattable.copy.command.CopyDataToClipboardCommand;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.action.KeyEditAction;
import org.eclipse.nebula.widgets.nattable.edit.action.MouseEditAction;
import org.eclipse.nebula.widgets.nattable.edit.command.EditCellCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.EditSelectionCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.export.command.ExportCommand;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsSortModel;
import org.eclipse.nebula.widgets.nattable.extension.nebula.richtext.RegexMarkupValue;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.command.ViewportSelectColumnGroupCommand;
import org.eclipse.nebula.widgets.nattable.group.command.ViewportSelectRowGroupCommand;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.print.command.PrintCommand;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.resize.action.ColumnResizeCursorAction;
import org.eclipse.nebula.widgets.nattable.resize.command.AutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.AutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnSizeConfigurationCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.ColumnWidthResetCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeColumnsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.InitializeAutoResizeRowsCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.MultiColumnResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.MultiRowResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.RowHeightResetCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.RowResizeCommand;
import org.eclipse.nebula.widgets.nattable.resize.command.RowSizeConfigurationCommand;
import org.eclipse.nebula.widgets.nattable.resize.event.ColumnResizeEventMatcher;
import org.eclipse.nebula.widgets.nattable.resize.mode.ColumnResizeDragMode;
import org.eclipse.nebula.widgets.nattable.search.command.SearchCommand;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.MoveSelectionCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.ScrollSelectionCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectColumnCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRegionCommand;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectRowsCommand;
import org.eclipse.nebula.widgets.nattable.sort.ISortModel;
import org.eclipse.nebula.widgets.nattable.sort.SortConfigAttributes;
import org.eclipse.nebula.widgets.nattable.sort.SortDirectionEnum;
import org.eclipse.nebula.widgets.nattable.sort.SortHeaderLayer;
import org.eclipse.nebula.widgets.nattable.sort.command.SortColumnCommand;
import org.eclipse.nebula.widgets.nattable.tickupdate.command.TickUpdateCommand;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.command.RecalculateScrollBarsCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowCellInViewportCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowColumnInViewportCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ShowRowInViewportCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportDragCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportSelectColumnCommand;
import org.eclipse.nebula.widgets.nattable.viewport.command.ViewportSelectRowCommand;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import com.huawei.mppdbide.bl.serverdatacache.ColumnMetaData;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnGroupProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.PropertiesUserRoleImpl;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.BatchDropGridStyleConfiguration;
import com.huawei.mppdbide.view.component.grid.DSGridToolTipProvider;
import com.huawei.mppdbide.view.component.grid.DSHTMLAwareRegexMarkupValue;
import com.huawei.mppdbide.view.component.grid.EditTableGridStyleConfiguration;
import com.huawei.mppdbide.view.component.grid.GridScrollEventDataLoadListener;
import com.huawei.mppdbide.view.component.grid.GridSelectionLayerPortData;
import com.huawei.mppdbide.view.component.grid.GridUIUtils;
import com.huawei.mppdbide.view.component.grid.GridViewPortData;
import com.huawei.mppdbide.view.component.grid.IDataGridContext;
import com.huawei.mppdbide.view.component.grid.SEARCHOPTIONS;
import com.huawei.mppdbide.view.component.grid.ServerObjectTypeForDialog;
import com.huawei.mppdbide.view.component.grid.TableGridStyleConfiguration;
import com.huawei.mppdbide.view.handler.IHandlerUtilities;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;
import com.huawei.mppdbide.view.utils.icon.IconUtility;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.TextMatcherEditor;

/**
 * 
 * Title: class
 * 
 * Description: The Class DataGrid.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DataGrid {

    /**
     * The ui pref.
     */
    protected IGridUIPreference uiPref;

    /**
     * The state machine.
     */
    protected DSGridStateMachine stateMachine;

    /**
     * The data provider.
     */
    protected IDSGridDataProvider dataProvider;

    /**
     * Gets the data provider.
     *
     * @return the data provider
     */
    public IDSGridDataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * The data event list.
     */
    protected EventList<IDSGridDataRow> dataEventList;

    /**
     * The data filter list.
     */
    protected FilterList<IDSGridDataRow> dataFilterList;

    /**
     * The data sorted list.
     */
    protected SortedList<IDSGridDataRow> dataSortedList;

    /**
     * The col prop accessor.
     */
    protected GridColumnValueAccessor colPropAccessor;

    /**
     * The config registry.
     */
    protected ConfigRegistry configRegistry;

    /**
     * The regex matcher.
     */
    protected RegexMarkupValue regexMatcher = null;
    private TextMatcherEditor<IDSGridDataRow> matcherEditor;
    private GridScrollEventDataLoadListener scrollDataLoadListener;
    private SortRegistryConfiguration sortRegistrConfiguration;

    /**
     * The event table.
     */
    protected DSEventTable eventTable;

    /**
     * The body data layer.
     */
    protected DataLayer bodyDataLayer;

    /**
     * The body data provider.
     */
    protected ListDataProvider<IDSGridDataRow> bodyDataProvider;

    /**
     * The reorder layer.
     */
    protected ColumnReorderLayer reorderLayer;

    /**
     * The selection layer.
     */
    protected SelectionLayer selectionLayer;

    /**
     * The viewport layer.
     */
    protected GridViewPortLayer viewportLayer;

    // Nattable Column Stack
    private SortHeaderLayer<IDSGridDataRow> sortHeaderLayer;

    /**
     * The column header data provider.
     */
    protected GridColHeaderDataProvider columnHeaderDataProvider;

    /**
     * The column header data layer.
     */
    protected DefaultColumnHeaderDataLayer columnHeaderDataLayer;

    /**
     * The selection col header layer.
     */
    protected SelectionLayer selectionColHeaderLayer;

    /**
     * The row header data provider.
     */
    protected DefaultRowHeaderDataProvider rowHeaderDataProvider;

    /**
     * The row header data layer.
     */
    protected DefaultRowHeaderDataLayer rowHeaderDataLayer;

    /**
     * The row header layer.
     */
    protected RowHeaderLayer rowHeaderLayer;

    /**
     * The selection row header layer.
     */
    protected SelectionLayer selectionRowHeaderLayer;

    /**
     * The corner data provider.
     */
    protected GridCornerLayerDataProvider cornerDataProvider;

    /**
     * the corner DataLayer
     */
    protected DataLayer cornerDataLayer;

    /**
     * The corner layer.
     */
    protected CornerLayer cornerLayer;

    /**
     * The grid layer.
     */
    protected GridLayer gridLayer;

    /**
     * The data grid.
     */
    protected volatile DSNatTable dataGrid;

    /**
     * The column header layer.
     */
    protected ILayer columnHeaderLayer;

    /**
     * The edit table config.
     */
    protected AbstractRegistryConfiguration editTableConfig;
    private BatchDropGridStyleConfiguration batchDropTableConfig;

    /**
     * The col label accumulator.
     */
    protected ColumnOverrideLabelAccumulator colLabelAccumulator;

    /**
     * The ds grid tool tip provider.
     */
    protected DSGridToolTipProvider dsGridToolTipProvider;

    /**
     * The column group model.
     */
    protected ColumnGroupModel columnGroupModel;

    /**
     * The column group header layer.
     */
    protected ColumnGroupHeaderLayer columnGroupHeaderLayer;

    /**
     * The datatype column accumulator.
     */
    protected DSAbstractRegistryConfiguration datatypeColumnAccumulator;

    /**
     * The col header label accumulator.
     */
    protected ColumnLabelAccumulator colHeaderLabelAccumulator;

    /**
     * The parent.
     */
    protected Composite parent;

    /**
     * The data context.
     */
    protected IDataGridContext dataContext;

    /**
     * The save sort store.
     */
    protected GridSaveSortState saveSortStore;

    /**
     * The copy handler.
     */
    protected DSCopyDataCommandHandler copyHandler;

    /**
     * The reorder properties.
     */
    protected Properties reorderProperties;

    /**
     * The reorder prefix.
     */
    protected String reorderPrefix = "reorderState";
    private GridPasteHandler pasteCommandHandler;
    private TableGridStyleConfiguration tableGridStyleconfiguration;
    private RightClickShowMenuConfiguration rightClickShowMenuconfiguration;
    private NatTableBorderOverlayPainter borderOverlayPainter;
    private MouseListener mouseListenerForSort;

    /**
     * Instantiates a new data grid.
     *
     * @param uiPref the ui pref
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public DataGrid(IGridUIPreference uiPref, IDSGridDataProvider dataProvider, DSEventTable eventTable,
            DSGridStateMachine stateMachine) {
        this.uiPref = uiPref;
        dataContext = new DataGridContext();
        this.dataProvider = dataProvider;
        this.eventTable = eventTable;
        this.stateMachine = stateMachine;
        this.saveSortStore = new GridSaveSortState();
        this.reorderProperties = new Properties();
        this.borderOverlayPainter = new NatTableBorderOverlayPainter();
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    protected void setDataProvider(IDSGridDataProvider dataProvider) {

        if (this.dataProvider != dataProvider) {
            if (null != this.dataProvider) {
                this.dataProvider.preDestroy();
            }
            this.dataProvider = dataProvider;
        }

        dataContext.setDataProvider(this.dataProvider);
        if (this.dataProvider instanceof IDSEditGridDataProvider) {
            this.dataEventList = GlazedLists
                    .eventList(((IDSEditGridDataProvider) this.dataProvider).getConsolidatedRows());
        } else {
            this.dataEventList = GlazedLists.eventList(this.dataProvider.getAllFetchedRows());
        }
        this.dataFilterList = new FilterList<IDSGridDataRow>(GlazedLists.threadSafeList(this.dataEventList));
        if (null != this.dataSortedList) {
            this.dataSortedList.clear();
        }
        this.dataSortedList = new SortedList<IDSGridDataRow>(this.dataFilterList, null);
        if (uiPref.isEnableEdit()) {
            setColumnValueAccessor();
        } else {
            this.colPropAccessor = new GridNonEditableColumnValueAccessor(this.dataProvider);
        }

        if (null != this.copyHandler) {
            // Anyway will reset during next execution. But to clearup the
            // reference to old dataprovider (memory leak), better to reset.
            this.copyHandler.setDataProvider(this.dataProvider);
        }
    }

    /**
     * Sets the column value accessor.
     */
    protected void setColumnValueAccessor() {
        this.colPropAccessor = new GridColumnValueAccessor(this.dataProvider);
    }

    /**
     * Gets the data grid.
     *
     * @return the data grid
     */
    public DSNatTable getDataGrid() {
        return this.dataGrid;
    }

    /**
     * Gets the list of rows.
     *
     * @return the list of rows
     */
    public EventList<IDSGridDataRow> getListOfRows() {
        return this.dataEventList;
    }

    /**
     * On pre destroy.
     */
    public void onPreDestroy() {
        if (null != getDataGrid() && null != mouseListenerForSort) {
            getDataGrid().removeMouseListener(mouseListenerForSort);
        }
        cleanDataProviderInfo();
        clearColumnDataTypesInfo();
        clearConfiguration();
        clearColHeaderLabelInfo();
        clearCopyPasteCommandHandler();
        clearGridLayerInfo();
        clearRowHeaderDataLayerCommands();
        clearSelectionRowHeaderLayerCommands();
        clearCornerDataLayerCommands();
        clearColumnHeaderDataLayerCommands();
        clearViewPortLayerCommands();
        clearToolTipInfo();
        clearRightClickConfigInfo();
        clearDataList();
        cleanDataGridRegistry();

        /* Remove all local reference variables. */
        if (null != this.saveSortStore) {
            saveSortStore.cleanUp();
            saveSortStore = null;
        }

        if (this.dataContext != null) {
            dataContext.onPreDestroy();
        }

        if (this.stateMachine != null && this.stateMachine.countObservers() > 0) {
            this.stateMachine.deleteObservers();
        }

        if (this.cornerDataProvider != null) {
            cornerDataProvider.onPreDestroy();
        }
        setUsedObjectToNull();
    }

    private void clearRightClickConfigInfo() {
        if (this.rightClickShowMenuconfiguration != null) {
            rightClickShowMenuconfiguration.preDestroy();
            rightClickShowMenuconfiguration = null;
        }
    }

    private void clearToolTipInfo() {
        if (dsGridToolTipProvider != null) {
            dsGridToolTipProvider.onPreDestroy();
            dsGridToolTipProvider = null;
        }
    }

    private void clearGridLayerInfo() {
        if (this.gridLayer != null) {
            gridLayer.unregisterCommandHandler(AutoResizeRowsCommand.class);
            gridLayer.unregisterCommandHandler(AutoResizeColumnsCommand.class);
            gridLayer.unregisterCommandHandler(PrintCommand.class);
            gridLayer.unregisterCommandHandler(ExportCommand.class);
            gridLayer.unregisterCommandHandler(EditCellCommand.class);
            gridLayer.clearConfiguration();
        }
    }

    private void clearColHeaderLabelInfo() {
        if (this.colHeaderLabelAccumulator != null
                && colHeaderLabelAccumulator instanceof GridColumnHeaderAccumulator) {
            ((GridColumnHeaderAccumulator) colHeaderLabelAccumulator).onPreDestroy();
        }
    }

    private void clearCopyPasteCommandHandler() {
        if (null != gridLayer) {
            if (this.copyHandler != null) {
                this.gridLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) copyHandler.getCommandClass());
                copyHandler.onPreDestroy();
            }

            if (this.pasteCommandHandler != null) {
                this.gridLayer.unregisterCommandHandler(
                        (Class<? extends ILayerCommand>) pasteCommandHandler.getCommandClass());
                pasteCommandHandler.onPreDestroy();
            }
        }
    }

    private void clearViewPortLayerCommands() {
        if (viewportLayer != null) {
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) RecalculateScrollBarsCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ScrollSelectionCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ShowCellInViewportCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ShowColumnInViewportCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ShowRowInViewportCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ViewportSelectColumnCommand.class);
            viewportLayer
                    .unregisterCommandHandler((Class<? extends ILayerCommand>) ViewportSelectColumnGroupCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ViewportSelectRowCommand.class);
            viewportLayer
                    .unregisterCommandHandler((Class<? extends ILayerCommand>) ViewportSelectRowGroupCommand.class);
            viewportLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) ViewportDragCommand.class);
            viewportLayer.clearConfiguration();
        }
    }

    private void clearColumnHeaderDataLayerCommands() {
        if (null != bodyDataLayer) {
            bodyDataLayer.unregisterCommandHandler(ColumnResizeCommand.class);
            bodyDataLayer.unregisterCommandHandler(MultiRowResizeCommand.class);
            bodyDataLayer.unregisterCommandHandler(RowResizeCommand.class);
            bodyDataLayer.unregisterCommandHandler(MultiColumnResizeCommand.class);
            bodyDataLayer.unregisterCommandHandler(ColumnWidthResetCommand.class);
            bodyDataLayer.unregisterCommandHandler(RowSizeConfigurationCommand.class);
            bodyDataLayer.unregisterCommandHandler(ColumnSizeConfigurationCommand.class);
            bodyDataLayer.unregisterCommandHandler(RowHeightResetCommand.class);
        }
        if (null != columnHeaderDataLayer) {
            columnHeaderDataLayer.setConfigLabelAccumulator(null);
            columnHeaderDataLayer.clearConfiguration();
        }
    }

    private void clearCornerDataLayerCommands() {
        if (cornerDataLayer != null) {
            cornerDataLayer.unregisterCommandHandler(ColumnResizeCommand.class);
            cornerDataLayer.unregisterCommandHandler(MultiRowResizeCommand.class);
            cornerDataLayer.unregisterCommandHandler(RowResizeCommand.class);
            cornerDataLayer.unregisterCommandHandler(MultiColumnResizeCommand.class);
            cornerDataLayer.unregisterCommandHandler(ColumnWidthResetCommand.class);
            cornerDataLayer.unregisterCommandHandler(RowSizeConfigurationCommand.class);
            cornerDataLayer.unregisterCommandHandler(ColumnSizeConfigurationCommand.class);
            cornerDataLayer.unregisterCommandHandler(RowHeightResetCommand.class);
            cornerDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
            cornerDataLayer.clearConfiguration();
        }
    }

    private void clearSelectionRowHeaderLayerCommands() {
        if (selectionRowHeaderLayer != null) {
            selectionRowHeaderLayer.unregisterCommandHandler(SelectColumnCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(SelectRowsCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(SelectCellCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(InitializeAutoResizeColumnsCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(MoveSelectionCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(EditSelectionCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(SearchCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(CopyDataToClipboardCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(InitializeAutoResizeRowsCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(SelectRegionCommand.class);
            selectionRowHeaderLayer.unregisterCommandHandler(TickUpdateCommand.class);
            selectionColHeaderLayer.clearConfiguration();
        }
    }

    private void clearRowHeaderDataLayerCommands() {
        if (rowHeaderDataLayer != null) {
            rowHeaderDataLayer.unregisterCommandHandler(ColumnResizeCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(MultiRowResizeCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(RowResizeCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(MultiColumnResizeCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(ColumnWidthResetCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(RowSizeConfigurationCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(ColumnSizeConfigurationCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(RowHeightResetCommand.class);
            rowHeaderDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
            rowHeaderDataLayer.clearConfiguration();
        }
    }

    private void clearColumnDataTypesInfo() {
        if (null != this.colLabelAccumulator && null != bodyDataLayer) {
            bodyDataLayer.setConfigLabelAccumulator(null);
            ((GridColumnLabelAccumulator) colLabelAccumulator).onPreDestroy();
        }

        if (this.datatypeColumnAccumulator != null) {
            EditTableGridStyleConfiguration.getDatatypeColumnAccumulator(colLabelAccumulator, dataContext, uiPref)
                    .onPreDestroy();
        }
    }

    private void setUsedObjectToNull() {
        colHeaderLabelAccumulator = null;
        datatypeColumnAccumulator = null;
        columnGroupHeaderLayer = null;
        columnGroupModel = null;
        dsGridToolTipProvider = null;
        colLabelAccumulator = null;
        batchDropTableConfig = null;
        editTableConfig = null;
        gridLayer = null;
        cornerDataProvider = null;
        cornerLayer = null;
        selectionRowHeaderLayer = null;
        rowHeaderLayer = null;
        rowHeaderDataLayer = null;
        rowHeaderDataProvider = null;
        selectionColHeaderLayer = null;
        columnHeaderDataLayer = null;
        columnHeaderDataProvider = null;
        sortHeaderLayer = null;
        tableGridStyleconfiguration = null;
        sortRegistrConfiguration = null;
        scrollDataLoadListener = null;
        bodyDataLayer = null;
        bodyDataProvider = null;
        reorderLayer = null;
        selectionLayer = null;
        viewportLayer = null;
        configRegistry = null;
        copyHandler = null;
        columnHeaderLayer = null;
        dataGrid = null;
        reorderProperties = null;
        reorderPrefix = null;
        dataContext = null;
        eventTable = null;
        regexMatcher = null;
        uiPref = null;
        pasteCommandHandler = null;
        stateMachine = null;
        cornerDataLayer = null;
    }

    private void cleanDataGridRegistry() {
        if (null != this.dataGrid) {
            // No issue even if this.dataGrid.isDisposed() is equalto true
            if (uiPref.isNeedAdvancedCopy()) {
                this.dataGrid.getUiBindingRegistry()
                        .unregisterKeyBinding(new KeyEventMatcher(SWT.MOD1 | SWT.MOD2, 'c'));
            }
            this.dataGrid.getUiBindingRegistry().unregisterKeyBinding(new KeyEventMatcher(SWT.MOD1, 'c'));
            if (uiPref.isEnableEdit()) {
                this.dataGrid.getUiBindingRegistry().unregisterKeyBinding(new KeyEventMatcher(SWT.MOD1, 'v'));
            }
            this.dataGrid.getUiBindingRegistry().unregisterSingleClickBinding(new MouseEventMatcher(GridRegion.CORNER));
            this.dataGrid.setUiBindingRegistry(null);
            this.dataGrid.clearConfigurations();
            this.dataGrid.getConfigRegistry().unregisterConfigAttribute(SortConfigAttributes.SORT_COMPARATOR);
            this.dataGrid.getConfigRegistry().unregisterConfigAttribute(CellConfigAttributes.CELL_PAINTER);
            if (this.scrollDataLoadListener != null) {
                scrollDataLoadListener.onPreDestroy();
                this.dataGrid.getVerticalBar().removeListener(SWT.Selection, scrollDataLoadListener);
                this.dataGrid.removeKeyListener(this.scrollDataLoadListener);
            }
            ConfigRegistry tempRegistry = new ConfigRegistry();
            this.dataGrid.setConfigRegistry(tempRegistry);
            tempRegistry = null;
            dataGrid.clearLayers();
        }
    }

    private void clearDataList() {
        if (null != this.dataEventList) {
            this.dataEventList.clear();
        }
        if (null != dataFilterList) {
            this.dataFilterList.clear();
        }
        if (null != dataSortedList) {
            this.dataSortedList.clear();
        }
    }

    private void clearConfiguration() {
        if (tableGridStyleconfiguration != null) {
            tableGridStyleconfiguration.preDestroy();
        }
        if (this.sortRegistrConfiguration != null) {
            this.sortRegistrConfiguration.onPreDestroy();
        }
        if (this.gridLayer != null) {
            gridLayer.unregisterCommandHandler((Class<? extends ILayerCommand>) DSCopyDataCommandHandler.class);
            gridLayer.clearConfiguration();
        }
        if (this.dataGrid != null) {
            dataGrid.clearConfigurations();
            dataGrid.removeOverlayPainter(borderOverlayPainter);
        }
    }

    private void cleanDataProviderInfo() {
        if (null != this.dataProvider) {
            // Close the statement and rollback the transaction.
            this.dataProvider.preDestroy();
            this.dataProvider = null;
        }
        if (null != colPropAccessor) {
            this.colPropAccessor.onPreDestroy();
            this.colPropAccessor = null;
        }
        if (this.columnHeaderDataProvider != null) {
            this.columnHeaderDataProvider.onPreDestroy();
        }
    }

    /**
     * Creates the component.
     *
     * @param gridParent the grid parent
     */
    public void createComponent(Composite gridParent) {
        this.parent = gridParent;
        parent.setLayout(new GridLayout(1, false));
        setDataProvider(getDataProvider());
        createAllLayerDataProviders();
        createDataStackLayer();
        createColumnHeaderStackLayer();
        createRowHeaderStackLayer();
        createCornerLayerStack();

        createTableGrid(parent);
        if (uiPref.isEnableSort()) {
            addListenersforSort();
        }
    }

    /**
     * Do hide grid.
     *
     * @param composite the composite
     */
    public void doHideGrid(Composite composite) {

        GridUIUtils.toggleCompositeSectionVisibility(composite, true, null, false);
    }

    /**
     * Do show grid.
     *
     * @param composite the composite
     */
    public void doShowGrid(Composite composite) {

        GridUIUtils.toggleCompositeSectionVisibility(composite, false, null, false);
    }

    private MouseListener getMouseListenerForSort() {
        return new MouseListener() {

            @Override
            public void mouseUp(MouseEvent event) {

            }

            @Override
            public void mouseDown(MouseEvent event) {
                if ((event.stateMask & SWT.MOD3) == SWT.MOD3) {
                    return;
                }
                int colPos = getDataGrid().getColumnPositionByX(event.x);
                int rowPos = getDataGrid().getRowPositionByY(event.y);
                ILayerCell cell = getDataGrid().getCellByPosition(colPos, rowPos);
                int columnWidth = getDataGrid().getColumnWidthByPosition(colPos);
                int columnHeight = getDataGrid().getRowHeightByPosition(0);
                int startXOfColumn = getDataGrid().getStartXOfColumnPosition(colPos);
                int startYOfColumn = getDataGrid().getStartYOfRowPosition(0);
                Image myImage = IconUtility.getIconSmallImage(IconUtility.ICO_SORT_NONE, getClass());
                int sortImageWidth = myImage.getBounds().width;

                if (null != cell && event.x >= startXOfColumn + columnWidth - sortImageWidth - 3
                        && event.x <= startXOfColumn + columnWidth - 3 && event.y >= startYOfColumn
                        && event.y <= startYOfColumn + columnHeight) {
                    ISortModel sortModel = sortHeaderLayer.getSortModel();
                    getDataGrid().doCommand(new SortColumnCommand(getDataGrid(), cell.getColumnPosition(), false,
                            sortModel.getSortDirection(cell.getColumnIndex()).getNextSortDirection()));
                }
            }

            @Override
            public void mouseDoubleClick(MouseEvent event) {

            }
        };
    }

    private void addListenersforSort() {
        if (null != getDataGrid()) {
            this.mouseListenerForSort = getMouseListenerForSort();
            getDataGrid().addMouseListener(mouseListenerForSort);
        }
    }

    /**
     * Creates the all layer data providers.
     */
    protected void createAllLayerDataProviders() {
        this.bodyDataProvider = new ListDataProvider<IDSGridDataRow>(this.dataSortedList, this.colPropAccessor);
        this.columnHeaderDataProvider = new GridColHeaderDataProvider(this.dataProvider);
        this.rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
        this.cornerDataProvider = new GridCornerLayerDataProvider(dataContext);
        if (null != this.scrollDataLoadListener) {
            // Will be applicable only when altering dataprovider, incase of
            // scroll load, reexecute etc.
            this.scrollDataLoadListener.setDataProvider(this.dataProvider);
        }
    }

    /**
     * Creates the row header stack layer.
     */
    protected void createRowHeaderStackLayer() {
        this.rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
        this.selectionRowHeaderLayer = new SelectionLayer(this.rowHeaderDataLayer);
        this.rowHeaderLayer = new RowHeaderLayer(this.selectionRowHeaderLayer, viewportLayer, selectionLayer);
        RowHeaderColumnLabelAccumulator rowHeaderColumnLabelAccumulator = new RowHeaderColumnLabelAccumulator(
                this.bodyDataLayer);
        this.rowHeaderDataLayer.setConfigLabelAccumulator(rowHeaderColumnLabelAccumulator);
    }

    /**
     * Creates the column header stack layer.
     */
    protected void createColumnHeaderStackLayer() {
        this.columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
        this.selectionColHeaderLayer = new SelectionLayer(this.columnHeaderDataLayer);
        this.columnHeaderLayer = new ColumnHeaderLayer(this.selectionColHeaderLayer, viewportLayer, selectionLayer);

        this.colHeaderLabelAccumulator = getColumnHeaderAccumulator();
        this.columnHeaderDataLayer.setConfigLabelAccumulator(colHeaderLabelAccumulator);

        if (uiPref.isEnableSort()) {
            this.sortHeaderLayer = new SortHeaderLayer<IDSGridDataRow>(columnHeaderLayer,
                    new GlazedListsSortModel<IDSGridDataRow>(this.dataSortedList, this.colPropAccessor,
                            getConfigRegistry(), selectionColHeaderLayer),
                    false);

        }

        if (null != this.dataProvider.getColumnGroupProvider()) {
            columnGroupModel = new ColumnGroupModel();
            columnGroupHeaderLayer = new ColumnGroupHeaderLayer(
                    null != this.sortHeaderLayer ? this.sortHeaderLayer : this.columnHeaderLayer,
                    this.selectionColHeaderLayer, this.columnGroupModel);
            configureColumnGroup(this.dataProvider.getColumnGroupProvider());
        }
    }

    /**
     * Gets the column header accumulator.
     *
     * @return the column header accumulator
     */
    protected ColumnLabelAccumulator getColumnHeaderAccumulator() {
        return new GridColumnHeaderAccumulator(dataContext);
    }

    private void configureColumnGroup(IDSGridColumnGroupProvider columnGroupProvider) {
        int count = columnGroupProvider.getGroupCount();
        // Looping for every column,
        // Column not in group, then make it sum of static column & row count as
        // 2
        // Column in group, addcolumnsindexestogroup.

        for (int element = 0; element < count; element++) {
            columnGroupHeaderLayer.addColumnsIndexesToGroup(columnGroupProvider.getColumnGroupName(element),
                    columnGroupProvider.getColumnIndexInGroup(element));
        }

        columnGroupHeaderLayer.setStaticColumnIndexesByGroup(columnGroupProvider.getColumnGroupName(0), 0);
        columnGroupHeaderLayer.setGroupUnbreakable(1);
    }

    /**
     * Creates the corner layer stack.
     */
    protected void createCornerLayerStack() {
        this.cornerDataLayer = new DataLayer(this.cornerDataProvider);
        this.cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer,
                null != this.sortHeaderLayer ? this.sortHeaderLayer : this.columnHeaderLayer);

    }

    /**
     * Creates the data stack layer.
     */
    protected void createDataStackLayer() {
        bodyDataLayer = new DataLayer(bodyDataProvider);
        GlazedListsEventLayer<IDSGridDataRow> eventLayer = new GlazedListsEventLayer<IDSGridDataRow>(bodyDataLayer,
                this.dataSortedList);

        if (uiPref.isAllowColumnReorder()) {
            this.reorderLayer = addColumnReorderLayer(eventLayer);
            this.selectionLayer = new SelectionLayer(this.reorderLayer);
        } else {
            this.selectionLayer = new SelectionLayer(eventLayer);
        }
        // Commenting Reorder, to avoid issues during copy of reordered columns
        // : Reordered columns doesn't copy correct headers
        this.viewportLayer = new GridViewPortLayer(selectionLayer);
    }

    /**
     * Adds the column reorder layer.
     *
     * @param eventLayer the event layer
     * @return the column reorder layer
     */
    protected ColumnReorderLayer addColumnReorderLayer(GlazedListsEventLayer<IDSGridDataRow> eventLayer) {
        return new ColumnReorderLayer(eventLayer);
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DSGridLayerConfiguration.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    static final class DSGridLayerConfiguration extends DefaultGridLayerConfiguration {

        /**
         * Instantiates a new DS grid layer configuration.
         *
         * @param gridLayer the grid layer
         */
        DSGridLayerConfiguration(CompositeLayer gridLayer) {
            super(gridLayer);
        }

        /**
         * Configure ui bindings.
         *
         * @param uiBindingRegistry the ui binding registry
         */
        @Override
        public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {

            uiBindingRegistry.registerDoubleClickBinding(new MouseEventMatcher(), new MouseEditAction());
            uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.F2), new KeyEditAction());
        }

    }

    /**
     * Creates the table grid.
     *
     * @param tableparent the tableparent
     */
    protected void createTableGrid(Composite tableparent) {
        // build the grid layer
        gridLayer = new GridLayer(this.viewportLayer,
                null != this.sortHeaderLayer ? this.sortHeaderLayer : this.columnHeaderLayer, this.rowHeaderLayer,
                this.cornerLayer, false);
        configureSelectionCopyWithPref(gridLayer);

        this.dataGrid = new DSNatTable(tableparent, gridLayer, false);
        GridDataFactory.fillDefaults().grab(true, true).applyTo(this.dataGrid);
        this.dataGrid.setConfigRegistry(getConfigRegistry());
        if (this.uiPref.isShowRightClickMenu()) {
            rightClickShowMenuconfiguration = new RightClickShowMenuConfiguration(this.dataGrid);
            this.dataGrid.addConfiguration(rightClickShowMenuconfiguration);
        }

        configureGridStyle();

        enableCornerClickSelectAll();
        enableHeaderToolTip();
        configureCopyByPref();

        configureColumnDatatypes();

        gridLayer.addConfiguration(new DSGridLayerConfiguration(gridLayer));

        if (!uiPref.isEnableEdit()) {
            if (dataProvider instanceof DSResultSetGridDataProvider) {
                ((DSResultSetGridDataProvider) dataProvider).setEditSupported(false);
            } else if (dataProvider instanceof BatchDropDataProvider) {
                ((BatchDropDataProvider) dataProvider).setEditSupported(false);
            }
        }

        if (uiPref.isEnableEdit()) {

            coanfigurePasteConfiguration();
        }

        this.dataGrid.configure();
        this.dataGrid.addOverlayPainter(borderOverlayPainter);

        // Set size for each column.

        /*
         * Display without horizontal space bar. I.e., squeeze records to
         * available space with equal percentage of data,
         */
        if (uiPref.isFitToOnePage()) {
            this.bodyDataLayer.setColumnPercentageSizing(true);
        }

        registerDisposedListener();
    }

    /**
     * Coanfigure paste configuration.
     */
    public void coanfigurePasteConfiguration() {
        pasteCommandHandler = new GridPasteHandler(selectionLayer);
        this.gridLayer.registerCommandHandler(pasteCommandHandler);
    }

    /**
     * Configure column datatypes.
     */
    protected void configureColumnDatatypes() {
        colLabelAccumulator = new GridColumnLabelAccumulator(this.bodyDataLayer, this.dataProvider);
        bodyDataLayer.setConfigLabelAccumulator(colLabelAccumulator);

        datatypeColumnAccumulator = EditTableGridStyleConfiguration.getDatatypeColumnAccumulator(colLabelAccumulator,
                dataContext, this.uiPref);
        this.dataGrid.addConfiguration(datatypeColumnAccumulator);

        if (uiPref.isAddBatchDropTool()) {
            datatypeColumnAccumulator = BatchDropGridStyleConfiguration.getColumnAccumulator(colLabelAccumulator,
                    dataContext);
            this.dataGrid.addConfiguration(datatypeColumnAccumulator);
        }
    }

    /**
     * Register disposed listener.
     */
    protected void registerDisposedListener() {
        this.dataGrid.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent event) {
                onPreDestroy();
            }
        });

    }

    /**
     * Configure copy by pref.
     */
    protected void configureCopyByPref() {
        if (uiPref.isNeedAdvancedCopy()) {
            this.dataGrid.getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD1 | SWT.MOD2, 'c'),
                    new CopyDataWithPrefAction());
        }

        this.dataGrid.getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'c'),
                new CopyDataWithoutPrefAction());

        if (uiPref.isEnableEdit()) {
            this.dataGrid.getUiBindingRegistry().registerKeyBinding(new KeyEventMatcher(SWT.MOD1, 'v'),
                    new PasteDataWithPrefAction());
        }
    }

    /**
     * Save sort state.
     */
    public void saveSortState() {
        ISortModel sortModel = this.sortHeaderLayer.getSortModel();
        if (null != sortModel) {
            this.saveSortStore.cleanUp();
            List<Integer> sortedColList = sortModel.getSortedColumnIndexes();
            if (!sortedColList.isEmpty()) {
                List<Integer> sortPriorityList = new ArrayList<>(10);
                GridSaveSortState tempSortStore = new GridSaveSortState();
                for (int cnt = 0; cnt < sortedColList.size(); cnt++) {
                    int sortedColIndex = sortedColList.get(cnt);
                    sortPriorityList.add(sortModel.getSortOrder(sortedColIndex));
                    SortDirectionEnum sortDir = sortModel.getSortDirection(sortedColIndex);
                    String sortedColName = this.dataProvider.getColumnDataProvider().getColumnName(sortedColIndex);
                    SortEntryData entry = new SortEntryData(sortedColName, sortDir);
                    tempSortStore.saveSortEntry(entry);
                }

                for (int elmnt = 0; elmnt < sortPriorityList.size(); elmnt++) {
                    this.saveSortStore.saveSortEntry(tempSortStore.getSavedSortList().get(sortPriorityList.get(elmnt)));
                }
            }
        }
        /*
         * In future if performance of this function is bad, then can use below
         * code snippet. At the expense of some anamolies mentioned in
         * restoreLastSortState() function
         */

    }

    /**
     * Restore last sort state.
     *
     * @param sortSnapshot the sort snapshot
     */
    public void restoreLastSortState(GridSaveSortState sortSnapshot) {
        boolean isInputSortSnapshot = null != sortSnapshot;

        if (!isInputSortSnapshot && this.saveSortStore == null) {
            return;
        }

        if (isInputSortSnapshot ? sortSnapshot.hasSortKeys() : this.saveSortStore.hasSortKeys()) {
            List<SortEntryData> sortList = isInputSortSnapshot ? sortSnapshot.getSavedSortList()
                    : saveSortStore.getSavedSortList();
            List<String> colNames = Arrays.asList(this.dataProvider.getColumnDataProvider().getColumnNames());
            SortEntryData entry = null;
            boolean needClearSort = true;

            if (colNames.isEmpty()) {
                return;
            }

            for (int cnt = 0; cnt < sortList.size(); cnt++) {
                entry = sortList.get(cnt);

                if (colNames.contains(entry.getColumnName())) {
                    ISortModel sortModel = this.sortHeaderLayer.getSortModel();
                    if (needClearSort) {
                        sortModel.clear();
                        needClearSort = false;
                    }
                    if (null != sortModel) {
                        sortModel.sort(colNames.indexOf(entry.getColumnName()), entry.getSortDirection(), true);
                    }
                }
            }
            getDataGrid().refresh();
        }
        /*
         * Chose not to use below method because when a column is changed from
         * sorted state to non sorted state and refreshed immediately, the
         * column appears sorted again. NO-SORT state is not preserved And if a
         * sorted column is dropped, then next column appears as sorted, which
         * is wrong
         */

    }

    /**
     * Clear sort.
     */
    public void clearSort() {
        /*
         * clear all sorting fire a sort command on 1st column with do not
         * accumulate and sort state as NONE to clear all sorting
         */
        this.getDataGrid().doCommand(new SortColumnCommand(this.getDataGrid(), 1, false, SortDirectionEnum.NONE));

    }

    /**
     * Gets the current sort snapshot.
     *
     * @return the current sort snapshot
     */
    public GridSaveSortState getCurrentSortSnapshot() {
        saveReorderState();
        saveSortState();
        return this.saveSortStore.getClone();
    }

    /**
     * Apply sort snapshot.
     *
     * @param sortSnapshot the sort snapshot
     */
    public void applySortSnapshot(GridSaveSortState sortSnapshot) {
        restoreLastSortState(sortSnapshot);
        /*
         * If columns were reordered then they should be restored after sort is
         * applied
         */
        restoreReorderState();
    }

    /**
     * Save reorder state.
     */
    public void saveReorderState() {
        if (null != this.reorderLayer) {
            this.reorderLayer.saveState(reorderPrefix, reorderProperties);
        }
    }

    /**
     * Restore reorder state.
     */
    public void restoreReorderState() {
        if (null != this.reorderLayer) {
            this.reorderLayer.loadState(reorderPrefix, reorderProperties);
        }
    }

    /**
     * Configure grid style.
     */
    protected void configureGridStyle() {
        tableGridStyleconfiguration = new TableGridStyleConfiguration(getRegexMarkup(), uiPref, dataContext);
        this.dataGrid.addConfiguration(tableGridStyleconfiguration);

        configureColumnHeaderResizeable();

        // configuration of NatTable when User is in edit mode
        editTableConfig = new EditTableGridStyleConfiguration();

        this.dataGrid.addConfiguration(editTableConfig);

        if (uiPref.isAddBatchDropTool()) {
            batchDropTableConfig = new BatchDropGridStyleConfiguration();
            this.dataGrid.addConfiguration(batchDropTableConfig);
        }

        if (uiPref.isEnableSort()) {
            if (null != this.sortRegistrConfiguration) {
                this.sortRegistrConfiguration.setDataProvider(null);
            }
            this.dataGrid.addConfiguration(new SortAwareSelectionUIBindings());
            this.sortRegistrConfiguration = new SortRegistryConfiguration(this.dataProvider, this.uiPref);
            this.dataGrid.addConfiguration(sortRegistrConfiguration);
        }

        // Set fixed column width. Data will be truncated from view.
        // Expand to see the data.
        this.bodyDataLayer.setDefaultColumnWidth(this.uiPref.getColumnWidth());
    }

    private void configureColumnHeaderResizeable() {
        this.gridLayer.addConfiguration(new AbstractUiBindingConfiguration() {
            @Override
            public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
                uiBindingRegistry.registerFirstMouseMoveBinding(
                        new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 0),
                        new ColumnResizeCursorAction());

                uiBindingRegistry.registerFirstMouseDragMode(
                        new ColumnResizeEventMatcher(SWT.NONE, GridRegion.ROW_HEADER, 1), new ColumnResizeDragMode());

                uiBindingRegistry.registerFirstMouseMoveBinding(
                        new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 0), new ColumnResizeCursorAction());

                uiBindingRegistry.registerFirstMouseDragMode(
                        new ColumnResizeEventMatcher(SWT.NONE, GridRegion.CORNER, 1), new ColumnResizeDragMode());
            }
        });

    }

    /**
     * Configure load on scroll.
     *
     * @param eventTbl the event tbl
     */
    public void configureLoadOnScroll(final DSEventTable eventTbl) {
        preventConcurrentDataLoad(eventTbl);
        scrollDataLoadListener = new GridScrollEventDataLoadListener(this.viewportLayer, this.dataProvider, eventTbl,
                this.stateMachine, this);
        this.dataGrid.getVerticalBar().addListener(SWT.Selection, scrollDataLoadListener);
        this.dataGrid.addKeyListener(scrollDataLoadListener);
    }

    private void preventConcurrentDataLoad(DSEventTable eventTbl) {
        eventTbl.hook(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_CHANGED, new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                DataGrid.this.scrollDataLoadListener.resetLoadingStatus();
            }
        });
        eventTbl.hook(IDSGridUIListenable.LISTEN_TYPE_ON_ERROR, new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                DataGrid.this.scrollDataLoadListener.resetLoadingStatus();
            }
        });
    }

    /**
     * Enable header tool tip.
     */
    protected void enableHeaderToolTip() {
        dsGridToolTipProvider = new DSGridToolTipProvider(this.dataGrid,
                new String[] {GridRegion.COLUMN_HEADER, GridRegion.BODY}, dataContext, this.bodyDataProvider,
                uiPref.isEnableEdit());
    }

    private void enableCornerClickSelectAll() {
        this.cornerDataProvider.enableCornerClickSelectAll(this.dataGrid.getUiBindingRegistry(), this.selectionLayer);
    }

    private void configureSelectionCopy(GridLayer gridLyr, boolean isCopyWithColumnHeader,
            boolean isCopyWithRowheader) {
        // register a CopyDataCommandHandler that also copies the headers and
        // uses the configured IDisplayConverters
        ILayer columnHeaderLyr = null;
        ILayer rowNumberLayer = null;
        if (isCopyWithColumnHeader) {
            columnHeaderLyr = (null != this.selectionColHeaderLayer && !uiPref.isAllowColumnReorder())
                    ? this.selectionColHeaderLayer
                    : this.columnHeaderLayer;
        }

        if (isCopyWithRowheader) {
            rowNumberLayer = this.selectionRowHeaderLayer;
        }

        copyHandler = new DSCopyDataCommandHandler(selectionLayer, columnHeaderLyr, rowNumberLayer, dataProvider);

        copyHandler.setCopyFormattedText(false);
        gridLyr.registerCommandHandler(copyHandler);
    }

    /**
     * Configure selection copy with pref.
     *
     * @param gridLyr the grid lyr
     */
    protected void configureSelectionCopyWithPref(GridLayer gridLyr) {
        configureSelectionCopy(gridLyr, this.uiPref.isCopyWithColumnHeader(), this.uiPref.isCopywithRowHeader());
    }

    /**
     * Gets the config registry.
     *
     * @return the config registry
     */
    protected ConfigRegistry getConfigRegistry() {
        if (this.configRegistry == null) {
            this.configRegistry = new ConfigRegistry();
        }

        return this.configRegistry;
    }

    /**
     * Do advanced copy.
     */
    public void doAdvancedCopy() {
        setFocus();

        // update copy configuration based on user preference
        configureSelectionCopyWithPref(this.gridLayer);

        // Trigger copy command
        this.dataGrid
                .doCommand(new CopyDataToClipboardCommand("\t", MPPDBIDEConstants.LINE_SEPARATOR, getConfigRegistry()));
    }

    /**
     * Do copy.
     */
    public void doCopy() {
        setFocus();

        ICellEditor activeEditableCell = dataGrid.getActiveCellEditor();
        if (activeEditableCell != null) {
            if (activeEditableCell.getEditorControl() instanceof Text) {
                Text textControl = (Text) activeEditableCell.getEditorControl();
                textControl.copy();
            }
        } else {
            // update copy configuration to copy just cells
            configureSelectionCopy(this.gridLayer, false, false);

            // Trigger copy command
            this.dataGrid.doCommand(
                    new CopyDataToClipboardCommand("\t", MPPDBIDEConstants.LINE_SEPARATOR, getConfigRegistry()));
        }
    }

    /**
     * Do sort.
     */
    public void doSort() {
        int selectedCol = -1;
        int[] selectedCols = this.selectionLayer.getSelectedColumnPositions();
        if (selectedCols.length == 1) {
            selectedCol = this.selectionLayer.getColumnIndexByPosition(selectedCols[0]);
            ISortModel sortModel = this.sortHeaderLayer.getSortModel();
            if (null == sortModel) {
                return;
            }
            sortModel.sort(selectedCol, sortModel.getSortDirection(selectedCol).getNextSortDirection(), false);
        }
    }

    /**
     * Do search.
     *
     * @param searchText the search text
     * @param searchOptions the search options
     */
    public void doSearch(String searchText, SEARCHOPTIONS searchOptions) {
        saveReorderState();
        TextMatcherEditor<IDSGridDataRow> rowsMatcher = getMatcherEditor();
        switch (searchOptions) {
            case SRCH_CONTAINS: {
                searchContains(rowsMatcher, searchText);
                break;
            }
            case SRCH_EQUALS: {
                searchEquals(rowsMatcher, searchText);
                break;
            }
            case SRCH_STARTS_WITH: {
                searchStartsWith(rowsMatcher, searchText);
                break;
            }
            case SRCH_NULL: {
                searchRegex(rowsMatcher, searchText);
                break;
            }
            default: {
                // Commented feature: Not working for user input: 1*3
                // Need to be debugged.
                break;
            }
        }

        this.dataGrid.refresh(true);
        // : Not a clean fix to set search string from here.
        // But no other design choice as of now, so making an
        // Ugly fix. To be reviewed, later.
        if (null != this.scrollDataLoadListener) {
            this.scrollDataLoadListener.setSearchString(searchText);
        }
        if (searchText.length() > 0) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, null));
        } else {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, null));
        }
        restoreReorderState();
    }

    private void searchRegex(TextMatcherEditor<IDSGridDataRow> rowsMatcher, String searchText) {
        String[] strArray = new String[] {searchText};
        rowsMatcher.setFilterText(strArray);
        rowsMatcher.setMode(TextMatcherEditor.REGULAR_EXPRESSION);
        if (null != dataFilterList) {
            this.dataFilterList.setMatcherEditor(rowsMatcher); // String
        }
        this.getRegexMarkup().setRegexValue(isStrEmpty(searchText) ? "" : searchText);
    }

    private void search(TextMatcherEditor<IDSGridDataRow> rowsMatcher, String searchText, int mode, String quotedStr) {
        rowsMatcher.setMode(mode);
        String[] strArray = new String[] {searchText};
        rowsMatcher.setFilterText(strArray);
        if (null != dataFilterList) {
            this.dataFilterList.setMatcherEditor(rowsMatcher);
        }
        this.getRegexMarkup().setRegexValue(StringEscapeUtils.escapeHtml(isStrEmpty(searchText) ? "" : quotedStr));
    }

    private void searchStartsWith(TextMatcherEditor<IDSGridDataRow> rowsMatcher, String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : ("(?i)(^" + Pattern.quote(searchText) + ')');
        search(rowsMatcher, searchText, TextMatcherEditor.STARTS_WITH, quotedStr);
    }

    /**
     * Search for text exactly matching the given words.
     *
     * @param rowsMatcher the rows matcher
     * @param searchText the search text
     */
    private void searchEquals(TextMatcherEditor<IDSGridDataRow> rowsMatcher, String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : ("(?i)(^" + Pattern.quote(searchText) + "$)");
        search(rowsMatcher, searchText, TextMatcherEditor.EXACT, quotedStr);
    }

    /**
     * Search the text which contains the given words.
     *
     * @param rowsMatcher the rows matcher
     * @param searchText the search text
     */
    private void searchContains(TextMatcherEditor<IDSGridDataRow> rowsMatcher, String searchText) {
        String quotedStr = isStrEmpty(searchText) ? "" : "(?i)(" + Pattern.quote(searchText) + ")";
        search(rowsMatcher, searchText, TextMatcherEditor.CONTAINS, quotedStr);
    }

    /**
     * Is the given text really empty.
     *
     * @param searchText the search text
     * @return true, if is str empty
     */
    private boolean isStrEmpty(String searchText) {
        return null == searchText || searchText.isEmpty();
    }

    /**
     * Gets the regex markup.
     *
     * @return the regex markup
     */
    protected RegexMarkupValue getRegexMarkup() {
        if (this.regexMatcher == null) {
            this.regexMatcher = new DSHTMLAwareRegexMarkupValue("",
                    "<span style=\"background-color:rgb(255, 255, 0);\">", "</span>");
        }
        return this.regexMatcher;
    }

    private TextMatcherEditor<IDSGridDataRow> getMatcherEditor() {
        if (this.matcherEditor == null) {
            this.matcherEditor = new TextMatcherEditor<IDSGridDataRow>(new DSGridTextFilterator());
        }

        return this.matcherEditor;
    }

    /**
     * Clear matcher.
     */
    public void clearMatcher() {
        this.matcherEditor = null;
    }

    /**
     * Input changed.
     *
     * @param newDataProvider the new data provider
     * @param preserveScrollPos the preserve scroll pos
     */
    public void inputChanged(IDSGridDataProvider newDataProvider, boolean preserveScrollPos) {
        if (null == this.dataGrid || this.dataGrid.isDisposed()) {
            /* nattable is already disposed, skip this function call */
            return;
        }
        setDataProvider(newDataProvider);
        createAllLayerDataProviders();

        dsGridToolTipProvider.setBodyDataProvider(this.bodyDataProvider);
        bodyDataLayer.setDataProvider(this.bodyDataProvider);
        columnHeaderDataLayer.setDataProvider(this.columnHeaderDataProvider);
        rowHeaderDataLayer.setDataProvider(this.rowHeaderDataProvider);
        colLabelAccumulator = new GridColumnLabelAccumulator(this.bodyDataLayer, this.dataProvider);

        bodyDataLayer.setConfigLabelAccumulator(colLabelAccumulator);
        if (datatypeColumnAccumulator != null) {
            datatypeColumnAccumulator.setColumnLabelAccumulator(colLabelAccumulator);
        }

        if (uiPref.isEnableSort()) {
            this.sortHeaderLayer = new SortHeaderLayer<IDSGridDataRow>(columnHeaderLayer,
                    new GlazedListsSortModel<IDSGridDataRow>(this.dataSortedList, this.colPropAccessor,
                            getConfigRegistry(), selectionColHeaderLayer),
                    false);
            gridLayer.setColumnHeaderLayer(this.sortHeaderLayer);
            this.dataGrid.setLayer(gridLayer);
        }
        this.cornerLayer.setVerticalLayerDependency(
                null != this.sortHeaderLayer ? this.sortHeaderLayer : this.columnHeaderLayer);
        configureGridStyle();
        this.dataGrid.configure();
        this.dataGrid.refresh();
        this.eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, newDataProvider));
        this.eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_CHANGED, newDataProvider));

    }

    /**
     * Gets the selection layer data iterator.
     *
     * @return the selection layer data iterator
     */
    public GridSelectionLayerPortData getSelectionLayerDataIterator() {
        return new GridSelectionLayerPortData(this.selectionLayer, this.dataProvider);
    }

    /**
     * Gets the view layer data iterator.
     *
     * @return the view layer data iterator
     */
    public GridViewPortData getViewLayerDataIterator() {
        return new GridViewPortData(this.gridLayer);
    }

    /**
     * Sets the focus.
     */
    public void setFocus() {
        if (0 == this.selectionLayer.getSelectedCells().size()) {
            this.selectionLayer.setSelectedCell(0, 0);
        }
        this.dataGrid.setFocus();
    }

    /**
     * Sets the focus.
     *
     * @param columnIndex the column index
     * @param rowIndex the row index
     */
    public void setFocus(int columnIndex, int rowIndex) {
        this.selectionLayer.setLastSelectedCell(columnIndex, rowIndex);
        this.dataGrid.setFocus();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DSGridTextFilterator.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private final class DSGridTextFilterator implements TextFilterator<IDSGridDataRow> {
        private String encoding;
        private String convertedValue;

        @Override
        public void getFilterStrings(List<String> baseList, IDSGridDataRow row) {
            // Use cloned values to avoid accidental/unknown updates.
            Object[] values = row.getClonedValues();
            for (int colIndex = 0; colIndex < values.length; colIndex++) {
                Object value = values[colIndex];

                if (null != value) {
                    String columnDataType = dataProvider.getColumnDataProvider().getColumnDataTypeName(colIndex);
                    if (uiPref.isIncludeEncoding()) {
                        if (dataProvider instanceof DSResultSetGridDataProvider) {
                            encoding = ((DSResultSetGridDataProvider) dataProvider).getEncoding();
                            if ("".equals(encoding)) {
                                encoding = uiPref.getDefaultEncoding();
                            }
                            if (value instanceof byte[]) {
                                byte[] valu1 = (byte[]) value;

                                try {
                                    convertedValue = new String(valu1, encoding);
                                } catch (UnsupportedEncodingException exception) {
                                    MPPDBIDELoggerUtility.error("DSGridTextFilterator: text filterator failed.",
                                            exception);
                                }
                            } else {
                                convertedValue = value.toString();
                            }

                        }
                    } else {
                        convertedValue = value.toString();
                    }
                    baseList.add(getConvertedColumnData(convertedValue, columnDataType));
                } else {
                    // this is important for null value search
                    baseList.add("");
                }
            }
        }

        private String getConvertedColumnData(String dataValue, String columnDataType) {
            if ("bit".equals(columnDataType)) {
                if (Boolean.toString(true).equalsIgnoreCase(dataValue)) {
                    return "1";
                } else {
                    return "0";
                }
            }
            return dataValue;
        }

    }

    /**
     * Insert empty row.
     *
     * @param isSearched the is searched
     * @param serverObject the server object
     */
    public void insertEmptyRow(boolean isSearched, ServerObject serverObject) {
        IDSGridEditDataRow newRow = null;
        saveReorderState();
        if (dataGrid.getActiveCellEditor() != null) {
            dataGrid.commitAndCloseActiveCellEditor();
        }

        // if Searched then add it to the beginning.
        int insertedRowIndex = 0;

        if (!isSearched) {
            insertedRowIndex = getSelectedRowPosition();
            if (insertedRowIndex >= 0) {
                insertedRowIndex++;
            } else if (insertedRowIndex == -1) {
                insertedRowIndex = 0;
            }
        }
        if (serverObject == null) {
            IDSEditGridDataProvider dp = (IDSEditGridDataProvider) dataProvider;
            newRow = dp.getEmptyRowForInsert(insertedRowIndex);
        } else {
            newRow = ((DSObjectPropertiesGridDataProvider) dataProvider).createNewRow(serverObject, insertedRowIndex);
        }
        if (newRow == null) {
            return;
        }
        this.dataEventList.add(insertedRowIndex, newRow);
        dataGrid.refresh();
        setFocus(0, newRow.getRowIndex());
        restoreReorderState();
    }

    /**
     * Delete row.
     */
    public void deleteRow() {
        saveReorderState();
        setFocus();
        int[] cellPosForDelete = getSelectedRowsForDelete();
        Arrays.sort(cellPosForDelete);
        int numDeleted = 0;
        boolean isInserted = true;
        IDSGridEditDataRow row = null;
        if (!this.dataEventList.isEmpty()) {

            for (int position = 0; position < cellPosForDelete.length; position++) {
                row = (IDSGridEditDataRow) this.bodyDataProvider.getRowObject(cellPosForDelete[position] - numDeleted);
                if (row.getUpdatedState() == EditTableRecordStates.INSERT) {
                    isInserted = true;
                    int index = cellPosForDelete[position] - numDeleted;
                    this.dataEventList.remove(index);
                    ((IDSEditGridDataProvider) dataProvider).deleteRecord(row, isInserted);

                    numDeleted++;
                } else {
                    isInserted = false;
                    ((IDSEditGridDataProvider) dataProvider).deleteRecord(row, isInserted);
                }

            }

            dataGrid.refresh();
        }
        restoreReorderState();
    }

    private int[] getSelectedRowsForDelete() {
        ArrayList<Integer> selectedRows = new ArrayList<Integer>();
        int colCount = this.selectionLayer.getColumnCount();
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        PositionCoordinate[] selectedCell = this.selectionLayer.getSelectedCellPositions();
        for (int cnt = 0; cnt < selectedCell.length; cnt++) {
            if (map.containsKey(selectedCell[cnt].rowPosition)) {
                map.put(selectedCell[cnt].rowPosition, map.get(selectedCell[cnt].rowPosition) + 1);
            } else {
                map.put(selectedCell[cnt].rowPosition, 1);
            }
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();
            if (value == colCount) {
                selectedRows.add(key);
            }
        }

        int[] selectedRowArray = new int[selectedRows.size()];
        int rowId = 0;
        for (Integer rowIdx : selectedRows) {
            selectedRowArray[rowId] = rowIdx;
            rowId++;
        }

        return selectedRowArray;
    }

    /**
     * Roll back changes.
     */
    public void rollBackChanges() {
        setFocus();
        if (dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDP = (IDSEditGridDataProvider) dataProvider;
            editDP.rollBackProvider();
        }
        this.dataEventList.clear();

        this.dataEventList.addAll(dataProvider.getAllFetchedRows());
        dataGrid.refresh();
    }

    /**
     * Gets the selected row position.
     *
     * @return the selected row position
     */
    public int getSelectedRowPosition() {
        int index = -1;
        PositionCoordinate position = this.selectionLayer.getLastSelectedCellPosition();
        if (position != null) {
            ILayerCell cell = this.selectionLayer.getCellByPosition(position.getColumnPosition(),
                    position.getRowPosition());
            if (cell != null) {
                index = cell.getRowPosition();
            }

        }
        return index;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CopyDataWithPrefAction.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class CopyDataWithPrefAction extends CopyDataAction {
        @Override
        public void run(NatTable natTable, KeyEvent event) {
            // Call customized Copy action.
            doAdvancedCopy();
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class CopyDataWithoutPrefAction.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class CopyDataWithoutPrefAction extends CopyDataAction {
        @Override
        public void run(NatTable natTable, KeyEvent event) {
            // Call customized Copy action.
            doCopy();
        }
    }

    /**
     * Update grid data.
     */
    public void updateGridData() {
        dataGrid.refresh();
    }

    /**
     * Refresh.
     */
    public void refresh() {
        setFocus();
        if ((dataProvider instanceof IDSEditGridDataProvider
                && !((IDSEditGridDataProvider) dataProvider).isGridDataEdited())
                || dataProvider instanceof IDSGridDataProvider) {
            if (!stateMachine.isLoading()) {
                eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY, dataProvider));
            }

        }
    }

    /**
     * Paste.
     */
    public void paste() {
        setFocus();
        dataGrid.doCommand(new GridPasteCommand(getConfigRegistry()));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PasteDataWithPrefAction.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class PasteDataWithPrefAction extends PasteDataAction {
        @Override
        public void run(NatTable natTable, KeyEvent event) {
            // Call customized paste action.
            paste();
        }
    }

    /**
     * Gets the commit status listener.
     *
     * @return the commit status listener
     */
    public IDSListener getCommitStatusListener() {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        DataGrid.this.dataGrid.refresh();
                    }
                });

            }
        };
    }

    /**
     * Commit and close active cell editor.
     */
    public void commitAndCloseActiveCellEditor() {
        dataGrid.commitAndCloseActiveCellEditor();

    }

    /**
     * Close editor.
     */
    public void closeEditor() {
        ICellEditor cellEditor = dataGrid.getActiveCellEditor();
        if (null != cellEditor && !cellEditor.isClosed()) {
            // Force close with the cost of losing the data.
            cellEditor.close();
        }
    }

    /**
     * Checks if is modified records saveable.
     *
     * @return true, if is modified records saveable
     */
    public boolean isModifiedRecordsSaveable() {
        if (dataGrid.getActiveCellEditor() != null) {
            // If cell is in edit mode, attempt to close the cell and do not
            // allow to commit
            // as the data entered might be invalid.
            commitAndCloseActiveCellEditor();
            return false;
        }
        setFocus();
        return !this.dataEventList.isEmpty();
    }

    /**
     * Adds the grid layer listener.
     *
     * @param iLayerListener the i layer listener
     */
    public void addGridLayerListener(ILayerListener iLayerListener) {
        dataGrid.addLayerListener(iLayerListener);
    }

    /**
     * Checks if is rows only selected.
     *
     * @return true, if is rows only selected
     */
    public boolean isRowsOnlySelected() {
        /*
         * Null check on selectionLayer for ignoring discard changes call on
         * closing of Edit Table window
         */
        if (this.selectionLayer != null) {
            int rowCount = this.selectionLayer.getSelectedRowCount();
            int fullySelectedRowsCount = getSelectedRowsForDelete().length;
            return fullySelectedRowsCount > 0 && fullySelectedRowsCount == rowCount;
        }
        return false;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class DataGridContext.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class DataGridContext implements IDataGridContext {
        private IDSGridDataProvider dp;

        @Override
        public IDSGridDataProvider getDataProvider() {
            return dp;
        }

        @Override
        public void setDataProvider(IDSGridDataProvider dataProvider) {
            this.dp = dataProvider;
        }

        @Override
        public List<ColumnMetaData> getColumnMetaDataList() {
            TableMetaData table = (TableMetaData) dp.getTable();
            if (table != null) {
                return table.getColumnMetaDataList();
            }
            return null;
        }

        /**
         * On pre destroy.
         */
        public void onPreDestroy() {
            dp = null;
        }
    }

    /**
     * Checks if is properties grid.need to check if it can be differentiated
     * with uipref
     *
     * @return true, if is properties grid
     */
    public boolean isPropertiesGrid() {

        if (this.dataProvider.getClass() == DSObjectPropertiesGridDataProvider.class) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is user role properties grid.
     *
     * @return true, if is user role properties grid
     */
    public boolean isUserRolePropertiesGrid() {
        if (dataProvider instanceof DSObjectPropertiesGridDataProvider
                && ((DSObjectPropertiesGridDataProvider) dataProvider)
                        .getObjectPropertyObject() instanceof PropertiesUserRoleImpl) {
            return true;
        }
        return false;
    }

    /**
     * Gets the type of dialog required.
     *
     * @return the type of dialog required
     */
    public ServerObjectTypeForDialog getTypeOfDialogRequired() {
        if (!(this.dataProvider instanceof DSObjectPropertiesGridDataProvider)) {
            return null;
        }
        String objectPropertyName = ((DSObjectPropertiesGridDataProvider) dataProvider).getObjectPropertyName();
        switch (objectPropertyName) {
            case "General": {
                return ServerObjectTypeForDialog.GENERAL;
            }
            case "Columns": {
                return ServerObjectTypeForDialog.COLUMNS;
            }
            case "Constraints": {
                return ServerObjectTypeForDialog.CONSTRAINTS;
            }
            case "Index": {
                return ServerObjectTypeForDialog.INDEX;
            }
            default: {
                break;
            }

        }
        return null;

    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Composite getParent() {
        return this.parent;
    }

    /**
     * Sets the parent.
     *
     * @param composite the new parent
     */
    public void setParent(Composite composite) {
        this.parent = composite;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RightClickShowMenuConfiguration.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    class RightClickShowMenuConfiguration extends AbstractUiBindingConfiguration {
        private Menu menu;
        private Menu showMenu;
        private MenuItem copyToExcelMenuItem;
        private MenuItem copyToxlsMenuItem;
        private MenuItem copyToxlsxMenuItem;
        private MenuItem copyToclipboardMenuItem;
        private MenuItem advancedCopyMenuItem;
        private MenuItem copyMenuItem;
        private MenuItem searchMenuItem;
        private MenuItem setNullMenuItem;
        private MenuItem exportMenuItem;
        private MenuItem exportCurrentPageItem;
        private MenuItem exportAllDataMenuItem;
        private MenuItem generateInsertMenuItem;
        private MenuItem generateSelectedRowInsertMenuItem;
        private MenuItem generateCurrentInsertMenuItem;
        private MenuItem generateAllInsertMenuItem;

        private ShowCopyToExcelMenuItemProvider showCopyToExcelMenuItemProvider;
        private ShowSearchItemProvider showSearchItemProvider;
        private ShowGenerateInsertItemProvider showGenerateInsertItemProvider;
        private ShowExpotDataItemProvider showExpotDataItemProvider;
        private ShowCopyMenuItemProvider menuItemProvider;
        private ShowSetNullItemProvider showsetNullItemProvider;

        /**
         * Instantiates a new right click show menu configuration.
         *
         * @param dsNatTable the ds nat table
         */
        public RightClickShowMenuConfiguration(DSNatTable dsNatTable) {

            this.showCopyToExcelMenuItemProvider = new ShowCopyToExcelMenuItemProvider();
            this.showGenerateInsertItemProvider = new ShowGenerateInsertItemProvider();
            this.showSearchItemProvider = new ShowSearchItemProvider();
            this.showExpotDataItemProvider = new ShowExpotDataItemProvider();
            if (uiPref.isShowGenerateInsert()) {
                menuItemProvider = new ShowCopyMenuItemProvider();
                showsetNullItemProvider = new ShowSetNullItemProvider();

                PopupMenuBuilder builder = new PopupMenuBuilder(dsNatTable).withMenuItemProvider(menuItemProvider);
                builder.withMenuItemProvider(showCopyToExcelMenuItemProvider);
                menu = builder.withMenuItemProvider(showExpotDataItemProvider)
                        .withMenuItemProvider(showGenerateInsertItemProvider)
                        .withMenuItemProvider(showsetNullItemProvider).withMenuItemProvider(showSearchItemProvider)
                        .build();
            } else {
                ShowCopyMenuItemProvider showCopyMenuItemProvider = new ShowCopyMenuItemProvider();
                showsetNullItemProvider = new ShowSetNullItemProvider();
                PopupMenuBuilder builder = new PopupMenuBuilder(dsNatTable)
                        .withMenuItemProvider(showCopyMenuItemProvider);
                builder.withMenuItemProvider(showCopyToExcelMenuItemProvider);
                menu = builder.withMenuItemProvider(showExpotDataItemProvider)
                        .withMenuItemProvider(showsetNullItemProvider).withMenuItemProvider(showSearchItemProvider)
                        .build();
            }

            this.menu.addMenuListener(new ShowMenuConditional());
        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowMenuConditional.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowMenuConditional implements MenuListener {

            @Override
            public void menuHidden(MenuEvent event) {

            }

            @Override
            public void menuShown(MenuEvent event) {
                getCopyToExcelMenu();
                getSearchMenu();
                setNullMenu();
                getGenerateInsertItem();
                showMenuItemForOpenConnection();

                if (copyToclipboardMenuItem != null && !copyToclipboardMenuItem.isDisposed()) {
                    copyToclipboardMenuItem.setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
                }

                if (showExpotDataItemProvider.getExportMenuItem() != null
                        && !showExpotDataItemProvider.getExportMenuItem().isDisposed()) {
                    showExpotDataItemProvider.getExportMenuItem()
                            .setEnabled(IHandlerUtilities.getExportDataSelectionOptions());
                }

                IHandlerUtilities.setMenuItemEnabled(showCopyToExcelMenuItemProvider.getCopyToExcelMenuItem(),
                        IHandlerUtilities.getExportDataSelectionOptions());
                IHandlerUtilities.setMenuItemEnabled(showGenerateInsertItemProvider.getGenerateInsertMenuItem(),
                        IHandlerUtilities.getExportDataSelectionOptions());

            }

            private void showMenuItemForOpenConnection() {
                if (!uiPref.isClosedConnection()) {
                    if (uiPref.isShowGenerateInsert()) {
                        showExpotDataItemProvider.getExportAllDataMenuItem().setEnabled(false);
                        showGenerateInsertItemProvider.getGenerateAllInsertMenuItem().setEnabled(false);
                    } else {
                        showExpotDataItemProvider.getExportAllDataMenuItem().setEnabled(false);
                    }
                } else {
                    if (uiPref.isShowGenerateInsert()) {
                        showExpotDataItemProvider.getExportAllDataMenuItem()
                                .setEnabled(!dataProvider.isFuncProcExport());
                        showGenerateInsertItemProvider.getGenerateAllInsertMenuItem().setEnabled(true);
                    } else {
                        showExpotDataItemProvider.getExportAllDataMenuItem()
                                .setEnabled(!dataProvider.isFuncProcExport());
                    }
                }
            }

            private void getGenerateInsertItem() {
                if (!uiPref.isStartSelectQuery() && uiPref.isShowGenerateInsert()) {
                    showGenerateInsertItemProvider.getGenerateInsertMenuItem().setEnabled(false);
                } else if (showGenerateInsertItemProvider.getGenerateInsertMenuItem() != null) {
                    showGenerateInsertItemProvider.getGenerateInsertMenuItem().setEnabled(true);
                } else {
                    // do nothing
                }
            }

            private void setNullMenu() {
                if (uiPref.isEnableEdit() && ((IDSEditGridDataProvider) dataProvider).isEditSupported()) {
                    if (null != uiPref.getSelectedEncoding() && uiPref.getSelectedEncoding().equals(PreferenceWrapper
                            .getInstance().getPreferenceStore().getString(UserEncodingOption.DATA_STUDIO_ENCODING))) {
                        setNullMenuItem.setEnabled(true);
                    } else {
                        setNullMenuItem.setEnabled(false);
                    }

                } else {
                    setNullMenuItem.setEnabled(false);
                }
            }

            private void getSearchMenu() {
                if (selectionLayer.getSelectedCells().size() > 1) {
                    showSearchItemProvider.getSearchMenuItem().setEnabled(false);
                } else {
                    handleSearchMenuEnableForSingleSelection();
                }
            }

            private void handleSearchMenuEnableForSingleSelection() {
                if (selectionLayer.getSelectedCellPositions().length == 0
                        || (dataProvider != null && isSelectedCellColumnUnstrucredDatatype())) {
                    searchMenuItem.setEnabled(false);
                } else {
                    searchMenuItem.setEnabled(true);
                }
            }

            private boolean isSelectedCellColumnUnstrucredDatatype() {
                String datatypeName = dataProvider.getColumnDataProvider()
                        .getColumnDataTypeName(selectionLayer.getSelectedCellPositions()[0].columnPosition);
                if (datatypeName == null) {
                    return false;
                }
                switch (datatypeName) {
                    case MPPDBIDEConstants.BLOB:
                    case MPPDBIDEConstants.BYTEA: {
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            }

            private void getCopyToExcelMenu() {
                if (0 == selectionLayer.getSelectedCells().size()) {
                    showCopyToExcelMenuItemProvider.getCopyToExcelMenuItem().setEnabled(false);
                } else {
                    showCopyToExcelMenuItemProvider.getCopyToExcelMenuItem().setEnabled(true);
                }
            }

        }

        /**
         * Configure ui bindings.
         *
         * @param uiBindingRegistry the ui binding registry
         */
        @Override
        public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
            uiBindingRegistry.registerMouseDownBinding(
                    new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON),
                    new PopupMenuAction(this.menu));
            uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.F9), new IKeyAction() {
                @Override
                public void run(NatTable nattable, KeyEvent keyevent) {
                    menu.setVisible(true);
                }
            });
        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowCopyToExcelMenuItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowCopyToExcelMenuItemProvider implements IMenuItemProvider {

            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {
                copyToExcelMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
                copyToExcelMenuItem
                        .setText(MessageConfigLoader.getProperty(IMessagesConstants.COPY_TO_EXCEL_RESULT_WINDOW_MENU));
                showMenu = new Menu(popupMenu);
                copyToExcelMenuItem.setMenu(showMenu);
                copyToxlsMenuItem = new MenuItem(showMenu, SWT.PUSH);
                copyToxlsMenuItem.setText(
                        MessageConfigLoader.getProperty(IMessagesConstants.COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM));
                copyToxlsxMenuItem = new MenuItem(showMenu, SWT.PUSH);
                copyToxlsxMenuItem.setText(
                        MessageConfigLoader.getProperty(IMessagesConstants.COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM));

                // copy to xls
                copyToxlsMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                            eventTable.sendEvent(
                                    new DSEvent(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM,
                                            new Observer() {
                                                @Override
                                                public void update(Observable observable, Object obj) {
                                                    if (obj instanceof Boolean && ((boolean) obj)) {
                                                        stateMachine.set(DSGridStateMachine.State.IDLE);
                                                    }

                                                }
                                            }));
                        }

                    }

                });
                // copy to xlsx
                copyToxlsxMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                            eventTable.sendEvent(
                                    new DSEvent(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM,
                                            new Observer() {
                                                @Override
                                                public void update(Observable observable, Object obj) {
                                                    if (obj instanceof Boolean && ((boolean) obj)) {
                                                        if (stateMachine != null) {
                                                            stateMachine.set(DSGridStateMachine.State.IDLE);
                                                        }
                                                    }

                                                }
                                            }));
                        }
                    }

                });

            }

            /**
             * Gets the copy to excel menu item.
             *
             * @return the copy to excel menu item
             */
            public MenuItem getCopyToExcelMenuItem() {
                return copyToExcelMenuItem;
            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowCopyMenuItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowCopyMenuItemProvider implements IMenuItemProvider {
            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {
                copyToclipboardMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
                copyToclipboardMenuItem.setText(MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_COPY_TO_CLIPBOARD_RESULT_WINDOW_CONTENTS));
                showMenu = new Menu(popupMenu);
                copyToclipboardMenuItem.setMenu(showMenu);
                copyMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsg = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_COPY_RESULT_WINDOW_CONTENTS);
                copyMenuItem.setText(toolTipMsg);

                copyMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        doCopy();
                    }

                });
                advancedCopyMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String copyToolTipMsg = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_COPY_ADVANCED_RESULT_WINDOW_CONTENTS);
                advancedCopyMenuItem.setText(copyToolTipMsg);
                advancedCopyMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {

                        doAdvancedCopy();

                    }

                });
            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowSearchItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowSearchItemProvider implements IMenuItemProvider {

            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {

                searchMenuItem = new MenuItem(popupMenu, SWT.PUSH);
                String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.SEARCH_RESULT_WINDOW_CONTENTS);
                searchMenuItem.setText(toolTipMsg);
                searchMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        Object dataValueByPosition = null;
                        if (0 == selectionLayer.getSelectedCells().size()) {
                            NatEventData natEventData = MenuItemProviders.getNatEventData(event);
                            int rowPosition = natEventData == null ? 0 : natEventData.getRowPosition();
                            int columnPosition = natEventData == null ? 0 : natEventData.getColumnPosition();
                            dataValueByPosition = natTable.getDataValueByPosition(columnPosition, rowPosition);
                        }
                        eventTable.sendEvent(new DSEvent(
                                IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH, dataValueByPosition));
                    }

                });
            }

            /**
             * Gets the search menu item.
             *
             * @return the search menu item
             */
            public MenuItem getSearchMenuItem() {
                return searchMenuItem;
            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowSetNullItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowSetNullItemProvider implements IMenuItemProvider {

            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {
                setNullMenuItem = new MenuItem(popupMenu, SWT.PUSH);
                String toolTipMsg = MessageConfigLoader.getProperty(IMessagesConstants.SET_NULL_RESULT_WINDOW_CONTENTS);
                setNullMenuItem.setText(toolTipMsg);
                setNullMenuItem.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (0 == selectionLayer.getSelectedCells().size()) {
                            NatEventData natEventData = MenuItemProviders.getNatEventData(event);
                            int rowPosition = natEventData == null ? 0 : natEventData.getRowPosition();
                            int columnPosition = natEventData == null ? 0 : natEventData.getColumnPosition();
                            natTable.doCommand(new UpdateDataCommand(natTable, columnPosition, rowPosition, null));
                        } else {
                            UpdateDataCommandHandler updateHandler = new UpdateDataCommandHandler(bodyDataLayer);
                            PositionCoordinate[] selectedCellPositions = selectionLayer.getSelectedCellPositions();
                            for (PositionCoordinate positionCoordinate : selectedCellPositions) {
                                int columnPosition = positionCoordinate.getColumnPosition();
                                int rowPosition = positionCoordinate.getRowPosition();
                                updateHandler.doCommand(natTable,
                                        new UpdateDataCommand(natTable, columnPosition, rowPosition, null));
                            }

                        }

                    }
                });
            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowGenerateInsertItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowGenerateInsertItemProvider implements IMenuItemProvider {

            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {
                generateInsertMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
                generateInsertMenuItem.setText(MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_GENERATE_INSERT_RESULT_WINDOW_CONTENTS));
                showMenu = new Menu(popupMenu);
                generateInsertMenuItem.setMenu(showMenu);
                generateSelectedRowInsertMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsg = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_SELECTED_ROW_INSERT_RESULT_WINDOW_CONTENTS);
                generateSelectedRowInsertMenuItem.setText(toolTipMsg);
                generateSelectedRowInsertMenuItem.addSelectionListener(selectedRowInsertMenuSelectionListener());
                generateCurrentInsertMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsgCurrentInsert = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_CURRENT_INSERT_RESULT_WINDOW_CONTENTS);
                generateCurrentInsertMenuItem.setText(toolTipMsgCurrentInsert);
                generateCurrentInsertMenuItem.addSelectionListener(currentInsertMenuSelectionListener());
                generateAllInsertMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsgAllData = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_ALL_DATA_INSERT_RESULT_WINDOW_CONTENTS);
                generateAllInsertMenuItem.setText(toolTipMsgAllData);
                generateAllInsertMenuItem.addSelectionListener(allInsertMenuSelectionListener());
            }

            private SelectionAdapter allInsertMenuSelectionListener() {
                return new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        eventTable.sendEvent(new DSEvent(
                                IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT, new Observer() {
                                    @Override
                                    public void update(Observable observable, Object obj) {
                                        if (obj instanceof Boolean && ((boolean) obj)) {
                                            stateMachine.set(DSGridStateMachine.State.IDLE);
                                        }

                                    }
                                }));
                    }

                };
            }

            private SelectionAdapter currentInsertMenuSelectionListener() {
                return new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        eventTable.sendEvent(new DSEvent(
                                IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT,
                                new Observer() {
                                    @Override
                                    public void update(Observable observable, Object obj) {
                                        if (obj instanceof Boolean && ((boolean) obj)) {
                                            stateMachine.set(DSGridStateMachine.State.IDLE);
                                        }

                                    }
                                }));
                    }

                };
            }

            private SelectionAdapter selectedRowInsertMenuSelectionListener() {
                return new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        setFocus();
                        eventTable.sendEvent(new DSEvent(
                                IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT,
                                new Observer() {
                                    @Override
                                    public void update(Observable observable, Object obj) {
                                        if (obj instanceof Boolean && ((boolean) obj)) {
                                            stateMachine.set(DSGridStateMachine.State.IDLE);
                                        }

                                    }
                                }));
                    }

                };
            }

            /**
             * Gets the generate insert menu item.
             *
             * @return the generate insert menu item
             */
            public MenuItem getGenerateInsertMenuItem() {
                return generateInsertMenuItem;
            }

            /**
             * Gets the generate all insert menu item.
             *
             * @return the generate all insert menu item
             */
            public MenuItem getGenerateAllInsertMenuItem() {
                return generateAllInsertMenuItem;
            }

        }

        /**
         * 
         * Title: class
         * 
         * Description: The Class ShowExpotDataItemProvider.
         * 
         * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
         *
         * @author pWX553609
         * @version [DataStudio 6.5.1, 17 May, 2019]
         * @since 17 May, 2019
         */
        private class ShowExpotDataItemProvider implements IMenuItemProvider {

            @Override
            public void addMenuItem(NatTable natTable, Menu popupMenu) {
                exportMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
                exportMenuItem.setText(
                        MessageConfigLoader.getProperty(IMessagesConstants.RIGHT_CLICK_EXPORT_RESULT_WINDOW_CONTENTS));
                showMenu = new Menu(popupMenu);
                exportMenuItem.setMenu(showMenu);
                exportCurrentPageItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsg1 = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_EXPORT_CURRENT_PAGE_DATA_RESULT_WINDOW_CONTENTS);
                exportCurrentPageItem.setText(toolTipMsg1);
                exportCurrentPageItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
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
                });
                exportAllDataMenuItem = new MenuItem(showMenu, SWT.PUSH);
                String toolTipMsg2 = MessageConfigLoader
                        .getProperty(IMessagesConstants.RIGHT_CLICK_EXPORT_ALL_DATA_RESULT_WINDOW_CONTENTS);
                exportAllDataMenuItem.setText(toolTipMsg2);
                exportAllDataMenuItem.setEnabled(!dataProvider.isFuncProcExport());
                exportAllDataMenuItem.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent event) {
                        if (stateMachine.set(DSGridStateMachine.State.EXPORTING)) {
                            eventTable.sendEvent(
                                    new DSEvent(IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA, new Observer() {
                                        @Override
                                        public void update(Observable observable, Object obj) {
                                            if (obj instanceof Boolean && !((boolean) obj)) {
                                                stateMachine.set(DSGridStateMachine.State.IDLE);
                                            }

                                        }
                                    }));
                        }
                    }

                });

            }

            /**
             * Gets the export all data menu item.
             *
             * @return the export all data menu item
             */
            public MenuItem getExportAllDataMenuItem() {
                return exportAllDataMenuItem;
            }

            /**
             * Gets the export parent menu item.
             *
             * @return the export parent menu item
             */
            public MenuItem getExportMenuItem() {
                return exportMenuItem;
            }

        }

        /**
         * Pre destroy.
         */
        public void preDestroy() {
            showCopyToExcelMenuItemProvider = null;
            showSearchItemProvider = null;
            showGenerateInsertItemProvider = null;
            showExpotDataItemProvider = null;
            menuItemProvider = null;
            showsetNullItemProvider = null;
        }

    }

}
