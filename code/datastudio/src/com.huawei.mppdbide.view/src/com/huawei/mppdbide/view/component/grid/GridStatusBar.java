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

import java.util.Observable;
import java.util.Observer;

import javax.annotation.PreDestroy;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.edittabledata.CommitStatus;
import com.huawei.mppdbide.presentation.edittabledata.DSResultSetGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import com.huawei.mppdbide.presentation.visualexplainplan.ExecutionPlanTextDisplayGrid;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.DSGridStateMachine;
import com.huawei.mppdbide.view.component.DSGridStateMachine.State;

/**
 * 
 * Title: class
 * 
 * Description: The Class GridStatusBar.
 *
 * @since 3.0.0
 */
public class GridStatusBar {
    private Label lblFetchedSummary;
    private Label lblExecutionTime;
    private Label lblOperationDesc;
    private Label refreshLbl;

    private IDSGridDataProvider dataProvider;
    private Composite statusCom;

    /**
     * Creates the component.
     *
     * @param parent the parent
     * @param stateMachine the state machine
     * @param gridDataProvider the grid data provider
     */
    public void createComponent(Composite parent, DSGridStateMachine stateMachine,
            IDSGridDataProvider gridDataProvider) {
        statusCom = createComposite(parent);

        addExecutionTime(statusCom);
        addFetchedSummary(statusCom);
        addRefreshLabel(statusCom);
        addOperationDescItem(statusCom, stateMachine);
        setDataProvider(gridDataProvider);
    }

    private void addExecutionTime(Composite statusComposite) {
        lblExecutionTime = new Label(statusComposite, SWT.BORDER);
        GridData layout = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        layout.widthHint = 100;
        lblExecutionTime.setLayoutData(layout);
    }

    private void addFetchedSummary(Composite statusComposite) {
        lblFetchedSummary = new Label(statusComposite, SWT.BORDER);
        GridData layout = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        layout.widthHint = 10;
        lblFetchedSummary.setLayoutData(layout);
    }

    private void addRefreshLabel(Composite statusComposite) {
        refreshLbl = new Label(statusComposite, SWT.BORDER);
        refreshLbl.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_BLUE));
        GridData layout = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        refreshLbl.setLayoutData(layout);
    }

    private void addOperationDescItem(Composite statusComposite, DSGridStateMachine stateMachine) {
        lblOperationDesc = new Label(statusComposite, SWT.BORDER);
        GridData layout = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        lblOperationDesc.setLayoutData(layout);

        stateMachine.addObserver(new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                if (arg instanceof DSGridStateMachine.State) {
                    DSGridStateMachine.State newState = (DSGridStateMachine.State) arg;
                    if (!lblOperationDesc.isDisposed()) {
                        lblOperationDesc.setText(newState.getDisplayMsg());
                        lblOperationDesc.setToolTipText(newState.getDisplayMsg());
                    }
                    setOperationDescColor(newState);
                }
            }
        });
    }

    private Composite createComposite(Composite parent) {
        Composite newComposite = new Composite(parent, SWT.NONE);
        /* Make each row expand horizontally but not vertically */
        GridData layout = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        layout.heightHint = 20;
        GridLayout glayout = new GridLayout(4, false);
        glayout.marginWidth = 0;
        glayout.marginHeight = 0;
        glayout.horizontalSpacing = 0;
        glayout.verticalSpacing = 0;
        newComposite.setLayout(glayout);
        newComposite.setLayoutData(layout);
        return newComposite;
    }

    /**
     * Update data provider summary data.
     *
     * @param dataObj the data obj
     */
    public void updateDataProviderSummaryData(IDSGridDataProvider dataObj) {
        if (dataObj instanceof DSResultSetGridDataProvider | dataObj instanceof ExecutionPlanTextDisplayGrid) {
            IQueryExecutionSummary summary = null;
            if (dataObj instanceof DSResultSetGridDataProvider) {
                summary = ((DSResultSetGridDataProvider) dataObj).getSummary();
            } else {
                summary = ((ExecutionPlanTextDisplayGrid) dataObj).getSummary();
            }
            lblExecutionTime.setText(composeQuerySubmitTimeMsg(summary));

            lblExecutionTime.setToolTipText(composeQuerySubmitTimeMsg(summary));

            lblFetchedSummary.setText(composeFetchRecordSummary(dataObj, summary));

            lblFetchedSummary.setToolTipText(composeFetchRecordSummary(dataObj, summary));

            setEditableOrNonEditableStatusMessage(dataObj);
        }
    }

    /**
     * Update data provider summary data.
     *
     * @param summary the summary
     */
    public void updateDataProviderSummaryData(IQueryExecutionSummary summary) {
        lblExecutionTime.setText(composeQuerySubmitTimeMsg(summary));
        lblExecutionTime.setToolTipText(composeQuerySubmitTimeMsg(summary));
        lblFetchedSummary.setText(composeExecutionTimeTaken(summary));
        lblFetchedSummary.setToolTipText(composeExecutionTimeTaken(summary));
    }

    private void setEditableOrNonEditableStatusMessage(IDSGridDataProvider dataObj) {
        if (this.refreshLbl == null || this.refreshLbl.isDisposed()) {
            return;
        }
        if (!(this.dataProvider instanceof DSObjectPropertiesGridDataProvider)) {
            refreshLblSetText(dataObj);
        }
    }

    /**
     * Sets the editable or non editable status message for data text.
     *
     * @param dataObj the data obj
     * @param partloaded the partloaded
     * @param loadedRowCnt the loaded row cnt
     */
    public void setEditableOrNonEditableStatusMessageForDataText(IDSGridDataProvider dataObj, boolean partloaded,
            int loadedRowCnt) {
        if (this.refreshLbl == null || this.refreshLbl.isDisposed()) {
            return;
        }
        if (!(dataObj instanceof DSObjectPropertiesGridDataProvider)) {
            if (partloaded) {
                refreshLbl.setText(
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_ALL_DATALOAD_MSG, loadedRowCnt));
                refreshLbl.setToolTipText(
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_ALL_DATALOAD_MSG, loadedRowCnt));
            } else {
                refreshLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_NON_EDITABLE_MSG));
                refreshLbl
                        .setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_NON_EDITABLE_MSG));
            }

        }
    }

    /**
     * Sets the editable or non editable status message for data grid.
     *
     * @param dataObj the new editable or non editable status message for data
     * grid
     */
    public void setEditableOrNonEditableStatusMessageForDataGrid(IDSGridDataProvider dataObj) {
        if (this.refreshLbl == null || this.refreshLbl.isDisposed()) {
            return;
        }
        if (!(this.dataProvider instanceof DSObjectPropertiesGridDataProvider)) {
            refreshLblSetText(dataObj);

        }
    }

    private void refreshLblSetText(IDSGridDataProvider dataObj) {
        if (dataObj instanceof IDSEditGridDataProvider) {
            refreshLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_EDITABLE_MSG));
            refreshLbl.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_EDITABLE_MSG));
        } else {
            refreshLbl.setText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_NON_EDITABLE_MSG));
            refreshLbl.setToolTipText(MessageConfigLoader.getProperty(IMessagesConstants.RESULTSET_NON_EDITABLE_MSG));
        }
    }

    /**
     * Update edit table status.
     *
     * @param dataObj the data obj
     */
    private void updateEditTableStatus(IDSGridDataProvider dataObj) {
        if (dataObj instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDataProvider = (IDSEditGridDataProvider) dataObj;
            CommitStatus commitStatus = editDataProvider.getLastCommitStatus();
            updateEditTableDataMsg(commitStatus);
        }
    }

    private String composeExecutionTimeTaken(IQueryExecutionSummary summary) {
        String msg;
        msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EXECUTION_TIME,
                summary.getElapsedTime());
        return msg;
    }

    private String composeFetchRecordSummary(IDSGridDataProvider dataProvidr, IQueryExecutionSummary summary) {
        String msg;

        if (dataProvidr.isEndOfRecords() || dataProvidr.getRecordCount() == 0) {
            if (summary.getNumRecordsFetched() == 1) {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_RECORD_FETCHED_ONE,
                        summary.getNumRecordsFetched(), summary.getElapsedTime());
            } else {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_RECORD_FETCHED_ALL,
                        summary.getNumRecordsFetched(), summary.getElapsedTime());
            }
        } else {
            if (summary.getNumRecordsFetched() == 1) {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_ROW_FETCHED,
                        summary.getNumRecordsFetched(), summary.getElapsedTime(),
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_RECORD_MORE));
            } else {
                msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_ROWS_FETCHED,
                        summary.getNumRecordsFetched(), summary.getElapsedTime(),
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_RECORD_MORE));
            }
        }

        return msg;
    }

    private String composeQuerySubmitTimeMsg(IQueryExecutionSummary summary) {
        return MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_QUERY_SUBMIT_TIME,
                summary.getQueryStartDate());
    }

    /**
     * Gets the update message listener.
     *
     * @return the update message listener
     */
    public IDSListener getUpdateMessageListener() {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                Object dataObj = event.getObject();
                if (dataObj instanceof IDSGridDataProvider) {
                    updateDataProviderSummaryData((IDSGridDataProvider) dataObj);
                }
            }

        };
    }

    /**
     * Gets the data load error listener.
     *
     * @param stateMachine the state machine
     * @return the data load error listener
     */
    public IDSListener getDataLoadErrorListener(final DSGridStateMachine stateMachine) {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                stateMachine.set(State.ERROR);
            }
        };
    }

    /**
     * Gets the update edit table status.
     *
     * @return the update edit table status
     */
    public IDSListener getUpdateEditTableStatus() {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {
                Object dataObj = event.getObject();
                if (dataObj instanceof IDSGridDataProvider) {
                    updateEditTableStatus((IDSGridDataProvider) dataObj);
                }
            }
        };

    }

    /**
     * Gets the update Z edit properties status.
     *
     * @return the update Z edit properties status
     */
    public IDSListener getUpdateZEditPropertiesStatus() {
        return new IDSListener() {
            @Override
            public void handleEvent(DSEvent event) {

                Object dataObj = event.getObject();
                updateEditTableDataMsg((CommitStatus) dataObj);
            }
        };

    }

    /**
     * Update edit table data msg.
     *
     * @param commitStatus the commit status
     */
    private void updateEditTableDataMsg(CommitStatus commitStatus) {
        int successCount = (null == commitStatus) ? 0 : commitStatus.getListOfSuccessRows().size();
        int failureCount = (null == commitStatus) ? 0 : commitStatus.getListOfFailureRows().size();
        int updatedRows = (null == commitStatus) ? 0 : commitStatus.getUpdatedRecords();
        int nonExecutedRows = (null == commitStatus) ? 0 : commitStatus.getListOfNotExecutedRows().size();

        String msg = MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_SUCCESS,
                successCount,
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_FAILURE,
                        failureCount),
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_UPDATED, updatedRows),
                MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_NONEXECUTED,
                        nonExecutedRows));
        if (!lblOperationDesc.isDisposed()) {
            lblOperationDesc.setText(msg);
            lblOperationDesc.setToolTipText(msg);
        }
        if (!(this.dataProvider instanceof DSObjectPropertiesGridDataProvider)) {
            if (!refreshLbl.isDisposed()) {
                refreshLbl.setText(
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_REFRESH));
                refreshLbl.setToolTipText(
                        MessageConfigLoader.getProperty(IMessagesConstants.RESULT_WINDOW_EDITTABLE_COMMIT_REFRESH));
            }
        }
        if (failureCount > 0) {
            setOperationDescColor(DSGridStateMachine.State.ERROR);
        } else if (successCount > 0) {
            setOperationDescColor(DSGridStateMachine.State.LOADING);
        } else {
            setOperationDescColor(DSGridStateMachine.State.IDLE);
        }
    }

    /**
     * Handle data edit event.
     */
    public void handleDataEditEvent() {

        if (this.dataProvider instanceof IDSEditGridDataProvider) {
            IDSEditGridDataProvider editDP = (IDSEditGridDataProvider) this.dataProvider;
            StringBuilder strBlr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            final String statusBreak = " / ";
            setEditableOrNonEditableStatusMessage(this.dataProvider);
            strBlr.append(MessageConfigLoader.getProperty(IMessagesConstants.GRID_EDIT_STATUS_INSERTED));
            strBlr.append(editDP.getInsertedRowCount());
            strBlr.append(statusBreak);
            strBlr.append(MessageConfigLoader.getProperty(IMessagesConstants.GRID_EDIT_STATUS_UPDATED));
            strBlr.append(editDP.getUpdatedRowCount());
            strBlr.append(statusBreak);
            strBlr.append(MessageConfigLoader.getProperty(IMessagesConstants.GRID_EDIT_STATUS_DELETED));
            strBlr.append(editDP.getDeletedRowCount());
            if (!lblOperationDesc.isDisposed()) {
                lblOperationDesc.setText(strBlr.toString());
                lblOperationDesc.setToolTipText(strBlr.toString());
                setOperationDescColor(DSGridStateMachine.State.IDLE);
            }
        }

    }

    private void setOperationDescColor(DSGridStateMachine.State state) {
        int fgColor = state.getColor();
        if (null != lblOperationDesc && !lblOperationDesc.isDisposed()) {
            lblOperationDesc.setForeground(Display.getDefault().getSystemColor(fgColor));
        }
    }

    /**
     * Sets the data provider.
     *
     * @param dataProvider the new data provider
     */
    public void setDataProvider(IDSGridDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        dataProvider = null;
    }

    /**
     * Show status bar.
     */
    public void showStatusBar() {

        GridUIUtils.toggleCompositeSectionVisibility(statusCom, false, null, false);
    }

    /**
     * Hide status bar.
     */
    public void hideStatusBar() {

        GridUIUtils.toggleCompositeSectionVisibility(statusCom, true, null, false);
    }
}
