/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.nio.charset.Charset;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Observer;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.JSQLParserUtils;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.GridUIPreference;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.CommitInputData;
import com.huawei.mppdbide.view.component.grid.CommitRecordEventData;
import com.huawei.mppdbide.view.component.grid.DSGridComponent;
import com.huawei.mppdbide.view.component.grid.GridSearchAreaToDataGrid;
import com.huawei.mppdbide.view.component.grid.GridSelectionLayerPortData;
import com.huawei.mppdbide.view.component.grid.GridToolbar;
import com.huawei.mppdbide.view.component.grid.GridViewPortData;
import com.huawei.mppdbide.view.component.grid.SEARCHOPTIONS;
import com.huawei.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager.RowEffectedConfirmation;
import com.huawei.mppdbide.view.core.edittabledata.EditTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.core.edittabledata.EditTableUIWorker;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.ui.ResultSetWindow;
import com.huawei.mppdbide.view.ui.connectiondialog.SaveChangesNotificationDialog;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultTab.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ResultTab extends CTabItem implements IDSListener, IResultTab {

    /**
     * The grid component.
     */
    protected DSGridComponent gridComponent;
    private IGridUIPreference resultGridUIPref;

    /**
     * The parent tabmanager.
     */
    protected ResultTabManager parentTabmanager;
    private IQueryExecutionSummary resultSummary;

    /**
     * The console display data.
     */
    protected IConsoleResult consoleDisplayData;

    /**
     * The composite.
     */
    protected Composite composite;

    /**
     * The is disposed.
     */
    protected boolean isDisposed;
    private boolean isTabEditable = false;
    private boolean isDirty = false;
    private boolean isSQLTermContext;
    private String tableName = null;

    /**
     * The term connection.
     */
    protected TerminalExecutionConnectionInfra termConnection;
    private CommitInputData rememberedUserOptions;
    private IDSGridDataProvider result;
    private RowEffectedConfirmation rowEffectedConfirm;
    private int formatIndex;
    private boolean generateCurrentSql = false;

    /**
     * Sets the grid component.
     *
     * @param pref the pref
     * @param resultsetDisplaydata the resultset displaydata
     */
    protected void setGridComponent(IGridUIPreference pref, IDSGridDataProvider resultsetDisplaydata) {

        /*
         * if grid component and grid preferences are not set in constructor,
         * this function is called to set
         */

        this.resultGridUIPref = pref;
        this.gridComponent = new DSGridComponent(resultGridUIPref, resultsetDisplaydata);
    }

    /**
     * Instantiates a new result tab.
     *
     * @param parentUI the parent UI
     * @param style the style
     * @param composite the composite
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoleDisplayData the console display data
     * @param resultSummary the result summary
     * @param parent the parent
     * @param termConnection the term connection
     */
    public ResultTab(CTabFolder parentUI, int style, Composite composite, IDSGridDataProvider resultsetDisplaydata,
            IConsoleResult consoleDisplayData, IQueryExecutionSummary resultSummary, ResultTabManager parent,
            TerminalExecutionConnectionInfra termConnection) {
        /*
         * this constructor is used by ExecutionPlanTab
         */
        super(parentUI, style);
        this.termConnection = termConnection;
        this.resultSummary = resultSummary;
        this.consoleDisplayData = consoleDisplayData;
        setControl(composite);
        this.resultGridUIPref = null;
        this.gridComponent = null;
        this.parentTabmanager = parent;
        this.composite = composite;

        this.result = resultsetDisplaydata; // text view
        this.isSQLTermContext = false;
        this.isTabEditable = false;
    }

    /**
     * Instantiates a new result tab.
     *
     * @param parentUI the parent UI
     * @param style the style
     * @param composite the composite
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoleDisplayData the console display data
     * @param resultSummary the result summary
     * @param parent the parent
     * @param termConnection the term connection
     * @param isSQLTermContext the is SQL term context
     * @throws DatabaseCriticalException the database critical exception
     */
    public ResultTab(CTabFolder parentUI, int style, Composite composite, IDSGridDataProvider resultsetDisplaydata,
            IConsoleResult consoleDisplayData, IQueryExecutionSummary resultSummary, ResultTabManager parent,
            TerminalExecutionConnectionInfra termConnection, boolean isSQLTermContext)
            throws DatabaseCriticalException {
        /*
         * This constructor is used for creating query execution result tab
         */
        super(parentUI, style);
        if (isSQLTermContext) {
            this.isTabEditable = JSQLParserUtils.isQueryResultEditSupported(resultSummary.getQuery());
        } else {
            this.isTabEditable = false;
        }
        this.termConnection = termConnection;
        initResultSetTableName(resultsetDisplaydata, resultSummary);
        this.resultSummary = resultSummary;
        this.consoleDisplayData = consoleDisplayData;
        setControl(composite);
        this.resultGridUIPref = new EditQueryResultsGridUIPref();
        this.gridComponent = new DSGridComponent(resultGridUIPref, resultsetDisplaydata);
        this.parentTabmanager = parent;
        this.composite = composite;
        this.result = resultsetDisplaydata;
        this.isSQLTermContext = isSQLTermContext;
    }

    private void initResultSetTableName(IDSGridDataProvider resultsetDisplaydata, IQueryExecutionSummary summary) {
        DSResultSetGridDataProvider gdp = (DSResultSetGridDataProvider) resultsetDisplaydata;
        ResultSetMetaData rsmd = null;

        try {
            ResultSet rs = gdp.getQueryResults().getResultsSet();
            if (rs != null) {
                rsmd = rs.getMetaData();
            }
            this.tableName = JSQLParserUtils.getQualifiedTableName(summary.getQuery());
            if ((this.tableName == null || "".equals(this.tableName)) && null != rsmd && rsmd.getColumnCount() > 0) {
                this.tableName = rsmd.getTableName(1);
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("ResultTab: SQLException occurred.", exception);
        }
    }

    /**
     * Inits the.
     */
    public void init() {
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA_COMPLETE, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_USER_FORGET_OPTION, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_GRID_CREATION, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH, this);
        // copy to excel
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT,
                this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT,
                this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT, this);
        this.gridComponent.createComponents(this.composite);

        if (this.result != null && this.result instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider editDataProvider = (DSResultSetGridDataProvider) this.result;
            editDataProvider.setDatabase(termConnection.getDatabase());
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    gridComponent.getDataEditListener());
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, this);
        }
    }

    private void updateResultTabDirtyState() {
        this.result.setResultTabDirtyFlag(this.isDirty);
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (null != gridComponent) {
                    gridComponent.getToolbar().enableDisableExportAllButton();
                }
                if (!isDisposed()) {
                    if (isDirty) {
                        if (!getText().startsWith("*")) {
                            setText("*" + getText());
                            parentTabmanager.modifyDirtyTabCount(true);
                        }
                    } else {
                        if (getText().startsWith("*")) {
                            setText(getText().substring(1));
                            parentTabmanager.modifyDirtyTabCount(false);
                        }
                    }
                }
            }
        });
    }

    /**
     * Handle focus.
     */
    public void handleFocus() {
        this.gridComponent.focus();
    }

    /**
     * Sets the dispose.
     */
    public void setDispose() {
        this.isDisposed = true;
    }

    /**
     * Checks if is result tab dirty.
     *
     * @return true, if is result tab dirty
     */
    public boolean isResultTabDirty() {
        return this.isDirty;
    }

    /**
     * Gets the result tab name.
     *
     * @return the result tab name
     */
    protected String getResultTabName() {
        return getText().startsWith("*") ? getText().substring(1) : getText();
    }

    private void reloadResultTabData(boolean isRefresh) {
        ResultTabResultDisplayUIManager rTabUIManager = new ResultTabResultDisplayUIManager(this.parentTabmanager,
                this);
        String label = this.parentTabmanager.getmPartLabel() == null ? this.parentTabmanager.getPartID()
                : this.parentTabmanager.getmPartLabel();
        String msgParam = getResultTabName() + '.' + label;
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.REEXECUTE_QUERY_PROGRESS_NAME,
                msgParam);
        gridComponent.setLoadingStatus(true);
        gridComponent.enableDisableGrid(false);
        if (isRefresh) {
            this.getResultSummary().setNumRecordsFetched(0);
        }
        ResultTabQueryExecuteContext rtabExecutionContext = new ResultTabQueryExecuteContext(progressLabel, this,
                rTabUIManager, this.termConnection);
        TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(rtabExecutionContext);
        worker.setTaskDB(termConnection.getDatabase());
        SQLTerminal terminal = UIElement.getInstance().getSqlTerminalModel();
        worker.setTerminal(terminal);
        worker.schedule();
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        handleResultEvents(event);
        handleToolbarEvents(event);
        handleEditResultEvents(event);
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM: {
                listenOnResultWindowCopyToExcelXlsxMenu(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM: {
                listenResultWindowCopyToExcelXlsMenu(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT: {
                listenOnResultWindowGenerateAllInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT: {
                listenResultWindowGenerateSelectLineInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT: {
                listenResultWindowGenerateCurrentPageInsert(event);
                break;
            }

            default: {
                break;
            }
        }
    }

    private void handleEditResultEvents(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS: {
                listenDbConnectDisconnectStatus(event);
                break;
            }

            case IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA_COMPLETE: {
                this.gridComponent.restoreLastReorderState();
                this.isDirty = false;
                updateResultTabDirtyState();
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE: {
                gridComponent.restoreLastReorderState();
                gridComponent.enableDisableGrid(true);
                gridComponent.getToolbar().handleDataEditEvent(true);
                listenOnGridDataEdited();
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_USER_FORGET_OPTION: {
                this.rememberedUserOptions = null;
                this.rowEffectedConfirm = null;
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA: {
                this.gridComponent.saveReorderState();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_GRID_CREATION: {
                listenOnGridCreation(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA: {
                listenEdittableCommitData(event);
                break;
            }
            default: {
                break;
            }
        }

    }

    private void handleToolbarEvents(DSEvent event) {
        switch (event.getType()) {

            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA: {
                listenOnExportCurrentPage(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA: {
                listenOnExportAllData(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED: {
                listenSearchCleared();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE: {
                listenSearchDone();
                break;
            }
            case IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING: {
                listenDataChangeEncoding(event);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleResultEvents(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED: {
                listenOnGridDataEdited();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD: {
                listenOnPostGridDataLoad();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY: {
                lisenOnRefreshQuery();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY: {
                listenOnReExecuteQuery();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH: {
                listenOnResultWindowMenuSearch(event);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void listenDataChangeEncoding(DSEvent event) {
        this.gridComponent.saveSortState();
        this.gridComponent.saveReorderState();
        IDSGridDataProvider provider = gridComponent.getDataProvider();
        if (provider != null && provider instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider dp = (DSResultSetGridDataProvider) provider;
            String encoding = event.getObject().toString();
            dp.changeEncoding(encoding);
            gridComponent.setDataProvider(dp);
        }
    }

    private void listenSearchDone() {
        if (this.resultGridUIPref.isEnableEdit()) {
            gridComponent.getToolbar().enableDisableOnSearch(true, termConnection.getDatabase().isConnected());
        }
    }

    private void listenSearchCleared() {
        if (this.resultGridUIPref.isEnableEdit()) {
            gridComponent.getToolbar().enableDisableOnSearch(false, termConnection.getDatabase().isConnected());
        }
    }

    private void listenDbConnectDisconnectStatus(DSEvent event) {
        GridToolbar toolbar = (GridToolbar) event.getObject();
        toolbar.setDataProvider(this.result);
        if (this.resultGridUIPref.isEnableEdit()) {
            toolbar.handleDataEditEvent(termConnection.getDatabase().isConnected());
        }
    }

    private void listenEdittableCommitData(DSEvent event) {
        if (null == parentTabmanager.getTermConnection().getConnection()) {
            connectionDropedDialogForResultTab();
            return;
        }
        if (!this.termConnection.getAutoCommitFlag() && !this.parentTabmanager.getDoNotShowSaveChangesPopUpFlag()) {
            SaveChangesNotificationDialog saveChangesDialog = new SaveChangesNotificationDialog(
                    Display.getDefault().getActiveShell(),
                    MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_SAVE_CHANGES_TITLE),
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.SQL_AUTOCOMMIT_SAVE_CHANGES_MSG),
                    MessageDialog.WARNING, null, 1);
            saveChangesDialog.open();
            this.parentTabmanager.setDoNotShowSaveChangesPopUpFlag(saveChangesDialog.isDontShowAgain());
        }
        if (rowEffectedConfirm == null) {
            rowEffectedConfirm = new RowEffectedConfirmation();
        }

        IDSEditGridDataProvider dataProvider = (IDSEditGridDataProvider) this.result;

        CommitInputData saveUserOptions = getSaveConditionalInput(dataProvider);

        if (null == saveUserOptions) {
            return;
        }
        gridComponent.saveReorderState();
        gridComponent.enableDisableGrid(false);
        gridComponent.getToolbar().handleDataEditEvent(false);
        CommitRecordEventData commitEventData = (CommitRecordEventData) event.getObject();

        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.COMMITING_DATA));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        commitEventData.setCommitData(saveUserOptions);
        if (null != bottomStatusBar) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }

        String progressLabel = null;
        progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(this.tableName, "",
                termConnection.getDatabase().getDbName(), termConnection.getDatabase().getServerName(),
                IMessagesConstants.EDIT_TABLE_COMMIT_PROGRESS_NAME);
        EditTableUIWorker editTableUIWorker = new EditTableUIWorker(progressLabel, "", termConnection,
                commitEventData.getDataGrid(), commitEventData.getDataText(), this.result,
                commitEventData.getCommitData().getUniqueKeys(), commitEventData.getCommitData().isAtomic(),
                commitEventData.getEventTable(), bottomStatusBar, statusMessage, rowEffectedConfirm, true);
        StatusMessageList.getInstance().push(statusMessage);
        if (null != bottomStatusBar) {
            bottomStatusBar.activateStatusbar();
        }
        SQLTerminal terminal = UIElement.getInstance().getSqlTerminalModel();
        editTableUIWorker.setTerminal(terminal);
        editTableUIWorker.schedule();
    }

    private void listenOnGridCreation(DSEvent event) {
        if (event.getObject() instanceof GridToolbar) {
            GridToolbar toolbar = (GridToolbar) event.getObject();
            toolbar.setResultTabDB(termConnection.getDatabase());
            toolbar.enableDisableExportAllButton();
        }
    }

    private void listenResultWindowGenerateCurrentPageInsert(DSEvent event) {
        generateCurrentSql = true;
        GridResultGenerateSelectedLineInsertSql generateCurrentPageInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), this.parentTabmanager.getConsoleMessageWindow(false), this.result,
                getResultSummary(), getResultTabName(), generateCurrentSql);
        generateCurrentPageInsertSql.addObserver((Observer) event.getObject());
        if (this.isDirty) {
            if (!isExportDirtyData()) {
                generateCurrentPageInsertSql.endOfGenerate();
                return;
            }
        }
        generateCurrentPageInsertSql.generate();
        generateCurrentSql = false;
    }

    private void listenResultWindowGenerateSelectLineInsert(DSEvent event) {
        GridResultGenerateSelectedLineInsertSql generateSelectedLineInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), this.parentTabmanager.getConsoleMessageWindow(false), this.result,
                getResultSummary(), getResultTabName(), generateCurrentSql);
        generateSelectedLineInsertSql.addObserver((Observer) event.getObject());
        if (this.isDirty) {
            if (!isExportDirtyData()) {
                generateSelectedLineInsertSql.endOfGenerate();
                return;
            }
        }
        generateSelectedLineInsertSql.generate();
    }

    private void listenOnResultWindowGenerateAllInsert(DSEvent event) {
        String label = this.parentTabmanager.getmPartLabel() == null ? this.parentTabmanager.getPartID()
                : this.parentTabmanager.getmPartLabel();
        String msgParam = getResultTabName() + '.' + label;
        String progressLabel = MessageConfigLoader
                .getProperty(IMessagesConstants.GENERATE_INSERT_ALL_RESULTDATA_PROGRESS_NAME, msgParam);
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataGenerateInsertAllWorker worker = new GridDataGenerateInsertAllWorker(this.termConnection,
                this.parentTabmanager.getConsoleMessageWindow(false), getResultTabName(), progressLabel, this.result,
                (Observer) event.getObject(), isSQLTermContext, bottomStatusBar, statusMessage, getFileEncoding());
        StatusMessageList.getInstance().push(statusMessage);
        if (worker.isSaveSqlFileDialog()) {
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            worker.setTaskDB(termConnection.getDatabase());
            worker.schedule();
        }
    }

    private void listenResultWindowCopyToExcelXlsMenu(DSEvent event) {
        formatIndex = 1;
        GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                formatIndex, this.parentTabmanager.getConsoleMessageWindow(false), getResultSummary());

        resultCopyTOExcel.addObserver((Observer) event.getObject());

        if (this.isDirty) {
            if (!isExportDirtyData()) {
                resultCopyTOExcel.endOfCopyToEXCEL();
                return;
            }
        }

        resultCopyTOExcel.export();
    }

    private void listenOnResultWindowCopyToExcelXlsxMenu(DSEvent event) {
        formatIndex = 0;
        GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                formatIndex, this.parentTabmanager.getConsoleMessageWindow(false), getResultSummary());

        resultCopyTOExcel.addObserver((Observer) event.getObject());

        if (this.isDirty) {
            if (!isExportDirtyData()) {
                resultCopyTOExcel.endOfCopyToEXCEL();
                return;
            }
        }

        resultCopyTOExcel.export();
    }

    private void listenOnExportAllData(DSEvent event) {
        if (null == parentTabmanager.getTermConnection().getConnection() && isSQLTermContext) {
            connectionDropedDialogForResultTab();
            return;
        }
        String label = this.parentTabmanager.getmPartLabel() == null ? this.parentTabmanager.getPartID()
                : this.parentTabmanager.getmPartLabel();
        String msgParam = getResultTabName() + '.' + label;
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_RESULTDATA_PROGRESS_NAME,
                msgParam);
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataExportAllWorker worker = new GridDataExportAllWorker(this.termConnection,
                this.parentTabmanager.getConsoleMessageWindow(false), getResultTabName(), progressLabel, this.result,
                (Observer) event.getObject(), isSQLTermContext, bottomStatusBar, statusMessage);
        StatusMessageList.getInstance().push(statusMessage);
        if (worker.isExportDialog()) {
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            worker.setTaskDB(termConnection.getDatabase());
            worker.schedule();
        }
    }

    private void listenOnExportCurrentPage(DSEvent event) {
        GridResultDataCurrentPageExport resultDataExporter = new GridResultDataCurrentPageExport(getUIData(),
                this.parentTabmanager.getConsoleMessageWindow(false), getResultSummary(), getResultTabName());
        resultDataExporter.addObserver((Observer) event.getObject());

        if (this.isDirty) {
            if (!isExportDirtyData()) {
                resultDataExporter.endOfExport();
                return;
            }
        }

        resultDataExporter.export(false);
    }

    private void listenOnResultWindowMenuSearch(DSEvent event) {
        Object resultTabFocusData = event.getObject();
        Object selectSearchTabData = getSelectData().getCellData();
        if (getSearchArea().getCmbSearchOpt().getText().equals(SEARCHOPTIONS.SRCH_NULL.getDisplayName())) {
            getSearchArea().getTxtSearchStr().setEnabled(false);
            if (null == selectSearchTabData || selectSearchTabData.toString().isEmpty()) {
                if (resultTabFocusData != null) {
                    getSearchArea().getTxtSearchStr().setEnabled(true);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                    getSearchArea().getTxtSearchStr().setText(resultTabFocusData.toString());
                    getSearchArea().getTriggerSearch(resultTabFocusData.toString(), true);
                } else {
                    getSearchArea().getTriggerSearch(true);
                }
            } else {
                getSearchArea().getTxtSearchStr().setEnabled(true);
                getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                getSearchArea().getTxtSearchStr().setText(selectSearchTabData.toString());
                getSearchArea().getTriggerSearch(selectSearchTabData.toString(), true);

            }
        } else {
            if (null == selectSearchTabData || selectSearchTabData.toString().isEmpty()) {
                if (resultTabFocusData != null) {
                    getSearchArea().getTxtSearchStr().setText(resultTabFocusData.toString());
                    getSearchArea().getTriggerSearch(resultTabFocusData.toString(), true);
                } else {
                    getSearchArea().getTxtSearchStr().setText("");
                    getSearchArea().getTxtSearchStr().setEnabled(false);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_NULL.getDisplayName());
                    getSearchArea().getTriggerSearch(true);
                }

            } else {
                getSearchArea().getTxtSearchStr().setText(selectSearchTabData.toString());
                getSearchArea().getTriggerSearch(selectSearchTabData.toString(), true);

            }
        }
    }

    private void listenOnReExecuteQuery() {
        if (null == parentTabmanager.getTermConnection().getConnection()) {
            connectionDropedDialogForResultTab();
            return;
        }
        if (this.isDirty) {
            showPopUpOnDirtyTableFetchNextRecords();
            gridComponent.setLoadingStatus(false);
            gridComponent.enableDisableGrid(true);
            return;
        }
        this.gridComponent.saveSortState();
        this.gridComponent.saveReorderState();
        reloadResultTabData(false);
    }

    private void lisenOnRefreshQuery() {
        if (null == parentTabmanager.getTermConnection().getConnection()) {
            connectionDropedDialogForResultTab();
            return;
        }
        this.gridComponent.saveSortState();
        this.gridComponent.saveReorderState();
        reloadResultTabData(true);
    }

    private void listenOnPostGridDataLoad() {
        IConsoleResult consoleMsgsToShow = this.getConsoleMsgResult();
        parentTabmanager.getConsoleMessageWindow(false).logInfo(consoleMsgsToShow);
        if (gridComponent != null) {
            gridComponent.setLoadingStatus(false);
            gridComponent.enableDisableGrid(true);
            gridComponent.restoreLastSortState();
            gridComponent.restoreLastReorderState();
        }
        this.isDirty = false;
        updateResultTabDirtyState();
    }

    private void listenOnGridDataEdited() {
        IDSEditGridDataProvider provider = (IDSEditGridDataProvider) result;
        this.isDirty = provider.isGridDataEdited();
        updateResultTabDirtyState();
    }

    private void connectionDropedDialogForResultTab() {
        gridComponent.setLoadingStatus(false);
        gridComponent.getToolbar().handleDataEditEvent(false);
        gridComponent.getToolbar().enableDisableExportAllButton(false);
        MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WINDOW_ERROR_POPUP_HEADER),
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_TAB_CONNECTION_LOST_ERR_MSG),
                new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
    }

    private CommitInputData getSaveConditionalInput(IDSEditGridDataProvider dataProvider) {
        if (null != this.rememberedUserOptions) {
            return this.rememberedUserOptions;
        }
        CommitInputData saveUserOptions = null;
        if (termConnection.getDatabase().getDBType() == DBTYPE.OPENGAUSS) {
            saveUserOptions = EditTableDataResultDisplayUIManager.getSaveOptions(dataProvider);
        }
        if (null == saveUserOptions) {
            gridComponent.getToolbar().handleDataEditEvent(true);
            return null;
        } else if (saveUserOptions.getRemember()) {
            this.rememberedUserOptions = saveUserOptions;
            this.gridComponent.savedUserOption();
            // Send event -> remembered user option.
        }
        return saveUserOptions;
    }

    /**
     * Pre destroy.
     *
     * @return true, if successful
     */
    public boolean preDestroy() {
        if (this.isDirty) {
            if (isDiscardChanges()) {
                parentTabmanager.modifyDirtyTabCount(false);
                preDestroyWithoutDirtyCheck();
                return true;
            } else {
                return false;
            }
        } else {
            preDestroyWithoutDirtyCheck();
            return true;
        }
    }

    /**
     * Pre destroy without dirty check.
     */
    public void preDestroyWithoutDirtyCheck() {
        this.isDisposed = true;
        resultGridUIPref = null;
        gridComponent.onPreDestroy();
        gridComponent = null;
        parentTabmanager = null;
        resultSummary = null;
        this.result = null;
        this.consoleDisplayData = null;
    }

    /**
     * Checks if is discard changes.
     *
     * @return true, if is discard changes
     */
    public boolean isDiscardChanges() {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_CHANGES_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DISCARD_TERMINAL_DATA_BODY);
        String cancel = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC);
        String discardChanges = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_DISCARD);
        String discardAll = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_DISCARD_All);
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()), title, message,
                new String[] {discardChanges, discardAll, cancel}, 2);

        if (0 == userChoice) {
            return true;
        }
        if (1 == userChoice) {
            ResultSetWindow.setDiscardAllModified(true);
            return true;
        }
        ResultSetWindow.setCancelForAllModified(true);
        return false;
    }

    /**
     * Show pop up on dirty table fetch next records.
     */
    public void showPopUpOnDirtyTableFetchNextRecords() {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.SAVE_CHANGES_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.SAVE_CHANGES_DATA_BODY);

        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true, title, message);
    }

    /**
     * Checks if is export dirty data.
     *
     * @return true, if is export dirty data
     */
    public boolean isExportDirtyData() {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_CHANGES_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_DIRTY_DATA_BODY);
        String cancel = MessageConfigLoader.getProperty(IMessagesConstants.MPPDBIDE_DIA_BTN_CANC);
        String export = MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_DATA);
        int userChoice = MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true,
                IconUtility.getIconImage(IiconPath.ICO_EDIT_EDIT, this.getClass()), title, message,
                new String[] {export, cancel}, 1);

        if (0 == userChoice) {
            return true;
        }

        return false;
    }

    /**
     * Gets the console msg result.
     *
     * @return the console msg result
     */
    public IConsoleResult getConsoleMsgResult() {
        return this.consoleDisplayData;
    }

    /**
     * Gets the result summary.
     *
     * @return the result summary
     */
    public IQueryExecutionSummary getResultSummary() {
        return resultSummary;
    }

    /**
     * Gets the UI data.
     *
     * @return the UI data
     */
    public GridViewPortData getUIData() {
        return this.gridComponent.getUIDataIterator();
    }

    /**
     * Gets the select data.
     *
     * @return the select data
     */
    public GridSelectionLayerPortData getSelectData() {
        return this.gridComponent.getSelectDataIterator();
    }

    /**
     * Gets the search area.
     *
     * @return the search area
     */
    public GridSearchAreaToDataGrid getSearchArea() {
        return this.gridComponent.getSearchArea();
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    public String getFileEncoding() {
        String encoding = UserPreference.getInstance().getFileEncoding().isEmpty() ? Charset.defaultCharset().name()
                : UserPreference.getInstance().getFileEncoding();

        return encoding;
    }

    /**
     * Sets the result summary.
     *
     * @param resultSummary the new result summary
     */
    protected void setResultSummary(IQueryExecutionSummary resultSummary) {
        this.resultSummary = resultSummary;
    }

    /**
     * Reload data failure handle.
     */
    public void reloadDataFailureHandle() {
        gridComponent.dataLoadError();
    }

    /**
     * Reset data.
     *
     * @param provider the provider
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    public void resetData(final IDSGridDataProvider provider, IConsoleResult consoleData,
            IQueryExecutionSummary queryExecSummary) {
        if (isDisposed) {
            return;
        }

        this.setResultSummary(queryExecSummary);
        this.consoleDisplayData = consoleData;
        this.result = provider;

        if (provider != null && provider instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider editDataProvider = (DSResultSetGridDataProvider) provider;
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    gridComponent.getDataEditListener());
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, this);
        }

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (!gridComponent.isDisposed()) {
                    gridComponent.setDataProvider(provider);
                }
            }
        });

    }

    /**
     * Cancel flow.
     */
    protected void cancelFlow() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (null != gridComponent && !gridComponent.isDisposed()) {
                    gridComponent.setLoadingStatus(false);
                    gridComponent.enableDisableGrid(true);
                }
            }
        });
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EditQueryResultsGridUIPref.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public class EditQueryResultsGridUIPref extends GridUIPreference {

        /**
         * Checks if is enable edit.
         *
         * @return true, if is enable edit
         */
        @Override
        public boolean isEnableEdit() {
            if (isTabEditable) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Checks if is edits the query results flow.
         *
         * @return true, if is edits the query results flow
         */
        @Override
        public boolean isEditQueryResultsFlow() {
            return true;
        }

        /**
         * Checks if is show right click menu.
         *
         * @return true, if is show right click menu
         */
        @Override
        public boolean isShowRightClickMenu() {
            if (isSQLTermContext) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Checks if is show generate insert.
         *
         * @return true, if is show generate insert
         */
        @Override
        public boolean isShowGenerateInsert() {
            Boolean flag = true;
            switch (termConnection.getDatabase().getDBType()) {
                case OPENGAUSS: {
                    flag = true;
                    break;
                }
                default: {
                    break;
                }
            }
            return flag;
        }

        /**
         * Gets the selected encoding.
         *
         * @return the selected encoding
         */
        @Override
        public String getSelectedEncoding() {
            String encoding = gridComponent.getSelectedEncoding();

            if (MPPDBIDEConstants.SPACE_CHAR.equals(encoding) || null == encoding) {
                encoding = PreferenceWrapper.getInstance().getPreferenceStore()
                        .getString(UserEncodingOption.DATA_STUDIO_ENCODING);
            }

            return encoding;

        }

        /**
         * Checks if is start select query.
         *
         * @return true, if is start select query
         */
        @Override
        public boolean isStartSelectQuery() {
            Boolean flag = true;
            tableName = JSQLParserUtils.getSelectQueryMainTableName(resultSummary.getQuery());
            if ("".equals(tableName) || null == tableName) {
                flag = false;
            }
            return flag;
        }

        /**
         * Checks if is closed connection.
         *
         * @return true, if is closed connection
         */
        @Override
        public boolean isClosedConnection() {
            return termConnection.getDatabase().isConnected();
        }

        /**
         * Checks if is unique remembered.
         *
         * @return true, if is unique remembered
         */
        public boolean isUniqueRemembered() {
            return null != ResultTab.this.rememberedUserOptions;
        }

        /**
         * Checks if is enable sort.
         *
         * @return true, if is enable sort
         */
        @Override
        public boolean isEnableSort() {
            return true;
        }

        /**
         * Checks if is need create text mode.
         *
         * @return true, if is need create text mode
         */
        @Override
        public boolean isNeedCreateTextMode() {
            return prefStore.getBoolean(MPPDBIDEConstants.PREF_RESULT_IS_SHOW_TEXTMODE);
        }

        /**
         * Checks if is show load more record button.
         *
         * @return true, if is show load more record button
         */
        @Override
        public boolean isShowLoadMoreRecordButton() {
            return true;
        }

        @Override
        public boolean isRefreshSupported() {
            return isEnableEdit();
        }
    }

    /**
     * Gets the parent tab manager.
     *
     * @return the parent tab manager
     */
    @Override
    public ResultTabManager getParentTabManager() {
        return this.parentTabmanager;
    }
}
