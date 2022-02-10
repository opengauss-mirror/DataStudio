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

package org.opengauss.mppdbide.bl.adapter.gauss;

import java.nio.charset.Charset;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import org.opengauss.mppdbide.adapter.IConnectionDriver;
import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.IDBConnectionWrapper;
import org.opengauss.mppdbide.bl.preferences.BLPreferenceManager;
import org.opengauss.mppdbide.bl.serverdatacache.IDebugObject;
import org.opengauss.mppdbide.bl.serverdatacache.ObjectParameter;
import org.opengauss.mppdbide.bl.serverdatacache.ServerObject;
import org.opengauss.mppdbide.bl.serverdatacache.connectioninfo.conif.IServerConnectionInfo;
import org.opengauss.mppdbide.bl.util.BLUtils;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;
import org.opengauss.mppdbide.utils.messaging.MessageQueue;
import org.opengauss.mppdbide.utils.security.SecureUtil;

/**
 * 
 * Title: class
 * 
 * Description: The Class GaussConnection.
 *
 * @since 3.0.0
 */
public class GaussConnection implements IDBConnectionWrapper {

    private static final int MAX_WAIT_TIME = 100000;

    private DBConnection con;

    private MessageQueue messageQueue;

    /**
     * Instantiates a new gauss connection.
     *
     * @param iConnectionDriver the i connection driver
     */
    public GaussConnection(IConnectionDriver iConnectionDriver) {
        con = new DBConnection(iConnectionDriver);
    }


    /**
     * closeResultSet.
     *
     * @param rs the rs
     */

    private void closeResultSet(ResultSet rs) {
        try {
            if (null != rs) {
                rs.close();
            }
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error("ADAPTER: statement close returned exception.", exp);
        }
    }

    /**
     * Execute the query and fetch the result.
     *
     * @param stmt the stmt
     * @return the result set
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private ResultSet executeAndFetch(PreparedStatement stmt)
            throws DatabaseOperationException, DatabaseCriticalException {
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery();
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);

            con.closeStatement(stmt);
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED),
                    exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED, exp);
        } catch (IndexOutOfBoundsException iobe) {
            con.closeStatement(stmt);
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED),
                    iobe);
            throw new DatabaseCriticalException(IMessagesConstants.ERR_GUI_EXECUTE_FUN_PROC_TRIG_QUERY_FAILED, iobe);
        }

        return rs;
    }

    /**
     * Gets the version.
     *
     * @return the version
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */

    public String getVersion() throws DatabaseOperationException, DatabaseCriticalException {
        ResultSet rs = null;
        PreparedStatement stmt = null;
        String serverVersion = null;
        stmt = con.getPrepareStmt("select * from version();");

        rs = executeAndFetch(stmt);

        try {
            boolean bRetVal = rs.next();
            if (!bRetVal) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
            }

            serverVersion = rs.getString("VERSION");
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            closeResultSet(rs);
            con.closeStatement(stmt);
        }
        if (serverVersion == null) {
            return "";
        }
        return serverVersion;
    }

    /**
     * Gets the server IP.
     *
     * @return the server IP
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getServerIP1() throws DatabaseOperationException, DatabaseCriticalException {
        ResultSet rs = null;
        String query = "select inet_server_addr();";
        String serverIP = "";
        try {
            rs = con.execSelectAndReturnRs(query);
            rs.next();
            serverIP = rs.getString(1);
        } catch (SQLException exp) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.SERVERIP_TOOLTIP_FAIL), exp);
            throw new DatabaseOperationException(IMessagesConstants.SERVERIP_TOOLTIP_FAIL, exp);
        } finally {
            if (rs != null) {
                con.closeResultSet(rs);
            }
        }
        return serverIP;
    }

    /**
     * disconnect database.
     */
    public void dbDisconnect() {
        con.disconnect();
    }

    /**
     * Connect.
     *
     * @param serverInformation the server information
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void connect(IServerConnectionInfo serverInformation)
            throws DatabaseCriticalException, DatabaseOperationException {
        String url = null;
        Properties properties = new Properties();

        properties.setProperty("user", serverInformation.getDsUsername());
        properties.setProperty("password", new String(serverInformation.getPrd()));
        properties.setProperty("allowEncodingChanges", "true");
        String dsEncoding = BLPreferenceManager.getInstance().getBLPreference().getDSEncoding();
        if (dsEncoding.isEmpty()) {
            dsEncoding = Charset.defaultCharset().toString();
        }
        properties.setProperty("characterEncoding", dsEncoding);
        properties.setProperty("useUnicode", "true");
        properties.setProperty("ApplicationName", "Data Studio");
        if (con.getDriver() != null) {
            properties.putAll(con.getDriver().getDriverSpecificProperties());
        }

        String[] args = BLUtils.getInstance().getPlatformArgs();
        boolean hasLoginTimeout = false;
        String loginTimeout = null;

        int len = args.length;
        for (int index = 0; index < len; index++) {
            if (args[index] != null && args[index].startsWith("-loginTimeout")) {
                loginTimeout = args[index].split("=")[1].trim();
                hasLoginTimeout = true;
                break;
            }
        }

        properties.setProperty("loginTimeout", hasLoginTimeout ? loginTimeout : "180");

        if (serverInformation.isSSLEnabled()) {
            properties.setProperty("sslmode", serverInformation.getSSLMode());
            properties.setProperty("sslcert", serverInformation.getClientSSLCertificate());
            properties.setProperty("sslkey", serverInformation.getClientSSLKey());
            properties.setProperty("sslrootcert", serverInformation.getRootCertificate());
            properties.setProperty("sslpassword", new String(serverInformation.getSSLPrd()));
            properties.setProperty("ssl", "true");
        }

        url = getUrl(serverInformation);
        try {
            doConnect(con, properties, url);
        } finally {
            SecureUtil.cleanKeyString(properties.getProperty("password"));
            properties.setProperty("password", "");
            properties.remove("password");
            SecureUtil.cleanKeyString(properties.getProperty("sslpassword"));
            properties.setProperty("sslpassword", "");
            properties.remove("sslpassword");
            serverInformation.clearPasrd();
        }

    }

    private String getUrl(IServerConnectionInfo serverInformation) {
        if (!StringUtils.isEmpty(serverInformation.getServerIp())) {
            return "jdbc:postgresql://" + serverInformation.getServerIp() + ':' + serverInformation.getServerPort()
                    + '/' + serverInformation.getDatabaseName();
        } else {
            return "";
        }
    }

    /**
     * Do connect.
     *
     * @param conn the conn
     * @param props the props
     * @param url the url
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void doConnect(DBConnection conn, Properties props, String url)
            throws DatabaseOperationException, DatabaseCriticalException {
        conn.connectViaDriver(props, url);
    }

}
