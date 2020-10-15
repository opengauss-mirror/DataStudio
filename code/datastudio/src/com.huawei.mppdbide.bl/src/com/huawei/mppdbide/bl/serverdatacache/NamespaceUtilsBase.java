/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Description: The Interface NamespaceUtilsBase.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface NamespaceUtilsBase {

    static final String SHALLOW_LOAD_QUERY = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, "
            + "pr.prorettype ret, pr.proallargtypes alltype, pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname, "
            + "pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, pr.proisstrict isstrict, "
            + "pr.proretset retset, " + "pr.procost procost, pr.prorows setrows, lng.lanname lang " + "FROM pg_proc pr "
            + "JOIN pg_type typ ON typ.oid=prorettype " + "JOIN pg_namespace typns ON typns.oid=typ.typnamespace "
            + "JOIN pg_language lng ON lng.oid=prolang  " + "WHERE lng.lanname in ('plpgsql','sql','c')";
    static final String DBG_PRIVILEGE_FILTER = " and has_function_privilege(pr.oid, 'EXECUTE')";

    static final String SHALLOW_LOAD_ORDERBY_QRY = " ORDER BY objname";
    static final String SHALLOW_LOAD_FUNCTION_ONLY = SHALLOW_LOAD_QUERY;
    static final String RESTRICT_TO_SCHEMA = " and pr.pronamespace= %d";

    static final String SHALLOW_LOAD_QRY_ON_DEMAND = "SELECT pr.oid oid, pr.proname objname, pr.pronamespace namespace, pr.prorettype ret, "
            + "pr.proallargtypes alltype, "
            + "pr.pronargs nargs, pr.proargtypes argtype,  pr.proargnames argname, pr.proargmodes argmod, "
            + "pr.proretset retset, lng.lanname lang " + "FROM pg_proc pr " + "JOIN pg_language lng ON lng.oid=prolang "
            + "WHERE lng.lanname in ('plpgsql','sql','c')";

    /**
     * Gets the debug object type by group type.
     *
     * @param groupType the group type
     * @return the debug object type by group type
     */
    public static OBJECTTYPE getDebugObjectTypeByGroupType(OBJECTTYPE groupType) {
        switch (groupType) {
            case FUNCTION_GROUP: {
                return OBJECTTYPE.PLSQLFUNCTION;
            }
            case PROCEDURE_GROUP: {
                return OBJECTTYPE.PROCEDURE;
            }
            default: {
                return OBJECTTYPE.OBJECTTYPE_BUTT;
            }
        }
    }

    /**
     * Fetch namespace.
     *
     * @param namespaceId the namespace id
     * @param resetChildDetails the reset child details
     * @param isRenameFlow the is rename flow
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void fetchNamespace(long namespaceId, boolean resetChildDetails, boolean isRenameFlow, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {

        String qry = null;
        if (db.getPrivilegeFlag() && !isRenameFlow) {
            qry = "SELECT oid, nspname from pg_namespace WHERE oid=" + namespaceId
                    + " and has_schema_privilege(nspname, 'USAGE');";
        } else {
            qry = "SELECT oid, nspname from pg_namespace WHERE oid=" + namespaceId + ";";
        }

        ResultSet rs = null;
        boolean isSysNamespace = false;
        Namespace newNamespace = db.getUserNamespaceGroup().getObjectById(namespaceId);
        if (newNamespace == null) {
            newNamespace = db.getSystemNamespaceGroup().getObjectById(namespaceId);
            isSysNamespace = true;
        }

        try {
            rs = fetchAndAddNamespaceToDb(namespaceId, resetChildDetails, isRenameFlow, db, qry, isSysNamespace,
                    newNamespace);

        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

    /**
     * Fetch and add namespace to db.
     *
     * @param namespaceId the namespace id
     * @param resetChildDetails the reset child details
     * @param isRenameFlow the is rename flow
     * @param db the db
     * @param qry the qry
     * @param isSysNamespace the is sys namespace
     * @param newNamespace the new namespace
     * @return the result set
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public static ResultSet fetchAndAddNamespaceToDb(long namespaceId, boolean resetChildDetails, boolean isRenameFlow,
            Database db, String qry, boolean isSysNamespace, Namespace newNamespace)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        ResultSet rs;
        rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
        boolean hasNext = rs.next();

        if (hasNext) {
            if (newNamespace == null) {
                newNamespace = new Namespace(rs.getInt("oid"), rs.getString("nspname"), db);

            } else {
                if (isSysNamespace) {
                    db.getSystemNamespaceGroup().removeFromGroup(namespaceId);
                } else {
                    db.getUserNamespaceGroup().removeFromGroup(namespaceId);
                }

                newNamespace.setName(rs.getString("nspname"));
                if (resetChildDetails) {
                    newNamespace.clearAllObjects();
                }

            }

            if (isSysNamespace) {
                db.getSystemNamespaceGroup().addToGroup((SystemNamespace) newNamespace);
            } else {
                db.getUserNamespaceGroup().addToGroup((UserNamespace) newNamespace);
            }

        } else {
            if (isSysNamespace) {
                db.getSystemNamespaceGroup().removeFromGroup(namespaceId);
            } else {
                db.getUserNamespaceGroup().removeFromGroup(namespaceId);
            }
            newNamespace.setValid(false);
            DatabaseUtils.checkExceptionForNoAccessNoRenameflow(isRenameFlow, db.getPrivilegeFlag());
        }
        return rs;
    }

    /**
     * Gets the namespace list for search.
     *
     * @param namespacesList the namespaces list
     * @param nsMap the ns map
     * @param nss the nss
     * @return the namespace list for search
     */
    public static void getNamespaceListForSearch(ArrayList<String> namespacesList, HashMap<Integer, Namespace> nsMap,
            ArrayList<Namespace> nss) {
        for (int idx = 0; idx < nss.size(); idx++) {
            Namespace ns = nss.get(idx);
            namespacesList.add(ns.getName());
            nsMap.put(idx, ns);
        }
    }

    /**
     * Fetch all namespaces.
     *
     * @param db the db
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void fetchAllNamespaces(Database db) throws DatabaseOperationException, DatabaseCriticalException {
        int counter = 0;
        String query = "SELECT oid, nspname from pg_namespace";
        if (db.getPrivilegeFlag()) {
            query += " where has_schema_privilege(nspname, 'USAGE')";
        }
        query += " ORDER BY nspname;";

        ResultSet rs = null;
        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(query);
            boolean hasNext = rs.next();
            db.getUserNamespaces().clear();
            db.getSystemNamespaces().clear();
            db.getSearchPoolManager().clearTrie();
            while (hasNext) {
                int oid = rs.getInt("oid");
                String name = rs.getString("nspname");
                if (oid >= 16384 && (!name.contains("pg_")) || name.toLowerCase(Locale.ENGLISH).contains("public")) {
                    UserNamespace usernamespace = new UserNamespace(oid, name, db);
                    db.getUserNamespaces().addToGroup(usernamespace);
                } else {
                    SystemNamespace systemnamespace = new SystemNamespace(oid, name, db);
                    db.getSystemNamespaces().addToGroup(systemnamespace);
                }
                counter++;
                hasNext = rs.next();
            }
        } catch (SQLException exe) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exe);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility.trace("Total number of namespace loaded is : " + counter);
            }

            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

    /**
     * Refresh namespace.
     *
     * @param namespaceId the namespace id
     * @param isDrop the is drop
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void refreshNamespace(long namespaceId, boolean isDrop, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        MPPDBIDELoggerUtility.debug("ConnectionProfile: refresh namespace.");
        Namespace namespace = db.getNameSpaceById(namespaceId);
        namespace.clearAllObjects();

        if (isDrop) {
            db.getUserNamespaces().removeFromGroup(namespaceId);
        } else {
            fetchNamespace(namespaceId, true, false, db);
        }

    }

    /**
     * Refresh namespace meta data.
     *
     * @param namespaceId the namespace id
     * @param db the db
     * @return the namespace
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static Namespace refreshNamespaceMetaData(long namespaceId, Database db)
            throws DatabaseCriticalException, DatabaseOperationException {
        MPPDBIDELoggerUtility.debug("ConnectionProfile: refresh namespace metadata.");

        /*
         * It is mandatory not to touch/clear any child details. The below line
         * is deliberately commented.
         */

        fetchNamespace(namespaceId, false, true, db);

        return db.getNameSpaceById(namespaceId);

    }
}
