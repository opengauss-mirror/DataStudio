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

package org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif;

import java.util.Properties;
import java.util.Set;

import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.DBTYPE;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface IServerConnectionInfo.
 * 
 */

public interface IServerConnectionInfo {

    /**
     * Gets the clone.
     *
     * @return the clone
     */
    IServerConnectionInfo getClone();

    /**
     * Gets the conection name.
     *
     * @return the conection name
     */
    String getConectionName();

    /**
     * Sets the conection name.
     *
     * @param conectionName the new conection name
     */
    void setConectionName(String conectionName);

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    String getDatabaseName();

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     */
    void setDatabaseName(String databaseName);

    /**
     * Gets the prd.
     *
     * @return the prd
     */
    char[] getPrd();

    /**
     * Sets the prd.
     *
     * @param prd the new prd
     */
    void setPrd(char[] prd);

    /**
     * Clear pasrd.
     */
    void clearPasrd();

    /**
     * Gets the server ip.
     *
     * @return the server ip
     */
    String getServerIp();

    /**
     * Sets the server ip.
     *
     * @param serverIp the new server ip
     */
    void setServerIp(String serverIp);

    /**
     * Gets the server port.
     *
     * @return the server port
     */
    int getServerPort();

    /**
     * Sets the server port.
     *
     * @param serverPort the new server port
     */
    void setServerPort(int serverPort);

    /**
     * Gets the username.
     *
     * @return the username
     */
    String getDsUsername();

    /**
     * Sets the username.
     *
     * @param username the new username
     */
    void setUsername(String username);

    /**
     * Checks if is SSL enabled.
     *
     * @return true, if is SSL enabled
     */
    boolean isSSLEnabled();

    /**
     * Sets the SSL enabled.
     *
     * @param isSSLEnabled the new SSL enabled
     */
    void setSSLEnabled(boolean isSSLEnabled);

    /**
     * Gets the client SSL certificate.
     *
     * @return the client SSL certificate
     */
    String getClientSSLCertificate();

    /**
     * Sets the client SSL certificate.
     *
     * @param clSSLCertificatePath the new client SSL certificate
     */
    void setClientSSLCertificate(String clSSLCertificatePath);

    /**
     * Gets the client SSL key.
     *
     * @return the client SSL key
     */
    String getClientSSLKey();

    /**
     * Sets the client SSL key.
     *
     * @param clSSLKeyPath the new client SSL key
     */
    void setClientSSLKey(String clSSLKeyPath);

    /**
     * Gets the SSL prd.
     *
     * @return the SSL prd
     */
    char[] getSSLPrd();

    /**
     * Sets the SSL prd.
     *
     * @param prd the new SSL prd
     */
    void setSSLPrd(char[] prd);

    /**
     * Sets the save prd option.
     *
     * @param savePrdOption the new save prd option
     */
    void setSavePrdOption(SavePrdOptions savePrdOption);

    /**
     * Sets the save prd option.
     *
     * @param ordinal the ordinal
     * @param isPermanentPrdReq the is permanent prd req
     */
    void setSavePrdOption(int ordinal, boolean isPermanentPrdReq);

    /**
     * Gets the save prd option.
     *
     * @return the save prd option
     */
    SavePrdOptions getSavePrdOption();

    /**
     * Gets the SSL mode.
     *
     * @return the SSL mode
     */
    String getSSLMode();

    /**
     * Sets the SSL mode.
     *
     * @param sslMode the new SSL mode
     */
    void setSSLMode(String sslMode);

    /**
     * Gets the profile id.
     *
     * @return the profile id
     */
    String getProfileId();

    /**
     * Sets the profile id.
     *
     * @param id the new profile id
     */
    void setProfileId(String id);

    /**
     * Gets the root certificate.
     *
     * @return the root certificate
     */
    String getRootCertificate();

    /**
     * Sets the root certificate.
     *
     * @param rootCertificatePath the new root certificate
     */
    void setRootCertificate(String rootCertificatePath);

    /**
     * Gets the schema exclusion list.
     *
     * @return the schema exclusion list
     */
    Set<String> getSchemaExclusionList();

    /**
     * Gets the schema inclusion list.
     *
     * @return the schema inclusion list
     */
    Set<String> getSchemaInclusionList();

    /**
     * Gets the load limit.
     *
     * @return the load limit
     */
    int getLoadLimit();

    /**
     * Sets the load limit.
     *
     * @param loadLimit the new load limit
     */
    void setLoadLimit(int loadLimit);

    /**
     * Checks if is privilege based ob access enabled.
     *
     * @return true, if is privilege based ob access enabled
     */
    public boolean isPrivilegeBasedObAccessEnabled();

    /**
     * Sets the driver name.
     *
     * @param dname the new driver name
     */
    void setDriverName(String dname);

    /**
     * Gets the driver name.
     *
     * @return the driver name
     */
    String getDriverName();

    /**
     * Gets the db type.
     *
     * @return the db type
     */
    DBTYPE getServerDBType();

    /**
     * Gets the version.
     *
     * @return the version
     */
    String getVersion();

    /**
     * Compose property.
     *
     * @param driverName the driver name
     * @return the properties
     */
    Properties composeProperty(String driverName);

    /**
     * Compose url.
     *
     * @return the string
     */
    String composeUrl();

    /**
     * Sets the schema exclusion list.
     *
     * @param newExcludeList the new schema exclusion list
     */
    void setSchemaExclusionList(Set<String> newExcludeList);

    /**
     * Sets the client SSL private key.
     *
     * @param keyFileName the new client SSL private key
     */
    void setClientSSLPrivateKey(String keyFileName);

    /**
     * Gets the client SSL private key.
     *
     * @return the client SSL private key
     */
    String getClientSSLPrivateKey();

    /**
     * Sets the DB version.
     *
     * @param serverVersion the new DB version
     */
    void setDBVersion(String serverVersion);

    /**
     * Gets the DB version.
     *
     * @return the DB version
     */
    String getDBVersion();

    /**
     * Sets the privilege based ob access.
     *
     * @param selection the new privilege based ob access
     */
    void setPrivilegeBasedObAccess(boolean selection);

    /**
     * Sets the db type.
     */
    void setDbType();

    /**
     * getModifiedSchemaExclusionList
     * 
     * @return schema list
     */
    Set<String> getModifiedSchemaExclusionList();

    /**
     * setModifiedSchemaExclusionList
     * 
     * @param modifiedSchemaExclusionList set value
     */
    void setModifiedSchemaExclusionList(Set<String> modifiedSchemaExclusionList);

    /**
     * getModifiedSchemaInclusionList
     * 
     * @return schema list
     */
    Set<String> getModifiedSchemaInclusionList();

    /**
     * setModifiedSchemaInclusionList
     * 
     * @param modifiedSchemaInclusionList set value
     */
    void setModifiedSchemaInclusionList(Set<String> modifiedSchemaInclusionList);

    /**
     * canLoadChildObjects
     * 
     * @return boolean can load child obj
     */
    boolean canLoadChildObjects();

    /**
     * setCanLoadChildObjects
     * 
     * @param canLoadChildObj boolean
     */
    void setCanLoadChildObjects(boolean canLoadChildObj);
}