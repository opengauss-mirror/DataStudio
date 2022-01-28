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

package com.huawei.mppdbide.bl.serverdatacache.groups;

import com.huawei.mppdbide.bl.serverdatacache.Database;

/**
 * This class collects the Database objects
 * 
 */

import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class DatabaseObjectGroup.
 * 
 */

public class DatabaseObjectGroup extends OLAPObjectGroup<Database> {

    private boolean isLoadingDatabaseGroupInProgress;

    /**
     * Instantiates a new database object group.
     *
     * @param type the type
     * @param server the server
     */
    public DatabaseObjectGroup(OBJECTTYPE type, Server server) {
        super(type, server);
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return (Server) getParent();
    }

    /**
     * Refresh.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refresh() throws DatabaseOperationException, DatabaseCriticalException {
        Database db = this.getServer().findOneActiveDb();
        this.getServer().refreshDBs(db);
    }

    /**
     * Checks if is loading database group in progress.
     *
     * @return true, if is loading database group in progress
     */
    public boolean isLoadingDatabaseGroupInProgress() {
        return isLoadingDatabaseGroupInProgress;
    }

    /**
     * Sets the loading database group in progress.
     *
     * @param status the new loading database group in progress
     */
    public void setLoadingDatabaseGroupInProgress(boolean status) {
        this.isLoadingDatabaseGroupInProgress = status;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
