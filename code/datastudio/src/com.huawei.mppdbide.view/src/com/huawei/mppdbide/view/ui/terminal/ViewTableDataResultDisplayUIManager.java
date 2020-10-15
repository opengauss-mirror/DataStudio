/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.terminal;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Observer;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.GridUIPreference;
import com.huawei.mppdbide.view.component.IGridUIPreference;
import com.huawei.mppdbide.view.component.grid.DSGridComponent;
import com.huawei.mppdbide.view.component.grid.GridSearchAreaToDataGrid;
import com.huawei.mppdbide.view.component.grid.GridSelectionLayerPortData;
import com.huawei.mppdbide.view.component.grid.GridToolbar;
import com.huawei.mppdbide.view.component.grid.GridViewPortData;
import com.huawei.mppdbide.view.component.grid.SEARCHOPTIONS;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindowDummy;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.ViewTableDataExecutionContext;
import com.huawei.mppdbide.view.ui.ViewEditTableDataUIWindow;
import com.huawei.mppdbide.view.ui.terminal.resulttab.GridDataExportAllWorker;
import com.huawei.mppdbide.view.ui.terminal.resulttab.GridDataGenerateInsertAllWorker;
import com.huawei.mppdbide.view.ui.terminal.resulttab.GridResultDataCurrentPageExport;
import com.huawei.mppdbide.view.ui.terminal.resulttab.GridResultDataSelectedCopyToExcel;
import com.huawei.mppdbide.view.ui.terminal.resulttab.GridResultGenerateSelectedLineInsertSql;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.UserPreference;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;
import com.huawei.mppdbide.view.workerjob.UIWorkerJob;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableDataResultDisplayUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewTableDataResultDisplayUIManager extends AbstractResultDisplayUIManager implements IDSListener {
    /**
     *  the IViewTableDataCore
     */
    protected IViewTableDataCore core;
    private IDSGridDataProvider resultsetDisplaydata;
    private IQueryExecutionSummary queryExecSummary;
    private DSGridComponent gridComponent;
    private IGridUIPreference resultGridUIPref;
    private Composite composite;
    private boolean isInitialised;
    private ConsoleMessageWindow consoleMessageWindowDummy;
    private MPart part;

    /**
     * Instantiates a new view table data result display UI manager.
     *
     * @param core1 the core 1
     */
    public ViewTableDataResultDisplayUIManager(IViewTableDataCore core1) {
        super(core1.getTermConnection());
        this.core = core1;
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {

        return null;
    }

    /**
     * Gets the console message window.
     *
     * @param bringOnTop the bring on top
     * @return the console message window
     */
    @Override
    protected ConsoleMessageWindow getConsoleMessageWindow(boolean bringOnTop) {
        if (null == this.consoleMessageWindowDummy) {
            this.consoleMessageWindowDummy = new ConsoleMessageWindowDummy();
        }

        return this.consoleMessageWindowDummy;
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    @Override
    public IEventBroker getEventBroker() {

        return null;
    }

    /**
     * Creates the result new.
     *
     * @param result the result
     * @param consoledata the consoledata
     * @param queryExectnSummary the query exectn summary
     */
    @Override
    protected void createResultNew(IDSGridDataProvider result, IConsoleResult consoledata,
            IQueryExecutionSummary queryExectnSummary) {
        if (this.isDisposed()) {
            return;
        }

        setResultData(result, queryExectnSummary);

        IWindowDetail windowDetails = core.getWindowDetails();
        boolean isViewTableWindowExists = UIElement.getInstance().isWindowExists(windowDetails);
        ViewEditTableDataUIWindow viewTableDataWindow = (ViewEditTableDataUIWindow) UIElement.getInstance()
                .getViewTableDataWindow(windowDetails, this);

        // Check if the window is null. This will be due to the number of
        // windows open is more than the threshold.
        if (viewTableDataWindow == null) {
            return;
        }

        if (null != viewTableDataWindow.getResultDisplayUIManager() && isViewTableWindowExists) {
            // If old manager is being used, then reset the new result and
            // summary.
            viewTableDataWindow.getResultDisplayUIManager().setResultData(result, queryExectnSummary);
            viewTableDataWindow.resetData();
            UIElement.getInstance().bringOnTopViewTableDataWindow(windowDetails);
        }
    }

    /**
     * Sets the result data.
     *
     * @param result the result
     * @param queryExecSummry the query exec summry
     */
    private void setResultData(IDSGridDataProvider result, IQueryExecutionSummary queryExecSummry) {
        this.resultsetDisplaydata = result;
        this.queryExecSummary = queryExecSummry;
    }

    /**
     * Can dislay result.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDislayResult() {
        return !UIElement.getInstance().isWindowLimitReached();
    }

    /**
     * Handle result display failure dialog.
     */
    @Override
    protected void handleResultDisplayFailureDialog() {
        UIElement.getInstance().openMaxSourceViewerDialog();
    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    public void showResult(Composite parentComposite) {
        this.composite = parentComposite;
        this.resultGridUIPref = new ViewTableDataGridUIPref();
        this.gridComponent = new DSGridComponent(resultGridUIPref, resultsetDisplaydata);
        this.gridComponent.createComponents(this.composite);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT,
                this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT,
                this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, this);
        this.setInnitialised(true);
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD: {
                listenOnPostGridDataLoad();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY: {
                listenOnReexecuteQuery();
                break;
            }
            case IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING: {
                listenOnChangeEncoding(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY: {
                listenRefreshQuery();
                break;
            }
            default: {
                handleEventLevelOne(event);
            }
        }
    }
    
    /**
     * handles the EventLevelOne
     * 
     * @param event the event
     */
    private void handleEventLevelOne(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA: {
                listenOnExportCurrentPage(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA: {
                listenOnExportAllData(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM: {
                listenOnViewTableDataWindowCopyToExcelXlsxMenu(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM: {
                listenViewTableDataWindowCopyToExcelXlsMenu(event);
                break;
            }
            default: {
                handleEventLevelTwo(event);
            }
        }
    }
    
    /**
     * handles the EventLevelTwo 
     * 
     * @param event the event
     */
    private void handleEventLevelTwo(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT: {
                listenOnViewTableDataWindowGenerateAllInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT: {
                listenOnViewTableDataWindowGenerateSelectLineInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT: {
                listenOnViewTableDataWindowGenerateCurrentPageInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH: {
                listenOnViewTableDataWindowMenuSearch(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS: {
                listenDbConnectDisConnect(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED: {
                listenSearchCleared();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE: {
                listenOnSearchDone();
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Listen on search done.
     */
    private void listenOnSearchDone() {
        if (resultGridUIPref.isRefreshSupported()) {
            gridComponent.getToolbar().enableDisableOnSearchNonEdit(true,
                    core.getTermConnection().getDatabase().isConnected());
        }
    }

    /**
     * Listen search cleared.
     */
    private void listenSearchCleared() {
        if (resultGridUIPref.isRefreshSupported()) {
            gridComponent.getToolbar().enableDisableOnSearchNonEdit(false,
                    core.getTermConnection().getDatabase().isConnected());
        }
    }

    /**
     * Listen db connect dis connect.
     *
     * @param event the event
     */
    private void listenDbConnectDisConnect(DSEvent event) {
        GridToolbar toolbar = (GridToolbar) event.getObject();
        toolbar.setDataProvider(this.resultsetDisplaydata);
        toolbar.handleNonDataEditEvent(core.getTermConnection().getDatabase().isConnected());
    }

    private void listenOnChangeEncoding(DSEvent event) {
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

    private void listenOnReexecuteQuery() {
        this.gridComponent.saveSortState();
        this.gridComponent.saveReorderState();
        ViewTableDataExecutionContext executionCOntext = null;
        try {
            executionCOntext = new ViewTableDataExecutionContext(this.core, this,
                    new ViewOrEditTableDataResultSetConfigData(queryExecSummary.getNumRecordsFetched()));
        } catch (DatabaseOperationException exception) {
            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE));
            return;
        }

        TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(executionCOntext);
        worker.setTaskDB(executionCOntext.getTermConnection().getDatabase());
        worker.schedule();
        gridComponent.getToolbar().resetLoadMoreRecordStatus();
    }

    private void listenOnPostGridDataLoad() {
        if (gridComponent != null) {
            gridComponent.setLoadingStatus(false);
            gridComponent.enableDisableGrid(true);
            gridComponent.restoreLastSortState();
            gridComponent.restoreLastReorderState();
            gridComponent.getToolbar().resetLoadMoreRecordStatus();
        }
    }

    private void listenOnViewTableDataWindowMenuSearch(DSEvent event) {
        Object focusData = event.getObject();
        Object selectSearchData = getSelectData().getCellData();
        if (getSearchArea().getCmbSearchOpt().getText().equals(SEARCHOPTIONS.SRCH_NULL.getDisplayName())) {
            getSearchArea().getTxtSearchStr().setEnabled(false);
            if (null == selectSearchData || selectSearchData.toString().isEmpty()) {
                if (focusData != null) {
                    getSearchArea().getTxtSearchStr().setEnabled(true);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                    getSearchArea().getTxtSearchStr().setText(focusData.toString());
                    getSearchArea().getTriggerSearch(focusData.toString(), true);
                } else {
                    getSearchArea().getTriggerSearch(true);
                }
            } else {
                getSearchArea().getTxtSearchStr().setEnabled(true);
                getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                getSearchArea().getTxtSearchStr().setText(selectSearchData.toString());
                getSearchArea().getTriggerSearch(selectSearchData.toString(), true);

            }
        } else {
            if (null == selectSearchData || selectSearchData.toString().isEmpty()) {
                if (focusData != null) {
                    getSearchArea().getTxtSearchStr().setText(focusData.toString());
                    getSearchArea().getTriggerSearch(focusData.toString(), true);
                } else {
                    getSearchArea().getTxtSearchStr().setText("");
                    getSearchArea().getTxtSearchStr().setEnabled(false);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_NULL.getDisplayName());
                    getSearchArea().getTriggerSearch(true);
                }

            } else {
                getSearchArea().getTxtSearchStr().setText(selectSearchData.toString());
                getSearchArea().getTriggerSearch(selectSearchData.toString(), true);

            }
        }
    }

    /**
     * Gets the search area.
     *
     * @return the search area
     */
    private GridSearchAreaToDataGrid getSearchArea() {
        return this.gridComponent.getSearchArea();
    }

    private void listenOnViewTableDataWindowGenerateCurrentPageInsert(DSEvent event) {
        GridResultGenerateSelectedLineInsertSql generateCurrentPageInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), null, this.resultsetDisplaydata, queryExecSummary,
                core.getWindowTitle(), true);
        generateCurrentPageInsertSql.addObserver((Observer) event.getObject());
        generateCurrentPageInsertSql.generate();
    }

    private void listenOnViewTableDataWindowGenerateSelectLineInsert(DSEvent event) {
        GridResultGenerateSelectedLineInsertSql generateSelectedLineInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), null, this.resultsetDisplaydata, queryExecSummary,
                core.getWindowTitle(), false);
        generateSelectedLineInsertSql.addObserver((Observer) event.getObject());
        generateSelectedLineInsertSql.generate();
    }

    private void listenOnViewTableDataWindowGenerateAllInsert(DSEvent event) {
        String progressLabel = MessageConfigLoader
                .getProperty(IMessagesConstants.GENERATE_INSERT_ALL_RESULTDATA_PROGRESS_NAME, core.getWindowTitle());
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataGenerateInsertAllWorker worker = new GridDataGenerateInsertAllWorker(this.termConnection, null,
                core.getWindowTitle(), progressLabel, this.resultsetDisplaydata, (Observer) event.getObject(), false,
                bottomStatusBar, statusMessage, getFileEncoding());
        StatusMessageList.getInstance().push(statusMessage);
        if (worker.isSaveSqlFileDialog()) {
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            worker.setTaskDB(termConnection.getDatabase());
            worker.schedule();
        }
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    private String getFileEncoding() {
        String encoding = UserPreference.getInstance().getFileEncoding().isEmpty() ? Charset.defaultCharset().name()
                : UserPreference.getInstance().getFileEncoding();

        return encoding;
    }

    private void listenViewTableDataWindowCopyToExcelXlsMenu(DSEvent event) {
        int formatIndex = 1;
        GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                formatIndex, null, null);
        resultCopyTOExcel.addObserver((Observer) event.getObject());
        resultCopyTOExcel.export();
    }

    private void listenOnViewTableDataWindowCopyToExcelXlsxMenu(DSEvent event) {
        int formatIndex = 0;
        GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                formatIndex, null, null);
        resultCopyTOExcel.addObserver((Observer) event.getObject());
        resultCopyTOExcel.export();
    }

    /**
     * Gets the select data.
     *
     * @return the select data
     */
    private GridSelectionLayerPortData getSelectData() {
        return this.gridComponent.getSelectDataIterator();
    }

    private void listenOnExportAllData(DSEvent event) {
        if (!isTableExist()) {
            return;
        }
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_PROGRESS_NAME,
                core.getWindowTitle());
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataExportAllWorker worker = new GridDataExportAllWorker(this.termConnection, null, core.getWindowTitle(),
                progressLabel, this.resultsetDisplaydata, (Observer) event.getObject(), false, bottomStatusBar,
                statusMessage);
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
        GridResultDataCurrentPageExport resultDataExporter = new GridResultDataCurrentPageExport(getUIData(), null,
                null, core.getWindowTitle());
        resultDataExporter.addObserver((Observer) event.getObject());
        resultDataExporter.export(false);
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
     * Listen refresh query.
     */
    private void listenRefreshQuery() {
        if (!isTableExist()) {
            return;
        }
        gridComponent.setLoadingStatus(true);
        gridComponent.enableDisableGrid(false);
        this.gridComponent.saveSortState();
        this.gridComponent.saveReorderState();
        RefreshViewTableDataWorker refreshmetadataworker = new RefreshViewTableDataWorker(core);
        refreshmetadataworker.schedule();
    }

    /**
     * Checks if is table exist.checks if the table being accessed is not
     * dropped from backend
     *
     * @return true, if is table exist
     */
    private boolean isTableExist() {
        if (core.isTableDropped()) {

            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABLE_DATA_ERROR_POPUP_HEADER),
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABLE_DATA_DROPPED_REFRESH_ERROR),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
            return false;
        } else {
            return true;
        }
    }

    /**
     * Handle exception display.
     *
     * @param object the object
     */
    @Override
    public void handleExceptionDisplay(Object object) {
        if (isDisposed()) {
            return;
        }
        if (isInnitialised()) {
            gridComponent.dataLoadError();
        }
        MPPDBIDEException exception = (MPPDBIDEException) object;
        StringBuilder customErrorMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String strServerError = "";
        String msgString = MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE);

        String hintMsg = "";
        if (null != exception) {
            if (exception.getServerMessage() != null && exception.getServerMessage().contains("does not exist")) {
                customErrorMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_VIEW_TABLE_FAILURE));
            } else {
                customErrorMsg.append(exception.getServerMessage());
            }

            strServerError = customErrorMsg.toString();

            if (strServerError.contains("Position:")) {
                strServerError = strServerError.split("Position:")[0];
            }

            if (exception.getDBErrorMessage().contains("No matching")) {
                hintMsg = MessageConfigLoader.getProperty(IMessagesConstants.CHECK_FILE_ENCODE_SET);
            }
        }

        if (!strServerError
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED))) {
            MPPDBIDEDialogs.generateErrorPopup(
                    MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                    Display.getDefault().getActiveShell(), exception, hintMsg, msgString, strServerError);
        }
        String selectedObjectName = getSelectedObjectName();
        String schemaName = null;
        if (core.getServerObject().getNamespace() != null) {
            schemaName = core.getServerObject().getNamespace().getName();
        }
        ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getError(MessageConfigLoader
                .getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE_VIEW, schemaName, selectedObjectName)));    
    }

    /**
     * Gets the selected object name.
     *
     * @return the selected object name
     */
    private String getSelectedObjectName() {
        return core.getServerObject().getName();
    }

    /**
     * On focus.
     */
    public void onFocus() {
        this.gridComponent.focus();
    }

    /**
     * Reset data result.
     */
    public void resetDataResult() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (gridComponent != null && !gridComponent.isDisposed()) {
                    gridComponent.setDataProvider(ViewTableDataResultDisplayUIManager.this.resultsetDisplaydata);
                }
            }
        });

    }

    /**
     * Checks if is innitialised.
     *
     * @return true, if is innitialised
     */
    public boolean isInnitialised() {
        return isInitialised;
    }

    /**
     * Sets the innitialised.
     *
     * @param isInnitialised the new innitialised
     */
    private void setInnitialised(boolean isInnitialised) {
        this.isInitialised = isInnitialised;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewTableDataGridUIPref.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    public class ViewTableDataGridUIPref extends GridUIPreference {

        @Override
        public boolean isEnableSort() {
            return true;
        }

        @Override
        public boolean isRefreshSupported() {
            return true;
        }

        @Override
        public boolean isShowLoadMoreRecordButton() {
            return true;
        }

        /**
         * Checks if is closed connection.
         *
         * @return true, if is closed connection
         */
        @Override
        public boolean isClosedConnection() {
            return core.getTermConnection().getDatabase().isConnected();
        }

        @Override
        public boolean isShowRightClickMenu() {
            return true;
        }

        /**
         * Checks if is show generate insert.
         *
         * @return true, if is show generate insert
         */
        @Override
        public boolean isShowGenerateInsert() {
            Boolean isOlapDB = true;
            switch (termConnection.getDatabase().getDBType()) {
                case OPENGAUSS: {
                    isOlapDB = true;
                    break;
                }
                default: {
                    break;
                }
            }
            return isOlapDB;
        }

        @Override
        public boolean isStartSelectQuery() {
            return true;
        }
    }

    /**
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {
        return;
    }

    /**
     * Sets the disposed.
     */
    @Override
    public void setDisposed() {
        super.setDisposed();
        gridComponent.onPreDestroy();

        core = null;
        resultsetDisplaydata = null;
        queryExecSummary = null;
        gridComponent = null;
        resultGridUIPref = null;
        composite.dispose();
        composite = null;
    }

    /**
     * Gets the single query array.
     *
     * @param queryArray the query array
     * @param query the query
     * @return the single query array
     */
    @Override
    public void getSingleQueryArray(ArrayList<String> queryArray, String query) {
        return;
    }

    /**
     * Sets the cursor offset.
     *
     * @param offset the new cursor offset
     */
    @Override
    public void setCursorOffset(int offset) {
        return;
    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {
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
     * Description: The Class RefreshViewTableDataWorker.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author g00408002
     * @version [DataStudio 8.0.2, 06 Feb, 2020]
     * @since 06 Feb, 2020
     */
    private class RefreshViewTableDataWorker extends UIWorkerJob {

        private IViewTableDataCore core;
        private DBConnection conn;

        /**
         * Instantiates a new refresh view table meta data worker.
         *
         * @param core the core
         */
        private RefreshViewTableDataWorker(IViewTableDataCore core) {
            super("Refresh View Core", null);
            this.core = core;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            // oid Before refresh to check availability in cache
            conn = core.getTermConnection().getDatabase().getConnectionManager().getObjBrowserConn();
            core.refreshTable(conn);
            if (core.isTableDropped()) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED);
            }
            return null;
        }

        @Override
        public void onSuccessUIAction(Object obj) {
            checkWindowDetails();
            if (isDisposed()) {
                return;
            }
            ViewTableDataResultDisplayUIManager.this.setCore(core);
            ViewTableDataExecutionContext context = null;
            try {
                context = new ViewTableDataExecutionContext(core, ViewTableDataResultDisplayUIManager.this);
            } catch (DatabaseOperationException exception) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_ERROR),
                        MessageConfigLoader.getProperty(IMessagesConstants.VIEW_TABALE_DATA_UNABLE));
                return;
            }
            TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(context);
            worker.setTaskDB(core.getTermConnection().getDatabase());
            worker.schedule();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
            handleExceptionDisplay(exception);
            MPPDBIDELoggerUtility.error("Refresh edit table metadata failed.", exception);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
            handleExceptionDisplay(exception);
            if (exception.getMessage().equalsIgnoreCase(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED))) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_HEADER),
                        MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_DROPPED_REFRESH_ERROR),
                        new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
            }
            MPPDBIDELoggerUtility.error("Refresh edit table metadata failed.", exception);
        }

        @Override
        public void finalCleanup() throws MPPDBIDEException {
            MPPDBIDELoggerUtility.info("Table metaData refreshed for Edit Table Wndow");
        }

        @Override
        public void finalCleanupUI() {
            MPPDBIDELoggerUtility.info("Table metaData refreshed for Edit Table Wndow");
        }

    }

    /**
     * Check window details.
     */
    private void checkWindowDetails() {
        MPart oldMPart = this.getPart();
        String oldLabel = oldMPart.getLabel();
        String oldID = oldMPart.getElementId();
        String newLabel = core.getWindowDetails().getTitle();
        String newID = core.getWindowDetails().getUniqueID();
        if (!oldLabel.equals(newLabel)) {
            oldMPart.setLabel(newLabel);
        }
        if (null != oldID && !oldID.equals(newID)) {
            oldMPart.setElementId(newID);

        }
    }

    public void setCore(IViewTableDataCore core) {
        this.core = core;
    }

    public void setPart(MPart part) {
        this.part = part;
    }

    private MPart getPart() {
        return this.part;
    }

}
