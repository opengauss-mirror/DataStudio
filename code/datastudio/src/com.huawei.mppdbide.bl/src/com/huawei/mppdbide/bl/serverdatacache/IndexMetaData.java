/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.bl.serverdatacache.groups.OLAPObjectList;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class IndexMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject {

    private TableMetaData table;
    private boolean isUnique;
    private boolean isPrimary;
    private boolean isExclusion;
    private boolean isImmediate;
    private boolean isLastClustered;
    private boolean checkxmin;
    private boolean isReady;
    private ArrayList<IndexedColumnExpr> colExpr;
    private Namespace namespace;

    private AccessMethod accessMethod;
    private long accessMethId;
    private Tablespace tablespace;
    private String tblspce;
    private int fillFactor;
    private String whereExpr;
    private String indexdeff;
    private Comparator<IndexedColumnExpr> comparator;
    private boolean isLoaded;
    private static final String DROP_QUERY = "DROP INDEX IF EXISTS ";
    private IndexManager indexManager;

    /**
     * Instantiates a new index meta data.
     *
     * @param oid the oid
     * @param name the name
     */
    public IndexMetaData(long oid, String name) {
        super(oid, name, OBJECTTYPE.INDEX_METADATA, false);
        colExpr = new ArrayList<IndexedColumnExpr>(4);
        comparator = new IndexedColumnComparator();
        indexManager = new IndexManager();
        this.isLoaded = false;
    }

    /**
     * Instantiates a new index meta data.
     *
     * @param name the name
     */
    public IndexMetaData(String name) {
        super(-1, name, OBJECTTYPE.INDEX_METADATA, false);
        colExpr = new ArrayList<IndexedColumnExpr>(4);
        comparator = new IndexedColumnComparator();
        this.isLoaded = false;
        indexManager = new IndexManager();
    }

    /**
     * Gets the table.
     *
     * @return the table
     */
    public TableMetaData getTable() {
        return table;
    }

    /**
     * Sets the table.
     *
     * @param table the new table
     */
    public void setTable(TableMetaData table) {
        this.table = table;
    }

    /**
     * Checks if is unique.
     *
     * @return true, if is unique
     */
    public boolean isUnique() {
        return isUnique;
    }

    /**
     * Sets the unique.
     *
     * @param isUniq the new unique
     */
    public void setUnique(boolean isUniq) {
        this.isUnique = isUniq;
    }

    /**
     * Checks if is primary.
     *
     * @return true, if is primary
     */
    public boolean isPrimary() {
        return isPrimary;
    }

    /**
     * Sets the primary.
     *
     * @param isPrmry the new primary
     */
    public void setPrimary(boolean isPrmry) {
        this.isPrimary = isPrmry;
    }

    /**
     * Checks if is exclusion.
     *
     * @return true, if is exclusion
     */
    public boolean isExclusion() {
        return isExclusion;
    }

    /**
     * Sets the exclusion.
     *
     * @param isExclusn the new exclusion
     */
    public void setExclusion(boolean isExclusn) {
        this.isExclusion = isExclusn;
    }

    /**
     * Checks if is immediate.
     *
     * @return true, if is immediate
     */
    public boolean isImmediate() {
        return isImmediate;
    }

    /**
     * Sets the immediate.
     *
     * @param isImmediat the new immediate
     */
    public void setImmediate(boolean isImmediat) {
        this.isImmediate = isImmediat;
    }

    /**
     * Checks if is last clustered.
     *
     * @return true, if is last clustered
     */
    public boolean isLastClustered() {
        return isLastClustered;
    }

    /**
     * Sets the last clustered.
     *
     * @param isLastClusterd the new last clustered
     */
    public void setLastClustered(boolean isLastClusterd) {
        this.isLastClustered = isLastClusterd;
    }

    /**
     * Checks if is checkxmin.
     *
     * @return true, if is checkxmin
     */
    public boolean isCheckxmin() {
        return checkxmin;
    }

    /**
     * Sets the checkxmin.
     *
     * @param checkxmin the new checkxmin
     */
    public void setCheckxmin(boolean checkxmin) {
        this.checkxmin = checkxmin;
    }

    /**
     * Checks if is ready.
     *
     * @return true, if is ready
     */
    public boolean isReady() {
        return isReady;
    }

    /**
     * Sets the ready.
     *
     * @param isRdy the new ready
     */
    public void setReady(boolean isRdy) {
        this.isReady = isRdy;
    }

    /**
     * Gets the indexed columns.
     *
     * @return the indexed columns
     */
    public ArrayList<IndexedColumnExpr> getIndexedColumns() {
        return colExpr;
    }

    /**
     * Sets the indexed columns.
     *
     * @param colExprs the new indexed columns
     */
    public void setIndexedColumns(ArrayList<IndexedColumnExpr> colExprs) {
        this.colExpr = colExprs;
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
    }

    /**
     * Adds the index column.
     *
     * @param newColExpr the new col expr
     */
    private void addIndexColumn(IndexedColumnExpr newColExpr) {
        int insertionPoint = Collections.binarySearch(this.colExpr, newColExpr, comparator);
        if (insertionPoint < 0) {
            insertionPoint = -(insertionPoint + 1);
        }

        newColExpr.setPosition(insertionPoint);
        this.colExpr.add(insertionPoint, newColExpr);
    }

    /**
     * Adds the column.
     *
     * @param col the col
     */
    public void addColumn(ColumnMetaData col) {
        IndexedColumnExpr columnExpr = new IndexedColumnExpr(IndexedColumnType.COLUMN);
        columnExpr.setCol(col);
        addIndexColumn(columnExpr);
    }

    /**
     * Adds the expr.
     *
     * @param expr the expr
     */
    public void addExpr(String expr) {
        IndexedColumnExpr columnExpr = new IndexedColumnExpr(IndexedColumnType.EXPRESSION);
        columnExpr.setExpr(expr);
        addIndexColumn(columnExpr);
    }

    /**
     * Gets the indexdeff.
     *
     * @return the indexdeff
     */
    public String getIndexdeff() {
        return indexdeff;
    }

    /**
     * Sets the indexdeff.
     *
     * @param indexdeff the new indexdeff
     */
    public void setIndexdeff(String indexdeff) {
        this.indexdeff = indexdeff;
    }

    /**
     * Gets the access method.
     *
     * @return the access method
     */
    public AccessMethod getAccessMethod() {
        return this.accessMethod;
    }

    /**
     * Sets the access method.
     *
     * @param am the new access method
     */
    public void setAccessMethod(AccessMethod am) {
        this.accessMethod = am;
    }

    /**
     * Gets the tablespace.
     *
     * @return the tablespace
     */
    public Tablespace getTablespace() {
        return tablespace;
    }

    /**
     * Sets the tablespace.
     *
     * @param tablespace the new tablespace
     */
    public void setTablespace(Tablespace tablespace) {
        this.tablespace = tablespace;
    }

    /**
     * Gets the fill factor.
     *
     * @return the fill factor
     */
    public int getFillFactor() {
        return this.fillFactor;
    }

    /**
     * Sets the index fill factor.
     *
     * @param fillFact the new index fill factor
     */
    public void setIndexFillFactor(int fillFact) {
        this.fillFactor = fillFact;
    }

    /**
     * Gets the where expr.
     *
     * @return the where expr
     */
    public String getWhereExpr() {
        return this.whereExpr;
    }

    /**
     * Sets the where expr.
     *
     * @param whereExpr the new where expr
     */
    public void setWhereExpr(String whereExpr) {
        this.whereExpr = whereExpr;
    }

    /**
     * Form create query.
     *
     * @param isCreateTable the is create table
     * @return the string
     */
    public String formCreateQuery(boolean isCreateTable) {
        StringBuilder qry = new StringBuilder("CREATE ");

        if (isUnique()) {
            qry.append("UNIQUE ");
        }
        appendSchemaOrTableNameForIndex(isCreateTable, qry);

        appendAccessMethod(qry);

        qry.append("(");

        appendIndexColumn(qry);

        qry.append(") ");

        if (table instanceof PartitionTable) {
            qry.append("LOCAL ");
        }

        appendFillFactor(qry);

        appendTablespace(qry);

        appendWhrExpression(qry);

        qry.append(";");
        return qry.toString();

    }

    /**
     * Append whr expression.
     *
     * @param qry the qry
     */
    private void appendWhrExpression(StringBuilder qry) {
        if (null != this.whereExpr && !"".equals(this.whereExpr)) {
            qry.append("WHERE ").append(this.whereExpr);
        }
    }

    /**
     * Append tablespace.
     *
     * @param qry the qry
     */
    private void appendTablespace(StringBuilder qry) {
        if (null != this.tablespace) {
            qry.append("TABLESPACE ").append(this.tablespace.getQualifiedObjectName()).append(" ");
        }
    }

    /**
     * Append fill factor.
     *
     * @param qry the qry
     */
    private void appendFillFactor(StringBuilder qry) {
        if (this.table.isRowTableOrientation()) {
            // if none of the access method selected or if access method other
            // than gin and psort is selected
            if ((getAccessMethod() == null
                    || (!getAccessMethod().getName().equals("gin") && !getAccessMethod().getName().equals("psort")))) {
                if (this.fillFactor > 0) {
                    qry.append("WITH (FILLFACTOR=").append(this.fillFactor).append(") ");
                }
            }
        }
    }

    /**
     * Append index column.
     *
     * @param qry the qry
     */
    private void appendIndexColumn(StringBuilder qry) {
        Iterator<IndexedColumnExpr> colExprItr = this.colExpr.iterator();
        boolean hasNext = colExprItr.hasNext();
        boolean isFirst = true;
        String columnExpr = null;
        while (hasNext) {
            columnExpr = colExprItr.next().toString();
            if (!isFirst) {
                qry.append(", ");
            }
            appendColumnName(qry, columnExpr);
            hasNext = colExprItr.hasNext();
            isFirst = false;
        }
    }

    private void appendColumnName(StringBuilder qry, String columnExpr) {
        if (this.isQSimpleObjectName(columnExpr)) {
            qry.append(ServerObject.getQualifiedObjectName(columnExpr));
        } else {
            qry.append(columnExpr);
        }
    }

    private boolean isQSimpleObjectName(String columnExpr) {
        return !ServerObject.isQualifiedSimpleObjectName(columnExpr) && !columnExpr.contains("(");
    }

    /**
     * Append access method.
     *
     * @param qry the qry
     */
    private void appendAccessMethod(StringBuilder qry) {
        if (null != this.accessMethod) {
            qry.append("USING ").append(getAccessMethod().getName()).append(" ");
        }
    }

    /**
     * Append schema or table name for index.
     *
     * @param isCreateTable the is create table
     * @param qry the qry
     */
    private void appendSchemaOrTableNameForIndex(boolean isCreateTable, StringBuilder qry) {
        if (!isCreateTable) {
            qry.append("INDEX ").append(ServerObject.getQualifiedObjectName(getName())).append(" ON ")
                    .append(table.getDisplayName()).append(" ");
        } else {
            qry.append("INDEX ").append(ServerObject.getQualifiedObjectName(getName())).append(" ON ")
                    .append(getNamespace().getQualifiedObjectName()).append(".")
                    .append(ServerObject.getQualifiedObjectName(table.getName())).append(" ");
        }
    }

    /**
     * Form drop query.
     *
     * @param isCreateTable the is create table
     * @return the string
     */
    public String formDropQuery(boolean isCreateTable) {
        return DROP_QUERY + this.getDisplayName() + ';';
    }

    /**
     * Drop.
     *
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void drop(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
        indexManager.drop(conn);
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
        indexManager.rename(newName, conn);
    }

    /**
     * Change tablespace.
     *
     * @param tblspaceName the tblspace name
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void changeTablespace(String tblspaceName, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        indexManager.changeTablespace(tblspaceName, conn);
    }

    /**
     * Change fill factor.
     *
     * @param fillFact the fill fact
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void changeFillFactor(int fillFact, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        indexManager.changeFillFactor(fillFact, conn);
    }

    /**
     * Gets the columns string.
     *
     * @return the columns string
     */
    public String getColumnsString() {
        return indexManager.getColumnsString();
    }

    /**
     * Checks if is loaded.
     *
     * @return true, if is loaded
     */
    public boolean isLoaded() {
        return isLoaded;
    }

    /**
     * Sets the loaded.
     *
     * @param isLoad the new loaded
     */
    public void setLoaded(boolean isLoad) {
        this.isLoaded = isLoad;
    }

    /**
     * Sets the tablespace.
     *
     * @param tablespace the new tablespace
     */
    public void setTablespace(String tablespace) {
        if (null != tablespace) {
            this.tblspce = tablespace;
        } else {
            this.tblspce = getDatabase().getDBDefaultTblSpc();
        }
    }

    /**
     * Gets the tablespc.
     *
     * @return the tablespc
     */
    public String getTablespc() {
        return this.tblspce;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return this.getTable().getDatabase();
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
     * Gets the parent.
     *
     * @return the parent
     */
    public TableMetaData getParent() {
        return table;

    }

    /**
     * Gets the where expresionfor property.
     *
     * @param conn the conn
     * @return the where expresionfor property
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public String getwhereExpresionforProperty(DBConnection conn)
            throws DatabaseCriticalException, DatabaseOperationException {
        return indexManager.getwhereExpresionforProperty(conn);
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

    @Override
    public boolean isDropAllowed() {
        if (getParent() instanceof ForeignTable) {
            return false;
        }

        return true;
    }
    
    /**
     * get Access Method Id
     * 
     * @return accessMethId the access Method Id
     */
    public long getAccessMethId() {
        return accessMethId;
    }
    
    /**
     * set Access Method Id
     * 
     * @param accessMethId the access Meth Id
     */
    public void setAccessMethId(long accessMethId) {
        this.accessMethId = accessMethId;
    }

    private class IndexManager {
        /**
         * getwhereExpresionforProperty
         * 
         * @param conn connection
         * @return string query
         * @throws DatabaseCriticalException exception
         * @throws DatabaseOperationException exception
         */
        public String getwhereExpresionforProperty(DBConnection conn)
                throws DatabaseCriticalException, DatabaseOperationException {
            String query = String.format(Locale.ENGLISH,
                    "select pg_get_expr(indpred,%d) from pg_index where indrelid = %d and indexrelid = %d;",
                    getParent().getOid(), getParent().getOid(), getOid());
            return conn.execSelectAndGetFirstVal(query);

        }

        /**
         * Gets the columns string.
         *
         * @return the columns string
         */
        public String getColumnsString() {
            StringBuilder colNames = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            boolean isFirst = true;

            ArrayList<IndexedColumnExpr> columns = getIndexedColumns();
            if (null != columns) {
                int size = columns.size();
                IndexedColumnExpr column = null;
                for (int clmIndex = 0; clmIndex < size; clmIndex++) {
                    column = columns.get(clmIndex);
                    isFirst = addColumnNames(colNames, isFirst, column);
                }
            }

            return colNames.toString();
        }

        private boolean addColumnNames(StringBuilder colNames, boolean isFirstParam, IndexedColumnExpr column) {
            boolean isFirst = isFirstParam;
            if (validateColumn(column)) {
                if (!isFirst) {
                    colNames.append(",");
                } else {
                    isFirst = false;
                }

                colNames.append(ServerObject.getQualifiedObjectName(column.getCol().getName()));
            }
            return isFirst;
        }

        private boolean validateColumn(IndexedColumnExpr column) {
            return null != column && column.validateColumn();
        }

        /**
         * Change fill factor.
         *
         * @param fillFact the fill fact
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void changeFillFactor(int fillFact, DBConnection conn)
                throws DatabaseOperationException, DatabaseCriticalException {
            String qry = String.format(Locale.ENGLISH, "ALTER INDEX %s SET (fillfactor = %d);", getDisplayName(),
                    fillFact);
            conn.execNonSelectForTimeout(qry);
            getTable().refresh(conn);
        }

        /**
         * Change tablespace.
         *
         * @param tblspaceName the tblspace name
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void changeTablespace(String tblspaceName, DBConnection conn)
                throws DatabaseOperationException, DatabaseCriticalException {
            String qualifiedTblspaceName = ServerObject.getQualifiedObjectName(tblspaceName);
            String qry = String.format(Locale.ENGLISH, "ALTER INDEX %s SET TABLESPACE %s;", getDisplayName(),
                    qualifiedTblspaceName);
            conn.execNonSelectForTimeout(qry);
            getTable().refresh(conn);
        }

        /**
         * Rename.
         *
         * @param newName the new name
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void rename(String newName, DBConnection conn)
                throws DatabaseOperationException, DatabaseCriticalException {
            String qry = String.format(Locale.ENGLISH, "ALTER INDEX %s RENAME TO %s ;", getDisplayName(),
                    ServerObject.getQualifiedObjectName(newName));
            conn.execNonSelectForTimeout(qry);
            setName(newName);
            getTable().refresh(conn);
        }

        /**
         * Drop.
         *
         * @param conn the conn
         * @throws DatabaseOperationException the database operation exception
         * @throws DatabaseCriticalException the database critical exception
         */
        public void drop(DBConnection conn) throws DatabaseOperationException, DatabaseCriticalException {
            String qry = String.format(Locale.ENGLISH, "drop index %s;", getDisplayName());
            conn.execNonSelectForTimeout(qry);
            getTable().refresh(conn);
        }
    }
}
