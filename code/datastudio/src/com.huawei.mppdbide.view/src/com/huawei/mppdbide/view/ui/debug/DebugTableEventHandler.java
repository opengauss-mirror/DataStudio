/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */


package com.huawei.mppdbide.view.ui.debug;

import java.util.List;

/**
 * Title: DebugTableEventHandler for use
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author z00588921
 * @version [DataStudio for openGauss 2020-12-31]
 * @since 2020-12-31
 */
public interface DebugTableEventHandler {
    /**
     * description: if item clicked, this func will be called!
     *
     * @param selectItems the select item which is instanceof IDebugSourceData,
     * if no value, selectItems is empty not null
     * @param event the event
     */
    void selectHandler(List<IDebugSourceData> selectItems, DebugCheckboxEvent event);
}
