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

package org.opengauss.mppdbide.view.core.sourceeditor;

import org.eclipse.swt.graphics.RGB;

import org.opengauss.mppdbide.utils.IMessagesConstants;
import org.opengauss.mppdbide.utils.loader.MessageConfigLoader;

/**
 * Title: class
 * Description: The Class DebugEditorItem.
 *
 * @since 3.0.0
 */
public class AnnotationHelper {
    /**
     * break point annotation layer
     */
    public static final int BREAKPOINT_LAYER = 1;
    /**
     * break point annotation type label
     */
    public static final String BREAKPOINIT_TYPE_LABEL = IMessagesConstants.BREAKPOINT_ANNOTATION_LABEL;
    /**
     * break point annotation strategy id
     */
    public static final String BREAKPOINT_STRATEGY_ID = "PLSQL_DEBUGGER_BREAKPOINT";
    /**
     * break point annotation rgb
     */
    public static final RGB BREAKPOINT_RGB = new RGB(188, 188, 222);

    /**
     * debug position annotation layer
     */
    public static final int DEBUG_POSITION_LAYER = 2;
    /**
     * debug position annotation type label
     */
    public static final String DEBUG_POSITION_TYPE_LABEL = IMessagesConstants.DEBUG_POSITION_LABEL;

    /**
     * debug position annotation type pass
     */
    public static final String DEBUG_POSITION_TYPE_PASS = IMessagesConstants.DEBUG_POSITION_LABEL_PASS;

    /**
     * debug position annotation type fail
     */
    public static final String DEBUG_POSITION_TYPE_FAIL = IMessagesConstants.DEBUG_POSITION_LABEL_FAIL;
    /**
     * debug position annotation strategy id
     */
    public static final String DEBUG_POSITION_STRATEGY_ID = "PLSQL_DEBUGGER_DEBUG_POSITION";

    /**
     * debug position annotation pass id
     */
    public static final String DEBUG_POSITION_STRATEGY_PASS_ID = "PLSQL_DEBUGGER_DEBUG_POSITION_PASS";

    /**
     * debug position annotation fail id
     */
    public static final String DEBUG_POSITION_STRATEGY_FAIL_ID = "PLSQL_DEBUGGER_DEBUG_POSITION_FAIL";
    /**
     * debug position annotation rgb
     */
    public static final RGB DEBUG_POSITION_RGB = new RGB(255, 153, 51);

    /**
     * error annotation layer
     */
    public static final int ERROR_LAYER = 3;
    /**
     * error annotation type label
     */
    public static final String ERROR_TYPE_LABEL = IMessagesConstants.ERROR_ANNOTATION_LABEL;
    /**
     * error annotation strategy id
     */
    public static final String ERROR_STRATEGY_ID = "error.type";
    /**
     * error annotation rgb
     */
    public static final RGB ERROR_RGB = new RGB(255, 0, 0);

    /**
     * error position annotation layer
     */
    public static final int ERROR_POSITION_LAYER = 2;
    /**
     * error position annotation type label
     */
    public static final String ERROR_POSITION_TYPE_LABEL = IMessagesConstants.ERROR_POSITION_LABEL;
    /**
     * error position annotation strategy id
     */
    public static final String ERROR_POSITION_STRATEGY_ID = "PLSQL_ERROR_POSITION";
    /**
     * error position annotation rgb
     */
    public static final RGB ERROR_POSITION_RGB = new RGB(255, 255, 0);

    /**
     * pass position annotation rgb
     */
    public static final int DEBUG_POSITION_LAYER_PASS = 4;

    /**
     * pass position annotation rgb
     */
    public static final RGB DEBUG_POSITION_PASS_RGB = new RGB(40, 255, 15);

    /**
     * fail position annotation rgb
     */
    public static final int DEBUG_POSITION_LAYER_FAIL = 5;

    /**
     * fail position annotation rgb
     */
    public static final RGB DEBUG_POSITION_FAIL_RGB = new RGB(255, 30, 25);


    /**
     * Title: AnnotationType enum this enum assocate with AnnotationWithLineNumber's instance
     * Description: The Class AnnotationType
     */
    public static enum AnnotationType {
        BREAKPOINT(BREAKPOINT_LAYER, BREAKPOINIT_TYPE_LABEL, BREAKPOINT_STRATEGY_ID,
                BREAKPOINT_RGB),
        DEBUG_POSITION(DEBUG_POSITION_LAYER, DEBUG_POSITION_TYPE_LABEL, DEBUG_POSITION_STRATEGY_ID,
                DEBUG_POSITION_RGB),
        DEBUG_POSITION_PASS(DEBUG_POSITION_LAYER_PASS, DEBUG_POSITION_TYPE_PASS, DEBUG_POSITION_STRATEGY_PASS_ID,
                DEBUG_POSITION_PASS_RGB),
        DEBUG_POSITION_FAIL(DEBUG_POSITION_LAYER_FAIL, DEBUG_POSITION_TYPE_FAIL, DEBUG_POSITION_STRATEGY_FAIL_ID,
                DEBUG_POSITION_FAIL_RGB),
        ERROR(ERROR_LAYER, ERROR_TYPE_LABEL, ERROR_STRATEGY_ID,
                ERROR_RGB),
        ERROR_POSITION(ERROR_POSITION_LAYER, ERROR_POSITION_TYPE_LABEL, ERROR_POSITION_STRATEGY_ID,
                ERROR_POSITION_RGB);
        private final int layer;
        private final String typeLabel;
        private final String stragety;
        private final RGB rgbShow;
        AnnotationType(int layer, String typeLabel, String strategy, RGB rgbShow) {
            this.layer = layer;
            String tmpTypeLabel = "";
            if (!"".equals(typeLabel)) {
                tmpTypeLabel = MessageConfigLoader.getProperty(typeLabel);
            }
            this.typeLabel = tmpTypeLabel;
            this.stragety = strategy;
            this.rgbShow = rgbShow;
        }

        /**
         * get layer
         *
         * @return int layer
         * */
        public int getLayer() {
            return layer;
        }

        /**
         * get type label
         *
         * @return String type label
         * */
        public String getTypeLabel() {
            return typeLabel;
        }

        /**
         * get  stragegy
         *
         * @return String strategy id
         * */
        public String getStrategy() {
            return stragety;
        }

        /**
         * get  rgb
         *
         * @return RGB  get rgb
         * */
        public RGB getRGB() {
            return rgbShow;
        }
    }
}
