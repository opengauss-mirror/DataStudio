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

package com.huawei.mppdbide.view.core.edittabledata;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Observer;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IEditTableDataCore;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.edittabledata.DSEditTableDataGridDataProvider;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IRowEffectedConfirmation;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.GridUIPreference;
import com.huawei.mppdbide.view.component.grid.CommitInputData;
import com.huawei.mppdbide.view.component.grid.CommitRecordEventData;
import com.huawei.mppdbide.view.component.grid.DSGridComponent;
import com.huawei.mppdbide.view.component.grid.GridSearchAreaToDataGrid;
import com.huawei.mppdbide.view.component.grid.GridSelectionLayerPortData;
import com.huawei.mppdbide.view.component.grid.GridToolbar;
import com.huawei.mppdbide.view.component.grid.GridViewPortData;
import com.huawei.mppdbide.view.component.grid.RowEffectedConfirmationPrompt;
import com.huawei.mppdbide.view.component.grid.SEARCHOPTIONS;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindowDummy;
import com.huawei.mppdbide.view.prefernces.PreferenceWrapper;
import com.huawei.mppdbide.view.prefernces.UserEncodingOption;
import com.huawei.mppdbide.view.terminal.TerminalQueryExecutionWorker;
import com.huawei.mppdbide.view.terminal.executioncontext.EditTableDataExecutionContext;
import com.huawei.mppdbide.view.ui.EditTableDataUIWindow;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;
import com.huawei.mppdbide.view.ui.terminal.ViewOrEditTableDataResultSetConfigData;
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
 * Description: The Class AbstractEditTableDataResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public abstract class AbstractEditTableDataResultDisplayUIManager extends AbstractResultDisplayUIManager
        implements IDSListener {

    /**
     * The core.
     */
    protected IEditTableDataCore core;

    /**
     * The result.
     */
    protected IDSGridDataProvider result;

    /**
     * The grid component.
     */
    protected volatile DSGridComponent editTableGridComponent;

    /**
     * The result grid UI pref.
     */
    protected EditTableDataGridUIPref resultGridUIPref;

    /**
     * The console message window dummy.
     */
    protected ConsoleMessageWindowDummy consoleMessageWindowDummy;

    /**
     * The dirty handler.
     */
    protected MDirtyable dirtyHandler;

    /**
     * The remembered user options.
     */
    protected CommitInputData rememberedUserOptions;

    /**
     * The part.
     */
    protected MPart part;

    /**
     * The row effected confirm.
     */
    protected RowEffectedConfirmation rowEffectedConfirm;

    /**
     * The query exec summary.
     */
    protected IQueryExecutionSummary queryExecSummary;

    /**
     * Instantiates a new abstract edit table data result display UI manager.
     *
     * @param core1 the core 1
     */
    public AbstractEditTableDataResultDisplayUIManager(IEditTableDataCore core1) {
        super(core1.getTermConnection());
        this.core = core1;
    }

    /**
     * Sets the core.
     *
     * @param newCore the new core
     */
    public void setCore(IEditTableDataCore newCore) {
        this.core = newCore;
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
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {

    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA: {
                this.editTableGridComponent.saveReorderState();
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA_COMPLETE: {
                this.editTableGridComponent.restoreLastReorderState();
                break;
            }
            case IDSGridUIListenable.LISTEN_EDITTABLE_USER_FORGET_OPTION: {
                this.rememberedUserOptions = null;
                this.rowEffectedConfirm = null;
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED: {
                listenSearchCleared();
                break;
            }
            default: {
                handleEventLevelOne(event);
            }
        }

    }

    private void handleReExecuteQuery() {
        this.editTableGridComponent.saveSortState();
        this.editTableGridComponent.saveReorderState();

        EditTableDataExecutionContext executionContext = new EditTableDataExecutionContext(this.core, this,
                this.core.getTable(),
                new ViewOrEditTableDataResultSetConfigData(queryExecSummary.getNumRecordsFetched()));

        TerminalQueryExecutionWorker worker = new TerminalQueryExecutionWorker(executionContext);
        worker.setTaskDB(executionContext.getTermConnection().getDatabase());
        worker.schedule();

        editTableGridComponent.getToolbar().resetLoadMoreRecordStatus();
    }

    private void handleEventLevelOne(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE: {
                listenOnSearchDone();
                break;
            }
            case IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING: {
                listenOnDataChangeEncoding(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY: {
                if (dirtyHandler.isDirty()) {
                    showPopUpOnDirtyTableFetchNextRecords();
                    editTableGridComponent.setLoadingStatus(false);
                    return;
                }
                handleReExecuteQuery();
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED: {
                IDSEditGridDataProvider provider = (IDSEditGridDataProvider) result;
                dirtyHandler.setDirty(provider.isGridDataEdited());
                break;
            }
            default: {
                handleEventLevelTwo(event);
            }
        }
    }

    private void handleEventLevelTwo(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY: {
                listenRefreshQuery();
                break;
            }

            case IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA: {
                listenOnEditableCommitData(event);
                break;
            }

            case IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS: {
                listenDbConnectDisConnect(event);
                break;
            }

            case IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD: {
                handlePostGridDataLoadEvent();
                break;
            }
            default: {
                handleEventLevelThree(event);
            }
        }
    }

    private void handleEventLevelThree(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE: {
                handleCommitDataCompleteEvent();
                dirtyHandler.setDirty(((IDSEditGridDataProvider) result).isGridDataEdited());
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA: {
                listenOnExportCurrentPage(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA: {
                listenOnExportAllData(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM: {
                listenOnEditTableDataWindowCopyToExcelXlsxMenu(event);
                break;
            }
            default: {
                handleEventLevelFour(event);
            }
        }
    }

    private void handleEventLevelFour(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM: {
                listenEditTableDataWindowCopyToExcelXlsMenu(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT: {
                listenOnEditTableDataWindowGenerateAllInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT: {
                listenOnEditTableDataWindowGenerateSelectLineInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT: {
                listenOnEditTableDataWindowGenerateCurrentPageInsert(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH: {
                listenOnEditTableDataWindowMenuSearch(event);
                break;
            }
            default: {
                break;
            }
        }
    }

    private void listenOnEditTableDataWindowMenuSearch(DSEvent event) {
        Object editTableFocusData = event.getObject();
        Object editTableSelectSearchData = getSelectData().getCellData();
        if (SEARCHOPTIONS.SRCH_NULL.getDisplayName().equals(getSearchArea().getCmbSearchOpt().getText())) {
            getSearchArea().getTxtSearchStr().setEnabled(false);
            if (null == editTableSelectSearchData || editTableSelectSearchData.toString().isEmpty()) {
                if (editTableFocusData != null) {
                    getSearchArea().getTxtSearchStr().setEnabled(true);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                    getSearchArea().getTxtSearchStr().setText(editTableFocusData.toString());
                    getSearchArea().getTriggerSearch(editTableFocusData.toString(), true);
                } else {
                    getSearchArea().getTriggerSearch(true);
                }
            } else {
                getSearchArea().getTxtSearchStr().setEnabled(true);
                getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_CONTAINS.getDisplayName());
                getSearchArea().getTxtSearchStr().setText(editTableSelectSearchData.toString());
                getSearchArea().getTriggerSearch(editTableSelectSearchData.toString(), true);

            }
        } else {
            if (null == editTableSelectSearchData || editTableSelectSearchData.toString().isEmpty()) {
                if (editTableFocusData != null) {
                    getSearchArea().getTxtSearchStr().setText(editTableFocusData.toString());
                    getSearchArea().getTriggerSearch(editTableFocusData.toString(), true);
                } else {
                    getSearchArea().getTxtSearchStr().setText("");
                    getSearchArea().getTxtSearchStr().setEnabled(false);
                    getSearchArea().getCmbSearchOpt().setText(SEARCHOPTIONS.SRCH_NULL.getDisplayName());
                    getSearchArea().getTriggerSearch(true);
                }

            } else {
                getSearchArea().getTxtSearchStr().setText(editTableSelectSearchData.toString());
                getSearchArea().getTriggerSearch(editTableSelectSearchData.toString(), true);

            }
        }
    }

    /**
     * Gets the search area.
     *
     * @return the search area
     */
    public GridSearchAreaToDataGrid getSearchArea() {
        return this.editTableGridComponent.getSearchArea();
    }

    private void listenOnEditTableDataWindowGenerateCurrentPageInsert(DSEvent event) {
        GridResultGenerateSelectedLineInsertSql generateCurrentPageInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), null, this.result, queryExecSummary, core.getWindowTitle(), true);
        generateCurrentPageInsertSql.addObserver((Observer) event.getObject());
        if (this.dirtyHandler.isDirty()) {
            if (!isExportDirtyData()) {
                generateCurrentPageInsertSql.endOfGenerate();
                return;
            }
        }
        generateCurrentPageInsertSql.generate();
    }

    private void listenOnEditTableDataWindowGenerateSelectLineInsert(DSEvent event) {
        GridResultGenerateSelectedLineInsertSql generateSelectedLineInsertSql = new GridResultGenerateSelectedLineInsertSql(
                this.termConnection, getSelectData(), null, this.result, queryExecSummary, core.getWindowTitle(),
                false);
        generateSelectedLineInsertSql.addObserver((Observer) event.getObject());
        if (this.dirtyHandler.isDirty()) {
            if (!isExportDirtyData()) {
                generateSelectedLineInsertSql.endOfGenerate();
                return;
            }
        }
        generateSelectedLineInsertSql.generate();
    }

    private void listenOnEditTableDataWindowGenerateAllInsert(DSEvent event) {
        String progressLabel = MessageConfigLoader
                .getProperty(IMessagesConstants.GENERATE_INSERT_ALL_RESULTDATA_PROGRESS_NAME, core.getWindowTitle());
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataGenerateInsertAllWorker editTableWorker = new GridDataGenerateInsertAllWorker(this.termConnection, null,
                core.getWindowTitle(), progressLabel, this.result, (Observer) event.getObject(), false, bottomStatusBar,
                statusMessage, getFileEncoding());
        StatusMessageList.getInstance().push(statusMessage);
        if (editTableWorker.isSaveSqlFileDialog()) {
            if (bottomStatusBar != null) {
                bottomStatusBar.activateStatusbar();
            }
            editTableWorker.setTaskDB(termConnection.getDatabase());
            editTableWorker.schedule();
        }
    }

    /**
     * Gets the file encoding.
     *
     * @return the file encoding
     */
    public String getFileEncoding() {
        String fileEncoding = UserPreference.getInstance().getFileEncoding().isEmpty() ? Charset.defaultCharset().name()
                : UserPreference.getInstance().getFileEncoding();

        return fileEncoding;
    }

    private void listenEditTableDataWindowCopyToExcelXlsMenu(DSEvent event) {
        int formatIndex = 1;
        GridResultDataSelectedCopyToExcel resultCopyTOExcel = new GridResultDataSelectedCopyToExcel(getSelectData(),
                formatIndex, null, null);

        resultCopyTOExcel.addObserver((Observer) event.getObject());

        if (this.dirtyHandler.isDirty()) {
            if (!isExportDirtyData()) {
                resultCopyTOExcel.endOfCopyToEXCEL();
                return;
            }
        }

        resultCopyTOExcel.export();
    }

    private void listenOnEditTableDataWindowCopyToExcelXlsxMenu(DSEvent event) {
        int formatIndex = 0;
        GridResultDataSelectedCopyToExcel editTableResultCopyTOExcel = new GridResultDataSelectedCopyToExcel(
                getSelectData(), formatIndex, null, null);

        editTableResultCopyTOExcel.addObserver((Observer) event.getObject());

        if (this.dirtyHandler.isDirty()) {
            if (!isExportDirtyData()) {
                editTableResultCopyTOExcel.endOfCopyToEXCEL();
                return;
            }
        }

        editTableResultCopyTOExcel.export();
    }

    /**
     * Gets the select data.
     *
     * @return the select data
     */
    public GridSelectionLayerPortData getSelectData() {
        return this.editTableGridComponent.getSelectDataIterator();
    }

    private void listenOnExportAllData(DSEvent event) {
        if (!isTableExist()) {
            return;
        }
        String progressLabel = MessageConfigLoader.getProperty(IMessagesConstants.EXPORT_ALL_DATA_PROGRESS_NAME,
                core.getWindowTitle());
        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_IN_PROGRESS));
        BottomStatusBar editTableBottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (editTableBottomStatusBar != null) {
            editTableBottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        GridDataExportAllWorker editTableWorker = new GridDataExportAllWorker(this.termConnection, null,
                core.getWindowTitle(), progressLabel, this.result, (Observer) event.getObject(), false,
                editTableBottomStatusBar, statusMessage);
        StatusMessageList.getInstance().push(statusMessage);
        if (editTableWorker.isExportDialog()) {
            if (editTableBottomStatusBar != null) {
                editTableBottomStatusBar.activateStatusbar();
            }
            editTableWorker.setTaskDB(termConnection.getDatabase());
            editTableWorker.schedule();
        }
    }

    private void listenOnExportCurrentPage(DSEvent event) {
        GridResultDataCurrentPageExport editTableResultDataExporter = new GridResultDataCurrentPageExport(getUIData(),
                null, null, core.getWindowTitle());
        editTableResultDataExporter.addObserver((Observer) event.getObject());

        if (dirtyHandler.isDirty()) {
            if (!isExportDirtyData()) {
                editTableResultDataExporter.endOfExport();
                return;
            }
        }

        editTableResultDataExporter.export(false);
    }

    /**
     * Gets the UI data.
     *
     * @return the UI data
     */
    public GridViewPortData getUIData() {
        return this.editTableGridComponent.getUIDataIterator();
    }

    /**
     * Checks if is export dirty data.
     *
     * @return true, if is export dirty data
     */
    public boolean isExportDirtyData() {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.TITLE_EXPORT_DATA);
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

    private void handleCommitDataCompleteEvent() {
        editTableGridComponent.restoreLastReorderState();
        editTableGridComponent.enableDisableGrid(true);
        editTableGridComponent.getToolbar().handleDataEditEvent(true);
    }

    /**
     * Handle post grid data load event.
     */
    public void handlePostGridDataLoadEvent() {
        if (null != editTableGridComponent) {
            editTableGridComponent.setLoadingStatus(false);
            editTableGridComponent.enableDisableGrid(true);
            editTableGridComponent.restoreLastSortState();
            editTableGridComponent.restoreLastReorderState();
        }
    }

    /**
     * Listen on data change encoding.
     *
     * @param event the event
     */
    private void listenOnDataChangeEncoding(DSEvent event) {
        this.editTableGridComponent.saveSortState();
        this.editTableGridComponent.saveReorderState();
        IDSGridDataProvider dataProvider = editTableGridComponent.getDataProvider();
        if (dataProvider != null && dataProvider instanceof DSEditTableDataGridDataProvider) {
            DSEditTableDataGridDataProvider dp = (DSEditTableDataGridDataProvider) dataProvider;
            String encoding = event.getObject().toString();
            dp.changeEncoding(encoding);
            editTableGridComponent.updateGridData();
            editTableGridComponent.getToolbar()
                    .handleDataEditEvent(core.getTermConnection().getDatabase().isConnected());
        }
        editTableGridComponent.restoreLastSortState();
        editTableGridComponent.restoreLastReorderState();
    }

    /**
     * Listen on search done.
     */
    private void listenOnSearchDone() {
        if (resultGridUIPref.isEnableEdit()) {
            editTableGridComponent.getToolbar().enableDisableOnSearch(true,
                    core.getTermConnection().getDatabase().isConnected());
        }
    }

    /**
     * Listen search cleared.
     */
    private void listenSearchCleared() {
        if (resultGridUIPref.isEnableEdit()) {
            editTableGridComponent.getToolbar().enableDisableOnSearch(false,
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
        toolbar.setDataProvider(this.result);
        if (resultGridUIPref.isEnableEdit()) {
            toolbar.handleDataEditEvent(core.getTermConnection().getDatabase().isConnected());
        }
    }

    /**
     * Listen on editable commit data.
     *
     * @param event the event
     */
    /**
     * Listen on editable commit data.
     *
     * @param event the event
     */
    private void listenOnEditableCommitData(DSEvent event) {
        if (!isTableExist()) {
            return;
        }
        if (rowEffectedConfirm == null) {

            rowEffectedConfirm = new RowEffectedConfirmation();
        }
        IDSEditGridDataProvider dataProvider = (IDSEditGridDataProvider) result;

        CommitInputData saveUserOptions = getSaveConditionalInput(dataProvider);

        if (null == saveUserOptions) {
            editTableGridComponent.getToolbar().handleDataEditEvent(true);
            return;
        }
        editTableGridComponent.saveReorderState();
        editTableGridComponent.enableDisableGrid(false);
        editTableGridComponent.getToolbar().handleDataEditEvent(false);
        CommitRecordEventData commitEventData = (CommitRecordEventData) event.getObject();

        StatusMessage statusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.COMMITING_DATA));
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        commitEventData.setCommitData(saveUserOptions);
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage(statusMessage.getMessage());
        }
        String progressLabel = getProgressLabel();

        EditTableUIWorker editTableUIWorker = new EditTableUIWorker(progressLabel, "", core.getTermConnection(),
                commitEventData.getDataGrid(), commitEventData.getDataText(), this.result,
                commitEventData.getCommitData().getUniqueKeys(), commitEventData.getCommitData().isAtomic(),
                commitEventData.getEventTable(), bottomStatusBar, statusMessage, rowEffectedConfirm, false);

        StatusMessageList.getInstance().push(statusMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        editTableUIWorker.schedule();
    }

    /**
     * Gets the progress label.
     *
     * @return the progress label
     */
    protected abstract String getProgressLabel();

    /**
     * Listen refresh query.
     */
    private void listenRefreshQuery() {
        if (!isTableExist()) {
            return;
        }
        editTableGridComponent.setLoadingStatus(true);
        editTableGridComponent.enableDisableGrid(false);
        this.editTableGridComponent.saveSortState();
        this.editTableGridComponent.saveReorderState();
        RefreshEditTableMetaDataWorker refreshmetadataworker = new RefreshEditTableMetaDataWorker(core);
        refreshmetadataworker.schedule();
    }

    /**
     * Checks if is table exist.checks if the table being accessed is not
     * dropped from backend
     *
     * @return true, if is table exist
     */
    public boolean isTableExist() {
        if (core.isTableDropped()) {

            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_HEADER),
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_DROPPED_REFRESH_ERROR),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check window details.
     */
    private void checkWindowDetails() {
        MPart oldPart = this.getPart();
        String oldLabel = oldPart.getLabel();
        String oldID = oldPart.getElementId();
        String newLabel = core.getWindowDetails().getTitle();
        String newID = core.getWindowDetails().getUniqueID();
        if (!oldLabel.equals(newLabel)) {
            oldPart.setLabel(newLabel);
        }
        if (null != oldID && !oldID.equals(newID)) {
            oldPart.setElementId(newID);

        }
    }

    /**
     * Sets the remembered user options.
     *
     * @param rememberedUserOptions the new remembered user options
     */
    public void setRememberedUserOptions(CommitInputData rememberedUserOptions) {
        this.rememberedUserOptions = rememberedUserOptions;
    }

    /**
     * Gets the save conditional input.
     *
     * @param dataProvider the data provider
     * @return the save conditional input
     */
    protected abstract CommitInputData getSaveConditionalInput(IDSEditGridDataProvider dataProvider);

    /**
     * Sets the part.
     *
     * @param part the new part
     */
    public void setPart(MPart part) {
        this.part = part;
    }

    /**
     * Gets the part.
     *
     * @return the part
     */
    public MPart getPart() {
        return this.part;
    }

    /**
     * Handle exception display.
     *
     * @param obj the obj
     */
    @Override
    public void handleExceptionDisplay(Object obj) {
        if (editTableGridComponent != null) {
            editTableGridComponent.setLoadingStatus(false);
            editTableGridComponent.enableDisableGrid(true);
        }
        if (isDisposed()) {
            return;
        }
        MPPDBIDEException exception = (MPPDBIDEException) obj;
        StringBuilder customErrorMsg = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (null != exception && exception.getServerMessage() != null) {
            if (exception.getServerMessage().contains("does not exist")) {
                customErrorMsg.append(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDIT_TABLE_DROP_TABLE));
            } else {
                customErrorMsg.append(exception.getServerMessage());
            }
        }
        String strServerError = customErrorMsg.toString();
        if (strServerError.contains("Position:")) {
            strServerError = strServerError.split("Position:")[0];
        }

        String msgString = MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_MESSAGE);

        String hintMsg = "";
        if (null != exception && exception.getDBErrorMessage().contains("No matching")) {
            hintMsg = MessageConfigLoader.getProperty(IMessagesConstants.CHECK_FILE_ENCODE_SET);
        }

        if (!strServerError
                .contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED))) {
            MPPDBIDEDialogs.generateErrorPopup(
                    MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_HEADER),
                    Display.getDefault().getActiveShell(), exception, hintMsg, msgString, strServerError);
        }
    }
    // CHECKTYLE: ON

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
     * Can dislay result.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDislayResult() {

        return !UIElement.getInstance().isWindowLimitReached();
    }

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     */
    @Override
    protected void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary) {
        if (this.isDisposed()) {
            return;
        }

        setResultData(resultsetDisplaydata, queryExecSummary);

        IWindowDetail windowDetails = core.getWindowDetails();
        boolean isEditTableWindowExists = UIElement.getInstance().isWindowExists(windowDetails);
        EditTableDataUIWindow editTableDataWindow = (EditTableDataUIWindow) UIElement.getInstance()
                .getEditTableDataWindow(windowDetails, this);

        // Check if the window is null. This will be due to the number of
        // windows open is more than the threshold.
        if (editTableDataWindow == null) {
            return;
        }
        if (null != editTableDataWindow.getResultDisplayUIManager() && isEditTableWindowExists) {
            // If old manager is being used, then reset the new result and
            // summary.
            editTableDataWindow.getResultDisplayUIManager().setResultData(result, queryExecSummary);
            editTableDataWindow.resetData();
            UIElement.getInstance().bringOnTopViewTableDataWindow(windowDetails);
        }
    }

    /**
     * Sets the result data.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param queryExecSummary the query exec summary
     */
    protected void setResultData(IDSGridDataProvider resultsetDisplaydata, IQueryExecutionSummary queryExecSummary) {
        this.queryExecSummary = queryExecSummary;
        this.result = resultsetDisplaydata;
        if (result instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider editDataProvider = (DSResultSetGridDataProvider) result;
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, this);
        }
    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    public void showResult(Composite parentComposite) {
        if (editTableGridComponent == null) {
            this.resultGridUIPref = new EditTableDataGridUIPref();
            this.editTableGridComponent = new DSGridComponent(resultGridUIPref, this.result);
            this.editTableGridComponent.createComponents(parentComposite);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_ROLLBACK_DATA_COMPLETE, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS,
                    this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_POST_GRID_DATA_LOAD, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_USER_FORGET_OPTION, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_CLEARED, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_SEARCH_DONE, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_DATA_CHANGE_ENCODING, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_REEXECUTE_QUERY, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_CURR_PAGE_DATA, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_EXPORT_ALL_DATA, this);
            this.editTableGridComponent
                    .addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLSX_RESULT_WINDOW_MENUITEM, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_COPY_TO_EXCEL_XLS_RESULT_WINDOW_MENUITEM,
                    this);
            this.editTableGridComponent
                    .addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_SELECT_LINE_INSERT, this);
            this.editTableGridComponent
                    .addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_CURRENT_PAGE_INSERT, this);
            this.editTableGridComponent
                    .addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_MENUITEM_GERERATE_ALL_INSERT, this);
            this.editTableGridComponent.addListener(IDSGridUIListenable.LISTEN_TYPE_ON_RESULT_WINDOW_MENUITEM_SEARCH,
                    this);
            registerDataProviderEditListener();
        }
    }

    /**
     * Register data provider edit listener.
     */
    private void registerDataProviderEditListener() {
        if (null != result && result instanceof DSResultSetGridDataProvider) {
            DSResultSetGridDataProvider editDataProvider = (DSResultSetGridDataProvider) result;
            editDataProvider.addListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    editTableGridComponent.getDataEditListener());
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class EditTableDataGridUIPref.
     */
    public class EditTableDataGridUIPref extends GridUIPreference {
        /**
         * Checks if is enable edit.
         *
         * @return true, if is enable edit
         */
        @Override
        public boolean isEnableEdit() {

            return true;
        }

        /**
         * Checks if is unique remembered.
         *
         * @return true, if is unique remembered
         */
        public boolean isUniqueRemembered() {
            return null != AbstractEditTableDataResultDisplayUIManager.this.rememberedUserOptions;
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
         * Checks if is edits the query results flow.
         *
         * @return true, if is edits the query results flow
         */
        @Override
        public boolean isEditQueryResultsFlow() {
            return true;
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

        /**
         * Is refresh enabled.
         *
         * @return true, if refresh is supported
         */
        @Override
        public boolean isRefreshSupported() {
            return true;
        }

        @Override
        public boolean isShowRightClickMenu() {
            return true;
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
         * Checks if is show generate insert.
         *
         * @return true, if is show generate insert
         */
        @Override
        public boolean isShowGenerateInsert() {
            Boolean isOlap = true;
            switch (termConnection.getDatabase().getDBType()) {
                case OPENGAUSS: {
                    isOlap = true;
                    break;
                }
                default: {
                    break;
                }
            }
            return isOlap;
        }

        @Override
        public boolean isStartSelectQuery() {
            return true;
        }

        /**
         * Gets the selected encoding.
         *
         * @return the selected encoding
         */
        @Override
        public String getSelectedEncoding() {
            String encoding = editTableGridComponent.getSelectedEncoding();

            if (MPPDBIDEConstants.SPACE_CHAR.equals(encoding) || null == encoding) {
                encoding = PreferenceWrapper.getInstance().getPreferenceStore()
                        .getString(UserEncodingOption.DATA_STUDIO_ENCODING);
            }

            return encoding;

        }
    }

    /**
     * Reset data result.
     */
    public void resetDataResult() {
        registerDataProviderEditListener();
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (editTableGridComponent != null && !editTableGridComponent.isDisposed()) {
                    editTableGridComponent.setDataProvider(AbstractEditTableDataResultDisplayUIManager.this.result);
                }
            }
        });
    }

    /**
     * On focus.
     */
    public void onFocus() {
        this.editTableGridComponent.focus();
    }

    /**
     * Sets the dirty handler.
     *
     * @param terminalDirty the new dirty handler
     */
    public void setDirtyHandler(MDirtyable terminalDirty) {
        this.dirtyHandler = terminalDirty;
    }

    /**
     * Sets the disposed.
     */
    @Override
    public void setDisposed() {
        super.setDisposed();
        if (result instanceof DSResultSetGridDataProvider) {
            ((DSResultSetGridDataProvider) result).removeListener(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED,
                    this);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class RefreshEditTableMetaDataWorker.
     */
    private class RefreshEditTableMetaDataWorker extends UIWorkerJob {

        private IEditTableDataCore editTableCore;
        private DBConnection dbconn;

        /**
         * Instantiates a new refresh edit table meta data worker.
         *
         * @param core the core
         */
        private RefreshEditTableMetaDataWorker(IEditTableDataCore core) {
            super("Refresh Edit Core", null);
            this.editTableCore = core;
        }

        @Override
        public Object doJob()
                throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
            // oid Before refresh to check availability in cache
            dbconn = editTableCore.getTermConnection().getDatabase().getConnectionManager().getObjBrowserConn();
            editTableCore.refreshTable(dbconn);
            if (editTableCore.isTableDropped()) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED));
                throw new DatabaseOperationException(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED);
            }
            return null;
        }

        @Override
        public void onSuccessUIAction(Object object) {
            checkWindowDetails();
            if (isDisposed()) {
                return;
            }
            AbstractEditTableDataResultDisplayUIManager.this.setCore(editTableCore);
            EditTableDataExecutionContext context = new EditTableDataExecutionContext(editTableCore,
                    AbstractEditTableDataResultDisplayUIManager.this, editTableCore.getTable());

            TerminalQueryExecutionWorker terminalQueryWorker = new TerminalQueryExecutionWorker(context);
            terminalQueryWorker.setTaskDB(editTableCore.getTermConnection().getDatabase());
            terminalQueryWorker.schedule();
        }

        @Override
        public void onCriticalExceptionUIAction(DatabaseCriticalException excep) {
            handleExceptionDisplay(excep);
            MPPDBIDELoggerUtility.error("Refresh edit table metadata failed.", excep);
        }

        @Override
        public void onOperationalExceptionUIAction(DatabaseOperationException excep) {
            handleExceptionDisplay(excep);
            if (excep.getMessage().equalsIgnoreCase(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_EDITTABLE_REFRESH_FAILED))) {
                MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                        MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_HEADER),
                        MessageConfigLoader.getProperty(IMessagesConstants.EDIT_TABLE_DATA_DROPPED_REFRESH_ERROR),
                        new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
            }
            MPPDBIDELoggerUtility.error("Refresh edit table metadata failed.", excep);
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
     * 
     * Title: class
     * 
     * Description: The Class RowEffectedConfirmation.
     */
    public static class RowEffectedConfirmation implements IRowEffectedConfirmation {

        /**
         * Prompt uer confirmation.
         */
        @Override
        public void promptUerConfirmation() {
            Display.getDefault().syncExec(new Runnable() {

                @Override
                public void run() {
                    RowEffectedConfirmationPrompt promptMsg = new RowEffectedConfirmationPrompt(
                            Display.getDefault().getActiveShell(),
                            MessageConfigLoader.getProperty(IMessagesConstants.EDIT_DUPLICATE_MODIFICATION_TITLE),
                            IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                            MessageConfigLoader.getProperty(IMessagesConstants.EDIT_DUPLICATE_MODIFICATION_MSG),
                            MessageDialog.INFORMATION, null, 1);

                    promptMsg.open();
                }
            });

        }
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

    }

    /**
     * Sets the cursor offset.
     *
     * @param offset the new cursor offset
     */
    @Override
    public void setCursorOffset(int offset) {

    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {
        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                if (null != editTableGridComponent && !editTableGridComponent.isDisposed()) {
                    editTableGridComponent.setLoadingStatus(false);
                    editTableGridComponent.enableDisableGrid(true);
                }
            }
        });
    }

    private void showPopUpOnDirtyTableFetchNextRecords() {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.INFORMATION, true,
                MessageConfigLoader.getProperty(IMessagesConstants.SAVE_CHANGES_TITLE),
                MessageConfigLoader.getProperty(IMessagesConstants.SAVE_CHANGES_DATA_BODY));
    }
}
