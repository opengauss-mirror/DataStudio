/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.parts;

import java.util.Map;

import org.eclipse.gef.mvc.fx.parts.IContentPart;
import org.eclipse.gef.zest.fx.parts.ZestFxContentPartFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * The Class ERPartFactory.
 *
 * @ClassName: ERPartFactory
 * @Description: The Class ERPartFactory.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Sep 28, 2019
 */
public class ERPartFactory extends ZestFxContentPartFactory {
    @Inject
    private Injector injector;

    @Override
    public IContentPart<? extends javafx.scene.Node> createContentPart(Object content, Map<Object, Object> contextMap) {
        if (content instanceof org.eclipse.gef.graph.Node) {
            // create custom node if we find the custom attribute
            ERNodePart part = new ERNodePart();
            injector.injectMembers(part);
            return part;
        }
        return super.createContentPart(content, contextMap);
    }
}
