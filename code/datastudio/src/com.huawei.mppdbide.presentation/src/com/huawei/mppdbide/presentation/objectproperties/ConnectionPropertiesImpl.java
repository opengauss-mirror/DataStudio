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

package com.huawei.mppdbide.presentation.objectproperties;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Server;
import com.huawei.mppdbide.bl.serverdatacache.ServerProperty;
import com.huawei.mppdbide.utils.CustomStringUtility;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConnectionPropertiesImpl.
 * 
 * @since 3.0.0
 */
public class ConnectionPropertiesImpl implements IServerObjectProperties {
    private Server server;
    private String serverType;
    private String serverVersion;
    private ConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new connection properties impl.
     *
     * @param obj the obj
     */
    public ConnectionPropertiesImpl(Object obj) {
        // get the IADAPTABLE CALL
        server = (Server) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public String getObjectName() {

        return server.getDisplayName();
    }

    @Override
    public String getHeader() {

        return server.getName();
    }

    @Override
    public String getUniqueID() {

        return server.getId() + "properties";
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        List<String> tabNameList = new ArrayList<String>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        List<List<String[]>> propertyList = new ArrayList<List<String[]>>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        tabNameList.add(PropertiesConstants.GENERAL);
        propertyList.add(getGeneralproperty(conn));
        return convertToObjectPropertyData.getObjectPropertyData(tabNameList, propertyList, null, this);
    }

    /**
     * Gets the generalproperty.
     *
     * @param conn the conn
     * @return the generalproperty
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    private List<String[]> getGeneralproperty(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
        List<String[]> props = new ArrayList<String[]>();
        String[] dbPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE)};
        parseVersionForProperty(server.getServerVersion(true));

        props.add(dbPropHeader);
        // only one record expected
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_NAME),
                server.getServerConnectionInfo().getConectionName()).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_HOST),
                server.getServerConnectionInfo().getServerIp()).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_PORT),
                server.getServerConnectionInfo().getServerPort()).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_USERNAME),
                server.getServerConnectionInfo().getDsUsername()).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_SERVER_IP),
                server.getServerIP2()).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_SERVER_TYPE),
                this.serverType).getProp());
        props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONN_PROP_DB_VERSION),
                this.serverVersion).getProp());
        if (props.isEmpty()) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_PROPERTIES_UNAVAILABLE));
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_PROPERTIES_UNAVAILABLE,
                    server.getName());
        }
        return props;
    }

    /**
     * Parses the version for property.
     *
     * @param version the version
     */
    private void parseVersionForProperty(String version) {
        String[] serverTypeAndInfo = CustomStringUtility.getServerType(version);
        this.serverType = (String) Array.get(serverTypeAndInfo, 0);
        this.serverVersion = (String) Array.get(serverTypeAndInfo, 1);
    }

    @Override
    public Database getDatabase() {
        return null;
    }
}
