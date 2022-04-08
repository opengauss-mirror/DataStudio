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

package org.opengauss.mppdbide.view.ui.visualexplainplan;

import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import org.opengauss.mppdbide.presentation.visualexplainplan.ExplainPlanPresentation;

/**
 * Title: VisualExplainPlanCompositeListener
 * 
 * Description: VisualExplainPlanCompositeListener
 *
 * @since 3.0.0
 */
public class VisualExplainPlanCompositeListener implements Listener {
    private LinkedHashMap<String, ExplainPlanPresentation> presentation;

    public VisualExplainPlanCompositeListener(LinkedHashMap<String, ExplainPlanPresentation> presentation) {
        this.presentation = presentation;
    }

    /**
     * Handle event.
     *
     * @param event the event
     */
    @Override
    public void handleEvent(Event event) {
        // Mouse UP listener for composite tabs
        if (event.widget instanceof CTabFolder && event.stateMask == SWT.BUTTON1) {
            CTabFolder folder = (CTabFolder) event.widget;
            CTabItem item = folder.getSelection();
            if (null != item && !item.isDisposed()) {
                String key = item.getText();
                ExplainPlanPresentation planPresentation = presentation.get(key);
                if (planPresentation != null) {
                    VisualExplainPlanUIPresentation prep = new VisualExplainPlanUIPresentation(key, planPresentation);
                    VisualExplainPartsManager.getInstance().createVisualExplainPlanParts(prep.getExplainPlanTabId(),
                            prep);
                }
            }
        }
    }

}
