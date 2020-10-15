/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan;

import java.util.LinkedHashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.huawei.mppdbide.presentation.visualexplainplan.ExplainPlanPresentation;

/**
 * Title: VisualExplainPlanCompositeListener
 * 
 * Description: VisualExplainPlanCompositeListener
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author pWX759367
 * @version [DataStudio 6.5.1, 03-Dec-2019]
 * @since 03-Dec-2019
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
