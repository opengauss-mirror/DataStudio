/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class PartitionMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject {

    private PartitionTable pTable;
    private String partitionName;
    private String partitionValue;
    private String partitionType = "By Range";
    private Tablespace ts;
    private boolean isRollBack = true;
    private ColumnMetaData columnMetadata;

    /**
     * Gets the partition name.
     *
     * @return the partition name
     */
    public String getPartitionName() {
        return partitionName;
    }

    /**
     * Sets the partition name.
     *
     * @param partitionName the new partition name
     */
    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    /**
     * Gets the partition value.
     *
     * @return the partition value
     */
    public String getPartitionValue() {
        return partitionValue;
    }

    /**
     * Sets the partition value.
     *
     * @param partitionValue the new partition value
     */
    public void setPartitionValue(String partitionValue) {
        this.partitionValue = partitionValue;
    }

    /**
     * Gets the partition values as list.
     *
     * @return the partition values as list
     */
    public List<String> getPartitionValuesAsList() {
        return Arrays.asList(partitionValue.split(",", -1));
    }

    /**
     * Sets the partition type.
     *
     * @param partitionType the new partition type
     */
    public void setPartitionType(String partitionType) {
        this.partitionType = partitionType;
    }

    /**
     * Gets the partition type.
     *
     * @return the partition type
     */
    public String getPartitionType() {
        return this.partitionType;
    }

    /**
     * Gets the ts.
     *
     * @return the ts
     */
    public Tablespace getTs() {
        return ts;
    }

    /**
     * Gets the tablespace name.
     *
     * @return the tablespace name
     */
    public String getTablespaceName() {
        return ts.getName();
    }

    /**
     * Checks if is tablespace null.
     *
     * @return true, if is tablespace null
     */
    public boolean isTablespaceNull() {
        return ts == null;
    }

    /**
     * Sets the ts.
     *
     * @param ts the new ts
     */
    public void setTs(Tablespace ts) {
        this.ts = ts;
    }

    /**
     * Gets the column metadata.
     *
     * @return the column metadata
     */
    public ColumnMetaData getColumnMetadata() {
        return columnMetadata;
    }

    /**
     * Sets the column metadata.
     *
     * @param columnMetadata the new column metadata
     */
    public void setColumnMetadata(ColumnMetaData columnMetadata) {
        this.columnMetadata = columnMetadata;
    }

    /**
     * Instantiates a new partition meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param partitionTable the partition table
     */
    public PartitionMetaData(long oid, String name, PartitionTable partitionTable) {
        super(oid, name, OBJECTTYPE.PARTITION_METADATA, false);
        this.pTable = partitionTable;
    }

    /**
     * Instantiates a new partition meta data.
     *
     * @param name the name
     */
    public PartitionMetaData(String name) {
        super(-1, name, OBJECTTYPE.PARTITION_METADATA, false);
    }

    /**
     * Convert to partition meta data.
     *
     * @param rs the rs
     * @param partitionTable the partition table
     * @param database the database
     * @return the partition meta data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public static PartitionMetaData convertToPartitionMetaData(ResultSet rs, PartitionTable partitionTable,
            Database database) throws DatabaseCriticalException, DatabaseOperationException {

        PartitionMetaData part = null;
        try {
            part = new PartitionMetaData(rs.getLong("partition_id"), rs.getString("partition_name"), partitionTable);
        } catch (SQLException exp) {
            GaussUtils.handleCriticalException(exp);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exp);
        }
        return part;
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public PartitionTable getParent() {
        return this.pTable;
    }

    /**
     * Sets the parent.
     *
     * @param partTable the new parent
     */
    public void setParent(PartitionTable partTable) {
        this.pTable = partTable;
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

        String qry = String.format(Locale.ENGLISH, "ALTER TABLE %s RENAME PARTITION %s TO %s ;",
                this.getParent().getQualifiedName(), getQualifiedObjectName(),
                ServerObject.getQualifiedObjectName(newName));
        dbConnection.execNonSelect(qry);
        getParent().refreshPartition(dbConnection);

    }

    /**
     * Exec drop.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execDrop(DBConnection dbConnection) throws DatabaseOperationException, DatabaseCriticalException {
        String qry = String.format(Locale.ENGLISH, "ALTER TABLE %s DROP PARTITION %s",
                this.getParent().getDisplayName(), getDisplayName());
        dbConnection.execNonSelect(qry);
        this.pTable.getNamespace().refreshTable(this.pTable, dbConnection, false);
        if (isRollBack) {
            isRollBack = false;
        }
    }

    /**
     * Form create partitions qry.
     *
     * @param list the list
     * @return the string
     */
    public String formCreatePartitionsQry(List<PartitionColumnExpr> list) {
        StringBuilder sbPartition = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        sbPartition.append("partition ");
        sbPartition.append(ServerObject.getQualifiedObjectName(getPartitionName()));
        sbPartition.append(" values less than ");
        sbPartition.append("(");
        sbPartition.append(composePartitionValuePart(list));
        sbPartition.append(")");
        if (getTs() != null) {
            sbPartition.append(" tablespace ");
            sbPartition.append(getTs().getDisplayName());
        }
        return sbPartition.toString();
    }

    /**
     * Compose partition value part.
     *
     * @param list the list
     * @return the string
     */
    private String composePartitionValuePart(List<PartitionColumnExpr> list) {
        String value = getPartitionValue();
        List<String> splittedColNames = Arrays.asList(value.split(",", -1));
        StringBuilder strbulder = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        if (!splittedColNames.isEmpty()) {
            int colIdx = 0;
            for (Iterator iterator = splittedColNames.iterator(); iterator.hasNext();) {
                String colValue = (String) iterator.next();

                if (isNumberType(list.get(colIdx).getCol().getDataType().getName(), colValue)) {
                    strbulder.append(colValue);
                } else {
                    strbulder.append(ServerObject.isQualifiedPartitionValue(colValue));
                }

                strbulder.append(',');
                colIdx++;
            }
        }

        strbulder.deleteCharAt(strbulder.lastIndexOf(","));
        return strbulder.toString();
    }

    /**
     * Checks if is number type.
     *
     * @param datatypeName the datatype name
     * @param value the value
     * @return true, if is number type
     */
    public static boolean isNumberType(String datatypeName, String value) {
        String[] numberDatatypes = {"int4", "integer", "int2", "smallint", "int8", "bigint", "numeric", "decimal",
            "float8", "double precision", "float4", "real"};
        String[] dateTimeDatatypes = {"date", "interval", "time", "time without time zone", "timetz",
            "time with time zone", "timestamptz", "timestamp with time zone"};
        Arrays.sort(numberDatatypes);
        Arrays.sort(dateTimeDatatypes);

        if (Arrays.binarySearch(numberDatatypes, datatypeName) >= 0) {
            return true;
        }

        if (Arrays.binarySearch(dateTimeDatatypes, datatypeName) >= 0 && "MAXVALUE".equalsIgnoreCase(value)) {
            return true;
        }

        return false;
    }

    @Override
    public Database getDatabase() {
        return pTable.getDatabase();
    }

    @Override
    public String getObjectFullName() {
        return getParent().getDisplayName() + "." + getDisplayName();
    }

    @Override
    public String getDropQuery(boolean isCascade) {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        query.append("ALTER TABLE IF EXISTS ").append(this.getParent().getDisplayName()).append(" DROP PARTITION ")
                .append(getDisplayName());

        // No cascade support

        return query.toString();
    }
}
