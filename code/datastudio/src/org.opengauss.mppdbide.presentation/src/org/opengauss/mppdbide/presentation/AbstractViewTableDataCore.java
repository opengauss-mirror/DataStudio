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

import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class AbstractViewTableDataCore.
 *
 * @since 3.0.0
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
