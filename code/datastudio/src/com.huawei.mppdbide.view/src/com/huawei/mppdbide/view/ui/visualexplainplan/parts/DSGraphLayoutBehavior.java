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

package com.huawei.mppdbide.view.ui.visualexplainplan.parts;

import org.eclipse.gef.geometry.planar.Rectangle;
import org.eclipse.gef.layout.LayoutProperties;
import org.eclipse.gef.zest.fx.behaviors.GraphLayoutBehavior;

import javafx.scene.Scene;

/**
 * Title: DSGraphLayoutBehavior
 * 
 * Description:DSGraphLayoutBehavior
 * 
 * @since 3.0.0
 */
public class DSGraphLayoutBehavior extends GraphLayoutBehavior {

    /**
     * Update bounds.
     */
    @Override
    protected void updateBounds() {
        Scene scene = getHost().getVisual().getScene();
        if (scene == null || scene.getWindow() == null) {
            return;
        }

        /* Bounds calibrated based on 22 TPCH queries. */
        Rectangle newBounds = new Rectangle(0, 0, 2500, 2500);
        Rectangle oldBounds = LayoutProperties.getBounds(getHost().getContent());
        if (oldBounds != newBounds && (oldBounds == null || !oldBounds.equals(newBounds))) {
            LayoutProperties.setBounds(getHost().getContent(), newBounds);
            applyLayout(true, null);
        }
    }

}
