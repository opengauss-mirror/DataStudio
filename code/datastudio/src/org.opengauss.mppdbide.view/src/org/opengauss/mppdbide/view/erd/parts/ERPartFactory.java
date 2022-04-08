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

package org.opengauss.mppdbide.view.erd.parts;

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
 * @since 3.0.0
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
