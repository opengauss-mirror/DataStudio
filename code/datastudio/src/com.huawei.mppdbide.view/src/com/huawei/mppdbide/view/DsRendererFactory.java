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
 * @since 3.0.0
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
