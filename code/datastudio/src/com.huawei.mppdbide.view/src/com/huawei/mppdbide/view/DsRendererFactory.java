/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view;

import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.ui.internal.workbench.swt.AbstractPartRenderer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.workbench.renderers.swt.WorkbenchRendererFactory;

import com.huawei.mppdbide.view.ui.visualexplainplan.VisualExplainPartsManager;
import com.huawei.mppdbide.view.utils.consts.UIConstants;

/**
 * Title: DsRendererFactory
 * 
 * Description: A factory for creating DsRenderer objects.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 20-May-2019]
 * @since 20-May-2019
 */

public class DsRendererFactory extends WorkbenchRendererFactory {
    private DsStackRenderer stackRenderer;

    /**
     * Gets the renderer.
     *
     * @param uiElement the ui element
     * @param parent the parent
     * @return the renderer
     */
    @Override
    public AbstractPartRenderer getRenderer(MUIElement uiElement, Object parent) {
        if (uiElement instanceof MPartStack) {
            MPartStack partStack = (MPartStack) uiElement;
            List<String> partStackIdList = Arrays.asList(UIConstants.PARTSTACK_ID_EDITOR,
                    VisualExplainPartsManager.MAIN_PART_STACKID, VisualExplainPartsManager.OVERALL_PROPERTIES_STACKID,
                    VisualExplainPartsManager.DETAILS_STACKID);

            if (partStackIdList.contains(partStack.getElementId())) {
                if (null == stackRenderer) {
                    stackRenderer = new DsStackRenderer();
                    super.initRenderer(stackRenderer);
                }

                return stackRenderer;
            }
        }

        return super.getRenderer(uiElement, parent);
    }

}
