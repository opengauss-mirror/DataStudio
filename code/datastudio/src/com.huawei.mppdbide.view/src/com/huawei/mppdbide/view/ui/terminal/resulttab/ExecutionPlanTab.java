/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.util.ArrayList;
import java.util.List;
import java.util.Observer;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.keywordssyntax.SQLSyntax;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.explainplan.ui.model.ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import com.huawei.mppdbide.presentation.visualexplainplan.UIModelAnalysedPlanNode;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.ExecutionPlanGridComponent;
import com.huawei.mppdbide.view.terminal.ExecutionPlanWorker;

/**
 * Title: PLAN
 * 
 * Description:The Enum PLAN. Explain plan tab, that is positioned same as
 * result tab for a SQL terminal
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author a00415838
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
 */

enum PLAN {
    TEXT, TREE;
}

/**
 * 
 * Title: class
 * 
 * Description: The Class ExecutionPlanTab.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ExecutionPlanTab extends ResultTab {

    // this format will be used for saving
    private ExecutionPlanTextDisplayGrid textView;
    private ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView;
    private PLAN viewType; // indicator of current view
    private IGridUIPreference gridUiPref;
    private boolean flag = false;
    private boolean textViewFlag = false;

    /**
     * 
     * Title: class
     * 
     * Description: The Class ExecutionPlanGridUIPref.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class ExecutionPlanGridUIPref extends EditQueryResultsGridUIPref {
        @Override
        public boolean isEnableSort() {
            return false;
        }

        @Override
        public boolean isAllowColumnReorder() {
            return true;
        }
    }

    /**
     * Pre destroy.
     *
     * @return true, if successful
     */
    @Override
    public boolean preDestroy() {
        this.isDisposed = true;
        if (this.textView != null) {
            this.textView.preDestroy();
        }
        this.textView = null;
        if (this.treeView != null) {
            this.treeView.preDestroy();
        }
        this.treeView = null;
        this.gridUiPref = null;
        this.viewType = null;
        super.preDestroy();
        return true;
    }

    /**
     * Instantiates a new execution plan tab.
     *
     * @param parentUI the parent UI
     * @param style the style
     * @param composite the composite
     * @param planData the plan data
     * @param consoleDisplayData the console display data
     * @param resultSummary the result summary
     * @param parent the parent
     * @param termConnection the term connection
     * @param totalRuntime the total runtime
     */
    public ExecutionPlanTab(CTabFolder parentUI, int style, Composite composite, UIModelAnalysedPlanNode planData,
            IConsoleResult consoleDisplayData, IQueryExecutionSummary resultSummary, ResultTabManager parent,
            TerminalExecutionConnectionInfra termConnection, double totalRuntime) {
        super(parentUI, style, composite, planData.getModelInTextFormat(totalRuntime), consoleDisplayData,
                resultSummary, parent, termConnection);
        this.gridUiPref = new ExecutionPlanGridUIPref();
        this.setResultSummary(resultSummary);

        this.textView = planData.getModelInTextFormat(totalRuntime);
        this.treeView = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(planData);
        this.treeView.setSummary(resultSummary);
        this.textView.setSummary(resultSummary);
        this.viewType = PLAN.TREE;
        setGridComponent(this.gridUiPref, treeView, textView,
                termConnection.getDatabase() == null ? null : termConnection.getDatabase().getSqlSyntax());
    }

    private void setGridComponent(IGridUIPreference gridUiPref2,
            ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView2, ExecutionPlanTextDisplayGrid textView2,
            SQLSyntax sqlSyntax) {
        this.gridComponent = new ExecutionPlanGridComponent(gridUiPref2, treeView2, textView2, sqlSyntax);
    }

    /**
     * Sets the tree view.
     *
     * @param planData the new tree view
     */
    public void setTreeView(UIModelAnalysedPlanNode planData) {
        if (this.treeView != null) {
            this.treeView.preDestroy();
            this.treeView = null;
        }
        this.treeView = new ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat(planData);
    }

    /**
     * Reset data.
     *
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    public void resetData(IConsoleResult consoleData, IQueryExecutionSummary queryExecSummary) {
        if (this.isDisposed) {
            return;
        }
        this.textView.setSummary(queryExecSummary);
        this.treeView.setSummary(queryExecSummary);
        this.consoleDisplayData = consoleData;

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (!gridComponent.isDisposed()) {
                    ExecutionPlanGridComponent gridComp = (ExecutionPlanGridComponent) gridComponent;
                    gridComp.setDataProvider(treeView, textView);
                }
            }
        });
    }

    /**
     * Inits the.
     */
    @Override
    public void init() {
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TREE_VIEW, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TEXT_VIEW, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_REFRESH, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, this);

        this.gridComponent.createComponents(this.composite);
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_REFRESH: {
                refreshPlan(event);
                flag = true;
                break;
            }
            case IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TEXT_VIEW: {
                switchView(PLAN.TEXT);
                textViewFlag = true;
                break;
            }
            case IDSGridUIListenable.LISTEN_EXEC_PLAN_WINDOW_TREE_VIEW: {
                switchView(PLAN.TREE);
                textViewFlag = false;
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED: {
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE: {
                break;
            }
            case IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS: {
                this.gridComponent.getToolbar().enableDisableRefreshPlanButton();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA: {
                listenOnExportExecutionPlan(event);
                break;
            }
            default: {
                super.handleEvent(event);
                break;
            }
        }
    }

    /**
     * Gets the tree view.
     *
     * @return the tree view
     */
    public ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat getTreeView() {
        return treeView;
    }

    /**
     * Sets the tree view.
     *
     * @param treeView2 the new tree view
     */
    public void setTreeView(ExplainAnalyzePlanNodeTreeDisplayDataTreeFormat treeView2) {
        if (this.treeView != null) {
            this.treeView.preDestroy();
        }
        this.treeView = treeView2;
        this.treeView.setSummary(getResultSummary());
    }

    /**
     * Gets the text view.
     *
     * @return the text view
     */
    public IDSGridDataProvider getTextView() {
        return textView;
    }

    /**
     * Sets the text view.
     *
     * @param textView the new text view
     */
    public void setTextView(ExecutionPlanTextDisplayGrid textView) {
        if (this.textView != null) {
            this.textView.preDestroy();
            this.textView = null;
        }
        this.textView = textView;
        this.textView.setSummary(getResultSummary());
    }

    private void setViewType(PLAN type) {
        this.viewType = type;
    }

    private void switchView(PLAN type) {
        if (this.viewType == type) {
            return;
        }
        setViewType(type);
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                ((ExecutionPlanGridComponent) gridComponent).switchExecutionPlanGridData();
            }
        });
    }

    private void listenOnExportExecutionPlan(DSEvent event) {
        GridResultDataCurrentPageExport resultDataExporter;
        if (flag != true) {
            resultDataExporter = new GridResultDataCurrentPageExport(getUIData(), textView, treeView,
                    this.parentTabmanager.getConsoleMessageWindow(false), getResultSummary(), getResultTabName(), true,
                    !textViewFlag);
        } else {
            resultDataExporter = new GridResultDataCurrentPageExport(getUIData(), textView, treeView,
                    this.parentTabmanager.getConsoleMessageWindow(false), this.textView.getSummary(),
                    getResultTabName(), true, !textViewFlag);
        }
        resultDataExporter.addObserver((Observer) event.getObject());
        resultDataExporter.export(false);
    }

    /**
     * Refresh plan.
     *
     * @param event the event
     */
    public void refreshPlan(DSEvent event) {
        ExecutionPlanDisplayUIManager rTabUIManager = new ExecutionPlanDisplayUIManager(this.parentTabmanager, this);
        String label = this.parentTabmanager.getmPartLabel() == null ? this.parentTabmanager.getPartID()
                : this.parentTabmanager.getmPartLabel();
        String msgParam = getResultTabName() + '.' + label;
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.REEXECUTE_QUERY_PROGRESS_NAME,
                msgParam);

        ExecutionPlanQueryExecuteContext rtabExecutionContext = new ExecutionPlanQueryExecuteContext(progressLabel,
                this, rTabUIManager, this.termConnection);
        rtabExecutionContext.setObserver((Observer) event.getObject());
        List<String> queryArray = new ArrayList<String>(1);
        queryArray.add(rtabExecutionContext.getQuery());
        ExecutionPlanWorker worker = new ExecutionPlanWorker(queryArray, rtabExecutionContext);
        worker.setTaskDB(this.termConnection.getDatabase());
        worker.schedule();
    }
}
