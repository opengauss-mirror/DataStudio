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

package org.opengauss.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionProfileId.
 * 
 */

public class ConnectionProfileId {

    private int serverId;
    private long databaseId;

    /**
     * Instantiates a new connection profile id.
     *
     * @param serverId the server id
     * @param databaseId the database id
     */
    public ConnectionProfileId(int serverId, long databaseId) {
        this.serverId = serverId;
        this.databaseId = databaseId;
    }

    /**
     * Gets the server id.
     *
     * @return the server id
     */
    public int getServerId() {
        return serverId;
    }

    /**
     * Gets the database id.
     *
     * @return the database id
     */
    public long getDatabaseId() {
        return databaseId;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return DBConnProfCache.getInstance().getDbForProfileId(this);
    }

    /**
     * Checks if is equals.
     *
     * @param obj the obj
     * @return true, if is equals
     */
    public boolean isEquals(Object obj) {
        ConnectionProfileId otherConn = (ConnectionProfileId) obj;
        return otherConn.getDatabaseId() == this.databaseId && otherConn.getServerId() == this.serverId;
    }
}
