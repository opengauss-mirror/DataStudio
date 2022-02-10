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

package org.opengauss.mppdbide.view.component.grid.core;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.GlazedListsEventLayer;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeData;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.tree.GlazedListTreeRowModel;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.NatTableBorderOverlayPainter;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.tree.ITreeRowModel;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;
import org.eclipse.nebula.widgets.nattable.tree.config.TreeLayerExpandCollapseKeyBindings;
import org.eclipse.swt.widgets.Composite;

import org.opengauss.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayData;
import org.opengauss.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import org.opengauss.mppdbide.explainplan.ui.model.TreeGridColumnHeader;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.component.DSGridStateMachine;
import org.opengauss.mppdbide.view.component.IGridUIPreference;
import org.opengauss.mppdbide.view.component.grid.DSGridToolTipProvider;
import org.opengauss.mppdbide.view.component.grid.TableGridStyleConfiguration;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.TransformedList;
import ca.odell.glazedlists.TreeList;

/**
 * 
 * Title: class
 * 
 * Description: The Class TreeDataGrid.
 *
 * @since 3.0.0
 */
public class TreeDataGrid extends DataGrid {
    private Map<String, String> columnLabelMap;
    private List<ExplainAnalyzePlanNodeTreeDisplayData> modelData;
    private Composite shell;
    private BodyLayerStack<ExplainAnalyzePlanNodeTreeDisplayData> bodyLayerStack;
    private IColumnPropertyAccessor<ExplainAnalyzePlanNodeTreeDisplayData> columnPropertyAccessor;
    private ExecutionPlanNodeTypeConfiguration planNodeStyleConfig;

    /**
     * On pre destroy.
     */
    @Override
    public void onPreDestroy() {
        if (this.planNodeStyleConfig != null) {
            this.planNodeStyleConfig.preDestroy();
        }
        this.columnPropertyAccessor = null;
        if (this.bodyLayerStack != null) {
            this.bodyLayerStack.preDestroy();
        }
        this.bodyLayerStack = null;

        if (this.modelData != null) {
            this.modelData.clear();
        }
        if (this.columnLabelMap != null) {
            this.columnLabelMap.clear();
        }
        this.modelData = null;
        this.columnLabelMap = null;
        super.onPreDestroy();
    }

    /**
     * Gets the model data.
     *
     * @return the model data
     */
    public List<ExplainAnalyzePlanNodeTreeDisplayData> getModelData() {
        return modelData;
    }

    /**
     * Sets the model data.
     *
     * @param modelData the new model data
     */
    public void setModelData(List<ExplainAnalyzePlanNodeTreeDisplayData> modelData) {
        if (this.modelData != null) {
            this.modelData.clear();
        }
        this.modelData = modelData;
    }

    /**
     * Gets the column label map.
     *
     * @return the column label map
     */
    public Map<String, String> getColumnLabelMap() {
        return this.columnLabelMap;
    }

    /**
     * Sets the column label map.
     *
     * @param columnLabelMap the column label map
     */
    public void setColumnLabelMap(Map<String, String> columnLabelMap) {
        this.columnLabelMap = columnLabelMap;
    }

    /**
     * Instantiates a new tree data grid.
     *
     * @param uiPref the ui pref
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param stateMachine the state machine
     */
    public TreeDataGrid(IGridUIPreference uiPref, ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat dataProvider,
            DSEventTable eventTable, DSGridStateMachine stateMachine) {
        super(uiPref, dataProvider, eventTable, stateMachine);
        this.dataGrid = null;
        this.modelData = dataProvider.getNodes();
    }

    /**
     * Sets the focus.
     */
    public void setFocus() {
        this.dataGrid.setFocus();
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
     * Update grid data.
     */
    public void updateGridData() {
        refresh();
    }

    /**
     * Creates the all layer data providers.
     */
    @Override
    protected void createAllLayerDataProviders() {
        columnPropertyAccessor = new ReflectiveColumnPropertyAccessor<>(
                this.dataProvider.getColumnDataProvider().getColumnNames());

        this.bodyLayerStack = new BodyLayerStack<>(this.modelData, columnPropertyAccessor,
                (ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat) this.dataProvider);

        // build the column header layer
        columnHeaderDataProvider = new GridColHeaderDataProvider(this.dataProvider);
        columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
        columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayerStack,
                bodyLayerStack.getSelectionLayer());

        // build the row header layer
        rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyLayerStack.getBodyDataProvider());
        rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
        rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayerStack, bodyLayerStack.getSelectionLayer());

        // build the corner layer
        this.cornerDataProvider = new GridCornerLayerDataProvider(dataContext);
        bodyDataLayer = new DataLayer(bodyLayerStack.getBodyDataProvider());

        cornerLayer = new CornerLayer(bodyDataLayer, rowHeaderLayer, columnHeaderLayer);

        gridLayer = new GridLayer(bodyLayerStack, columnHeaderLayer, rowHeaderLayer, cornerLayer);

        this.dataEventList = bodyLayerStack.getEventList();
        this.dataFilterList = bodyLayerStack.getFilterList();
        this.reorderLayer = bodyLayerStack.getReorderLayer();
        this.selectionLayer = bodyLayerStack.getSelectionLayer();
        this.viewportLayer = bodyLayerStack.getViewportLayer();

        configureSelectionCopyWithPref(gridLayer);
    }

    /**
     * Enable header tool tip.
     */
    @Override
    protected void enableHeaderToolTip() {
        dsGridToolTipProvider = new DSGridToolTipProvider(this.dataGrid, new String[] {GridRegion.BODY}, dataContext,
                this.bodyDataProvider, uiPref.isEnableEdit());
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class BodyLayerStack.
     * 
     * @param <T> the generic type
     */
    static class BodyLayerStack<T> extends AbstractLayerTransform {

        private final IDataProvider bodyDataProvider;

        private final SelectionLayer selectionLayer;

        private final TreeLayer treeLayer;

        private EventList<T> eventList;

        private TransformedList<T, T> rowObjectsGlazedList;

        private ColumnReorderLayer reorderLayer;

        private GridViewPortLayer viewportLayer;

        /**
         * Pre destroy.
         */
        public void preDestroy() {
            if (this.eventList != null) {
                this.eventList.clear();
            }
            this.eventList = null;
            this.rowObjectsGlazedList = null;
            this.reorderLayer = null;
            this.viewportLayer = null;
        }

        /**
         * Instantiates a new body layer stack.
         *
         * @param values the values
         * @param columnPropertyAccessor the column property accessor
         * @param treeFormat the tree format
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public BodyLayerStack(List<T> values, IColumnPropertyAccessor<T> columnPropertyAccessor,
                TreeList.Format<T> treeFormat) {

            eventList = GlazedLists.eventList(values);
            rowObjectsGlazedList = GlazedLists.threadSafeList(eventList);

            SortedList<T> sortedList = new SortedList<>(rowObjectsGlazedList, null);

            TreeList<T> treeList = new TreeList<T>(sortedList, treeFormat, TreeList.NODES_START_EXPANDED);

            this.bodyDataProvider = new ListDataProvider<>(treeList, columnPropertyAccessor);
            DataLayer bodyDataLayer = new DataLayer(this.bodyDataProvider);

            TreeGridColumnAccumulator columnLabelAccumulator = new TreeGridColumnAccumulator(this.bodyDataProvider,
                    (ReflectiveColumnPropertyAccessor) columnPropertyAccessor);
            bodyDataLayer.setConfigLabelAccumulator(columnLabelAccumulator);

            bodyDataLayer.setDefaultColumnWidthByPosition(0, 150);
            // lets keep the node type column little wider

            GlazedListsEventLayer<T> glazedListsEventLayer = new GlazedListsEventLayer<>(bodyDataLayer, treeList);

            GlazedListTreeData<T> treeData = new GlazedListTreeData<>(treeList);
            ITreeRowModel<T> treeRowModel = new GlazedListTreeRowModel<>(treeData);

            this.reorderLayer = new ColumnReorderLayer(glazedListsEventLayer);

            this.selectionLayer = new SelectionLayer(this.reorderLayer);

            this.treeLayer = new TreeLayer(this.selectionLayer, treeRowModel);
            this.treeLayer.setUseTreeColumnIndex(true);
            viewportLayer = new GridViewPortLayer(this.treeLayer);

            setUnderlyingLayer(viewportLayer);
        }

        private GridViewPortLayer getViewportLayer() {
            return this.viewportLayer;
        }

        private SelectionLayer getSelectionLayer() {
            return this.selectionLayer;
        }

        private TreeLayer getTreeLayer() {
            return this.treeLayer;
        }

        private IDataProvider getBodyDataProvider() {
            return this.bodyDataProvider;
        }

        private ColumnReorderLayer getReorderLayer() {
            return reorderLayer;
        }

        @SuppressWarnings("unchecked")
        private EventList<IDSGridDataRow> getEventList() {
            return (EventList<IDSGridDataRow>) eventList;
        }

        /**
         * Gets the filter list.
         *
         * @return the filter list
         */
        @SuppressWarnings("unchecked")
        public FilterList<IDSGridDataRow> getFilterList() {
            return (FilterList<IDSGridDataRow>) new FilterList<T>(rowObjectsGlazedList);
        }
    }

    /**
     * Input changed.
     *
     * @param newDataProvider the new data provider
     * @param preserveScrollPos the preserve scroll pos
     */
    public void inputChanged(IDSGridDataProvider newDataProvider, boolean preserveScrollPos) {
        saveReorderState();
        super.setDataProvider(newDataProvider);
        setModelData(((ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat) newDataProvider).getNodes());

        createAllLayerDataProviders();

        gridLayer.addConfiguration(new DSGridLayerConfiguration(gridLayer));
        dataGrid.setLayer(gridLayer);

        configureGridStyle();

        enableHeaderToolTip();

        dataGrid.configure();
        refresh();
        GridDataFactory.fillDefaults().grab(true, true).applyTo(dataGrid);

        restoreReorderState();

        this.eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, this.dataProvider));
        this.eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_CHANGED, this.dataProvider));

    }

    /**
     * Refresh.
     */
    @Override
    public void refresh() {
        this.dataGrid.refresh(true);
    }

    /**
     * Creates the component.
     *
     * @param gridAreaComposite the grid area composite
     */
    @Override
    public void createComponent(Composite gridAreaComposite) {
        this.shell = gridAreaComposite;
        setDataProvider(getDataProvider());

        createAllLayerDataProviders();

        dataGrid = new DSNatTable(this.shell, gridLayer, false);

        GridDataFactory.fillDefaults().grab(true, true).applyTo(dataGrid);
        // create a new ConfigRegistry which will be needed for GlazedLists
        // handling
        dataGrid.setConfigRegistry(getConfigRegistry());

        configureGridStyle();

        enableHeaderToolTip();

        gridLayer.addConfiguration(new DSGridLayerConfiguration(gridLayer));
        dataGrid.configure();
        this.dataGrid.addOverlayPainter(new NatTableBorderOverlayPainter());

        registerDisposedListener();
    }

    /**
     * Configure grid style.
     */
    @Override
    protected void configureGridStyle() {
        this.dataGrid.addConfiguration(new TableGridStyleConfiguration(getRegexMarkup(), uiPref, dataContext));

        this.planNodeStyleConfig = new ExecutionPlanNodeTypeConfiguration(dataProvider);
        dataGrid.addConfiguration(planNodeStyleConfig);

        dataGrid.addConfiguration(new TreeLayerExpandCollapseKeyBindings(bodyLayerStack.getTreeLayer(),
                bodyLayerStack.getSelectionLayer()));
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class TreeGridColumnAccumulator.
     */
    @SuppressWarnings("rawtypes")
    public static class TreeGridColumnAccumulator extends ColumnLabelAccumulator {

        /**
         * The list data.
         */
        ListDataProvider listData;

        /**
         * The column prop accessor.
         */
        ReflectiveColumnPropertyAccessor columnPropAccessor;

        /**
         * Instantiates a new tree grid column accumulator.
         *
         * @param bodyDataProvider the body data provider
         * @param columnPropertyAccessor the column property accessor
         */
        public TreeGridColumnAccumulator(IDataProvider bodyDataProvider,
                ReflectiveColumnPropertyAccessor columnPropertyAccessor) {
            this.listData = (ListDataProvider) bodyDataProvider;
            this.columnPropAccessor = columnPropertyAccessor;
        }

        /**
         * Accumulate config labels.
         *
         * @param configLabels the config labels
         * @param columnPosition the column position
         * @param rowPosition the row position
         */
        @Override
        public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
            super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);
            try {
                addPlanAnalysisLabelOnCells(configLabels, columnPosition, rowPosition);
            } catch (MPPDBIDEException exception) {
                MPPDBIDELoggerUtility.error("error while assigning column labels", exception);
            }
        }

        private void addPlanAnalysisLabelOnCells(LabelStack configLabels, int columnPosition, int rowPosition)
                throws MPPDBIDEException {

            ExplainAnalyzePlanNodeTreeDisplayData rowData = (ExplainAnalyzePlanNodeTreeDisplayData) this.listData
                    .getRowObject(rowPosition);

            if (rowData.isHeaviest()) {
                if (columnPropAccessor.getColumnProperty(columnPosition)
                        .equals(TreeGridColumnHeader.PROP_ACTUAL_ROWS)) {
                    configLabels.addLabel(TreeGridColumnHeader.COLUMN_LABEL_HEAVIEST);
                }
            }
            if (rowData.isCostliest()) {
                if (columnPropAccessor.getColumnProperty(columnPosition).equals(TreeGridColumnHeader.PROP_TOTAL_COST)) {
                    configLabels.addLabel(TreeGridColumnHeader.COLUMN_LABEL_COSTLIEST);
                }
            }
            if (rowData.isSlowest()) {
                if (columnPropAccessor.getColumnProperty(columnPosition)
                        .equals(TreeGridColumnHeader.PROP_ACTUAL_TOTAL_TIME)) {
                    configLabels.addLabel(TreeGridColumnHeader.COLUMN_LABEL_SLOWEST);
                }
            }
        }
    }
}
