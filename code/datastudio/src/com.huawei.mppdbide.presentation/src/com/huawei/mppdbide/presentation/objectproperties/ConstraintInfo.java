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

package com.huawei.mppdbide.presentation.objectproperties;

/**
 * 
 * Title: class
 * 
 * Description: The Class ConstraintInfo.
 * 
 * @since 3.0.0
 */
public class ConstraintInfo {
    private String constraintExpr;
    private String columns;

    private String constraintType;
    private String constraintName;
    private String tablespace;
    private String consSchema;
    private boolean isdeferred;

    /**
     * Gets the constraint type.
     *
     * @return the constraint type
     */
    public String getConstraintType() {
        return constraintType;
    }

    /**
     * Sets the constraint type.
     *
     * @param constraintType the new constraint type
     */
    public void setConstraintType(String constraintType) {
        this.constraintType = constraintType;
    }

    /**
     * Gets the constraint name.
     *
     * @return the constraint name
     */
    public String getConstraintName() {
        return constraintName;
    }

    /**
     * Sets the constraint name.
     *
     * @param constraintName the new constraint name
     */
    public void setConstraintName(String constraintName) {
        this.constraintName = constraintName;
    }

    /**
     * Gets the constraint expr.
     *
     * @return the constraint expr
     */
    public String getConstraintExpr() {
        return constraintExpr;
    }

    /**
     * Sets the constraint expr.
     *
     * @param constraintExpr the new constraint expr
     */
    public void setConstraintExpr(String constraintExpr) {
        this.constraintExpr = constraintExpr;
    }

    /**
     * Gets the columns.
     *
     * @return the columns
     */
    public String getColumns() {
        return columns;
    }

    /**
     * Sets the columns.
     *
     * @param columns the new columns
     */
    public void setColumns(String columns) {
        this.columns = columns;
    }

    /**
     * Gets the table space.
     *
     * @return the table space
     */
    public String getTableSpace() {
        return tablespace;
    }

    /**
     * Sets the tablespace.
     *
     * @param tablespace the new tablespace
     */
    public void setTablespace(String tablespace) {
        this.tablespace = tablespace;
    }

    /**
     * Gets the cons schema.
     *
     * @return the cons schema
     */
    public String getConsSchema() {
        return consSchema;
    }

    /**
     * Sets the cons schema.
     *
     * @param consSchema the new cons schema
     */
    public void setConsSchema(String consSchema) {
        this.consSchema = consSchema;
    }

    /**
     * Checks if is deferred.
     *
     * @return true, if is deferred
     */
    public boolean isDeferred() {
        return isdeferred;
    }

    /**
     * Sets the deferred.
     *
     * @param isDeferred the new deferred
     */
    public void setDeferred(boolean isDeferred) {
        this.isdeferred = isDeferred;
    }

}
