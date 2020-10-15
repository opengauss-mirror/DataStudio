/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

/**
 * Title: OneItemPerLineParameter Description: Copyright (c) Huawei Technologies
 * Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 18-Dec-2019]
 * @since 18-Dec-2019
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