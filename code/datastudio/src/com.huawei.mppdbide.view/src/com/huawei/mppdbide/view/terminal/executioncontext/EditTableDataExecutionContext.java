/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.terminal.executioncontext;

import java.util.ArrayList;

import com.huawei.mppdbide.bl.serverdatacache.DefaultParameter;
import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.presentation.IEditTableDataCore;
import com.huawei.mppdbide.presentation.IResultDisplayUIManager;
import com.huawei.mppdbide.presentation.resultset.ActionAfterResultFetch;
import com.huawei.mppdbide.presentation.resultsetif.IResultConfig;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.view.core.edittabledata.AbstractEditTableDataResultDisplayUIManager;
import com.huawei.mppdbide.view.utils.UserPreference;

/**
 * 
 * Title: class
 * 
 * Description: The Class EditTableDataExecutionContext.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
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
