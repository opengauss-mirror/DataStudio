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

package com.huawei.mppdbide.view.ui.terminal.resulttab;

import java.util.ArrayList;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Display;

import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import com.huawei.mppdbide.presentation.TerminalExecutionConnectionInfra;
import com.huawei.mppdbide.presentation.grid.IDSGridDataProvider;
import com.huawei.mppdbide.presentation.resultsetif.IConsoleResult;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.view.core.ConsoleMessageWindow;
import com.huawei.mppdbide.view.ui.terminal.AbstractResultDisplayUIManager;
import com.huawei.mppdbide.view.uidisplay.UIDisplayFactoryProvider;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * 
 * Title: class
 * 
 * Description: The Class ResultTabResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public class ResultTabResultDisplayUIManager extends AbstractResultDisplayUIManager {

    /**
     * The tab manager.
     */
    protected ResultTabManager tabManager;

    /**
     * The result tab.
     */
    protected ResultTab resultTab;

    /**
     * Instantiates a new result tab result display UI manager.
     *
     * @param tabMgr the tab mgr
     * @param rTab the r tab
     */
    public ResultTabResultDisplayUIManager(ResultTabManager tabMgr, ResultTab rTab) {
        super(tabMgr.getTermConnection());
        this.tabManager = tabMgr;
        this.resultTab = rTab;
    }

    /**
     * Handle exception display.
     *
     * @param object the object
     */
    @Override
    public void handleExceptionDisplay(Object object) {
        this.resultTab.reloadDataFailureHandle();
        if (object instanceof DatabaseCriticalException) {
            DatabaseCriticalException databaseCriticalException = (DatabaseCriticalException) object;

            UIDisplayFactoryProvider.getUIDisplayStateIf().handleDBCriticalError(databaseCriticalException,
                    this.resultTab.getParentTabManager().getTermConnection().getDatabase());
            resultTabConsoleLogExecutionFailure(databaseCriticalException);
        } else if (object instanceof DatabaseOperationException) {
            resultTabConsoleLogExecutionFailure((DatabaseOperationException) object);
        } else if (object instanceof OutOfMemoryError) {
            OutOfMemoryError ome = (OutOfMemoryError) object;
            this.tabManager.getConsoleMessageWindow(true).logInfo(ome.getMessage());
        }
    }

    private void resultTabConsoleLogExecutionFailure(final MPPDBIDEException exception) {
        ConsoleMessageWindow consoleWindow = this.tabManager.getConsoleMessageWindow(true);
        String message = MessageConfigLoader.getProperty(IMessagesConstants.EXECUTION_FAILED_ITEM,
                MPPDBIDEConstants.LINE_SEPARATOR, exception.getErrorCode(),
                exception.getServerMessage() == null ? exception.getDBErrorMessage() : exception.getServerMessage());

        if (exception.getServerMessage() != null
                && exception.getServerMessage().contains("canceling statement due to user request")) {
            if (null != consoleWindow) {
                consoleWindow.logInfo(MessageConfigLoader.getProperty(IMessagesConstants.SQL_QUREY_CANCEL_MSG));
            }
        } else {
            if (null != consoleWindow) {
                consoleWindow.logError(message);
            }
        }

        MPPDBIDELoggerUtility.error("Log  messages in console window");
    }

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    @Override
    public IEventBroker getEventBroker() {
        return this.tabManager.getEventBroker();
    }

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    @Override
    public void handleResultDisplay(Object result, IConsoleResult consoleData,
            IQueryExecutionSummary queryExecSummary) {
        if (result instanceof IDSGridDataProvider && !resultTab.isDisposed()) {
            this.resultTab.resetData((IDSGridDataProvider) result, consoleData, queryExecSummary);
        } else {
            this.handleConsoleOnlyResultDisplay(consoleData);
        }
    }

    /**
     * Handle grid component on dialog cancel.
     */
    @Override
    public void handleGridComponentOnDialogCancel() {
        this.resultTab.cancelFlow();
    }

    /**
     * Handle console only result display.
     *
     * @param consoleDisplayData the console display data
     */
    protected void handleConsoleOnlyResultDisplay(final IConsoleResult consoleDisplayData) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                ResultTabResultDisplayUIManager.this.handleConsoleDisplay(consoleDisplayData);
            }
        });
    }

    /**
     * Handle console display.
     *
     * @param consoleData the console data
     */
    @Override
    public void handleConsoleDisplay(IConsoleResult consoleData) {
        ConsoleMessageWindow consoleWindow = ResultTabResultDisplayUIManager.this.tabManager
                .getConsoleMessageWindow(true);
        consoleWindow.logInfo(consoleData);
    }

    /**
     * Gets the part ID.
     *
     * @return the part ID
     */
    @Override
    protected String getPartID() {
        return this.tabManager.getPartID();
    }

    /**
     * Gets the console message window.
     *
     * @param bringOnTop the bring on top
     * @return the console message window
     */
    @Override
    protected ConsoleMessageWindow getConsoleMessageWindow(boolean bringOnTop) {
        return ResultTabResultDisplayUIManager.this.tabManager.getConsoleMessageWindow(bringOnTop);
    }

    /**
     * Can dislay result.
     *
     * @return true, if successful
     */
    @Override
    protected boolean canDislayResult() {
        return UIElement.getInstance().isMoreResultWindowAllowed();
    }

    /**
     * Creates the result new.
     *
     * @param resultsetDisplaydata the resultset displaydata
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    @Override
    protected void createResultNew(IDSGridDataProvider resultsetDisplaydata, IConsoleResult consoleData,
            IQueryExecutionSummary queryExecSummary) {
        this.resultTab.resetData(resultsetDisplaydata, consoleData, queryExecSummary);
    }

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        return null;
    }

    /**
     * Reset display UI manager.
     */
    @Override
    public void resetDisplayUIManager() {
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
}
