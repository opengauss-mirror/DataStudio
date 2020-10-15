/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.IViewTableDataCore;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableDataExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ViewTableDataExecutionContext extends AbstractTerminalExecutionContext {
    private IViewTableDataCore core;
    private IResultConfig resultConfig;
    private IResultDisplayUIManager resultUIManager;

    /**
     * Instantiates a new view table data execution context.
     *
     * @param viewTableDataCore the view table data core
     * @param resultUIManager the result UI manager
     * @throws DatabaseOperationException 
     */
    public ViewTableDataExecutionContext(IViewTableDataCore viewTableDataCore,
            IResultDisplayUIManager resultUIManager) throws DatabaseOperationException {
        super(resultUIManager.getTermConnection());
        setQuery(viewTableDataCore.getQuery());
        this.core = viewTableDataCore;
        this.resultConfig = new ViewTableResultConfig();
        this.resultUIManager = resultUIManager;
    }

    /**
     * Instantiates a new view table data execution context.
     *
     * @param viewTableDataCore the view table data core
     * @param resultUIManager the result UI manager
     * @param resultConfig the result config
     * @throws DatabaseOperationException 
     */
    public ViewTableDataExecutionContext(IViewTableDataCore viewTableDataCore, IResultDisplayUIManager resultUIManager,
            IResultConfig resultConfig) throws DatabaseOperationException {
        super(resultUIManager.getTermConnection());
        setQuery(viewTableDataCore.getQuery());
        this.core = viewTableDataCore;
        this.resultConfig = resultConfig;
        this.resultUIManager = resultUIManager;
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
     * Gets the context name.
     *
     * @return the context name
     */
    @Override
    public String getContextName() {
        return core.getProgressBarLabel();
    }

    /**
     * Gets the result config.
     *
     * @return the result config
     */
    @Override
    public IResultConfig getResultConfig() {
        return this.resultConfig;
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ViewTableResultConfig.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private static class ViewTableResultConfig implements IResultConfig {
        @Override
        public int getFetchCount() {
            return UserPreference.getInstance().getResultDataFetchCount();
        }

        @Override
        public ActionAfterResultFetch getActionAfterFetch() {
            return ActionAfterResultFetch.CLOSE_CONNECTION_AFTER_FETCH;
        }
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return resultUIManager;
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
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }

}
