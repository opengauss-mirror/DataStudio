/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.bl.serverdatacache;

/**
 * 
 * Title: class
 * 
 * Description: The Class PartitionColumnExpr.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
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
