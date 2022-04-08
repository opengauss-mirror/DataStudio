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
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.presentation.IEditTableDataCore;
import org.opengauss.mppdbide.presentation.IResultDisplayUIManager;
import org.opengauss.mppdbide.presentation.resultset.ActionAfterResultFetch;
import org.opengauss.mppdbide.presentation.resultsetif.IResultConfig;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager;
import org.opengauss.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataExecutionContext.
 *
 * @since 3.0.0
 */
public class EditTableDataExecutionContext extends AbstractTerminalExecutionContext {
    private IResultConfig config;
    private AbstractEditTableDataResultDisplayUIManager uiManager;
    private IEditTableDataCore core;
    private ServerObject servObject;

    /**
     * Instantiates a new edits the table data execution context.
     *
     * @param core the core
     * @param uiManager the ui manager
     * @param selTable the sel table
     */
    public EditTableDataExecutionContext(IEditTableDataCore core, AbstractEditTableDataResultDisplayUIManager uiManager,
            ServerObject selTable) {
        super(core.getTermConnection());
        this.servObject = selTable;
        this.config = new EditTableResultConfig();
        this.core = core;
        this.uiManager = uiManager;
        setQuery(core.getQuery());
    }

    /**
     * Instantiates a new edits the table data execution context.
     *
     * @param core the core
     * @param abstractEditTableDataResultDisplayUIManager the abstract edit
     * table data result display UI manager
     * @param selTable the sel table
     * @param resultConfig the result config
     */
    public EditTableDataExecutionContext(IEditTableDataCore core,
            AbstractEditTableDataResultDisplayUIManager abstractEditTableDataResultDisplayUIManager,
            ServerObject selTable, IResultConfig resultConfig) {
        super(core.getTermConnection());
        this.servObject = selTable;
        this.config = resultConfig;
        this.core = core;
        this.uiManager = abstractEditTableDataResultDisplayUIManager;
        setQuery(core.getQuery());
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
        return this.config;
    }

    /**
     * Gets the result display UI manager.
     *
     * @return the result display UI manager
     */
    @Override
    public IResultDisplayUIManager getResultDisplayUIManager() {
        return this.uiManager;
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
     * 
     * Title: class
     * 
     * Description: The Class EditTableResultConfig.
     */
    private static class EditTableResultConfig implements IResultConfig {
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
     * Gets the current server object.
     *
     * @return the current server object
     */
    @Override
    public ServerObject getCurrentServerObject() {
        return servObject;
    }

    /**
     * gets the input values
     */
    @Override
    public ArrayList<DefaultParameter> getInputValues() {
        return null;
    }

}
