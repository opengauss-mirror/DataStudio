/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import com.huawei.mppdbide.debuger.vo.BreakpointVo;
import com.huawei.mppdbide.view.core.BreakpointTableWindowCore;

/**
 * Title: class
 * Description: The Class BreakpointTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class BreakpointTableWindow extends WindowBase<BreakpointVo> {
    /**
     * Instantiates a new breakpoint table window.
     */
    public BreakpointTableWindow() {
        tableWindowCore = new BreakpointTableWindowCore();
    }
}