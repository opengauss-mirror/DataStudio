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
 * Title: OneItemPerLineParameter
 *
 * @since 3.0.0
 */
public class OneItemPerLineParameter {
    private int offset;

    private boolean commaAfterItem;

    private int itemsSize;

    private int runningSize;

    public OneItemPerLineParameter(int offset, boolean commaAfterItem, int itemsSize, int runningSize) {
        this.offset = offset;
        this.commaAfterItem = commaAfterItem;
        this.itemsSize = itemsSize;
        this.runningSize = runningSize;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public boolean isCommaAfterItem() {
        return commaAfterItem;
    }

    public void setCommaAfterItem(boolean commaAfterItem) {
        this.commaAfterItem = commaAfterItem;
    }

    public int getItemsSize() {
        return itemsSize;
    }

    public void setItemsSize(int itemsSize) {
        this.itemsSize = itemsSize;
    }

    public int getRunningSize() {
        return runningSize;
    }

    public void setRunningSize(int runningSize) {
        this.runningSize = runningSize;
    }
}