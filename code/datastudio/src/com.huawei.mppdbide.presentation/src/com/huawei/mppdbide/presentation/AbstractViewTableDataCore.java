/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.bl.serverdatacache.ServerObject;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractViewTableDataCore.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public abstract class AbstractViewTableDataCore implements IViewTableDataCore {

    private TerminalExecutionConnectionInfra termConnection = null;

    /**
     * init.
     *
     * @param obj the obj
     */
    @Override
    public abstract void init(ServerObject obj);

    /**
     * getServerObject.
     *
     * @return the server object
     */
    @Override
    public abstract ServerObject getServerObject();

    /**
     * getWindowDetails.
     *
     * @return the window details
     */
    @Override
    public abstract IWindowDetail getWindowDetails();

    /**
     * getWindowTitle.
     *
     * @return the window title
     */
    @Override
    public abstract String getWindowTitle();

    /**
     * get Progress Bar Label.
     *
     * @return the progress bar label
     */
    @Override
    public abstract String getProgressBarLabel();

    /**
     * getQuery
     *
     * @return getQuery
     * @throws DatabaseOperationException 
     */
    @Override
    public abstract String getQuery() throws DatabaseOperationException;

    /**
     * Gets the term connection.
     *
     * @return the term connection
     */
    public TerminalExecutionConnectionInfra getTermConnection() {
        if (null == this.termConnection) {
            this.termConnection = new TerminalExecutionConnectionInfra();
            this.termConnection.setDatabase(getServerObject().getDatabase());
        }
        return this.termConnection;
    }

}
