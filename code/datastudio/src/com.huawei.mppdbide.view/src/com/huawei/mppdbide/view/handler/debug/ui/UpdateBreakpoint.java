/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.handler.debug.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import com.huawei.mppdbide.view.ui.BreakpointTableWindow;
import com.huawei.mppdbide.view.ui.WindowBase;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class UpdateBreakpoint.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @version [openGauss DataStudio 1.0.1, 04,12,2020]
 * @since 04,12,2020
 */
public class UpdateBreakpoint implements Runnable {
    /**
     * Instantiates a new update breakpoint.
     */
    public UpdateBreakpoint () {
    }

    /**
     * Run.
     */
    @Override
    public void run() {
        String breakpointPartId = "com.huawei.mppdbide.part.id.breakpoint";
        MPart breakpointPart = UIElement.getInstance().getPartService().findPart(breakpointPartId);
        if (!(breakpointPart.getObject() instanceof BreakpointTableWindow)) {
            return;
        }
        BreakpointTableWindow breakpointWindow = (BreakpointTableWindow) breakpointPart.getObject();
        breakpointWindow.refresh();
    }
}