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
 * 
 * Title: class
 * 
 * Description: The Class ErrorAnnotation.
 * 
 * Copyright (c) Huawei Technologies Co., Ltd. 2012-2019.
 *
 * @author pWX553609
 * @version [DataStudio 6.5.1, 17 May, 2019]
 * @since 17 May, 2019
 */
public class ErrorAnnotation extends AnnotationWithLineNumber {
    private static final AnnotationType ANNOTATION_TYPE = AnnotationType.ERROR; 
    /**
     * The Constant STRATEGY_ID.
     */

    private static final Object LOCK = new Object();
    private static volatile RGB errorRGB;

    /**
     * Instantiates a new error annotation.
     *
     * @param line the line
     * @param info the info
     */
    public ErrorAnnotation(int line, String info) {
        super(ANNOTATION_TYPE.getStrategy(), false, info, line);
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
        return Optional.of(loadImage(IiconPath.ICO_ERROR));
    }

    /**
     * Gets the error rgb.
     *
     * @return the error rgb
     */
    public static RGB getErrorRgb() {
        if (null == errorRGB) {
            synchronized (LOCK) {
                if (null == errorRGB) {
                    errorRGB = ANNOTATION_TYPE.getRGB();
                }
            }
        }
        return errorRGB;
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
