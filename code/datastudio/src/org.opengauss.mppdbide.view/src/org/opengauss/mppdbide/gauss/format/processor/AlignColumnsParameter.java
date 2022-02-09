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

package org.opengauss.mppdbide.gauss.format.processor;

/**
 * Title: AlignColumnsParameter
 *
 * @since 3.0.0
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