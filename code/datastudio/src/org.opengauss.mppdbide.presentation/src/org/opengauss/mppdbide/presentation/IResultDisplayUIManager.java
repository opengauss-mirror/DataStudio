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

package org.opengauss.mppdbide.presentation;

import java.util.ArrayList;

import org.eclipse.e4.core.services.events.IEventBroker;

import org.opengauss.mppdbide.bl.sqlhistory.IQueryExecutionSummary;
import org.opengauss.mppdbide.presentation.resultsetif.IConsoleResult;
import org.opengauss.mppdbide.utils.exceptions.MPPDBIDEException;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IResultDisplayUIManager.
 *
 * @since 3.0.0
 */
public interface IResultDisplayUIManager {

    /**
     * Handle pre execution UI display setup.
     *
     * @param terminalExecutionConnectionInfra the terminal execution connection
     * infra
     * @param isActivateStatusBar the is activate status bar
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    void handlePreExecutionUIDisplaySetup(TerminalExecutionConnectionInfra terminalExecutionConnectionInfra,
            boolean isActivateStatusBar) throws MPPDBIDEException;

    /**
     * Handle result display.
     *
     * @param result the result
     * @param consoleData the console data
     * @param queryExecSummary the query exec summary
     */
    void handleResultDisplay(Object result, IConsoleResult consoleData, IQueryExecutionSummary queryExecSummary);

    /**
     * Handle result display.
     *
     * @param result the result
     */
    void handleResultDisplay(Object result);

    /**
     * Handle console display.
     *
     * @param consoleData the console data
     */
    void handleConsoleDisplay(IConsoleResult consoleData);

    /**
     * Handle exception display.
     *
     * @param e the e
     */
    void handleExceptionDisplay(Object e);

    /**
     * Handle step completion.
     */
    void handleStepCompletion();

    /**
     * Handle successfull completion.
     */
    void handleSuccessfullCompletion();

    /**
     * Handle final cleanup.
     */
    void handleFinalCleanup();

    /**
     * Gets the event broker.
     *
     * @return the event broker
     */
    IEventBroker getEventBroker();

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    TerminalExecutionConnectionInfra getTermConnection();

    /**
     * Can context execution continue.
     *
     * @return the can context continue execute rule
     */
    CanContextContinueExecuteRule canContextExecutionContinue();

    /**
     * Reset display UI manager.
     */
    void resetDisplayUIManager();

    /**
     * Inits the display manager.
     *
     * @param execType the exec type
     */
    void initDisplayManager(ContextExecutionOperationType execType);

    /**
     * Handle cancel request.
     */
    void handleCancelRequest();

    /**
     * Gets the single query array.
     *
     * @param queryArray the query array
     * @param query the query
     * @return the single query array
     */
    void getSingleQueryArray(ArrayList<String> queryArray, String query);

    /**
     * Sets the cursor offset.
     *
     * @param offset the new cursor offset
     */
    void setCursorOffset(int offset);

    /**
     * Handle pre execution UI display setup critical.
     *
     * @param termConn the term conn
     * @param isCriticalErr the is critical err
     */
    void handlePreExecutionUIDisplaySetupCritical(TerminalExecutionConnectionInfra termConn, boolean isCriticalErr);

    /**
     * Handle grid component on dialog cancel.
     */
    void handleGridComponentOnDialogCancel();

}
