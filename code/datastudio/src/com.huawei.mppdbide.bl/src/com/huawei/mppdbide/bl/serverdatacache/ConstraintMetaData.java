/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConstraintMetaData.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */

public class ConstraintMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject {
    /**
     * this is the constraint type
     */
    protected ConstraintType contype;

    /*
     * c is check constraint f is foreign key constraint p is primary key
     * constraint u is unique constraint x is exclusion constraint
     */
    private ConstraintQueryBuilder queryBuilder = null;
    private String expr;
    private String consDef;
    private TableMetaData table;

    /*
     * Following are only valid for UNIQUE, PRIMARY KEY, FOREIGN KEY, EXCLUDE
     * constraints
     */
    private boolean condeferrable;
    private boolean condeferred;

    /*
     * only valid while creating / validating constraint
     */
    private boolean isValidated;

    /*
     * Used only for creating index options
     */
    private String tableSpace;

    /*
     * used only for creating new constraint
     */
    private String columnList;

    /* valid only for foreign key constraint */
    private TableMetaData refernceTable;
    private IndexMetaData referenceIndex;

    /**
     * The on delete action.a = no action, r = restrict, c = cascade, n = set
     * null, d = set default
     */
    private ForeignKeyActionType onDeleteAction;
    private ForeignKeyActionType onUpdateAction;
    private ForeignKeyMatchType fkMatchType;

    private Namespace namespace;
    private int fillfactor;
    private boolean isLoaded;

    /**
     * Instantiates a new constraint meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param contype the contype
     */
    public ConstraintMetaData(long oid, String name, ConstraintType contype) {
        super(oid, name, OBJECTTYPE.CONSTRAINT, false);
        this.contype = contype;
        this.fillfactor = 100;
        this.isLoaded = false;
        this.queryBuilder = new ConstraintQueryBuilder();
    }

    /**
     * Checks if is convalidated.
     *
     * @return true, if is convalidated
     */
    public boolean isConvalidated() {
        return isValidated;
    }

    /**
     * Sets the validated.
     *
     * @param convalidated the new validated
     */
    public void setValidated(boolean convalidated) {
        this.isValidated = convalidated;
    }

    /**
     * Sets the refernce table.
     *
     * @param refernceTable the new refernce table
     */
    public void setRefernceTable(TableMetaData refernceTable) {
        this.refernceTable = refernceTable;
    }

    /**
     * Gets the cons def.
     *
     * @return the cons def
     */
    public String getConsDef() {
        return consDef;
    }

    /**
     * Sets the cons def.
     *
     * @param consDef the new cons def
     */
    public void setConsDef(String consDef) {
        this.consDef = consDef;
    }

    /**
     * Sets the reference index.
     *
     * @param referenceIndex the new reference index
     */
    public void setReferenceIndex(IndexMetaData referenceIndex) {
        this.referenceIndex = referenceIndex;
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
     * Gets the constraint fillfactor.
     *
     * @return the constraint fillfactor
     */
    public int getConstraintFillfactor() {
        return fillfactor;
    }

    /**
     * Sets the check constraint expr.
     *
     * @param expression the new check constraint expr
     */
    public void setCheckConstraintExpr(String expression) {
        this.expr = expression;
    }

    /**
     * Gets the check constraint expr.
     *
     * @return the check constraint expr
     */
    public String getCheckConstraintExpr() {
        return this.expr;
    }

    /**
     * Sets the pkey or ukey constraint.
     *
     * @param colList the col list
     * @param tbleSpace the tble space
     */
    public void setPkeyOrUkeyConstraint(String colList, String tbleSpace) {
        this.columnList = colList;
        this.tableSpace = tbleSpace;
    }

    /**
     * Gets the column list.
     *
     * @return the column list
     */
    public String getColumnList() {
        if (null == columnList) {
            return "";
        }
        return columnList;
    }

    /**
     * Gets the table space.
     *
     * @return the table space
     */
    public String getTableSpace() {
        return tableSpace;
    }

    /**
     * Sets the deffearable options.
     *
     * @param deferrable the deferrable
     * @param deferred the deferred
     */
    public void setDeffearableOptions(boolean deferrable, boolean deferred) {
        this.condeferrable = deferrable;
        this.condeferred = deferred;
    }

    /**
     * Sets the exclude where clause expr.
     *
     * @param excludeWhereClauseExpr the new exclude where clause expr
     */
    public void setExcludeWhereClauseExpr(String excludeWhereClauseExpr) {
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
     * Sets the namespace.
     *
     * @param namespace the new namespace
     */
    public void setNamespace(Namespace namespace) {
        this.namespace = namespace;
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
     * Form constraint string.
     *
     * @return the string
     */
    public String formConstraintString() {
        return queryBuilder.formConstraintString();
    }

    /**
     * 
     * Title: class
     * 
     * Description: The Class ConstraintQueryBuilder.
     * 
     * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
     *
     * @author pWX553609
     * @version [DataStudio 6.5.1, 17 May, 2019]
     * @since 17 May, 2019
     */
    private class ConstraintQueryBuilder {

        /**
         * Form constraint string.
         *
         * @return the string
         */
        public String formConstraintString() {
            StringBuilder query = new StringBuilder(512);
            appendQuery(query);
            switch (contype) {
                case CHECK_CONSTRSINT: {
                    return formCheckConstraint(query);
                }
                case PRIMARY_KEY_CONSTRSINT:
                case UNIQUE_KEY_CONSTRSINT:
                case PARTIAL_CLUSTER_KEY: {
                    return formPrimaryUniqueConstraint(query);
                }
                case FOREIGN_KEY_CONSTRSINT: {
                    return formForeignConstraint(query);
                }
                default: {
                    break;
                }
            }
            appendDeferedDeferable(query);
            return query.toString();
        }

        private void appendQuery(StringBuilder query) {
            if (!getName().isEmpty()) {
                query.append("CONSTRAINT ");
                query.append(ServerObject.getQualifiedObjectName(getName()));
                query.append(' ');
            }
        }

        /**
         * Form foreign constraint.Redundant code removed from inside the method
         *
         * @param query the query
         * @return the string
         */
        private String formForeignConstraint(StringBuilder query) {
            query.append("FOREIGN KEY (");
            if (columnList != null) {

                query.append(ServerObject.getQualifiedObjectName(columnList));

            }
            query.append(") REFERENCES ");
            query.append(ServerObject.getQualifiedObjectName(refernceTable.getNameSpaceName()));
            query.append('.');
            query.append(ServerObject.getQualifiedObjectName(refernceTable.getName()));

            if (null != referenceIndex && referenceIndex.getColumnsString().length() > 0) {
                query.append(" (");
                query.append(referenceIndex.getColumnsString());
                query.append(") ");
            }

            query.append(fkMatchType.getLabel());
            query.append(onDeleteAction.getDeleteLabel());
            query.append(onUpdateAction.getUpdateLabel());
            appendDeferedDeferable(query);

            return query.toString();
        }

        /**
         * Form primary unique constraint.
         *
         * @param query the query
         * @return the string
         */
        private String formPrimaryUniqueConstraint(StringBuilder query) {
            query.append(contype.strType);
            query.append(" (");
            if (columnList != null) {
                query.append(columnList);
            }
            query.append(')');
            if (90 != fillfactor) {
                query.append("WITH (fillfactor=");
                query.append(fillfactor);
                query.append(')');
            }

            if (null != tableSpace) {
                query.append("USING INDEX TABLESPACE ");
                query.append(ServerObject.getQualifiedObjectName(tableSpace));
            }

            appendDeferedDeferable(query);

            return query.toString();
        }

        /**
         * Form check constraint.
         *
         * @param query the query
         * @return the string
         */
        private String formCheckConstraint(StringBuilder query) {
            query.append(contype.strType);
            query.append(" (");
            if (expr != null) {
                query.append(expr.trim());

            }
            query.append(')');
            return query.toString();
        }

        /**
         * Append defered deferable.
         *
         * @param query the query
         */
        private void appendDeferedDeferable(StringBuilder query) {
            if (condeferred) {
                query.append(" DEFERRABLE INITIALLY DEFERRED");
            } else if (condeferrable) {
                query.append(" DEFERRABLE INITIALLY IMMEDIATE");
            }
        }
    }

    /**
     * Sets the rerence options.
     *
     * @param reftable the reftable
     * @param refindex the refindex
     */
    public void setRerenceOptions(TableMetaData reftable, IndexMetaData refindex) {
        this.refernceTable = reftable;
        this.referenceIndex = refindex;
    }

    /**
     * Sets the fk actions.
     *
     * @param fkMatchType2 the fk match type 2
     * @param onDeleteAction2 the on delete action 2
     * @param onUpdateAction2 the on update action 2
     */
    public void setFkActions(ForeignKeyMatchType fkMatchType2, ForeignKeyActionType onDeleteAction2,
            ForeignKeyActionType onUpdateAction2) {
        this.fkMatchType = fkMatchType2;
        this.onDeleteAction = onDeleteAction2;
        this.onUpdateAction = onUpdateAction2;
    }

    /**
     * Exec rename constraint.
     *
     * @param newName the new name
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execRenameConstraint(String newName, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        qry.append("ALTER TABLE ").append(table.getDisplayName()).append(" RENAME CONSTRAINT ")
                .append(super.getDisplayName()).append(" TO " + ServerObject.getQualifiedObjectName(newName));

        dbConnection.execNonSelectForTimeout(qry.toString());
        this.setName(newName);
    }

    /**
     * Exec validate constraint.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execValidateConstraint(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(table.getDisplayName()).append(" VALIDATE CONSTRAINT ")
                .append(super.getDisplayName());

        dbConnection.execNonSelect(qry.toString());
        this.setValidated(true);

    }

    /**
     * Exec deferable constraint.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execDeferableConstraint(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("SET CONSTRAINTS ").append(table.getDisplayName()).append("DEFERRED").append(';');

        dbConnection.execNonSelect(qry.toString());

        this.condeferrable = true;
        this.condeferred = true;
    }

    /**
     * Gets the constraint type.
     *
     * @return the constraint type
     */
    public ConstraintType getConstraintType() {
        return contype;
    }

    /**
     * Checks if is condeferred.
     *
     * @return true, if is condeferred
     */
    public boolean isCondeferred() {
        return condeferred;
    }

    /**
     * Checks if is deferable.
     *
     * @return true, if is deferable
     */
    public boolean isDeferable() {
        return condeferrable;
    }

    /**
     * Exec drop.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execDrop(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        query.append("ALTER TABLE ").append(table.getDisplayName()).append(" DROP CONSTRAINT ")
                .append(super.getDisplayName()).append(";");

        dbConnection.execNonSelectForTimeout(query.toString());
    }

    /**
     * Exec alter add constraint.
     *
     * @param tableMetaData the table meta data
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execAlterAddConstraint(TableMetaData tableMetaData, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        query.append("ALTER TABLE ").append(tableMetaData.getDisplayName()).append(" ADD ")
                .append(formConstraintString());
        dbConnection.execNonSelectForTimeout(query.toString());
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
     * Gets the database.
     *
     * @return the database
     */
    public Database getDatabase() {
        return table.getDatabase();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public TableMetaData getParent() {
        return table;

    }

    @Override
    public String getDropQuery(boolean isCascade) {
        return ConstraintMetaDataUtils.getDropQuery(table.getDisplayName(), super.getDisplayName(), isCascade);
    }

    @Override
    public String getObjectFullName() {
        return table.getDisplayName() + "." + getDisplayName();
    }

    @Override
    public boolean isDropAllowed() {
        if (getParent() instanceof ForeignTable) {
            return false;
        }

        return true;
    }
}
