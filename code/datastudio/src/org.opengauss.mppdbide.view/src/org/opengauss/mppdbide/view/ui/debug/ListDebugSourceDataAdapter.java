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

import java.util.ArrayList;
import java.util.List;

/**
 * Title: ListDebugSourceDataAdapter for use
 *
 * @since 3.0.0
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
