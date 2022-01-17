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

import com.huawei.mppdbide.adapter.gauss.DBConnection;
import com.huawei.mppdbide.utils.MPPDBIDEConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ColumnMetaData.
 * 
 */

public class ColumnMetaData extends BatchDropServerObject implements GaussOLAPDBMSObject {

    private String dataTypeSchema;

    private TypeMetaData dataType;

    private int lenOrPrecision;

    private int scale;

    private int arrayNDim; /* number of dimensions */

    private boolean isNotNull;

    private boolean hasDefVal;

    private String attDefString;

    private String defaultValue;

    private String checkConstraintExpr;

    private boolean isUnique;

    private boolean isLoaded;

    private TableMetaData parentTable;

    private String displayDatatypeName;

    private boolean columnCase;

    private boolean isDistributionColm;

    private boolean isFunction;

    private String colDescription;

    /**
     * Gets the col description.
     *
     * @return the col description
     */
    public String getColDescription() {
        return colDescription;
    }

    /**
     * Sets the col description.
     *
     * @param colDescription the new col description
     */
    public void setColDescription(String colDescription) {
        this.colDescription = colDescription;
    }

    /**
     * Instantiates a new column meta data.
     *
     * @param parentTable the parent table
     * @param oid the oid
     * @param name the name
     * @param dataType the data type
     */
    public ColumnMetaData(TableMetaData parentTable, long oid, String name, TypeMetaData dataType) {
        super(oid, name, OBJECTTYPE.COLUMN_METADATA, false);
        this.parentTable = parentTable;
        this.dataType = dataType;
        this.isLoaded = false;
    }

    /**
     * Sets the att def string.
     *
     * @param attrDefStr the new att def string
     */
    public void setAttDefString(String attrDefStr) {
        this.attDefString = attrDefStr;
    }

    /**
     * Gets the att def string.
     *
     * @return the att def string
     */
    public String getAttDefString() {
        return this.attDefString;
    }

    /**
     * Gets the len or precision.
     *
     * @return the len or precision
     */
    public int getLenOrPrecision() {
        return lenOrPrecision;
    }

    /**
     * Sets the len or precision.
     *
     * @param lenOrPrecision the new len or precision
     */
    public void setLenOrPrecision(int lenOrPrecision) {
        this.lenOrPrecision = lenOrPrecision;
    }

    /**
     * Gets the scale.
     *
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * Sets the scale.
     *
     * @param scale the new scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

    /**
     * Gets the array N dim.
     *
     * @return the array N dim
     */
    public int getArrayNDim() {
        return arrayNDim;
    }

    /**
     * Sets the array N dim.
     *
     * @param arrayNDim the new array N dim
     */
    public void setArrayNDim(int arrayNDim) {

        this.arrayNDim = arrayNDim;
    }

    /**
     * Gets the default value.
     *
     * @return the default value
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the new default value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Checks if is not null.
     *
     * @return true, if is not null
     */
    public boolean isNotNull() {
        return isNotNull;
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
     * Sets the checks for def val.
     *
     * @param val the new checks for def val
     */
    public void setHasDefVal(boolean val) {
        this.hasDefVal = val;
    }

    /**
     * Gets the checks for def val.
     *
     * @return the checks for def val
     */
    public boolean getHasDefVal() {
        return this.hasDefVal;
    }

    /**
     * Column details.
     *
     * @param displayColumns the display columns
     * @param isCreate the is create
     * @return the string[]
     */
    public String[] columnDetails(int displayColumns, boolean isCreate) {
        StringBuilder query = new StringBuilder(512);
        String[] returnValue = new String[4];

        if (isCreate) {
            returnValue[0] = getName();
        } else {
            StringBuilder tmpColName = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
            String columnName = getName();
            tmpColName.append(columnName);
            returnValue[0] = tmpColName.toString();
        }

        query.append(' ');

        appendDataTypeInfoInQuery(isCreate, query);

        for (int i = 0; i < this.arrayNDim; i++) {
            query.append("[]");
        }

        returnValue[1] = query.toString();

        query.setLength(0);
        if (this.isNotNull) {
            query.append(" NOT NULL");
        }

        if (null != this.defaultValue) {
            query.append(" DEFAULT ");
            query.append(getIsFunction() ? this.defaultValue : ServerObject.getLiteralName(this.defaultValue));
        }

        if (this.isUnique) {
            query.append(" UNIQUE");
        }

        if (null != this.checkConstraintExpr) {
            query.append(" CHECK (");
            query.append(this.checkConstraintExpr);
            query.append(") ");
        }

        returnValue[2] = query.toString();
        returnValue[3] = this.colDescription;
        return returnValue;
    }

    /**
     * Append data type info in query.
     *
     * @param isCreate the is create
     * @param query the query
     */
    private void appendDataTypeInfoInQuery(boolean isCreate, StringBuilder query) {

        if (null != this.dataTypeSchema) {
            query.append(this.dataTypeSchema);
            query.append('.');
        }

        if (!isCreate) {
            query.append(this.displayDatatypeName);
        } else {
            if (null != dataType) {
                query.append(this.dataType.getName());
            }
            if (this.lenOrPrecision > 0) {
                query.append('(');
                query.append(this.lenOrPrecision);
                if (0 != this.scale) {
                    query.append(',');
                    query.append(this.scale);
                }
                query.append(')');
            }
        }
    }

    /**
     * Sets the data type scheam.
     *
     * @param dataTypeSchm the new data type scheam
     */
    public void setDataTypeScheam(String dataTypeSchm) {
        this.dataTypeSchema = dataTypeSchm;
    }

    /**
     * Gets the data type schema.
     *
     * @return the data type schema
     */
    public String getColDataTypeSchema() {
        return dataTypeSchema;
    }

    /**
     * Sets the pre.
     *
     * @param len the len
     * @param scl the scl
     */
    public void setPre(int len, int scl) {
        this.lenOrPrecision = len;
        this.scale = scl;
    }

    /**
     * Sets the not null.
     *
     * @param isNtNull the new not null
     */
    public void setNotNull(boolean isNtNull) {
        this.isNotNull = isNtNull;
    }

    /**
     * Sets the unique.
     *
     * @param isUnqe the new unique
     */
    public void setUnique(boolean isUnqe) {
        this.isUnique = isUnqe;
    }

    /**
     * Sets the check constraint.
     *
     * @param checkConsExpr the new check constraint
     */
    public void setCheckConstraint(String checkConsExpr) {
        this.checkConstraintExpr = checkConsExpr;
    }

    /**
     * Gets the check constraint expr.
     *
     * @return the check constraint expr
     */
    public String getCheckConstraintExpr() {
        return checkConstraintExpr;
    }

    /**
     * Form column string.
     *
     * @param isCreateTable the is create table
     * @return the string
     */
    public String formColumnString(boolean isCreateTable) {
        StringBuilder query = new StringBuilder(512);
        boolean isSchemaselected = false;

        query.append(ServerObject.getQualifiedObjectName(getName()));

        query.append(' ');

        if (null != this.dataTypeSchema) {
            query.append(ServerObject.getQualifiedObjectName(this.dataTypeSchema));
            query.append('.');
            isSchemaselected = true;
        }

        if (null != this.dataType) {
            if (isSchemaselected) {
                query.append(this.dataType.getDisplayName());
            } else {
                query.append(this.dataType.getName());
            }

        }
        formPrecisionAndArrayDimensionForQuery(query);

        if (this.isNotNull) {
            query.append(" NOT NULL");
        }

        if (null != this.defaultValue) {
            query.append(" DEFAULT ");
            query.append(getIsFunction() ? this.defaultValue : ServerObject.getLiteralName(this.defaultValue));
        }

        if (this.isUnique) {
            if (parentTable.getOrientation() == TableOrientation.ROW) {
                query.append(" UNIQUE");
            }
        }

        if (null != this.checkConstraintExpr) {
            if (parentTable.getOrientation() == TableOrientation.ROW) {
                query.append(" CHECK ( ");
                query.append(this.checkConstraintExpr);
                query.append(" )");
            }
        }

        return query.toString();
    }

    /**
     * Form precision and array dimension for query.
     *
     * @param query the query
     */
    public void formPrecisionAndArrayDimensionForQuery(StringBuilder query) {
        if (0 != this.lenOrPrecision) {
            query.append('(');
            query.append(this.lenOrPrecision);
            if (0 != this.scale) {
                query.append(',');
                query.append(this.scale);
            }
            query.append(')');
        }

        for (int index = 0; index < this.arrayNDim; index++) {
            if (parentTable.getOrientation() == TableOrientation.ROW) {
                query.append("[]");
            }
        }
    }

    /**
     * Exec alter toggle set null.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execAlterToggleSetNull(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(parentTable.getDisplayName()).append(" ALTER COLUMN ")
                .append(super.getQualifiedObjectName());

        if (!this.isNotNull) {
            qry.append(" SET NOT NULL ");
        } else {
            qry.append(" DROP NOT NULL ");
        }

        dbConnection.execNonSelectForTimeout(qry.toString());
        this.isNotNull = !this.isNotNull;

    }

    /**
     * Exec alter default.
     *
     * @param expr the expr
     * @param isFunc the is func
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execAlterDefault(String expr, boolean isFunc, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(parentTable.getDisplayName()).append(" ALTER COLUMN ")
                .append(super.getQualifiedObjectName());

        if (null == expr || expr.length() < 1) {
            qry.append(" DROP DEFAULT ");
        } else {
            qry.append(" SET DEFAULT ").append(isFunc ? expr : ServerObject.getLiteralName(expr));
        }

        dbConnection.execNonSelectForTimeout(qry.toString());
        this.isNotNull = !this.isNotNull;

        /*
         * if alter query executes successfully, i.e. execution reaches here (no
         * exception), then only set expression flag
         */
        setIsFunction(isFunc);

    }

    /**
     * Gets the parent table.
     *
     * @return the parent table
     */
    public TableMetaData getParentTable() {
        return parentTable;
    }

    /**
     * Exec alter add column.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execAlterAddColumn(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(this.getParentTable().getDisplayName()).append(" ADD COLUMN ")
                .append(formColumnString(false)).append(MPPDBIDEConstants.SEMICOLON);
        qry.append(MPPDBIDEConstants.LINE_SEPARATOR).append(formSetCommentQuery());

        dbConnection.execNonSelectForTimeout(qry.toString());
    }

    /**
     * Sets the description.
     *
     * @param columnName the column name
     * @param desc the desc
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setDescription(String columnName, String desc, DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        query.append("COMMENT ON COLUMN " + columnName + " IS " + ServerObject.getLiteralName(desc));
        dbConnection.execNonSelectForTimeout(query.toString());
    }

    /**
     * Exec drop.
     *
     * @param dbConnection the db connection
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     */
    public void execDrop(DBConnection dbConnection) throws DatabaseCriticalException, DatabaseOperationException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(this.getParentTable().getDisplayName()).append(" DROP COLUMN ")
                .append(super.getQualifiedObjectName()).append(";");

        dbConnection.execNonSelectForTimeout(qry.toString());
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
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        qry.append("ALTER TABLE ").append(this.getParentTable().getDisplayName()).append(" RENAME COLUMN ")
                .append(super.getQualifiedObjectName()).append(" TO ")
                .append(ServerObject.getQualifiedObjectName(newName));

        dbConnection.execNonSelectForTimeout(qry.toString());
    }

    /**
     * Exec change data type.
     *
     * @param dbConnection the db connection
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void execChangeDataType(DBConnection dbConnection)
            throws DatabaseOperationException, DatabaseCriticalException {
        StringBuilder qry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        boolean isSchemaselected = false;

        qry.append("ALTER TABLE ").append(this.parentTable.getDisplayName()).append(" ALTER COLUMN ")
                .append(super.getQualifiedObjectName()).append(" TYPE ");

        if (null != this.dataTypeSchema) {
            qry.append(ServerObject.getQualifiedObjectName(this.dataTypeSchema));
            qry.append('.');
            isSchemaselected = true;
        }
        if (null != this.dataType) {
            if (isSchemaselected) {
                qry.append(this.dataType.getDisplayName());
            } else {
                qry.append(this.dataType.getName());
            }

        }
        if (0 != this.lenOrPrecision) {
            qry.append('(');
            qry.append(this.lenOrPrecision);
            if (0 != this.scale) {
                qry.append(',');
                qry.append(this.scale);
            }
            qry.append(')');
        }
        qry.append(';');

        dbConnection.execNonSelect(qry.toString());

    }

    /**
     * Gets the data type.
     *
     * @return the data type
     */
    public TypeMetaData getDataType() {
        return dataType;
    }

    /**
     * Sets the data type.
     *
     * @param typeMetaData the new data type
     */
    public void setDataType(TypeMetaData typeMetaData) {
        dataType = typeMetaData;
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
     * Gets the search name.
     *
     * @return the search name
     */
    @Override
    public String getSearchName() {

        return getName() + " - " + getParentDetails();
    }

    /**
     * Gets the parent details.
     *
     * @return the parent details
     */
    public String getParentDetails() {
        String table = getParentTable().getName();
        String ns = getParentTable().getNamespace().getName();
        return ns + '.' + table + " - " + getTypeLabel();

    }

    /**
     * Gets the parent DB.
     *
     * @return the parent DB
     */
    public Database getParentDB() {
        return parentTable.getDatabase();
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    public Object getParent() {
        return this.parentTable;
    }

    /**
     * Sets the display datatype.
     *
     * @param datatypeName the new display datatype
     */
    public void setDisplayDatatype(String datatypeName) {
        this.displayDatatypeName = datatypeName;
    }

    /**
     * Gets the display datatype.
     *
     * @return the display datatype
     */
    public String getDisplayDatatype() {
        return displayDatatypeName;
    }

    /**
     * Gets the column case.
     *
     * @return the column case
     */
    public boolean getColumnCase() {
        return this.columnCase;
    }

    /**
     * Sets the column case.
     *
     * @param colCase the new column case
     */
    public void setColumnCase(boolean colCase) {
        this.columnCase = colCase;
    }

    /**
     * Sets the checks if is function.
     *
     * @param isFunction the new checks if is function
     */
    public void setIsFunction(boolean isFunction) {
        this.isFunction = isFunction;
    }

    /**
     * Gets the checks if is function.
     *
     * @return the checks if is function
     */
    public boolean getIsFunction() {
        return this.isFunction;
    }

    /**
     * Checks if is distribution colm.
     *
     * @return true, if is distribution colm
     */
    public boolean isDistributionColm() {
        return isDistributionColm;
    }

    /**
     * Sets the distribution colm.
     *
     * @param isDistributionColumn the new distribution colm
     */
    public void setDistributionColm(boolean isDistributionColumn) {
        this.isDistributionColm = isDistributionColumn;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return this.getParentDB();
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ColumnMetaData) {
            ColumnMetaData otherColMetaData = (ColumnMetaData) obj;

            if (super.equals(obj)) {
                return getDataType().equals(otherColMetaData.getDataType());
            }
        }

        return false;
    }

    /**
     * Hash code.
     *
     * @return the int
     */
    @Override
    public int hashCode() {
        return super.hashCode() + getDataType().hashCode();
    }

    /**
     * Gets the object full name.
     *
     * @return the object full name
     */
    @Override
    public String getObjectFullName() {
        return this.getParentTable().getDisplayName() + "." + this.getDisplayName();
    }

    /**
     * Gets the drop query.
     *
     * @param isCascade the is cascade
     * @return the drop query
     */
    @Override
    public String getDropQuery(boolean isCascade) {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        query.append("ALTER TABLE IF EXISTS ").append(this.getParentTable().getDisplayName())
                .append(" DROP COLUMN IF EXISTS ").append(super.getQualifiedObjectName());

        if (isCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }

    /**
     * Checks if is drop allowed.
     *
     * @return true, if is drop allowed
     */
    @Override
    public boolean isDropAllowed() {
        if (getParent() instanceof ForeignTable) {
            return false;
        }
        return true;
    }

    /**
     * Gets the data type name.
     *
     * @return the data type name
     */
    public String getDataTypeName() {
        return getDataType().getName();
    }

    /**
     * Form set comment query.
     *
     * @return the string
     * @Title: formSetCommentQuery
     * @Description: generate comment query
     */
    public String formSetCommentQuery() {
        StringBuilder commentQry = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);
        commentQry.append("COMMENT ON COLUMN ");
        commentQry.append(this.parentTable.getQualifiedName());
        commentQry.append(MPPDBIDEConstants.DOT);
        commentQry.append(ServerObject.getQualifiedObjectName(getName()));
        commentQry.append(" IS ");
        commentQry.append(null == this.colDescription ? "NULL" : ServerObject.getLiteralName(this.colDescription));
        commentQry.append(MPPDBIDEConstants.SEMICOLON);
        return commentQry.toString();
    }

    /**
     * Gets the clm name with datatype.
     *
     * @param isParentDescNeeded the is parent desc needed
     * @return the clm name with datatype
     */
    public String getClmNameWithDatatype(boolean isParentDescNeeded) {
        return getName() + " - " + getDataType().getName() + getcolumnPrecisionScale()
                + addParentDetails(isParentDescNeeded);
    }

    /**
     * Gets the column precision scale.
     *
     * @return the column precision scale
     */
    private String getcolumnPrecisionScale() {
        StringBuilder strBldr = new StringBuilder();
        if (this.lenOrPrecision > 0) {
            strBldr.append('(');
            strBldr.append(this.lenOrPrecision);
            if (0 != this.scale) {
                strBldr.append(',');
                strBldr.append(this.scale);
            }
            strBldr.append(')');
        }
        return strBldr.toString();
    }

    /**
     * Adds the parent details.
     *
     * @param isParentDescNeeded the is parent desc needed
     * @return the string
     */
    private String addParentDetails(boolean isParentDescNeeded) {
        if (isParentDescNeeded) {
            return " - " + getParentDetails();
        }
        return "";
    }

}
