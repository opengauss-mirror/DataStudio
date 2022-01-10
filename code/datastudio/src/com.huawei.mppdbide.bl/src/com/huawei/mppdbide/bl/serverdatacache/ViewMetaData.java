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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.SortedMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewColumnList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
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
 * Description: The Class ViewMetaData.
 * 
 */

public class ViewMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject, IViewMetaData {

    private Namespace namespace;
    private String source;
    private String owner;
    private ViewColumnList columns;

    private static final String CASCADE = " CASCADE";
    private boolean isLevel3Loaded;
    private boolean isLevel3LoadInProgress;
    private boolean isViewCodeLoaded;
    private ViewManager viewManager;
    private Database database;
    private String relkind = "v";

    /**
     * Instantiates a new view meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param namespace the namespace
     */
    public ViewMetaData(long oid, String name, Namespace namespace, Database database) {
        super(oid, name, OBJECTTYPE.VIEW_META_DATA, validateNamespace(namespace));
        this.namespace = namespace;
        this.columns = new ViewColumnList(this);
        this.isViewCodeLoaded = false;
        this.viewManager = new ViewManager();
        this.database = database;
    }

    private static boolean validateNamespace(Namespace namespace) {
        return namespace != null ? namespace.getPrivilegeFlag() : true;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public Namespace getNamespace() {
        return this.namespace;
    }

    /**
     * Sets the namespace.
     *
     * @param namespace the new namespace
     */
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
        privilegeFlag = namespace.getPrivilegeFlag();
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return this.source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Sets the relkind.
     *
     * @param relkind the new relkind
     */
    public void setRelKind(String relkind) {
        if (relkind != null) {
            if (!relkind.equals("v")) {
                this.relkind = "m";
            }
        }
    }

    /**
     * Gets the relkind.
     *
     * @return the relkind
     */
    public String getRelKind() {
        return this.relkind;
    }
 
    /** 
    * Gets the materview String 
    *
    * @return String materview str 
    */
    public String getMaterViewString() {
        return getRelKind().equals("v") ? "" : "MATERIALIZED ";
    }

    /**
    * get orReplace String
    *
    * @return String orReplace str
    */
    public String getOrReplaceString() {
        return getRelKind().equals("v") ? "OR REPLACE " : "";
    }

    /**
     * Gets the owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Checks if is level 3 loaded.
     *
     * @return true, if is level 3 loaded
     */
    public boolean isLevel3Loaded() {
        return isLevel3Loaded;
    }

    /**
     * Sets the level 3 loaded.
     *
     * @param isLvl3Loaded the new level 3 loaded
     */
    public void setLevel3Loaded(boolean isLvl3Loaded) {
        this.isLevel3Loaded = isLvl3Loaded;
    }

    /**
     * Checks if is level 3 load in progress.
     *
     * @return true, if is level 3 load in progress
     */
    public boolean isLevel3LoadInProgress() {
        return isLevel3LoadInProgress;
    }

    /**
     * Sets the level 3 load in progress.
     *
     * @param isLvl3LoadInProgress the new level 3 load in progress
     */
    public void setLevel3LoadInProgress(boolean isLvl3LoadInProgress) {
        this.isLevel3LoadInProgress = isLvl3LoadInProgress;
    }

    @Override
    public String getSearchName() {
        return getName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public ViewColumnList getColumns() {
        return this.columns;
    }

    /**
     * Sets the columns.
     *
     * @param columns the new columns
     */
    public void setColumns(ViewColumnList columns) {
        this.columns = columns;
    }

    /**
     * Gets the ddl.
     *
     * @param db the db
     * @return the ddl
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public String getDDL(Database db) throws MPPDBIDEException {
        return viewManager.getDDL(db);
    }

    /**
     * Gets the display name.
     *
     * @return the display name
     */
    public String getDisplayName() {
        return getNamespace().getDisplayName() + '.' + super.getDisplayName();
    }

    /**
     * Gets the drop query for OB.
     *
     * @param isAppendCascade the is append cascade
     * @return the drop query for OB
     */
    public String getDropQueryForOB(boolean isAppendCascade) {
        return viewManager.getDropQueryForOB(isAppendCascade);
    }

    /**
     * Rename.
     *
     * @param newName the new name
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void rename(String newName, DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        viewManager.rename(newName, conn, this);
    }

    /**
     * Sets the namespace to.
     *
     * @param userInput the user input
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setNamespaceTo(String userInput, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        viewManager.setNamespaceTo(userInput, conn, this);
    }

    /**
     * Find matching child object.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ViewColumnMetaData> findMatchingChildObject(String prefix) {
        return columns.findMatching(prefix);
    }

    @Override
    public int hashCode() {
        return MPPDBIDEConstants.PRIME_31 + Long.valueOf(getOid()).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }

        if (!(obj instanceof ViewMetaData)) {
            return false;
        }

        ViewMetaData other = (ViewMetaData) obj;
        if (validateObjects(other)) {
            return true;
        }

        return false;
    }

    private boolean validateObjects(ViewMetaData other) {
        return this.getOid() == other.getOid() && this.getNamespace().equals(other.getNamespace());
    }

    /**
     * Adds the view column.
     *
     * @param rs the rs
     * @param type the type
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public void addViewColumn(ResultSet rs, TypeMetaData type)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        if (isLevel3Loaded()) {
            return;
        }

        ViewColumnMetaData viewCol = ViewColumnMetaData.convertToViewColumnMetaData(rs, this, type);
        this.getColumns().addItem(viewCol);
    }

    /**
     * Checks if is view code loaded.
     *
     * @return true, if is view code loaded
     */
    public boolean isViewCodeLoaded() {
        return this.isViewCodeLoaded;
    }

    /**
     * Sets the view code loaded.
     *
     * @param isViewCodeLoad the new view code loaded
     */
    public void setViewCodeLoaded(boolean isViewCodeLoad) {
        this.isViewCodeLoaded = isViewCodeLoad;
    }

    @Override
    public Object[] getChildren() {
        Object[] objs = {this.getColumns()};
        return objs;
    }

    /**
     * Refresh selfdata.
     *
     * @param conn the conn
     * @param isRenameFlow the is rename flow
     * @return the view meta data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public ViewMetaData refreshSelfdata(DBConnection conn, boolean isRenameFlow)
            throws DatabaseCriticalException, DatabaseOperationException {
        return viewManager.refreshSelfdata(conn, isRenameFlow, this);
    }

    /**
     * Clear details cached.
     */
    private void clearDetailsCached() {
        this.getColumns().clear();
        this.setLevel3Loaded(false);
    }

    /**
     * Fill view properties from RS.
     *
     * @param rs the rs
     * @return true, if successful
     * @throws SQLException the SQL exception
     */
    private boolean fillViewPropertiesFromRS(ResultSet rs) throws SQLException {
        boolean isBasePropertyChanged = false;
        String name = rs.getString("viewname");
        if (!name.equals(this.getName())) {
            this.setName(name);
            isBasePropertyChanged = true;
        }
        this.setOwner(rs.getString("viewowner"));
        this.setViewCodeLoaded(false);
        return isBasePropertyChanged;
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return super.isLoaded;
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    /**
     * Find all child objects.
     *
     * @return the sorted map
     */
    public SortedMap<String, ViewColumnMetaData> findAllChildObjects() {
        return this.findMatchingChildObject("");
    }

    /**
     * Addview to search pool.
     *
     * @param view the view
     */
    public void addviewToSearchPool(ViewMetaData view) {
        namespace.addViewInSearchPool(view);
    }

    /**
     * Adds the views to group.
     *
     * @param view the view
     */
    public void addViewsToGroup(ViewMetaData view) {
        namespace.addViewsToGroup(view);
    }

    /**
     * Fetch view column info.
     *
     * @param view the view
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void fetchViewColumnInfo(ViewMetaData view, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        namespace.fetchViewColumnInfo(view, conn);
    }

    @Override
    public String getDropQuery(boolean isAppendCascade) {
        return viewManager.getDropQuery(isAppendCascade);
    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(ServerObject obj) {
        if (obj instanceof ViewColumnMetaData) {
            this.removeColumn(obj);
        }
    }

    private void removeColumn(ServerObject obj) {
        if (columns != null) {
            columns.remove((ViewColumnMetaData) obj);
        }
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        if (exportType == EXPORTTYPE.SQL_DDL) {
            return true;
        }
        return false;
    }

    @Override
    public String getNamespaceQualifiedName() {
        return getNamespace().getQualifiedObjectName();
    }

    @Override
    public String getViewPathQualifiedName() {
        return getNamespace().getQualifiedObjectName() + '.' + getQualifiedObjectName();
    }

    @Override
    public String getNameSpaceName() {
        return getNamespace().getName();
    }

    @Override
    public ServerObject getParent() {
        return getNamespace();
    }

    @Override
    public void dropView(DBConnection connection, boolean isAppendCascade)
            throws DatabaseOperationException, DatabaseCriticalException {
        String query = getDropQueryForOB(isAppendCascade);
        connection.execNonSelect(query);
        this.getDatabase().getSearchPoolManager().removeViewFromSearchPool(this);
        this.getNamespace().removeViewFromGroup(getOid());
    }

    @Override
    public boolean isDbConnected() {
        return this.getDatabase().isConnected();
    }

    /**
     * Checks if view is dropped.
     *
     * @return true, if view is dropped
     */
    public boolean isViewDropped() {
        Namespace ns = this.getNamespace();
        if (null == ns) {
            return true;
        }
        return ns.validateView(getOid());
    }

    private class ViewManager {
        /**
         * getDropQuery
         * 
         * @param isAppendCascade boolean
         * @return string object
         */
        public String getDropQuery(boolean isAppendCascade) {
            String dropQuery = String.format(Locale.ENGLISH,
                    "DROP %sVIEW IF EXISTS ",
                    getMaterViewString());
            StringBuilder query = new StringBuilder(dropQuery);
            query.append(getNamespace().getQualifiedObjectName()).append('.').append(getQualifiedObjectName());

            if (isAppendCascade) {
                query.append(CASCADE);
            }

            return query.toString();
        }

        /**
         * Refresh selfdata.
         *
         * @param conn the conn
         * @param isRenameFlow the is rename flow
         * @return the view meta data
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        public ViewMetaData refreshSelfdata(DBConnection conn, boolean isRenameFlow, ViewMetaData viewMetaData)
                throws DatabaseCriticalException, DatabaseOperationException {
            boolean privilegeFilter = privilegeFlag && !isRenameFlow;
            String query = ViewUtils.getViewQuery(viewMetaData.getOid(), privilegeFilter);
            ResultSet rs = null;
            boolean hasNext = false;

            try {
                rs = conn.execSelectAndReturnRs(query);
                hasNext = rs.next();
                if (hasNext) {
                    // the cleared details[column] will loaded after this
                    // method,where column details are got.
                    viewMetaData.clearDetailsCached();
                    viewMetaData.setNamespace(getUpdatedNamespaceForView(rs));
                    viewMetaData.fillViewPropertiesFromRS(rs);
                } else {
                    return null;
                }
            } catch (SQLException exception) {
                GaussUtils.handleCriticalException(exception);

                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            } finally {
                conn.closeResultSet(rs);
            }
            return viewMetaData;
        }

        /**
         * Gets the updated namespace for view.
         *
         * @param rs the rs
         * @return the updated namespace for view
         * @throws DatabaseOperationException the database operation exception
         */
        private Namespace getUpdatedNamespaceForView(ResultSet rs) throws DatabaseOperationException {
            Namespace ns = null;
            try {
                long nsID = rs.getLong("nspoid");
                ns = getDatabase().getNameSpaceById(nsID);
            } catch (SQLException exception) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID), exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            }
            return ns;
        }

        /**
         * Rename.
         *
         * @param newName the new name
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void rename(String newName, DBConnection conn, ViewMetaData viewMetaData)
                throws DatabaseOperationException, DatabaseCriticalException {
            String query = getRenameQuery(newName);
            conn.execNonSelect(query);
            getNamespace().refreshView(viewMetaData, conn, true);
        }

        /**
         * Sets the namespace to.
         *
         * @param userInput the user input
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void setNamespaceTo(String userInput, DBConnection conn, ViewMetaData viewMetaData)
                throws DatabaseOperationException, DatabaseCriticalException {
            String query = getSchemaSetQuery(userInput);
            conn.execNonSelect(query);
            getNamespace().refreshView(viewMetaData, conn, false);
        }

        /**
         * Gets the schema set query.
         *
         * @param newSchemaName the new schema name
         * @return the schema set query
         */
        private String getSchemaSetQuery(String newSchemaName) {
            return String.format(Locale.ENGLISH, "ALTER %sVIEW %s.%s" + " SET schema %s;",
                    getMaterViewString(),
                    getNamespace().getQualifiedObjectName(), getQualifiedObjectName(),
                    ServerObject.getQualifiedObjectName(newSchemaName));

        }

        /**
         * Gets the rename query.
         *
         * @param newName the new name
         * @return the rename query
         */
        private String getRenameQuery(String newName) {
            String qry = String.format(Locale.ENGLISH, "ALTER %sVIEW %s.%s RENAME TO %s;",
                    getMaterViewString(),
                    getNamespace().getQualifiedObjectName(), getQualifiedObjectName(),
                    ServerObject.getQualifiedObjectName(newName));
            return qry;
        }

        /**
         * Gets the drop query for OB.
         *
         * @param isAppendCascade the is append cascade
         * @return the drop query for OB
         */
        public String getDropQueryForOB(boolean isAppendCascade) {
            String dropQuery = String.format(Locale.ENGLISH, "DROP %sVIEW %s.%s ",
                    getMaterViewString(), 
                    getNamespace().getQualifiedObjectName(),
                    getQualifiedObjectName());
            StringBuilder query = new StringBuilder(dropQuery);

            if (isAppendCascade) {
                query.append(CASCADE);
            }

            return query.toString();
        }

        /**
         * Gets the ddl.
         *
         * @param db the db
         * @return the ddl
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        public String getDDL(Database db) throws MPPDBIDEException {
            if (!isViewCodeLoaded()) {
                fetchDDL(db);
            }

            StringBuilder strbldr = getViewTemplate();
            return strbldr.toString();
        }

        private StringBuilder getViewTemplate() {
            StringBuilder strbldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            if (isViewCodeLoaded()) {
                strbldr.append(String.format(Locale.ENGLISH, "CREATE %s%sVIEW ",
                        getOrReplaceString(), getMaterViewString()));
                strbldr.append(namespace.getQualifiedObjectName()).append(".").append(getQualifiedObjectName())
                        .append(System.lineSeparator()).append("\tAS ").append(System.lineSeparator())
                        .append(getSource());
            }
            return strbldr;
        }

        /**
         * Fetch DDL.
         *
         * @param db the db
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        private void fetchDDL(Database db) throws MPPDBIDEException {
            DBConnection conn = db.getConnectionManager().getFreeConnection();
            ResultSet rs = null;
            try {
                rs = fetchViewDDLTemplate(conn);
            } catch (SQLException exception) {
                GaussUtils.handleCriticalException(exception);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
            } finally {
                conn.closeResultSet(rs);
                db.getConnectionManager().releaseConnection(conn);
            }
        }

        private ResultSet fetchViewDDLTemplate(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException, SQLException {
            String fetchSiurceCodeQuery = String.format(Locale.ENGLISH, "SELECT * FROM pg_get_viewdef('%s')",
                    getDisplayName());
            ResultSet rs = conn.execSelectAndReturnRs(fetchSiurceCodeQuery);
            boolean hasNext = rs.next();
            if (hasNext) {
                setSource(rs.getString(1));
                setViewCodeLoaded(true);
            }
            return rs;
        }

    }
}
