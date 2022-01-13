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

package com.huawei.mppdbide.view.ui.visualexplainplan;

import java.util.List;

import org.eclipse.e4.ui.model.application.ui.basic.MStackElement;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindowElement;
import org.eclipse.e4.ui.model.application.ui.basic.impl.PartStackImpl;
import org.eclipse.e4.ui.model.application.ui.basic.impl.TrimmedWindowImpl;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.google.inject.Inject;
import com.huawei.mppdbide.view.utils.UIElement;

/**
 * Title: VisualPlanWindowShutdownListener
 * 
 * Description: VisualPlanWindowShutdownListener
 * 
 * @since 3.0.0
 */
public final class VisualPlanWindowShutdownListener implements Listener {

    @Inject
    private IPresentationEngine engine;

    @Inject
    private EModelService modelService;

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        if (event.widget instanceof Shell) {
            boolean isMainWindow = UIElement.getInstance().isMainWindow((Shell) event.widget);
            if (!isMainWindow) {
                Shell shell = (Shell) event.widget;
                if (!shell.isDisposed()) {
                    Object obj = shell.getData("modelElement");
                    if (obj instanceof MTrimmedWindow && obj instanceof TrimmedWindowImpl) {
                        TrimmedWindowImpl trimWindow = (TrimmedWindowImpl) obj;
                        MWindowElement mWindowElement = trimWindow.getChildren().get(0);
                        if (mWindowElement instanceof PartStackImpl) {
                            PartStackImpl mainPartStack = (PartStackImpl) mWindowElement;
                            List<MStackElement> compositeList = mainPartStack.getChildren();
                            UIElement.getInstance().clearVisualPlan(compositeList);
                        }
                    }
                }
            }
        }
    }
}