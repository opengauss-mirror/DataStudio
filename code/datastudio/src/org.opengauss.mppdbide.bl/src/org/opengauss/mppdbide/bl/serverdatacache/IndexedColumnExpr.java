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

/**
 * 
 * Title: class
 * 
 * Description: The Class IndexedColumnExpr.
 * 
 */

public class IndexedColumnExpr {

    private int position;
    private IndexedColumnType type;
    private ColumnMetaData col;
    private String expr;

    /**
     * Instantiates a new indexed column expr.
     *
     * @param type the type
     */
    public IndexedColumnExpr(IndexedColumnType type) {
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
     * Gets the type.
     *
     * @return the type
     */
    public IndexedColumnType getType() {
        return type;
    }

    @Override
    public String toString() {
        if (IndexedColumnType.COLUMN == getType()) {
            return getCol().getName();
        } else {
            return getExpr();
        }
    }

    /**
     * Validate column.
     *
     * @return true, if successful
     */
    public boolean validateColumn() {
        if (getType().equals(IndexedColumnType.COLUMN) && null != getCol()) {
            return true;
        }
        return false;
    }
}
