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

package org.opengauss.mppdbide.bl.serverdatacache.connectioninfo;

import java.nio.charset.Charset;
import java.text.Normalizer;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.annotations.Expose;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.serverdatacache.savepsswordoption.SavePrdOptions;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.CustomStringUtility;
import org.opengauss.mppdbide.utils.DsEncodingEnum;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.connectionprofileversion.IConnectionProfileVersions;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * 
 * Title: class
 * 
 * Description: The Class ServerConnectionInfo.
 * 
 */

public class ServerConnectionInfo implements IServerConnectionInfo {

    @Expose
    private String profileId;

    private static final int DEFAULT_OBJECT_LOAD_THRESHOLD = 30000;
    @Expose
    private String databaseVersion = "";
    @Expose
    private DBTYPE dbType;
    @Expose
    private String version;
    @Expose
    private GeneralServerConnectionInfo general;
    @Expose
    private SSLServerConnectionInfo ssl;
    @Expose
    private AdvancedServerConnectionInfo advanced;

    /**
     * Instantiates a new server connection info.
     */
    public ServerConnectionInfo() {
        super();
        this.version = IConnectionProfileVersions.CONNECTION_PROFILE_CURRENT_VERSION;
        general = new GeneralServerConnectionInfo();
        ssl = new SSLServerConnectionInfo();
        advanced = new AdvancedServerConnectionInfo();
    }

    @Override
    public String getVersion() {
        return version;
    }

    /**
     * Gets the clone.
     *
     * @return the clone
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#getClone()
     */

    @Override
    public ServerConnectionInfo getClone() {
        ServerConnectionInfo newInfo = new ServerConnectionInfo();
        newInfo.setConectionName(getConectionName());
        newInfo.setDatabaseName(getDatabaseName());
        newInfo.setSSLEnabled(isSSLEnabled());
        newInfo.setServerIp(getServerIp());
        newInfo.setServerPort(getServerPort());
        newInfo.setSavePrdOption(getSavePrdOption());
        newInfo.setUsername(getDsUsername());
        newInfo.setClientSSLCertificate(getClientSSLCertificate());
        newInfo.setClientSSLKey(getClientSSLKey());
        newInfo.setRootCertificate(getRootCertificate());
        newInfo.setSSLMode(getSSLMode());
        newInfo.setProfileId(this.getProfileId());
        newInfo.setSchemaExclusionList(getSchemaExclusionList());
        newInfo.setSchemaInclusionList(getSchemaInclusionList());
        newInfo.setCanLoadChildObjects(canLoadChildObjects());
        newInfo.setLoadLimit(getLoadLimit());
        newInfo.setDBVersion(this.databaseVersion);
        newInfo.setPrivilegeBasedObAccess(isPrivilegeBasedObAccessEnabled());
        newInfo.setDriverName(getDriverName());
        newInfo.setPrd(new char[0]);
        newInfo.setSSLPrd(new char[0]);
        newInfo.setClientSSLPrivateKey(getClientSSLPrivateKey());
        newInfo.setModifiedSchemaExclusionList(getModifiedSchemaExclusionList());
        newInfo.setModifiedSchemaInclusionList(getModifiedSchemaInclusionList());
        return newInfo;
    }

    /**
     * Gets the conection name.
     *
     * @return the conection name
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * getConectionName ()
     */

    @Override
    public String getConectionName() {
        return general.getConectionName();
    }

    /**
     * Sets the conection name.
     *
     * @param conectionName the new conection name
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setConectionName (java.lang.String)
     */
    @Override
    public void setConectionName(String conectionName) {
        general.setConectionName(Normalizer.normalize(conectionName, Normalizer.Form.NFD));
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * getDatabaseName ()
     */
    @Override
    public String getDatabaseName() {
        return this.general.getDatabaseName();
    }

    /**
     * Sets the database name.
     *
     * @param databaseName the new database name
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setDatabaseName (java.lang.String)
     */
    @Override
    public void setDatabaseName(String databaseName) {
        this.general.setDatabaseName(databaseName);
    }

    /**
     * Gets the prd.
     *
     * @return the prd
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#getPrd ()
     */
    @Override
    public char[] getPrd() {
        return (char[]) this.general.getPrd();
    }

    /**
     * Sets the prd.
     *
     * @param prd the new prd
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#setPrd
     * (char[])
     */
    public void setPrd(char[] prd) {
        this.general.setPrd(prd);
    }

    @Override
    public void clearPasrd() {
        this.general.clearPssrd();
        this.ssl.clearSSLPssrd();
    }

    /**
     * Gets the server ip.
     *
     * @return the server ip
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#getServerIp
     */

    @Override
    public String getServerIp() {
        return this.general.getServerIp();
    }

    /**
     * Sets the server ip.
     *
     * @param serverIp the new server ip
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#setServerIp
     * (java.lang.String)
     */
    @Override
    public void setServerIp(String serverIp) {
        this.general.setServerIp(serverIp);
    }

    /**
     * Gets the server port.
     *
     * @return the server port
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * getServerPort ()
     */

    @Override
    public int getServerPort() {
        return this.general.getServerPort();
    }

    /**
     * Sets the server port.
     *
     * @param serverPort the new server port
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setServerPort (int)
     */
    @Override
    public void setServerPort(int serverPort) {
        this.general.setServerPort(serverPort);
    }

    /**
     * Gets the ds username.
     *
     * @return the ds username
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#getUsername
     */
    @Override
    public String getDsUsername() {
        return this.general.getUsername();
    }

    /**
     * Sets the username.
     *
     * @param username the new username
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#setUsername
     * (java.lang.String)
     */
    @Override
    public void setUsername(String username) {
        this.general.setUsername(username);
    }

    @Override
    public boolean isPrivilegeBasedObAccessEnabled() {
        return this.advanced.isPrivilegeBasedObAcess();
    }

    @Override
    public boolean isSSLEnabled() {
        return this.general.isSSLEnabled();
    }

    /**
     * Sets the SSL enabled.
     *
     * @param isSSLEnabld the new SSL enabled
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setSSLEnabled (boolean)
     */
    @Override
    public void setSSLEnabled(boolean isSSLEnabld) {
        this.general.setSSLEnabled(isSSLEnabld);
    }

    /**
     * Gets the client SSL certificate.
     *
     * @return the client SSL certificate
     */
    public String getClientSSLCertificate() {
        return this.ssl.getClSSLCertificatePath();
    }

    /**
     * Sets the client SSL certificate.
     *
     * @param clSSLCertificate the new client SSL certificate
     */
    public void setClientSSLCertificate(String clSSLCertificate) {
        this.ssl.setClSSLCertificatePath(clSSLCertificate);
    }

    /**
     * Gets the client SSL key.
     *
     * @return the client SSL key
     */
    public String getClientSSLKey() {
        return this.ssl.getClSSLKeyPath();
    }

    /**
     * Sets the client SSL key.
     *
     * @param clSSLKey the new client SSL key
     */
    public void setClientSSLKey(String clSSLKey) {
        this.ssl.setClSSLKeyPath(clSSLKey);
    }

    /**
     * Sets the save prd option.
     *
     * @param savePrdOption the new save prd option
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setSavePrdOption
     * (org.opengauss.mppdbide.bl.serverdatacache.Server.SAVE_PRD_OPTIONS)
     */
    @Override
    public void setSavePrdOption(SavePrdOptions savePrdOption) {
        this.general.setSavePrdOption(savePrdOption);
    }

    /**
     * Sets the save prd option.
     *
     * @param selIndex the sel index
     * @param isPermanentPrdReq the is permanent prd req
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * setSavePrdOption(int)
     */
    @Override
    public void setSavePrdOption(int selIndex, boolean isPermanentPrdReq) {
        if (isPermanentPrdReq) {
            switch (selIndex) {
                case 0: {
                    setSavePrdOption(SavePrdOptions.PERMANENTLY);
                    break;
                }
                case 1: {
                    setSavePrdOption(SavePrdOptions.CURRENT_SESSION_ONLY);
                    break;
                }
                default: {
                    setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
                }
            }
        } else {
            switch (selIndex) {
                case 0: {
                    setSavePrdOption(SavePrdOptions.CURRENT_SESSION_ONLY);
                    break;
                }
                default: {
                    setSavePrdOption(SavePrdOptions.DO_NOT_SAVE);
                }
            }
        }
    }

    /**
     * Gets the save prd option.
     *
     * @return the save prd option
     * org.opengauss.mppdbide.bl.serverdatacache.IServerConnectionInfo#
     * getSavePrdOption()
     */
    @Override
    public SavePrdOptions getSavePrdOption() {
        return this.general.getSavePrdOption();
    }

    @Override
    public String getProfileId() {
        return profileId;
    }

    @Override
    public void setProfileId(String id) {
        profileId = id;
    }

    @Override
    public String getRootCertificate() {
        return this.ssl.getRootCertFilePathText();
    }

    @Override
    public void setRootCertificate(String rootCertificatePath) {
        this.ssl.setRootCertFilePathText(rootCertificatePath);
    }

    @Override
    public String getSSLMode() {
        return this.ssl.getSSLMode();
    }

    @Override
    public void setSSLMode(String sslMod) {
        this.ssl.setSSLMode(sslMod);
    }

    @Override
    public Set<String> getSchemaExclusionList() {
        return this.advanced.getSchemaExclusionList();
    }

    @Override
    public Set<String> getSchemaInclusionList() {
        return this.advanced.getSchemaInclusionList();
    }

    /**
     * Sets the schema exclusion list.
     *
     * @param schemaExclusionList the new schema exclusion list
     */
    public void setSchemaExclusionList(Set<String> schemaExclusionList) {
        this.advanced.setSchemaExclusionList(schemaExclusionList);
    }

    /**
     * Sets the schema inclusion list.
     *
     * @param schemaInclusionList the new schema inclusion list
     */
    public void setSchemaInclusionList(Set<String> schemaInclusionList) {
        this.advanced.setSchemaInclusionList(schemaInclusionList);
    }

    @Override
    public int getLoadLimit() {
        return this.advanced.getLoadLimit();
    }

    @Override
    public void setLoadLimit(int setloadLimitParam) {
        int setloadLimit = setloadLimitParam;
        if (0 == setloadLimit) {
            setloadLimit = DEFAULT_OBJECT_LOAD_THRESHOLD;
        }
        this.advanced.setLoadLimit(setloadLimit);
    }

    @Override
    public void setDriverName(String dname) {
        this.general.setConnctionDriverName(MessageConfigLoader.getProperty(IMessagesConstants.OPEN_GAUSS));
        setDbType();
    }

    @Override
    public String getDriverName() {
        return this.general.getConnctionDriverName();
    }

    /**
     * Sets the DB version.
     *
     * @param serverVersion the new DB version
     */
    @Override
    public void setDBVersion(String serverVersion) {
        this.databaseVersion = serverVersion;
    }

    /**
     * Gets the DB version.
     *
     * @return the DB version
     */
    @Override
    public String getDBVersion() {
        return this.databaseVersion;
    }

    /**
     * Sets the privilege based ob access.
     *
     * @param selection the new privilege based ob access
     */
    @Override
    public void setPrivilegeBasedObAccess(boolean selection) {
        this.advanced.setPrivilegeBasedObAcess(selection);
    }

    @Override
    public DBTYPE getServerDBType() {
        return dbType;
    }

    /**
     * Sets the db type.
     */
    @Override
    public void setDbType() {
        this.dbType = DBTYPE.OPENGAUSS;
    }

    @Override
    public Properties composeProperty(String driverName) {
        Properties properties = new Properties();
        properties.setProperty("user", getDsUsername());
        properties.setProperty("password", new String(getPrd()));
        properties.setProperty("allowEncodingChanges", "true");
        String dsEncoding = BLPreferenceManager.getInstance().getBLPreference().getDSEncoding();
        if (dsEncoding.isEmpty()) {
            dsEncoding = Charset.defaultCharset().toString();
        } else if (!dsEncoding.equals(DsEncodingEnum.GBK.getEncoding())) {
            dsEncoding = DsEncodingEnum.UTF_8.getEncoding();
        }

        properties.setProperty("characterEncoding", dsEncoding);
        properties.setProperty("ApplicationName", "Data Studio");
        if (CustomStringUtility.isProtocolVersionNeeded(driverName)) {
            properties.setProperty("protocolVersion", "3");
        }

        String[] arguments = BLUtils.getInstance().getPlatformArgs();
        boolean isLoginTimeout = false;
        String loginTimeout = null;

        int len = arguments.length;
        for (int argIndex = 0; argIndex < len; argIndex++) {
            if (arguments[argIndex] != null && arguments[argIndex].startsWith("-loginTimeout")) {
                String[] splitArgs = arguments[argIndex].split("=");
                loginTimeout = splitArgs.length > 1 ? splitArgs[1].trim() : "";
                isLoginTimeout = "".equals(loginTimeout) ? false : true;
                break;
            }
        }

        properties.setProperty("loginTimeout", isLoginTimeout ? loginTimeout : "180");
        if (isSSLEnabled()) {
            properties.setProperty("sslmode", getSSLMode());
            properties.setProperty("sslcert", getClientSSLCertificate());
            properties.setProperty("sslkey", getClientSSLKey());
            properties.setProperty("sslrootcert", getRootCertificate());
            properties.setProperty("sslpassword", new String(getSSLPrd()));
            properties.setProperty("ssl", "true");
        }

        return properties;
    }

    @Override
    public String composeUrl() {
        if (!StringUtils.isEmpty(getServerIp())) {
            return "jdbc:postgresql://" + getServerIp() + ':' + getServerPort() + '/' + getDatabaseName();
        } else {
            return "";
        }
    }

    @Override
    public char[] getSSLPrd() {
        char[] sslPrd = this.ssl.getSSLPsrd();
        return (char[]) sslPrd.clone();
    }

    @Override
    public void setSSLPrd(char[] pword) {
        this.ssl.setSSLPsrd(pword);
    }

    @Override
    public void setClientSSLPrivateKey(String keyFileName) {
        this.ssl.setClSSLPrivateKeyFile(keyFileName);
    }

    @Override
    public String getClientSSLPrivateKey() {
        return this.ssl.getClSSLPrivateKeyFile();
    }

    @Override
    public Set<String> getModifiedSchemaExclusionList() {
        return advanced.getModifiedSchemaExclusionList();
    }

    @Override
    public void setModifiedSchemaExclusionList(Set<String> modifiedSchemaExclusionList) {
        advanced.setModifiedSchemaExclusionList(modifiedSchemaExclusionList);
    }

    @Override
    public Set<String> getModifiedSchemaInclusionList() {
        return advanced.getModifiedSchemaInclusionList();
    }

    @Override
    public void setModifiedSchemaInclusionList(Set<String> modifiedSchemaInclusionList) {
        advanced.setModifiedSchemaInclusionList(modifiedSchemaInclusionList);
    }

    @Override
    public boolean canLoadChildObjects() {
        return this.advanced.isCanLoadChildObj();
    }

    @Override
    public void setCanLoadChildObjects(boolean canLoadChildObj) {
        this.advanced.setCanLoadChildObj(canLoadChildObj);
    }
}