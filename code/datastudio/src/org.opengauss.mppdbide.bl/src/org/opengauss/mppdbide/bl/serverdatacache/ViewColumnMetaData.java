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

import org.opengauss.mppdbide.adapter.gauss.DBConnection;
import org.opengauss.mppdbide.utils.exceptions.DatabaseCriticalException;
import org.opengauss.mppdbide.utils.exceptions.DatabaseOperationException;

/**
 * 
 * Title: class
 * 
 * Description: The Class ViewColumnMetaData.
 * 
 */

public class ViewColumnMetaData extends ServerObject implements GaussOLAPDBMSObject {

    private String dataTypeSchema;

    private TypeMetaData dataType;

    private int scale;

    private int lenOrPrecision;

    private int arrayNDim;

    private String defaultValue;

    private boolean isNotNull;

    private String checkConstraintExpr;

    private boolean isUnique;

    private boolean isLoaded;

    private ViewMetaData parent;

    private String viewDatatype;

    private static final String QUERY_ALTER_DEFAULT_PREFIX = "ALTER VIEW ";

    private static final String QUERY_ALTER_DEFAULT_COMMAND = " ALTER COLUMN ";

    private static final String QUERY_ALTER_DEFAULT_SETVALUE = " SET DEFAULT ";

    private static final String QUERY_ALTER_DEFAULT_REMOVEVALUE = " DROP DEFAULT ";

    /**
     * Instantiates a new view column meta data.
     *
     * @param view the view
     * @param oid the oid
     * @param name the name
     * @param dataType the data type
     */
    public ViewColumnMetaData(ViewMetaData view, long oid, String name, TypeMetaData dataType) {
        super(oid, name, OBJECTTYPE.VIEW_COLUMN_METADATA, false);
        this.parent = view;
        this.dataType = dataType;
        this.isLoaded = false;
    }

    /**
     * Gets the database.
     *
     * @return the database
     */
    @Override
    public Database getDatabase() {
        return parent.getDatabase();
    }

    /**
     * Gets the data type schema.
     *
     * @return the data type schema
     */
    public String getDataTypeSchema() {
        return dataTypeSchema;
    }

    /**
     * Sets the data type schema.
     *
     * @param dataTypeSchema the new data type schema
     */
    public void setDataTypeSchema(String dataTypeSchema) {
        this.dataTypeSchema = dataTypeSchema;
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
     * Checks if is not null.
     *
     * @return true, if is not null
     */
    public boolean isNotNull() {
        return isNotNull;
    }

    /**
     * Sets the not null.
     *
     * @param isNotNul the new not null
     */
    public void setNotNull(boolean isNotNul) {
        this.isNotNull = isNotNul;
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
     * Gets the check constraint expr.
     *
     * @return the check constraint expr
     */
    public String getCheckConstraintExpr() {
        return checkConstraintExpr;
    }

    /**
     * Sets the check constraint expr.
     *
     * @param checkConstraintExpr the new check constraint expr
     */
    public void setCheckConstraintExpr(String checkConstraintExpr) {
        this.checkConstraintExpr = checkConstraintExpr;
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
     * @param isUniqe the new unique
     */
    public void setUnique(boolean isUniqe) {
        this.isUnique = isUniqe;
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
     * Gets the parent.
     *
     * @return the parent
     */
    public ViewMetaData getParent() {
        return parent;
    }

    /**
     * Sets the parent.
     *
     * @param parent the new parent
     */
    public void setParent(ViewMetaData parent) {
        this.parent = parent;
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
        String view = getParent().getName();
        String ns = getParent().getNamespace().getName();
        return ns + '.' + view + " - " + getTypeLabel();

    }

    /**
     * Convert to view column meta data.
     *
     * @param rs the rs
     * @param view the view
     * @param type the type
     * @return the view column meta data
     * @throws DatabaseCriticalException the database critical exception
     * @throws DatabaseOperationException the database operation exception
     * @throws SQLException the SQL exception
     */
    public static ViewColumnMetaData convertToViewColumnMetaData(ResultSet rs, ViewMetaData view, TypeMetaData type)
            throws DatabaseCriticalException, DatabaseOperationException, SQLException {
        ViewColumnMetaData viewCol = new ViewColumnMetaData(view, rs.getLong("columnidx"), rs.getString("name"), type);
        extractPrecisionScale(rs, viewCol);
        viewCol.setArrayNDim(rs.getInt("dimentions"));
        viewCol.setNotNull(rs.getBoolean("notnull"));
        viewCol.setDefaultValue(rs.getString("default_value"));
        viewCol.setLoaded(true);
        viewCol.setViewDisplayDatatype(rs.getString("displayColumns"));
        return viewCol;
    }

    /**
     * Sets the view display datatype.
     *
     * @param viewDatatyp the new view display datatype
     */
    private void setViewDisplayDatatype(String viewDatatyp) {
        this.viewDatatype = viewDatatyp;
    }

    /**
     * Gets the view display datatype.
     *
     * @return the view display datatype
     */
    public String getViewDisplayDatatype() {
        return viewDatatype;
    }

    /**
     * Extract precision scale.
     *
     * @param rs the rs
     * @param col the col
     * @throws SQLException the SQL exception
     */
    public static void extractPrecisionScale(ResultSet rs, ViewColumnMetaData col) throws SQLException {
        int len = rs.getInt("length");

        if (len > 0) {
            col.setLenOrPrecision(len);
            return;
        }

        int precision = rs.getInt("precision");
        if (precision < 0) {
            col.setLenOrPrecision(-1);
            return;
        }
        if ("bpchar".equals(col.getDataType().getName()) || "varchar".equals(col.getDataType().getName())) {
            precision -= 4;
            col.setLenOrPrecision(precision);
        } else if ("bit".equals(col.getDataType().getName())) {
            col.setLenOrPrecision(precision);
        } else {
            precision -= 4;
            col.setLenOrPrecision(precision >> 16);
            col.setScale(precision % (1 << 16));
        }
    }

    /**
     * Sets the default value.
     *
     * @param newDefaultValue the new default value
     * @param conn the conn
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public void setDefaultValue(String newDefaultValue, DBConnection conn)
            throws DatabaseOperationException, DatabaseCriticalException {
        String query = getSetDefaultValueQuery(newDefaultValue);
        conn.execNonSelect(query);
    }

    /**
     * Gets the sets the default value query.
     *
     * @param newDefaultValue the new default value
     * @return the sets the default value query
     */
    public String getSetDefaultValueQuery(String newDefaultValue) {
        StringBuilder builder = new StringBuilder(QUERY_ALTER_DEFAULT_PREFIX);
        ViewMetaData view = getParent();
        builder.append(view.getNamespace().getQualifiedObjectName()).append('.').append(view.getQualifiedObjectName())
                .append(QUERY_ALTER_DEFAULT_COMMAND).append(getQualifiedObjectName());

        if (newDefaultValue.trim().isEmpty()) {
            builder.append(QUERY_ALTER_DEFAULT_REMOVEVALUE);
        } else {
            builder.append(QUERY_ALTER_DEFAULT_SETVALUE);

            builder.append(ServerObject.getLiteralName(newDefaultValue));

        }

        return builder.toString();
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

    /**
     * Gets the column precision scale.
     *
     * @return the column precision scale
     */
    private String getcolumnPrecisionScale() {
        StringBuilder strBuilder = new StringBuilder();
        if (this.lenOrPrecision > 0) {
            strBuilder.append('(');
            strBuilder.append(this.lenOrPrecision);
            if (0 != this.scale) {
                strBuilder.append(',');
                strBuilder.append(this.scale);
            }
            strBuilder.append(')');
        }
        return strBuilder.toString();
    }
}
