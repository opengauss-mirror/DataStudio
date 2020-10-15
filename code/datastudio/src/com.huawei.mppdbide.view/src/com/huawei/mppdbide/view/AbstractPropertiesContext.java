/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view;

import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.objectproperties.handler.PropertyHandlerCore;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import com.huawei.mppdbide.utils.messaging.MessageQueue;
import com.huawei.mppdbide.view.terminal.executioncontext.AbstractTerminalExecutionContext;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractPropertiesContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractPropertiesContext extends AbstractTerminalExecutionContext {

    /**
     * The core.
     */
    protected PropertyHandlerCore core;

    /**
     * Instantiates a new abstract properties context.
     *
     * @param core2 the core 2
     */
    protected AbstractPropertiesContext(PropertyHandlerCore core2) {
        super(core2.getTermConnection());
        this.core = core2;
    }

    /**
     * Gets the property handler core.
     *
     * @return the property handler core
     */
    public PropertyHandlerCore getPropertyHandlerCore() {
        return this.core;
    }

    /**
     * Can free connection after use.
     *
     * @return true, if successful
     */
    @Override
    public boolean canFreeConnectionAfterUse() {
        return true;
    }

    /**
     * Need query parse and split.
     *
     * @return true, if successful
     */
    @Override
    public boolean needQueryParseAndSplit() {
        return false;
    }

    /**
     * Sets the critical error thrown.
     *
     * @param value the new critical error thrown
     */
    @Override
    public void setCriticalErrorThrown(boolean value) {
        MPPDBIDELoggerUtility.none(Boolean.toString(value));
    }

    /**
     * Gets the query.
     *
     * @return the query
     */
    @Override
    public String getQuery() {
        return null;
    }

    /**
     * Sets the working job context.
     *
     * @param jobContext the new working job context
     */
    @Override
    public void setWorkingJobContext(Object jobContext) {
        MPPDBIDELoggerUtility.none(jobContext.toString());
    }

    /**
     * Gets the notice message queue.
     *
     * @return the notice message queue
     */
    @Override
    public MessageQueue getNoticeMessageQueue() {
        return null;
    }

    /**
     * Gets the connection profile ID.
     *
     * @return the connection profile ID
     */
    @Override
    public String getConnectionProfileID() {
        return null;
    }

    /**
     * Handle successfull completion.
     */
    @Override
    public void handleSuccessfullCompletion() {
        MPPDBIDELoggerUtility.none("empty msg");

    }

    /**
     * Sets the job done.
     */
    @Override
    public void setJobDone() {

    }

    /**
     * Job type.
     *
     * @return the string
     */
    @Override
    public String jobType() {
        return MPPDBIDEConstants.CANCELABLEJOB;
    }

    /**
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public abstract String getContextName();

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public abstract IResultConfig getResultConfig();

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public abstract IResultDisplayUIManager getResultDisplayUIManager();
}
