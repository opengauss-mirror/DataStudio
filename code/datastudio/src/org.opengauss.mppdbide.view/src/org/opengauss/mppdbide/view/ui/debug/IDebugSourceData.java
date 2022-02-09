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

package org.opengauss.mppdbide.view.ui.debug;

/**
 * Title: IDebugSourceData for use
 *
 * @since 3.0.0
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
