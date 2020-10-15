/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache.connectioninfo;

import com.google.gson.annotations.Expose;
import com.huawei.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import com.huawei.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class GeneralServerConnectionInfo.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public final class GeneralServerConnectionInfo {

    @Expose
    private String conectionName;
    @Expose
    private String databaseName;
    @Expose
    private char[] prd;
    @Expose
    private boolean isSSLEnabled;
    @Expose
    private String serverIp;
    @Expose
    private int serverPort;
    @Expose
    private String username;
    @Expose
    private DBTYPE dbType;
    @Expose
    private SavePrdOptions savePrdOption;
    @Expose
    private String connctionDriverName;

    /**
     * Instantiates a new general server connection info.
     */
    public GeneralServerConnectionInfo() {
        this.conectionName = "";
        this.databaseName = "";
        this.prd = new char[0];
        this.serverIp = "";
        this.username = "";
        this.savePrdOption = SavePrdOptions.DO_NOT_SAVE;
    }

    /**
     * Gets the conection name.
     *
     * @return the conection name
     */
    public String getConectionName() {
        return conectionName;
    }

    /**
     * Sets the conection name.
     *
     * @param conectionName the new conection name
     */
    public void setConectionName(String conectionName) {
        this.conectionName = conectionName;
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Gets the prd.
     *
     * @return the prd
     */
    public char[] getPrd() {
        if (this.prd == null) {
            return new char[0];
        }
        return prd.clone();
    }

    /**
     * Sets the prd.
     *
     * @param prd the new prd
     */
    public void setPrd(char[] prd) {
        this.prd = prd.clone();
    }

    /**
     * Checks if is SSL enabled.
     *
     * @return true, if is SSL enabled
     */
    public boolean isSSLEnabled() {
        return isSSLEnabled;
    }

    /**
     * Sets the SSL enabled.
     *
     * @param isSSLEnable the new SSL enabled
     */
    public void setSSLEnabled(boolean isSSLEnable) {
        this.isSSLEnabled = isSSLEnable;
    }

    /**
     * Gets the server ip.
     *
     * @return the server ip
     */
    public String getServerIp() {
        return serverIp;
    }

    /**
     * Sets the server ip.
     *
     * @param serverIp the new server ip
     */
    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    /**
     * Gets the server port.
     *
     * @return the server port
     */
    public int getServerPort() {
        return serverPort;
    }

    /**
     * Sets the server port.
     *
     * @param serverPort the new server port
     */
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the db type.
     *
     * @return the db type
     */
    public DBTYPE getDatabaseType() {
        return dbType;
    }

    /**
     * Sets the db type.
     *
     * @param dbType the new db type
     */
    public void setDbType(DBTYPE dbType) {
        this.dbType = dbType;
    }

    /**
     * Gets the save prd option.
     *
     * @return the save prd option
     */
    public SavePrdOptions getSavePrdOption() {
        return savePrdOption;
    }

    /**
     * Sets the save prd option.
     *
     * @param savePrdOption the new save prd option
     */
    public void setSavePrdOption(SavePrdOptions savePrdOption) {
        this.savePrdOption = savePrdOption;
    }

    /**
     * Gets the connction driver name.
     *
     * @return the connction driver name
     */
    public String getConnctionDriverName() {
        return connctionDriverName;
    }

    /**
     * Sets the connction driver name.
     *
     * @param connctionDriverName the new connction driver name
     */
    public void setConnctionDriverName(String connctionDriverName) {
        this.connctionDriverName = connctionDriverName;
    }

    /**
     * Clear pssrd.
     */
    public void clearPssrd() {
        SecureUtil.clearPassword(this.prd);
    }

}
