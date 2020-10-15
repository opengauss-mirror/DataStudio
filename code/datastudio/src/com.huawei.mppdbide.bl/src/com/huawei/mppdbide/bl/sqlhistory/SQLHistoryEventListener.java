/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.sqlhistory;

import org.eclipse.e4.core.services.events.IEventBroker;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import com.huawei.mppdbide.bl.sqlhistory.manager.ISqlHistoryManager;

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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
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
