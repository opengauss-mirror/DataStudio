/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.gauss.format.processor;

/**
 * Title: HandleOneItemPerLineParameter Description: Copyright (c) Huawei
 * Technologies Co., Ltd. 2012-2019.
 *
 * @author S72444
 * @version [DataStudio 6.5.1, 18-Dec-2019]
 * @since 18-Dec-2019
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