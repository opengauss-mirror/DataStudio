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

package com.huawei.mppdbide.view.ui;

import java.util.List;
import java.util.stream.Collectors;

import com.huawei.mppdbide.view.ui.debug.DebugBaseTableComposite;
import com.huawei.mppdbide.view.ui.debug.DebugTableEventHandler;
import com.huawei.mppdbide.view.ui.debug.IDebugSourceData;

/**
 * Title: class
 * Description: The Class WindowBase.
 *
 * @since 3.0.0
 */
public abstract class WindowBase<T> implements DebugTableEventHandler {
    /**
     * the tableComposite
     */
    protected DebugBaseTableComposite tableComposite = null;

    /**
     * Clear.
     */
    public void clear() {
        tableComposite.removeAllData();
        VariableTableWindow.clearVariableValues();
        VariableTableWindow.initializeVariableValues();
    }

    /**
     * Refresh.
     */
    public void refresh() {
        List<IDebugSourceData> datas = getDataList()
                .stream()
                .map(var -> baseVoToSourceData(var))
                .collect(Collectors.toList());
        tableComposite.resetAllData(datas);
    }

    /**
     * description: get data list
     *
     * @return List<T> the data list
     */
    protected abstract List<T> getDataList();

    /**
     * description: convert objVo to IDebugSourceData
     *
     * @param objVo the T type
     * @return the IDebugSourceData
     */
    protected abstract IDebugSourceData baseVoToSourceData(T objVo);
}