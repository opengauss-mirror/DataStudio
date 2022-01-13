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

package com.huawei.mppdbide.view.ui.terminal;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PreDestroy;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.IServerObjectBatchOperations;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.Tablespace;
import com.huawei.mppdbide.bl.serverdatacache.UserRole;
import com.huawei.mppdbide.bl.serverdatacache.ViewMetaData;
import com.huawei.mppdbide.bl.serverdatacache.groups.DatabaseObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.TablespaceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.UserRoleObjectGroup;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.grid.batchdrop.BatchDropDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.Message;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.DSEventTable;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.batchdrop.BatchDropWorkerJob;
import com.huawei.mppdbide.view.component.GridUIPreference;
import com.huawei.mppdbide.view.component.grid.DSGridComponent;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindowDummy;
import com.huawei.mppdbide.view.core.statusbar.ObjectBrowserStatusBarProvider;
import com.huawei.mppdbide.view.ui.BatchDropUIWindow;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class Description: The Class BatchDropDisplayUIManager.
 *
 * @since 3.0.0
 */
public class BatchDropDisplayUIManager extends AbstractResultDisplayUIManager implements IDSListener {
    private static final Object LOCK = new Object();

    /**
     * Title: enum Description: The Enum BatchDropOperState.
     */
    private static enum BatchDropOperState {
        INIT, START, FINISHED, ROLLBACK_STOP_CANCEL;

        /**
         * Checks if is state transition allowed.
         *
         * @param currentState the current state
         * @param newState the new state
         * @return true, if is state transition allowed
         */
        public static boolean isStateTransitionAllowed(BatchDropOperState currentState, BatchDropOperState newState) {
            switch (currentState) {
                case INIT:
                case ROLLBACK_STOP_CANCEL: {
                    if (newState == START) {
                        return true;
                    }

                    break;
                }

                case START: {
                    if (newState == FINISHED || newState == ROLLBACK_STOP_CANCEL || newState == INIT) {
                        return true;
                    }

                    break;
                }

                case FINISHED: {
                    return false;
                }
            }

            return false;
        }
    }

    /**
     * Title: enum Description: The Enum BatchDropAllowed.
     */
    private static enum BatchDropAllowed {
        DROPALLOWED, NODBFOUND, MULTIDBFOUND, DROPNOTALLOWED;
    }

    private BatchDropUIPref batchDropUIPref;

    private BatchDropWindowDetails windowDetails;

    private MDirtyable dirtyHandler;

    private volatile DSGridComponent gridComponent;

    private volatile IDSGridDataProvider dataProvider;

    private DSEventTable eventTable;

    private List<?> objectsToDrop;

    private List<ServerObject> objectsToDropParent;

    private List<UserRoleObjectGroup> userRoleToDropParent;

    private volatile Database database = null;

    private ConsoleMessageWindow consoleMessageWindowDummy;

    private BatchDropWorkerJob batchDropWorker = null;

    private static int dropWindowCounterId = 1;

    private static int openedDropWindowsCount;

    private int objectDropSuccessCnt;

    private int objectDropFailureCnt;

    private volatile int objectDropTotalCnt;

    private BatchDropOperState currentState;

    private IExecTimer timer;

    /**
     * Instantiates a new batch drop display UI manager.
     *
     * @param sObjects the s objects
     */
    public BatchDropDisplayUIManager(List<?> sObjects) {
        super(new TerminalExecutionConnectionInfra());
        objectsToDrop = sObjects;
        windowDetails = null;
        objectDropSuccessCnt = 0;
        objectDropFailureCnt = 0;
        eventTable = new DSEventTable();
        objectsToDropParent = null;
        currentState = BatchDropOperState.INIT;
    }

    private BatchDropAllowed isObjectDropAllowed() {
        List<Database> dataBaseList = new ArrayList<Database>(1);
        List<ServerObject> objList = new ArrayList<ServerObject>(1);
        initiliazeAllObjectsList();
        int index = 0;
        for (Object obj : objectsToDrop) {
            if (isDropNotAllowed(obj)) {
                return BatchDropAllowed.DROPNOTALLOWED;
            } else if (isServerObject(obj)) {
                if (!((IServerObjectBatchOperations) obj).isDropAllowed()) {
                    return BatchDropAllowed.DROPNOTALLOWED;
                }

                Database db = ((ServerObject) obj).getDatabase();

                if (dataBaseList.size() >= 1 && isMultiDbFound(dataBaseList, obj, db)) {
                    return BatchDropAllowed.MULTIDBFOUND;
                } else {
                    dataBaseList.add(db);
                }

                objList.add(0, (ServerObject) obj);

                /* Add parent to List */
                // support UserRoleObjectGroup by Martin
                if (obj instanceof UserRole) {
                    addUserRoleGroup(obj);
                } else {
                    addObjectToDropParent(obj);
                }
                index++;
            } else {
                BatchDropAllowed retVal = handleOtherObjectDrop(dataBaseList, obj);
                if (null != retVal) {
                    return retVal;
                }
            }
        }

        if (isNoDBFound(dataBaseList, index)) {
            return BatchDropAllowed.NODBFOUND;
        }

        this.database = dataBaseList.get(0);
        this.objectsToDrop = objList;
        this.objectDropTotalCnt = index;
        return BatchDropAllowed.DROPALLOWED;
    }

    private void addObjectToDropParent(Object obj) {
        Object parent = ((ServerObject) obj).getParent();
        objectsToDropParent.add(0, (ServerObject) parent);
    }

    private void addUserRoleGroup(Object obj) {
        Object userRoleparent;
        userRoleparent = ((UserRole) obj).getParent();
        userRoleToDropParent.add(0, (UserRoleObjectGroup) userRoleparent);
    }

    private void initiliazeAllObjectsList() {
        objectsToDropParent = new ArrayList<ServerObject>(1);
        userRoleToDropParent = new ArrayList<UserRoleObjectGroup>(1);
    }

    private BatchDropAllowed handleOtherObjectDrop(List<Database> dataBaseList, Object obj) {
        if (obj instanceof OLAPObjectGroup) {
            Database db = ((OLAPObjectGroup<?>) obj).getDatabase();
            if (dataBaseList.size() >= 1) {
                if (!dataBaseList.get(0).equals(db)) {
                    return BatchDropAllowed.MULTIDBFOUND;
                }
            } else {
                dataBaseList.add(db);
            }
        } else if (obj instanceof OLAPObjectList) {
            Database db = getDatabase(obj);

            if (dataBaseList.size() >= 1) {
                if (!dataBaseList.get(0).equals(db)) {
                    return BatchDropAllowed.MULTIDBFOUND;
                }
            } else {
                dataBaseList.add(db);
            }
        }

        return null;
    }

    private boolean isNoDBFound(List<Database> dataBaseList, int index) {
        return dataBaseList.size() == 0 || index == 0;
    }

    private Database getDatabase(Object obj) {
        Database db = null;
        Object listParent = ((OLAPObjectList<?>) obj).getParent();
        if (listParent instanceof TableMetaData) {
            db = ((TableMetaData) listParent).getDatabase();
        } else if (listParent instanceof ViewMetaData) {
            db = ((ViewMetaData) listParent).getDatabase();
        }
        return db;
    }

    private boolean isMultiDbFound(List<Database> dataBaseList, Object obj, Database db) {
        return !(obj instanceof UserRole) && !dataBaseList.get(0).equals(db);
    }

    private boolean isServerObject(Object obj) {
        return obj instanceof ServerObject && obj instanceof IServerObjectBatchOperations;
    }

    private boolean isDropNotAllowed(Object obj) {
        return obj instanceof Server || obj instanceof Database || obj instanceof DatabaseObjectGroup
                || obj instanceof TablespaceObjectGroup || obj instanceof Tablespace || obj instanceof OLAPObjectGroup;
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
        return;
    }

    private boolean checkAndUpdateState(BatchDropOperState newState) {
        synchronized (LOCK) {
            boolean result = BatchDropOperState.isStateTransitionAllowed(currentState, newState);
            if (result) {
                if (MPPDBIDELoggerUtility.isInfoEnabled()) {
                    MPPDBIDELoggerUtility.info("Batch Drop Window Details" + ':' + currentState + "->" + newState);
                }
                currentState = newState;
            }

            return result;
        }
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        handleEventFirst(event);
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_BATCHDROP_DROP_REVERTED: {
                handleDropRevert();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAIL_REVERTED: {
                handleFailRevert();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAILED: {
                handleDropFailure();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_OP_COMPLETE: {
                handleDropOpComplete();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED: {
                handleGridInputChanged();

                break;
            }

            default: {
                break;
            }
        }
    }

    private void handleEventFirst(DSEvent event) {
        switch (event.getType()) {
            case IDSGridUIListenable.LISTEN_BATCHDROP_ATOMIC_OPTION: {
                ((BatchDropDataProvider) dataProvider).setAtomic(((Button) event.getObject()).getSelection());
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_CASCADE_OPTION: {
                ((BatchDropDataProvider) dataProvider).setCascade(((Button) event.getObject()).getSelection());
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_START_OPTION: {
                handleDropStartEvent();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_STOP_OPTION: {
                handleDropStopEvent();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_JOB_CANCEL: {
                handleJobCancelEvent();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_OP_ROLLBACK: {
                handleRollbackEvent();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_CONN_FAILED: {
                handleConnFailedEvent();
                break;
            }

            case IDSGridUIListenable.LISTEN_BATCHDROP_DROP_SUCCESS: {
                handleDropSuccess();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleGridInputChanged() {
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (null != gridComponent) {
                    gridComponent.setDataProvider(dataProvider);
                }
            }
        });
    }

    private void handleRollbackEvent() {
        if (checkAndUpdateState(BatchDropOperState.ROLLBACK_STOP_CANCEL)) {
            stopOperation(true);
        }
    }

    private void handleJobCancelEvent() {
        if (checkAndUpdateState(BatchDropOperState.ROLLBACK_STOP_CANCEL)) {
            if (null != dataProvider) {
                ((BatchDropDataProvider) dataProvider).setPauseStopOperation(true);
                ((BatchDropDataProvider) dataProvider).setCancelOperation(true);
            }
            stopOperation(true);
        }
    }

    private void handleDropStopEvent() {
        if (checkAndUpdateState(BatchDropOperState.ROLLBACK_STOP_CANCEL)) {
            handleStopButtonOper();
        }
    }

    private void handleDropStartEvent() {
        if (checkAndUpdateState(BatchDropOperState.START)) {
            timer = new ExecTimer(windowDetails.getTitle());
            timer.start();
            handleBatchDropStart();
        }
    }

    private void handleDropOpComplete() {
        if (checkAndUpdateState(BatchDropOperState.FINISHED)) {
            dirtyHandler.setDirty(false);
            Display.getDefault().asyncExec(new Runnable() {

                /**
                 * run
                 */
                public void run() {
                    if (null != gridComponent && null != gridComponent.getToolbar()) {
                        gridComponent.getToolbar().updateBatchDropStartButton(false);
                        gridComponent.getToolbar().updateBatchDropAtomicButton(false);
                        gridComponent.getToolbar().updateBatchDropCascadeButton(false);
                        gridComponent.getToolbar().updateBatchDropStopButton(false);
                    }
                }
            });

            if (this.getTermConnection() != null) {
                this.getTermConnection().releaseConnection();
            }

            try {
                timer.stopAndLog();
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("BatchDropDisplayUIManager: Handle batch drop complete failed.", exception);
            }
        }
    }

    private void handleDropSuccess() {
        if ((objectDropSuccessCnt + objectDropFailureCnt) < objectDropTotalCnt) {
            objectDropSuccessCnt++;
        }

        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (gridComponent != null && gridComponent.getToolbar() != null) {
                    gridComponent.getToolbar().updateBatchDropRunsLabel(objectDropSuccessCnt, objectDropTotalCnt);
                }
            }
        });
    }

    private void handleDropFailure() {
        if ((objectDropSuccessCnt + objectDropFailureCnt) < objectDropTotalCnt) {
            objectDropFailureCnt++;
        }

        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (gridComponent != null && gridComponent.getToolbar() != null) {
                    gridComponent.getToolbar().updateBatchDropErrorsLabel(objectDropFailureCnt);
                }
            }
        });
    }

    private void handleFailRevert() {
        if (objectDropFailureCnt > 0) {
            objectDropFailureCnt--;
        }

        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (gridComponent != null && gridComponent.getToolbar() != null) {
                    gridComponent.getToolbar().updateBatchDropErrorsLabel(objectDropFailureCnt);
                }
            }
        });
    }

    private void handleDropRevert() {
        if (objectDropSuccessCnt > 0) {
            objectDropSuccessCnt--;
        }

        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (gridComponent != null && gridComponent.getToolbar() != null) {
                    gridComponent.getToolbar().updateBatchDropRunsLabel(objectDropSuccessCnt, objectDropTotalCnt);
                }
            }
        });
    }

    /**
     * User cancelled operation.
     */
    public void userCancelledOperation() {
        currentState = BatchDropOperState.INIT;
        gridComponent.updateBatchDropButtons();
    }

    private void handleBatchDropStart() {
        objectDropSuccessCnt = 0;
        objectDropFailureCnt = 0;
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                gridComponent.getToolbar().updateBatchDropRunsLabel(objectDropSuccessCnt, objectDropTotalCnt);
                gridComponent.getToolbar().updateBatchDropErrorsLabel(objectDropFailureCnt);
            }
        });

        ((BatchDropDataProvider) dataProvider).setCancelOperation(false);
        ((BatchDropDataProvider) dataProvider).setPauseStopOperation(false);
        eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_JOB_CANCEL, this);
        String statusEle = this.database.getServerName() + '.' + this.database.getName();

        String jobName = ProgressBarLabelFormatter.getProgressLabelForDatabase(this.database.getName(),
                this.database.getServerName(), IMessagesConstants.DROP_OBJECTS_JOB_NAME);
        if (objectsToDrop.get(0) instanceof UserRole) {
            // As if the first object is userrole, obj.get_database will return
            // an any active database, which may be different from the other
            // type of objects to drop, it will
            // lead other objects(table,view under different database) failed to
            // drop. If Pass the last object to drop,
            // It will make sure to drop all the objects.
            batchDropWorker = new BatchDropWorkerJob(jobName,
                    (ServerObject) objectsToDrop.get(objectsToDrop.size() - 1), "", MPPDBIDEConstants.CANCELABLEJOB,
                    this);
        } else {
            batchDropWorker = new BatchDropWorkerJob(jobName, (ServerObject) objectsToDrop.get(0), "",
                    MPPDBIDEConstants.CANCELABLEJOB, this);
        }

        batchDropWorker.init((BatchDropDataProvider) gridComponent.getDataProvider(), objectsToDropParent,
                userRoleToDropParent, eventTable, statusEle);

        dirtyHandler.setDirty(true);
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                gridComponent.getToolbar().updateBatchDropStartButton(false);
                gridComponent.getToolbar().updateBatchDropAtomicButton(false);
                gridComponent.getToolbar().updateBatchDropCascadeButton(false);
                gridComponent.getToolbar().updateBatchDropStopButton(true);
            }
        });

        batchDropWorker.schedule();
    }

    private void handleStopButtonOper() {
        if (dataProvider == null) {
            return;
        }
        // Pause the worker thread
        ((BatchDropDataProvider) dataProvider).setPauseStopOperation(true);
        int userChoice = stopOperationDialog();
        if (userChoice == 0) {
            stopOperation(false);

            // Set the cancel Flag
            ((BatchDropDataProvider) dataProvider).setCancelOperation(true);

            String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_JOB_STOPPED_STATUS,
                    this.database.getServerName() + '.' + this.database.getName());
            ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
        } else {
            ((BatchDropDataProvider) dataProvider).setPauseStopOperation(false);
            checkAndUpdateState(BatchDropOperState.START);
        }
    }

    /**
     * Handle stop operation.
     *
     * @return the int
     */
    public int handleStopOperation() {
        if (dataProvider == null) {
            return 0;
        }
        if (checkAndUpdateState(BatchDropOperState.ROLLBACK_STOP_CANCEL)) {
            // Pause the worker thread
            ((BatchDropDataProvider) dataProvider).setPauseStopOperation(true);
            int userChoice = stopOperationDialog();
            if (userChoice == 0) {
                stopOperationFromClosingPart();
                // Set the cancel Flag
                ((BatchDropDataProvider) dataProvider).setCancelOperation(true);

                String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_JOB_STOPPED_STATUS,
                        this.database.getServerName() + '.' + this.database.getName());
                ObjectBrowserStatusBarProvider.getStatusBar().displayMessage(Message.getInfo(message));
            } else {
                ((BatchDropDataProvider) dataProvider).setPauseStopOperation(false);
                checkAndUpdateState(BatchDropOperState.START);
            }

            return userChoice;
        }

        return 0;
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {
        return "com.huawei.mppdbide.partstack.id.batchdelete";
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
     * Handle result display failure dialog.
     */
    @Override
    protected void handleResultDisplayFailureDialog() {
        UIElement.getInstance().openMaxSourceViewerDialog();
    }

    /**
     * Reset data result.
     */
    public void resetDataResult() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                if (null != gridComponent) {
                    gridComponent.setDataProvider(BatchDropDisplayUIManager.this.dataProvider);
                }
            }
        });
    }

    /**
     * Creates the result new.
     *
     * @param result the result
     * @param consoledata the consoledata
     * @param queryExecSummary the query exec summary
     */
    @Override
    protected void createResultNew(IDSGridDataProvider result, IConsoleResult consoledata,
            IQueryExecutionSummary queryExecSummary) {
        if (this.isDisposed()) {
            return;
        }

        setResultData(result);
        try {
            result.init();
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("BatchDropDisplayUIManager: Create new result for batch drop failed.",
                    exception);
            return;
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("BatchDropDisplayUIManager: Create new result for batch drop failed.",
                    exception);
            return;
        }

        IWindowDetail windowDet = getWindowDetails();
        BatchDropUIWindow batchDropUIWindow = (BatchDropUIWindow) UIElement.getInstance().getBatchDropWindow(windowDet,
                this);

        // Check if the window is null. This will be due to the number of
        // windows open is more than the threshold.
        if (batchDropUIWindow == null) {
            return;
        }

        BatchDropDisplayUIManager currUiManager = batchDropUIWindow.getUIManager();
        if (null != currUiManager) {
            // If old manager is being used, then reset the new result and
            // summary.
            currUiManager.setResultData(result);
            UIElement.getInstance().bringOnTopViewTableDataWindow(windowDet);
            ((BatchDropDataProvider) currUiManager.dataProvider).setEventTable(eventTable);
        }

    }

    /**
     * Sets the result data.
     *
     * @param result the new result data
     */
    private void setResultData(IDSGridDataProvider result) {
        this.dataProvider = result;
    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    public void showResult(Composite parentComposite) {
        Composite composite = parentComposite;
        this.batchDropUIPref = new BatchDropUIPref();
        this.gridComponent = new DSGridComponent(batchDropUIPref, dataProvider);
        this.gridComponent.createComponents(composite);
        this.gridComponent.getToolbar().updateBatchDropObjectLabel(database.getServerName() + "." + database.getName());
        this.gridComponent.getToolbar().updateBatchDropRunsLabel(objectDropSuccessCnt, objectDropTotalCnt);

        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_BATCHDROP_ATOMIC_OPTION, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_BATCHDROP_CASCADE_OPTION, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_BATCHDROP_START_OPTION, this);
        this.gridComponent.addListener(IDSGridUIListenable.LISTEN_BATCHDROP_STOP_OPTION, this);

        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_CONN_FAILED, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_OP_ROLLBACK, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_SUCCESS, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_REVERTED, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAIL_REVERTED, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_DROP_FAILED, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_OP_COMPLETE, this);
        this.eventTable.hook(IDSGridUIListenable.LISTEN_BATCHDROP_GRID_INPUT_CHANGED, this);
    }

    /**
     * Title: class Description: The Class BatchDropUIPref.
     */
    private static class BatchDropUIPref extends GridUIPreference {

        @Override
        public boolean isShowQueryArea() {
            return false;
        }

        @Override
        public boolean isSupportDataExport() {
            return false;
        }

        @Override
        public boolean isAddBatchDropTool() {
            return true;
        }

        @Override
        public boolean isShowStatusBar() {
            return false;
        }

        @Override
        public boolean isAllowColumnReorder() {
            return false;
        }

        @Override
        public boolean isEnableSort() {
            return true;
        }

        @Override
        public int getColumnWidth() {
            return (getColumnWidthStrategy() == ColumnWidthType.FIXED_WIDTH)
                    ? prefStore.getInt(MPPDBIDEConstants.PREF_COLUMN_WIDTH_LENGTH)
                    : 130;
        }

    }

    /**
     * Gets the window details.
     *
     * @return the window details
     */
    private IWindowDetail getWindowDetails() {
        if (null == this.windowDetails) {
            this.windowDetails = new BatchDropWindowDetails();
        }

        return this.windowDetails;
    }

    /**
     * Sets the disposed.
     */
    @Override
    public void setDisposed() {
        super.setDisposed();
        if (this.getTermConnection() != null) {
            this.getTermConnection().releaseConnection();
        }

        preDestroy();
    }

    /**
     * Title: class Description: The Class BatchDropWindowDetails.
     */
    private class BatchDropWindowDetails implements IWindowDetail {
        private static final String DROP_OBJECTS_WINDOW = "com.huawei.mppdbide.partstack.id.batchdelete";

        private int currentWindowCounter;

        /**
         * Instantiates a new batch drop window details.
         */
        public BatchDropWindowDetails() {
            this.currentWindowCounter = dropWindowCounterId;
            dropWindowCounterId++;
            openedDropWindowsCount++;
        }

        @Override
        public String getTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_WINDOW_TITLE,
                    BatchDropDisplayUIManager.this.getTermConnection().getDatabase().getName() + "@"
                            + BatchDropDisplayUIManager.this.getTermConnection().getDatabase().getServerName(),
                    this.currentWindowCounter);
        }

        @Override
        public String getUniqueID() {
            return DROP_OBJECTS_WINDOW + ' ' + getTitle();
        }

        @Override
        public String getIcon() {
            return IconUtility.getIconImageUri(IiconPath.ICON_DROP_OBJECTS_WINDOW, UIElement.class);
        }

        @Override
        public String getShortTitle() {
            return MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_WINDOW_SHORTTITLE,
                    this.currentWindowCounter);
        }

        @Override
        public boolean isCloseable() {
            return true;
        }
    }

    /**
     * Start UI work.
     */
    public void startUIWork() {
        BatchDropAllowed result = isObjectDropAllowed();
        switch (result) {
            case MULTIDBFOUND: {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_DIALOG_TITLE),
                        MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_DIALOG_MESSAGE));

                return;
            }
            case NODBFOUND: {
                MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                        MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_DIALOG_TITLE),
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_NO_DB_DIALOG_MESSAGE));
                return;
            }
            case DROPNOTALLOWED: {

                MPPDBIDEDialogs.generateDSErrorDialog(
                        MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_DIALOG_TITLE),
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_OBJECT_DIALOG_MESSAGE),
                        MessageConfigLoader
                                .getProperty(IMessagesConstants.DROP_OBJECTS_NOT_ALLOWED_OBJECT_DIALOG_OBJ_LIST),
                        null);
                return;
            }
            default: {
                break;
            }
        }

        windowDetails = new BatchDropWindowDetails();
        getTermConnection().setDatabase(database);
        this.handleResultDisplay(new BatchDropDataProvider(objectsToDrop), null, null);

    }

    /**
     * Sets the dirty handler.
     *
     * @param dirtyHandler the new dirty handler
     */
    public void setDirtyHandler(MDirtyable dirtyHandler) {
        this.dirtyHandler = dirtyHandler;
    }

    private int stopOperationDialog() {
        String title = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STOP_DIALOG_TITLE);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.DROP_OBJECTS_OPER_STOP_DIALOG_MESSAGE);
        return MPPDBIDEDialogs.generateYesNoMessageDialog(MESSAGEDIALOGTYPE.QUESTION, true, title, message);
    }

    private void stopOperationFromClosingPart() {
        if (batchDropWorker != null) {
            batchDropWorker.handleStop();
            batchDropWorker.cancel();
        }
    }

    private void handleConnFailedEvent() {
        if (checkAndUpdateState(BatchDropOperState.INIT)) {
            dirtyHandler.setDirty(false);
            Display.getDefault().asyncExec(new Runnable() {

                /**
                 * run
                 */
                public void run() {
                    if (null == gridComponent) {
                        return;
                    }
                    gridComponent.getToolbar().updateBatchDropStopButton(false);
                    if (null != database) {
                        gridComponent.getToolbar().updateBatchDropAtomicButton(database.hasSupportForAtomicDDL());
                    }
                    gridComponent.getToolbar().updateBatchDropCascadeButton(true);
                    gridComponent.getToolbar().updateBatchDropStartButton(true);
                }
            });
        }
    }

    private void disableStopButton() {
        dirtyHandler.setDirty(false);
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (null != gridComponent && null != gridComponent.getToolbar()) {
                    gridComponent.getToolbar().updateBatchDropStopButton(false);
                }
            }
        });
    }

    private void stopOperation(boolean isRollbackFlow) {
        disableStopButton();

        if (batchDropWorker != null) {
            batchDropWorker.handleStop();

            if (!isRollbackFlow) {
                batchDropWorker.cancel();
            }
        }
    }

    /**
     * Disable buttons.
     */
    public void disableButtons() {
        Display.getDefault().asyncExec(new Runnable() {

            /**
             * run
             */
            public void run() {
                if (!database.isConnected()) {
                    if (null != gridComponent && null != gridComponent.getToolbar()) {
                        gridComponent.getToolbar().updateBatchDropStopButton(false);
                        gridComponent.getToolbar().updateBatchDropAtomicButton(false);
                        gridComponent.getToolbar().updateBatchDropCascadeButton(false);
                        gridComponent.getToolbar().updateBatchDropStartButton(false);
                    }
                }
            }
        });
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
     * Reduce window count.
     */
    public static void reduceWindowCount() {
        openedDropWindowsCount--;
    }

    /**
     * Gets the window count.
     *
     * @return the window count
     */
    public int getWindowCount() {
        return openedDropWindowsCount;
    }

    /**
     * Reset window counter.
     */
    public static void resetWindowCounter() {
        dropWindowCounterId = 1;
    }

    /**
     * Pre destroy.
     */
    @PreDestroy
    public void preDestroy() {
        if (this.gridComponent != null) {
            this.gridComponent.onPreDestroy();
            this.gridComponent = null;
        }

        if (this.eventTable != null) {
            this.eventTable.unhookall();
            this.eventTable = null;
        }

        if (objectsToDrop != null) {
            this.objectsToDrop.clear();
            this.objectsToDrop = null;
        }

        if (this.batchDropWorker != null) {
            this.batchDropWorker.preDestroy();
            this.batchDropWorker = null;
        }

        if (this.consoleMessageWindowDummy != null) {
            this.consoleMessageWindowDummy.preDestroy();
            this.consoleMessageWindowDummy = null;
        }

        if (this.objectsToDropParent != null) {
            this.objectsToDropParent.clear();
            this.objectsToDropParent = null;
        }

        if (this.userRoleToDropParent != null) {
            this.userRoleToDropParent.clear();
            this.userRoleToDropParent = null;
        }

        this.batchDropUIPref = null;
        this.windowDetails = null;
        this.dataProvider = null;
        this.currentState = null;
        this.timer = null;
    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {
        return;
    }
}
