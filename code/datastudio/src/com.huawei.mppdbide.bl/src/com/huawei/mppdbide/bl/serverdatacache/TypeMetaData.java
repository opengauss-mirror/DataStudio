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
import java.util.ArrayList;

import com.huawei.mppdbide.adapter.gauss.GaussUtils;
import com.huawei.mppdbide.utils.IMessagesConstants;
import com.huawei.mppdbide.utils.exceptions.DatabaseCriticalException;
import com.huawei.mppdbide.utils.exceptions.DatabaseOperationException;
import com.huawei.mppdbide.utils.loader.MessageConfigLoader;
import com.huawei.mppdbide.utils.logger.MPPDBIDELoggerUtility;

/**
 * 
 * Title: class
 * 
 * Description: The Class TypeMetaData.
 * 
 */

public class TypeMetaData extends ServerObject implements GaussOLAPDBMSObject {

    private Namespace namespace;

    /* Any addition to be added in clone() function */
    private int typelen;

    private boolean isbyval;

    private PgtypeCategory category;

    private PgtypeType pgType;

    private long typtypmod;

    private boolean typnotnull;

    private long typarray;

    private String description;

    private String displayName;

    enum PgtypeCategory {
        PGTYPE_CAT_ARRAY, /* A - Array */
        PGTYPE_CAT_BOOLEAN, /* B - Boolean */
        PGTYPE_CAT_COMPOSITE, /* C Composite */
        PGTYPE_CAT_DATA_TIME, /* D Date/time */
        PGTYPE_CAT_ENUM, /* E Enum */
        PGTYPE_CAT_GEOMETRIC, /* G Geometric Do not pull */
        PGTYPE_CAT_NETWORK, /* I Network address Do not pull */
        PGTYPE_CAT_NUMERIC, /* N Numeric */
        PGTYPE_CAT_PSEUDO, /* P Pseudo-types Do not pull */
        PGTYPE_CAT_RANGE, /* R Range types Do not pull */
        PGTYPE_CAT_STRING, /* S String */
        PGTYPE_CAT_TIMESPAN, /* T Timespan */
        PGTYPE_CAT_USER_DEFINED, /* U User-defined */
        PGTYPE_CAT_BIT_STRING, /* V Bit-string */
        PGTYPE_CAT_UNKNOWN /* X unknown type Do not pull */
    }

    enum PgtypeType {
        PGTYPE_BASE, /* b base */
        PGTYPE_COMPOSITE, /* c composite */
        PGTYPE_DOMAIN, /* d domain */
        PGTYPE_PSEUDO, /* p pseudo */
    }

    /**
     * Instantiates a new type meta data.
     *
     * @param oid the oid
     * @param name the name
     * @param namespace the namespace
     */
    public TypeMetaData(long oid, String name, Namespace namespace) {
        super(oid, name, OBJECTTYPE.TYPEMETADATA, false);
        this.namespace = namespace;
    }

    /**
     * Gets the typelen.
     *
     * @return the typelen
     */
    public int getTypelen() {
        return typelen;
    }

    /**
     * Sets the typelen.
     *
     * @param typelen the new typelen
     */
    private void setTypelen(int typelen) {
        this.typelen = typelen;
    }

    /**
     * Checks if is isbyval.
     *
     * @return true, if is isbyval
     */
    public boolean isIsbyval() {
        return isbyval;
    }

    /**
     * Sets the isbyval.
     *
     * @param isbyval the new isbyval
     */
    private void setIsbyval(boolean isbyval) {
        this.isbyval = isbyval;
    }

    /**
     * Gets the category.
     *
     * @return the category
     */
    public PgtypeCategory getCategory() {
        return this.category;
    }

    /**
     * Sets the categoy.
     *
     * @param type the new categoy
     */
    private void setCategoy(PgtypeCategory type) {
        this.category = type;
    }

    /**
     * Sets the pg type.
     *
     * @param type the new pg type
     */
    private void setPgType(PgtypeType type) {
        this.pgType = type;
    }

    /**
     * Gets the typtypmod.
     *
     * @return the typtypmod
     */
    public long getTyptypmod() {
        return typtypmod;
    }

    /**
     * Sets the typtypmod.
     *
     * @param typtypmod the new typtypmod
     */
    private void setTyptypmod(long typtypmod) {
        this.typtypmod = typtypmod;
    }

    /**
     * Checks if is typnotnull.
     *
     * @return true, if is typnotnull
     */
    public boolean isTypnotnull() {
        return typnotnull;
    }

    /**
     * Sets the typnotnull.
     *
     * @param typnotnull the new typnotnull
     */
    public void setTypnotnull(boolean typnotnull) {
        this.typnotnull = typnotnull;
    }

    /**
     * Gets the typarray.
     *
     * @return the typarray
     */
    public long getTyparray() {
        return typarray;
    }

    /**
     * Sets the typarray.
     *
     * @param typarray the new typarray
     */
    private void setTyparray(long typarray) {
        this.typarray = typarray;
    }

    /**
     * Sets the namespace.
     *
     * @param ns the new namespace
     */
    public void setNamespace(Namespace ns) {
        this.namespace = ns;
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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    private void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the PG type.
     *
     * @param typename the typename
     * @return the PG type
     */
    public static PgtypeType getPGType(String typename) {
        if ("b".equals(typename)) {
            return PgtypeType.PGTYPE_BASE;
        } else if ("c".equals(typename)) {
            return PgtypeType.PGTYPE_COMPOSITE;
        } else if ("d".equals(typename)) {
            return PgtypeType.PGTYPE_DOMAIN;
        } else {
            return PgtypeType.PGTYPE_PSEUDO;
        }
    }

    /**
     * Gets the PG type category.
     *
     * @param typeName the type name
     * @return the PG type category
     */
    public static PgtypeCategory getPGTypeCategory(String typeName) {
        if ("A".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_ARRAY;
        } else if ("B".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_BOOLEAN;
        } else if ("C".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_COMPOSITE;
        } else if ("D".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_DATA_TIME;
        } else if ("E".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_ENUM;
        } else if ("G".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_GEOMETRIC;
        } else if ("I".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_NETWORK;
        } else if ("N".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_NUMERIC;
        } else if ("P".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_PSEUDO;
        } else if ("R".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_RANGE;
        } else if ("S".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_STRING;
        } else if ("T".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_TIMESPAN;
        } else if ("U".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_USER_DEFINED;
        } else if ("V".equals(typeName)) {
            return PgtypeCategory.PGTYPE_CAT_BIT_STRING;
        }

        return PgtypeCategory.PGTYPE_CAT_UNKNOWN;

    }

    /**
     * Convert to type meta data.
     *
     * @param rs the rs
     * @param db the db
     * @param includeDescription the include description
     * @throws DatabaseOperationException the database operation exception
     * @throws DatabaseCriticalException the database critical exception
     */
    public static void convertToTypeMetaData(ResultSet rs, Database db, boolean includeDescription)
            throws DatabaseOperationException, DatabaseCriticalException {
        long oid = 0;
        String name = null;
        long namespaceOid = 0;

        try {
            oid = rs.getLong("oid");
            name = rs.getString("typname");
            namespaceOid = rs.getLong("typnamespace");

            Namespace ns = db.getNameSpaceById(namespaceOid);

            if (null == ns) {
                MPPDBIDELoggerUtility.error("Unable to map few datatype to Namespace.");
                throw new DatabaseOperationException("Unable to map few datatype to Namespace.");
            }

            TypeMetaData type = new TypeMetaData(oid, name, ns);
            type.setIsbyval(rs.getBoolean("typbyval"));
            type.setTyparray(rs.getLong("typarray"));

            String dtCatogry = rs.getString("typcategory");
            type.setCategoy(getPGTypeCategory(dtCatogry));
            type.setPgType(getPGType(rs.getString("typtype")));
            type.setTypelen(rs.getInt("typlen"));
            type.setTypnotnull(rs.getBoolean("typnotnull"));
            type.setTyptypmod(rs.getLong("typtypmod"));
            if (includeDescription) {
                type.setDescription(rs.getString("desc"));
            }
            type.setDisplayDatatype(rs.getString("displaycolumns"));
            ns.getTypes().addItem(type);
        } catch (SQLException exception) {
            GaussUtils.handleCriticalException(exception);
            throw new DatabaseOperationException(IMessagesConstants.ERR_GUI_RESULT_SET_INVALID, exception);
        }
    }

    /**
     * Sets the display datatype.
     *
     * @param displayDatatypeName the new display datatype
     */
    private void setDisplayDatatype(String displayDatatypeName) {
        this.displayName = displayDatatypeName;
    }

    /**
     * Gets the display datatype.
     *
     * @return the display datatype
     */
    public String getDisplayDatatype() {
        return displayName;
    }

    /**
     * Gets the clone obj.
     *
     * @return the clone obj
     */
    protected TypeMetaData getCloneObj() {
        TypeMetaData type = new TypeMetaData(getOid(), getName(), this.namespace);

        setTypeDetails(type);

        return type;
    }

    /**
     * Sets the type details.
     *
     * @param type the new type details
     */
    private void setTypeDetails(TypeMetaData type) {
        type.setPgType(this.pgType);
        type.setIsbyval(this.isbyval);
        type.setTyparray(this.typarray);
        type.setTypnotnull(this.typnotnull);
        type.setTyptypmod(this.typtypmod);
        type.setCategoy(this.category);
        type.setTypelen(this.typelen);
        type.setDescription(this.description);
        type.setPgType(PgtypeType.PGTYPE_PSEUDO);
    }

    /**
     * Gets the clone obj with name.
     *
     * @param nameParam the name param
     * @return the clone obj with name
     */
    public TypeMetaData getCloneObjWithName(String nameParam) {
        String name = nameParam;
        if (null == name) {
            name = getName();
        }
        TypeMetaData type = new TypeMetaData(getOid(), name, this.namespace);

        setTypeDetails(type);

        return type;
    }

    /**
     * Gets the type by id.
     *
     * @param namespace the namespace
     * @param oid the oid
     * @return the type by id
     */
    public static TypeMetaData getTypeById(Namespace namespace, long oid) {
        ArrayList<TypeMetaData> types = namespace.getTypes().getList();

        for (TypeMetaData type : types) {
            if (oid == type.getOid()) {
                return type;
            }
        }

        // If control reaches here, then user has used Complex User defined
        // datatypes. We DONOT SUPPORT this type of feature, so just return a
        // dummy datatype object, so support errors.
        //
        return new TypeMetaData(oid, "", namespace);
    }

    /**
     * Gets the parent.
     *
     * @return the parent
     */
    @Override
    public Object getParent() {
        return this.namespace;
    }

    /**
     * Gets the search name.
     *
     * @return the search name
     */
    @Override
    public String getSearchName() {
        return getName() + " - " + getTypeLabel();
    }

}
