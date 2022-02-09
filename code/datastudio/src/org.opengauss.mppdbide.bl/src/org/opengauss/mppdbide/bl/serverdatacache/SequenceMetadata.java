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
import org.opengauss.mppdbide.bl.export.EXPORTTYPE;
import org.opengauss.mppdbide.bl.serverdatacache.groups.SequenceObjectGroup;
import org.opengauss.mppdbide.bl.util.ExecTimer;
import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.MPPDBIDEConstants;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;
import org.opengauss.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class SequenceMetadata.
 * 
 */

public class SequenceMetadata extends BatchDropServerObject implements GaussOLAPDBMSObject, ISequenceMetaData {

    private Namespace namespace;
    private String owner;
    private String sequenceName;
    private String minValue;
    private String maxValue;
    private String startValue;
    private String cache;
    private boolean cycle;
    private String seqTableName;
    private String seqColumnName;
    private String seqSchemaName;
    private String incrementBy;

    private long oid;

    private static final String DROP_QUERY = "DROP SEQUENCE ";
    private static final String DROP_QUERY_IF_EXISTS = "DROP SEQUENCE IF EXISTS ";

    /**
     * Instantiates a new sequence metadata.
     *
     * @param oid the oid
     * @param name the name
     * @param namespace the namespace
     */
    public SequenceMetadata(long oid, String name, Namespace namespace) {
        super(oid, name, OBJECTTYPE.SEQUENCE_METADATA_GROUP, namespace != null ? namespace.getPrivilegeFlag() : true);
        this.namespace = namespace;
        this.oid = oid;
    }

    /**
     * Instantiates a new sequence metadata.
     *
     * @param nameSpace the name space
     */
    public SequenceMetadata(Namespace nameSpace) {
        this(0, "nosequence", nameSpace);
    }

    @Override
    public void dropSequence(DBConnection conn, boolean isAppendCascade)
            throws DatabaseOperationException, DatabaseCriticalException {
        String query = getDropQueryForOB(isAppendCascade);
        conn.execNonSelect(query);
        this.namespace.getDatabase().getSearchPoolManager().removeSequenceFromSearchPool(this);
        this.namespace.getSequenceGroup().removeFromGroup(oid);
    }

    /**
     * Sets the owner.
     *
     * @param owner the new owner
     */
    void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets the sequence owner.
     *
     * @return the sequence owner
     */
    public String getSequenceOwner() {
        return owner;
    }

    /**
     * Gets the namespace.
     *
     * @return the namespace
     */
    public Namespace getNamespace() {
        return namespace;
    }

    /**
     * Gets the sequence name.
     *
     * @return the sequence name
     */
    public String getSequenceName() {
        return sequenceName;
    }

    /**
     * Sets the sequence name.
     *
     * @param sequenceName the new sequence name
     */
    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

    /**
     * Checks if is cycle.
     *
     * @return true, if is cycle
     */
    public boolean isCycle() {
        return cycle;
    }

    /**
     * Sets the cycle.
     *
     * @param cycle the new cycle
     */
    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    public String getTableName() {
        return seqTableName;
    }

    /**
     * Sets the table name.
     *
     * @param tableName the new table name
     */
    public void setTableName(String tableName) {
        this.seqTableName = tableName;
    }

    /**
     * Gets the column name.
     *
     * @return the column name
     */
    public String getColumnName() {
        return seqColumnName;
    }

    /**
     * Sets the column name.
     *
     * @param columnName the new column name
     */
    public void setColumnName(String columnName) {
        this.seqColumnName = columnName;
    }

    /**
     * Gets the schema name.
     *
     * @return the schema name
     */
    public String getSchemaName() {
        return seqSchemaName;
    }

    /**
     * Sets the schema name.
     *
     * @param schemaName the new schema name
     */
    public void setSchemaName(String schemaName) {
        this.seqSchemaName = schemaName;
    }

    /**
     * Gets the min value.
     *
     * @return the min value
     */
    public String getMinValue() {
        return minValue;
    }

    /**
     * Sets the min value.
     *
     * @param minValue the new min value
     */
    public void setMinValue(String minValue) {
        this.minValue = minValue;
    }

    /**
     * Gets the max value.
     *
     * @return the max value
     */
    public String getMaxValue() {
        return maxValue;
    }

    /**
     * Sets the max value.
     *
     * @param maxValue the new max value
     */
    public void setMaxValue(String maxValue) {
        this.maxValue = maxValue;
    }

    /**
     * Gets the start value.
     *
     * @return the start value
     */
    public String getStartValue() {
        return startValue;
    }

    /**
     * Sets the start value.
     *
     * @param startValue the new start value
     */
    public void setStartValue(String startValue) {
        this.startValue = startValue;
    }

    /**
     * Gets the cache size.
     *
     * @return the cache size
     */
    public String getCacheSize() {
        return cache;
    }

    /**
     * Sets the cache.
     *
     * @param cache the new cache
     */
    public void setCache(String cache) {
        this.cache = cache;
    }

    /**
     * Gets the increment by.
     *
     * @return the increment by
     */
    public String getIncrementBy() {
        return incrementBy;
    }

    /**
     * Sets the increment by.
     *
     * @param incrementBy the new increment by
     */
    public void setIncrementBy(String incrementBy) {
        this.incrementBy = incrementBy;
    }

    /**
     * Gets the search name.
     *
     * @return getSearchName
     */
    public String getSearchName() {
        return getName() + " - " + getNamespace().getName() + " - " + getTypeLabel();
    }

    @Override
    public Object getParent() {
        return this.namespace;
    }

    @Override
    public Database getDatabase() {
        return this.namespace.getDatabase();
    }

    /**
     * Gets the drop query for OB.
     *
     * @param isAppendCascade the is append cascade
     * @return the drop query for OB
     */
    private String getDropQueryForOB(boolean isAppendCascade) {
        StringBuilder query = new StringBuilder(DROP_QUERY);
        query.append(getNamespace().getQualifiedObjectName()).append('.').append(getQualifiedObjectName());

        if (isAppendCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }

    /**
     * Gets the drop query.
     *
     * @param isAppendCascade the is append cascade
     * @return the drop query
     */
    public String getDropQuery(boolean isAppendCascade) {
        StringBuilder query = new StringBuilder(DROP_QUERY_IF_EXISTS);
        query.append(getNamespace().getQualifiedObjectName()).append('.').append(getQualifiedObjectName());

        if (isAppendCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }

    @Override
    public void refreshSequence(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        SequenceMetadataUtil.refresh(getOid(), getNamespace().getDatabase(), this);

    }

    @Override
    public INamespace getSeqNameSpace() {
        return getNamespace();
    }

    @Override
    public boolean isExportAllowed(EXPORTTYPE exportType) {
        if (exportType == EXPORTTYPE.SQL_DDL || exportType == EXPORTTYPE.SQL_DDL_DATA) {
            return true;
        }
        return false;
    }

    @Override
    public String getDisplayName() {
        return getNamespace().getDisplayName() + '.' + super.getDisplayName();
    }

    /**
     * isTableDropped
     * 
     * @return boolean
     */
    public boolean isTableDropped() {
        Namespace ns = getNamespace();
        if (null == ns) {
            return true;
        }
        SequenceObjectGroup group = ns.getSequenceGroup();
        return null == group || null == group.getObjectById(getOid());
    }
    
    /**
     * getDDL get sequence DDL
     * 
     * @param database db obj
     * @param conn connection
     * @return string Ddl
     * @throws DatabaseOperationException exception
     */
    public String getDDL(Database database, DBConnection conn) throws DatabaseOperationException {
        String fetchDDLQueryPrefix = "SELECT sequence_name,start_value, increment_by,"
                + " CASE WHEN increment_by > 0 AND max_value = %d THEN NULL      "
                + "WHEN increment_by < 0 AND max_value = -1 THEN NULL  ELSE max_value END AS max_value,"
                + " CASE WHEN increment_by > 0 AND min_value = 1 THEN NULL WHEN increment_by < 0 AND min_value = %s "
                + "THEN NULL  ELSE min_value END AS min_value, cache_value, is_cycled FROM %s;";

        ExecTimer timer = new ExecTimer("fetch DDL from server");
        String seqDDL = "";
        ResultSet rs = null;
        try {
            timer.start();
            rs = conn.execSelectAndReturnRs(String.format(Locale.ENGLISH, fetchDDLQueryPrefix, Integer.MAX_VALUE,
                    Integer.MIN_VALUE, this.getDisplayName()));
            timer.stopAndLogNoException();
            while (rs.next()) {
                this.setSequenceName(rs.getString("sequence_name"));
                this.setStartValue(rs.getString("start_value"));
                this.setIncrementBy(rs.getString("increment_by"));
                this.setMaxValue(rs.getString("max_value"));
                this.setMinValue(rs.getString("min_value"));
                this.setCache(rs.getString("cache_value"));
                this.setCycle(rs.getBoolean("is_cycled"));
            }
            seqDDL = composeQuery(ServerObject.getQualifiedObjectName(this.getSequenceName()));
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

        return seqDDL;
    }

    /**
     * getSequenceQwnedByDDL seq qwned DDl
     * 
     * @param conn connection
     * @return string query
     * @throws DatabaseOperationException exception
     */
    public String getSequenceQwnedByDDL(DBConnection conn) throws DatabaseOperationException {
        String qury = String.format(Locale.ENGLISH,
                "select d.refobjid as tableid,d.refobjsubid as clmidx FROM pg_class c "
                        + " LEFT JOIN pg_depend d ON (c.relkind = 'S' AND "
                        + "d.classid = c.tableoid AND d.objid = c.oid AND d.objsubid = 0 AND "
                        + "d.refclassid = c.tableoid AND d.deptype = 'a') where c.oid=%d",
                this.oid);
        StringBuilder seqDDL = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        ResultSet rs = null;
        long tableOid;
        int clmIndex;
        try {
            rs = conn.execSelectAndReturnRs(qury);
            while (rs.next()) {
                tableOid = rs.getInt("tableid");
                clmIndex = rs.getInt("clmidx");
                seqDDL.append(getSeqOwnQuery(conn, tableOid, clmIndex));
            }
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("Error while fetching column details for sequence", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Error while fetching column details for sequence", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.debug("fetch DDL done");
        }
        return seqDDL.toString();
    }

    private String getSeqOwnQuery(DBConnection conn, long tableOid, int clmIndex) throws DatabaseOperationException {
        String query = String.format(Locale.ENGLISH,
                "select relname, attname  from pg_class as c"
                        + "  LEFT JOIN pg_catalog.pg_attribute as att ON (c.oid = att.attrelid)"
                        + " where attrelid = %d  and attnum = %d",
                tableOid, clmIndex);
        ResultSet rs = null;
        String queryDDL = "";
        try {
            rs = conn.execSelectAndReturnRs(query);
            while (rs.next()) {
                String tableName = rs.getString("relname");
                String clmName = rs.getString("attname");
                if (tableName != null || clmName != null) {
                    queryDDL = String.format(Locale.ENGLISH, "ALTER SEQUENCE %s OWNED BY %s.%s ;",
                            ServerObject.getQualifiedObjectName(this.getName()),
                            ServerObject.getQualifiedObjectName(tableName),
                            ServerObject.getQualifiedObjectName(clmName));
                }
            }
        } catch (DatabaseCriticalException exception) {
            MPPDBIDELoggerUtility.error("Error while fetching owner details for sequence", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } catch (SQLException exception) {
            MPPDBIDELoggerUtility.error("Error while fetching owner details for sequence", exception);
            throw new DatabaseOperationException(IMessagesConstants.CONNECTION_ERR);
        } finally {
            conn.closeResultSet(rs);
            MPPDBIDELoggerUtility.debug("fetch DDL done");
        }
        return queryDDL;
    }

    /**
     * Compose query.
     * 
     * @param objectName the object name
     * @return the string
     */
    public String composeQuery(String objectName) {
        StringBuffer queryBuff = new StringBuffer(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        queryBuff.append(" CREATE ");

        queryBuff.append(" SEQUENCE ");

        queryBuff.append(objectName);
        if (null != this.getStartValue()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" START  WITH  ");
            queryBuff.append(this.getStartValue());
        }
        if (null != this.getIncrementBy()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" INCREMENT  BY  ");
            queryBuff.append(this.getIncrementBy());
        }
        if (null != this.getMinValue()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" MINVALUE ");
            queryBuff.append(this.getMinValue());
        } else {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" NO MINVALUE  ");
        }
        if (null != this.getMaxValue()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" MAXVALUE ");
            queryBuff.append(this.getMaxValue());
        }

        if (null != this.getCacheSize()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" CACHE ");
            queryBuff.append(this.getCacheSize());
        }
        if (this.isCycle()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" CYCLE ");
        }

        if (null != this.getTableName() && !this.getTableName().isEmpty()) {
            queryBuff.append(MPPDBIDEConstants.LINE_SEPARATOR);
            queryBuff.append(" OWNED BY ");
            queryBuff.append(ServerObject.getQualifiedObjectName(this.getSchemaName())).append(".");
            queryBuff.append(ServerObject.getQualifiedObjectName(this.getTableName())).append(".");
            if (null != this.getColumnName() && !this.getColumnName().isEmpty()) {
                queryBuff.append(ServerObject.getQualifiedObjectName(this.getColumnName()));
            }
        }

        queryBuff.append(";");
        return queryBuff.toString();
    }
}
