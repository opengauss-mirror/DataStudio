/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
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