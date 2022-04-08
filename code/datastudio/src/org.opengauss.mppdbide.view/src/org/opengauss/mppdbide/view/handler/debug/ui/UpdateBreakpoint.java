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

package org.opengauss.mppdbide.view.handler.debug.ui;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import org.opengauss.mppdbide.view.ui.BreakpointTableWindow;
import org.opengauss.mppdbide.view.utils.UIElement;

/**
 * Title: class
 * Description: The Class UpdateBreakpoint.
 *
 * @since 3.0.0
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
        String breakpointPartId = "org.opengauss.mppdbide.part.id.breakpoint";
        MPart breakpointPart = UIElement.getInstance().getPartService().findPart(breakpointPartId);
        if (!(breakpointPart.getObject() instanceof BreakpointTableWindow)) {
            return;
        }
        BreakpointTableWindow breakpointWindow = (BreakpointTableWindow) breakpointPart.getObject();
        breakpointWindow.refresh();
    }
}