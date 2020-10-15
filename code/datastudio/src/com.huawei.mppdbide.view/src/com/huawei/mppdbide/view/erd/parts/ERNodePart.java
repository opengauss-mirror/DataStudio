/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.erd.parts;

import org.eclipse.gef.zest.fx.parts.NodePart;

import com.huawei.mppdbide.bl.erd.model.AbstractEREntity;
import com.huawei.mppdbide.bl.erd.model.IERNodeConstants;
import com.huawei.mppdbide.view.erd.visuals.EREntityVisual;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.scene.Group;

/**
 * The Class ERNodePart.
 *
 * @ClassName: ERNodePart
 * @Description: The Class ERNodePart. Copyright (c) Huawei Technologies Co.,
 * Ltd. 2012-2019.
 * @author: f00512995
 * @version:
 * @since: Sep 28, 2019
 */
public class ERNodePart extends NodePart {

    @Override
    protected Group doCreateVisual() {
        ReadOnlyMapProperty<String, Object> property = getContent().attributesProperty();
        AbstractEREntity entity = (AbstractEREntity) property.get(IERNodeConstants.NODE_PROPERTY);
        EREntityVisual entityVisual = new EREntityVisual(entity);
        entityVisual.initEREntityVisual(entity);
        return entityVisual;
    }
}
