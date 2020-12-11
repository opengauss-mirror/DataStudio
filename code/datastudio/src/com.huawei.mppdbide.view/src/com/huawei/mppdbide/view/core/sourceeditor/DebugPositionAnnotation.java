/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;

import java.util.Optional;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * Title: class
 * Description: The Class DebugPositionAnnotation.
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class DebugPositionAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationType ANNOTATION_TYPE = AnnotationType.DEBUG_POSITION;
    private static final RGB DEBUGPOSITION_RGB = new RGB(255, 153, 51);

    /**
     * Instantiates a new debug position annotation.
     *
     * @param line the line
     */
    public DebugPositionAnnotation(int line) {
        super(ANNOTATION_TYPE.getStrategy(), false, "[Line : " + line, line);
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
    public Optional<Image> getImage() {
        return Optional.of(
                loadImage(IiconPath.ICO_BREAKPOINT_ARROW)
                );
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
        return ANNOTATION_TYPE.getStrategy();
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return ANNOTATION_TYPE.getTypeLabel();
    }

    @Override
    public AnnotationType getAnnotationType() {
        return ANNOTATION_TYPE;
    }
}
