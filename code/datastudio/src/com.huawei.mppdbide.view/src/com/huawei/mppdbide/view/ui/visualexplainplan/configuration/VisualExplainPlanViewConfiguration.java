/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.ui.visualexplainplan.configuration;

import java.util.Locale;

import javafx.scene.paint.Color;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanViewConfiguration.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class VisualExplainPlanViewConfiguration {
    private static final Object INSTANCE_LOCK = new Object();
    private static volatile VisualExplainPlanViewConfiguration myObject;

    /**
     * Gets the single instance of VisualExplainPlanViewConfiguration.
     *
     * @return single instance of VisualExplainPlanViewConfiguration
     */
    public static VisualExplainPlanViewConfiguration getInstance() {
        if (null == myObject) {
            synchronized (INSTANCE_LOCK) {
                if (null == myObject) {
                    myObject = new VisualExplainPlanViewConfiguration();
                }
            }
        }
        return myObject;
    }

    /**
     * Gets the color.
     *
     * @param nodeType the node type
     * @return the color
     */
    public Color getColor(String nodeType) {
        if (nodeType.toLowerCase(Locale.ENGLISH).contains("scan")) {
            return Color.rgb(32, 178, 170);
        } else if (nodeType.toLowerCase(Locale.ENGLISH).contains("redistribute")) {
            return Color.rgb(30, 144, 255);
        }
        return Color.rgb(255, 255, 206);
    }

}
