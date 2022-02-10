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

package org.opengauss.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;

import org.opengauss.mppdbide.bl.serverdatacache.DefaultParameter;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.IViewTableDataCore;
import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewTableDataExecutionContext.
 *
 * @since 3.0.0
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
