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
 * Description: The Class DebugFailPositionAnnotation.
 *
 * @since 3.0.0
 */
public class DebugFailPositionAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationType ANNOTATION_TYPE_FAIL = AnnotationType.DEBUG_POSITION_FAIL;
    private static final RGB DEBUGPOSITION_RGB = new RGB(255, 153, 51);

    /**
     * Instantiates a new debug position annotation.
     *
     * @param line the line
     */
    public DebugFailPositionAnnotation(int line) {
        super(ANNOTATION_TYPE_FAIL.getStrategy(), false, "[Line : " + line, line);
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return ANNOTATION_TYPE_FAIL.getLayer();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Optional<Image> getImage() {
        return Optional.of(loadImage(IiconPath.ICO_EXECUTE_FAIL));
    }

    /**
     * Gets the breakpoint rgb.
     *
     * @return the breakpoint rgb
     */
    public static RGB getBreakpointRgb() {
        return DEBUGPOSITION_RGB;
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return ANNOTATION_TYPE_FAIL.getStrategy();
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return ANNOTATION_TYPE_FAIL.getTypeLabel();
    }

    @Override
    public AnnotationType getAnnotationType() {
        return ANNOTATION_TYPE_FAIL;
    }
}
