/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui;

import com.huawei.mppdbide.debuger.vo.VariableVo;
import com.huawei.mppdbide.view.core.VariableTableWindowCore;

/**
 * Title: class
 * Description: The Class VariableTableWindow.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class VariableTableWindow extends WindowBase<VariableVo> {
    /**
     * Instantiates a new variable table window.
     */
    public VariableTableWindow() {
        tableWindowCore = new VariableTableWindowCore();
    }
}