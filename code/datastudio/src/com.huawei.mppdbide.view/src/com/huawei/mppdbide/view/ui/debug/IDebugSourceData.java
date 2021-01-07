/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.debug;

/**
 * Title: IDebugSourceData for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public interface IDebugSourceData {
    /**
     * description: is show order of column
     *
     * @return boolean if show order, first column is order
     */
    boolean isShowOrder();

    /**
     * description: set the data order index in tableview
     *
     * @param order the order to set
     */
    void setDataOrder(int order);

    /**
     * description: get order of the data
     *
     * @return int the order, based of 0
     */
    int getDataOrder();

    /**
     * description: get value by index
     *
     * @param titleIndex the title index
     * @return Object the title value
     */
    Object getValue(int titleIndex);

    /**
     * description: is cur column editable
     *
     * @param titleIndex the title index
     * @return boolean the title index is editable
     */
    default boolean isEditable(int titleIndex) {
        return true;
    }

    /**
     * description: get total title index length
     *
     * @return int the title size
     */
    int getTitleSize();
}
