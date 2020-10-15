/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
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
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * 
 * @author sWX316469
 * @version [DataStudio 6.5.1, 11-Oct-2019]
 * @since 11-Oct-2019
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
