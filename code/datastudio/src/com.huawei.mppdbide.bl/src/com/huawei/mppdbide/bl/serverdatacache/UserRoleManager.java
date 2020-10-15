/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface UserRoleManager.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public interface UserRoleManager {

    /**
     * The Constant GET_METHOD_PREFIX.
     */
    public static final String GET_METHOD_PREFIX = "get";

    /**
     * The Constant SET_METHOD_PREFIX.
     */
    public static final String SET_METHOD_PREFIX = "set";

    /**
     * Fetch all user role.
     *
     * @param server the server
     * @param conn the conn
     * @return the list
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static List<UserRole> fetchAllUserRole(Server server, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles;";
        List<UserRole> userRoles = UserRoleManagerHidden.fetchUserRoleQuery(server, conn, query);
        return userRoles;
    }

    /**
     * Fetch all user role with out super user.
     *
     * @param server the server
     * @param conn the conn
     * @return the list
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static List<UserRole> fetchAllUserRoleWithOutSuperUser(Server server, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE rolsuper = false;";
        List<UserRole> userRoles = UserRoleManagerHidden.fetchUserRoleQuery(server, conn, query);
        return userRoles;
    }

    /**
     * Fetch user role detail info by oid.
     *
     * @param server the server
     * @param conn the conn
     * @param userRole the user role
     * @return the user role
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static UserRole fetchUserRoleDetailInfoByOid(Server server, DBConnection conn, UserRole userRole)
            throws MPPDBIDEException {
        String query = "SELECT rolname,rolinherit,rolcreaterole,rolcreatedb,rolcanlogin,rolreplication"
                + ",rolauditadmin,rolsystemadmin,rolconnlimit,rolvalidbegin,rolvaliduntil,oid,rolrespool"
                + " FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        return UserRoleManagerHidden.fetchUserRoleByOid(server, conn, userRole, query);
    }

    /**
     * Fetch user role simple info by oid.
     *
     * @param server the server
     * @param conn the conn
     * @param userRole the user role
     * @return the user role
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static UserRole fetchUserRoleSimpleInfoByOid(Server server, DBConnection conn, UserRole userRole)
            throws MPPDBIDEException {
        String query = "SELECT rolname,rolcanlogin,oid FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        return UserRoleManagerHidden.fetchUserRoleByOid(server, conn, userRole, query);
    }

    /**
     * Fetch all parent.
     *
     * @param server the server
     * @param conn the conn
     * @param userRole the user role
     * @return the list
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static List<UserRole> fetchAllParent(Server server, DBConnection conn, UserRole userRole)
            throws DatabaseOperationException, DatabaseCriticalException {
        List<UserRole> parents = new ArrayList<>();
        String query = "SELECT r.oid, r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                + " WHERE r.oid = m.roleid AND member = ?::oid;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectForSearch(query, String.valueOf(userRole.getOid()));
            while (rs.next()) {
                parents.add(UserRoleManagerHidden.convertToUserRole(rs, server));
            }
            return parents;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class UserRoleManagerHidden.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    static class UserRoleManagerHidden {

        private static final String ROLE_QUERY = "SELECT rolsystemadmin,rolauditadmin,rolcreatedb,rolcreaterole,rolinherit,rolcanlogin,rolreplication,"
                + "rolconnlimit,rolvalidbegin,rolvaliduntil,rolrespool from pg_roles WHERE oid = %d;";
        private static final String MEMBER_QUERY = "SELECT r.rolname rolname,m.admin_option admin_option FROM pg_auth_members m, "
                + "pg_roles r WHERE r.oid = m.member AND m.roleid = %d;";
        private static final String DESCIPTIONQUERY = "SELECT description FROM "
                + "PG_SHDESCRIPTION WHERE objoid = %d;";

        private static List<UserRole> fetchUserRoleQuery(Server server, DBConnection conn, String query)
                throws DatabaseCriticalException, DatabaseOperationException {
            List<UserRole> userRoles = new ArrayList<>();
            ResultSet rs = null;
            try {
                rs = conn.execSelectAndReturnRs(query);
                while (rs.next()) {
                    userRoles.add(convertToUserRole(rs, server));
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            } finally {
                conn.closeResultSet(rs);
            }
            return userRoles;
        }

        private static UserRole fetchUserRoleByOid(Server server, DBConnection conn, UserRole userRole, String query)
                throws DatabaseCriticalException, DatabaseOperationException, MPPDBIDEException {
            ResultSet rs = null;
            try {
                rs = conn.execSelectForSearch(query, String.valueOf(userRole.getOid()));
                while (rs.next()) {
                    return convertToUserRole(rs, server);
                }
                MPPDBIDELoggerUtility.error(MessageConfigLoader
                        .getProperty(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST, String.valueOf(userRole.getOid())));
                throw new MPPDBIDEException(IMessagesConstants.ERR_USER_ROLE_IS_NOT_EXIST,
                        String.valueOf(userRole.getOid()));
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            } finally {
                conn.closeResultSet(rs);
            }
        }

        /**
         * Convert to user role.
         *
         * @param rs the rs
         * @param server the server
         * @return the user role
         * @throws SQLException the SQL exception
         */
        private static UserRole convertToUserRole(ResultSet rs, Server server) throws SQLException {
            return new UserRole(OBJECTTYPE.USER_ROLE, rs.getLong("oid"), rs.getString("rolname"),
                    isExistColumn(rs, "rolinherit") ? rs.getBoolean("rolinherit") : null,
                    isExistColumn(rs, "rolcreaterole") ? rs.getBoolean("rolcreaterole") : null,
                    isExistColumn(rs, "rolcreatedb") ? rs.getBoolean("rolcreatedb") : null,
                    isExistColumn(rs, "rolcanlogin") ? rs.getBoolean("rolcanlogin") : null,
                    isExistColumn(rs, "rolreplication") ? rs.getBoolean("rolreplication") : null,
                    isExistColumn(rs, "rolauditadmin") ? rs.getBoolean("rolauditadmin") : null,
                    isExistColumn(rs, "rolsystemadmin") ? rs.getBoolean("rolsystemadmin") : null,
                    isExistColumn(rs, "rolconnlimit") ? rs.getInt("rolconnlimit") : null,
                    isExistColumn(rs, "rolvalidbegin") ? rs.getDate("rolvalidbegin") : null,
                    isExistColumn(rs, "rolvaliduntil") ? rs.getDate("rolvaliduntil") : null,
                    isExistColumn(rs, "rolResPool") ? rs.getString("rolResPool") : null, server);
        }

        /**
         * Checks if is exist column.
         *
         * @param rs the rs
         * @param name the name
         * @return true, if is exist column
         */
        private static boolean isExistColumn(ResultSet rs, String name) {
            try {
                if (rs.findColumn(name) > 0) {
                    return true;
                }
                return false;
            } catch (SQLException e) {
                return false;
            }
        }

        /**
         * Generate parents change preview sql.
         *
         * @param conn the conn
         * @param userRole the user role
         * @param userRoleName the user role name
         * @return the list
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private static List<String> generateParentsChangePreviewSql(DBConnection conn, UserRole userRole,
                String userRoleName) throws DatabaseCriticalException, DatabaseOperationException {
            List<String> previewSqls = new ArrayList<>();
            // firstly, revoke all parents
            String currentParentsQuery = "SELECT r.rolname FROM pg_catalog.pg_roles r, pg_catalog.pg_auth_members m"
                    + " WHERE r.oid = m.roleid AND m.member = ?::oid;";
            ResultSet currentParentsRs = null;

            List<String> newParentNames = new ArrayList<>();
            userRole.getParents().stream().forEach(parent -> newParentNames.add(parent.getName()));

            try {
                currentParentsRs = conn.execSelectForSearch(currentParentsQuery, String.valueOf(userRole.getOid()));
                // if current parents already contains new parent, don't revoke
                while (currentParentsRs.next()) {
                    String currentParentName = currentParentsRs.getString("rolname");
                    if (newParentNames.contains(currentParentName)) {
                        newParentNames.remove(currentParentName);
                        continue;
                    }
                    String revokeQuery = MessageFormat.format("REVOKE {0} FROM {1};",
                            ServerObject.getQualifiedObjectName(currentParentsRs.getString("rolname")),
                            ServerObject.getQualifiedObjectName(userRoleName));
                    previewSqls.add(revokeQuery);
                }
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            } finally {
                conn.closeResultSet(currentParentsRs);
            }

            // secondly grant
            List<String> grantQuerys = new ArrayList<>();
            newParentNames.stream()
                    .forEach(newParentName -> grantQuerys.add(MessageFormat.format("GRANT {0} TO {1};",
                            ServerObject.getQualifiedObjectName(newParentName),
                            ServerObject.getQualifiedObjectName(userRoleName))));
            previewSqls.addAll(grantQuerys);

            return previewSqls;
        }
    }

    /**
     * Generate property change preview SQL.
     *
     * @param conn the conn
     * @param userRole the user role
     * @param userRoleName the user role name
     * @return the list
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static List<String> generatePropertyChangePreviewSQL(DBConnection conn, UserRole userRole,
            String userRoleName) throws MPPDBIDEException {
        List<String> previewSqls = new ArrayList<>();

        if (userRoleName == null) {
            return previewSqls;
        }
        StringBuffer strBuf;
        if (userRole.getIsUser()) {
            strBuf = new StringBuffer("ALTER USER ");
        } else {
            strBuf = new StringBuffer("ALTER ROLE ");
        }
        strBuf.append(ServerObject.getQualifiedObjectName(userRoleName)).append(" ");
        String initialStr = strBuf.toString();

        addConnectionLimitSQLStr(userRole, strBuf);
        addValidCluaseSQLStr(userRole, strBuf);

        addResourcePoolSQLStr(userRole, strBuf);
        addLoginSQLStr(userRole, strBuf);
        addCreateRoleSQLStr(userRole, strBuf);

        addCreateDBSQLStr(userRole, strBuf);

        addSysadminSQLStr(userRole, strBuf);
        addAuditAdminSQLStr(userRole, strBuf);
        addInheritSQLStr(userRole, strBuf);
        addReplicationSQLStr(userRole, strBuf);

        // the order of adding sql is base on lock option
        addSqlOnLockOption(userRole, userRoleName, previewSqls, strBuf, initialStr);

        addCommentSQLStr(userRole, userRoleName, previewSqls);

        if (userRole.getParents() != null) {
            previewSqls.addAll(UserRoleManagerHidden.generateParentsChangePreviewSql(conn, userRole, userRoleName));
        }

        // Rename SQL should be added at last.
        addRenameSQLStr(userRole, userRoleName, previewSqls);

        return previewSqls;
    }

    /**
     * Adds the rename SQL str.
     *
     * @param userRole the user role
     * @param userRoleName the user role name
     * @param previewSqls the preview sqls
     */
    public static void addRenameSQLStr(UserRole userRole, String userRoleName, List<String> previewSqls) {
        if (StringUtils.isNotEmpty(userRole.getName())) {
            String renameSql;
            if (userRole.getIsUser()) {
                renameSql = MessageFormat.format("ALTER USER {0} RENAME TO {1};",
                        ServerObject.getQualifiedObjectName(userRoleName), userRole.getQualifiedObjectName());
            } else {
                renameSql = MessageFormat.format("ALTER ROLE {0} RENAME TO {1};",
                        ServerObject.getQualifiedObjectName(userRoleName), userRole.getQualifiedObjectName());
            }
            previewSqls.add(renameSql);
        }
    }

    /**
     * Adds the comment SQL str.
     *
     * @param userRole the user role
     * @param userRoleName the user role name
     * @param previewSqls the preview sqls
     */
    public static void addCommentSQLStr(UserRole userRole, String userRoleName, List<String> previewSqls) {
        if (userRole.getComment() != null) {
            String commentSql;
            if (userRole.getIsUser()) {
                commentSql = String.format(Locale.ENGLISH, "COMMENT ON USER %s IS %s;",
                        ServerObject.getQualifiedObjectName(userRoleName),
                        ServerObject.getLiteralName(userRole.getComment()));
            } else {
                commentSql = String.format(Locale.ENGLISH, "COMMENT ON ROLE %s IS %s;",
                        ServerObject.getQualifiedObjectName(userRoleName),
                        ServerObject.getLiteralName(userRole.getComment()));
            }
            previewSqls.add(commentSql);
        }
    }

    /**
     * Adds the sql on lock option.
     *
     * @param userRole the user role
     * @param userRoleName the user role name
     * @param previewSqls the preview sqls
     * @param strBuf the str buf
     * @param initialStr the initial str
     */
    public static void addSqlOnLockOption(UserRole userRole, String userRoleName, List<String> previewSqls,
            StringBuffer strBuf, String initialStr) {
        if (userRole.getIsLock() != null) {
            // alter user firstly, lock latter
            if (userRole.getIsLock()) {
                if (!initialStr.equals(strBuf.toString())) {
                    previewSqls.add(strBuf.toString() + ";");
                }
                if (userRole.getIsUser()) {
                    previewSqls.add(MessageFormat.format("ALTER USER {0} ACCOUNT LOCK;",
                            ServerObject.getQualifiedObjectName(userRoleName)));
                } else {
                    previewSqls.add(MessageFormat.format("ALTER ROLE {0} ACCOUNT LOCK;",
                            ServerObject.getQualifiedObjectName(userRoleName)));
                }
            }
            // unlock firstly, alter user latter
            else {
                if (userRole.getIsUser()) {
                    previewSqls.add(MessageFormat.format("ALTER USER {0} ACCOUNT UNLOCK;",
                            ServerObject.getQualifiedObjectName(userRoleName)));
                } else {
                    previewSqls.add(MessageFormat.format("ALTER ROLE {0} ACCOUNT UNLOCK;",
                            ServerObject.getQualifiedObjectName(userRoleName)));
                }
                if (!initialStr.equals(strBuf.toString())) {
                    previewSqls.add(strBuf.toString() + ";");
                }
            }
        } else {
            if (!initialStr.equals(strBuf.toString())) {
                previewSqls.add(strBuf.toString() + ";");
            }
        }
    }

    /**
     * Adds the replication SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addReplicationSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolReplication() != null) {
            if (userRole.getRolReplication()) {
                strBuf.append("REPLICATION ");
            } else {
                strBuf.append("NOREPLICATION ");
            }
        }
    }

    /**
     * Adds the inherit SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addInheritSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolInherit() != null) {
            if (userRole.getRolInherit()) {
                strBuf.append("INHERIT ");
            } else {
                strBuf.append("NOINHERIT ");
            }
        }
    }

    /**
     * Adds the audit admin SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addAuditAdminSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolAuditAdmin() != null) {
            if (userRole.getRolAuditAdmin()) {
                strBuf.append("AUDITADMIN ");
            } else {
                strBuf.append("NOAUDITADMIN ");
            }
        }
    }

    /**
     * Adds the sysadmin SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addSysadminSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolSystemAdmin() != null) {
            if (userRole.getRolSystemAdmin()) {
                strBuf.append("SYSADMIN ");
            } else {
                strBuf.append("NOSYSADMIN ");
            }
        }
    }

    /**
     * Adds the create DBSQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addCreateDBSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolCreateDb() != null) {
            if (userRole.getRolCreateDb()) {
                strBuf.append("CREATEDB ");
            } else {
                strBuf.append("NOCREATEDB ");
            }
        }
    }

    /**
     * Adds the create role SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addCreateRoleSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolCreateRole() != null) {
            if (userRole.getRolCreateRole()) {
                strBuf.append("CREATEROLE ");
            } else {
                strBuf.append("NOCREATEROLE ");
            }
        }
    }

    /**
     * Adds the login SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addLoginSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolCanLogin() != null) {
            if (userRole.getRolCanLogin()) {
                strBuf.append("LOGIN ");
                userRole.setIsUser(true);
            } else {
                strBuf.append("NOLOGIN ");
                userRole.setIsUser(false);
            }
        }
    }

    /**
     * Adds the resource pool SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addResourcePoolSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolResPool() != null && !userRole.getRolResPool().isEmpty()) {
            String resourcePoolName = ServerObject.getQualifiedObjectName(userRole.getRolResPool());
            resourcePoolName = resourcePoolName.contains("\"") ? resourcePoolName : "'" + resourcePoolName + "'";
            strBuf.append("RESOURCE POOL ").append(resourcePoolName).append(" ");
        }
    }

    /**
     * Adds the valid cluase SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addValidCluaseSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolValidBegin() != null) {
            String formatBeginDate = new SimpleDateFormat(MPPDBIDEConstants.USER_ROLE_DATE_DISPLAY_FORMAT)
                    .format(userRole.getRolValidBegin());
            strBuf.append("VALID BEGIN '").append(formatBeginDate).append("' ");
        }
        if (userRole.getRolValidUntil() != null) {
            String formatUntilDate = new SimpleDateFormat(MPPDBIDEConstants.USER_ROLE_DATE_DISPLAY_FORMAT)
                    .format(userRole.getRolValidUntil());
            strBuf.append("VALID UNTIL '").append(formatUntilDate).append("' ");
        }
    }

    /**
     * Adds the connection limit SQL str.
     *
     * @param userRole the user role
     * @param strBuf the str buf
     */
    public static void addConnectionLimitSQLStr(UserRole userRole, StringBuffer strBuf) {
        if (userRole.getRolConnLimit() != null) {
            strBuf.append("CONNECTION LIMIT ").append(userRole.getRolConnLimit()).append(" ");
        }
    }

    /**
     * Gets the ddl.
     *
     * @param conn the conn
     * @param userRole the user role
     * @return the ddl
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static String getDDL(DBConnection conn, UserRole userRole)
            throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        final String rolQry = String.format(Locale.ENGLISH, UserRoleManagerHidden.ROLE_QUERY, userRole.getOid());

        ResultSet roleResultSet = null;

        try {
            roleResultSet = conn.execSelectAndReturnRs(rolQry);
            boolean hasNext = roleResultSet.next();
            while (hasNext) {
                addCreateRoleStr(userRole, sb, roleResultSet);

                addKeywordsInQuery(sb, roleResultSet);

                if (-1 != roleResultSet.getInt("rolconnlimit")) {
                    sb.append("CONNECTION LIMIT " + roleResultSet.getInt("rolconnlimit") + " ");
                }

                if (null != roleResultSet.getDate("rolvalidbegin")) {
                    sb.append("VALID BEGIN ");
                    sb.append("'" + roleResultSet.getDate("rolvalidbegin").toString() + "' ");
                }

                if (null != roleResultSet.getDate("rolvaliduntil")) {
                    sb.append("VALID UNTIL ");
                    sb.append("'" + roleResultSet.getDate("rolvaliduntil").toString() + "' ");
                }

                if (roleResultSet.getString("rolrespool").length() > 0) {
                    sb.append("RESOURCE POOL ");
                    sb.append("'" + roleResultSet.getString("rolrespool") + "' ");
                }

                hasNext = roleResultSet.next();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(roleResultSet);
        }

        addMemberDDL(conn, userRole, sb);

        return sb.toString();
    }

    /**
     * Adds the keywords in query.
     *
     * @param sb the sb
     * @param roleResultSet the role result set
     * @throws SQLException the SQL exception
     */
    public static void addKeywordsInQuery(StringBuilder sb, ResultSet roleResultSet) throws SQLException {
        if (roleResultSet.getBoolean("rolsystemadmin")) {
            sb.append("SYSADMIN ");
        }

        if (roleResultSet.getBoolean("rolauditadmin")) {
            sb.append("AUDITADMIN ");
        }

        if (roleResultSet.getBoolean("rolcreatedb")) {
            sb.append("CREATEDB ");
        }

        if (roleResultSet.getBoolean("rolcreaterole")) {
            sb.append("CREATEROLE ");
        }

        if (roleResultSet.getBoolean("rolinherit")) {
            sb.append("INHERIT ");
        }

        if (roleResultSet.getBoolean("rolcanlogin")) {
            sb.append("LOGIN ");
        }

        if (roleResultSet.getBoolean("rolreplication")) {
            sb.append("REPLICATION ");
        }
    }

    /**
     * Adds the create role str.
     *
     * @param userRole the user role
     * @param sb the sb
     * @param roleResultSet the role result set
     * @throws SQLException the SQL exception
     */
    public static void addCreateRoleStr(UserRole userRole, StringBuilder sb, ResultSet roleResultSet)
            throws SQLException {
        if (roleResultSet.getBoolean("rolcanlogin")) {
            sb.append("CREATE USER ");
            sb.append(userRole.getQualifiedObjectName() + " ");
        } else {
            sb.append("CREATE ROLE ");
            sb.append(userRole.getQualifiedObjectName() + " ");
        }
    }

    /**
     * Adds the member DDL.
     *
     * @param conn the conn
     * @param userRole the user role
     * @param sb the sb
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void addMemberDDL(DBConnection conn, UserRole userRole, StringBuilder sb)
            throws DatabaseCriticalException, DatabaseOperationException {
        final String membershipQuery = String.format(Locale.ENGLISH, UserRoleManagerHidden.MEMBER_QUERY,
                userRole.getOid());
        List<String> names = new ArrayList<>();
        List<String> admin = new ArrayList<>();

        executeMemberShipQuery(conn, membershipQuery, names, admin);

        if (!names.isEmpty()) {
            sb.append("ROLE ");
            sb.append(String.join(",", names) + " ");
        }

        if (!admin.isEmpty()) {
            sb.append("ADMIN ");
            sb.append(String.join(",", admin) + " ");
        }

        sb.append("PASSWORD '********'");
        sb.append(";");

        sb.append(MPPDBIDEConstants.LINE_SEPARATOR);

        addCommentsInQuery(conn, userRole, sb);
    }

    /**
     * Adds the comments in query.
     *
     * @param conn the conn
     * @param userRole the user role
     * @param sb the sb
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void addCommentsInQuery(DBConnection conn, UserRole userRole, StringBuilder sb)
            throws DatabaseCriticalException, DatabaseOperationException {

        ResultSet descriptionResultSet = null;
        try {
            final String qry = String.format(Locale.ENGLISH, UserRoleManagerHidden.DESCIPTIONQUERY, userRole.getOid());
            descriptionResultSet = conn.execSelectAndReturnRs(qry);
            boolean hasNext = descriptionResultSet.next();
            while (hasNext) {
                if (descriptionResultSet.getString("description").length() > 0
                        && !(descriptionResultSet.getString("description") == null)) {
                    sb.append("COMMENT ON ROLE ");
                    sb.append(userRole.getQualifiedObjectName());
                    sb.append(" IS ");
                    sb.append(null == descriptionResultSet.getString("description") ? "NULL"
                            : ServerObject.getLiteralName(descriptionResultSet.getString("description")));
                    sb.append(";");

                }
                hasNext = descriptionResultSet.next();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(descriptionResultSet);
        }
    }

    /**
     * Execute member ship query.
     *
     * @param conn the conn
     * @param membershipQuery the membership query
     * @param names the names
     * @param admin the admin
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void executeMemberShipQuery(DBConnection conn, final String membershipQuery, List<String> names,
            List<String> admin) throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet memberResultSet = null;

        try {
            memberResultSet = conn.execSelectAndReturnRs(membershipQuery);
            boolean hasNext = memberResultSet.next();
            while (hasNext) {
                names.add(memberResultSet.getString("rolname"));
                if (memberResultSet.getBoolean("admin_option")) {
                    admin.add(memberResultSet.getString("rolname"));
                }
                hasNext = memberResultSet.next();
            }
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(memberResultSet);
        }
    }

    /**
     * Alter user role.
     *
     * @param conn the conn
     * @param sqls the sqls
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static void alterUserRole(DBConnection conn, List<String> sqls) throws MPPDBIDEException {
        for (String sql : sqls) {
            conn.execNonSelect(sql);
        }
    }

    /**
     * Fetch description of user role.
     *
     * @param conn the conn
     * @param userRole the user role
     * @return the string
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static String fetchDescriptionOfUserRole(DBConnection conn, UserRole userRole)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT pg_catalog.shobj_description(?::oid, 'pg_authid') description;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectForSearch(query, String.valueOf(userRole.getOid()));
            while (rs.next()) {
                return rs.getString("description");
            }
            return null;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Fetch resource pool.
     *
     * @param conn the conn
     * @return the list
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static List<String> fetchResourcePool(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        List<String> resourcePoolNames = new ArrayList<>();
        String query = "SELECT respool_name FROM pg_resource_pool;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectAndReturnRs(query);
            while (rs.next()) {
                resourcePoolNames.add(rs.getString("respool_name"));
            }
            return resourcePoolNames;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Checks if is sys admin.
     *
     * @param conn the conn
     * @return true, if is sys admin
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static boolean isSysAdmin(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT rolsystemadmin FROM pg_catalog.pg_roles WHERE rolname = user;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectAndReturnRs(query);
            while (rs.next()) {
                return rs.getBoolean("rolsystemadmin");
            }
            return false;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Gets the user role name by oid.
     *
     * @param conn the conn
     * @param oid the oid
     * @return the user role name by oid
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static String getUserRoleNameByOid(DBConnection conn, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT rolname FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectForSearch(query, String.valueOf(oid));
            while (rs.next()) {
                return rs.getString(1);
            }
            return null;
        } catch (SQLException exception) {
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }
    
    /**
     * Gets the user or role by oid.
     *
     * @param conn the conn
     * @param oid the oid
     * @return the user role name by oid
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static boolean getUserRoleLoginByOid(DBConnection conn, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT rolcanlogin FROM pg_catalog.pg_roles WHERE oid = ?::oid;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectForSearch(query, String.valueOf(oid));
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Fetch lock status of user role.
     *
     * @param conn the conn
     * @param userRole the user role
     * @return the boolean
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static Boolean fetchLockStatusOfUserRole(DBConnection conn, UserRole userRole) throws MPPDBIDEException {
        String query = "SELECT rolstatus FROM pg_catalog.pg_user_status WHERE roloid = ?::oid;";
        ResultSet rs = null;
        try {
            rs = conn.execSelectForSearch(query, String.valueOf(userRole.getOid()));
            while (rs.next()) {
                if (rs.getInt("rolstatus") != 0) {
                    return true;
                } else {
                    return false;
                }
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader
                    .getProperty(IMessagesConstants.ERR_FETCH_USER_ROLE_LOCK_STATUS, userRole.getOid()));
            throw new MPPDBIDEException(IMessagesConstants.ERR_FETCH_USER_ROLE_LOCK_STATUS, userRole.getOid());
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Copy properties.
     *
     * @param source the source
     * @param destination the destination
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public static void copyProperties(UserRole source, UserRole destination) throws MPPDBIDEException {
        destination.setOid(source.getOid());
        destination.setName(source.getName());
        destination.setRolConnLimit(source.getRolConnLimit());
        destination.setRolValidBegin(source.getRolValidBegin());
        destination.setRolValidUntil(source.getRolValidUntil());
        destination.setRolResPool(source.getRolResPool());
        destination.setComment(source.getComment());
        destination.setRolCanLogin(source.getRolCanLogin());
        destination.setRolCreateRole(source.getRolCreateRole());
        destination.setRolCreateDb(source.getRolCreateDb());
        destination.setRolSystemAdmin(source.getRolSystemAdmin());
        destination.setRolAuditAdmin(source.getRolAuditAdmin());
        destination.setRolInherit(source.getRolInherit());
        destination.setRolReplication(source.getRolReplication());
        destination.setIsLock(source.getIsLock());
        destination.setParents(source.getParents());
    }

    /**
     * Form create query.
     *
     * @param userRole the user role
     * @return the string
     */
    public static String formCreateQuery(UserRole userRole) {
        StringBuffer query = new StringBuffer(512);

        /* CREATE */
        query.append("CREATE ");

        appendUserKeyword(userRole, query);

        appendRoleKeyword(userRole, query);

        query.append(userRole.getQualifiedObjectName() + " WITH");

        query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");

        appendSysadminPermission(userRole, query);

        appendAuditAdminPermission(userRole, query);

        appendCreateDBPermission(userRole, query);

        appendCreateRolePermission(userRole, query);

        appendInheritPermission(userRole, query);

        appendLoginPermission(userRole, query);

        appendReplicationPermission(userRole, query);

        appendConnectionLimit(userRole, query);

        appendValidity(userRole, query);

        appendResourcePool(userRole, query);
        // Add the getQualifiedObjectName in createuserrole class for every
        // element
        appendRoleSQLStr(userRole, query);
        // Add the getQualifiedObjectName in createuserrole class for every
        // element
        addObjectAdminSQLStr(userRole, query);

        addPasswordSQLStr(userRole, query);

        query.append(";");

        return query.toString();
    }

    /**
     * Adds the password SQL str.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void addPasswordSQLStr(UserRole userRole, StringBuffer query) {
        char[] password = userRole.getPasswordInput();

        query.append("PASSWORD ");
        query.append("'").append(password).append("'");
        clear(password);
    }

    /**
     * Adds the object admin SQL str.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void addObjectAdminSQLStr(UserRole userRole, StringBuffer query) {
        if (!userRole.getAdminComo().isEmpty()) {
            query.append("ADMIN ");
            query.append(userRole.getAdminComo() + " ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append role SQL str.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendRoleSQLStr(UserRole userRole, StringBuffer query) {
        if (!userRole.getRoleCombo().isEmpty()) {
            query.append("ROLE ");
            query.append(userRole.getRoleCombo() + " ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append resource pool.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendResourcePool(UserRole userRole, StringBuffer query) {
        if (!userRole.getRolResPool().isEmpty() && !"\"\"".equals(userRole.getRolResPool())) {
            query.append("RESOURCE POOL ");
            query.append("'" + ServerObject.getQualifiedObjectName(userRole.getRolResPool()) + "' ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append validity.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendValidity(UserRole userRole, StringBuffer query) {
        if (null != userRole.getBeginTime() && !(userRole.getBeginTime().equals("<choose date>"))) {
            query.append("VALID BEGIN ");
            query.append("'" + userRole.getBeginTime() + "' ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }

        if (null != userRole.getUntilTime() && !(userRole.getUntilTime().equals("<choose date>"))) {
            query.append("VALID UNTIL ");
            query.append("'" + userRole.getUntilTime() + "' ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append connection limit.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendConnectionLimit(UserRole userRole, StringBuffer query) {
        if (null != userRole.getRolConnLimit() && -1 != userRole.getRolConnLimit()) {
            query.append("CONNECTION LIMIT ");
            query.append(userRole.getRolConnLimit() + " ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append replication permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendReplicationPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolReplication()) {
            query.append("REPLICATION ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append login permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendLoginPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolCanLogin()) {
            query.append("LOGIN ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append inherit permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendInheritPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolInherit()) {
            query.append("INHERIT ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append create role permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendCreateRolePermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolCreateRole()) {
            query.append("CREATEROLE ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append create DB permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendCreateDBPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolCreateDb()) {
            query.append("CREATEDB ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append audit admin permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendAuditAdminPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getAuditAdmin()) {
            query.append("AUDITADMIN ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append sysadmin permission.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendSysadminPermission(UserRole userRole, StringBuffer query) {
        if (userRole.getRolSystemAdmin()) {
            query.append("SYSADMIN ");
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
        }
    }

    /**
     * Append role keyword.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendRoleKeyword(UserRole userRole, StringBuffer query) {
        if (userRole.getRole()) {
            query.append("ROLE ");
        }
    }

    /**
     * Append user keyword.
     *
     * @param userRole the user role
     * @param query the query
     */
    public static void appendUserKeyword(UserRole userRole, StringBuffer query) {
        if (userRole.getUser()) {
            query.append("USER ");
        }
    }

    /**
     * Form role comment query.
     *
     * @param userRole the user role
     * @return the string
     */
    public static String formRoleCommentQuery(UserRole userRole) {
        StringBuffer buff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if ((null != userRole.getComment()) && (userRole.getComment().length() > 0)) {
            buff.append(MPPDBIDEConstants.LINE_SEPARATOR).append(formSetCommentQuery(userRole));
        }
        return buff.toString();
    }

    /**
     * Form set comment query.
     *
     * @param userRole the user role
     * @return the string
     */
    public static String formSetCommentQuery(UserRole userRole) {

        StringBuilder commentQry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        commentQry.append("COMMENT ON ROLE ");
        commentQry.append(userRole.getQualifiedObjectName());
        commentQry.append(" IS ");
        commentQry.append(null == userRole.getComment() ? "NULL" : ServerObject.getLiteralName(userRole.getComment()));
        commentQry.append(";");
        return commentQry.toString();

    }

    /**
     * Exec create.
     *
     * @param conn the conn
     * @param userRole the user role
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void execCreate(DBConnection conn, UserRole userRole)
            throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder buff = new StringBuilder(formCreateQuery(userRole)).append(formRoleCommentQuery(userRole));
        conn.execNonSelect(buff.toString());
        userRole.clearPwd();

    }

    /**
     * Exec drop.
     *
     * @param conn the conn
     * @param userRole the user role
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void execDrop(DBConnection conn, UserRole userRole)
            throws DatabaseCriticalException, DatabaseOperationException {
        String dropQry = null;
        if (userRole.getRolCanLogin()) {
            dropQry = "DROP USER " + userRole.getQualifiedObjectName() + ";";
        } else {
            dropQry = "DROP ROLE " + userRole.getQualifiedObjectName() + ";";
        }
        conn.execNonSelect(dropQry);
    }

    /**
     * Clear.
     *
     * @param passwordInput the password input
     */
    public static void clear(char[] passwordInput) {
        for (int index = 0; index < passwordInput.length; index++) {
            passwordInput[index] = 0;
        }
    }

}
