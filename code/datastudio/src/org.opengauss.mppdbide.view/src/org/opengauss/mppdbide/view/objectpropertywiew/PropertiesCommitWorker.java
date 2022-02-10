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

package org.opengauss.mppdbide.view.objectpropertywiew;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.TableMetaData;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.presentation.edittabledata.CommitStatus;
import org.opengauss.mppdbide.presentation.edittabledata.EditTableRecordStates;
import org.opengauss.mppdbide.presentation.edittabledata.IDSGridEditDataRow;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataProvider;
import org.opengauss.mppdbide.presentation.grid.IDSGridDataRow;
import org.opengauss.mppdbide.presentation.objectproperties.DSObjectPropertiesGridDataProvider;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.messaging.StatusMessage;
import org.opengauss.mppdbide.utils.observer.DSEvent;
import org.opengauss.mppdbide.utils.observer.DSEventTable;
import org.opengauss.mppdbide.utils.observer.IDSGridUIListenable;
import org.opengauss.mppdbide.view.component.grid.core.DataGrid;
import org.opengauss.mppdbide.view.ui.ObjectBrowser;
import org.opengauss.mppdbide.view.utils.BottomStatusBar;
import org.opengauss.mppdbide.view.utils.UIElement;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import org.opengauss.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;

import ca.odell.glazedlists.EventList;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesCommitWorker.
 *
 * @since 3.0.0
 */
public class PropertiesCommitWorker extends AbstarctPropertiesWorker {
    private DataGrid grid;
    private DSEventTable eventTable;
    private BottomStatusBar bottomStatusBar;
    private StatusMessage statusMessage;
    private CommitStatus commitStatus;

    /**
     * Instantiates a new properties commit worker.
     *
     * @param name the name
     * @param family the family
     * @param grid the grid
     * @param dataProvider the data provider
     * @param eventTable the event table
     * @param bottomStatusBar the bottom status bar
     * @param statusMessage the status message
     */
    public PropertiesCommitWorker(String name, Object family, DataGrid grid, IDSGridDataProvider dataProvider,
            DSEventTable eventTable, BottomStatusBar bottomStatusBar, StatusMessage statusMessage) {
        super(name, MPPDBIDEConstants.CANCELABLEJOB, IMessagesConstants.PROP_HANDLER_PROPERTIES_ERROR, dataProvider);
        this.grid = grid;
        this.eventTable = eventTable;
        this.bottomStatusBar = bottomStatusBar;
        this.statusMessage = statusMessage;

    }

    /**
     * Pre UI setup.
     *
     * @param preHandlerObject the pre handler object
     * @return true, if successful
     */
    @Override
    public boolean preUISetup(Object preHandlerObject) {
        boolean preUISetup = super.preUISetup(preHandlerObject);
        if (!preUISetup) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_ON_CANCEL_PASSWORD, null));
        }
        return preUISetup;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    protected Database getDatabase() {

        TableMetaData table = (TableMetaData) dataProvider.getTable();
        if (null != table) {

            return table.getDatabase();
        }
        return null;
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
        Database db = getDatabase();
        setServerPwd(null != db && db.getServer().getSavePrdOption().equals(SavePrdOptions.DO_NOT_SAVE));
        commitStatus = ((DSObjectPropertiesGridDataProvider) dataProvider).commit();

        return null;
    }

    /**
     * On success UI action.
     *
     * @param obj the obj
     */
    @Override
    public void onSuccessUIAction(Object obj) {
        ObjectBrowser objectBrowserModel = UIElement.getInstance().getObjectBrowserModel();
        if (objectBrowserModel != null) {
            objectBrowserModel.refreshObject(dataProvider.getTable());
        }
        EventList<IDSGridDataRow> listOfRows = this.grid.getListOfRows();
        int index = 0;
        Integer rowIndex = null;
        IDSGridDataRow value = null;
        Map<Integer, IDSGridDataRow> failedInsertsMap = new TreeMap<Integer, IDSGridDataRow>();
        if (commitStatus != null && null != commitStatus.getListOfFailureRows()) {
            List<IDSGridEditDataRow> listOfFailureRows = commitStatus.getListOfFailureRows();
            for (IDSGridEditDataRow failureRow : listOfFailureRows) {
                if (failureRow.getUpdatedState() == EditTableRecordStates.INSERT) {
                    index = listOfRows.indexOf(failureRow);
                    failedInsertsMap.put(index, failureRow);
                }
            }

        }
        listOfRows.clear();
        List<IDSGridDataRow> allFetchedRows = dataProvider.getAllFetchedRows();
        listOfRows.addAll(allFetchedRows);

        if (!listOfRows.isEmpty()) {
            for (Map.Entry<Integer, IDSGridDataRow> entry : failedInsertsMap.entrySet()) {
                rowIndex = entry.getKey();
                value = entry.getValue();
                listOfRows.add(rowIndex, value);

            }

        } else {
            listOfRows.addAll(failedInsertsMap.values());
        }

        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED, dataProvider));
        if (null != commitStatus && null != commitStatus.getListOfFailureRows()
                && commitStatus.getListOfFailureRows().size() > 0) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                    MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR));

        }
    }

    /**
     * Final cleanup.
     */
    @Override
    public void finalCleanup() {
        super.finalCleanup();
        statusMessage = null;
        ((DSObjectPropertiesGridDataProvider) dataProvider).releaseConnection();
    }

    /**
     * Final cleanup UI.
     */
    @Override
    public void finalCleanupUI() {
        if (bottomStatusBar != null) {
            bottomStatusBar.hideStatusbar(statusMessage);
        }
        if (eventTable != null) {
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA_COMPLETE, null));
            eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
        }

    }

    /**
     * Exception event call.
     *
     * @param exception the e
     */
    protected void exceptionEventCall(MPPDBIDEException exception) {
        MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                MessageConfigLoader.getProperty(IMessagesConstants.QUERY_EXECUTION_FAILURE_ERROR_TITLE),
                exception.getServerMessage());
        eventTable.sendEvent(new DSEvent(IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_STATUS, dataProvider));
    }

}
