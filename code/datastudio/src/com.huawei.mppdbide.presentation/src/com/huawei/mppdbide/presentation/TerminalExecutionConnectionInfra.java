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

package com.huawei.mppdbide.presentation;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class TerminalExecutionConnectionInfra.
 * 
 * @since 3.0.0
 */
public class TerminalExecutionConnectionInfra {

    /**
     * The connection.
     */
    protected DBConnection connection;

    /**
     * The database.
     */
    protected Database database;

    /**
     * The auto commit flag.
     */
    protected boolean autoCommitFlag;
    private boolean isReconnect;

    /**
     * Instantiates a new terminal execution connection infra.
     */
    public TerminalExecutionConnectionInfra() {
        // By default DS supports Auto commit ON.
        this.autoCommitFlag = true;
    }

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    public DBConnection getConnection() {
        return connection;
    }

    /**
     * Sets the connection.
     *
     * @param connection the new connection
     */
    public void setConnection(DBConnection connection) {
        this.connection = connection;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Sets the database.
     *
     * @param database the new database
     */
    public void setDatabase(Database database) {
        this.database = database;
    }

    /**
     * Gets the auto commit flag.
     *
     * @return the auto commit flag
     */
    public boolean getAutoCommitFlag() {
        return autoCommitFlag;
    }

    /**
     * Sets the auto commit flag.
     *
     * @param autoCommitFlag the new auto commit flag
     */
    public void setAutoCommitFlag(boolean autoCommitFlag) {
        this.autoCommitFlag = autoCommitFlag;
    }

    /**
     * Reset information.
     */
    public void resetInformation() {
        releaseConnection();

        /*
         * We will not set the database to null for reset. Setting to NULL will
         * lead to force NULL checks (by static tools) and unexpected
         * NullPointerException where it is not done.
         */
    }

    /**
     * Release connection.
     */
    public void releaseConnection() {
        if (null != this.getConnection()) {
            try {
                if (!this.getConnection().isClosed()) {
                    database.getConnectionManager().releaseAndDisconnection(this.getConnection());
                } else {
                    database.getConnectionManager().removeConnectionFromPool(this.getConnection());
                }
            } catch (DatabaseOperationException e) {
                // cant do anything. Just ignore and proceed!
                this.setConnection(null);
            }
            this.setConnection(null);
        }
    }

    private boolean isClosed(DBConnection conn) {
        try {
            return this.getConnection().isClosed();
        } catch (DatabaseOperationException e) {
            return false;
        }
    }

    /**
     * Checks if is connected.
     *
     * @return true, if is connected
     */
    public boolean isConnected() {
        if (null != this.getConnection() && !isClosed(this.getConnection())) {
            return true;
        }
        return false;
    }

    /**
     * Checks if is reconnect on terminal.
     *
     * @return true, if is reconnect on terminal
     */
    public boolean isReconnectOnTerminal() {
        return isReconnect;
    }

    /**
     * Sets the reconnect on terminal.
     *
     * @param reconnect the new reconnect on terminal
     */
    public void setReconnectOnTerminal(boolean reconnect) {
        this.isReconnect = reconnect;
    }
}
