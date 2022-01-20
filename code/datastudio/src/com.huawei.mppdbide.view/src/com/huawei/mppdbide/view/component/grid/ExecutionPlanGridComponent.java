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

package com.huawei.mppdbide.view.component.grid;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.TreeDataGrid;

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanGridComponent.
 *
 * @since 3.0.0
 */
public class ExecutionPlanGridComponent extends DSGridComponent {
    private TreeDataGrid treeGrid;
    private DataGrid textGrid;

    // execution plan data providers
    private ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat execPlanTreeData;
    private ExecutionPlanTextDisplayGrid execPlanTextData;

    private Composite textTabComposite;
    private Composite treeTabComposite;

    private StackLayout slayout;

    private GRIDTYPE gridType;
    private Composite gridComposite;
    private Composite toolbarComposite;
    private Composite statusbarComposite;
    private SQLSyntax sqlSyntax;

    /**
     * On pre destroy.
     */
    @Override
    public void onPreDestroy() {
        super.onPreDestroy();

        if (this.treeGrid != null) {
            this.treeGrid.onPreDestroy();
        }
        this.treeGrid = null;
        if (this.textGrid != null) {
            this.textGrid.onPreDestroy();
        }
        this.textGrid = null;

        if (this.execPlanTreeData != null) {
            this.execPlanTreeData.preDestroy();
        }
        this.execPlanTreeData = null;
        if (this.execPlanTextData != null) {
            this.execPlanTextData.preDestroy();
        }
        this.execPlanTextData = null;
        this.slayout = null;
        this.gridType = null;

        if (searchArea != null) {
            searchArea.preDestroy();
        }
        if (this.toolbar != null) {
            this.toolbar.preDestroy();
        }
        this.toolbar = null;
    }

    /**
     * 
     * Title: enum
     * 
     * Description: The Enum GRIDTYPE.
     */
    private enum GRIDTYPE {
        TABLE, TREE
    };

    /**
     * Instantiates a new execution plan grid component.
     *
     * @param gridUiPref2 the grid ui pref 2
     * @param treeView2 the tree view 2
     * @param textView2 the text view 2
     * @param sqlSyntax the sql syntax
     */
    public ExecutionPlanGridComponent(IGridUIPreference gridUiPref2,
            ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView2, ExecutionPlanTextDisplayGrid textView2,
            SQLSyntax sqlSyntax) {
        super(gridUiPref2, treeView2);
        this.uiPref = gridUiPref2;
        this.execPlanTreeData = treeView2;
        this.execPlanTextData = textView2;
        this.textGrid = null;
        this.treeGrid = null;
        this.gridType = GRIDTYPE.TREE;
        this.sqlSyntax = sqlSyntax;
    }

    /**
     * Reset data provider.
     *
     * @param dataProvider the data provider
     */
    public void resetDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    private void showTable(Composite composite) {
        slayout.topControl = composite;
        gridComposite.layout();
    }

    /**
     * Switch execution plan grid data.
     */
    public void switchExecutionPlanGridData() {
        if (gridType == GRIDTYPE.TREE) {
            gridType = GRIDTYPE.TABLE;
            resetDataProvider(this.execPlanTextData);
            this.grid = this.textGrid;
            showTable(textTabComposite);
        } else {
            gridType = GRIDTYPE.TREE;
            resetDataProvider(this.execPlanTreeData);
            this.grid = this.treeGrid;
            showTable(treeTabComposite);
        }
        this.searchArea.setGrid(this.grid);
    }

    /**
     * Sets the data provider.
     *
     * @param treeView the tree view
     * @param textView the text view
     */
    public void setDataProvider(ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView,
            ExecutionPlanTextDisplayGrid textView) {
        IDSGridDataProvider dataProvider = (this.gridType == GRIDTYPE.TREE) ? treeView : textView;
        if (this.execPlanTreeData != null) {
            this.execPlanTreeData.preDestroy();
            this.execPlanTreeData = null;
        }
        this.execPlanTreeData = treeView;
        if (this.execPlanTextData != null) {
            this.execPlanTextData.preDestroy();
            this.execPlanTextData = null;
        }
        this.execPlanTextData = textView;
        super.setDataProvider(dataProvider);
        updateGridStatusBar(this.execPlanTreeData.getSummary());
    }

    /**
     * Creates the data grid area.
     *
     * @param parent the parent
     * @return the data grid
     */
    protected DataGrid createDataGridArea(Composite parent) {
        ExecutionPlanGridComponent.setExecGridLayoutProperties(parent);
        DataGrid dataGrid = null;
        dataGrid = createDataGrid(parent);
        eventTable.sendEvent(new DSEvent(LISTEN_TYPE_POST_GRID_DATA_LOAD, dataProvider));
        return dataGrid;
    }

    /**
     * Sets the exec grid layout properties.
     *
     * @param parent the new exec grid layout properties
     */
    public static void setExecGridLayoutProperties(Composite parent) {
        GridLayout gridlt = new GridLayout();

        gridlt.marginTop = 0;
        gridlt.marginBottom = 0;
        gridlt.marginRight = 0;
        gridlt.marginLeft = 0;
        gridlt.marginHeight = 0;
        gridlt.marginWidth = 0;
        gridlt.verticalSpacing = 0;
        gridlt.horizontalSpacing = 0;
        parent.setLayout(gridlt);
        GridDataFactory.fillDefaults().grab(true, false).applyTo(parent);
    }

    /**
     * Creates the components.
     *
     * @param parent the parent
     */
    public void createComponents(Composite parent) {

        toolbarComposite = createNewComposite(parent);
        gridComposite = createNewComposite(parent);
        textTabComposite = createNewComposite(gridComposite);
        treeTabComposite = createNewComposite(gridComposite);
        statusbarComposite = createNewComposite(parent);

        this.toolbar = createToolBar(toolbarComposite, this.dataProvider);
        this.searchArea = createSearchArea(toolbarComposite);
        this.searchArea.setExecutionPlanTabFlag();
        if (uiPref.isShowQueryArea()) {
            this.queryArea = createQueryArea(toolbarComposite);
            this.queryArea.setExecutionPlanTabFlag();
        }
        this.grid = createDataGridArea(parent);

        if (uiPref.isShowStatusBar()) {
            this.statusBar = createStatusBar(statusbarComposite, this.dataProvider);
            updateGridStatusBar(this.execPlanTreeData.getSummary());
        }

        this.searchArea.setGrid(this.grid);

        ToolItem beginSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
        beginSeparator.setEnabled(true);
        this.toolbar.addItemTreeView(this.eventTable, this.stateMachine);
        this.toolbar.addItemTextView(this.eventTable, this.stateMachine);
        ToolItem endSeparator = new ToolItem(this.toolbar.getToolBar(), SWT.SEPARATOR);
        endSeparator.setEnabled(true);
        this.toolbar.addItemCopy(this.stateMachine, false);
        this.toolbar.setDsExportState(this.stateMachine);
        this.toolbar.addRefreshPlanItem(this.eventTable, this.stateMachine);
        this.toolbar.setDsRefreshState(this.stateMachine);

        if (uiPref.isShowQueryArea()) {
            this.toolbar.addItemToggleQueryArea(this.queryArea);
        }
        this.toolbar.addItemToggleSearchArea(this.searchArea);

        if (this.uiPref.isSupportDataExport()) {
            this.toolbar.addItemExportExecutionPlan(this.eventTable, this.stateMachine);
        }

        ((GridData) toolbarComposite.getLayoutData()).grabExcessVerticalSpace = false;
        ((GridData) gridComposite.getLayoutData()).grabExcessVerticalSpace = true;
        ((GridData) textTabComposite.getLayoutData()).grabExcessVerticalSpace = true;
        ((GridData) treeTabComposite.getLayoutData()).grabExcessVerticalSpace = true;
        ((GridData) statusbarComposite.getLayoutData()).grabExcessVerticalSpace = false;

        this.slayout = new StackLayout();
        gridComposite.setLayout(slayout);

        showTable(treeTabComposite);

        this.grid.setFocus();
    }

    /**
     * Creates the query area.
     *
     * @param gridCmposite the grid cmposite
     * @return the grid query area
     */
    protected GridQueryArea createQueryArea(Composite gridCmposite) {
        GridQueryArea area = new GridQueryArea(this.execPlanTreeData.getSummary().getQuery());
        area.setSQLSyntax(getSqlSyntax());
        area.createComponent(gridCmposite, false);
        return area;
    }

    /**
     * Creates the data grid.
     *
     * @param composite the composite
     * @return the data grid
     */
    protected DataGrid createDataGrid(Composite composite) {
        DataGrid dataGrid;
        /*
         * for execution plan tree view
         */

        this.treeGrid = new TreeDataGrid(this.uiPref,
                (ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat) this.execPlanTreeData, this.eventTable,
                this.stateMachine);
        this.treeGrid.createComponent(treeTabComposite);
        /*
         * and execution plan text view
         */
        this.textGrid = new DataGrid(this.uiPref, this.execPlanTextData, this.eventTable, this.stateMachine);
        this.textGrid.createComponent(textTabComposite);

        dataGrid = this.treeGrid;
        return dataGrid;
    }

    /**
     * Gets the sql syntax.
     *
     * @return the sql syntax
     */
    public SQLSyntax getSqlSyntax() {
        return sqlSyntax;
    }

    /**
     * Sets the SQL syntax.
     *
     * @param sqlSyntax the new SQL syntax
     */
    public void setSQLSyntax(SQLSyntax sqlSyntax) {
        this.sqlSyntax = sqlSyntax;
    }
}
