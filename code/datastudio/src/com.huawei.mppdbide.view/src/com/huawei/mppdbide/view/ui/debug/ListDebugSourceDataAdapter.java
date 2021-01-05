/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.view.ui.debug;

import java.util.ArrayList;
import java.util.List;

/**
 * Title: ListDebugSourceDataAdapter for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public abstract class ListDebugSourceDataAdapter implements IDebugSourceData {
    /**
     * the default order index
     */
    protected int orderIndex = 0;

    /**
     * the array data
     */
    protected List<Object> dataArrays = new ArrayList<Object>(1);

    @Override
    public void setDataOrder(int order) {
        this.orderIndex = order;
    }

    @Override
    public int getDataOrder() {
        return this.orderIndex;
    }

    @Override
    public Object getValue(int titleIndex) {
        int dataIndex = titleIndex;
        if (isShowOrder()) {
            if (titleIndex == 0) {
                return this.orderIndex;
            }
            dataIndex -= 1;
        }
        return dataArrays.get(dataIndex);
    }

    @Override
    public int getTitleSize() {
        return dataArrays.size() + (isShowOrder() ? 1 : 0);
    }
}
