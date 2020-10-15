/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.objectpropertywiew;

import java.util.ArrayList;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.swt.widgets.Composite;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.ITableMetaData;
import com.huawei.mppdbide.bl.serverdatacache.TableMetaData;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.IWindowDetail;
import com.huawei.mppdbide.presentation.grid.IDSEditGridDataProvider;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.objectproperties.handler.IPropertyDetail;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.messaging.ProgressBarLabelFormatter;
import com.huawei.mppdbide.utils.messaging.StatusMessage;
import com.huawei.mppdbide.utils.messaging.StatusMessageList;
import com.huawei.mppdbide.utils.observer.DSEvent;
import com.huawei.mppdbide.utils.observer.IDSGridUIListenable;
import com.huawei.mppdbide.utils.observer.IDSListener;
import com.huawei.mppdbide.view.component.grid.CommitRecordEventData;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.core.ConsoleMessageWindowDummy;
import com.huawei.mppdbide.view.core.sourceeditor.templates.StatusInfo;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;
import com.huawei.mppdbide.view.utils.BottomStatusBar;
import com.huawei.mppdbide.view.utils.UIElement;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs;
import com.huawei.mppdbide.view.utils.dialog.MPPDBIDEDialogs.MESSAGEDIALOGTYPE;
import com.huawei.mppdbide.view.utils.icon.IconUtility;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewObjectPropertiesResultDisplayUIManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewObjectPropertiesResultDisplayUIManager extends AbstractResultDisplayUIManager implements IDSListener {
    private IPropertyDetail propDetails;

    /**
     * The property core.
     */
    protected PropertyHandlerCore propertyCore;

    /**
     * The tab manager.
     */
    protected ViewObjectPropertyTabManager tabManager;
    private ConsoleMessageWindowDummy consoleMessageWindowDummy;

    /**
     * The dirty handler.
     */
    protected MDirtyable dirtyHandler;
    private MPart mPart;

    /**
     * Instantiates a new view object properties result display UI manager.
     *
     * @param core the core
     */
    public ViewObjectPropertiesResultDisplayUIManager(PropertyHandlerCore core) {
        super(core.getTermConnection());
        this.propertyCore = core;
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
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {

        return null;
    }

    /**
     * Gets the m part.
     *
     * @return the m part
     */
    public MPart getmPart() {
        return mPart;
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
        return true;
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(DSEvent event) {
        switch (event.getType()) {

            case IDSGridUIListenable.LISTEN_TYPE_GRID_DATA_EDITED: {
                dirtyHandler.setDirty(tabManager.isAnyTabEdited());
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_PROPERITES_COMMIT_DATA:
            case IDSGridUIListenable.LISTEN_EDITTABLE_COMMIT_DATA: {
                handleCommitData(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_DATABASE_CONNECT_DISCONNECT_STATUS: {
                boolean isConnected = false;
                if (propertyCore.getTermConnection().getDatabase() != null) {
                    isConnected = propertyCore.getTermConnection().getDatabase().isConnected();
                }
                tabManager.handleToolbarIcons(isConnected);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_REFRESH_QUERY: {
                refreshPropertiesEventCall(event);
                break;
            }
            case IDSGridUIListenable.LISTEN_TYPE_ON_CANCEL_PASSWORD: {
                tabManager.handleToolbarIcons(true);
                break;
            }
            default: {
                break;
            }
        }
    }

    /**
     * Handle commit data.
     *
     * @param event the event
     */
    protected void handleCommitData(DSEvent event) {
        CommitRecordEventData commitData = (CommitRecordEventData) event.getObject();
        // When the table is dropped from OB and user commit the data
        // from any of the tab
        TableMetaData table = (TableMetaData) commitData.getDataProvider().getTable();
        if (!isTableExist(table)) {
            return;
        }
        StatusMessage statusMessage = new StatusMessage("StatusMessage");
        BottomStatusBar bottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (bottomStatusBar != null) {
            bottomStatusBar.setStatusMessage("bottomStatusBar");
        }
        String progressLabel = ProgressBarLabelFormatter.getProgressLabelForTableWithMsg(table.getName(),
                table.getNamespace().getName(), table.getDatabaseName(), table.getServerName(),
                IMessagesConstants.EDIT_TABLE_COMMIT_PROGRESS_NAME);
        PropertiesCommitWorker worker = new PropertiesCommitWorker(progressLabel, "", commitData.getDataGrid(),
                commitData.getDataProvider(), commitData.getEventTable(), bottomStatusBar, statusMessage);

        StatusMessageList.getInstance().push(statusMessage);
        if (bottomStatusBar != null) {
            bottomStatusBar.activateStatusbar();
        }
        worker.schedule();
    }

    /**
     * Refresh properties event call.
     *
     * @param event the event
     */
    protected void refreshPropertiesEventCall(DSEvent event) {
        IDSEditGridDataProvider object = (IDSEditGridDataProvider) event.getObject();
        // when table is dropped from OB and user refresh the properties
        // tab
        if (!isTableExist((TableMetaData) object.getTable())) {
            return;
        }
        StatusMessage refreshStatusMessage = new StatusMessage(
                MessageConfigLoader.getProperty(IMessagesConstants.REFRESH_IN_PROGRESS));
        BottomStatusBar refreshBottomStatusBar = UIElement.getInstance().getProgressBarOnTop();
        if (null != refreshBottomStatusBar) {
            refreshBottomStatusBar.setStatusMessage(refreshStatusMessage.getMessage());
        }
        DBConnection connection = propertyCore.getTermConnection().getDatabase().getConnectionManager()
                .getSqlTerminalConn();
        PropertiesTabRefreshWorker tabRefreshWorker = new PropertiesTabRefreshWorker("", "'", object,
                refreshStatusMessage, refreshBottomStatusBar, connection, this.tabManager, new PropertiesStatus(this));
        StatusMessageList.getInstance().push(refreshStatusMessage);
        if (null != refreshBottomStatusBar) {
            refreshBottomStatusBar.activateStatusbar();
        }
        tabRefreshWorker.schedule();
    }

    /**
     * Checks if is table exist.
     *
     * @param table the table
     * @return true, if is table exist
     */
    public boolean isTableExist(ITableMetaData table) {
        if (null == table) {
            return false;
        } else if (table.isDropped()) {

            MPPDBIDEDialogs.generateMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    IconUtility.getIconImage(IiconPath.ICO_TOOL_32X32, this.getClass()),
                    MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WINDOW_ERROR_POPUP_HEADER),
                    MessageConfigLoader
                            .getProperty(IMessagesConstants.EDIT_TABLE_PROPERTIES_DATA_DROPPED_REFRESH_ERROR),
                    new String[] {MessageConfigLoader.getProperty(IMessagesConstants.BTN_OK)});
            return false;
        }
        return true;
    }

    /**
     * Creates the result new.
     *
     * @param result the result
     */
    @Override
    protected void createResultNew(Object result) {
        setResultData((IPropertyDetail) result);

        IWindowDetail windowDetails = getPropertyCore().getWindowDetails();
        PropertiesWindow viewTableDataWindow = (PropertiesWindow) UIElement.getInstance()
                .getViewObjectPartitionWindow(windowDetails, this);

        // Check if the window is null. This will be due to the number of
        // windows open is more than the threshold.
        if (viewTableDataWindow == null) {
            return;
        }

        if (null != viewTableDataWindow.getResultDisplayUIManager()) {
            // If old manager is being used, then reset the property detail.
            viewTableDataWindow.getResultDisplayUIManager().setResultData(getPropDetails());
            viewTableDataWindow.resetData();
            UIElement.getInstance().bringOnTopViewTableDataWindow(windowDetails);
        }

    }

    /**
     * Sets the result data.
     *
     * @param propertyDetails the new result data
     */
    public void setResultData(IPropertyDetail propertyDetails) {
        this.setPropDetails(propertyDetails);
        setPropertyCore(propertyDetails.getPropertyCore());
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
        // nothing to handle here.
        // Not expected to come here
    }

    /**
     * Show result.
     *
     * @param parentComposite the parent composite
     */
    public void showResult(Composite parentComposite) {
        if (null == tabManager) {
            tabManager = new ViewObjectPropertyTabManager(parentComposite, this);
        }

        tabManager.createResult(getPropDetails());

    }

    /**
     * Reset data result.
     */
    public void resetDataResult() {
        this.tabManager.resetResult(getPropDetails());
    }

    /**
     * Handle exception display.
     *
     * @param object the object
     */
    @Override
    public void handleExceptionDisplay(Object object) {
        MPPDBIDEException exception = (MPPDBIDEException) object;

        String msg = exception.getServerMessage();
        if (null == msg) {
            msg = exception.getDBErrorMessage() == null ? exception.getMessage() : exception.getDBErrorMessage();
        }

        if (!msg.contains(MessageConfigLoader.getProperty(IMessagesConstants.ERR_BL_SERVER_CONNECTION_FAILED))) {
            MPPDBIDEDialogs.generateOKMessageDialog(MESSAGEDIALOGTYPE.ERROR, true,
                    MessageConfigLoader.getProperty(IMessagesConstants.PROP_HANDLER_PROPERTIES_ERROR),
                    MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WARNING, msg));
        }

    }

    /**
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {

    }

    /**
     * Gets the property core.
     *
     * @return the property core
     */
    public PropertyHandlerCore getPropertyCore() {
        return propertyCore;
    }

    /**
     * Sets the property core.
     *
     * @param propertyCore the new property core
     */
    public void setPropertyCore(PropertyHandlerCore propertyCore) {
        this.propertyCore = propertyCore;
    }

    /**
     * Gets the prop details.
     *
     * @return the prop details
     */
    public IPropertyDetail getPropDetails() {
        return propDetails;
    }

    /**
     * Sets the prop details.
     *
     * @param propDetails the new prop details
     */
    public void setPropDetails(IPropertyDetail propDetails) {
        this.propDetails = propDetails;
    }

    /**
     * Sets the dirty handler.
     *
     * @param dirtyHandler the new dirty handler
     */
    public void setDirtyHandler(MDirtyable dirtyHandler) {
        this.dirtyHandler = dirtyHandler;
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
     * Sets the part.
     *
     * @param part the new part
     */
    public void setPart(MPart part) {
        this.mPart = part;

    }

    /**
     * Update window part.
     */
    public void updateWindowPart() {

        MPart oldPart = this.mPart;
        String oldLabel = oldPart.getLabel();
        String oldID = oldPart.getElementId();
        IWindowDetail windowDetails = propertyCore.getWindowDetails();
        String newLabel = (windowDetails != null) ? windowDetails.getTitle() : "";
        String newID = (windowDetails != null) ? windowDetails.getUniqueID() : "";
        if (!newLabel.equals(oldLabel)) {
            oldPart.setLabel(newLabel);
            oldPart.setTooltip(newLabel);
        }
        if (oldID != null && !oldID.equals(newID)) {
            oldPart.setElementId(newID);

        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class PropertiesStatus.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static final class PropertiesStatus extends StatusInfo {

        private ViewObjectPropertiesResultDisplayUIManager uiManager;

        /**
         * Instantiates a new properties status.
         *
         * @param uiManager the ui manager
         */
        public PropertiesStatus(ViewObjectPropertiesResultDisplayUIManager uiManager) {
            this.uiManager = uiManager;
        }

        @Override
        public void setOK() {
            uiManager.updateWindowPart();
        }
    }

    /**
     * Gets the update Z edit properties status.
     *
     * @return the update Z edit properties status
     */
    public IDSListener getUpdateZEditPropertiesStatus() {

        return null;
    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {

    }
}
