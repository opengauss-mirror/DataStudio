/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019. All rights reserved.
 */

package com.huawei.mppdbide.view.core.sourceeditor;
/**
 * Title: class Description: The Class ErrorPositionAnnotation. Copyright (c)
 * Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author g00408002
 * @version [DataStudio 8.0.1, 17 Jan, 2020]
 * @since 17 Jan, 2020
 */

import java.util.Optional;

import org.eclipse.swt.graphics.Image;

import com.huawei.mppdbide.view.core.sourceeditor.AnnotationHelper.AnnotationType;
import com.huawei.mppdbide.view.utils.icon.IiconPath;

/**
 * 
 * Title: class
 * 
 * Description: The Class ErrorPositionAnnotation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2020.
 *
 * @author s00428892
 * @version [DataStudio 8.0.2, 04 Apr, 2020]
 * @since 04 Apr, 2020
 */
public class ErrorPositionAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationType annotationType = AnnotationType.ERROR_POSITION;
    /**
     * Instantiates a new debug position annotation.
     *
     * @param errorMessage the line
     */
    public ErrorPositionAnnotation(String errorMessage, int line) {
        super(annotationType.getStrategy(), false, errorMessage, line);
    }

    /**
     * Gets the layer.
     *
     * @return the layer
     */
    public static int getLayer() {
        return annotationType.getLayer();
    }

    /**
     * Gets the image.
     *
     * @return the image
     */
    public Optional<Image> getImage() {
        return Optional.of(loadImage(IiconPath.ICO_ERROR));
    }

    /**
     * Gets the strategyid.
     *
     * @return the strategyid
     */
    public static String getStrategyid() {
        return annotationType.getStrategy();
    }

    /**
     * Gets the typelabel.
     *
     * @return the typelabel
     */
    public static String getTypelabel() {
        return annotationType.getTypeLabel();
    }

    @Override
    public AnnotationType getAnnotationType() {
        return annotationType;
    }
}
