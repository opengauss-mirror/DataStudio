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

package com.huawei.mppdbide.bl.serverdatacache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import com.huawei.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.bl.util.IExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.exceptions.PasswordExpiryException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class DBConnProfCache.
 * 
 */

public final class DBConnProfCache {

    private static volatile DBConnProfCache instance = null;
    private HashMap<Integer, Server> servers;

    private ArrayList<Server> serverList; // controls the viewer in the OB
    private static final Object LOCK = new Object();

    private DBConnProfCache() {
        servers = new HashMap<Integer, Server>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        serverList = new ArrayList<Server>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
    }

    /**
     * Gets the single instance of DBConnProfCache.
     *
     * @return single instance of DBConnProfCache
     */
    public static DBConnProfCache getInstance() {
        if (null == instance) {
            synchronized (LOCK) {
                if (null == instance) {
                    instance = new DBConnProfCache();
                }
            }
        }
        return instance;
    }

    /**
     * Inits the connection profile.
     *
     * @param serverInfo the server info
     * @param status the status
     * @return the connection profile id
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     * @throws PasswordExpiryException the password expiry exception
     * @throws OutOfMemoryError the out of memory error
     */
    public ConnectionProfileId initConnectionProfile(IServerConnectionInfo serverInfo, IJobCancelStatus status)
            throws DatabaseOperationException, MPPDBIDEException, PasswordExpiryException, OutOfMemoryError {
        Iterator<Server> serversItr = this.servers.values().iterator();
        boolean hasNext = serversItr.hasNext();
        Server server = null;
        while (hasNext) {
            server = serversItr.next();

            if (server.getName().equals(serverInfo.getConectionName())) {
                MPPDBIDELoggerUtility.error(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_ALREADY_CONNECTION_PROFILE_EXISTS, server.getName()));
                throw new DatabaseOperationException(IMessagesConstants.ERR_ALREADY_CONNECTION_PROFILE_EXISTS,
                        server.getName());
            }

            hasNext = serversItr.hasNext();
        }
        // Added for remembering PSWD/PORT ends

        IExecTimer timer = new ExecTimer("New Connection").start();
        Server node = null;
        ConnectionProfileId id = null;

        node = new Server(serverInfo);
        id = node.createDBConnectionProfile(serverInfo, status);
        node.clearPrds();
        timer.stopAndLog();
        this.servers.put(node.getId(), node);
        this.serverList.add(0, node);
        return id;
    }

    /**
     * Destroy connection.
     *
     * @param db the db
     */
    public void destroyConnection(Database db) {
        MPPDBIDELoggerUtility.info("DBConnProfCache: destroy Connection Profile.");

        if (null != db && db.isConnected()) {
            db.destroy();
        }
    }

    /**
     * Removes the server.
     *
     * @param serverId the server id
     */
    public void removeServer(int serverId) {
        if (servers.containsKey(serverId)) {
            Server server = servers.get(serverId);
            server.close();
            server.destroy();
            servers.remove(serverId);
            serverList.remove(server);
        }
    }

    /**
     * Gets the db for profile id.
     *
     * @param id the id
     * @return the db for profile id
     */
    public Database getDbForProfileId(ConnectionProfileId id) {
        if (id == null) {
            return null;
        }
        Server server = this.servers.get(id.getServerId());

        if (null == server) {
            return null;
        }

        return server.getDbById(id.getDatabaseId());
    }

    /**
     * Gets the servers.
     *
     * @return the servers
     */
    public Collection<Server> getServers() {
        return servers.values();
    }

    /**
     * Gets the servers list.
     *
     * @return the servers list
     */
    public Collection<Server> getServersList() {
        return serverList;
    }

    /**
     * Gets the server by name.
     *
     * @param name the name
     * @return the server by name
     */
    public Server getServerByName(String name) {
        Iterator<Server> srvs = servers.values().iterator();
        boolean hasNext = srvs.hasNext();
        Server server = null;
        while (hasNext) {
            server = srvs.next();
            if (server != null && server.getName().equals(name)) {
                return server;
            }
            hasNext = srvs.hasNext();
        }
        return null;
    }

    /**
     * Gets the server by id.
     *
     * @param id the id
     * @return the server by id
     */
    public Server getServerById(int id) {
        return servers.get(id);
    }

    /**
     * Close all nodes.
     */
    public void closeAllNodes() {
        Iterator<Server> serverItr = servers.values().iterator();
        boolean hasNext = serverItr.hasNext();
        while (hasNext) {
            serverItr.next().close();
            hasNext = serverItr.hasNext();
        }
    }

    /**
     * Gets the all profiles for search.
     *
     * @param connectionDetails the connection details
     * @param connectionMap the connection map
     * @return the all profiles for search
     */
    public void getAllProfilesForSearch(ArrayList<String> connectionDetails, HashMap<Integer, Server> connectionMap) {
        Iterator<Server> serverItr = getServers().iterator();
        boolean serverHasNext = serverItr.hasNext();
        Server servr = null;
        int idx = 0;
        while (serverHasNext) {
            servr = serverItr.next();
            if (servr.isAleastOneDbConnected()) {
                connectionDetails.add(servr.getDisplayName());
                connectionMap.put(idx, servr);
                idx++;
            }
            serverHasNext = serverItr.hasNext();
        }
    }

}
