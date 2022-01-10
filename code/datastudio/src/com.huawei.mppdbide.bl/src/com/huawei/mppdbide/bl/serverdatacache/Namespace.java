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

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.bl.export.EXPORTTYPE;
import com.huawei.mppdbide.bl.serverdatacache.groups.DebugObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ForeignTableGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ObjectList;
import com.huawei.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.SynonymObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TableObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.TriggerObjectGroup;
import com.huawei.mppdbide.bl.serverdatacache.groups.ViewObjectGroup;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.exceptions.MPPDBIDEException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * Title: class Description: The Class Namespace.
 */

public class Namespace extends BatchDropServerObject implements GaussOLAPDBMSObject, ILazyLoadObject, INamespace {

    private DebugObjectsManager dbgObjManager = null;

    private TableViewManager tblManager = null;

    private OLAPObjectGroup<DebugObjects> functions;

    /**
     * The db.
     */
    protected Database db;

    private TableObjectGroup tables;

    private ViewObjectGroup views;

    private ForeignTableGroup foreigntables;

    private SequenceObjectGroup sequence;

    private SynonymObjectGroup synonyms;

    private TriggerObjectGroup triggerGroups;

    private boolean isSynoymSupported = true;

    private ObjectList<TypeMetaData> types;

    private LOADSTATUS loadStatus;

    private int defaultObjectLoadthreshold = 30000;

    private static final String LOAD_INDEXES = "SELECT i.indexrelid as oid, i.indrelid as tableId, ci.relname as indexname, ci.relnamespace as "
            + "namespaceid, " + "ci.relam as accessmethodid, i.indisunique as isunique, i.indisprimary as isprimary, "
            + "i.indisexclusion as isexclusion,"
            + " i.indimmediate as isimmediate, i.indisclustered as isclustered, i.indcheckxmin as checkmin, "
            + "i.indisready as isready, "
            + "i.indkey as cols, array_to_string(ci.reloptions, ',') as reloptions, def.indexdef , "
            + "def.tablespace  " + "FROM pg_index i" + " LEFT JOIN pg_class t on (t.oid = i.indrelid) "
            + "LEFT JOIN pg_class ci on (i.indexrelid = ci.oid) "
            + "LEFT JOIN pg_namespace ns on (ci.relnamespace = ns.oid) "
            + "LEFT JOIN pg_indexes def on (ci.relname = def.indexname and ns.nspname = def.schemaname)"
            + " WHERE t.relkind = 'r' and ci.relnamespace = %d";

    private static final String QUERY_FOR_ALL_NORMAL_TABLES_BY_NAMESPACE_ID = "select tbl.oid as oid, tbl.relname relname, tbl.relpersistence relpersistence,"
            + "tbl.reloptions as reloptions, tbl.parttype as parttype, "
            + "array_to_string(part.partkey,',') as partkey "
            + "from pg_class tbl left join pg_partition part on(tbl.oid=part.parentid and part.parttype='r') "
            + "where tbl.relkind = 'r' " + "and tbl.relnamespace = %d";

    private static final String DROP_QUERY = "DROP SCHEMA IF EXISTS ";

    /**
     * The Constant REFRESH_DBG_OBJ_QRY.
     */
    public static final String REFRESH_DBG_OBJ_QRY = "SELECT pr.oid oid, pr.proname objname, "
            + "pr.pronamespace namespace, "
            + "pr.prorettype ret, pr.proallargtypes alltype,pr.pronargs nargs,pr.proargtypes argtype, "
            + "pr.proargnames argname,pr.proargmodes argmod,pr.prosecdef secdef, pr.provolatile vola, "
            + "pr.proisstrict isstrict, pr.proretset retset, pr.procost procost, pr.prorows setrows, "
            + "lng.lanname lang " + "FROM pg_proc pr " + "JOIN pg_language lng ON lng.oid=pr.prolang "
            + "WHERE lng.lanname in ('plpgsql','sql','c') " + " and oid = %d";

    /**
     * The Constant QUERY_DBG_OBJ_PRIVILEGE.
     */
    public static final String QUERY_DBG_OBJ_PRIVILEGE = " and has_function_privilege(pr.oid, 'EXECUTE')";   
    private Set<String> errorTableList;

    /**
     * Gets the debug object group by type.
     *
     * @param type the type
     * @return the debug object group by type
     * @throws DatabaseOperationException the database operation exception
     */
    public DebugObjectGroup getDebugObjectGroupByType(OBJECTTYPE type) throws DatabaseOperationException {
        MPPDBIDELoggerUtility.debug("ConnectionProfile: get debug group by type.");
        switch (type) {
            case PLSQLFUNCTION:
            case SQLFUNCTION:
            case CFUNCTION:
            case FUNCTION_GROUP: {
                return (DebugObjectGroup) getFunctions();
            }
            default: {
                return null;
            }
        }
    }

    /**
     * Instantiates a new namespace.
     *
     * @param oid the oid
     * @param name the name
     * @param parentDb the parent db
     */
    public Namespace(long oid, String name, Database parentDb) {
        super(oid, name, OBJECTTYPE.NAMESPACE, parentDb != null ? parentDb.getPrivilegeFlag() : true);
        this.db = parentDb;
        this.dbgObjManager = new DebugObjectsManager();
        this.tblManager = new TableViewManager();

        functions = new DebugObjectGroup(OBJECTTYPE.FUNCTION_GROUP, this);

        tables = new TableObjectGroup(OBJECTTYPE.TABLE_GROUP, this);
        views = new ViewObjectGroup(this);
        foreigntables = new ForeignTableGroup(OBJECTTYPE.FOREIGN_TABLE_GROUP, this);
        this.types = new ObjectList<TypeMetaData>(OBJECTTYPE.DATATYPE_GROUP, this);
        sequence = new SequenceObjectGroup(OBJECTTYPE.SEQUENCE_GROUP, this);
        synonyms = new SynonymObjectGroup(OBJECTTYPE.SYNONYM_GROUP, this);
        triggerGroups = new TriggerObjectGroup(this);
        errorTableList = new HashSet<String>();
        setNotLoaded();
        updateObjectLoadLimit();
    }

    /**
     * Update object load limit.
     */
    private void updateObjectLoadLimit() {
        if (db != null) {
            int loadLimit = db.getLoadLimit();
            if (loadLimit >= 0) {
                defaultObjectLoadthreshold = loadLimit;
            }
        }
    }

    /**
     * Gets the tables.
     *
     * @return the tables
     */
    public TableObjectGroup getTables() {
        return this.tables;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return this.db;
    }

    /**
     * Gets the functions.
     *
     * @return the functions
     */
    public OLAPObjectGroup<DebugObjects> getFunctions() {
        return this.functions;
    }

    /**
     * Gets the types.
     *
     * @return the types
     */
    public ObjectList<TypeMetaData> getTypes() {
        return this.types;
    }

    /**
     * Clear collection.
     *
     * @param group the group
     */
    private void clearCollection(OLAPObjectGroup<? extends ServerObject> group) {
        if (group != null) {
            getDatabase().getSearchPoolManager().clearObject(group);
            group.clear();

        }
    }

    /**
     * Clear all objects.
     */
    public void clearAllObjects() {
        clearCollection(functions);
        clearCollection(tables);
        clearCollection(views);
        clearCollection(foreigntables);
        clearCollection(sequence);
        clearCollection(synonyms);
    }

    /**
     * Clear.
     */
    public void clear() {
        functions = null;
        tables = null;
        views = null;
        foreigntables = null;
        sequence = null;
        synonyms = null;
    }

    /**
     * get debugobject by id.
     *
     * @param debugObjectId the debug object id
     * @return the debug object by id
     */
    public IDebugObject getDebugObjectById(long debugObjectId) {
        MPPDBIDELoggerUtility.debug("ConnectionProfile: get debug object by id.");
        DebugObjects object = functions.getObjectById(debugObjectId);

        return object;
    }

    /**
     * Check cancel status and abort.
     *
     * @param conn the conn
     * @param status the status
     * @throws DatabaseOperationException the database operation exception
     */
    private void checkCancelStatusAndAbort(DBConnection conn, IJobCancelStatus status)
            throws DatabaseOperationException {
        if (status.getCancel() && null != db) {
            try {
                conn.cancelQuery();
            } catch (DatabaseCriticalException exception) {
                MPPDBIDELoggerUtility.error("Namespace: check cancel status failed", exception);
            } catch (DatabaseOperationException exception) {
                MPPDBIDELoggerUtility.error("Namespace: check cancel status failed", exception);
            }
            MPPDBIDELoggerUtility.error(MessageConfigLoader.getProperty(IMessagesConstants.USER_CANCEL_MSG));
            throw new DatabaseOperationException(IMessagesConstants.USER_CANCEL_MSG);
        }
    }

    /**
     * Gets the all objects on demand.
     *
     * @param conn the conn
     * @return the all objects on demand
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void getAllObjectsOnDemand(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        if (getConnectionManager() != null) {
            this.shallowLoadDebugableObjects(OBJECTTYPE.OBJECTTYPE_BUTT, getConnectionManager().getObjBrowserConn(),
                    true);
            refreshAllMetadataInNamespaceOnDemand(conn);
            refreshSequences(conn);
            loadSynonyms(conn);
            MPPDBIDELoggerUtility.perf("Namepsace Load done!!");
            setLoaded();
        }
    }

    /**
     * Refresh sequences.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshSequences(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        MPPDBIDELoggerUtility.perf("Sequence: Stage : 1");
        clearCollection(this.sequence);
        if (!privilegeFlag) {
            SequenceMetadataUtil.fetchsequence(SequenceMetadataUtil.getAllSequencesByNamespaceIDQuery(this.getOid()),
                    conn, this.db);
        } else {
            SequenceMetadataUtil.fetchsequence(
                    SequenceMetadataUtil.getAllSequencesByNamespace(this.getName(), this.getOid()), conn, this.db);
        }
        MPPDBIDELoggerUtility.perf("Sequence: Stage : 2/2");
        MPPDBIDELoggerUtility.perf("Total sequences loaded :" + this.sequence.getSize());
    }

    /**
     * Can load next level.
     *
     * @param grp the grp
     * @return true, if successful
     */
    @SuppressWarnings("rawtypes")
    private boolean canLoadNextLevel(OLAPObjectGroup grp) {
        return db.canChildObjectsLoaded() && (grp.getSize() > 0 && grp.getSize() < defaultObjectLoadthreshold) ? true
                : false;
    }

    /**
     * Sets the level 3 loaded flag on all tables.
     */
    protected void setLevel3LoadedFlagOnAllTables() {
        NamespaceUtils.setLevel3LoadedFlag(this.tables.getSortedServerObjectList().iterator());
        NamespaceUtils.setLevel3LoadedFlag(this.foreigntables.getSortedServerObjectList().iterator());
    }

    @Override
    public String toString() {
        return "Namespace []";
    }

    /**
     * Load partition next level.
     *
     * @param tableObjGrp the table obj grp
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void loadPartitionNextLevel(TableObjectGroup tableObjGrp, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        PartitionTable.getColumnInfo(tableObjGrp, conn, this, getOid());

        MPPDBIDELoggerUtility.perf("Partition Table: Stage : 4");
        PartitionTable.getPartitionConstraints(tableObjGrp, conn, this, getOid());
        MPPDBIDELoggerUtility.perf("Partition Table: Stage : 5");
        PartitionTable.getPartitionIndexes(tableObjGrp, conn, this, getOid());
        MPPDBIDELoggerUtility.perf("Partition Table: Stage : 6");
        PartitionTable.getPartitions(tableObjGrp, conn, this, getOid());
    }

    /**
     * Expected to be called after refreshTableMetaData() only. Doesn't clear
     * columns before fetching.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void fetchColumnMetaData(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        String qry1 = "WITH tbl AS ( select oid as tableid,relnamespace as namespaceid from pg_class where relnamespace = %d"
                + "and relkind = 'r' and parttype ='n'), "
                + "attr AS ( select c.attnum as columnidx,c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid "
                + ",c.attlen as length,c.atttypmod as precision,c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid "
                + "from pg_attribute c where c.attrelid in (select tableid from tbl) and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ "
                + "where typ.oid in (select datatypeoid from attr) ), attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d "
                + "where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))"
                + "select t.tableid as tableid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,"
                + "default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.tableid = c.tableoid) LEFT JOIN attrdef d ON(t.tableid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.tableid ,c.columnidx;";

        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, qry1, this.getOid()));
        try {
            fetchColumnMetadataFromRs(rs);

        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Fetch column metadata from rs.
     *
     * @param rs the rs
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void fetchColumnMetadataFromRs(ResultSet rs)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        boolean hasNext = rs.next();
        while (hasNext) {
            long tableId = rs.getLong("tableid");
            TableMetaData table = getTablesGroup().getObjectById(tableId);
            long datatypeid = rs.getLong("datatypeoid");
            Namespace nameSpace = getDatabase().getNameSpaceById(rs.getLong("dtns"));
            TypeMetaData typeMeta = TypeMetaData.getTypeById(nameSpace, datatypeid);
            if (table == null) {
                if (!privilegeFlag) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                }
            } else {
                table.convertToColumnMetaData(rs, typeMeta);
            }

            hasNext = rs.next();
        }
    }

    /**
     * Fetch table column meta data.
     *
     * @param tableOid the table oid
     * @param tablegrp the tablegrp
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void fetchTableColumnMetaData(long tableOid, TableObjectGroup tablegrp, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        String qry = "select t.oid as tableid, t.relnamespace as namespaceid, c.attnum as columnidx, "
                + "c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, c.atttypmod as "
                + "precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, d.adsrc as default_value, "
                + "d.adbin as attDefStr "
                + "from pg_class t left join pg_attribute c on (t.oid = c.attrelid and t.relkind = 'r') "
                + "left join pg_attrdef d on (t.oid = d.adrelid and c.attnum = d.adnum) left join pg_type "
                + "typ on (c.atttypid = typ.oid) " + "where c.attisdropped = 'f' and c.attnum > 0 and t.oid = %d"
                + " order by t.oid, c.attnum;";

        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, qry, tableOid));
        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                convertToColumnMetaData(tablegrp, rs);
                hasNext = rs.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Convert to column meta data.
     *
     * @param tablegrp the tablegrp
     * @param rs the rs
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void convertToColumnMetaData(TableObjectGroup tablegrp, ResultSet rs)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        long tableId = rs.getLong("tableid");
        TableMetaData tbl = tablegrp.getObjectById(tableId);
        long datatypeid = rs.getLong("datatypeoid");

        Namespace dtNamspace = getDatabase().getNameSpaceById(rs.getLong("dtns"));

        TypeMetaData type = TypeMetaData.getTypeById(dtNamspace, datatypeid);
        if (tbl == null) {
            if (!privilegeFlag) {
                MPPDBIDELoggerUtility
                        .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
            }
        } else {
            tbl.convertToColumnMetaData(rs, type);
        }
    }

    /**
     * Expected to be called after refreshTableMetaData. So will not clear
     * constraints.
     *
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void fetchAllConstraints(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        MPPDBIDELoggerUtility.perf("Loading Table constraints start");
        String qry = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
                + "c.conname  as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
                + "c.condeferred  as deferred, c.convalidated as validate, c.conindid  as indexid, "
                + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
                + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, "
                + "c.confkey as fkeycolumnlist, " + "pg_get_constraintdef(c.oid) as const_def "
                + "FROM pg_constraint c join pg_class cl on c.conrelid = cl.oid where c.connamespace=%d"
                + " and cl.parttype not in ('p','v')" + " and c.conrelid <> 0" + ';';
        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, qry, getOid()));
        boolean hasNext = false;

        try {
            hasNext = rs.next();
            while (hasNext) {
                long namespaceid = rs.getLong("namespaceid");
                Namespace ns = getDatabase().getNameSpaceById(namespaceid);

                long tableId = rs.getLong("tableid");

                TableMetaData tbl = ns.getTablesGroup().getObjectById(tableId);
                TableMetaData fkeyTable = ns.getTablesGroup().getObjectById(rs.getLong("fkeytableId"));
                if (tbl == null) {
                    if (!ns.getPrivilegeFlag()) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                        throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                    }
                } else {
                    tbl.addConstraint(rs, ns, fkeyTable);
                }

                hasNext = rs.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.perf("Loading Table constraints ends");
        }
    }

    /**
     * Fetch constraint for table.
     *
     * @param tbl the tbl
     * @param tblgrp the tblgrp
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void fetchConstraintForTable(TableMetaData tbl, TableObjectGroup tblgrp, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        String qry = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
                + "c.conname as constraintname, c.contype as constrainttype, c.condeferrable as deferrable, "
                + "c.condeferred as deferred, c.convalidated as validate, c.conindid as indexid, "
                + "c.confrelid as fkeytableId, c.confupdtype as updatetype, c.confdeltype as deletetype, "
                + "c.confmatchtype as matchtype, c.consrc as expr, c.conkey as columnlist, "
                + "c.confkey as fkeycolumnlist, pg_get_constraintdef(c.oid) as const_def "
                + "FROM pg_constraint c where c.conrelid = %d" + ';';

        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, qry, tbl.getOid()));
        boolean hasNext = false;
        TableMetaData fkeyTable;

        try {
            hasNext = rs.next();
            while (hasNext) {
                long namespaceid = rs.getLong("namespaceid");
                Namespace ns = getDatabase().getNameSpaceById(namespaceid);
                fkeyTable = tblgrp.getObjectById(rs.getLong("fkeytableId"));
                tbl.addConstraint(rs, ns, fkeyTable);
                hasNext = rs.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Expected to be called after refreshTableMetaData. So will not clear
     * Indexes.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void fetchAllIndexes(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        MPPDBIDELoggerUtility.perf("Loading Indexes starts");
        String qry = String.format(Locale.ENGLISH, LOAD_INDEXES
                + " and ci.parttype not in ('p','v') and ci.relkind not in ('I');",
                getOid());
        ResultSet resultSet = conn.execSelectAndReturnRs(qry);
        boolean hasNext = false;

        try {
            hasNext = resultSet.next();
            while (hasNext) {
                Namespace ns = this.db.getNameSpaceById(resultSet.getLong("namespaceid"));
                TableMetaData table = ns.getTablesGroup().getObjectById(resultSet.getLong("tableId"));
                if (table == null) {
                    if (!privilegeFlag) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                        throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                    }
                } else {
                    table.addIndex(resultSet);
                }
                hasNext = resultSet.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(resultSet);
            MPPDBIDELoggerUtility.perf("Loading Indexes ends");
        }
    }

    /**
     * Gets the tables group.
     *
     * @return the tables group
     */
    public TableObjectGroup getTablesGroup() {
        return this.tables;
    }

    /**
     * Gets the view group.
     *
     * @return the view group
     */
    public ViewObjectGroup getViewGroup() {
        return this.views;
    }

    /**
     * Gets the sequence group.
     *
     * @return the sequence group
     */
    public SequenceObjectGroup getSequenceGroup() {
        return this.sequence;
    }

    /**
     * Gets the synonym group.
     *
     * @return the synonym group
     */
    public SynonymObjectGroup getSynonymGroup() {
        return this.synonyms;
    }

    /**
     * description: gets the trigger group
     *
     * @return TriggerObjectGroup the trigger object group
     */
    public TriggerObjectGroup getTriggerObjectGroup() {
        return this.triggerGroups;
    }

    /**
     * Gets the type by name.
     *
     * @param name the name
     * @return the type by name
     */
    public TypeMetaData getTypeByName(String name) {
        ServerObject obj = this.types.get(name);

        // servObj will definately be namespace, but findbugs doesn't detect
        // generics. So below fix
        if (obj instanceof TypeMetaData) {
            return (TypeMetaData) obj;
        }

        return null;
    }

    /**
     * Gets the type by oid.
     *
     * @param oid the oid
     * @return the type by oid
     */
    public TypeMetaData getTypeByOid(long oid) {
        ServerObject obj = this.types.getObjectById(oid);

        // servObj will definately be namespace, but findbugs doesn't detect
        // generics. So below fix
        if (obj instanceof TypeMetaData) {
            return (TypeMetaData) obj;
        }

        return null;
    }

    @Override
    public int hashCode() {
        return MPPDBIDEConstants.PRIME_31 + Long.valueOf(getOid()).hashCode() + getDatabase().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Namespace)) {
            return false;
        }

        Namespace other = (Namespace) obj;
        if (getOid() == other.getOid() && getDatabase().equals(other.getDatabase())) {
            return true;

        }
        return false;
    }

    /**
     * Adds the table to search pool.
     *
     * @param table the table
     */
    public void addTableToSearchPool(TableMetaData table) {
        getTablesGroup().addToGroup(table);
        getDatabase().getSearchPoolManager().addTableToSearchPool(table);

    }

    /**
     * Adds the table to given search pool.
     *
     * @param table the table
     */
    public void addTableToGivenSearchPool(TableMetaData table) {
        if (table instanceof ForeignPartitionTable) {
            getForeignTablesGroup().addToGroup(table);
        } else {
            getTablesGroup().addToGroup(table);
        }
        getDatabase().getSearchPoolManager().addTableToSearchPool(table);

    }

    /**
     * Adds the debug object to search pool.
     *
     * @param dbgObj the dbg obj
     */
    public void addDebugObjectToSearchPool(DebugObjects dbgObj) {
        DebugObjectGroup objGroup = (DebugObjectGroup) getFunctions();
        objGroup.addToGroup(dbgObj);
        getDatabase().getSearchPoolManager().addDebugObjectToSearchPool(dbgObj);
    }

    /**
     * Find prefix matching child objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findPrefixMatchingChildObjects(String prefix) {
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(findMatchingTables(prefix));
        retObj.putAll(findMatchingForeignTables(prefix));
        retObj.putAll(findMatchingDebugObjects(prefix));
        retObj.putAll(findMatchingViews(prefix));
        retObj.putAll(findMatchingSequences(prefix));
        retObj.putAll(findMatchingSynonyms(prefix));
        return retObj;
    }

    /**
     * Find matching hyperlink.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findMatchingHyperlink(String prefix) {
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(findMatchingTablesHyperLink(prefix));
        retObj.putAll(findMatchingDebugObjectForsHyperLink(prefix));
        retObj.putAll(findMatchingViewsForHyperLink(prefix));
        return retObj;
    }

    /**
     * Find matching debug object fors hyper link.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, DebugObjects> findMatchingDebugObjectForsHyperLink(String prefix) {
        return functions.getMatchingHyperLink(prefix);
    }

    /**
     * Find matching tables hyper link.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, TableMetaData> findMatchingTablesHyperLink(String prefix) {
        return tables.getMatchingTablesHyperLink(prefix);
    }

    /**
     * Find matching foreign tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, TableMetaData> findMatchingForeignTables(String prefix) {
        return foreigntables.getMatching(prefix);
    }

    /**
     * Find matching views for hyper link.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, ViewMetaData> findMatchingViewsForHyperLink(String prefix) {
        return views.getMatchingTablesHyperLink(prefix);
    }

    /**
     * Find matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, TableMetaData> findMatchingTables(String prefix) {
        return tables.getMatching(prefix);
    }

    /**
     * Find matching views.
     *
     * @param prefix the prefix
     * @return the map<? extends string,? extends server object>
     */
    private Map<? extends String, ? extends ServerObject> findMatchingViews(String prefix) {
        return views.getMatching(prefix);
    }

    /**
     * Find matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, SequenceMetadata> findMatchingSequences(String prefix) {
        return sequence.getMatching(prefix);
    }

    /**
     * Find matching synonyms.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, SynonymMetaData> findMatchingSynonyms(String prefix) {
        return synonyms.getMatching(prefix);
    }

    /**
     * Find matching trigger.
     *
     * @param String the prefix
     * @return SortedMap<String, TriggerMetaData> the sorted map
     */
    private SortedMap<String, TriggerMetaData> findMatchingTrigger(String prefix) {
        return triggerGroups.getMatching(prefix);
    }

    @Override
    public String getSearchName() {
        return getName() + " - " + getTypeLabel();
    }

    /**
     * Find matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, DebugObjects> findMatchingDebugObjects(String prefix) {
        return functions.getMatching(prefix);
    }

    /**
     * Adds the view to search pool.
     *
     * @param view the view
     */
    public void addViewInSearchPool(ViewMetaData view) {
        db.getSearchPoolManager().addviewToSearchPool(view);
    }

    /**
     * Adds the views to group.
     *
     * @param view the view
     */
    public void addViewsToGroup(ViewMetaData view) {
        views.addToGroup(view);
    }

    /**
     * Adds the synonym to group.
     *
     * @param synonym the synonym
     */
    public void addSynonymToGroup(SynonymMetaData synonym) {
        synonyms.addToGroup(synonym);
    }

    /**
     * Fetch level 2 view column info.
     *
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void fetchLevel2ViewColumnInfo(DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        int clmCount = 0;
        String query = " WITH tbl AS ( select oid as viewid,relnamespace as namespaceid from pg_class where relnamespace =%d"
                + " and relkind = 'v'), attr AS ( select c.attnum as columnidx,"
                + "c.attname as name , pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns,c.atttypid as datatypeoid ,c.attlen as length,c.atttypmod as precision,"
                + "c.attndims as dimentions,c.attnotnull as notnull,c.atthasdef as isdefaultvalueavailable,c.attrelid as tableoid from pg_attribute c where c.attrelid in (select viewid from tbl) "
                + "and c.attisdropped = 'f' and c.attnum > 0), dtype AS (select typ.typnamespace as dtns,oid from pg_type typ where typ.oid in (select datatypeoid from attr) ), "
                + "attrdef AS ( select d.adsrc as default_value ,d.adbin as attDefStr ,adrelid,adnum from pg_attrdef d where d.adrelid in ( select oid from tbl) and d.adnum in( select columnidx from attr))"
                + " select t.viewid as viewid ,t.namespaceid as namespaceid,c.columnidx,c.name,c.displaycolumns,c.datatypeoid,typ.dtns,c.length,c.precision,c.dimentions,c.notnull,c.isdefaultvalueavailable,"
                + "default_value, d.attDefStr FROM tbl t LEFT JOIN attr c ON(t.viewid = c.tableoid) LEFT JOIN attrdef d ON(t.viewid = d.adrelid AND c.columnidx = d.adnum) LEFT JOIN dtype typ ON (c.datatypeoid = typ.oid) ORDER BY t.viewid ,c.columnidx;";

        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, query, this.getOid()));
        try {
            boolean hasNext = rs.next();
            while (hasNext) {
                long viewId = rs.getLong("viewid");
                long datatypeid = rs.getLong("datatypeoid");

                Object viewObj = getViewGroup().getObjectById(viewId);
                if (null == viewObj) {
                    if (!privilegeFlag) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                        throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                    }
                } else {
                    ViewMetaData view = (ViewMetaData) viewObj;
                    Namespace dtNamspace = this.db.getNameSpaceById(rs.getLong("dtns"));
                    TypeMetaData type = TypeMetaData.getTypeById(dtNamspace, datatypeid);

                    view.addViewColumn(rs, type);
                }
                clmCount++;
                hasNext = rs.next();
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                MPPDBIDELoggerUtility
                        .trace("Level 3 object loaded for columns :" + clmCount + " Source :Selected Namespace");
            }
            conn.closeResultSet(rs);
        }
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
        String query = "select v.oid as viewid, v.relnamespace as namespaceid, c.attnum as columnidx, "
                + "c.attname as name, pg_catalog.format_type(c.atttypid, c.atttypmod) as displayColumns, "
                + "c.atttypid as datatypeoid, typ.typnamespace as dtns, c.attlen as length, "
                + "c.atttypmod as precision, c.attndims as dimentions, "
                + "c.attnotnull as notnull, c.atthasdef as isdefaultvalueavailable, "
                + "d.adsrc as default_value, d.adbin as attDefStr " + "from pg_class v "
                + "left join pg_attribute c on (v.oid = c.attrelid and v.relkind = 'v') "
                + "left join pg_attrdef d on (v.oid = d.adrelid and c.attnum = d.adnum) "
                + "left join pg_type typ on (c.atttypid = typ.oid) " + "where c.attisdropped = 'f' and c.attnum > 0 "
                + "and v.oid = %d" + " order by v.oid, c.attnum";

        ResultSet rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, query, view.getOid()));
        try {
            ViewMetaData viewObj = getViewGroup().getObjectById(view.getOid());
            if (null != viewObj) {
                addViewColumnInfo(view, rs, viewObj);
            } else {
                if (!privilegeFlag) {
                    MPPDBIDELoggerUtility
                            .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID));
                    throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
                }
            }
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        } finally {
            conn.closeResultSet(rs);
        }
    }

    /**
     * Adds the view column info.
     *
     * @param view the view
     * @param rs the rs
     * @param viewObj the view obj
     * @throws SQLException the SQL exception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void addViewColumnInfo(ViewMetaData view, ResultSet rs, ViewMetaData viewObj)
            throws SQLException, DatabaseOperationException, DatabaseCriticalException {
        boolean hasNext = rs.next();
        if (hasNext) {
            long datatypeid = 0;
            Namespace dtNamspace = null;
            TypeMetaData type = null;
            while (hasNext) {
                datatypeid = rs.getLong("datatypeoid");
                dtNamspace = this.db.getNameSpaceById(rs.getLong("dtns"));
                type = TypeMetaData.getTypeById(dtNamspace, datatypeid);

                viewObj.addViewColumn(rs, type);

                hasNext = rs.next();
            }
            // successfully loaded all column info. Setting l3 loaded.
            viewObj.setLevel3Loaded(true);
        } else {
            view.setValid(false);
        }
    }

    /**
     * Gets the server name.
     *
     * @return the server name
     */
    public String getServerName() {
        return db.getServerName();
    }

    /**
     * Gets the database name.
     *
     * @return the database name
     */
    public String getDatabaseName() {
        return db.getName();
    }

    /**
     * Adds the view.
     *
     * @param view the view
     */
    public void addView(ViewMetaData view) {
        this.views.addToGroup(view);
        this.db.getSearchPoolManager().addviewToSearchPool(view);
    }

    /**
     * Adds the sequence.
     *
     * @param seq the seq
     */
    public void addSequence(SequenceMetadata seq) {
        this.sequence.addToGroup(seq);
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
     * Belongs to.
     *
     * @param database the database
     * @param server the server
     * @return true, if successful
     */
    public boolean belongsTo(Database database, Server server) {
        return this.getDatabase().belongsTo(database, server);
    }

    /**
     * Gets the foreign tables group.
     *
     * @return the foreign tables group
     */
    public ForeignTableGroup getForeignTablesGroup() {
        return foreigntables;
    }

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        String sizeStr = "...";
        if (isLoaded()) {
            int size = getFunctions().getSize() + getTablesGroup().getSize() + getViewGroup().getSize()
                    + getForeignTablesGroup().getSize() + getSequenceGroup().getSize() + getSynonymGroup().getSize();
            sizeStr = Integer.toString(size);
        }

        return getName() + " (" + sizeStr + ") ";
    }

    @Override
    public String getDisplayLabel() {
        return this.getLabel();
    }

    @Override
    public Object[] getChildren() {
        Collection<Object> children = new ArrayList<Object>(5);
        children.add(this.getFunctions());
        children.add(this.getTablesGroup());
        children.add(this.getViewGroup());
        children.add(this.getForeignTablesGroup());
        children.add(this.getSequenceGroup());
        if (isSynoymSupported()) {
            children.add(this.getSynonymGroup());
        }
        children.add(this.getTriggerObjectGroup());
        return children.toArray();
    }

    /**
     * Sets the load failed.
     */
    public void setLoadFailed() {
        ILazyLoadObject.super.setLoadFailed();
        removefromSearchPath();
    }

    /**
     * Removefrom search path.
     */
    private void removefromSearchPath() {
        db.getSearchPathHelper().removeFromSearchPath(this.getName());
    }

    /**
     * Exec rename table.
     *
     * @param table the table
     * @param newName the new name
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execRenameTable(TableMetaData table, String newName, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        table.execRename(newName, conn);
        refreshTable(table, conn, true);
    }

    /**
     * Adds the tables to group.
     *
     * @param table the table
     * @param conn the conn
     * @param tabletype the tabletype
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void addTablesToGroup(TableMetaData table, DBConnection conn, OBJECTTYPE tabletype)
            throws DatabaseCriticalException, DatabaseOperationException {
        switch (tabletype) {
            case PARTITION_TABLE: {
                if (table instanceof PartitionTable) {
                    if (table instanceof ForeignPartitionTable) {
                        table.addForeignTableToGroup(table);
                        loadPartitionNextLevel(foreigntables, conn);
                    } else {
                        table.addTableToGroup(table);
                        loadPartitionNextLevel(tables, conn);
                    }
                }
                break;
            }
            case FOREIGN_TABLE: {
                table.addForeignTableToGroup(table);
                break;
            }
            default: {
                table.addTableToGroup(table);
                table.refreshTableDetails(conn);
                break;
            }
        }
    }

    /**
     * Adds the foreign table to group.
     *
     * @param table the table
     */
    public void addForeignTableToGroup(TableMetaData table) {
        getForeignTablesGroup().addToGroup(table);
    }

    /**
     * Adds the table to group.
     *
     * @param table the table
     */
    public void addTableToGroup(TableMetaData table) {
        getTablesGroup().addToGroup(table);
        getDatabase().getSearchPoolManager().addTableToSearchPool(table);
    }

    /**
     * Removes the table.
     *
     * @param table the table
     * @param tableOid the table oid
     * @return the objecttype
     */
    private OBJECTTYPE removeTable(TableMetaData table, long tableOid) {
        table.clearDetailsCached();

        getDatabase().getSearchPoolManager().removeTableFromSearchPool(this.tables.getObjectById(tableOid));
        OBJECTTYPE tabletype = table.getType();

        switch (tabletype) {
            case PARTITION_TABLE: {
                if (table instanceof PartitionTable) {
                    if (table instanceof ForeignPartitionTable) {
                        this.getForeignTablesGroup().removeFromGroup(tableOid);
                    } else {
                        this.getTablesGroup().removeFromGroup(tableOid);
                    }
                }
                break;
            }
            case FOREIGN_TABLE: {
                this.getForeignTablesGroup().removeFromGroup(tableOid);
                break;
            }
            default: {
                this.getTablesGroup().removeFromGroup(tableOid);
                break;
            }
        }
        return tabletype;
    }

    /**
     * Gets the updated namespace.
     *
     * @param resultSet the result set
     * @return the updated namespace
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    private Namespace getUpdatedNamespace(ResultSet resultSet) throws DatabaseOperationException, SQLException {
        long namespaceID = resultSet.getLong("relnamespace");
        Namespace namespace = this.getDatabase().getNameSpaceById(namespaceID);
        return namespace;
    }

    /**
     * Gets the children size.
     *
     * @return the children size
     */
    public int getChildrenSize() {
        return getFunctions().getSize() + getTablesGroup().getSize() + getViewGroup().getSize()
                + getForeignTablesGroup().getSize() + getSequenceGroup().getSize() + getSynonymGroup().getSize()
                + getTriggerObjectGroup().getSize();
    }

    /**
     * Gets the object browser label.
     *
     * @return the object browser label
     */
    public String getObjectBrowserLabel() {
        String sizeStr = "";
        if (isLoaded()) {
            int size = getChildrenSize();
            sizeStr = " (" + Integer.toString(size) + ") ";
        }

        return getName() + sizeStr;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Object getParent() {
        return this.getDatabase();
    }

    /**
     * Gets the searched table.
     *
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched table
     */
    public TableMetaData getSearchedTable(int tblId, String tblName) {
        TableMetaData tbl = getTablesGroup().getObjectById(tblId);
        if (null == tbl) {
            tbl = new TableMetaData(tblId, tblName, this, null, OBJECTTYPE.TABLEMETADATA);
            addTableToSearchPool(tbl);
        }
        return tbl;
    }

    /**
     * Gets the searched partition table.
     *
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched partition table
     */
    public PartitionTable getSearchedPartitionTable(int tblId, String tblName) {
        PartitionTable parTable = (PartitionTable) getTablesGroup().getObjectById(tblId);
        if (null == parTable) {
            parTable = (PartitionTable) getForeignTablesGroup().getObjectById(tblId);
        }
        if (null == parTable) {
            parTable = new PartitionTable(this);
            parTable.setOid(tblId);
            parTable.setName(tblName);
            addTableToGroup(parTable);
        }
        return parTable;
    }

    /**
     * Gets the searched foreign table.
     *
     * @param tblId the tbl id
     * @param ftOptions the ft options
     * @return the searched foreign table
     */
    public TableMetaData getSearchedForeignTable(int tblId, String ftOptions) {
        TableMetaData forTable = getForeignTablesGroup().getObjectById(tblId);
        if (null == forTable) {
            if (ftOptions.contains("gsfs")) {
                forTable = new ForeignTable(this, OBJECTTYPE.FOREIGN_TABLE_GDS);
            } else if (ftOptions.contains("format=orc")) {
                forTable = new ForeignTable(this, OBJECTTYPE.FOREIGN_TABLE_HDFS);
            }
            if (null != forTable) {
                addForeignTableToGroup(forTable);
            }
        }
        return forTable;
    }

    /**
     * Gets the searched sequence.
     *
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched sequence
     */
    public SequenceMetadata getSearchedSequence(int tblId, String tblName) {
        SequenceMetadata seq = getSequenceGroup().getObjectById(tblId);
        if (null == seq) {
            seq = SequenceMetadataUtil.getSearchedSequence(this, tblId, tblName);
            addSequence(seq);
        }
        return seq;
    }

    /**
     * Gets the searched view.
     *
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched view
     */
    public ViewMetaData getSearchedView(int tblId, String tblName) {
        ViewMetaData viewTable = getViewGroup().getObjectById(tblId);
        if (null == viewTable) {
            viewTable = new ViewMetaData(tblId, tblName, this, getDatabase());
            addViewsToGroup(viewTable);
        }
        return viewTable;
    }

    /**
     * Gets the searched synonym.
     *
     * @param tblId the tbl id
     * @param tblName the tbl name
     * @return the searched synonym
     */
    public SynonymMetaData getSearchedSynonym(int tblId, String tblName) {
        SynonymMetaData synonym = getSynonymGroup().getObjectById(tblId);
        if (null == synonym) {
            synonym = new SynonymMetaData(tblName, this);
            addSynonymToGroup(synonym);
        }
        return synonym;
    }

    /**
     * Find exact matching child objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findExactMatchingChildObjects(String prefix) {
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(findExactMatchingTables(prefix));
        retObj.putAll(findExactMatchingForeignTables(prefix));
        retObj.putAll(findExactMatchingDebugObjects(prefix));
        retObj.putAll(findExactMatchingViews(prefix));
        retObj.putAll(findExactMatchingSequences(prefix));
        retObj.putAll(findExactMatchingSynonyms(prefix));
        retObj.putAll(findExactMatchingTrigger(prefix));
        return retObj;
    }

    /**
     * Find exact matching sequences.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, SequenceMetadata> findExactMatchingSequences(String prefix) {
        return sequence.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching synonyms.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, SynonymMetaData> findExactMatchingSynonyms(String prefix) {
        return synonyms.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching trigger
     *
     * @param String the prefix
     * @return SortedMap<String, TriggerMetaData> the sorted map
     */
    private SortedMap<String, TriggerMetaData> findExactMatchingTrigger(String prefix) {
        return triggerGroups.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching views.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, ViewMetaData> findExactMatchingViews(String prefix) {
        return views.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching debug objects.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, DebugObjects> findExactMatchingDebugObjects(String prefix) {
        return functions.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching foreign tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    private SortedMap<String, TableMetaData> findExactMatchingForeignTables(String prefix) {
        return foreigntables.getMatchingHyperLink(prefix);
    }

    /**
     * Find exact matching tables.
     *
     * @param prefix the prefix
     * @return the sorted map
     */
    public SortedMap<String, TableMetaData> findExactMatchingTables(String prefix) {
        return tables.getMatchingHyperLink(prefix);
    }

    /**
     * Find all child objects.
     *
     * @return the sorted map
     */
    public SortedMap<String, ServerObject> findAllChildObjects() {
        SortedMap<String, ServerObject> retObj = new TreeMap<String, ServerObject>();
        retObj.putAll(findMatchingTables(""));
        retObj.putAll(findMatchingForeignTables(""));
        retObj.putAll(findMatchingDebugObjects(""));
        retObj.putAll(findMatchingViews(""));
        retObj.putAll(findMatchingSequences(""));
        retObj.putAll(findMatchingSynonyms(""));
        retObj.putAll(findMatchingTrigger(""));
        return retObj;
    }

    /**
     * Gets the all tables for namespace.
     *
     * @return the all tables for namespace
     */
    public ArrayList<TableMetaData> getAllTablesForNamespace() {
        ArrayList<TableMetaData> tablesList = new ArrayList<TableMetaData>(MPPDBIDEConstants.OBJECT_ARRAY_SIZE);
        tablesList.addAll(tables.getSortedServerObjectList());
        tablesList.addAll(foreigntables.getSortedServerObjectList());
        Collections.sort(tablesList, new NamespaceComparator());
        return tablesList;
    }

    /**
     * Title: class Description: The Class NamespaceComparator. 
     *
     */
    private static class NamespaceComparator implements Comparator<TableMetaData>, Serializable {

        private static final long serialVersionUID = 1L;

        @Override
        public int compare(TableMetaData o1, TableMetaData o2) {
            if (o1.getName().equalsIgnoreCase(o2.getName())) {
                if (!o1.getName().equals(o2.getName())) {
                    return o1.getName().compareTo(o2.getName());
                }
            }
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        StringBuilder query = new StringBuilder(DROP_QUERY);
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
        if (obj instanceof ViewMetaData) {
            removeFromCollection(views, obj);
        } else if (obj instanceof SequenceMetadata) {
            removeFromCollection(sequence, obj);
        } else if (obj instanceof DebugObjects) {
            removeFromCollection(functions, obj);
        } else if (obj instanceof ForeignTable || obj instanceof ForeignPartitionTable) {
            removeFromCollection(foreigntables, obj);
        } else if (obj instanceof TableMetaData || obj instanceof PartitionTable) {
            removeFromCollection(tables, obj);
        } else if (obj instanceof SynonymMetaData) {
            removeFromCollection(synonyms, obj);
        }
    }

    /**
     * Removes the from collection.
     *
     * @param group the group
     * @param obj the obj
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void removeFromCollection(OLAPObjectGroup group, ServerObject obj) {
        if (group != null) {
            group.remove(obj);
        }
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        return true;
    }

    @Override
    public void getAllObjects(DBConnection conn, JobCancelStatus status)
            throws DatabaseOperationException, DatabaseCriticalException {
        checkCancelStatusAndAbort(conn, status);
        shallowLoadDebugableObjects(OBJECTTYPE.OBJECTTYPE_BUTT, db.getConnectionManager().getObjBrowserConn(), false);
        checkCancelStatusAndAbort(conn, status);
        checkCancelStatusAndAbort(conn, status);
        refreshAllViewsInNamespace(conn);
        checkCancelStatusAndAbort(conn, status);
        refreshTableHirarchy(conn);
        checkCancelStatusAndAbort(conn, status);
        refreshAllForeignTableMetadataInNamespace(conn);
        checkCancelStatusAndAbort(conn, status);
        checkCancelStatusAndAbort(conn, status);
        refreshSequences(conn);
        loadSynonyms(conn);
        loadTriggers(conn);
        MPPDBIDELoggerUtility.perf(this.getDisplayLabel() + " - Load done!!");
        checkCancelStatusAndAbort(conn, status);
        setLevel3LoadedFlagOnAllTables();
        setLoaded();
    }

    /**
     * Refresh all views in namespace.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshAllViewsInNamespace(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshAllViewsInNamespace(conn);
    }

    /**
     * Refresh table hirarchy.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshTableHirarchy(DBConnection conn) throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshTableHirarchy(conn);
    }

    /**
     * Refresh all foreign table metadata in namespace.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshAllForeignTableMetadataInNamespace(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshAllForeignTableMetadataInNamespace(conn);
    }

    /**
     * Refresh all metadata in namespace on demand.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void refreshAllMetadataInNamespaceOnDemand(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshAllMetadataInNamespaceOnDemand(conn);
    }

    /**
     * Refresh all table metadata in namespace.
     *
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshAllTableMetadataInNamespace(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshAllTableMetadataInNamespace(conn);
    }

    /**
     * Refresh view.
     *
     * @param view the view
     * @param conn the conn
     * @param isRenameFlow the is rename flow
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshView(ViewMetaData view, DBConnection conn, boolean isRenameFlow)
            throws DatabaseOperationException, DatabaseCriticalException {
        tblManager.refreshView(view, conn, isRenameFlow);
    }

    /**
     * Refresh table.
     *
     * @param table the table
     * @param conn the conn
     * @param isRenameFlow the is rename flow
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshTable(TableMetaData table, DBConnection conn, boolean isRenameFlow)
            throws DatabaseCriticalException, DatabaseOperationException {
        tblManager.refreshTable(table, conn, isRenameFlow);
    }

    /**
     * Title: class Description: The Class TableViewManager.
     */
    private class TableViewManager {

        /**
         * Refresh table.
         *
         * @param table the table
         * @param conn the conn
         * @param isRenameFlow the is rename flow
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshTable(TableMetaData table, DBConnection conn, boolean isRenameFlow)
                throws DatabaseCriticalException, DatabaseOperationException {
            long tableOid = table.getOid();
            String qry = table.getSQLForTableMetaDatabyOid(tableOid, isRenameFlow);
            ResultSet resultSet = conn.execSelectAndReturnRs(qry);
            OBJECTTYPE tabletype = removeTable(table, tableOid);
            try {
                boolean hasnext = resultSet.next();
                if (hasnext) {
                    Namespace ns = getUpdatedNamespace(resultSet);
                    table.setTableNamespace(ns);
                    table.fillTableMetaFromRS(resultSet);
                    if (table.isValid()) {
                        addTablesToGroup(table, conn, tabletype);
                    }
                } else {
                    table.setValid(false);
                    if (privilegeFlag && !isRenameFlow) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
                        throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
                    } else if (!privilegeFlag) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED));
                        throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
                    }
                }
                table.setLoaded(true);
            } catch (SQLException exp) {
                GaussUtils.handleCriticalException(exp);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
            } finally {
                conn.closeResultSet(resultSet);
            }
        }

        /**
         * Refresh view.
         *
         * @param view the view
         * @param conn the conn
         * @param isRenameFlow the is rename flow
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        private void refreshView(ViewMetaData viewParam, DBConnection conn, boolean isRenameFlow)
                throws DatabaseOperationException, DatabaseCriticalException {
            ViewMetaData view = viewParam;
            long oid = view.getOid();
            db.getSearchPoolManager().removeViewFromSearchPool(view);
            views.removeFromGroup(oid);

            view = view.refreshSelfdata(conn, isRenameFlow);
            if (view != null) {
                view.addviewToSearchPool(view);
                view.addViewsToGroup(view);
                view.fetchViewColumnInfo(view, conn);
                view.setLoaded(true);
            } else {
                // no self data returned
                if (privilegeFlag && !isRenameFlow) {
                    throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
                } else {
                    throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
                    // refreshing a view didnt get any row, means it is dropped
                }
            }
        }

        /**
         * Refresh all views in namespace.
         *
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshAllViewsInNamespace(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            MPPDBIDELoggerUtility.perf("View: Stage : 1");
            for (ViewMetaData view : views) {
                db.getSearchPoolManager().removeViewFromSearchPool(view);
            }
            MPPDBIDELoggerUtility.perf("View: Stage : 2");
            views.clear();
            MPPDBIDELoggerUtility.perf("View: Stage : 3");
            String query = ViewUtils.getViewQueryByNamespaceId(getOid(), privilegeFlag);
            ViewUtils.fetchViews(Namespace.this, query, conn);
            MPPDBIDELoggerUtility.perf("Total views loaded :" + views.getSize());
            if (canLoadNextLevel(views)) {
                MPPDBIDELoggerUtility.perf("View: Stage : 4");
                fetchLevel2ViewColumnInfo(conn);
                setLoadedValueNonLoadedObject(views);
            } else {
                MPPDBIDELoggerUtility.perf("View: Stage : 4(skip)");
            }

            MPPDBIDELoggerUtility.perf("View: Stage : 5/5");
        }

        /**
         * Refresh table hirarchy.
         *
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshTableHirarchy(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            MPPDBIDELoggerUtility.perf("Table: Stage : 1");
            clearCollection(tables);
            MPPDBIDELoggerUtility.perf("Table: Stage : 2");

            refreshAllTableMetadataInNamespace(conn);

            MPPDBIDELoggerUtility.perf("Total tables loaded :" + tables.getSize());
            if (canLoadNextLevel(tables)) {
                MPPDBIDELoggerUtility.perf("Table: Stage : 3");
                fetchColumnMetaData(conn);
                MPPDBIDELoggerUtility.perf("Table: Stage : 4");
                fetchAllConstraints(conn);
                MPPDBIDELoggerUtility.perf("Table: Stage : 5");
                fetchAllIndexes(conn);
                setLoadedValueNonLoadedObject(tables);
            } else {
                MPPDBIDELoggerUtility.perf("Table: Stage : 3(skip)");
                MPPDBIDELoggerUtility.perf("Table: Stage : 4(skip)");
                MPPDBIDELoggerUtility.perf("Table: Stage : 5(skip)");
            }
            MPPDBIDELoggerUtility.perf("Table: Stage : 6/6");
        }

        /**
         * Removes all the tables from the namespace and adds all the tables.
         *
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshAllForeignTableMetadataInNamespace(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            List<TableMetaData> ftables;
            boolean isAtleastOnePTPresent = false;
            MPPDBIDELoggerUtility.perf("Foreign Table: Stage : 1");
            clearCollection(foreigntables);
            ftables = ForeignTable.getAllFTablesInSchema(conn, Namespace.this, getOid());
            MPPDBIDELoggerUtility.perf("Foreign Table: Stage : 2");
            for (TableMetaData ftab : ftables) {
                if (ftab instanceof PartitionTable) {
                    isAtleastOnePTPresent = true;
                }
                ftab.setLoaded(false);
                getForeignTablesGroup().addToGroup(ftab);
                getDatabase().getSearchPoolManager().addTableToSearchPool(ftab);
            }
            MPPDBIDELoggerUtility.perf("Total Foreign Tables loaded :" + getForeignTablesGroup().getSize());
            if (canLoadNextLevel(foreigntables)) {
                MPPDBIDELoggerUtility.perf("Foreign Table: Stage : 3");
                ForeignTable.getColumnInfo(getForeignTablesGroup(), conn, Namespace.this, getOid());
                if (isAtleastOnePTPresent) {
                    loadPartitionNextLevel(foreigntables, conn);
                }
            } else {
                MPPDBIDELoggerUtility.perf("Foreign Table: Stage : 3(skip)");
            }

            MPPDBIDELoggerUtility.perf("Foreign Table: Stage : 4/4");
        }

        /**
         * Refresh all metadata in namespace on demand.
         *
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshAllMetadataInNamespaceOnDemand(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            String qry = "select tbl.oid as oid, tbl.relname relname, tbl.relnamespace relnamespace, "
                    + "tbl.relkind relkind, tbl.parttype parttype, frgn.ftoptions " + "from pg_class tbl "
                    + "left join pg_foreign_table frgn on (tbl.oid = frgn.ftrelid) "
                    + "where tbl.relkind in ('r','v','f') and tbl.parttype in ('n', 'p', 'v') and "
                    + "tbl.relnamespace = %d";
            if (privilegeFlag) {
                qry += " and has_table_privilege(QUOTE_IDENT(%s" + ") || '.' || QUOTE_IDENT(tbl.relname), 'SELECT')";
            }
            qry += ";";

            ResultSet rs = conn.execSelectAndReturnRs(
                    String.format(Locale.ENGLISH, qry, getOid(), ServerObject.getLiteralName(getName())));
            TableMetaData table = null;
            ViewMetaData view = null;
            String relkind = null;
            try {
                boolean hasNext = rs.next();
                while (hasNext) {
                    relkind = rs.getString("relkind");

                    if ("v".equals(relkind)) {
                        view = ViewUtils.convertToViewMetaDataOnDemand(rs, getDatabase());
                        view.setLoaded(false);
                        addView(view);
                    } else {
                        table = TableMetaData.convertToTableMetadataOnDemand(rs, db);
                        if (table != null) {
                            table.setLoaded(false);
                            if (table instanceof ForeignTable) {
                                getForeignTablesGroup().addToGroup(table);
                            } else {
                                getTablesGroup().addToGroup(table);
                            }
                            getDatabase().getSearchPoolManager().addTableToSearchPool(table);
                        }
                    }
                    hasNext = rs.next();
                }
            } catch (SQLException exp) {
                GaussUtils.handleCriticalException(exp);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
            } finally {
                conn.closeResultSet(rs);
            }
        }

        /**
         * Removes all the tables from the namespace and adds all the tables.
         *
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshAllTableMetadataInNamespace(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            String baseQuery = String.format(Locale.ENGLISH, QUERY_FOR_ALL_NORMAL_TABLES_BY_NAMESPACE_ID, getOid());
            String qry = TableMetaData.formQueryForTableMetadata(baseQuery,
                    getDatabase().getServer().isServerCompatibleToNodeGroupPrivilege(), privilegeFlag);

            ResultSet rs = conn.execSelectAndReturnRs(qry);
            clearCollection(tables);
            try {
                boolean hasNext = rs.next();
                boolean isAtleastOnePTPresent = false;
                while (hasNext) {
                    if ("n".equals(rs.getString("parttype"))) {
                        // regular table
                        TableMetaData.convertToTableMetadata(rs, Namespace.this);
                    } else {
                        // partition table
                        PartitionTable.convertToPartitionTable(rs, Namespace.this);
                        isAtleastOnePTPresent = true;
                    }
                    hasNext = rs.next();
                }

                if (canLoadNextLevel(tables) && isAtleastOnePTPresent) {
                    loadPartitionNextLevel(tables, conn);
                }
            } catch (SQLException exp) {
                GaussUtils.handleCriticalException(exp);
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
            } finally {
                conn.closeResultSet(rs);
            }
        }
    }

    /**
     * Refresh debug object.
     *
     * @param objectId the object id
     * @param conn the conn
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshDebugObject(long objectId, DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        dbgObjManager.refreshDebugObject(objectId, conn);
    }

    /**
     * Shallow load debugable objects.
     *
     * @param type the type
     * @param objBrowserConn the obj browser conn
     * @param onDemand the on demand
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    private void shallowLoadDebugableObjects(OBJECTTYPE type, DBConnection objBrowserConn, boolean onDemand)
            throws DatabaseCriticalException, DatabaseOperationException {
        dbgObjManager.shallowLoadDebugableObjects(type, objBrowserConn, onDemand);
    }

    /**
     * Refresh db object.
     *
     * @param object the object
     * @return the i debug object
     * @throws MPPDBIDEException the MPPDBIDE exception
     */
    public IDebugObject refreshDbObject(IDebugObject object) throws MPPDBIDEException {
        return dbgObjManager.refreshDbObject(object);
    }

    @Override
    public void dropDbObject(IDebugObject debugObject, DBConnection dbconn) throws MPPDBIDEException {
        dbgObjManager.dropDbObject(debugObject, dbconn);
    }

    /**
     * Refresh debug object group.
     *
     * @param type the type
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void refreshDebugObjectGroup(OBJECTTYPE type) throws DatabaseCriticalException, DatabaseOperationException {
        dbgObjManager.refreshDebugObjectGroup(type);
    }

    /**
     * Refresh debug object group.
     *
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void refreshDebugObjectGroup() throws DatabaseOperationException, DatabaseCriticalException {
        dbgObjManager.refreshDebugObjectGroup();
    }

    /**
     * Title: class Description: The Class DebugObjectsManager.
     */
    private class DebugObjectsManager {

        /**
         * Refresh debug object.
         *
         * @param objectId the object id
         * @param conn the conn
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshDebugObject(long objectId, DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            String qry1 = Namespace.REFRESH_DBG_OBJ_QRY ;
            if (privilegeFlag) {
                qry1 += QUERY_DBG_OBJ_PRIVILEGE;
            }
            String qry = String.format(Locale.ENGLISH, qry1, objectId);
            ResultSet rs = conn.execSelectAndReturnRs(qry);
            boolean hasNext = false;
            db.getSearchPoolManager().removeDebugObjectFromSearchPool(functions.getObjectById(objectId));
            functions.removeFromGroup(objectId);

            try {
                hasNext = rs.next();
                if (!hasNext) {
                    if (privilegeFlag) {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE));
                        throw new DatabaseOperationException(IMessagesConstants.ERR_DENIED_ACCESS_PRIVILEGE);
                    } else {
                        MPPDBIDELoggerUtility
                                .error(MessageConfigLoader.getProperty(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED));
                        throw new DatabaseOperationException(IMessagesConstants.MSG_GUI_OBJECT_MAY_DROPPED);
                    }
                }
                while (hasNext) {
                    DebugObjects.DebugObjectsUtils.convertToObject(rs, db);
                    hasNext = rs.next();
                }
                refreshDebugObjectGroup();
            } catch (SQLException exp) {
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
            } finally {
                conn.closeResultSet(rs);
            }
        }

        /**
         * Shallow load debugable objects.
         *
         * @param type the type
         * @param objBrowserConn the obj browser conn
         * @param onDemand the on demand
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void shallowLoadDebugableObjects(OBJECTTYPE type, DBConnection objBrowserConn, boolean onDemand)
                throws DatabaseCriticalException, DatabaseOperationException {
            int counter = 0;
            String query;
            MPPDBIDELoggerUtility.perf("Debug Object: Stage : 1");
            query = NamespaceUtils.getDebugObjectsLoadQuery(type, onDemand, Namespace.this);
            ResultSet rs = objBrowserConn.execSelectAndReturnRs(query);
            boolean hasNext = false;

            try {
                hasNext = rs.next();
                while (hasNext) {
                    DebugObjects.DebugObjectsUtils.convertToObject(rs, getDatabase());
                    counter++;
                    hasNext = rs.next();
                }
            } catch (SQLException exp) {
                throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID);
            } finally {
                MPPDBIDELoggerUtility.perf("Debug Object: Stage : 2/2");
                if (MPPDBIDELoggerUtility.isTraceEnabled()) {
                    MPPDBIDELoggerUtility
                            .trace("Total number of Debug Objects Loaded for selected database is : " + counter);
                }
                objBrowserConn.closeResultSet(rs);
            }
        }

        /**
         * Refresh a particular object.
         *
         * @param object the object
         * @return the i debug object
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        private IDebugObject refreshDbObject(IDebugObject object) throws MPPDBIDEException {
            MPPDBIDELoggerUtility.debug("Namespace: refresh debug object.");
            long objectId = object.getOid();
            DebugObjects newObject = null;
            DebugObjectGroup objectGroup = null;

            try {
                objectGroup = getDebugObjectGroupByType(object.getObjectType());
                if (null != objectGroup && getConnectionManager() != null) {
                    refreshDebugObject(objectId, getConnectionManager().getObjBrowserConn());
                    newObject = objectGroup.getObjectById(objectId);
                }
            } catch (DatabaseOperationException dbOperationException) {
                throw dbOperationException;
            } catch (DatabaseCriticalException dbCriticalException) {
                throw dbCriticalException;
            }

            return newObject;
        }

        /**
         * Drop db object.
         *
         * @param debugObject the debug object
         * @param dbconn the dbconn
         * @throws MPPDBIDEException the MPPDBIDE exception
         */
        private void dropDbObject(IDebugObject debugObject, DBConnection dbconn) throws MPPDBIDEException {
            MPPDBIDELoggerUtility.debug("Namespace: drop debug object.");

            DebugObjectGroup objectGroup = null;
            try {
                objectGroup = getDebugObjectGroupByType(debugObject.getObjectType());

                dbconn.execNonSelect(((DebugObjects) debugObject).generateDropQuery());

                if (objectGroup != null) {
                    db.getSearchPoolManager().removeDebugObjectFromSearchPool((DebugObjects) debugObject);
                    objectGroup.removeFromGroup(debugObject.getOid());
                }
            } catch (DatabaseOperationException dbOperationException) {
                throw dbOperationException;
            } catch (DatabaseCriticalException dbCriticalException) {
                throw dbCriticalException;
            }
        }

        /**
         * Refresh a particular object group.
         *
         * @param type the type
         * @throws DatabaseCriticalException the database critical exception
         * @throws DatabaseOperationException the database operation exception
         */
        private void refreshDebugObjectGroup(OBJECTTYPE type)
                throws DatabaseCriticalException, DatabaseOperationException {
            MPPDBIDELoggerUtility.debug("ConnectionProfile: refresh Debug Object Group.");
            DebugObjectGroup objectGroup = getDebugObjectGroupByType(type);
            clearCollection(objectGroup);
            refreshDebugObjectGroup();
        }

        /**
         * Refresh debug object group.
         *
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        private void refreshDebugObjectGroup() throws DatabaseOperationException, DatabaseCriticalException {
            MPPDBIDELoggerUtility.debug("Executor: Refresh debug object group.");
            clearCollection(functions);
            if (getConnectionManager() != null) {
                shallowLoadDebugableObjects(OBJECTTYPE.PLSQLFUNCTION, getConnectionManager().getObjBrowserConn(),
                        false);
                MPPDBIDELoggerUtility.info("Executor: Getting all functions.");
            }
        }
    }

    @Override
    public boolean isLoaded() {
        return ILazyLoadObject.super.isLoaded();
    }

    @Override
    public boolean isLoadingInProgress() {
        return ILazyLoadObject.super.isLoadingInProgress();
    }

    @Override
    public LOADSTATUS getLoadStatus() {
        return this.loadStatus;
    }

    @Override
    public void setLoadStatus(LOADSTATUS status) {
        this.loadStatus = status;
    }

    /**
     * Gets the packages.
     *
     * @return the packages
     */
    @Override
    public ObjectGroup<?> getPackages() {
        return null;
    }

    @Override
    public ServerObject getNewlyCreatedTable(String tableName) {
        String query1 = "select tbl.relname relname,tbl.parttype parttype,tbl.relnamespace relnamespace,tbl.oid oid,"
                + "ts.spcname as reltablespace,tbl.relpersistence relpersistence, d.description as desc, "
                + "xctbl.nodeoids as nodes ,tbl.reloptions as reloptions "
                + "from pg_class tbl left join (select d.description, d.objoid from pg_description d "
                + "where d.objsubid=0) d on (tbl.oid = d.objoid) left join pgxc_class xctbl "
                + "on (tbl.oid = xctbl.pcrelid) left join pg_tablespace ts on ts.oid = tbl.reltablespace "
                + "where tbl.relkind = 'r' and tbl.parttype in ('n','p') and tbl.relname = '%s' "
                + "and tbl.relnamespace= %d";
        String query = String.format(Locale.ENGLISH, query1, tableName, this.getOid());
        DBConnection conn = getDatabase().getConnectionManager().getObjBrowserConn();
        TableMetaData table = null;
        ResultSet rs = null;
        try {
            rs = conn.execSelectAndReturnRs(query);
            boolean hasNext = rs.next();
            boolean isAtleastOnePTPresent = false;
            if (hasNext) {
                if ("p".equalsIgnoreCase(rs.getString("parttype"))) {
                    table = new PartitionTable(this);
                    isAtleastOnePTPresent = true;
                } else {
                    table = new TableMetaData(this);
                }
                table.populateTableMetaData(rs);
                table.setLoaded(false);
                this.addTableToSearchPool(table);
                if (canLoadNextLevel(tables) && isAtleastOnePTPresent) {
                    loadPartitionNextLevel(tables, conn);
                }
                table.refreshTableDetails(conn);
                return table;
            }
        } catch (DatabaseCriticalException | DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error while getting newly created table from database");
        } catch (SQLException exce) {
            MPPDBIDELoggerUtility.error("Error while getting newly created table from database");
        } finally {
            conn.closeResultSet(rs);
        }
        return table;
    }

    @Override
    public ServerObject getNewlyUpdatedTable(String tableName) {
        TableMetaData table = getTablesGroup().get(tableName);
        DBConnection dbConn = getDatabase().getConnectionManager().getObjBrowserConn();
        try {
            if (null != table) {
                refreshTable(table, dbConn, true);
                table.refreshTableDetails(dbConn);
            }
        } catch (DatabaseCriticalException | DatabaseOperationException exception) {
            MPPDBIDELoggerUtility.error("Error while getting newly updated table from database");
        }
        return table;
    }

    @Override
    public ServerObject getNewlyCreatedView(String viewName) {
        String query1 = "SELECT c.oid, n.oid as nspoid, n.nspname AS schemaname, c.relname AS viewname, "
                + "pg_get_userbyid(c.relowner) AS viewowner, "
                + "c.relkind as relkind "
                + "FROM (pg_class c LEFT JOIN pg_namespace n ON ((n.oid = c.relnamespace))) WHERE "
                + "(c.relkind = 'v'::\"char\") and c.relname='%s' and n.nspname='%s';";
        String query = String.format(Locale.ENGLISH, query1, viewName, this.getName());
        ResultSet rs = null;
        boolean hasNext = false;
        ViewMetaData view = null;
        DBConnection dbConn = null;
        Namespace ns = null;
        try {
            dbConn = getDatabase().getConnectionManager().getObjBrowserConn();
            rs = dbConn.execSelectAndReturnRs(query);
            hasNext = rs.next();
            while (hasNext) {
                view = ViewUtils.convertToViewMetaData(rs, this);
                view.setLoaded(false);
                ns = view.getNamespace();
                ns.addView(view);
                hasNext = rs.next();
            }
        } catch (SQLException exception) {
            try {
                GaussUtils.handleCriticalException(exception);
            } catch (DatabaseCriticalException exc) {
                MPPDBIDELoggerUtility.error("Error while getting newly created view from database");
            }
        } catch (DatabaseCriticalException | DatabaseOperationException exce) {
            MPPDBIDELoggerUtility.error("Error while getting newly created view from database");
        } finally {
            if (dbConn != null) {
                dbConn.closeResultSet(rs);
            }
        }
        return view;
    }

    @Override
    public ServerObject getNewlyUpdatedView(String viewName) {
        ViewMetaData view = getViewGroup().get(viewName);
        DBConnection dbConn = getDatabase().getConnectionManager().getObjBrowserConn();
        try {
            refreshView(view, dbConn, true);
        } catch (DatabaseOperationException | DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("Error while getting newly updated view from database");
        }
        return view;
    }

    @Override
    public ServerObject getNewlyCreateTrigger(String triggerName) {
        List<TriggerMetaData> triggers ;
        TriggerMetaData trigger = null;
        try {
            DBConnection dbConn = getDatabase().getConnectionManager().getObjBrowserConn();
            triggers = TriggerObjectGroup.fetchTriggerByName(this, dbConn, triggerName);
            for (TriggerMetaData tmpTrigger: triggers) {
                this.addTrigger(tmpTrigger);
                trigger = tmpTrigger;
            }
        } catch (DatabaseCriticalException exce) {
            MPPDBIDELoggerUtility.error("Error while getting newly created view from database");
        } catch (DatabaseOperationException excep) {
            MPPDBIDELoggerUtility.error("Error while getting newly created view from database");
        }
        return trigger;
    }

    /**
     * Gets the synonyms.
     *
     * @return the synonyms
     * @Title: getSynonyms
     * @Description: get the the synonyms
     */
    public OLAPObjectGroup<SynonymMetaData> getSynonyms() {
        return synonyms;
    }

    /**
     * Adds the synonym.
     *
     * @param SynonymMetaData the synonymMetaData
     */
    public void addSynonym(SynonymMetaData synonymMetaData) {
        synonyms.addToGroup(synonymMetaData);
        db.getSearchPoolManager().addsynonymToSearchPool(synonymMetaData);
    }

    /**
     * Load synonyms.
     *
     * @param freeConnection the free connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void loadSynonyms(DBConnection freeConnection) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            SynonymUtil.fetchSynonyms(this, freeConnection);
        } catch (MPPDBIDEException exception) {
            extractMPPDBIDExeception(exception);
        }
    }

    /**
     * Adds the trigger.
     *
     * @param TriggerMetaData the trigger
     */
    public void addTrigger(TriggerMetaData trigger) {
        triggerGroups.addToGroup(trigger);
        db.getSearchPoolManager().addTriggerToSearchPool(trigger);
    }

    /**
     * Load triggers.
     *
     * @param freeConnection the free connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void loadTriggers(DBConnection freeConnection) throws DatabaseOperationException, DatabaseCriticalException {
        try {
            TriggerObjectGroup.fetchTriggers(this, freeConnection);
        } catch (MPPDBIDEException exception) {
            extractMPPDBIDExeception(exception);
        }
    }

    /**
     * Extract MPPDBID exeception.
     *
     * @param mppDBexception the mpp D bexception
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    private void extractMPPDBIDExeception(MPPDBIDEException mppDBexception)
            throws DatabaseOperationException, DatabaseCriticalException {
        if (mppDBexception.getServerMessage() != null) {
            if (mppDBexception instanceof DatabaseOperationException) {
                MPPDBIDELoggerUtility.error(
                        MessageConfigLoader.getProperty(IMessagesConstants.EXCEPTION_OCCURED_WHILE_FETCHING_OBJECTS),
                        mppDBexception);
                throw new DatabaseOperationException(IMessagesConstants.EXCEPTION_OCCURED_WHILE_FETCHING_OBJECTS,
                        mppDBexception);
            }
            MPPDBIDELoggerUtility.error(
                    MessageConfigLoader.getProperty(IMessagesConstants.NAMESPACE_RETRIVE_CRITICAL_ERROR),
                    mppDBexception);
            throw new DatabaseCriticalException(IMessagesConstants.NAMESPACE_RETRIVE_CRITICAL_ERROR, mppDBexception);
        }
    }

    /**
     * isSynoymSupported
     * 
     * @return boolean isSynonym
     */
    public boolean isSynoymSupported() {
        return isSynoymSupported;
    }

    /**
     * setSynoymSupported
     * 
     * @param isSynoymSupported boolean
     */
    public void setSynoymSupported(boolean isSynoymSupported) {
        this.isSynoymSupported = isSynoymSupported;
    }

    /**
     * validateView
     * 
     * @return boolean value
     */
    public boolean validateView(long oid) {
        ViewObjectGroup viewGroup = getViewGroup();
        return null == viewGroup || null == viewGroup.getObjectById(oid);
    }

    /**
     * removeViewFromGroup method
     */
    public void removeViewFromGroup(long oid) {
        this.getViewGroup().removeFromGroup(oid);
    }

    /**
     * getTableObjectById
     * 
     * @param oid long
     * @return PartitionTable object
     * @throws SQLException exception
     */
    public PartitionTable getTableObjectById(long oid) throws SQLException {
        TableMetaData table = getTablesGroup().getObjectById(oid);
        if (table instanceof PartitionTable) {
            return (PartitionTable) table;
        }
        return null;
    }

    /**
     * getForeignTableById
     * 
     * @param oid value
     * @return object partition
     * @throws SQLException
     */
    public PartitionTable getForeignTableById(long oid) throws SQLException {
        TableMetaData table = getForeignTablesGroup().getObjectById(oid);
        if (table instanceof PartitionTable) {
            return (PartitionTable) table;
        }
        return null;
    }

    /**
     * validateAndGetTableObjGrp
     * 
     * @return boolean value
     */
    public boolean validateAndGetTableObjGrp(long oId) {
        TableObjectGroup group = getTableObjGroup(oId);
        return validateTableWithId(group, oId);
    }

    /**
     * validateTableWithId
     * 
     * @param group grp
     * @return boolean
     */
    private boolean validateTableWithId(TableObjectGroup group, long oId) {
        return null == group || null == group.getObjectById(oId);
    }

    /**
     * getTableObjGroup
     * 
     * @return table object group
     */
    private TableObjectGroup getTableObjGroup(long oId) {
        TableObjectGroup group = getTablesGroup();
        if (validateTableWithId(group, oId)) {
            group = getForeignTablesGroup();
        }
        return group;
    }

    /**
     * removeTableFRomSearchPool
     * 
     * @param name string
     */
    public void removeTableFRomSearchPool(String name) {
        getDatabase().getSearchPoolManager().getTableTrie().remove(name);
    }
    
    /**
     * getNamespceComments namespace commenst
     * 
     * @param conn connection
     * @return string query
     * @throws DatabaseOperationException exception
     */
    public String getNamespceComments(DBConnection conn) throws DatabaseOperationException {
        String query = String.format(Locale.ENGLISH,
                " SELECT description FROM pg_catalog.pg_description where objoid=%d", this.getOid());
        ResultSet rs = null;
        StringBuilder seqDDL = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        String desc = "";
        try {
            rs = conn.execSelectAndReturnRs(query);
            while (rs.next()) {
                desc = rs.getString("description");
                if (desc != null && !desc.isEmpty()) {
                    seqDDL.append(String.format(Locale.ENGLISH, "COMMENT ON SCHEMA %s IS '%s' ;", this.getDisplayName(),
                            desc));
                    seqDDL.append(MPPDBIDEConstants.LINE_SEPARATOR);
                }
            }
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("error getting free connection", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("error getting free connection", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.debug("fetch DDL done");
        }
        return seqDDL.toString();
    }
    
    /**
     * getErrorTableList
     * 
     * @return list error table
     */
    public Set<String> getErrorTableList() {
        return errorTableList;
    }
}
