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

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionColumnExpr.
 * 
 */

public class PartitionColumnExpr {

    private int position;
    private PartitionColumnType type;
    private String expr;
    private ColumnMetaData col;

    /**
     * Instantiates a new partition column expr.
     *
     * @param type the type
     */
    public PartitionColumnExpr(PartitionColumnType type) {
        this.type = type;
        this.position = -1;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets the expr.
     *
     * @return the expr
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Sets the expr.
     *
     * @param expr the new expr
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }

    /**
     * Gets the col.
     *
     * @return the col
     */
    public ColumnMetaData getCol() {
        return col;
    }

    /**
     * Sets the col.
     *
     * @param col the new col
     */
    public void setCol(ColumnMetaData col) {
        this.col = col;
    }

    /**
     * Gets the type.
     *
     * @return the type
     */
    public PartitionColumnType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (PartitionColumnType.COLUMN == getType()) {
            return getCol().getName();
        } else {
            return getExpr();
        }
    }

    /**
     * Validate partition column type.
     *
     * @return true, if successful
     */
    public boolean validatePartitionColumnType() {
        if (PartitionColumnType.COLUMN == getType()) {
            return true;
        }
        return false;

    }
}
