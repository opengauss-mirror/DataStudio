/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

/**
 * Title: AlignColumnsParameter Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 18-Dec-2019]
 * @since 18-Dec-2019
 */
public class AlignColumnsParameter {
    private boolean expContainStmt;

    private int runningSize;

    private int itemsSize;

    public AlignColumnsParameter(boolean expContainStmt, int runningSize, int itemsSize) {
        this.expContainStmt = expContainStmt;
        this.runningSize = runningSize;
        this.itemsSize = itemsSize;
    }

    public boolean isExpContainStmt() {
        return expContainStmt;
    }

    public void setExpContainStmt(boolean expContainStmt) {
        this.expContainStmt = expContainStmt;
    }

    public int getRunningSize() {
        return runningSize;
    }

    public void setRunningSize(int runningSize) {
        this.runningSize = runningSize;
    }

    public int getItemsSize() {
        return itemsSize;
    }

    public void setItemsSize(int itemsSize) {
        this.itemsSize = itemsSize;
    }
}