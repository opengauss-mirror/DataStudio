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

package org.opengauss.mppdbide.presentation.objectproperties;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.bl.serverdatacache.Database;
import org.opengauss.mppdbide.bl.serverdatacache.DatabaseHelper;
import org.opengauss.mppdbide.bl.serverdatacache.ServerProperty;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class PropertiesDatabaseImpl.
 * 
 * @since 3.0.0
 */
public class PropertiesDatabaseImpl implements IServerObjectProperties {
    private Database db;
    private ConvertToObjectPropertyData convertToObjectPropertyData;

    /**
     * Instantiates a new properties database impl.
     *
     * @param obj the obj
     */
    public PropertiesDatabaseImpl(Object obj) {
        // get the IADAPTABLE CALL
        db = (Database) obj;
        convertToObjectPropertyData = new OlapConvertToObjectPropertyData();
    }

    @Override
    public String getObjectName() {

        return db.getQualifiedObjectName();
    }

    @Override
    public List<IObjectPropertyData> getAllProperties(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {

        List<String> tabNameList = new ArrayList<String>(5);
        List<List<String[]>> propertyList = new ArrayList<List<String[]>>(5);
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
     */
    private List<String[]> getGeneralproperty(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "SELECT  oid as oid, datname AS name, pg_encoding_to_char(encoding) as encoding,"
                + " datallowconn as allow_conn, datconnlimit as max_conn_limit, "
                + "(select spcname from pg_tablespace where oid=dattablespace) as  default_tablespace, "
                + "datcollate as collation, datctype as char_type " + "from pg_database where oid = "
                + DatabaseHelper.fetchDBOid(db) + ';';

        ResultSet rs = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;
        List<String[]> props = new ArrayList<String[]>();
        String[] dbPropHeader = {MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_PROPERTY),
            MessageConfigLoader.getProperty(IMessagesConstants.PROPERTIES_WID_VALUE)};
        props.add(dbPropHeader);

        try {
            hasNext = rs.next();

            // only one record expected
            if (hasNext) {
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.OID_MSG),
                        rs.getString("oid")).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.NAME_MSG),
                        rs.getString("name")).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.ENCODING_MSG),
                        rs.getString("encoding")).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.ALLOW_CON),
                        convertToBoolean(rs.getString("allow_conn"))).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_LIMIT),
                        appendNoLimit(rs.getString("max_conn_limit"))).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.DFLT_TBSPACE),
                        rs.getString("default_tablespace")).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.COLLECTION_MSG),
                        rs.getString("Collation")).getProp());
                props.add(new ServerProperty(MessageConfigLoader.getProperty(IMessagesConstants.CHAR_TYPE),
                        rs.getString("char_type")).getProp());
            }
        } catch (SQLException sqlExcept) {
            GaussUtils.handleCriticalException(sqlExcept);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlExcept);
        } finally {
            conn.closeResultSet(rs);
        }
        if (props.isEmpty()) {
            MPPDBIDELoggerUtility
                    .error(MessageConfigLoader.getProperty(IMessagesConstants.CONNECTION_PROPERTIES_UNAVAILABLE));
            throw new DatabaseOperationException("PROPERT_TYABLE_UNAVAILABLE", db.getDisplayName());
        }
        return props;
    }

    /**
     * Convert to boolean.
     *
     * @param value the value
     * @return the string
     */
    private String convertToBoolean(String value) {

        if ("t".equalsIgnoreCase(value)) {
            return "" + true;
        } else if ("f".equalsIgnoreCase(value)) {
            return "" + false;
        }

        return "";
    }

    /**
     * Append no limit.
     *
     * @param value the value
     * @return the string
     */
    public String appendNoLimit(String value) {
        if ("-1".equalsIgnoreCase(value)) {
            return value + "(No Limit)";
        }
        return value;
    }

    @Override
    public String getHeader() {
        return db.getDbName() + '@' + db.getServerName();
    }

    @Override
    public String getUniqueID() {
        return db.getOid() + '@' + db.getServerName() + "properties";
    }

    @Override
    public Database getDatabase() {
        return db;
    }
}
