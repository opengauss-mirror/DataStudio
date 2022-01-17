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

package com.huawei.mppdbide.bl.serverdatacache.groups;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.Database;
import com.huawei.mppdbide.bl.serverdatacache.Namespace;
import com.huawei.mppdbide.bl.serverdatacache.OBJECTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.SynonymMetaData;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SynonymObjectGroup.
 *
 * @since 3.0.0
 */
public class SynonymObjectGroup extends OLAPObjectGroup<SynonymMetaData> {
    private static final String QUERY_FOR_ALL_NORMAL_TABLES_BY_NAMESPACE_ID = "select  tbl.relname relname "
            + "from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') "
            + "where tbl.relkind = 'r' and tbl.relnamespace = %d";
    private static final String QUERY_FOR_ALL_FOREIGN_TABLES_BY_NAMESPACE_ID = "SELECT c.relname AS relname"
            + " FROM pg_class c WHERE (c.relkind = 'f' :: char) and c.relnamespace = %d";
    private static final String QUERY_BY_SCHEMA_ID = "SELECT c.relname AS relname FROM pg_class c "
            + "WHERE (c.relkind = 'v' :: char or c.relkind = 'm' :: char) and c.relnamespace = %d ";
    private static final String QUERY_FOR_ALL_FUNCTIONS_BY_NAMESPACE_ID = "SELECT pr.proname relname "
            + "FROM pg_proc pr JOIN pg_type typ ON typ.oid = prorettype JOIN pg_namespace typns "
            + "ON typns.oid = typ.typnamespace JOIN pg_language lng ON lng.oid = prolang "
            + "and pronamespace = %d and has_function_privilege(pr.oid, 'EXECUTE') ORDER BY relname";

    private Namespace namespace = null;
    private Database database = null;

    /**
     * Instantiates a new synonym object group
     * 
     * @param type the type
     * @param parentObject the parent object
     */
    public SynonymObjectGroup(OBJECTTYPE type, Object parentObject) {
        super(type, parentObject);
        if (parentObject instanceof Namespace) {
            namespace = (Namespace) parentObject;
            database = namespace.getDatabase();
        }
    }

    
    /**
     * gets the Database
     * 
     * @return database the database
     */
    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public Object getParent() {
        return this.namespace;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Fetch object name.
     *
     * @param namespace the namespace
     * @param dbConnection the db connection
     * @param objectOwner the object owner
     * @param objectType the object type
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static List<String> fetchObjectName(Namespace namespace, DBConnection dbConnection, String objectOwner,
            String objectType) throws DatabaseOperationException, DatabaseCriticalException {
        String fetchObjName = String.format(Locale.ENGLISH, QUERY_FOR_ALL_NORMAL_TABLES_BY_NAMESPACE_ID,
                namespace.getOid()) + " UNION "
                + String.format(Locale.ENGLISH, QUERY_FOR_ALL_FOREIGN_TABLES_BY_NAMESPACE_ID, namespace.getOid())
                + " UNION " + String.format(Locale.ENGLISH, QUERY_BY_SCHEMA_ID, namespace.getOid()) + " UNION "
                + String.format(Locale.ENGLISH, QUERY_FOR_ALL_FUNCTIONS_BY_NAMESPACE_ID, namespace.getOid());
        String fetchObejctNamesStatement = null;
        if (objectType.equals(MPPDBIDEConstants.PRIVILEGE_ALL)) {
            fetchObejctNamesStatement = String.format(Locale.ENGLISH, fetchObjName, namespace.getOid());
        } else if (objectType.equals(MessageConfigLoader.getProperty(IMessagesConstants.FUNCTION_PROCEDURE_NAME))) {
            fetchObejctNamesStatement = String.format(Locale.ENGLISH, QUERY_FOR_ALL_FUNCTIONS_BY_NAMESPACE_ID,
                    namespace.getOid());
        } else if (objectType.equals(MessageConfigLoader.getProperty(IMessagesConstants.TABLES_NAME))) {
            fetchObejctNamesStatement = String.format(Locale.ENGLISH, QUERY_FOR_ALL_NORMAL_TABLES_BY_NAMESPACE_ID,
                    namespace.getOid()) + " UNION "
                    + String.format(Locale.ENGLISH, QUERY_FOR_ALL_FOREIGN_TABLES_BY_NAMESPACE_ID, namespace.getOid());
        } else if (objectType.equals(MessageConfigLoader.getProperty(IMessagesConstants.VIEWS_NAME))) {
            fetchObejctNamesStatement = String.format(Locale.ENGLISH, QUERY_BY_SCHEMA_ID, namespace.getOid());
        } else {
            return new ArrayList<String>();
        }

        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = dbConnection.getPrepareStmt(fetchObejctNamesStatement);

            rs = preparedStatement.executeQuery();
            boolean hasNext = rs.next();
            List<String> objectNameList = new ArrayList<>();
            while (hasNext) {
                String objectName = rs.getString("relname");
                objectNameList.add(objectName);
                hasNext = rs.next();
            }
            return objectNameList;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            if (rs != null) {
                dbConnection.closeResultSet(rs);
            }
        }
    }
}
