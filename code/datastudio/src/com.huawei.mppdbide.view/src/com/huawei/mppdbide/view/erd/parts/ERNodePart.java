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
 * @Description: The Class ERNodePart.
 *
 * @since 3.0.0
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
