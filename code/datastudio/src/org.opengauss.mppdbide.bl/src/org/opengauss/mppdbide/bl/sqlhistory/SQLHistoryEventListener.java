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

package org.opengauss.mppdbide.bl.sqlhistory;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import org.opengauss.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;

/**
 * Title: SQLHistoryEventListener
 * 
 * Description:The listener interface for receiving SQLHistoryEvent events. The
 * class that is interested in processing a SQLHistoryEvent event implements
 * this interface, and the object created with that class is registered with a
 * component using the component's <code>addSQLHistoryEventListener<code>
 * method. When the SQLHistoryEvent event occurs, that object's appropriate
 * method is invoked.
 * 
 */

public class SQLHistoryEventListener implements EventHandler {

    /**
     * The Constant QUERY_EXEC_RESULT.
     */
    public static final String QUERY_EXEC_RESULT = "query_execution_result";

    /**
     * Inits the.
     *
     * @param eventBroker the event broker
     */
    public void init(IEventBroker eventBroker) {
        eventBroker.subscribe(QUERY_EXEC_RESULT, this);
    }

    /**
     * Handle event.
     *
     * @param eventQueryExecutionResult the eventQueryExecutionResult
     */
    @Override
    public void handleEvent(Event eventQueryExecutionResult) {
        if (QUERY_EXEC_RESULT.equals(eventQueryExecutionResult.getTopic())) {
            QueryExecutionSummary summary = (QueryExecutionSummary) eventQueryExecutionResult
                    .getProperty(IEventBroker.DATA);
            if (summary != null) {
                ISqlHistoryManager histmgr = SQLHistoryManager.getInstance();
                histmgr.addNewQueryExecutionInfo(summary.getProfileId(), summary);

            }
        }
    }
}
