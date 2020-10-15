/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionProfileId.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
