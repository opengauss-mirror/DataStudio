/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.StringTokenizer;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.groups.ColumnList;
import com.huawei.mppdbide.bl.serverdatacache.groups.ConstraintList;
import com.huawei.mppdbide.bl.serverdatacache.groups.IndexList;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.util.ExecTimer;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class TableMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class TableMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject, ITableMetaData {

    private Namespace nameSpace;
    private String tablespaceName;
    private boolean isTempTable;
    private boolean isUnLoggedTable;
    private boolean isIfNotExtists;
    private TableOrientation tabOrientation;
    private boolean hasOidCol;
    private int fillfactor;

    private String distributeOptions;
    private String nodeOptions;

    private ColumnList columns;

    /**
     * The is roll back.
     */
    protected boolean isRollBack = false;

    private boolean isLevel3Loaded;
    private boolean isLevel3LoadInProgress;

    /**
     * The orc version.
     */
    protected String orcVersion;
    private List<String> distributionClmList = new ArrayList<String>(10);

    private String source = "";

    /**
     * Table constraints Note: incase of edit all column level constraints are
     * also come here as it can't be associates with column
     */
    private ConstraintList constraintList;

    /**
     * Index List Note: incase of edit all column level unique indexes are also
     * come here as it can't be associates with column anymore.
     */
    private IndexList indexList;

    private String description;

    /**
     * Instantiates a new table meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param nameSpace the name space
     * @param tableSpace the table space
     * @param type the type
     */
    public TableMetaData(long oid, String name, Namespace nameSpace, String tableSpace, OBJECTTYPE type) {
        super(oid, name, type, nameSpace != null ? nameSpace.getPrivilegeFlag() : true);

        this.nameSpace = nameSpace;
        this.tablespaceName = tableSpace;

        this.fillfactor = 100;

        this.columns = new ColumnList(OBJECTTYPE.COLUMN_GROUP, this);
        this.constraintList = new ConstraintList(OBJECTTYPE.CONSTRAINT_GROUP, this);
        this.indexList = new IndexList(OBJECTTYPE.INDEX_GROUP, this);
        this.isLevel3Loaded = false;
        this.isLevel3LoadInProgress = false;
    }

    /**
     * Instantiates a new table meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param nameSpace the name space
     * @param tableSpace the table space
     */
    public TableMetaData(long oid, String name, Namespace nameSpace, String tableSpace) {
        this(oid, name, nameSpace, tableSpace, OBJECTTYPE.TABLEMETADATA);
    }

    /**
     * Instantiates a new table meta data.
     *
     * @param nameSpace the name space
     */
    public TableMetaData(Namespace nameSpace) {
        this(0, "notablename", nameSpace, null);
    }

    /**
     * Gets the source.
     *
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source the new source
     */
    private void setSource(String source) {
        this.source = source;
    }

    /**
     * Sets the distribute options.
     *
     * @param distributeOptions the new distribute options
     */
    public void setDistributeOptions(String distributeOptions) {
        this.distributeOptions = distributeOptions;
    }

    /**
     * Sets the node options.
     *
     * @param nodeOptions the new node options
     */
    public void setNodeOptions(String nodeOptions) {
        this.nodeOptions = nodeOptions;
    }

    /**
     * Sets the fillfactor.
     *
     * @param fillfactor the new fillfactor
     */
    public void setFillfactor(int fillfactor) {
        this.fillfactor = fillfactor;
    }

    /**
     * Move column.
     *
     * @param index the index
     * @param up the up
     */
    public void moveColumn(int index, boolean up) {
        this.columns.moveItem(index, up);
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public OLAPObjectList<ColumnMetaData> getColumns() {
        return this.columns;
    }

    /**
     * Sets the checks for oid.
     *
     * @param hasOidColumn the new checks for oid
     */
    public void setHasOid(boolean hasOidColumn) {
        this.hasOidCol = hasOidColumn;
    }

    /**
     * Sets the tablespace name.
     *
     * @param tablespaceName the new tablespace name
     */
    public void setTablespaceName(String tablespaceName) {
        this.tablespaceName = tablespaceName;
    }

    /**
     * Sets the temp table.
     *
     * @param isTempTble the new temp table
     */
    public void setTempTable(boolean isTempTble) {
        this.isTempTable = isTempTble;
    }

    /**
     * Checks if is un logged table.
     *
     * @return true, if is un logged table
     */
    public boolean isUnLoggedTable() {
        return isUnLoggedTable;
    }

    /**
     * Sets the un logged table.
     *
     * @param isUnLoggedTble the new un logged table
     */
    public void setUnLoggedTable(boolean isUnLoggedTble) {
        this.isUnLoggedTable = isUnLoggedTble;
    }

    /**
     * Sets the description.
     *
     * @param desc the new description
     */
    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the if exists.
     *
     * @param isIfNtExtists the new if exists
     */
    public void setIfExists(boolean isIfNtExtists) {
        this.isIfNotExtists = isIfNtExtists;
    }

    /**
     * Gets the orientation.
     *
     * @return the orientation
     */
    public TableOrientation getOrientation() {
        return this.tabOrientation;
    }

    /**
     * Sets the orientation.
     *
     * @param orientation the new orientation
     */
    public void setOrientation(TableOrientation orientation) {
        this.tabOrientation = orientation;
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
     * Gets the bottombar display name.
     *
     * @return the bottombar display name
     */
    public String getBottombarDisplayName() {
        return getNamespace().getName() + '.' + super.getName();
    }

    @Override
    public String getSearchName() {
        return getName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    /**
     * Form create query.
     *
     * @return the string
     */
    public String formCreateQuery() {
        StringBuffer query = new StringBuffer(512);

        /* CREATE */
        query.append("CREATE ");

        /*
         * [ [ GLOBAL | LOCAL ] { TEMPORARY | TEMP } | UNLOGGED ] TABLE [ IF NOT
         * EXISTS ]
         */

        addUnLoggedTable(query);

        query.append("TABLE ");

        addIfNotExists(query);

        query.append(this.nameSpace.getQualifiedObjectName());

        query.append('.');
        query.append(ServerObject.getQualifiedObjectName(getName()));

        query.append("(").append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");

        appendColumns(query);

        addConstraintsList(query);

        fillWithSection(query);

        /* [ ON COMMIT { PRESERVE ROWS | DELETE ROWS | DROP } ] */

        addTablespace(query);

        addDistributionType(query);

        addNodeOptions(query);

        query.append(';');

        return query.toString();
    }

    /**
     * Adds the node options.
     *
     * @param query the query
     */
    private void addNodeOptions(StringBuffer query) {
        if (null != this.nodeOptions && !this.nodeOptions.isEmpty()) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append(ServerObject.getLiteralName(this.nodeOptions));
        }
    }

    /**
     * Adds the distribution type.
     *
     * @param query the query
     */
    private void addDistributionType(StringBuffer query) {
        if (null != this.distributeOptions && !this.distributeOptions.isEmpty()) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append("DISTRIBUTE BY ");
            query.append(this.distributeOptions);
        }
    }

    /**
     * Adds the tablespace.
     *
     * @param query the query
     */
    private void addTablespace(StringBuffer query) {
        if (null != this.tablespaceName) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append("TABLESPACE ");
            query.append(ServerObject.getQualifiedObjectName(this.tablespaceName));
        }
    }

    /**
     * Adds the constraints list.
     *
     * @param query the query
     */
    private void addConstraintsList(StringBuffer query) {
        int index;
        int size = this.constraintList.getSize();
        for (index = 0; index < size; index++) {
            query.append(",").append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
            query.append(this.constraintList.getItem(index).formConstraintString());
        }

        query.append(")");
    }

    /**
     * Adds the if not exists.
     *
     * @param query the query
     */
    private void addIfNotExists(StringBuffer query) {
        if (this.isIfNotExtists) {
            query.append("IF NOT EXISTS ");
        }
    }

    /**
     * Adds the un logged table.
     *
     * @param query the query
     */
    private void addUnLoggedTable(StringBuffer query) {
        if (this.isUnLoggedTable) {
            query.append("UNLOGGED ");
        }
    }

    /**
     * Append columns.
     *
     * @param query the query
     */
    private void appendColumns(StringBuffer query) {
        int index = 1;
        if (this.columns.getSize() > 0) {
            query.append(this.columns.getItem(0).formColumnString(true));
            int size = this.columns.getSize();

            for (; index < size; index++) {
                query.append(",").append(MPPDBIDEConstants.LINE_SEPARATOR).append("\t");
                query.append(this.columns.getItem(index).formColumnString(true));
            }
        }
    }

    /**
     * Form table comment query.
     *
     * @return the string
     */
    public String formTableCommentQuery() {
        StringBuffer buff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if ((null != this.description) && (this.description.length() > 0)) {
            buff.append(MPPDBIDEConstants.LINE_SEPARATOR).append(this.formSetCommentQuery(true));
        }
        return buff.toString();
    }

    @Override
    public String getDDL(Database db) throws MPPDBIDEException {
        StringBuilder strbldr = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        DBConnection conn = getConnForFetchingDDL(db);
        fetchDDL(db, conn);
        strbldr.append(getSource());
        db.getConnectionManager().releaseConnection(conn);
        return strbldr.toString();

    }

    /**
     * Fetch DDL.
     *
     * @param db the db
     * @param conn TODO
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public void fetchDDL(Database db, DBConnection conn) throws MPPDBIDEException {
        String fetchDDLQueryPrefix = "SELECT * FROM pg_get_tabledef";
        ExecTimer timer = new ExecTimer("fetch DDL from server");

        ResultSet rs = null;
        try {
            timer.start();
            rs = conn.execSelectAndReturnRs(fetchDDLQueryPrefix + "('" + this.getDisplayName() + "')");
            timer.stopAndLogNoException();
            setDDLSourceCode(rs);
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.debug("fetch DDL done");
        }
    }

    private void setDDLSourceCode(ResultSet rs) throws SQLException {
        /*
         * we do not assume how many rows DDL query result will contain. We
         * concatenate 1st column texts for all the result rows and show as the
         * DDL
         */
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        while (rs.next()) {
            sb.append(rs.getString(1));
        }
        this.setSource(sb.toString());
    }

    private DBConnection getConnForFetchingDDL(Database db) throws DatabaseOperationException {
        DBConnection conn = null;
        try {
            conn = db.getConnectionManager().getFreeConnection();
        } catch (MPPDBIDEException exception) {
            MPPDBIDELoggerUtility.error("error getting free connection", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        }
        return conn;
    }

    /**
     * Constructs "WITH" section of create table query.
     *
     * @param query the query
     */
    private void fillWithSection(StringBuffer query) {

        if (validateFillFactor()) {
            return;
        }

        query.append(MPPDBIDEConstants.LINE_SEPARATOR).append("WITH (");
        boolean commaRequired = false;

        commaRequired = addFillFactorIfNotDefaultValue(query);

        commaRequired = addOids(query, commaRequired);

        addForTableOrientation(query, commaRequired);

        query.append(")");
    }

    /**
     * Adds the for table orientation.
     *
     * @param query the query
     * @param commaRequired the comma required
     */
    private void addForTableOrientation(StringBuffer query, boolean commaRequired) {
        if (this.tabOrientation == TableOrientation.COLUMN) {
            addComma(query, commaRequired);

            query.append("orientation = column");
        }
    }

    /**
     * Adds the comma.
     *
     * @param query the query
     * @param commaRequired the comma required
     */
    private void addComma(StringBuffer query, boolean commaRequired) {
        if (commaRequired) {
            query.append(", ");
        }
    }

    /**
     * Adds the oids.
     *
     * @param query the query
     * @param commaRequired the comma required
     * @return true, if successful
     */
    private boolean addOids(StringBuffer query, boolean commaRequiredParam) {
        boolean commaRequired = commaRequiredParam;
        if (this.hasOidCol) {
            addComma(query, commaRequired);

            query.append("OIDS=TRUE");
            commaRequired = true;
        }
        return commaRequired;
    }

    /**
     * Adds the fill factor if not default value.
     *
     * @param query the query
     * @return true, if successful
     */
    private boolean addFillFactorIfNotDefaultValue(StringBuffer query) {
        boolean commaRequired = false;
        if (this.fillfactor != 100) {
            query.append("fillfactor=").append(this.fillfactor);
            commaRequired = true;
        }
        return commaRequired;
    }

    /**
     * Validate fill factor.
     *
     * @return true, if successful
     */
    private boolean validateFillFactor() {
        return this.fillfactor == 100 && !this.hasOidCol && isDefaultOrientation();
    }

    /**
     * Checks if is default orientation.
     *
     * @return true, if is default orientation
     */
    private boolean isDefaultOrientation() {
        return this.tabOrientation == TableOrientation.UNKNOWN || this.tabOrientation == TableOrientation.ROW;
    }

    /**
     * Form index queries.
     *
     * @return the string
     */
    public String formIndexQueries() {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int size = this.indexList.getSize();

        for (int i = 0; i < size; i++) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append(this.indexList.getItem(i).formCreateQuery(true));
        }
        return query.toString();
    }

    /**
     * Convert to table metadata.
     *
     * @param rs the rs
     * @param ns the ns
     * @return the table meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static TableMetaData convertToTableMetadata(ResultSet rs, Namespace ns)
            throws DatabaseOperationException, DatabaseCriticalException {
        TableMetaData table = null;

        table = new TableMetaData(ns);
        table.populateTableMetaData(rs);
        table.setLoaded(false);
        ns.addTableToSearchPool(table);

        return table;
    }

    /**
     * Convert to table metadata on demand.
     *
     * @param rs the rs
     * @param db the db
     * @return the table meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static TableMetaData convertToTableMetadataOnDemand(ResultSet rs, Database db)
            throws DatabaseOperationException, DatabaseCriticalException {
        TableMetaData table = null;
        int namespaceOid = 0;
        Namespace ns = null;

        try {
            namespaceOid = rs.getInt("relnamespace");
            ns = db.getNameSpaceById(namespaceOid);

            table = addTableByType(rs, ns);
            if (null != table) {
                table.setName(rs.getString("relname"));
                table.setOid(rs.getInt("oid"));
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        }
        return table;
    }

    /**
     * Adds the table by type.
     *
     * @param rs the rs
     * @param ns the ns
     * @return the table meta data
     * @throws SQLException the SQL exception
     */
    private static TableMetaData addTableByType(ResultSet rs, Namespace ns) throws SQLException {
        TableMetaData table = null;
        String relkind = null;
        String parttype = null;
        relkind = rs.getString("relkind");
        parttype = rs.getString("parttype");
        if ("r".equals(relkind)) {
            table = validateForTableType(ns, parttype);
        } else if ("f".equals(relkind)) {
            table = addForeignTable(rs, ns, parttype);
        }
        return table;
    }

    /**
     * Adds the foreign table.
     *
     * @param rs the rs
     * @param ns the ns
     * @param parttype the parttype
     * @return the table meta data
     * @throws SQLException the SQL exception
     */
    private static TableMetaData addForeignTable(ResultSet rs, Namespace ns, String parttype) throws SQLException {
        TableMetaData table = null;
        if (!("n".equals(parttype))) {
            table = new ForeignPartitionTable(ns);
        } else {
            table = addForeignTableType(rs, ns);
        }
        return table;
    }

    /**
     * Adds the foreign table type.
     *
     * @param rs the rs
     * @param ns the ns
     * @return the table meta data
     * @throws SQLException the SQL exception
     */
    private static TableMetaData addForeignTableType(ResultSet rs, Namespace ns) throws SQLException {
        TableMetaData table = null;
        String ftOptions = rs.getString("ftoptions");
        if (ftOptions.contains("gsfs")) {
            table = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE_GDS);
        } else if (ftOptions.contains("format=orc")) {
            table = new ForeignTable(ns, OBJECTTYPE.FOREIGN_TABLE_HDFS);
        }
        ForeignTable.getForeignErrorTable(ftOptions, ns);
        return table;
    }

    /**
     * Validate for table type.
     *
     * @param ns the ns
     * @param parttype the parttype
     * @return the table meta data
     */
    private static TableMetaData validateForTableType(Namespace ns, String parttype) {
        TableMetaData table;
        if ("n".equals(parttype)) {
            table = new TableMetaData(ns);
        } else {
            table = new PartitionTable(ns);
        }
        return table;
    }

    /**
     * Populate table meta data.
     *
     * @param rs the rs
     * @return the table meta data
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public TableMetaData populateTableMetaData(ResultSet rs)
            throws DatabaseOperationException, DatabaseCriticalException {
        this.fillTablePropertiesFromRS(rs);
        return this;
    }

    /**
     * FillTablePropertiesFromRS - Reads the basic table details from the result
     * set. this method is forbidden to call getNext(), as it is controlled by
     * the caller
     *
     * @param reloptions the reloptions
     * @return the table orientation
     * @throws SQLException the SQL exception
     */

    private static TableOrientation readOrientation(Array reloptions) throws SQLException {
        if (null == reloptions) {
            return TableOrientation.ROW;
        }

        String[] options = (String[]) reloptions.getArray();
        if (options.length > 0) {
            return addTableOrientation(options[0]);
        }
        return TableOrientation.UNKNOWN;
    }

    /**
     * Adds the table orientation.
     *
     * @param opt the opt
     * @return the table orientation
     */
    private static TableOrientation addTableOrientation(String opt) {
        if (opt.contains("orientation=column")) {
            return TableOrientation.COLUMN;
        } else if (opt.contains("orientation=row")) {
            return TableOrientation.ROW;
        }
        return TableOrientation.UNKNOWN;
    }

    /**
     * FillTablePropertiesFromRS - Reads the basic table details from the result
     * set. this method is forbidden to call getNext(), as it is controlled by
     * the caller
     *
     * @param rs - contains the query result (current row)
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    protected void fillTablePropertiesFromRS(ResultSet rs)
            throws DatabaseOperationException, DatabaseCriticalException {

        try {
            this.setName(rs.getString("relname"));
            this.setOid(rs.getInt("oid"));
            this.setOrientation(readOrientation(rs.getArray("reloptions")));

            setTablePersistence(rs);

        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        }

    }

    private void setTablePersistence(ResultSet rs) throws SQLException {
        String persistence;
        persistence = rs.getString("relpersistence");
        if ("t".equals(persistence)) {
            this.setTempTable(true);
        } else if ("u".equals(persistence)) {
            this.setUnLoggedTable(true);
        }
    }

    /**
     * Adds the constraint.
     *
     * @param cons the cons
     */
    public void addConstraint(ConstraintMetaData cons) {
        constraintList.addItem(cons);
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public Namespace getNamespace() {
        return this.nameSpace;
    }

    /**
     * Gets the tablespace name.
     *
     * @return the tablespace name
     */
    public String getTablespaceName() {
        return this.tablespaceName;
    }

    /**
     * Removes the constraint.
     *
     * @param selected the selected
     */
    public void removeConstraint(int selected) {
        constraintList.removeItemByIdx(selected);
    }

    /**
     * Adds the index.
     *
     * @param index the index
     */
    public void addIndex(IndexMetaData index) {
        indexList.addItem(index);
    }

    /**
     * Removes the index.
     *
     * @param pos the pos
     */
    public void removeIndex(int pos) {
        indexList.removeItemByIdx(pos);
    }

    /**
     * Gets the constraints.
     *
     * @return the constraints
     */
    public OLAPObjectList<ConstraintMetaData> getConstraints() {
        return this.constraintList;
    }

    /**
     * Gets the indexes.
     *
     * @return the indexes
     */
    public IndexList getIndexes() {
        return this.indexList;
    }

    /**
     * Gets the index array list.
     *
     * @return the index array list
     */
    public ArrayList<IndexMetaData> getIndexArrayList() {
        return this.indexList.getList();
    }

    /**
     * Exec rename.
     *
     * @param newName the new name
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execRename(String newName, DBConnection dbConnection)
            throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder sb = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sb.append("ALTER TABLE ");
        sb.append(getDisplayName());
        sb.append(" RENAME TO ");
        sb.append(ServerObject.getQualifiedObjectName(newName));

        dbConnection.execNonSelect(sb.toString());
        this.nameSpace.refreshTable(this, dbConnection, true);
    }

    /**
     * Exec create.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execCreate(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder buff = new StringBuilder(formCreateQuery());

        buff.append(formTableCommentQuery());
        buff.append(formColumnCommentQuery());
        int indexSize = 0;
        conn.execNonSelect(buff.toString());
        try {
            indexSize = this.indexList.getSize();

            for (int i = 0; i < indexSize; i++) {
                conn.execNonSelect(this.indexList.getItem(i).formCreateQuery(true));
            }
        } catch (DatabaseOperationException dbOperationException) {
            rollbackTableCreate(indexSize, conn);
            throw dbOperationException;
        }
        getNamespace().getNewlyCreatedTable(getName());
    }

    /**
     * Rollback table create.
     *
     * @param indexSize the index size
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     */
    public void rollbackTableCreate(int indexSize, DBConnection conn) throws DatabaseCriticalException {
        isRollBack = true;
        try {
            for (int i = 0; i < indexSize; i++) {
                conn.execNonSelect(this.indexList.getItem(i).formDropQuery(true));
            }
            execDrop(conn);
        } catch (DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error(" Nothing to do, skip", exception);
        }
    }

    /**
     * Exec drop.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execDrop(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String dropQry = String.format(Locale.ENGLISH, "DROP TABLE %s;", this.getDisplayName());
        conn.execNonSelect(dropQry);
        this.nameSpace.refreshTableHirarchy(conn);

        if (isRollBack) {
            isRollBack = false;
        }
    }

    /**
     * Exec vacumm.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execVacumm(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "VACUUM %s;", this.getDisplayName());
        dbConnection.execNonSelect(qry);
    }

    /**
     * Exec analyze.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execAnalyze(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "ANALYZE %s;", this.getDisplayName());
        dbConnection.execNonSelect(qry);
    }

    /**
     * Exec reindex.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execReindex(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "REINDEX TABLE  %s;", this.getDisplayName());
        dbConnection.execNonSelectForTimeout(qry);
    }

    /**
     * Exec truncate.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execTruncate(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = null;
        qry = String.format(Locale.ENGLISH, "TRUNCATE TABLE ONLY  %s;", this.getDisplayName());
        dbConnection.execNonSelectForTimeout(qry);
    }

    /**
     * Exec set table space.
     *
     * @param newTableSpace the new table space
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execSetTableSpace(String newTableSpace, DBConnection dbConnection)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "ALTER TABLE ONLY %s SET TABLESPACE %s", this.getDisplayName(),
                getQualifiedObjectName(newTableSpace));
        dbConnection.execNonSelectForTimeout(qry);
    }

    /**
     * Form set comment query.
     *
     * @param isCreateTable the is create table
     * @return the string
     */
    public String formSetCommentQuery(boolean isCreateTable) {

        StringBuilder commentQry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        commentQry.append("COMMENT ON TABLE ");
        commentQry.append(this.getQualifiedName());
        commentQry.append(" IS ");
        commentQry.append(null == this.description ? "NULL" : ServerObject.getLiteralName(this.description));
        commentQry.append(";");
        return commentQry.toString();

    }

    /**
     * Exec set table description.
     *
     * @param newTableDescription the new table description
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execSetTableDescription(String newTableDescription, DBConnection dbConnection)
            throws DatabaseCriticalException, DatabaseOperationException {
        this.description = newTableDescription;
        dbConnection.execNonSelect(this.formSetCommentQuery(false));
    }

    /**
     * Exec set schema.
     *
     * @param newSchemaName the new schema name
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execSetSchema(String newSchemaName, DBConnection dbConnection)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = String.format(Locale.ENGLISH, "ALTER TABLE ONLY %s SET SCHEMA %s", this.getDisplayName(),
                ServerObject.getQualifiedObjectName(newSchemaName));
        dbConnection.execNonSelectForTimeout(qry);
    }

    /**
     * Exec create index.
     *
     * @param index the index
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execCreateIndex(IndexMetaData index, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = index.formCreateQuery(false);
        dbConnection.execNonSelectForTimeout(qry);
    }

    /**
     * Refresh.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refresh(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        this.nameSpace.refreshTable(this, conn, false);
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

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TableMetaData)) {
            return false;
        }

        TableMetaData other = (TableMetaData) obj;
        if (validateTable(other)) {
            return true;
        }
        return false;
    }

    private boolean validateTable(TableMetaData other) {
        return getOid() == other.getOid() && getNamespace().equals(other.getNamespace());
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

    /**
     * Find matching child objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ColumnMetaData> findMatchingChildObjects(String prefix) {
        return columns.findMatching(prefix);
    }

    /**
     * Gets the qualified name.
     *
     * @return the qualified name
     */
    public String getQualifiedName() {
        return this.getNamespace().getQualifiedObjectName() + '.' + this.getQualifiedObjectName();
    }

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName() {
        if (nameSpace == null) {
            return "";
        }
        return nameSpace.getServerName();
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        if (nameSpace == null) {
            return "";
        }
        return nameSpace.getDatabaseName();
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return nameSpace.getDatabase();
    }

    /**
     * Convert to column meta data.
     *
     * @param rs the rs
     * @param type the type
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public void convertToColumnMetaData(ResultSet rs, TypeMetaData type)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        if (isLevel3Loaded()) {
            return;
        }
        ColumnMetaData col = ColumnUtil.convertToColumnMetaData(rs, type, this, getDatabase());
        getColumns().addItem(col);

    }

    /**
     * Removes the column by idx.
     *
     * @param editIndex the edit index
     */
    public void removeColumnByIdx(int editIndex) {
        this.getColumns().removeItemByIdx(editIndex);
    }

    /**
     * Adds the column at index.
     *
     * @param newTempColumn the new temp column
     * @param editIndex the edit index
     */
    public void addColumnAtIndex(ColumnMetaData newTempColumn, int editIndex) {
        this.getColumns().addItemAtIndex(newTempColumn, editIndex);
    }

    /**
     * Adds the column.
     *
     * @param newTempColumn the new temp column
     */
    public void addColumn(ColumnMetaData newTempColumn) {
        this.getColumns().addItem(newTempColumn);

    }

    /**
     * Gets the column meta data list.
     *
     * @return the column meta data list
     */
    public List<ColumnMetaData> getColumnMetaDataList() {
        return this.getColumns().getList();
    }

    /**
     * Gets the constraint meta data list.
     *
     * @return the constraint meta data list
     */
    public List<ConstraintMetaData> getConstraintMetaDataList() {
        return this.getConstraints().getList();
    }

    /**
     * Gets the index meta data list.
     *
     * @return the index meta data list
     */
    public List<IndexMetaData> getIndexMetaDataList() {
        return this.getIndexes().getList();
    }

    /**
     * Adds the constraint.
     *
     * @param rs the rs
     * @param namespaceOfConstraint the namespace of constraint
     * @param fkeyTable the fkey table
     * @throws SQLException the SQL exception
     */
    public void addConstraint(ResultSet rs, Namespace namespaceOfConstraint, TableMetaData fkeyTable)
            throws SQLException {
        ConstraintMetaData constraint = null;

        long indexId = rs.getLong("indexid");

        Iterator<IndexMetaData> indexItr = getIndexArrayList().iterator();
        boolean hasNext = indexItr.hasNext();
        IndexMetaData idx = null;
        IndexMetaData idxUsable = null;
        while (hasNext) {
            idx = indexItr.next();
            if (idx.getOid() == indexId) {
                idxUsable = idx;
                break;
            }

            hasNext = indexItr.hasNext();
        }

        constraint = ConstraintMetaDataUtils.convertToConstraint(rs, namespaceOfConstraint, this, fkeyTable, idxUsable);
        addConstraint(constraint);
    }

    /**
     * Gets the server.
     *
     * @return the server
     */
    public Server getServer() {
        return getDatabase().getServer();
    }

    /**
     * Clear details cached.
     */
    public void clearDetailsCached() {
        this.getColumns().clear();
        this.getConstraints().clear();
        this.getIndexes().clear();
        this.setLevel3Loaded(false);
    }

    /**
     * Adds the index.
     *
     * @param resultSet the result set
     * @throws SQLException the SQL exception
     */
    public void addIndex(ResultSet resultSet) throws SQLException {
        IndexUtil.convertToIndex(resultSet, this, this.nameSpace, columns);
    }

    /**
     * Fetch index for table.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchIndexForTable(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String query = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, "
                + "ci.relnamespace as namespaceid, "
                + "ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, "
                + "i.indisexclusion as isexclusion,"
                + " i.indimmediate as isimmediate, i.indisclustered as isclustered, "
                + "i.indcheckxmin as checkmin, i.indisready as isready, "
                + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, "
                + "def.indexdef , def.tablespace " + "FROM pg_index i"
                + " LEFT JOIN pg_class t on (t.oid = i.indrelid) " + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) "
                + "LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) "
                + "LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname)"
                + " WHERE t.relkind = 'r' and t.oid = %d;";
        query = String.format(Locale.ENGLISH, query, getOid());

        ResultSet rs = conn.execSelectAndReturnRs(query);
        boolean hasNext = false;

        try {
            hasNext = rs.next();
            while (hasNext) {
                this.addIndex(rs);
                hasNext = rs.next();
            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Checks if is temp table.
     *
     * @return true, if is temp table
     */
    public boolean isTempTable() {
        return isTempTable;
    }

    @Override
    public Object[] getChildren() {

        Object[] objs = {this.getColumns(), this.getConstraints(), this.getIndexes()};
        return objs;
    }

    /**
     * Form query for table metadata.
     *
     * @param queryParam the query param
     * @param tableFlag the table flag
     * @param privilegeFlag the privilege flag
     * @return the string
     */
    public static String formQueryForTableMetadata(String queryParam, boolean tableFlag, boolean privilegeFlag) {
        String query = queryParam;
        if (!privilegeFlag) {
            return query + ";";
        }
        // this is false for view metadata
        if (tableFlag) {
            query += " and oid in (" + "select pcrelid from pgxc_class "
                    + "where has_nodegroup_privilege(pgroup, 'USAGE'))";
        }
        // this is for both table and view
        return "with x as (" + query + ") select * from x " + "where has_table_privilege(x.oid,'SELECT');";
    }

    /**
     * Gets the SQL for table meta databy oid.
     *
     * @param oid the oid
     * @param isRenameFlow the is rename flow
     * @return the SQL for table meta databy oid
     */
    public String getSQLForTableMetaDatabyOid(long oid, boolean isRenameFlow) {
        String query = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
                + "ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, "
                + "xctbl.nodeoids as nodes ,tbl.reloptions as reloptions "
                + "from pg_class tbl left join (select d.description, d.objoid from pg_description d "
                + "where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl "
                + "on (tbl.oid = xctbl.pcrelid) " + "left join pg_tablespace ts on ts.oid = tbl.reltablespace "
                + "where tbl.relkind = 'r' and " + "tbl.parttype in ('n','p') and tbl.oid = %d";
        query = String.format(Locale.ENGLISH, query, oid);
        String qry = TableMetaData.formQueryForTableMetadata(query,
                this.getServer().isServerCompatibleToNodeGroupPrivilege(), privilegeFlag && !isRenameFlow);

        return qry;
    }

    /**
     * Fill table meta from RS.
     *
     * @param rs the rs
     * @return the table meta data
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public TableMetaData fillTableMetaFromRS(ResultSet rs)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        return populateTableMetaData(rs);
    }

    /**
     * Refresh table details.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshTableDetails(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        clearDetailsCached();
        this.getNamespace().fetchTableColumnMetaData(this.getOid(), this.getNamespace().getTables(), conn);
        this.getNamespace().fetchConstraintForTable(this, this.getNamespace().getTables(), conn);
        this.fetchIndexForTable(conn);
        this.setLevel3Loaded(true);
        this.setLoaded(true);
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return super.getLoaded();
    }

    /**
     * Sets the name space.
     *
     * @param ns the new name space
     */
    public void setTableNamespace(Namespace ns) {
        this.nameSpace = ns;
        this.privilegeFlag = ns.getPrivilegeFlag();
    }

    @Override
    public Object getParent() {
        return this.nameSpace;
    }

    /**
     * Find all child objects.
     *
     * @return the sorted map
     */
    public SortedMap<String, ColumnMetaData> findAllChildObjects() {
        return findMatchingChildObjects("");
    }

    /**
     * Validate orientation.
     *
     * @param orientation the orientation
     * @return true, if successful
     */
    public boolean validateOrientation(TableOrientation orientation) {
        if (getOrientation() == orientation) {
            return true;
        }
        return false;
    }

    /**
     * isRowTableOrientation
     * 
     * @return boolean value
     */
    public boolean isRowTableOrientation() {
        return validateOrientation(TableOrientation.ROW);
    }

    /**
     * Fetch distribution column list.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchDistributionColumnList(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String getDistributedKey = "select CASE pclocatortype WHEN 'R' THEN 'REPLICATION' WHEN 'H' THEN 'HASH' END as distriute_type, "
                + "getdistributekey(%d) as distributekey"
                + " from pgxc_class, (SELECT count(*) AS dn_cn FROM pg_catalog.pgxc_node WHERE node_type = 'D') "
                + "as nc where pcrelid = %d";
        List<String> distList = new ArrayList<String>();
        String query = String.format(Locale.ENGLISH, getDistributedKey, this.getOid(), this.getOid());
        ResultSet rs = conn.execSelectAndReturnRs(query);
        String distriuteType = null;
        String distributeClms = null;
        try {
            boolean hasnext = rs.next();
            if (hasnext) {
                distriuteType = rs.getString("distriute_type");
                distributeClms = rs.getString("distributekey");
                addDistribution(distList, distriuteType, distributeClms);
                addNewDistribution(distList);
            }
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Adds the new distribution.
     *
     * @param distList the dist list
     */
    private void addNewDistribution(List<String> distList) {
        if (distList.size() > 0) {
            distributionClmList.clear();
            distributionClmList = distList;
        }
    }

    /**
     * Adds the distribution.
     *
     * @param distList the dist list
     * @param distriuteType the distriute type
     * @param distributeClms the distribute clms
     */
    private void addDistribution(List<String> distList, String distriuteType, String distributeClms) {
        StringTokenizer token;
        if (!"REPLICATION".equals(distriuteType)) {
            token = new StringTokenizer(distributeClms, ",");
            while (token.hasMoreElements()) {
                String object = (String) token.nextElement();

                addColumnToDistributionList(distList, object);
            }
        }
    }

    /**
     * Adds the column to distribution list.
     *
     * @param distList the dist list
     * @param object the object
     */
    private void addColumnToDistributionList(List<String> distList, String object) {
        for (ColumnMetaData clm : getColumnMetaDataList()) {
            if (clm.getName().equals(object.trim())) {
                clm.setDistributionColm(true);
                distList.add(object);
                break;
            }
        }
    }

    /**
     * Gets the distribution column list.
     *
     * @return the distribution column list
     */
    public List<String> getDistributionColumnList() {
        return distributionClmList;
    }

    /**
     * Gets the orc version.
     *
     * @return the orc version
     */
    public String getOrcVersion() {
        return orcVersion;
    }

    /**
     * Sets the orc version.
     *
     * @param orcVersion the new orc version
     */
    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    /**
     * Checks if is table dropped.
     *
     * @return true, if is table dropped
     */
    public boolean isTableDropped() {
        Namespace ns = getNamespace();
        if (null == ns) {
            return true;
        }

        TableObjectGroup group = ns.getTables();
        return null == group || null == group.getObjectById(getOid());
    }

    /**
     * Checks if is distribution column.
     *
     * @param columnIndex the column index
     * @return true, if is distribution column
     */
    public boolean isDistributionColumn(int columnIndex) {
        return getColumnMetaDataList().get(columnIndex).isDistributionColm();
    }

    /**
     * Adds the foreign table to group.
     *
     * @param table the table
     */
    public void addForeignTableToGroup(TableMetaData table) {
        nameSpace.addForeignTableToGroup(table);
    }

    /**
     * Adds the table to group.
     *
     * @param table the table
     */
    public void addTableToGroup(TableMetaData table) {
        nameSpace.addTableToGroup(table);
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        String dropQuery = "DROP TABLE IF EXISTS ";
        StringBuilder query = new StringBuilder(dropQuery);
        query.append(this.getDisplayName());

        if (isCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(ServerObject obj) {
        if (obj instanceof ColumnMetaData) {
            removeColumns(obj);
        } else if (obj instanceof ConstraintMetaData) {
            removeConstraints(obj);
        } else if (obj instanceof IndexMetaData) {
            removeTableIndex(obj);
        }
    }

    /**
     * Removes the table index.
     *
     * @param obj the obj
     */
    private void removeTableIndex(ServerObject obj) {
        if (indexList != null) {
            indexList.remove((IndexMetaData) obj);
        }
    }

    /**
     * Removes the constraints.
     *
     * @param obj the obj
     */
    private void removeConstraints(ServerObject obj) {
        if (constraintList != null) {
            constraintList.remove((ConstraintMetaData) obj);
        }
    }

    /**
     * Removes the columns.
     *
     * @param obj the obj
     */
    private void removeColumns(ServerObject obj) {
        if (columns != null) {
            columns.remove((ColumnMetaData) obj);
        }
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        return true;
    }

    @Override
    public String getNameSpaceName() {
        return getNamespace().getName();
    }

    @Override
    public boolean isDropped() {
        return isTableDropped();
    }

    /**
     * Form column comment query.
     *
     * @return the string
     * @Author: lijialiang(l00448174)
     * @Date: Aug 16, 2019
     * @Title: formColumnCommentQuery
     * @Description: generate comment statement for column
     */
    public String formColumnCommentQuery() {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        int size = this.columns.getSize();
        for (int i = 0; i < size; i++) {
            String colDescription = this.columns.getItem(i).getColDescription();
            appendCommentQuery(query, i, colDescription);
        }
        return query.toString();
    }

    private void appendCommentQuery(StringBuilder query, int i, String colDescription) {
        if (validateClmDescription(colDescription)) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append(this.columns.getItem(i).formSetCommentQuery());
        }
    }

    private boolean validateClmDescription(String colDescription) {
        return null != colDescription && !"".equals(colDescription);
    }
    
    /**
         * getTablespaceForTable method
         *    
         * @param conn connection
         * @return string string
         * @throws DatabaseCriticalException exception
         * @throws DatabaseOperationException exception
         */
    public String getTablespaceForTable(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String query = String.format(Locale.ENGLISH,
                "select spcname from pg_class as class left join pg_tablespace as tablespace"
                        + " on class.reltablespace=tablespace.oid where class.oid=%d",
                this.getOid());
        ResultSet rs = null;
        String tblspaceName = "";
        try {
            rs = conn.execSelectAndReturnRs(query);
            while (rs.next()) {
                tblspaceName = rs.getString("spcname");
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.debug("fetch DDL done");
        }
        return tblspaceName;
    }
}
