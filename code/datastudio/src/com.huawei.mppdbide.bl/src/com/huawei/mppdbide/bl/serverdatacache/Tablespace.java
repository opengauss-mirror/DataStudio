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

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
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
 * Description: The Class Tablespace.
 * 
 */

public class Tablespace extends ServerObject implements GaussOLAPDBMSObject {

    private Server server;

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return server;
    }

    private String[] fileOption;
    private String location;
    private String maxsize;
    private TablespaceType tspctype;
    private boolean privilege;
    private boolean isRelative;

    /**
     * Instantiates a new tablespace.
     *
     * @param oid the oid
     * @param name the name
     * @param location the location
     * @param maxsize the maxsize
     * @param options the options
     * @param server the server
     * @param tspctype the tspctype
     * @param privilege the privilege
     * @param isRelative the is relative
     */
    public Tablespace(long oid, String name, String location, String maxsize, String[] options, Server server,
            TablespaceType tspctype, boolean privilege, boolean isRelative) {
        super(oid, name, OBJECTTYPE.TABLESPACE, server.getPrivilegeFlag());
        this.setFileOption(options);
        this.server = server;
        this.location = location;
        this.maxsize = maxsize;
        this.tspctype = tspctype;
        this.privilege = privilege;
        this.isRelative = isRelative;
    }

    @Override
    public Object getParent() {
        return this.getServer();
    }

    /**
     * Convert to tablesapce.
     *
     * @param rs the rs
     * @param server the server
     * @param privilege the privilege
     * @return the tablespace
     * @throws DatabaseOperationException the database operation exception
     */
    public static Tablespace convertToTablesapce(ResultSet rs, Server server, boolean privilege)
            throws DatabaseOperationException {
        long oid = 0;
        String name = null;
        String location = null;
        String[] options = null;
        String maxsize = null;
        TablespaceType tspctype = TablespaceType.NORMAL;
        try {
            oid = rs.getLong("oid");
            name = rs.getString("spcname");
            Array spcOptions = rs.getArray("spcoptions");
            if (spcOptions != null) {
                options = (String[]) spcOptions.getArray();
                if (options.length > 0 && "filesystem=hdfs".equalsIgnoreCase(options[0])) {
                    tspctype = TablespaceType.HDFS;
                }
            } else {
                options = new String[0];
            }
            if (privilege) {
                location = rs.getString("location");
            } 
            boolean isRelative = false;
            maxsize = rs.getString("spcmaxsize");
            isRelative = rs.getBoolean("relative");
            Tablespace tablespace = new Tablespace(oid, name, location, maxsize, options, server, tspctype, privilege,
                    isRelative);
            return tablespace;
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        }

    }

    /**
     * Gets the file option.
     *
     * @return the file option
     */
    public String getFileOption() {
        int len = fileOption.length;
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String[] tempStr = null;
        String key = null;
        for (int index = 0; index < len; index++) {
            sb.append(',');
            tempStr = fileOption[index].split("=", 2);
            key = tempStr[0].trim();
            if ((key.startsWith("cfgpath") || key.startsWith("storepath") || key.startsWith("address"))
                    && tempStr.length > 1) {
                sb.append(tempStr[0]).append('=');
                sb.append(ServerObject.getLiteralName(tempStr[1]));
            } else {
                sb.append(fileOption[index]);
            }
        }
        if (!sb.toString().isEmpty()) {
            sb.deleteCharAt(0);
        }

        return sb.toString();
    }

    /**
     * Sets the file option.
     *
     * @param options the new file option
     */
    private void setFileOption(String[] options) {
        if (options != null) {
            this.fileOption = options.clone();
        }
    }

    /**
     * Rename tablespace.
     *
     * @param newName the new name
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void renameTablespace(String newName, DBConnection connection)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = createRenameQuery(newName);
        connection.execNonSelect(qry);
        this.server.refreshTablespace();
    }

    /**
     * Drop tablespace.
     *
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void dropTablespace(DBConnection connection) throws DatabaseOperationException, DatabaseCriticalException {
        connection.execNonSelect(createDropQuery());
        this.server.refreshTablespace();
    }

    /**
     * Sets the tablespace option.
     *
     * @param query the query
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setTablespaceOption(String query, DBConnection connection)
            throws DatabaseOperationException, DatabaseCriticalException {
        connection.execNonSelect(query);
        this.server.refreshTablespace();
    }

    /**
     * Sets the tablespace size.
     *
     * @param newsize the newsize
     * @param connection the connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setTablespaceSize(String newsize, DBConnection connection)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = resizeQuery(newsize);
        connection.execNonSelect(qry);
        this.server.refreshTablespace();
    }

    /**
     * Refresh.
     *
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws Exception the exception
     */
    public void refresh() throws DatabaseCriticalException, DatabaseOperationException, Exception {
        this.server.refreshTablespaceMetadata(this.getOid());
    }

    /**
     * Gets the ddl.
     *
     * @return the ddl
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public String getDDL() throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (isRelative) {
            sb.append("set enable_absolute_tablespace=off ;");
        } else {
            sb.append("set enable_absolute_tablespace=on ;");
        }
        sb.append("CREATE TABLESPACE ");
        sb.append(this.getDisplayName());
        if (isRelative) {
            sb.append(" RELATIVE");
        }
        if (!"".equalsIgnoreCase(this.location) && null != this.location) {
            sb.append(" LOCATION ");
            sb.append(ServerObject.getLiteralName(this.location));
        }
        if (null != this.maxsize && !"".equalsIgnoreCase(this.maxsize)) {
            sb.append(" MAXSIZE ");
            sb.append(ServerObject.getLiteralName(this.maxsize));
        }
        if (!getFileOption().trim().isEmpty()) {
            sb.append(" WITH ");
            sb.append("( ");
            sb.append(getFileOption());
            sb.append(" )");
        }
        sb.append(" ;");
        return sb.toString();
    }
    
    /**
     * get previlege
     * 
     * @return privilege flag
     */
    public boolean getPrivilege() {
        return this.privilege;
    }

    /**
     * Creates the drop query.
     *
     * @return the string
     */
    public String createDropQuery() {
        String qry = String.format(Locale.ENGLISH, "DROP TABLESPACE IF EXISTS %s", super.getDisplayName());
        return qry;
    }

    /**
     * Creates the rename query.
     *
     * @param newName the new name
     * @return the string
     */
    public String createRenameQuery(String newName) {
        String qry = String.format(Locale.ENGLISH, "ALTER TABLESPACE %s RENAME TO %s ;", super.getDisplayName(),
                ServerObject.getQualifiedObjectName(newName));
        return qry;
    }

    /**
     * Resize query.
     *
     * @param newSize the new size
     * @return the string
     */
    public String resizeQuery(String newSize) {
        String qry = String.format(Locale.ENGLISH, "ALTER TABLESPACE %s RESIZE MAXSIZE %s", this.getDisplayName(),
                ServerObject.getLiteralName(newSize));
        return qry;
    }

    /**
     * Gets the tablespace type.
     *
     * @return the tablespace type
     */
    public TablespaceType getTablespaceType() {
        return this.tspctype;
    }

    /**
     * Fetch all tablespace.
     *
     * @param db the db
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void fetchAllTablespace(Database db) throws DatabaseCriticalException, DatabaseOperationException {
        int counter = 0;
        String qry = "";
        boolean privilege = db.getConnectionManager().execSelectCheckIfAdmin();
        if (!privilege) {
            qry = "select oid,spcname,spcacl," + "spcoptions, spcmaxsize, relative from pg_tablespace";
            if (db.getPrivilegeFlag()) {
                qry += " where has_tablespace_privilege(spcname, 'CREATE')";
            }
            qry += " order by spcname;";
        } else {
            qry = "select oid, pg_tablespace_location(oid) as location ,spcname,spcacl,"
                    + "spcoptions, spcmaxsize, relative from pg_tablespace";
            if (db.getPrivilegeFlag()) {
                qry += " where has_tablespace_privilege(spcname, 'CREATE')";
            }
            qry += " order by spcname;";
        }
        ResultSet rs = null;
        boolean hasMoreRs = false;
        ObjectGroup<Tablespace> tblsGrp = db.getServer().getTablespaceGroup();
        try {
            rs = db.getConnectionManager().execSelectAndReturnRsOnObjBrowserConn(qry);
            hasMoreRs = rs.next();
            while (hasMoreRs) {
                Tablespace tablespace = Tablespace.convertToTablesapce(rs, db.getServer(), privilege);
                counter++;
                tblsGrp.addToGroup(tablespace);
                hasMoreRs = rs.next();
            }
        } catch (SQLException exception) {
            try {
                GaussUtils.handleCriticalException(exception);
            } catch (DatabaseCriticalException dc) {
                throw dc;
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID),
                    exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility.trace("Total number of Tablespaces loaded for selected server is : " + counter);
            }
            db.getConnectionManager().closeRSOnObjBrowserConn(rs);
        }
    }

    /**
     * Gets the tablespace query.
     *
     * @param tableOid the table oid
     * @param privilegeFlag the privilege flag
     * @return the tablespace query
     */
    public static String getTablespaceQuery(long tableOid, boolean privilegeFlag) {
        String qry = "select oid, pg_tablespace_location(oid) as location, "
                + "spcname,spcacl,spcoptions, spcmaxsize, relative from pg_tablespace  where oid =%d";
        if (privilegeFlag) {
            qry += " and has_tablespace_privilege(oid, 'CREATE')";
        }
        qry += ";";
        qry = String.format(Locale.ENGLISH, qry, tableOid);
        return qry;
    }

}