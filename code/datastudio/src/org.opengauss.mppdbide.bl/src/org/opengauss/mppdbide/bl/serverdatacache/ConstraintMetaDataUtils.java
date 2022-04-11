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

import org.opengauss.mppdbide.utils.MPPDBIDEConstants;

/**
 * 
 * Title: interface
 * 
 * Description: The Interface ConstraintMetaDataUtils.
 * 
 */

public interface ConstraintMetaDataUtils {

    static final String TABLE_CONSTRAINT_QRY = "SELECT c.oid as constraintid, c.conrelid as tableid, c.connamespace as namespaceid, "
            + "c.conname  as constraintname ,"
            + "c.contype as constrainttype, c.condeferrable as deferrable, c.condeferred  as deferred, "
            + "c.convalidated as validate, "
            + "c.conindid  as indexid, c.confrelid as fkeytableId, c.confupdtype as updatetype, "
            + "c.confdeltype as deletetype, c.confmatchtype as matchtype, "
            + "c.consrc as expr, c.conkey as columnlist, c.confkey as fkeycolumnlist, "
            + "pg_get_constraintdef(c.oid) as const_def , ts.spcname as tablespace  "
            + "FROM pg_constraint c LEFT JOIN " + "pg_class t on (t.oid = c.conrelid) "
            + "LEFT JOIN pg_index ind ON c.conindid = ind.indexrelid "
            + "LEFT JOIN pg_class ci on (ind.indexrelid = ci.oid) "
            + "LEFT JOIN  pg_tablespace ts ON (ts.oid = ci.reltablespace) " + "WHERE t.relkind = 'r' and t.oid = ";

    /**
     * Convert to constraint.
     *
     * @param rs the rs
     * @param parentNS the parent NS
     * @param parentTable the parent table
     * @param fkeyTable the fkey table
     * @param idx the idx
     * @return the constraint meta data
     * @throws SQLException the SQL exception
     */
    public static ConstraintMetaData convertToConstraint(ResultSet rs, Namespace parentNS, TableMetaData parentTable,
            TableMetaData fkeyTable, IndexMetaData idx) throws SQLException {
        ConstraintMetaData constraint = null;

        long oid = rs.getLong("constraintid");
        String typeStr = rs.getString("constrainttype");
        String name = rs.getString("constraintname");

        ConstraintType type = getConstraintType(typeStr);

        constraint = new ConstraintMetaData(oid, name, type);

        constraint.setNamespace(parentNS);

        constraint.setTable(parentTable);
        constraint.setDeffearableOptions(rs.getBoolean("deferrable"), rs.getBoolean("deferred"));
        constraint.setValidated(rs.getBoolean("validate"));

        constraint.setRefernceTable(fkeyTable);
        ForeignKeyActionType update = getActionType(rs.getString("updatetype"));
        ForeignKeyActionType delete = getActionType(rs.getString("deletetype"));
        ForeignKeyMatchType matchType = getMatchType(rs.getString("matchtype"));
        constraint.setFkActions(matchType, delete, update);
        constraint.setExcludeWhereClauseExpr(rs.getString("expr"));
        constraint.setConsDef(rs.getString("const_def"));
        constraint.setReferenceIndex(idx);
        constraint.setLoaded(true);

        return constraint;

    }

    /**
     * Gets the table constraint qry.
     *
     * @param l1 the l
     * @return the table constraint qry
     */
    public static String getTableConstraintQry(long l1) {
        return TABLE_CONSTRAINT_QRY + l1;
    }

    /**
     * Gets the constraint type.
     *
     * @param typeStr the type str
     * @return the constraint type
     */
    public static ConstraintType getConstraintType(String typeStr) {
        if ("c".equals(typeStr)) {
            return ConstraintType.CHECK_CONSTRSINT;
        } else if ("f".equals(typeStr)) {
            return ConstraintType.FOREIGN_KEY_CONSTRSINT;
        } else if ("p".equals(typeStr)) {
            return ConstraintType.PRIMARY_KEY_CONSTRSINT;
        } else if ("u".equals(typeStr)) {
            return ConstraintType.UNIQUE_KEY_CONSTRSINT;
        } else {
            return ConstraintType.EXCLUSION_CONSTRSINT;
        }
    }

    /**
     * Gets the action type.
     *
     * @param typeStr the type str
     * @return the action type
     */
    public static ForeignKeyActionType getActionType(String typeStr) {
        if ("a".equals(typeStr)) {
            return ForeignKeyActionType.FK_NO_ACTION;
        } else if ("r".equals(typeStr)) {
            return ForeignKeyActionType.FK_RESTRICT;
        } else if ("c".equals(typeStr)) {
            return ForeignKeyActionType.FK_CASCADE;
        } else if ("n".equals(typeStr)) {
            return ForeignKeyActionType.FK_SET_NULL;
        } else {
            return ForeignKeyActionType.FK_SET_DEFAULT;
        }
    }

    /**
     * Gets the match type.
     *
     * @param typeStr the type str
     * @return the match type
     */
    public static ForeignKeyMatchType getMatchType(String typeStr) {
        if ("f".equals(typeStr)) {
            return ForeignKeyMatchType.FK_MATCH_FULL;
        } else if ("p".equals(typeStr)) {
            return ForeignKeyMatchType.FK_MATCH_PARTIAL;
        } else {
            return ForeignKeyMatchType.FK_MATCH_SIMPLE;
        }
    }

    /**
     * Gets the drop query.
     *
     * @param tableName the table name
     * @param constraintName the constraint name
     * @param isCascade the is cascade
     * @return the drop query
     */
    public static String getDropQuery(String tableName, String constraintName, boolean isCascade) {
        StringBuilder query = new StringBuilder(MPPDBIDEConstants.STRING_BUILDER_CAPACITY);

        query.append("ALTER TABLE IF EXISTS ").append(tableName).append(" DROP CONSTRAINT IF EXISTS ")
                .append(constraintName);

        if (isCascade) {
            query.append(MPPDBIDEConstants.CASCADE);
        }

        return query.toString();
    }
}
