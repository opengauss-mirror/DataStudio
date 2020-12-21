/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import com.huawei.mppdbide.debuger.vo.StackVo;
import com.huawei.mppdbide.view.core.StackTableWindowCore;

/**
 * Title: class
 * Description: The Class StackTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class StackTableWindow extends WindowBase<StackVo> {
    /**
     * Instantiates a new stack table window.
     */
    public StackTableWindow() {
        tableWindowCore = new StackTableWindowCore();
    }
}