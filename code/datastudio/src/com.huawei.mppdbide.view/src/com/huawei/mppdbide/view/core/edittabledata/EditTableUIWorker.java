/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.edittabledata;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.TerminalExecutionSQLConnectionInfra;
import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.EditTableCellState;
import com.huawei.mppdbide.presentation.edittabledata.EditTableRecordStates;
import com.huawei.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridColumnProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataRow;
import com.huawei.mppdbide.presentation.grid.IRowEffectedConfirmation;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.view.component.grid.core.DataGrid;
import com.huawei.mppdbide.view.component.grid.core.DataText;
import com.huawei.mppdbide.view.handler.connection.PromptPasswordUIWorkerJob;
import com.huawei.mppdbide.view.ui.terminal.SQLTerminal;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

import ca.odell.glazedlists.EventList;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableUIWorker.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class EditTableUIWorker extends PromptPasswordUIWorkerJob {
    private IDSGridDataProvider dataProvider;
    private DataGrid dataGrid;
    private DataText dataText;
    private List<String> colNames;
    private boolean isAtomic;
    private DSEventTable eventTable;
    private CommitStatus commitStatus;
    private BottomStatusBar statusBar;

    private StatusMessage statusMessage;
    private TerminalExecutionConnectionInfra terminalConnection;
    private IRowEffectedConfirmation rowEffectedConfirm;
    private boolean isResultTabFlow;
    private DBConnection conn;
    private SQLTerminal terminal;

    /**
     * Instantiates a new edits the table UI worker.
     *
     * @param name the name
     * @param family the family
     * @param termConnection the term connection
     * @param dataGrid the data grid
     * @param dataText the data text
     * @param dataProvider the data provider
     * @param columnNames the column names
     * @param isAtomic the is atomic
     * @param eventTable the event table
     * @param statusBar the status bar
     * @param statusMessage the status message
     * @param rowEffectedConfirm the row effected confirm
     * @param isResultTabFlow the is result tab flow
     */
    public EditTableUIWorker(String name, Object family, TerminalExecutionConnectionInfra termConnection,
            DataGrid dataGrid, DataText dataText, IDSGridDataProvider dataProvider, List<String> columnNames,
            boolean isAtomic, DSEventTable eventTable, BottomStatusBar statusBar, StatusMessage statusMessage,
            IRowEffectedConfirmation rowEffectedConfirm, boolean isResultTabFlow) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.EDIT_TABLE_DATA_ERROR_POPUP_HEADER);
        this.terminalConnection = termConnection;
        this.dataGrid = dataGrid;
        this.dataText = dataText;
        this.dataProvider = dataProvider;
        this.colNames = columnNames;
        this.isAtomic = isAtomic;
        this.eventTable = eventTable;
        this.statusBar = statusBar;
        this.statusMessage = statusMessage;
        this.rowEffectedConfirm = rowEffectedConfirm;
        this.isResultTabFlow = isResultTabFlow;
        this.conn = null;
    }

    /**
     * Do job.
     *
     * @return the object
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws Exception the exception
     */
    @Override
    public Object doJob() throws DatabaseOperationException, DatabaseCriticalException, MPPDBIDEException, Exception {
        if (isResultTabFlow) {
            TerminalExecutionSQLConnectionInfra sqlConnection = (TerminalExecutionSQLConnectionInfra) terminalConnection;
            if (null == sqlConnection) {
                return null;
            }
            setServerPwd(!sqlConnection.getReuseConnectionFlag()
                    && sqlConnection.getDatabase().getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
            conn = sqlConnection.getSecureConnection(this);
            while (conn == null) {
                Thread.sleep(SQL_TERMINAL_THREAD_SLEEP_TIME);
                if (this.isCancel()) {
                    return null;
                }
                if (this.isNotified()) {
                    conn = this.terminalConnection.getConnection();
                }
            }
        }

        Database db = getDatabase();
        if (null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE)
                && getEncrpytedProfilePrd() != null) {
            db.getServer().setPrd(getEncrpytedProfilePrd());
        }
        // local variable to be used in future
        // CommitStatus commitStatus
        ((IDSEditGridDataProvider) dataProvider).setCancel(false);
        commitStatus = ((IDSEditGridDataProvider) dataProvider).commit(colNames, isAtomic, rowEffectedConfirm, conn);

        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        if (null == this.dataGrid.getDataGrid()) {
            return;
        }

        EventList<IDSGridDataRow> listOfRows = this.dataGrid.getListOfRows();
        Map<Integer, IDSGridDataRow> failedInsertsMap = new TreeMap<Integer, IDSGridDataRow>();
        int index = 0;
        Integer rowIndex;
        IDSGridDataRow row = null;

        if (null != commitStatus) {
            List<IDSGridEditDataRow> listOfFailureRows = commitStatus.getListOfFailureRows();

            for (Iterator<IDSGridEditDataRow> iterator = listOfFailureRows.iterator(); iterator.hasNext();) {
                IDSGridEditDataRow idsGridEditDataRow = (IDSGridEditDataRow) iterator.next();
                if (idsGridEditDataRow.getUpdatedState() == EditTableRecordStates.INSERT) {
                    index = listOfRows.indexOf(idsGridEditDataRow);
                    failedInsertsMap.put(index, idsGridEditDataRow);
                }
            }
        }

        listOfRows.clear();
        listOfRows.addAll(dataProvider.getAllFetchedRows());

        for (Map.Entry<Integer, IDSGridDataRow> entry : failedInsertsMap.entrySet()) {
            row = entry.getValue();
            rowIndex = entry.getKey();
            listOfRows.add(rowIndex, row);

        }

        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
        if (null != commitStatus && commitStatus.getListOfFailureRows().size() > 0) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR));
            resetCellValues(commitStatus.getListOfFailureRows());
        }

        if (null != dataText && null != commitStatus && commitStatus.getListOfSuccessRows().size() > 0) {
            this.dataText.updateTextData();
        }
    }

    /**
     * Reset cell values.
     *
     * @param listOfFailureRows the list of failure rows
     */
    private void resetCellValues(List<IDSGridEditDataRow> listOfFailureRows) {
        IDSGridColumnProvider columnDataProvider = dataProvider.getColumnDataProvider();
        if (null != columnDataProvider) {
            int length = columnDataProvider.getColumnCount();
            // CHECKSTYLE:OFF
            for (IDSGridEditDataRow row : listOfFailureRows) {
                for (int cnt = 0; cnt < length; cnt++) {
                    row.setCellSatus(EditTableCellState.MODIFIED_FAILED, cnt);
                }
            }
        }
        // CHECKSTYLE:ON
    }

    /**
     * On operational exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onOperationalExceptionUIAction(DatabaseOperationException exception) {
        exceptionEventCall(exception);
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        super.finalCleanup();
        if (isResultTabFlow) {
            ((TerminalExecutionSQLConnectionInfra) terminalConnection).releaseSecureConnection(this.conn);
        }
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (null != commitStatus && commitStatus.getListOfSuccessRows().size() > 0) {
            if (terminal != null) {
                terminal.resetCommitAndRollbackButton();
            }
        }
        statusBar.hideStatusbar(statusMessage);
        if (eventTable != null) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE, null));
        }

    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        if (isResultTabFlow) {
            TerminalExecutionSQLConnectionInfra sqlConnection = (TerminalExecutionSQLConnectionInfra) terminalConnection;
            if (sqlConnection.getReuseConnectionFlag()) {
                return true;
            }
        }
        return promptAndValidatePassword();
    }

    /**
     * On critical exception UI action.
     *
     * @param exception the exception
     */
    @Override
    public void onCriticalExceptionUIAction(DatabaseCriticalException exception) {
        exceptionEventCall(exception);
    }

    /**
     * Exception event call.
     *
     * @param exception the exception
     */
    public void exceptionEventCall(MPPDBIDEException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                exception.getServerMessage());
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
        CommitStatus lastCommitStatus = ((IDSEditGridDataProvider) dataProvider).getLastCommitStatus();
        if (lastCommitStatus != null) {
            resetCellValues(lastCommitStatus.getListOfFailureRows());
        }
    }

    /**
     * Canceling.
     */
    @Override
    protected void canceling() {
        super.canceling();
        try {
            ((IDSEditGridDataProvider) dataProvider).setCancel(true);

            ((IDSEditGridDataProvider) dataProvider).cancelCommit();

            if (isResultTabFlow) {
                ((TerminalExecutionSQLConnectionInfra) terminalConnection).releaseSecureConnection(this.conn);
            }
        } catch (DatabaseCriticalException exception) {
            exceptionEventCall(exception);
        } catch (DatabaseOperationException exception) {
            exceptionEventCall(exception);
        }

    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    protected Database getDatabase() {
        return terminalConnection.getDatabase();
    }

    /**
     * Sets the terminal.
     *
     * @param terminal the new terminal
     */
    public void setTerminal(SQLTerminal terminal) {
        this.terminal = terminal;
    }
}
