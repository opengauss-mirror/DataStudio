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

package org.opengauss.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.adapter.gauss.GaussUtils;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: ViewUtils
 * 
 * 
 */

public class ViewUtils {
    /**
     * Convert to view meta data.
     *
     * @param rs the rs
     * @param parentNamespace the parent namespace
     * @return the view meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws OutOfMemoryError the out of memory error
     */
    public static ViewMetaData convertToViewMetaData(ResultSet rs, Namespace parentNamespace)
            throws DatabaseOperationException, OutOfMemoryError {
        ViewMetaData view = null;

        try {
            long oid = rs.getLong("oid");
            String name = rs.getString("viewname");
            view = new ViewMetaData(oid, name, parentNamespace, parentNamespace.getDatabase());
            view.setOwner(rs.getString("viewowner"));
            view.setRelKind(rs.getString("relkind"));
            return view;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Error while converting view data from resultset.", exception);
            throw new DatabaseOperationException("Error while converting view data from resultset.", exception);
        }
    }

    /**
     * Fetch views.
     *
     * @param parentNamespace the parent namespace
     * @param query the query
     * @param conn the conn
     * @return the view meta data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static ViewMetaData fetchViews(Namespace parentNamespace, String query, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = null;
        boolean hasNext = false;
        ViewMetaData view = null;
        try {
            rs = conn.execSelectAndReturnRs(query);
            hasNext = rs.next();
            view = addAllViews(parentNamespace, rs, hasNext);
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
        return view;
    }

    private static ViewMetaData addAllViews(Namespace parentNamespace, ResultSet rs, boolean hasNextParam)
            throws DatabaseOperationException, OutOfMemoryError, SQLException {
        Namespace ns = null;
        ViewMetaData view = null;
        boolean hasNext = hasNextParam;
        while (hasNext) {
            view = ViewUtils.convertToViewMetaData(rs, parentNamespace);
            view.setLoaded(false);
            ns = view.getNamespace();
            ns.addView(view);
            hasNext = rs.next();
        }
        return view;
    }
    
    /**
     * Convert to view meta data on demand.
     *
     * @param rs the rs
     * @param db the db
     * @return the view meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws OutOfMemoryError the out of memory error
     */
    public static ViewMetaData convertToViewMetaDataOnDemand(ResultSet rs, Database db)
            throws DatabaseOperationException, OutOfMemoryError {
        ViewMetaData view = null;

        try {
            long namespaceId = rs.getLong("relnamespace");
            Namespace namespace = db.getNameSpaceById(namespaceId);

            long oid = rs.getLong("oid");
            String name = rs.getString("relname");
            view = new ViewMetaData(oid, name, namespace, namespace.getDatabase());
            view.setViewCodeLoaded(false);
            view.setRelKind(rs.getString("relkind"));
            return view;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Error while converting view data from resultset.", exception);
            throw new DatabaseOperationException("Error while converting view data from resultset.", exception);
        }
    }

    /**
     * Gets the view query by namespace id.
     *
     * @param oid the oid
     * @param privilegeFlag the privilege flag
     * @return the view query by namespace id
     */
    public static String getViewQueryByNamespaceId(long oid, boolean privilegeFlag) {
        String queryBySchema = String.format(Locale.ENGLISH,
                "SELECT c.oid, c.relname AS viewname, pg_get_userbyid(c.relowner) AS viewowner "
                        + ", c.relkind as relkind "
                        + "FROM pg_class c WHERE (c.relkind = 'v'::char or c.relkind = 'm'::char) "
                        + "and " + "c.relnamespace = %d",
                oid);
        return TableMetaData.formQueryForTableMetadata(queryBySchema, false, privilegeFlag);
    }

    /**
     * Gets the view query.
     *
     * @param oid the oid
     * @param privilegeFlag the privilege flag
     * @return the view query
     */
    public static String getViewQuery(long oid, boolean privilegeFlag) {
        String query = String.format(Locale.ENGLISH,
                "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, "
                        + "pg_get_userbyid(c.relowner) AS viewowner "
                        + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) "
                        + "WHERE (c.relkind = 'v'::\"char\" or c.relkind = 'm'::\"char\") " + "and c.oid=%d",
                oid);
        return TableMetaData.formQueryForTableMetadata(query, false, privilegeFlag);
    }

    /**
     * Gets the creates the view template.
     *
     * @param ns the ns
     * @return the creates the view template
     */
    public static String getCreateViewTemplate(Namespace ns) {
        StringBuilder strbldr = new StringBuilder("CREATE (OR REPLACE) [ TEMP | TEMPORARY | MATERIALIZED ] VIEW ");
        strbldr.append(ns.getQualifiedObjectName()).append(".").append("<VIEW NAME>")
                .append(" [ ( column_name [, ...] ) ] ").append(System.lineSeparator())
                .append("[ WITH ( {view_option_name [= view_option_value]} [, ... ] ) ]").append(System.lineSeparator())
                .append("\tAS ").append(System.lineSeparator()).append("<SQL QUERY>").append(";");
        return strbldr.toString();
    }
}
