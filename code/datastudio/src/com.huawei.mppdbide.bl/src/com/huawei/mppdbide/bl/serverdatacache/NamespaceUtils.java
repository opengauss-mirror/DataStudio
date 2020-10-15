/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Locale;

import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface NamespaceUtils.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface NamespaceUtils extends NamespaceUtilsBase {

    /**
     * Fetch all user namespaces.
     *
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void fetchAllUserNamespaces(Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        int counter = 0;
        String query = "SELECT oid, nspname from pg_namespace where ((oid >= 16384 or nspname LIKE 'public') "
                + "and nspname  NOT LIKE 'pg_%')";
        if (db.getPrivilegeFlag()) {
            query += " and has_schema_privilege(nspname, 'USAGE')";
        }
        query += " ORDER BY nspname;";
        ResultSet rs = null;
        int oid = 0;
        String name = null;

        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(query);
            boolean hasNext = rs.next();
            UserNamespace namespace = null;
            db.getUserNamespaces().clear();
            db.getSearchPoolManager().clearTrie();
            while (hasNext) {
                oid = rs.getInt("oid");
                name = rs.getString("nspname");
                namespace = new UserNamespace(oid, name, db);

                db.getUserNamespaces().addToGroup(namespace);
                counter++;
                hasNext = rs.next();
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility.trace("Total number of namespace loaded for selected database is " + counter);
            }
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

    /**
     * Fetch all system namespaces.
     *
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void fetchAllSystemNamespaces(Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        int counter = 0;

        String query = "SELECT oid, nspname from pg_namespace where ((oid < 16384 and nspname NOT LIKE 'public') "
                + "or nspname LIKE 'pg_%')";
        if (db.getPrivilegeFlag()) {
            query += " and has_schema_privilege(nspname, 'USAGE')";
        }
        query += " ORDER BY nspname;";
        ResultSet rs = null;
        int oid = 0;
        String name = null;

        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(query);
            boolean hasNext = rs.next();
            SystemNamespace namespace = null;
            db.getSystemNamespaces().clear();
            db.getSearchPoolManager().clearTrie();
            while (hasNext) {
                oid = rs.getInt("oid");
                name = rs.getString("nspname");
                namespace = new SystemNamespace(oid, name, db);

                db.getSystemNamespaces().addToGroup(namespace);
                counter++;
                hasNext = rs.next();
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility.trace("Total number of namespace loaded for selected database is : " + counter);
            }
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

    /**
     * Gets the shallow load qry.
     *
     * @param ns the ns
     * @return the shallow load qry
     */
    static String getShallowLoadQry(Namespace ns) {
        String privilegeString = (ns != null && ns.getPrivilegeFlag()) ? DBG_PRIVILEGE_FILTER : "";
        String str = getSchemaInfo(ns);
        return SHALLOW_LOAD_QUERY + privilegeString + str + SHALLOW_LOAD_ORDERBY_QRY;
    }

    /**
     * getSchemaInfo schema info
     * 
     * @param ns namespace
     * @return string
     */
    static String getSchemaInfo(Namespace ns) {
        if (ns != null) {
            return String.format(Locale.ENGLISH, RESTRICT_TO_SCHEMA, ns.getOid());
        }
        return "";
    }

    /**
     * Gets the shallow load function qry.
     *
     * @param ns the ns
     * @return the shallow load function qry
     */
    static String getShallowLoadFunctionQry(Namespace ns) {
        String privilegeString = (ns != null && ns.getPrivilegeFlag()) ? DBG_PRIVILEGE_FILTER : "";
        String str = getSchemaInfo(ns);
        return SHALLOW_LOAD_FUNCTION_ONLY + privilegeString + str + SHALLOW_LOAD_ORDERBY_QRY;
    }

    /**
     * Gets the shallow load qry on demand.
     *
     * @param ns the ns
     * @return the shallow load qry on demand
     */
    static String getShallowLoadQryOnDemand(Namespace ns) {
        String privilegeString = (ns != null && ns.getPrivilegeFlag()) ? DBG_PRIVILEGE_FILTER : "";
        String str = getSchemaInfo(ns);
        return SHALLOW_LOAD_QRY_ON_DEMAND + privilegeString + str + SHALLOW_LOAD_ORDERBY_QRY;
    }

    /**
     * Gets the shallow load function qry on demand.
     *
     * @param ns the ns
     * @return the shallow load function qry on demand
     */
    static String getShallowLoadFunctionQryOnDemand(Namespace ns) {
        String privilegeString = (ns != null && ns.getPrivilegeFlag()) ? DBG_PRIVILEGE_FILTER : "";
        String str = getSchemaInfo(ns);
        return SHALLOW_LOAD_QRY_ON_DEMAND + privilegeString + str + SHALLOW_LOAD_ORDERBY_QRY;
    }

    /**
     * Gets the debug objects load query.
     *
     * @param type the type
     * @param onDemand the on demand
     * @param ns the ns
     * @return the debug objects load query
     */
    public static String getDebugObjectsLoadQuery(OBJECTTYPE type, boolean onDemand, Namespace ns) {
        String query;
        switch (type) {
            case PLSQLFUNCTION: {
                if (onDemand) {
                    query = getShallowLoadFunctionQryOnDemand(ns);
                } else {
                    query = getShallowLoadFunctionQry(ns);
                }
                break;
            }
            default: {
                if (onDemand) {
                    query = getShallowLoadQryOnDemand(ns);
                } else {
                    query = getShallowLoadQry(ns);
                }
                break;
            }
        }
        return query;
    }

    /**
     * Sets the level 3 loaded flag.
     *
     * @param itr the new level 3 loaded flag
     */
    public static void setLevel3LoadedFlag(Iterator<TableMetaData> itr) {
        boolean hasNext = itr.hasNext();

        while (hasNext) {
            TableMetaData table = itr.next();
            table.setLevel3Loaded(true);
            hasNext = itr.hasNext();
        }
    }
}
