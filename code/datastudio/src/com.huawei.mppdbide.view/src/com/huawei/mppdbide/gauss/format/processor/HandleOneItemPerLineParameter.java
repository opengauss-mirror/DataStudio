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

package com.huawei.mppdbide.gauss.format.processor;

/**
 * Title: HandleOneItemPerLineParameter
 *
 * @since 3.0.0
 */
public class HandleOneItemPerLineParameter {
    private int offset;

    private boolean commaAfterItem;

    private int runningSize;

    private boolean isLastIndex;

    public HandleOneItemPerLineParameter(int offset, boolean commaAfterItem, int runningSize, boolean isLastIndex) {
        this.offset = offset;
        this.commaAfterItem = commaAfterItem;
        this.runningSize = runningSize;
        this.isLastIndex = isLastIndex;
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

    public int getRunningSize() {
        return runningSize;
    }

    public void setRunningSize(int runningSize) {
        this.runningSize = runningSize;
    }

    public boolean isLastIndex() {
        return isLastIndex;
    }

    public void setLastIndex(boolean isLastIndex) {
        this.isLastIndex = isLastIndex;
    }
}