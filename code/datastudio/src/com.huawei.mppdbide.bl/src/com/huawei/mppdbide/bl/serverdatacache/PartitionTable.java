/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.PartitionList;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
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
 * Description: The Class PartitionTable.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PartitionTable extends TableMetaData {

    private PartitionList partitions;

    private List<PartitionColumnExpr> selCols;

    private String partKey;

    /**
     * The Constant PARTITION_INDEX_QUERY.
     */
    public static final String PARTITION_INDEX_QUERY = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, "
            + "ci.relnamespace as namespaceid, "
            + "ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, "
            + "i.indisexclusion as isexclusion,"
            + " i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, "
            + "i.indisready as isready, "
            + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef, def.tablespace "
            + "FROM pg_index i" + " LEFT JOIN pg_class t on (t.oid = i.indrelid) "
            + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) "
            + "LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) "
            + "LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname) "
            + "WHERE t.relkind in ('r', 'f') " + "and ci.parttype in ('p','v') ";

    /**
     * Instantiates a new partition table.
     *
     * @param ns the ns
     */
    public PartitionTable(Namespace ns) {
        super(0, "notablename", ns, null, OBJECTTYPE.PARTITION_TABLE);
        this.partitions = new PartitionList(OBJECTTYPE.PARTITION_GROUP, this);
    }

    /**
     * Instantiates a new partition table.
     *
     * @param ns the ns
     * @param objType the obj type
     */
    public PartitionTable(Namespace ns, OBJECTTYPE objType) {
        super(0, "notablename", ns, null, objType);
        this.partitions = new PartitionList(OBJECTTYPE.PARTITION_GROUP, this);
    }

    /**
     * Form partition queries.
     *
     * @return the string
     */
    public String formPartitionQueries() {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        List<PartitionMetaData> partList = this.partitions.getList();
        int size = partList.size();

        appendPartitionColumns(query, size);

        // Adding partitions
        for (int index = 0; index < size; index++) {
            appendPartitionQuery(query, partList, size, index);
        }

        if (size > 0) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR).append(");");
        }
        return query.toString();
    }

    private void appendPartitionQuery(StringBuilder query, List<PartitionMetaData> partList, int size, int index) {
        if (index == size - 1) {
            query.append(partList.get(index).formCreatePartitionsQry(getSelColumns()));
        } else {
            query.append(partList.get(index).formCreatePartitionsQry(getSelColumns())).append(",")
                    .append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
    }

    private void appendPartitionColumns(StringBuilder query, int size) {
        if (size > 0) {
            query.append(MPPDBIDEConstants.LINE_SEPARATOR);
            query.append(formCreatePartitionColumnsQry());
            query.append("( ").append(MPPDBIDEConstants.LINE_SEPARATOR);
        }
    }

    /**
     * Form create partition columns qry.
     *
     * @return the string
     */
    private String formCreatePartitionColumnsQry() {
        String selCol = null;
        List<PartitionColumnExpr> selectedColumns = null;

        StringBuilder sbPartitionCol = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        selectedColumns = getSelColumns();
        sbPartitionCol.append("PARTITION BY RANGE ");

        sbPartitionCol.append("(");

        int size = selectedColumns.size();

        for (int index = 0; index < size; index++) {
            selCol = ServerObject.getQualifiedObjectName(selectedColumns.get(index).toString());
            if (index == size - 1) {
                sbPartitionCol.append(selCol);
            } else {
                sbPartitionCol.append(selCol).append(", ");
            }
        }

        sbPartitionCol.append(")");

        return sbPartitionCol.toString();
    }

    /**
     * Gets the oid.
     *
     * @return the oid
     */
    public long getOid() {
        return super.getOid();
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName() {
        return super.getName();
    }

    /**
     * Gets the column info.
     *
     * @param ptabGroup the ptab group
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the column info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void getColumnInfo(TableObjectGroup ptabGroup, DBConnection conn, Namespace ns, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = "
                + " %dand relkind <> 'i' and parttype in ('p', 'v')), "
                + "attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid "
                + ",c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid "
                + "from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ "
                + "where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d "
                + "where d.adrelid in ( select tableid from tbl) and d.adnum in( select columnidx from attr))"
                + "select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,"
                + "default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";
        qry = String.format(Locale.ENGLISH, qry, oid);
        getColumnIndoByExecuteQuery(ptabGroup, conn, ns, qry);
    }

    /**
     * Gets the column indo by execute query.
     *
     * @param ptabGroup the ptab group
     * @param conn the conn
     * @param ns the ns
     * @param qry the qry
     * @return the column indo by execute query
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void getColumnIndoByExecuteQuery(TableObjectGroup ptabGroup, DBConnection conn, Namespace ns,
            String qry) throws DatabaseCriticalException, DatabaseOperationException {
        ResultSet rs = conn.execSelectAndReturnRs(qry);
        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                hasNext = getColumnDataType(ptabGroup, ns, rs);
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    private static boolean getColumnDataType(TableObjectGroup ptabGroup, Namespace ns, ResultSet rs)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        boolean hasNext;
        long tableId = rs.getLong("tableid");
        TableMetaData tbl = ptabGroup.getObjectById(tableId);
        long datatypeid = rs.getLong("datatypeoid");

        Namespace dtNamspace = ns.getDatabase().getNameSpaceById(rs.getLong("dtns"));

        TypeMetaData type = TypeMetaData.getTypeById(dtNamspace, datatypeid);

        if (tbl == null) {
            validateNamespacePrivilege(ns);
        } else {
            tbl.convertToColumnMetaData(rs, type);
        }
        hasNext = rs.next();
        return hasNext;
    }

    private static void validateNamespacePrivilege(Namespace ns) throws DatabaseOperationException {
        if (!ns.getPrivilegeFlag()) {
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
        }
    }

    /**
     * Gets the column info.
     *
     * @param conn the conn
     * @return the column info
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void getColumnInfo(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, c.attname as name, "
                + " pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                + " c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod "
                + " as precision, c.attndims as dimentions, "
                + " c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as "
                + " default_value, d.adbin as attDefStr " + " from pg_class t "
                + " left join pg_attribute c on (t.oid = c.attrelid and t.parttype in ('p', 'v')) "
                + " left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) "
                + " left join pg_type typ on (c.atttypid = typ.oid) "
                + " where c.attisdropped = 'f' and c.attnum > 0 and t.oid = %d and t.relkind <> 'i' "
                + " order by c.attnum;";
        qry = String.format(Locale.ENGLISH, qry, this.getOid());
        ResultSet rs = conn.execSelectAndReturnRs(qry);

        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                long datatypeid = rs.getLong("datatypeoid");

                Namespace dtNamspace = this.getDatabase().getNameSpaceById(rs.getLong("dtns"));

                TypeMetaData type = TypeMetaData.getTypeById(dtNamspace, datatypeid);
                this.convertToColumnMetaData(rs, type);

                hasNext = rs.next();

            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }

    }

    /**
     * Gets the partition constraints.
     *
     * @param ptabgroup the ptabgroup
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the partition constraints
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void getPartitionConstraints(TableObjectGroup ptabgroup, DBConnection conn, Namespace ns, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
                + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
                + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
                + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
                + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, "
                + "c.confkey as fkeycolumnlist, " + "pg_get_constraintdef(c.oid) as const_def "
                + "FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=%d"
                + " and cl.parttype in ('p','v')" + " and c.conrelid <> 0" + ';';
        qry = String.format(Locale.ENGLISH, qry, oid);
        ResultSet rs = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;

        try {
            hasNext = rs.next();
            while (hasNext) {
                hasNext = addPartConstraints(ptabgroup, ns, rs);
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }

    }

    private static boolean addPartConstraints(TableObjectGroup ptabgroup, Namespace ns, ResultSet rs)
            throws SQLException, DatabaseOperationException {
        boolean hasNext;
        long namespaceid = rs.getLong("namespaceid");
        Namespace nsp = ns.getDatabase().getNameSpaceById(namespaceid);

        long ptableId = rs.getLong("tableid");

        TableMetaData ptbl = (TableMetaData) ptabgroup.getObjectById(ptableId);
        /*
         * ptbl can be null in case of the user doesnt have access privilege to
         * ptbl. Since table is the lowest level of object that get filtered by
         * privilege, all constraints are fetched
         */
        TableMetaData fkeyTable = (TableMetaData) ptabgroup.getObjectById(rs.getLong("fkeytableId"));
        if (ptbl == null) {
            validateNamespacePrivilege(ns);
        } else {
            ptbl.addConstraint(rs, nsp, fkeyTable);
        }

        hasNext = rs.next();
        return hasNext;
    }

    /**
     * Gets the partition constraints.
     *
     * @param conn the conn
     * @return the partition constraints
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void getPartitionConstraints(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
                + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
                + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
                + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
                + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, "
                + "c.confkey as fkeycolumnlist, " + "pg_get_constraintdef(c.oid) as const_def "
                + "FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.conrelid=%d"
                + " and cl.parttype in ('p','v')" + " and c.conrelid <> 0" + ';';
        qry = String.format(Locale.ENGLISH, qry, this.getOid());
        ResultSet rs = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;

        try {
            hasNext = rs.next();
            while (hasNext) {
                hasNext = addPartitionConstraints(rs);
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }

    }

    private boolean addPartitionConstraints(ResultSet rs) throws SQLException, DatabaseOperationException {
        boolean hasNext;
        long namespaceid = rs.getLong("namespaceid");
        Namespace nsp = this.getDatabase().getNameSpaceById(namespaceid);

        PartitionTable fkeyTable = nsp.getTableObjectById(rs.getLong("fkeytableId"));
        fkeyTable = getForeignTableKey(rs, nsp, fkeyTable);

        this.addConstraint(rs, nsp, fkeyTable);

        hasNext = rs.next();
        return hasNext;
    }

    private PartitionTable getForeignTableKey(ResultSet rs, Namespace nsp, PartitionTable fkeyTableParam)
            throws SQLException {
        PartitionTable fkeyTable = fkeyTableParam;
        if (null == fkeyTable) {
            fkeyTable = nsp.getForeignTableById(rs.getLong("fkeytableId"));
        }
        return fkeyTable;
    }

    /**
     * Gets the partition indexes.
     *
     * @param ptabGroup the ptab group
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the partition indexes
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void getPartitionIndexes(TableObjectGroup ptabGroup, DBConnection conn, Namespace ns, long oid)
            throws DatabaseOperationException, DatabaseCriticalException {
        String query = String.format(Locale.ENGLISH,
                PartitionTable.PARTITION_INDEX_QUERY + " and ci.relnamespace = %d;", oid);

        ResultSet resultSet = conn.execSelectAndReturnRs(query);
        boolean hasNext = false;

        try {
            hasNext = resultSet.next();
            while (hasNext) {
                hasNext = addPArtitionTableIndex(ptabGroup, ns, resultSet);
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(resultSet);
        }

    }

    private static boolean addPArtitionTableIndex(TableObjectGroup ptabGroup, Namespace ns, ResultSet resultSet)
            throws DatabaseOperationException, SQLException {
        boolean hasNext;
        ns.getDatabase().getNameSpaceById(resultSet.getLong("namespaceid"));
        PartitionTable table = (PartitionTable) ptabGroup.getObjectById(resultSet.getLong("tableId"));
        if (table == null) {
            validateNamespacePrivilege(ns);
        } else {
            table.addIndex(resultSet);
        }
        hasNext = resultSet.next();
        return hasNext;
    }

    /**
     * Gets the partition indexes.
     *
     * @param conn the conn
     * @return the partition indexes
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void getPartitionIndexes(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        String qry = String.format(Locale.ENGLISH, PartitionTable.PARTITION_INDEX_QUERY + "and ci.oid = %d;",
                this.getOid());
        ResultSet resultSet = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;

        try {
            hasNext = resultSet.next();
            while (hasNext) {
                this.addIndex(resultSet);
                hasNext = resultSet.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(resultSet);
        }
    }

    /**
     * Sets the part key.
     *
     * @param partKey the new part key
     */
    protected void setPartKey(String partKey) {
        this.partKey = partKey;
    }

    /**
     * Gets the part key.
     *
     * @return the part key
     */
    public String getPartKey() {
        if (null != this.partKey && !this.getColumnMetaDataList().isEmpty()) {
            List<String> splittedColNames = Arrays.asList(this.partKey.split(","));
            StringBuilder str = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            appendColumnName(splittedColNames, str);
            str.deleteCharAt(str.length() - 1);

            return str.toString();
        }
        return this.partKey;
    }

    private void appendColumnName(List<String> splittedColNames, StringBuilder str) {
        int index = 0;
        for (ColumnMetaData colMetadata : this.getColumnMetaDataList()) {
            if (index == splittedColNames.size()) {
                break;
            }
            index = appendColumnName(splittedColNames, str, index, colMetadata);
        }
    }

    private int appendColumnName(List<String> splittedColNames, StringBuilder str, int indexParam,
            ColumnMetaData colMetadata) {
        int index = indexParam;
        if (colMetadata.getOid() == Integer.parseInt(splittedColNames.get(index))) {
            str.append(colMetadata.getName());
            str.append(",");
            index++;
        }
        return index;
    }

    /**
     * Gets the partitions.
     *
     * @param ptabGroup the ptab group
     * @param conn the conn
     * @param ns the ns
     * @param oid the oid
     * @return the partitions
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static void getPartitions(TableObjectGroup ptabGroup, DBConnection conn, Namespace ns, long oid)
            throws DatabaseCriticalException, DatabaseOperationException {
        /*
         * We get the partition details only for the non HDFS partition tables.
         * SO the partition type is restricted to 'p'
         */
        String qry = "select p.oid AS partition_id " + ", p.relname AS partition_name " + ", p.parentid AS table_id "
                + " from pg_class c, pg_partition p " + " where c.relnamespace =  %d and c.parttype = 'p' "
                + " and p.parentid = c.oid " + " and p.parttype = 'p' " + " order by p.boundaries;";
        qry = String.format(Locale.ENGLISH, qry, oid);
        ResultSet rs = conn.execSelectAndReturnRs(qry);

        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                hasNext = addPartitionTable(ptabGroup, ns, rs);
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }

    }

    private static boolean addPartitionTable(TableObjectGroup ptabGroup, Namespace ns, ResultSet rs)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        boolean hasNext;
        long tableId = rs.getLong("table_id");
        PartitionTable tbl = (PartitionTable) ptabGroup.getObjectById(tableId);
        if (tbl == null) {
            validateNamespacePrivilege(ns);
        } else {
            tbl.convertToPartitionMetaData(rs);
        }

        hasNext = rs.next();
        return hasNext;
    }

    /**
     * Gets the partitions.
     *
     * @param conn the conn
     * @return the partitions
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void getPartitions(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        /*
         * We get the partition details only for the non HDFS partition tables.
         * SO the partition type is restricted to 'p'
         */
        String qry = "select p.oid AS partition_id " + ", p.relname AS partition_name " + ", p.parentid AS table_id "
                + " from pg_class c, pg_partition p " + " where c.oid =  %d and c.parttype = 'p' "
                + " and p.parentid = c.oid " + " and p.parttype = 'p' " + " order by p.boundaries;";
        qry = String.format(Locale.ENGLISH, qry, this.getOid());
        ResultSet rs = conn.execSelectAndReturnRs(qry);

        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                this.convertToPartitionMetaData(rs);
                hasNext = rs.next();
            }
        } catch (SQLException sqlException) {
            GaussUtils.handleCriticalException(sqlException);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, sqlException);
        } finally {
            conn.closeResultSet(rs);
        }

    }

    /**
     * Convert to partition meta data.
     *
     * @param rs the rs
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void convertToPartitionMetaData(ResultSet rs) throws DatabaseCriticalException, DatabaseOperationException {
        if (isLevel3Loaded()) {
            return;
        }

        PartitionMetaData part = PartitionMetaData.convertToPartitionMetaData(rs, this, getDatabase());

        getPartitions().addItem(part);

    }

    /**
     * Gets the partitions.
     *
     * @return the partitions
     */
    public OLAPObjectList<PartitionMetaData> getPartitions() {
        return this.partitions;
    }

    /**
     * Gets the children.
     *
     * @return children
     */
    @Override
    public Object[] getChildren() {
        Object[] parntObjs = super.getChildren();
        ArrayList<Object> abc = new ArrayList<Object>(parntObjs.length + 1);
        int index = 0;

        index = addObject(parntObjs, abc);

        abc.add(index, this.partitions);
        return abc.toArray();
    }

    private int addObject(Object[] parntObjs, ArrayList<Object> abc) {
        int index;
        Object obj;
        for (index = 0; index < parntObjs.length; index++) {
            obj = parntObjs[index];
            abc.add(index, obj);
        }
        return index;
    }

    @Override
    public void execCreate(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder qry = new StringBuilder(formCreateQuery());
        int partitionSize = this.partitions.getSize();
        if (partitionSize > 0) {
            qry.deleteCharAt(qry.length() - 1);
            qry.append(this.formPartitionQueries());
        }
        qry.append(formTableCommentQuery());
        qry.append(formColumnCommentQuery());
        conn.execNonSelect(qry.toString());
        execCreateTAbleQuery(conn);
        getNamespace().getNewlyCreatedTable(getName());

    }

    private void execCreateTAbleQuery(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        int indexSize = 0;
        try {
            List<IndexMetaData> indexList = this.getIndexes().getList();
            indexSize = indexList.size();

            for (int i = 0; i < indexSize; i++) {
                conn.execNonSelect(indexList.get(i).formCreateQuery(true));
            }
        } catch (DatabaseOperationException dbOperationException) {
            rollbackTableCreate(indexSize, conn);
            throw dbOperationException;
        }
    }

    @Override
    public void execDrop(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String dropQry = String.format(Locale.ENGLISH, "DROP TABLE %s;", this.getDisplayName());
        conn.execNonSelect(dropQry);
        this.getNamespace().removeTableFRomSearchPool(getSearchName());
        this.getNamespace().refreshAllTableMetadataInNamespace(conn);

        if (isRollBack) {
            isRollBack = false;
        }
    }

    /**
     * Removes the partition.
     *
     * @param pos the pos
     */
    public void removePartition(int pos) {

        partitions.removeItemByIdx(pos);
    }

    /**
     * Removes the all partition.
     */
    public void removeAllPartition() {
        int size = partitions.getSize();
        if (size > 0) {
            partitions.clear();
        }
    }

    /**
     * Gets the namespace.
     *
     * @return namespace
     */
    public Namespace getNamespace() {
        return super.getNamespace();
    }

    /**
     * Adds the partition.
     *
     * @param partition the partition
     */
    public void addPartition(PartitionMetaData partition) {
        partitions.addItem(partition);
    }

    /**
     * Move partition.
     *
     * @param index the index
     * @param up the up
     */
    public void movePartition(int index, boolean up) {
        this.partitions.moveItem(index, up);
    }

    // CHECKSTYLE:OFF:
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * Sets the sel columns.
     *
     * @param selColumns the new sel columns
     */
    public void setSelColumns(List<PartitionColumnExpr> selColumns) {
        selCols = selColumns;
    }

    /**
     * Gets the sel columns.
     *
     * @return the sel columns
     */
    public List<PartitionColumnExpr> getSelColumns() {
        return selCols;
    }

    /**
     * Gets the search name.
     *
     * @return name
     */
    @Override
    public String getSearchName() {

        return getName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    /**
     * clearDetailsCached
     */
    public void clearDetailsCached() {
        super.clearDetailsCached();
        this.getPartitions().clear();
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
        this.getColumnInfo(conn);
        this.getPartitionConstraints(conn);
        this.getPartitionIndexes(conn);
        this.getPartitions(conn);
        this.setLevel3Loaded(true);
        this.setLoaded(true);
    }

    /**
     * Refresh partition.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshPartition(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        this.getPartitions().clear();
        setLevel3Loaded(false);
        this.getPartitions(conn);
    }

    /**
     * Checks if is table dropped.
     *
     * @return isTabelDropped
     */
    @Override
    public boolean isTableDropped() {
        Namespace ns = getNamespace();
        if (null == ns) {
            return true;
        }
        return ns.validateAndGetTableObjGrp(getOid());
    }

    /**
     * Removes the.
     *
     * @param obj the obj
     */
    public void remove(ServerObject obj) {
        if (obj instanceof PartitionMetaData) {
            if (partitions != null) {
                partitions.remove((PartitionMetaData) obj);
            }

            return;
        }

        super.remove(obj);
    }

    /**
     * Convert to partition table.
     *
     * @param rs the rs
     * @param ns the ns
     * @return the partition table
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     * @throws SQLException the SQL exception
     */
    public static PartitionTable convertToPartitionTable(ResultSet rs, Namespace ns)
            throws DatabaseOperationException, DatabaseCriticalException, SQLException {
        PartitionTable ptab = new PartitionTable(ns);
        ptab.fillTablePropertiesFromRS(rs);
        ptab.setPartKey(rs.getString("partkey"));
        ptab.setLoaded(false);
        ns.addTableToGivenSearchPool(ptab);

        return ptab;
    }

    /**
     * Checks if is partitions available.
     *
     * @return true, if is partitions available
     */
    public boolean isPartitionsAvailable() {
        return getPartitions().getSize() > 0;
    }

    /**
     * Gets the columns list.
     *
     * @return the columns list
     */
    public ArrayList<ColumnMetaData> getColumnsList() {
        return this.getColumns().getList();
    }

    /**
     * Validate for duplicate name.
     *
     * @param partName the part name
     * @throws DatabaseOperationException the database operation exception
     */
    public void validateForDuplicateName(String partName) throws DatabaseOperationException {
        OLAPObjectList<PartitionMetaData> metaDatas = getPartitions();

        int size = metaDatas.getSize();
        PartitionMetaData data = null;
        for (int index = 0; index < size; index++) {
            data = metaDatas.getItem(index);
            if (data.getName().trim().equalsIgnoreCase(partName)) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DUPLICATE_PARTITION_NAME));
                throw new DatabaseOperationException(IMessagesConstants.ERR_DUPLICATE_PARTITION_NAME);
            }

        }

    }
}
