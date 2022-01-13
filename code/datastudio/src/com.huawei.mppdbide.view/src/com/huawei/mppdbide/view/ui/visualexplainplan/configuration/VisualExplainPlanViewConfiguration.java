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

package com.huawei.mppdbide.view.ui.visualexplainplan.configuration;

import java.util.Locale;

import javafx.scene.paint.Color;

/**
 * 
 * Title: class
 * 
 * Description: The Class VisualExplainPlanViewConfiguration.
 *
 * @since 3.0.0
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
