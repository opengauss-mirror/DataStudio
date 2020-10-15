/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan.parts;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * 
 * Title: CustomContentPartFactory
 * 
 * Description:CustomContentPartFactory
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author sWX316469
 * @version [DataStudio 6.5.1, 15 Oct, 2019]
 * @since 15 Oct, 2019
 */
public class CustomContentPartFactory extends ZestFxContentPartFactory {
    @Inject
    private Injector injector;

    /**
     * Creates a new CustomContentPart object.
     *
     * @param content the content
     * @param contextMap the context map
     * @return the i content part<? extends javafx.scene. node>
     */
    @Override
    public IContentPart<? extends javafx.scene.Node> createContentPart(Object content, Map<Object, Object> contextMap) {
        if (content instanceof org.eclipse.gef.graph.Node) {
            // create custom node if we find the custom attribute
            CustomNodePart part = new CustomNodePart();
            injector.injectMembers(part);
            return part;
        }
        return super.createContentPart(content, contextMap);
    }
}
