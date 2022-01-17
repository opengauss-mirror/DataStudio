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

package com.huawei.mppdbide.view.erd.visuals;

/**
 * Title: ERVisualStyleConstants
 *
 * @since 3.0.0
 */
public interface IERVisualStyleConstants {
    /**
     * The entity visual style.
     */
    String ENTITY_VISUAL_STYLE = "-fx-border-color: deepskyblue; -fx-border-width: 2; -fx-background-color: white;";

    /**
     * The table header style.
     */
    String TABLE_HEADER_STYLE = "-fx-alignment : center";

    /**
     * The attribute text style.
     */
    String ATTRIBUTE_TEXT_STYLE = "-fx-text-alignment : left";

    /**
     * The line style.
     */
    String LINE_STYLE = "-fx-border-style: solid; -fx-border-width: 2 0 0 0; -fx-border-color: deepskyblue";

    /**
     * The edge style.
     */
    String EDGE_STYLE = "-fx-stroke-style : deepskyblue";
}
