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

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import org.opengauss.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import org.opengauss.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class BreakpointAnnotation.
 *
 * @since 3.0.0
 */
public class BreakpointAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationHelper.AnnotationType ANNOTATION_TYPE = AnnotationType.BREAKPOINT;
    private static final RGB BREAKPOINT_RGB = new RGB(188, 188, 222);
    private int breakpointId;
    private boolean enable = true;

    /**
     * Instantiates a new breakpoint annotation.
     *
     * @param info the info
     * @param line the line
     */
    public BreakpointAnnotation(String info, int line) {
        super(ANNOTATION_TYPE.getStrategy(), false, info, line);
    }

    @Override
    public AnnotationHelper.AnnotationType getAnnotationType() {
        return ANNOTATION_TYPE;
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return ANNOTATION_TYPE.getLayer();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    @Override
    public Optional<Image> getImage() {
        String imgPath = enable ? IiconPath.ICO_BREAKPOINT_ENABLED :
                IiconPath.ICO_BREAKPOINT_DISABLED;
        return Optional.ofNullable(loadImage(imgPath));
    }

    /**
     * Gets the breakpoint rgb.
     *
     * @return the breakpoint rgb
     */
    public static RGB getBreakpointRgb() {
        return BREAKPOINT_RGB;
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return ANNOTATION_TYPE.getStrategy();
    }

    /**
     * Gets the breakpoint id.
     *
     * @return the breakpoint id
     */
    public int getBreakpointId() {
        return breakpointId;
    }

    /**
     * Sets the breakpoint id.
     *
     * @param breakpointId the new breakpoint id
     */
    public void setBreakpointId(int breakpointId) {
        this.breakpointId = breakpointId;
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return ANNOTATION_TYPE.getTypeLabel();
    }

    /**
     * Sets the disabled.
     *
     * @param isDisabld the new disabled
     */
    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    /**
     * Sets the disabled.
     *
     * @param isDisabld the new disabled
     * @return boolean true is enable
     */
    public boolean getEnable() {
        return this.enable;
    }
}
